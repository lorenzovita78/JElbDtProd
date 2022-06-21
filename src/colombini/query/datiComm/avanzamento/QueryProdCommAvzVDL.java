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
public class QueryProdCommAvzVDL extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
        StringBuilder qry=new StringBuilder();

      
    String campiConv=" avz.CommissionId , avz.TruckloadDate ";

    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   

    
    qry.append(" SELECT ").append(campiConv).append(" ,count(distinct avz.ColloID) \n").append(
            " FROM [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] as avz \n" ).append(
            " inner join (select MAX(SYSTEMDATE) dat from [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] group by ColloID) as DetMax on detMax.dat = SYSTEMDATE ").append(
            " left join (select colloId from [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] where Status>=200 " ).append(addAND(inStatement("ProductionLine",FilterFieldCostantXDtProd.FT_LINEA))).append(
            " and SYSTEMDATE<convert( datetime ,").append(dI).append(" ,120)) as storico on storico.ColloID=avz.ColloID ").append(
            " where 1=1 and ").append(
            " avz.area>0 ").append(addAND(inStatement("avz.ProductionLine",FilterFieldCostantXDtProd.FT_LINEA))).append(
            " and avz.SYSTEMDATE>=convert( datetime ,").append(dI).append(" ,120)").append(
            " and avz.SYSTEMDATE<=convert( datetime ,").append(dF).append(" ,120) and storico.ColloID is null");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_BU_DIVERSO)){
      qry.append(addAND(notInStatement("avz.ClientId",FilterFieldCostantXDtProd.FT_BU_DIVERSO)));                              
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_BU_UGUALE)){
      qry.append(addAND(inStatement("avz.ClientId",FilterFieldCostantXDtProd.FT_BU_UGUALE)));                              
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_SISTEMA)){
     qry.append(addAND(inStatement("avz.MFSystem",FilterFieldCostantXDtProd.FT_SISTEMA)));                                      
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_PERSONALIZZATO)){
    // qry.append(FilterFieldCostantXDtProd.FT_PERSONALIZZATO);                                      
    }
    
    qry.append(
            " group by ").append(campiConv);
           
    return qry.toString();
  }
}

