/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P2;

import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.CalcoloTempoCiclo;
import colombini.logFiles.R1P2.LogFileTavoli;
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
public class IndicatoriLineaTavoli extends AIndicatoriLineaForOee {

  public final static Double TCICLOMED=Double.valueOf(27.11);

  public IndicatoriLineaTavoli(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      Map map=elabTempiMacchina(con, dataIni, dataFin, getIoeeBean().getCdLav() );
      
      getIoeeBean().setTRun((Long) map.get(CostantsColomb.TRUNTIME));
      getIoeeBean().setNPzTot((Long) map.get(CostantsColomb.NPZTOT));


      Long micro=(Long) map.get(CostantsColomb.TMICROFERMI);
      micro=micro-getIoeeBean().getTImprodImpianto();
      //prendo i fermi da file xls e li condiziono a quello che deriva da file di log
      if(getIoeeBean().getTGuasti()+getIoeeBean().getTPerditeGestionali()+getIoeeBean().getTSetup()<=micro){
        micro-=getIoeeBean().getTGuasti();
        micro-=getIoeeBean().getTPerditeGestionali();
        micro-=getIoeeBean().getTSetup();
      }else{
        micro=Long.valueOf(0); 
        getIoeeBean().addWarning("Attenzione fermi da file xls superiori  a quelli da file di log ");
      }
      getIoeeBean().setTMicrofermi(micro);
      
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
  
  
  private Map elabTempiMacchina(Connection con,Date dataInizio,Date dataFine,String cdLavoro) throws ParseException, FileNotFoundException, IOException, SQLException {
    
   
    String fileName="";
    Map map=new HashMap();


    fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.TAVOLISCRIV,dataInizio);

    LogFileTavoli lt=new LogFileTavoli(fileName);
    List<List> lista=lt.elabFile(dataInizio, dataFine);

    Long nPzTot=Long.valueOf(lista.size());
    
    
    List lst=getTempiMacchinaFromMvx(con, lista,cdLavoro);

    
    //dai fermi del log escludo le ore nn produttive
    

    //prendo i fermi da file xls e li condiziono a quello che deriva da file di log
    
    map.put(CostantsColomb.TRUNTIME, ClassMapper.classToClass(lst.get(0), Long.class));
    map.put(CostantsColomb.TMICROFERMI, ClassMapper.classToClass(lst.get(1), Long.class));
    map.put(CostantsColomb.NPZTOT, nPzTot);
      
      
    return map;
  }
  
  
  
  private List getTempiMacchinaFromMvx(Connection con,List<List> listColli,String cdLavoro) throws SQLException{
    CalcoloTempoCiclo ctc=new CalcoloTempoCiclo(cdLavoro);
    int i=0;
    Long tRuntime=Long.valueOf(0);
    Long fermiLog=Long.valueOf(0);
    List lista=new ArrayList();
    for(List record:listColli){
      
      ArrayList colli=new ArrayList();
      colli.add(record);
      Double tcM=ctc.getTempoCiclo(con,colli, null);
      if(tcM.intValue()==0){
        tcM=TCICLOMED;
      }
      Long tcLog=ClassMapper.classToClass(record.get(2),Long.class);
      Long tcML=tcM.longValue();
      if(tcLog>tcML){
        fermiLog+=tcLog-tcML;
        tRuntime+=tcML;
      }else{
        tRuntime+=tcLog;
      }
      i++; 
    }
    
    lista.add(tRuntime);
    lista.add(fermiLog);
    
    return lista;
  }
  
  
  

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsTavoliScriv xls=(XlsTavoliScriv) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    
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
//    List periodiNnProd=xls.getPeriodiImprodImpianto(data, rigaIni, rigaFin);
//    
//    
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTSetup(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    try{
//      Map map=elabTempiMacchina(con, data, periodiNnProd, centrodiLavoro );
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
//        micro=Long.valueOf(0); 
//        ioee.addWarning("Attenzione fermi da file xls superiori  a quelli da file di log ");
//      }
//      ioee.setTMicrofermi(micro);
//      
//    } catch (SQLException ex) {
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
//    
//    return ioee;
//  }
//  
//  private Map elabTempiMacchina(Connection con,Date data,List periodiNnProd,String cdLavoro) throws ParseException, FileNotFoundException, IOException, SQLException {
//    
//    Date dataInizio;
//    Date dataFine;
//    String fileName="";
//    Map map=new HashMap();
//    
//    dataInizio=DateUtils.getInizioGg(data);
//    dataFine = DateUtils.getFineGg(data);
//
//    fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.TAVOLISCRIV,data);
//
//    LogFileTavoli lt=new LogFileTavoli(fileName);
//    List<List> lista=lt.elabFile(dataInizio, dataFine);
//
//    Long nPzTot=Long.valueOf(lista.size());
//    
//    Long tDispImp=DateUtils.numSecondiDiff(lt.getDataInizio(),lt.getDataFine());
//    Long tImprodImp=DateUtils.getTempoIntersect(lt.getDataInizio(),lt.getDataFine(),periodiNnProd);
//    List lst=getTempiMacchinaFromMvx(con, lista,cdLavoro);
//
//    
//    //dai fermi del log escludo le ore nn produttive
//    Long tMicrofermi=ClassMapper.classToClass(lst.get(1), Long.class) -tImprodImp;
//
//    //prendo i fermi da file xls e li condiziono a quello che deriva da file di log
//    map.put(CostantsColomb.TDISPON, tDispImp);
//    map.put(CostantsColomb.TIMPROD, tImprodImp);
//    map.put(CostantsColomb.TRUNTIME, lst.get(0));
//    map.put(CostantsColomb.TMICROFERMI, tMicrofermi);
//    map.put(CostantsColomb.NPZTOT, nPzTot);
//      
//      
//    return map;
//  }
  
  private static final Logger _logger = Logger.getLogger(IndicatoriLineaTavoli.class);

  
  
}
