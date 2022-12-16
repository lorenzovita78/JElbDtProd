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
 * @author gg
 */
public class QueryListDesmosFileRem extends CustomQuery{

  public final static String FLTRCOMM="FLTRCOMM";
  
  
  @Override
  public String toSQLString() throws QueryException {
        
    StringBuilder select=new StringBuilder(" SELECT collo,Posizione,Linea,0 box,0 pedana,0 clnror, 0 clriga, codart,substring(DescArt,0,29) as DescArt,DescArt ").append(
                           " FROM desmosFebal.dbo.DesmosFileREM WHERE 1=1 ");
                                     
    
    select.append(addAND(eqStatement("Commessa", FLTRCOMM)));
    select.append(addAND(eqStatement("dataCommessa", FilterQueryProdCostant.FTDATACOMMN)));
      
      if (isFilterPresent(FilterQueryProdCostant.FTLINEELAV)) {
        select.append(addAND(notInStatement("linea", FilterQueryProdCostant.FTLINEELAV)));
      }
        
    return select.toString(); 
  }
  
  
  
}
