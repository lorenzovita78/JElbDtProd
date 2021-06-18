/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;


import colombini.logFiles.ALogFile;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileMawFianchi2 extends ALogFile{

  public LogFileMawFianchi2(String pathFile) {
    super(pathFile);
  }


  @Override
  public List processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException {
    
    if(getBfR()==null)
      return null;
    
    
    List colli=new ArrayList();
    Date fineGg=DateUtils.getFineGg(dataInizio);
    String riga=getBfR().readLine();
    while(riga!=null && !riga.isEmpty()){
      Date dataLog= getDataLogR(riga);
      
      if(dataLog!=null && dataLog.after(fineGg)){
        break;
      }
      
      if(dataLog!=null && dataLog.after(dataInizio) && dataLog.before(dataFine)){
         List info=getInfoLog(riga);
         if(info!=null){
           List record=new ArrayList();
           String st=((String)info.get(1)).trim();
           Integer commessa=ClassMapper.classToClass(st , Integer.class);
           st=((String)info.get(2)).trim();
           Long collo=ClassMapper.classToClass(st, Long.class);
           record.add(commessa);
           record.add(collo);
           record.add(dataLog);
           colli.add(record);
         }
      }
     
      riga = getBfR().readLine();
    }
    
    return colli;
  }
  
  
  private Date getDataLogR(String riga) throws ParseException{
    Date data=null;
    String dtS=riga.substring(0, 17);
    data=DateUtils.StrToDate(dtS, "dd.MM.yy HH:mm:ss");
   
    return data;
  }
  
  private List getInfoLog(String riga){
    List info=new ArrayList();
    if(!riga.contains(","))
      return null;
    
    Object [] objArray=riga.substring(17).split(",");
    info=ArrayUtils.getListFromArray(objArray);
    
    return info;
  }
  
 
  private static final Logger _logger = Logger.getLogger(LogFileMawFianchi2.class);
}
