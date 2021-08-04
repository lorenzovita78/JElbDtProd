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
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileSchellingTop4Oee {
  
  public final static Double SECAVANZAMENTO=Double.valueOf(0.0031580020153808);
  public final static Double SECATTESA=Double.valueOf(9.1584);
  public final static Double SECTAGLIO=Double.valueOf(5.2453);
  public final static Double SECPRETAGLIO=Double.valueOf(2.29);

  private String fileName;
  
  
  /**
   * CALCOLO
   * 
   * Runtime = T1 + T2 + T3
   *   T1 = Lungh.za x 0.0032 sec/mm [T.Avanzamento]
   *   T2 = 5,25 sec [T.Taglio]
   *   T3 = 2,29 sec/p.zo [Quota pretaglio]
   * 
   */
  
  
  
  
  public LogFileSchellingTop4Oee(String fileName) {
    this.fileName=fileName;
  }
  
  public  Map processFile(Date dataInizio ,Date dataFine) throws FileNotFoundException, IOException{
    Map map=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    
    Double tempoRun=Double.valueOf(0);
    Double tempoAttesa=Double.valueOf(0);
    Double tempoLav=Double.valueOf(0);
    Double tempoGua=Double.valueOf(0);
    Double tempoMicro=Double.valueOf(0);
    Date dataOldBanda=null;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        RigaLogFileSchellingTop rlf=new RigaLogFileSchellingTop(riga);
        
        if(rlf.getDataRif()!=null && rlf.getDataRif().after(dataFine)){
          break;
        }
        
        if(rlf.getDataRif()!=null && DateUtils.afterEquals(rlf.getDataRif(),dataInizio)){
          if(rlf.isPresentBanda()){
            tempoRun+=SECPRETAGLIO;  
            tempoAttesa+=SECATTESA;
            if(dataOldBanda!=null){
              Double sec=new Double(DateUtils.numSecondiDiff(dataOldBanda, rlf.getDataRif()));
              Double appo= sec-tempoLav-SECATTESA-SECPRETAGLIO;
              if(sec>0 && sec<180 && appo>0){
                tempoMicro+=appo;
              }else  if(sec>=180 && appo>0){
                tempoGua+=appo;
              }
            }
            dataOldBanda=rlf.getDataRif();
            tempoLav=Double.valueOf(0);
           }else if(rlf.isPresentSezionatura()){
            Double tmp=rlf.getDimBanda()*SECAVANZAMENTO;
            tempoRun+=(tmp+SECTAGLIO);
            tempoLav+=(tmp+SECTAGLIO);
          }     
        }
        
        riga = bfR.readLine();  
      }
      map.put(CostantsColomb.TRUNTIME,tempoRun.longValue() );
      map.put(CostantsColomb.TSETUP,tempoAttesa.longValue() );
      map.put(CostantsColomb.TMICROFERMI,tempoMicro.longValue() );
      map.put(CostantsColomb.TGUASTI,tempoGua.longValue() );
     
    }finally{
      _logger.info("File "+fileName+" righe processate:"+count);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return map;
  }
  
  public  List processFileForStat() throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    List info=new ArrayList();
    List appo=new ArrayList();
   
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      
      while(riga!=null && !riga.isEmpty()){
        count++;
        RigaLogFileSchellingTop rlf=new RigaLogFileSchellingTop(riga);

        if(rlf.isPresentBanda()){
          int szL=appo.size();
          for(int i=0; i<szL; i++){
            List record=(List)appo.get(i);
            Double tempo=ClassMapper.classToClass(record.get(1), Double.class);
            tempo+=(SECTAGLIO/szL);
            record.set(1, tempo);
            info.add(record);
          }
          appo=new ArrayList();
         }else if(rlf.isPresentSezionatura()){
          Double tmp=(rlf.getDimBanda()*SECAVANZAMENTO)+SECTAGLIO;
          List record=new ArrayList();
          record.add(rlf.getDimBanda().intValue());
          record.add(tmp);
          appo.add(record);
          
        }     
        
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
  
  private static final Logger _logger = Logger.getLogger(LogFileSchellingTop4Oee.class);   
}
