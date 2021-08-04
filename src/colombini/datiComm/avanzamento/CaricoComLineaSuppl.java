/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento;

import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.AvProdCommLineaBean;
import java.sql.Connection;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public abstract class CaricoComLineaSuppl {

  public LineaLavBean infoLinea;
  
  public void setInfoLinea(LineaLavBean llav) {
    this.infoLinea = llav;
  }
  
  
  public AvProdCommLineaBean getAvProdCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    if(infoLinea==null)
      return null;
    
    AvProdCommLineaBean bean=new AvProdCommLineaBean(infoLinea.getAzienda(),infoLinea.getCommessa(), 
            infoLinea.getDataCommessa(), infoLinea.getCodLineaLav());
    
    Integer pzCom=getPzComm(con);
    if(pzCom<=0)
      return null;
    
    bean.setPzComm(pzCom);
    
    _logger.info("Linea "+infoLinea.getCodLineaLav() +" commessa n."+infoLinea.getCommessa()+" --> pezzi da produrre n."+pzCom);
    
    
    return bean;
    
  }
  
  
  
  
  
  
  protected abstract Integer getPzComm(Connection con) throws DatiCommLineeException ;
 
  
  
  
  private static final Logger _logger = Logger.getLogger(CaricoComLineaSuppl.class);

  
}
