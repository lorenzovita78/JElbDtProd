/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;

import fileXLS.FileExcelJXL;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class XlsForatriceMaw extends XlsForatrici{

  public XlsForatriceMaw(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getColIniFermi() {
    return FileExcelJXL.POS_CAUSFERMI1;
  }

  @Override
  public Integer getColFineFermi() {
    return FileExcelJXL.POS_VALFERMI2;
  }
  
  
  
  public Integer getColMicroFermi1(){
    return Integer.valueOf(18);
  }
  
  public Integer getColMicroFermi2(){
    return Integer.valueOf(20);
  }

  @Override
  public Long getTempoSetupGg(Date data) {
    return Long.valueOf(0);
  }

  @Override
  public Long getTempoTotFermiGg(Map fermi) {
    return Long.valueOf(0);
  }

  
  
  @Override
  public Long getTempoMicrofermiGg(Date data) {
    Long tmicro=Long.valueOf(0);
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFin=getRigaFineGg(rigaIni);
    tmicro+=getValNumColonna(rigaIni, rigaFin, getColMicroFermi1()).longValue();
    tmicro+=getValNumColonna(rigaIni, rigaFin, getColMicroFermi2()).longValue();
    
    
    return tmicro;        
  }
  
  
          
  
}
