/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryPWInfoLineeTurni extends CustomQuery{

  public final static String FCOPYACTIVE="FCOPYACTIVE";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder  s=new StringBuilder(
            " select distinct coplgr,coidco,clfact,clplan,clplgr ").append(
            " from mcobmoddta.ZDPWCO inner join mcobmoddta.ZDPWCL on coplgr=clplgr ").append(
            " where 1=1").append(
            " and codtrf=").append(getFilterSQLValue(FilterQueryProdCostant.FTDATARIF));
    
    if(isFilterPresent(FCOPYACTIVE))
      s.append(" and clfcyd =1 ");  
    
    if(isFilterPresent(FilterQueryProdCostant.FTLINEELAV))
      s.append(addAND(inStatement("COPLGR", FilterQueryProdCostant.FTLINEELAV)));
  
    s.append("order by  clfact,clplan,clplgr");
    
    
    return s.toString();
  }

  
  
  
  
  
  
  
}
