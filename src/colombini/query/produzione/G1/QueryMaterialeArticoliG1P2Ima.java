/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.G1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryMaterialeArticoliG1P2Ima extends CustomQuery{

  
  public final static String FTCDL="FTCDL";
  public final static String FTCDLPADRE="FTCDLPADRE";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer qry=new StringBuffer();
    
    String cdL=getFilterSQLValue(FTCDL);
    String cdLP=getFilterSQLValue(FTCDLPADRE);
    String dataRif=getFilterSQLValue(FilterQueryProdCostant.FTDATARIF);
    
    qry.append("SELECT djprno,mmdim1,mmdim2,mmdim3, ").append(
                " sum(djmaqt) as qta").append(
               " FROM MWOPTR,MITMAD ").append(
              " WHERE 1=1").append(
                " and djcono=mmcono").append(
                " and djprno=mmitno").append(
                " and DJDPLG = ").append(cdL).append( 
                " and djrgdt =").append(dataRif).append(  
             " GROUP BY DJPRNO,mmdim1,mmdim2,mmdim3").append(
             " union all").append(
             " SELECT distinct vhprno,mmdim1,mmdim2,mmdim3, ( (vhmaqt)*0.333 ) as qta ").append(
             " FROM mwohed ").append(
             " inner join mitmad on vhcono=mmcono and vhprno=mmitno").append(
             " inner join mwoope on vhcono=vocono and vhprno=voprno and vhmfno=vomfno").append(  
             " left  join mwoptr on vhcono=djcono and vhprno=djprno and vhmfno=djmfno  and voopno=djopno ").append( 
             " WHERE 1=1").append(
             " and vowost='90' ").append(
             " and vhmaqt>0  ").append(
             " and vhorty in ('FV1' , 'FVX' , 'FVZ')  ").append(
             " and djopno is null  ").append(
             " and voplgr= ").append(cdLP).append(
             " and vorefd=").append(dataRif);
    
    
    return qry.toString();
    
  }
  
  
}
