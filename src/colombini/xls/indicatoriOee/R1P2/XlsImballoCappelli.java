/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P2;

import colombini.xls.FileXlsDatiProdStd;

/**
 *
 * @author lvita
 */
public class XlsImballoCappelli extends FileXlsDatiProdStd {

  public XlsImballoCappelli(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }
 
  
  
  
  
}
