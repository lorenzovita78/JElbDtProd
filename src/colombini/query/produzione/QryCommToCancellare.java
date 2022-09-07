/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.costant.CostantsColomb;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per trovare le commesse che sono state elaborate e concluse ad una certa data 
 * @author lvita
 */
public class QryCommToCancellare extends CustomQuery{

  
  
  public final static String TABLOGELABCOMM="ZJBLOG";
  public final static String TABELABCOMM="ZCOMME";
  
  public final static String TIPOCOMM="TIPOCOMM";
  public final static String FTNUMCOMM="FTNUMCOMM";

    
  @Override
  public String toSQLString() throws QueryException {
    
      
    String DataComm=getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN);
      
    if( DataComm==null || DataComm.isEmpty())
      throw new QueryException("Filtro data non presente!! Impossibile eseguire la query");
    
    StringBuilder str=new StringBuilder();
    str.append(" SELECT distinct zccdld data, zcccom commessa , zcrgdt dataelab , zcmccd as tipo ").append(
               " FROM ").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(TABLOGELABCOMM).append(
                " ,").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(TABELABCOMM).append(
               " WHERE 1=1 ").append(
               " and zccono = ").append(CostantsColomb.AZCOLOM).append(
               " and zccono=zjcono").append(
               " and zjid01=zcccom").append(
               " and zcrgdt>0 ");        
    
     if(isFilterPresent(FilterQueryProdCostant.FTDATACOMMN))
         str.append( " and zccdld =  ").append(DataComm);
    
    if(isFilterPresent(FTNUMCOMM))
         str.append( " and zcccom =  ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM));    
    
    if(isFilterPresent(TIPOCOMM))
      str.append(" and zcmccd = ").append(QryCommToCancellare.TIPOCOMM);
    
    str.append(" order by 1");
    
    return str.toString();
  } 
  
}
