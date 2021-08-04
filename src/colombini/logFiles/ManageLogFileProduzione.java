/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles;

import colombini.costant.NomiLineeColomb;
import colombini.logFiles.R1P1.LogFileCombima;
import colombini.logFiles.R1P1.LogFileSchelling;
import colombini.logFiles.R1P3.LogFileForAnteRem;
import colombini.util.InfoMapLineeUtil;
import elabObj.MessagesElab;
import exception.ElabException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 * Classe che si preoccupa di generare o copiare i file di log delle varie linee di produzione 
 * @author lvita
 */
public class ManageLogFileProduzione {
  
  
 private static ManageLogFileProduzione instance;
  
  public static ManageLogFileProduzione getInstance(){
    if(instance==null){
      instance= new ManageLogFileProduzione();
    }
    
    return instance;
  }
  
  
  public ManageLogFileProduzione() {
    
  }
  
  /**
   * Genera tanti file di log per i giorni indicati in lista.
   * Cerca tutti i file presenti nel pathSource fornito e verifica quali di questi appartengono alla data che sta elaborando
   * @param giorni
   * @param pathSourceFiles
   * @param nomeLinea
   * @return MessagesElab segnalazioni dell'elaborazione appena conclusa
   */
  public MessagesElab generateLogsFileFromPathSource(List<Date> giorni,String pathSourceFiles,String nomeLinea) {
    FileOutputStream file = null;
    PrintStream output=null;
    List<String> sourceFiles=null;
    BufferedReader br=null;
    MessagesElab msg=new MessagesElab();
    
    
    sourceFiles=FileUtils.getListNFilesFolder(pathSourceFiles);
    
    if(!isParmsOk(sourceFiles, giorni, nomeLinea, msg))
      return msg;
    
    //ciclo sui giorni
    String infoL="";
    for(Date data: giorni){
      String dataS=""; 
      try {
        file=null;
        output=null;
        dataS = DateUtils.DateToStr(data, "yyyyMMdd");
        infoL= "\n GENERAZIONE LOG FILE per linea "+nomeLinea+" e gg "+dataS;
        _logger.info(infoL);
        System.out.println(infoL);
        //creazione file di testo
        file=new FileOutputStream(InfoMapLineeUtil.getLogFileGgLinea(nomeLinea, data));
        output = new PrintStream(file);
        
        for(int i=0;i<sourceFiles.size();i++){
          String sfileTemp=pathSourceFiles+sourceFiles.get(i);
          _logger.debug("Log file: "+sfileTemp);
          File ftemp=new File(sfileTemp);
          
          Date dtFile=new Date(ftemp.lastModified());
          if(DateUtils.daysBetween(data, dtFile)==0){
            br = new BufferedReader(new FileReader(sfileTemp));
            String line = br.readLine();
            while (line != null) {
              output.println(line);
              line = br.readLine();
            }
            br.close(); 
          }  
        }
      } catch (IOException ex) {
         _logger.error("Errore di accesso o generazione del file per il giorno "+dataS+" :"+ex.getMessage());
         msg.addError(infoL+" --> Errore di accesso o generazione del file :"+ex.toString());
      } catch (ParseException ex) {
       _logger.error("!! IMPOSSIBILE CONVERTIRE LA DATA !! NOME DEL FILE NON STANDARD!!");
       msg.addError(infoL+" --> Errore in fase di conversione dei dati :"+ex.toString());
      }finally{
        msg.addInfo(infoL+" conclusa correttamente .");
        if(output!=null)
          output.close();
        try {
          if(br!=null)
            br.close();
          
          if(file!=null)
            file.close();
        } catch (IOException ex) {
          _logger.error("Impossibile chiudere lo stream  -> "+ex.getMessage());
          msg.addError(infoL+" --> Impossibile chiudere lo stream  : "+ex.getMessage());
        }
      }
   }  
    
   return msg; 
  }
  
  
  
  public MessagesElab generateLogFileImpImaR1(List sourceFiles,List<Date> giorni ,String nomeLinea) {
    LogFileSchelling lfS=null;
    ALogFile aLS=null;
    FileOutputStream file = null;
    PrintStream output=null;
    MessagesElab msg=new MessagesElab();
    
    if(!isParmsOk(sourceFiles, giorni, nomeLinea, msg))
      return msg;
   
    //ciclo sui giorni
    String outputFileName;
    String infoL="";
    for(Date data: giorni){
      String dataS=""; 
      outputFileName="";
      String msgS="";
      try {
        dataS = DateUtils.DateToStr(data, "yyyyMMdd");
        infoL="\n GENERAZIONE LOG FILE per "+nomeLinea +" e gg "+dataS;
        _logger.info(infoL);
        //creazione file di testo
        outputFileName=InfoMapLineeUtil.getLogFileGgLinea(nomeLinea, data);
        if(StringUtils.isEmpty(outputFileName)){
          _logger.error("Nome file di output non valorizzato per linea "+nomeLinea +" e giorno "+dataS);
          msg.addError(infoL+" --> Nome file di output non valorizzato per linea ");
          continue;
        }
        
        file = new FileOutputStream(outputFileName);
        output = new PrintStream(file);
        msgS+=" File da generare "+outputFileName;
        for(int i=0;i<sourceFiles.size();i++){
          String fileName=ClassMapper.classToString(sourceFiles.get(i));
          msgS+=" . Lettura file:"+fileName;
          _logger.info("Elaborazione log file: "+fileName);
          aLS=getClassLogFileImpIma(nomeLinea,fileName);
          try {
            aLS.initialize();
            msgS+=" --> "+aLS.createFileLogGg(data,output);
            output.flush();
          } catch (Exception ex) {
            _logger.error(" PROBLEMI NELLA LETTURA DEL FILE:"+fileName+" :"+ex.getMessage());
            msg.addError(infoL+" --> PROBLEMI NELLA LETTURA DEL FILE:"+fileName+" :"+ex.toString());
          } finally {
            aLS.terminate();
          }
        }
      } catch (IOException ex) {
        _logger.error("Errore di accesso o generazione del file per il giorno "+dataS+" :"+ex.getMessage());
        msg.addError(infoL +" --> Errore di accesso o generazione del file  :"+ex.toString());
      } catch (ParseException ex) {
        _logger.error("!! IMPOSSIBILE CONVERTIRE LA DATA !! NOME DEL FILE NON STANDARD!!");
        msg.addError(infoL +" --> Errore in fase di conversione dei dati :"+ex.toString());
      }finally{
        msg.addInfo(infoL +"  conclusa correttamente : [ "+msgS+" ]");
        
        if(output!=null)
          output.close();
        try {
          if(file!=null)
            file.close();
        } catch (IOException ex) {
          _logger.error("Impossibile chiudere lo stream di"+outputFileName+" -> "+ex.getMessage());
        }
      }
   }
    return msg;
  }
  
  
  public MessagesElab loadLogFileAnteRem(List<Date> giorni){
    _logger.info("Generazione FILE LOG ANTE REM R1");
    String infoL="";
    MessagesElab msg=new MessagesElab();
    LogFileForAnteRem lg=new LogFileForAnteRem(InfoMapLineeUtil.getLogFileSourceLinea(NomiLineeColomb.FORANTEREM));
    for(Date data:giorni){ 
      try{
        infoL="\n GENERAZIONE LOG FILE FORATRICE ANTE REM  per gg "+data;
        String msgS=lg.writeLogGg(data);
        if(!StringUtils.isEmpty(msgS))
          msg.addInfo(msgS);
      
      }catch(ElabException e){
        msg.addError("File Log Foratrice Ante Rem non generato per il giorno "+data+" --> "+e.toString());
      }
    }
    
    msg.addInfo(infoL);
    _logger.info("Fine generazione FILE LOG ANTE REM R1");
    return msg;
  }
  
  
  public MessagesElab copyAndRenameFiles(List<Date> giorni,String linea){
    MessagesElab msg=new MessagesElab();
    
    for(Date data:giorni){
       String pathFileS=InfoMapLineeUtil.getLogFileSourceGgLinea(linea, data); 
       String fileDest=InfoMapLineeUtil.getLogFileGgLinea(linea,data);
       MessagesElab tmpMsg=copyAndRenameFile(data, linea, pathFileS, fileDest);
       msg.addMessages(tmpMsg);
    }
    
    
    return msg;
  }
  
  
  /**
   * Data una lista di linee verifica l'esistenza del file e lo copia nel nuovo rinominandolo
   * @param linee 
   */
  public MessagesElab copyAndRenameFile(Date data, String linea,String pathFileC,String pathFileFinal){
    boolean copy=true;
    MessagesElab msg=new MessagesElab();
    String infoL="";
    try{  
      infoL="\n COPIA LOG FILE per linea "+linea +" e gg" +data;
      _logger.info(infoL); 
      File f=new File(pathFileC);
      if(f==null ||  !f.exists()){
        _logger.warn("File da copiare non presente : "+pathFileC);
        msg.addWarning(infoL+" --> File da copiare non presente : "+pathFileC);
        copy=false;
        //caso particolare per SchellingLong Galazzano
        if(NomiLineeColomb.SCHELLLONG_GAL.equals(linea)){
          _logger.info("Ricerca e copia file .tmp");
          pathFileC=pathFileC.replace("mde", "tmp");
          f=new File(pathFileC);
          if(f==null ||  !f.exists()){
            _logger.warn("Anche file: "+pathFileC+ " non presente.");
            msg.addWarning(infoL+" --> Anche file: "+pathFileC+ " non presente.");
          }else{
            copy=true;
          }  
        }
      }

      if(!StringUtils.isEmpty(pathFileFinal) && copy){
        FileUtils.copyFile(pathFileC, pathFileFinal);
        _logger.info(infoL+" COMPLETATA.");
        msg.addInfo(infoL+" COMPLETATA.");
      }else{
        _logger.warn("Nome del file di destinazione non generato ");
        msg.addWarning(infoL+" NON effettuata.");
      }

    }catch(IOException pe){
      _logger.error("Problemi durante la copia del file :"+pathFileC+" --> "+pe.getMessage());
      msg.addError(infoL+" --> Problemi durante la copia del file :"+pathFileC+" :"+pe.toString());
    } 
    
    return msg;
  }
  
  
  
  public ALogFile getClassLogFileImpIma(String nomeLinea,String nomeFile) throws FileNotFoundException{
    if(nomeLinea.contains(NomiLineeColomb.COMBIMA)){
      LogFileCombima lfC=new LogFileCombima(nomeFile);
      lfC.initialize();
      return lfC;
    }else if(nomeLinea.contains(NomiLineeColomb.SCHELLING)){
      LogFileSchelling lfS=new LogFileSchelling(nomeFile);
      if(nomeLinea.equals(NomiLineeColomb.SCHELLING2R1P1))
        lfS.setDateFormat("MM/dd/yyyy");
      
      lfS.initialize();
      
      return lfS;
    }
    
    return null;
  }
  
  

  
  
  private Boolean isParmsOk(List sourceFiles,List giorni ,String nomeLinea,MessagesElab msg){
    if(sourceFiles==null || sourceFiles.isEmpty()){
      _logger.error("Impossibile generare file per linea "+nomeLinea+" ->File sorgenti non presenti!!");
      msg.addError("Impossibile generare file per linea "+nomeLinea+" ->File sorgenti non presenti !!");
      return Boolean.FALSE;
    }
     
    
    if(giorni==null || giorni.isEmpty()){
      _logger.error("Impossibile generare file per linea "+nomeLinea+" lista dei gg vuoti");
      msg.addError("Impossibile generare file per linea "+nomeLinea+" lista dei gg vuoti");
      return Boolean.FALSE;
    }
    
    if(StringUtils.isEmpty(nomeLinea)){
      _logger.error("Nome linea non specificato. Impossibile generare file di log ");
      msg.addError("Nome linea non specificato. Impossibile generare file di log ");
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }
  
  
  public Map<String,List<File>> getMapDaysFiles(List<File> files) {
    Map dayFiles=new HashMap<String,List<File>> ();
    if(files==null || files.isEmpty())
      return null;
    
    for(File f:files){
      Date dtFile=new Date(f.lastModified());
      try {
        String dtS=DateUtils.DateToStr(dtFile, "yyyyMMdd");
        List filesTmp=null;
        if(dayFiles.containsKey(dtS)){
          filesTmp=(List) dayFiles.get(dtS);
        }else{
          filesTmp=new ArrayList();
        }
        filesTmp.add(f);
        dayFiles.put(dtS, filesTmp);
      } catch (ParseException ex) {
       _logger.error("Problemi di conversione di data del file"+f.getName()+" -> "+ex.getMessage());
      }  
    }
    
    
    return dayFiles;
  }
  
  
  private static final Logger _logger = Logger.getLogger(ManageLogFileProduzione.class);   
  
  
}
