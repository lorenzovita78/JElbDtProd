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
public class QryNPziMawFianchi extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String condLinea=ClassMapper.classToString(getFilterValue(FilterFieldCostantXDtProd.FT_LINEE));
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    String tabZpdsum=FilterFieldCostantXDtProd.getNomeTabDistintaCommessa(numCom);
    String tabMpdCdm=FilterFieldCostantXDtProd.getNomeTabMisDisegnoCommessa(numCom);
    
    StringBuilder sql=new StringBuilder();
    sql.append("select  cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " ,sum(clqta) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
               " from ").append(tabCommessa).append(
               " , ").append(tabZpdsum).append(" , ").append(tabMpdCdm).append(
               " , ").append(FilterFieldCostantXDtProd.TABFACILITY).append(
               " where 1=1").append(
               " and CLNART<>0 ").append(
               condLinea).append(
            //               " and CLLINP IN ('06022','06023', '06024', '06025','06029') ").append(
               " and CLAMGS=").append(dataC).append(
               " and pscono=").append(azdefault).append(
               " and clnror=psridn ").append(
               " and clriga=psridl ").append(
               " and clncol=pselno ").append(
               " and psrlev=1 ").append(
               " and cfcono=pscono ").append(
               " and cffaci=psfcco ").append(
               " and qkcono=cfcono ").append(
               " and qkcfin=pscfin ").append(
               " and QKDMID ='DM015' "); //serve per scartare le schiene,ecc-->prende solo i fianchi
            
    
    //group by e order by
    sql.append("\n group by cfdivi").append(
               " order by cfdivi");
    
    
    
    return sql.toString();
  }
  
  
  
}
