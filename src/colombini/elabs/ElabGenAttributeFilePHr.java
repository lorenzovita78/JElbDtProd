/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;


import elabObj.ElabClass;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabGenAttributeFilePHr extends ElabClass{
  
  public final static String PDIRSOURCE="DIRSOURCE";
  public final static String PMODELNAME="MODELNAME";
  public final static String PCODAZIENDA="CODAZIENDA";
  public final static String PANNORIF="ANNORIF";
  public final static String PSTRUCTFILENAME="STRUCTFILENAME";
  public final static String PCHARSPLIT="CHARSPLIT";
  
  public final static String $CODISS$="$CODISS$";
  public final static String $CODAZ$="$CODAZ$";
  
  
  private String dirFiles;
  private String modelName;
  private String codazienda;
  //private String idazienda;
  private String structFileName;
  private String annoRif;
  private String charSplit;
  
  private List<String> elmsToRemoveFromFile=new ArrayList();
  private Map mapAziende;
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(PDIRSOURCE)!=null){
      this.dirFiles=ClassMapper.classToString(parameter.get(PDIRSOURCE));
    }  
    
    if(parameter.get(PMODELNAME)!=null){
      this.modelName=ClassMapper.classToString(parameter.get(PMODELNAME));
    }  
    
    if(parameter.get(PCODAZIENDA)!=null){
      this.codazienda=ClassMapper.classToString(parameter.get(PCODAZIENDA));
    }
    
    if(parameter.get(PANNORIF)!=null){
      this.annoRif=ClassMapper.classToString(parameter.get(PANNORIF));
    }
    
    if(parameter.get(PSTRUCTFILENAME)!=null){
      this.structFileName=ClassMapper.classToString(parameter.get(PSTRUCTFILENAME));
    }
    
    if(parameter.get(PCHARSPLIT)!=null){
      this.charSplit=ClassMapper.classToString(parameter.get(PCHARSPLIT));
    }
    
    if(StringUtils.IsEmpty(dirFiles) || StringUtils.isEmpty(modelName) 
            ||StringUtils.IsEmpty(structFileName)  || StringUtils.isEmpty(annoRif) 
             ){
      
      _logger.warn("Parametri non valorizzati correttamente >> "+parameter.toString());
      return Boolean.FALSE;
    }
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    _logger.info("Inizio elaborazione generazione attributefile per modello "+modelName);
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBFILEDESCRPHR );
    String nomeFileAttribute=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEATTR));
    String stringelementstoremove=ClassMapper.classToString(getElabProperties().get(NameElabs.STRINGSTOREMOVE));
    if(!StringUtils.isEmpty(stringelementstoremove)){
      elmsToRemoveFromFile=ArrayUtils.getListFromArray(stringelementstoremove.split(","));
    }
    
    mapAziende=prepareMapAziende();
    if(mapAziende==null || mapAziende.isEmpty()){
      addError("Aziende da gestire no valorizzate. Impossibile proseguire!");
      return;
    }
    if(!structFileName.contains($CODISS$)){
      addError("Campo codice Iss non indicato nella struttura del file. Impossibile proseguire!");
      return;
    }
    if(charSplit!=null && !structFileName.contains(charSplit)){
      addError("Campo di split non presente nella struttura del file. Impossibile proseguire!");
      return;
    }
    
    File dir=new File(dirFiles);   
    if(dir.isFile()){
      addError("Attenzione percorso non fornito corretto . Impossibile proseugire!");
      return;
    }
    
     List listStructFile=null;
    if(charSplit==null){
      listStructFile=new ArrayList();
      listStructFile.add(structFileName);
    }else{
      listStructFile=ArrayUtils.getListFromArray(structFileName.split(charSplit));
    }
    FileOutputStream fileOutput = null;
    PrintStream output=null;

    
    //structFileName="XXX_X"+$CODAZ$+"_X"+$CODISS$+"_XXX_XXX_XXX.pdf"; 
    //structFileName="X1_X2_X3_X4_"+$CODISS$+".pdf"; 
    
    
    try {
      List<String> files =ArrayUtils.getListFromArray(dir.list());
      if(files==null || files.isEmpty()){
        addError("La cartella indicata non contiene nessun file.Impossibile proseguire");
        return;
      }
      
      fileOutput = new FileOutputStream(dirFiles+"\\"+nomeFileAttribute);
      output = new PrintStream(fileOutput);

      for(String fileNameTmp:files){
        String rigaToAdd=getStringForAttributeFile(fileNameTmp,listStructFile);
        if(!StringUtils.IsEmpty(rigaToAdd)){
          output.println(rigaToAdd);
        }else{
          System.out.println("Impossibile generare riga su attribute file per :"+fileNameTmp);
        }  
      }
      
      output.flush();
      
    } catch (FileNotFoundException ex) {
      _logger.error("Errore in fase di generazione del file attribute -->"+ex.getMessage());
       addError("Errore in fase di generazione del file attribute -->"+ex.toString());
    } finally {
      if(output!=null)
        output.close();
      
      if(output!=null)
        output.close();
      
    }
    
  }

  private Map prepareMapAziende(){
    Map azMap=new HashMap();
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBFILEDESCRPHR);
    String codiciAz=ClassMapper.classToString(getElabProperties().get(NameElabs.CODAZIENDE));
    String idsAz=ClassMapper.classToString(getElabProperties().get(NameElabs.IDAZIENDE));
    
    if(StringUtils.isEmpty(codiciAz) || StringUtils.IsEmpty(idsAz) ){
      addError("Parametri relativi alle aziende gestite non valorizzati. Impossibile proseguire");
      return null;
    }
      
    List<String> listCod=ArrayUtils.getListFromArray(codiciAz.split(","));
    List<String> listIds=ArrayUtils.getListFromArray(idsAz.split(","));
    if(listCod.size()!=listIds.size()){
      addError("Parametri relativi alle aziende gestite non valorizzati correttamente. Impossibile proseguire");
      return null;
    }
    
    for(int i=0 ; i<listCod.size();i++){
      azMap.put(listCod.get(i),listIds.get(i));
    }
    
    return azMap;
  }  
  
  private String getStringForAttributeFile(String fileName,List listStructFile) {
    StringBuilder  rowAtbFile=new StringBuilder();
    if(charSplit!=null && !fileName.contains(charSplit)){
      addError("Impossibile generare riga su file attribute --> Separatore file non presente nel nome del file :"+fileName);
      return null;
    }
    //lista degli elementi che compongono il nome del file in base al separatore delle proprietà
    List listFileName=null;
    if(charSplit==null){
      listFileName=new ArrayList();
      listFileName.add(fileName);
    }else{
      listFileName=ArrayUtils.getListFromArray(fileName.split(charSplit));
    }
    if(listStructFile.size()!=listFileName.size()){
      addError("Impossibile generare riga su file attribute --> Nome file differente dalla struttura fornita :"+fileName);
      return null;
    }
    rowAtbFile.append(getStringProperty("FILENAME", fileName));
    rowAtbFile.append(getStringProperty("HRZ_MODEL", modelName));
    if(!StringUtils.isEmpty(codazienda)){
      rowAtbFile.append(getStringProperty("CFISC", codazienda));
      rowAtbFile.append(getStringProperty("IDCOMPANY", (String) mapAziende.get(codazienda) ) );
    }else{
      if(!listStructFile.contains($CODAZ$)){
        return null;
      }else{
        String codAzTmp=getValString(listStructFile, listFileName, $CODAZ$);
        if(StringUtils.isEmpty(codAzTmp)){
          addError("Codice Azienda non rilevato nel file "+fileName);
          return null;
        } else{
          Set keys=mapAziende.keySet();
          Iterator iter=keys.iterator();
          while(iter.hasNext()){
            String codAziendaUff=ClassMapper.classToString(iter.next());
            if(codAziendaUff.contains(codAzTmp)){
              rowAtbFile.append(getStringProperty("CFISC", codAziendaUff));
              rowAtbFile.append(getStringProperty("IDCOMPANY", (String) mapAziende.get(codAziendaUff) ) );
            }
          }
        } 
      }
    }
    
    rowAtbFile.append(getStringProperty("ANNO", annoRif));
    //CODICE ISS
    String codIssTmp=getValString(listStructFile, listFileName, $CODISS$);
    if(StringUtils.isEmpty(codIssTmp)){
      addError("Codice Iss non rilevato nel file "+fileName);
      return null;
    } else{
      rowAtbFile.append(getStringProperty("COFISCD", codIssTmp));
    }
    
    return rowAtbFile.toString();
  }
  
  private String getStringProperty(String nomeProp,String valueProp){
    return nomeProp+"="+valueProp+";";
  }
  
  
  private String getValString(List<String> listStructFile,List<String> listFileName,String elToFind){
    String val="";
    for(int i=0;i<listStructFile.size();i++){
      String elStruct=ClassMapper.classToString(listStructFile.get(i));
      if(elStruct.contains(elToFind)){
        String el=ClassMapper.classToString(listFileName.get(i));
        for(String charR:elmsToRemoveFromFile){
          el=el.replace(charR, "");
        }
        val=el.trim();
        break;
      }
    }
    return val;
  }
  
  

  private static final Logger _logger = Logger.getLogger(ElabGenAttributeFilePHr.class);  

  
}
