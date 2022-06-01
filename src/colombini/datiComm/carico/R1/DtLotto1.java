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
import colombini.query.datiComm.carico.R1.QryNPziDatiProduzione;
import colombini.util.DatiCommUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 *
 * @author lvita
 */
public class DtLotto1 implements IDatiCaricoLineaComm {

  public DtLotto1(){
    
  }
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
      
   Connection conSqlS=null;
    
   try{
     conSqlS=ColombiniConnections.getDbDesmosColProdConnection(); 
   }
     catch(SQLException s){
       throw new DatiCommLineeException(s);}
   
    QryNPziDatiProduzione qry=new QryNPziDatiProduzione();
    qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, " and descfase20='P4 SQUADRABORDATURA' ");
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, ll.getDataCommessa());
    //qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    
    return DatiCommUtils.getInstance().getListDtCommFromQuery(conSqlS, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
  }
  
}
