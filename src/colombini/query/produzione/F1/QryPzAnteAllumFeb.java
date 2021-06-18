/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.F1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryPzAnteAllumFeb extends CustomQuery{

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String select=" select distinct Codice_Collo,0 as nart,LineaDest,idBox,Pedana,Cod_Ordine,num_riga,BarCodart,substring(descrizione,1,30),descrizione";
      
    String db=" DesmosFebal.dbo.";
    
    String where=" where Commessa ="+getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    
    q.append(select);
    q.append(" from ").append(db).append("ETKLNA00_LISTA_ETICHETTE_PDF_ETK_LIN5_A5 ");
    q.append(where);
    q.append(" UNION ");
    q.append(select);
    q.append(" from ").append(db).append("ETKLNA00_LISTA_ETIC_VD_PDF_ETK_LL_A5 ");
    q.append(where);
    q.append(" UNION ");
    q.append(select);
    q.append(" from ").append(db).append("ETKLNA00_LISTA_ETIC_PDF_ETK_MOB_ACQ_A5 ");
    q.append(where);
    
    
//    //NEW per Artec in Febal 
//    q.append(" UNION ");
//    q.append(" select Codice_Collo , ROW_NUMBER() OVER(partition by Codice_Collo  order by Codice_Collo) , linea,idBox,Pedana,Odv,num_riga,BarCodart,descBreve ,descCodicePadre " ).append(
//              "        from (\n").append(
//                " SELECT distinct Codice_Collo,1 as nart ,DesmosDestinatario as linea,idBox,Pedana,Odv,num_riga,BarCodart,substring(descCodicePadre,1,30) descBreve ,descCodicePadre").append(
//                "  from ").append(db).append("[ETK32_PDF_ETK_ARTEC_A5_AS]").append(
//                where).append(" ) a" );
    
    return q.toString();
    
  }
 
  
  
}
