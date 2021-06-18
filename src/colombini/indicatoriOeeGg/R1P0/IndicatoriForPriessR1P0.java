/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P0;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.logFiles.R1P0.LogFilePriess;
import colombini.model.CausaliLineeBean;
import colombini.model.persistence.IndicatoriOeeGgBean;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.indicatoriOee.R1P0.XlsImaTop;
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
public class IndicatoriForPriessR1P0 implements ICalcIndicatoriOeeLinea {
  
  
  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine,String cdLavoro) throws OEEException{
    String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro);
    String fileName=InfoMapLineeUtil.getLogFileGgLinea(nomeLinea, dataInizio);
    Map mapInfo=new HashMap();

    try {
      LogFilePriess lf=new LogFilePriess(fileName);
      mapInfo=lf.elabFile(dataInizio,dataFine);
      
      return mapInfo; 
    } catch (IOException ex) {
       _logger.error("Errore di accesso al file di Log "+fileName+" --> "+ex.getMessage());
       throw new OEEException("Problemi di accesso al file di log"+fileName);
    }
  }


  
  @Override
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
    
      XlsImaTop xls=(XlsImaTop) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
      Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
      Map causaliLinea=(Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
      
      IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
      
      Date dataInizio=xls.getInizioTurnoGg(data);
      Date dataFine=xls.getFineTurnoGg(data);
      
      if(dataInizio==null || dataFine==null){
        ioee.addError(" Turni di lavoro  non specificati per linea .");
        return ioee;
      }
      
      ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
      ioee.setTImprodImp(xls.getMinutiImprodImpianto(data,causaliLinea)*60);
      ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
      try {
        Map infoFl=loadDatiFromLogFile(dataInizio, dataFine, centrodiLavoro);
        
        ioee.setTPerditeGest((Long) infoFl.get(CostantsColomb.TPERDGEST));
        ioee.setTGuasti((Long) infoFl.get(CostantsColomb.TGUASTI));
    
        Long tRuntime=(Long) infoFl.get(CostantsColomb.TRUNTIME);
        Long tMicrofermi=(Long) infoFl.get(CostantsColomb.TMICROFERMI);
    
        ioee.setTRun(tRuntime);
        ioee.setTMicrofermi(tMicrofermi);
        
        _logger.info("Tempo Runtime iniziale:"+tMicrofermi);
        if(ioee.getTPerditeNnRilevate()<0){
          tRuntime+=ioee.getTPerditeNnRilevate();
          _logger.info("Tempo Runtime  finale:"+tMicrofermi);  
        }
        ioee.setTRun(tRuntime);
      
      
      
      } catch (OEEException ex) {
        ioee.addError(ex.getMessage());
      }
    
    
    return ioee;
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriForPriessR1P0.class);

}
