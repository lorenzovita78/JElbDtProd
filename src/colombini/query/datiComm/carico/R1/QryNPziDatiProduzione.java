/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class QryNPziDatiProduzione extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    //String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String condLinea=ClassMapper.classToString(getFilterValue(FilterFieldCostantXDtProd.FT_LINEE));
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    //
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
//    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
//      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
//    

    
    StringBuilder sql=new StringBuilder();
    sql.append("SELECT divi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " ,count(*) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
               " from ").append(
               "  [DesmosColombini].[dbo].[DatiProduzione]").append(
               "inner join  (select * from openquery(COLOM,'select CFFACI as faci,CFDIVI as divi from mvxbdta.cfacil') ) as divi on divi.faci=BU" ).append( 
               " where 1=1 ").append(
               condLinea).append(
               " and SUBSTRING(commessa, len(commessa)-2, 3) like '%").append(numCom).append("' and bu is not null "
                + " and datacommessa='").append(dataC).append("' ");
               
            
    
    //group by e order by
    sql.append("\n group by divi").append(
               " order by divi");
    
    
    
    return sql.toString();
  }
  
  
  
}
