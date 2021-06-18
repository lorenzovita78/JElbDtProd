/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.costant.CostantsColomb;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.logFiles.R1P3.FileTabulatoAnteRem;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.util.InfoMapLineeUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class DtAnteRemArtecOLd implements IDatiCaricoLineaComm {

  public static final String PATHFANTEREM="//pegaso/flussi/FileProd/ARTEC/ANTEREM/ANTE";

  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(ll.getCodLineaLav());
    String fileN=InfoMapLineeUtil.getTabuProdNameLinea(nomeLinea, ll.getCommessa());
    Double numPz;
    try {
      //codice modificato per aggiungere avanProd Ante Rem!!! NO BUONO!!!
      //      List listaBeanAVP = processFile(fileN, ll);
      //      saveListAvanProd(con, listaBeanAVP);
      //
      //cerco il file standard se non lo trovo provo a prendere il file bck
      File f=new File(fileN);
      if(!f.exists())
        fileN=FileUtils.getNomeFileBKC(fileN);
      
      FileTabulatoAnteRem ft=new FileTabulatoAnteRem(fileN);
      List listPz=ft.processFile(ll.getDataCommessa());
      
      numPz=new Double(listPz.size());
      
      CaricoCommLineaBean bean=new CaricoCommLineaBean();
      bean.setCommessa(ll.getCommessa());
      bean.setLineaLav(ll.getCodLineaLav());
      bean.setDataRifN(ll.getDataCommessa());
      bean.setUnitaMisura(ll.getUnitaMisura());
      bean.setDivisione(CostantsColomb.BUARTEC);
      bean.setValore(numPz);
      beans.add(bean);
      
    } catch (FileNotFoundException ex) {
      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
    } catch (IOException ex) {
      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
    }  
    
     
    return beans;
  }
  
  
  
  
//  private List processFile(String fileName,LineaLavBean ll) throws FileNotFoundException, IOException{
//    FileReader fR = null;
//    BufferedReader bfR = null;
//    String riga;
//    Long count=Long.valueOf(0);
//    List beans=new ArrayList();
//    Map mappaColli=new HashMap();
//    
//    try{
//      fR = new FileReader(fileName);
//      bfR=new BufferedReader(fR);
//      
//      riga = bfR.readLine();  
//      while(riga!=null && !riga.isEmpty()){
//        count++;
//        
//        BeanAvanProdLinea av=BeanAvanProdLinea.getInfoCollo(ll.getCodLineaLav(), riga);
//        
//        if(av!=null){
//          if(!mappaColli.containsKey(av.getKey())){
//            mappaColli.put(av.getKey(),av.getKey());
//            av.setDataComN(ll.getDataCommessa());
//            beans.add(av);
//          }else{
//            _logger.warn("Attenzione  "+ av.getKey()+" -- collo già presente in lista");
//          }
//        }
//        
//        riga = bfR.readLine();  
//        
//      }
//      _logger.info("File "+fileName+" righe processate:"+count+ " oggetti da salvare "+beans.size());
//    }finally{
//      if(bfR!=null)
//        bfR.close();
//      if(fR!=null)
//        fR.close();
//    }
//    return beans;
//  }
 
  
//  @Override
//  public List<LineaLavCommessaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws LineeLavException {
//    List <LineaLavCommessaBean> beans=new ArrayList();
//    String fileN=getFileName(ll.getCommessaString());
//    Double numPz;
//    try {
//      numPz = processFile(fileN);
//      LineaLavCommessaBean bean=new LineaLavCommessaBean();
//      bean.setCommessa(ll.getCommessa());
//      bean.setLineaLav(ll.getCodLineaLav());
//      bean.setDataRifN(ll.getDataCommessa());
//      bean.setUnitaMisura(ll.getUnitaMisura());
//      bean.setDivisione("A03");
//      bean.setValore(new Double(numPz));
//      beans.add(bean);
//    } catch (FileNotFoundException ex) {
//        _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
//    } catch (IOException ex) {
//        _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
//    }  
//    
//     
//    return beans;
//  }
//  
//  
//  
//  
//  private Double processFile(String fileName) throws FileNotFoundException, IOException{
//    FileReader fR = null;
//    BufferedReader bfR = null;
//    String riga;
//    Double count=Double.valueOf(0);
//    try{
//      fR = new FileReader(fileName);
//      bfR=new BufferedReader(fR);
//      
//      riga = bfR.readLine();  
//      while(riga!=null && !riga.isEmpty()){
//        count++;
//        riga = bfR.readLine();  
//      }
//      _logger.info("File "+fileName+" righe processate:"+count);
//    }finally{
//      if(bfR!=null)
//        bfR.close();
//      if(fR!=null)
//        fR.close();
//    }
//    return count;
//  }
    
//  private void saveListAvanProd(Connection con,List beansAv){
//    
//    As400PersistenceMan pm=new As400PersistenceMan(con);
////    try {
//      pm.storeDtFromBeans(beansAv);
////    } catch (SQLException ex) {
////     _logger.error("Attenzione impossibile scrivere file per Avanzamento Produzione -->"+ex.getMessage());
////    }
//    
//  }
  
  
//  private String getFileName(String commString){
//    
//    
//    return PATHFANTEREM+commString+".TXT"; 
//  }
  
 
  private static final Logger _logger = Logger.getLogger(DtAnteRemArtecOLd.class);   
}
