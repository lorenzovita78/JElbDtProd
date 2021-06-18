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
public class QueryScartiMovex extends  CustomQuery{

  
  public final static String DATAINIZIO="DATAINIZIO";
  public final static String DATAFINE="DATAFINE";

  
  @Override
  public String toSQLString() throws QueryException {
    
    StringBuilder query=new StringBuilder();
    String dtInizio = null;
    String dtFine = null;
    
    //filtri obbligatori
    dtInizio=getFilterSQLValue(DATAINIZIO);
    dtFine=getFilterSQLValue(DATAFINE);  
    
    query.append(" SELECT IFNULL(sum(MTTRQT*-1),0) as pz_scartati ").append(
                   " FROM mvxbdta.mittra10").append(
                  " WHERE mtcono=30 ").append(
                  " and mtwhlo='MP1'").append(
                  " and ( MTRGDT>= ").append(dtInizio).append(
                  " and MTRGDT<= ").append(dtFine).append(" )").append(
                  " and MTTRTP ='CCC' ");
   
    return query.toString();
  }
  
  
}
