/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles;

import colombini.costant.NomiLineeColomb;
import colombini.logFiles.R1P1.LogFileCombima;
import colombini.logFiles.R1P1.LogFileSchelling;
import colombini.util.InfoMapLineeUtil;
import elabObj.MessagesClass;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.StringUtils;

/**
 * Classe che si preoccupa di generare i file giornalieri per le varie linee
 * Per le combima calcola anche l'utilizzo dei materiali
 * @author lvita
 */
public class GenLogFileGgImaR1 extends MessagesClass{

  private List<Date> giorni;
  private static final Logger _logger = Logger.getLogger(GenLogFileGgImaR1.class);

  private Boolean elabImaAnte=Boolean.FALSE;
  private Boolean elabImaTop=Boolean.FALSE;
  
  
  public GenLogFileGgImaR1(List giorni ) {
    this.giorni = giorni;
  }

  public void setElabImaAnte(Boolean elabImaAnte) {
    this.elabImaAnte = elabImaAnte;
  }

  public void setElabImaTop(Boolean elabImaTop) {
    this.elabImaTop = elabImaTop;
  }
  
  

  public void elabFileLog() {
    if(elabImaAnte){
      //Combima 1 Ima Ante
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.COMBIMA1R1P1), 
                           NomiLineeColomb.COMBIMA1R1P1);

      //Combima 2 Linea Ante
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.COMBIMA2R1P1), 
                           NomiLineeColomb.COMBIMA2R1P1);
      
      //Schelling 1 Ima Ante
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.SCHELLING1R1P1), 
                           NomiLineeColomb.SCHELLING1R1P1);
      
      //Schelling 2 Ima Ante
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.SCHELLING2R1P1), 
                           NomiLineeColomb.SCHELLING2R1P1);
      
    }
    if(elabImaTop){
      //Combima  Linea Top
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.COMBIMAR1P0), 
                           NomiLineeColomb.COMBIMAR1P0);
      //Schelling 1 Linea Top
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.SCHELLING1R1P0),
                              NomiLineeColomb.SCHELLING1R1P0);

      //Schelling 2 Linea Top
      generateLogFileLinea(InfoMapLineeUtil.getListLogFileSourceLinea(NomiLineeColomb.SCHELLING2R1P0),
                              NomiLineeColomb.SCHELLING2R1P0);

    }  

  }
  
  
  
  public void generateLogFileLinea(List<String> files, String nomeLinea){
    if (giorni == null || giorni.isEmpty()) {
      return;
    }
    if(files==null || files.isEmpty())
      return ;
    
    List filesOk = new ArrayList();
    
    for(String nomeF :files){
      File f = new File(nomeF);
      if (f != null && f.exists()) {
        filesOk.add(nomeF);
      }
    }
    if(files.size()!=filesOk.size()){
      _logger.warn("Attenzione lista dei file da processare differente dalla lista dei file passati!!! ");
    }
    
    ALogFile aLgF=null;
    FileOutputStream file = null;
    PrintStream output=null;
    String msg;
    String outputFileName;
    
    //ciclo sui giorni
    for(Date data: giorni){
      outputFileName="";
      msg="";
      
      try {        
        _logger.info("Elaborazione file linea "+nomeLinea+" per gg :"+data);
        outputFileName=InfoMapLineeUtil.getLogFileGgLinea(nomeLinea, data);
        if(StringUtils.isEmpty(outputFileName)){
          _logger.error("Nome file di output non valorizzato per linea "+nomeLinea +" e giorno "+data);
          continue;
        }
        file = new FileOutputStream(outputFileName);
        msg+=" Generazione file "+outputFileName;
        output = new PrintStream(file);
        for(int i=0;i<files.size();i++){
          String fileName=ClassMapper.classToString(files.get(i));
          _logger.info("Elaborazione log file: "+fileName);
          msg+=" .Lettura file:"+fileName;
          try {
            aLgF=getClassLogFileImpIma(nomeLinea, fileName);
            msg+=" --> "+aLgF.createFileLogGg(data,output);
            output.flush();
          } catch (FileNotFoundException ex) {
            _logger.error(" File sorgente "+fileName+" per linea "+nomeLinea+" NON TROVATO --> "+ex.getMessage());
          }  catch (ParseException ex) {
            _logger.error(" Problemi in fase di lettura  del file :"+fileName+" per linea "+nomeLinea+" --> "+ex.getMessage());
          } finally {
            aLgF.terminate();
          }
        }
      } catch(FileNotFoundException fx){
       _logger.error("Impossibile generare il file "+outputFileName+" --> "+fx.getMessage());
      } catch(IOException ix){
       _logger.error("Problemi in fase di chiusura del file "+outputFileName+" --> "+ix.getMessage());
      }  finally{
        if(!StringUtils.isEmpty(msg))
          addMsg(msg);
        
        if(output!=null)
          output.close();
        try {
          if(file!=null)
            file.close();
        } catch (IOException ex) {
          _logger.error("Impossibile chiudere lo stream di"+outputFileName+" -> "+ex.getMessage());
        }
      }
   }  //chiusura for sui giorni  
    
    
  }
  
  
  public ALogFile getClassLogFileImpIma(String nomeLinea,String nomeFile) throws FileNotFoundException{
    if(nomeLinea.contains(NomiLineeColomb.COMBIMA)){
      LogFileCombima lfC=new LogFileCombima(nomeFile);
      lfC.initialize();
      return lfC;
    }else if(nomeLinea.contains(NomiLineeColomb.SCHELLING)){
      LogFileSchelling lfS=new LogFileSchelling(nomeFile);
      lfS.initialize();
      return lfS;
    }
    
    return null;
  }
 
}


