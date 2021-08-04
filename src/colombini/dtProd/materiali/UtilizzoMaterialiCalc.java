/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.dtProd.materiali;

import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.dtProd.R1.XmlFileSezR1P4;
import colombini.logFiles.G1P2.LogFileSchellingLong;
import colombini.logFiles.G1P2.LogFileSquadrabordaLong;
import colombini.logFiles.R1P1.LogFileCombima;
import colombini.logFiles.R1P1.LogFileSchelling;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.G1.QueryMaterialeArticoliG1P2Ima;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import elabObj.MessagesElab;
import exception.QueryException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class UtilizzoMaterialiCalc {
  
  public final static String IMAANTECOMB1SP1O="R1P10111";
  public final static String IMAANTECOMB1SP1L="R1P10112";
  public final static String IMAANTECOMB1SP2O="R1P10121";
  public final static String IMAANTECOMB1SP2L="R1P10122";
  
  public final static String IMAANTECOMB2SP1O="R1P10211";
  public final static String IMAANTECOMB2SP1L="R1P10212";
  public final static String IMAANTECOMB2SP2O="R1P10221";
  public final static String IMAANTECOMB2SP2L="R1P10222";
  
  public final static String IMAANTESCHELL1="R1P103";
  public final static String IMAANTESCHELL2="R1P104";
  
  public final static String IMATOPCOMBSP1O="R1P00111";
  public final static String IMATOPCOMBSP1L="R1P00112";
  public final static String IMATOPCOMBSP2O="R1P00121";
  public final static String IMATOPCOMBSP2L="R1P00122";
 
  public final static String IMATOPSCHELL1="R1P002";
  public final static String IMATOPSCHELL2="R1P003";
  
  
  //GALAZZANO
  //Schelling Longitudinale
  public final static String SCHELLINGLONGG1="G1P201";
  //Schelling 13266
  public final static String SCHELL13266BSP1="G1P20201";
  public final static String SCHELL13266BSP2="G1P20202";
  public final static String SCHELL13266SEZ="G1P20203";
  //Schelling 13268
  public final static String SCHELL13268BSP1="G1P20301";
  public final static String SCHELL13268BSP2="G1P20302";
  public final static String SCHELL13268SEZ="G1P20303";
  //Schelling 13200
  public final static String IMA13200SP1="G1P20401";
  public final static String IMA13200SP2="G1P20402";
  //Schelling 13267
  public final static String IMA13267SP1="G1P20501";
  public final static String IMA13267SP2="G1P20502";
  public final static String IMA13267MFBA="G1P20503";
  //Schelling 13269
  public final static String IMA13269SP1="G1P20601";
  public final static String IMA13269SP2="G1P20602";
  public final static String IMA13269MFBA="G1P20603";
  
  public final static String SEZBIESSEWNP4="R1P402";
  
  public final static String SPALLA1="SPALLA1";
  public final static String SPALLA2="SPALLA2";
  public final static String MFBA="MONT.FBA";
  
  
  
  public final static String METRI="ML";
  
  
//Valore x anno 2012-->  public static final Long CMTAGLIOIMAANTE=Long.valueOf(550);
//Valore x anno 2012-->  public static final Long CMTAGLIOIMATOP=Long.valueOf(410);
  public static final Long CMTAGLIOIMAANTE=Long.valueOf(475);
  public static final Long CMTAGLIOIMATOP=Long.valueOf(615); //valore x anno 2013
  
  
  private Map<String,String> strGer;
  private MessagesElab msg;
  
  
  public UtilizzoMaterialiCalc() {
    loadMapStrGer();
    msg=new MessagesElab();
  }

  public MessagesElab getMsg() {
    return msg;
  }
  
  
  
  
  
  private void loadMapStrGer() {
    strGer=new HashMap();
    //R1
    strGer.put(NomiLineeColomb.COMBIMA1R1P1+LogFileCombima.LAVSP1_N, IMAANTECOMB1SP1O);
    strGer.put(NomiLineeColomb.COMBIMA1R1P1+LogFileCombima.LAVSP1_L, IMAANTECOMB1SP1L);
    strGer.put(NomiLineeColomb.COMBIMA1R1P1+LogFileCombima.LAVSP2_N, IMAANTECOMB1SP2O);
    strGer.put(NomiLineeColomb.COMBIMA1R1P1+LogFileCombima.LAVSP2_L, IMAANTECOMB1SP2L);
    
    strGer.put(NomiLineeColomb.COMBIMA2R1P1+LogFileCombima.LAVSP1_N, IMAANTECOMB2SP1O);
    strGer.put(NomiLineeColomb.COMBIMA2R1P1+LogFileCombima.LAVSP1_L, IMAANTECOMB2SP1L);
    strGer.put(NomiLineeColomb.COMBIMA2R1P1+LogFileCombima.LAVSP2_N, IMAANTECOMB2SP2O);
    strGer.put(NomiLineeColomb.COMBIMA2R1P1+LogFileCombima.LAVSP2_L, IMAANTECOMB2SP2L);
    
    strGer.put(NomiLineeColomb.COMBIMAR1P0+LogFileCombima.LAVSP1_N, IMATOPCOMBSP1O);
    strGer.put(NomiLineeColomb.COMBIMAR1P0+LogFileCombima.LAVSP1_L, IMATOPCOMBSP1L);
    strGer.put(NomiLineeColomb.COMBIMAR1P0+LogFileCombima.LAVSP2_N, IMATOPCOMBSP2O);
    strGer.put(NomiLineeColomb.COMBIMAR1P0+LogFileCombima.LAVSP2_L, IMATOPCOMBSP2L);
    
    strGer.put(NomiLineeColomb.SCHELLING1R1P1, IMAANTESCHELL1);
    strGer.put(NomiLineeColomb.SCHELLING2R1P1, IMAANTESCHELL2);
    strGer.put(NomiLineeColomb.SCHELLING1R1P0, IMATOPSCHELL1);
    strGer.put(NomiLineeColomb.SCHELLING2R1P0, IMATOPSCHELL2);
    
    //G1
    strGer.put(NomiLineeColomb.SCHELLLONG_GAL, SCHELLINGLONGG1);
    
    strGer.put(NomiLineeColomb.SQBL13266+SPALLA1, SCHELL13266BSP1);
    strGer.put(NomiLineeColomb.SQBL13266+SPALLA2, SCHELL13266BSP2);
    strGer.put(NomiLineeColomb.SQBL13266+LogFileSquadrabordaLong.SEZIONATURA, SCHELL13266SEZ);
    
    
    strGer.put(NomiLineeColomb.SQBL13268+SPALLA1, SCHELL13268BSP1);
    strGer.put(NomiLineeColomb.SQBL13268+SPALLA2, SCHELL13268BSP2);
    strGer.put(NomiLineeColomb.SQBL13268+LogFileSquadrabordaLong.SEZIONATURA, SCHELL13268SEZ);
    
    strGer.put(NomiLineeColomb.IMAGAL_13200+SPALLA1, IMA13200SP1);
    strGer.put(NomiLineeColomb.IMAGAL_13200+SPALLA2, IMA13200SP2);
    
    strGer.put(NomiLineeColomb.IMAGAL_13267+SPALLA1, IMA13267SP1);
    strGer.put(NomiLineeColomb.IMAGAL_13267+SPALLA2, IMA13267SP2);
    strGer.put(NomiLineeColomb.IMAGAL_13267+MFBA, IMA13267MFBA);
    
    strGer.put(NomiLineeColomb.IMAGAL_13269+SPALLA1, IMA13269SP1);
    strGer.put(NomiLineeColomb.IMAGAL_13269+SPALLA2, IMA13269SP2);
    strGer.put(NomiLineeColomb.IMAGAL_13269+MFBA, IMA13269MFBA);
    
    strGer.put(NomiLineeColomb.SEZBIESSEP4, SEZBIESSEWNP4);
    
    
    
  }

  public List getListMaterialiCombimaR1(Date data ,String linea, String note) {
    Double lav1o,lav2o,lav1l,lav2l=Double.valueOf(0);
    List info=new ArrayList(); 
    LogFileCombima logComb =null;
    String fileName = "";
    String infoL="";
    try {
      infoL="UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
      fileName =InfoMapLineeUtil.getLogFileGgLinea(linea,data);
      //controlliamo che il file non sia vuoto altrimenti fermiamo il calcolo
      if (FileUtils.isEmpty(fileName)) {
         _logger.error(infoL+ ". File log vuoto !!");
         msg.addError(infoL+ ". File log vuoto !!");
         return info;
      }
      logComb = new LogFileCombima(fileName);
      logComb.initialize();
      Map materiali = logComb.getMapUtilMateriali(data);
      if(materiali==null || materiali.isEmpty()){
        _logger.warn(infoL+ ". Lista materiali vuota!!");
        msg.addWarning(infoL+ ". Lista materiali vuota!!");
        return info;
      }
        
      lav1o=getValoreMateriale(materiali, LogFileCombima.LAVSP1_N);
      lav2o=getValoreMateriale(materiali, LogFileCombima.LAVSP2_N);
      lav1l=getValoreMateriale(materiali, LogFileCombima.LAVSP1_L);
      lav2l=getValoreMateriale(materiali, LogFileCombima.LAVSP2_L);
    
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+LogFileCombima.LAVSP1_N), data, lav1o, null, null, null,METRI, note));
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+LogFileCombima.LAVSP2_N), data, lav2o, null, null, null,METRI, note));        
      if(linea.contains("P1")){//Lucido solo per Ima Ante e non Ima Top
        info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+LogFileCombima.LAVSP1_L), data, lav1l, null, null, null,METRI, note));
        info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+LogFileCombima.LAVSP2_L), data, lav2l, null, null, null,METRI, note));
      }
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" - Errore in fase di conversione dei dati : "+ex.toString());
    } catch (IOException ex) {
       _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log : "+ex.toString());
    } finally {
      try {
        if(logComb!=null)
          logComb.terminate();
      } catch (IOException ex) {
        _logger.error("Problemi nella chiusura del file " + fileName + " :" + ex.getMessage());
      }
    }
    
    return info;
  }
  
  
  public List getListMaterialiSchellingR1(Date data,String linea,Long larghPann, String note)  {
    String fileName ="";
    LogFileSchelling logSch =null;
    List info=new ArrayList();
    String infoL="";
    try {
      infoL="UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
      fileName =InfoMapLineeUtil.getLogFileGgLinea(linea,data);
      if (FileUtils.isEmpty(fileName)) {
       _logger.error(infoL+ ". File log vuoto !!");
       msg.addError(infoL+ ". File log vuoto !!");
       return info;
      }
      
      logSch = new LogFileSchelling(fileName);
      logSch.initialize();
      Double valM = logSch.getUtilMateriale(data,larghPann);
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea),  data, valM, null, null, null,METRI, note));
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" - Errore in fase di conversione dei dati : "+ex.toString());
    } catch (IOException ex) {
       _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log : "+ex.toString());
    } finally {
      try {
        if(logSch!=null)
          logSch.terminate();
      } catch (IOException ex) {
        _logger.error("Problemi nella chiusura del file " + fileName + " :" + ex.getMessage());
      }
    }
    return info;
  }
  
  
  public List getListMaterialiSchLongG1(Date data,String note){
    String linea=NomiLineeColomb.SCHELLLONG_GAL;
    String fileName ="";
    LogFileSchellingLong logSch =null;
    List info=new ArrayList();
    String infoL="";
    try {
      infoL="UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
      fileName =InfoMapLineeUtil.getLogFileGgLinea(linea,data);
      //controlliamo che il file non sia vuoto altrimenti fermiamo il calcolo
      if(FileUtils.isEmpty(fileName)) {
        _logger.error(infoL+ ". File log vuoto !!");
        msg.addError(infoL+ ". File log vuoto !!");
        return info;
      }

      logSch = new LogFileSchellingLong(fileName,null);
      Double valM = logSch.getUtilMateriale(data);
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea),  data, valM, null, null, null,METRI, note));
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+NomiLineeColomb.SCHELLLONG_GAL+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" - Errore in fase di conversione dei dati : "+ex.toString());
    } catch (IOException ex) {
       _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log : "+ex.toString());
    }
    
    return info;
  }
  
  
  public List getListMaterialiSqbG1(Date data,String linea,String note){
    LogFileSquadrabordaLong lfs=null;
    String fileName="";
    List info=new ArrayList();
    String infoL="";
    try {
      infoL="UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
      fileName =InfoMapLineeUtil.getLogFileGgLinea(linea,data);
      
      if(FileUtils.isEmpty(fileName)) {
        _logger.error(infoL+ ". File log vuoto !!");
        msg.addError(infoL+ ". File log vuoto !!");
        return info;
      }
      lfs=new LogFileSquadrabordaLong(fileName, linea);
      lfs.initialize();

      Map valori=lfs.loadDatiUtlsMateriali(data);
      if(valori==null || valori.isEmpty()){
        _logger.warn(infoL+ ". Lista materiali vuota!!");
        msg.addWarning(infoL+ ". Lista materiali vuota!!");
        return info;
      }  
      Double sez=(Double)valori.get(LogFileSquadrabordaLong.SEZIONATURA);
      Double bord=(Double)valori.get(LogFileSquadrabordaLong.BORDATURA);
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+SPALLA1), data, bord, null, null, null, METRI, note));  
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+SPALLA2), data, bord, null, null, null, METRI, note));  
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+LogFileSquadrabordaLong.SEZIONATURA), data, sez, null, null, null, METRI, note));  
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" - Errore in fase di conversione dei dati : "+ex.toString());
    } catch (FileNotFoundException ex) {
      _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - File di log non trovato : "+ex.toString());
    } catch (IOException ex) {
      _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log : "+ex.toString());
    } finally {
      try{
        lfs.terminate();
      } catch(IOException e){
        _logger.error("Errore nella chiusura del file"+fileName);
      }  
    }  
    
   return info;
  }
  
  public List getListMaterialiImaG1(Connection con,Date data,String linea,String lineaP,String note){
    List info=new ArrayList();
    List <List>result=new ArrayList();
    Double met=Double.valueOf(0);
    QueryMaterialeArticoliG1P2Ima qry=new QueryMaterialeArticoliG1P2Ima();
    Long dtL=DateUtils.getDataForMovex(data);
    
    String codLinea=InfoMapLineeUtil.getCodiceLinea(linea);
    String infoL=" UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dtL);
    qry.setFilter(QueryMaterialeArticoliG1P2Ima.FTCDL, codLinea);
    qry.setFilter(QueryMaterialeArticoliG1P2Ima.FTCDLPADRE, lineaP);
    try {
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
      for(List record:result){
        Double dim1=ClassMapper.classToClass(((String)record.get(1)).trim(), Double.class);
        Double dim2=ClassMapper.classToClass(((String)record.get(2)).trim(), Double.class);
        Double dim3=ClassMapper.classToClass(((String)record.get(3)).trim(), Double.class);
        Double qta=ClassMapper.classToClass(record.get(4), Double.class);
        Double min1=Math.min(dim1, dim2);
        Double min2=Math.min(dim2, dim3);
        Double min3=Math.min(dim1, dim3);
        Double max1=Math.max(Math.max(min1,min2),Math.max(min2,min3));
        met+=(max1*qta);
      }
      met=met/1000;
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+SPALLA1), data, met, null, null, null, METRI, note));  
      info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+SPALLA2), data, met, null, null, null, METRI, note));  
      //aggiunta nuova linea:Montaggio FBA per 13267 e 13269
      if(!linea.equals(NomiLineeColomb.IMAGAL_13200)){
        info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea+MFBA), data, met, null, null, null, METRI, note));    
      }
      
    } catch (QueryException ex) {
      _logger.error("Problemi di esecuzione della query per la:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" -  problemi di accesso ai dati su db : "+ex.toString());
    } catch (SQLException ex) {
      _logger.error("Problemi di accesso al database per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" -  problemi di accesso al db : "+ex.toString());
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" -  problemi di conversione dei dati : "+ex.toString());
    }
    
    
    return info;
  }
  
  public List getListMaterialiSezP4(Date data,String note) {
    String linea=NomiLineeColomb.SEZBIESSEP4;
    String fileNameTmp ="";
    List info=new ArrayList();
    String infoL="";
    String pathFile=InfoMapLineeUtil.getPathLogSourceFile(linea);
    Double val=Double.valueOf(0);
    
     try {
       List<File> files=FileUtils.getListFileFolderForDate(pathFile, data, DateUtils.getFineGg(data));
       infoL="UTILIZZO MATERIALI x linea "+linea+" e gg "+DateUtils.getDataForMovex(data);
      
       for(File f:files){
         fileNameTmp=f.getAbsolutePath();
        
         if(FileUtils.isEmpty(fileNameTmp)) {
           _logger.error(infoL+ ". File log vuoto !!");
           msg.addError(infoL+ ". File log vuoto !!");
          continue;
         }
        
        val+=getValMLSezP4(fileNameTmp);

      }
      if(val>0)
        info.add(UtilizzoMatUtils.getInstance().getMappaValoriInsert(CostantsColomb.AZCOLOM, strGer.get(linea),  data, val, null, null, null,METRI, note));
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione data per per la linea:"+NomiLineeColomb.SCHELLLONG_GAL+" e giorno:"+data+" --> "+ ex.getMessage());
      msg.addError(infoL+" - Errore in fase di conversione dei dati : "+ex.toString());
    } catch (IOException ex) {
       _logger.error("Problemi di accesso al file log per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log : "+ex.toString());
    
    } catch (ParserConfigurationException ex) {
      _logger.error("Problemi di accesso al file log "+fileNameTmp+" per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log "+fileNameTmp+" :  "+ex.toString());
       
    } catch (SAXException ex) {
      _logger.error("Problemi di accesso al file log "+fileNameTmp+" per la linea:"+linea+" e giorno:"+data+" --> "+ ex.getMessage());
       msg.addError(infoL+" - Errore in fase di accesso al file di log "+fileNameTmp+" : "+ex.toString());
       
    }
    
    return info;
  }
  
  
  private Double getValMLSezP4(String fileName) throws ParserConfigurationException, SAXException, IOException{
    Double val=Double.valueOf(0);
     
    _logger.info("Process file "+fileName); 

    XmlFileSezR1P4 fileXml=new XmlFileSezR1P4(fileName);
    fileXml.loadPropertiesMap();
    List<Map> listSolution=fileXml.getListSolution();
    

    for(Map pattern:listSolution){
       val+=(ClassMapper.classToClass(pattern.get(XmlFileSezR1P4.XML_SOLUTION_CutLen),Double.class)/10000);
    }
      
     
    return val;
  }   
     
     
     
  private  Double getValoreMateriale(Map <String,Double> materiali,String lavorazione){
    Double valore=Double.valueOf(0);
    if(materiali==null)
      return valore;
    
    if(materiali.containsKey(lavorazione)){
      valore=ClassMapper.classToClass(materiali.get(lavorazione),Double.class);
      valore=valore/new Double(1000);
    }
    
    return valore;
  }
  
  
  public void elabDatiLinea(Connection con, List<Date> giorni, String linea){
    for(Date data:giorni){
      List info=getListInfoMateriali(con, data, linea);
      if(info!=null && !info.isEmpty()){
        try {
          UtilizzoMatUtils.getInstance().deleteDatiUtlMaterialiGg(con, data,getListLineeGer(linea));
          UtilizzoMatUtils.getInstance().insertDatiUtlMaterialiGg(con, info);
        } catch (SQLException ex) {
          _logger.error(" Errore in fase di accesso al db per cancellazione-inserimento dati --> "+ex.getMessage());
          msg.addError("\" Errore in fase di  cancellazione/inserimento dati --> "+ex.toString());
        } catch (ParseException ex) {
          _logger.error(" Errore in fase di conversione dei dati --> "+ex.getMessage());
          msg.addError("\" Errore in fase di conversione dei dati --> "+ex.toString());
        }
      }
    }
  }
  
  
  public List getListInfoMateriali(Connection con,Date data,String linea){
    if(NomiLineeColomb.COMBIMA1R1P1.equals(linea) || NomiLineeColomb.COMBIMA2R1P1.equals(linea) || NomiLineeColomb.COMBIMAR1P0.equals(linea))
      return getListMaterialiCombimaR1(data, linea, "");
    else if (NomiLineeColomb.SCHELLING1R1P1.equals(linea) || NomiLineeColomb.SCHELLING2R1P1.equals(linea) )
      return getListMaterialiSchellingR1(data, linea, CMTAGLIOIMAANTE, "");
    else if( NomiLineeColomb.SCHELLING1R1P0.equals(linea) || NomiLineeColomb.SCHELLING2R1P0.equals(linea)  )
      return getListMaterialiSchellingR1(data, linea, CMTAGLIOIMATOP, "");
    else if (NomiLineeColomb.SQBL13266.equals(linea) || NomiLineeColomb.SQBL13268.equals(linea) )
      return getListMaterialiSqbG1(data, linea,"");
    else if (NomiLineeColomb.SCHELLLONG_GAL.equals(linea))
      return getListMaterialiSchLongG1(data, "");
    else if(NomiLineeColomb.IMAGAL_13200.equals(linea) || NomiLineeColomb.IMAGAL_13267.equals(linea) || NomiLineeColomb.IMAGAL_13269.equals(linea))
      return getListMaterialiImaG1(con, data, linea, "01001", "");
    else if(NomiLineeColomb.SEZBIESSEP4.equals(linea))
      return getListMaterialiSezP4(data, "");
    
    return new ArrayList();
  }
  
  
  private List getListLineeGer(String nomeLinea){
    List list=new ArrayList();
    Set keys=strGer.keySet();
    Iterator iter =keys.iterator();
    while(iter.hasNext() ){
      String keyMap=(String) iter.next();
      if(keyMap.contains(nomeLinea))
        list.add(strGer.get(keyMap));
    }
    
    return list;
  }
  
  
  private static final Logger _logger = Logger.getLogger(UtilizzoMaterialiCalc.class);
}
  

