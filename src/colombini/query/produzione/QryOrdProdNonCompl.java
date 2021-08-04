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
public class QryOrdProdNonCompl extends CustomQuery {

  public final static String FTSTATONOTCOMPLETE="FTSTATONOTCOMPLETE";
  
  @Override
  public String toSQLString() throws QueryException {
    
    StringBuilder qrOrdFaseComplete=new StringBuilder();
    StringBuilder qrOrdCentroNnComplete=new StringBuilder();
    
    StringBuilder s1=new StringBuilder(" select vhcono, vhfaci,vhprno,vhmfno,vhorty,vhwhlo,vhwhsl,vhschn,vhorqt,vowost,voplgr,voopno ").append(
            " from mvxbdta.mwohed inner join mvxbdta.mwoope on  vhcono=vocono and vhfaci=vofaci and vhprno=voprno and vhmfno=vomfno ").append(
             " where 1=1 ").append("\n and vhcono=").append(getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA));
                     
    
    if(isFilterPresent(FilterQueryProdCostant.FTMAGAZ)){
      s1.append(" and vhwhlo =").append(getFilterSQLValue(FilterQueryProdCostant.FTMAGAZ));
    }
    
    if(isFilterPresent(FTSTATONOTCOMPLETE)){
      s1.append("and VHWHST<>'90' ");
    }
    
    //ordini non completi con operazioni completate
     qrOrdFaseComplete.append(s1).append(
             " and voplgr<>'01000' ").append(
             " and vowost=90 ");
                                
     //ordini non completi con operazioni di un determinato centro non completati
     qrOrdCentroNnComplete.append(s1).append(
             " and voplgr = ").append(getFilterSQLValue(FilterQueryProdCostant.FTCDL)).append(
             " and vowost<>90 ");
     
     
     
     // da verificare se fare un ' estrazione con più campi
     String qryFinal=String.valueOf(" select sum(qta) from \n" +
             "(select  a.vhmfno,int(max(a.vhorqt) ) as qta from  ( "
             + qrOrdFaseComplete + " ) a  INNER JOIN  ( "  + qrOrdCentroNnComplete + "  ) b " + 
           " on a.vhcono=b.vhcono and a.vhfaci=b.vhfaci and a.vhprno=b.vhprno and a.vhmfno=b.vhmfno and a.vhschn=b.vhschn  "+
             " group by a.vhmfno ) zero "  );               
     
     
     
    return qryFinal;
  }
  
  
}
