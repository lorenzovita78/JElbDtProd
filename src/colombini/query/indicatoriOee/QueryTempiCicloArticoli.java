/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query per ottenere il tempo di lavorazione (tempo ciclo) per un determinato pantografo con un determinato articolo (padre)
 * @author lvita
 */
public class QueryTempiCicloArticoli extends CustomQuery{

  
  public final static String CODICEPADRE="CODICEPADRE";
  public final static String CENTRODILAVORO="CENTRODILAVORO";
  public final static String LISTACENTRI="LISTACENTRI";
  public final static String AZIENDA="AZIENDA";
  public final static String FACILITY="FACILITY";
  
  @Override
  public String toSQLString() throws QueryException {
     String codicepadre = null; 
     String listaPantografi = null;
     String azienda = null;
     String facility = null;
     StringBuilder qry=new StringBuilder();
     
    //    Filtri obbligatori 
    codicepadre=getFilterSQLValue(CODICEPADRE);
    azienda=getFilterSQLValue(AZIENDA);
    facility=getFilterSQLValue(FACILITY);
    
    //centrodilavoro=getFilterSQLValue(CENTRODILAVORO);
    if(isFilterPresent(LISTACENTRI)){
      listaPantografi=(String) getFilterValue(LISTACENTRI);
    }  
    
    qry.append(" select POPITI,PPPLGR ").append(
               " from MVXBDTA.MPDOPE,MVXBDTA.MPDWCT ").append(
               " where POPLGR=PPPLGR").append(
               " and POFACI=PPFACI").append(       
               "  and POCONO=").append(azienda).append(
                  " and POFACI=").append(facility).append(
                  " and POPRNO=").append(codicepadre);
    
    if(isFilterPresent(LISTACENTRI)){
      qry.append(" and POPLGR in ( ").append(listaPantografi).append(" )");
    } 
    
    if(isFilterPresent(CENTRODILAVORO)){
      String centrodilavoro=getFilterSQLValue(CENTRODILAVORO);
      qry.append(" and POPLGR=").append(centrodilavoro);
    }
    return qry.toString();
  }
  
  
  
  
}
