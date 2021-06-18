/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.NomiLineeColomb;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.logFiles.R1P2.LogFileTavoli;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class AvProdLineaTavoli extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
    Map commMap=getDatiCommFromLogFile(conAs400);
    storeDatiProdComm(conAs400, commMap);
    
  }
  
  
  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
  }

  
  
 private Map getDatiCommFromLogFile(Connection con) throws DatiCommLineeException{
   Map commMap=new HashMap();
   String fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.TAVOLISCRIV,getDataRifCalc());
   //String fileName=InfoMapLineeUtil.getLogFileGgLinea(NomiLineeColomb.TAVOLISCRIV,getDataForLogFile());
   _logger.info("Processing log file "+fileName);
   LogFileTavoli lf=new LogFileTavoli(fileName);
   try {
     commMap=lf.elabFileForAvProd();


   } catch (IOException ex) {
     throw new DatiCommLineeException(ex);
   }
   
   return commMap;
 }
  
 
 
  
  
 private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdLineaTavoli.class);  

  
  
  
}
