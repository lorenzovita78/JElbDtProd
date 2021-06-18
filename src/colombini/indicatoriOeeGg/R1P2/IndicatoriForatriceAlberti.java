/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P2;

import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.CalcTempoCicloSmart;
import colombini.logFiles.R1P2.LogFileSupervisoreAlberti;
import colombini.util.InfoMapLineeUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
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

/**
 *
 * @author lvita
 */
public class IndicatoriForatriceAlberti extends AIndicatoriLineaForOee {


  public final static Double TCICLOMED=Double.valueOf(7.10);
  
  List componenti=new ArrayList();
  Long fermiLog;    

  public IndicatoriForatriceAlberti(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    componenti.add("SP");
    componenti.add("ST");
    
    fermiLog=Long.valueOf(0);
    
    try{
//      Map map=getTempiMacchina(con, data, centrodiLavoro, periodiNnProd);
      Map map=getTempiMacchina(con, dataIni,dataFin, getIoeeBean().getCdLav());
     
      getIoeeBean().setTRun((Long) map.get(CostantsColomb.TRUNTIME));
      getIoeeBean().setNPzTot((Long) map.get(CostantsColomb.NPZTOT));


      Long micro=(Long) map.get(CostantsColomb.TMICROFERMI);
      micro-=getIoeeBean().getTImprodImpianto();

      //prendo i fermi da file xls e li condiziono a quello che deriva da file di log
      if(getIoeeBean().getTGuasti()+getIoeeBean().getTPerditeGestionali()+getIoeeBean().getTSetup()<=micro){
        micro-=getIoeeBean().getTGuasti();
        micro-=getIoeeBean().getTPerditeGestionali();
        micro-=getIoeeBean().getTSetup();
      }else{
        Double tFermi=new Double(getIoeeBean().getTGuasti()+getIoeeBean().getTPerditeGestionali()+getIoeeBean().getTSetup());
        getIoeeBean().setTGuasti(  ClassMapper.classToClass((getIoeeBean().getTGuasti()/tFermi)*micro,Long.class) );
        getIoeeBean().setTPerditeGest(  ClassMapper.classToClass((getIoeeBean().getTPerditeGestionali()/tFermi)*micro,Long.class) );
        getIoeeBean().setTSetup(  ClassMapper.classToClass((getIoeeBean().getTSetup()/tFermi)*micro,Long.class) );
        micro=Long.valueOf(0); 
        getIoeeBean().addWarning("Attenzione fermi da file xls superiori  a quelli da file di log --> Riproporzionamento dei fermi in base ai fermi da file log");
        _logger.warn("Attenzione fermi da file xls superiori  a quelli da file di log --> Riproporzionamento dei fermi in base ai fermi da file log");
      }
      
      
      
      getIoeeBean().setTMicrofermi(micro);
      
      Long tNnRil=getIoeeBean().getTPerditeNnRilevate();
      if(tNnRil<0){
        _logger.warn(" Attenzione somma dei tempi superiore al tempo disponibile "+tNnRil);
        micro+=tNnRil;
        getIoeeBean().setTMicrofermi(micro);
        
        if(getIoeeBean().getTMicroFermi()<0){
          getIoeeBean().addWarning("Attenzione calcolo con errori --> Microfermi < 0");
          getIoeeBean().setTMicrofermi(Long.valueOf(0));
        }
      }
      
      
      
     } catch (SQLException ex) {
      _logger.error("Problemi di accesso al database --> "+ex.getMessage());
      getIoeeBean().addError("Errore in fase di interrogazione del database per estrarre i tempi ciclo");
    } catch (FileNotFoundException ex) {
      _logger.error("Impossibile accedere al file log : "+InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(getIoeeBean().getCdLav()),getIoeeBean().getData())
              + " --> "+ex.getMessage());
      getIoeeBean().addError("Impossibile accedere al file di log ");
    } catch (IOException ex) {
      _logger.error("Problemi di lettura del file di log : "+InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(getIoeeBean().getCdLav()),getIoeeBean().getData())
              + " --> "+ex.getMessage());
      getIoeeBean().addError("Impossibile accedere al file di log ");
    } catch (ParseException ex) {
      _logger.info(" Problemi nella conversione delle date-->"+ex.getMessage());
      getIoeeBean().addError("Problemi in fase di calcolo Oee ");
    }  
  }
  
  
 
  
  private Map  getTempiMacchina(Connection con,Date dataInizio,Date dataFine,String cdLavoro) throws ParseException, FileNotFoundException, IOException, SQLException {
    
    
    String fileName="";
    Map map =new HashMap();
    
    fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro),dataInizio);
    LogFileSupervisoreAlberti lt=new LogFileSupervisoreAlberti(fileName);
    List<List> lista=lt.elabFile(dataInizio, dataFine);
    Long tRuntime=loadTempoRunMachine(con, lista,cdLavoro);
    //visto che i microfermi a questo punto del calcolo sono la differenza del runtime dal tempo di disponibilità
    //Long tMicrofermi=(fermiLog- tImprodImp); //MODIFICA del 08/03/2017
    Long tMicrofermi=fermiLog;

    Long nPzTot=Long.valueOf(lista.size());
    _logger.info("Periodo:+"+lt.getDataInizio()+" - "+lt.getDataFine()+ "--->   Runtime: "+tRuntime+" -- Microfermi: "+tMicrofermi);


    map.put(CostantsColomb.TRUNTIME, tRuntime);
    map.put(CostantsColomb.TMICROFERMI, tMicrofermi);
    map.put(CostantsColomb.NPZTOT, nPzTot);

    //-------------pezzi particolari
//      fileName=PATHFILEALBERTI+"Log del "+DateUtils.getDataForMovex(data)+".Err4";
//      lt=new LogFileSupervisoreAlberti(fileName);
//      List<List> lista2=lt.elabFile(dataInizio, dataFine);
//      nPzTurni=Long.valueOf(lista2.size());
//      Double tempoRunAgg=nPzTurni*TCICLOMED;
//      _logger.info("Runtime da pezzi del log .err -->"+tempoRunAgg);
//      tMicrofermi-=tempoRunAgg.longValue();
//      tRuntime+=tempoRunAgg.longValue();
    //-------------





  return map;
  } 
  
  
  
   private Long loadTempoRunMachine(Connection con,List<List> listaColli,String cdLavoro) throws SQLException{
     Long tRuntime=Long.valueOf(0);
     CalcTempoCicloSmart ctc=new CalcTempoCicloSmart(cdLavoro,null,componenti);
      for(List record:listaColli){
        Integer comm=ClassMapper.classToClass(record.get(0), Integer.class);
        Long  collo=ClassMapper.classToClass(record.get(1), Long.class);
        Double tcM=ctc.getTempoCicloCollo(con,comm, collo);
        Long tcLog=ClassMapper.classToClass(record.get(2),Long.class);
        Integer occ=ClassMapper.classToClass(record.get(3),Integer.class);
        
        if(tcM.intValue()==0){
          tcM=TCICLOMED*occ;
        }
        if(tcLog.intValue()==0){
          Double tcLogD=(TCICLOMED*occ);
          tcLog=tcLogD.longValue();
        }  
          
        Long tcML=tcM.longValue();
        if(tcLog>tcML){
          fermiLog+=tcLog-tcML;
          tRuntime+=tcML;
        }else{
          tRuntime+=tcLog;
        }
      }
      
      
      return tRuntime;
   }
  
  
   
  
   

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter)  {
//    XlsForatriceAlberti xls=(XlsForatriceAlberti) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    fermiLog=Long.valueOf(0);
//    
//    componenti.add("SP");
//    componenti.add("ST");
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//    Date dtInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//    Date dtFine=xls.getFineTurno(data, rigaIni, rigaFin);
//    
//    
//    if(dtInizio==null || dtFine==null){
//      ioee.addError(" Turni di lavoro  non specificati .");
//      return ioee;
//    }
//    
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTSetup(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    
//    List periodiNnProd=xls.getPeriodiImprodImpianto(data, rigaIni, rigaFin);
//    
//    try{
////      Map map=getTempiMacchina(con, data, centrodiLavoro, periodiNnProd);
//      Map map=getTempiMacchina(con, dtInizio,dtFine, centrodiLavoro, periodiNnProd);
//      ioee.setTDispImp((Long)map.get(CostantsColomb.TDISPON));
//      ioee.setTImprodImp((Long)map.get(CostantsColomb.TIMPROD));
//
//      ioee.setTRun((Long) map.get(CostantsColomb.TRUNTIME));
//      ioee.setNPzTot((Long) map.get(CostantsColomb.NPZTOT));
//
//
//      Long micro=(Long) map.get(CostantsColomb.TMICROFERMI);
//
//      //prendo i fermi da file xls e li condiziono a quello che deriva da file di log
//      if(ioee.getTGuasti()+ioee.getTPerditeGestionali()+ioee.getTSetup()<=micro){
//        micro-=ioee.getTGuasti();
//        micro-=ioee.getTPerditeGestionali();
//        micro-=ioee.getTSetup();
//      }else{
//        Double tFermi=new Double(ioee.getTGuasti()+ioee.getTPerditeGestionali()+ioee.getTSetup());
//        ioee.setTGuasti(  ClassMapper.classToClass((ioee.getTGuasti()/tFermi)*micro,Long.class) );
//        ioee.setTPerditeGest(  ClassMapper.classToClass((ioee.getTPerditeGestionali()/tFermi)*micro,Long.class) );
//        ioee.setTSetup(  ClassMapper.classToClass((ioee.getTSetup()/tFermi)*micro,Long.class) );
//        micro=Long.valueOf(0); 
//        ioee.addWarning("Attenzione fermi da file xls superiori  a quelli da file di log --> Riproporzionamento dei fermi in base ai fermi da file log");
//        _logger.warn("Attenzione fermi da file xls superiori  a quelli da file di log --> Riproporzionamento dei fermi in base ai fermi da file log");
//      }
//      
//      
//      
//      ioee.setTMicrofermi(micro);
//      
//      Long tNnRil=ioee.getTPerditeNnRilevate();
//      if(tNnRil<0){
//        _logger.warn(" Attenzione somma dei tempi superiore al tempo disponibile "+tNnRil);
//        micro+=tNnRil;
//        ioee.setTMicrofermi(micro);
//        
//        if(ioee.getTMicroFermi()<0){
//          ioee.addWarning("Attenzione calcolo con errori --> Microfermi < 0");
//          ioee.setTMicrofermi(Long.valueOf(0));
//        }
//      }
//      
//      
//      
//     } catch (SQLException ex) {
//      _logger.error("Problemi di accesso al database --> "+ex.getMessage());
//      ioee.addError("Errore in fase di interrogazione del database per estrarre i tempi ciclo");
//    } catch (FileNotFoundException ex) {
//      _logger.error("Impossibile accedere al file log : "+InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(centrodiLavoro),data)
//              + " --> "+ex.getMessage());
//      ioee.addError("Impossibile accedere al file di log ");
//    } catch (IOException ex) {
//      _logger.error("Problemi di lettura del file di log : "+InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(centrodiLavoro),data)
//              + " --> "+ex.getMessage());
//      ioee.addError("Impossibile accedere al file di log ");
//    } catch (ParseException ex) {
//      _logger.info(" Problemi nella conversione delle date-->"+ex.getMessage());
//      ioee.addError("Problemi in fase di calcolo Oee ");
//    }  
//    
//    return ioee;
//  }
//  
//   private Map  getTempiMacchina(Connection con,Date data,String cdLavoro,List<List> periodiNnProd) throws ParseException, FileNotFoundException, IOException, SQLException {
//    
//    Date dataInizio;
//    Date dataFine;
//    String fileName="";
//    Map map =new HashMap();
//    
//   
//    dataInizio=DateUtils.getInizioGg(data);
//    dataFine = DateUtils.getFineGg(data);
//
//    fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro),data);
//
//    LogFileSupervisoreAlberti lt=new LogFileSupervisoreAlberti(fileName);
//    List<List> lista=lt.elabFile();
//    Long tDispImp=DateUtils.numSecondiDiff(lt.getDataInizio(),lt.getDataFine());
//    Long tImprodImp=DateUtils.getTempoIntersect(lt.getDataInizio(), lt.getDataFine(), periodiNnProd);
//    Long tRuntime=loadTempoRunMachine(con, lista,cdLavoro);
//
//
//    //visto che i microfermi a questo punto del calcolo sono la differenza del runtime dal tempo di disponibilità
//    Long tMicrofermi=(fermiLog- tImprodImp);
//
//    Long nPzTot=Long.valueOf(lista.size());
//    _logger.info("Periodo:+"+lt.getDataInizio()+" - "+lt.getDataFine()+ "--->   Runtime: "+tRuntime+" -- Microfermi: "+tMicrofermi);
//
//    map.put(CostantsColomb.TDISPON, tDispImp);
//    map.put(CostantsColomb.TIMPROD, tImprodImp);
//    map.put(CostantsColomb.TRUNTIME, tRuntime);
//    map.put(CostantsColomb.TMICROFERMI, tMicrofermi);
//    map.put(CostantsColomb.NPZTOT, nPzTot);
//
//    //-------------pezzi particolari
////      fileName=PATHFILEALBERTI+"Log del "+DateUtils.getDataForMovex(data)+".Err4";
////      lt=new LogFileSupervisoreAlberti(fileName);
////      List<List> lista2=lt.elabFile(dataInizio, dataFine);
////      nPzTurni=Long.valueOf(lista2.size());
////      Double tempoRunAgg=nPzTurni*TCICLOMED;
////      _logger.info("Runtime da pezzi del log .err -->"+tempoRunAgg);
////      tMicrofermi-=tempoRunAgg.longValue();
////      tRuntime+=tempoRunAgg.longValue();
//    //-------------
//
//
//
//
//
//  return map;
//  }
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriForatriceAlberti.class);

  
}
