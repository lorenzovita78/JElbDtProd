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
 * Query per estrarre su impianto Ima Ante
 * per un dato giorno la qta dei pz controllati relativi alle due Combima 
 * @author lvita
 */
public class QueryPzCtrlImaAnteXCb extends CustomQuery{

 
  
  
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FilterQueryProdCostant.FTDATARIF);
    
    StringBuilder qry=new StringBuilder(
            " select count(*) as qta, coalesce(b.station,'CX') as station ").append(
                    "\n from tab_et_histstatus a ").append( 
                    "\n left outer join (select *  from tab_flow_c ").append(
                    "\n      where barcode + convert(varchar,zp,20) in ( ").append(
                    "\n        select barcode + convert(varchar,min(zp),20) from tab_flow_c  where barcode in (  ").append(
                    "\n         select barcode from tab_et_histstatus ").append(
                    "\n          where zpstatus>= convert(datetime ,(' ").append(dataRif).append(" 00:00:01'),120)   ").append(
                    "\n            and zpstatus<= convert(datetime ,('  ").append(dataRif).append("  23:59:59'),120)  ").append(
                    "\n            and substring(barcode,1,1) in ('2','8') and status='63') ").append(
                    "\n          group by barcode) " ).append(
                    "\n ) b on a.barcode=b.barcode  " ).append(
"\n where  zpstatus>= convert(datetime ,(' ").append(dataRif).append(" 00:00:01'),120)   ").append( 
  "\n    and zpstatus<= convert(datetime ,('  ").append(dataRif).append("  23:59:59'),120)  ").append( 
"\n and substring(a.barcode,1,1) in ('2' , '8') ").append(
"\n and a.status='63' ").append(
"\n group by coalesce(b.station,'CX') ").append(
" order by coalesce(b.station,'CX') ");
            
    
    
    return qry.toString();
    
  }
  
}
