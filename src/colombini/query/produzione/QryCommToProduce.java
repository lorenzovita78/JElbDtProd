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
public class QryCommToProduce extends CustomQuery{

  
  
  public final static String TABLOGELABCOMM="ZJBLOG";
  public final static String TABELABCOMM="ZCOMME";
  
  public final static String FONLYCOMM="FONLYCOMM";
  public final static String FONLYCCLOSED="FONLYCCLOSED";
  public final static String FEXCLUDENANO="FEXCLUDENANO";
    
  @Override
  public String toSQLString() throws QueryException {
    
    String fDataI=getFilterSQLValue(FilterQueryProdCostant.FTDATAINI);
    String fDataF=getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN);
    
    if( fDataI==null || fDataI.isEmpty() || fDataF==null || fDataF.isEmpty())
      throw new QueryException("Filtro data non presente!! Impossibile eseguire la query");
    
    StringBuilder str=new StringBuilder();
    str.append(" SELECT distinct zccdld data, zcccom commessa , zcrgdt dataelab , zcmccd as tipo").append(
               " FROM ").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(TABLOGELABCOMM).append(
                " ,").append(Connections.getInstance().getLibraryPersAs400()).append(".").append(TABELABCOMM).append(
               " WHERE 1=1 ").append(
//               " and zccmst=60 ").append(
               " and zccono = ").append(CostantsColomb.AZCOLOM).append(
               " and zccono=zjcono").append(
               " and zjid01=zcccom").append(
               " and zccdld between  ").append(fDataI).append(" and ").append(fDataF).append(
               " and zcrgdt>0 ");        
    
    if(isFilterPresent(FONLYCOMM))
      str.append(" and zcmccd = 0");
    
    if(isFilterPresent(FONLYCCLOSED))
      str.append(" and zccmst>=60 ");
    
    
    if(isFilterPresent(FEXCLUDENANO)){
      str.append(" and zcmccd <= 1 ");
    }
    
    str.append(" order by 1");
    
    return str.toString();
  }
  
  
  
}
