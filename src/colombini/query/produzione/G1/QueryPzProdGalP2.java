/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.G1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryPzProdGalP2 extends CustomQuery {

  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder str=new StringBuilder();
    
    str=new StringBuilder(" SELECT djtrdt as DT,sum(djmaqt) as PZ ").append(
            "\n FROM mvxbdta.mwoptr ").append(
            "\n where ((djplgr ='01001' and DJDPLG in ('01012', '01013', '01014')) ) ");
    
    if(isFilterPresent(FilterQueryProdCostant.FTDATARIF)){
      str.append(" and DJTRDT=").append(getFilterSQLValue(FilterQueryProdCostant.FTDATARIF));
    } else if(isFilterPresent(FilterQueryProdCostant.FTDATAINI) &&  isFilterPresent(FilterQueryProdCostant.FTDATAFIN )){
      str.append(" and DJTRDT between ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATAINI)).append(
                 " and  ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN));
    }        
    
    str.append(" group by djtrdt ");
    str.append(" order by 1");
    
    return str.toString();
  }
  
  
  
}
