/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.calc;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOeeGg.G1P0.IndicatoriNesting;
import colombini.indicatoriOeeGg.G1P0.IndicatoriPantografi;
import colombini.indicatoriOeeGg.G1P2.IndicatoriForatrici;
import colombini.indicatoriOeeGg.G1P2.IndicatoriForatriciBiesse;
import colombini.indicatoriOeeGg.G1P2.IndicatoriImaGalazzano;
import colombini.indicatoriOeeGg.G1P2.IndicatoriSchellLong;
import colombini.indicatoriOeeGg.G1P2.IndicatoriSquadraborda;
import colombini.indicatoriOeeGg.R1P0.IndicatoriCombimaR1P0;
import colombini.indicatoriOeeGg.R1P0.IndicatoriSchellingR1P0;
import colombini.indicatoriOeeGg.R1P0.IndicatoriSortingStationR1P0;
import colombini.indicatoriOeeGg.R1P1.IndicatoriCombimaR1P1;
import colombini.indicatoriOeeGg.R1P1.IndicatoriMawArtec;
import colombini.indicatoriOeeGg.R1P1.IndicatoriMawFianchi2;
import colombini.indicatoriOeeGg.R1P1.IndicatoriSchellingR1P1;
import colombini.indicatoriOeeGg.R1P2.IndicatoriForatriceAlberti;
import colombini.indicatoriOeeGg.R1P2.IndicatoriImballoCappelli;
import colombini.indicatoriOeeGg.R1P2.IndicatoriLineaTavoli;
import colombini.indicatoriOeeGg.R1P2.IndicatoriStrettoiMawPriess;
import colombini.indicatoriOeeGg.R2.IndicatoriHomagPost;
import colombini.indicatoriOeeGg.R2.IndicatoriSQBStefani;
import colombini.indicatoriOeeGg.R2.IndicatoriSezGabbiani;
import colombini.model.CausaliLineeBean;
import colombini.util.DatiColliGgVDL;
import colombini.util.DatiProdUtils;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import elabObj.MapsMessagesElab;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ArrayListSorted;
import utils.ClassMapper;
import utils.FileUtils;
import utils.ListUtils;

/**
 * Classe astratta per gestire la schedulazione dei calcoli degli indicatori OEE
 * @author lvita
 */
public class CalcIndicatoriOeeGg extends MapsMessagesElab{
  
  public final static String P01043="P01043";
  public final static String P01044="P01044";
  public final static String P01045="P01045";
  public final static String P01046="P01046";
  public final static String P01064="P01064";
  public final static String P01065="P01065";
  
  protected List<Date> giorni;
  protected Map lineeToElab;
  
  private Map <Date,DatiColliGgVDL> colliGgVDL; //mappa con chiave il giorno e valore oggettoInfoCollo
  private Map <String,List> lineeLogicheLinea; //mappa con chiave il giorno e valore oggettoInfoCollo
  protected Map <String,Map> causaliLinee;
  
  protected Map <String,Map> percSagomati;
  protected Map <String,Integer> sagomeArticoli;
  
  public CalcIndicatoriOeeGg(List<Date> giorni, Map lineeToElab) {
    super();
    this.giorni = giorni;
    this.lineeToElab = lineeToElab;
    
    colliGgVDL=new HashMap();
    lineeLogicheLinea=new HashMap();
    
    //-----------------------
//    this.mapErrorsElab=new HashMap();
//    this.mapWarningsElab=new HashMap();
  }
  
  
  //----------------------------get e set basici---
  public List<Date> getGiorni() {
    return giorni;
  }

  public void setGiorni(List<Date> giorni) {
    this.giorni = giorni;
  }

  public Map getLineeToElab() {
    return lineeToElab;
  }

  public void setLineeToElab(Map lineeToElab) {
    this.lineeToElab = lineeToElab;
  }

  
  protected void logInfoMsg(String msg){
    _logger.info(msg);
    System.out.println(msg);
  }
  
 
  //-----------------------------------------------------------------------
  
  
  public  void execute(){
    Connection conAs400=null;
    try { 
      conAs400=ColombiniConnections.getAs400ColomConnection();
      
      PersistenceManager apm=new PersistenceManager(conAs400);
      
      execute(apm);
      
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con AS400 :"+ex.getMessage());
    }finally{
      if(conAs400!=null)
        try {
        conAs400.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura di connessione al db");
      }
    }
  }
  
  
  public void execute(PersistenceManager apm){
    if(apm==null ){
      _logger.error("## Connessione al Database nulla !!! SCHEDULAZIONE INDICATORI OEE  ANNULLATA ##");
      addError("XXXXXX", " Connessione al Database nulla !!! SCHEDULAZIONE INDICATORI OEE  ANNULLATA ");
      return;
    }
    
    if(giorni==null || giorni.isEmpty()){
      _logger.error("## Lista giorni da processare vuota !!! SCHEDULAZIONE INDICATORI OEE ANNULLATA ##");
      addError("XXXXXX", "Lista giorni da processare vuota !!! SCHEDULAZIONE INDICATORI OEE  ANNULLATA ");
      return;
    }
    
    preProcessingElab(apm.getConnection());
    
    elabIndicatoriOee(apm);
    
    postProcessingElab(apm.getConnection());
  }
  
  
  protected void preProcessingElab(Connection con){
    // per linee R1
    prepareDatiVDL();
    prepareLineeLogicheStrettR1P2(con);
    prepareFilesMdb();
    prepareMapForSagomati(con);
    loadMapCausaliLineeAttive(con);
    //---
  }
  
  
  
  protected void elabIndicatoriOee(PersistenceManager man){
    
    List<String> stabilimenti=CostantsColomb.getListStabilimenti();
    
    for(String stab :stabilimenti){
      List<String> lineeStab=getListLineeToElab(stab, null);
      if(!lineeStab.isEmpty()){
        logInfoMsg("------------ Stabilimento "+stab+" ------------");
        List<List>  lineeOrd=getListLineeSorted(lineeStab);
        elabIndicatoriOeeLinee(man, lineeOrd);
      }
    }
    
  }
  
  protected void elabIndicatoriOeeLinee(PersistenceManager man,List<List> lineeStab){
    
    for(List infoLinea:lineeStab){
      String codLinea=(String) infoLinea.get(1);
      //codice da togliere quando il passaggio al nuovo metodo sarà completo
      logInfoMsg(" ----- Inizio Elaborazione "+codLinea);
      for(Date data :giorni){
         logInfoMsg("Elaborazione Indicatori Oee per giorno "+data);
         AIndicatoriLineaForOee ioee=getObjIndicatoreOeeLinea(codLinea,data);
         if(ioee==null){
           System.out.println("Attenzione nessuna classe per generazione indicatori linea "+codLinea);
           addError(codLinea, "Attenzione nessuna classe per generazione indicatori linea");
         }else{
           Map param=getParameterCdl(codLinea,data);
           IIndicatoriOeeGg ie=ioee.getIndicatoriOeeLineaGg(man.getConnection(), data, codLinea, param);
           consolidamentoIndicatoriLinea(man, data, ie);
         }
      }
      logInfoMsg(" ----- Fine Elaborazione "+codLinea);
    }
    
  }
  

  
  protected void postProcessingElab(Connection con){
    //
  }
  
  public void prepareMapForSagomati(Connection con){
    percSagomati=loadMapPercPantografi(con); 
    sagomeArticoli=loadSagomeArticoli(con);
    
  }
  
 private Map<String,Integer> loadSagomeArticoli(Connection con){
   Map sagome=new HashMap();
   List<List> l =new ArrayList();
    try {
      ResultSetHelper.fillListList(con, " select trim(ZPARTI),ZPNMSG from mcobmoddta.zoesap ", l);
      for(List record:l){
        String arti=ClassMapper.classToString(record.get(0));
        Integer nsag=ClassMapper.classToClass(record.get(1),Integer.class);
        if(!sagome.containsKey(l)){
          sagome.put(arti, nsag);
        }else{
          addWarning("SAGOMATI", "Articolo già presente in tabella ZOESAP :"+arti);
        }
      }
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso alla tabella ZOESAP -->"+ex.getMessage());
      addError("SAGOMATI", "Errore in fase di lettura della tabella ZOESAP");
    }
   
   return sagome;
  }
//  
  private Map loadMapPercPantografi(Connection con ) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    Map matrice=new HashMap <String,Map>();
    List <String> campiPer=getListPercSagom();
    String qry=" SELECT CENTRO,"+ListUtils.toCommaSeparatedString(getListPercSagom())
            +  " from MCOBMODDTA.RTVOEEMSAG ";
    
    try{
      ps=con.prepareStatement(qry);
      rs=ps.executeQuery();
      while(rs.next()){
        String centro=rs.getString("CENTRO");       
        Map mappaPerc=new HashMap <String,Double> ();
        for(String perc:campiPer){
          Double val=rs.getDouble(perc);
          mappaPerc.put(perc, val);
        }
       matrice.put(centro.trim(),mappaPerc);
      }
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso ai dati relative alla matrice dei sagomati -->"+ex.getMessage());
      addError("SAGOMATI", "Errore in fase di accesso ai dati RTVOEEMSAG");
      
    }finally{
      try{
        if(ps!=null)
          ps.close();
        if(rs!=null)
          rs.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura dello statment -->"+ex.getMessage());
        addWarning("SAGOMATI", "Errore in fase di chiusura dello statment "+ex.toString());
      
      }  
    }
    return matrice;
  }
//  
//  
  private List getListPercSagom(){
    List perc=new ArrayList();
    perc.add(P01043);
    perc.add(P01044);
    perc.add(P01045);
    perc.add(P01046);
    perc.add(P01064);
    perc.add(P01065);
    
    return perc;
  }
  
  
  
  public void loadMapCausaliLineeAttive(Connection conAs400){
    if(lineeToElab==null || lineeToElab.isEmpty())
      return;
    
    causaliLinee=new HashMap();
    Set keys=lineeToElab.keySet();
    Iterator iter =keys.iterator();
    while (iter.hasNext()){
      String nLinea=(String)iter.next();
      String tipoCausale=CausaliLineeBean.FLAGDESC;
      
      if(NomiLineeColomb.SCHELLLONG_GAL.equals(nLinea)  || NomiLineeColomb.SEZGABBIANI.equals(nLinea) 
              || NomiLineeColomb.FORBSKERNEL.equals(nLinea) || NomiLineeColomb.FORBSFTT.equals(nLinea) 
              || NomiLineeColomb.FORBSKERNELBIS.equals(nLinea) || NomiLineeColomb.FORBSFTTBIS.equals(nLinea) ){
        
        tipoCausale=CausaliLineeBean.FLAGCODICE;
      
      
      
        String codLinea=InfoMapLineeUtil.getCodiceLinea(nLinea);
        Map causaliLinea=CausaliLineeBean.getMapCausaliLinea(conAs400, CostantsColomb.AZCOLOM, codLinea, tipoCausale);
        causaliLinee.put(codLinea,causaliLinea);
      }
    }
  }
  
  private void prepareDatiVDL(){
    for(Date data:giorni){
      DatiColliGgVDL ldtc=new DatiColliGgVDL(data);
      colliGgVDL.put(data,ldtc);
    }
  }
  
  private void prepareFilesMdb() {
    if(checkLineaElab(NomiLineeColomb.IMBCAPPELLI)){
      try{
        _logger.info("Copia file "+IndicatoriImballoCappelli.getPathFileMdbSource() +" in corso...");
        FileUtils.copyFile(IndicatoriImballoCappelli.getPathFileMdbSource(), IndicatoriImballoCappelli.getPathFileMdbLocal());
        _logger.info("File copiato");
      }catch(IOException e){
         String msg="Impossibile accedere al file Db.Calcoli Indicatori non possibili --> "+e.getMessage() ;
        _logger.error(msg);
        addError(NomiLineeColomb.IMBCAPPELLI, msg);
      }
    }
    
  }
  
  
  public void consolidamentoIndicatoriLinea(PersistenceManager apm,Date data,IIndicatoriOeeGg dtoee) {
    
    Boolean valid=dtoee.validate();
    addErrors(dtoee.getCdLav(), dtoee.getErrors());
    addWarnings(dtoee.getCdLav(), dtoee.getWarnings());
    
    try{
      _logger.info("Cancellazione indicatori oee per linea : "+dtoee.getCdLav()+" -- gg :"+dtoee.getDataRifN());
      apm.deleteDtFromBean(dtoee);
      //se il bean non ha superato la validazione non procedo al salvataggio 
      if(!valid){
        _logger.error("#-# Dati Indicatori Oee NON SALVATI per "+dtoee.toString()+" : "+dtoee.getErrors());
        return;
      }
      
      _logger.info("Salvataggio indicatori oee per linea : "+dtoee.getCdLav()+" -- gg :"+dtoee.getDataRifN());
      _logger.info(dtoee.getInfo());
      apm.storeDtFromBean(dtoee);
      
    } catch(SQLException ex){
      _logger.error("Problemi nella gestione dei dati per la linea "+dtoee.getCdLav()+" nel giorno "+data+ " -->"+ex.getMessage());
      addError(dtoee.getCdLav(),"Errore in fase di cancellazione/inserimento dei dati di Oee ");
    } 
    
  }
  
  
  /**
   * Torna la classe che si occupa di calcolare gli indicatori per una data linea
   * @param codLinea
   * @param data
   * @return 
   */
  protected  AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea,Date data){
    AIndicatoriLineaForOee ioeeLinea=null;
    
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMAR1P0).equals(codLinea)){
      ioeeLinea=new IndicatoriCombimaR1P0(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P0).equals(codLinea)  ||
             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P0).equals(codLinea) ){
      ioeeLinea=new IndicatoriSchellingR1P0(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SORTSTATIONR1P0).equals(codLinea) ) {
      ioeeLinea=new IndicatoriSortingStationR1P0(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA1R1P1).equals(codLinea)  ||
             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA2R1P1).equals(codLinea) ){
      ioeeLinea=new IndicatoriCombimaR1P1(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P1).equals(codLinea)  ||
             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P1).equals(codLinea) ){
      ioeeLinea=new IndicatoriSchellingR1P1(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWFIANCHI2).equals(codLinea) ) {
      ioeeLinea=new IndicatoriMawFianchi2(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOMAW).equals(codLinea)  ||
             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOPRIESS).equals(codLinea) ){
      ioeeLinea=new IndicatoriStrettoiMawPriess(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMBCAPPELLI).equals(codLinea) ) {
      ioeeLinea=new IndicatoriImballoCappelli(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORALBERTI).equals(codLinea) ) {
      ioeeLinea=new IndicatoriForatriceAlberti(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.TAVOLISCRIV).equals(codLinea) ) {
      ioeeLinea=new IndicatoriLineaTavoli(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL1).equals(codLinea) || 
            InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL2).equals(codLinea)) {
      ioeeLinea=new IndicatoriMawArtec(CostantsColomb.AZCOLOM, data, codLinea);
    //linee R2
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.HOMAGPOST).equals(codLinea)){
      ioeeLinea=new IndicatoriHomagPost(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SEZGABBIANI).equals(codLinea)){
      ioeeLinea=new IndicatoriSezGabbiani(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SQBSTEFANI).equals(codLinea)){
      ioeeLinea=new IndicatoriSQBStefani(CostantsColomb.AZCOLOM, data, codLinea);
    //linee G1
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLLONG_GAL).equals(codLinea)){
      ioeeLinea=new IndicatoriSchellLong(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13200).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13267).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMAGAL_13269).equals(codLinea)){
      ioeeLinea=new IndicatoriImaGalazzano(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SQBL13266).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SQBL13268).equals(codLinea) ){
      ioeeLinea=new IndicatoriSquadraborda(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORMAW).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTMORBIDELLI).equals(codLinea) ){  
      ioeeLinea=new IndicatoriForatrici(CostantsColomb.AZCOLOM, data, codLinea);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNEL).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTT).equals(codLinea) 
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNELBIS).equals(codLinea)
          || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTTBIS).equals(codLinea) ){  
      ioeeLinea=new IndicatoriForatriciBiesse(CostantsColomb.AZCOLOM, data, codLinea);
    } else if(isPantografoG1P0(codLinea) ){  
      ioeeLinea=new IndicatoriPantografi(CostantsColomb.AZCOLOM, data, codLinea);
    } else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.NESTINGGAL).equals(codLinea)){
      ioeeLinea=new IndicatoriNesting(CostantsColomb.AZCOLOM, data, codLinea);
    }
    
    
    return ioeeLinea;
}  
  
  
  private Boolean isPantografoG1P0(String codLinea){
    if( InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493).equals(codLinea)
        || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH2519).equals(codLinea) 
        || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(codLinea)
        || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(codLinea) 
        || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB4001).equals(codLinea)
        || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH4038).equals(codLinea) ) {
      
      return Boolean.TRUE;
    }
    
    return Boolean.FALSE;
  }
  
  /**
   * Metodo per fornire dei parametri particolari per ogni singola linea in un determinato giorno
   * @param codiceLinea
   * @param data
   * @return 
   */
  protected Map getParameterCdl(String codiceLinea,Date data){
    Map param=new HashMap();
     
    if(codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOMAW)) || 
       codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOPRIESS))     ){
       DatiColliGgVDL ldtc=(DatiColliGgVDL) colliGgVDL.get(data);
       List colliGg=ldtc.getListColliLineeLog(lineeLogicheLinea.get(codiceLinea));
       
       param.put(ICalcIndicatoriOeeLinea.LISTCOLLI,colliGg);
       
    }else if(codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SEZGABBIANI))
          || codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLLONG_GAL))  
          || codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNEL))    
          || codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTT)) 
          || codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNELBIS))    
          || codiceLinea.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTTBIS))   ){
      
      param.put(ICalcIndicatoriOeeLinea.CAUSALILINEA,causaliLinee.get(codiceLinea));
      
    } else if(isPantografoG1P0(codiceLinea) ){
      
      param.put(IndicatoriPantografi.MAPPERCENTUALI,percSagomati);
      param.put(IndicatoriPantografi.MAPNSAGARTICOLI,sagomeArticoli);
    }
    
    return param;
  }
  
  public List getListLineeToElab(String stab,String piano){
    return InfoMapLineeUtil.getListLinee(stab, piano, lineeToElab);
  }
  
  public List getListLineeSorted(List<String> linee){
    List sorted=new ArrayListSorted(0);
    for(String nomelinea : linee){
      String codice=InfoMapLineeUtil.getCodiceLinea(nomelinea);
      String stab=InfoMapLineeUtil.getMapLineeOee().get(nomelinea);
      List record=new ArrayList();
      record.add(stab);
      record.add(codice);
      
      sorted.add(record);
    }
    
    
    return sorted;
  }
  
  
  public Boolean checkLineaElab(String linea){
    if(lineeToElab.containsKey(linea))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  
  private void prepareLineeLogicheStrettR1P2(Connection con){
    
    for(String linea:getListLineeStrettoiP2()){ 
       List lineeLog=DatiProdUtils.getInstance().getLineeLogicheMVX(con, linea);
       lineeLogicheLinea.put(linea, lineeLog);
    }
  }
  
  
  private List<String> getListLineeStrettoiP2(){
    List linee=new ArrayList();
    linee.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOMAW));
    linee.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOPRIESS));
    
    
    return linee;
  }
  
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(CalcIndicatoriOeeGg.class);
  
  
}
