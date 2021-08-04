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
public class QryCSYTAB extends CustomQuery {

 
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder();
    
    qry.append(" SELECT TRIM(CTSTKY) as key,TRIM(CTTX15) as name,TRIM(CTTX40) as descr,TRIM(CTPARM) as parm ").append(
               " FROM ").append(ColombiniConnections.getAs400LibStdColom()).append(".").append(
                 " CSYTAB").append(
                 " WHERE 1=1 ").append(
                 " AND CTCONO=").append(getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA)).append(
                 " AND CTDIVI=").append(getFilterSQLValue(FilterQueryProdCostant.FTDIVISION)).append(        
                 " AND CTSTCO=").append(getFilterSQLValue(FilterQueryProdCostant.FTCONSTCSYTAB));               
    
    String query=qry.toString();
    
  if(isFilterPresent(FilterQueryProdCostant.FTKEYCSYTAB) ) {  
    query=query.replace("CTSTKY", "REPLACE(CTSTKY,"+getFilterSQLValue(FilterQueryProdCostant.FTKEYCSYTAB)+",'')") ;
    
    query+=addAND(likeStatement("CTSTKY",FilterQueryProdCostant.FTKEYCSYTAB));
  }
    
  
  
    
    return query;
            
  }
  
  
  
}
