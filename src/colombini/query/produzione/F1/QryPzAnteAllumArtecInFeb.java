/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.F1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryPzAnteAllumArtecInFeb extends CustomQuery{

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String db=" DesmosFebal.dbo.";
    
    String where=" where DesmosLancio ="+getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS);
    where+=addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV));
   
    q.append(" select Codice_Collo , ROW_NUMBER() OVER(partition by Codice_Collo  order by Codice_Collo) , linea,idBox,Pedana,Odv,num_riga,BarCodart,descBreve ,descCodicePadre " ).append(
              "        from (\n").append(
                " SELECT distinct Codice_Collo,1 as nart ,DesmosDestinatario as linea,idBox,Pedana,Odv,num_riga,BarCodart,substring(descCodicePadre,1,30) descBreve ,descCodicePadre").append(
                "  from ").append(db).append("[ETK34_PDF_ETK_ARTEC_FEB_A5]").append(  //[ETK32_PDF_ETK_ARTEC_A5_AS]
                where).append(" ) a" );
    
    
    //ETK34_PDF_ETK_ARTEC_FEB_A5
    return q.toString();
    
  }
 
  
  
}
