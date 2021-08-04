/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.logFiles.R1P3;

import colombini.costant.NomiLineeColomb;
import colombini.model.persistence.BeanInfoColloComForTAP;
import colombini.util.InfoMapLineeUtil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class FileTabulatoAnteRem {
  

 //codici programmi foratura per campioni ed espositori 
 public final static String PRGFOREX="00000000990000,0000000099L000,0000000099E000,000000XX30L000,000000TM99L000,000000XXN0L000";
  
  private String fileName;

  public FileTabulatoAnteRem(String fileName) {
    this.fileName = fileName;
  }
  
  
  public List<BeanInfoColloComForTAP> processFile(Long dataRifC) throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    List beans=new ArrayList();
    Map mappaColli=new HashMap();
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null){ // && !riga.isEmpty())
        count++;
        
        BeanInfoColloComForTAP av=getInfoCollo(riga);
        
        if(av!=null){
          av.setDataComN(dataRifC);
          String barC=av.getBarcode();
          if(!mappaColli.containsKey(barC)){
            mappaColli.put(barC,barC);
            beans.add(av);
          }else{
            _logger.warn("Attenzione  "+barC+" -- collo già presente in lista");
          }
        }
        
        riga = bfR.readLine();  
        
      }
      _logger.info("File "+fileName+" righe processate:"+count+ " oggetti da salvare "+beans.size());
    }catch(Exception e){
    _logger.error("Eccezione generica -->"+e.getMessage()+" nrighe : "+count);
    
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return beans;
  }
  
  
  
  private BeanInfoColloComForTAP getInfoCollo(String riga){
    if(StringUtils.IsEmpty(riga))
      return null;
    
    String comm=riga.substring(0, 3);
    String collo=riga.substring(3,8);
    String tipo=riga.substring(8,10);
    String barcode=riga.substring(0, 10);
    String prgForm=riga.substring(22,36);
    String lineaL=riga.substring(36,41).trim();
    String coArt=riga.substring(44,53).trim();
    String descArt=riga.substring(53,riga.length()).trim();
    String descEst=descArt.trim();
    if(descArt.length()>30)
      descArt=descArt.substring(0, 30);
    
    
    //se il programma di foratura è tra quelli da escludere non prendo in considerazione il pezzo
    if(PRGFOREX.contains(prgForm)){
      return null;
    }  
    
    BeanInfoColloComForTAP infoC=new BeanInfoColloComForTAP(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORANTEREM),
            Integer.valueOf(comm), Integer.valueOf(collo), Integer.valueOf(tipo),Boolean.FALSE);
    
    infoC.setLineaLogica(lineaL);
    infoC.setBarcode(barcode);
    infoC.setCodArticolo(coArt);
    infoC.setDescArticolo(descArt);

    if(descEst.length()>225)
      descEst=descEst.substring(0, 225);
    
    infoC.setDescEstesa(descEst); 
    
    return infoC;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(FileTabulatoAnteRem.class);
  
}
