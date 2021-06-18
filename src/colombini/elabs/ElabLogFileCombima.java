/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import elabObj.ElabClass;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 * elaborazione per aggiornare il file di Log delle combima
 * @author lvita
 */
public class ElabLogFileCombima extends ElabClass{

  
 
  
  @Override
  public Boolean configParams() {
   
    
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBLOGFILETOTCOMB);
    
    String pathFileDestCombimaR1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDESTCOMBIMAR1P0));
    String pathFileSourceCombimaR1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESOURCECOMBIMAR1P0));
    String pathFileDestCombima1R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDESTCOMBIMA1R1P1));
    String pathFileSourceCombima1R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESOURCECOMBIMA1R1P1));
    String pathFileDestCombima2R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDESTCOMBIMA2R1P1));
    String pathFileSourceCombima2R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESOURCECOMBIMA2R1P1));
    
    String sourceFileCombima=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILESOURCECOMBIMA));
    
    String fileTotCombimaR1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILELOGCOMBIMAR1P0));
    String fileTotCombima1R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILELOGCOMBIMA1R1P1));
    String fileTotCombima2R1P1=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILELOGCOMBIMA2R1P1));
    
    String pathFileSourceSchelling1R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESOURCESCHELLING1R1P0));
    String pathFileSourceSchelling2R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESOURCESCHELLING2R1P0));
    String sourceFileSchelling=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILESOURCESCHELLING));
    
    String fileTotSchelling1R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILELOGSCHELLING1R1P0));
    String fileTotSchelling2R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.NAMEFILELOGSCHELLING2R1P0));
    String pathFileDestSchelling1R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDESTSCHELLING1R1P0));
    String pathFileDestSchelling2R1P0=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDESTSCHELLING2R1P0));
    
    //Combima
    gestFileLogLinea(pathFileSourceCombima1R1P1, pathFileDestCombima1R1P1, sourceFileCombima, fileTotCombima1R1P1 , "Combima1R1P1");
    
    gestFileLogLinea(pathFileSourceCombima2R1P1, pathFileDestCombima2R1P1, sourceFileCombima, fileTotCombima2R1P1 , "Combima2R1P1");
    
    gestFileLogLinea(pathFileSourceCombimaR1P0, pathFileDestCombimaR1P0, sourceFileCombima, fileTotCombimaR1P0,"CombimaR1P0");
    
    //Schelling
    gestFileLogLinea(pathFileSourceSchelling1R1P0, pathFileDestSchelling1R1P0, sourceFileSchelling, fileTotSchelling1R1P0 , "Schelling1R1P0");
    
    gestFileLogLinea(pathFileSourceSchelling2R1P0, pathFileDestSchelling2R1P0, sourceFileSchelling, fileTotSchelling2R1P0 , "Schelling1R1P0");
    
  }
  
  
  
  private void gestFileLogLinea(String source,String dest,String fileSource,String fileDest,String linea){
    if(StringUtils.IsEmpty(source) || StringUtils.IsEmpty(dest)|| StringUtils.isEmpty(fileSource)  || StringUtils.isEmpty(fileDest)){
      addWarning("Accodamento file per linea "+linea+" parametri non completi ");
      return;
    }
    
    try {
      boolean ok=false;
      String fSource=source+fileSource;
      ok=FileUtils.appendFile(fSource, dest+fileDest);
      
      
      if(ok){
        String ora=DateUtils.DateToStr(new Date(), "yyyyMMddHHmmss");
        FileUtils.copyFile(fSource, dest+"temp/"+linea+"_"+ora+".txt");
        boolean b=new File(fSource).delete();
        if(b){
          _logger.info("Cancellazione file"+fSource+" eseguita \n");
          addInfo("Cancellazione file"+fSource+" eseguita \n");
        }
        
      }  
      else{
        addWarning("Accodamento file non eseguito per linea "+linea+" \n");
      }  
    
    } catch (IOException ex) {
        _logger.error("Errore in fase di append per linea "+linea+" --> "+ex.getMessage());
        addError("Errore in fase di append del file per linea "+linea +" --> "+ex.toString());
    } 
    catch (ParseException p){
      _logger.error("Errore in fase di conversione dei dati per linea "+linea+" --> "+p.getMessage());
       addWarning("Errore in fase di conversione dei dati per linea "+linea +" --> "+p.toString());
    }
    
    
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabLogFileCombima.class);   
  
}
