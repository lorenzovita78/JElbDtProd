/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;


import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;


/**
 *
 * @author lvita
 */
public abstract class CaricoComAnteP3 extends CaricoComLineaSuppl {
  
  public abstract String getUtRef();
  
  @Override
  protected Integer getPzComm(Connection conAs400) throws DatiCommLineeException {
    
    return InfoDatiAnteP4.getInstance().getPzCommLinea(conAs400,  infoLinea.getAzienda(), infoLinea.getCommessa(), infoLinea.getDataCommessa(), getUtRef());         
    
  }

  
}
