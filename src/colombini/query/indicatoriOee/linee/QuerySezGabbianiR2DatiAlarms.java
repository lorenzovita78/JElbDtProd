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
public class QuerySezGabbianiR2DatiAlarms extends CustomQuery{

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
    
    query.append(" select nodeid,date,symbname,status,a.causalid,description ").append(
                   " FROM dbo.SignalsHistory a left outer join dbo.AlarmCausal b on a.causalid=b.causalid  ").append(
                  "  WHERE 1=1 ").append(
                  "     and  Date between convert(datetime,(").append(dtInizio).append("),112) ").append(
                  "     and  convert(datetime,(").append(dtFine).append("),112) ").append(
                  " order by nodeid,date ,status "); 
   
    return query.toString();
  }
  
}
