/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryNClStrettoi extends CustomQuery{
  
  public final static String STRETTOIO="STRETTOIO";
  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String condStrettotio=(String) getFilterValue(STRETTOIO);
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    
    StringBuilder sql=new StringBuilder();
    sql.append(" select oadivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                 " ,count (*) as ").append(FilterFieldCostantXDtProd.FD_NUMCOLLI).append(
                 " from ( ").append(
                   " select distinct oadivi, clncol ").append(
                      " from ").append(tabCommessa).append(
               " , ").append(Connections.getInstance().getLibraryStdAs400()).append(".OOHEAD").append(
               " , ").append(Connections.getInstance().getLibraryStdAs400()).append(".MITMAD").append(
               " where 1=1").append(
               " and CLNART<>0 ").append(
               " and CLAMGS=").append(dataC).append(
               " and oacono = ").append(azdefault).append(                  
               " and mmcono=oacono").append(
               " and clnror=oaorno").append(
               " and cllinp in ('06030','06031','06033','06034','06039') ").append(
               " and clarti=mmitno").append(
               condStrettotio);        
    
      sql.append(" )  a");
    
    //group by e order by
    sql.append("\n group by oadivi").append(
               " order by oadivi");
    
    
    return sql.toString();
  }
  
}
