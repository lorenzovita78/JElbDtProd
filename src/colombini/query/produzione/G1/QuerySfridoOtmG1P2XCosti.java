/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.G1;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.ISfridoInfo;
import colombini.model.persistence.tab.SfridiCdlDettaglio;
import db.CustomQuery;
import db.JDBCDataMapper;
import exception.QueryException;
import utils.DateUtils;

/**
 * Query per estrazione dati di sfrido da tabella ottimizzazioni
 * X i costi si è deciso di mostrare solo i mq di sfrido di ottimizzazione che risulta essere al netto 
 * dello sfrido fisiologico e del rifilo del pannello ( per Galazanno p2 ZSSUP2) modifica del 22/09/2016
 * @author lvita
 */
public class QuerySfridoOtmG1P2XCosti extends CustomQuery {

  public final static String ANNO="ANNO";
  public final static String MESE="MESE";  
  
  @Override
  public String toSQLString() throws QueryException {
    
    Integer anno=(Integer) getFilterValue(ANNO);
    Integer mese=(Integer) getFilterValue(MESE);
    
    
    
    String ms=mese.toString();
    if(ms.length()==1)
      ms="0"+ms;
    
    Long dataIni =DateUtils.getNumericData(anno, mese, Integer.valueOf(1));
    Long dataFin=DateUtils.getNumericData(anno, mese, Integer.valueOf(31));
    
    Long annoMese=Long.valueOf(anno+ms);
    
    StringBuilder qry=new StringBuilder(" ");
    qry.append("select trim( b.pnp) PNP ,ifnull(trim(c.mpitno),'XXX') PAN ,ifnull(mhcyp6,").append(JDBCDataMapper.objectToSQL(annoMese)).append(
                       " ) as periodo, ifnull(mhusqt,0) qta, cast (sfridoOtm as dec (9 ,4))  ,cast (supDisp as dec (9 ,4))   \n  from  \n ").append(
                " ( select zscpnp PNP,max(zsitno) as coda,sum(zssupd+zsspsp-zssupu-zssfis-zssprf) as sfridoOtm , sum(zssupd) supDisp  ").append(
                     "\n from ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(SfridiCdlDettaglio.TABNAME).append(
                     "\n where zscono=").append(CostantsColomb.AZCOLOM).append(
                       " and zsplgr=").append(JDBCDataMapper.objectToSQL(ISfridoInfo.OTMGALP2)).append(
                       " and zsdtrf>=").append(JDBCDataMapper.objectToSQL(dataIni)).append(
                       " and zsdtrf<=").append(JDBCDataMapper.objectToSQL(dataFin)).append( 
                    " group by zscpnp  ) B  \n LEFT OUTER JOIN  \n " ).append(
                 " (  select  a1.mpitno,a1.pnp , a2.mhcyp6,a2.mhusqt from \n").append(
                     " ( select distinct mpitno, mppopn as pnp from mitpop  ").append(
                       "\n  where mpcono=").append(CostantsColomb.AZCOLOM).append(
                            " and mpalwt=3  and mpitno like 'PAN%' ) a1     INNER JOIN   \n ").append(
                     " ( select mhitno ,mhcyp6,mhusqt  from mitsta ").append(
                     " where mhcono=").append(CostantsColomb.AZCOLOM).append(     
                     " and mhwhlo='M13' and mhcyp6=").append(JDBCDataMapper.objectToSQL(annoMese)).append(         
                     " and mhitno like 'PAN%' and mhitno not like 'PANSC%' ").append(           
                     " and mhusqt>0 ) a2  ").append(
                " on a1.mpitno =a2.mhitno ) C \n ").append(
                  "  on b.pnp=c.pnp \n").append(
             " order by 1");
    
    return qry.toString();
  }
  
  
  
//  qry.append("select TRIM(pnp), TRIM(a.mhitno) as pan, mhcyp6, mhusqt , sfridoOtm ,supDisp   ").append(
//               " from ").append(
//                "\n ( select mhitno ,mhcyp6,mhusqt ").append(
//                     " from mitsta ").append(
//                     "\n where mhcono=").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(
//                     " and mhwhlo='M13' ").append(
//                     " and mhcyp6=").append(JDBCDataMapper.objectToSQL(annoMese)).append(
//                     " and mhitno like 'PAN%' and mhitno not like 'PANSC%' ").append(           
//                     " and mhusqt>0 ) a, ").append(
//                "\n (select distinct mpitno, mppopn as pnp from mitpop ").append(                
//                  " where mpcono=").append(CostantsColomb.AZCOLOM).append(
//                  " and mpalwt=3 ").append(
//                  " and mpitno like 'PAN%' ").append(") b , ").append(
//                  //" and mppopn like 'PNP%' 
//               "\n (select zscpnp,max(zsitno) as coda,sum(zssupd+zsspsp-zssupu-zssfis-zssprf) as sfridoOtm , sum(zssupd) supDisp  ").append(
//                  " from ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(SfridiCdlDettaglio.TABNAME).append(
//                  "\n where zscono=").append(CostantsColomb.AZCOLOM).append(
//                  " and zsplgr=").append(JDBCDataMapper.objectToSQL(ISfridoInfo.OTMGALP2)).append(
//                  " and zsdtrf>=").append(JDBCDataMapper.objectToSQL(dataIni)).append(
//                  " and zsdtrf<=").append(JDBCDataMapper.objectToSQL(dataFin)).append( 
//                  " group by zscpnp ) c ").append(
//             " where a.mhitno=b.mpitno ").append(
//             " and b.PNP=c.zscpnp ").append(
//             " order by 1");
  
  
}
