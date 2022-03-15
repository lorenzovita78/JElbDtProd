/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.Allegati;
import db.ResultSetHelper;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
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
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
      //Data attuale
      String date = formatter.format(new Date());  
      String updateIni = getUpdateDataOraInizioPresaCarico(date);
      PreparedStatement ps=conDbAS400.prepareStatement(updateIni);
      ps.execute();
      
      List<List> files=new ArrayList();
      List<Allegati> filesAllegati = new ArrayList();
      ResultSetHelper.fillListList(conDbAS400,getQueryListDestTipoFile() , tipoFile);
      //Mapa tipo file - Destinazione
      tipoFilesDest=getPathDestMap(tipoFile);
      ResultSetHelper.fillListList(conDbAS400,getQueryListFileAllegati(date) , files);
      
      //Converto List<list> to List<Allegati>
      filesAllegati=elabFiles(files,date,tipoFilesDest);
      
      for(Allegati rec:filesAllegati){
        try {
          FileUtils.copyFile(rec.getPath(),rec.getPathDest());
          pm =new PersistenceManager(conDbAS400);
          rec.setDataFineCarico(new Date());
          pm.updateDt(rec, rec.getFieldValuesForUpdate());
             }
        catch (IOException ex) 
            //  catch (Exception ex) 
            {
             addError("Errore in fase di copia file -->"+ex.getMessage());
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
  
  
 private String getUpdateDataOraInizioPresaCarico (String data){
 String query ="update " + libraryMvxPersonalizzata + "ZZBSTO set Z2DDIP='"+ data + "' where Z2DDIP is null";
 return query;
 
 }
          
          
  private List<Allegati> elabFiles(List<List> filesPresent,String data,Map<String,String> tipoFilesDest){
    List listFilesToCopy=new ArrayList() ;
    //SELECT Z2CONO,Z2ORNO,Z2TDOC,Z2PTHS,Z2TITL,Z2DMAG FROM mcobmoddem.ZZBSTO
    
    for(List file:filesPresent){
        try {
           Allegati fileAllegato =new Allegati();
           fileAllegato.setCono(ClassMapper.classToString(file.get(0)).trim());
           fileAllegato.setOrdine(ClassMapper.classToString(file.get(1)).trim());
           fileAllegato.setTipoDoc(ClassMapper.classToString(file.get(2)).trim());
           fileAllegato.setPath(ClassMapper.classToString(file.get(3)).trim());
           fileAllegato.setTitolo(ClassMapper.classToString(file.get(4)).trim());
           fileAllegato.setDataUltAggior(ClassMapper.classToClass(file.get(5),Date.class));
           fileAllegato.setDataPresaCarico(data);
           fileAllegato.setPathDest(tipoFilesDest.get(file.get(2))+"\\"+fileAllegato.getTitolo());
           
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
  
  private String getQueryListDestTipoFile(){
    
    String sql="SELECT TRIM(CTSTKY),TRIM(CTPARM) FROM " +libraryMvx + "CSYTAB WHERE CTCONO =030 AND CTSTCO='CO_STORAGE'";
             
    return sql;         
  }
  
  /**
   * Torna una mappa in cui la chiave è il tipo file e il cui elemento è la destinazione per quel tipo file
   */

  private Map <String,Map> getPathDestMap(List<List> TipoFileDest){
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
