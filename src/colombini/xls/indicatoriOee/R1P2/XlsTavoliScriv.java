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
public class XlsTavoliScriv extends FileXlsDatiProdStd {

  public XlsTavoliScriv(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(14);
  }

  @Override
  public Integer getNumColFermi() {
    return Integer.valueOf(2);
  }
 
  
  
  
  
  
  
}
