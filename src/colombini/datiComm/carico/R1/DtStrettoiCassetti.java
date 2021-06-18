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
public class DtStrettoiCassetti implements IDatiCaricoLineaComm {

  public final static String PATHP2 = "U:/TABUPROD/P2/CASSETTI/";
  
  public DtStrettoiCassetti(){
    
  }
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    String fileN=getFileName(ll.getCommessaString());
    
    try {
      Map dittaDiv=DatiCommUtils.getInstance().loadMapDittDIV(con);
      //caricamento dati da Desmos per cassetti da Alberti
      DtForatriceAlberti dtA=new DtForatriceAlberti();
      String lancio= ll.getDataCommessa().toString().substring(0,4)+"__"+ll.getCommessaString();
      List<List> infoAlberti =dtA.loadDatiFromDesmos(ll.getCommessa(), lancio);
      Map infoCass=processFile(fileN);
      
      for(List record:infoAlberti){
        String ditta=ClassMapper.classToString(record.get(0));
        if(ditta==null || ditta.isEmpty()){
          _logger.error(" LINEA : "+ll.getCodLineaLav()+ " - ATTENZIONE DITTA NON VALORIZZATA PER COMMESSA "+ll.getCommessa());
          continue; 
        }  
        
        Double valAlb=ClassMapper.classToClass(record.get(1),Double.class);
        Double val2=ClassMapper.classToClass(infoCass.get(ditta),Double.class);
        if(val2==null)
          val2=Double.valueOf(0);
        
        infoCass.remove(ditta);
        
        DatiCommUtils.getInstance().addInfoCaricoComBu(beans, ll.getCodLineaLav(), ll.getDataCommessa(),ll.getCommessa() , ll.getUnitaMisura(),
                                                       ClassMapper.classToString(dittaDiv.get(ditta)), valAlb+val2);
      }
      Set keySet=infoCass.keySet();
      Iterator iter=keySet.iterator();
      while (iter.hasNext()){
        String ditta=(String) iter.next();
        Double valore=ClassMapper.classToClass(infoCass.get(ditta),Double.class);
        DatiCommUtils.getInstance().addInfoCaricoComBu(beans, ll.getCodLineaLav(), ll.getDataCommessa(),ll.getCommessa() , ll.getUnitaMisura(),
                                                       ClassMapper.classToString(dittaDiv.get(ditta)), valore);
      }
      
      
//      Set keySet=info.keySet();
//      Iterator iter=keySet.iterator();
//      while (iter.hasNext()){
//        String ditta=(String) iter.next();
//        Double valore=ClassMapper.classToClass(info.get(ditta),Double.class);
//        CaricoCommLineaBean bean=new CaricoCommLineaBean();
//        bean.setCommessa(ll.getCommessa());
//        bean.setLineaLav(ll.getCodLineaLav());
//        bean.setDataRifN(ll.getDataCommessa());
//        bean.setUnitaMisura(ll.getUnitaMisura());
//        bean.setDivisione((String)dittaDiv.get(ditta));
//        bean.setValore(valore);
//        beans.add(bean);
//      }
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
  
  
  
  private Map processFile(String fileName) throws FileNotFoundException, IOException{
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
        List record=ArrayUtils.getListFromArray(riga.split(";"));
        if(record!=null && record.size()==7){
          Double value =ClassMapper.classToClass(record.get(4),Double.class);
          String ditta =ClassMapper.classToString(record.get(6));

          if(mappaVal.containsKey(ditta)){
            Double appo=(Double)mappaVal.get(ditta);
            appo+=value;
            mappaVal.put(ditta, appo);
          }else{
            mappaVal.put(ditta, value);
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
    
    return PATHP2+commString+"CSS1.CSV"; 
  }
  
  
  private static final Logger _logger = Logger.getLogger(DtStrettoiCassetti.class);   
}
