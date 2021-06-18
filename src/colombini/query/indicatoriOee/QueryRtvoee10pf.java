/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryRtvoee10pf extends  CustomQuery {
   
  public final static String CENTROLAVORO="CENTROLAVORO";

  @Override
  public String toSQLString() throws QueryException {
    
    String centroLav=getFilterSQLValue(CENTROLAVORO);
    
    StringBuffer qry=new StringBuffer(" select VAL_RIP,TEM_SET,TEM_AVV,TEM_RIP,LINP ").append(
                                      " from mcobmoddta.RTVOEE10PF");
    
    qry.append(" where centro =").append(centroLav);
    
    return qry.toString();
  }
  
  
}
