/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;


import colombini.xls.FileXlsDatiProdStd;
import java.util.Date;

/**
 *
 * @author lvita
 */
public class XlsImaSezionatrici extends FileXlsDatiProdStd {

  public XlsImaSezionatrici(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }
 
  public Integer getClnCambioSagoma(){
    return new Integer(17); 
  }
  
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(5);
  }
  
  
  public Long getTempoCambioSagoma(Date data){
    Integer rigaIni=this.getRigaInizioGg(data);
    Integer rigaFine=this.getRigaFineGg(rigaIni);
    return getValNumColonna(rigaIni, rigaFine, getClnCambioSagoma()).longValue();
  }
  

  
}
