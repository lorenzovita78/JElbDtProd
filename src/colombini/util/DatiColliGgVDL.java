/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.util;

import colombini.model.InfoColloVDL;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class DatiColliGgVDL {
 
  public final static String PATHFILEENTRYGGVDL="//pegaso/Produzione/Rovereta1/VDL-MSL/EntryVDL/";
  private Date dataRif=null;
  private Map <String,Map> mapLinee; //mappa contenente come chiave il codice della linea e come elemento un 'altra mappa
                                     // che a sua volta contiene come chiave la stringa ncommessa;ncollo e come valore l'oggetto InfoColloVDL
  
  
  public DatiColliGgVDL(Date data ) {
    this.dataRif=data;
    
  }
  
  
  public Map getMapColliLinea(){
    if(mapLinee!=null)
      return mapLinee;
    
    mapLinee=new HashMap();
    try{
      elabDatiLinee();
    
    } catch(FileNotFoundException ex){
      _logger.error(" File relativo ai dati VDL non trovato.Impossibile proseguire -->"+ex.getMessage());
    } catch(IOException ex){
      _logger.error(" Problemi di accesso ai dati VDL .Impossibile proseguire -->"+ex.getMessage());
    } catch(ParseException ex){
      _logger.error(" Problemi di conversione dei campi data.Impossibile proseguire -->"+ex.getMessage());
    }
    
    return mapLinee;
  }

  
  private void elabDatiLinee() throws FileNotFoundException, IOException, ParseException {
    
//    Map propE=ElabsProps.getInstance().getProperties(ElabsName.ELABAVPRODCOM);
//    String pathFile=(String) propE.get(ElabsName.PATHFILEENTRYGGVDL);
    String nomeFile=PATHFILEENTRYGGVDL+getFileName(PATHFILEENTRYGGVDL);

    if(nomeFile==null || StringUtils.isEmpty(nomeFile)){
      throw new FileNotFoundException("Nessun file presente per data "+dataRif);
    }
      
    _logger.info("Nome file ricercato : "+nomeFile);
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Long proc=Long.valueOf(0);
    
    try{
      fR = new FileReader(nomeFile);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        if(count==1){
          riga = bfR.readLine();  
          continue;
        }
          
        InfoColloVDL collo=getInfoColloNew(riga);
        if(DateUtils.daysBetween(dataRif, collo.getOraIn())==0){
          proc++;
          String linea=collo.getLineaLogicaMvx();
          Map <String,InfoColloVDL> comcolli =null ;
          if(mapLinee.containsKey(linea)){
            comcolli=mapLinee.get(linea);
            InfoColloVDL tmp=comcolli.get(collo.getKey());
            if(tmp!=null){
              tmp.addArticoli(collo.getArticoli());
            }else{
              tmp=collo;
            }
            comcolli.put(collo.getKey(),tmp);
          }else{
            comcolli=new HashMap();
            comcolli.put(collo.getKey(), collo);
          }
           mapLinee.put(linea,comcolli);
         }
        riga= bfR.readLine();
      }
        
    }finally{
      if(proc==0)
        _logger.error("!!Attenzione righe processate del file "+nomeFile+" == 0 ");
      
      _logger.info("File "+nomeFile+" righe lette:"+count+" righe processate:"+proc);
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    
  }
  
  /**
   * Data la cartella cerca il nome del file la cui data corrisponde alla data di elaborazione
   * @param pathFile
   * @return String nomeFile
   * @throws ParseException 
   */
  private String getFileName(String pathFile) throws ParseException{
    String dateL=DateUtils.DateToStr(DateUtils.addDays(dataRif, 1),"ddMMyyyy");
    String nomeFile="";
    File dir=new File (pathFile);
    
    if(dir.isFile())
      return null;
    
    List<String> filesName=ArrayUtils.getListFromArray(dir.list());
    if(filesName==null || filesName.isEmpty())
      return null;
    
    for(String name:filesName){
      if(name.contains(dateL)){
        nomeFile=name;
      }
    }
    
    return nomeFile;
  }
  
  
  
  private InfoColloVDL getInfoColloNew(String riga){
    InfoColloVDL clVDL;
    String [] vett=riga.split(";");
    String stringaEl=vett[0];
    
    Date data=DateUtils.strToDate(stringaEl, "dd/MM/yyyy hh:mm:ss");
    // linea
    String linea=vett[1];
    //commessa collo
    Integer comm=Integer.valueOf(vett[2].substring(0, 3));
    Long collo=Long.valueOf(vett[2].substring(3));
    //articolo
    clVDL=new InfoColloVDL(data, comm, linea, collo);
    clVDL.addArticolo(vett[3]);
    clVDL.setRifSpunta(vett[4]);
    clVDL.setCodiceBU(vett[5]);
    
    return clVDL;
  }
 
  
  public List<InfoColloVDL> getListColliLineaLog(String lineaLogica){
    return getListColliLineaLog4Bu(lineaLogica, null);
  }
  
  public List<InfoColloVDL> getListColliLineaLog4Bu(String lineaLogica,List<String> bu){
    if(StringUtils.IsEmpty(lineaLogica))
      return null;
    
    if(mapLinee==null){
      getMapColliLinea();
    }

    Map<String,InfoColloVDL> colli =(Map<String,InfoColloVDL>) mapLinee.get(lineaLogica);
    if(colli==null)
      return null;
    
    Set keys=colli.keySet();
    Iterator iter=keys.iterator();
    List colliLinea=new ArrayList();
    while(iter.hasNext()){
      InfoColloVDL infoVDL=colli.get((String)iter.next());
      //verifichiamo che il codice della ditta del pz sia tra quelli forniti come parametro
      List record=new ArrayList();
      if(bu==null ){
        record.add(infoVDL.getCommessa());
        record.add(infoVDL.getnCollo());
      }else {
        if( bu.contains(infoVDL.getCodiceBU()) ){
          record.add(infoVDL.getCommessa());
          record.add(infoVDL.getnCollo());
          record.add(infoVDL.getCodiceBU());
        }
      }
      if(record.size()>0)
        colliLinea.add(record);
    }
    
    return colliLinea;
  }
  
  
  public List getListColliLineeLog(List<String> lineeLog) {
    return getListColliLineeLog4Bu(lineeLog,null);
  }
  
  
  public List getListColliLineeLog4Bu(List<String> lineeLog,List<String> bu ) {
    if(lineeLog==null || lineeLog.isEmpty())
      return null;
    
    List colliLinea=new ArrayList();
    
    for(String linea : lineeLog){
      List tempLinea=getListColliLineaLog4Bu(linea,bu);
      if(tempLinea!=null)
        colliLinea.addAll(tempLinea);
    }  
    
    return colliLinea;
  }
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(DatiColliGgVDL.class);
  
  
}
