/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;

import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author lvita
 */
public abstract class AvProdAnteP3 extends AvProdLineaStd {
  
  public abstract String getUtRef();
  
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    List pzComm=InfoDatiAnteP4.getInstance().loadDatiPzProdGGLinea(conAs400, getInfoLinea().getAzienda(), this.getDataRifCalc(), getUtRef());
    storeDatiProdComm(conAs400, pzComm);
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    
     return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
  
  }

  
}
