/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P0;

import colombini.costant.CostantsColomb;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileCombimaTop {
  
  
  /**
   * RUNTIME
   *  Largh. < 260 8 ,5 sec/p.zo
   *  260 < Largh. < 450 8 ,5 sec/p.zo
   *  450 < Largh. < 600 9 ,3 sec/p.zo
   *   Largh. > 600 9 ,7 sec/p.zo
   *
   * 
   *  Tempo di log < 15 sec Tempo coincide con runtime
   *  15 sec < Tempo di log < 112 sec Tempo composto da runtime (vd. tabella) + microfermo
   *  Tempo di log > 112 sec Tempo composto da runtime (vd. tabella) + fermo 
   */
  
  public final static Double SECPMMB450=Double.valueOf(8.5);
  public final static Double SECPMMBMIN600=Double.valueOf(9.3);
  public final static Double SECPMMBMAX600=Double.valueOf(9.7);
  public final static Double SECMAXMICRO=Double.valueOf(9.7);
  
  public final static Double LARGHMM450=Double.valueOf(450);
  public final static Double LARGHMM600=Double.valueOf(600);
  
  private static final Long  TMICROLSUP=Long.valueOf(112);
  private static final Long  TMICROLINF=Long.valueOf(15);
  
  private String fileName;
  
  public LogFileCombimaTop(String fileName) {
    this.fileName=fileName;
  }
  
  
  public  Map processFile(Date dataInizio ,Date dataFine) throws FileNotFoundException, IOException{
    Map map=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    
    Double tempoRun=Double.valueOf(0);
    Double tempoSetup=Double.valueOf(0);
    Double tempoMicrofermi=Double.valueOf(0);
    Double tempoFermi=Double.valueOf(0);
    Date dataOld = null;
    Double spessOld=null;
    Integer npzCmbS=Integer.valueOf(0);
    Long maxLavorazione=Long.valueOf(0);
    Date dataCmbS=null;
    boolean loadSetup=false;
    boolean loadMicro=false;
    boolean loadFermo=false;
    boolean cambioSetup=false;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      //System.out.println("DataOra ; Tpassaggio ; TRunBanda ; TMicrofermi ; TFermi; TSetup   " );
      while(riga!=null && !riga.isEmpty()){
        count++;
        RigaLogFileCombimaTop rlf=new RigaLogFileCombimaTop(riga);
        Date dataCorr=rlf.getDataRif();
        
        if(dataCorr!=null && dataCorr.after(dataFine)){
          break;
        }
        
        if(dataCorr!=null && DateUtils.afterEquals(dataCorr, dataInizio) 
                && DateUtils.beforeEquals(dataCorr, dataFine) ){
          loadSetup=false;
          loadMicro=false;
          loadFermo=false;
          Long secTmp=null;
          npzCmbS++;
          
          Double runTmp=getRuntimeLarghezzaB(rlf.getLargBanda());
          
          
          if(dataOld!=null){
            secTmp=DateUtils.numSecondiDiff(dataOld, dataCorr);
          }
          
          //se varia lo spessore allora devo incrementare il tempo di setup
          if(spessOld!=null && !spessOld.equals(rlf.getSpessoreBanda())){
            cambioSetup=true; 
            dataCmbS=dataCorr;
            npzCmbS=0;
          }
          
          //gestisco il tempo di setup
          if(cambioSetup && npzCmbS==1 && dataCmbS!=null){
            Long sec=DateUtils.numSecondiDiff(dataCmbS, dataCorr);
            if(sec>=180){
              _logger.warn("Attenzione tempo setup >3 min "+sec);
              
              Double valFermo=(sec-runTmp-180);
              sec=Long.valueOf(180);
              loadFermo=true;
              if(valFermo>0)
                tempoFermi+=valFermo;
              
              //System.out.println(DateUtils.dateToStr(dataCorr, "dd/MM/yyyy HH:mm:ss")+";"+sec.toString()+";"+runTmp+";0;"+valFermo+";"+(sec-runTmp));
            }  
            tempoSetup+=(sec-runTmp);
            tempoRun+=runTmp;
            //System.out.println(DateUtils.dateToStr(dataCorr, "dd/MM/yyyy HH:mm:ss")+";"+sec.toString()+";"+runTmp+";0;0;"+(sec-runTmp));
            dataCmbS=null;
            cambioSetup=false;
            loadSetup=true;
            
          }
          
          //gestisco il tempo relativo ai microfermi 
          if(secTmp!=null && secTmp>TMICROLINF && secTmp<=TMICROLSUP && !loadSetup){
            loadMicro=true;
            tempoMicrofermi+=(secTmp-runTmp);
            tempoRun+=runTmp;
            //System.out.println(DateUtils.dateToStr(dataCorr, "dd/MM/yyyy HH:mm:ss")+";"+secTmp.toString()+";"+runTmp+";"+(secTmp-runTmp)+";0;0");
          } 
          
          //gestisco il tempo relativo ai fermi
          if(secTmp!=null &&  secTmp>TMICROLSUP && !loadFermo){
            loadFermo=true;
            tempoFermi+=(secTmp-runTmp);
            tempoRun+=runTmp;
            //System.out.println(DateUtils.dateToStr(dataCorr, "dd/MM/yyyy HH:mm:ss")+";"+secTmp.toString()+";"+runTmp+";0;"+(secTmp-runTmp)+";0");
          }
          
          if(!loadFermo && !loadMicro && !loadSetup && secTmp!=null){
            tempoRun+=secTmp;
            //System.out.println(DateUtils.dateToStr(dataCorr, "dd/MM/yyyy HH:mm:ss")+";"+secTmp.toString()+";"+secTmp+";0;0;0");
            if(maxLavorazione==0)
              maxLavorazione=secTmp;
            if(maxLavorazione>0)
              maxLavorazione=Math.max(maxLavorazione, secTmp);
          }  
          dataOld=dataCorr;
          spessOld=rlf.getSpessoreBanda();
        }
        
        riga = bfR.readLine();  
      }
      
      map.put(CostantsColomb.TRUNTIME,tempoRun.longValue() );
      map.put(CostantsColomb.TMICROFERMI,tempoMicrofermi.longValue() );
      map.put(CostantsColomb.TGUASTI,tempoFermi.longValue() );
      map.put(CostantsColomb.TSETUP,tempoSetup.longValue() );
      map.put(CostantsColomb.NPZTOT,count );
//      _logger.info("Tempo massimo lavorazione"+maxLavorazione);
      
    }finally{
        _logger.info("File "+fileName+" righe processate:"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return map;
  }
  
  private Double getRuntimeLarghezzaB(Double larghBanda){
    if(larghBanda<=LARGHMM450){
      return SECPMMB450;
    }else if(larghBanda>LARGHMM450 && larghBanda<=LARGHMM600)  {
      return SECPMMBMIN600;
    } else if(larghBanda >LARGHMM600)  {
      return SECPMMBMIN600;
    }
    
    return Double.valueOf(0);
  }
  
  public  List processFileForStat() throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    List info=new ArrayList();
    
    Date dataOld = null;
    Long secTmp=Long.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      
      while(riga!=null && !riga.isEmpty()){
        count++;
        RigaLogFileCombimaTop rlf=new RigaLogFileCombimaTop(riga);
        Date dataCorr=rlf.getDataRif();
        
        if(dataOld!=null){
          secTmp=DateUtils.numSecondiDiff(dataOld, dataCorr);
        }
        
        Integer lunghezza=rlf.getLungBandaRid().intValue();
        Integer larghezza=rlf.getLargBanda().intValue();
        Integer spessore=rlf.getSpessoreBanda().intValue();
        
        
        
        List record=new ArrayList();
        record.add(lunghezza);
        record.add(larghezza);
        record.add(spessore);
        record.add(secTmp > 0 ? secTmp : Long.valueOf(9));
        
        info.add(record);
        
        
        dataOld=dataCorr;
        riga = bfR.readLine();
      } 
    }finally{
        _logger.info("File "+fileName+" righe processate:"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return info;
  }
  
  private static final Logger _logger = Logger.getLogger(LogFileCombimaTop.class);   
}
