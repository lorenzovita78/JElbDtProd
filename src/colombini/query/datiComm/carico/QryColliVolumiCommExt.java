/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per sapere il volume ,e il numero di colli di una determinata commessa
 * relativa ad ordini estero
 * @author lvita
 */
public class QryColliVolumiCommExt extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    
    
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    
    StringBuilder sql=new StringBuilder();
    sql.append(" select oadivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                 " ,count(*) as ").append(FilterFieldCostantXDtProd.FD_NUMCOLLI).append(
                 " ,sum(clvom3) as ").append(FilterFieldCostantXDtProd.FD_VOLUME).append(
               " from ").append(tabCommessa).append(
               " , ").append(Connections.getInstance().getLibraryStdAs400()).append(".OOHEAD").append(
               " , ").append(Connections.getInstance().getLibraryStdAs400()).append(".OCUSMA").append(
               " where 1=1").append(
               " and CLNART=0 ").append(
               " and CLAMGS=").append(dataC).append(
               " and oacono=").append(azdefault).append(
               " and clnror=oaorno").append(
               " and oacuno=okcuno" ).append(
               " and (okcfc4 <>'ITA'").append(
                      " and okcfc4 <>'RSM' )");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_CONDLINEA)){
      String condizioneLinea=(String) getFilterValue(FilterFieldCostantXDtProd.FT_CONDLINEA);
      sql.append("\n ").append(condizioneLinea);  
    }
    //group by e order by
    sql.append("\n group by oadivi").append(
               " order by oadivi");
    
    
    return sql.toString();
  }
  
  
}
