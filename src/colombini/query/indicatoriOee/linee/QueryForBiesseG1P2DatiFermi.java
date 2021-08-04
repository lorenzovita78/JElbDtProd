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
public class QueryForBiesseG1P2DatiFermi extends CustomQuery{

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
    
    query.append(" select articolo, causale,  orainizio, orafine, datediff(SS,orainizio, orafine) diff, a.id , testo as descCausale , LongStoppage as tipoEvento  ").append(
                  " from dbo.LogEventi a left outer join dbo.Definizione_Causali b on a.causale=b.id").append(
                  "  WHERE 1=1 ").append(
                  "     and  orainizio <= convert(datetime,(").append(dtFine).append("),120) ").append(
                  "     and  orafine >= convert(datetime,(").append(dtInizio).append("),120) ").append(                  
                  "order by orainizio "); 
   
    return query.toString();
  }
  
}
