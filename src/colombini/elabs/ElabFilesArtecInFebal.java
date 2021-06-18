/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.util.DatiCommUtils;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import java.io.File;
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
import utils.FilePropUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabFilesArtecInFebal extends ElabClass {
  
  
  public final static String PRM_FILEPROPS_ELAB="FILEPROPSELAB";
  public final static String PRM_FORCE_WRITE="FORCEWRITE";
  public final static String PRM_PATHFILECOMM="PATHFILECOMM";
  
  public final static String PRM_N_COMMESSA="COMMESSA";
  
  public final static String PRM_COMM_TEST="COMMTEST";
  
  public final static String PRM_COMM_AFEBAL="COMMANTFEB";
  
  public Date dataElab=null;
  
  public String pathFileProps=null;
  public String pathFileCommessa=null;
  public Integer commessa=null;
  
  public Boolean forceWrite=Boolean.FALSE;
  public Boolean isTest=Boolean.FALSE;
  public Boolean commAntFeb=Boolean.FALSE;
  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
    
    dataElab=(Date) param.get(ALuncherElabs.DATAINIELB);
    
    pathFileProps=ClassMapper.classToString(param.get(PRM_FILEPROPS_ELAB));
    
    if(param.get(PRM_PATHFILECOMM)!=null)
      pathFileCommessa=ClassMapper.classToString(param.get(PRM_PATHFILECOMM));

    
    if(param.get(PRM_N_COMMESSA)!=null){
      this.commessa=ClassMapper.classToClass(param.get(PRM_N_COMMESSA),Integer.class);
    }
    
    if(param.get(PRM_COMM_TEST)!=null){
      this.isTest=ClassMapper.classToClass(param.get(PRM_COMM_TEST),Boolean.class);
    }
    
    if(param.get(PRM_COMM_AFEBAL)!=null){
      this.commAntFeb=ClassMapper.classToClass(param.get(PRM_COMM_AFEBAL),Boolean.class);
    }
    
    
    if(StringUtils.IsEmpty(pathFileProps)){
      addError("Attenzione parametro file properties per percorsi file non valorizzato correttamente");
      return Boolean.FALSE;
    }
      
    
//    if(StringUtils.IsEmpty(pathFileCommessa)){
//      addError("Attenzione parametro percorso commesse Crystal non valorizzato correttamente");
//      return Boolean.FALSE;
//    }
      
    
    if(dataElab==null)
      return Boolean.FALSE;
    
    if(param.get(PRM_FORCE_WRITE)!=null){
      this.forceWrite=ClassMapper.classToClass(param.get(PRM_FORCE_WRITE),Boolean.class);
    }
    
    
     
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Connection conDesmosFebal=null;
    try {
      _logger.info("Inizio elaborazione copia file x Artec in Febal");
      
      Map mappaParm=FilePropUtils.getInstance().readFilePropKeyVal(pathFileProps); 
      _logger.info("Lettura file properties :"+pathFileProps+" effettuata ");
      
      String pathFileS1=ClassMapper.classToString(mappaParm.get(NameElabs.PATHFILESRC1));
      String pathFileS2=ClassMapper.classToString(mappaParm.get(NameElabs.PATHFILESRC2));
      String pathFileDest=ClassMapper.classToString(mappaParm.get(NameElabs.PATHFILESDEST));
      String filesNameS1=ClassMapper.classToString(mappaParm.get(NameElabs.LISTFILESS1));
      String filesNameS2=ClassMapper.classToString(mappaParm.get(NameElabs.LISTFILESS2));
      List filesS1=ArrayUtils.getListFromArray(filesNameS1.split(","));
      List filesS2=ArrayUtils.getListFromArray(filesNameS2.split(","));
      
      if(StringUtils.IsEmpty(pathFileS1) || StringUtils.IsEmpty(pathFileS1) || StringUtils.IsEmpty(pathFileDest) ){
        addError("Directory fornite come parametri non valorizzate correttamente");
        return;
      }
      
      File dirS1=new File(pathFileS1);
      File dirS2=new File(pathFileS2);
      File dirDest=new File(pathFileDest);
      
      if(!dirS1.isDirectory() || !dirS2.isDirectory() ||  !dirDest.isDirectory()){
        addError("Percorsi passati come parametri non validi -->\n  1 :"+pathFileS1 + "  2 :"+pathFileS2+ "  3 :"+pathFileDest);
        return;
      }
      
      conDesmosFebal=ColombiniConnections.getDbDesmosFebalProdConnection();
      
      if(!commAntFeb){
      //commessa standard
        if(commessa!=null && isTest){
          List infoComm=Arrays.asList(commessa,DateUtils.getDataForMovex(new Date()));
          elabFilesCommessa(con,conDesmosFebal,pathFileS1,pathFileS2, pathFileDest,filesS1,filesS2,infoComm,isTest);
        }else{
          List<List> commesseGg=getCommesseToElab(con,dataElab,commessa);

          for(List infoComm:commesseGg){
            _logger.info("Elaborazione commessa " + infoComm.toString());
            elabFilesCommessa(con,conDesmosFebal,pathFileS1,pathFileS2, pathFileDest,filesS1,filesS2,infoComm);
          }
        }
      }else{ 
      //precommessa Febal
        if(commessa!=null && isTest){
          List infoComm=Arrays.asList(commessa,DateUtils.getDataForMovex(new Date()));
          elabFilesPreCommessa(con,conDesmosFebal,pathFileS1,pathFileS2, pathFileDest,filesS1,filesS2,infoComm,isTest);
        }else{
          List<List> precommesseGg=getPreCommesseFebalToElab( dataElab, commessa);

          for(List infoComm:precommesseGg){
            _logger.info("Elaborazione commessa " + infoComm.toString());
            elabFilesPreCommessa(con,conDesmosFebal,pathFileS1,pathFileS2, pathFileDest,filesS1,filesS2,infoComm,Boolean.FALSE);
          }
        }  
      }
      
      
    } catch (QueryException ex) {
      _logger.error("");
      addError("Impossibile verificare i numeri di commessa su As400-->"+ex.toString());
    } catch (SQLException ex) {
      addError("Impossibile verificare i numeri di commessa su As400-->"+ex.toString());
    } catch (IOException ex) {
      _logger.error("Errore in fase di lettura del file "+pathFileProps+ ">> Impossibile proseguire nell'elaborazione  : "+ex.getMessage());
      addError("Errore in fase di lettura del file "+pathFileProps+ ">> Impossibile proseguire nell'elaborazione : "+ex.toString());
    } finally{
      if(conDesmosFebal!=null)
        try{
          conDesmosFebal.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione con DbDesmos -->"+s.getMessage());
        }
    }
    
  }
  
  
  private void elabFilesCommessa(Connection conAs400, Connection conDbDesmosF,String dirS1,String dirS2,String dirDest,
          List<String> listFileNames1,List<String> listFileNames2,List infoComm){
    
    elabFilesCommessa(conAs400,conDbDesmosF,dirS1,dirS2,dirDest,listFileNames1,listFileNames2,infoComm,Boolean.FALSE);
  }
  
  
  
 private void elabFilesCommessa(Connection conAs400, Connection conDbDesmosF,String dirS1,String dirS2,String dirDest,
          List<String> listFileNames1,List<String> listFileNames2,List infoComm,Boolean isTest){
   
    Long numComm=ClassMapper.classToClass(infoComm.get(0),Long.class);
    Long dataCommL=ClassMapper.classToClass(infoComm.get(1),Long.class);
    String nCommS=DatiCommUtils.getInstance().getStringCommessa(numComm.intValue());
    
    try{
      if(!isTest){
        Boolean elabFebasDesmos=DesmosUtils.getInstance().isElabsDesmosFebalFinish(conAs400, numComm, dataCommL);
        if(!elabFebasDesmos){
          addWarning("Attenzione elaborazione DesmosFebal non ancora terminata per commessa -->"+nCommS);
          return; 
        }

        if(!isElabCrystalReportFinished(dataElab,nCommS)){
          addWarning("Attenzione elaborazione Crystal Report non ancora terminata per commessa -->"+nCommS);
          return; 
        }
      }
      
      for(int i=0;i<listFileNames1.size();i++){
        String fileS1=listFileNames1.get(i);
        
        String nFileTmp1=dirS1+"/"+fileS1.replace("$$$",nCommS);
        File f1=new File(nFileTmp1);
        String fileDestTmp=dirDest+"/"+fileS1.replace("$$$",nCommS);
        
        //se il file esiste già e non c'è impostato il parametro per la forzatura
        //allora non faccio niente
        File fDestAppo=new File(fileDestTmp);
        Date dataFS=new Date(f1.lastModified());
        Date dataFD=new Date(fDestAppo.lastModified());
        
        if(fDestAppo.exists()){
          int numdd=DateUtils.numGiorniDiff(dataFD, dataFS);
          if(numdd<=28 && !forceWrite){
            //se la differenza di giorni è maggiore di 0 segnalo anomalia
            // se la differenza giorni è=0 significa che il file è stato già copiato
            if(numdd>0){   
              addWarning("Attenzione file "+fileDestTmp+ " non processato perchè già presente nella cartella di destinazione");
            }
            continue;
          }else  if((forceWrite|| numdd>=30) ){
            fDestAppo.delete();
          }
        }
        //creo il file di CrystalReport  se sono in test
        if(!f1.exists() && isTest){
          try {
            f1.createNewFile();
          }catch(IOException e){
            _logger.error("Errore in fase di generazione del file "+f1+"-->"+e.getMessage());
            addError("Errore in fase di generazione del file "+f1);
          }
        } 
        
        if(f1.exists()){
          String fileS2=listFileNames2.get(i);
          String nFileTmp2=dirS2+"/"+fileS2.replace("$$$",nCommS);
          File f2=new File(nFileTmp2);
          if(f2.exists()){
            List filesAppend=new ArrayList();
            filesAppend.add(nFileTmp1);
            filesAppend.add(nFileTmp2);
            try {
              FileUtils.appendFiles(filesAppend, fileDestTmp);
              addInfo("Commessa "+numComm+" file mergiati "+filesAppend.toString()+" --> "+fileDestTmp);
            } catch (IOException ex) {
              _logger.error("Errore in fase di generazione del file "+fileDestTmp+"-->"+ex.getMessage());
              addError("Errore in fase di generazione del file "+fileDestTmp);
            }
          }else{
            try {
              FileUtils.copyFile(nFileTmp1, fileDestTmp);
              addInfo("Commessa "+numComm+" file copiato "+nFileTmp1+" --> "+fileDestTmp);
            } catch (IOException ex) {
              _logger.error("Errore in fase di generazione del file "+fileDestTmp+"-->"+ex.getMessage());
              addError("Errore in fase di generazione del file "+fileDestTmp);
            }
          }
        }
        
      }
      
      
    }catch(SQLException s){
      _logger.error("Errore in fase di interrogazione del db Desmos Febal -->"+s.getMessage());
      addError("Errore in fase di interrogazione del db Desmos Febal -->"+s.toString());
    }catch(QueryException q){
      _logger.error("Errore in fase di interrogazione del db Desmos Febal -->"+q.getMessage());
      addError("Errore in fase di interrogazione del db Desmos Febal -->"+q.toString());
    } 
 }
 

 private void elabFilesPreCommessa(Connection conAs400, Connection conDbDesmosF,String dirS1,String dirS2,String dirDest,
          List<String> listFileNames1,List<String> listFileNames2,List infoComm,Boolean isTest){
   
    Long numComm=ClassMapper.classToClass(infoComm.get(0),Long.class);
    Long dataCommL=ClassMapper.classToClass(infoComm.get(1),Long.class);
    Date dataComm=DateUtils.strToDate(dataCommL.toString(), "yyyyMMdd");
    
    
    String nCommS=DatiCommUtils.getInstance().getStringCommessa(numComm.intValue());
    
    try{
      if(!isTest){
        Boolean elabFebasDesmos=DesmosUtils.getInstance().isElabDesmosFebalFinished(conDbDesmosF, numComm, dataComm);
        if(!elabFebasDesmos){
          addWarning("Attenzione elaborazione DesmosFebal non ancora terminata per commessa -->"+nCommS);
          return; 
        }
      }
      //per il precommessa si aggiunge +400
      numComm=numComm+400;
      nCommS=DatiCommUtils.getInstance().getStringCommessa(numComm.intValue()).substring(4);
      for(int i=0;i<listFileNames1.size();i++){
        String fileS1=listFileNames1.get(i);
        
        String nFileTmp1=dirS1+"/"+fileS1.replace("$$$",nCommS);
        File f1=new File(nFileTmp1);
        String fileDestTmp=dirDest+"/"+fileS1.replace("$$$",nCommS);
        
        //se il file esiste già e non c'è impostato il parametro per la forzatura
        //allora non faccio niente
        File fDestAppo=new File(fileDestTmp);
        Date dataFS=new Date(f1.lastModified());
        Date dataFD=new Date(fDestAppo.lastModified());
        
        if(fDestAppo.exists()){
          int numdd=DateUtils.numGiorniDiff(dataFD, dataFS);
          if(numdd<=28 && !forceWrite){
            //se la differenza di giorni è maggiore di 0 segnalo anomalia
            // se la differenza giorni è=0 significa che il file è stato già copiato
            if(numdd>0){   
              addWarning("Attenzione file "+fileDestTmp+ " non processato perchè già presente nella cartella di destinazione");
            }
            continue;
          }else  if((forceWrite|| numdd>=30) ){
            fDestAppo.delete();
          }
        }
        //creo il file 
        if(!f1.exists()){
          try {
            f1.createNewFile();
          }catch(IOException e){
            _logger.error("Errore in fase di generazione del file "+f1+"-->"+e.getMessage());
            addError("Errore in fase di generazione del file "+f1);
          }
        }   
        String fileS2=listFileNames2.get(i);
        String nFileTmp2=dirS2+"/"+fileS2.replace("$$$",nCommS);
        File f2=new File(nFileTmp2);
        if(f2.exists()){
          List filesAppend=new ArrayList();
          filesAppend.add(nFileTmp1);
          filesAppend.add(nFileTmp2);
          try {
            FileUtils.appendFiles(filesAppend, fileDestTmp);
            addInfo("Commessa "+numComm+" file mergiati "+filesAppend.toString()+" --> "+fileDestTmp);
          } catch (IOException ex) {
            _logger.error("Errore in fase di generazione del file "+fileDestTmp+"-->"+ex.getMessage());
            addError("Errore in fase di generazione del file "+fileDestTmp);
          }
        }else{
          try {
            FileUtils.copyFile(nFileTmp1, fileDestTmp);
            addInfo("Commessa "+numComm+" file copiato "+nFileTmp1+" --> "+fileDestTmp);
          } catch (IOException ex) {
            _logger.error("Errore in fase di generazione del file "+fileDestTmp+"-->"+ex.getMessage());
            addError("Errore in fase di generazione del file "+fileDestTmp);
          }
        }
        
        
      }
      
      
    }catch(SQLException s){
      _logger.error("Errore in fase di interrogazione del db Desmos Febal -->"+s.getMessage());
      addError("Errore in fase di interrogazione del db Desmos Febal -->"+s.toString());
    } 
 }
 
  private List getPreCommesseFebalToElab(Date dataRif,Integer commessa) throws QueryException, SQLException{

    return DatiProdUtils.getInstance().getListCommesseFebalGgElab(dataRif,commessa,Boolean.FALSE,Boolean.TRUE,null);
  }
  
  
  
  private List getCommesseToElab(Connection con,Date dataRif,Integer commessa) throws QueryException, SQLException{

    return DatiProdUtils.getInstance().getListCommesseGgElab(con, dataRif, commessa, null,null);
  }
  
  
  
  
  private Boolean isElabCrystalReportFinished(Date dataRif,String nCommString) {
    Boolean exist=Boolean.FALSE;
    String nomeFile=pathFileCommessa.replace("$$$", nCommString);
    
    File f=new File(nomeFile);
    if(f.exists() ){
      Date dataF=new Date(f.lastModified());
      if(DateUtils.numGiorniDiff(dataRif,dataF)>=0)
        exist=Boolean.TRUE;
    }
    
    
    return exist;
  }
  
  
    
  
   private static final Logger _logger = Logger.getLogger(ElabFilesArtecInFebal.class);
}
