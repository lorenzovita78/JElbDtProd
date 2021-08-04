/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class LogFileSupervisoreAlberti {
  
  private String fileName;
  
  private Date dataInizio;
  private Date dataFine;
  
  
  public LogFileSupervisoreAlberti(String fn){
    fileName=fn;
  }

  public Date getDataFine() {
    return dataFine;
  }

  public Date getDataInizio() {
    return dataInizio;
  }
  
  /**
   * Elabora il file di log tornando una lista contenente commessa,collo e tempo 
   * @return 
   */
  public List elabFile() throws FileNotFoundException, IOException{
    List colli=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
   
    Long colloOld = Long.valueOf(0);
    Long commOld = Long.valueOf(0);
    Long timeOld= Long.valueOf(0);
    Integer occOld=Integer.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      Date dataOld=null;
      riga = bfR.readLine();  
      while(riga!=null){
        count++;
        try{ 
          if(riga.contains("Data")){
            String dataS=riga.substring(riga.indexOf(":")+1, riga.indexOf("-"));
            String oraS=riga.substring(riga.indexOf("Ora:")+5, riga.indexOf("- File:")-1);
            Date dataRif=DateUtils.StrToDate(dataS+oraS, "dd/MM/yyyy HH.mm.ss");
            
            if(this.dataInizio==null)
             this.dataInizio=dataRif;
           
            this.dataFine=dataRif;
           
           
            //leggo subito la seconda riga per prendere il numero di commessa e di collo
            riga=bfR.readLine(); 
            count++;
            Long commessa=Long.valueOf(0);
            Long collo=Long.valueOf(0);
            Long time=Long.valueOf(0);
            Integer occorrenza=Integer.valueOf(1);
            if(dataOld!=null && dataRif!=null ){
              time=Long.valueOf(DateUtils.numSecondiDiff(dataOld, dataRif));
            }
            
            if(!StringUtils.IsEmpty(riga)){
              if(riga.length()==8){
                commessa=ClassMapper.classToClass(riga.substring(0, 3),Long.class);
                collo=ClassMapper.classToClass(riga.substring(3, 8),Long.class);
              }else{
                commessa=ClassMapper.classToClass(riga.substring(0, 2),Long.class);
                collo=ClassMapper.classToClass(riga.substring(2, 7),Long.class);
              }
            }  
              
              
            // nel caso in cui ci siano due righe con stessa commessa e collo
            // sommo i tempi ma considero il collo 1 volta sola
            if(commessa.equals(commOld) && collo.equals(colloOld) &&commessa.longValue()>0 ){
              time+=timeOld;
              occorrenza+=occOld;
              int idx=colli.size();
              colli.remove(idx-1);
            }
            
            List record=new ArrayList();

            record.add(commessa);
            record.add(collo);
            record.add(time);
            record.add(occorrenza);

            colli.add(record);

            commOld=commessa;
            colloOld=collo;

            timeOld=time;
            dataOld=dataRif;
            occOld=occorrenza;
          }  
         
         
         
        }catch(ParseException ep){
          _logger.error("Errore di conversione della data (riga n."+count+" ) --> "+ep.getMessage());
          
        } catch(Exception e){
          _logger.error("Errore generico -->"+e.getMessage());
        }
        
        riga=bfR.readLine(); 
      }
    
      
     
    }finally{
      _logger.info("File "+fileName+" righe lette :"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
    return colli;
  }
  
  
  /**
   * Elabora il file di log tornando una lista contenente commessa,collo e tempo 
   * @param dataInizio
   * @param dataFine
   * @return 
   */
  public List elabFile(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException{
    List colli=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
   
    Long colloOld = Long.valueOf(0);
    Long commOld = Long.valueOf(0);
    Long timeOld= Long.valueOf(0);
    Integer occOld=Integer.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      Date dataOld=null;
      riga = bfR.readLine();  
      while(riga!=null){
        count++; 
        if(riga.contains("Data")){
          String dataS=riga.substring(riga.indexOf(":")+1, riga.indexOf("-"));
          String oraS=riga.substring(riga.indexOf("Ora:")+5, riga.indexOf("- File:")-1);
          Date dataRif=null;
          try{
            dataRif=DateUtils.StrToDate(dataS+oraS, "dd/MM/yyyy HH.mm.ss");
          }catch(ParseException ep){
            _logger.error("Errore di conversione della data (riga n."+count+" ) --> "+ep.getMessage());
          }

          if(dataRif==null)
            continue;

          // per verificare dataInizio e dataFine lavorazione su file di log
          if(this.dataInizio==null)
           this.dataInizio=dataRif;

          this.dataFine=dataRif;

          
          if(DateUtils.afterEquals(dataRif, dataInizio) && DateUtils.beforeEquals(dataRif, dataFine)){
            //leggo subito la seconda riga per prendere il numero di commessa e di collo
            riga=bfR.readLine(); 
            count++;
            Long commessa=Long.valueOf(0);
            Long collo=Long.valueOf(0);
            Long time=Long.valueOf(0);
            Integer occorrenza=Integer.valueOf(1);
            if(dataOld!=null && dataRif!=null ){
              time=Long.valueOf(DateUtils.numSecondiDiff(dataOld, dataRif));
            }

            if(!StringUtils.IsEmpty(riga)){
              if(riga.length()==8){
                commessa=ClassMapper.classToClass(riga.substring(0, 3),Long.class);
                collo=ClassMapper.classToClass(riga.substring(3, 8),Long.class);
              }else{
                commessa=ClassMapper.classToClass(riga.substring(0, 2),Long.class);
                collo=ClassMapper.classToClass(riga.substring(2, 7),Long.class);
              }
            }  


            // nel caso in cui ci siano due righe con stessa commessa e collo
            // sommo i tempi ma considero il collo 1 volta sola
            if(commessa.equals(commOld) && collo.equals(colloOld) &&commessa.longValue()>0 ){
              time+=timeOld;
              occorrenza+=occOld;
              int idx=colli.size();
              colli.remove(idx-1);
            }

            List record=new ArrayList();

            record.add(commessa);
            record.add(collo);
            record.add(time);
            record.add(occorrenza);

            colli.add(record);

            commOld=commessa;
            colloOld=collo;

            timeOld=time;
            dataOld=dataRif;
            occOld=occorrenza;
          }  

        }  

      riga=bfR.readLine(); 
    }
    
      
     
    }finally{
      _logger.info("File "+fileName+" righe lette :"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
    return colli;
  }
  
  
  
   public Map elabFileForAvProd() throws FileNotFoundException, IOException{
    Map mappaComm=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Integer count=Integer.valueOf(0);
    
     try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      Date dataOld=null;
      riga = bfR.readLine();  
      while(riga!=null ){
        count++;
        
        if(riga.contains("Data")){
          //le informazioni sulla commessa collo sono inserite nella riga successiva
          riga=bfR.readLine(); 
          count++;
          Integer commessa=null;
          if(!StringUtils.IsEmpty(riga)){
            if(riga.length()==8){
              commessa=ClassMapper.classToClass(riga.substring(0, 3),Integer.class);
            }else{
              commessa=ClassMapper.classToClass(riga.substring(0, 2),Integer.class);
            }

            if(mappaComm.containsKey(commessa)) {
              mappaComm.put(commessa,((Integer)mappaComm.get(commessa))+1);
            }else{
              mappaComm.put(commessa,Integer.valueOf(1));
            }
          }
        }
        riga=bfR.readLine(); 
     }
    
      
     
    }finally{
      _logger.info("File "+fileName+" righe lette :"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
    
    return mappaComm;
  }
  
  
  private static final Logger _logger = Logger.getLogger(LogFileSupervisoreAlberti.class);   
  
}
