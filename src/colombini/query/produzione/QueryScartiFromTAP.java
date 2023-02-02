/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryScartiFromTAP extends CustomQuery{

  public final static String FT_DELAB2="FT_DELAB2";
  public final static String FT_CDLRESP="FT_CDLRESP";
  public final static String FT_CDLTRANS="FT_CDLTRANS";
  public final static String FT_DATAINDA="FT_DATAINDA";
  public final static String FT_DATAINA="FT_DATAINA";
  
  public final static String FT_INNERJOIN="FT_INNERJOIN";
  
  
  @Override
  public String toSQLString() throws QueryException {

    StringBuffer str=new StringBuffer("SELECT REPLACE(SUBSTRING(CHAR((TSDTIN)), 1, 10),'-',''),TICART AS CODART").append(
                                     " ,1 as qta, TSCAUS , TICOMM , TINCOL ,TSIDSP , trim(TSBARP),  TSPLGT , TSPLGR     \n ");
   
   
    StringBuffer sub1=new StringBuffer( " select  TSBARP ,TSCAUS ,  TSPLGT , TSPLGR ,max(TSDTIN) AS TSDTIN,max(TSIDSP) AS  TSIDSP,MAX(TSDTEL) AS TSDTEL  " ).append(
                                       " \n  from  MCOBMODDTA.ZTAPSP " ).append(
                                       " \n where 1=1 ");
   
   
    StringBuffer sub2=new StringBuffer(" select TIBARP, TICOMM,TINCOL ,TICART ").append(
                                       "\n from MCOBMODDTA.ZTAPCI where 1=1 \n ").append(        
                                       " and TIPLGR = ").append(getFilterSQLValue(FT_CDLRESP));
   
   //impostare questo filtro per prendere tutti gli scarti che sono stati inseriti ad una determinata linea
   if(isFilterPresent(FT_CDLTRANS))
      sub1.append(" and TSPLGT = ").append(getFilterSQLValue(FT_CDLTRANS));
   
   //impostare questo filtro per prendere tutti gli scarti che sono stati assegnati sul portale ad una determinata linea
   if(isFilterPresent(FT_CDLRESP))
      sub1.append(" and TSPLGR = ").append(getFilterSQLValue(FT_CDLRESP));
   
   
   if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATADA))  
     sub1.append(" and TSDTIN >= ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA));
   
   if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATAA))  
     sub1.append(" and TSDTIN <= ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA));
   
   
   if(!isFilterPresent(FT_DELAB2))   
     sub1.append("and TSDTEL is null" );      
   
     //" and TSDTIN <= ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA)).append(                    
    sub1.append(" group by TSBARP ,TSCAUS ,TSPLGT , TSPLGR "); // " ) A  \n INNER JOIN ").append(
    
    
   
    
    
    
//    String select =str+" ( "+ sub1.toString() +" ) A \n INNER JOIN ( "+sub2.toString() + "\n  ) B     "
//            + " ON TSBARP=TIBARP \n  where 1=1 ";
//   
// left outer join per prendere cmq tutti i pz  scartati anche se non ho le info sulla commessa   
    String select =str+"  FROM ( "+ sub1.toString() +" ) A  \n";
    if(isFilterPresent(FT_INNERJOIN))
      select+=" INNER JOIN ";
    else
      select+=" LEFT OUTER JOIN ";
    
    select+=" ( "+sub2.toString() + "\n  ) B     "
            + " ON TSBARP=TIBARP \n  where 1=1 ";
          

    
    return select;
  }
  
  
  
}
