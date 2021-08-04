/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGgOLD;



import colombini.util.InfoMapLineeUtil;
import colombini.costant.NomiLineeColomb;
import colombini.util.DatiColliGgVDL;
import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOeeGg.R1P0.IndicatoriCombimaR1P0;
import colombini.indicatoriOeeGg.R1P0.IndicatoriSchellingR1P0;
import colombini.indicatoriOeeGg.R1P0.IndicatoriSortingStationR1P0;
import colombini.indicatoriOeeGg.R1P1.IndicatoriCombimaR1P1;
import colombini.indicatoriOeeGg.R1P1.IndicatoriMawArtec;
import colombini.indicatoriOeeGg.R1P1.IndicatoriMawFianchi2;
import colombini.indicatoriOeeGg.R1P1.IndicatoriSchellingR1P1;
import colombini.indicatoriOeeGg.R1P2.IndicatoriForatriceAlberti;
import colombini.indicatoriOeeGg.R1P2.IndicatoriImballoCappelli;
import colombini.indicatoriOeeGg.R1P2.IndicatoriLineaTavoli;
import colombini.indicatoriOeeGg.R1P2.IndicatoriStrettoiMawPriess;
import colombini.util.DatiProdUtils;
import colombini.xls.indicatoriOee.R1P1.XlsMawArtec;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @TODO classe da modificare ...prevedere chiamata alla classe dell'indicatore invece di ripetere il codice 01/03/2017
 * @author lvita
 */
public class CalcIndicatoriOeeGgR1  extends ACalcIndicatoriOeeGgOLD {

  public CalcIndicatoriOeeGgR1(List<Date> giorni, Map lineeToElab) {
    super(giorni, lineeToElab);
  }

  

  @Override
  protected AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea, Date data) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
 
  
//  private Map <Date,DatiColliGgVDL> colliGgVDL; //mappa con chiave il giorno e valore oggettoInfoCollo
//  private Map <String,List> lineeLogicheLinea; //mappa con chiave il giorno e valore oggettoInfoCollo
//  
////  public SchedulatoreRov1(){
////  }
//   
//  public CalcIndicatoriOeeGgR1(List<Date> giorni,Map lineeToElab){
//    super(giorni, lineeToElab);
//    colliGgVDL=new HashMap();
//    lineeLogicheLinea=new HashMap();
//    
//  }
//
//  
//  @Override
  @Override
  protected void elabIndicatoriOeeLinee(PersistenceManager apm) {
    _logger.info(" ----- ####  CALCOLO INDICATORI OEE ROVERETA 1 #### -----");
//    prepareDatiVDL();
//    prepareLineeLogicheStrettR1P2(apm.getConnection());
//    prepareFilesMdb();
//    
//    elabIndicatoriOeeLinee(apm);
    
    
    //vecchia gestione da eliminare
    if(checkLineaElab(NomiLineeColomb.MAWARTEC) || checkLineaElab(NomiLineeColomb.MAWARTECL1) || checkLineaElab(NomiLineeColomb.MAWARTECL2) ){
     _logger.info(" ----- Inizio Elaborazione Maw Artec -----");
     XlsMawArtec xls=new XlsMawArtec(InfoMapLineeUtil.getNomeFileXls(NomiLineeColomb.MAWARTECL1));
     IndicatoriMawArtec mwA=new IndicatoriMawArtec(CostantsColomb.AZCOLOM,new Date(),NomiLineeColomb.MAWARTECL1);
     _logger.info("Foratrice 1");
     super.elabIndicatoriOeeLineaStdOld(apm, mwA,xls,NomiLineeColomb.MAWARTECL1 );
     _logger.info("Foratrice 2");
     super.elabIndicatoriOeeLineaStdOld(apm, mwA,xls,NomiLineeColomb.MAWARTECL2 );
     _logger.info(" ----- Fine Elaborazione Maw Artec -----");
   }
    
    _logger.info(" ----- ####  FINE CALCOLO INDICATORI OEE ROVERETA 1 #### -----");
  }
//
//  
//  private void elabIndicatoriOeeLinee(PersistenceManager man){
//    List<String> linee=getListLineeToElab(CostantsColomb.ROVERETA1, null);
//    List<List> lineeOrd=getListLineeSorted(linee);
//    
//    for(List infoLinea:lineeOrd){
//      String codLinea=(String) infoLinea.get(1);
//      //codice da togliere quando il passaggio al nuovo metodo sarà completo
//      
//      logInfoMsg(" ----- Inizio Elaborazione "+codLinea);
//      for(Date data :giorni){
//         logInfoMsg("Elaborazione Indicatori Oee per giorno "+data);
//         AIndicatoriLineaForOee ioee=getObjIndicatoreOeeLinea(codLinea,data);
//         if(ioee==null){
//           addError(codLinea, "Attenzione nessuna classe per generazione indicatori linea");
//         }else{
//           Map param=getParameterCdl(codLinea,data);
//           IIndicatoriOeeGg ie=ioee.getIndicatoriOeeLineaGg(man.getConnection(), data, codLinea, param);
//           consolidamentoIndicatoriLinea(man, data, ie);
//         }
//      }
//      logInfoMsg(" ----- Fine Elaborazione "+codLinea);
//    }
//    
//  }
//  
//  /**
//   * Prepara la mappa contenente dataRiferimento e oggetti DatiColliGgVDL
//   */
//  private void prepareDatiVDL(){
//    for(Date data:giorni){
//      DatiColliGgVDL ldtc=new DatiColliGgVDL(data);
//      colliGgVDL.put(data,ldtc);
//    }
//  }
// 
//  
//  private void prepareLineeLogicheStrettR1P2(Connection con){
//    
//    for(String linea:getListLineeStrettoiP2()){ 
//       List lineeLog=DatiProdUtils.getInstance().getLineeLogicheMVX(con, linea);
//       lineeLogicheLinea.put(linea, lineeLog);
//    }
//  }
//  
//  
//  
//  
//  
//  
//  @Override
//  protected AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea,Date data){
//    AIndicatoriLineaForOee ioeeLinea=null;
//    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMAR1P0).equals(codLinea)){
//      ioeeLinea=new IndicatoriCombimaR1P0(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P0).equals(codLinea)  ||
//             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P0).equals(codLinea) ){
//      ioeeLinea=new IndicatoriSchellingR1P0(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SORTSTATIONR1P0).equals(codLinea) ) {
//      ioeeLinea=new IndicatoriSortingStationR1P0(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA1R1P1).equals(codLinea)  ||
//             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA2R1P1).equals(codLinea) ){
//      ioeeLinea=new IndicatoriCombimaR1P1(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P1).equals(codLinea)  ||
//             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P1).equals(codLinea) ){
//      ioeeLinea=new IndicatoriSchellingR1P1(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWFIANCHI2).equals(codLinea) ) {
//      ioeeLinea=new IndicatoriMawFianchi2(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOMAW).equals(codLinea)  ||
//             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOPRIESS).equals(codLinea) ){
//      ioeeLinea=new IndicatoriStrettoiMawPriess(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.IMBCAPPELLI).equals(codLinea) ) {
//      ioeeLinea=new IndicatoriImballoCappelli(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORALBERTI).equals(codLinea) ) {
//      ioeeLinea=new IndicatoriForatriceAlberti(CostantsColomb.AZCOLOM, data, codLinea);
//    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.TAVOLISCRIV).equals(codLinea) ) {
//      ioeeLinea=new IndicatoriLineaTavoli(CostantsColomb.AZCOLOM, data, codLinea);
//    }
//    
//    
//    return ioeeLinea;
//  }
//          
//  
//  
// 
//  
//  
  private static final Logger _logger = Logger.getLogger(CalcIndicatoriOeeGgR1.class);
//
//  

  
}
