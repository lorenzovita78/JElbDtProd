/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.calc;

import java.util.Date;
import java.util.List;
import db.persistence.IBeanPersSIMPLE;

/**
 * Interfaccia che serve per gestire i dati relativi agli Oee giornalieri di una linea
 * @author lvita
 */
public interface IIndicatoriOeeGg  extends IBeanPersSIMPLE {
  
  
  public Integer getAzienda();
  
  public String getCdLav();
  
  public Date getData();
  
  public Long getDataRifN();
  
  
  public Long getTTotImpianto();
  
  public Long getTDispImpianto();
  
  public Long getTImprodImpianto();
  
  public Long getTProdImpianto();
  
  
  public Long getTGuasti();
  
  public Long getTSetup();
  
  public Long getTSetup2();
  
  public Long getTPerditeGestionali();
   
  public Long getTMicroFermi();
  
  public Long getTVelocitaRidotta();
  
  public Long getTRilavorazioni();
  
  public Long getTScarti();
  
  public Long getTScarti2();
  
  
  public Long getTExt1();
  
  public Long getTExt2();
  
  public Long getTExt3();
  
  
  public Long getTRun();
  
  //tempo fattore ciclo
  public Long getTRun2();
  
  //tempo fattore ciclo
  public Long getTRun3();
  
  public Long getTPerditeNnRilevate();

  
  public Long getNumPzTot();
  
  public Long getNumPzTurni();
  
  public Long getNumGuasti();
  
  public Long getNumScarti();
  
  public Long getNumRilavorazioni();
  
  public Long getNumBande();
  
  public Long getNumCicliLav();
  
  
  public Double getOee();
  
  public Double getTeep();
  
  public Double getMtbf();
  
  public Double getMttr();
  
  public Long getNumeroScarti();
  
  public Long getNumeroCicliLav();
  
  
  
  public List<String> getWarnings();
  
  public List<String> getErrors();
  
  
  
  public String getInfo();
   
  
  
}
