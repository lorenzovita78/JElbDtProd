/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P2;

import java.io.BufferedReader;
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
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileTavoli {
  
  private String fileName;
  
  private Date dataInizio;
  private Date dataFine;
  
  
  public LogFileTavoli(String fn){
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
    Long cColliUg=Long.valueOf(0);
    Long colloOld = Long.valueOf(0);
    Long commOld = Long.valueOf(0);
    Long timeOld= Long.valueOf(0);
    Integer occOld=Integer.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      Date dataOld=null;
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        try{ 
         List info=ArrayUtils.getListFromArray(riga.split(";"));
         if(info!=null && info.size()>2){
           List record=new ArrayList();
           String dateString=info.get(1)+" "+info.get(2);
           Date dataCorr=DateUtils.StrToDate(dateString, "dd.MM.yyyy HH:mm:ss");
           
           //codice necessario per sapere inizio e fine periodo da file di log
           if(this.dataInizio==null)
             this.dataInizio=dataCorr;
           
           this.dataFine=dataCorr;
           Long commessa=Long.valueOf(0);
           Long collo=Long.valueOf(0);
           Long time=Long.valueOf(0);
           Integer occorrenze=Integer.valueOf(1);
           
           if(info.size()==5){
             commessa=ClassMapper.classToClass(info.get(3), Long.class);
             collo=ClassMapper.classToClass(info.get(4), Long.class);
           }
           
           if(dataOld!=null && dataCorr!=null ){
             time=Long.valueOf(DateUtils.numSecondiDiff(dataOld, dataCorr));
           }
           
           // nel caso in cui ci siano due righe con stessa commessa e collo
           // sommo i tempi ma considero il collo 1 volta sola
           if(commessa!=null && commessa.equals(commOld) && collo.equals(colloOld) &&commessa.longValue()>0 ){
             time+=timeOld;
             occorrenze+=occOld;
             int idx=colli.size();
             colli.remove(idx-1);
             cColliUg++;
           }
           
           record.add(commessa);
           record.add(collo);
           record.add(time);
           record.add(occorrenze);
           
           colli.add(record);
           
           dataOld=dataCorr;
           commOld=commessa;
           colloOld=collo;
           timeOld=time;
           occOld=occorrenze;
         }
         
        }catch(ParseException ep){
          _logger.error("Errore di conversione della data (riga n."+count+" ) --> "+ep.getMessage());
        }
        
        riga=bfR.readLine(); 
      }
    
      
     _logger.info("File "+fileName+" righe lette :"+count+" colli uguali"+cColliUg);
    }finally{
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
      while(riga!=null && !riga.isEmpty()){
        count++;
        
         List info=ArrayUtils.getListFromArray(riga.split(";"));
         if(info!=null && info.size()==5){
           Integer commessa=ClassMapper.classToClass(info.get(3), Integer.class);
           if(commessa!=null){
            if(mappaComm.containsKey(commessa)) {
              mappaComm.put(commessa,((Integer)mappaComm.get(commessa))+1);

            }else{
              mappaComm.put(commessa,Integer.valueOf(1));
            }
           }
         }
         
       
        riga=bfR.readLine(); 
      }
    
      
     _logger.info("File "+fileName+" righe lette :"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
    
    return mappaComm;
  }
  
  
  private static final Logger _logger = Logger.getLogger(LogFileTavoli.class);   
  
}
