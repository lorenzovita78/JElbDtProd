/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.costant.NomiLineeColomb;
import colombini.costant.OeeCostant;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.CalcoloTempoCiclo;
import colombini.indicatoriOee.utils.OeeUtils;
import colombini.logFiles.R1P1.LogFileMawArtec;
import colombini.util.InfoMapLineeUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
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
public class IndicatoriMawArtec extends AIndicatoriLineaForOee {

  public IndicatoriMawArtec(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }

  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    List periodiProd=this.getInfoTCdl().getListTurniValidi(dataIni, dataFin);
    
    try{
      Map mapT=calcolaTempiRunTime(con, periodiProd,getIoeeBean().getData(), getIoeeBean().getCdLav());

      getIoeeBean().setTRun((Long) mapT.get(OeeUtils.TRUN));
      getIoeeBean().setNPzTot((Long) mapT.get(OeeUtils.NPZTOT));
    
    }catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
    }
  }
  
 
//  public Map calcolaTempiRunTime(Connection con,Date dtInizio,Date dtFine,String cdLavoro) throws OEEException {
//    LogFileMawArtec lma=null;
//    Double tcicloT=Double.valueOf(0);
//    Map tempi=new HashMap();
//    Long nPzTot=Long.valueOf(0);
//    Long tRuntime=Long.valueOf(0);
//    String fileName="";
//    try{
//      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro), dtInizio);
//      lma=new LogFileMawArtec(fileName);
//      _logger.info("Apertura file log: "+fileName+"-> data inizio e fine :"+dtInizio+" - "+dtFine);
//      lma.initialize();
//      //modifica dovuta all'orario sballato della macchina
//      if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL2).equals(cdLavoro) ){
//        dtInizio=DateUtils.addMinutes(dtInizio, -30);
//        dtFine=DateUtils.addMinutes(dtFine, -5);
//      }else{
////        dtInizio=DateUtils.addMinutes(dtInizio, -60);
////        dtFine=DateUtils.addMinutes(dtFine, -60);
//      }
//      
//      List listColli=(List) lma.processLogFile(dtInizio, dtFine);
//      if(listColli!=null){
//        CalcoloTempoCiclo ct=new CalcoloTempoCiclo(OeeCostant.MAWARTEC);
//        _logger.info(new Date());
//        tcicloT=ct.getTempoCiclo(con, listColli,null);
//        _logger.info(new Date());
//        nPzTot=ct.getNumArticoli();
//        tRuntime=tcicloT.longValue();
//      }
//      
//      tempi.put(OeeUtils.TRUN, tRuntime);
//      tempi.put(OeeUtils.NPZTOT,nPzTot);
//      
//      _logger.info("Runtime: "+tRuntime + " ; ncolli:"+nPzTot );
//    } catch (SQLException ex) {
//      _logger.error("Errore in fase di accesso al db :"+ex.getMessage());
//      throw new OEEException("Errore in fase di lettura dei tempi ciclo dei colli");
//    } catch (FileNotFoundException ex) {
//      _logger.error("Impossibile accedere al file :"+ex.getMessage());
//      throw new OEEException(" File di log"+fileName+" non presente ");
//    } catch (IOException ex) {
//      _logger.error("Problemi di I/O con il log file :"+ex.getMessage());
//      throw new OEEException("Impossibile accedere al file di log"+fileName);
//    } catch (ParseException ex) {
//      _logger.error("Problemi di conversione di date :"+ex.getMessage());
//      throw new OEEException("Problemi in fase di lettura del file di log"+fileName);
//    }finally{
//      try {
//        if(lma!=null)
//          lma.terminate();
//      } catch (FileNotFoundException ex) {
//        _logger.error("Problemi di accesso al log di file in chiusura:"+ex.getMessage());
//      } catch (IOException ex) {
//        _logger.error("Problemi di I/O con il log file in chiusura:"+ex.getMessage());
//      }
//
//    } 
//    return tempi;
//  }
  
  
  public Map calcolaTempiRunTime(Connection con,List<List> periodiProd,Date dataRif,String cdLavoro) throws OEEException {
    LogFileMawArtec lma=null;
    Double tcicloT=Double.valueOf(0);
    Map tempi=new HashMap();
    Long nPzTot=Long.valueOf(0);
    Long tRuntime=Long.valueOf(0);
    String fileName="";
    
    fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro), dataRif);
    for(List periodo:periodiProd){
      Date dtInizio=ClassMapper.classToClass(periodo.get(0),Date.class);
      Date dtFine=ClassMapper.classToClass(periodo.get(1),Date.class);
      //modifica dovuta all'orario sballato della macchina
      if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL2).equals(cdLavoro) ){
        dtInizio=DateUtils.addMinutes(dtInizio, -30);
        dtFine=DateUtils.addMinutes(dtFine, -5);
      }
//      else{
//        dtInizio=DateUtils.addMinutes(dtInizio, -60);
//        dtFine=DateUtils.addMinutes(dtFine, -60);
//      }
      try{
        lma=new LogFileMawArtec(fileName);
        _logger.info("Apertura file log: "+fileName);
        lma.initialize();
        List listColli=(List) lma.processLogFile(dtInizio, dtFine);
        if(listColli!=null){
          CalcoloTempoCiclo ct=new CalcoloTempoCiclo(OeeCostant.MAWARTEC);
          _logger.info(new Date());
          tcicloT=ct.getTempoCiclo(con, listColli,null);
          _logger.info(new Date());
          nPzTot+=ct.getNumArticoli();
          tRuntime+=tcicloT.longValue();
        }

      } catch (SQLException ex) {
        _logger.error("Errore in fase di accesso al db :"+ex.getMessage());
        throw new OEEException("Errore in fase di lettura dei tempi ciclo dei colli");
      } catch (ParseException ex) {
        _logger.error("Problemi di conversione di date :"+ex.getMessage());
        throw new OEEException("Problemi in fase di lettura del file di log"+fileName);
      } catch (FileNotFoundException ex) {
        _logger.error("Impossibile accedere al file :"+ex.getMessage());
        throw new OEEException(" File di log"+fileName+" non presente ");
      } catch (IOException ex) {
        _logger.error("Problemi di I/O con il log file :"+ex.getMessage());
        throw new OEEException("Impossibile accedere al file di log"+fileName);
      } finally{
        try {
          if(lma!=null)
            lma.terminate();
        } catch (FileNotFoundException ex) {
          _logger.error("Problemi di accesso al log di file in chiusura:"+ex.getMessage());
        } catch (IOException ex) {
          _logger.error("Problemi di I/O con il log file in chiusura:"+ex.getMessage());
        }
      }

    }
    tempi.put(OeeUtils.TRUN, tRuntime);
    tempi.put(OeeUtils.NPZTOT,nPzTot);

    _logger.info("Runtime: "+tRuntime + " ; ncolli:"+nPzTot );
    
        
    return tempi;
  }
  
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter)  {
//    XlsMawArtec xls=(XlsMawArtec) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    //istanziamo l'oggetto per l'indicatore
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    
//    Integer rI=xls.getRigaInizioGg(data);
//    Integer rF=xls.getRigaFineGg(rI);
//    
//    Date dataInizio=xls.getInizioTurnoGg(data, centrodiLavoro, rI, rF);
//    Date dataFine=xls.getFineTurnoGg(data, centrodiLavoro, rI, rF);
//    
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea .");
//      return ioee;
//    }
//    
//    
//    List periodiProd=xls.getListPeriodiProd(centrodiLavoro,data, rI, rF);
//    
//    List minutiProd=xls.getListPeriodiMinProd(centrodiLavoro, rI, rF);
//    List minutiNnProd=xls.getListPeriodiMinNnProd(centrodiLavoro, rI, rF);
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(minutiProd)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(minutiProd, minutiNnProd) *60);
//    
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTGuasti(xls.getMinutiFermiImpianto(data, centrodiLavoro, rI, rF)*60);
//    
//    try{
//      Map mapT=calcolaTempiRunTime(con, periodiProd,data, centrodiLavoro);
//
//      ioee.setTRun((Long) mapT.get(OeeUtils.TRUN));
//      ioee.setNPzTot((Long) mapT.get(OeeUtils.NPZTOT));
//    
//    }catch(OEEException e){
//      ioee.addError(e.getMessage());
//    }
//    
//    return ioee;
//  }
//  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriMawArtec.class);

  
}
