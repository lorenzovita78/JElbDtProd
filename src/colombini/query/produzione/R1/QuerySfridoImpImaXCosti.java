/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import db.CustomQuery;
import db.JDBCDataMapper;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QuerySfridoImpImaXCosti extends CustomQuery {

  
  public final static String STABILIMENTO="STABILIMENTO";
  public final static String PIANO="PIANO";
  public final static String CDL="CDL";
  
  public final static String ANNO="ANNO";
  public final static String MESE="MESE";
  
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder query=new StringBuilder();
    
    
    String stab=getFilterSQLValue(STABILIMENTO);  
    String piano=getFilterSQLValue(PIANO);  
    String centroL=getFilterSQLValue(CDL);  
    Integer anno=(Integer) getFilterValue(ANNO);
    Integer mese=(Integer) getFilterValue(MESE);
    
    
    String ms=mese.toString();
    if(ms.length()==1)
      ms="0"+ms;
    
    String dataIni=anno+"-"+ms+"-01";
    String dataFin=anno+"-"+ms+"-31";
    
    
    
    query.append(" select 30 ,").append(stab).append(
                 ",").append(piano).append( //modifica del 23/09/2016 per tabella costi 
                 ",").append(centroL).append(",").append(anno).append(
                 ",").append(mese).append(",");
    
   //modifica del  22/9/2016
   // sfrido fisiologico composto da   SawbladeSquare+precutsquare+postcutsquare e non più SawbladeSquare
   //sfrido ottimizzazione pulito anche  del pre e post taglio
    query.append("\n '' as codPnp, Barno as codI, ").append(
                  " sum(barssquare) , ").append( 
                  " sum(barssquare - orderssquare - SawbladeSquare - precutsquare - postcutsquare ) sfridoOtt, ").append(
                  " 0 sfridoFisio ").append(
                 //" sum(WasteDefectSquare-r2wastesquare-WasteWidthReductionSquare) sfridoOtt, ").append(
                 //" sum(SawbladeSquare+precutsquare+postcutsquare) sfridoFisio,").append(
                 //" sum(r2wastesquare) as difettoR2M2,sum(WasteWidthReductionSquare) as riduzioniM2").append(
     "\n from dbo.tab_OPTI_StatisticMaterialWaste a left outer join tab_Optimierung b on a.optimization_idno=b.optiidnr  ").append(
     "\n where 1=1 ").append( 
        " and substring(convert(char,optimization_day, 21),1,10) >=").append(JDBCDataMapper.objectToSQL(dataIni)).append(
        " and substring(convert(char,optimization_day, 21),1,10) <=").append(JDBCDataMapper.objectToSQL(dataFin)).append(
        " and a.barslength>0").append(
        " and coalesce(b.Status,25)=25  ").append(
     "\n group by Barno");
    
    return query.toString();
  }
  
  
  
  
}
