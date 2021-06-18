/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P2;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.persistence.IndicatoriOeeGgBean;
import colombini.xls.indicatoriOee.R1P2.XlsImballoCappelli;
import db.Connections;
import db.ConnectionsProps;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */



public class IndicatoriImballoCappelli extends AIndicatoriLineaForOee {
  
  
//  private final String URLDBIMBALLO="//172.16.150.10/colombini/History.mdb";
//  private final String URLDBIMBALLOLOCALE="E:/elabJava/DB/History.mdb";      
////  private final String URLDBIMBALLOLOCALE="D:/test/History.mdb";     
//  
//  public final static String urlDB="LCR";
  
  
  private final String PUNCHING="Punching";
  private final String CLOSING="Closing";
  
  private final String PG1="SE - Etichettatrice SETUP";
  private final String PG2="CC - Cambio Cartone";

  public IndicatoriImballoCappelli(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
   @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    Long tpge=Long.valueOf(0);
    List<InfoFermoCdL> fermi=getFermiGg();
    
    if(fermi!=null && !fermi.isEmpty()){
      for(InfoFermoCdL fermo: fermi){
        String caus=fermo.getDescCausale().trim();
        Long value= fermo.getSec();
        if(PG1.equals(caus)|| PG2.equals(caus))
          tpge+=value;
      }
    }
    
    Map map=null;
    try {
      
      map = elabTempiMacchina(dataIni);
      
      getIoeeBean().setTImprodImp(Long.valueOf(0));
      getIoeeBean().setTSetup(Long.valueOf(0));
      
      getIoeeBean().setTDispImp((Long)map.get(CostantsColomb.TDISPON));
      getIoeeBean().setTRun((Long) map.get(CostantsColomb.TRUNTIME));
      
      Long tgua=(Long) map.get(CostantsColomb.TGUASTI);
    
    
      _logger.info(" Guasti da db: "+tgua+"  - perditeGestionali da xls:"+tpge);
      if(tpge>tgua){
        tpge=tgua;
        tgua=Long.valueOf(0);
      }else if(tpge<=tgua){
        tgua-=tpge;
      }

      getIoeeBean().setTGuasti(tgua);
      getIoeeBean().setTPerditeGest(tpge);

      _logger.info("Runtime:"+getIoeeBean().getTRun()+" tGuasti:"+tgua+" tPerdGestionali:"+tpge);
    } catch (SQLException ex) {
      getIoeeBean().addError("Problemi di accesso al database ");
    } catch (ParseException ex){
      getIoeeBean().addError("Problemi in fase di decodifica della data di elaborazione ");
    } catch(Exception e){
      _logger.error(" Errore generico -->"+e.getMessage());
      getIoeeBean().addError("Errore generico "+e.toString());
    }
  }
  
  
  public static String getPathFileMdbSource(){
    String pathFileSource=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.FSOURCE_IMBCAPPELLI));
    return pathFileSource;
  }
  
  public static String getPathFileMdbLocal(){
    String pathLocal=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PATHLOCALE_IMBCAPPELLI));
    String nFile=FileUtils.getShortFileName(getPathFileMdbSource());
    
    return pathLocal+nFile;
  }
  
  
  
  
  
  public Map elabTempiMacchina(Date dataRif) throws SQLException, ParseException  {
    List<List> list=new ArrayList();
    Connection con =null;
    Map map=new HashMap();
    try{
      String dataS=DateUtils.DateToStr(dataRif, "dd/MM/yyyy");
      String urlOdbc=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.ODBC_IMBCAPPELLI));
      con=Connections.getAccessDBConnection(urlOdbc, null, null);
      
  //    Connection con =Connections.getAccessDBConnection("D:\\temp\\oee\\History.mdb", null, null);
      String select=" SELECT  Efficiency.[Stop time], Efficiency.[Alarm time], Efficiency.[Start time],Efficiency.[Unit] "
                    +" FROM Efficiency "+
                     " WHERE Date=DateValue('"+dataS+"')" +
                     "  AND Unit='Closing'"                     ;

      ResultSetHelper.fillListList(con, select, list);
      
      Long tGuasti=Long.valueOf(0);
      Long tDispImp=Long.valueOf(0);
      Long tRuntime=Long.valueOf(0);
      
      if(!list.isEmpty()){
//        for(List record :list){
        List record=list.get(0);
        String type=ClassMapper.classToString(record.get(3));
        //prendo i fermi della chiusura
//          if(CLOSING.equals(type)){
        tGuasti+=ClassMapper.classToClass(record.get(0), Long.class);
        tGuasti+=ClassMapper.classToClass(record.get(1), Long.class);
//          }else{
        tDispImp=ClassMapper.classToClass(record.get(2), Long.class);
//          }
//        }
        tRuntime=tDispImp-tGuasti;
        //Prendiamo le perdite gestionali dal file xls ma per noi il tetto massimo dei fermi
        //è sempre dato da quello che ci dice il db  della macchina
        
      }
      
      map.put(CostantsColomb.TDISPON, tDispImp);
      map.put(CostantsColomb.TGUASTI, tGuasti);
      map.put(CostantsColomb.TRUNTIME, tRuntime);
      
      
    } finally{
      if(con!=null){
        con.close();
      }
    }
     return map;
  }  
  
  
//  public Map elabTempiMacchinaTest(Date dataRif) throws SQLException, ParseException  {
//    List<List> list=new ArrayList();
//    Connection con =null;
//    Map map=new HashMap();
//    try{
//      String dataS=DateUtils.DateToStr(dataRif, "dd/MM/yyyy");
//      con=Connections.getUcanaccessConnection(URLDBIMBALLOLOCALE);
//      
//  //    Connection con =Connections.getAccessDBConnection("D:\\temp\\oee\\History.mdb", null, null);
//      String select=" SELECT  Efficiency.[Stop time], Efficiency.[Alarm time], Efficiency.[Start time],Efficiency.[Unit] "
//                    +" FROM Efficiency "+
//                     " WHERE Date=DateValue('"+dataS+"')" +
//                     "  AND Unit='Closing'"                     ;
//
//      ResultSetHelper.fillListList(con, select, list);
//      
//      Long tGuasti=Long.valueOf(0);
//      Long tDispImp=Long.valueOf(0);
//      Long tRuntime=Long.valueOf(0);
//      
//      if(!list.isEmpty()){
////        for(List record :list){
//        List record=list.get(0);
//        String type=ClassMapper.classToString(record.get(3));
//        //prendo i fermi della chiusura
////          if(CLOSING.equals(type)){
//        tGuasti+=ClassMapper.classToClass(record.get(0), Long.class);
//        tGuasti+=ClassMapper.classToClass(record.get(1), Long.class);
////          }else{
//        tDispImp=ClassMapper.classToClass(record.get(2), Long.class);
////          }
////        }
//        tRuntime=tDispImp-tGuasti;
//        //Prendiamo le perdite gestionali dal file xls ma per noi il tetto massimo dei fermi
//        //è sempre dato da quello che ci dice il db  della macchina
//        
//      }
//      
//      map.put(CostantsColomb.TDISPON, tDispImp);
//      map.put(CostantsColomb.TGUASTI, tGuasti);
//      map.put(CostantsColomb.TRUNTIME, tRuntime);
//      
//      
//    } finally{
//      if(con!=null){
//        con.close();
//      }
//    }
//     return map;
//  } 
  
  
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsImballoCappelli xls=(XlsImballoCappelli) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    List<List> fermi=(List) parameter.get(ICalcIndicatoriOeeLinea.LISTFERMIOEE);
//    
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//   
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//    Date dtInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//    Date dtFine=xls.getFineTurno(data, rigaIni, rigaFin);
//    
//    if(dtInizio==null || dtFine==null){
//      ioee.addError(" Turni di lavoro  non specificati.");
//      return ioee;
//    }
//    
//    //    tImprodImp=xls.getMinutiImprodImpianto(data)*60;
//    Long tpge=Long.valueOf(0);
//    if(fermi!=null && !fermi.isEmpty()){
//      for(List fermo: fermi){
//        String caus=ClassMapper.classToString(fermo.get(0));
//        Long val=ClassMapper.classToClass(fermo.get(1),Long.class);
//        if(PG1.equals(caus)|| PG2.equals(caus))
//          tpge+=val*60;
//      }
//      
//    }
//    Map map=null;
//    try {
//      
//     map = elabTempiMacchina(data);
//      
//      ioee.setTDispImp((Long)map.get(CostantsColomb.TDISPON));
//      ioee.setTRun((Long) map.get(CostantsColomb.TRUNTIME));
//      Long tgua=(Long) map.get(CostantsColomb.TGUASTI);
//    
//    
//      _logger.info(" Guasti da db: "+tgua+"  - perditeGestionali da xls:"+tpge);
//      if(tpge>tgua){
//        tpge=tgua;
//        tgua=Long.valueOf(0);
//      }else if(tpge<=tgua){
//        tgua-=tpge;
//      }
//
//      ioee.setTGuasti(tgua);
//      ioee.setTPerditeGest(tpge);
//
//      _logger.info("Runtime:"+ioee.getTRun()+" tGuasti:"+tgua+" tPerdGestionali:"+tpge);
//    } catch (SQLException ex) {
//      ioee.addError("Problemi di accesso al database ");
//    } catch (ParseException ex){
//      ioee.addError("Problemi in fase di decodifica della data di elaborazione ");
//    }
//    
//    
//    
//    return ioee;
//    
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriImballoCappelli.class);

 
}
