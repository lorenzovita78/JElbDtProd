/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi.old;

import colombini.dtProd.sfridi.*;
import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.query.produzione.R1.QuerySfridiOtmImaR1;
import db.ResultSetHelper;
import exception.ElabException;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DatiSfridoImaTopOld implements ISfridoInfo {
  
  
  
  
  @Override
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException {
    if(dataInizio==null || dataFine==null){
      _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
      return null;
    }
    
    return loadDatiFromDb(dataInizio,dataFine);
  }
  
  
  private List loadDatiFromDb(Date dataInizio,Date dataFine) throws ElabException{
    List list=new ArrayList();
    Connection con=null;
    try {
      String dtIS = DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");

      con=ColombiniConnections.getDbImaTopConnection();
      
      QuerySfridiOtmImaR1 qry=new QuerySfridiOtmImaR1();
      qry.setFilter(QuerySfridiOtmImaR1.DATAINIZIO, dtIS);
      qry.setFilter(QuerySfridiOtmImaR1.DATAFINE, dtFS);
             
      ResultSetHelper.fillListList(con, qry.toSQLString(), list);
    
    } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (SQLException ex) {
      _logger.error(" Problemi di connessione con db :"+ColomConnectionsCostant.DBIMATOP+" --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date --> "+ex.getMessage());
      throw new ElabException(ex);
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi con la chiusura della connessione con db :"+ColomConnectionsCostant.DBIMATOP+" --> "+ex.getMessage());
      }
    }
       
    return list;
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoImaTopOld.class); 
}
