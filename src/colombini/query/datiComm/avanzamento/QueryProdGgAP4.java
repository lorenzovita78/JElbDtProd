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
 * Query per avanzamento produzione impianti ante piano 4 : Ante Quadrotto,Ante Scorrevoli
 * Fornisce i pz prodotti in un determinato giorno suddivisi per commessa
 * @author lvita
 */
public class QueryProdGgAP4 extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder();
    
    
    
    qry.append("select zhcomm, zhdtco, sum(int(zhflgc)) pzprod " ).append(
                " from mcobmoddta.zanp3h " ).append(
                " where zhcono=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
                " and zhlmdt=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(
                " and zhutrf=" ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_UTLINEA)).append(
                " and zhflgc=1 ").append(        
                " group by zhcomm, zhdtco ");       
            
    
    return qry.toString();
  }
}
