  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.avanzamento;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryProdGgImpImaFromHitS extends CustomQuery{

  public final static String FT_CLIIMAANTE="FT_CLIIMAANTE";
  public final static String FT_TYPEPROD="FT_TYPEPROD";
  public final static String FT_STATUS="FT_STATUS";
  
  
  public final static Integer STATUSCOMCOMPLETE=100;
  public final static Integer STATUSMAGCOMPLETE=87;
  
  public final static String TYPEPRODCOMM="2";
  
  public final static String TYPEPRODMAG="1";
  
  public final static String TYPEPRODEXT="4";
  
  @Override
  public String toSQLString() throws QueryException {
    
    String select=" select  count(*) as tot ";
    String group=" ";
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_NUMCOMM)){
      select=" select b.CommissionNo, count(*) as tot";
      group=" group by b.CommissionNo";
    }
    
    StringBuilder qry=new StringBuilder(
                     select ).append(
                      "\n  from tab_et_histstatus a left outer join tab_Et b on a.Barcode=b.barcode  ").append(
                      "\n  where 1=1  ");
    
    if(isFilterPresent(FT_STATUS)){
      qry.append(" and  a.Status = ").append(getFilterSQLValue(FT_STATUS));
           
    }
    
    if(isFilterPresent(FT_TYPEPROD)){
      qry.append(" and  ( Left(a.Barcode,1) = ").append(getFilterSQLValue(FT_TYPEPROD)).append(
              "  )  and   b.KenBc =  ").append(getFilterSQLValue(FT_TYPEPROD));        
    }
    
    
    //per pezzi a commessa
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_NUMCOMM)){
      if(isFilterPresent(FT_CLIIMAANTE)){
        qry.append(" and  b.CommissionNo between 400 and 797 ");
      }else{
        qry.append(" and  ( b.CommissionNo < 400 or b.CommissionNo> 797 )");
      }
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATADA) ){
      String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA);
      qry.append("AND a.ZPStatus >= convert( datetime ,").append(dI).append(" ,120)");
//      qry.append("AND b.ZPStatus >= convert( datetime ,").append(dI).append(" ,120)");
    } 
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATAA)){
      String dI=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA);
      qry.append("AND a.ZPStatus <= convert( datetime ,").append(dI).append(" ,120)");
//      qry.append("AND b.ZPStatus <= convert( datetime ,").append(dI).append(" ,120)");
    }
    
    
    qry.append(group);
    
    return qry.toString(); 
  }
  
  
  
}
