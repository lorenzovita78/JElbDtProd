/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.CostantsColomb;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class AvProdImbMontFebal_OLD extends AvProdLineaStd{

  public final static String LINEAFEBALIMBALLOCOL="06516A";
  public final static String LINEAFEBALIMBALLOFEB="06516F";
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
     _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
     
    List bu=new ArrayList();
    
    bu.add(CostantsColomb.CODDITTAFEBAL);
    //devo far in modo di prendere solo i colli Febal e Febal giorno
    Map commMapFeb=getDatiFromVDL(conAs400,getLineeLogicheImbFebal(),bu);
    
    if(LINEAFEBALIMBALLOFEB.equals(getInfoLinea().getCodLineaLav())){
      storeDatiProdComm(conAs400, commMapFeb);
    }else{
      Map commMapCol=getDatiFromVDL(conAs400,getLineeLogicheImbFebal(),null);
      
      Set keysM=commMapFeb.keySet();
      Iterator iter =keysM.iterator();
      while (iter.hasNext()){
        Integer comm=(Integer) iter.next();
        Integer valueF=(Integer) commMapFeb.get(comm);
        if(commMapCol.containsKey(comm)){
          Integer valueC=(Integer) commMapCol.get(comm);
          commMapCol.put(comm, valueC-valueF);
        }
      }
      
      storeDatiProdComm(conAs400, commMapCol);
    }
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400,getInfoLinea().getAzienda(), getInfoLinea().getCommessa(), 
                         getInfoLinea().getDataCommessa(),getInfoLinea().getCodLineaLav());
  }
 
  
  
  

  private List<String> getLineeLogicheImbFebal() {
    List<String> lineeLogiche=new ArrayList();
    
    lineeLogiche.add("06516");
    lineeLogiche.add("06517");
    lineeLogiche.add("06518");
    
    return lineeLogiche;
  }
  
  
  private static final Logger _logger = Logger.getLogger(AvProdImbMontFebal_OLD.class);
}
