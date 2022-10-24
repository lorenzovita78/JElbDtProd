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
public class QueryProdCommAvzPriessOne extends CustomQuery {
 
  @Override
  public String toSQLString() throws QueryException {
        StringBuilder qry=new StringBuilder();

      
    String campiConv=" pro.commessa,count(*) as quantità ";

    String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);   
    String dF=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);   

    
    qry.append(" SELECT ").append(campiConv).append(
             " from GMSPriess.GMSPriess.dbo.Prodotto as pro\n" +
             "left join (select IdProdotto,commessa,collo,Side from GMSPriess.GMSPriess.dbo.Prodotto where DataUscita is not null and \n" ).append(
            " dataUscita<= CONVERT(DATETIME,").append(dI).append(", 102)").append(
            " ) as sto on sto.IdProdotto=pro.IdProdotto and sto.commessa=pro.commessa and sto.collo=pro.collo and sto.Side=pro.Side ").append(
            " where pro.DataUscita is not null and pro.dataUscita>= CONVERT(DATETIME," ).append(dI).append(", 102)").append(
            "  and pro.dataUscita<=CONVERT(DATETIME," ).append(dF).append(", 102) and sto.IdProdotto is null group by pro.commessa");
    

    return qry.toString();
  }
}

