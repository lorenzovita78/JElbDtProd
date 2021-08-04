/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.utils.ElabDatiOrdiniProdMovex;
import colombini.logFiles.G1P2.LogFileSchellingLong;
import colombini.model.persistence.ProdGgLinea;
import colombini.model.persistence.tab.AvanProdGalTable;
import colombini.model.persistence.tab.G1.DtScartiGalP2;
import colombini.model.persistence.tab.RitardoProdLinee;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.G1.QueryPzProdGalP2;
import colombini.query.produzione.G1.QueryScartiGal;
import colombini.query.produzione.QryOrdProdNonCompl;
import colombini.util.DatiProdUtils;
import colombini.util.InfoMapLineeUtil;
import db.Connections;
import db.ConnectionsProps;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.MapUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDatiProdG1 extends ElabClass{

  private  Date dataRif;
  
  //utilizzato solo in caso di necessità
  private Date dataFineElab;
  
  private Boolean calcAvanProdGal;
  private Boolean calcPanProcSchellLong;
  private Boolean calcScartiGal;
  private Boolean calcPzProdG1P2;
  private Boolean calcCodaLavForG1;
  
  
  public ElabDatiProdG1(){
    this.calcAvanProdGal=Boolean.FALSE;
    this.calcPanProcSchellLong=Boolean.FALSE;
    this.calcScartiGal=Boolean.FALSE;
    this.calcPzProdG1P2=Boolean.FALSE;
    this.calcCodaLavForG1=Boolean.FALSE;
  }

  public Boolean isCalcAvanProdGal() {
    return calcAvanProdGal;
  }

  public void setCalcAvanProdGal(Boolean calcAvanProdGal) {
    this.calcAvanProdGal = calcAvanProdGal;
  }

  public Boolean isCalcPanProcSchellLong() {
    return calcPanProcSchellLong;
  }

  public void setCalcPanProcSchellLong(Boolean calcPanProcSchellLong) {
    this.calcPanProcSchellLong = calcPanProcSchellLong;
  }

  public Boolean isCalcScartiGal() {
    return calcScartiGal;
  }

  public void setCalcScartiGal(Boolean calcScartiGal) {
    this.calcScartiGal = calcScartiGal;
  }

  public Boolean isCalcPzProdG1P2() {
    return calcPzProdG1P2;
  }

  public void setCalcPzProdG1P2(Boolean calcPzProdG1P2) {
    this.calcPzProdG1P2 = calcPzProdG1P2;
  }

  public Boolean isCalcCodaLavForG1() {
    return calcCodaLavForG1;
  }

  public void setCalcCodaLavForG1(Boolean calcCodaLavForG1) {
    this.calcCodaLavForG1 = calcCodaLavForG1;
  }

  
  
  
  private void setAllElab(Boolean b) {
    setCalcAvanProdGal(b);
    setCalcPanProcSchellLong(b);
    setCalcScartiGal(b);
    setCalcPzProdG1P2(b);
    setCalcCodaLavForG1(b);
  }
  
  
  
  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
      
    

    dataRif=(Date) param.get(ALuncherElabs.DATAINIELB);
    
    dataFineElab=(Date) param.get(ALuncherElabs.DATAFINELB);
    
    if(dataRif==null)
      return Boolean.FALSE;
    
    if(dataFineElab==null)
       dataFineElab=dataRif; 
    
    
    if(this.getInfoElab().getCode().equals(NameElabs.ELBDATIPRODG1)){
      setAllElab(Boolean.TRUE);
    } else {
      if (this.getInfoElab().getCode().equals(NameElabs.ELBPPROCSCHELLONGG1))
         setCalcPanProcSchellLong(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBAVANPRODG1))
         setCalcAvanProdGal(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDSCARTIG1))
         setCalcScartiGal(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBPZPRODG1P2))
         setCalcPzProdG1P2(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBCODALAVFORG1))
         setCalcCodaLavForG1(Boolean.TRUE);
    }
    
    
    return  Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    
    if(isCalcAvanProdGal())
      avanProdGalNew(con);
    
    if(isCalcPanProcSchellLong())
      elabPanProcSchLong(con);
    
    if(isCalcScartiGal())
      elabScartiGal(con);
    
    if(isCalcPzProdG1P2())
      elabPzProdG1P2(con);
    
    if(isCalcCodaLavForG1())
      elabCodaForatrici(con);
    
  }
  
  private void elabPzProdG1P2(Connection con ){
      PersistenceManager man=new PersistenceManager(con);
      Date dataT=dataRif;
      while(DateUtils.beforeEquals(dataT, dataFineElab)){
        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13200), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13267), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13269), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORMAW), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNEL), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTT), null, null);

        loadInfoPzProdLinea(man, dataT, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTMORBIDELLI), null, null);

        loadInfoPzProdLinea(man, dataT, "01018", null, null);

        dataT=DateUtils.addDays(dataT, 1);
      }
  }
  
  
  
  
  private void elabCodaForatrici(Connection con){
    PersistenceManager man=new PersistenceManager(con);
    
    loadRitardoLinea(man, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORMAW));
    loadRitardoLinea(man, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNEL));
    loadRitardoLinea(man, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTT));
    loadRitardoLinea(man, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTMORBIDELLI));
    loadRitardoLinea(man,"01018");
  }
  
  private void loadInfoPzProdLinea(PersistenceManager man,Date data,String cdl,String cdlPr,String tipo){
    Long pz=Long.valueOf(0);
    List<ProdGgLinea> listB=new ArrayList();
    Map mapTurni=new HashMap<Date,Object>();
    ProdGgLinea bean=null;
    try{
      List<List> l=ElabDatiOrdiniProdMovex.getInstance().getListDatiProd(man.getConnection(), data, cdl, null, Double.valueOf(1), "000001", "235959", null);
      for(List rec:l){
        String ora=ClassMapper.classToString(rec.get(1));
        Integer qtSc=ClassMapper.classToClass(rec.get(2),Integer.class);
        Integer qtP=ClassMapper.classToClass(rec.get(3),Integer.class);
        
        if(qtSc==null)
          qtSc=Integer.valueOf(0);
        
        if(qtP==null)
          qtP=Integer.valueOf(0);
        
        //per gli ordini urgenti chiusi senza avanzamento di fase assegno l'oraio del mattino
        if("250000".equals(ora))
           ora="080000";
        
        Date dataTmp=DateUtils.strToDate(DateUtils.getDataForMovex(data)+" "+getOraString(ora), "yyyyMMdd HHmmss");
        Date turno=DatiProdUtils.getInstance().getTurno(dataTmp);
        if(!mapTurni.containsKey(turno)){
          bean=new ProdGgLinea(CostantsColomb.AZCOLOM,turno,cdl);
        }else{
          bean=(ProdGgLinea) mapTurni.get(turno);
        }
        bean.addPzProdotti(qtP);
        bean.addPzScartati(qtSc);
        mapTurni.put(turno,bean);
        
      }
      
      listB=MapUtils.mapToValuesList(mapTurni);
      //caricamento dati su Db
      try{
      
        for(ProdGgLinea pgl:listB){
          man.storeDtFromBean(pgl);
        }
      
      }catch(SQLException s){
        _logger.error("Errore in fase di salvataggio dei dati della linea "+cdl+ " --> "+s.getMessage());    
        addError("Errore in fase di salvataggio dei dati di produzione della linea "+cdl+" -->"+s.toString());
      }  
      
      
    } catch(QueryException q){
      _logger.error("Errore in fase di caricamento dati per linea "+cdl+" --> "+q.getMessage());
      addError("Errore in fase di caricamento dei pz prodotti per linea "+cdl+" --> "+q.toString());
    } catch(SQLException s){
      _logger.error("Errore in fase di caricamento dati per linea "+cdl+" --> "+s.getMessage());
      addError("Errore in fase di caricamento dei pz prodotti per linea "+cdl+" --> "+s.toString());
    }
    
    
    
  
  }
  
  private void loadRitardoLinea(PersistenceManager man,String cdl){
    QryOrdProdNonCompl qry=new QryOrdProdNonCompl();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA,CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTCDL, cdl);
    qry.setFilter(FilterQueryProdCostant.FTMAGAZ, "M13");
    
    try{
      Object [] obj=ResultSetHelper.SingleRowSelect(man.getConnection(), qry.toSQLString());
      if(obj!=null && obj.length>0){
        List dati=new ArrayList();
        
        dati.add(CostantsColomb.AZCOLOM);
        dati.add(new Date());
        dati.add(cdl);
        
        RitardoProdLinee rt=new RitardoProdLinee();
        //eliminazione dati 
        man.deleteDtWithKey(rt, dati);
        
        //inserimento dati
        dati.add(ClassMapper.classToClass(obj[0],Integer.class));
        dati.add(new Date());
        
        List l=new ArrayList();
        l.add(dati);
        man.saveListDt(rt, l);
        
      }
    } catch(QueryException q){
      _logger.error("Errore in fase di caricamento dati ritardo per linea "+cdl+" -->"+q.getMessage());
      addError("Errore in fase di caricamento della coda lavori della linea "+cdl+" -->"+q.toString());
    } catch(SQLException s){
      _logger.error("Errore in fase di caricamento dati per linea "+cdl+" -->"+s.getMessage());
      addError("Errore in fase di caricamento della coda lavori della linea "+cdl+" -->"+s.toString());
    }
    
    
  }
  
  
  private String getOraString(String o){
    if(o==null || StringUtils.IsEmpty(o))
      return null;
    if(o.length()==1)
      o="00000"+o;
    else if(o.length()==2)
      o="0000"+o;
    else if(o.length()==3)
      o="000"+o;
    else if(o.length()==4)
      o="00"+o;
    else if(o.length()==5)
      o="0"+o;
    
    return o;
  }
  
  private void elabPanProcSchLong(Connection con){
    try { 
      _logger.info(" -----  Dati Pannelli Utilizzati per turno Schelling Longitudinale -----");
      PersistenceManager pm=new PersistenceManager(con);
      Date dataT=dataRif;
      
      while(DateUtils.beforeEquals(dataT, dataFineElab)){
        LogFileSchellingLong sc=new LogFileSchellingLong(InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.SCHELLLONG_GAL,dataT));
        Map beans=sc.processLogFileForPanels(dataT);
        List listB=MapUtils.mapToValuesList(beans);
        pm.deleteDtFromBeans(listB);
        pm.storeDtFromBeans(listB);
        dataT=DateUtils.addDays(dataT, 1);
      }
      
    } catch (SQLException ex) {
      _logger.error(" Problemi di accesso al database AS400 --> "+ex.getMessage());
    } finally{
      _logger.info(" ----- Fine Dati Pannelli Utilizzati per turno Schelling Longitudinale -----");
    }
  }
  
  private void elabScartiGal(Connection con){
    _logger.info(" -----  Inizio elaborazione dati scarti Galazzano -----");
    Date dataT=dataRif;
    DtScartiGalP2 dtSca=new DtScartiGalP2();
    
    PersistenceManager man=new PersistenceManager(con);
    
//    while(DateUtils.beforeEquals(dataT, dataFin)){
      try{
        _logger.info("Data rif. elaborazione : "+dataT);
        Long pzProd=getPzProdGal(con ,dataT);
        Map mapdelete=new HashMap();
        mapdelete.put(DtScartiGalP2.ZGCONO,CostantsColomb.AZCOLOM);
        mapdelete.put(DtScartiGalP2.ZGDTRF,DateUtils.getDataForMovex(dataT));

       

        String tipo=CostantsColomb.GALAZZANO+CostantsColomb.PIANO2;
        List listScaG1P2=getScartiGal(con,dataT,tipo);  
        Map mappaValG1P2=elabListScarti(listScaG1P2, dataT,tipo,pzProd);
       
        tipo=CostantsColomb.GALAZZANO;
        List listScaG1=getScartiGal(con,dataT,tipo);
        listScaG1P2.addAll(listScaG1);
        Map mappaValAllG1=elabListScarti(listScaG1P2, dataT,tipo,pzProd);
        
        
        if(pzProd!=null && pzProd>0 ){
          man.deleteDt(dtSca, mapdelete);
          man.saveMapDt(dtSca, mappaValG1P2);
          man.saveMapDt(dtSca, mappaValAllG1);
          _logger.info("Salvataggio dati completato per gg:"+dataT);
        }else{
          _logger.info("Dati non presenti per gg:"+dataT);
        }
      }catch(SQLException s){
        _logger.error("Errore in fase di cancellazione/scrittura su db --> "+s.getMessage());
      }
      
//      dataT=DateUtils.addDays(dataT, 1);
//    }
    _logger.info(" -----  Fine elaborazione dati scarti Galazzano -----");
  }
   
   
  

  private Long getPzProdGal(Connection con, Date dataT) {
    Long pzProd=null;
    QueryPzProdGalP2 qry=new QueryPzProdGalP2();
    qry.setFilter(FilterQueryProdCostant.FTDATARIF,DateUtils.getDataForMovex(dataT));
    try {
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>1)
        pzProd=ClassMapper.classToClass(obj[1],Long.class);
      
    } catch (QueryException ex) {
      _logger.error(" Errore in fase di caricamento dati Pz Prodotti -->"+ex.getMessage());
    } catch(SQLException e){
      _logger.error(" Errore in fase di caricamento dati Pz Prodotti -->"+e.getMessage());
    }
    
    return pzProd;
  }
  
  private List getScartiGal(Connection con, Date dataT, String tipo) {
    List<List> scarti=new ArrayList();
    QueryScartiGal qry=new QueryScartiGal();
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, DateUtils.getDataForMovex(dataT));
    
    if(tipo!=null && tipo.contains(CostantsColomb.GALAZZANO+CostantsColomb.PIANO2))
      qry.setFilter(QueryScartiGal.ONLYG1, "YES");
    else if(tipo!=null && tipo.contains(CostantsColomb.GALAZZANO))
      qry.setFilter(QueryScartiGal.ALLG1, "YES");
    
    try {
      ResultSetHelper.fillListList(con, qry.toSQLString(), scarti);
     
    } catch (QueryException ex) {
      _logger.error(" Errore in fase di caricamento dei dati di scarto -->"+ex.getMessage());
    } catch(SQLException e){
      _logger.error(" Errore in fase di caricamento dei dati di scarto -->"+e.getMessage());
    }
    
    
    return scarti;        
  }

  
  
  private Map elabListScarti(List<List> lista,Date dataT,String tipo,Long pzProd){
    DtScartiGalP2 dt=new DtScartiGalP2();
    Map mappa=dt.getEmptyMapFieds(CostantsColomb.AZCOLOM, DateUtils.getDataForMovex(dataT),tipo, pzProd);
    
    for(List rec:lista){
      String caus=ClassMapper.classToString(rec.get(1)).trim();
      Integer val=ClassMapper.classToClass(rec.get(2), Integer.class);
      if(mappa.containsKey(DtScartiGalP2.PREFIX+caus)){
        Integer valT=(Integer) mappa.get(DtScartiGalP2.PREFIX+caus);
        mappa.put(DtScartiGalP2.PREFIX+caus, val+valT);
      }else{
        mappa.put(DtScartiGalP2.PREFIX+caus, val);
      }
    }
    
    
    return mappa;
  }
  
  
  
  private void avanProdGalNew(Connection con){
    _logger.info(" -----  Dati Avanzanmento Produzione Galazzano  -----");
    PersistenceManager pm=null;
    Long totPianificato=Long.valueOf(0);
    Long totProdotto=Long.valueOf(0);
    try { 
      _logger.info(" -----  Dati Avanzanmento Produzione Galazzano  -----");
      _logger.info(" Copia prevendiva del file access");
      String pathFileSource=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.FSOURCE_PANNELLIGAL));
      String pathLocal=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PATHLOCALE_PANNELLIGAL));
      String urlODBC=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.ODBC_PANNELLIGAL));
      String nFile=FileUtils.getShortFileName(pathFileSource);
      
      
      FileUtils.copyFile(pathFileSource, pathLocal+nFile);
      
      List schedAccess=getBacklog(urlODBC);
     
      if(schedAccess!=null && schedAccess.size()==1){
        totPianificato=ClassMapper.classToClass( ((List)schedAccess.get(0)).get(0),Long.class);
        totProdotto+=ClassMapper.classToClass( ((List)schedAccess.get(0)).get(1),Long.class);
      }else{
        _logger.info("Dati di produzione  non presenti in pannelli.mdb");
        addWarning("Dati di produzione  non presenti in pannelli.mdb");
      }
      
      _logger.info("Totale Pianificato : "+totPianificato + " -- Totale Prodotto " +totProdotto );
      List infoToSave=new ArrayList();
      AvanProdGalTable avp=new AvanProdGalTable();
      pm=new PersistenceManager(con);
      
      List rec=new ArrayList();
      rec.add(CostantsColomb.AZCOLOM);
      rec.add(DateUtils.RemoveTime(dataRif));
      pm.deleteDtWithKey(avp, rec);
      
      rec.add(totPianificato);
      rec.add(totProdotto);
      rec.add(new Date());
      infoToSave.add(rec);
      
      
      _logger.info("Salvataggio dati");
      pm.saveListDt(avp, infoToSave);
      
    } catch(IOException e){
      addError(" Problemi in fase di accesso al file mdb --> "+e.toString());
    } catch(SQLException s){
      addError(" Problemi in fase di salvataggio dei dati --> "+s.toString());
    }  finally{
      _logger.info(" ----- Fine Dati Avanzanmento Produzione Galazzano  -----");
      pm=null;
      
    } 
     
  }
  
  /**Torna il backlog dello stato attuale del sistema
   * 
   * @param urlODBC
   * @return 
   */
  private List getBacklog(String urlODBC){
    
    Connection conAccess=null;
    List schedulazioniACS=new ArrayList();
   
    
    try {
      addInfo(" Connessione a file access ");
      conAccess=Connections.getAccessDBConnection(urlODBC,null,null);
      addInfo(" Caricamento dati pianificazione da dbAccess per giorno "+dataRif);
      //String dataS=DateUtils.DateToStr(dataRif, "dd/MM/yyyy");
      
      String query="SELECT  Sum(PANNELLI_IMPEGNATI.QTA) as pianificati,Sum(PANNELLI_SCARICATI.SCARICATO) AS scaricati \n" +
                   " FROM PANNELLI_IMPEGNATI LEFT JOIN PANNELLI_SCARICATI ON PANNELLI_IMPEGNATI.PIANIFICAZIONE=PANNELLI_SCARICATI.PIANIFICAZIONE ";
      
     ResultSetHelper.fillListList(conAccess, query, schedulazioniACS);
      
    }catch (SQLException s){
      addError(" Problemi di accesso al file access :"+urlODBC+ " --> "+s.toString());
    
    } finally{
      if(conAccess!=null)
        try {
        conAccess.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura di connessione al db access");
      }  
    }
    
    return schedulazioniACS; 
    
  }
  
  
//private void avanProdGal(Connection con){
//    _logger.info(" -----  Dati Avanzanmento Produzione Galazzano  -----");
//    PersistenceManager pm=null;
//    Long totPianificato=Long.valueOf(0);
//    Long totProdotto=Long.valueOf(0);
//    try { 
//      _logger.info(" -----  Dati Avanzanmento Produzione Galazzano  -----");
//      _logger.info(" Copia prevendiva del file access");
//      String pathFileSource=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.FSOURCE_PANNELLIGAL));
//      String pathLocal=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PATHLOCALE_PANNELLIGAL));
//      String urlODBC=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.ODBC_PANNELLIGAL));
//      String nFile=FileUtils.getShortFileName(pathFileSource);
//      
//      FileUtils.copyFile(pathFileSource, pathLocal+nFile);
//      
//      List<List> schedAccess=getDatiSchedFromAccess(urlODBC);
//      if(schedAccess==null || schedAccess.isEmpty()){
//        addError("Nessuna schedualazione presente per data "+dataRif+ " su db Access");
//        return; 
//      }
//      
//      pm=new PersistenceManager(con);
//      List<BigDecimal> schedAs400=new ArrayList();
//      addInfo(" Caricamento lista ordini di produzione su As400 ");
//      ResultSetHelper.fillListList(con, getQuerySchedOnAs(), schedAs400);
//      
//      for(BigDecimal idSched : schedAs400){
//        List infoS=getInfoSched(idSched.intValue(), schedAccess);
//        if(infoS!=null && infoS.size()==2){
//          totPianificato+=(Long) infoS.get(0);
//          totProdotto+=(Long) infoS.get(1) ;
//        }else{
//          _logger.info("Schedulazione "+idSched+" non presente in pannelli.mdb");
//        }
//      }
//      _logger.info("Totale Pianificato : "+totPianificato + " -- Totale Prodotto " +totProdotto );
//      List infoToSave=new ArrayList();
//      AvanProdGalTable avp=new AvanProdGalTable();
//      
//      List rec=new ArrayList();
//      rec.add(CostantsColomb.AZCOLOM);
//      rec.add(DateUtils.RemoveTime(dataRif));
//      pm.deleteDtWithKey(avp, rec);
//      
//      rec.add(totPianificato);
//      rec.add(totProdotto);
//      rec.add(new Date());
//      infoToSave.add(rec);
//      
//      
//      _logger.info("Salvataggio dati");
//      pm.saveListDt(avp, infoToSave);
//      
//    } catch(IOException e){
//      addError(" Problemi in fase di accesso al file mdb --> "+e.toString());
//    } catch (SQLException ex) {
//      addError(" Problemi in fase di lettura dei dati da As400 --> "+ex.toString());
//    } finally{
//      _logger.info(" ----- Fine Dati Avanzanmento Produzione Galazzano  -----");
//      pm=null;
//      
//    } 
//     
//  }  
 
//  private List getDatiSchedFromAccess(String urlODBC){
//    
//    Connection conAccess=null;
//    List<List> schedulazioniACS=new ArrayList();
//    
//    try {
//      addInfo(" Connessione a file access ");
//      conAccess=Connections.getAccessDBConnection(urlODBC,null,null);
//    
//      addInfo(" Caricamento lista pianificazioni su dbAccess ");
//      String dataS=DateUtils.DateToStr(dataRif, "dd/MM/yyyy");
//      
//      ResultSetHelper.fillListList(conAccess, getQuerySchedOnAccess(dataS), schedulazioniACS);
//      
//      stampaLista(schedulazioniACS);    
//      
//    }catch (SQLException s){
//      addError(" Problemi di accesso al file access :"+urlODBC+ " --> "+s.toString());
//    }catch (ParseException p){
//      addError(" Problemi in fase di decodifica della data --> "+p.toString());
//    } finally{
//      if(conAccess!=null)
//        try {
//        conAccess.close();
//      } catch (SQLException ex) {
//        _logger.error("Errore in fase di chiusura di connessione al db access");
//      }  
//    }
//    
//    return schedulazioniACS; 
//    
//  }
//  
//  
//  
//  
//  private List getInfoSched(Integer idSched,List<List> infoSched){
//    List rec=new ArrayList();
//    for(List record:infoSched ){
//      Integer idSchedTmp=ClassMapper.classToClass(record.get(1),Integer.class);
//      if(idSchedTmp.equals(idSched)){
//        rec.add(record.get(3)!=null ? ClassMapper.classToClass(record.get(3),Long.class) : Long.valueOf(0));
//        rec.add(record.get(4)!=null ? ClassMapper.classToClass(record.get(4),Long.class) : Long.valueOf(0));
//        break;
//      }
//    }
//    
//    
//    
//    return rec;
//  }
//  
//  private String getQuerySchedOnAs(){
//    StringBuilder qry=new StringBuilder();
//    qry.append(" select distinct vhschn ").append(
//              "\n from (mvxbdta.ZMWOHE1J inner join mvxbdta.mitmas on vhprno=mmitno) ").append( 
//              " inner join mvxbdta.mschma on mvxbdta.mschma.hsschn=vhschn  ").append(
//              "\n where vhwhlo = 'M13' and mvxbdta.mschma.hstx40 like 'G %' ").append( 
//              "\n union ").append(
//              "\n select distinct vhschn ").append(
//              "\n from mvxbdta.mwohedu2 left join mvxbdta.mitloc00 on mlcono=vhcono and int(substr(mlbrem , 1 , 9))=vhwosq ").append(  
//              "\n inner join mvxbdta.mitmas on vhcono = mmcono and vhprno=mmitno ").append(
//              " inner join mvxbdta.mschma on vhcono = hscono and vhschn=hsschn ").append(
//              "\n where mlcono=30 and vhcono=30 and vhfaci='F01' and vhwhlo='M13' and mlwhlo = 'M13' ").append(
//              " and substr(mlitno , 1 , 1) in ('1','2','3','4','5','6','7','8','9') and mlalqt=0  ").append(
//              " and mlstqt>0 and mlbrem<>'' ");
//
//    return qry.toString();
//  }
//
//
//  private String getQuerySchedOnAccess(String dataS){
//    StringBuilder qry=new StringBuilder();
//    qry.append(" SELECT TOTALE_PIANIFICATO_PIAN.* , TOTALE_SCARICO_PIAN.totale_scaricato ").append(
//               "\n FROM TOTALE_PIANIFICATO_PIAN LEFT JOIN TOTALE_SCARICO_PIAN ").append(
//               "\n ON TOTALE_PIANIFICATO_PIAN.NUMSCH=TOTALE_SCARICO_PIAN.PIANIFICAZIONE").append(
//               "\n where 1=1 and TOTALE_PIANIFICATO_PIAN.DATA<=DateValue('").append(
//                         dataS).append("') ").append(        
//               "\n  order by 2");
//
//    return qry.toString();
//  }
//  
//  
//  private void stampaLista(List<List> schedAccess) {
//    System.out.println("Lista schedulazioni da db");
//    System.out.println("DATA;ID;DESCR;PIAN;PROD");
//    
//    for(List record:schedAccess){ 
//     if(record.size()<5){
//      //_logger.warn("riga non completa --> "+record.toString());  
//     }else{
//       String s=ClassMapper.classToString(record.get(0))+";"+
//              ClassMapper.classToString(record.get(1))+";"+ 
//              ClassMapper.classToString(record.get(2))+";"+
//              ClassMapper.classToString(record.get(3))+";"+
//              ClassMapper.classToString(record.get(4))+";";
//       //System.out.println(s);
//       System.out.println(s);
//     }
//   }
//    System.out.println("-------------------------------");
//  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabDatiProdG1.class); 

 
  
}