/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;




/**
 *
 * @author lvita
 */
public class LogFileBima {
  
  public final static String TAG_TR_INI="<tr>";
  public final static String TAG_TR_FIN="</tr>";
  
  
  public final static String TAG_MAGGIORE=">";
  public final static String TAG_TD_PROG="<td width=\"107\">";
  public final static String TAG_TD_DURATA="<td width=\"100\">";
  public final static String TAG_TD_INILAV="<td width=\"56\">";
  public final static String TAG_TD_GGLAV="<td width=\"54\">";
  
  public final static String TAG_TD_FIN="</td>";
  
  
  private String fileName;

  public LogFileBima(String fileName) {
    this.fileName = fileName;
  }
  
  public List processFile(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException{
    List result=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      count++;
      Boolean inRiga=Boolean.FALSE;
      InfoLogBima ilg=null;
      while(riga!=null ){
        if(riga.contains(TAG_TR_INI)){
          inRiga=Boolean.TRUE;
          riga=bfR.readLine();
          count++;
          continue;
        }
        if(riga.contains(TAG_TR_FIN)){
          inRiga=Boolean.FALSE;
          if(ilg!=null)
            result.add(ilg);
          riga=bfR.readLine();
          count++;
          continue;
        }
        if(inRiga && count>=30){  
          if(riga.contains(TAG_TD_PROG)){
            String progN=riga.substring(riga.indexOf(TAG_MAGGIORE)+1, riga.lastIndexOf(TAG_TD_FIN));
            progN=progN.replace(".SRC", "");
            ilg=new InfoLogBima(progN);
          }
          if(riga.contains(TAG_TD_DURATA)){
            String durata=riga.substring(riga.indexOf(TAG_MAGGIORE)+1, riga.lastIndexOf(TAG_TD_FIN));
            int sec=DateUtils.getDurataSec(durata);
            ilg.setSec(sec);
          }
          if(riga.contains(TAG_TD_INILAV)){
            String or=riga.substring(riga.indexOf(TAG_MAGGIORE)+1, riga.lastIndexOf(TAG_TD_FIN));
            ilg.setOraString(or);
          }
          if(riga.contains(TAG_TD_GGLAV)){
            String dt=riga.substring(riga.indexOf(TAG_MAGGIORE)+1, riga.lastIndexOf(TAG_TD_FIN));
            ilg.setDataString(dt);
          }
        }
        
        riga=bfR.readLine();
        count++;
      }
    
    }finally{
        _logger.info("File "+fileName+" righe processate:"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }  
      
      
    return result;
  }
 
  
  public Boolean exist(){
    
    File f=new File(fileName);
    return f.exists();
    
  }
  
//  private Date getDataFromString(String dataS,String oraSAMPM){
//    Date dataora=null;
//    
//    String [] infoData=oraSAMPM.split(" ");
//    
//    if(infoData==null || infoData.length==0)
//      return dataora;
//    
//    String orario=infoData[0];
//    String tipoOra=infoData[1];
//    
//    infoData=null;
//    infoData=orario.split(":");
//    if(infoData==null || infoData.length==0)
//      return dataora;
//    
//    String hh=infoData[0];
//    String mm=infoData[1];
//    String ss=infoData[2];
//    Integer oraappo=new Integer(hh);
//    
//    String oraS="";
//    String dataAppo="";
//    
//    boolean orariomod=false;
//    if("PM".equals(tipoOra)){
//      if(oraappo<12){
//        oraS= new Integer(oraappo+12).toString();
//        oraS+=":";
//        orariomod=true;
//      }
//    }else{
//      if(Integer.valueOf(12).equals(oraappo)){
//        oraS="00:";
//        orariomod=true;
//      }
//    }
//    if(!orariomod)
//      oraS=hh+":";
//    
//   
//    dataAppo=dataS+" "+oraS+mm+":"+ss;
//    dataora=DateUtils.strToDate(dataAppo, "MM/dd/yy HH:mm:ss");
//    
//    return dataora;
//  }
  
  public class InfoLogBima {
    private String programName;
    private Integer sec;
    private Date dataElab;
    private String oraString;
    private String dataString;
    
    public InfoLogBima(String programName) {
      this.programName = programName;
    }

    
    public String getProgramName() {
      return programName;
    }

    public void setProgramName(String programName) {
      this.programName = programName;
    }

    public Integer getSec() {
      return sec;
    }

    public void setSec(Integer sec) {
      this.sec = sec;
    }

    public Date getDataElab() {
      return dataElab;
    }

    public void setDataElab(Date dataElab) {
      this.dataElab = dataElab;
    }

    public String getOraString() {
      return oraString;
    }

    public void setOraString(String oraString) {
      this.oraString = oraString;
    }

    public String getDataString() {
      return dataString;
    }

    public void setDataString(String dataString) {
      this.dataString = dataString;
    }

    
    
    @Override
    public String toString() {
      return programName+" ; "+dataElab.toString() +" ; "+sec; //To change body of generated methods, choose Tools | Templates.
    }
  
    
    
    
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(LogFileCombimaTop.class);   
  
}
