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
public class XlsPant01046 extends XlsPantografiStd  {

  public XlsPant01046(String fileName) {
    super(fileName);
  }
  
  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(13);
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(16);
  }
  
   @Override
  public Long getTempoCambioBordiGg(Date data) {
    return Long.valueOf(0);
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
