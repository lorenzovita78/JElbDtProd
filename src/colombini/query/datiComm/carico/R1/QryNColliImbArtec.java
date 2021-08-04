/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryNColliImbArtec extends CustomQuery{
  
  @Override
  public String toSQLString() throws QueryException {
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    
    
    
    StringBuilder query=new StringBuilder(
            " select oadivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
              " ,count(*) as ").append(FilterFieldCostantXDtProd.FD_NUMCOLLI).append(
              " ,sum(clvom3) as ").append(FilterFieldCostantXDtProd.FD_VOLUME).append(
              " from ").append(tabCommessa).append( 
                     ", MVXBDTA.OOHEAD , MVXBDTA.OCUSMA").append(
              " where 1=1 ").append(
              " and CLNART=0 ").append( 
              " and CLAMGS=").append(dataC).append(  
              " and oacono=").append(azdefault).append(
              " and clnror=oaorno ").append(
              " and oacono=okcono ").append(
              " and oacuno=okcuno ").append(
              " and (okcfc4 <>'ITA' ").append(
              " and okcfc4 <>'RSM' ) ").append( 
              " AND ( (CLLINP>='36010' AND CLLINP<='36030') OR CLLINP='36150' OR CLLINP='36200' ) ").append(
            " group by oadivi order by oadivi");
    
   
    
    return query.toString();
    
   }
  
}
