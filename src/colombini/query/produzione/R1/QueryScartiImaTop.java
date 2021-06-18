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
public class QueryScartiImaTop extends CustomQuery {

  
  
  @Override
  public String toSQLString() throws QueryException {
    String dtIni=getFilterSQLValue(FilterQueryProdCostant.FTDATAINI);
    String dtFin=getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN);
    
    StringBuilder qry=new StringBuilder();
    
    qry.append(" SELECT  CAST(year(data)*10000 +month(data)*100+day(data) AS DECIMAL (8) )as dataAs , ").append(
                         " codice_articolo as articolo, pezzi_pacco  as QTA, causale1_scarto as causale ,").append(
                         "   commessa,  cast (collo as varchar(5)) as COLLO, codice_progressivo as progressivo  ").append(
               "\n  FROM  PanotecImballo.dbo.ReportProduzione  a").append( 
               "\n WHERE  a.scarto=1 ").append(
               " and causale1_scarto > 0 ").append(
               " and a.data between  convert(datetime,").append(dtIni).append(",120) ").append(
                        " and convert(datetime,").append(dtFin).append(",120) ");
    
    
    return qry.toString();
  }
  
  
  
}
