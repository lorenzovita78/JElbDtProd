/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryColliCommessaMovex extends  CustomQuery {

  public final static String COMMESSA="COMMESSA";
  public final static String ARTICOLO="ARTICOLO";
  public final static String ARTICOLI="ARTICOLI";
  public final static String LINEALOG="LINEALOG";
  public final static String LINEELOG="LINEELOG";
  public final static String NUMCOLLO="NUMCOLLO";
  public final static String RAGGRARTI="RAGGRARTI";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer qry=new StringBuffer();
    
    String select=" select CLNCOL, CLZONA, CLAMGS, CLNROR, CLRIGA,"
            + "CLRIFN, CLRIFD, CLPADR, CLARTI, CLDESC, CLDSVL, CLLINP, CLDIM1, CLDIM2, CLDIM3,"+
            "\n CLCOLO, CLPEDA, CLTOTP, CLBARC, CLVOLC, CLSTRL, CLSTRD, CLLVS1,CLLVS2 ";
    String groupby="";
    String commessa=getFilterSQLValue(COMMESSA);
    
    
    if(isFilterPresent(RAGGRARTI)){
      select=" select trim(CLARTI),count(*) NUMART ";
      groupby=" group by CLARTI ";
    }  
    
    qry.append(select).append(
               "\n FROM mcobmoddta.scxxxcol ").append(
               "\n where 1=1 ").append(
               " and  clcomm=").append(commessa);
    
    if(isFilterPresent(ARTICOLO)){
      qry.append(" and CLARTI=").append(getFilterSQLValue(ARTICOLO));
    }else if (isFilterPresent(ARTICOLI)){
      qry.append(addAND(inStatement("CLARTI", ARTICOLI)));
    }
    
    if(isFilterPresent(LINEALOG)){
      qry.append(addAND(eqStatement("CLLINP", LINEALOG)));
    }else if (isFilterPresent(LINEELOG)){
      qry.append(addAND(inStatement("CLLINP", LINEELOG)));
    }
    
    if(isFilterPresent(NUMCOLLO)){
      qry.append(" and CLNCOL=").append(getFilterSQLValue(NUMCOLLO));
    }
    
    qry.append(" AND CLNART<>0");
    
    qry.append(groupby);
    
    return qry.toString();
  }
  
  
  
}
