/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryProdOrNnFromTAP extends CustomQuery{

    public final static String FT_PZNONPROD="FT_PZNONPROD";
    
    @Override
    public String toSQLString() throws QueryException {
        StringBuffer q=new StringBuffer();
        String ftAzienda=getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA);
        String ftCdl=getFilterSQLValue(FilterQueryProdCostant.FTCDL);
        
        
        String sub1=" select TPIDPZ,TPBARP,tpnote,TPDTIN  \n" +
                    "  from mcobmoddta.ZTAPCP inner join mcobmoddta.ZTAPCU on tputin=tuutrf\n" +
                    " where tuplgr= "+ftCdl;
        
        String sub2=" select a.ppplgr codll,a.PPPLNM descll, b.ppplgr cdl ,b.PPPLNM desccdl,c.clfact stab,c.clplan plan, b.ppdept cdccdl , b.ppwcre respcdl \n" +
                    "  from mvxbdta.mpdwct  a inner join mvxbdta.mpdwct b on a.ppcono=b.ppcono and a.ppfaci=b.ppfaci and a.ppiiwc=b.ppplgr \n" +
                    " inner join mcobmoddta.zdpwcl c  on b.ppplgr=c.clplgr\n" +
                    " where a.ppcono="+ftAzienda;
        
        q.append("SELECT distinct ticono, tiplgr, tibarp, tidtco, ticomm, tincol, tinart, tilinp, ifnull(descll,''), tiorno, ticonn, ticart, tidart, tistrd,\n").append(
                  " ifnull(cdl,tilinp), current timestamp  ").append(
                 "\n FROM  mcobmoddta.ztapci  ").append( 
                 "\n left outer join  ( ").append(sub1).append( " ) d on tibarp=tpbarp  ").append(
                 "\n left outer join  ( ").append(sub2).append( " ) e on tilinp=e.codll ").append(        
        "\n WHERE 1=1 ").append(
        "\n  and TICONO =").append(ftAzienda).append(
        "\n  and TIPLGR =").append(ftCdl).append(                
        "\n  and TIDTCO =").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN));
        
        if(isFilterPresent(FT_PZNONPROD))
          q.append("\n  and TPIDPZ is null");        
        
        return q.toString();
    }
   
    
    
}
