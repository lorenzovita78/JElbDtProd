/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.Allegati;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author ggraziani
 */
public class ElabGestAllegati extends ElabClass{
  private Date dataInizio;
  
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
  return true;
  }
  String libraryMvx = "MVXBDTADEM.";
  String libraryMvxPersonalizzata = "MCOBMODDEM.";
 
 
  
  @Override
  public void exec(Connection con) {
    Map propsElab=getElabProperties();
    PersistenceManager pm =null;
    //mappa contenente per ogni tipo file, la destinazione
    Map<String,String> tipoFilesDest=new HashMap <String,String> ();
    List tipoFile =new ArrayList();
    Connection conDbAS400=null;
    
    try {
       conDbAS400=ColombiniConnections.getAs400ColomConnection();
       
      //Paso 1: Prendo tutti allegati con dataPresaCarico Null --> Update dataPresaCarico a data attuale
      //Data attuale
      Date dataPresaCarico=new Date();
      String date =DateUtils.dateToStr(dataPresaCarico, "yyyy-MM-dd HH:mm:ss");
      String updateIni = getUpdateDataOraInizioPresaCarico(dataPresaCarico);
      PreparedStatement ps=conDbAS400.prepareStatement(updateIni);
      ps.execute();
      
      List<List> files=new ArrayList();
      List<Allegati> filesAllegati = new ArrayList();
      ResultSetHelper.fillListList(conDbAS400,getQueryListDestTipoFile() , tipoFile);
      //Mapa tipo file - Destinazione
      tipoFilesDest=getPathDestMap(tipoFile);
      ResultSetHelper.fillListList(conDbAS400,getQueryListFileAllegati(date) , files);
      
      //Converto List<list> to List<Allegati>
      filesAllegati=elabFiles(files,dataPresaCarico,tipoFilesDest);
      
       PreparedStatement psUpdFineCarico = null;
       psUpdFineCarico = conDbAS400.prepareStatement(getUpdateFinePresaCarico(dataPresaCarico));
       
       PreparedStatement psUpdErroreCarico = null;
       psUpdErroreCarico = conDbAS400.prepareStatement(getUpdateErroreCopy(dataPresaCarico));
  
      for(Allegati rec:filesAllegati){
        try {
          File sorgente = new File(rec.getPath());
          FileUtils.copyFile(rec.getPath(),rec.getPathDest());
          Date dateNow = new Date();
          long timeInMilliSeconds = dateNow.getTime();
          java.sql.Date dateFineCar = new java.sql.Date(timeInMilliSeconds);
          rec.setDataFineCarico(dateFineCar);
          
          if(sorgente.exists()){
          rec.updateAllegati(psUpdFineCarico,conDbAS400,rec.getCono(),rec.getTipoDoc(),rec.getOrdine(),dateFineCar,rec.getPathDest());
            }
          else {
          rec.updateErrorAllegati(psUpdErroreCarico, conDbAS400, rec.getCono(), rec.getTipoDoc(), rec.getOrdine(), "Errore copy file");
          }
          //pm.updateDt(rec, rec.getFieldValuesForUpdate()); UPDATE BEAN non funziona per formato dataPresaCaric
          if(ps!=null)
                  ps.close();
             }
        catch (IOException ex) 
            {
                addError("Errore in fase di copia file -->"+ex.getMessage());
                try{rec.updateErrorAllegati(psUpdErroreCarico, conDbAS400, rec.getCono(), rec.getTipoDoc(), rec.getOrdine(), "Errore copy file");}
                catch (Exception err) {addError("Errore generica -->"+err.getMessage());}

             }
         catch (SQLException ex) 
            {
                addError("Errore in fase di esecuzione della query -->"+ex.getMessage());
            }
         catch (Exception ex) 
            {
                addError("Errore generica -->"+ex.getMessage());
            }
      }

      
    } catch (SQLException ex) {
      addError("Errore in fase di esecuzione della query -->"+ex.getMessage());
    } finally{
      if(conDbAS400!=null)
        try{
        conDbAS400.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione con Desmos"
                  + s.getMessage());
        }
    }
    
  }
  
  
// private String getUpdateDataOraInizioPresaCarico (String data){
// String query ="update " + libraryMvxPersonalizzata + "ZZBSTO set Z2DDIP='"+ data + "' where Z2DDIP is null";
// return query;
// 
// }
        
 
  private String getUpdateDataOraInizioPresaCarico (Date data){
  String query ="update " + libraryMvxPersonalizzata + "ZZBSTO set Z2DDIP="+JDBCDataMapper.objectToSQL(data)+ " where Z2DDIP is null or Z2DDIP<'2000-01-01 00:00:00'";
  return query;

 }
  
  private String getUpdateFinePresaCarico (Date data){
   
   String query=" UPDATE "+libraryMvxPersonalizzata+ "ZZBSTO "
                                       +" SET Z2PTHD=?"
                                       +" ,Z2DDFP=?"  
                                       +" WHERE Z2CONO=?"            
                                       +" and Z2ORNO=?"       
                                       +" and Z2TDOC=?"
                                       +" and Z2DDIP ="+JDBCDataMapper.objectToSQL(data);
   
   return query;
   }
  
  private String getUpdateErroreCopy (Date data){
   
   String query=" UPDATE "+libraryMvxPersonalizzata+ "ZZBSTO "
                                       +" SET Z2NOTE=?"
                                       +" WHERE Z2CONO=?"            
                                       +" and Z2ORNO=?"       
                                       +" and Z2TDOC=?"
                                       +" and Z2DDIP ="+JDBCDataMapper.objectToSQL(data);
   return query;
   }
        
        
  private List<Allegati> elabFiles(List<List> filesPresent,Date dataPC,Map<String,String> tipoFilesDest){
    List listFilesToCopy=new ArrayList() ;
    
    for(List file:filesPresent){
        try {
           Allegati fileAllegato =new Allegati();
           fileAllegato.setCono(ClassMapper.classToString(file.get(0)).trim());
           fileAllegato.setOrdine(ClassMapper.classToString(file.get(1)).trim());
           fileAllegato.setTipoDoc(ClassMapper.classToString(file.get(2)).trim());
           fileAllegato.setPath(ClassMapper.classToString(file.get(3)).trim());
           fileAllegato.setTitolo(ClassMapper.classToString(file.get(4)).trim());
           fileAllegato.setDataUltAggior(ClassMapper.classToClass(file.get(5),Date.class));
           fileAllegato.setDataPresaCarico(dataPC);
           fileAllegato.setPathDest(tipoFilesDest.get(ClassMapper.classToString(file.get(2)).trim())+"\\"+fileAllegato.getTitolo());
           
           System.out.println("DataUltimoAggiornamento-->"+DateUtils.dateToStr(fileAllegato.getDataUltAggior(), "yyyyMMddHHmmssSSS"));

           listFilesToCopy.add(fileAllegato);
           
         } catch (Exception ex) {
          addError("Errore in fase di lettura del file "+ ClassMapper.classToString(file.get(4)).trim() +" allegato --> " + ex.getMessage());
         }
    }
    
    return listFilesToCopy;
  }
  
  private String getQueryListFileAllegati(String data ){
    
    String sql="SELECT Z2CONO,Z2ORNO,Z2TDOC,Z2PTHS,Z2TITL,Z2DMAG FROM "+ libraryMvxPersonalizzata +"ZZBSTO WHERE Z2DDIP='"+data+"'";
             
    return sql;         
  }
  
    private String getQueryListFileAllegati(Date data ){
    
    String sql="SELECT Z2CONO,Z2ORNO,Z2TDOC,Z2PTHS,Z2TITL,Z2DMAG FROM "+ libraryMvxPersonalizzata +"ZZBSTO WHERE Z2DDIP="+JDBCDataMapper.objectToSQL(data);
             
    return sql;         
  }
  
  private String getQueryListDestTipoFile(){
    
    String sql="SELECT TRIM(CTSTKY),TRIM(CTPARM) FROM " +libraryMvx + "CSYTAB WHERE CTCONO =030 AND CTSTCO='CO_STORAGE'";
             
    return sql;         
  }
  
  /**
   * Torna una mappa in cui la chiave è il tipo file e il cui elemento è la destinazione per quel tipo file
   */

  private Map <String,String> getPathDestMap(List<List> TipoFileDest){
    Map destMap=new HashMap <String,String> ();
    for(List infoF:TipoFileDest){
      String tipoFile=ClassMapper.classToString(infoF.get(0));
      String PathDest=ClassMapper.classToString(infoF.get(1));
      destMap.put(tipoFile,PathDest);
    }
    
    return destMap;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestAllegati.class); 
  
}
