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
public class XlsPant01045 extends XlsPantografiStd {

  public XlsPant01045(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(14);
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(17);
  }

  @Override
  public Long getTempoCambioBordiGg(Date data) {
    return Long.valueOf(0);
  }

  @Override
  public Long getTempoMicrofermiGg(Date data) {
    return Long.valueOf(0);
  }
  
  
  
}
