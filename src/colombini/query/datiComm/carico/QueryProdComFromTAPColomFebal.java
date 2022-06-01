/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.carico;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryProdComFromTAPColomFebal extends CustomQuery {

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sq=new StringBuilder("  select distinct oadivi as divi,tincol  ");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FD_NUMPEZZI))
      sq.append(" , tinart ");
    
    sq.append("FROM mcobmoddta.ztapci inner join mvxbdta.oohead on ticono=oacono and tiorno=oaorno ");
    sq.append(" WHERE 1=1");
    
    sq.append(" AND TICONO = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA));
    //Modifica fatta per prendere i dati anche da Febal (GG 31052022)
    // sq.append(" AND TICOMM = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM));
    sq.append(addAND(inStatement("TICOMM", FilterFieldCostantXDtProd.FT_NUMCOMM)));
    sq.append(" AND TIPLGR = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA));
    sq.append(" and TIDTCO = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
    
    
    return " SELECT divi,count(*) FROM ( "+sq.toString()+" ) a GROUP BY divi"; //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
}

