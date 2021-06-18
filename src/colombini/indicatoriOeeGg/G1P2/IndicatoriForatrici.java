/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P2;

import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.ElabDatiOrdiniProdMovex;
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
public class IndicatoriForatrici  extends AIndicatoriLineaForOee {

  public IndicatoriForatrici(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      String oraInizio=DateUtils.DateToStr(dataIni, "HHmmss");
      String oraFine=DateUtils.DateToStr(dataFin, "HHmmss");
      Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, this.getIoeeBean().getCdLav(), null, getIoeeBean().getData(), oraInizio, oraFine,null);
      
      if(mappaDati!=null){
          getIoeeBean().setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
          getIoeeBean().setTRun2(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TLORDO,Long.class));
          getIoeeBean().setTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
          
          getIoeeBean().setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
          getIoeeBean().setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class));
          
      } 
      _logger.info("Runtime da spunte:"+getIoeeBean().getTRun()+" npztot :"+getIoeeBean().getNumPzTot());
        
      
      
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
//    
//      IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//      XlsForatrici xls=(XlsForatrici) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//      Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//      Integer rigaIni=xls.getRigaInizioGg(data);
//      Integer rigaFin=xls.getRigaFineGg(rigaIni);
//      
//  //    Date dtInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//  //    Date dtFine=xls.getFineTurno(data, rigaIni, rigaFin);
//      Date dtInizio=xls.getInizioTurnoStdGg(data);
//      Date dtFine=xls.getFineTurnoStdGg(data);
//      
//      if(!ioee.checkDateTurni(dtInizio, dtFine)){
//        return ioee;
//      }
//      
//      ioee.setTDispImp(DateUtils.numSecondiDiff(dtInizio, dtFine)); //ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//      ioee.setTImprodImp(xls.getDurataPeriodiInRange(xls.getPeriodiImprodImpianto(dtInizio, rigaIni, rigaFin), dtInizio, dtFine));
//      
//      ioee.setTSetup(xls.getTempoSetupGg(data)*60);
//      ioee.setTMicrofermi(xls.getTempoMicrofermiGg(data)*60);
//      
//      ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//      ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//      ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//      ioee.setTVelRidotta(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_VELRID, Long.class)*60);
//      
//      Long ril=MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_RIPASSI, Long.class)*60;
//      ril+=MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_RECUPERI, Long.class)*60;
//      ioee.setTRilavorazioni(ril);
//      
//      String oraini=DateUtils.dateToStr(dtInizio, "HHmmss");
//      String orafin=DateUtils.dateToStr(dtFine, "HHmmss");
//      try {
//        Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, centrodiLavoro, null, data, oraini, orafin,null);
//        
//        if(mappaDati!=null){
//          ioee.setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
//          ioee.setTRun2(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TLORDO,Long.class));
//          ioee.setTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
//          
//          ioee.setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
//          ioee.setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class));
//          
//      } 
//      _logger.info("Runtime da spunte:"+ioee.getTRun()+" npztot :"+ioee.getNumPzTot());
//        
//      
//        
//      } catch (QueryException ex) {
//        _logger.error("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//        ioee.addError("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea");
//      } catch (SQLException ex) {
//        _logger.error("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//        ioee.addError("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea");
//      } 
//      
//      return ioee;
//      
//  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriForatrici.class);

  
}
