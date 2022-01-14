/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author GGraziani
 */
public class QueryPzCommImaTop extends CustomQuery {

  public final static String PACKTYPEEQ="PACKTYPEEQ";
  public final static String PACKTYPENOTEQ="PACKTYPENOTEQ";
  public final static String PACKTYPEDISEQ="PACKTYPEDISEQ";
  public final static String COMMISSIONNO="COMMISSIONNO";
  public final static String COMMISSIONDATE="COMMISSIONDATE";
  public final static String COMMISSIONYEAR="COMMISSIONYEAR";
  public final static String LINEELOG="LINEELOG";
  public final static String ISSCORR="ISSCORR";

  
  
  @Override
  public String toSQLString() throws QueryException {
      
     StringBuilder cond=new StringBuilder(" where 1=1  \n");
    
    if(!isFilterPresent(ISSCORR)){
              cond.append(" and ProductionLine<>1035 and barcode like '2%' ");}

    String select= " SELECT PackageNo collo, 1 prog_collo, ProductionLine,\n" +
                    "box, Pedana , orderno, 0 nr_riga,\n" +
                    "refart, ItemDecription , ItemDecription,barcode,'' colore";

    if(!isFilterPresent(ISSCORR)){
     select=  " SELECT PackageNo collo, 1 prog_collo, CASE\n" +
                    "WHEN ProductionLine=36111 THEN 'P4LSM'\n" +
                    "WHEN ProductionLine=6030 THEN 'P2STRETTOI'\n" +
                    "WHEN ProductionLine=6031 THEN 'P2STRETTOI'\n" +
                    "WHEN ProductionLine=6050 THEN 'P4SCORREVOLI'\n" +
                    "WHEN ProductionLine=6051 THEN 'P4SCORREVOLI'\n" +
                    "WHEN ProductionLine=6148 THEN 'P2TAVOLI'\n" +
                    "WHEN ProductionLine=6150 THEN 'P2TAVOLI'\n" +
                    "WHEN ProductionLine=6516 THEN 'P2MF1'\n" +
                    "WHEN ProductionLine=6145 THEN 'P2TAVOLI'\n" +
                    "WHEN ProductionLine=6100 THEN 'P4LSM'\n" +
                    "ELSE ProductionLine\n" +
                    "END linea,\n" +
                    "box, Pedana , orderno, 0 nr_riga,\n" +
                    "refart, ItemDecription , ItemDecription,barcode,'' colore";
    }
    
    select+= " from tab_ET ";
    
    if(isFilterPresent(PACKTYPEEQ)){
     cond.append(addAND(eqStatement(" packagingType ", PACKTYPEEQ))); 
    }
    

    if(isFilterPresent(COMMISSIONNO)){
      cond.append(addAND(eqStatement(" commissionNo ", COMMISSIONNO))); 
    }
    
    if(isFilterPresent(COMMISSIONDATE)){
      cond.append(" and CommissionDate=convert(datetime, ").append(getFilterSQLValue(COMMISSIONDATE)).append(" ,120 )");
    }else if(isFilterPresent(COMMISSIONYEAR)){
      cond.append(" and year(CommissionDate)= ").append(getFilterSQLValue(COMMISSIONYEAR));
    }
  
    if(isFilterPresent(LINEELOG)){
      cond.append(addAND(inStatement(" ProductionLine ", LINEELOG))); 
    }
     
    return select + cond.toString(); 
  
  }
 
  
  
  
}
