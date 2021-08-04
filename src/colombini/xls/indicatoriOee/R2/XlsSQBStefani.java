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
public class XlsSQBStefani extends FileXlsDatiProdStd{

  public XlsSQBStefani(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(14);
  }

  
  
  
}
