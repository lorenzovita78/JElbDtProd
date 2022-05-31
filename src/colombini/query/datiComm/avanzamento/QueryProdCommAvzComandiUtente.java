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

      
    String campiConv=" Max(SUBSTRING(Lancio,1,3)) ";

    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   

    
    qry.append(" SELECT ").append(campiConv).append(" ,count(distinct Lancio) \n").append(
             " FROM [DesmosColombini].[dbo].[COMANDI_UTENTE] \n" ).append(
             "  where NomeBatch like '%DesmosVediCollo%' and len(ltrim(rtrim(Lancio)))=8\n ").append(
             addAND(inStatement("Utente",FilterFieldCostantXDtProd.FT_LINEA))).append(
            " and cast(REPLACE(DataOraInserimento,'.',':')as DATETIME)>=").append(dI).append(
            " and cast(REPLACE(DataOraInserimento,'.',':')as DATETIME)<=").append(dF).append(
            " and lancio not in (select lancio FROM [DesmosColombini].[dbo].[COMANDI_UTENTE] \n" ).append(
            "where NomeBatch like '%DesmosVediCollo%' and len(ltrim(rtrim(Lancio)))=8\n ").append(
             addAND(inStatement("Utente",FilterFieldCostantXDtProd.FT_LINEA))).append(
            " and cast(REPLACE(DataOraInserimento,'.',':')as DATETIME)<=").append(dI).append(")");
    
 
    
    qry.append(
            " group by ").append("SUBSTRING(Lancio,1,3)");
           
    return qry.toString();
  }
}

