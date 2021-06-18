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
public class XlsPant01044 extends XlsPantografiStd {

  public XlsPant01044(String fileName) {
    super(fileName);
  }
  
  
  @Override
  public Long getNPzVitalityGg(Date data) {
    return Long.valueOf(0);
  }
  
  
}
