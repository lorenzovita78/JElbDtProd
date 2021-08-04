/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.OeeUtils;
import colombini.logFiles.R1P1.LogFileCombima;
import colombini.util.InfoMapLineeUtil;
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
import java.util.HashMap;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriCombimaR1P1 extends AIndicatoriLineaForOee {
  
  protected Map mappaFermi=null;

  public IndicatoriCombimaR1P1(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }

  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    //tempo dei pezzi di scarto ghe girano sulla linea--->VELOCITA' RIDOTTA
    Long tPassaggioScarti=new Long(0);
    //tempo passaggio inutile sulle combima  (pezzo che al primo giro non subisce nessuna lavorazione)---> TEMPO LORDO
    Long tPassInutile=new Long(0);
    try{
      Map map=loadDatiFromLogFile(dataIni,dataFin,getInfoTCdl().getCdl());

      Long runtime=(Long) map.get(LogFileCombima.TEMPORUN);
      tPassaggioScarti=(Long) map.get(LogFileCombima.TEMPOSCARTO);
      tPassInutile=(Long) map.get(LogFileCombima.TEMPOPASSINUTILE);
      Long microfermi= (Long) map.get(LogFileCombima.MICROFERMI);

      getIoeeBean().setNPzTot((Long)map.get(LogFileCombima.TOTPEZZI));
      getIoeeBean().setNPzTurni((Long) map.get(LogFileCombima.TOTPZTURNO));
      getIoeeBean().setNScarti((Long) map.get(LogFileCombima.TOTSCARTI));


      //somma dei pezzi scartati dal controllo qualità e dal magazzino interno
      Long pzScartiProc=getScartiProcesso(con, getInfoTCdl().getDataRif());
      Double tempoCicloMedio=OeeUtils.getInstance().arrotonda(new Double(tPassaggioScarti+tPassInutile+runtime)/getIoeeBean().getNumPzTurni(),2);
      Double tScartoCq=new Double(new Double(tempoCicloMedio*pzScartiProc)/2);
      //tempo perso per i pezzi scartati dal controllo qualità e dai nostri "clienti" interni-->TEMPO SCARTO
      getIoeeBean().setTScarti(tScartoCq.longValue());
      // tempo dei pezzi di scarto (prodotti dalla sezionatrice ) che girano sulla linea 
      getIoeeBean().setTScarti2(tPassaggioScarti);
      //runtime=dato dal tempo di funzionamento meno (-) il tempo relativo ai pezzi prodotti che però sono stati scartati dal CQ
      getIoeeBean().setTRun(runtime-tScartoCq.longValue());
      //tempo dei pezzi che girano almeno due volte sulla macchina ma al primo passaggio non subiscono una lavorazione
      getIoeeBean().setTRun2(tPassInutile);

      getIoeeBean().setTMicrofermi(microfermi);
      
      _logger.info("Tempo Microfermi iniziale:"+microfermi);
      Long tmpNNRil=getIoeeBean().getTPerditeNnRilevate();
      if(tmpNNRil<0){
        microfermi+=tmpNNRil;
        _logger.info("Tempo Microfermi finale:"+microfermi);
        if(microfermi<0){
          _logger.warn("Attenzione MICROFERMI<0 -> settiamo a 0");
          microfermi=Long.valueOf(0);
        }
      }

      getIoeeBean().setTMicrofermi(microfermi);
    } catch (OEEException ex){
      getIoeeBean().addError(ex.getMessage());
    }
  }
  
  
  private Long getScartiProcesso(Connection con,Date data) throws OEEException{
    return getScartoCollaudo(data)+getScartoMP1(con, data);
  }
  
  
  private Map loadDatiFromLogFile(Date dataInizio,Date dataFine,String cdLavoro) throws OEEException{
    LogFileCombima lg=null;
    String fileName = "";
    Map mappaInfo=new HashMap();
    
        
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
          lg.terminate();

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
  


  
  

  //tempo Scarti   : somma dei pezzi scartati dal controllo qualità e dal magazzino interno * tempoCicloMedio
  //tempo Scarti 2 : torna il tempo di passaggio dei pezzi di scarto (prodotti dalle sezionatrici) che girano a vuoto sulla linea
  //Runtime        : tempo di funzionamento meno (-) il tempo relativo ai pezzi prodotti che però sono stati scartati dal CQ
  //Runtime  2     : tempo dei pz che girano almeno due volte sulla macchina ma al primo passaggio non subiscono una lavorazione
 
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriCombimaR1P1.class);

  
}
