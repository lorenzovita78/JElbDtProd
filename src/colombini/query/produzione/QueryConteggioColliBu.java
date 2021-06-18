/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che data una data di spedizione torna il numero totale di colli .
 * La query permette di filtrare per commessa,minicommessa e nanocommessa
 * E' possibile filtrare anche per DIVISIONE
 * 
 * @author lvita
 */
public class QueryConteggioColliBu extends CustomQuery {
  
  public final static String FILTERLISTBU="FLISTBU";
  public final static String FILTERBU="FILTERBU";
  public final static String FILTERCOLLIALLCOMM="FILTERCOLLIALLCOMM";
  
  public final static String FILTERCOLLICOMM="FILTERCOLLICOMM";
  public final static String FILTERCOLLIMINICOMM="FILTERCOLLIMINICOMM";
  public final static String FILTERCOLLINANOCOMM="FILTERCOLLINANOCOMM";
  
  public final static String FILTERTYPEORD="FILTERTYPEORD";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder query=new StringBuilder();
    
    String select ="SELECT OACONO,OADIVI,OAORNO,ORPANR, ORVOM3 , case OAOPRI when '0' then 1 when '7' then 2 else 0 end OAOPRI";
    String from=" FROM MVXBDTA.DCONSI,MVXBDTA.DOHEAD,MVXBDTA.OOHEAD";
    String from1=from +" ,MVXBDTA.MDNHEA,MVXBDTA.MPTRNS";
    String from2=from +" ,MCOBMODDTA.ZMDNHEA,MCOBMODDTA.ZMPTRNS";
    
    
    StringBuilder where = new StringBuilder (" WHERE 1=1 ").append(
            " AND DACONO=EACONO ").append(
            " AND DACONN=EACONN ").append(
            " AND EACONO=OACONO ").append(
            " AND EAORNO=OAORNO ").append(
            " AND OACONO=OQCONO ").append(
            " AND OAORNO=OQRIDN ").append(
            " AND OQCONO=ORCONO ").append(
            " AND OQWHLO=ORWHLO ").append(
            " AND OQDNNO=ORDNNO ");
    
   
    where.append(addAND(eqStatement("DACONO", FilterQueryProdCostant.FTAZIENDA)));
    where.append(addAND(eqStatement("DACDLD", FilterQueryProdCostant.FTDATACOMMN)));
    
    
    
     // nel caso si vogliano considerare tutti i colli di tutte le commessa della giornata (comm+mini+nano) 
     // allora non serve applicare nessun filtro alla priorità dell'ordine
      
    if(isFilterPresent(FILTERCOLLICOMM)){
      //tolgo ordini delle minicommesse e nanocommesse 
      where.append(" AND OAOPRI not in ( '0', '7' )");
    }else if (isFilterPresent(FILTERCOLLIMINICOMM)) {
      where.append(" AND OAOPRI='0'");
    } else if (isFilterPresent(FILTERCOLLINANOCOMM)){
      where.append(" AND OAOPRI='7'");
    }
    
    //considero solo bolle da ordini (OQRORC=5 sarebbero i trasferimenti da galazzano 
    where.append (" AND OQRORC=3 ");
    
    if(isFilterPresent(FILTERLISTBU)){
      where.append(addAND(inStatement("OADIVI", FILTERLISTBU)));
    }else if (isFilterPresent(FILTERBU) ){
      where.append(addAND(eqStatement("OADIVI", FILTERBU)));
    }
    
    String selectG="SELECT OADIVI as DIVI, count(*) NCOLLI , cast (sum (ORVOM3) as dec(10,3) ) as VOL ";
    String groupByG=" group by oadivi ";
    if(isFilterPresent(FILTERTYPEORD)){
      selectG+=" ,  OAOPRI";
      groupByG+=" , OAOPRI";        
    }
    
    
      query.append(selectG).append("  FROM ( ").append(
                   select).append(from1).append(where).append(" \n  UNION  \n " ).append(
                   select).append(from2).append(where).append(
                 " ) A ").append(groupByG); 
    
    return query.toString();
    
  }
  
  
  
}
