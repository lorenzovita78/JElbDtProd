/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import colombini.logFiles.R2P1.LogFileSQBStefani;
import colombini.logFiles.R2P1.ProgFileSQBStefani;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ZZElabProgrammiSQBStefani {
  
  public final static String FILEREPORTPRGSQBSTEFANI="//pegaso/scambio/PrgSqbStefani";
  public final static String HEADERFILEREPORTPRGSQBSTEFANI="NOMEPROGRAMMA;LUNGHEZZA;LARGHEZZA;SPESSORE;VELOCITA;CADENZAMENTO";
  
  
  public void run(){
    List info=getProgramsInfo();
    generateFileStat(info);
    
  }
  
  private List getProgramsInfo(){
    File dirF=new File(LogFileSQBStefani.PATHFILEPROGRAM);
    List<String> files=ArrayUtils.getListFromArray(dirF.list());
    List<String> infoS=new ArrayList();
    for(String fName :files){
      if(fName.contains(".prg")){
        String fileNameC=LogFileSQBStefani.PATHFILEPROGRAM+fName;
        ProgFileSQBStefani prgFile=new ProgFileSQBStefani(fileNameC);
        try {
          prgFile.elabFile();
          StringBuilder riga=new StringBuilder();
          riga.append(fName).append(
                  ";").append(prgFile.getLunghezza()).append(
                  ";").append(prgFile.getLarghezza()).append(
                  ";").append(prgFile.getSpessore()).append(
                  ";").append(prgFile.getVelocita()).append(
                  ";").append(prgFile.getCadenzamento());

          infoS.add(riga.toString());
       
        } catch (FileNotFoundException ex) {
          _logger.error("File "+fName+" non trovato .Impossibile reperire i dati  --> "+ex.getMessage());
        } catch (IOException ex) {
          _logger.error("Problemi di accesso al file "+fName+" --> "+ex.getMessage());
        }
      }
    }
   
    
    return infoS; 
  }
  
  private String generateFileStat(List<String> infoS){
    FileOutputStream file=null;
    PrintStream output=null;
    String fileName=null;
    
    try {
     
      String dateS=DateUtils.DateToStr(new Date(), "ddMMyyyy");
      fileName=FILEREPORTPRGSQBSTEFANI+"_"+dateS+".csv";
      file = new FileOutputStream(fileName);
      output = new PrintStream(file);
      
      //scrivo l'intestazione
      output.println(HEADERFILEREPORTPRGSQBSTEFANI);
      
      for(String s:infoS){
        output.println(s);  
      }
         
    } catch (FileNotFoundException ex) {
      _logger.error("Impossibile aprire il file "+fileName+" -->"+ex.getMessage());
    } catch (ParseException e){
      _logger.error("Impossibile aprire il file "+fileName+" -->"+e.getMessage());
    } finally{
      if(output!=null)
        output.close();
    }
    
    return fileName;
    
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ZZElabProgrammiSQBStefani.class); 
  
}
