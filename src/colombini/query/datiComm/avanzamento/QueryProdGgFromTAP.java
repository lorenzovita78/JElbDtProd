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
 * Query che resistuisce il numero di pezzi prodotti suddivisi per commessa in un determinato giorno
 * prelevando le informazioni dal sistema di spunta dei pz/colli del portale (TAP)
 * @author lvita
 */
public class QueryProdGgFromTAP extends CustomQuery{

 
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer qry=new StringBuffer();
    
    qry.append("select LPAD(trim(SUBSTR(trim(commessa),length(trim(commessa))-2,3)),3,'0') as commessa, MAX(datacomm) as datacomm, SUM(pzprod) as pzprod from (  \n " ).append(
              "     select ticomm as commessa ,tidtco as datacomm , \n" ).append(
              "           count(tibarp) pztot ,count(tpbarp) pzprod \n" +
              "        from ( \n" ).append(
                          " select ticomm ,tidtco ,tibarp , a.tpbarp\n" +
                          " from mcobmoddta.ztapci left outer  join     \n" ).append(
              "           ( select tpbarp   from mcobmoddta.ztapcp \n" +
                                " inner join mcobmoddta.ztapcu on tputin=tuutrf \n" ).append(
                                " where 1=1" ).append(
                                " and tpdtin >= " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA)).append(
                                " and tpdtin <= " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA)).append(
                                " and tuplgr =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEAVP)).append(  
                          " group by tpbarp ) a on tibarp=a.tpbarp " ).append(
                  " where tiplgr=  ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEAVP)).append(
                  " group by ticomm,tidtco ,tibarp , tpbarp ) b \n").append(
                  " group by ticomm ,tidtco  ) c \n").append(
                  " where pzprod>0 \n").append(
                  " group by LPAD(trim(SUBSTR(trim(commessa),length(trim(commessa))-2,3)),3,'0') \n").append(
                  " order by 2,1 " );
    
//        qry.append("select commessa, datacomm , pzprod from ( \n " ).append(
//              "     select ticomm as commessa ,tidtco as datacomm , \n" ).append(
//              "           count(tibarp) pztot ,count(tpbarp) pzprod \n" +
//              "        from ( \n" ).append(
//                          " select ticomm ,tidtco ,tibarp , a.tpbarp\n" +
//                          " from mcobmoddta.ztapci left outer  join     \n" ).append(
//              "           ( select tpbarp   from mcobmoddta.ztapcp \n" +
//                                " inner join mcobmoddta.ztapcu on tputin=tuutrf \n" ).append(
//                                " where 1=1" ).append(
//                                " and tpdtin >= " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA)).append(
//                                " and tpdtin <= " ).append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA)).append(
//                                " and tuplgr =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEAVP)).append(  
//                          " group by tpbarp ) a on tibarp=a.tpbarp " ).append(
//                  " where tiplgr=  ").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_LINEAVP)).append(
//                  " group by ticomm,tidtco ,tibarp , tpbarp ) b \n").append(
//                  " group by ticomm ,tidtco  ) c \n").append(
//                  " where pzprod>0 \n").append(
//                  " order by 2,1 " );
                     
    
    return qry.toString();
  }
  
  
  
}
