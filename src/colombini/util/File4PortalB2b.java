/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class File4PortalB2b {
  
  
  private static File4PortalB2b instance;
  
  public static File4PortalB2b getInstance(){
    if(instance==null){
      instance= new File4PortalB2b();
    }
    
    return instance;
  }
  
  
  public void  createXmlForPortal (String fileSource,String fileDest,String pathDest,Map parmsToSwitch) throws FileNotFoundException, IOException {
    FileOutputStream file = null;
    PrintStream output=null;
    FileReader fr=null;
    BufferedReader bfr=null;
    List parms=new ArrayList();
    try{
      //prima copio
      String fileNameNew=pathDest+"\\"+fileDest+".xml";
      file = new FileOutputStream(fileNameNew);
      output = new PrintStream(file);
      fr=new FileReader(fileSource);
      bfr=new BufferedReader(fr);
      parms=MapUtils.mapToListOfList(parmsToSwitch);
      if(bfr==null){
        
      }
      String riga=bfr.readLine();
      while(riga!=null ){
        String rigaNew=checkParmsOnRiga(riga,parms);
        output.println(rigaNew);
        riga=bfr.readLine();
      }
      output.flush();
      
     
    }  finally{
      try{
      if(bfr!=null)
        bfr.close();
      
      if(fr!=null)
        fr.close();
      
      if(output!=null)
        output.close();
      
      if(file!=null)
        file.close();
      
      }catch(FileNotFoundException f){
        _logger.error("Errore in fase di chiusura del file sorgente -->"+f.getMessage());
      }catch(IOException i){
        _logger.error("Errore in fase di chiusura del file sorgente -->"+i.getMessage());
      }
        
    }
      
    
  }
  
  private String checkParmsOnRiga(String riga, List<List> parms) {
    String rigaNew=riga;
    if(parms==null || parms.isEmpty())
      return rigaNew;
    
    for(List rec:parms){
      String parm=ClassMapper.classToString(rec.get(0));  
      String value=ClassMapper.classToString(rec.get(1));
      
      if(rigaNew.contains(parm))
        rigaNew=rigaNew.replace(parm, value);
    }
    
    return rigaNew;
  }
  
  
  private static final Logger _logger = Logger.getLogger(File4PortalB2b.class);  

  
}
