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
public class QryPzAnteForBiesseP3 extends CustomQuery{

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder q=new StringBuilder();
    
    String select=//" select distinct Codice_Collo,0 as nart,LineaDest,idBox,Pedana,Cod_Ordine,num_riga,BarCodart,substring(descrizione,1,30),descrizione";
                  " select distinct codice_collo, LineaDest,IdBox,Pedana,cod_ordine,num_riga,Codice,substring(def_comp,1,30) as descrArt,def_comp, coalesce(cid+'.CID','0') as cid";
    
    
    String db=" DesmosFebal.dbo.";
    
    String where=" where DesmosLancio ="+getFilterSQLValue(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS)+
                 " and (cid<>'0' and cid not like '%---%') ";
    
    q.append("select codice_collo,ROW_NUMBER() over (partition by codice_collo order by codice_collo) as numart, LineaDest,IdBox,Pedana,cod_ordine,num_riga,Codice,descrArt,def_comp,cid ");
    q.append(" from ( \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK00_LST_ETIC_PDF_ANTECAM_LL_NEW_ACQ ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK00_LST_ETIC_PDF_ANTECAM_LL_NEW_PROD ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK00_LST_ETIC_PDF_ANTECAM_LL_NEW_VT ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK_LST_ETIC_PDF_ANTECAM_CLSQ_V4 ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK_LST_ETIC_PDF_ANTECAM_CLSQ_V4_SMON ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK_LST_ETIC_PDF_ANTECAM_SMON_ACQ ");
    q.append(where);
    q.append("\n UNION \n");
    q.append(select);
    q.append(" from ").append(db).append("ETK00_LST_ETIC_PDF_ANTECAM_LL_NEW_PROD3 ");
    q.append(where);
    q.append("\n  ) a ");
    
    return q.toString();
    
  }
 
  
  
}
