/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per trovare le commesse che sono state elaborate e concluse ad una certa data 
 * @author lvita
 */
public class QryDeleteZtappi extends CustomQuery{
  
  public final static String LIBRERIAMVX="mcobmoddta";
  public final static String FTCOMMFEB="FTCOMMFEB";
  
  @Override
  public String toSQLString() throws QueryException {

    StringBuilder str=new StringBuilder();
    str.append(" Delete ").append(LIBRERIAMVX).append(".ztappi where 1=1 "); 
    str.append(addAND(inStatement("TXDTRF", FilterQueryProdCostant.FTDATACOMMN)));
    
  
    if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM)  ){
       str.append(addAND(eqStatement("TXCOMM",FilterQueryProdCostant.FTNUMCOMM)));
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEETOELAB)  ){
       str.append(addAND(inStatement("TXPLGR",FilterFieldCostantXDtProd.FT_LINEETOELAB)));             
    }
    return str.toString();
  } 
  
}
