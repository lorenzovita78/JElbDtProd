/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.tab.R1.SfridiBandeImaAnteR1;
import colombini.query.produzione.R1.QuerySfridiBandeImaAnte;
import db.ResultSetHelper;
import elabObj.ElabClass;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabSfridiBandeImaAnteR1 extends ElabClass{
 
  
  
  private Date dataInizio;
  private Date dataFine;

  public ElabSfridiBandeImaAnteR1(Date dataInizio, Date dataFine) {
    this.dataInizio = dataInizio;
    this.dataFine = dataFine;
  }
  
  
  
  
  
  
  public void run(){
    Connection conAs400=null; 
    Connection conSQLServer=null;
    PersistenceManager pm=null;
    List<List> result=new ArrayList();
    try { 
      _logger.info(" ----- Inizio Elaborazione Sfridi Bande Ima Ante -----");
      conAs400=ColombiniConnections.getAs400ColomConnection();
      
      conSQLServer=ColombiniConnections.getDbImaAnteConnection();
      pm=new PersistenceManager(conAs400);
      QuerySfridiBandeImaAnte qry=new QuerySfridiBandeImaAnte();
      String oraini=DateUtils.DateToStr(DateUtils.getInizioGg(dataInizio), "yyyyMMdd HH:mm:ss");
      String orafin=DateUtils.DateToStr(DateUtils.getFineGg(dataFine), "yyyyMMdd HH:mm:ss");
      qry.setFilter(QuerySfridiBandeImaAnte.DATAORAINIZIO, oraini);
      qry.setFilter(QuerySfridiBandeImaAnte.DATAORAFINE, orafin);
      
      Integer anno=DateUtils.getYear(dataInizio);
      Integer sett=DateUtils.getWorkWeek(dataInizio);
      _logger.info(" Elaborazione per settimana "+sett +" anno "+anno);
      ResultSetHelper.fillListList(conSQLServer, qry.toSQLString(), result);
      
      
      if(result!=null && !result.isEmpty()){
        //aggiungo info aggiuntive
        for(List record:result){
          record.add(0,CostantsColomb.AZCOLOM);
          record.add(1,anno);
          record.add(2,sett);

          record.add(DateUtils.getDataSysLong());
          record.add(DateUtils.getOraSysLong());
        }

        Map keyvalues=new HashMap();
        keyvalues.put(SfridiBandeImaAnteR1.ZSCONO,CostantsColomb.AZCOLOM);
        keyvalues.put(SfridiBandeImaAnteR1.ZSANNO,anno);
        keyvalues.put(SfridiBandeImaAnteR1.ZSSETT,sett);

        SfridiBandeImaAnteR1 sbi=new SfridiBandeImaAnteR1();

        pm.deleteDt(sbi, keyvalues);
        pm.saveListDt(sbi, result);
    
      }
    } catch (QueryException eq) {
      _logger.error("Impossibile eseguire l'interrogazione al database : "+eq.getMessage());
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con il database : "+ex.getMessage());
    } catch (ParseException ep){
      _logger.error("Problemi di conversioni di date : "+ep.getMessage());
      
    } finally{
      
      _logger.info(" ----- Fine Elaborazione Sfridi Bande Ima Ante -----");
      if(conAs400!=null){
        try {
          conAs400.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura di connessione al db");
        } 
      }
      if(conSQLServer!=null){
        try {
          conSQLServer.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura di connessione al db");
        } 
      }
      if(pm!=null)
        pm=null;
      
    }     
  
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabSfridiBandeImaAnteR1.class);   

  @Override
  public Boolean configParams() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void exec(Connection con) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
}
