/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.old;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.ISfridoInfo;
import colombini.dtProd.sfridi.old.DatiSfridoImaAnteOld;
import colombini.dtProd.sfridi.old.DatiSfridoImaTopOld;
import colombini.dtProd.sfridi.old.DatiSfridoOtmGalOld;
import colombini.dtProd.sfridi.old.DatiSfridoOtmNestingOld;
import colombini.dtProd.sfridi.old.DatiSfridoSezR2Old;
import colombini.elabs.NameElabs;
import colombini.model.persistence.tab.SfridiProdPersistence;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.ElabException;
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

/**
 *
 * @author lvita
 */
public class ElabSfridiProdColombiniOld  extends ElabClass{
  
  private Date dataInizio;
  private Date dataFine;
  
  private Boolean elabImaAnte;
  private Boolean elabImaTop;
  private Boolean elabOtmG1;
  private Boolean elabSezR2;
  private Boolean elabOtmNesting;
  
  public ElabSfridiProdColombiniOld(){
    super();
    
    this.elabOtmG1=Boolean.FALSE;
    this.elabImaAnte=Boolean.FALSE;
    this.elabSezR2=Boolean.FALSE;
    this.elabImaTop=Boolean.FALSE;
    this.elabOtmNesting=Boolean.FALSE;
    
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
    
    if(this.getInfoElab().getCode().equals(NameElabs.ELBSFRIDGRUPPOOLD)){
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
    }
    
    return Boolean.TRUE;
  }

  

  @Override
  public void exec(Connection con) {
    
    PersistenceManager manP=null;
    
    manP=new PersistenceManager(con);

    if(elabOtmG1){
       _logger.info(" ----- Elaborazioni Dati Sfrido Ottimizzatore Galazzano -----");
       DatiSfridoOtmGalOld dsOtGal=new DatiSfridoOtmGalOld();
       elabDatiSfridoLinea(manP, dsOtGal, SfridiProdPersistence.FACTG1, SfridiProdPersistence.PLANP2, SfridiProdPersistence.UNMQ);        
       _logger.info("----- Elaborazione Dati Sfrido Ottimizzatore Galazzano  terminata correttamente ----");
    }

    if(elabImaAnte){
      _logger.info(" ----- Elaborazioni Dati Sfrido Ima Ante R1 ----- ");
      DatiSfridoImaAnteOld dsIAR1=new DatiSfridoImaAnteOld();
      elabDatiSfridoLinea(manP, dsIAR1, SfridiProdPersistence.FACTR1, SfridiProdPersistence.PLANP1, SfridiProdPersistence.UNML);        
      _logger.info("----- Elaborazione Dati Sfrido Ima Ante R1 terminata correttamente ----");
    }

    if(elabImaTop){
       _logger.info(" ----- Elaborazioni Dati Sfrido Ima Top R1 ----- ");
      DatiSfridoImaTopOld dsITR1=new DatiSfridoImaTopOld();
      elabDatiSfridoLinea(manP, dsITR1, SfridiProdPersistence.FACTR1, SfridiProdPersistence.PLANP0, SfridiProdPersistence.UNML);        
      _logger.info("----- Elaborazione Dati Sfrido Ima Top R1 terminata correttamente ----");
    }

    if(elabSezR2){
      _logger.info(" ----- Elaborazioni Dati Sfrido Sezionatrice R2 ----- ");
      DatiSfridoSezR2Old dsSzR2=new DatiSfridoSezR2Old();
      elabDatiSfridoLinea(manP, dsSzR2, SfridiProdPersistence.FACTR2, SfridiProdPersistence.PLANP1, SfridiProdPersistence.UNMQ);        
      _logger.info("----- Elaborazione Dati Sfrido Sezionatrice R2 terminata correttamente ----");
    }  

    if(elabOtmNesting){
      _logger.info(" ----- Elaborazioni Dati Sfrido Nesting Galazzno ----- ");
      DatiSfridoOtmNestingOld dsOtmNe=new DatiSfridoOtmNestingOld();
      elabDatiSfridoLinea(manP, dsOtmNe, SfridiProdPersistence.FACTG1, SfridiProdPersistence.PLANP0, SfridiProdPersistence.UNMQ);        
      _logger.info("----- Elaborazione Dati Sfrido Nesting Galazzno  terminata correttamente ----");
    }
      
      
     
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
  
  public void setAllElab(Boolean b){
    this.elabOtmG1=b;
    this.elabImaAnte=b;
    this.elabSezR2=b;
    this.elabImaTop=b;
    this.elabOtmNesting=b;
  }
  
  
  
  private void elabDatiSfridoLinea(PersistenceManager manP,ISfridoInfo iS,String stab,String plan,String um){
    try{
      List info=getDatiSfrido(iS, stab, plan, um);
      if(info==null || info.isEmpty()){
        addWarning(" Attenzione dati Sfrido relativi a stabilimento : "+stab+" - piano : "+plan+" non presenti");
        return;
      }

      deleteOldData(manP,stab,plan);
      manP.saveListDt(new SfridiProdPersistence(), info);
      
    } catch(ElabException e){
       addError("Errore in fase di elaborazione per dati di sfrido relativi a "+stab+plan+" nel periodo +"+dataInizio+" - "+dataFine+ 
               " -->"+ e.toString());
    } catch ( SQLException s){
       addError("Errore in fase di elaborazione per dati di sfrido relativi  a cdL"+stab+plan+" nel periodo +"+dataInizio+" - "+dataFine+ 
               " -->"+ s.toString());
    }
  }
  
  
  private List getDatiSfrido(ISfridoInfo iS,String stab,String plan,String um) throws ElabException{
    List info=new ArrayList();
    info=getListInfoComplete(stab, plan, um, iS.getInfoSfrido(dataInizio, dataFine));
    
    return info;
  }
  
  
  private List getListInfoComplete(String stab,String plan,String un,List<List> infoBase){
    List listComplete=new ArrayList();
    
    if(infoBase==null || infoBase.isEmpty())
      return null;   
    
    
    
    for(List record : infoBase){
      record.add(0, CostantsColomb.AZCOLOM );
      record.add(1, stab);
      record.add(2, plan);
      record.add(13, un);
      
      record.add(DateUtils.getDataSysLong());
      record.add(DateUtils.getOraSysLong());
      
      listComplete.add(record);
    }
    
    return listComplete;
  }
  
  
  private void deleteOldData(PersistenceManager pm,String stab,String piano){
    Date tmp=dataInizio;
    
    Map <String, Object> campiValore=new HashMap();
    campiValore.put(SfridiProdPersistence.ZSCONO,CostantsColomb.AZCOLOM);
    campiValore.put(SfridiProdPersistence.ZSFACT,stab);
    campiValore.put(SfridiProdPersistence.ZSPLAN,piano);
    
    while(DateUtils.beforeEquals(tmp, dataFine)){
      campiValore.put(SfridiProdPersistence.ZSDTRF,DateUtils.getDataForMovex(tmp));
      
      try{
        pm.deleteDt(new SfridiProdPersistence(),campiValore);
      } catch(SQLException e){
        addError("Errore in fase di cancellazione dei dati di sfrido per :"+stab+piano+" per il gg : "+tmp+" --> "+e.toString());
      }
      tmp=DateUtils.addDays(tmp, 1);
    }
  }
  
  
  
  
  

  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabSfridiProdColombiniOld.class);

  
}
