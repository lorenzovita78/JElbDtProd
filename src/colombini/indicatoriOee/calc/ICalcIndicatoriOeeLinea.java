/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.calc;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;

/**
 * Interfaccia che serve per gestire il calcolo degli indicatori oee di una linea
 * @author lvita
 */
public interface ICalcIndicatoriOeeLinea {
  
  public final static String FILEXLS="FILEXLS";
  public final static String CAUSALILINEA="CAUSALILINEA";
  public final static String LISTFERMIOEE="LISTFERMIOEE";
  public final static String LISTFERMITOT="LISTFERMITOT";
  public final static String MAPTOTFERMI="MAPTOTFERMI";
  public final static String LISTCOLLI="LISTCOLLI"; 
  
  public final static String LISTERRORS="LISTERRORS"; 
  public final static String LISTWARNINGS="LISTWARNINGS"; 
  
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con , Date data,String centrodiLavoro, Map parameter)  ;
    
  
  
  
}
