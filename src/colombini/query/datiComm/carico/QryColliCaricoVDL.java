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
 * volendo anche per linea
 * @author lvita
 */
public class QryColliCaricoVDL extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    //String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    String TableColli="[AvanzamentoVDL].[dbo].[V2H_ColloInfo_detail]";
    String TableDivi="[AvanzamentoVDL].[dbo].[Divisioni]";
    
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    //FT_SISTEMA

    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    
    StringBuilder sql=new StringBuilder();
    sql.append(" select DIVI as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                 " ,count(distinct colloid) as ").append(FilterFieldCostantXDtProd.FD_NUMCOLLI).append(
               " FROM ").append(TableColli).append(
               " INNER JOIN ").append(TableDivi).append( " on  ClientId=DIVI_COD").append(
               " where 1=1").append(
               " and CommissionId=").append(numCom);
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_CONDLINEA)){
      String condizioneLinea=(String) getFilterValue(FilterFieldCostantXDtProd.FT_CONDLINEA);
      sql.append("\n ").append(condizioneLinea);
    }
   
     if(isFilterPresent(FilterFieldCostantXDtProd.FT_SISTEMA)){
      String condizioneSist=(String) getFilterValue(FilterFieldCostantXDtProd.FT_SISTEMA);
      sql.append("\n ").append(condizioneSist); 
    }
    
    
    //group by e order by
    sql.append("\n group by DIVI").append(
               " order by DIVI");
    
    
    return sql.toString();
  }
  
  
}
