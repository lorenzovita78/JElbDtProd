/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryNumPezziCommessaStd;
import exception.QueryException;


/**
 *
 * @author lvita
 */
public class QryNPzForatricePriessOne extends QryNumPezziCommessaStd{
  
  @Override
  public String toSQLString() throws QueryException { 
  String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);

    StringBuffer sql=new StringBuffer(" select CFDIVI").append(
              ", count(*) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
              " from ( ").append("select * from OPENQUERY(GMSPRIESS,'select * from GMSPriess.dbo.Scannermode where com=''").append(  
              getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM )).append( "''')) as gmspriess  inner join \n" +
              "(select * from OPENQUERY(COLOM,'SELECT distinct clcomm,clncol,CFDIVI  FROM mcobmoddta.scxxxcol "
                      + "inner join ( SELECT SUBSTR(cffacn, 21, 1) as diviL,CFDIVI FROM mcobmoddta.ZPILCOM) as divi on diviL=CDDITT "
                      + "where clcomm=''").append( 
              getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM )).append( 
              "'' and CLAMGS=").append(dataC).append("') ) as sc \n"  +
              "on collo=clncol\n" +
              "group by CFDIVI ");
              
    
 /*select CDDITT,count(*) as cant from OPENQUERY(GMSPRIESS,'select * from GMSPriess.dbo.Scannermode where com=''291''')
inner join 
(select * from OPENQUERY(COLOM,'SELECT distinct clcomm,clncol,CDDITT  FROM mcobmoddta.scxxxcol where clcomm=''291''') ) as sc 
on collo=clncol
group by CDDITT*/
  
    
    
    return sql.toString();
  }
  
}
