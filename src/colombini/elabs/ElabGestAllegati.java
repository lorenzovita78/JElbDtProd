/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.LogCopiaLibretti;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import db.ResultSetHelper;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class ElabGestLibretti extends ElabClass{

  private Date dataInizio;
  private Date dataFine;
  private Integer commessa=null;
  
  private Boolean copyFile=Boolean.TRUE;
  
  private String PARAM_COPY_FILE="COPYFILES";
  private String PRM_COMMESSA="COMMESSA";
  
  @Override
  public Boolean configParams() {
     Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(PARAM_COPY_FILE)!=null){
      this.copyFile=ClassMapper.classToClass(parameter.get(PARAM_COPY_FILE),Boolean.class);
    }  
    
    if(parameter.get(PRM_COMMESSA)!=null){
      this.commessa=ClassMapper.classToClass(parameter.get(PRM_COMMESSA),Integer.class);
    } 
    
    
    if(dataInizio==null)
      return Boolean.FALSE;
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Map propsElab=getElabProperties();
    PersistenceManager pm =null;
    //mappa contenente per ogni commessa una mappa di tutti i file (dei libretti) ad essa associati
    Map<String,Map> commMap=new HashMap <String,Map> ();
    List<LogCopiaLibretti> filesToProcess=null;
    Connection conDbDesmos=null;
    String pathSource=(String) propsElab.get(NameElabs.PATHFILESOURCELIBRETTI);
    String pathDest=(String) propsElab.get(NameElabs.PATHFILEDESTLIBRETTI);
    String infoagg="";
    
    try {
      List<List> commGg=DatiProdUtils.getInstance().getListCommesseGgElab(con, dataInizio,commessa, Boolean.TRUE,Boolean.TRUE);
      
      List<List> files=new ArrayList();
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      ResultSetHelper.fillListList(conDbDesmos,getQueryListFileLibretti(dataInizio) , files);
      commMap=getCommesseMap(files);
      
      for(List lcomm:commGg){
        filesToProcess=new ArrayList();
        
        String commessa=DatiProdUtils.getInstance().getStringNComm(ClassMapper.classToClass(lcomm.get(0),Long.class));
        String dataCommS=ClassMapper.classToString(lcomm.get(1));
        Date dataComm=DateUtils.strToDate(dataCommS, "yyyyMMdd");
        Date dataElbComm=DateUtils.strToDate(ClassMapper.classToString(lcomm.get(3)), "yyyyMMdd");
        
        if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conDbDesmos, Long.valueOf(commessa), dataComm) && 
                DesmosUtils.getInstance().isElabLibrettiDesmosColomFinished(conDbDesmos, Long.valueOf(commessa), dataComm) ){
          Map commFiles= commMap.get(DateUtils.dateToStr(dataComm, "yyyy-MM-dd")+"_"+commessa);
          if(commFiles==null)
            commFiles=new HashMap();
          
          _logger.info("Processing commessa N."+commessa);
          filesToProcess.addAll(elabFiles(pathSource+"/Colombini", pathDest+"/Colombini", commessa, dataComm, dataElbComm,commFiles ));
          filesToProcess.addAll(elabFiles(pathSource+"/Artec", pathDest+"/Artec", commessa, dataComm,dataElbComm, commFiles ));
          filesToProcess.addAll(elabFiles(pathSource+"/Febal", pathDest+"/Febal", commessa, dataComm,dataElbComm, commFiles ));
        } else{
          addWarning("!!! Attenzione elaborazione DESMOS per commessa "+commessa+" non ancora terminata !!!");
        }
        
        if(filesToProcess.size()>0){
          int fileLoad=0;
          int numPgTot=0;
          pm =new PersistenceManager(conDbDesmos);
          for(LogCopiaLibretti lfl:filesToProcess){
            try {
              if(copyFile)
                FileUtils.copyFile(lfl.getCompleteFileNameSource(), lfl.getCompleteFileNameDest());
              
              Integer numPFile=lfl.getNumeroPagine();
              numPgTot+=numPFile;
              if(numPFile>=140)
                infoagg+="\n Libretto "+lfl.getFileName()+" con numero pagine = "+numPFile;
              
              pm.storeDtFromBean(lfl);
              fileLoad++;
            } catch (IOException ex) {
              addError("Impossibile compiare il file "+lfl.getFileName()+"-->"+ex.getMessage());
            } catch(SQLException s){
              addError("Impossibile scrivere il log del file "+lfl.getFileName()+"-->"+s.getMessage());
            }
          }
          addInfo("\n Commessa "+commessa+" ; N. file da processare : "+filesToProcess.size()+"  ; "
                  + " N. file caricati "+fileLoad+" . Totale Pagine : "+numPgTot+" \n\n "+infoagg);
        }
      }
      
    } catch (QueryException ex) {
      addError("Errore in fase di esecuzione della query -->"+ex.getMessage());
      
    } catch (SQLException ex) {
      addError("Errore in fase di esecuzione della query -->"+ex.getMessage());
    } finally{
      if(conDbDesmos!=null)
        try{
        conDbDesmos.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione con Desmos"
                  + s.getMessage());
        }
    }
    
  }
  
  private List<LogCopiaLibretti> elabFiles(String pathSource,String pathDest,String commessa ,Date dataCommessa,Date dataElbComm,Map filesCommPresent){
    List listFilesToCopy=new ArrayList() ;
    File dirSource=new File(pathSource);
    if(!dirSource.isDirectory())
      return null;
    
    List<String> filesSource=ArrayUtils.getListFromArray(dirSource.list());
    for(String fileName:filesSource){
       if(!filesCommPresent.containsKey(fileName) && fileName.startsWith(commessa)){
         try {
           LogCopiaLibretti logFile =new LogCopiaLibretti(commessa, dataCommessa, fileName);
           logFile.setPathSource(pathSource);
           logFile.setPathDest(pathDest);
           logFile.setDataGenerazione(dataElbComm);
           
           File f=new File(pathSource+"/"+fileName);
           PDDocument doc=PDDocument.load(f);
           Integer pages=doc.getNumberOfPages();
           doc.close();
           logFile.setNumeroPagine(pages);
           _logger.info("File :"+fileName+"  --> n.pagine :"+pages);
           listFilesToCopy.add(logFile);
           
         } catch (IOException ex) {
          addError("Errore in fase di lettura del file "+fileName+" --> "+ex.getMessage());
         }
       }
    }
    
    return listFilesToCopy;
  }
  
  
  private String getQueryListFileLibretti(Date data ){
    
    String sql=" select NomeFile,Commessa,DataCommessa from [GestoreStampe2].[dbo].[LogCopiaLibretti] "
             + " where DataGenerazione>=convert(date,'"+DateUtils.dateToStr(data, "yyy-MM-dd")+"' )";
             
    return sql;         
  }
  
  /**
   * Torna una mappa in cui la chiave è la commessa e il cui elemento è una mappa contenente tutti i file
   * di quella commessa
   * @param filesProc
   * @return 
   */
  private Map <String,Map> getCommesseMap(List<List> filesProc){
    Map commMap=new HashMap <String,Map> ();
    for(List infoF:filesProc){
      String nomeFTmp=ClassMapper.classToString(infoF.get(0));
      String commTmp=ClassMapper.classToString(infoF.get(1));
      String dataCTmp=ClassMapper.classToString(infoF.get(2));
      String keyTmp=dataCTmp+"_"+commTmp;
      Map fileMap=null;
      
      if(commMap.containsKey(keyTmp)){
        fileMap=(Map) commMap.get(keyTmp);
      }else{
        fileMap=new HashMap <String,String>();
      }
      
      fileMap.put(nomeFTmp, nomeFTmp);
      commMap.put(keyTmp,fileMap);
    }
    
    return commMap;
  }
  
  private class FileInfo{
    
    private String pathSource;
    private String pathDest;
    private String fileName;
    private Integer numPg;
    
    public FileInfo(String fileN){
      this.fileName=fileN;
    }

    public String getPathSource() {
      return pathSource;
    }

    public void setPathSource(String pathSource) {
      this.pathSource = pathSource;
    }

    public String getPathDest() {
      return pathDest;
    }

    public void setPathDest(String pathDest) {
      this.pathDest = pathDest;
    }

    public Integer getNumPg() {
      return numPg;
    }

    public void setNumPg(Integer numPg) {
      this.numPg = numPg;
    }
    
    
    
  }
          
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestLibretti.class); 
  
}
