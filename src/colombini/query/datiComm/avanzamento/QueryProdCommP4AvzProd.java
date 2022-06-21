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
public class QueryProdCommP4AvzProd extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
        StringBuilder qry=new StringBuilder();

      
    String campiConv=" case when(len([Commessa])>3) then SUBSTRING(commessa,5,3) else Commessa end " +
                     "   , convert(varchar(8),[DataCommessa],112) ";

    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   

    
    qry.append(" SELECT ").append(campiConv).append(" ,count(distinct barcode) \n").append(
             " FROM [AvanzamentoProd].[dbo].[TBL_Avanzamento_Dett] \n" ).append(
             "  inner join [AvanzamentoProd].[dbo].[TBL_DatiProduzione] on barcode=partnumber").append(
             " where 1=1 ").append(
            " and centro=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA)).append(
            " and dataOra>=convert( datetime ,").append(dI).append(" ,120)").append(
            " and dataOra<=convert( datetime ,").append(dF).append(" ,120)").append(
            " group by ").append(campiConv);
           
    return qry.toString();
  }
}

// String campiConv=" case when(len([Commessa])>3) then SUBSTRING(commessa,5,3) else Commessa end " +
//                     "   , convert(varchar(8),[DataCommessa],112) ";
//
//    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
//    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   
//
//    
//    String sql=" SELECT "+campiConv+ " ,count(*) \n"
//            + " [dbo].[TBL_Avanzamento_Dett] \n" 
//            + "  inner join [AvanzamentoProd].[dbo].[TBL_DatiProduzione] on barcode=partnumber"
//            + "where 1=1 "
//            +" and centro='"+getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEA)+"'"
//            +" and dataInserimento>="+dI
//            +" and dataInserimento<="+dF
//            +" group by "+campiConv;
           