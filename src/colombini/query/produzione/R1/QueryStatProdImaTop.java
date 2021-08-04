/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryStatProdImaTop extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FilterQueryProdCostant.FTDATARIF);
    
    StringBuffer qry=new StringBuffer(
            " select Datepart(HH,zpstatus) ora,Status stato ,COUNT(*) numpz ").append(
            " from Colombini_Tops.dbo.tab_ET_HistStatus ").append(
            " where 1=1 ").append(
              " and ZPStatus between convert(datetime,(' ").append(dataRif).append(" 00:00:01'),120)   ").append(    
              " and  convert(datetime,('  ").append(dataRif).append("  23:59:59'),120)  ").append(
            " group by Datepart(HH,zpstatus),status ").append(
            " order by ora,status");
                   
    return qry.toString();
  }
  
  
  
}
