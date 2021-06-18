/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm;

import db.Connections;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;

/**
 * Query per trovare le commesse che sono state elaborate  ad una certa data 
 * @author lvita
 */
public class QryCommesseElab extends CustomQuery{

  public final static String FEXCLUDENANO="FEXCLUDENANO";
  public final static String FCOMMANTICIPO="FCOMMANTICIPO";
  public final static String FCOMMFEBAL="FCOMMFEBAL";
  
  public final static String LIBRARYPERSAS400="LIBRARYPERSAS400";
  
  
  @Override
  public String toSQLString() throws QueryException {
    
    String libraryAs400=Connections.getInstance().getLibraryPersAs400();
    
    if(isFilterPresent(LIBRARYPERSAS400) )
      libraryAs400=ClassMapper.classToString(getFilterValue(LIBRARYPERSAS400));
      
    
    StringBuilder str=new StringBuilder();
    str.append(" SELECT distinct zcccom commessa,zccdld data,zcmccd tipo,zjrgdt  ").append(
               " FROM ").append(libraryAs400).append(".").append(FilterFieldCostantXDtProd.TABLOGELABCOMM).append(
                " ,").append(libraryAs400).append(".").append(FilterFieldCostantXDtProd.TABELABCOMM).append(
               " WHERE 1=1").append(
               " and zccono = ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA)).append(
               " and zccmst>=60 ").append(
               " and zccono=zjcono").append(
               " and zjid01=zcccom");
               
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATA)){
      String fData=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
      str.append(" and  ZJRGDT = ").append(fData);
    } else if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATADA)) {
      String fDataDa=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);
      str.append(" and  ZJRGDT >= ").append(fDataDa);
    } else {
      throw new QueryException("Filtro data non presente!! Impossibile eseguire la query");
    }        
               
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_NUMCOMM))
      if(isFilterPresent(FCOMMFEBAL))
        str.append(" AND zcccom  LIKE '%'|| ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM));
      else  
        str.append(" and zcccom=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM));
    
    
    if(isFilterPresent(FEXCLUDENANO)){
      str.append(" and zcmccd <= 1 ");
    }
    
    if(isFilterPresent(FCOMMANTICIPO)){
      str.append(" and zcmccd = 4 ");
    
    }
    
    return str.toString();
  }
  
  
  
}
