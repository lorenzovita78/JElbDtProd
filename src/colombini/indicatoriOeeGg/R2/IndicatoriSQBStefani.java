/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R2;

import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.logFiles.R2P1.LogFileSQBStefani;
import colombini.logFiles.R2P1.ObjPrgSQBStefani;
import colombini.logFiles.R2P1.ProgFileSQBStefani;
import colombini.model.CausaliLineeBean;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.util.InfoMapLineeUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriSQBStefani extends AIndicatoriLineaForOee {

  public IndicatoriSQBStefani(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Map info=calcolaTempiFromLogFile(dataIni, dataFin);
      
      if(info==null || info.isEmpty()){
        getIoeeBean().addWarning("Nessuna informazione presente per la data indicata");
        return ;
      }
      
      getIoeeBean().setNCicliLav((Long)info.get(CostantsColomb.NCICLI));
      getIoeeBean().setNPzTot((Long)info.get(CostantsColomb.NPZTOT));
      getIoeeBean().setTRun((Long)info.get(CostantsColomb.TRUNTIME));
      
      List<InfoFermoCdL> periodoNnProd=getListFermiTipo(CausaliLineeBean.TIPO_CAUS_ORENONPROD,getIoeeBean().getCdLav(), dataIni, dataFin);
      List fermiLogList=(List) info.get(CostantsColomb.LISTFERMI);
      
      Long tempoImprodLog=getTempoImprod(fermiLogList,periodoNnProd);

      Long microLog=(Long)info.get(CostantsColomb.TMICROFERMI);
      Long guastiLog=(Long)info.get(CostantsColomb.TGUASTI);
      
      microLog-=tempoImprodLog;
      guastiLog-=tempoImprodLog;
      
      
      getIoeeBean().setTSetup((Long)info.get(CostantsColomb.TSETUP));
      microLog-=getIoeeBean().getTSetup();
      
      
      _logger.info("Guasti da file di log :"+guastiLog);

//      if(guastiLog!=null && guastiLog>0)
//        ioee.addTGuasti(guastiLog);
      
      Long tMicrofermi=Long.valueOf(0);
      //se la somma dei fermi inseriti da file xls è minore di quella calcolata da file di log allora associo ifermi da logfile??
      if(getIoeeBean().getTGuasti()+getIoeeBean().getTPerditeGestionali()<microLog){
        microLog-=getIoeeBean().getTPerditeGestionali();
        microLog-=getIoeeBean().getTGuasti();
        
        tMicrofermi=microLog;
      }else{
        _logger.warn(" Attenzione la durata dei fermi inseriti da file xls è > della durata dei fermi da file di Log");
        getIoeeBean().addWarning(" Attenzione la durata dei fermi inseriti da file xls è > della durata dei fermi da file di Log");
      }
       
      getIoeeBean().setTMicrofermi(tMicrofermi);
      
      _logger.info("Tempo Microfermi iniziale:"+tMicrofermi);
      if(getIoeeBean().getTPerditeNnRilevate()<0 && tMicrofermi>0){
        tMicrofermi+=getIoeeBean().getTPerditeNnRilevate();
        _logger.info("Tempo Microfermi finale:"+tMicrofermi);  
        getIoeeBean().setTMicrofermi(tMicrofermi);
      }
      
    } catch (OEEException ex){
      getIoeeBean().addError(ex.getMessage());
    }
  }
  
  public Map calcolaTempiFromLogFile(Date dtInizio,Date dtFine) throws OEEException  {
     Map mapInfo=new HashMap();
     LogFileSQBStefani lf=null;
     String fileName="";
     try{

       
       Long tRuntime=Long.valueOf(0);
       Long tprod=Long.valueOf(0);

       fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.SQBSTEFANI,dtInizio);
       lf=new LogFileSQBStefani(fileName);
       _logger.info("Apertura file log: "+fileName+ " lettura da "+dtInizio +" a "+dtFine);
       Map mappa=lf.elabFile(dtInizio,dtFine);

       if(mappa==null || mappa.isEmpty()){
         _logger.warn("Nessun programma elaborato per il periodo indicato.Impossibile proseguire");
         return mapInfo;
       }
       
       mapInfo.put(CostantsColomb.NPZTOT,ClassMapper.classToClass(mappa.get(CostantsColomb.NPZTOT),Long.class));    
       mapInfo.put(CostantsColomb.NCICLI,ClassMapper.classToClass(mappa.get(CostantsColomb.NCICLI),Long.class));    
       mapInfo.put(CostantsColomb.TSETUP,ClassMapper.classToClass(mappa.get(CostantsColomb.TSETUP),Long.class)); 
       mapInfo.put(CostantsColomb.TGUASTI,ClassMapper.classToClass(mappa.get(CostantsColomb.TGUASTI),Long.class)); 
       
       mapInfo.put(CostantsColomb.LISTFERMI,mappa.get(CostantsColomb.LISTFERMI)); 
       
       tprod=ClassMapper.classToClass(mappa.get(CostantsColomb.TPRODUZ),Long.class); 
        
       Map programmi=(Map) mappa.get(LogFileSQBStefani.PROGRAMMIUTIL);
       _logger.info("Tempo produzione da file di log:"+tprod);
       
       Set keys=programmi.keySet();
       Iterator iter=keys.iterator();
       String program="";
       while(iter.hasNext()){
         ObjPrgSQBStefani obj=(ObjPrgSQBStefani) programmi.get(iter.next());
         String prgName=obj.getNome();
         String fPrgName=getPrgFileName(prgName);
         ProgFileSQBStefani prgFile=new ProgFileSQBStefani(fPrgName);
         try{
           prgFile.elabFile();
           obj.setVelocita(prgFile.getVelocita());
           obj.setCadenzamento(prgFile.getCadenzamento());
           Double runEff=obj.getRuntimeEff();
           tRuntime+=runEff.longValue();
           tprod-= runEff.longValue();
           //per ogni programma aggiungo 4 minuti di tempo di setup
         } catch (FileNotFoundException ex) {
           _logger.error("Impossibile accedere al file di programma "+prgName+ " --> " +ex.getMessage());
           _logger.warn(" Associazione cadenzamento e velocità di default : 800 mm e 30 m/min");
           obj.setVelocita(Double.valueOf(30));
           obj.setCadenzamento(Double.valueOf(800));
           Double runEff=obj.getRuntimeEff();
           tRuntime+=runEff.longValue();
        } catch (IOException ex) {
          _logger.error("Problemi di I/O con il file di programma "+prgName+ " --> " +ex.getMessage());
        } 
      }

      mapInfo.put(CostantsColomb.TRUNTIME,tRuntime);
      mapInfo.put(CostantsColomb.TMICROFERMI,tprod);
      _logger.info("Tempo runtime macchina :"+tRuntime+" --> tempo restante di produzione: "+tprod);
       
    } catch(FileNotFoundException f){
       _logger.error("Impossibile trovare il file di log :"+fileName+" -->"+f.getMessage());
        throw new OEEException("Impossibile trovare il file di log:"+fileName);
    } catch(ParseException p){
      _logger.error(" Problemi in fase di decodifica delle informazioni -->"+p.getMessage());
      throw new OEEException("Problemi in fase di decodifica delle informazioni "+fileName);
    } catch(IOException i){
      _logger.error("Impossibile accedere al file di log:"+fileName+" -->"+i.getMessage());
      throw new OEEException("Impossibile accedere al file di log:"+fileName);
    }

    return mapInfo;
  }
  
 
 
  private String getPrgFileName(String progr) throws ParseException{ 
    return LogFileSQBStefani.PATHFILEPROGRAM+progr+".prg";
  }

 
// 

  
  

  private Long getTempoImprod(List<List> fermiLogList, List<InfoFermoCdL> periodiNnProd) {
    Long durata=Long.valueOf(0);
    for(List logList: fermiLogList){
      Date dataIniLog=ClassMapper.classToClass(logList.get(0), Date.class);
      Date dataFinLog=ClassMapper.classToClass(logList.get(1), Date.class);
      
      for(InfoFermoCdL fermo:periodiNnProd){
        Date dataIniF=fermo.getOraInizio();
        Date dataFinF=fermo.getOraFine();
        durata +=getDurataPeriodiIntersect(dataIniLog,dataFinLog,dataIniF,dataFinF);
      }
    }
    
    
    return durata;
  }

  private Long getDurataPeriodiIntersect(Date dataIniLog, Date dataFinLog, Date dataIniXls, Date dataFinXls) {
    Long durata=Long.valueOf(0);
    
    if(DateUtils.afterEquals(dataFinLog, dataIniXls) && DateUtils.beforeEquals(dataIniLog, dataFinXls) ) {
       
        if( DateUtils.beforeEquals(dataIniLog, dataIniXls) && DateUtils.afterEquals(dataFinLog, dataFinXls)  ){ 
          // il fermo è all'interno del periodo di produzione
          durata+=DateUtils.numSecondiDiff(dataIniXls, dataFinXls);
        }else if((dataIniXls.before(dataIniLog) && dataFinXls.after(dataFinLog))){
          // il fermo è più grande del periodo di produzione
          durata+=DateUtils.numSecondiDiff(dataIniLog, dataFinLog);
        
        }else if(dataFinXls.after(dataIniLog) && DateUtils.beforeEquals(dataFinXls, dataFinLog)){
          durata+=DateUtils.numSecondiDiff(dataIniLog, dataFinXls);
          
        }else if(DateUtils.afterEquals(dataIniXls, dataIniLog) && dataIniXls.before(dataFinLog)){
          durata+=DateUtils.numSecondiDiff(dataIniXls, dataFinLog);
        }
   
      }
    
    
    return durata;
  }
  
  
//   @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsSQBStefani xls=(XlsSQBStefani) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermiM=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//      
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//
//    Date dataInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//    Date dataFine=xls.getFineTurno(data, rigaIni, rigaFin);
//
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea . ");
//      return ioee;
//    }
//
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data)*60);
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermiM, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    
//    try{
//      Map info=calcolaTempiFromLogFile(dataInizio, dataFine);
//      
//      if(info==null || info.isEmpty()){
//        ioee.addWarning("Nessuna informazione presente per la data indicata");
//        return ioee;
//      }
//      
//      ioee.setNCicliLav((Long)info.get(CostantsColomb.NCICLI));
//      ioee.setNPzTot((Long)info.get(CostantsColomb.NPZTOT));
//      ioee.setTRun((Long)info.get(CostantsColomb.TRUNTIME));
//      
//      List periodoNnProd=xls.getPeriodiImprodImpianto(data, rigaIni, rigaFin);
//      List fermiLogList=(List) info.get(CostantsColomb.LISTFERMI);
//      
//      Long tempoImprodLog=getTempoImprod(fermiLogList,periodoNnProd);
//
//      Long microLog=(Long)info.get(CostantsColomb.TMICROFERMI);
//      Long guastiLog=(Long)info.get(CostantsColomb.TGUASTI);
//      
//      microLog-=tempoImprodLog;
//      guastiLog-=tempoImprodLog;
//      
//      
//      ioee.setTSetup((Long)info.get(CostantsColomb.TSETUP));
//      microLog-=ioee.getTSetup();
//      
//      
//      _logger.info("Guasti da file di log :"+guastiLog);
//
////      if(guastiLog!=null && guastiLog>0)
////        ioee.addTGuasti(guastiLog);
//      
//      Long tMicrofermi=Long.valueOf(0);
//      //se la somma dei fermi inseriti da file xls è minore di quella calcolata da file di log allora associo ifermi da logfile??
//      if(ioee.getTGuasti()+ioee.getTPerditeGestionali()<microLog){
//        microLog-=ioee.getTPerditeGestionali();
//        microLog-=ioee.getTGuasti();
//        
//        tMicrofermi=microLog;
//      }else{
//        _logger.warn(" Attenzione la durata dei fermi inseriti da file xls è > della durata dei fermi da file di Log");
//        ioee.addWarning(" Attenzione la durata dei fermi inseriti da file xls è > della durata dei fermi da file di Log");
//      }
//       
//      ioee.setTMicrofermi(tMicrofermi);
//      
//      _logger.info("Tempo Microfermi iniziale:"+tMicrofermi);
//      if(ioee.getTPerditeNnRilevate()<0 && tMicrofermi>0){
//        tMicrofermi+=ioee.getTPerditeNnRilevate();
//        _logger.info("Tempo Microfermi finale:"+tMicrofermi);  
//        ioee.setTMicrofermi(tMicrofermi);
//      }
//      
//    } catch (OEEException ex){
//      ioee.addError(ex.getMessage());
//    }
//    
//       
//      
//    return ioee;
//  }
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSQBStefani.class);

  
}
