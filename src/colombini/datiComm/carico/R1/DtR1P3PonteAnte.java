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
import utils.ArrayUtils;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class DtR1P3PonteAnte implements IDatiCaricoLineaComm {

  //public static final String PATHP3="//colom/qdls/TABUPROD/P3/";
  public static final String PATHP3="U:/TABUPROD/P3/";
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    String fileN=getFileName(ll.getCommessaString());
    
    try {
      Map dittaDiv=DatiCommUtils.getInstance().loadMapDittDIV(con);
      Map info=processFile(fileN, ll.getCodLineaLav());
      Set keySet=info.keySet();
      Iterator iter=keySet.iterator();
      while (iter.hasNext()){
        String ditta=(String) iter.next();
        Double valore=ClassMapper.classToClass(info.get(ditta),Double.class);
        CaricoCommLineaBean bean=new CaricoCommLineaBean();
        bean.setCommessa(ll.getCommessa());
        bean.setLineaLav(ll.getCodLineaLav());
        bean.setDataRifN(ll.getDataCommessa());
        bean.setUnitaMisura(ll.getUnitaMisura());
        bean.setDivisione((String)dittaDiv.get(ditta));
        bean.setValore(valore);
        beans.add(bean);
      }
    } catch (FileNotFoundException ex) {
      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
    } catch (IOException ex) {
      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
    } catch (SQLException ex) {
      _logger.error("Impossibile accedere al db AS400 -->"+ex.getMessage());
      throw new DatiCommLineeException("Problemi di interrogazione del database .Dati carico commessa non generabili");
    } catch (QueryException ex) {
      _logger.error("Impossibile caricare la mappa ditta - divisione :"+ex.getMessage());
      throw new DatiCommLineeException("Problemi di interrogazione del database .Dati carico commessa non generabili");
    }  
    
     
    return beans;
  }
  
  
  
  
  private Map processFile(String fileName,String codLinea) throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    Map mappaVal=new HashMap();
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();
      count++;
      while(riga!=null && !riga.isEmpty()){
        //prima riga con intestazione
        if(count>1){
          List record=ArrayUtils.getListFromArray(riga.split(";"));
          if(record!=null && record.size()==7){
            String gruppo = ClassMapper.classToString(record.get(1));
            Double value =ClassMapper.classToClass(record.get(4),Double.class);
            String linea =ClassMapper.classToString(record.get(5));
            String ditta =ClassMapper.classToString(record.get(6));
            if(linea.equals(codLinea) && isGruppoOk(linea, gruppo) ){
              if(mappaVal.containsKey(ditta)){
                Double appo=(Double)mappaVal.get(ditta);
                appo+=value;
                mappaVal.put(ditta, appo);
              }else{
                mappaVal.put(ditta, value);
              }
            }
          }
        }        
        riga = bfR.readLine();  
        count++;
      }
      _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    return mappaVal;
  }
  
  private String getFileName(String commString){
    
    return PATHP3+commString+"CSVP3.CSV"; 
  }
  
  
  /**
   * Verifica il gruppo di appartenenza.
   * Il gruppo ha un significato solo per la linea 01032 in quanto vogliamo escludere tutto
   * quello che non fa riferimento a delle ante.
   * @param codLinea
   * @param gruppo
   * @return 
   */
  private Boolean isGruppoOk(String codLinea,String gruppo){
    if(!"01032".equals(codLinea))
      return Boolean.TRUE;
    
    if(gruppo.toUpperCase().contains("PANNELLI") || gruppo.toUpperCase().contains("TELAIONI"))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
 
  private static final Logger _logger = Logger.getLogger(DtR1P3PonteAnte.class);   
}
