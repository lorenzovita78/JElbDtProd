/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P2;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.G1P2.LogFileSchellingLong;
import colombini.model.CausaliLineeBean;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriSchellLong extends  AIndicatoriLineaForOee{

  
   List fermiGg;

  public IndicatoriSchellLong(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
   
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try {
      Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
      LogFileSchellingLong lf=new LogFileSchellingLong(InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.SCHELLLONG_GAL,getIoeeBean().getData()),causali);
      _logger.info(" Lettura file di log  nel periodo da "+dataIni +" a "+dataFin);
      lf.processLogFile(dataIni, dataFin);
      fermiGg=lf.getFermi(); 
      
      getIoeeBean().setTImprodImp(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_ORENONPROD,Long.class));
      getIoeeBean().setTSetup(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_SETUP,Long.class));
      getIoeeBean().setTPerditeGest(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_PERDGEST,Long.class));
      getIoeeBean().setTGuasti(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_GUASTO,Long.class));
      getIoeeBean().setNGuasti(lf.getnGuasti());
      getIoeeBean().setNCicliLav(lf.getNumCicli());
      getIoeeBean().setTRun(lf.getTempoRun().longValue());

      getIoeeBean().setTRun2(lf.getTempoLordo().longValue()-lf.getTempoRun().longValue());


      //Da tempi e metodi :
      //L'attesa tra un ciclo e l'altro la metto tra i tempi di setup ogni ciclo prevede circa 12 sec di setup(Amantini)
      Double setupCicli=getIoeeBean().getNumCicliLav()*new Double("11.9");
      getIoeeBean().addTSetup(setupCicli.longValue());
      //tutto il grigio lo trasformiamo in microfermi
      getIoeeBean().setTMicrofermi(getIoeeBean().getTPerditeNnRilevate()); 
      

    } catch (IOException ex) {
     _logger.error("Problemi di accesso al file di log della macchina --> "+ex.getMessage());
     getIoeeBean().addError("Errore in fase di accesso al file di log ");
    } catch (ParseException ex) {
      _logger.error("Errore in fase di lettura del file di log --> "+ex.getMessage());
      getIoeeBean().addError("Errore in fase di lettura al file di log ");
    }
  }
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    
//    XlsSchellingLong xls=(XlsSchellingLong) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    LogFileSchellingLong lf=null;
//    fermiGg=new ArrayList();
//    
//    Date dataInizio=xls.getInizioTurnoStdGg(data);
//    Date dataFine=xls.getFineTurnoStdGg(data);
//
//    if(!ioee.checkDateTurni(dataInizio, dataFine)){
//      return ioee;
//    }
//    //unica informazione presa dal file xls  
////    tDispImp=xls.getMinutiProdImpianto(data) * 60;
//
//    ioee.setTDispImp(DateUtils.numSecondiDiff(dataInizio, dataFine));
//    try {
//      lf=new LogFileSchellingLong(InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.SCHELLLONG_GAL,data),causali);
//      _logger.info(" Lettura file di log  nel periodo da "+dataInizio +" a "+dataFine);
//      lf.processLogFile(dataInizio, dataFine);
//      fermiGg=lf.getFermi(); 
//      
//      ioee.setTImprodImp(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_ORENONPROD,Long.class));
//      ioee.setTSetup(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_SETUP,Long.class));
//      ioee.setTPerditeGest(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_PERDGEST,Long.class));
//      ioee.setTGuasti(MapUtils.getNumberFromMap(lf.getTipoFermi(),CausaliLineeBean.TIPO_CAUS_GUASTO,Long.class));
//      ioee.setNGuasti(lf.getnGuasti());
//      ioee.setNCicliLav(lf.getNumCicli());
//      ioee.setTRun(lf.getTempoRun().longValue());
//
//      ioee.setTRun2(lf.getTempoLordo().longValue()-lf.getTempoRun().longValue());
//
//
//      //Da tempi e metodi :
//      //L'attesa tra un ciclo e l'altro la metto tra i tempi di setup ogni ciclo prevede circa 12 sec di setup(Amantini)
//      Double setupCicli=ioee.getNumCicliLav()*new Double("11.9");
//      ioee.addTSetup(setupCicli.longValue());
//      //tutto il grigio lo trasformiamo in microfermi
//      ioee.setTMicrofermi(ioee.getTPerditeNnRilevate()); 
//      
//
//    } catch (IOException ex) {
//     _logger.error("Problemi di accesso al file di log della macchina --> "+ex.getMessage());
//     ioee.addError("Errore in fase di accesso al file di log ");
//    } catch (ParseException ex) {
//      _logger.error("Errore in fase di lettura del file di log --> "+ex.getMessage());
//      ioee.addError("Errore in fase di lettura al file di log ");
//    }
//
//   return ioee;
//  }

  public List getFermiGg() {
    return fermiGg;
  }
 
  
  
  
 private static final Logger _logger = Logger.getLogger(IndicatoriSchellLong.class);  

  
  
}
