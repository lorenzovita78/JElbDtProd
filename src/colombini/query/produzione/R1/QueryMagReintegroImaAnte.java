/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import db.JDBCDataMapper;
import exception.QueryException;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class QueryMagReintegroImaAnte extends CustomQuery {

  @Override
  public String toSQLString() throws QueryException {
    
    String dataRif=ClassMapper.classToString(getFilterValue(FilterQueryProdCostant.FTDATARIF));
    String oraIni=dataRif+" 00:00:00";
    String oraFin=dataRif+" 23:59:59";
    
    StringBuilder qry=new StringBuilder(" select qtot.* ,getDate() from ( ").append(
            " select 'IN' tipo,  min(a.zpstatus) data,\n").append(
                    " a.barcode, b.ItemNo, b.ItemDecription, b.BarNo, b.Length, b.Width\n").append(
              " from tab_ET_HistStatus a inner join tab_et b on a.barcode=b.barcode \n").append(
             " where a.barcode like '8%' and a.Status='45'\n").append(
             " group by a.barcode, b.ItemNo, b.ItemDecription, b.Length, b.Width, b.BarNo ").append(
             "  \n having min(CONVERT(date, a.zpstatus))=convert(date , ").append(
                      JDBCDataMapper.objectToSQL(dataRif)).append(" )");
    
    qry.append("\n union all ");
    qry.append("\n select 'OUT_RE' tipo , a.zpstatus  data,\n").append(
                    " a.barcode, a.ItemNo, a.ItemDecription, a.BarNo, a.Length, a.Width\n").append(
                " from tab_ET a\n").append(
                " where a.Status='100' and a.BarcodeNeu like '8%'\n").append(
                " and a.ZPStatus between  CONVERT(datetime,").append(JDBCDataMapper.objectToSQL(oraIni)).append(
                        " , 120 ) and CONVERT(datetime,").append(JDBCDataMapper.objectToSQL(oraFin)).append(
                                           " , 120 ) ");
    
    
    
    qry.append("\n union all ");
    qry.append("\n select case a.status when '100' then 'OUT_NRE' when '999' then 'OUT_999' end tipo ,\n").append(
                    " a.zpstatus data, a.barcode, a.ItemNo, a.ItemDecription, a.BarNo, a.Length, a.Width\n").append(
                " from tab_ET a left outer join ").append(
                        "\n (select BarcodeNeu from tab_et b where b.BarcodeNeu<>'' group by BarcodeNeu) b ").append(
                        " on a.barcode = b.barcodeNeu"   ).append(     
                " where a.Status in ('100','999') and a.Barcode like '8%'\n").append(
                " and a.ZPStatus between  CONVERT(datetime, ").append(JDBCDataMapper.objectToSQL(oraIni)).append(
                        " , 120 ) and CONVERT(datetime, ").append(JDBCDataMapper.objectToSQL(oraFin)).append(" , 120 ) ").append(
                "\n and b.BarcodeNeu is null ");
    
    qry.append("  ) qtot \n").append(
               " order by tipo");
    
    
    return qry.toString();
    
  }
  
  
  
 
  
}
