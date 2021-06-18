/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R2;

import colombini.xls.FileXlsDatiProdStd;

/**
 *
 * @author lvita
 */
public class XlsHomagPost extends FileXlsDatiProdStd{

  public final static Integer COLSETUPAGG=Integer.valueOf(19);
  
  public XlsHomagPost(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(15);
  }

  
  public Long getMinutiSetupAgg(Integer rigaIni,Integer rigaFin){
    return getValNumColonna(rigaIni, rigaFin, COLSETUPAGG).longValue();
  }
  
}
