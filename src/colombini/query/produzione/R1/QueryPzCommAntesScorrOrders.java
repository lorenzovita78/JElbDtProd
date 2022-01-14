/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.COMMISSIONDATE;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.COMMISSIONNO;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.COMMISSIONYEAR;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.ISRICCIO;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.LINEELOG;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.PACKTYPEEQ;
import static colombini.query.produzione.R1.QueryPzCommImaAnte.SOSTBARCODEIDNR;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;

/**
 *
 * @author GGraziani
 */
public class QueryPzCommAntesScorrOrders extends CustomQuery {
  
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
     String s="";
    StringBuilder cond=new StringBuilder(
//                                         " select PackageNo,").append(
//                                         " ROW_NUMBER() OVER(PARTITION BY CommissionNo,PackageNo Order By CommissionNo  ) as numArticolo, \n").append(
//                                         " ProductionLine,Box,Pedana,OrderNo,0 as numOrd, \n ").append(
//                                         " itemNo,ItemDecription,rtrim(ltrim(DescriptionArt)) + ' - ' + ").append(
//                                         " cast(Length as varchar)+' X '+ cast(width as varchar)+' X '+ cast(thickness as varchar) ").append(
//                                         ",Barcode ").append(
                                       
                                        //" from tab_ET\n").append(  MOD del 21102020
                                       " where 1=1 \n");
    
    String subselect="Select cast(IdNR AS varchar(15)) as Idnr,CommissionNo comm ,PackageNo collo ,BarNo bar,ItemNo art ,colour color,Model modello,DrillingPRG prgDrill,GrovingPRG prgGroov "+
                     " from SRVPROD0.ColombiniORDERS.dbo.Orders "+
                     " where 1=1"; 
    
    
    String select=" SELECT PackageNo, ROW_NUMBER() OVER(PARTITION BY CommissionNo,PackageNo Order By CommissionNo  ) as numArticolo, \n"+
                         " ProductionLine, Box, Pedana, OrderNo, 0 as numOrd, itemNo, ItemDecription,\n rtrim(ltrim(DescriptionArt)) + ' - ' + "+
                         " cast(Length as varchar)+' X '+ cast(width as varchar)+' X '+ cast(thickness as varchar) , Barcode ";
                        
    
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
      subselect+=addAND(eqStatement(" commissionNo ", COMMISSIONNO));
    }
    
    if(isFilterPresent(COMMISSIONDATE)){
      cond.append(" and CommissionDate=convert(datetime, ").append(getFilterSQLValue(COMMISSIONDATE)).append(" ,120 )");
    }else if(isFilterPresent(COMMISSIONYEAR)){
      cond.append(" and year(CommissionDate)= ").append(getFilterSQLValue(COMMISSIONYEAR));
    }
  
    if(isFilterPresent(LINEELOG)){
      cond.append(addAND(inStatement(" ProductionLine ", LINEELOG))); 
    }
    
    if(isFilterPresent(SOSTBARCODEIDNR)){
      select+=" left outer join ( "+subselect+" )  B on CommissionNo=comm collate DATABASE_DEFAULT\n" +
                                              " and PackageNo=collo collate DATABASE_DEFAULT and BarNo=bar collate DATABASE_DEFAULT\n" +
                                              " and ItemNo=art  collate DATABASE_DEFAULT  and Colour_code=color collate DATABASE_DEFAULT\n" +
                                              " and Model=modello   collate DATABASE_DEFAULT and DrillingPRG=prgDrill  collate DATABASE_DEFAULT\n" +
                                              " and GroovingPRG=prgGroov collate DATABASE_DEFAULT" ;
    }
    
    //s=cond.toString();
    
    
    return select + cond.toString(); 
  }
  
}
