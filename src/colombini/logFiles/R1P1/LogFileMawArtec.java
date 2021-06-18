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
public class LogFileMawArtec extends ALogFile {

  
  
  public LogFileMawArtec(String pathFile) {
    super(pathFile);
  }
  
  @Override
  public Object processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    
    List colli=new ArrayList();
    Date fineGg=DateUtils.getFineGg(dataInizio);
    String riga=getBfR().readLine();
    Long countRighe=Long.valueOf(1);
    Long countRgPrc=Long.valueOf(0);
    Long iniRP=null;
    Long finRP=null;
    while(riga!=null && !riga.isEmpty()){
      List infoR=getInfoLog(riga);
      Date dataLog= getDataLogR(infoR);
//      System.out.println("Riga processata"+count);
      if(dataLog!=null && dataLog.after(fineGg)){
        break;
      }
      //non controllo la data inizio in quanto spesso le prime righe riguardano gli ultimi pezzi del giorno prima
      if(dataLog!=null &&  dataLog.after(dataInizio) && dataLog.before(dataFine)){ //dataLog.after(dataInizio) &&
         countRgPrc++;
         //---
         if(iniRP==null)
           iniRP=countRighe;
         
         finRP=countRighe;
         //---
         List record=getNumColli(infoR);
         if(record!=null)
          colli.addAll(record);
      }
      
      riga = getBfR().readLine();
      countRighe++;
    }
    _logger.info("Data ini"+dataInizio +" - data fin"+dataFine +" righe processate "+iniRP+ " - "+ finRP);
    return colli;
    
    
  }
  
   
//  private Boolean isRigaValida(String riga){
//    if(riga==null)
//      return Boolean.FALSE;
//    
//    if(!riga.contains("PC"))
//      return Boolean.FALSE;
//    
//    return Boolean.TRUE;
//  }
  
  private List getInfoLog(String riga){
    List info=new ArrayList();
    if(riga==null || !riga.contains("PC"))
      return null;
    
    Object [] objArray=riga.split(";");
    info=ArrayUtils.getListFromArray(objArray);
    
    return info;
  }
  
  
  private Date getDataLogR(String riga) throws ParseException{
    Date data=null;
    if(riga==null || !riga.contains("PC"))
      return null;
    
    int indice=riga.indexOf(";");
    String dataS=riga.substring(indice,indice+8);
    String oraS=riga.substring(indice+9, indice+17);
    data=DateUtils.StrToDate(dataS+oraS, "dd.MM.yy HH:mm:ss");
   
    return data;
  }
  
  private Date getDataLogR(List info) throws ParseException{
    Date data=null;
    if(info==null || info.isEmpty())
      return null;
    
    
    String dataS=ClassMapper.classToString(info.get(1));
    String oraS=ClassMapper.classToString(info.get(2));
    data=DateUtils.StrToDate(dataS+" "+oraS, "dd.MM.yy HH:mm:ss");
    //-- 16 ore -->960
//    data=DateUtils.addMinutes(data, 975);
    //--
    return data;
  }
  
  private List getNumColli(List info){
    if(info==null || info.isEmpty())
      return null;
    
    List colli=new ArrayList();
    int sizeL=info.size();
    int i=3;
    
    
    while (i<sizeL-1){
      String stringa=ClassMapper.classToString(info.get(i));
      i++;
      if(stringa.contains("<") || stringa.contains(">") || stringa.isEmpty())
        continue;
      
      Integer commessa=ClassMapper.classToClass(stringa.substring(0, 3),Integer.class);
      if(commessa>0){
        List record=new ArrayList();
        Long collo=ClassMapper.classToClass(stringa.substring(3, stringa.lastIndexOf("/")),Long.class);

        record.add(commessa);
        record.add(collo);
        colli.add(record);
      }
    }  
    
    
    return colli;
  }
  
  
 private static final Logger _logger = Logger.getLogger(LogFileMawArtec.class); 
}
