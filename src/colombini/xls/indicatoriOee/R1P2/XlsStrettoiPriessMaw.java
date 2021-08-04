/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P2;

import colombini.xls.FileXlsDatiProdStd;
import java.util.Date;

/**
 * Classe per gestire i file xls dei due strettoi (1 sola classe perchè la struttura è la stessa
 * @author lvita
 */
public class XlsStrettoiPriessMaw extends FileXlsDatiProdStd{

  public final static Integer COL1MICROF=Integer.valueOf(18);
  public final static Integer COL2MICROF=Integer.valueOf(20);
  
  
  public XlsStrettoiPriessMaw(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(30);
  }
  
  
  public Long getTempoMicrofermiGg(Date data) {
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFin=getRigaFineGg(rigaIni);
    
    return getTempoMicrofermi(rigaIni, rigaFin);
  }
  
  public Long getTempoMicrofermi(Integer rigaIni,Integer rigaFin){
    Long tmicro=Long.valueOf(0);
    tmicro+=getValNumColonna(rigaIni, rigaFin, COL1MICROF).longValue();
    tmicro+=getValNumColonna(rigaIni, rigaFin, COL2MICROF).longValue();
    
    return tmicro;        
  }
  
  
}
