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
public class QueryOrdiniMovex extends CustomQuery{
  
  public final static String AZIENDA="AZIENDA";
  public final static String NUMORDINE="NUMORDINE";
  public final static String RIGA="RIGA";
  public final static String SOTTORIGA="SOTTORIGA";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder();
    
    String azienda=getFilterSQLValue(AZIENDA);
    String numord=getFilterSQLValue(NUMORDINE);
    String riga=getFilterSQLValue(RIGA);
    String sottoriga=getFilterSQLValue(SOTTORIGA);
    
    qry.append("select * from  ").append(
              "\n from ooline ").append(
              " where 1=1");
    
    qry.append( addAND(eqStatement("OBCONO", azienda)));
    qry.append( addAND(eqStatement("OBORNO", numord)));
    qry.append( addAND(eqStatement("OBPONR", riga)));
    qry.append( addAND(eqStatement("OBPOSX", sottoriga)));
    
    
    
    return qry.toString();
  }
  
  
  
}
