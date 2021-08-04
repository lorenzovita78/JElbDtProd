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
 * Query per impianti ante piano 4 : Ante Quadrotto,Ante Scorrevoli
 * Fornisce la lista dei pezzi da produrre e prodotti per una determinata commessa
 * @author lvita
 */
public class QueryProdCommAP4 extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder();
    
    
    
    qry.append("select  count(*) totpz , sum(int(zhflgc)) totprod " ).append(
                " from mcobmoddta.zanp3h " ).append(
                " where zhcono=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
                " and zhcomm=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                " and zhdtco=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(
                " and zhutrf=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_UTLINEA));
            
    
    return qry.toString();
  }
}
