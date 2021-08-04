/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
public class QueryInfoPziImaAnte extends CustomQuery{

  
  public final static String FTPZBORD="FTPZBORD";
  public final static String FTPZCRTL="FPZCRTL";
  
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FilterQueryProdCostant.FTDATARIF);
    
    StringBuffer qry=new StringBuffer(
            " select ").append(
            " substring(barcode,1,1) as tipo,count(*) as qta ").append(
            "\n from tab_et_histstatus ").append(
            "\n where zpstatus>= convert(datetime ,(' ").append(dataRif).append(" 00:00:01'),120)   ").append(
            " and zpstatus<= convert(datetime ,('  ").append(dataRif).append("  23:59:59'),120)  "); 
            
    
    if(isFilterPresent(FTPZBORD)){
       qry.append("\n and status='57' ");
                  
       
    }else if(isFilterPresent(FTPZCRTL)){
      qry.append("\n and status='63' ").append(
                   " and substring(barcode,1,1) in ('2','8') ");
    }           
    
    qry.append("\n group by substring(barcode,1,1) ").append(
               "\n order by 1 ");
    
    
    return qry.toString();
    
  }
  
}
