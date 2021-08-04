/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;

/**
 *
 * @author lvita
 */
public class XlsFPantografoMorbidelli extends XlsForatrici{

  public XlsFPantografoMorbidelli(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(10);
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
  public Integer getColIniSetup() {
    return Integer.valueOf(13);
  }

  @Override
  public Integer getColFinSetup() {
    return getColIniSetup();
  }
  
  
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(2);
  }
}
