/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.R1P1.LogFileSchelling;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class IndicatoriSchellingR1P1  extends AIndicatoriLineaForOee {

  public IndicatoriSchellingR1P1(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Map mapTempi=loadDatiFromLogFile(dataIni,dataFin,getInfoTCdl().getCdl());

      //indica il tempo di passaggio dei pezzi di scarto che vengono prodotti dalla sezionatrice
      getIoeeBean().setTScarti(((Double)mapTempi.get(LogFileSchelling.TEMPOSCARTO)).longValue());
      getIoeeBean().setTRun( ((Double)mapTempi.get(LogFileSchelling.TEMPORUN)).longValue() );


      Long tLordo=((Double) mapTempi.get(LogFileSchelling.TEMPOLORDO)).longValue();
      //Indica la differenza tra il vero tempo lordo e il tempo di runtime netto
      getIoeeBean().setTRun2( tLordo-getIoeeBean().getTRun() );

      getIoeeBean().setNPzTurni((Long) mapTempi.get(LogFileSchelling.BANDETOT));
      getIoeeBean().setNCicliLav((Long) mapTempi.get(LogFileSchelling.CICLITOT));
    
    }catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
      
    }
  }
  
  
  
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
  
  
 
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSchellingR1P1.class);

  
}

