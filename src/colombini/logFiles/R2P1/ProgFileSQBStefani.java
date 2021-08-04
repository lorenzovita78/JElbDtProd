/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R2P1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class ProgFileSQBStefani {
   
  
  public final static String LUNGHEZZA="Lunghezza pannello";
  public final static String LARGHEZZA="Larghezza pannello";
  public final static String SPESSORE="Spessore pannello";
  
  public final static String VELOCITA="Velocita";
  public final static String CADENZAMENTO="Cadenzamento";
  
  private String fileName;
  
  private Double velocita;
  private Double cadenzamento;
  private Double lunghezza;
  private Double larghezza;
  private Double spessore;
  
  public ProgFileSQBStefani(String fn) {
    this.fileName=fn;
  }
  
  
  public void elabFile() throws FileNotFoundException, IOException{
    
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      //leggo fino a quando non ho valorizzato velocita e cadenza
//      while(riga!=null && !riga.isEmpty() &&  checkInfo()){
      while(riga!=null &&  checkInfo()){
        count++;
        
        
        if(riga.contains(LUNGHEZZA)){
          String appo=riga.substring(riga.lastIndexOf("=")+1);
          lunghezza=Double.valueOf(appo);
        }
        if(riga.contains(LARGHEZZA)){
          String appo=riga.substring(riga.lastIndexOf("=")+1);
          larghezza=Double.valueOf(appo);
        }
        if(riga.contains(SPESSORE)){
          String appo=riga.substring(riga.lastIndexOf("=")+1);
          spessore=Double.valueOf(appo);
        }
        if(riga.contains(VELOCITA)){
          String appo=riga.substring(riga.lastIndexOf("=")+1);
          velocita=Double.valueOf(appo);
        }
        if(riga.contains(CADENZAMENTO)){
          String appo=riga.substring(riga.lastIndexOf("=")+1);
          cadenzamento=Double.valueOf(appo);
        }

        riga = bfR.readLine();  
      }
      _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
  }
  
  private boolean checkInfo(){
    if(lunghezza==null || larghezza==null || spessore==null||velocita==null || cadenzamento==null)
      return true;
    
    return false;
  }
  

  public Double getCadenzamento() {
    return cadenzamento;
  }

  public Double getVelocita() {
    return velocita;
  }

  public Double getLarghezza() {
    return larghezza;
  }

  public Double getLunghezza() {
    return lunghezza;
  }

  public Double getSpessore() {
    return spessore;
  }
  
  
  
  

   private static final Logger _logger = Logger.getLogger(ProgFileSQBStefani.class);   
}
