/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.costant.CostantsColomb;
import colombini.costant.TAPWebCostant;
import colombini.datiComm.carico.CaricoComOnTAP;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class DtAnteRemArtec extends CaricoComOnTAP {

//  public static final String PATHFANTEREM="//pegaso/flussi/FileProd/ARTEC/ANTEREM/ANTE";
//
//  @Override
//  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
//    List <CaricoCommLineaBean> beans=new ArrayList();
//    String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(ll.getCodLineaLav());
//    String fileN=InfoMapLineeUtil.getTabuProdNameLinea(nomeLinea, ll.getCommessa());
//    Double numPz;
//    try {
//      //codice modificato per aggiungere avanProd Ante Rem!!! NO BUONO!!!
//      //      List listaBeanAVP = processFile(fileN, ll);
//      //      saveListAvanProd(con, listaBeanAVP);
//      //
//      //cerco il file standard se non lo trovo provo a prendere il file bck
//      File f=new File(fileN);
//      if(!f.exists())
//        fileN=FileUtils.getNomeFileBKC(fileN);
//      
//      FileTabulatoAnteRem ft=new FileTabulatoAnteRem(fileN);
//      List listPz=ft.processFile(ll.getDataCommessa());
//      
//      numPz=new Double(listPz.size());
//      
//      CaricoCommLineaBean bean=new CaricoCommLineaBean();
//      bean.setCommessa(ll.getCommessa());
//      bean.setLineaLav(ll.getCodLineaLav());
//      bean.setDataRifN(ll.getDataCommessa());
//      bean.setUnitaMisura(ll.getUnitaMisura());
//      bean.setDivisione(CostantsColomb.BUARTEC);
//      bean.setValore(numPz);
//      beans.add(bean);
//      
//    } catch (FileNotFoundException ex) {
//      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
//      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
//    } catch (IOException ex) {
//      _logger.error("Problemi di accesso al file :"+fileN+" -> "+ex.getMessage());
//      throw new DatiCommLineeException("Impossibile accedere al file di log : "+fileN+" .Dati carico commessa non generabili");
//    }  
//    
//     
//    return beans;
//  }
  

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_ANTEREM_EDPC;
  }

  @Override
  public String getUniqueCodDivi() {
    return CostantsColomb.BUARTEC; //To change body of generated methods, choose Tools | Templates.
  }

  
  
  
  
  private static final Logger _logger = Logger.getLogger(DtAnteRemArtec.class);   
}
