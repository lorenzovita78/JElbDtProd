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
 * Query per impianti ante piano 4 : Lotto1 e Combicut
 * Fornisce la lista dei pezzi da produrre e prodotti per una determinata commessa
 * @author lvita
 */
public class QueryProdCommAvzComandiUtente extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
        StringBuilder qry=new StringBuilder();

      
    String campiConv=" CommissionId , TruckloadDate ";

    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   

    
    qry.append(" SELECT ").append(campiConv).append(" ,count(distinct ColloID) \n").append(
             " FROM [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] \n" ).append(
             "  inner join (select MAX(SYSTEMDATE) dat from [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] group by ColloID) as DetMax on detMax.dat = SYSTEMDATE ").append(
             " where 1=1 and ").append(
            " Status>=200 ").append(addAND(inStatement("ProductionLine",FilterFieldCostantXDtProd.FT_LINEA))).append(
            " and SYSTEMDATE>=convert( datetime ,").append(dI).append(" ,120)").append(
            " and SYSTEMDATE<=convert( datetime ,").append(dF).append(" ,120) ");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_BU_DIVERSO)){
      qry.append(addAND(notInStatement("ClientId",FilterFieldCostantXDtProd.FT_BU_DIVERSO)));                              
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_BU_UGUALE)){
      qry.append(addAND(inStatement("ClientId",FilterFieldCostantXDtProd.FT_BU_UGUALE)));                              
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_SISTEMA)){
     qry.append(addAND(inStatement("MFSystem",FilterFieldCostantXDtProd.FT_SISTEMA)));                                      
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_PERSONALIZZATO)){
    // qry.append(FilterFieldCostantXDtProd.FT_PERSONALIZZATO);                                      
    }
    
    qry.append(
            " group by ").append(campiConv);
           
    return qry.toString();
  }
}

