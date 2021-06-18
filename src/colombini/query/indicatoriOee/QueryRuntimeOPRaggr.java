/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

/**
 *
 * @author lvita
 */
public class QueryRuntimeOPRaggr extends QueryRuntimeOP{

  @Override
  public String getSelect1() {
    StringBuffer select1Raggr=new StringBuffer(
      "\n SELECT count(distinct vomfno) as nped,int(mwoptr.djtrtm/10000) as ora ,").append( 
              " sum(DJSCQT) as qt_scarto , sum(DJMAQT - DJSCQT) as qt_buona,").append(
              " sum(vopiti * (DJMAQT - DJSCQT)) as tmp_buono,  ").append( 
              " sum(vopiti * (DJSCQT)) as tmp_scarto");
    
    return  select1Raggr.toString();
  }

  @Override
  public String getSelect2(String valRipartizione) {
    StringBuffer select2Raggr=new StringBuffer(
      "\n select int( count(distinct vomfno) * ").append(valRipartizione).append(" ) as nped,").append( 
         " 25 as ora, 0 as qt_scarto,").append(
         " int(sum(vhmaqt)*").append(valRipartizione).append(" ) as qt_buona,").append(
         " int(sum(vhmaqt * vopiti)*").append(valRipartizione).append(" )  tmp_buono,  ").append(
         " 0 as tmp_scarto ");
    
    return  select2Raggr.toString();
  }

  @Override
  public String getGroupBy1() {
    return "\n group by int(mwoptr.djtrtm/10000) ";
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
    return " ";
  }
  
}
