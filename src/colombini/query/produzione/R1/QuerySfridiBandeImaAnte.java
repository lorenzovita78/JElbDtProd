/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QuerySfridiBandeImaAnte extends CustomQuery{

  public final static String DATAORAINIZIO="DATAORAINIZIO";
  public final static String DATAORAFINE="DATAORAFINE"; 
    
  @Override
  public String toSQLString() throws QueryException {
    
    String dtInizio = null;
    String dtFine = null;
    
    
    
   //filtri obbligatori
   dtInizio=getFilterSQLValue(DATAORAINIZIO);
   dtFine=getFilterSQLValue(DATAORAFINE);  
    
   StringBuilder qry=new StringBuilder(
           "select max(Materialnr) as [CODICE BANDA],").append(
                    " round (100-(").append(
           "(  SUM(case when Substring(bc_0,1,1) in ('1','2') then schnittlaenge else 0 end)  ").append(
           " + SUM(case when Substring(bc_1,1,1) in ('1','2') then schnittlaenge else 0 end) ").append(
           " + SUM(case when Substring(bc_2,1,1) in ('1','2') then schnittlaenge else 0 end) ").append(
           ")").append( 
           "/").append( 
           " (select sum(anzLagen * Laenge) from colombini_fronten.dbo.tab_schnittplaene t3").append(
             " where ").append(
              " t3.AngelegtAm between convert(datetime,( ").append(dtInizio).append(" ),112) ").append(
                          "  and convert(datetime,(").append(dtFine).append("),112) ").append(
           " and t3.MaterialNr = t1.MaterialNr ").append(
           " )) ").append(
     " * 100,2) as [PERCENTUALE DI SFRIDO], ").append(
 " (select sum(anzLagen) from colombini_fronten.dbo.tab_schnittplaene t4 ").append(
            " where ").append(
               " t4.AngelegtAm between convert(datetime,(").append(dtInizio).append("),112) ").append(
							    " and convert(datetime,(").append(dtFine).append("),112) ").append(
           " and t4.MaterialNr = t1.MaterialNr ").append(
           " ) as [NUMERO DI BANDE] ").append(
  " from colombini_fronten.dbo.tab_schnittplaene t1 join      ").append( 
       " colombini_fronten.dbo.tab_SchnittplanPos t2 ").append(
       " on t1.IDNr = t2.SPlanIDnr ").append(
  " where ").append(
       " t1.AngelegtAm between convert(datetime,(").append(dtInizio).append("),112) ").append(
							    " and convert(datetime,(").append(dtFine).append("),112) ").append(
       " group by t1.MaterialNr ").append(
       " order by t1.Materialnr ");
    
    
   return qry.toString();
   
  }
  
  
  
  
}
