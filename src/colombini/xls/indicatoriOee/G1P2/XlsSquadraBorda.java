/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;

import colombini.xls.FileXlsDatiProdStd;
import fileXLS.FileExcelJXL;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class XlsSquadraBorda extends FileXlsDatiProdStd {
 
  Map setupSquadraborda;
  
  public XlsSquadraBorda(String fileName){
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }

  @Override
  public Integer getColFineFermi() {
    return FileExcelJXL.POS_VALFERMI2;
  }
  
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(5);
  }
  

  
  
}
