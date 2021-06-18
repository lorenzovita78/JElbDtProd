/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.G1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryScartiGal extends CustomQuery {

 
  //filtro per far vedere solo gli scarti di G2
  //se non specificato vengono presi tutti gli scarti che hanno come centro responsabile G1P2
  public final static String ONLYG1="ONLYG1P2";
  
  public final static String ALLG1="ALLG1";
  
  
  public final static String CODCAUSALE="CODCAUSALE";
  public final static String CAUSALI="CAUSALI";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder str=new StringBuilder();
    
    String select="select z9trdt as dt,SUM(Z9TRQT) as qta"; 
    String groupBy=" group by Z9TRDT ";
    
    String subqueryCdl=" select distinct zpplgr from mcobmoddta.zmpdwct " +
                       " where zpcono=30 and zpfact='G1' " +
                       " and zpplan='P2' ";
    
    
    if(!isFilterPresent(CODCAUSALE) && !isFilterPresent(CAUSALI)){
      select = "select z9trdt as dt, trim(Z9RSCD) as caus,SUM(Z9TRQT) qta";
      groupBy+=" ,Z9RSCD ";
    }
    
    str=new StringBuilder(select).append(
            "\n from mcobmoddta.pmz790pf").append(
            "\n where 1=1 ");
            
    
    if(isFilterPresent(ONLYG1)){        
      str.append("\n and z9plgt in  ( ").append(subqueryCdl ).append( " )");
    }
    if(isFilterPresent(ALLG1)){
      str.append("\n and z9plgr in ( ").append(subqueryCdl ).append( " )");
      str.append("\n and z9plgt not in  ( ").append(subqueryCdl).append( " )");
    }                        
    
    str.append("\n and z9cono=30 ");
    
    if(isFilterPresent(FilterQueryProdCostant.FTDATARIF)){
      str.append("\n and z9trdt =").append(getFilterSQLValue(FilterQueryProdCostant.FTDATARIF));
    } else if(isFilterPresent(FilterQueryProdCostant.FTDATAINI) &&  isFilterPresent(FilterQueryProdCostant.FTDATAFIN )){
      str.append("\n and z9trdt between ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATAINI)).append(
                 " and  ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN));
    }    
 
    
    if(isFilterPresent(CODCAUSALE)){
      str.append(" and Z9RSCD = ").append(getFilterSQLValue(CODCAUSALE));
    }else if (isFilterPresent(CAUSALI)) {
      str.append(inStatement("Z9RSCD",CAUSALI));
    }
    
    
    str.append(groupBy);
    str.append(" order by 1 ");
    
    return str.toString();
  }
  
  
  
}
