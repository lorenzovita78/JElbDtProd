/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.model.persistence.tab.R1.DtMagReintetroImaAnte;
import colombini.model.persistence.tab.R1.DtProdImaAnte;
import colombini.model.persistence.tab.R1.DtProdImaTopGg;
import colombini.model.persistence.tab.R1.DtProdImaTopOra;
import colombini.model.persistence.tab.R1.DtProdSSImaAnte;
import colombini.model.persistence.tab.TabProcessiAs400;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryScartiFromTAP;
import colombini.query.produzione.R1.QueryDtSortingStationImaAnte;
import colombini.query.produzione.R1.QueryInfoPziImaAnte;
import colombini.query.produzione.R1.QueryMagReintegroImaAnte;
import colombini.query.produzione.R1.QueryProdImaTop;
import colombini.query.produzione.R1.QueryPzCtrlImaAnteXCb;
import colombini.query.produzione.R1.QueryPzScaImaAnteXCb;
import colombini.query.produzione.R1.QueryScartiImaAnte;
import colombini.query.produzione.R1.QueryScartiImaTop;
import colombini.query.produzione.R1.QueryStatProdImaTop;
import colombini.util.DatiProdUtils;
import db.CustomQuery;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDatiProdImpImaR1 extends ElabClass{
  
  
  
  public final static String TABGENSCARTIIMAANTE="CO_MMZ8691";
  public final static String TABGENSCARTIIMATOP="CO_MMZ8692";
  public final static String TABGENSCARTIIMALOTTO1="CO_MMZ101R";
  
  private final static String STATMENT_INFO_IMA_TOP=" Select refArt from dbo.Tab_ET where barcode= ?";
  
  
   private final String UPD_ZTAPSP=" UPDATE MCOBMODDTA.ZTAPSP  set TSDTEL=(CURRENT TIMESTAMP)"
                                   +" WHERE TSIDSP=? "
                                   + " AND TSDTEL is null ";
  
  private List<Date> giorni;
  
  private Date dataInizio;
  private Date dataFine;
  
//  private Boolean calcGG=Boolean.FALSE;
//  private Boolean calcOra=Boolean.FALSE;
  
  private Boolean calcDatiProdImaTop;
  private Boolean calcDatiProdImaAnte;
  private Boolean calcScartiImaTop;
  private Boolean calcScartiImaAnte;
  private Boolean calcScartiImaLotto1;
  private Boolean calcScartiImaLotto1XOtt;
  private Boolean calcDatiMagReintegro;
  
  
  
  private Map mapCausScartiImaAnte=null;
  private Map mapCausScartiImaTop=null;
  private Map mapCausScartiImaLotto1=null;
  private Map mapUnMArticoli=null;
  
  
  public ElabDatiProdImpImaR1() {
    this.calcDatiProdImaTop=Boolean.FALSE;
    this.calcDatiProdImaAnte=Boolean.FALSE;
    this.calcScartiImaTop=Boolean.FALSE;
    this.calcScartiImaAnte=Boolean.FALSE;
    this.calcScartiImaLotto1=Boolean.FALSE;
    this.calcDatiMagReintegro=Boolean.FALSE;
    
    this.mapCausScartiImaAnte=new HashMap<String,String>();
    this.mapCausScartiImaTop=new HashMap<String,String>();
    this.mapCausScartiImaLotto1=new HashMap<String,String>();
    //this.mapCausScartiImaLotto1=new HashMap<String,Map>();
    this.mapUnMArticoli=new HashMap<String,String>();
  }
  

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
    
    if(this.getInfoElab().getCode().equals(NameElabs.ELBDTIMPIMAR1)){
      setAllElab(Boolean.TRUE);
    } else {
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDPRODIMAANTER1))
         setCalcDatiProdImaAnte(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDPMAGREINIMAANTE))
         setCalcDatiMagReintegro(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDPRODIMATOPR1))
         setCalcDatiProdImaTop(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDSCARTIIMAANTER1))
         setCalcScartiImaAnte(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDSCARTIIMATOPR1))
         setCalcScartiImaTop(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBDSCARTIIMALOTTO1))
         setCalcScartiImaLotto1(Boolean.TRUE);
      
    }
    
    giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection conAs400) {   
    _logger.info(" -----  Dati Produzione Linee Ima -----");

    //carico staticamente le causali di scarto della Ima Ante
    //so che fa cagare ma ad oggi è il modo più rapido....
    loadCuasScarti(conAs400);
    
    if(giorni==null|| giorni.isEmpty()){
      _logger.warn("Lista giorni da processare vuota . IMPOSSIBILE PROSEGUIRE");
      addWarning(" Impossibile lanciare l'elaborazione --> lista giorni da processare non valorizzata ");
      return;
    }
    
//    //Ima Top
//    if(isCalcDatiProdImaTop()){
//      elabDatiProdImaTop(conAs400);
//    }
    //Ima Ante
    if(isCalcDatiProdImaAnte() || isCalcDatiMagReintegro()){
      elabDatiProdImaAnte(conAs400);
    }
    
    if(isCalcScartiImaAnte())
      elabScartiImaAnte(conAs400);
    
    if(isCalcScartiImaTop())
      elabScartiImaTop(conAs400);

    if(isCalcScartiImaLotto1()){
      // elabScartiImaLotto1(conAs400);
    }
     
    
   
  }

 
  
  
  public List<Date> getGiorni() {
    return giorni;
  }

  public void setGiorni(List<Date> giorni) {
    this.giorni = giorni;
  }

  public Boolean isCalcDatiProdImaTop() {
    return calcDatiProdImaTop;
  }

  public void setCalcDatiProdImaTop(Boolean calcDatiProdImaTop) {
    this.calcDatiProdImaTop = calcDatiProdImaTop;
  }

  public Boolean isCalcDatiProdImaAnte() {
    return calcDatiProdImaAnte;
  }

  public void setCalcDatiProdImaAnte(Boolean calcDatiProdImaAnte) {
    this.calcDatiProdImaAnte = calcDatiProdImaAnte;
  }

  public Boolean isCalcScartiImaTop() {
    return calcScartiImaTop;
  }

  public void setCalcScartiImaTop(Boolean calcScartiImaTop) {
    this.calcScartiImaTop = calcScartiImaTop;
  }

  public Boolean isCalcScartiImaAnte() {
    return calcScartiImaAnte;
  }

  public void setCalcScartiImaAnte(Boolean calcScartiImaAnte) {
    this.calcScartiImaAnte = calcScartiImaAnte;
  }

  public Boolean isCalcScartiImaLotto1() {
    return calcScartiImaLotto1;
  }

  public void setCalcScartiImaLotto1(Boolean calcScartiImaLotto1) {
    this.calcScartiImaLotto1 = calcScartiImaLotto1;
  }

  
  
  
  public Boolean isCalcDatiMagReintegro() {
    return calcDatiMagReintegro;
  }

  public void setCalcDatiMagReintegro(Boolean calcDatiMagReintegro) {
    this.calcDatiMagReintegro = calcDatiMagReintegro;
  }

  
  
  private void setAllElab(Boolean b) {
    setCalcDatiProdImaAnte(b);
    setCalcDatiProdImaTop(b);
    setCalcScartiImaAnte(b);
    setCalcScartiImaTop(b);
    setCalcScartiImaLotto1(b);
    setCalcDatiMagReintegro(b);
  }  
   
  
  private void loadCuasScarti(Connection conAs400) {
    List<List> causIma=DatiProdUtils.getInstance().getElsCsyTab(conAs400, CostantsColomb.AZCOLOM, " ", TABGENSCARTIIMAANTE);
    List<List> causTop=DatiProdUtils.getInstance().getElsCsyTab(conAs400, CostantsColomb.AZCOLOM, " ", TABGENSCARTIIMATOP);
    List<List> causLotto1=DatiProdUtils.getInstance().getElsCsyTab(conAs400, CostantsColomb.AZCOLOM, " ", TABGENSCARTIIMALOTTO1,NomiLineeColomb.CDL_IMALOTTO1);
    List<List> causMMZ101=DatiProdUtils.getInstance().getElsCsyTab(conAs400, CostantsColomb.AZCOLOM, " ", TABGENSCARTIIMALOTTO1);
    
    prepareMapScarti(causTop, mapCausScartiImaTop);
    prepareMapScarti(causIma, mapCausScartiImaAnte);
    //prepareMapScarti(causLotto1, mapCausScartiImaLotto1);
    prepareMapScartiP4(causMMZ101, mapCausScartiImaLotto1); //modificare la struttura della mappa
  
  } 
  
  
  private void prepareMapScarti(List<List> listaC,Map scarti){
    if(listaC==null || listaC.isEmpty())
      return;
    
    for(List rec:listaC){
      scarti.put(ClassMapper.classToString(rec.get(0)),ClassMapper.classToString(rec.get(3)));
    }
    
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
  
  
  private void elabDatiProdImaTop(Connection conAs400){
    Connection conSQLServer=null;
    try { 
        conSQLServer=ColombiniConnections.getDbImaTopConnection();
        _logger.info(" -----  Elaborazione dati produzione Ima Top-----");
        for(Date giorno:giorni){
          elabDatiImaTopGg(giorno, conAs400, conSQLServer);
          elabDatiImaTopOra(giorno, conAs400, conSQLServer);
        }
      } catch (SQLException ex) {
        _logger.error("Impossibile stabilire la connessione con "+ColomConnectionsCostant.SRVFORIMATOP+" -->"+ex.getMessage());
        addError(" DATI PROD IMA TOP - Impossibile stabilire la connessione con il database "+ex.getMessage());
      } finally{
        _logger.info(" ----- Fine  Elaborazione dati produzione Ima Top   -----");
        if(conSQLServer!=null)
          try {
            conSQLServer.close();
          } catch (SQLException ex) {
            _logger.error("Errore in fase di chiusura della connessione al db"+ColomConnectionsCostant.SRVFORIMATOP);
          }
      }
  }
  
  private void elabDatiProdImaAnte(Connection conAs400){
    Connection conSQLServer=null;
    try {
      _logger.info(" -----  Elaborazione dati produzione  Ima Ante -----");
      conSQLServer=ColombiniConnections.getDbImaAnteConnection();
      for(Date giorno:giorni){
        
        if(isCalcDatiProdImaAnte())
          elabDatiImaAnteGg(giorno, conAs400, conSQLServer);
        
        if(isCalcDatiMagReintegro())
          elabDatiMagReintegroGg(giorno, conAs400, conSQLServer);
      }
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con "+ColomConnectionsCostant.SRVFORIMAANTE+" -->"+ex.getMessage());
      addError(" DATI PROD IMA ANTE - Impossibile stabilire la connessione con il database "+ex.getMessage());
    } finally{
      _logger.info(" ----- Fine  Elaborazione dati produzione Ima Ante   -----");
      if(conSQLServer!=null)
        try {
          conSQLServer.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db"+ColomConnectionsCostant.SRVFORIMAANTE);
        }
    }
  }
  
  
  private void elabScartiImaAnte(Connection conAs400){
    Connection conSqlS=null;
    try{
      conSqlS=ColombiniConnections.getDbImaAnteConnection();
      for(Date giorno:giorni){
        List infoS=getListScarti(conSqlS, new QueryScartiImaAnte(), NomiLineeColomb.CDL_IMAANTE,giorno);
        List beans=getBeansMvxScartiFromImpIma(conAs400, infoS, NomiLineeColomb.CDL_IMAANTE, giorno,TabProcessiAs400.TIPORECSCARTI, TabProcessiAs400.REASONCODESCARTOIMAANTE);
        storeDtScartiOnMvx(conAs400, beans, NomiLineeColomb.CDL_IMAANTE, giorno);
      }
    
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso al db  -->"+ex.getMessage());
      addError("SCARTI "+NomiLineeColomb.CDL_IMAANTE+" errori in fase di accesso al db "+ex.toString());
    }finally{
      if(conSqlS!=null){
        try{
          conSqlS.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione "+s.getMessage());
        }
      }
        
    } 
  }
  
  private void elabScartiImaTop(Connection conAs400){
    Connection conSqlS=null;
    try{
      conSqlS=ColombiniConnections.getDbPanotecImbTop();
      
      for(Date giorno:giorni){
        List infoS=getListScarti(conSqlS, new QueryScartiImaTop(), NomiLineeColomb.CDL_IMATOP,giorno);
        updateListScartiP0(infoS);
        List beans=getBeansMvxScartiFromImpIma(conAs400, infoS, NomiLineeColomb.CDL_IMATOP,giorno, TabProcessiAs400.TIPORECSCARTI, TabProcessiAs400.REASONCODESCARTOIMATOP);
        storeDtScartiOnMvx(conAs400, beans, NomiLineeColomb.CDL_IMATOP, giorno);
      }
    
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso al db  -->"+ex.getMessage());
      addError("SCARTI "+NomiLineeColomb.CDL_IMATOP+" errori in fase di accesso al db "+ex.toString());
    }finally{
      if(conSqlS!=null){
        try{
          conSqlS.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione "+s.getMessage());
        }
      }
        
    } 
  }
  
  
  
  private void elabScartiImaLotto1(Connection conAs400) {
    
    
    for(Date giorno:giorni){
      try {
        List infoS=new ArrayList();
        QueryScartiFromTAP q=new QueryScartiFromTAP();
        //q.setFilter(QueryScartiFromTAP.FT_CDLTRANS,"01024");
        q.setFilter(QueryScartiFromTAP.FT_CDLRESP,NomiLineeColomb.CDL_IMALOTTO1);
        q.setFilter(QueryScartiFromTAP.FT_INNERJOIN,"Y");
        q.setFilter(FilterFieldCostantXDtProd.FT_DATADA,DateUtils.getInizioGg(giorno));
        q.setFilter(FilterFieldCostantXDtProd.FT_DATAA,DateUtils.getFineGg(giorno));
        
        ResultSetHelper.fillListList(conAs400, q.toSQLString(), infoS);
        //List beans=getBeansMvxScartiFromImpIma(conAs400, infoS, NomiLineeColomb.CDL_IMALOTTO1, giorno, TabProcessiAs400.TIPORECSCARTI, TabProcessiAs400.REASONCODESCARTOIMALOTTO1);
        List beans=geBeansMvxScartiR1P4(conAs400, infoS, NomiLineeColomb.CDL_IMALOTTO1, giorno, TabProcessiAs400.TIPORECSCARTI, TabProcessiAs400.REASONCODESCARTOIMALOTTO1);
        storeDtScartiOnMvx(conAs400, beans, NomiLineeColomb.CDL_IMALOTTO1, giorno);
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
  }

  
  
  
  private void updateListScartiP0(List<List> infoScarti){
    Connection con =null;
    PreparedStatement st=null;
    ResultSet rs=null;
    if(infoScarti==null || infoScarti.isEmpty())
      return;
    
    try{
      con=ColombiniConnections.getDbImaTopConnection();
      st=con.prepareStatement(STATMENT_INFO_IMA_TOP);
      
      for(List scarto:infoScarti){
        String barcode=ClassMapper.classToString(scarto.get(6));
        st.setString(1, barcode);
        rs=st.executeQuery();
        while(rs.next()){
          scarto.set(1, rs.getString("RefArt"));
        }
      }
    }catch(SQLException s){
      addError("Errore in fase di interrogazione del db Ima Top");
      _logger.error("Errore in fase di interrogazione del db Ima Top -->"+s.getMessage());
    }finally{
      try{
        if(rs!=null)
          rs.close();
        if(st!=null)
          st.close();
        if(con!=null)
        con.close();
      }catch(SQLException s1){
          _logger.error("Errore in fase di chiusura della connessione con il db Ima Top-->"+s1.getMessage());
      }
      }
    }
    
  
  
  private void elabDatiImaTopGg(Date giorno,Connection conAs400,Connection conSQLS){
    QueryProdImaTop qry=new QueryProdImaTop();
    _logger.info(" -----  Elaborazione dati Ima Top giornaliera   -----");
    try{
      List<List> result=new ArrayList();
      String dataR=DateUtils.DateToStr(giorno, "yyyy-MM-dd");
      Long dataM=DateUtils.getDataForMovex(giorno);

      qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
      ResultSetHelper.fillListList(conSQLS, qry.toSQLString(), result);

      List keyvalues=new ArrayList();
      keyvalues.add(CostantsColomb.AZCOLOM);
      keyvalues.add(dataM);

      if (result.isEmpty() ){
        _logger.warn("Dati non presenti per data: "+dataR);
        return ;
      }

      for(List record :result){
        Long totale=ClassMapper.classToClass(record.get(0), Long.class);
        if(totale<=0){
          _logger.warn("Dati non presenti per data: "+dataR);
          return;
        }
        //aggiungo i dati mancanti :azienda data rif,utente data e ora inserimento
        record.add(0, keyvalues.get(0));
        record.add(1, keyvalues.get(1)); 
        record.add(CostantsColomb.UTDEFAULT);
        record.add(new Long(DateUtils.getDataSysString()));
        record.add(DateUtils.getOraSysLong());
        //persistenza dei dati
        _logger.info(result.toString());
        DtProdImaTopGg dt=new DtProdImaTopGg();
        dt.deleteDt(conAs400, keyvalues);
        dt.saveDt(conAs400, result);
        _logger.info("Salvataggio dati completato per gg:"+dataM);
      }

    } catch (ParseException ex) {
     _logger.error("Problemi di conversione della data .Impossibile proseguire");
      addError("DATI PROD IMA TOP - Problemi di conversione dei dati");
    } catch (QueryException ex) {
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMATOP +" per dati giornalieri -->" + ex.getMessage());
     addError("DATI PROD IMA TOP - Problemi nell'esecuzione della query -->"+ex.toString());
    } catch (SQLException ex){
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMATOP +" per dati giornalieri  -->" + ex.getMessage());
     addError("DATI PROD IMA TOP - Problemi di accesso al database -->"+ex.toString());
    } 
    
  }
  
  private void elabDatiImaTopOra(Date giorno,Connection conAs400,Connection conSQLS){
    QueryStatProdImaTop qry=new QueryStatProdImaTop();
    _logger.info(" -----  Elaborazione dati Ima Top oraria   -----");
    try{
      List<List> result=new ArrayList();
      String dataR=DateUtils.DateToStr(giorno, "yyyyMMdd");
      Long dataM=DateUtils.getDataForMovex(giorno);

      qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
      ResultSetHelper.fillListList(conSQLS, qry.toSQLString(), result);

      List keyvalues=new ArrayList();
      keyvalues.add(CostantsColomb.AZCOLOM);
      keyvalues.add(dataM);

      if (result.isEmpty() ){
        _logger.warn("Dati non presenti per data: "+dataR);
        return;
      }
      
      DtProdImaTopOra dt=new DtProdImaTopOra();
      dt.deleteDt(conAs400, keyvalues);

      for(List record :result){
        //aggiungo i dati mancanti :azienda data rif,utente data e ora inserimento
        record.add(0, keyvalues.get(0));
        record.add(1, keyvalues.get(1)); 
        record.add(CostantsColomb.UTDEFAULT);
        record.add(new Long(DateUtils.getDataSysString()));
        record.add(DateUtils.getOraSysLong());

      }
      dt.saveDt(conAs400, result);
      _logger.info("Salvataggio dati completato per gg:"+dataM);

    } catch (ParseException ex) {
     _logger.error("Problemi di conversione della data .Impossibile proseguire");
     addError("DATI PROD IMA TOP - Problemi di conversione dei dati");
    } catch (QueryException ex) {
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMATOP +" per dati orari -->" + ex.getMessage());
     addError("DATI PROD IMA TOP - Problemi nell'esecuzione della query -->"+ex.toString());
    } catch (SQLException ex){
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMATOP +" per dati orari  -->" + ex.getMessage());
     addError("DATI PROD IMA TOP - Problemi di accesso al database -->"+ex.toString());
    } 
      
  }
  
  private void elabDatiMagReintegroGg(Date giorno,Connection conAs400,Connection conSQLS){
    PersistenceManager man=null;
    DtMagReintetroImaAnte dt=new DtMagReintetroImaAnte();
    List l=new ArrayList();
    _logger.info(" -----  Elaborazione dati magazzino reintregro -----");
    try{
      man=new PersistenceManager(conAs400);
      QueryMagReintegroImaAnte qry=new QueryMagReintegroImaAnte();
      qry.setFilter(FilterQueryProdCostant.FTDATARIF, DateUtils.DateToStr(giorno,"yyyy-MM-dd"));
      ResultSetHelper.fillListList(conSQLS, qry.toSQLString(), l);
      
      if(l!=null && !l.isEmpty()){
        Map md=new HashMap();
        md.put(DtMagReintetroImaAnte.MRDTRF, giorno);
        man.deleteDt(dt, md);
        
        man.saveListDt(dt, l);
      }
      
    } catch (SQLException s){
      _logger.error("Errore in fase di lettura dei dati del magazzino reintegro --> "+s.getMessage());
      addError("Errore in fase di lettura dei dati del magazzino reintegro --> "+s.toString());
    } catch (ParseException p) {
      _logger.error("Errore in fase di lettura dei dati del magazzino reintegro --> "+p.getMessage());
      addError("Errore in fase di lettura dei dati del magazzino reintegro --> "+p.toString());
    } catch (QueryException q) {
      _logger.error("Errore in fase di lettura dei dati del magazzino reintegro --> "+q.getMessage());
      addError("Errore in fase di lettura dei dati del magazzino reintegro --> "+q.toString());
    }
    
  }
  private void elabDatiImaAnteGg(Date giorno,Connection conAs400,Connection conSQLS){
    PersistenceManager man=null;
    DtProdImaAnte dt=new DtProdImaAnte();
    try{
      man=new PersistenceManager(conAs400);
      //1 pz prodotti
      //2 pz controllati
      //3 pz controllati per combima
      //4 scarti per combima  
              
      
      Long dataM=DateUtils.getDataForMovex(giorno);
      Map values=dt.getEmptyMapFieds(CostantsColomb.AZCOLOM, dataM);      
      
      Map mapdelete=new HashMap();
      mapdelete.put(DtProdImaAnte.ZACONO,CostantsColomb.AZCOLOM);
      mapdelete.put(DtProdImaAnte.ZADTRF,dataM);
      
      //1 pz prodotti + pz controllati
      loadDatiPzProd(conSQLS, giorno, values);
      //2
      loadDatiPzCombima(conSQLS, giorno, values);
      //3 
      
      if(dt.isValidDt(values)){
        man.deleteDt(dt, mapdelete);
        man.saveMapDt(dt, values);
        _logger.info("Salvataggio dati completato per gg:"+dataM);
      }else{
        _logger.info("Dati non presenti per gg:"+dataM);
      }
      
      

    } catch (ParseException ex) {
     _logger.error("Problemi di conversione della data .Impossibile proseguire");
     addError("DATI PROD IMA ANTE - Problemi di conversione dei dati");
    } catch (QueryException ex) {
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMAANTE +" --> " + ex.getMessage());
     addError("DATI PROD IMA ANTE - Problemi nell'esecuzione della query -->"+ex.toString());
    } catch (SQLException ex){
     _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMAANTE +" --> " + ex.getMessage());
     addError("DATI PROD IMA ANTE - Problemi di accesso al database -->"+ex.toString());
    } finally{
      if(man!=null)
        man=null;
    }
    
  }
  
  
  
  
  private void loadDatiPzProd(Connection conSQLS,Date giorno,Map values) throws ParseException, SQLException, QueryException{
    List<List> result=new ArrayList();
    String s="";
    String dataR=DateUtils.DateToStr(giorno, "yyyy-MM-dd");
    QueryInfoPziImaAnte qry=new QueryInfoPziImaAnte();
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
    qry.setFilter(QueryInfoPziImaAnte.FTPZBORD,"Y");
    
    s=qry.toSQLString(); 
    _logger.debug("Query 1 :"+s);
    ResultSetHelper.fillListList(conSQLS, s, result);
    
    if (result.isEmpty() ){
        _logger.warn("Dati pz prodotti non presenti per data: "+dataR); 
    }
    
    for(List record :result){
      String type=ClassMapper.classToString(record.get(0));
      Integer qta=ClassMapper.classToClass(record.get(1),Integer.class);
      if("1".equals(type))
         values.put(DtProdImaAnte.ZAPSTK,qta);
      else if("2".equals(type))
         values.put(DtProdImaAnte.ZAPCOM,qta);
      else if("6".equals(type))
         values.put(DtProdImaAnte.ZAPSCA,qta);
      else if("8".equals(type))
         values.put(DtProdImaAnte.ZAPREI,qta);
    }
    
    qry=new QueryInfoPziImaAnte();
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
    qry.setFilter(QueryInfoPziImaAnte.FTPZCRTL,"Y");
    result=new ArrayList();
    s=qry.toSQLString();
    _logger.debug("Query 2 :"+s);
    ResultSetHelper.fillListList(conSQLS, s, result);
    
    if (result.isEmpty() ){
        _logger.warn("Dati pz controllati non presenti per data: "+dataR); 
        return;
    }
    
    Integer qta=Integer.valueOf(0);
    for(List record :result){
      qta+=ClassMapper.classToClass(record.get(1),Integer.class);
    }
    values.put(DtProdImaAnte.ZAPCTL, qta);
    
  }
  
  private void loadDatiPzCombima(Connection conSQLS,Date giorno,Map values) throws ParseException, SQLException, QueryException{
    List<List> result=new ArrayList();
    String s="";
    String dataR=DateUtils.DateToStr(giorno, "yyyy-MM-dd");
    QueryPzCtrlImaAnteXCb qry=new QueryPzCtrlImaAnteXCb();
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
    
    s=qry.toSQLString();
    _logger.debug("Query 3 :"+s);
    ResultSetHelper.fillListList(conSQLS, s , result);
    
    if (result.isEmpty() ){
        _logger.warn("Dati pz controllati su combima non presenti per data: "+dataR); 
    }
    
    for(List record :result){
      String station=ClassMapper.classToString(record.get(1));
      Integer qta=ClassMapper.classToClass(record.get(0),Integer.class);
      if("C1".equals(station))
         values.put(DtProdImaAnte.ZAPCB1,qta);
      else if("C2".equals(station))
         values.put(DtProdImaAnte.ZAPCB2,qta);
      
    }
    
    QueryPzScaImaAnteXCb qry2=new QueryPzScaImaAnteXCb();
    qry2.setFilter(FilterQueryProdCostant.FTDATARIF, dataR);
    result=new ArrayList();
    s=qry2.toSQLString();
    _logger.debug("Query 4 :"+s);
    ResultSetHelper.fillListList(conSQLS, s, result);
    if (result.isEmpty() ){
        _logger.warn("Dati pz controllati su combima non presenti per data: "+dataR); 
    }
    Integer totsC1=Integer.valueOf(0);
    Integer totsC2=Integer.valueOf(0);
    for(List record :result){
      
      String station=ClassMapper.classToString(record.get(2));
      String caus=ClassMapper.classToString(record.get(1));
      Integer qta=ClassMapper.classToClass(record.get(0),Integer.class);
      if("C1".equals(station)){
         totsC1+=qta;
         String field="ZAS1"+mapCausScartiImaAnte.get(caus);
         values.put(field,qta);
      }else if("C2".equals(station)){
        totsC2+=qta;
        String field="ZAS2"+mapCausScartiImaAnte.get(caus);
        values.put(field,qta);
      }
    } 
    values.put(DtProdImaAnte.ZAPSC1,totsC1);     
    values.put(DtProdImaAnte.ZAPSC2,totsC2);
      
    
    
  }
  
  
  
  public void elabDatiSortingStationImaAnte(Connection conAs400) {        
    Connection conSQLServer=null;
    
    _logger.info(" -----  Dati Ima Ante -----");
    try { 
      conSQLServer=ColombiniConnections.getDbImaAnteConnection();
      QueryDtSortingStationImaAnte qry=new QueryDtSortingStationImaAnte();
      try{
        List<List> result=new ArrayList();

        String oraRif=DateUtils.getOraSysString();
        Long dtL=DateUtils.getDataSysLong();

        oraRif=oraRif.substring(0, 2);
        Long oraL=new Long(oraRif);
        
        ResultSetHelper.fillListList(conSQLServer, qry.toSQLString(), result);

        if (result.isEmpty() ){
          _logger.warn("Dati non presenti per data: "+dtL + " e ora :"+oraL);
          return ;
        }

        List keyvalues=new ArrayList();
        keyvalues.add(CostantsColomb.AZCOLOM);
        keyvalues.add(dtL);
        keyvalues.add(oraL);
          
        for(List record :result){
          //aggiungo i dati mancanti :azienda, data rif,ora rif
          record.add(0, CostantsColomb.AZCOLOM);
          record.add(1, dtL); 
          record.add(2, oraL);
          
          record.add(CostantsColomb.UTDEFAULT);
          record.add(DateUtils.getDataSysLong());
          record.add(DateUtils.getOraSysLong());
        }  
        //persistenza dei dati
//        _logger.info(result.toString());
        DtProdSSImaAnte dt=new DtProdSSImaAnte();
        dt.deleteDt(conAs400, keyvalues);
        dt.saveDt(conAs400, result);
        _logger.info("Salvataggio dati completato per gg:"+dtL);
        

      } catch (QueryException ex) {
       _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMAANTE +" -->" + ex.getMessage());
      }
      
      
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con "+ColomConnectionsCostant.SRVFORIMAANTE);
    } finally{
      _logger.info(" ----- Fine  Dati Ima Ante   -----");
      if(conSQLServer!=null)
        try {
          conSQLServer.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db"+ColomConnectionsCostant.SRVFORIMAANTE);
        }
    }  
  }
        
//  
  
   
  private List getListScarti(Connection con,CustomQuery qry,String codLinea,Date ggRif){
    List info=new ArrayList();
    try{
      qry.setFilter(FilterQueryProdCostant.FTCDL,codLinea);
      qry.setFilter(FilterQueryProdCostant.FTDATAINI, DateUtils.DateToStr(DateUtils.getInizioGg(ggRif), "yyyyMMdd HH:mm:ss"));
      qry.setFilter(FilterQueryProdCostant.FTDATAFIN, DateUtils.DateToStr(DateUtils.getFineGg(ggRif), "yyyyMMdd HH:mm:ss"));
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), info);
      
    } catch (ParseException ex) {
      _logger.error("Errore in fase di conversione dei dati di tipo data  -->"+ex.getMessage());
      addError("SCARTI "+codLinea+" e gg "+ggRif+" - errori in fase di conversione dei dati"+ex.toString());
    } catch(QueryException q){
      _logger.error("Errore in fase di esecuzione della query per l'estrazion degli scarti  -->"+q.getMessage());
      addError("SCARTI "+codLinea+" e gg "+ggRif+" -errori in fase di lettura dei dati di scarto "+q.toString());
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso al db  -->"+ex.getMessage());
      addError("SCARTI "+codLinea+" e gg "+ggRif+" - errori in fase di accesso al db "+ex.toString());
    }
    
    return info;
  }
  
  
  private List<TabProcessiAs400> getBeansMvxScartiFromImpIma(Connection conAs400,List<List> l,String codLinea,Date ggRif,Integer tipoRec,String reasonCode){
    if(l==null || l.isEmpty()){
      addWarning("Attenzione nessuno scarto presente per linea "+codLinea+" e gg "+ggRif);
      return null; 
    }
      
    
    List<TabProcessiAs400> beans=new ArrayList();
    
    Integer idProc= DateUtils.getDayOfYear(new Date())*DateUtils.getYear(new Date());
    for(List scarto:l){
      
      TabProcessiAs400 bean=new TabProcessiAs400(CostantsColomb.AZCOLOM, TabProcessiAs400.STATOSCARTOTOPROC, "", "JAVA_ELB"+idProc,tipoRec,reasonCode);
      bean.setDataS(ClassMapper.classToString(scarto.get(0)));  //DateUtils.getDataForMovex(ggRif);
      bean.setCodArticolo(ClassMapper.classToString(scarto.get(1)));
      bean.setQta(ClassMapper.classToClass(scarto.get(2), Long.class));
      
      String codCaus=ClassMapper.classToString(scarto.get(3));
      String decCaus=getDecodCausScarto(codCaus, codLinea);
      if(decCaus==null){
        decCaus="CAUSALE NON CODIFICATA";
        addWarning("\n Attenzione causale non codificata --> "
                +codCaus+"per la linea "+codLinea+" relativo al gg "+bean.getDataS());
      }
      
      bean.setCausCod(decCaus);
      String lineId="";
      if(scarto.size()==5){
        bean.setIdProcAcq(ClassMapper.classToString(scarto.get(4)));
        lineId=codLinea;
      }else if(scarto.size()>5){
        String comm=ClassMapper.classToString(scarto.get(4));
        String ncol=ClassMapper.classToString(scarto.get(5));
//        String barc=ClassMapper.classToString(scarto.get(6));
        lineId=codLinea+"    "+comm+" "+ncol;
      }
      bean.setLineId(lineId);  
      checkUnMisuraArt(conAs400,bean);
      
      beans.add(bean);
    }
    
    return beans;
//    PersistenceManager pm=new PersistenceManager(conAs400);
//    for (TabProcessiAs400 b:beans){
//      try{
//        pm.storeDtFromBean(b);  
//      }catch(SQLException s){
//        _logger.error("Impossibile salvare le informazioni per"+b.getDesc()+" -->"+s.getMessage());
//        addError("SCARTI "+codLinea+" per gg: "+ggRif+" - Impossibile salvare i dati relativi allo scarto "+b.getDesc()+" -->"+s.getMessage());
//      }
//    }
//    
    
  }
  
  
  private List<TabProcessiAs400> geBeansMvxScartiR1P4(Connection conAs400,List<List> l,String codLineaRif,Date ggRif,Integer tipoRec,String reasonCode){
    if(l==null || l.isEmpty()){
      addWarning("Attenzione nessuno scarto presente per linea "+codLineaRif+" e gg "+ggRif);
      return null; 
    }
      
    
    List<TabProcessiAs400> beans=new ArrayList();
    
    Integer idProc= DateUtils.getDayOfYear(new Date())*DateUtils.getYear(new Date());
    for(List scarto:l){
      TabProcessiAs400 bean=new TabProcessiAs400(CostantsColomb.AZCOLOM, TabProcessiAs400.STATOSCARTOTOPROC, "", "JAVA_ELB"+idProc,tipoRec,reasonCode);
      bean.setDataS(ClassMapper.classToString(scarto.get(0)));  //DateUtils.getDataForMovex(ggRif);
      bean.setCodArticolo(ClassMapper.classToString(scarto.get(1)));
      bean.setQta(ClassMapper.classToClass(scarto.get(2), Long.class));
      
      String codCaus=ClassMapper.classToString(scarto.get(3));
      String decCaus=null;
      String codLineaTrs=ClassMapper.classToString(scarto.get(8)).trim();
      String comm=ClassMapper.classToString(scarto.get(4));
      String ncol=ClassMapper.classToString(scarto.get(5));
      
      Map scartiLinea=(Map) mapCausScartiImaLotto1.get(codLineaTrs);
      if(scartiLinea!=null && scartiLinea.containsKey(codCaus)){
        //if(codLineaRif.equals(codLineaTrs)){
          decCaus=codCaus;
//        }else{
//          String lineaCaus=(String) scartiLinea.get(codCaus);
//          if(codLineaRif.equals(lineaCaus)){
//            decCaus=codCaus;
//           }else{
//            decCaus=null;
//           }
//        }
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
  
  
  
  private void storeDtScartiOnMvx(Connection conAs400 , List<TabProcessiAs400> beans,String codLineaRif,Date ggRif){
    if(beans==null)
      return;
    
    PersistenceManager pm=new PersistenceManager(conAs400);
    for (TabProcessiAs400 b:beans){
      try{
        pm.storeDtFromBean(b);  
      }catch(SQLException s){
        _logger.error("Impossibile salvare le informazioni per"+b.getDesc()+" -->"+s.getMessage());
        addError("SCARTI "+codLineaRif+" per gg: "+ggRif+" - Impossibile salvare i dati relativi allo scarto "+b.getDesc()+" -->"+s.getMessage());
      }
    }
  }
  
  
  private String getDecodCausScarto(String codCaus,String linea){
    if(StringUtils.IsEmpty(codCaus) || StringUtils.IsEmpty(codCaus))
      return null;
    
    if(linea.equals(NomiLineeColomb.CDL_IMAANTE))
      return (String) mapCausScartiImaAnte.get(codCaus);
    else if(linea.equals(NomiLineeColomb.CDL_IMATOP))               
      return (String) mapCausScartiImaTop.get(codCaus);
    else if(linea.equals(NomiLineeColomb.CDL_IMALOTTO1)) {
      if(mapCausScartiImaLotto1.containsKey(codCaus))
      return codCaus;
    }
    return null;
  }
  
  
  private void checkUnMisuraArt(Connection conAs400, TabProcessiAs400 bean) {
    
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

  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabDatiProdImpImaR1.class); 

  

  
 

  
}
