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
  public final static String CHANGELLOG="CHANGELLOG";
  public final static String ADDCOLOR="ADDCOLOR";
  
  public final static String ISRICCIO="ISRICCIO";
  
  public final static String SOSTBARCODEIDNR="SOSTBARCODEIDNR";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder cond=new StringBuilder(

                                       " where 1=1 and ProductionLine<>1035 and barcode like '2%' \n");
    
//    String subselect="Select cast(IdNR AS varchar(15)) as Idnr,CommissionNo comm ,PackageNo collo ,BarNo bar,ItemNo art ,colour color,Model modello,DrillingPRG prgDrill,GrovingPRG prgGroov "+
//                     " from SRVPROD0.ColombiniORDERS.dbo.Orders "+
//                     " where 1=1"; 
    
    
    
    String select=" SELECT PackageNo collo, 1 prog_collo, right('00000' + ProductionLine, 5) linea,\n" +
                  "box, Pedana , orderno, 0 nr_riga,\n" +
                  "refart, ItemDecription , ItemDecription,barcode,'' colore";
                        
    
    if(isFilterPresent(ISRICCIO)){
      select+=" , Colour_code";
      select=select.replace("ProductionLine", "case when packagingType='' then 'HOMAG' else packagingType end ");
      cond.append("and ( packagingType <> ''  or ProductionLine='06257' )");  //FILTRO BRUTTO DA SISTEMARE APPENA POSSIBILE
    }
    
//    if(isFilterPresent(ADDCOLOR)){
//      select+=" , Colour_code";
//    }
//    
//    if(isFilterPresent(CHANGELLOG)){
//      select=select.replace("ProductionLine", "packagingType");
//    }
    
    if(isFilterPresent(SOSTBARCODEIDNR)){
      select+=" , NULL AS Colour_code, IDNR ";
     
    }
    
    select+= " from tab_ET ";
    
    if(isFilterPresent(PACKTYPEEQ)){
     cond.append(addAND(eqStatement(" packagingType ", PACKTYPEEQ))); 
    }
    
//    if(isFilterPresent(PACKTYPENOTEQ)) {
//      cond.append(addAND(" packagingType <> '' ")); 
//    }
    
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
