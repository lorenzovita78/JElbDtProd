/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.carico;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryCaricoComFromTAP extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder("SELECT COUNT(*) FROM MCOBMODDTA.ZTAPCI WHERE 1=1").append(
                    " and ticono = " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
                    " and tiplgr = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA)).append(
                    " and tidtco = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(
                    " and ticomm = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM));
    
    
    
    return qry.toString(); //To change body of generated methods, choose Tools | Templates.
  }
   
}
