/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm;

import db.Connections;
import db.CustomQuery;
import db.JDBCDataMapper;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryVolumiComm extends CustomQuery {

  @Override
  public String toSQLString() throws QueryException {
     StringBuilder sql=new StringBuilder(" SELECT oadivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                      ", sum( EAVOL3 ) as " ).append(FilterFieldCostantXDtProd.FD_VOLUME).append(
                     "\n FROM ").append(Connections.getInstance().getLibraryPersAs400()).append(".ZCOMME, ").append(
                                Connections.getInstance().getLibraryStdAs400()).append(".DCONSI ,").append( 
                                Connections.getInstance().getLibraryStdAs400()).append(".DOHEAD ").append(  
                                " INNER JOIN ").append(
                                Connections.getInstance().getLibraryStdAs400()).append(".OOHEAD ").append(
                              " on eacono=oacono and eaorno=oaorno " ).append(
                      "\n WHERE 1=1").append(
                        " AND ZCCONO=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
                        " AND ZCCCOM=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                        " AND ZCCDLD=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(
                        " AND ZCCONO=DACONO ").append(
                        " AND ZCCDLD=DACDLD ").append(
                        " AND DACONO=EACONO ").append(
                        " AND DACONN=EACONN ").append(
                        " group by oadivi");
     
     
    return sql.toString();
  }
  
  
  
  
}
