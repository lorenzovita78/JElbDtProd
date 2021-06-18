/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P1;

import colombini.xls.FileXlsDatiProdStd;



/**
 *
 * @author lvita
 */
public class XlsMawFianchi2 extends FileXlsDatiProdStd{

  public XlsMawFianchi2(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(16);
  }

  
  
  
  
  
}
