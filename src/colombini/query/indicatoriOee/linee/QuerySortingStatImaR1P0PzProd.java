/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee.linee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che torna il numero di pezzi usciti dalla sorting station (stato 72)
 * @author lvita
 */
public class QuerySortingStatImaR1P0PzProd extends CustomQuery{

  
  public final static String FDATARIF="FDATARIF";
  public final static String FDATAINI="FDATAINI";
  public final static String FDATAFIN="FDATAFIN";
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FDATARIF);
    String dataIni=(String) getFilterSQLValue(FDATAINI);
    String dataFin=(String) getFilterSQLValue(FDATAFIN);
    
    StringBuffer qry=new StringBuffer(
            "  select a.pz pztot, b.pz pzTurno  from ( ").append(
                  " select COUNT(*) as pz ").append(
                    " from Colombini_Tops.dbo.tab_ET_HistStatus ").append(
                    " where 1=1 ").append(
                    "  and status =72").append( 
                     " and ZPStatus between convert(datetime,(' ").append(dataRif).append(" 00:00:01'),120)   ").append(    
                     " and  convert(datetime,('  ").append(dataRif).append("  23:59:59'),120)  )a ,").append(
                  "( select COUNT(*) as pz ").append(
                    " from Colombini_Tops.dbo.tab_ET_HistStatus ").append(
                    " where 1=1 ").append(
                    "  and status =72").append( 
                    " and ZPStatus between convert(datetime,(").append(dataIni).append("),120)   ").append(    
                    " and  convert(datetime,(").append(dataFin).append(" ),120)  ) b  ");
                   
    return qry.toString();
  }
  
  
  
}
