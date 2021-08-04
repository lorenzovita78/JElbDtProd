/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.tab.R1.SfridiBandeImaR1;
import colombini.query.produzione.R1.QuerySfridiOtmImaR1;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
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
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabSfridiBandeImaR1 extends ElabClass{
 
  
  
  private Date dataInizio;
  private Date dataFine;

  public ElabSfridiBandeImaR1() {
    super();
  }
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Connection conSQLServer1=null,conSQLServer2=null;
    PersistenceManager pm=null;
    
    try { 
      _logger.info(" ----- Inizio Elaborazione Sfridi Bande Impianti Ima  -----");
      
      conSQLServer1=ColombiniConnections.getDbImaAnteConnection();
      conSQLServer2=ColombiniConnections.getDbImaTopConnection();
      
      pm=new PersistenceManager(con);
      QuerySfridiOtmImaR1 qry=new QuerySfridiOtmImaR1();
      String dtini=DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtfin=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");
      
      qry.setFilter(QuerySfridiOtmImaR1.GROUPBYBANDA, "yes");
      qry.setFilter(QuerySfridiOtmImaR1.DATAINIZIO, dtini);
      qry.setFilter(QuerySfridiOtmImaR1.DATAFINE, dtfin);
      
      String query=qry.toSQLString();
      
      _logger.info("Elaborazione dati Ima Ante");
      elabDatiSfridoXCdl(conSQLServer1, pm, query, "01008");
      _logger.info("Elaborazione dati Ima Top");
      elabDatiSfridoXCdl(conSQLServer2, pm, query, "01035");
     
      
    } catch (QueryException eq) {
      addError("Impossibile eseguire l'interrogazione al database : "+eq.toString());
    } catch (SQLException ex) {
      addError("Impossibile stabilire la connessione con il database : "+ex.toString());
    } catch (ParseException ep){
      addError("Problemi di conversioni di date : "+ep.getMessage());
    } finally{
      
      if(conSQLServer1!=null){
        try {
          conSQLServer1.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura di connessione al db Ima Ante");
        } 
      }
      if(conSQLServer2!=null){
        try {
          conSQLServer2.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura di connessione al db Ima Top");
        } 
      }
      if(pm!=null)
        pm=null;
      
    }
  }
  
  
  
  private void elabDatiSfridoXCdl(Connection conSql,PersistenceManager man,String qry,String cdL){
    List<List> result=new ArrayList();
    List<Long> dateL=new ArrayList();
    try {
      ResultSetHelper.fillListList(conSql, qry, result);
      if(!result.isEmpty()){
        //aggiungo info aggiuntive
        for(List record:result){
          
          Long dt=ClassMapper.classToClass(record.get(0), Long.class);
          if(!dateL.contains(dt))
            dateL.add(dt);
          
          record.add(0,CostantsColomb.AZCOLOM);
          record.add(2,cdL);
          record.remove(record.size()-1);
          record.add(new Date());
          
        }

        Map keyvalues=new HashMap();
        keyvalues.put(SfridiBandeImaR1.ZICONO,CostantsColomb.AZCOLOM);
        keyvalues.put(SfridiBandeImaR1.ZIPLGR,cdL);

        SfridiBandeImaR1 sbi=new SfridiBandeImaR1();
        
        for(Long dtL:dateL){
          keyvalues.put(SfridiBandeImaR1.ZIDTRF,dtL);
          man.deleteDt(sbi, keyvalues);
        
        }
        man.saveListDt(sbi, result);
      }
      
    } catch (SQLException ex) {
      addError("Impossibile stabilire la connessione con il database per linea: "+cdL+" -->"+ex.toString());
    }  
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabSfridiBandeImaR1.class);   

  
  
}
