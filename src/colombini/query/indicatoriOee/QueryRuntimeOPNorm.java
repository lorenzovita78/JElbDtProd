/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

/**
 *
 * @author lvita
 */
public class QueryRuntimeOPNorm extends QueryRuntimeOP{

  @Override
  public String getSelect1() {
    StringBuffer select1=new StringBuffer(
                " SELECT distinct vomfno as nped,djtrtm as ora ,").append( 
                " DJSCQT as qt_scarto ,(DJMAQT - DJSCQT) as qt_buona, ").append(
                " (vopiti * (DJMAQT - DJSCQT)) as tmp_buono,  ").append( 
                " (vopiti * (DJSCQT)) as tmp_scarto , ").append(       
                " DJPLGR as centro_princ, DJDPLG as centro_alt ");
    
    return select1.toString();
  }

  @Override
  public String getSelect2(String valRipartizione) {
    StringBuffer select2=new StringBuffer(
           " select distinct vomfno as nped, ").append( 
            " 250000 as ora, 0 as qt_scarto,").append(
            "  vhmaqt as qt_buona, ").append(
            " (vhmaqt * vopiti)  tmp_buono,   0 as tmp_scarto  , ").append(
            " ifnull(DJPLGR,'XX') as centro_princ ,ifnull(DJDPLG,'XX') as centro_alt");
    
    return  select2.toString();
  }

  @Override
  public String getGroupBy1() {
    return " ";
  }

  @Override
  public String getGroupBy2() {
    return " ";
  }

  @Override
  public String getOrderBy1() {
    return " ";
  }

  @Override
  public String getOrderBy2() {
    return " ORDER BY ORA ";
  }
  
}
