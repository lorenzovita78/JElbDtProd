/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.indicatoriOeeGg.G1P0;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.G1P0.LogFileSuperVNesting;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */


public class IndicatoriNesting extends  AIndicatoriLineaForOee {

  public final static String SCRAP="SCRAP";
  public final static String DROP="DROP";
  
  
  
  public final static String STMINFORIQ=" SELECT panelid,PanelCode,Description,JobOrderID,ProgrSheet,PairedPanelCode \n" +
                                         "      ,PairedPanelID,PairedDescr,ExtractionOrder,Length,Width,LeftSide,Setup \n" +
                                         "  FROM [spv].[dbo].[PanelsForJobAndSheet]\n" +
                                         " where 1=1 \n" +
                                         " and  requestid=? " +
                                         " and PanelID=? ";

  public IndicatoriNesting(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL,Boolean.FALSE);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    
    List<List> periodiProd=getInfoTCdl().getListTurniValidi(dataIni, dataFin);
    
    Long tFermiPort=getIoeeBean().getTGuasti()+getIoeeBean().getTSetup()+getIoeeBean().getTPerditeGestionali();
            
    try {
      Long tFermiLog=Long.valueOf(0);
      Long tsetLog=Long.valueOf(0);
      Long tMicroFLog=Long.valueOf(0);
      
      Map mapp=elabDatiProd(periodiProd);
      tFermiLog=((Long) mapp.get(CostantsColomb.TFERMI));
      tMicroFLog=((Long) mapp.get(CostantsColomb.TMICROFERMI));
      //lavorazione std
      getIoeeBean().setTRun((Long) mapp.get(CostantsColomb.TRUNTIME));
      //lavorazione resti
      getIoeeBean().setTRun2((Long) mapp.get(CostantsColomb.TRUN2));
      //lavorazione scarti
      getIoeeBean().setTRun3((Long) mapp.get(CostantsColomb.TRUN3));
      
      //ioee.addTSetup((Long) mapp.get(CostantsColomb.TSETUP));
      //il cambio pannello sulla  macchina lo associamo al tempo buono e non più al setup
      //ioee.addTExt1((Long) mapp.get(CostantsColomb.TEXT1));
      getIoeeBean().addTRun((Long) mapp.get(CostantsColomb.TEXT1));
      
      //verifica dei periodi di fermo
      _logger.info(">>> Tempo Fermi da Log:"+tFermiLog+
                  " -->tempoMicroF da Log : "+tMicroFLog+
                  " -->tempoSetup da Log :"+(Long) mapp.get(CostantsColomb.TEXT1)+
                  " \n -->tempoImprod da Portale : "+getIoeeBean().getTImprodImpianto()
                   +" --> tempoFermi da Portale: "+tFermiPort);
      
      

      if(tFermiLog>=getIoeeBean().getTImprodImpianto()){
        tFermiLog-=getIoeeBean().getTImprodImpianto();
        
      }else if((tFermiLog+tMicroFLog)>=getIoeeBean().getTImprodImpianto()){
        Long appo=getIoeeBean().getTImprodImpianto()-tMicroFLog;
        tMicroFLog=Long.valueOf(0);
        tFermiLog-=appo;
      }else {
        getIoeeBean().setTImprodImp(Long.valueOf(0));
        getIoeeBean().addWarning("Attenzione ore non produttive superiore ai fermi da file di log");
      }
      getIoeeBean().setTGuasti(tFermiLog);
      getIoeeBean().setTMicrofermi(tMicroFLog);
      
      
    } catch (IOException ex) {
      _logger.error("Errore in fase di accesso al file di log -->"+ex.getMessage());
      getIoeeBean().addError("Problemi di accesso al file di log ");
    } catch (ParseException ex) {
      _logger.error("Errore in fase di lettura del file di log -->"+ex.getMessage());
      getIoeeBean().addError("Problemi di lettura del file di log ");
    } catch (SQLException ex) {
      _logger.error("Errore in fase di lettura dei dati relativi ai riquadri -->"+ex.getMessage());
      getIoeeBean().addError("Problemi di accesso al db del supervisore");
    }
  }
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    IndicatoriOeeGgBean ioee =new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    FileXlsDatiProdStd xls=(FileXlsDatiProdStd) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    
//    
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//    
//    Date dtInizio=xls.getInizioTurnoGg(data);
//    Date dtFine=xls.getFineTurnoGg(data);
//    
//    List<List> periodiProd=xls.getPeriodiProdImpianto(dtInizio,rigaIni, rigaFin);
//    List<List> periodiNnProd=xls.getPeriodiImprodImpianto(dtInizio,rigaIni, rigaFin);
//    
//    
//    ioee.setTDispImp(xls.getTempoFromPeriodi(periodiProd));
//    ioee.setTImprodImp(DateUtils.getTempoIntersectSec(periodiProd, periodiNnProd));
//
////    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
////    ioee.setTSetup(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
////    ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
////    ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//   
//    
//    Long tFermiXls=MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60+
//                   MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60+
//                   MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60;
//    
//    try {
//      Long tFermiLog=Long.valueOf(0);
//      Long tsetLog=Long.valueOf(0);
//      Long tMicroFLog=Long.valueOf(0);
//      
//      Map mapp=elabDatiProd(periodiProd);
//      tFermiLog=((Long) mapp.get(CostantsColomb.TFERMI));
//      tMicroFLog=((Long) mapp.get(CostantsColomb.TMICROFERMI));
//      //lavorazione std
//      ioee.setTRun((Long) mapp.get(CostantsColomb.TRUNTIME));
//      //lavorazione resti
//      ioee.setTRun2((Long) mapp.get(CostantsColomb.TRUN2));
//      //lavorazione scarti
//      ioee.setTRun3((Long) mapp.get(CostantsColomb.TRUN3));
//      
//      //ioee.addTSetup((Long) mapp.get(CostantsColomb.TSETUP));
//      //il cambio pannello sulla  macchina lo associamo al tempo buono e non più al setup
//      //ioee.addTExt1((Long) mapp.get(CostantsColomb.TEXT1));
//      ioee.addTRun((Long) mapp.get(CostantsColomb.TEXT1));
//      
//      //verifica dei periodi di fermo
//      _logger.info(">>> Tempo Fermi da Log:"+tFermiLog+
//                  " -->tempoMicroF da Log : "+ioee.getTImprodImpianto()+
//                  " -->tempoSetup da Log :"+(Long) mapp.get(CostantsColomb.TEXT1)+
//                  " \n -->tempoImprodXls : "+ioee.getTImprodImpianto()
//                   +" --> tempoFermiXls: "+tFermiXls);
//      
//      
////      tFermiLog-=ioee.getTImprodImpianto();
////      if(tFermiLog>tFermiXls){
////        tFermiLog-=(tFermiXls);
////        ioee.setTMicrofermi(tFermiLog);
////      }else{
////       if(ioee.getTPerditeNnRilevate()<0)
////         ioee.addWarning("Attenzione durata Fermi da File Xls superiore alla durata fermi da file di Log"); 
////        
////       }
//      if(tFermiLog>=ioee.getTImprodImpianto()){
//        tFermiLog-=ioee.getTImprodImpianto();
//        
//      }else if((tFermiLog+tMicroFLog)>=ioee.getTImprodImpianto()){
//        Long appo=ioee.getTImprodImpianto()-tMicroFLog;
//        tMicroFLog=Long.valueOf(0);
//        tFermiLog-=appo;
//      }else {
//        ioee.setTImprodImp(Long.valueOf(0));
//        ioee.addWarning("Attenzione ore non produttive superiore ai fermi da file di log");
//      }
//      ioee.setTGuasti(tFermiLog);
//      ioee.setTMicrofermi(tMicroFLog);
//      
//      
//    } catch (IOException ex) {
//      _logger.error("Errore in fase di accesso al file di log -->"+ex.getMessage());
//      ioee.addError("Problemi di accesso al file di log ");
//    } catch (ParseException ex) {
//      _logger.error("Errore in fase di lettura del file di log -->"+ex.getMessage());
//      ioee.addError("Problemi di lettura del file di log ");
//    } catch (SQLException ex) {
//      _logger.error("Errore in fase di lettura dei dati relativi ai riquadri -->"+ex.getMessage());
//      ioee.addError("Problemi di accesso al db del supervisore");
//    }
//    
//    
//    return ioee;
//  }
  
  
  private Map elabDatiProd(List<List> periodiProd) throws IOException, ParseException,SQLException{
    Map mappa=new HashMap();
    LogFileSuperVNesting lg=null;
    Long setup=Long.valueOf(0);
    List<List> listProc=new ArrayList();
    
    for(List per:periodiProd){
       Date dataIni=(Date) per.get(0);
       Date dataFin=(Date) per.get(1);
       
       lg=new LogFileSuperVNesting(InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.NESTINGGAL,dataIni));
       Map info=lg.processLogFile(dataIni, dataFin);
       Long nsetup=ClassMapper.classToClass(info.get(LogFileSuperVNesting.NSETUP),Long.class);
       setup+=(nsetup*60);
       listProc.addAll( (List)info.get(LogFileSuperVNesting.LISTPAN));
    }
    
//    System.out.println("DATAORA;DURATA");
//    for (List rec:listProc){
//      System.out.println(DateUtils.dateToStr((Date)rec.get(0), "dd/MM/yyyy HH:mm:ss")+";"+ClassMapper.classToString(rec.get(3)));
//    }
    
    mappa.put(CostantsColomb.TEXT1, setup);
    mappa.putAll(processListPan(listProc));
    
    return mappa;
  }
  
  
  
  
  
  private Map processListPan(List<List> panProc) throws SQLException, ParseException{
    Map tempiLav=new HashMap<String,Long>();
    Long trun=Long.valueOf(0);
    Long trun2=Long.valueOf(0);//tempo per riquadri di recupero
    Long trun3=Long.valueOf(0);//tempo per riquadri che vanno nel macinatore
    Long tfermi=Long.valueOf(0);
    Long tmicro=Long.valueOf(0);
    
    Connection conSQL=null;
    Long idReq=Long.valueOf(0);
    Long idPan=Long.valueOf(0);
    try{
      conSQL=ColombiniConnections.getDbNestingGalConnection();
      
      for(List record:panProc){
        Date dataIniLav=ClassMapper.classToClass(record.get(0),Date.class);
        idReq=ClassMapper.classToClass(record.get(1),Long.class);
        idPan=ClassMapper.classToClass(record.get(2),Long.class);
        Long runP=ClassMapper.classToClass(record.get(3),Long.class);
        List infoPan=getInfoRiquadro(conSQL, idReq, idPan);
        if(infoPan==null || infoPan.size()<4){
          _logger.warn("Attenzione non trovate informazioni per pannello "+idPan+" e commessa "+idReq);
          trun+=runP;
          continue;
        }
        String type=ClassMapper.classToString(infoPan.get(2));
        String descr=ClassMapper.classToString(infoPan.get(3));
        if(runP>90 && runP<180){ 
          tmicro+=(runP-90);
          runP=Long.valueOf(90);
         //_logger.warn("Attenzione tempo intercorso tra due pz maggiore di 2 minuti.--> IdReq:"+idReq+" idPan:"+idPan);
        }else if(runP>180){
          tfermi+=(runP-90);
          runP=Long.valueOf(90);
        }
        
        //a seconda della tipologia del pannello associo il tempo
        if(type.equals(DROP)){
          trun2+=runP;
        }else if (type.equals(SCRAP)){
          trun3+=runP;
        }else{
          trun+=runP;
        }
        System.out.println(DateUtils.DateToStr(dataIniLav,"dd/MM/yyyy HH:mm:ss")+";"+idReq+";"+idPan+";"+type+";"+descr+";"+runP);
      }
     
    }catch(Exception e){
     _logger.error(" Errore in fase di elaborazione del pannello n."+idPan+" e  commessa "+idReq+" -->"+e.getMessage()); 
    }finally{
      if(conSQL!=null){
        conSQL.close();
      }
    }
    
    tempiLav.put(CostantsColomb.TRUNTIME,trun);
    tempiLav.put(CostantsColomb.TRUN2,trun2);
    tempiLav.put(CostantsColomb.TRUN3,trun3);
    tempiLav.put(CostantsColomb.TFERMI, tfermi);
    tempiLav.put(CostantsColomb.TMICROFERMI, tmicro);
    
    return tempiLav;
  }
  
  
  /**
   * Torna la lista dei riquadri prodotti la lista è composta da idRec,idPan, tempoinSec
   * @param dataRif
   * @return 
   */
  private List getInfoRiquadro(Connection con,Long idReq,Long idPan) throws SQLException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List infoPan=new ArrayList();
    
    try{
      ps = con.prepareStatement(STMINFORIQ); 
      ps.setLong(1, idReq);
      ps.setLong(2, idPan);
      rs=ps.executeQuery();
      while(rs.next()){
        infoPan.add(idReq);
        infoPan.add(idPan);
        infoPan.add(rs.getString("PanelCode")); // indica se il pannello è un resto o uno scarto
        infoPan.add(rs.getString("Description"));
      
      }
    
    }finally{
     if(ps!=null)
       ps.close();
     if(rs!=null)
       rs.close();
   }  
    
    return infoPan;
  }
  

  
  
//  private Map processListPanOld(List<List> panProc) throws SQLException, ParseException{
//    Map tempiLav=new HashMap<String,Long>();
//    Long trun=Long.valueOf(0);
//    Long trun2=Long.valueOf(0);//tempo per riquadri di recupero
//    Long trun3=Long.valueOf(0);//tempo per riquadri che vanno nel macinatore
//    Long tfermi=Long.valueOf(0);
//    Long tmicro=Long.valueOf(0);
//    
//    Connection conSQL=null;
//    Long idReq=Long.valueOf(0);
//    Long idPan=Long.valueOf(0);
//    try{
//      conSQL=Connections.getDbNestingGalConnection();
//      
//      for(List record:panProc){
//        Date dataIniLav=ClassMapper.classToClass(record.get(0),Date.class);
//        idReq=ClassMapper.classToClass(record.get(1),Long.class);
//        idPan=ClassMapper.classToClass(record.get(2),Long.class);
//        Long runP=ClassMapper.classToClass(record.get(3),Long.class);
//        List infoPan=getInfoRiquadro(conSQL, idReq, idPan);
//        if(infoPan==null || infoPan.size()<4){
//          _logger.warn("Attenzione non trovate informazioni per pannello "+idPan+" e commessa "+idReq);
//          trun+=runP;
//          continue;
//        }
//        String type=ClassMapper.classToString(infoPan.get(2));
//        String descr=ClassMapper.classToString(infoPan.get(3));
//        if(runP>120){
//          tfermi+=(runP-120);
//          runP=Long.valueOf(120);
//         _logger.warn("Attenzione tempo intercorso tra due pz maggiore di 2 minuti.--> IdReq:"+idReq+" idPan:"+idPan);
//        }
//        
//        //a seconda della tipologia del pannello associo il tempo
//        if(type.equals(DROP)){
//          trun2+=runP;
//        }else if (type.equals(SCRAP)){
//          trun3+=runP;
//        }else{
//          trun+=runP;
//        }
//        System.out.println(DateUtils.DateToStr(dataIniLav,"dd/MM/yyyy HH:mm:ss")+";"+idReq+";"+idPan+";"+type+";"+descr+";"+runP);
//      }
//     
//    }catch(Exception e){
//     _logger.error(" Errore in fase di elaborazione del pannello n."+idPan+" e  commessa "+idReq+" -->"+e.getMessage()); 
//    }finally{
//      if(conSQL!=null){
//        conSQL.close();
//      }
//    }
//    
//    tempiLav.put(CostantsColomb.TRUNTIME,trun);
//    tempiLav.put(CostantsColomb.TRUN2,trun2);
//    tempiLav.put(CostantsColomb.TRUN3,trun3);
//    tempiLav.put(CostantsColomb.TFERMI, tfermi);
//    
//    return tempiLav;
//  }
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(IndicatoriNesting.class);

  
  
}
