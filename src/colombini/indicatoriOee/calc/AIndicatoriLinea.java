/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.calc;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class AIndicatoriLinea implements ICalcIndicatoriOeeLinea{

  
  
  @Override
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
 
  /**
   * Carica i dati standard della linea dalla nuova gestione dei dati di produzione 
   * sviluppata su portale aziendale
   * @param con
   * @param data
   * @param parameter 
   */
  public void loadDataStd(Connection con ,IIndicatoriOeeGg beanIoee ){
    
  }
  
  
  public Integer getMinutiProdImpiantoGg (Connection con ,String cdl,Date data){
    Integer min=Integer.valueOf(0);
    
    return min;
  }
  
  public Integer getMinutiImprodImpiantoGg (Connection con ,String cdl,Date data){
    Integer min=Integer.valueOf(0);
    
    return min;
  }
  
  public Map getMapFermiGg(Connection con ,String cdl,Date data){
    Map map=new HashMap();
    
    
    return map;
  }
  
  public List getListTurniGg(Connection con ,String cdl,Date data){
    List turni=new ArrayList();
    
    
    return turni;
  }
  
  
  public List getListFermiGg(Connection con ,String cdl,Date data){
    List fermi=new ArrayList();
    
    
    return fermi;
  }
 
  
  //public getInfoGGLinea()
  
}
