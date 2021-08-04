/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.carico;

import colombini.costant.CostantsColomb;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryCommesseCoeff extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
     StringBuilder s=new StringBuilder();
     s.append(" SELECT zddtco,zdcomm,trim(zlplgr),zlunme,zddivi,zdvale ,zlplde").append(
             " FROM ").append( Connections.getInstance().getLibraryPersAs400() ).append(" .ZDPCOM ").append(
              " INNER JOIN ").append( Connections.getInstance().getLibraryPersAs400() ).append(" .ZDPLLC ").append(
              " ON zdcono=zlcono and zdplgr=zlplgr").append(        
             " WHERE 1=1 ").append(
             //" and zlfact='R1' and zlplan='P1' ").append(        
             " and zdcono=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
             " and zddtco >=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA)).append(
             " and zdunme='V3'").append(
             " and zdcomm <=").append(CostantsColomb.NUMMAX_COMMESSA).append(
             " and zddivi<>'RS1'");
     
               
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATAA) ) {
      s.append(" and zddtco <=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA));
    } 
     
     
    s.append(" order by zdplgr,zddtco,zdcomm,zddivi ");                 
     
     
     return s.toString();
  }
  
  
  
}
