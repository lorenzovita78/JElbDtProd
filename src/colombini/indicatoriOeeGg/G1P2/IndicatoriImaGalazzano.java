/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P2;

import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.costant.OeeCostant;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.ElabDatiOrdiniProdMovex;
import colombini.util.InfoMapLineeUtil;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriImaGalazzano extends AIndicatoriLineaForOee {

  public IndicatoriImaGalazzano(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
   
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    Long tSetupCambioB=Long.valueOf(0);
    Long tSetupAs=Long.valueOf(0);
    Long setupPedane=Long.valueOf(0);
    
    try{
      String oraInizio=DateUtils.DateToStr(dataIni, "HHmmss");
      String oraFine=DateUtils.DateToStr(dataFin, "HHmmss");
      Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, getIoeeBean().getCdLav(), OeeCostant.IMAGALPRINC, getIoeeBean().getData(), oraInizio, oraFine,ElabDatiOrdiniProdMovex.RAGGR);

      if(mappaDati!=null){
        getIoeeBean().setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
        getIoeeBean().setTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
        getIoeeBean().addTImprodImp(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TAVVIO,Long.class));
        
        getIoeeBean().setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
        getIoeeBean().setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class));
        
        setupPedane=MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSETUP,Long.class);
        if(mappaDati.containsKey(CostantsColomb.WARNINGS)){
          getIoeeBean().addWarning((String) mappaDati.get(CostantsColomb.WARNINGS));
        }
      } 
      
      if(!NomiLineeColomb.IMAGAL_13200.equals(InfoMapLineeUtil.getNomeLineaFromCodice(getIoeeBean().getCdLav()) ) ){
        tSetupAs=ElabDatiOrdiniProdMovex.getInstance().getTempoSetupAs(con, getIoeeBean().getData(),getIoeeBean().getCdLav());
        setupPedane=Long.valueOf(0);
      }
      
      _logger.info("Runtime da spunte:"+getIoeeBean().getTRun()+" npztot :"+getIoeeBean().getNumPzTot());
      //se il centro di lavoro è il 13200 prendiamo il tempo di setup delle pedane oltre a quello dei cambi bordo
      //se invece i centri sono 13267 o 13269 allora carico il tempo di setup dalla rtvoee21pf mentre setupPedane =0 e tSetupCambioB=0   
      getIoeeBean().addTSetup(tSetupCambioB+setupPedane+tSetupAs); 
      
    } catch (QueryException ex) {
      _logger.error("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea -->"+ex.getMessage());
      getIoeeBean().addError("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea");
    } catch (SQLException ex) {
      _logger.error("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea -->"+ex.getMessage());
      getIoeeBean().addError("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea");
    } catch (ParseException ex) {
      _logger.error("Problemi nella conversione dei dati per calcolare i tempi ciclo della linea -->"+ex.getMessage());
      getIoeeBean().addError("Problemi nella conversione dei dati per calcolare i tempi ciclo della linea");
    }  
  }
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    XlsImaSezionatrici xls=(XlsImaSezionatrici) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    
//    
//    Date dtInizio=xls.getInizioTurnoStdGg(data);
//    Date dtFine=xls.getFineTurnoStdGg(data);
//    
//    if(!ioee.checkDateTurni(dtInizio, dtFine)){
//        return ioee;
//    }
//    
//    Long tSetupCambioB=Long.valueOf(0);
//    Long tSetupAs=Long.valueOf(0);
//    Long setupPedane=Long.valueOf(0);
//    
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//    
//    ioee.setTDispImp(DateUtils.numSecondiDiff(dtInizio, dtFine));
//    ioee.setTImprodImp(xls.getDurataPeriodiInRange(xls.getPeriodiImprodImpianto(dtInizio, rigaIni, rigaFin), dtInizio, dtFine));
//
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTSetup(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
//    ioee.setTScarti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_RIPASSI, Long.class)*60);
//    ioee.setTMicrofermi(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_MICROFRM, Long.class)*60);
//    ioee.setTVelRidotta(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_VELRID, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    
//
//    String oraini=DateUtils.dateToStr(dtInizio, "HHmmss");
//    String orafin=DateUtils.dateToStr(dtFine, "HHmmss");
//    try {
//      Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, centrodiLavoro, OeeCostant.IMAGALPRINC, data, oraini, orafin,ElabDatiOrdiniProdMovex.RAGGR);
//
//      if(mappaDati!=null){
//        ioee.setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
//        ioee.addTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
//        ioee.addTImprodImp(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TAVVIO,Long.class));
//        
//        ioee.setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
//        ioee.setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class)); //nPzTurni
//
//        setupPedane=MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSETUP,Long.class);
//      
//      }
//      if(NomiLineeColomb.IMAGAL_13200.equals(InfoMapLineeUtil.getNomeLineaFromCodice(centrodiLavoro) ) ){
//          tSetupCambioB=xls.getTempoCambioSagoma(data)*180; 
//      }
//      if(!NomiLineeColomb.IMAGAL_13200.equals(InfoMapLineeUtil.getNomeLineaFromCodice(centrodiLavoro) ) ){
//        tSetupAs=ElabDatiOrdiniProdMovex.getInstance().getTempoSetupAs(con, data,centrodiLavoro);
//        setupPedane=Long.valueOf(0);
//      }
//      
//      _logger.info("Runtime da spunte:"+ioee.getTRun()+" npztot :"+ioee.getNumPzTot());
//      //se il centro di lavoro è il 13200 prendiamo il tempo di setup delle pedane oltre a quello dei cambi bordo
//      //se invece i centri sono 13267 o 13269 allora carico il tempo di setup dalla rtvoee21pf mentre setupPedane =0 e tSetupCambioB=0   
//      ioee.addTSetup(tSetupCambioB+setupPedane+tSetupAs); 
//
//    } catch (QueryException ex) {
//      _logger.error("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//      ioee.addError("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea");
//    } catch (SQLException ex) {
//      _logger.error("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//      ioee.addError("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea");
//    } 
//    
//    
//    return ioee;
//  }

  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriImaGalazzano.class);

  
}
