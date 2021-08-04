/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee.linee;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryImaAntePzScartati extends CustomQuery{

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
    
    query.append(" SELECT count(*) as pz_scartati ").append(
                   " FROM dbo.tab_ETAusschuss").append(
                  "  WHERE 1=1 ").append(
                  "     and  ZPStatus between convert(datetime,(").append(dtInizio).append("),120) ").append(
                  "     and  convert(datetime,(").append(dtFine).append("),120) "); 
   
    return query.toString();
  }
  
}
