/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.indicatoriOee.linee;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryForBiesseG1P2PeriodiProd extends CustomQuery{
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
    
//    query.append(" Select min(ini) iniz,max(fin) fine from ( ").append(
//                  "  select MIN(oraInizio) ini,max(OraFine) fin from dbo.LogEventi ").append(
//                  "  where 1=1 ").append(
//                  "    and  orainizio >= convert(datetime,(").append(dtInizio).append("),120) ").append(
//                  "    and  orafine <= convert(datetime,(").append(dtFine).append("),120) ").append(                  
//                  " UNION ").append( 
//                  "  select MIN(oraInizio),max(OraFine) from dbo.LogProduzioneLotti ").append(
//                  "  where 1=1 ").append(
//                  "    and  orainizio >= convert(datetime,(").append(dtInizio).append("),120) ").append(
//                  "    and  orafine <= convert(datetime,(").append(dtFine).append("),120) ").append(                  
//                  " ) U" );
    
    
     query.append(" Select min(ini) iniz,max(fin) fine from ( ").append(
                  "  select MIN(oraInizio) ini,max(OraFine) fin from dbo.LogEventi ").append(
                  "  where 1=1 ").append(
                  "    and  orainizio <= convert(datetime,(").append(dtFine).append("),120) ").append(
                  "    and  orafine >= convert(datetime,(").append(dtInizio).append("),120) ").append(                  
                  " UNION ").append( 
                  "  select MIN(oraInizio),max(OraFine) from dbo.LogProduzioneLotti ").append(
                  "  where 1=1 ").append(
                  "    and  orainizio <= convert(datetime,(").append(dtFine).append("),120) ").append(
                  "    and  orafine >= convert(datetime,(").append(dtInizio).append("),120) ").append(                  
                  " ) U" );
    
    return query.toString();
  }
}
