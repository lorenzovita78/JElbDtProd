/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P0;

import java.util.Date;

/**
 *
 * @author lvita
 */
public class XlsPant01043 extends XlsPantografiStd {

  public XlsPant01043(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }

  @Override
  public Long getTempoMicrofermiGg(Date data) {
    return Long.valueOf(0);
  }

  @Override
  public Long getNPzVitalityGg(Date data) {
    return Long.valueOf(0);
  }
 
  
  
}
