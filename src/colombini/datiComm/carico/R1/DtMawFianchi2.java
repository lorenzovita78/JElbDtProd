/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.R1.QryNPziMawFianchi;
import colombini.util.DatiCommUtils;
import java.sql.Connection;
import java.util.List;


/**
 * 
 * @author lvita
 */
public class DtMawFianchi2 implements IDatiCaricoLineaComm{

  
  public DtMawFianchi2(){
    
  }
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    
    QryNPziMawFianchi qry=new QryNPziMawFianchi();
    qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, " and CLLINP IN ('06022','06023', '06024', '06025', '06026', '06029', '06222', '06223', '06224', '06229') ");
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    
    return DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
  }
  
}
