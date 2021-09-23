/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryPzHomagR1P1 extends CustomQuery{

    
  public final static String FT_DIM1_GT="FT_DIM1_GT";  
  public final static String FT_DIM1_LE="FT_DIM1_LE";  
    
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer q=new StringBuffer(" SELECT nCollo,clnart,linea, 0 as Box," ).append(
                                    " pedana,odv,0 rgO,ItemNo,Descrizione,CLSTRD,PanelID\n").append(
                                      " FROM   DesmosColombini.DBO.LDLMVX102_TXT_MACC_HOMAG_B\n").append(
                                   " where 1=1" );
    
    q.append(" AND DesmosLancio=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS));
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEE)){
      q.append(addAND(inStatement(" Linea ", FilterFieldCostantXDtProd.FT_LINEE))); 
    }
    
    if(isFilterPresent(FT_DIM1_LE)){
      q.append(addAND(leStatement("Dim1",FT_DIM1_LE)) ); 
    }
    
    if(isFilterPresent(FT_DIM1_GT)){
      q.append(addAND(gtStatement("Dim1", FT_DIM1_GT))); 
    }
    return q.toString();        
  }
  
  
  
}
