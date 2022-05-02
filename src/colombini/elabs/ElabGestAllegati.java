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
import org.apache.commons.lang.StringUtils;
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
  String libraryMvx = "MVXBDTA.";
  String libraryMvxPersonalizzata = "MCOBMODDTA.";
 
 
  
  @Override
  public void exec(Connection con) {
    PersistenceManager pm =null;
    //mappa contenente per ogni tipo file, la destinazione
    Map<String,String> tipoFilesDest=new HashMap <String,String> ();
    Map<String,String> tipoFilesEst=new HashMap <String,String> ();
    List tipoFile =new ArrayList();
    List est =new ArrayList();

    Connection conDbAS400=null;
    
    try {
       conDbAS400=ColombiniConnections.getAs400ColomConnection();
       List<List> files=new ArrayList();
       List<Allegati> filesAllegati = new ArrayList();
      //Paso 1: Prendo tutti allegati con dataPresaCarico Null --> Update dataPresaCarico a data attuale
      //Data attuale
      Date dataPresaCarico=new Date();
      String date =DateUtils.dateToStr(dataPresaCarico, "yyyy-MM-dd HH:mm:ss");
      String updateIni = getUpdateDataOraInizioPresaCarico(dataPresaCarico);
      PreparedStatement ps=conDbAS400.prepareStatement(updateIni);
      ps.execute();
      //Prendo tutti allegati da copiare 
      ResultSetHelper.fillListList(conDbAS400,getQueryListFileAllegati(date) , files);
      
      //Mapa tipo file - Destinazione
      ResultSetHelper.fillListList(conDbAS400,getQueryListDestTipoFile() , tipoFile);
      tipoFilesDest=getFileMap(tipoFile);
      
      //Mapa tipo file - estensione file
      ResultSetHelper.fillListList(conDbAS400,getQueryListEstensioneTipoFile() , est);
      tipoFilesEst=getFileMap(est);
    
      //Convert List<list> to List<Allegati>
      filesAllegati=elabFiles(files,dataPresaCarico,tipoFilesDest,tipoFilesEst);
      
       PreparedStatement psUpdFineCarico = null;
       psUpdFineCarico = conDbAS400.prepareStatement(getUpdateFinePresaCarico());
       
       PreparedStatement psUpdErroreCarico = null;
       psUpdErroreCarico = conDbAS400.prepareStatement(getUpdateErroreCopy());
  
      //Per ogni allegato copio il file + faccio update sulla zzbsto con datafinecarico + pathdestinazione + statoten
      for(Allegati rec:filesAllegati){
        try {
          File sorgente = new File(rec.getPath());
          String pathDest=null;

          //Concateno al nome file --> _idallegato, in modo di creare sempre un file diverso per tutti allegati
          pathDest=StringUtils.substringBefore(rec.getPathDest(),rec.getEstensione()).concat("_").concat(String.valueOf(rec.getIdSequence())).concat(rec.getEstensione());
          
          //Se trovo un allegato stato T --> non faccio la copia, e aggiorno il record pero con tengo lo stato T invece di U
          if(!rec.getDaCancellare().equals("TRUE")){
          FileUtils.copyFile(rec.getPath(),pathDest);
          }
          Date dateNow = new Date();
          long timeInMilliSeconds = dateNow.getTime();
          java.sql.Date dateFineCar = new java.sql.Date(timeInMilliSeconds);
          rec.setDataFineCarico(dateFineCar);
          
          //Verifico se esiste il file sorgente
          if(sorgente.exists()){
              if(!rec.getDaCancellare().equals("TRUE")){
                 rec.updateAllegati(psUpdFineCarico,conDbAS400,rec.getIdSequence(),dateFineCar,pathDest,"U");
              }
              else {
                 rec.updateAllegati(psUpdFineCarico,conDbAS400,rec.getIdSequence(),dateFineCar,pathDest,"T"); 
              }
            }
          else {
          rec.updateErrorAllegati(psUpdErroreCarico, conDbAS400, rec.getIdSequence(), "Errore copy file");
          }
          if(ps!=null)
                  ps.close();
             }
        catch (IOException ex) 
            {
                addError("Errore in fase di copia file -->"+ex.getMessage());
                try{
                    rec.updateErrorAllegati(psUpdErroreCarico, conDbAS400, rec.getIdSequence(), "Errore copy file");
                }
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
       
 
  private String getUpdateDataOraInizioPresaCarico (Date data){
  String query ="update " + libraryMvxPersonalizzata + "ZZBSTO set Z2DDIP="+JDBCDataMapper.objectToSQL(data)+ " where Z2DDIP is null or Z2DDIP<'2000-01-01 00:00:00' and Z2CONO=30  and Z2ORNO IN (SELECT Z6ORNO FROM "+ libraryMvxPersonalizzata +"zordin WHERE Z6MODI='' and Z6CONO=30) ";
  return query;

 }
  
  private String getUpdateFinePresaCarico (){
   
   String query=" UPDATE "+libraryMvxPersonalizzata+ "ZZBSTO "
                                       +" SET Z2PTHD=?"
                                       +" ,Z2DDFP=?"
                                       +" ,Z2ELAB=?"
                                       +" WHERE Z2IDUN=?";            
   
   return query;
   }
  
  private String getUpdateErroreCopy (){
   
   String query=" UPDATE "+libraryMvxPersonalizzata+ "ZZBSTO "
                                       +" SET Z2NOTD=?"
                                       +" WHERE Z2IDUN=?";            
   return query;
   }
        
        
  private List<Allegati> elabFiles(List<List> filesPresent,Date dataPC,Map<String,String> tipoFilesDest,Map<String,String> tipoFilesEst){
    List listFilesToCopy=new ArrayList() ;
    
    for(List file:filesPresent){
        try {
           Allegati fileAllegato =new Allegati();
           fileAllegato.setCono(ClassMapper.classToString(file.get(0)).trim());
           fileAllegato.setIdSequence(ClassMapper.classToClass(file.get(1), Integer.class));
           fileAllegato.setOrdine(ClassMapper.classToString(file.get(2)).trim());
           fileAllegato.setTipoDoc(ClassMapper.classToString(file.get(3)).trim());
           fileAllegato.setPath(ClassMapper.classToString(file.get(4)).trim());
           fileAllegato.setTitolo(ClassMapper.classToString(file.get(5)).trim());
           fileAllegato.setDataUltAggior(ClassMapper.classToClass(file.get(6),Date.class));
           fileAllegato.setDataPresaCarico(dataPC);
           fileAllegato.setPathDest(tipoFilesDest.get(ClassMapper.classToString(file.get(3)).trim())+"\\"+fileAllegato.getTitolo());
           fileAllegato.setEstensione(tipoFilesEst.get(ClassMapper.classToString(file.get(3)).trim()));
           fileAllegato.setStatoTen(ClassMapper.classToString(file.get(7)).trim());
           fileAllegato.setDaCancellare(ClassMapper.classToString(file.get(8)).trim());
           
           System.out.println("DataUltimoAggiornamento-->"+DateUtils.dateToStr(fileAllegato.getDataUltAggior(), "yyyyMMddHHmmssSSS"));

           listFilesToCopy.add(fileAllegato);
           
         } catch (Exception ex) {
          addError("Errore in fase di lettura del file "+ ClassMapper.classToString(file.get(4)).trim() +" allegato --> " + ex.getMessage());
         }
    }
    
    return listFilesToCopy;
  }
  
  private String getQueryListFileAllegati(String data ){
    
    String sql="SELECT Z2CONO,Z2IDUN,Z2ORNO,Z2TDOC,Z2PTHS,Z2TITL,Z2DMAG,Z2ELAB,Z6DEL FROM "+ libraryMvxPersonalizzata +"ZZBSTO INNER JOIN "+ libraryMvxPersonalizzata +" ZORDIN ON Z6CONO=Z2CONO AND Z6ORNO=Z2ORNO WHERE Z2DDIP='"+data+"'";
             
    return sql;         
  }
  
  private String getQueryListDestTipoFile(){
    
    String sql="SELECT TRIM(CTSTKY),TRIM(SUBSTR(CTPARM,1,100)) FROM " +libraryMvx + "CSYTAB WHERE CTCONO =030 AND CTSTCO='CO_STORAGE'";
             
    return sql;         
  }
  
  private String getQueryListEstensioneTipoFile(){
    
    String sql="SELECT TRIM(CTSTKY),TRIM(SUBSTR(CTPARM,101,120)) FROM " +libraryMvx + "CSYTAB WHERE CTCONO =030 AND CTSTCO='CO_STORAGE'";
             
    return sql;         
  }
  
  /**
   * Torna una mappa in cui la chiave è il tipo file e il cui elemento è la destinazione per quel tipo file
   */

  private Map <String,String> getFileMap(List<List> TipoFile){
    Map parametro=new HashMap <String,String> ();
    for(List infoF:TipoFile){
      String tipoFile=ClassMapper.classToString(infoF.get(0));
      String parm=ClassMapper.classToString(infoF.get(1));
      parametro.put(tipoFile,parm);
    }
    
    return parametro;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestAllegati.class); 
  
}
