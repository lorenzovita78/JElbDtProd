/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.util.DatiCommUtils;
import exception.QueryException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class DtImaAnte implements IDatiCaricoLineaComm {

  
  public static final String PATHFIMA="//pegaso/Flussi/FileProd/COLOMB/ANTEIMA/test/COM";
  public static final String PREFIXMINI="AE";
  public static final String PREFIXCOMF="F";
  public static final String PREFIXCOMG="G";
  public static final String EXTFILE=".TXT";
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    try {
      Map mapFABU=DatiCommUtils.getInstance().loadMapFADIV(con);
      Map mapDivPz=getMapDivPz(ll , mapFABU);
      
      Set keySet=mapDivPz.keySet();
      Iterator iter=keySet.iterator();
      while (iter.hasNext()){
        String divisione=(String) iter.next();
        Double valore=ClassMapper.classToClass(mapDivPz.get(divisione),Double.class);
        CaricoCommLineaBean bean=new CaricoCommLineaBean();
        bean.setCommessa(ll.getCommessa());
        bean.setLineaLav(ll.getCodLineaLav());
        bean.setDataRifN(ll.getDataCommessa());
        bean.setUnitaMisura(ll.getUnitaMisura());
        bean.setDivisione(divisione);
        bean.setValore(valore);
        beans.add(bean);
      }
    } catch (SQLException ex) {
      _logger.error("Impossibile caricare le informazioni relative alle BU ->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (QueryException ex) {
      _logger.error("Impossibile caricare le informazioni relative alle BU ->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }
     
    return beans;
  }
  
  private Map getMapDivPz(LineaLavBean lbean,Map mapFABU) throws DatiCommLineeException {
    Map mapBUpz=new HashMap<String,Long>();
    List<String> fileNames=getFileNames(lbean);
    for(String fn:fileNames){
      try {
        processFile(fn, mapFABU, mapBUpz);
      } catch (FileNotFoundException ex) {
        _logger.error("Problemi di accesso al file :"+fn+" -> "+ex.getMessage());
        throw new DatiCommLineeException("Impossibile accedere al file di log : "+fn+" .Dati carico commessa non generabili");
      } catch (IOException ex) {
        _logger.error("Problemi di accesso al file :"+fn+" -> "+ex.getMessage());
        throw new DatiCommLineeException("Impossibile accedere al file di log : "+fn+" .Dati carico commessa non generabili");
      }
    }
    
    return mapBUpz;
  } 
  
  
  private void processFile(String fileName,Map mapFaDiv,Map mapDivPz) throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    
    long count=0;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        String facility=riga.substring(3,6);
        String bu=DatiCommUtils.getInstance().getDivFromFacility(facility, mapFaDiv);
        if(mapDivPz.containsKey(bu)){
          Long value=(Long) mapDivPz.get(bu);
          mapDivPz.put(bu, value+1);
        }else{
          mapDivPz.put(bu,Long.valueOf(1));
        }
        riga = bfR.readLine();  
      }
      _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
  }
  
  private List getFileNames(LineaLavBean ll){
    List files=new ArrayList();
    String fn="";
    
    String commString=ll.getCommessaString();
    Integer commN=ll.getCommessa();
    
    
    if(commN>=400){
      fn=PATHFIMA+PREFIXMINI+commString+EXTFILE;
      files.add(fn);
    }else{
      // modifica del 18/07/2013
      // cambiato il nome del file F :--> il numero della commessa  è =  nCOM+400
      if(isImaAnteClienti(ll.getCodLineaLav())){
        commN+=400;
        fn=PATHFIMA+PREFIXCOMF+commN.toString()+EXTFILE;
        files.add(fn);
      }else{
        fn=PATHFIMA+PREFIXCOMG+commString+EXTFILE;
        files.add(fn);
      }
    }
    
    return files; 
  }
  
  public static Boolean isImaAnteClienti(String codLinea){
    if(("01008C").equals(codLinea)){
      return Boolean.TRUE;
    }
    
    return Boolean.FALSE;
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(DtImaAnte.class);   
}
