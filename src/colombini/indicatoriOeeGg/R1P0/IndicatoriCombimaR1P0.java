/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P0;

import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.R1P0.LogFileCombimaTop;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriCombimaR1P0  extends AIndicatoriLineaForOee {

  public IndicatoriCombimaR1P0(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
    
  //Attenzione i dati relativi ai guasti vengono salvati nella tabella giornaliera dei fermi
  // per fini statistici. La durata dei fermi però viene presa da logFile
  
 @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Map infoR=loadDatiFromLogFile(dataIni, dataFin);
      Long tRunLog=MapUtils.getNumberFromMap(infoR,CostantsColomb.TRUNTIME,Long.class);
      Long tGuastiLog=MapUtils.getNumberFromMap(infoR,CostantsColomb.TGUASTI,Long.class);
      Long tGuasti=Long.valueOf(0);
      getIoeeBean().setTRun(tRunLog);
      Long tMicrofermi= MapUtils.getNumberFromMap(infoR,CostantsColomb.TMICROFERMI,Long.class);
      
      //se il runtime è zero (es. rottura della macchina) 
      if(tRunLog<=0 && tGuastiLog<=0){
        tGuasti=getIoeeBean().getTGuasti();
      }else{
        tGuasti=tGuastiLog;
      }
        
        
      
      _logger.info("Tempo Guasti da file di log:"+tGuasti);
      _logger.info("Tempo Microfermi da file di log:"+tMicrofermi);
      //tolgo dal tempo totale dei guasti i tempi indicati sul file di log relativi alle perdite gestionali e alle ore improduttive
      Long valN=getIoeeBean().getTPerditeGestionali()+getIoeeBean().getTImprodImpianto();

      if(valN>tGuasti){
        valN-=tGuasti;
        tGuasti=Long.valueOf(0);
        tMicrofermi-=valN;
        if(tMicrofermi<0)
          _logger.warn("Attenzione dati fermi non corretti!!");
      }else{
        tGuasti-=valN;
      }

      getIoeeBean().setTGuasti(tGuasti);
      getIoeeBean().setTMicrofermi(tMicrofermi);

      Long tmpNNRil=getIoeeBean().getTPerditeNnRilevate();
      if(tmpNNRil<0 && tMicrofermi>0){
        tMicrofermi+=tmpNNRil;
        if(tMicrofermi<0){
          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
          tMicrofermi=Long.valueOf(0);
        }
      }
      
      getIoeeBean().setTMicrofermi(tMicrofermi);
      
    }catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
    }
  }
  
  
  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine) throws OEEException{
    LogFileCombimaTop lg=null;
    String fileName="";
    Map mappaInfo=new HashMap();
        
    try {
      fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.COMBIMAR1P0, dataInizio);
      lg=new LogFileCombimaTop(fileName);
      _logger.info("Apertura file log: "+fileName);
      
      mappaInfo=lg.processFile(dataInizio,dataFine);
    
      return mappaInfo;
      
    } catch (IOException ex) {
       _logger.error("Errore di accesso al file di Log "+fileName+" --> "+ex.getMessage());
       throw new OEEException(" Problemi di accesso al file di log "+fileName);
    }
  }

 

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsImaTop xls=(XlsImaTop) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    Map causaliLinea=(Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    
//    
//    Date dataInizio=xls.getInizioTurnoStdGg(data);
//    Date dataFine=xls.getFineTurnoStdGg(data);
//    
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea .");
//      return ioee;
//    }
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    //Attenzione è esclusa la manutenzione in quanto la causale viene usata in maniera impropria sulla linea
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data,causaliLinea)*60);
//
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//
//    try{
//      Map infoR=loadDatiFromLogFile(dataInizio, dataFine);
//      Long tRunLog=MapUtils.getNumberFromMap(infoR,CostantsColomb.TRUNTIME,Long.class);
//      Long tGuastiLog=MapUtils.getNumberFromMap(infoR,CostantsColomb.TGUASTI,Long.class);
//      Long tGuasti=Long.valueOf(0);
//      ioee.setTRun(tRunLog);
//      Long tMicrofermi= MapUtils.getNumberFromMap(infoR,CostantsColomb.TMICROFERMI,Long.class);
//      //se il runtime è zero (es. rottura della macchina) 
//      if(tRunLog<=0 && tGuastiLog<=0){
//        tGuasti=MapUtils.getNumberFromMap(fermi,CausaliLineeBean.TIPO_CAUS_GUASTO,Long.class)*60;
//      }else{
//        tGuasti=tGuastiLog;
//      }
//        
//        
//      
//      ioee.setTSetup(MapUtils.getNumberFromMap(infoR,CostantsColomb.TSETUP,Long.class));  
//      ioee.setNPzTot(MapUtils.getNumberFromMap(infoR,CostantsColomb.NPZTOT,Long.class));  
//    
//      
//      _logger.info("Tempo Guasti da file di log:"+tGuasti);
//      _logger.info("Tempo Microfermi da file di log:"+tMicrofermi);
//      //tolgo dal tempo totale dei guasti i tempi indicati sul file di log relativi alle perdite gestionali e alle ore improduttive
//      Long valN=ioee.getTPerditeGestionali()+ioee.getTImprodImpianto();
//
//      if(valN>tGuasti){
//        valN-=tGuasti;
//        tGuasti=Long.valueOf(0);
//        tMicrofermi-=valN;
//        if(tMicrofermi<0)
//          _logger.warn("Attenzione dati fermi non corretti!!");
//      }else{
//        tGuasti-=valN;
//      }
//
//      ioee.setTGuasti(tGuasti);
//      ioee.setTMicrofermi(tMicrofermi);
//
//      Long tmpNNRil=ioee.getTPerditeNnRilevate();
//      if(tmpNNRil<0 && tMicrofermi>0){
//        tMicrofermi+=tmpNNRil;
//        if(tMicrofermi<0){
//          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
//          tMicrofermi=Long.valueOf(0);
//        }
//      }
//      
//      ioee.setTMicrofermi(tMicrofermi);
//      
//    }catch(OEEException e){
//      ioee.addError(e.getMessage());
//    }
//    
//    
//    return ioee;
//  }
  
   
  
   private static final Logger _logger = Logger.getLogger(IndicatoriCombimaR1P0.class);

  
}
