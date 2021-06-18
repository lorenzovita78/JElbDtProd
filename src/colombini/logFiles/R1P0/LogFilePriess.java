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
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class LogFilePriess {

  
  public static Long TEMPOMAXFORATURA=Long.valueOf(122);
  
  private String fileName;
  
  public LogFilePriess(String fName){
    this.fileName=fName;
  }
  
  
  public Map elabFile(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException {
    Map tempi=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Long proc=Long.valueOf(0);
    Long runtime=Long.valueOf(0);
    InfoRow rowOld=null;
    Long guasti=Long.valueOf(0);
    Long perdG=Long.valueOf(0);
    Long microF=Long.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        InfoRow row=new InfoRow(riga);
        if( DateUtils.afterEquals(row.getOraIniF1(), dataInizio) 
                   && DateUtils.beforeEquals(row.getOraIniF1(), dataFine)) {
          
          proc++;
          Long g1=Long.valueOf(0);
          
//          g1=row.getSecGUF1()+row.getSecGUF2()+row.getSecFMF1()+row.getSecFMF2();
          g1=row.getSecGUF2()+row.getSecFMF2();
         
//          Long pg1=Math.min(row.getSecPGF1(),row.getSecPGF2());
          Long pg1=Long.valueOf(0);
            
          if(count==1){
            g1=Long.valueOf(0);
            pg1=Long.valueOf(0);
          }

          guasti+=g1;
          perdG+=pg1;
          if(rowOld!=null && rowOld.isValid()){
            Long t1=DateUtils.numSecondiDiff(rowOld.getOraFin(), row.getOraFin());
//            System.out.println(DateUtils.dateToStr(rowOld.getOraFin(),"HH:mm:ss")+";"+DateUtils.dateToStr(rowOld.getOraFin(),"mm")+";"+DateUtils.dateToStr(rowOld.getOraFin(),"ss")+";"+
//                               DateUtils.dateToStr(row.getOraFin(),"HH:mm:ss")+";"+DateUtils.dateToStr(row.getOraFin(), "mm")+";"+DateUtils.dateToStr(row.getOraFin(), "ss")+
//                               ";"+t1);
            
            if((t1-g1-pg1)<0){
              _logger.warn("Attezione -> "+row.getArticolo() +" alla riga n."+count +" con somma tempi negativa");
            }else{
              Long tmp=t1-g1-pg1;
              if(tmp>TEMPOMAXFORATURA){
                _logger.warn("Attezione -> "+row.getArticolo() +" alla riga n."+count +" tempo netto > tempomaxforatura");
                microF+=(tmp-TEMPOMAXFORATURA);
                tmp=TEMPOMAXFORATURA;
              }
              runtime+=(tmp);
            }
          }
        }
        rowOld=row;
        riga = bfR.readLine();  
      }
      
      tempi.put(CostantsColomb.TRUNTIME,runtime);
      tempi.put(CostantsColomb.TGUASTI,guasti);
      tempi.put(CostantsColomb.TPERDGEST,perdG);
      tempi.put(CostantsColomb.TMICROFERMI,microF);
      
    }finally{
      _logger.info("File "+fileName+" righe lette:"+count+" righe processate :"+proc);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return tempi;
  }
  
  public Map elabFileOld(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException{
    Map tempi=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Long proc=Long.valueOf(0);
    Long runtime=Long.valueOf(0);
    Long runtime2=Long.valueOf(0);
    Long guasti=Long.valueOf(0);
    Long perdG=Long.valueOf(0);
    Long microF=Long.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        InfoRow row=new InfoRow(riga);
        if( DateUtils.afterEquals(row.getOraIniF1(), dataInizio) 
                   && DateUtils.beforeEquals(row.getOraIniF1(), dataFine)) {
          
          proc++;
          Long t1=DateUtils.numSecondiDiff(row.getOraIniF1(), row.getOraFinF1());
          Long t2=DateUtils.numSecondiDiff(row.getOraIniF2(), row.getOraFinF2());
          Long m1=Long.valueOf(0);
          Long m2=Long.valueOf(0);
//          String appo=riga.substring(0, 61);
//          System.out.println(" R. "+count.toString()+ " -> "+appo+"  -- Tempo F1:"+t1 +"  Tempo F2:"+t2);
          if(t1>TEMPOMAXFORATURA)
            m1=(t1-TEMPOMAXFORATURA);
          if(t2>TEMPOMAXFORATURA)
            m2=(t2-TEMPOMAXFORATURA);
          
          t1=Math.min(t1, TEMPOMAXFORATURA);
          t2=Math.min(t2, TEMPOMAXFORATURA);
          
          microF+=Math.max(m1, m2);
          runtime+=Math.max(t2,t1);
          runtime2+=Math.min(t2,t1);
          if(count!=1){
            guasti+=row.getSecGUF1()+row.getSecGUF2()+row.getSecFMF1()+row.getSecFMF2();
            perdG+=row.getSecPGF1()+row.getSecPGF2();
          }
          
        }
        riga = bfR.readLine();  
      }
      
      tempi.put(CostantsColomb.TRUNTIME,runtime);
      tempi.put(CostantsColomb.TLORDO,runtime2);
      tempi.put(CostantsColomb.TGUASTI,guasti);
      tempi.put(CostantsColomb.TPERDGEST,perdG);
      tempi.put(CostantsColomb.TMICROFERMI,microF);
      
    }finally{
      _logger.info("File "+fileName+" righe lette:"+count+" righe processate :"+proc);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return tempi;
  }
  
  public Map elabFile(Date dataInizio,Date dataFine,String foratrice) throws FileNotFoundException, IOException{
    Map tempi=new HashMap();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Long proc=Long.valueOf(0);
    Long runtime=Long.valueOf(0);
    Long guasti=Long.valueOf(0);
    Long perdG=Long.valueOf(0);
    Long runTmp=Long.valueOf(0);
    Long microF=Long.valueOf(0);
    Long microTmp=Long.valueOf(0);
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        InfoRow row=new InfoRow(riga);
        if( DateUtils.afterEquals(row.getOraIniF1(), dataInizio) 
                   && DateUtils.beforeEquals(row.getOraIniF1(), dataFine)) {
          
          proc++;
          Long t1=DateUtils.numSecondiDiff(row.getOraIniF1(), row.getOraFinF1());
          Long t2=DateUtils.numSecondiDiff(row.getOraIniF2(), row.getOraFinF2());
//          String appo=riga.substring(0, 61);
//          System.out.println(" R. "+count.toString()+ " -> "+appo+"  -- Tempo F1:"+t1 +"  Tempo F2:"+t2);
          microTmp=Long.valueOf(0);
          if(foratrice.contains("M1")){
            runTmp=Math.min(t1,TEMPOMAXFORATURA);
            if(t1>TEMPOMAXFORATURA)
              microTmp=(t1-TEMPOMAXFORATURA);
          }else{
            runTmp=Math.min(t2,TEMPOMAXFORATURA);
            if(t2>TEMPOMAXFORATURA)
              microTmp=(t2-TEMPOMAXFORATURA);
          }
          
          microF+=microTmp;
          runtime+=runTmp;
          
          if(count!=1){
            guasti+=row.getSecGUF1()+row.getSecGUF2()+row.getSecFMF1()+row.getSecFMF2();
            perdG+=row.getSecPGF1()+row.getSecPGF2();
          }
          
        }
        riga = bfR.readLine();  
      }
      
      tempi.put(CostantsColomb.TRUNTIME,runtime);
      tempi.put(CostantsColomb.TGUASTI,guasti);
      tempi.put(CostantsColomb.TPERDGEST,perdG);
      tempi.put(CostantsColomb.TMICROFERMI,microF);
      
    }finally{
      _logger.info("File "+fileName+" righe lette:"+count+" righe processate :"+proc);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return tempi;
  }
  
  public List getTempiForatrici() throws FileNotFoundException, IOException{
    List info=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        InfoRow row=new InfoRow(riga);
        List record=new ArrayList();
        
//        Tempi ciclo per Fuasto
        if(row.isValid()){
          Long t1=DateUtils.numSecondiDiff(row.getOraIniF1(), row.getOraFinF1());
          Long t2=DateUtils.numSecondiDiff(row.getOraIniF2(), row.getOraFinF2());
        
        
          record.add(row.getArticolo());
          record.add(t1);
          record.add(t2);
        
//        if(row.isValid()){
//          record.add(row.getOraIniF1());
//          record.add(row.getOraFin());
//          Long t1=DateUtils.numSecondiDiff(row.getOraIniF1(), row.getOraFinF1());
//          Long t2=DateUtils.numSecondiDiff(row.getOraIniF2(), row.getOraFinF2());
//          record.add(t1);
//          record.add(t2);
//          record.add(row.getSecPGF1());
//          record.add(row.getSecPGF2());
//          record.add(row.getSecGUF1());
//          record.add(row.getSecGUF2());
//          record.add(row.getSecFMF1());
//          record.add(row.getSecFMF2());
//        }
          info.add(record);
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
  
  
  public class InfoRow{
    
    private String articolo;
    private Date dataRif;
    private Date oraIniF1;
    private Date oraIniF2;
    private Date oraFinF1;
    private Date oraFinF2;
    private Date oraFin;
    
    private Long secPGF1;
    private Long secPGF2;
    private Long secGUF1;
    private Long secGUF2;
    private Long secFMF1;
    private Long secFMF2;
    private boolean valid=false;
    
    private InfoRow(String riga){
      if(riga==null)
        return;
      valid=false;      
      List info=ArrayUtils.getListFromArray(riga.split(";"));
      
      if(info!=null && info.size()>15){
        articolo=ClassMapper.classToString(info.get(0));
        String dataS=ClassMapper.classToString(info.get(1));
        dataRif=ClassMapper.classToClass(dataS,Date.class);
        
        oraIniF1=getDataOra(dataS,ClassMapper.classToString(info.get(2)));
        oraFinF1=getDataOra(dataS,ClassMapper.classToString(info.get(3)));
        oraIniF2=getDataOra(dataS,ClassMapper.classToString(info.get(4)));
        oraFinF2=getDataOra(dataS,ClassMapper.classToString(info.get(5)));
        oraFin=getDataOra(dataS,ClassMapper.classToString(info.get(6)));

        secPGF1=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(9)));
        secPGF2=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(10)));
        secGUF1=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(11)));
        secGUF2=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(12)));
        secFMF1=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(13)));
        secFMF2=getSecDiff(dataRif, dataS,ClassMapper.classToString(info.get(14)));
  
        if(articolo!=null && oraIniF1!=null && oraFinF1!=null && oraIniF2!=null && oraFinF2!=null  && oraFin!=null){
          valid=true;
        }
      }        
      
    }

    public boolean isValid() {
      return valid;
    }

    
    
    
    private Long getSecDiff(Date dataRif,String dataS,String oraS){
      Date data=getDataOra(dataS, oraS);
      
      return DateUtils.numSecondiDiff(dataRif,data);
      
    }
    
    private Date getDataOra(String dataS,String oraS){
      Date data=null;
      
      if(StringUtils.isEmpty(dataS))
        return data;
      
      if(StringUtils.isEmpty(oraS) )
        return data;
      
      try {
        data=DateUtils.StrToDate(dataS+" "+oraS , "yyyyMMdd HH:mm:ss");
      } catch (ParseException ex) {
          _logger.error("Impossibile convertire la data :"+dataS+oraS+ " --> " +ex.getMessage());
      } finally{
        
        return data;
      }
      
      
    }
    
    public String getArticolo() {
      return articolo;
    }

    public Date getDataRif() {
      return dataRif;
    }

    public Date getOraFin() {
      return oraFin;
    }

    public Date getOraFinF1() {
      return oraFinF1;
    }

    public Date getOraFinF2() {
      return oraFinF2;
    }

    public Date getOraIniF1() {
      return oraIniF1;
    }

    public Date getOraIniF2() {
      return oraIniF2;
    }

    public Long getSecFMF1() {
      return secFMF1;
    }

    public Long getSecFMF2() {
      return secFMF2;
    }

    public Long getSecGUF1() {
      return secGUF1;
    }

    public Long getSecGUF2() {
      return secGUF2;
    }

    public Long getSecPGF1() {
      return secPGF1;
    }

    public Long getSecPGF2() {
      return secPGF2;
    }
    

  }
  
  
  
  private static final Logger _logger = Logger.getLogger(LogFilePriess.class);   
}

