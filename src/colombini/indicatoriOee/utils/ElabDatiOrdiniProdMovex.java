/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.costant.CostantsColomb;
import colombini.query.indicatoriOee.QueryRtvoee10pf;
import colombini.query.indicatoriOee.QueryRuntimeOP;
import colombini.query.indicatoriOee.QueryRuntimeOPNorm;
import colombini.query.indicatoriOee.QueryRuntimeOPRaggr;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDatiOrdiniProdMovex {
   
  
   
   
   public final static String  RAGGR="QUERYRAGGR";
  
   private static ElabDatiOrdiniProdMovex instance;
   
   public ElabDatiOrdiniProdMovex(){
   
   }
   
   public static ElabDatiOrdiniProdMovex getInstance(){
     if(instance==null){
       instance= new ElabDatiOrdiniProdMovex();
     }
    
     return instance;
   }
   
   public Map loadDatiProdFromMovex(Connection con ,String centroLavoro,String centroLavPadre,Date data,String orainizio,String orafine,String tipo) throws QueryException, SQLException{
     Map mappa=new HashMap();
     Double tSetupCambio=Double.valueOf(0);
     Double tAvvio=Double.valueOf(0);
     Double ripartizione=Double.valueOf(1);
    
     if(centroLavoro==null)
       return null;
     //controllo dati su ripartizioni e tempi di avvio della macchina definiti a sistema
     QueryRtvoee10pf qry=new QueryRtvoee10pf();
     qry.setFilter(QueryRtvoee10pf.CENTROLAVORO, centroLavoro);
     String query=qry.toSQLString();
    
     Object [] obj=ResultSetHelper.SingleRowSelect(con, query);
     if(obj!=null && obj.length>2){
       ripartizione=ClassMapper.classToClass(obj[0], Double.class);
       tSetupCambio=ClassMapper.classToClass(obj[1], Double.class);
       tAvvio=ClassMapper.classToClass(obj[2], Double.class);
     }
     
     mappa=loadDatiProduzione(con, data,centroLavoro,centroLavPadre,ripartizione,orainizio,orafine,tipo);
     if(mappa!=null && !mappa.isEmpty()){
       Long npedane=mappa.get(CostantsColomb.NPEDANE)== null ? new Long(0) : (Long)mappa.get(CostantsColomb.NPEDANE);
       mappa.put(CostantsColomb.TSETUP, new Double(tSetupCambio*npedane).longValue());
     }
     mappa.put(CostantsColomb.TAVVIO, tAvvio.longValue());
    
    
     return mappa;
  }
  
  /**
    * Torna una mappa contenente informazioni relative alla produzione del centro di lavoro indicato nel periodo indicato
    * @param Connection con
    * @param Date data
    * @param centroLavoro
    * @param centroLavPrinc
    * @param ripartizione
    * @param orainizio
    * @param orafine
    * @return Map
    * @throws QueryException
    * @throws SQLException 
    */
  private Map loadDatiProduzione(Connection con,Date data,String centroLavoro,String centroLavPadre,Double ripartizione,String orainizio,String orafine,String tipo) throws QueryException, SQLException{
    Double tempoRun=Double.valueOf(0);
    Double tempoSca=Double.valueOf(0);
    Double nPedane=Double.valueOf(0);
    Double qtScarti=Double.valueOf(0);
    Double qtPz=Double.valueOf(0);
    Double tempoVar=Double.valueOf(0);
    StringBuilder stringWarn=new StringBuilder();
    
    
    Map mappaVal =new HashMap();
    PreparedStatement ps = null;
    ResultSet rs = null;
    QueryRuntimeOP qry=null;
    
    if(RAGGR.equals(tipo)){
      qry=new QueryRuntimeOPRaggr();
    } else{
      qry=new QueryRuntimeOPNorm();
    }  
    Long dataN=DateUtils.getDataForMovex(data);
    
    qry.setFilter(QueryRuntimeOPRaggr.CENTROLAVORO, centroLavoro);
    if(centroLavPadre!=null){
      qry.setFilter(QueryRuntimeOPRaggr.CENTROLAVOROPADRE, centroLavPadre);
    }
    qry.setFilter(QueryRuntimeOPRaggr.DATA, dataN);
    qry.setFilter(QueryRuntimeOPRaggr.VALRIP, ripartizione);
    qry.setFilter(QueryRuntimeOPRaggr.ORAINI, orainizio);
    qry.setFilter(QueryRuntimeOPRaggr.ORAFIN, orafine);
    _logger.info(qry.toSQLString());
    try{
      ps=con.prepareStatement(qry.toSQLString());
      rs=ps.executeQuery();
      Double qtaOld=Double.valueOf(0);
      Double tempoOld=Double.valueOf(1);
      
      while(rs.next()){
        String ora =rs.getString("ora");    
        tempoSca+=rs.getDouble("tmp_scarto");
        qtScarti+=rs.getDouble("qt_scarto");
        qtPz+=rs.getDouble("qt_buona");
        Double tempoB=rs.getDouble("tmp_buono");
        
        //modifica in data 09/09/2014 per errori su ordine di produzione 
        if(tempoB.intValue()>CostantsColomb.TEMPOTOT1TURNOSEC){
          String ped=ClassMapper.classToString(rs.getDouble("nped"));
          stringWarn.append("Attenzione tempo per produzione Pedana : "+ped+" errato :"+tempoB +"\n --> Viene impostato un default");
          tempoB=(tempoOld/qtaOld)*rs.getDouble("qt_buona");
        } 
        
        if(RAGGR.equals(tipo)){
          nPedane+=rs.getDouble("nped");
          tempoRun+=tempoB;
        }else{
          String clPrinc=rs.getString("centro_princ").trim();
          String clAlt=rs.getString("centro_alt").trim();
          if(checkCentriLav(clPrinc, clAlt)){
            tempoRun+=tempoB;
          }else{
            tempoVar+=tempoB;
          }
          nPedane++;
        }
        qtaOld=rs.getDouble("qt_buona");
        tempoOld=tempoB;
        _logger.info(" ORA: "+ora+ " RUNTIME: "+tempoRun.longValue()+ " RUNTIMEVAR: "+tempoVar.longValue()+" TEMPOSCARTO: "+tempoSca.longValue()+" NPEDANE: "+nPedane);
      }
      
      mappaVal.put(CostantsColomb.TRUNTIME, tempoRun.longValue());
      mappaVal.put(CostantsColomb.TSCARTI, tempoSca.longValue());
      mappaVal.put(CostantsColomb.TLORDO, tempoVar.longValue());
      mappaVal.put(CostantsColomb.NPEDANE, nPedane.longValue());
      mappaVal.put(CostantsColomb.NSCARTI, qtScarti.longValue());
      mappaVal.put(CostantsColomb.NPZTOT, qtPz.longValue());
      if(stringWarn.length()>0){
        mappaVal.put(CostantsColomb.WARNINGS, stringWarn.toString());
      }
      
    }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    }
    return mappaVal;
  }
  
  /**
   * Torna il tempo di setup calcolato e storicizzato sulla rtvoee21pf
   * @param Connection con
   * @param Date data
   * @return Long setup
   */
  public Long getTempoSetupAs(Connection con,Date data,String centroLavoro){
    Long setup = Long.valueOf(0);
    Long dtL=null;
    try {
      dtL = DateUtils.getDataForMovex(data);
      if(dtL==0)
        return setup;
        
      String query =" select setup as setup from mcobmoddta.rtvoee21pf "+
                  " where data="+dtL+
                  " and centro='" +centroLavoro + "'";
    
      Object [] obj = ResultSetHelper.SingleRowSelect(con, query);
      if(obj!=null && obj.length>0)
        setup=ClassMapper.classToClass(obj[0], Long.class);
    
    } catch (SQLException ex) {
      _logger.error("Errore di connessione al DB.Impossibile caricare il SETUP AS per il centro"+centroLavoro+" :"+ex.getMessage());
    }
    
    return setup;
  }
   
   
  private boolean checkCentriLav(String princ,String alt){
    
    if(!StringUtils.isEmpty(princ) && !StringUtils.isEmpty(alt) && !princ.equals(alt)){
      return false;
    } 
    
    return true;
  } 
  
  
  public List getListDatiProd(Connection con,Date data,String centroLavoro,String centroLavPadre,Double ripartizione,String orainizio,String orafine,String tipo) throws QueryException, SQLException{
    List l=new ArrayList();
    QueryRuntimeOP qry=null;
    
    
    if(RAGGR.equals(tipo)){
      qry=new QueryRuntimeOPRaggr();
    } else{
      qry=new QueryRuntimeOPNorm();
    }  
    Long dataN=DateUtils.getDataForMovex(data);
    
    qry.setFilter(QueryRuntimeOPRaggr.CENTROLAVORO, centroLavoro);
    if(centroLavPadre!=null){
      qry.setFilter(QueryRuntimeOPRaggr.CENTROLAVOROPADRE, centroLavPadre);
    }
    qry.setFilter(QueryRuntimeOPRaggr.DATA, dataN);
    qry.setFilter(QueryRuntimeOPRaggr.VALRIP, ripartizione);
    qry.setFilter(QueryRuntimeOPRaggr.ORAINI, orainizio);
    qry.setFilter(QueryRuntimeOPRaggr.ORAFIN, orafine);
    String s=qry.toSQLString();
    _logger.debug(qry.toSQLString());
    
    ResultSetHelper.fillListList(con, s, l);
    
    return l;
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabDatiOrdiniProdMovex.class);
   
}
