/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.F1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryPzAnteAllumDesmosPortale extends CustomQuery{
public final static String isFebal="isFebal";
public final static String numArt1="numArt1";
public final static String barcodeSenzaArt="barcodeSenzaArt";
public final static String filtroFor="filtroFor";
public final static String Classe_M="40";


  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String select="select Codice_Collo,NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa ";
          
    String db="DesmosColombini.dbo.";
    if(isFilterPresent(isFebal)){
         db="DesmosFebal.dbo.";}

    StringBuilder cond=new StringBuilder(" where 1=1 and DesmosLancio =" +getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS) + "\n");
    
    //if(isFilterPresent(isFebal) || isFilterPresent(numArt1)){
    if(isFilterPresent(isFebal) || isFilterPresent(numArt1) ){
        select="select Codice_Collo,0 NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa ";
    } 
    
    if(isFilterPresent(filtroFor)){
        select="select Codice_Collo,NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa ";
    }
    
    q.append(select);
    q.append(" from ").append(db).append("desmosPortale ");
    q.append(cond);
    q.append(addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV)));
    if(isFilterPresent(numArt1)){
        q.append(" AND NumArticolo = 1");
    }
    
    if(isFilterPresent(filtroFor)){
        q.append(" AND foratrice <> ''");
        
        //Modifica per prendere i pezzi non forati sulla 36090 di febal (Gaston 13-04-2022)
        StringBuilder pezziNonFor=new StringBuilder("  UNION  select NonForati.Codice_Collo,0 NumArticolo, linea, box, pedana, NumOrdine,RigaOrdine,CodArticolo,DescrizioneArticolo,DescrizioneArticoloEstesa \n" 
          + (" from ") + db + ("desmosPortale as NonForati \n") 
          + (" left join (select distinct Codice_Collo,NumArticolo from ") + db + ("desmosPortale where foratrice <> '' and DesmosLancio=") + getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS) +  addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV)) + " ) as pezziForati on pezziForati.Codice_Collo=NonForati.Codice_Collo \n"
          + (" where pezziForati.Codice_Collo is null and NonForati.DesmosLancio=") + getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS) + addAND(inStatement("NonForati.linea", FilterQueryProdCostant.FTLINEELAV)) + " and Classe_Merceologica='"+Classe_M+"'"
        );
                
        q.append(pezziNonFor);
    }
    return q.toString();
    
  }
  
}
