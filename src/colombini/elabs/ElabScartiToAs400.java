/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.model.persistence.tab.TabProcessiAs400;
import colombini.model.persistence.tab.TabScartiAs400Feb;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.QueryScartiFromTAP;
import colombini.util.DatiProdUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import db.persistence.IBeanPersCRUD;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabScartiToAs400 extends ElabClass{
  
  
  public final static String TABGENSCARTIIMALOTTO1="CO_MMZ101R";
  
  private final String UPD_ZTAPSP=" UPDATE MCOBMODDTA.ZTAPSP  set TSDTEL=(CURRENT TIMESTAMP)"
                                   +" WHERE TSIDSP=? "
                                   + " AND TSDTEL is null ";
  private List<Date> giorni;
  
  private Date dataInizio;
  private Date dataFine;
  
  private Map mapCausScartiImaLotto1=null;
  
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
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    _logger.info(" -----  Dati Produzione Linee Ima -----");

    //carico staticamente le causali di scarto della Ima Ante
    //so che fa cagare ma ad oggi è il modo più rapido....
    
   if(giorni==null|| giorni.isEmpty()){
      _logger.warn("Lista giorni da processare vuota . IMPOSSIBILE PROSEGUIRE");
      addWarning(" Impossibile lanciare l'elaborazione --> lista giorni da processare non valorizzata ");
      return;
    }
    
    loadCausaliScarti(con);
    
    elabScartiImaLotto1(con);
  }
  



private void loadCausaliScarti(Connection con){
    mapCausScartiImaLotto1 =new HashMap();
    List<List> causMMZ101=DatiProdUtils.getInstance().getElsCsyTab(con, CostantsColomb.AZCOLOM, " ", TABGENSCARTIIMALOTTO1);
    prepareMapScartiP4(causMMZ101, mapCausScartiImaLotto1);
}


private void prepareMapScartiP4(List<List> listaC,Map<String,Map> scarti){
    if(listaC==null || listaC.isEmpty())
      return;
    
    for(List rec:listaC){
      String info=ClassMapper.classToString(rec.get(0));
      String linea=info.substring(0, 6).trim();
      String cod=info.substring(7, 9).trim();
      String cdlTo=ClassMapper.classToString(rec.get(1)).trim();  //2--> descrizione causale :serve??
      Map scalin=null;
      if(scarti.containsKey(linea))
        scalin=scarti.get(linea);
      else
        scalin=new HashMap<String,String>();
      
      scalin.put(cod, cdlTo);
      scarti.put(linea,scalin);
    }
    
  }

private void elabScartiImaLotto1(Connection conAs400) {
    
  Connection conDesmos=null;
  try{
  conDesmos=ColombiniConnections.getDbDesmosColProdConnection();
    for(Date giorno:giorni){
      try {
        List infoS=new ArrayList();
        QueryScartiFromTAP q=new QueryScartiFromTAP();
        
        //q.setFilter(QueryScartiFromTAP.FT_CDLTRANS,"01024");
        q.setFilter(QueryScartiFromTAP.FT_CDLRESP,NomiLineeColomb.CDL_IMALOTTO1);
        q.setFilter(QueryScartiFromTAP.FT_INNERJOIN,"Y");
        q.setFilter(FilterFieldCostantXDtProd.FT_DATADA,DateUtils.getInizioGg(giorno));
        q.setFilter(FilterFieldCostantXDtProd.FT_DATAA,DateUtils.getFineGg(giorno));
        q.setFilter(QueryScartiFromTAP.FT_DELAB2,"Y");
        
        
        ResultSetHelper.fillListList(conAs400, q.toSQLString(), infoS);
        Map pNumFebal=getPNumberFebal(conDesmos,infoS);
        
        
        List beans=getBeans4Mvx100(conAs400, infoS,pNumFebal, NomiLineeColomb.CDL_IMALOTTO1, giorno, TabProcessiAs400.TIPORECSCARTI, TabProcessiAs400.REASONCODESCARTOIMALOTTO1);
        List beansFeb=getBeans4Mvx200(infoS,pNumFebal, NomiLineeColomb.CDL_IMALOTTO1, giorno);
        
        storeDtScartiOnMvx(conAs400, beans, NomiLineeColomb.CDL_IMALOTTO1, giorno);
        if(beansFeb!=null && beansFeb.size()>0){
          storeDtScartiOnMvx200(beansFeb, NomiLineeColomb.CDL_IMALOTTO1, giorno);
        }
        
        updateScartiOnTap(conAs400, infoS);
        
      } catch (ParseException ex) {
         _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+ex.getMessage());
         addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+ex.toString());
      } catch (QueryException ex) {
         _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db : "+ex.getMessage());
         addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db :"+ex.toString());
      } catch (SQLException ex) {
        _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db : "+ex.getMessage());
        addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db :"+ex.toString());
      }
      
    }
   } catch(SQLException s){
    addError("Errore in fase di connessione con il db Desmos-->"+s.getMessage()); 
   } finally{
     if(conDesmos!=null)
       try{
       conDesmos.close();
       } catch(SQLException s){
         _logger.error("Errore in fase di chiusura della connessione con DbDesmos -->"+s.getMessage());
       }
   }
  }





private Map getPNumberFebal(Connection conDesmos,List<List> infos){
  Map pNumFebal=new HashMap();
  try{
    String selectParts=getQueryStringListPartNumber(infos);

    String qryDesmos=" SELECT distinct PartNumber,ProvCommessa, odp as OdP  "
                    + " FROM [DesmosColombini].[dbo].[LDL05_BASE_SIRIO_IMA]   a"
                    +" inner join ("+selectParts+" ) b on  a.partnumber=b.pn "
                    + " where 1=1 ";

    List l=new ArrayList();
    ResultSetHelper.fillListList(conDesmos, qryDesmos, l);

    pNumFebal=getMapFromList(l);
  } catch(SQLException s){
    addError(" Errore in fase  di esecuzione della query su db Desmos --> " +s.getMessage());
  }
  return pNumFebal;      
}


private Map getMapFromList(List <List> list){
    Map map=new HashMap();
    if(list==null )
      return map;
    
    for(List l:list){
      String pNum=ClassMapper.classToString(l.get(0));
      String provComm=ClassMapper.classToString(l.get(1));
      Integer odP=ClassMapper.classToClass(l.get(2),Integer.class);
      if("F".equals(provComm) || "R".equals(provComm))
        map.put(pNum, odP);
    }
    return map;
  }


private String getQueryStringListPartNumber(List<List> infos){
  StringBuilder selectParts=new StringBuilder();
  
  if(infos==null || infos.isEmpty() )
    return "";
  
  
  int count=0;
  for(List info:infos){
    if(count>0)
      selectParts.append("\n UNION ");
    
    String part=ClassMapper.classToString(info.get(7));
    selectParts.append(" SELECT  ").append(JDBCDataMapper.objectToSQL(part)).append (" as PN ");
    count++;
  }
  
  
  return selectParts.toString();
}


private List<TabProcessiAs400> getBeans4Mvx100(Connection conAs400,List<List> l,Map pNumberFebs,String codLineaRif,Date ggRif,Integer tipoRec,String reasonCode){
    if(l==null || l.isEmpty()){
      addWarning("Attenzione nessuno scarto presente per linea "+codLineaRif+" e gg "+ggRif);
      return null; 
    }
      
    
    List<TabProcessiAs400> beans=new ArrayList();
    
    Integer idProc= DateUtils.getDayOfYear(new Date())*DateUtils.getYear(new Date());
    for(List scarto:l){
      String pNumber=ClassMapper.classToString(scarto.get(7));
      String codArticolo=ClassMapper.classToString(scarto.get(1));
      if(pNumberFebs.containsKey(pNumber))
        continue;
      
      if(codArticolo==null || codArticolo.trim().isEmpty()){
        addWarning("PartNumber con articolo non valorizzato :"+pNumber +" --> dati non inviabili a Mvx");
        continue;
      }
      
      TabProcessiAs400 bean=new TabProcessiAs400(CostantsColomb.AZCOLOM, TabProcessiAs400.STATOSCARTOTOPROC, "", "JAVA_ELB"+idProc,tipoRec,reasonCode);
      bean.setDataS(ClassMapper.classToString(scarto.get(0)));  //DateUtils.getDataForMovex(ggRif);
      bean.setCodArticolo(codArticolo);
      bean.setQta(ClassMapper.classToClass(scarto.get(2), Long.class));
      
      String codCaus=ClassMapper.classToString(scarto.get(3));
      String decCaus=null;
      String codLineaTrs=ClassMapper.classToString(scarto.get(8)).trim();
      String comm=ClassMapper.classToString(scarto.get(4));
      String ncol=ClassMapper.classToString(scarto.get(5));
      
      Map scartiLinea=(Map) mapCausScartiImaLotto1.get(codLineaTrs);
      if(scartiLinea!=null && scartiLinea.containsKey(codCaus)){
        decCaus=codCaus;
      }else{
        decCaus=null;
      }
      
      if(decCaus==null){
        decCaus="CAUSALE NON CODIFICATA";
        addWarning("\n Attenzione causale non codificata --> "
                +codCaus+"per la linea "+codLineaRif+" relativo al gg "+bean.getDataS());
      }
      
      bean.setCausCod(decCaus);
      String lineId=codLineaTrs+"    "+comm+" "+ncol;
      
      bean.setLineId(lineId);  
      checkUnMisuraArt(conAs400,bean);
      
      beans.add(bean);
    }
    
    return beans;
    
  }
  

  private List<TabScartiAs400Feb> getBeans4Mvx200(List<List> l,Map pNumberFebs,String codLineaRif,Date ggRif){
    if(l==null || l.isEmpty()){
      addWarning("Attenzione nessuno scarto presente per linea "+codLineaRif+" e gg "+ggRif);
      return null; 
    }
      
    
    List<TabScartiAs400Feb> beans=new ArrayList();
    
    for(List scarto:l){
      String pNumber=ClassMapper.classToString(scarto.get(7));
      Integer odpTemp=null;
      if(pNumberFebs.containsKey(pNumber)){
         odpTemp=(Integer)pNumberFebs.get(pNumber);
      
      TabScartiAs400Feb bean=new TabScartiAs400Feb(CostantsColomb.AZFEBAL, TabScartiAs400Feb.STATOSCARTOTOPROC,odpTemp,ggRif);
      
      bean.setCodArt(ClassMapper.classToString(scarto.get(1)));
      bean.setQta(ClassMapper.classToClass(scarto.get(2), Double.class));
      
      String codCaus=ClassMapper.classToString(scarto.get(3));
      String decCaus=null;
      String codLineaTrs=ClassMapper.classToString(scarto.get(8)).trim();
      String comm=ClassMapper.classToString(scarto.get(4));
      String ncol=ClassMapper.classToString(scarto.get(5));
      //la commessa che sul portale inzia con 9 è il precommessa rossana -->
      // in questo caso non devo mandare i dati relativi alla commessa e al collo
      if(!comm.startsWith("9")){
        bean.setCommessa(comm);
        bean.setCollo(ncol);
      }
      Map scartiLinea=(Map) mapCausScartiImaLotto1.get(codLineaTrs);
      
      if(scartiLinea!=null && scartiLinea.containsKey(codCaus)){
        decCaus=codCaus;
      }else{
        decCaus=null;
      }
      
      if(decCaus==null){
        decCaus="CAUSALE NON CODIFICATA";
        addWarning("\n Attenzione causale non codificata --> "
                +codCaus+"per la linea "+codLineaRif+" relativo al gg "+bean.getDataTrnN());
      }
      
      bean.setCausale(decCaus);
      
      //checkUnMisuraArt(conAs400,bean);
      bean.setCdLTrns(codLineaTrs);
      beans.add(bean);
      }
    }
    return beans;
    
  }



  
  private void checkUnMisuraArt(Connection conAs400, IBeanPersCRUD bean) {
    
  }
  
  
  
  private void storeDtScartiOnMvx(Connection conAs400 , List<IBeanPersCRUD> beans,String codLineaRif,Date ggRif){
    if(beans==null)
      return;
    
    PersistenceManager pm=new PersistenceManager(conAs400);
    for (IBeanPersCRUD b:beans){
      try{
        pm.storeDtFromBean(b);  
      }catch(SQLException s){
        _logger.error("Impossibile salvare le informazioni per"+b.toString()+" -->"+s.getMessage());
        addError("SCARTI "+codLineaRif+" per gg: "+ggRif+" - Impossibile salvare i dati relativi allo scarto "+b.toString()+" -->"+s.getMessage());
      }
    }
  }
  
  private void storeDtScartiOnMvx200(List<IBeanPersCRUD> beans,String codLineaRif,Date ggRif){
    Connection conAs400Feb=null;
    
    try{
      
      conAs400Feb=ColombiniConnections.getAs400FebalConnection();
      storeDtScartiOnMvx(conAs400Feb, beans, codLineaRif, ggRif);
      
    } catch(SQLException s){
      addError("Errore in fase di connessione al db FEBALAS -->"+s.getMessage());
    } finally{
      try{
      if(conAs400Feb!=null)
        conAs400Feb.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione con As400Febal -->"+s.getMessage());
      }
    }
  }
  
  
  
  
  private void updateScartiOnTap(Connection conAs400, List<List> infoS) throws SQLException  {
    PreparedStatement ps = null;
    Long id=null;
    try{
      ps=conAs400.prepareStatement(UPD_ZTAPSP); 
      for(List el:infoS){
        try{
          id=ClassMapper.classToClass(el.get(6),Long.class);
          ps.setLong(1, id);
          ps.execute();

        } catch(SQLException s){
          _logger.error("Errore in fase di aggiornamento dello scarto sulla tabella ZTAPSP");
          addError("Errore in fase di aggiornamento dello scarto (ZTASP) con id"+id);
        }
      }
    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabScartiToAs400.class); 

}