/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QryPWInfoLineeTurni;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDtProdPWCopy extends ElabClass {

  public final static String TABORARICDL="ZDPWCO";
  public final static String TABORARIOPERATORI="ZDPWPT";
  
  public static String INSERT_ZDPWCO="INSERT INTO MCOBMODDTA.ZDPWCO\n" +
      "( COIDCO,COCONO,COPLGR,CODTRF,COT1IN,COT1FN,COT1PP,COT1PS,COT1CP,COT1BP,COT1NO,COT1OT,COT1MT,COT1OS,COT1MS,COT2IN,COT2FN,COT2PP,\n" +
      "  COT2PS,COT2CP,COT2BP,COT2NO,COT2OT,COT2MT,COT2OS,COT2MS,COT3IN,COT3FN,COT3PP,COT3PS,COT3CP,COT3BP,COT3NO,COT3OT,COT3MT,COT3OS,\n" +
      "  COT3MS,COT4IN,COT4FN,COT4PP,COT4PS,COT4CP,COT4BP,COT4NO,COT4OT,COT4MT,COT4OS,COT4MS,COLMUT,COLMDT  )";
          
  public static String INSERT_ZDPWPT="INSERT INTO MCOBMODDTA.ZDPWPT "+
  " ( TPIDTO,TPIDCO,TPCONO,TPDTRF,TPIDOP,TPDSOP,TPT1IN,TPT1FN,TPT2IN,TPT2FN,TPLMUT,TPLMDT  ) ";
  
  
  private Date dataInizio;
  private Date dataFine;
  private List lineeElab;
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(dataInizio==null)
      return Boolean.FALSE;
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }
    
    if(dataFine==null)
      dataFine=DateUtils.addDays(dataInizio,1);
    
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Duplicazione dati di produzione per giorno "+dataInizio +" su giorno "+dataFine);
    try{
      
      List<List> elsTodupl=getListCdl(con, dataInizio,lineeElab);
      Map mapsCld=getMapCdlPresents(con, dataFine,lineeElab);
      
      if(elsTodupl==null || elsTodupl.isEmpty())
        return;
      
      for(List el:elsTodupl){
        String linea=ClassMapper.classToString(el.get(0));
        Long idSeqCdlOld=ClassMapper.classToClass(el.get(1),Long.class);
       
        if(mapsCld.containsKey(linea)){
          _logger.warn("Dati già presenti per linea "+linea + " e data "+dataFine+" . La procedura non copierà nulla !!");
          addWarning("Dati già presenti per linea "+linea + " e data "+dataFine+" . La procedura non duplicherà nessun dato!!");
          continue;
        }
        
        _logger.info(" Generazione dati per linea : "+linea +"e giorno : "+dataFine);
        try{
          Long idSeqCdlNew=getIdSeq(con, "MCOBMODDTA.SEQ_ZDPWCO");
          insertDataZdpwco(con, idSeqCdlOld, idSeqCdlNew, dataFine);
          insertDataZdpwpt(con, idSeqCdlOld, idSeqCdlNew, dataFine);
        
        }catch(SQLException s){
         _logger.error(" Impossibile generare i dati per linea "+linea+" --> "+s.getMessage());  
         addError(" Impossibile generare i dati per linea "+linea+" e giorno "+ dataFine+" --> "+s.toString());
        }
      }
      
    } catch (SQLException s){
      _logger.error("Errore in fase di duplicazione dei dati di produzione -->"+s.getMessage());
      addError("Errore in fase di duplicazione dei dati di produzione.Impossibile leggere la lista delle linee da processare -->"+s.toString());
    }
      
      
  }
  
  
  private List getListCdl(Connection con ,Date data,List linee) throws SQLException{
    List<List> l=new ArrayList(); 
    QryPWInfoLineeTurni q=new QryPWInfoLineeTurni();
    q.setFilter(FilterQueryProdCostant.FTDATARIF, data);
    q.setFilter(QryPWInfoLineeTurni.FCOPYACTIVE , "S");
    if(linee!=null && linee.size()>0)
      q.setFilter(FilterQueryProdCostant.FTLINEELAV, linee.toString());
//    String s=" select distinct coplgr,coidco,clfact,clplan,clplgr "
//            +" from mcobmoddta.ZDPWCO inner join mcobmoddta.ZDPWCL on coplgr=clplgr "
//            + " where codtrf="+JDBCDataMapper.objectToSQL(data)
//            + " and clfcyd =1 "
//            +" order by clfact,clplan,clplgr ";
//    
//    
    try {
      ResultSetHelper.fillListList(con, q.toSQLString(), l);
    } catch(QueryException ex){
      throw new SQLException(ex);
    }
    
    return l;
  }
  
  //MCOBMODDTA.SEQ_ZDPWCO
  private Long getIdSeq(Connection con,String sequenceName ) throws SQLException{
    Long id=null;
    String s="select next value for "+sequenceName
            + " from sysibm.sysdummy1 ";
    
    Object []obj = ResultSetHelper.SingleRowSelect(con, s);
    if(obj!=null && obj.length>0){
      id=ClassMapper.classToClass(obj[0], Long.class);
    }
    
    return id;
  }
  
  private Boolean insertDataZdpwco(Connection con, Long idTurnoOld,Long idTurnoNew,Date dataNew) throws SQLException{
    PreparedStatement ps = null;
    Boolean execute=Boolean.FALSE;
    try{
      StringBuilder insert=new StringBuilder(INSERT_ZDPWCO);
      insert.append("\n ( SELECT  ").append(JDBCDataMapper.objectToSQL(idTurnoNew)).append(" , ").append(
              " COCONO, COPLGR,  ").append(JDBCDataMapper.objectToSQL(dataNew)).append(" , ").append(
              "\n COT1IN,COT1FN,0,0,0,0,0,0,0,0,0, ").append(
              "\n COT2IN,COT2FN,0,0,0,0,0,0,0,0,0, ").append(
              "\n COT3IN,COT3FN,0,0,0,0,0,0,0,0,0, ").append(
              "\n COT4IN,COT4FN,0,0,0,0,0,0,0,0,0, ").append(
              "\n 'javabatch',current timestamp").append(
              "\n FROM mcobmoddta.ZDPWCO ").append(
              "\n WHERE COIDCO= ?    )");

      ps = con.prepareStatement(insert.toString());

      
      ps.setObject(1, idTurnoOld,Types.NUMERIC);
    
      execute=ps.execute();
      
    } catch(SQLException s){
      _logger.error("Errore in fase di inserimento dati Cdl -->"+s.getMessage());
      addError("Dati Orari Cdl  (id :"+idTurnoOld+") non generati correttamente -->"+s.toString() );
      
    } finally {
      if(ps!=null)
        ps.close();
    }
    
    return execute;
  }
  
  
  private Boolean insertDataZdpwpt(Connection con, Long idTurnoOld,Long idTurnoNew,Date dataNew) throws SQLException{
    PreparedStatement ps = null;
    Boolean execute=Boolean.FALSE;
    try{
      StringBuilder insert=new StringBuilder(INSERT_ZDPWPT);
      insert.append("\n ( SELECT   ").append(
              "  next value for MCOBMODDTA.SEQ_ZDPWPT, ").append(
                 JDBCDataMapper.objectToSQL(idTurnoNew)).append(
              " , TPCONO ,  ").append(
                 JDBCDataMapper.objectToSQL(dataNew)).append(" , ").append(
              "\n tpidop,tpdsop,tpt1in,tpt1fn,tpt2in,tpt2fn,").append(
              "\n 'javabatch',current timestamp").append(
              "\n FROM mcobmoddta.ZDPWPT ").append(
              "\n WHERE TPIDCO= ? )");//.append(JDBCDataMapper.objectToSQL(idTurnoOld));        

      ps = con.prepareStatement(insert.toString());

      ps.setObject(1, idTurnoOld,Types.NUMERIC);
    
      execute=ps.execute();
      
    } catch(SQLException s){
      _logger.error("Errore in fase di inserimento dati turni Operatori -->"+s.getMessage());
      addError("Dati Turni Orari DIpendenti (id :"+idTurnoOld+") non generati correttamente -->"+s.toString() );
      
    } finally {
      if(ps!=null)
        ps.close();
    }
    
    return execute;
  }
  
  
  
  

  private Map getMapCdlPresents(Connection con, Date dataFineList ,List lineeEl) throws SQLException {
    Map map=new HashMap();
    List<List> l=getListCdl(con, dataFine,lineeEl);
    if(l==null || l.isEmpty())
      return map;  
    
    for(List e:l){
      String linea =ClassMapper.classToString(e.get(0));
      map.put(linea, "Y");
    }
      
    return map;
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabDtProdPWCopy.class); 
}
