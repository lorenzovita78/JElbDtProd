/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.carico;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryProdComFromTAP extends CustomQuery {

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sq=new StringBuilder("  select distinct oadivi as divi,tincol  ");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FD_NUMPEZZI))
      sq.append(" , tinart ");
    
    sq.append("FROM mcobmoddta.ztapci inner join mvxbdta.oohead on ticono=oacono and tiorno=oaorno ");
    sq.append(" WHERE 1=1");
    
    sq.append(" AND TICONO = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA));
    sq.append(" AND TICOMM = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM));
    sq.append(" and TIDTCO = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEE))
      sq.append(addAND(inStatement("TIPLGR", FilterFieldCostantXDtProd.FT_LINEE)));
    else
     sq.append(" AND TIPLGR = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA));
    
    return " SELECT divi,count(*) FROM ( "+sq.toString()+" ) a GROUP BY divi"; //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
}

