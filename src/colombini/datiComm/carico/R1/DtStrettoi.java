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
import colombini.query.datiComm.carico.R1.QryNClStrettoi;
import colombini.util.DatiCommUtils;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author lvita
 */
public class DtStrettoi implements IDatiCaricoLineaComm {

  public final static String STRETTOIO="01115";
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    QryNClStrettoi qry=new QryNClStrettoi();
    String strettoio="";
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    strettoio=" and ( MMSPE4='STRET:S2' or MMSPE4='STRET:S0' or MMSPE4='STRET:S1') ";

    
    qry.setFilter(QryNClStrettoi.STRETTOIO, strettoio);
    
    
    return DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMCOLLI);
    
  }
  
}
