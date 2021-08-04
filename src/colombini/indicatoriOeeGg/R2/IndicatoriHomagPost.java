/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R2;


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
import utils.ClassMapper;
import utils.DateUtils;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriHomagPost  extends AIndicatoriLineaForOee {
  
   
  public final static Double TEMPOMEDIOSETUP=Double.valueOf(105);

  public IndicatoriHomagPost(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Long npedane=Long.valueOf(0);
      String oraInizio=DateUtils.DateToStr(dataIni, "HHmmss");
      String oraFine=DateUtils.DateToStr(dataFin, "HHmmss");
      Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, this.getIoeeBean().getCdLav(), null, getIoeeBean().getData(), oraInizio, oraFine,null);

      if(mappaDati!=null){
        getIoeeBean().setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
        getIoeeBean().setTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
        getIoeeBean().setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
        getIoeeBean().setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class));
        npedane=MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPEDANE,Long.class);
        if(mappaDati.containsKey(CostantsColomb.WARNINGS)){
          getIoeeBean().addWarning((String) mappaDati.get(CostantsColomb.WARNINGS));
        }
      } 
      _logger.info("Runtime da spunte:"+getIoeeBean().getTRun()+" pedane :"+npedane);
      
      
      getIoeeBean().setTSetup(ClassMapper.classToClass(npedane*TEMPOMEDIOSETUP,Long.class));
      
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
//    IndicatoriOeeGgBean ioee=null;
//    try {
//      Long npedane=Long.valueOf(0);
//      XlsHomagPost xls=(XlsHomagPost) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//      Map fermiM=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//      ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//      
//      Integer rigaIni=xls.getRigaInizioGg(data);
//      Integer rigaFin=xls.getRigaFineGg(rigaIni);
//      
//      Date dataInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//      Date dataFine=xls.getFineTurno(data, rigaIni, rigaFin);
//      
//      if(dataInizio==null || dataFine==null){
//        ioee.addError(" Turni di lavoro  non specificati per linea . ");
//        return ioee;
//      }
//      
//      ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//      ioee.setTImprodImp(xls.getMinutiImprodImpianto(data)*60);
//      Long tSetup= xls.getMinutiSetupAgg(rigaIni, rigaFin)*60;
//      
//      ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//      ioee.setTGuasti(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//      ioee.setNGuasti(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//      
//      String oraini=DateUtils.dateToStr(dataInizio, "HHmmss");
//      String orafin=DateUtils.dateToStr(dataFine, "HHmmss");
//      Map mappaDati=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, centrodiLavoro, null, data, oraini, orafin,null);
//
//      if(mappaDati!=null){
//        ioee.setTRun(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TRUNTIME,Long.class));
//        ioee.setTScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.TSCARTI,Long.class));
//        ioee.setNScarti(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NSCARTI,Long.class));
//        ioee.setNPzTot(MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPZTOT,Long.class));
//        npedane=MapUtils.getNumberFromMap(mappaDati,CostantsColomb.NPEDANE,Long.class);
//        if(mappaDati.containsKey(CostantsColomb.WARNINGS)){
//          ioee.addWarning((String) mappaDati.get(CostantsColomb.WARNINGS));
//        }
//      } 
//      _logger.info("Runtime da spunte:"+ioee.getTRun()+" pedane :"+npedane);
//      
//      Double appoSet=(npedane*TEMPOMEDIOSETUP);
//      tSetup+=appoSet.longValue();
//      ioee.setTSetup(tSetup);
//      
//    } catch (QueryException ex) {
//      _logger.error("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//      ioee.addError("Problemi nell'esecuzione della query per calcolare i tempi ciclo della linea");
//    } catch (SQLException ex) {
//      _logger.error("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea -->"+ex.getMessage());
//      ioee.addError("Problemi nell'estrazione dei dati per calcolare i tempi ciclo della linea");
//    }
//    
//    return ioee;
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriHomagPost.class);

  
}
