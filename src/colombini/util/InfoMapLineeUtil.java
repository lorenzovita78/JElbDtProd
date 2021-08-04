/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.util;

import java.io.FileNotFoundException;
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
import utils.ArrayListSorted;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FilePropUtils;
import utils.StringUtils;

/**
 * Classe statica che serve per gestire tutte le informazioni associate alle linee per
 * cui vengono calcolati gli indicatori di efficienza
 * @author lvita
 */
public class  InfoMapLineeUtil {
  
  
  public final static String LINEAOEE="LINEAOEE";
  public final static String FILEXLS="FILEXLS";
  public final static String FILELOGGG="FILELOGGG";
  public final static String FILELOGSOURCE="FILELOGSOURCE";
  public final static String TABUPROD="TABUPROD";
  
  
  private static Map mapLineeOee=null;
  private static Map mapLineeOeeCod=null;
  private static Map mapLineeOeeFileXls=null;
  private static Map mapLineeFileLogGg=null;
  private static Map mapLineeFileLogSource=null;
  private static Map mapLineeTabulatiProd=null;
  
  private static void readFileProperties() {
    
    mapLineeOee=new HashMap();
    mapLineeOeeCod=new HashMap();
    mapLineeOeeFileXls=new HashMap();
    mapLineeFileLogGg=new HashMap();
    mapLineeFileLogSource=new HashMap();
    mapLineeTabulatiProd=new HashMap();
            
    try{
      Map mappaProp = FilePropUtils.getInstance().readFilePropKeyVal("../props/colombiniProd.properties");

      Set keys=mappaProp.keySet();
      Iterator iter=keys.iterator();
      while(iter.hasNext()){
        String key=ClassMapper.classToString(iter.next());
        if(key.contains(LINEAOEE)){
          String linea=key.substring(key.indexOf("-")+1);
          String valore=ClassMapper.classToString(mappaProp.get(key));
          List val =ArrayUtils.getListFromArray(valore.split(":"));
          mapLineeOee.put(linea,ClassMapper.classToString(val.get(0)));
          mapLineeOeeCod.put(linea,ClassMapper.classToString(val.get(1)));
        }
        if(key.contains(FILEXLS)){
          String linea=key.substring(key.indexOf("-")+1);
          String nomeFile=ClassMapper.classToString(mappaProp.get(key));
          mapLineeOeeFileXls.put(linea,nomeFile);
        }
        if(key.contains(FILELOGGG)){
          String linea=key.substring(key.indexOf("-")+1);
          String nomeFile=ClassMapper.classToString(mappaProp.get(key));
          mapLineeFileLogGg.put(linea,nomeFile);
        }
        if(key.contains(FILELOGSOURCE)){
          String linea=key.substring(key.indexOf("-")+1);
          String nomeFiles=ClassMapper.classToString(mappaProp.get(key));
          mapLineeFileLogSource.put(linea,nomeFiles);  
        }  
         if(key.contains(TABUPROD)){
          String linea=key.substring(key.indexOf("-")+1);
          String nomeFiles=ClassMapper.classToString(mappaProp.get(key));
          mapLineeTabulatiProd.put(linea,nomeFiles);  
        }
        
      }
    }catch(FileNotFoundException ex){
      _logger.error("Impossibile trovare il file delle proprietà oee.properties ->"+ex.getMessage()); 
    }catch(IOException e){
      _logger.error("Impossibile accedere al file delle proprietà oee.properties->"+e.getMessage());
    }
    
  }
  
  /**
   * Restituisce la mappa contentente per ciascuna linea il relativo nome del 
   * file di log  (se esiste)
   * @return Map <String,String>
   */
  public static Map<String,String> getMapLineeFileLogGg() {
    if(mapLineeFileLogGg==null){
      readFileProperties();
    }
    return mapLineeFileLogGg;
  }

  /**
   * Restituisce una mappa che indica le linee attive per il calcolo Oee .
   * Data una linea viene indicata la stringa codstabilimento+codpiano
   * @return Map <String,String>
   */
  public static Map<String,String> getMapLineeOee() {
    if(mapLineeOee==null){
      readFileProperties();
    }
    return mapLineeOee;
  }
  
  /**
   * Restituisce una mappa che indica il codice utilizzato per gli Oee legato alla linea
   * @return Map <String,String>
   */
  public static Map<String,String> getMapLineeOeeCod() {
    if(mapLineeOeeCod==null){
      readFileProperties();
    }
    return mapLineeOeeCod;
  }

  
  /**
   * Restituisce la mappa contentente per ciascuna linea il relativo nome del 
   * file xls  relativo ai dati di produzione della linea
   * @return Map <String,String>
   */
  public static Map<String,String> getMapLineeOeeFileXls() {
    if(mapLineeOeeFileXls==null){
      readFileProperties();
    }
    return mapLineeOeeFileXls;
  }

  public static Map getMapLineeFileLogSource() {
    if(mapLineeFileLogSource==null){
      readFileProperties();
    }
    return mapLineeFileLogSource;
  }

  
  
  
  public static Map getMapLineeTabulatiProd() {
    if(mapLineeTabulatiProd==null){
      readFileProperties();
    }
    return mapLineeTabulatiProd;
  }
  
  
  
  
  
  /**
   * Dato il nome della linea trorna il relativo codice per i dati OEE
   * @param nomeLinea
   * @return String codiceLinea
   */
  public static String getCodiceLinea(String nomeLinea){
    return getMapLineeOeeCod().get(nomeLinea);
  }
  
  /**
   * Torna il path che contiene i file da analizzare per una determinata linea
   * @param String linea 
   * @return String path
   */
  public static String getPathLogSourceFile(String linea){
    String fileDest=(String)getMapLineeFileLogSource().get(linea);
    fileDest=FilePropUtils.getInstance().getJavaPath(fileDest);
    
    if(fileDest.length()==(fileDest.lastIndexOf("/")+1))
      return fileDest;
    
    
    return "";
  }
  
  
  public static String getPathLogDestFile(String linea) {
    String fileDest=(String)getMapLineeFileLogGg().get(linea);
    fileDest=FilePropUtils.getInstance().getJavaPath(fileDest);
    
    
    if(fileDest.contains("$")){
      return fileDest.substring(0, fileDest.lastIndexOf("/")+1);  
    }else if(fileDest.length()==(fileDest.lastIndexOf("/")+1)){
      return fileDest;
    }
    
    return "";
  }
  

  
   /**
   * Torna il nome del file di log  che deve essere elaborato per generare il file log giornaliero
   * @param linea
   * @param data
   * @return
   */
  public static String getLogFileSourceGgLinea(String linea,Date data) {
    String fileDest=null;
    Map mapFileLog=getMapLineeFileLogSource();
    fileDest=getLogFileName((String)mapFileLog.get(linea), data);
   
   
    return fileDest;
  }
  
  /**
   * Torna il file sorgente di una data linea (caso in cui il file sorgente contiene le informazioni 
   * di più giornate)
   * @param linea
   * @return 
   */
  public static String getLogFileSourceLinea(String linea) {
    String fileDest=(String) getMapLineeFileLogSource().get(linea);
   
    return fileDest;
  }
  
  
  /**
   * Torna una lista dei file che devono essere elaborati per generare il file giornaliero
   * @param linea
   * @return List files
   */
  public static List getListLogFileSourceLinea(String linea) {
    
    String filesString=(String) getMapLineeFileLogSource().get(linea);
    List files=ArrayUtils.getListFromArray(filesString.split(";"));
   
   
    return files;
  }
  
  /**
   * Torna il nome del file di log giornaliero (già elaborato)
   * @param linea
   * @param data
   * @return
   */
  public static String getLogFileGgLinea(String linea,Date data){
    String fileDest=null;
    Map mapFileLog=getMapLineeFileLogGg();
    fileDest=getLogFileName((String)mapFileLog.get(linea), data);
   
   
    return fileDest;
  }
 
 
  /**
   * Prevede che la stringa passata abbia il formato di come deve essere trasformata la data
   * per generare il nome del file di log giornaliero.
   * @param paramF
   * @param data
   * @return 
   */
  public static String getLogFileName(String paramF,Date data){
    
    return getStringReplaceWithDate(paramF, data);
  }
  
  /**
   * Data una stringa contentente un tag che indica il formato data ritorna
   * @param stringa
   * @param data
   * @return 
   */
  public static String getStringReplaceWithDate(String stringa,Date data){
    String nomeFile = null;
    String formatoD="";
    
    if(data==null)
      return stringa;
    
    try {
      formatoD=stringa.substring(stringa.indexOf("$")+1, stringa.lastIndexOf("$"));
      String dateS=DateUtils.DateToStr(data, formatoD);
      nomeFile=stringa.replace("$"+formatoD+"$", dateS);
      
    } catch (ParseException ex) {
      _logger.error(" Impossibile convertire la data nel formato richiesto :"+formatoD+ " -->"+ex.getMessage());
    }
    
    return nomeFile;
  }
  
  
  
  /**
   * Data una linea e il numero di commessa restituisce il nome del tabulato di produzione
   * @param linea
   * @param numC
   * @return String
   */
  public static String getTabuProdNameLinea(String linea,Integer numC){
    String fileDest=null;
    Map mapFileLog=getMapLineeTabulatiProd();
    fileDest=getTabulatoProdName((String)mapFileLog.get(linea), numC);
   
   
    return fileDest;
  }
  
  
  private static String getTabulatoProdName(String paramF, Integer numC) {
    String formatoN="";
    String nomeFile="";
    
    formatoN=paramF.substring(paramF.indexOf("$")+1, paramF.lastIndexOf("$"));
    String nC=numC.toString();
    while(formatoN.length()>nC.length()){
      nC="0"+nC;
    }
    nomeFile=paramF.replace("$"+formatoN+"$", nC);
      
    return nomeFile;
  }
  
  
  
  /**
   * Torna il nome della linea dato il codice
   * @param codLinea
   * @return String nomeLinea
   */
  public static String getNomeLineaFromCodice(String codLinea){
    String nome="";
    if(StringUtils.IsEmpty(codLinea))
      return nome;
    
    if(mapLineeOeeCod==null){
      readFileProperties();
    }
    
    Set keys=mapLineeOeeCod.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()&& StringUtils.IsEmpty(nome)){
      String key=(String) iter.next();
      String value=(String) mapLineeOeeCod.get(key);
      if(codLinea.equals(value)){
        nome=key;
      }          
    }
    
    return nome;
  }
  
  /**
   * Torna il nome del file Xls della linea indicata
   * @param nomeLinea
   * @return String nome file xls
   */
  public static String getNomeFileXls(String nomeLinea){
    return getMapLineeOeeFileXls().get(nomeLinea);
  }
  
  
  public static List getListLinee(String stab,String piano,Map lineeToElab){
    List<String> linee=new ArrayList();
    Set keys=lineeToElab.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String linea=(String) iter.next();
      String codStab=(String) getMapLineeOee().get(linea);
      if(codStab.contains(stab) && (piano==null || codStab.contains(piano) )  )
        linee.add(linea);
    }
    return linee;
  }
  
  
  private static final Logger _logger = Logger.getLogger(InfoMapLineeUtil.class);

  
}
