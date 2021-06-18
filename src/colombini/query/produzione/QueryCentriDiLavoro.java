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
public class QueryCentriDiLavoro extends CustomQuery {

  public final static String FTONLYCDL="FTONLYCDL";
  
  @Override
  public String toSQLString() throws QueryException {
    String s= "select PPFACI, PPPLGR, PPPLNM, PPIIWC, PPDEPT, PPREAR, PPPLTP, PPOPDS ,PPSUNO "
            + " from mvxbdta.mpdwct "
            + " where PPCONO ="+getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA);
            
    if(isFilterPresent(FTONLYCDL)){
      s+=" and PPDEPT <>'SPE' ";
    }
    
    return s;
  }
  
  
  
}
