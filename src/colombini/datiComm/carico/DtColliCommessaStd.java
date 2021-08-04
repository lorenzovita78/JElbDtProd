/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico;


import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryColliVolumiCommessa;
import colombini.util.DatiCommUtils;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * Classe per caricare le informazioni relative ai colli prodotti da una determinata linea
 * @author lvita
 */
public class DtColliCommessaStd implements IDatiCaricoLineaComm{
  
  public DtColliCommessaStd(){

  }


  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con,LineaLavBean ll) throws DatiCommLineeException {
    List<CaricoCommLineaBean> info=new ArrayList();
    ResultSetHelper rsh=null;
    ResultSet rs = null;
    
    if(con==null || ll==null)
      return null;
    
    QryColliVolumiCommessa qry=new QryColliVolumiCommessa();
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, ll.getQueryCondition());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    try {
      String sql=qry.toSQLString();
      _logger.info(sql);
      rsh = new ResultSetHelper(con, sql);
      rs=rsh.resultSet;
      
      while(rs.next()){
        CaricoCommLineaBean bean=new CaricoCommLineaBean();
        bean.setCommessa(ll.getCommessa());
        bean.setLineaLav(ll.getCodLineaLav());
        bean.setDataRifN(ll.getDataCommessa());
        bean.setUnitaMisura(ll.getUnitaMisura());
        bean.setDivisione(rs.getString(FilterFieldCostantXDtProd.FD_DIVISIONE));
        bean.setValore(rs.getDouble(FilterFieldCostantXDtProd.FD_NUMCOLLI));
        info.add(bean);
      }
      
    } catch (SQLException ex) {
      _logger.error("Impossibile caricare i dati di produzione per la linea:"+ll.getCodLineaLav()+" ->"+ex.getMessage()); 
      throw new DatiCommLineeException(ex);
    } catch (QueryException ex) {
      _logger.error("Impossibile caricare i dati di produzione per la linea:"+ll.getCodLineaLav()+" ->"+ex.getMessage()); 
    }
    
    return info;
  }

  

  
  private static final Logger _logger = Logger.getLogger(DtColliCommessaStd.class);   


}
