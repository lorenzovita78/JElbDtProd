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
public class QueryProdGgImpIma extends CustomQuery{

  public final static String FT_CLIIMAANTE="FT_CLIIMAANTE";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder(
                     " select CommissionNo,COUNT(*) from dbo.tab_ET").append(
                      "  where 1=1 and status>=100 ").append(
                      "  AND KenBC= 2 ")        ;
            

    if(isFilterPresent(FT_CLIIMAANTE)){
      qry.append(" and  CommissionNo between 400 and 797 ");
    }else{
      qry.append(" and  (CommissionNo < 400 or CommissionNo> 797 )");
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATADA) ){
      String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);
      qry.append("AND ZPStatus >= convert( datetime ,").append(dI).append(" ,120)");
    } 
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATAA)){
      String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);
      qry.append("AND ZPStatus <= convert( datetime ,").append(dI).append(" ,120)");
    }
    
    
    qry.append(" group by CommissionNo  ");
    
    return qry.toString(); 
  }
  
  
  
}
