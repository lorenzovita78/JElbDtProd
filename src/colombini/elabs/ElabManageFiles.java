/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;


import colombini.util.InfoMapLineeUtil;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import elabObj.MessagesElab;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.ManageFileUtils;
import utils.StringUtils;

/**
 * Elaborazione per la cancellazione o spostamento di file presenti in determinate cartelle.
 * Prevede come parametri obbligatori il percorso da dove prendere i file PATHSOURCE
 * e la tipologia di schedulazione (se far riferimento ad un determinato giorno o al giorno dell'anno.
 * @author lvita
 */
public class ElabManageFiles extends ElabClass{

  //identifica la cartella sorgente
  public final static String PATHSOURCE="PATHSRC";
  //identifica la cartella destinazione
  public final static String PATHDEST="PATHDST";
 
  //gestione singolo file
  public final static String FILESOURCE="FILESRC";
  //identifica la cartella destinazione
  public final static String FILEDEST="FILEDST";
  //--------------------------------------------------
  //identifica la tipologia di data da controllare (giorno dell'anno,data specifica ,giorni di differenza
  public final static String TYPEDT="TYPEDT";
  
  //identifica la posizione iniziale nel nome del file, del giorno dell'anno
  //parametro non obbligatorio
  public final static String POSINIDOY="POSINIDOY";
 
  //serve per specificare
  //1) nel caso di  giorno dell'anno la posizione iniziale nella stringa del nome del file del giorno dell'anno 
  //2) nel caso di giorni di differenza il numero dei giorni
  public final static String VALUEG="VALUEG";
  

  private String pathSourceFiles;
  private String pathDestFiles;
  
  private String fileSource;
  private String fileDest;
  
  private String typeOper;
  private String typeGg;
  private Date dataRif;
  private Integer valueGg;
  private Integer posIniDOY=null;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    
    if(parameter.get(PATHSOURCE)!=null){
      this.pathSourceFiles=ClassMapper.classToString(parameter.get(PATHSOURCE));
    }  
    
    if(StringUtils.IsEmpty(pathSourceFiles))
      return Boolean.FALSE;
    
    if(parameter.get(PATHDEST)!=null){
      this.pathDestFiles=ClassMapper.classToString(parameter.get(PATHDEST));
    }
    
     if(parameter.get(FILESOURCE)!=null){
      this.fileSource=ClassMapper.classToString(parameter.get(FILESOURCE));
    }  
    
    
    if(parameter.get(FILEDEST)!=null){
      this.fileDest=ClassMapper.classToString(parameter.get(FILEDEST));
    }
    
    if(fileSource!=null && !fileSource.isEmpty() && (fileDest==null || fileDest.isEmpty()) )
        fileDest=fileSource;
    
    if(parameter.get(ALuncherElabs.TYPEOPR)!=null){
      this.typeOper=ClassMapper.classToString(parameter.get(ALuncherElabs.TYPEOPR));
    }
    
    if(StringUtils.IsEmpty(ALuncherElabs.TYPEOPR))
      return Boolean.FALSE;
    
    
    
    if(parameter.get(TYPEDT)!=null){
      typeGg=ClassMapper.classToString(parameter.get(TYPEDT));
    }
    
    if(typeGg==null || typeGg.isEmpty())
      return Boolean.FALSE;
    
    
    if(parameter.get(VALUEG)!=null){
      valueGg=ClassMapper.classToClass(parameter.get(VALUEG), Integer.class);
    }
      
    if(valueGg==null)
      valueGg=Integer.valueOf(0);
  
    
    if(parameter.get(POSINIDOY)!=null){
      posIniDOY=ClassMapper.classToClass(parameter.get(POSINIDOY), Integer.class);
    }
    
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataRif=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }
    
    if(dataRif==null)
      dataRif=new Date();
    
    //imposto la descrizione per l'invio delle mail
    this.getInfoElab().setDescription("Elaborazione manipolazione files");
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    if(ManageFileUtils.COPY.equals(typeOper)){
      MessagesElab m=null;
      if(!StringUtils.isEmpty(fileSource)){
        m=ManageFileUtils.getInstance().copyFile(pathSourceFiles+"/"+fileSource, InfoMapLineeUtil.getStringReplaceWithDate(pathDestFiles+"/"+fileDest,dataRif), dataRif);
      }else{
        
        m=ManageFileUtils.getInstance().copyFiles(pathSourceFiles, pathDestFiles, typeGg, valueGg, posIniDOY, dataRif);
      
      }
      this.addMessagesToElab(m);
    }else if(ManageFileUtils.MOVE.equals(typeOper)){
      MessagesElab m=ManageFileUtils.getInstance().moveFiles(pathSourceFiles, pathDestFiles, typeGg, valueGg, posIniDOY, dataRif);
      this.addMessagesToElab(m);
    }else if(ManageFileUtils.DELETE.equals(typeOper)){
      MessagesElab m=ManageFileUtils.getInstance().deleteFiles(pathSourceFiles, typeGg, valueGg, posIniDOY, dataRif);
      this.addMessagesToElab(m);
    }
  }
  
  
  

  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabManageFiles.class);
}
