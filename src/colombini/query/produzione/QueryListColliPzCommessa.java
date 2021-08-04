/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione;

import colombini.conn.ColombiniConnections;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryListColliPzCommessa extends CustomQuery{

  public final static String FLTRCOMM="FLTRCOMM";
  public final static String FLTRCOLLO="FLTRCOLLO";
  public final static String FLTRBUS="FLTRBUS";
  
  public final static String FLCONDPERS="FLCONDPERS";
  
  @Override
  public String toSQLString() throws QueryException {
    
    String comm=getFilterSQLValue(FLTRCOMM);
    
    StringBuilder select=new StringBuilder(" SELECT CLNCOL,CLNART,CLLINP,CLBOXN,CLPEDA,CLNROR,CLRIGA,CLARTI,CLDESC,CLSTRD ").append(
                           " FROM ").append(ColombiniConnections.getAs400LibTemp()).append(
                                     ".SC").append(comm).append("COL").append(
                                     " WHERE 1=1");
                                     
      
    if(isFilterPresent(FLTRCOLLO)){
      select.append(addAND("CLNART = 0"));
    }else{
      select.append(addAND("CLNART <> 0"));
    }

    select.append(addAND(inStatement("CLLINP", FilterQueryProdCostant.FTLINEELAV)));
    
    if(isFilterPresent(FLTRBUS))
      select.append(addAND(inStatement("CDDITT", FLTRBUS)));
    
    
    //condizione della query personalizzata
    if(isFilterPresent(FLCONDPERS)){
      String condPersonal=(String) getFilterValue(FLCONDPERS);
      select.append(condPersonal);
    }
    
    
    return select.toString(); 
  }
  
  
  
}
