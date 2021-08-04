/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.xls.indicatoriOee.G1P2;

/**
 *
 * @author lvita
 */
public class XlsNuoveForatriciBiesse extends XlsForatrici {

  public XlsNuoveForatriciBiesse(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(10);
  }
  
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(2);
  }

 
 
  
}
