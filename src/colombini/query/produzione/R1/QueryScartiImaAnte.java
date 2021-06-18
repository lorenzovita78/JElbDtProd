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
public class QueryScartiImaAnte extends CustomQuery{
  
 
  @Override
  public String toSQLString() throws QueryException {
    String dtIni=getFilterSQLValue(FilterQueryProdCostant.FTDATAINI);
    String dtFin=getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN);
    
    StringBuilder qry=new StringBuilder();
    qry.append(" SELECT substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),7,4)+ ").append(
                       " substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),4,2)+ ").append(
                       " substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),1,2) as dateError ,").append(
                       " Itemno, count(*) as NumberofErrors, cast(grund as bigint) as errortype ,barno   ").append( 
                        // barlength as length,barwidth as width, barthickness as thickness, 
               "\n FROM dbo.tab_ETAusschuss INNER JOIN dbo.tab_ET ON dbo.tab_ET.Barcode = dbo.tab_ETAusschuss.Barcode ").append(
               "\n WHERE 1=1 ").append(
                        " and dbo.tab_ETAusschuss.ZPStatus between  convert(datetime,").append(dtIni).append(",120) ").append(
                        " and convert(datetime,").append(dtFin).append(",120) ").append(
               "\n AND Itemno is not null AND len(ItemNo) > 0").append(
              " GROUP BY substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),7,4)+ ").append(
                       " substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),4,2)+ ").append(
                       " substring(convert(Varchar(10),dbo.tab_ETAusschuss.ZPSTATUS,103),1,2), ").append(
                       " Itemno, barlength, barwidth, barthickness, barno, grund").append(
              " ORDER BY 1");        
    
    
    return qry.toString();
  }
  
  
  
}
