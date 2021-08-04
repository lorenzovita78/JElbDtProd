/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.tab.R1.DtProdSSImaAnte;
import colombini.query.produzione.R1.QueryDtSortingStationImaAnte;
import db.ResultSetHelper;
import elabObj.ElabClass;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 * Dati relativi al riempimento della Sorting Station della Ima Ante.
 * Elaborazione da lanciare una volta all'ora
 * @author lvita
 */
public class ElabDtSortingSImaAnte extends ElabClass{

  @Override
  public Boolean configParams() {
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Connection conSQLServer=null;
    
    _logger.info(" -----  Dati Sorting Station Ima Ante -----");
    try { 
      conSQLServer=ColombiniConnections.getDbImaAnteConnection();
      QueryDtSortingStationImaAnte qry=new QueryDtSortingStationImaAnte();
      try{
        List<List> result=new ArrayList();

        String oraRif=DateUtils.getOraSysString();
        Long dtL=DateUtils.getDataSysLong();

        oraRif=oraRif.substring(0, 2);
        Long oraL=new Long(oraRif);
        
        ResultSetHelper.fillListList(conSQLServer, qry.toSQLString(), result);

        if (result.isEmpty() ){
          _logger.warn("Dati non presenti per data: "+dtL + " e ora :"+oraL);
          return ;
        }

        List keyvalues=new ArrayList();
        keyvalues.add(CostantsColomb.AZCOLOM);
        keyvalues.add(dtL);
        keyvalues.add(oraL);
          
        for(List record :result){
          //aggiungo i dati mancanti :azienda, data rif,ora rif
          record.add(0, CostantsColomb.AZCOLOM);
          record.add(1, dtL); 
          record.add(2, oraL);
          
          record.add(CostantsColomb.UTDEFAULT);
          record.add(DateUtils.getDataSysLong());
          record.add(DateUtils.getOraSysLong());
        }  
        //persistenza dei dati
//        _logger.info(result.toString());
        DtProdSSImaAnte dt=new DtProdSSImaAnte();
        dt.deleteDt(con, keyvalues);
        dt.saveDt(con, result);
        _logger.info("Salvataggio dati completato per gg:"+dtL);
        

      } catch (QueryException ex) {
       _logger.error("Problemi nell'interogazione del database "+ColomConnectionsCostant.DBIMAANTE +" -->" + ex.getMessage());
       addError("Errore in fase di interrogazione del database :"+ex.toString());
      }
      
      
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con "+ColomConnectionsCostant.SRVFORIMAANTE);
      addError("mpossibile stabilire la connessione con il database :"+ex.toString());
    } finally{
      _logger.info(" ----- Fine  Dati Ima Ante   -----");
      if(conSQLServer!=null)
        try {
          conSQLServer.close();
        } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db"+ColomConnectionsCostant.SRVFORIMAANTE);
        }
    }  
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabDtSortingSImaAnte.class); 
  
}
