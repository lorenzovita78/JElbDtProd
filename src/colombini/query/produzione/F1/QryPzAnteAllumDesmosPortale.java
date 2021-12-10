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
public class QryPzAnteAllumDesmosPortale extends CustomQuery{
public final static String isFebal="isFebal";
public final static String numArt1="numArt1";

  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String select="select Codice_Collo,NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa ";

          
    String db="DesmosColombini.dbo.";
    if(isFilterPresent(isFebal)){
         db="DesmosFebal.dbo.";}

    StringBuilder cond=new StringBuilder(" where 1=1 and DesmosLancio =" +getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS) + "\n");
    
    if(isFilterPresent(isFebal)){
    select="select Codice_Collo,0 NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa ";
    } 
    

    
    q.append(select);
    q.append(" from ").append(db).append("desmosPortale ");
    q.append(cond);
    q.append(addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV)));
    if(isFilterPresent(numArt1)){
        q.append(" AND NumArticolo = 1");
    }
   
    
    return q.toString();
    
  }
 
  
  
}
