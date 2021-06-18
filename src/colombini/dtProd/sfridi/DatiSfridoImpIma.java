/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.costant.ColomConnectionsCostant;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.R1.QuerySfridoImpImaBandeMq;
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
public abstract class DatiSfridoImpIma extends InfoSfridoCdL{

  public DatiSfridoImpIma(String cdL) {
    super(cdL);
  }
  
  
  
   @Override
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException {
    if(dataInizio==null || dataFine==null){
      _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
      return null;
    }
    
    
    return loadDati(dataInizio,dataFine);
  }
  
  
  

  public abstract Connection getDbConnection() throws SQLException;
  
  

  protected List loadDati(Date dataInizio, Date dataFine) throws ElabException {
   List list=new ArrayList();
    Connection con=null;
    try {
      String dtIS = DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");

      con=getDbConnection();
      
      QuerySfridoImpImaBandeMq qry=new QuerySfridoImpImaBandeMq();
      
      qry.setFilter(FilterQueryProdCostant.FTDATAINI, dtIS);
      qry.setFilter(FilterQueryProdCostant.FTDATAFIN, dtFS);
      qry.setFilter(FilterQueryProdCostant.FTCDL, getCdL());
              
      ResultSetHelper.fillListList(con, qry.toSQLString(), list);
    
    } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (SQLException ex) {
      _logger.error(" Problemi di connessione con db  --> "+ex.getMessage());
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
  
  
  
  
   private static final Logger _logger = Logger.getLogger(DatiSfridoImaTop.class); 
}
