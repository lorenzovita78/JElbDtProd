/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QuerySfridiOtmImaR1 extends CustomQuery {
   
   public final static String DATAINIZIO="DATAINIZIO";
   public final static String DATAFINE="DATAFINE";

   public final static String GROUPBYBANDA="GROUPBANDA";
   
  @Override
  public String toSQLString() throws QueryException {
    String dtInizio = null;
    String dtFine = null;
    
    
    
    //filtri obbligatori
    dtInizio=getFilterSQLValue(DATAINIZIO);
    dtFine=getFilterSQLValue(DATAFINE);  
    
    String select =" select cast(convert(varchar(8),max(optimization_day) ,112) as int) as dataRif,"+
                   " cast(replace(convert(varchar(8), max(optimization_day) ,114),':','') as int) as oraRif, "+
                   " optimization_idno as nr_ott, '' as codPnp, '' as codI, \n 0 as lun, 0 as largh, 0 as spe, ";
    
    
    String groupBy=" group by Optimization_IDNo ";
    
    String orderBy="";
    
    if(isFilterPresent(GROUPBYBANDA)){
      select ="select cast(convert(varchar(8),optimization_day ,112) as int) as dataRif, BarNo, ";
      groupBy="group by cast(convert(varchar(8),optimization_day ,112) as int),BarNo"; 
      orderBy=" order by 1, 2";
    }
    
    
    StringBuilder qry=new StringBuilder(
                        select).append(
                        " sum(barsnumber)as numero_strisce, ").append(
                        " sum(ordersnumber) as pz_totali_ott, ").append(
                        " sum(barslength) as lunghezza_tot, ").append(
                        "\n sum(orderslength) as lunqhezza_pz, ").append(
                        " sum(r2wastelength) as difettoR2, ").append(
                        " sum(Sawbladelength) as sfridoFisio, ").append(
                        " sum(precutlength+postcutlength) as supp1, ").append(  
                        " SUM(WasteWidthReductionSquare+WasteDefectSquare+SawbladeSquare+precutsquare+postcutsquare) as supp2 , 0 sups ").append(
                        "\n from dbo.tab_OPTI_StatisticMaterialWaste a left outer join tab_Optimierung b on a.optimization_idno=b.optiidnr ").append(
                        "\n where 1=1 ").append(    
                        " and substring(convert(char,optimization_day, 21),1,10) >=").append(dtInizio).append(
                        " and substring(convert(char,optimization_day, 21),1,10) <=").append(dtFine).append(
                        " \n and a.barslength>0  ").append(
                        " and coalesce(b.Status,25)=25 ").append(
                        groupBy).append(
                        orderBy);   
    
    
    
    return qry.toString();
  }
   
   
}





//StringBuilder qry=new StringBuilder(
//                        " select cast(convert(varchar(8),max(optimization_day) ,112) as int) as dataRif, ").append(
//                        " cast(replace(convert(varchar(8), max(optimization_day) ,114),':','') as int) as oraRif,").append(
//                        " optimization_idno as nr_ott, '' as codPnp, '' as codI, ").append(
//                        "\n 0 as lun, 0 as largh, 0 as spe,").append(
//                        " sum(barsnumber)as numero_strisce, ").append(
//                        " sum(ordersnumber) as pz_totali_ott, ").append(
//                        " sum(barslength) as lunghezza_tot, ").append(
//                        "\n sum(orderslength) as lunqhezza_pz, ").append(
//                        " sum(r2wastelength) as difettoR2, ").append(
//                        " sum(Sawbladelength) as sfridoFisio, ").append(
//                        " sum(precutlength+postcutlength) as supp1, ").append(  
//                        " SUM(WasteWidthReductionSquare+WasteDefectSquare+SawbladeSquare+precutsquare+postcutsquare) as supp2 ").append(
//                        "\n from dbo.tab_OPTI_StatisticMaterialWaste a left outer join tab_Optimierung b on a.optimization_idno=b.optiidnr ").append(
//                        "\n where 1=1 ").append(    
//                        " and substring(convert(char,optimization_day, 21),1,10) >=").append(dtInizio).append(
//                        " and substring(convert(char,optimization_day, 21),1,10) <=").append(dtFine).append(
//                        " \n and a.barslength>0  ").append(
//                        " and coalesce(b.Status,25)=25 ").append(
//                        "group by Optimization_IDNo ");   