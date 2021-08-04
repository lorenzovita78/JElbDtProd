/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;


import colombini.model.persistence.BeanLogSpuntaForTAP;
import db.persistence.IBeanPersSIMPLE;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabLogFileTavoli extends ElabClass{

  
  public final static String NO_READ="NoRead";
  public final static String USER_INSERT="lineaptsr1";
  
  private Date dataInizio;
  private Date dataFine;
  
  @Override
  public Boolean configParams() {
    Map parm=getInfoElab().getParameter();
    
    if(parm.get(ALuncherElabs.DATAINIELB)!=null){
      dataInizio=(Date) parm.get(ALuncherElabs.DATAINIELB);
    }
    
    if(parm.get(ALuncherElabs.DATAFINELB)!=null){
      dataFine=(Date) parm.get(ALuncherElabs.DATAFINELB);
    }
    
    if(dataInizio==null)
      return Boolean.FALSE;
    
    if(dataFine==null)
      dataFine=dataInizio;
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Date dataTmp=dataInizio;
    Map propsElab=getElabProperties();
    String pathFile=(String) propsElab.get(NameElabs.PATHFILELOGTAVOLI);
    
    while(DateUtils.numGiorniDiff(dataTmp,dataFine)>=0){
      try{
        String dataS=DateUtils.dateToStr(dataTmp, "ddMMyyyy");
        String nomeFile=pathFile+dataS+".txt";
        deleteOldRecord(con, dataTmp);
        List<IBeanPersSIMPLE> beans=processFile(nomeFile, con,dataTmp);
        if(beans!=null){
          PersistenceManager ph=new PersistenceManager(con);
          for(IBeanPersSIMPLE bean:beans){
          try{
            ph.storeDtFromBean(bean);
          }catch(SQLException s){
           addError("Errore in fase di salvataggio del dato "+bean.toString());
           _logger.error("Errore in fase di salvataggio del dato del log "+bean.toString()+" --> "+s.getMessage());
          }  
    }
        }
      } catch(SQLException s){
        addError("Errore in fase di cancellazione dei dati per il giorno "+dataTmp);
      } catch (IOException ex) {
        addWarning("Errore in fase di accesso al file per il giorno "+dataTmp+" -->"+ex.toString());
        _logger.error("Errore in fase di accesso al file per il giorno "+dataTmp+" -->"+ex.getMessage());
      }
      dataTmp=DateUtils.addDays(dataTmp, 1);
    }
     
  }
  
  
  private void deleteOldRecord(Connection con,Date data) throws SQLException{
    BeanLogSpuntaForTAP bean=new BeanLogSpuntaForTAP(NO_READ, data, USER_INSERT);
    bean.deleleOldRecord(con);
  }
  
  private List processFile(String nomeFile,Connection con,Date dataRif) throws IOException{
    List listColli=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Long noread=Long.valueOf(0);
    
    try{
      fR = new FileReader(nomeFile);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();
      while(riga!=null ){
        List elem =ArrayUtils.getListFromArray(riga.split(";"));
        String barcode=(String) elem.get(2);
        if(barcode.contains(NO_READ)){
          riga=bfR.readLine();
          noread++; 
          count++;
          continue;
        }
        BeanLogSpuntaForTAP bean=null;
        try{
          String orario=ClassMapper.classToString(elem.get(1));
          Integer sec=Integer.valueOf(orario.substring(4));
          sec+=Integer.valueOf(orario.substring(0,2))*3600;
          sec+=Integer.valueOf(orario.substring(2,4))*60;
          Date dataTmp=DateUtils.addSeconds(dataRif, sec);
          bean=new BeanLogSpuntaForTAP(barcode, dataTmp, USER_INSERT);
          bean.loadIdPz(con);
          listColli.add(bean);
        }catch(SQLException s){
         _logger.error("Errore in fase di creazione del record su ZTAPCP -->"+bean.toString()+" - "+s.getMessage());
         addError("Errore in fase di creazione del record su ZTAPCP -->"+bean.toString());
        }
        riga=bfR.readLine();
        count++;
      }
    
    }finally{
      _logger.info("File "+nomeFile+"; Righe processate: "+count + ";  Righe NoRead: "+noread );
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }  
    return listColli;
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabLogFileTavoli.class);   
  
}
