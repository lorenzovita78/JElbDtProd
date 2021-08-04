/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per sapere il volume ,e il numero di colli di una determinata commessa
 * volendo anche per 
 * @author lvita
 */
public class QryNumPezziCommessaStd extends CustomQuery {

  
  
  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    String tabZpdsum=FilterFieldCostantXDtProd.getNomeTabDistintaCommessa(numCom);
//    String tabMpdCdm=FilterFieldCostantXDtProd.getNomeTabMisDisegnoCommessa(numCom);
    
    
    StringBuilder sql=new StringBuilder();
    sql.append("select  cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " ,sum(CLQTA) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(  //count(*) 
               " from ").append(tabCommessa).append(
               " , ").append(tabZpdsum).append(//" , ").append(tabMpdCdm).append(
               " , ").append(FilterFieldCostantXDtProd.TABFACILITY).append(
               " where 1=1").append(
               " and CLNART<>0 ").append(
               " and CLAMGS=").append(dataC).append(
               " and pscono=").append(azdefault).append(
               " and clnror=psridn ").append(
               " and clriga=psridl ").append(
               " and clncol=pselno ").append(
               " and psrlev=1 ").append(
               " and cfcono=pscono ").append(
               " and cffaci=psfcco ");
    
    
   if(isFilterPresent(FilterFieldCostantXDtProd.FT_CONDLINEA)){
      String condizioneLinea=(String) getFilterValue(FilterFieldCostantXDtProd.FT_CONDLINEA);
      sql.append("\n ").append(condizioneLinea);  
    }
   
    //group by e order by
    sql.append("\n group by cfdivi").append(
               " order by cfdivi");         
            
    
    
    return sql.toString();
  }
  
  
  
  
}
