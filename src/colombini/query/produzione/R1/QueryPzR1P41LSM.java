/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class QueryPzR1P41LSM extends CustomQuery {

  public final static String FT_ULTIMAFASEP4="FT_ULTIMAFASEP4";
  public final static String FT_LSM_CUCINE="FT_LSM_CUCINE";
 
  public final static String TAB_AS400_1="mcobmoddta.zpdsum";
  public final static String TAB_AS400_2="mcobmoddta.scxxxcol";
  public final static String TAB_AS400_3="MVXBDTA.mitbal";
  public final static String TAB_AS400_4="MVXBDTA.mitmas";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
      
  
      sql.append("select * from OPENQUERY(AS400,'select trim(pselno), 1 n_collo,''P2MF1'' linea, clboxn, clpeda, psridn, 0, ''0'', left(cldesc , 25) concat '' '' concat sum(int(pscnqt)) desc1,cldesc concat '' QTA: '' concat sum(int(pscnqt)) desc2,trim(psproj) concat trim(pselno) barcode \n").append(
                        " FROM \n").append(TAB_AS400_1).append(
                        " INNER JOIN ").append(TAB_AS400_2).append(" on psproj=clcomm and pselno=clncol \n").append(
                        " INNER JOIN (select mbcono, mbwhlo, mbitno, mbsuwh FROM ").append(TAB_AS400_3).append(
                        " \n WHERE mbpuit=3 and mboplc=3 and mbresp<>''TSTCMP'') bal1 \n"        
                        ).append( " on pscono=bal1.mbcono and psmtno=bal1.mbitno and pswhlo=bal1.mbwhlo \n"
                        ).append( " INNER JOIN (select mbcono, mbwhlo, mbitno FROM ").append(TAB_AS400_3).append(
                        " \n where mbpuit=1 and mboplc=3 and mbresp<>''TSTCMP'') bal2 on pscono=bal2.mbcono and psmtno=bal2.mbitno and bal2.mbwhlo=bal1.mbsuwh \n"
                        ).append( " INNER JOIN ").append(TAB_AS400_4).append(" on pscono=mmcono and psmtno=mmitno \n"
                        ).append( " WHERE pscono=30 and mmcfi4<>''LOT1'' and clnart=0 and bal2.mbwhlo=''MP4''\n"
                        ).append( "and psproj= '").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)
                        ).append( "' group by pscono, psproj, pselno, pscdld, clboxn, clpeda, psridn, clpadr, cldesc') \n");
      

      
    return sql.toString();
  }
  
}


