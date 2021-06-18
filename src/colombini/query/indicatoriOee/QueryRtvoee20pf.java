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
public class QueryRtvoee20pf extends CustomQuery{

  public final static String CENTROLAVORO="CENTROLAVORO";
  public final static String DATA="DATA";
  public final static String ORA="ORA";
  
  @Override
  public String toSQLString() throws QueryException {
    
    String centroLav=getFilterSQLValue(CENTROLAVORO);
    String dataN=getFilterSQLValue(DATA);
    
    StringBuffer qry=new StringBuffer(" select QTA_BUO,TIME_BUO,QTA_SCA,TIME_SCA,NPED ").append(
                                      " from mcobmoddta.RTVOEE20PF");
    
    qry.append(" where centro =").append(centroLav);
    qry.append(" and data =").append(dataN);
    
    if(isFilterPresent(ORA))
      qry.append(" and ora=").append(getFilterSQLValue(ORA));
    
    return qry.toString();
  }
  
}
