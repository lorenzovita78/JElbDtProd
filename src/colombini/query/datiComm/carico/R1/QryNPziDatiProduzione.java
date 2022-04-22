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
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    String tabZpdsum=FilterFieldCostantXDtProd.getNomeTabDistintaCommessa(numCom);
    String tabMpdCdm=FilterFieldCostantXDtProd.getNomeTabMisDisegnoCommessa(numCom);
    
    StringBuilder sql=new StringBuilder();
    sql.append("SELECT cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " ,count(*) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
               " from ").append(
               "  [AvanzamentoProd].[dbo].[TBL_DatiProduzione]").append(
               " inner join [DBDESMOS].[MVX2DESMOS].[dbo].[SCxxxCOL] on CLNROR=odv COLLATE SQL_Latin1_General_CP1_CI_AS and clncol=collo COLLATE SQL_Latin1_General_CP1_CI_AS").append(
               " inner join openquery(COLOM,'select cfdivi,SUBSTR (cffacn, 21, 1) as let from mcobmoddta.ZPILCOM') as bu on bu.let=cdditt COLLATE SQL_Latin1_General_CP1_CI_AS").append(                      
               " where 1=1").append(
               " and CLNART=0 ").append(
               condLinea).append(
               " and commessa='").append(numCom).append("'");
               
            
    
    //group by e order by
    sql.append("\n group by cfdivi").append(
               " order by cfdivi");
    
    
    
    return sql.toString();
  }
  
  
  
}
