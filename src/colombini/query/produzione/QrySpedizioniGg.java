/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che torna alcune informazioni : numero spedizione,ora spedizione,minuti spedizione
 * delle spedizioni di mobili attive in un determinato giorno
 * @author lvita
 */
public class QrySpedizioniGg extends CustomQuery{

  //filtro per indicare di visualizzare solo le spedizioni di colli , non quelle interne
  public final static String SPEDMOB="SPEDMOB";
  
  @Override
  public String toSQLString() throws QueryException {
    String qry=" select daconn,dadsdt,dadeth,dadetm,daardt,dagrw1,davol1 \n" +
               " from mvxbdta.dconsi \n" +
               " where dacono= "+getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA)+
               " and dadsdt="+getFilterSQLValue(FilterQueryProdCostant.FTDATARIF);
    
    
    if(isFilterPresent(SPEDMOB))
      qry+=" and dasdes='RSM'";
            
    qry+=" order by dadeth,dadetm";
    
    return qry;
  }
  
  
  
}
