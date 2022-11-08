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
              " from LDLMVX105_TXT_PRIESS_LISTA1" + " inner join (select * from OPENQUERY(COLOM,'SELECT SUBSTR(cffacn, 21, 1) as diviL,CFDIVI FROM mcobmoddta.ZPILCOM')) as d on d.DIVIL=BU ").append(
              " where DesmosCommessa='").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM )).append("' group by CFDIVI");    

 /*select CDDITT,count(*) as cant from OPENQUERY(GMSPRIESS,'select * from GMSPriess.dbo.Scannermode where com=''291''')
inner join 
(select * from OPENQUERY(COLOM,'SELECT distinct clcomm,clncol,CDDITT  FROM mcobmoddta.scxxxcol where clcomm=''291''') ) as sc 
on collo=clncol
group by CDDITT*/
  
    
    
    return sql.toString();
  }
  
}
