/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.util.DesmosUtils;
import db.CustomQuery;
import exception.QueryException;
import utils.DateUtils;

/**
 * Query per trovare le commesse che sono state elaborate e concluse ad una certa data 
 * @author lvita
 */
public class QryDeleteZtapci extends CustomQuery{
  
  public final static String LIBRERIAMVX="mcobmoddem";
  public final static String FTCOMMFEB="FTCOMMFEB";
  
  @Override
  public String toSQLString() throws QueryException {

    StringBuilder str=new StringBuilder();
    str.append(" Delete ").append(LIBRERIAMVX).append(".ztapci where 1=1 "); 
    str.append(addAND(inStatement("TIDTCO", FilterQueryProdCostant.FTDATACOMMN)));
    
  
    if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM)  ){
       str.append(addAND(eqStatement("( TICOMM",FTCOMMFEB)))
               .append(addOR(eqStatement("TICOMM",FilterQueryProdCostant.FTNUMCOMM))).append(")");     
    }
  
    return str.toString();
  } 
  
}
