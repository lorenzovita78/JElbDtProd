/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.costant.NomiLineeColomb;
import colombini.dtProd.materiali.UtilizzoMaterialiCalc;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe che serve per calcolare l'utilizzo degli utensili/materiali di 
 * alcune linee di produzione
 * @author lvita
 */
public class ElabCalcUtlMaterialiProd extends ElabClass{
  
  private Date dataInizio=null;
  private Date dataFine=null;
  private List<String> lineeToElab=null;
  
  public ElabCalcUtlMaterialiProd(){
    lineeToElab=getAllLineeToElab();
  }
  
  @Override
  public Boolean configParams() {
     Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    //parametriFacoltativi 
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    List<Date> giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    
    UtilizzoMaterialiCalc calc=new UtilizzoMaterialiCalc();
    for (String linea:lineeToElab){
      calc.elabDatiLinea(con, giorni, linea);
    }
    addMessagesToElab(calc.getMsg());
  }
  
  
  
  private List<String> getAllLineeToElab(){
    List<String> linee=new ArrayList();
    
    linee.add(NomiLineeColomb.SCHELLLONG_GAL);
    linee.add(NomiLineeColomb.SQBL13266);
    linee.add(NomiLineeColomb.SQBL13268);
    linee.add(NomiLineeColomb.IMAGAL_13200);
    linee.add(NomiLineeColomb.IMAGAL_13267);
    linee.add(NomiLineeColomb.IMAGAL_13269);
    
    
    linee.add(NomiLineeColomb.COMBIMAR1P0);
    linee.add(NomiLineeColomb.SCHELLING1R1P0);
    linee.add(NomiLineeColomb.SCHELLING2R1P0);
    
    linee.add(NomiLineeColomb.COMBIMA1R1P1);
    linee.add(NomiLineeColomb.COMBIMA2R1P1);
    linee.add(NomiLineeColomb.SCHELLING1R1P1);
    linee.add(NomiLineeColomb.SCHELLING2R1P1);
    
    
    
    return linee;
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabCalcUtlMaterialiProd.class); 
}
