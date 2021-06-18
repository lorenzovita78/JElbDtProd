/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.logFiles.R1P1.LogFileSchelling;
import colombini.model.CausaliLineeBean;
import colombini.model.persistence.IndicatoriOeeGgBean;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.indicatoriOee.R1P1.XlsImaAnte;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriSchellingR1P1Old  implements ICalcIndicatoriOeeLinea {
  
  
  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine,String cdLavoro) throws OEEException{
    LogFileSchelling lg=null;
    String fileName="";
    Map mappaT=new HashMap();
    try {
      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro), dataInizio);
      lg=new LogFileSchelling(fileName);
      _logger.info("Apertura file log: "+fileName);
      lg.initialize();
      mappaT=lg.processLogFile(dataInizio, dataFine);
    
    } catch (IOException ex) {
       _logger.error("Errore di accesso al file di Log:"+ex.getMessage());
       throw new OEEException("Errore di accesso al file di log"+fileName);
    } catch (ParseException ex) {
       _logger.error("Errore di conversione :"+ex.getMessage());
        throw new OEEException(" Errore in fase di lettura del file di log"+fileName);
    } finally {
      if(lg!=null)
        try {
          lg.terminate();
        } catch (IOException ex) {
         _logger.error("Errore in fase di chiususra del file di log:"+ex.getMessage());
         
        }
    }
    return mappaT;
  }
  
  
 
  @Override
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter)  {
    XlsImaAnte xls=(XlsImaAnte) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
    //istanziamo l'oggetto per l'indicatore
    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
    
    Date dataInizio=xls.getInizioTurnoStdGg(data);
    Date dataFine=xls.getFineTurnoStdGg(data);
 
    if(dataInizio==null || dataFine==null){
      ioee.addError(" Turni di lavoro  non specificati .");
      return ioee;
    }
    
    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data, centrodiLavoro)*60);
    
    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
    ioee.setTSetup(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
    try{
      Map mapTempi=loadDatiFromLogFile(dataInizio,dataFine,centrodiLavoro);

      //indica il tempo di passaggio dei pezzi di scarto che vengono prodotti dalla sezionatrice
      ioee.setTScarti(((Double)mapTempi.get(LogFileSchelling.TEMPOSCARTO)).longValue());
      ioee.setTRun( ((Double)mapTempi.get(LogFileSchelling.TEMPORUN)).longValue() );


      Long tLordo=((Double) mapTempi.get(LogFileSchelling.TEMPOLORDO)).longValue();
      //Indica la differenza tra il vero tempo lordo e il tempo di runtime netto
      ioee.setTRun2( tLordo-ioee.getTRun() );

      ioee.setNPzTurni((Long) mapTempi.get(LogFileSchelling.BANDETOT));
      ioee.setNCicliLav((Long) mapTempi.get(LogFileSchelling.CICLITOT));
    }catch(OEEException e){
      ioee.addError(e.getMessage());
      
    }
//    tMicrofermi=((Double) mappaTempi.get(LogFileSchelling.TEMPOMICRO)).longValue();
    
//     _logger.info("Tempo Microfermi iniziale:"+tMicrofermi);
//      Long tmpNNRil=OeeGgUtils.getInstance().getTempoPerditeNonRilevate(this);
//      if(tmpNNRil<0){
//        tMicrofermi+=tmpNNRil;
//        _logger.info("Tempo Microfermi finale:"+tMicrofermi);  
//      }
    return ioee;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSchellingR1P1Old.class);
}

