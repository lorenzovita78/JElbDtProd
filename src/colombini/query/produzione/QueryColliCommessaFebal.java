/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryColliCommessaFebal extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder(
            " select distinct  Codice_collo,0 as numart,linea,ltrim(rtrim(box)),ltrim(rtrim(pedana)),rtrim(ltrim(NumeroOrdine)),\n ").append( 
                             " 0 as rigaordine,articolo,descrizioneArticolo ,descrizioneArticolo \n").append(
                             //",NumeroCollo,Quantita  ").append(
             " from  DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL").append(
             " where 1=1 and articolo not like '%EL%' ");
 
    //Aggiunto filtro --> and articolo not like '%EL%' --> Richiesto dalla Jessica P. A questa linea non li serve vedere l'eletrodomestici
    
    qry.append(addAND(eqStatement("commessa", FilterQueryProdCostant.FTNUMCOMM)));
    qry.append(addAND(eqStatement("dataSpedizione", FilterQueryProdCostant.FTDATACOMMN)));
    qry.append(addAND(inStatement("linea", FilterQueryProdCostant.FTLINEELAV)));
                     
    
    
    qry.append(" order by codice_collo");
    
    return qry.toString();
  } 
  
  
  
}
