/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGgOLD;



import colombini.util.InfoMapLineeUtil;
import colombini.costant.NomiLineeColomb;
import db.persistence.PersistenceManager;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.indicatoriOee.utils.FermiGgUtils;
import colombini.indicatoriOeeGg.R2.IndicatoriHomagPost;
import colombini.indicatoriOeeGg.R2.IndicatoriSQBStefani;
import colombini.indicatoriOeeGg.R2.IndicatoriSezGabbiani;
import colombini.xls.indicatoriOee.R2.XlsHomagPost;
import colombini.xls.indicatoriOee.R2.XlsSQBStefani;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class CalcIndicatoriOeeGgR2 extends ACalcIndicatoriOeeGgOLD {

  public CalcIndicatoriOeeGgR2(List<Date> giorni, Map lineeToElab) {
    super(giorni, lineeToElab);
  }

  @Override
  public void execute(PersistenceManager apm) {
    super.execute(apm);
  }
  
//  @Override
//  protected void elabIndicatoriOeeLinee(PersistenceManager apm) {
//     _logger.info(" ----- #### INIZIO CALCOLO OEE ROVERETA 2 #### ----- ");
//    _logger.info("ROVERETA 2 PIANO 1  ");
//      
//    if(checkLineaElab(NomiLineeColomb.SQBSTEFANI)){
//       _logger.info(" ----- Inizio Elaborazione SQB Stefani -----");
//      XlsSQBStefani xls=new XlsSQBStefani(InfoMapLineeUtil.getNomeFileXls(NomiLineeColomb.SQBSTEFANI));
//      IndicatoriSQBStefani calc=new IndicatoriSQBStefani();
//      super.elabIndicatoriOeeLineaStdOld(apm, calc,xls,NomiLineeColomb.SQBSTEFANI );
//      _logger.info(" ----- Fine Elaborazione SQB Stefani -----");
//    }
//    
//    if(checkLineaElab(NomiLineeColomb.HOMAGPOST)){
//       _logger.info(" ----- Inizio Elaborazione Homag Post -----");
//      XlsHomagPost xls=new XlsHomagPost(InfoMapLineeUtil.getNomeFileXls(NomiLineeColomb.HOMAGPOST));
//      IndicatoriHomagPost calc=new IndicatoriHomagPost();
//      super.elabIndicatoriOeeLineaStdOld(apm, calc,xls,NomiLineeColomb.HOMAGPOST );
//      _logger.info(" ----- Fine Elaborazione Homag Post -----");
//    }
//      
//      
//    if(checkLineaElab(NomiLineeColomb.SEZGABBIANI))
//      executeSezGabbiani(apm,NomiLineeColomb.SEZGABBIANI);
//    
//    _logger.info(" ----- #### FINE CALCOLO OEE ROVERETA 2 #### -----   ");
//  }
//  
//  
//  
//  private void executeSezGabbiani(PersistenceManager apm,String nomeLinea){
//    _logger.info(" ----- Inizio Elaborazione Sezionatrice GABBIANI #### -----");
//    String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
//    
//    IndicatoriSezGabbiani calc= new IndicatoriSezGabbiani();
//    Map parameter=new HashMap();
//    
//    Map causaliLinea=getCausaliLinea(codLinea);
//    parameter.put(ICalcIndicatoriOeeLinea.CAUSALILINEA, causaliLinea);
//
//    for(Date data: giorni){
//      try{
//        IIndicatoriOeeGg dtOee= calc.getIndicatoriOeeLineaGg(apm.getConnection(), data, codLinea, parameter);
//
//        List fermiListgg=calc.getFermiGg();
//        List fermiBeans=FermiGgUtils.getInstance().getListBeanFermi(data, fermiListgg, causaliLinea);
//
//        consolidamentoFermiLineaOld(apm, data, codLinea, FermiGgUtils.getInstance().getListBeansDurataMin(fermiBeans));
//        consolidamentoIndicatoriLinea(apm, data, dtOee);
//      } catch (Exception e){
//      _logger.error(" Errore generico in fase di calcolo degli Indicatori Oee per la linea "+nomeLinea+ " --> "+e.getMessage());
//      addError(nomeLinea, " Errore nel calcolo degli Indicatori di Oee per il giorno "+data+ " --> "+e.getMessage());
//      }
//    }
//   _logger.info(" ----- Fine Elaborazione Sezionatrice GABBIANI   -----");
//  }
//  
//  
//  private static final Logger _logger = Logger.getLogger(CalcIndicatoriOeeGgR2.class);
//
//  @Override
//  protected AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea, Date data) {
//    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//  }
//
//  

  @Override
  protected void elabIndicatoriOeeLinee(PersistenceManager apm) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  protected AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea, Date data) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  
}
