/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.bi;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryCheckAcqAZero extends CustomQuery {

  @Override
  public String toSQLString() throws QueryException {
   String qry=" SELECT IBCONO as azienda,IBFACI as facility,IBWHLO as magazzino, IBSUNO as codFornitore,"
           + " IBPUNO as numOrdine, IBPNLI as rigaOrdine , IBPNLS as sottoriga , "
           + " IBITNO as codArticolo , IBPITD as descArticolo , IBBUYE as buyer,IBORQA as qta "
           + " FROM MVXBDTA.mpline  " +
              " WHERE IBRGDT > " +getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)+
              " AND IBPUST < '70' "+
              " AND ( IBLNAM = 0 or  IBPUPR = 0 )"+
              " AND NOT (IBPNLI= 0 AND IBPNLS=0 AND IBPOTC=30)";
   
   return qry;
  }
  
}
