/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P0;


import colombini.xls.FileXlsDatiProdStd;
import java.util.Date;

/**
 *
 * @author lvita
 */
public class XlsPantografiStd extends FileXlsDatiProdStd {

  
                                                                         
  public XlsPantografiStd(String fileName) {
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
  

  public Integer getClnCambioBordi(){
    return new Integer(13);
  }
  
  public Integer getClnPzVitality(){
    return new Integer(13);
  }
  
  public Integer getClnMicrofermi1(){
    return new Integer(19);
  }
  
  public Integer getClnMicrofermi2(){
    return new Integer(21);
  }
  
  
  
  public Long getTempoCambioBordiGg(Date data){
    if(getClnCambioBordi()<=0)
      return Long.valueOf(0);
    
    Integer rigaIni=this.getRigaInizioGg(data);
    Integer rigaFine=this.getRigaFineGg(rigaIni);
    return getValNumColonna(rigaIni, rigaFine, getClnCambioBordi()).longValue();
  }
  
  public Long getNPzVitalityGg(Date data){
    if(getClnPzVitality()<=0)
      return Long.valueOf(0);
    
    Integer rigaIni=this.getRigaInizioGg(data);
    Integer rigaFine=this.getRigaFineGg(rigaIni);
    return getValNumColonna(rigaIni, rigaFine, getClnPzVitality()).longValue();
  }
  

  public Long getTempoMicrofermiGg(Date data) {
    if(getClnMicrofermi1()<=0 || getClnMicrofermi2()<=0)
      return Long.valueOf(0);
    
    Long tmicro=Long.valueOf(0);
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFin=getRigaFineGg(rigaIni);
    tmicro+=getValNumColonna(rigaIni, rigaFin, getClnMicrofermi1()).longValue();
    tmicro+=getValNumColonna(rigaIni, rigaFin, getClnMicrofermi2()).longValue();
    
    
    return tmicro/2;        
  }
}
