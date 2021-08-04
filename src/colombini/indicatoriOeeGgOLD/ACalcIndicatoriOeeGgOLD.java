/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGgOLD;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.indicatoriOee.utils.FermiGgUtils;
import colombini.model.CausaliLineeBean;
import colombini.model.persistence.FermiGgLineaBean;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.FileXlsDatiProdStd;
import elabObj.MapsMessagesElab;
import java.io.IOException;
import java.sql.Connection;
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
import utils.DateUtils;

/**
 * Classe astratta per gestire la schedulazione dei calcoli degli indicatori OEE
 * @author lvita
 */
public abstract class ACalcIndicatoriOeeGgOLD extends MapsMessagesElab{
  
  
  protected List<Date> giorni;
  protected Map lineeToElab;
  
  protected Map <String,Map> causaliLinee;
 
  //  protected Map 

  public ACalcIndicatoriOeeGgOLD(List<Date> giorni, Map lineeToElab) {
    super();
    this.giorni = giorni;
    this.lineeToElab = lineeToElab;
    
    this.causaliLinee=new HashMap();
    
    this.mapErrorsElab=new HashMap();
    this.mapWarningsElab=new HashMap();
  }
  
  
  
  
  /**
   * Metodo astratto che deve essere reimplementato per la gestione della singola linea
   * @param conAs400 
   */
  protected abstract void elabIndicatoriOeeLinee(PersistenceManager apm);
  
  //per nuova gestioni dati di produzione 01/04/2017
  
  
  
  
  
  protected abstract AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea,Date data);
    
  
  
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

  public Map getCausaliLinea(String linea){
    return causaliLinee.get(linea);
  }
  
  public List getListLineeToElab(String stab,String piano){
    return InfoMapLineeUtil.getListLinee(stab, piano, lineeToElab);
  }

  protected void logInfoMsg(String msg){
    _logger.info(msg);
    System.out.println(msg);
  }
  
  //------------------------------------------------------------------------
  
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
    
  
    
    elabIndicatoriOeeLinee(apm);
    

  }
  
  
  protected void preProcessingElab(Connection con){
    loadMapCausaliLineeAttiveOld(con);
  }
  
  
  
  
  public Boolean checkLineaElab(String linea){
    if(lineeToElab.containsKey(linea))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public void loadMapCausaliLineeAttiveOld(Connection conAs400){
    if(lineeToElab==null || lineeToElab.isEmpty())
      return;
    
    Set keys=lineeToElab.keySet();
    Iterator iter =keys.iterator();
    while (iter.hasNext()){
      String nLinea=(String)iter.next();
      String tipoCausale=CausaliLineeBean.FLAGDESC;
      
      if(NomiLineeColomb.SCHELLLONG_GAL.equals(nLinea)  || NomiLineeColomb.SEZGABBIANI.equals(nLinea) || NomiLineeColomb.FORBSKERNEL.equals(nLinea)
              || NomiLineeColomb.FORBSFTT.equals(nLinea)){
        
        tipoCausale=CausaliLineeBean.FLAGCODICE;
      }
      
      
      String codLinea=InfoMapLineeUtil.getCodiceLinea(nLinea);
      Map causaliLinea=CausaliLineeBean.getMapCausaliLinea(conAs400, CostantsColomb.AZCOLOM, codLinea, tipoCausale);
      causaliLinee.put(codLinea,causaliLinea);
    }
  }
  
  /**
   * Calcolo dei dati di Oee standard
   * @param apm
   * @param calcolatoreInd
   * @param xls
   * @param nomeLinea 
   */
  public void elabIndicatoriOeeLineaStdOld(PersistenceManager apm,ICalcIndicatoriOeeLinea calcolatoreInd,FileXlsDatiProdStd xls,String nomeLinea){

    Map param=new HashMap();
    ACalcIndicatoriOeeGgOLD.this.elabIndicatoriOeeLineaStdOld(apm, calcolatoreInd, xls, nomeLinea, param);
  }
  

  /**
   * Calcolo dati Oee con passaggio di parametri aggiuntivi
   * @param apm
   * @param calcolatoreInd
   * @param xls
   * @param nomeLinea
   * @param Map parameter  mappa dei parametri che poi viene ampliata dal metodo
   */
  public void elabIndicatoriOeeLineaStdOld(PersistenceManager apm,ICalcIndicatoriOeeLinea calcolatoreInd,FileXlsDatiProdStd xls,String nomeLinea,Map parameter){
    _logger.info("-- ##Elaborazione "+nomeLinea+" --");
    String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
    try{
      xls.openFile();
      
      
      parameter.put(ICalcIndicatoriOeeLinea.FILEXLS, xls);
      parameter.put(ICalcIndicatoriOeeLinea.CAUSALILINEA,causaliLinee.get(codLinea));

      
      for(Date data: giorni){
        try{
          parameter.put(ICalcIndicatoriOeeLinea.LISTFERMIOEE,xls.getListaFermiGg(data));
          parameter.put(ICalcIndicatoriOeeLinea.LISTFERMITOT,xls.getListaFermiTotGg(data));
          calcIndicatoriOeeLineaGgOld(apm, data, codLinea, parameter, calcolatoreInd);
        } catch (Exception e){
          _logger.error(" Errore generico in fase di calcolo degli Indicatori Oee per la linea "+nomeLinea+ " --> "+e.getMessage());
          addError(codLinea, " Errore nel calcolo degli Indicatori di Oee per il giorno "+data+ " --> "+e.getMessage());
        }
      }
    } catch(IOException e){
      _logger.error(" Attenzione problema di accesso al file xls "+xls.getFileName()+ " -->"+e.getMessage());
      addError(codLinea, " Errore in fase di accesso al file xls "+xls.getFileName());
    } finally{
      if(xls!=null)
        try {
        xls.closeFile();
      } catch (IOException ex) {
        _logger.error(" Attenzione problema in fase di chiusura del file "+xls.getFileName()+ " -->"+ex.getMessage());
      }
      
      _logger.info("-- ##Elaborazione "+nomeLinea+"  terminata --");
    }
    
  }
  
  /**
   * Lancia la classe deputata a calcolare gli Indicatori della Linea.
   * Storicizza i dati relativi ai fermi e agli indicatori della linea per la data fornita
   * @param apm
   * @param data
   * @param codLinea
   * @param parameter
   * @param ICalcIndicatoriOeeLinea il 
   */
  public void calcIndicatoriOeeLineaGgOld(PersistenceManager apm,Date data,String codLinea,Map parameter,ICalcIndicatoriOeeLinea il) {
    //elabora tutti i dati della linea e salva il contenuto nei relativi archivi
    _logger.info("-- Elaborazione giorno :"+ data);
    System.out.println("Linea : "+codLinea+" -- Elaborazione giorno :"+ data);
    List fermiggOee=(List) parameter.get(ICalcIndicatoriOeeLinea.LISTFERMIOEE);
    List fermiggTot=(List) parameter.get(ICalcIndicatoriOeeLinea.LISTFERMITOT);
    if(fermiggOee==null){
      _logger.warn("Per la linea "+ codLinea+" in data "+data+" la lista dei fermi è vuota");
      addWarning(codLinea, " Fermi non presenti per il giorno "+DateUtils.getDataForMovex(data));
    }
    Map causaliLinea=(Map) causaliLinee.get(codLinea);
    if(causaliLinea==null || causaliLinea.isEmpty()){
      String msg="Causali Fermi per linea "+codLinea + " non presenti. Impossibile storicizzare i fermi giornalieri";
      addError(codLinea, msg);
      _logger.error(msg);
    }
   
    //fermi per il calcolo Oee
    List fermiBeans=FermiGgUtils.getInstance().getListBeanFermi(data, fermiggOee, causaliLinea);
    Map fermiMap=FermiGgUtils.getInstance().getMapTotaliFermiTipo(fermiBeans);
    if(fermiMap==null)
      fermiMap=new HashMap();

    parameter.put(ICalcIndicatoriOeeLinea.MAPTOTFERMI,fermiMap);
    IIndicatoriOeeGg dtOee=il.getIndicatoriOeeLineaGg(apm.getConnection(), data, codLinea, parameter);
    //fermi della giornata
    fermiBeans=FermiGgUtils.getInstance().getListBeanFermi(data, fermiggTot, causaliLinea);
    fermiMap=FermiGgUtils.getInstance().getMapTotaliFermiTipo(fermiBeans);

    consolidamentoFermiLineaOld(apm, data, codLinea, fermiBeans);
    consolidamentoIndicatoriLinea(apm, data, dtOee);

    
  }
  
  
  public void consolidamentoFermiLineaOld(PersistenceManager pm,Date data,String codLinea,List beans) {
     //cancellazione dei dati
    if(beans==null || beans.isEmpty()){
      _logger.info("#-# "+codLinea+" e gg "+data.toString()+" Consolidamento fermi NON ESEGUITO . Fermi giornalieri non presenti");
      return;
    }
    
    FermiGgLineaBean fm=new FermiGgLineaBean();
    fm.setAzienda(CostantsColomb.AZCOLOM);
    fm.setCentroLavoro(codLinea);
    fm.setDataRifN(DateUtils.getDataForMovex(data));
    
    try{
      _logger.info("Cancellazione fermi per linea "+codLinea+" e gg "+data.toString());
      pm.deleteDtFromBean(fm);
      _logger.info("Salvataggio fermi per linea "+codLinea+" e gg "+data.toString());
      pm.storeDtFromBeans(beans);
      
    } catch(SQLException ex){
      addError(codLinea,"Errore in fase di cancellazione/inserimento dei dati relativi ai fermi ");
      _logger.error(" Problemi in fase di cancellazione/inserimento dei dati relativi ai fermi della linea --> "+ex.getMessage());
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
  
  
  
//  public Map  elabFermiGgLinea(Connection conAs400,Date data,String linea, List fermiListGg) throws SQLException{
//    Long dataN=DateUtils.getDataForMovex(data);
//    Boolean isOk=Boolean.TRUE;
//    _logger.info("-- Elaborazione fermi linea "+linea );
//    
//    Map causaliLinea=(Map) causaliLinee.get(linea);
//    if(causaliLinea==null || causaliLinea.isEmpty()){
//      _logger.error("Causali Fermi per linea"+linea + " non presenti. Impossibile storicizzare i fermi giornalieri");
//      isOk=Boolean.FALSE;
//      return null;
//    }
//    
//    GestoreFermiGg fg= new GestoreFermiGg(dataN,CostantsColomb.AZCOLOM, linea);
//    fg.elabOccorrrenzeFermi(fermiListGg, causaliLinea);
//    fg.gestDtFLGg(conAs400);
//    
//    return fg.getTotFermiTipo();
//  }
  
  
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ACalcIndicatoriOeeGgOLD.class);
  
  
}
