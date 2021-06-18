/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGgOLD;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.calc.CalcIndicatoriOeeGg;
import colombini.indicatoriOee.utils.FermiGgUtils;
import colombini.indicatoriOeeGg.G1P0.IndicatoriNesting;
import colombini.indicatoriOeeGg.G1P0.IndicatoriPantografi;
import colombini.indicatoriOeeGg.G1P2.IndicatoriForatrici;
import colombini.indicatoriOeeGg.G1P2.IndicatoriForatriciBiesse;
import colombini.indicatoriOeeGg.G1P2.IndicatoriImaGalazzano;
import colombini.indicatoriOeeGg.G1P2.IndicatoriSchellLong;
import colombini.indicatoriOeeGg.G1P2.IndicatoriSquadraborda;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.FileXlsDatiProdStd;
import colombini.xls.indicatoriOee.G1P0.XlsPant01043;
import colombini.xls.indicatoriOee.G1P0.XlsPant01044;
import colombini.xls.indicatoriOee.G1P0.XlsPant01045;
import colombini.xls.indicatoriOee.G1P0.XlsPant01046;
import colombini.xls.indicatoriOee.G1P0.XlsPant01064;
import colombini.xls.indicatoriOee.G1P0.XlsPant01065;
import colombini.xls.indicatoriOee.G1P0.XlsPantografiStd;
import colombini.xls.indicatoriOee.G1P2.XlsFPantografoMorbidelli;
import colombini.xls.indicatoriOee.G1P2.XlsForatriceBusellato;
import colombini.xls.indicatoriOee.G1P2.XlsForatriceMaw;
import colombini.xls.indicatoriOee.G1P2.XlsForatrici;
import colombini.xls.indicatoriOee.G1P2.XlsImaSezionatrici;
import colombini.xls.indicatoriOee.G1P2.XlsNuoveForatriciBiesse;
import colombini.xls.indicatoriOee.G1P2.XlsSchellingLong;
import colombini.xls.indicatoriOee.G1P2.XlsSquadraBorda;
import db.ResultSetHelper;
import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.ListUtils;

/**
 *
 * @author lvita
 */
public class CalcIndicatoriOeeGgG1 extends ACalcIndicatoriOeeGgOLD {

  public final static String P01043="P01043";
  public final static String P01044="P01044";
  public final static String P01045="P01045";
  public final static String P01046="P01046";
  public final static String P01064="P01064";
  public final static String P01065="P01065";

  public CalcIndicatoriOeeGgG1(List<Date> giorni, Map lineeToElab) {
    super(giorni, lineeToElab );
  }

  @Override
  public void execute(PersistenceManager apm) {
    super.execute(apm);
  }
  
  
  @Override
  protected void elabIndicatoriOeeLinee(PersistenceManager apm) {
//    _logger.info(" ----- #### INIZIO CALCOLO OEE GALAZZANO #### -----");
//
//    _logger.info(" -----  GALAZZANO P0 -----");
//    elabG1P0(apm);
//    
//    _logger.info(" -----  GALAZZANO P2 -----");
//    elabG1P2(apm);
//      
//    _logger.info(" ----- #### FINE CALCOLO OEE GALAZZANO   #### -----");
  }

  
  
  
  //----------------  GALAZZANO P0  ------------------------\\
  
//  private void elabG1P0(PersistenceManager apm) {
//    ArrayList <String> lineeP0= (ArrayList<String>) getListLineeToElab(CostantsColomb.GALAZZANO, CostantsColomb.PIANO0);
//   
//    if(lineeP0==null || lineeP0.isEmpty())
//      return;
//    
//    
//    try {
//      Map matr=loadMapPercPantografi(apm.getConnection()); 
//      Map sag=loadSagomeArticoli(apm.getConnection());
//     
//      for(String nomeLinea: lineeP0){
//        if(NomiLineeColomb.NESTINGGAL.equals(nomeLinea)){
//          elabIndicatoriOeeLineaStdOld(apm, new IndicatoriNesting(), new FileXlsDatiProdStd(InfoMapLineeUtil.getNomeFileXls(nomeLinea)), nomeLinea);
//        
//        }else{
//           _logger.info(" ----- Inizio Elaborazione Pantografo "+nomeLinea+" ----- ");
//           Map parameter=new HashMap();
//           parameter.put(IndicatoriPantografi.MAPPERCENTUALI, matr);
//           parameter.put(IndicatoriPantografi.MAPNSAGARTICOLI, sag);
//           super.elabIndicatoriOeeLineaStdOld(apm,new IndicatoriPantografi(), getXlsPantografi(nomeLinea),nomeLinea,parameter);
//           _logger.info(" ----- Fine Elaborazione Pantografo "+nomeLinea+" -----");
//        }
//      }
//    
//    } catch (SQLException ex) {
//      addError(lineeP0.get(0), "Impossibile accedere al db  per il reperimento della matrice di conversione dei tempi");
//      _logger.error("Problemi di accesso al db per la lettura della matrice dei tempi ->"+ex.getMessage());
//    }
//    
//  }
//  
//  
//  private void elabG1P2(PersistenceManager apm){
//    ArrayList <String> lineeP2=(ArrayList<String>) getListLineeToElab(CostantsColomb.GALAZZANO, CostantsColomb.PIANO2);
//    
//    
//    for(String nomeLinea: lineeP2){
//      
//      if(isImaSez132(nomeLinea)){
//        _logger.info(" ----- Inizio Elaborazione Sezionatrice Ima "+nomeLinea+" ----- ");
//        elabIndicatoriOeeLineaStdOld(apm, new IndicatoriImaGalazzano(),new XlsImaSezionatrici(InfoMapLineeUtil.getNomeFileXls(nomeLinea)),nomeLinea);
//        _logger.info(" ----- Fine Elaborazione Sezionatrice Ima "+nomeLinea+" ----- ");
//      }
//      
//      if(isImaSqb(nomeLinea)){
//        _logger.info(" ----- Inizio Elaborazione Squadrabordatrice "+nomeLinea+" ----- ");
//        elabIndicatoriOeeLineaStdOld(apm, new IndicatoriSquadraborda(),new XlsSquadraBorda(InfoMapLineeUtil.getNomeFileXls(nomeLinea)),nomeLinea);
//        _logger.info(" ----- Fine Elaborazione Squadrabordatrice "+nomeLinea+" ----- ");
//      }
//      
//      if(NomiLineeColomb.SCHELLLONG_GAL.equals(nomeLinea)){
//        elabSchellingLong(apm);
//      }
//      
//      if(isForatrice(nomeLinea)){
//         _logger.info(" ----- Inizio Elaborazione Foratrice "+nomeLinea+" ----- ");
//        elabIndicatoriOeeLineaStdOld(apm, new IndicatoriForatrici(),getXlsForatrici(nomeLinea),nomeLinea);
//        _logger.info(" ----- Fine Elaborazione Foratrice "+nomeLinea+" ----- ");
//      }
//      
//      if(isNewForatriceBiesse(nomeLinea)){
//         _logger.info(" ----- Inizio Elaborazione Foratrice "+nomeLinea+" ----- ");
////        elabIndicatoriOeeLineaStdOld(apm, new IndicatoriForatriciBiesse(),getXlsForatrici(nomeLinea),nomeLinea);
//         elabForatriciBiesseNew(apm, nomeLinea);
//         elabForatriciBiesseNew(apm, nomeLinea+"BIS");
////        elabIndicatoriOeeLineaStdOld(apm, new IndicatoriForatrici(),getXlsForatrici(nomeLinea),nomeLinea);
//        _logger.info(" ----- Fine Elaborazione Foratrice "+nomeLinea+" ----- ");
//      }
//      
//    }
//
//    
//  }
//  
//  private Boolean isImaSez132(String nomeLinea){
//    if(NomiLineeColomb.IMAGAL_13200.equals(nomeLinea)||
//       NomiLineeColomb.IMAGAL_13267.equals(nomeLinea)||
//       NomiLineeColomb.IMAGAL_13269.equals(nomeLinea) )
//    return Boolean.TRUE;
//    
//    return Boolean.FALSE;   
//  }
//  
//  private Boolean isImaSqb(String nomeLinea){
//    if(NomiLineeColomb.SQBL13266.equals(nomeLinea)||
//       NomiLineeColomb.SQBL13268.equals(nomeLinea)
//       )
//    return Boolean.TRUE;
//    
//    
//    return Boolean.FALSE;   
//  }
//  
//  
//  private Boolean isForatrice(String nomeLinea){
//    if(NomiLineeColomb.FORBIESSE.equals(nomeLinea)||
//       NomiLineeColomb.FORMORBIDELLI.equals(nomeLinea)||
//       NomiLineeColomb.FORMAW.equals(nomeLinea)||
//       NomiLineeColomb.PANTMORBIDELLI.equals(nomeLinea)||
//       NomiLineeColomb.FORBUSELLATO.equals(nomeLinea) )
//    return Boolean.TRUE;
//    
//    return Boolean.FALSE;   
//  }
//  
//  private Boolean isNewForatriceBiesse(String nomeLinea){
//    if(NomiLineeColomb.FORBSKERNEL.equals(nomeLinea)||
//       NomiLineeColomb.FORBSFTT.equals(nomeLinea)  )
//    return Boolean.TRUE;
//    
//    return Boolean.FALSE;   
//  }
//  
//  
//  
//  private XlsPantografiStd getXlsPantografi(String nomeLinea){
//    
//    XlsPantografiStd xls=null; 
//    
//    if(NomiLineeColomb.PANTH2519.equals(nomeLinea)){
//      xls=new XlsPant01043(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(NomiLineeColomb.PANTH1493.equals(nomeLinea)){
//      xls=new XlsPant01044(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(NomiLineeColomb.PANTB22146.equals(nomeLinea)){
//      xls=new XlsPant01045(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(NomiLineeColomb.PANTB22110.equals(nomeLinea)){
//      xls=new XlsPant01046(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(NomiLineeColomb.PANTB4001.equals(nomeLinea)){
//      xls=new XlsPant01064(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(NomiLineeColomb.PANTH4038.equals(nomeLinea)){
//      xls=new XlsPant01065(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }
//    
//    return xls;
//  }
//  
//  private XlsForatrici getXlsForatrici(String nomeLinea){
//    XlsForatrici fxls=null;
//    
//    if(nomeLinea.equals(NomiLineeColomb.FORMAW)){
//      fxls=new XlsForatriceMaw(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if (nomeLinea.equals(NomiLineeColomb.FORBUSELLATO)){
//      fxls=new XlsForatriceBusellato(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else  if (nomeLinea.equals(NomiLineeColomb.PANTMORBIDELLI)){
//      fxls=new XlsFPantografoMorbidelli(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }else if(nomeLinea.equals(NomiLineeColomb.FORBSKERNEL) || nomeLinea.equals(NomiLineeColomb.FORBSFTT) ){
//      fxls=new XlsNuoveForatriciBiesse(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    } else{
//      fxls=new XlsForatrici(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//    }
//    
//    return fxls;
//  }
//  
//  
//  private Map<String,Integer> loadSagomeArticoli(Connection con){
//   Map sagome=new HashMap();
//   List<List> l =new ArrayList();
//    try {
//      ResultSetHelper.fillListList(con, " select trim(ZPARTI),ZPNMSG from mcobmoddta.zoesap ", l);
//      for(List record:l){
//        String arti=ClassMapper.classToString(record.get(0));
//        Integer nsag=ClassMapper.classToClass(record.get(1),Integer.class);
//        if(!sagome.containsKey(l)){
//          sagome.put(arti, nsag);
//        }else{
//          addWarning("SAGOMATI", "Articolo già presente in tabella ZOESAP :"+arti);
//        }
//      }
//    } catch (SQLException ex) {
//      _logger.error("Errore in fase di accesso alla tabella ZOESAP -->"+ex.getMessage());
//      addError("SAGOMATI", "Errore in fase di lettura della tabella ZOESAP");
//    }
//   
//   return sagome;
//  }
//  
//  private Map loadMapPercPantografi(Connection con ) throws SQLException{
//    PreparedStatement ps = null;
//    ResultSet rs = null;
//    Map matrice=new HashMap <String,Map>();
//    List <String> campiPer=getListPercSagom();
//    String qry=" SELECT CENTRO,"+ListUtils.toCommaSeparatedString(getListPercSagom())
//            +  " from MCOBMODDTA.RTVOEEMSAG ";
//    
//    try{
//      ps=con.prepareStatement(qry);
//      rs=ps.executeQuery();
//      while(rs.next()){
//        String centro=rs.getString("CENTRO");       
//        Map mappaPerc=new HashMap <String,Double> ();
//        for(String perc:campiPer){
//          Double val=rs.getDouble(perc);
//          mappaPerc.put(perc, val);
//        }
//       matrice.put(centro.trim(),mappaPerc);
//      }
//      
//    }finally{
//      if(ps!=null)
//        ps.close();
//      if(rs!=null)
//        rs.close();
//    }
//    return matrice;
//  }
//  
//  
//  private List getListPercSagom(){
//    List perc=new ArrayList();
//    perc.add(P01043);
//    perc.add(P01044);
//    perc.add(P01045);
//    perc.add(P01046);
//    perc.add(P01064);
//    perc.add(P01065);
//    
//    return perc;
//  }
//  
//  
//  private void elabSchellingLong(PersistenceManager apm){
//    XlsSchellingLong fxls=null;
//    _logger.info("-- ## Inizio Elaborazione Schelling Longitudinale ##---");
//    Map parameter=new HashMap();
//    
//    String nomeLinea=NomiLineeColomb.SCHELLLONG_GAL;
//    String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
//    try {
//      IndicatoriSchellLong indS=new IndicatoriSchellLong();
//      fxls=new XlsSchellingLong(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//      fxls.openFile();
//      
//      Map causaliLinea=getCausaliLinea(InfoMapLineeUtil.getCodiceLinea(nomeLinea));
//      
//      parameter.put(ICalcIndicatoriOeeLinea.FILEXLS, fxls);
//      parameter.put(ICalcIndicatoriOeeLinea.CAUSALILINEA, causaliLinea);
//      
//      for(Date data: giorni){ 
//        try{
//          IIndicatoriOeeGg dtOee= indS.getIndicatoriOeeLineaGg(apm.getConnection(), data, codLinea, parameter);
//
//          List fermiListgg=indS.getFermiGg();
//          List fermiBeans=FermiGgUtils.getInstance().getListBeanFermi(data, fermiListgg, causaliLinea);
//
//          consolidamentoFermiLineaOld(apm, data, codLinea, FermiGgUtils.getInstance().getListBeansDurataMin(fermiBeans));
//          consolidamentoIndicatoriLinea(apm, data, dtOee);
//        } catch (Exception e){
//          _logger.error(" Errore generico in fase di calcolo degli Indicatori Oee per la linea "+nomeLinea+ " --> "+e.getMessage());
//          addError(codLinea, " Errore nel calcolo degli Indicatori di Oee per il giorno "+data+ " --> "+e.getMessage());
//    
//        }
//      }
//    } catch (IOException ex) {
//     _logger.error("Errore in fase di accesso  del file xls "+fxls.getFileName()+" :"+ex.getMessage());
//     addError(codLinea, "Problemi di lettura del file xls");
//    } finally{
//       try {
//         fxls.closeFile();
//       } catch (IOException ex) {
//         _logger.error("Errore in fase di chiusura del file xls "+fxls.getFileName()+" :"+ex.getMessage());
//       }
//      _logger.info("-- ##Terminata elaborazione Schelling Longitudinale ##---"); 
//    }
//  }
//  
//  
//  private void elabForatriciBiesseNew(PersistenceManager apm,String nomeLinea){
//    XlsNuoveForatriciBiesse fxls=null;
//    Map causaliLinea=null;
//    _logger.info("-- ## Inizio Elaborazione Foratrice Biesse "+nomeLinea+" ##---");
//    Map parameter=new HashMap();
//    
//    
//    String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
//    try {
//      IndicatoriForatriciBiesse indF=new IndicatoriForatriciBiesse();
//      if(nomeLinea.contains(IndicatoriForatriciBiesse.BIS))
//        nomeLinea=nomeLinea.replace(IndicatoriForatriciBiesse.BIS, "");
//        
//      fxls=new XlsNuoveForatriciBiesse(InfoMapLineeUtil.getNomeFileXls(nomeLinea));
//      fxls.openFile();
//      parameter.put(ICalcIndicatoriOeeLinea.FILEXLS, fxls);
//      causaliLinea=getCausaliLinea(InfoMapLineeUtil.getCodiceLinea(nomeLinea));
//       
//      parameter.put(ICalcIndicatoriOeeLinea.CAUSALILINEA, causaliLinea);
//      
//      for(Date data: giorni){ 
//        try{
//          IIndicatoriOeeGg dtOee= indF.getIndicatoriOeeLineaGg(apm.getConnection(), data, codLinea, parameter);
//
//          List fermiListgg=indF.getFermiGg();
//          List fermiBeans=FermiGgUtils.getInstance().getListBeanFermi(data, fermiListgg, causaliLinea);
//
//          consolidamentoFermiLineaOld(apm, data, codLinea, FermiGgUtils.getInstance().getListBeansDurataMin(fermiBeans));
//          consolidamentoIndicatoriLinea(apm, data, dtOee);
//        } catch (Exception e){
//          _logger.error(" Errore generico in fase di calcolo degli Indicatori Oee per la linea "+codLinea+ " --> "+e.getMessage());
//          addError(codLinea, " Errore nel calcolo degli Indicatori di Oee per il giorno "+data+ " --> "+e.getMessage());
//    
//        }
//      }
//    } catch (IOException ex) {
//     _logger.error("Errore in fase di accesso  del file xls "+fxls.getFileName()+" :"+ex.getMessage());
//     addError(codLinea, "Problemi di lettura del file xls");
//    } finally{
//       try {
//         fxls.closeFile();
//       } catch (IOException ex) {
//         _logger.error("Errore in fase di chiusura del file xls "+fxls.getFileName()+" :"+ex.getMessage());
//       }
//      _logger.info("-- ##Terminata elaborazione Foratrice "+nomeLinea+ " ##---"); 
//    }
//  }
//  
// 
//  private static final Logger _logger = Logger.getLogger(CalcIndicatoriOeeGgG1.class);
//
  @Override
  protected AIndicatoriLineaForOee getObjIndicatoreOeeLinea(String codLinea, Date data) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  
  
}
