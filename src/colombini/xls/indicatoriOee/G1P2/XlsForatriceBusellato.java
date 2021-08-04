/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;

/**
 *
 * @author lvita
 */
public class XlsForatriceBusellato extends XlsForatrici{

  public XlsForatriceBusellato(String fileName) {
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
