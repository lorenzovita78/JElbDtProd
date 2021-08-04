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
public class QuerySezGabbianiR2DatiRuntime extends CustomQuery{

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
    
    query.append(" select id,DDT,DDTDate,DateTimeReady,DateTimeStart,DateTimeEnd,").append(
                          " datediff(SS,DateTimeStart, DateTimeEnd) diff  ").append(
                  " from dbo.AS400DDT ").append(
                  "  WHERE 1=1 ").append(
                  "     and  DDTDate between convert(datetime,(").append(dtInizio).append("),112) ").append(
                  "     and  convert(datetime,(").append(dtFine).append("),112) ").append(
                  " and TypeMovement=0 ").append(
                  " and ReasonMovement=1 ").append(
                  "order by DDTDate "); 
   
    return query.toString();
  }
  
}
