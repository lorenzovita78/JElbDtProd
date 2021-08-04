/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryDtSortingStationImaAnte extends CustomQuery{

  @Override
  public String toSQLString() throws QueryException {
 
    StringBuffer qry=new StringBuffer(
            " select t1.commissionno, t1.box as box,t2.platznr as placeNumber, t1.Itemno as itemNo, ").append(
              " case when (substring(ltrim(str(t2.platznr)),1,2))='20' then 'CRANE1' ").append(
              " when (substring(ltrim(str(t2.platznr)),1,2))='22' then 'CRANE2' ").append(
              " when (substring(ltrim(str(t2.platznr)),1,2))='24' then 'CRANE3' ").append(
              " when (substring(ltrim(str(t2.platznr)),1,2))='26' then 'CRANE4' ").append( 
              " when (substring(ltrim(str(t2.platznr)),1,2))='28' then 'CRANE5' ").append(
              " else 'PILE A TERRA' END as crane, ").append(
              " COUNT(*) num ").append(
            " from tab_et t1 join tab_lagen t2 on  t1.barcode = t2.barcode ").append(
            " where (t2.PlatzNr BETWEEN 201101 AND 299999) ").append(
            " group by t1.commissionno, t1.box, t1.Itemno, t2.platznr");
    
    return qry.toString();
  }
  
  
  
}
