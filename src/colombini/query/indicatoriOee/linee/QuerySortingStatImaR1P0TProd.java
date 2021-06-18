/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee.linee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che fornisce informazioni relative al tempo totale di impianto per la sorting station 
 * Il tempo totale dell'impianto viene calcolato come differenza tra la data min e la data max 
 * relativa ai pezzi in stato 71 (in entrata alla sorting  station)
 * @author lvita
 */
public class QuerySortingStatImaR1P0TProd extends CustomQuery{

  
  public final static String FDATARIF="FDATARIF";
  
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FDATARIF);
    
    StringBuffer qry=new StringBuffer(
            " select  isnull(datediff( SS,MIN(zpstatus),MAX(zpstatus)),0) as secTot , ").append(
            " isnull(MIN(zpstatus),0) as ini , isnull(MAX(zpstatus),0) as fin").append(
            " from Colombini_Tops.dbo.tab_ET_HistStatus ").append(
            " where 1=1 ").append(
            "  and status =71").append( 
             " and ZPStatus between convert(datetime,(' ").append(dataRif).append(" 00:00:01'),120)   ").append(    
             " and  convert(datetime,('  ").append(dataRif).append("  23:59:59'),120)  ");
            
                   
    return qry.toString();
  }
  
  
  
}
