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
public class QueryForBiesseG1P2DatiProd extends CustomQuery{

  public final static String DATAINIZIO="DATAINIZIO";
  public final static String DATAFINE="DATAFINE";
  public final static String TOTPZ="TOTPZ";
  
  @Override
  public String toSQLString() throws QueryException {

    StringBuilder query=new StringBuilder();
    String dtInizio = null;
    String dtFine = null;
    
    //filtri obbligatori
    dtInizio=getFilterSQLValue(DATAINIZIO);
    dtFine=getFilterSQLValue(DATAFINE);  
    
    if(!isFilterPresent(TOTPZ)){
    
      query.append(" select articolo, qta, orainizio, orafine, datediff(SS,orainizio, orafine) diff,id").append(
                    " from dbo.LogProduzioneLotti ").append(
                    "  WHERE 1=1 ").append(
                    "     and  orainizio <= convert(datetime,(").append(dtFine).append("),120) ").append(
                    "     and  orafine >= convert(datetime,(").append(dtInizio).append("),120) ").append(                  
                    "order by orainizio "); 
   
    }else{
      query.append(" select SUM(qta) ").append(
                    " from dbo.LogProduzioneLotti ").append(
                    "  WHERE 1=1 ").append(
                    "     and  orafine >= convert(datetime,(").append(dtInizio).append("),120) ").append(
                    "     and  orainizio <= convert(datetime,(").append(dtFine).append("),120) "); 
    }
    
    return query.toString();
  }
  
}
