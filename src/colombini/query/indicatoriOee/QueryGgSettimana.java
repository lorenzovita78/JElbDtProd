/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che dato un giorno torna tutti i giorni della settimana e la settimana stessa
 * @author lvita
 */
public class QueryGgSettimana extends CustomQuery{

  public final static String FILTERANNO="FANNO";
  public final static String FILTERGG="FGIORNO";
  public final static String FILTERAZ="FAZIENDA";
  
  @Override
  public String toSQLString() throws QueryException {
    
    StringBuffer sql=new StringBuffer(
                    "select integer(cdymd8) datan ,integer(substr(cdywd5,3,2)) sett ,").append(
                            " integer(cdwdpc) as plav,integer(substr(cdymd8,5,2)) as mese ").append(
                    "  from mvxbdta.csycal40 ").append(
                    " where 1=1 ").append( 
                      " and substr(cdymd8,1,4)=").append(getFilterSQLValue(FILTERANNO)).append(
                      " and cddivi='' ").append(
                      " and substr(cdywd5,3,2)=  ").append(
                           " (SELECT substr(cdywd5,3,2) ").append(
                              " from mvxbdta.csycal40 ").append(
                             " where 1=1 ").append(   
                               " and cdcono=").append(getFilterSQLValue(FILTERAZ)).append(
                               " and cddivi='' ").append(   
                               " and cdymd8=").append(getFilterSQLValue(FILTERGG)).append(
                            " ) ").append(
                    " order by 1 ");
    
    return sql.toString();
  }
  
  
  
}
