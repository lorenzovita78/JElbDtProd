/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.tab.SfridiCdlDettaglio;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class QuerySfridoPeriodoXCosti extends CustomQuery {

  
  public final static String STABILIMENTO="STABILIMENTO";
  public final static String PIANO="PIANO";
  public final static String CDL="CDL";
  
  public final static String ANNO="ANNO";
  public final static String MESE="MESE";
  
  public final static String PNP="PNP";
  
  public final static String CALCSFRIDO="CALCSFRIDO";
  public final static String CALCSFRIDOFISIO="CALCSFRIDOFISIO";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder query=new StringBuilder();
    
    
    String stab=getFilterSQLValue(STABILIMENTO);  
    String piano=getFilterSQLValue(PIANO);  
    String centroL=getFilterSQLValue(CDL);  
    Integer anno=(Integer) getFilterValue(ANNO);
    Integer mese=(Integer) getFilterValue(MESE);
    
    String calcSfrido=" sum(zssupd-zssupu-zssfis-zssprf) ";  // di default calcoliamo lo sfrido di ottimizzazione al netto del fisiologico e del rifilo
    String calcSfridoFisio=" 0 ";   // per i costi non interessa a meno che non sia presente in distinta  (vedi G1P0)
    
    
    
    String dataIni=DateUtils.getNumericData(anno, mese, Integer.valueOf(1)).toString();
    String dataFin=DateUtils.getNumericData(anno, mese, Integer.valueOf(31)).toString();
    
    
    String groupBy="GROUP BY ZSCONO,";
    
    if(isFilterPresent(CALCSFRIDO))
      calcSfrido=ClassMapper.classToString(getFilterValue(CALCSFRIDO)); 
    
    if(isFilterPresent(CALCSFRIDOFISIO))
      calcSfridoFisio=ClassMapper.classToString(getFilterValue(CALCSFRIDOFISIO)); 
    
    
    
    query.append(" select zscono,").append(stab).append(
                 ",").append(piano).append(  //modifica del 23/09/2016
                 ",").append(centroL).append(
                 ",").append(anno).append(
                 ",").append(mese);
    
    if(isFilterPresent(PNP)){
      groupBy+="ZSCPNP";
      query.append(", TRIM(ZSCPNP),'',");
    }else{
      groupBy+="ZSITNO";
      query.append(", '', TRIM(ZSITNO),");
    }
    
    
    query.append("\n   SUM( ZSSUPD ) as m2tot, ");  //modifica del 23/09/2016
    
    query.append("\n   ").append(calcSfrido).append(" as sfridoOtm , ");
    query.append(calcSfridoFisio).append(" as sfridoFisio ");
    
    //query.append(", 0 as m2difr2,0 as m2rid ").append(  //modifica del 23/09/2016
    query.append("\n from ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(SfridiCdlDettaglio.TABNAME).append(
     "\n where ZSCONO=30 ").append( 
        " and zsplgr=").append(centroL).append(
        " and zsdtrf between ").append(dataIni).append(" and ").append(dataFin);
    
    
    query.append("\n ").append(groupBy);
    
    return query.toString();
  }
  
  
  
  
}
