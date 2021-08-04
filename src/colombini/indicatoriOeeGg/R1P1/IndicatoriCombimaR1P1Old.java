/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.utils.OeeUtils;
import colombini.logFiles.R1P1.LogFileCombima;
import colombini.model.CausaliLineeBean;
import colombini.model.persistence.IndicatoriOeeGgBean;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.indicatoriOee.R1P1.XlsImaAnte;
import db.ResultSetHelper;
import exception.QueryException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import colombini.query.indicatoriOee.linee.QueryImaAntePzScartati;
import colombini.query.indicatoriOee.QueryScartiMovex;
import utils.ClassMapper;
import utils.DateUtils;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriCombimaR1P1Old implements ICalcIndicatoriOeeLinea {
  
  protected Map mappaFermi=null;
  protected Map mappaInfo=null;

  
  private Long getScartiProcesso(Connection con,Date data) throws OEEException{
    return getScartoCollaudo(data)+getScartoMP1(con, data);
  }
  
  
  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine,String cdLavoro) throws OEEException{
    LogFileCombima lg=null;
    String fileName = "";
    
        
    try {
      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro), dataInizio);
      lg=new LogFileCombima(fileName);
      _logger.info("Apertura file log: "+fileName);
      lg.initialize();
      
      mappaInfo=lg.processLogFile(dataInizio,dataFine);
    
    } catch (IOException ex) {
       _logger.error("Errore di accesso al file di Log:"+ex.getMessage());
       throw new OEEException("Impossibile accedere al file di Log "+fileName);
    } catch (ParseException ex) {
       _logger.error("Errore di conversione :"+ex.getMessage());
        throw new OEEException("Problemi di accesso ai dati del file di log"+fileName);
    } finally {
      if(lg!=null)
        try {
          // chiudiamo lo streaming del file
          lg.terminate();
//        } catch (FileNotFoundException ex) {
//         _logger.error("Errore di accesso al file di log:"+ex.getMessage());
//         throw new OEEException("");
        } catch (IOException ex) {
         _logger.error("Errore in fase di chiusura al file di log:"+ex.getMessage());
         
        }
    }
    return mappaInfo;
  }
  
  
  private Long getScartoMP1(Connection con,Date data) throws OEEException{
    Long scartoMP1=Long.valueOf(0);
    String dtS="";
    
    if(data==null)
      return null;
    
    try {
      dtS=DateUtils.DateToStr(data, "yyyyMMdd");
    
      QueryScartiMovex qry=new QueryScartiMovex();
      qry.setFilter(QueryScartiMovex.DATAINIZIO, ClassMapper.classToClass(dtS, Long.class));
      qry.setFilter(QueryScartiMovex.DATAFINE, ClassMapper.classToClass(dtS, Long.class));
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>0)
        scartoMP1=ClassMapper.classToClass(obj[0], Long.class);
      
    } catch (ParseException ex) {
      _logger.error("Errore di conversione della data:"+data);
      throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    } catch (QueryException ex) {
        _logger.error("Errore nel reperimento dei dati sugli scarti da Movex per il giorno:"+dtS);
       throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    } catch (SQLException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre per il giorno:"+dtS);
       throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    }
    
    return scartoMP1;
  }
  
  
  private Long getScartoCollaudo(Date data) throws OEEException{
    Long scartoCollaudo=Long.valueOf(0);
    Connection con=null;
    try {
      con=ColombiniConnections.getDbImaAnteConnection();
      String dataS=DateUtils.DateToStr(data, "yyyyMMdd");
      String dtini=dataS+" 00:00:01";
      String dtfin=dataS+" 23:59:59";

      QueryImaAntePzScartati qry=new QueryImaAntePzScartati();
      
      qry.setFilter(QueryImaAntePzScartati.DATAINIZIO, dtini);
      qry.setFilter(QueryImaAntePzScartati.DATAFINE, dtfin);
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>0)
        scartoCollaudo=ClassMapper.classToClass(obj[0], Long.class);
      
      
    } catch (QueryException ex) {
      _logger.error("Impossibile reperire dati :Problemi di conversione dei dati "+ex.getMessage());
      throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    } catch (SQLException ex) {
       _logger.error("Impossibile interrogare il server "+ColomConnectionsCostant.SRVFORIMAANTE+" :"+ex.getMessage());
       throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    } catch (ParseException ex) {
      _logger.error("Impossibile reperire dati :Problemi di conversione di data");
      throw new OEEException(" Impossibile calcolare gli scarti relativi al controllo qualità ");
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error("Impossibile rilasciare la connessione con il server:"+ColomConnectionsCostant.SRVFORIMAANTE);
      }
    }
    
    
    return scartoCollaudo;
  }
  


  @Override
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter)  {
   
    XlsImaAnte xls=(XlsImaAnte) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
    //istanziamo l'oggetto per l'indicatore
    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
    
    //tempo dei pezzi di scarto ghe girano sulla linea--->VELOCITA' RIDOTTA
    Long tPassaggioScarti=new Long(0);
    //tempo passaggio inutile sulle combima  (pezzo che al primo giro non subisce nessuna lavorazione)---> TEMPO LORDO
    Long tPassInutile=new Long(0);
    
    Date dataInizio=xls.getInizioTurnoStdGg(data);
    Date dataFine=xls.getFineTurnoStdGg(data);
 
    if(dataInizio==null || dataFine==null){
      ioee.addError(" Turni di lavoro non specificati .");
      return ioee;
    }
    
    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data, centrodiLavoro)*60);

    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
    ioee.setTSetup(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
    
    
    //caricamento info da file di log
    try{
      Map map=loadDatiFromLogFile(dataInizio,dataFine,centrodiLavoro);

      Long runtime=(Long) mappaInfo.get(LogFileCombima.TEMPORUN);
      tPassaggioScarti=(Long) mappaInfo.get(LogFileCombima.TEMPOSCARTO);
      tPassInutile=(Long) mappaInfo.get(LogFileCombima.TEMPOPASSINUTILE);
      Long microfermi= (Long) mappaInfo.get(LogFileCombima.MICROFERMI);

      ioee.setNPzTot((Long)mappaInfo.get(LogFileCombima.TOTPEZZI));
      ioee.setNPzTurni((Long) mappaInfo.get(LogFileCombima.TOTPZTURNO));
      ioee.setNScarti((Long) mappaInfo.get(LogFileCombima.TOTSCARTI));


      //somma dei pezzi scartati dal controllo qualità e dal magazzino interno
      Long pzScartiProc=getScartiProcesso(con, data);
      Double tempoCicloMedio=OeeUtils.getInstance().arrotonda(new Double(tPassaggioScarti+tPassInutile+runtime)/ioee.getNumPzTurni(),2);
      Double tScartoCq=new Double(new Double(tempoCicloMedio*pzScartiProc)/2);
      //tempo perso per i pezzi scartati dal controllo qualità e dai nostri "clienti" interni-->TEMPO SCARTO
      ioee.setTScarti(tScartoCq.longValue());
      // tempo dei pezzi di scarto (prodotti dalla sezionatrice ) che girano sulla linea 
      ioee.setTScarti2(tPassaggioScarti);
      //runtime=dato dal tempo di funzionamento meno (-) il tempo relativo ai pezzi prodotti che però sono stati scartati dal CQ
      ioee.setTRun(runtime-tScartoCq.longValue());
      //tempo dei pezzi che girano almeno due volte sulla macchina ma al primo passaggio non subiscono una lavorazione
      ioee.setTRun2(tPassInutile);

      ioee.setTMicrofermi(microfermi);
      
      _logger.info("Tempo Microfermi iniziale:"+microfermi);
      Long tmpNNRil=ioee.getTPerditeNnRilevate();
      if(tmpNNRil<0){
        microfermi+=tmpNNRil;
        _logger.info("Tempo Microfermi finale:"+microfermi);
        if(microfermi<0){
          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
          microfermi=Long.valueOf(0);
        }
      }

      ioee.setTMicrofermi(microfermi);
    } catch (OEEException ex){
      ioee.addError(ex.getMessage());
    }
    
    return ioee;
  }
  
  

  //tempo Scarti   : somma dei pezzi scartati dal controllo qualità e dal magazzino interno * tempoCicloMedio
  //tempo Scarti 2 : torna il tempo di passaggio dei pezzi di scarto (prodotti dalle sezionatrici) che girano a vuoto sulla linea
  //Runtime        : tempo di funzionamento meno (-) il tempo relativo ai pezzi prodotti che però sono stati scartati dal CQ
  //Runtime  2     : tempo dei pz che girano almeno due volte sulla macchina ma al primo passaggio non subiscono una lavorazione
 
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriCombimaR1P1Old.class);
}
