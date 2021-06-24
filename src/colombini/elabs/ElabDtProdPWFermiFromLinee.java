/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.costant.CostantsColomb;
import colombini.model.CausaliLineeBean;
import db.persistence.PersistenceManager;
import colombini.model.datiProduzione.IFermiLinea;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.datiProduzione.InfoTurniCdL;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.ElabException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe che storicizza i dati di Produzione settimanalmente o manualmente
 * @author lvita
 */
public class ElabDtProdPWFermiFromLinee extends ElabClass{

  
 
  
  private Date dataInizio;
  private Date dataFine;
  
  
  
  private List<String> lineeElab;
  
  
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
     
    if(dataInizio==null)
      return Boolean.FALSE;
    
    
    if(dataFine==null)
      dataFine=dataInizio;
    
    //---parametri non obbligatori
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeElab=ArrayUtils.getListFromArray(linee.split(","));
    }
  
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Map propsElab=getElabProperties();
    PersistenceManager pm=new PersistenceManager(con);
    //caricamento linee da processare
    for(String linea:lineeElab){
       String classLineaS=(String) propsElab.get(NameElabs.CLASSLINEAFERMIDTPROD+linea);
       String keyCaus=CausaliLineeBean.FLAGCODICE;
       if(propsElab.get(NameElabs.KEYCAUSLINEAFERMIDTPROD+linea)!=null)
       keyCaus=(String) propsElab.get(NameElabs.KEYCAUSLINEAFERMIDTPROD+linea);
       Map causaliLinea=CausaliLineeBean.getMapCausaliLinea(con, CostantsColomb.AZCOLOM, linea, keyCaus);
       
      try {
        Object classeCdl=Class.forName(classLineaS).newInstance();
        Date datatmp=dataInizio;
        while(DateUtils.beforeEquals(datatmp, dataFine)){
            try{
              InfoTurniCdL infoT=new InfoTurniCdL(CostantsColomb.AZCOLOM, linea, datatmp);
              infoT.retriveInfo(con);
              if(infoT.getIdTurno() != null) //Faccio storeDtFromBean solo se trovo info sul turno! 
                 {
                    List<InfoFermoCdL> listfermilinea=((IFermiLinea)classeCdl).getListFermiLinea(con,infoT,causaliLinea);
                    for(InfoFermoCdL fermo:listfermilinea){
                      try{
                        pm.storeDtFromBean(fermo);
                      } catch(SQLException s){
                        addError("Attenzione errore in fase di salvataggio del fermo+ "+fermo.toString()+" -->"+s.getMessage());
                      }
                    }
                    datatmp=DateUtils.addDays(datatmp, 1);
                 }
            } catch(SQLException s){
              addError("Impossibile reperire le informazioni del Cdl "+linea+" per il giorno"+datatmp+" >>"+s.getMessage());
//            } catch (ElabException e){
//              addError("Errore in fase di generazione dati fermi per "+linea+" per il giorno"+datatmp+" >>"+e.getMessage());
           }
            
            
        }    
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
        addError("Attenzione class non trovata per linea "+linea+"-->"+ex.getMessage());
      }
    }
    
    //mappa che indica per ogni Bu le commesse elaborate nel Periodo
    
    //se il calcolo settimanale e il periodo è più di una settimana ciclo sulle week
   
    
  }
  
  
  
  
  
  

  
  
  
    
    
  
  private static final Logger _logger = Logger.getLogger(ElabDtProdPWFermiFromLinee.class); 

}
