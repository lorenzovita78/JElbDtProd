/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.NomiLineeColomb;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.logFiles.R1P2.LogFileSupervisoreAlberti;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class AvProdForAlberti extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
    Map commMap=getDatiCommFromLogFile(conAs400);
    storeDatiProdComm(conAs400, commMap);
    
  }
  
  
  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
  }

  
  
 private Map getDatiCommFromLogFile(Connection con) throws DatiCommLineeException{
   Map commMap=new HashMap();
   Map commMap2=new HashMap();
   String fileName="";
   String fileName2="";
   
   //file già spostato su server
   fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.FORALBERTI,getDataRifCalc());
   //fileName=InfoMapLineeUtil.getLogFileSourceGgLinea(NomiLineeColomb.FORALBERTI,getDataForLogFile());
   //file ancora sulla macchina
   fileName2=InfoMapLineeUtil.getLogFileSourceGgLinea(NomiLineeColomb.FORALBERTI,getDataRifCalc());
   try{
     if(!FileUtils.exists(fileName))
       fileName=fileName2;
   } catch(IOException i){
     _logger.warn("File "+fileName +" non trovato");
   }
   _logger.info("Processing log file "+fileName);
   fileName2=fileName2.replace("OLD","Err4");
   
   LogFileSupervisoreAlberti lf= new LogFileSupervisoreAlberti(fileName);
   LogFileSupervisoreAlberti lf2= new LogFileSupervisoreAlberti(fileName2);
   
   try {
     commMap=lf.elabFileForAvProd();
     
   } catch (IOException ex) {
     _logger.error("Errore in fase di lettura del file di log"+fileName+" --> "+ex.getMessage());
     throw new DatiCommLineeException("Impossibile leggere il file di log "+fileName);
   }
   
   try{
     commMap2=lf2.elabFileForAvProd();
     
     if(!commMap2.isEmpty()){
       Set keys=commMap2.keySet();
       Iterator iter=keys.iterator();
       while(iter.hasNext()){
         Integer comm2=(Integer) iter.next();
         if(commMap.containsKey(comm2)){
           commMap.put(comm2, ((Integer)commMap.get(comm2))+((Integer)commMap2.get(comm2)));
         }else{
           commMap.put(comm2, commMap2.get(comm2));
         }
       }
     }
     
   } catch (IOException ex) {
     _logger.error("Errore in fase di lettura del file di log"+fileName+" --> "+ex.getMessage());
     
   }
   
   return commMap;
 }
  
 
 
  
  
 private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdForAlberti.class);  

 
}