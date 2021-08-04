/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.logFiles.G1P0;

import java.io.BufferedReader;
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
import utils.ParameterMap;

/**
 *
 * @author lvita
 */
public class LogFileSuperVNesting {

  public final static String RequestID="RequestID";
  public final static String PanelID="PanelID";
  
  public final static String Lunghezza="Lunghezza";
  public final static String Larghezza="Larghezza";
  public final static String Spessore="Spessore";
  
  public final static String LungReale="LungReale";
  public final static String LargReale="LargReale";
  public final static String LargFoglio="LargFoglio";
  public final static String D_LargY="D_LargY";        
  
  public final static String Programma="Programma";
  public final static String SEND="SEND";
  public final static String SMIS="SMIS";
  
  public final static String LISTPAN="LISTPAN";
  public final static String NSETUP="NSETUP";
  public final static String MAPREQ="MAPREQ";
  
  private String fileName;

  public LogFileSuperVNesting(String fileName) {
    this.fileName = fileName;
  }
  
  
  
  public Map<String,Object> processLogFile(Date dataInizio,Date dataFine) throws IOException, ParseException {
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Map result=new HashMap<String,Object> ();
    Map mapReqId=new HashMap<Long,Long> ();
    List listProc=new ArrayList();
    
    
    Integer nCambioPan=Integer.valueOf(0);
    Date dataCambioPan=dataInizio;
    Date dataOld=null;
    Long idReqOld=Long.valueOf(0);
    Long idPanOld=Long.valueOf(0);
    
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      String dtF=DateUtils.DateToStr(dataInizio, "dd/MM/yyyy");
      while(riga!=null && !riga.isEmpty()){
        Date dataR=DateUtils.StrToDate(dtF+" "+riga.substring(0,8), "dd/MM/yyyy HH.mm.ss");
        //controllo che la data della riga sia all'interno del range fornito
        if(DateUtils.afterEquals(dataR, dataInizio) && 
                DateUtils.beforeEquals(dataR, dataFine)){
          if(riga.contains("SEND	SMIS1") ){
            Map info=getInfoRiga(riga);
            Long idReq=ClassMapper.classToClass(info.get(RequestID),Long.class);
            Long idPan=ClassMapper.classToClass(info.get(PanelID),Long.class);
            if(dataOld!=null && 
                (!idPan.equals(idPanOld) || (idPan.equals(idPanOld) && !idReq.equals(idReqOld)) )){
                List record=new ArrayList();
                record.add(dataOld);
                record.add(idReqOld);
                record.add(idPanOld);
                Long sec=DateUtils.numSecondiDiff(dataR, dataOld);
                //se esiste un cambio pannello tra i due riquadri allora prendo il tempo tra l'inizio cambio pan
                // e inizio lav riq
                if(nCambioPan>0 && DateUtils.afterEquals(dataCambioPan, dataOld) &&
                        DateUtils.beforeEquals(dataCambioPan, dataR) ){
                  sec=DateUtils.numSecondiDiff(dataOld,dataCambioPan);
                }
                
                record.add(sec);
                listProc.add(record);
                mapReqId.put(idReqOld,idReqOld);
            }
            idReqOld=idReq;
            idPanOld=idPan;
            dataOld=dataR;
          }else if(riga.contains("POP	PONT      	START")){
            dataCambioPan=dataR;
            nCambioPan++;
          }
        }
        
     
        riga = bfR.readLine();
        count++;
      }
     _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    result.put(LISTPAN,listProc);
    result.put(NSETUP,nCambioPan);
    result.put(MAPREQ,mapReqId);
    
    return result;
  }
  
  public List<String> processLogFile(Date data) throws IOException, ParseException {
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    List result=new ArrayList();
    
    
    Integer nCambioPan=Integer.valueOf(0);
    
  
    
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
          String dataS =DateUtils.DateToStr(data, "dd/MM/yyyy")+" "+riga.substring(0,8);
        //controllo che la data della riga sia all'interno del range fornito
        
          if(riga.contains("SEND	SMIS1") ){
            Map info=getInfoRiga2(riga);
            
            Double idPan=ClassMapper.classToClass(info.get(PanelID),Double.class);
            Double lunghezza=ClassMapper.classToClass(info.get(Lunghezza),Double.class);
            Double larghezza=ClassMapper.classToClass(info.get(Larghezza),Double.class);
            Double spessore=ClassMapper.classToClass(info.get(Spessore),Double.class);
            Double lungReale=ClassMapper.classToClass(info.get(LungReale),Double.class);
            Double largReale=ClassMapper.classToClass(info.get(LargReale),Double.class);
            Double largFoglio=ClassMapper.classToClass(info.get(LargFoglio),Double.class);
            Double d_LargY=ClassMapper.classToClass(info.get(D_LargY),Double.class);
            
            
            StringBuilder infoS=new StringBuilder(dataS).append(";").append(
                               idPan).append(";").append(
                               lunghezza).append(";").append(
                               larghezza).append(";").append(
                               spessore).append(";").append(
                               lungReale).append(";").append(
                               largReale).append(";").append(
                               largFoglio).append(";").append(
                               d_LargY);
            
           result.add(infoS.toString()); 
          }else if(riga.contains("POP	PONT      	START")){
           
            nCambioPan++;
          }
   
        riga = bfR.readLine();
        count++;
      }
     _logger.info("File "+fileName+" righe processate:"+count);
     result.add(";;"+nCambioPan+";;;;;");
     
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
    return result;
  }
  
  
  private Map getInfoRiga(String riga){
    Map info=new HashMap();
    riga=riga.substring(riga.lastIndexOf("Programma"),riga.length()-1);
    info=ParameterMap.getParameterMap(riga);
    
    return info;
  }
  
  private Map getInfoRiga2(String riga){
    Map info=new HashMap();
    riga=riga.substring(riga.lastIndexOf("LungReale"),riga.length()-1);
    info=ParameterMap.getParameterMap(riga);
    
    return info;
  }
  
  
  private static final Logger _logger = Logger.getLogger(LogFileSuperVNesting.class);
  
  
}
