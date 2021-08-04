/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P2;

import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.G1P2.LogFileSquadrabordaLong;
import colombini.util.InfoMapLineeUtil;
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
public class IndicatoriSquadraborda extends AIndicatoriLineaForOee {

  public IndicatoriSquadraborda(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    
    _logger.debug("LINEA "+getIoeeBean().getCdLav() + " GG :"+getIoeeBean().getData());
    _logger.info(" Lettura file di log  nel periodo da "+dataIni +" a "+dataFin);
      //--------
    getIoeeBean().setTSetup2(MapUtils.getNumberFromMap(getMapTpFermiSec(dataIni, dataFin,getIoeeBean().getCdLav()), "S1", Long.class));
    Map mappaDati;
      
      
      try {
        mappaDati = getMapInfoLog(dataIni,dataFin,getIoeeBean().getCdLav());
        if(mappaDati!=null){
          getIoeeBean().setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
          Long tLordoAppo= MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TLORDO,Long.class);
          getIoeeBean().setNCicliLav(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NCICLI,Long.class));
          getIoeeBean().setNBande(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTURNI,Long.class));
          tLordoAppo=tLordoAppo-getIoeeBean().getTRun();
          getIoeeBean().setTRun2(tLordoAppo);
          Double setupCicli=getIoeeBean().getNumCicliLav()*new Double("11.64");
          getIoeeBean().addTSetup(setupCicli.longValue());
        }  

      }catch(OEEException ex){
       getIoeeBean().addError(ex.getMessage()); 
      }
      
  }
  
  
  private Map getMapInfoLog(Date dataInizio,Date dataFine,String cdLavoro) throws OEEException {
    LogFileSquadrabordaLong lf=null;
    
    Map mappaInfo=new HashMap();
    try{
      String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro);
      String fileName=InfoMapLineeUtil.getLogFileGgLinea(nomeLinea, dataInizio);
      lf= new LogFileSquadrabordaLong(fileName, cdLavoro);
      _logger.info("Apertura file log: "+fileName);
      lf.initialize();
      
      mappaInfo=lf.processLogFile(dataInizio, dataFine);
    } catch(IOException ex){
      _logger.error(" - " +cdLavoro+ " - Problemi di accesso al file di log :"+ex.getMessage());
      throw new OEEException("Problemi di accesso al file di log");
    } catch(ParseException ex){
      _logger.error(" - " +cdLavoro+ " - Problemi di conversione della data :"+ex.getMessage());
      throw new OEEException("Errore in fase di conversione dei dati del file di log");
    } finally{
      try{
        lf.terminate();
      } catch(IOException ex){
        
      }
    }
    
    return mappaInfo;
  }
  
  
  public String getInfoLog(Date data){
   String log="\n TSETUP-> Setup Sezionatrice "
        +" TSCARTO-> Setup Squadraborda"
        +" TLORDO->(TLORDO -TRUNTIME)"
        +" PZTURNO-> BANDE   ;";
                
    return log;
  }
  
  

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    
//      XlsSquadraBorda xls=(XlsSquadraBorda) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//      Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//      IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//      
//      Date dtInizio=xls.getInizioTurnoStdGg(data);
//      Date dtFine=xls.getFineTurnoStdGg(data);
//      
//      if(!ioee.checkDateTurni(dtInizio, dtFine)){
//        return ioee;
//      }
//      
//      Integer rigaIni=xls.getRigaInizioGg(data);
//      Integer rigaFin=xls.getRigaFineGg(rigaIni);
//      ioee.setTDispImp(DateUtils.numSecondiDiff(dtInizio, dtFine));
//      ioee.setTImprodImp(xls.getDurataPeriodiInRange(xls.getPeriodiImprodImpianto(dtInizio, rigaIni, rigaFin), dtInizio, dtFine));
//      
//      ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//      ioee.setTSetup(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
//      ioee.setTMicrofermi(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_MICROFRM, Long.class)*60);
//      ioee.setTVelRidotta(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_VELRID, Long.class)*60);
//      ioee.setTSetup2(MapUtils.getNumberFromMap(fermiTot, "S1", Long.class)*60);
//      ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//      ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//      
//      _logger.debug("LINEA "+centrodiLavoro + " GG :"+data);
//      _logger.info(" Lettura file di log  nel periodo da "+dtInizio +" a "+dtFine);
//      //--------
//      Map mappaDati;
//      try {
//        mappaDati = getMapInfoLog(dtInizio,dtFine,centrodiLavoro);
//        if(mappaDati!=null){
//          ioee.setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
//          Long tLordoAppo= MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TLORDO,Long.class);
//          ioee.setNCicliLav(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NCICLI,Long.class));
//          ioee.setNBande(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTURNI,Long.class));
//          tLordoAppo=tLordoAppo-ioee.getTRun();
//          ioee.setTRun2(tLordoAppo);
//          Double setupCicli=ioee.getNumCicliLav()*new Double("11.64");
//          ioee.addTSetup(setupCicli.longValue());
//        }  
//
//      }catch(OEEException ex){
//       ioee.addError(ex.getMessage()); 
//      }
//      
//      return ioee;
//    
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSquadraborda.class);

  
}
