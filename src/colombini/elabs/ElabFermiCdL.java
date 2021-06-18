/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.costant.CostantsColomb;
import colombini.model.CausaliLineeBean;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabFermiCdL extends ElabClass{

  
  private Date dataInizio=null;
  private Date dataFine=null;
  
  private List<String> lineeToElab=null;
  
  
  
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
    Map causaliLinee=new HashMap();
    String tipoCausale=CausaliLineeBean.FLAGDESC;
    //tipoCausale=CausaliLineeBean.FLAGCODICE;
    
    for(String codLinea:lineeToElab){
      Map causaliLinea=CausaliLineeBean.getMapCausaliLinea(con, CostantsColomb.AZCOLOM, codLinea, tipoCausale);
      causaliLinee.put(codLinea,causaliLinea);
      
    }
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabFermiCdL.class); 
  
}
