/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.conn.ColombiniConnections;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.R1.QryNPzForatricePriessOne;
import colombini.util.DatiCommUtils;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 *
 * @author lvita
 */
public class DtR1P1ForatricePriessOne implements IDatiCaricoLineaComm {

  public DtR1P1ForatricePriessOne(){
    
  }
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
   Connection conSql=null;
       
   try{
     conSql=ColombiniConnections.getDbDesmosColProdConnection();

  }
   catch (SQLException ex) {
       _logger.error("Impossibile rilasciare la connessione con SQL : "+ex.getMessage());
    }
   
   QryNPzForatricePriessOne qry=new QryNPzForatricePriessOne();
   
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    
    List beans=new ArrayList();
    beans=DatiCommUtils.getInstance().getListDtCommFromQuery(conSql, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
    //return DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
    
    return beans;
  }
    private static final Logger _logger = Logger.getLogger(DtR1P1ForatricePriessOne.class);

}
