/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.DatiStrettoioSqlS;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ElabDtStrettArtec extends ElabClass{

  
  
  private Date dataInizio;
  private String commessa=null;
  private Boolean isTest=Boolean.FALSE;
  private Boolean isCommAntFebal=Boolean.FALSE;
  
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
    
    if(parameter.get(ElabFilesArtecInFebal.PRM_N_COMMESSA)!=null){
      this.commessa=ClassMapper.classToString(parameter.get(ElabFilesArtecInFebal.PRM_N_COMMESSA));
    }
    
    if(parameter.get(ElabFilesArtecInFebal.PRM_COMM_TEST)!=null){
      this.isTest=ClassMapper.classToClass(parameter.get(ElabFilesArtecInFebal.PRM_COMM_TEST),Boolean.class);
    }
    
    if(parameter.get(ElabFilesArtecInFebal.PRM_COMM_AFEBAL)!=null){
      this.isCommAntFebal=ClassMapper.classToClass(parameter.get(ElabFilesArtecInFebal.PRM_COMM_AFEBAL),Boolean.class);
    }
    
    if(dataInizio==null)
      return Boolean.FALSE;
    
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    Map propsElab=getElabProperties();
    Connection conDbStrettoio=null;
    PersistenceManager pm =null;
    String listFiles=(String) propsElab.get(NameElabs.PATHFILESOURCESTRARTEC);
    List<String> filesToElab=ArrayUtils.getListFromArray(listFiles.split(";"));
    String fileElbCR=(String) propsElab.get(NameElabs.PATHFILECRTERM);
    Integer commInt= (commessa !=null) ? Integer.valueOf(commessa) : null; 
    
    try {
      conDbStrettoio=ColombiniConnections.getDbStrettoioArtec();
      pm=new PersistenceManager(conDbStrettoio);
      List<List> commGg=new ArrayList();
      if(commessa!=null && isTest){
        List oneC=Arrays.asList(commessa,DateUtils.getDataForMovex(new Date()));
        commGg.add(oneC);
      }else if(isCommAntFebal){
        commGg=DatiProdUtils.getInstance().getListCommesseFebalGgElab(dataInizio,commInt, Boolean.FALSE, Boolean.TRUE,Boolean.FALSE);
      }else{
        commGg=DatiProdUtils.getInstance().getListCommesseGgElab(con, dataInizio,commInt, Boolean.FALSE, Boolean.TRUE);
      }
      for(List lcomm:commGg){
        String fileS="";
        String commSTmp=DatiProdUtils.getInstance().getStringNComm(ClassMapper.classToClass(lcomm.get(0),Long.class));
        if(isCommAntFebal){
          commSTmp=DatiProdUtils.getInstance().getStringNComm(ClassMapper.classToClass(lcomm.get(0),Long.class)+400).substring(4);
        }
        Long commNTmp=ClassMapper.classToClass(lcomm.get(0),Long.class);
        Long dataCommN=ClassMapper.classToClass(lcomm.get(1),Long.class);
        Date dataComm=DateUtils.strToDate(dataCommN.toString(), "yyyyMMdd");
        
        if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(con, commNTmp, dataCommN) && 
                (isElabCrystalReportFinished(fileElbCR, dataInizio, commSTmp) || isCommAntFebal)   //se è una commessa di anticipo Febal Crystal non gira
                && !isElabPresentOnDbStrettoio(pm, commSTmp, dataComm) 
                || (commSTmp.equals(commessa) && isTest)  )  {
          for(String file:filesToElab){
            try{
              
              fileS=file.replace("$$$",commSTmp);
              File f=new File(fileS);
              if(f.exists()){
                List<DatiStrettoioSqlS> lstToSave=processFile(fileS,dataComm);
                if(lstToSave!=null && lstToSave.size()>0){
                  try{
                    for(DatiStrettoioSqlS b:lstToSave){
                      pm.storeDtFromBean(b);
                    }
                    addInfo("Commessa "+commSTmp+" - File "+fileS+" --> righe caricate "+lstToSave.size() );
                  }catch (SQLException s){
                    _logger.error("Errore in fase di salvataggio del dato  -->"+s.getMessage());
                    addError("Attenzione in fase di salvataggio del dato -->"+s.toString());
                  }
                }
              }else{
                addInfo("Commessa "+commSTmp+" - File non presente : "+fileS);
              }
            } catch(FileNotFoundException fe){
              _logger.error("Attenzione file non trovato "+fileS+" -->"+fe.getMessage());
              addError("Attenzione file non trovato "+fileS+" -->"+fe.toString());
            } catch(IOException io){
              _logger.error("Attenzione errore in accesso al file "+fileS+" -->"+io.getMessage());
              addError("Attenzione errore in accesso al file  "+fileS+" -->"+io.toString());
            }
          } 
        }
        
      } 
    } catch (SQLException s){
      _logger.error("Errore in fase di lettura delle commesse da processare  -->"+s.getMessage());
      addError("Errore in fase di lettura delle commesse da processare -->"+s.toString());
    } catch(QueryException q){
      _logger.error("Errore in fase di lettura delle commesse da processare -->"+q.getMessage());
      addError("Errore in fase di lettura delle commesse da processare"+q.toString());
    } finally{
      if(conDbStrettoio!=null)
        try{
          conDbStrettoio.close();
        } catch(SQLException s){
          
        }
    }
  }
  
  private Boolean isElabPresentOnDbStrettoio(PersistenceManager pm, String commessa ,Date dataComm) throws QueryException, SQLException{
   DatiStrettoioSqlS s=new DatiStrettoioSqlS(dataComm);
   s.setCommessa("0"+commessa);
   
   List l=Arrays.asList(DatiStrettoioSqlS.COMMESSA,DatiStrettoioSqlS.DATACOMMESSA);
   
   return pm.checkExist(s, l);
  }
  
  
  
  private Boolean isElabCrystalReportFinished(String fileCRElbFnh,Date dataRif,String nCommString) {
    Boolean exist=Boolean.FALSE;
    String nomeFile=fileCRElbFnh.replace("$$$", nCommString);
    
    File f=new File(nomeFile);
    if(f.exists() ){
      Date dataF=new Date(f.lastModified());
      if(DateUtils.numGiorniDiff(dataRif,dataF)>=0)
        exist=Boolean.TRUE;
    }
    
    
    return exist;
  }
  
  
  private List<DatiStrettoioSqlS> processFile(String fileS,Date dataComm) throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    List<DatiStrettoioSqlS> listElm=new ArrayList();
    
    
    long count=1;
    try{
      fR = new FileReader(fileS);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine(); 
      while(riga!=null && !riga.isEmpty() && !riga.startsWith("---")){
        DatiStrettoioSqlS dtS=new DatiStrettoioSqlS(dataComm);
        dtS.loadInfo(riga);
        if(dtS.validate())
          listElm.add(dtS);
        
        riga = bfR.readLine();
        count++;
      }
       
      
     _logger.info("File "+fileS+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return listElm;
  }   
    
    
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabDtStrettArtec.class); 
}
