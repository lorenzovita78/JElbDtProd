/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P1;

import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class AvProdImbMontArtec extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    
    
    //String lineaRif= getInfoLinea().getCodLineaLav();
    //modifico il codice della linea perchè devo cercare i pz prodotti da più linee lavorative 
    //che afferiscono ai centri 03011 e 03006
    
     _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
    Map commMap=getDatiFromVDL(conAs400,getLineeLogicheImballoArtec(),null);
    storeDatiProdComm(conAs400, commMap);
    //reimposto il valore iniziale della linea
    //getInfoLinea().setCodLineaLav(lineaRif);
    
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
   return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(),this.getInfoLinea().getCommessa() , 
            this.getInfoLinea().getDataCommessa(),this.getInfoLinea().getCodLineaLav() );
  }
  
  
  private List<String> getLineeLogicheImballoArtec() {
    List<String> lineeLogiche=new ArrayList();
    
    lineeLogiche.add("");
    lineeLogiche.add("36010");
    lineeLogiche.add("36015");
    lineeLogiche.add("36020");
    lineeLogiche.add("36025");// linea BASI NO VDL!!!!!!
    lineeLogiche.add("36030");
    lineeLogiche.add("36050"); //linea 5 Strettoio ,aggiunta dopo mail di Lucia del 13/10/2015
    lineeLogiche.add("36150");
    lineeLogiche.add("36200");
    
    return lineeLogiche;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(AvProdImbMontArtec.class);
}
