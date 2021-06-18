/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;


import colombini.logFiles.ALogFile;
import dtProduzione.rovereta1.P1.DtProdFMorbidelli;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ArrayUtils;

/**
 * Classe per la gestione del file di Log dei dati di Produzione della Foratrice Morbidelli P1
 * @author lvita
 */
public class LogFileProdFMorbidelli  extends ALogFile{

 
  public LogFileProdFMorbidelli(String pFile) {
    super(pFile);
  }
  
  
  @Override
  public List processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    
    String riga; 
    long count=0;
    riga = getBfR().readLine();  
    List <DtProdFMorbidelli> list=new ArrayList();
    while(riga!=null && !riga.isEmpty()){
      count++;
      List info=getListInfoRow(riga);
      if(info!=null && info.size()>6){
        DtProdFMorbidelli dt=new DtProdFMorbidelli(dataInizio);
        dt.loadDataFromList(info);
        list.add(dt);
      }
      riga = getBfR().readLine(); 
    }    
    
    return list;
  }
  
  
  private List getListInfoRow(String riga){
    List info=new ArrayList();
    if(!riga.contains(","))
      return null;
    
    Object [] objArray=riga.split(",");
    info=ArrayUtils.getListFromArray(objArray);
    
    return info;
  }
  

  
  
  
  private static final Logger _logger = Logger.getLogger(LogFileProdFMorbidelli.class); 
}
