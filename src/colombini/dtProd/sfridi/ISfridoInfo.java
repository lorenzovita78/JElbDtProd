/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import exception.ElabException;
import java.util.Date;
import java.util.List;

/**
 * Interfaccia per gestire i dati relativi allo sfrido
 * @author lvita
 */
public interface ISfridoInfo {

  public static final String OTMR1P1 = "01008";
  public static final String OTMR1P0 = "01035";
  public static final String OTMGALP0 = "01042";
  public static final String OTMGALP2 = "01000";
  public static final String SEZR2P1 = "01004";
  
  public static final String SEZR1P4 = "01083";
  
  public static final String COMBICUTR1P4 = "01088";
  
  
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException;
 
  
}
