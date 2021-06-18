/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QuerySfridoImpImaBandeMq extends CustomQuery {

  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder query=new StringBuilder();
    
    
     
    String centroL=getFilterSQLValue(FilterQueryProdCostant.FTCDL);  
    //filtri obbligatori
    String dtInizio=getFilterSQLValue(FilterQueryProdCostant.FTDATAINI);
    String dtFine=getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN);  
    
   
   // sfrido fisiologico composto da   SawbladeSquare+precutsquare+postcutsquare e non più SawbladeSquare
   //sfrido ottimizzazione pulito anche  del pre e post taglio
   
    query.append(" select 30 ,").append(centroL).append(
                    ", cast(convert(varchar(8),max(optimization_day) ,112) as int) as dataRif ").append(
                    ", \n min(Optimization_IDNo),'' as codPnp, Barno as codI ").append(
                    ", \n coalesce(c.Laenge,0) as lun, coalesce(c.Tiefe,0) as largh ,coalesce(c.Staerke,0) as spe  ").append(         
                    ", \n sum(barsnumber)as numero_strisce ").append(
                    ", sum(ordersnumber) as pz_totali_ott ").append(          
                  ", sum(barssquare)  ").append( 
                  ", sum(orderssquare)  ").append(
                  "\n , sum(r2wastesquare) as difettoR2 ").append(
                  ", 0 as sovraproduzione ").append(
                  ", SUM (precutsquare+postcutsquare) as rifilo").append(        
                  ", SUM(SawbladeSquare) sfridoFisio  ").append(
                  "\n , SUM( precutsquare+postcutsquare ) as sup1 ").append(        
                  ", SUM(WasteWidthReductionSquare+WasteDefectSquare+SawbladeSquare+precutsquare+postcutsquare) as supp2 ").append(   
                          ", 0 as supp3 , 0 as supp4 ").append(   
     "\n from dbo.tab_OPTI_StatisticMaterialWaste a left outer join tab_Optimierung b on a.optimization_idno=b.optiidnr  ").append(
             "\n left outer join dbo.tab_Material c on a.BarNo=convert (varchar, c.MaterialID ) collate SQL_Latin1_General_CP1_CI_AS ").append( 
     "\n where 1=1 ").append( 
        " and substring(convert(char,optimization_day, 21),1,10) >=").append(dtInizio).append(
        " and substring(convert(char,optimization_day, 21),1,10) <=").append(dtFine).append(
        " \n and a.barslength>0  ").append(
        " and coalesce(b.Status,25)=25 ").append(
     " group by cast(convert(varchar(8),optimization_day ,112) as int),BarNo,c.Laenge, c.Tiefe,c.Staerke").append(
     "  order by dataRif,Barno"   );
    
    return query.toString();
  }
  
  
  
  
}
