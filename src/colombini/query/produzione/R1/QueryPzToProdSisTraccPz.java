/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryPzToProdSisTraccPz extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder s=new StringBuilder(" select  TRIM(TIBARP) as TIBARP, ").append(
                                   " TICOMM, TINCOL, TINART, trim(TISTRD) as descrART, TILINP ").append(
                                   " from MCOBMODDTA.ZTAPCI left outer join   ").append(
                                   " \n  (  select TPIDPZ,TPBARP,TPDTIN  from mcobmoddta.ztapcp  \n ").append(
                                                 " where tputin in ( select tuutrf from mcobmoddta.ztapcu where tuplgr=").append(
                                                 getFilterSQLValue(FilterQueryProdCostant.FTCDL)).append("  )   ) A ").append(
                                     " on tibarp=tpbarp ").append(
                                     " where 1=1 ").append(
                                     " and tiplgr=").append(getFilterSQLValue(FilterQueryProdCostant.FTCDL)).append( 
                                     " and TIDTCO>=").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN));
    
    
    
    if(isFilterPresent(FilterQueryProdCostant.FTPZPROD)){
      s.append(" AND TPIDPZ is  not null");
    }else if(isFilterPresent(FilterQueryProdCostant.FTPZNONPROD)){
      s.append(" AND TPIDPZ is null");
    }
     
    
    s.append(" group by TIBARP,TICOMM, TINCOL, TINART, TIDART, TISTRD ,TILINP ").append(
             " order by TICOMM  ");
                                             
                                                 
    
    
    
    
    return s.toString(); 
  }

  @Override
  public String[] getFieldCaptions() {
    String [] captions=new String[]{"Barcode","Commessa","N.Collo","N.Articolo","Descr.Articolo","LineaLogica"};
    
    return captions;
  }
 
  
  
  
//  private List getListColumnsHeader(){
//    List columns=new ArrayList();
//    
//    columns.add("BARCODE");
//    columns.add("COMMESSA");
//    columns.add("NCOLLO");
//    columns.add("NARTICOLO");
//    columns.add("DESCRARTICOLO");
//    columns.add("LINEALOG");
//    
//    
//    return columns;
//    
//  }

    
  
}
