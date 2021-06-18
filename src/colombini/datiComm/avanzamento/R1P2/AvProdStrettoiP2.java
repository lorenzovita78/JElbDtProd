/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class AvProdStrettoiP2 extends AvProdLineaStd{

  
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
    Map commMap=getDatiFromVDL(conAs400);
    storeDatiProdComm(conAs400, commMap);  
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400,getInfoLinea().getAzienda(), getInfoLinea().getCommessa(), 
                         getInfoLinea().getDataCommessa(),getInfoLinea().getCodLineaLav());
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdStrettoiP2.class);  
  
}

