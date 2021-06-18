/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico;

import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.util.DatiCommUtils;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;


/**
 * 
 * @author lvita
 */
public class QryCoeffVolLinea extends CustomQuery{
  
 
  @Override
  public String toSQLString() throws QueryException {
    
    
    StringBuilder sub1=new StringBuilder();
    StringBuilder sub2=new StringBuilder();
    StringBuilder sql=new StringBuilder();
    
    sub1.append(" select zdcono, zdplgr, zddivi ").append(
                       ", sum(zdvale)/count(*) as ").append(FilterFieldCostantXDtProd.FD_COEFFM3).append(
            " from ").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(CaricoCommLineaBean.TABNAME).append(
            " where 1=1").append(
            " and zdcono=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
            " and zdunme=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_UNMIS)).append(
            " and zddtco between ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA)).append(
            " and ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA)).append(
            " group by zdcono,zdplgr,zddivi");
            
    sub2.append(" select zlcono,zlplgr,zlunme,zlnopt,zlnors,ZLNTRN,zlcado ").append(
                " from ").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(DatiCommUtils.TABDTLINEELAV).append(
                " where 1=1").append(
                " and zlcono=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA));
            
   sql.append(" SELECT ZDPLGR AS ").append(FilterFieldCostantXDtProd.FD_CODLINEA).append(
              ", ZDDIVI AS ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
              ", ").append(FilterFieldCostantXDtProd.FD_COEFFM3).append(
              ", ZLUNME AS ").append(FilterFieldCostantXDtProd.FD_UNMIS).append(
              ", ZLNOPT AS ").append(FilterFieldCostantXDtProd.FD_NUMOPERTURNO).append(
              ", ZLNORS AS ").append(FilterFieldCostantXDtProd.FD_NUMORESTD).append(
              ", ZLNTRN AS ").append(FilterFieldCostantXDtProd.FD_NUMTURNI).append(
              ", ZLCADO AS ").append(FilterFieldCostantXDtProd.FD_CADORARIA).append(
              " from ( ").append(sub1).append(" ) a").append(
                    ", ( ").append(sub2).append(" ) b").append(
              " where zdplgr=zlplgr  and  zdcono=zlcono").append(      
            " order by zdplgr,zddivi");

    
    return sql.toString();        
  }
  
  
  
}
