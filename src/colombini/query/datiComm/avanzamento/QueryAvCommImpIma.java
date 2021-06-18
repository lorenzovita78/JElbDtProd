/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.avanzamento;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryAvCommImpIma extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder("Select COUNT(*) from dbo.tab_ET where 1=1 and status>=100 ");

    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LISTCOMM)){
      qry.append(addAND(inStatement(" CommissionNo ", FilterFieldCostantXDtProd.FT_LISTCOMM) ) );
    } 
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_NUMCOMM)){
      qry.append(addAND(eqStatement(" CommissionNo ", FilterFieldCostantXDtProd.FT_NUMCOMM)));
    }
    
//    qry.append(" group by CommissionNo  ");
    
    return qry.toString(); 
  }
  
  
  
}
