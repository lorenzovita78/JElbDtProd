/*
 * To change this template, choose Tools | Templates
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
public class QryFacilityBU extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    String query="";
    query="select distinct cffaci "+FilterFieldCostantXDtProd.FD_FACILITY+" ,cfdivi "+FilterFieldCostantXDtProd.FD_DIVISIONE
            + "  from "+FilterFieldCostantXDtProd.TABFACILITY
            + " where cfcono="+getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    return query;
  }
  
  
  
}
