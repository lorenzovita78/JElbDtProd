/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P3;

import colombini.util.InfoMapLineeUtil;
import colombini.costant.NomiLineeColomb;
import exception.ElabException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class LogFileForAnteRem {
  
  
  private String fileName;
  private Date dataInizioLog;
  private Date dataFineLog;
  
  
  public LogFileForAnteRem(String fname){
    this.fileName=fname;
  }
  
  
  public List readFile(Date dataRif){
    List listArticoliProc=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    boolean limiteSup=false;
    Long count=Long.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();
      count++;
      while(riga!=null && !limiteSup){
        Date dataIniElab=null;
        Date dataFinElab=null;
        
        List info=ArrayUtils.getListFromArray(riga.split(";"));
        if(info==null || info.size()<5){
          riga=bfR.readLine();
          count++;
          continue;  
        }
        String dataS=(String) info.get(0);
        String commColl=ClassMapper.classToString(info.get(1));
        String collTipo=ClassMapper.classToString(info.get(2));
        String oraIni=ClassMapper.classToString(info.get(3));
        String oraFin=ClassMapper.classToString(info.get(4));
        Double secElab=Double.parseDouble((String)info.get(5));
        
        Date data=null;
        try {
          data=DateUtils.StrToDate(dataS, "dd/MM/yy");
          dataIniElab=DateUtils.StrToDate(dataS+" "+oraIni, "dd/MM/yy HH:mm:ss");
          dataFinElab=DateUtils.StrToDate(dataS+" "+oraFin, "dd/MM/yy HH:mm:ss");
        } catch (ParseException ex) {
          _logger.error("Errore in fase di decodifica delle date .Riga log n"+count+"  --> "+ex.getMessage());
          //la conversione potrebbe dare errore se c'è stato un problema nella realizzione del pezzo
          dataFinElab=dataIniElab;
        }
        
        if(data.after(dataRif)){
          limiteSup=true;
          break;
        }
        
        if(data!=null && DateUtils.daysBetween(data, dataRif)==0){
        
          if(dataInizioLog==null)
            dataInizioLog=dataIniElab;

          dataFineLog=dataFinElab;

          if(StringUtils.IsEmpty(commColl) && StringUtils.IsEmpty(collTipo) ){
            _logger.warn("Attenzione collo non indicato alla riga n."+count+" Passiamo alla riga successiva");
            riga=bfR.readLine();
            count++;
            continue;  
          }  
          
          try{
            String numComm=commColl.substring(0,1);
            String colloS=commColl.substring(1)+collTipo.substring(0, 3);
            String tipo=collTipo.substring(3);
            InfoPzLogFileARem pz=new InfoPzLogFileARem(Integer.valueOf(numComm),Integer.valueOf(colloS),Integer.valueOf(tipo));
            pz.setSecElab(secElab);
            pz.setDataFineLav(dataFinElab);
            pz.setDataIniLav(dataIniElab);

            listArticoliProc.add(pz);
          }catch(NumberFormatException ne){
            _logger.error("Errore in fase di decodifica del collo alla riga n."+count);
          }
        }
        
        riga=bfR.readLine();
        count++;
      }
    
    }catch(IOException e){  
     _logger.error(" Impossibile accedere al file "+fileName+" --> "+e.getMessage());
    }finally{
      _logger.info("File "+fileName+" righe lette :"+count);
      try{
        if(bfR!=null)
          bfR.close();
        if(fR!=null)
          fR.close();
      }catch(IOException e){  
       _logger.error(" Errore in fase di chiusura del file "+fileName+" --> "+e.getMessage());
      }
    }
    
    return listArticoliProc;
  }

  public Date getDataFineLog() {
    return dataFineLog;
  }

  public Date getDataInizioLog() {
    return dataInizioLog;
  }
  
  
  
  public String writeLogGg(Date data) throws ElabException{
    FileOutputStream outS=null;
    PrintStream ps = null;
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long read=Long.valueOf(0);
    Long write=Long.valueOf(0);
    boolean stop=false;
    String fileNameGg="";
    String msg="";
    try {
      fileNameGg=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.FORANTEREM,data);
      outS=new FileOutputStream(fileNameGg);
      ps = new PrintStream(outS);
     
      try{
        fR = new FileReader(fileName);
        bfR=new BufferedReader(fR);
      
        riga = bfR.readLine();
        read++;
        while(riga!=null && !stop){
          List info=ArrayUtils.getListFromArray(riga.split(";"));
          if(info==null || info.size()<5){
            _logger.error("Attenzione errore di lettura alla riga "+read);
            riga=bfR.readLine();
            read++;
            continue;  
          }
          String dataS=(String) info.get(0);
          try{
            Date dataLog=DateUtils.StrToDate(dataS,"dd/MM/yy");
            if(DateUtils.daysBetween(dataLog, data) ==0){
              ps.println(riga);
              write++;
            } else if(dataLog.after(data)){
              stop=true;
            }
          } catch (ParseException e){
            _logger.error("Problemi nella conversione della data.Salto riga --> "+e.getMessage());
          }
          riga = bfR.readLine();
          read++;  
        }
   
      } catch (IOException ex) {
        _logger.error("Problemi in fase di accesso al file di log -->"+ex.getMessage());
        throw new ElabException(" Problemi in fase di accesso al file di log -->"+ex.toString());
      }
      
    } catch (FileNotFoundException ex) {
      _logger.error("Problemi nella generazione del file di output -->"+ex.getMessage());
      throw new ElabException("Problemi nella generazione del file di output -->"+ex.toString());
      
    } finally{
      msg=" - File di log "+fileNameGg+" generato con righe : "+write+" --> righe lette da "+fileName +" : "+read ;
      _logger.info(msg);
      
      if(ps!=null)
        ps.close();
      try {
        if(outS!=null)
            outS.close();  
        if(bfR!=null)
            bfR.close();
      } catch (IOException ex) {
        _logger.error("Impossibile chiudere lo stream  -> "+ex.getMessage());
      }
    }
    
    return msg;
  }
  
  
  private static final Logger _logger = Logger.getLogger(LogFileForAnteRem.class);   
  
  
}
