/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P0;

import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.R1P0.LogFileSchellingTop4Oee;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class IndicatoriSchellingR1P0 extends AIndicatoriLineaForOee  {

  public IndicatoriSchellingR1P0(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
   @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Map mapInfo=loadDatiFromLogFile(dataIni,dataFin,this.getIoeeBean().getCdLav());
      getIoeeBean().setTRun((Long) mapInfo.get(CostantsColomb.TRUNTIME));
      getIoeeBean().setTSetup((Long) mapInfo.get(CostantsColomb.TSETUP));
      
      Long tGuasti=(Long) mapInfo.get(CostantsColomb.TGUASTI);
      Long tMicrofermi=(Long) mapInfo.get(CostantsColomb.TMICROFERMI);
      getIoeeBean().setTGuasti( tGuasti);
      getIoeeBean().setTMicrofermi( tMicrofermi);

      _logger.info("Tempo Guasti da file di log:"+tGuasti);
      _logger.info("Tempo Microfermi da file di log:"+tMicrofermi);  
    
      Long valN=getIoeeBean().getTPerditeGestionali()+getIoeeBean().getTImprodImpianto();
    
      if(valN>tGuasti){
        valN-=tGuasti;
        tGuasti=Long.valueOf(0);
        tMicrofermi-=valN;
        if(tMicrofermi<0){
          _logger.warn("Attenzione dati fermi non corretti -  Microfermi"+tMicrofermi);
          tMicrofermi=Long.valueOf(0);
        }  
      }else{
        tGuasti-=valN;
      }

      getIoeeBean().setTGuasti( tGuasti);
      getIoeeBean().setTMicrofermi( tMicrofermi);


      Long tmpNNRil=getIoeeBean().getTPerditeNnRilevate();
      if(tmpNNRil<0 && tMicrofermi>0){
        tMicrofermi+=tmpNNRil;
        if(tMicrofermi<0){
          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
          tMicrofermi=Long.valueOf(0);
        }
      }
      getIoeeBean().setTMicrofermi( tMicrofermi);  
      
      
    }catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
    }
  }
  
  

  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine,String linea) throws OEEException{
   String fileName="";
   Map mappaInfo=new HashMap(); 
        
    try {
      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(linea), dataInizio);
      LogFileSchellingTop4Oee sct=new LogFileSchellingTop4Oee(fileName);
      _logger.info("Apertura file log: "+fileName);
      
      mappaInfo=sct.processFile(dataInizio,dataFine);
    
    } catch (IOException ex) {
       _logger.error("Errore di accesso al file di Log "+fileName+" --> "+ex.getMessage());
       throw new OEEException(ex);
    }
    
    return mappaInfo;
    
  }

  

  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsImaTop xls=(XlsImaTop) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    Map causaliLinea=(Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
//    
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    Date dataInizio=xls.getInizioTurnoStdGg(data);
//    Date dataFine=xls.getFineTurnoStdGg(data);
//      
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea .");
//      return ioee;
//    }
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data, causaliLinea) *60);
//
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
////    ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    
//    try{
//      Map mapInfo=loadDatiFromLogFile(dataInizio,dataFine,centrodiLavoro);
//      ioee.setTRun((Long) mapInfo.get(CostantsColomb.TRUNTIME));
//      ioee.setTSetup((Long) mapInfo.get(CostantsColomb.TSETUP));
//      
//      Long tGuasti=(Long) mapInfo.get(CostantsColomb.TGUASTI);
//      Long tMicrofermi=(Long) mapInfo.get(CostantsColomb.TMICROFERMI);
//      ioee.setTGuasti( tGuasti);
//      ioee.setTMicrofermi( tMicrofermi);
//
//      _logger.info("Tempo Guasti da file di log:"+tGuasti);
//      _logger.info("Tempo Microfermi da file di log:"+tMicrofermi);  
//    
//      Long valN=ioee.getTPerditeGestionali()+ioee.getTImprodImpianto();
//    
//      if(valN>tGuasti){
//        valN-=tGuasti;
//        tGuasti=Long.valueOf(0);
//        tMicrofermi-=valN;
//        if(tMicrofermi<0){
//          _logger.warn("Attenzione dati fermi non corretti -  Microfermi"+tMicrofermi);
//          tMicrofermi=Long.valueOf(0);
//        }  
//      }else{
//        tGuasti-=valN;
//      }
//
//      ioee.setTGuasti( tGuasti);
//      ioee.setTMicrofermi( tMicrofermi);
//
//
//      Long tmpNNRil=ioee.getTPerditeNnRilevate();
//      if(tmpNNRil<0 && tMicrofermi>0){
//        tMicrofermi+=tmpNNRil;
//        if(tMicrofermi<0){
//          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
//          tMicrofermi=Long.valueOf(0);
//        }
//      }
//      ioee.setTMicrofermi( tMicrofermi);  
//      
//      
//    }catch(OEEException e){
//      ioee.addError(e.getMessage());
//    }
//    
//    
//    return ioee;
//  }
  
 
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSchellingR1P0.class);

 

  

  
  
}
