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
import colombini.query.datiComm.carico.R1.QryNPzStrettoioCassettiArtec;
import colombini.util.DatiCommUtils;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author lvita
 */
public class DtStrettoioCassettiArtec implements IDatiCaricoLineaComm {

  public DtStrettoioCassettiArtec(){
    
  }
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    
    
    QryNPzStrettoioCassettiArtec qry=new QryNPzStrettoioCassettiArtec();
    
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    return DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
  }
  
}
