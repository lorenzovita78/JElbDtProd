/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;

/**
 * Classe astratta per la lettura dei file di log
 * @author lvita
 */
public abstract class ALogFile {
 
  private String fileName;
  private FileReader fR;
  private BufferedReader bfR;
//  private Date data;
  
  public ALogFile(){
    
  }
          
  public ALogFile(String pFile) {
    this.fileName=pFile;
  }
  
  
  /**
   * Crea il buffer per la lettura del file
   * @throws FileNotFoundException 
   */
  public void initialize() throws FileNotFoundException{
    fR=new FileReader(fileName);
    bfR=new BufferedReader(fR);
  }
  
  /**
   * Chiude lo stream e il bffer per la lettura del file
   * @throws FileNotFoundException
   * @throws IOException 
   */
  public void terminate() throws FileNotFoundException, IOException{
    if(bfR!=null)
      bfR.close();
    if(fR!=null)
      fR.close();
  }

  public BufferedReader getBfR() {
    return bfR;
  }

  public FileReader getfR() {
    return fR;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  
  /**
   * Dato uno stream scrive nello stesso tutte le righe del file che includono il giorno indicato.
   * Non risulta necessario per tutte le classi che estendono ALogFile 
   * @param data
   * @param ps
   * @return String contenente  informazioni relative ai dati processati 
   * @throws IOException
   * @throws ParseException 
   */
  public String createFileLogGg(Date data,PrintStream ps) throws IOException, ParseException{
    return "";
  }
  
  
  
  public abstract Object processLogFile(Date dataInizio,Date dataFine) throws IOException, ParseException;
  
}
