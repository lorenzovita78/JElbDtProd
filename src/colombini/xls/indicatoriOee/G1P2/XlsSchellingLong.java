/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;

import colombini.xls.FileXlsDatiProdStd;


/**
 *
 * @author lvita
 */
public class XlsSchellingLong extends FileXlsDatiProdStd{

  public XlsSchellingLong(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }
 
  
  
  
}
