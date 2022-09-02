/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query per trovare le commesse che sono state elaborate e concluse ad una certa data 
 * @author lvita
 */
public class QryDeleteZtapci extends CustomQuery{
  
  public final static String LIBRERIAMVX="mcobmoddem";

  
  @Override
  public String toSQLString() throws QueryException {
    Integer nrocomm=0;
  if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM)){
    nrocomm= Integer.parseInt(FilterQueryProdCostant.FTNUMCOMM);
  }

    StringBuilder str=new StringBuilder();
    str.append(" Delete ").append(LIBRERIAMVX).append(".ztapci where 1=1 "); 
    str.append(addAND(inStatement("TIDTCO", FilterQueryProdCostant.FTDATACOMMN)));
    
  
    if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM) && nrocomm<=365 ){
       str.append(" and TICOMM = ").append(nrocomm); 
       //Manca aggiungere la comm febal
    }
    
    if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM) && nrocomm>365 ){
       str.append(" and TICOMM = ").append(nrocomm); 
    }
    
    return str.toString();
  } 
  
}
