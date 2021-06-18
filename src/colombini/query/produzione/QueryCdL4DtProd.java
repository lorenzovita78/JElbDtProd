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
public class QueryCdL4DtProd extends CustomQuery{

  
  public final static String FTGESTTURNI="FTGESTTURNI";
  public final static String FTCALCOEE="FTCALCOEE";
  public final static String FTCOPYDT="FTCOPYDT";
  public final static String FTWRKC="FTWRKC";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql= new StringBuilder();
    sql.append(" select distinct clcono, clfact, clplan, clplgr, clplde , cldivp, clmail ,clrwct,clunms ").append(
               " from mcobmoddta.ZDPWCL ").append(
               " where 1=1 ");

    if(isFilterPresent(FTGESTTURNI))
      sql.append("and clfgtc=1 ");
    
    if(isFilterPresent(FTCALCOEE))
      sql.append("and clcoee=1 ");
    
    if(isFilterPresent(FTCOPYDT))
      sql.append("and clfcyd=1 ");           
    
    if(isFilterPresent(FilterQueryProdCostant.FTSTAB))
      sql.append(" and clfact=").append(getFilterSQLValue(FilterQueryProdCostant.FTSTAB));
    
    if(isFilterPresent(FilterQueryProdCostant.FTPLAN))
      sql.append(" and clplan=").append(getFilterSQLValue(FilterQueryProdCostant.FTPLAN));
    
    if(isFilterPresent(FilterQueryProdCostant.FTLINEELAV))
      sql.append(addAND(inStatement("CLPLGR",FilterQueryProdCostant.FTLINEELAV)));
    
    
    if(isFilterPresent(FTWRKC))
      sql.append("and clrwct is not null ");
    
    sql.append(" order by clfact,clplan,clplgr ");
    
    return sql.toString();
  }
  
 
}
