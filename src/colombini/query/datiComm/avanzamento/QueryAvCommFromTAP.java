/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.avanzamento;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query che fornisce la qta dei pezzi prodotti di una determinata commessa
 * prelevando le informazioni dal sistema di spunta dei pz/colli del portale (TAP)
 * @author lvita
 */
public class QueryAvCommFromTAP extends CustomQuery{

 
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer qry=new StringBuffer();
    
    
    
    qry.append("select count(tibarp) npztot ,count(tpbarp) npzprod \n " ).append(
              " from \n" ).append(
              "( Select distinct ticomm,tidtco,tibarp,tpbarp \n" ).append(
              " from\n" ).append(
              " ( select distinct ticomm,tidtco,tibarp\n" ).append(
                  " from mcobmoddta.ztapci\n" ).append(
                  " where 1=1 \n").append(
                  " and ticono = " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
                  " and tiplgr = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA)).append(
                  " and tidtco = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(
                  " and ticomm = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                  ") a left outer join \n").append(
              "( select tpbarp\n" ).append(
                 " from mcobmoddta.ztapcp, \n" ).append(
  "    (select distinct tuutrf as utente from mcobmoddta.ztapcu ").append(
               " where tuplgr=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA)).append(
              " ) b \n" ).append(
" where 1=1\n" ).append(
" and tputin=utente\n" ).append(
" and tpdtin >= (current timestamp  - 40 days)\n" ).append(
") c\n" ).append(
"on  a.tibarp=c.tpbarp ) comm");
    
    
    
    
    return qry.toString();
  }
  
  
  
}
