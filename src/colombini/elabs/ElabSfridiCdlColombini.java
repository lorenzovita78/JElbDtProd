/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.DatiSfridoCombicutR1P4;
import colombini.dtProd.sfridi.InfoSfridoCdL;
import colombini.dtProd.sfridi.DatiSfridoImaAnte;
import colombini.dtProd.sfridi.DatiSfridoImaTop;
import colombini.dtProd.sfridi.DatiSfridoOtmGal;
import colombini.dtProd.sfridi.DatiSfridoOtmNesting;
import colombini.dtProd.sfridi.DatiSfridoSezBiesseR1P4;
import colombini.dtProd.sfridi.DatiSfridoSezR2;
import colombini.dtProd.sfridi.ISfridoInfo;
import colombini.model.persistence.tab.SfridiCdlDettaglio;
import colombini.model.persistence.tab.SfridiProdPersistence;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.ElabException;
import fileXLS.XlsXCsvFileGenerator;
import java.sql.Connection;
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
public class ElabSfridiCdlColombini  extends ElabClass{
  
  private Date dataInizio;
  private Date dataFine;
  
  private Boolean elabImaAnte;
  private Boolean elabImaTop;
  private Boolean elabOtmG1;
  private Boolean elabSezR2;
  private Boolean elabOtmNesting;
  private Boolean elabSezR1P4;
  private Boolean elabCombicutR1P4;
  
  public ElabSfridiCdlColombini(){
    super();
    
    this.elabOtmG1=Boolean.FALSE;
    this.elabImaAnte=Boolean.FALSE;
    this.elabSezR2=Boolean.FALSE;
    this.elabImaTop=Boolean.FALSE;
    this.elabOtmNesting=Boolean.FALSE;
    this.elabSezR1P4=Boolean.FALSE;
    this.elabCombicutR1P4=Boolean.FALSE;
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
    
    if(this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDGRUPPO)){
      setAllElab(Boolean.TRUE);
    } else {
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDOTMGAL))
         setElabOtmG1(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDNESTG1))
         setElabOtmNesting(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDSEZR2))
         setElabSezR2(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDIMAANTE))
         setElabImaAnte(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDIMATOP))
         setElabImaTop(Boolean.TRUE);
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDSEZR1P4))
         setElabSezR1P4(Boolean.TRUE);
       if (this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDCCUTR1P4))
           setElabCombicutR1P4(Boolean.TRUE);
    }
    
    return Boolean.TRUE;
  }

  

  @Override
  public void exec(Connection con) {
    
    PersistenceManager manP=null;
    
    manP=new PersistenceManager(con);
    
    if(elabOtmG1){
       _logger.info(" ----- Elaborazioni Dati Sfrido Ottimizzatore Galazzano -----");
       DatiSfridoOtmGal dsOtGal=new DatiSfridoOtmGal(ISfridoInfo.OTMGALP2);
       dsOtGal.setParams(getElabProperties());
       elabDatiSfridoLinea(manP, dsOtGal);        
       _logger.info("----- Elaborazione Dati Sfrido Ottimizzatore Galazzano  terminata correttamente ----");
    }

    if(elabImaAnte){
      _logger.info(" ----- Elaborazioni Dati Sfrido Ima Ante R1 ----- ");
      DatiSfridoImaAnte dsIAR1=new DatiSfridoImaAnte(ISfridoInfo.OTMR1P1);
      elabDatiSfridoLinea(manP, dsIAR1);        
      _logger.info("----- Elaborazione Dati Sfrido Ima Ante R1 terminata correttamente ----");
    }
//
    if(elabImaTop){
       _logger.info(" ----- Elaborazioni Dati Sfrido Ima Top R1 ----- ");
      DatiSfridoImaTop dsITR1=new DatiSfridoImaTop(ISfridoInfo.OTMR1P0);
      elabDatiSfridoLinea(manP, dsITR1);        
      _logger.info("----- Elaborazione Dati Sfrido Ima Top R1 terminata correttamente ----");
    }

    if(elabSezR2){
      _logger.info(" ----- Elaborazioni Dati Sfrido Sezionatrice R2 ----- ");
      DatiSfridoSezR2 dsSzR2=new DatiSfridoSezR2(ISfridoInfo.SEZR2P1);
      elabDatiSfridoLinea(manP, dsSzR2);        
      _logger.info("----- Elaborazione Dati Sfrido Sezionatrice R2 terminata correttamente ----");
    }  
//
    if(elabOtmNesting){
      _logger.info(" ----- Elaborazioni Dati Sfrido Nesting Galazzno ----- ");
      DatiSfridoOtmNesting dsOtmNe=new DatiSfridoOtmNesting(ISfridoInfo.OTMGALP0);
      elabDatiSfridoLinea(manP, dsOtmNe );
      _logger.info("----- Elaborazione Dati Sfrido Nesting Galazzno  terminata correttamente ----");
    }
    if(elabSezR1P4){
      XlsXCsvFileGenerator f=null;
      _logger.info(" ----- Elaborazioni Dati Sfrido Sezionatrice R1 P4 ----- ");
      DatiSfridoSezBiesseR1P4 dsR1P4=new DatiSfridoSezBiesseR1P4(ISfridoInfo.SEZR1P4);
      try {
        dataFine=DateUtils.getFineGg(dataFine);
        dsR1P4.setParams(getElabProperties());
        elabDatiSfridoLinea(manP, dsR1P4);
        _logger.info(" ----- Elaborazioni Dati Sfrido Sezionatrice P4 terminata correttamente ----- ");
      } catch (ParseException ex) {
        addError("Errore in fase di conversione della data -->"+ex.getMessage());
        _logger.error("Errore in fase di conversione della data-->"+ex.getMessage());
      }
    }
    if(elabCombicutR1P4){
       _logger.info(" ----- Elaborazioni Dati Sfrido Combicut R1 P4 ----- ");
      DatiSfridoCombicutR1P4 dsR1P4Combi=new DatiSfridoCombicutR1P4(ISfridoInfo.COMBICUTR1P4);
      try {
        dataFine=DateUtils.getFineGg(dataFine);
        dsR1P4Combi.setParams(getElabProperties());
        elabDatiSfridoLinea(manP, dsR1P4Combi);
        _logger.info(" ----- Elaborazioni Dati Sfrido Sezionatrice Combicut R1 P4 terminata correttamente ----- ");
      } catch (ParseException ex) {
        addError("Errore in fase di conversione della data -->"+ex.getMessage());
        _logger.error("Errore in fase di conversione della data-->"+ex.getMessage());
      }  
     } 
//      try{
//        List<List> info=dsR1P4.getInfoSfrido(dataInizio, DateUtils.getFineGg(dataFine));
//         f=new XlsXCsvFileGenerator("//pegaso/scambio/lv/SfridoOtmR1P4.csv", XlsXCsvFileGenerator.FILE_CSV);
//        f.generateFile(info);
//      } catch(ElabException e){
//       addError("Errore in fase di elaborazione per dati di sfrido relativi  a cdL"+dsR1P4.getCdL()+" nel periodo +"+dataInizio+" - "+dataFine+ 
//               " -->"+ e.toString());
//      } catch (ParseException ex) {  
//        java.util.logging.Logger.getLogger(ElabSfridiCdlColombini.class.getName()).log(Level.SEVERE, null, ex);
//      } catch(FileNotFoundException s){
//            addError("Errore in fase di generazione del file "+f.getFileName() +" --> "+s.toString());
//            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
//      }
     
    
     
  }
  
  
  
  public Boolean isElabImaAnteOn() {
    return elabImaAnte;
  }

  public void setElabImaAnte(Boolean elabImaAnte) {
    this.elabImaAnte = elabImaAnte;
  }

  public Boolean istElabOtmG1On() {
    return elabOtmG1;
  }

  public void setElabOtmG1(Boolean elabOtmG1) {
    this.elabOtmG1 = elabOtmG1;
  }

  public Boolean isElabSezR2On() {
    return elabSezR2;
  }

  public void setElabSezR2(Boolean elabSezR2) {
    this.elabSezR2 = elabSezR2;
  }

  public Boolean isElabImaTop() {
    return elabImaTop;
  }

  public void setElabImaTop(Boolean elabImaTop) {
    this.elabImaTop = elabImaTop;
  }

  public Boolean isElabOtmNesting() {
    return elabOtmNesting;
  }

  public void setElabOtmNesting(Boolean elabOtmNesting) {
    this.elabOtmNesting = elabOtmNesting;
  }

  public Boolean isElabSezR1P4() {
    return elabSezR1P4;
  }

  public void setElabSezR1P4(Boolean elabSezR1P4) {
    this.elabSezR1P4 = elabSezR1P4;
  }

  public Boolean isElabCombicutR1P4() {
    return elabCombicutR1P4;
  }

  public void setElabCombicutR1P4(Boolean elabNestingR1P4) {
    this.elabCombicutR1P4 = elabNestingR1P4;
  }
  
  
  
  public void setAllElab(Boolean b){
    this.elabOtmG1=b;
    this.elabImaAnte=b;
    this.elabSezR2=b;
    this.elabImaTop=b;
    this.elabOtmNesting=b;
    this.elabSezR1P4=b;
    this.elabCombicutR1P4=b;
  }
  
  
  
  private void elabDatiSfridoLinea(PersistenceManager manP,InfoSfridoCdL datiSfr){
    try{
      List info=getDatiSfrido(datiSfr);
      if(info==null || info.isEmpty()){
        addWarning(" Attenzione dati Sfrido relativi a cdL"+datiSfr.getCdL()+" non presenti");
        return;
      }

      deleteOldData(manP,datiSfr.getCdL());
      manP.saveListDt(new SfridiCdlDettaglio(), info);
      
    } catch(ElabException e){
       addError("Errore in fase di elaborazione per dati di sfrido relativi  a cdL"+datiSfr.getCdL()+" nel periodo +"+dataInizio+" - "+dataFine+ 
               " -->"+ e.toString());
    } catch(SQLException s){
       addError("Errore in fase di salvataggio dei dati di  sfrido relativi  a cdL"+datiSfr.getCdL()+" nel periodo +"+dataInizio+" - "+dataFine+ 
               " -->"+ s.toString());
    }
  }
  
  
  private List getDatiSfrido(ISfridoInfo iS) throws ElabException{
    List info=new ArrayList();
    info=getListInfoComplete(iS.getInfoSfrido(dataInizio, dataFine));
    
    return info;
  }
  
  
  private List getListInfoComplete(List<List> infoBase){
    List listComplete=new ArrayList();
    
    if(infoBase==null || infoBase.isEmpty())
      return null;   
    
    for(List record : infoBase){
      record.add(new Date());
      
      listComplete.add(record);
    }
    
    return listComplete;
  }
  
  
  private void deleteOldData(PersistenceManager pm,String cdL){
    Date tmp=dataInizio;
    
    Map <String, Object> campiValore=new HashMap();
    campiValore.put(SfridiCdlDettaglio.ZSCONO,CostantsColomb.AZCOLOM);
    campiValore.put(SfridiCdlDettaglio.ZSPLGR,cdL);
    
    while(DateUtils.beforeEquals(tmp, dataFine)){
      campiValore.put(SfridiProdPersistence.ZSDTRF,DateUtils.getDataForMovex(tmp));
      
      try{
        pm.deleteDt(new SfridiCdlDettaglio(),campiValore);
      } catch(SQLException e){
        addError("Errore in fase di cancellazione dei dati di sfrido per :"+cdL+" per il gg : "+tmp+" --> "+e.toString());
      }
      tmp=DateUtils.addDays(tmp, 1);
    }
  }
  
  
  
  
  

  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabSfridiCdlColombini.class);

  
}
