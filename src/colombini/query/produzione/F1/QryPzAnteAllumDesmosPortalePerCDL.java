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
public class QryPzAnteAllumDesmosPortalePerCDL extends CustomQuery{
//public final static String isFebal="isFebal";

  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String select="select Codice_Collo,NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa,barcode ";
          
   

    StringBuilder cond=new StringBuilder(" where 1=1  and Commessa =" +getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS) + "\n");
    
     if(isFilterPresent(FilterFieldCostantXDtProd.FT_CDL)){
        cond.append(addAND(eqStatement("CentroDiLavoro", FilterFieldCostantXDtProd.FT_CDL)))
         ;}

    
    q.append(select);
    q.append(" from ").append("desmosPortale ");
    q.append(cond);
    if(isFilterPresent(FilterQueryProdCostant.FTLINEELAV)){
        q.append(addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV)));
    }
    

    return q.toString();
    
  }
  
}
