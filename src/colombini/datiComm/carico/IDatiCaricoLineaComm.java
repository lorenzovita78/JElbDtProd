/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico;

import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import java.sql.Connection;
import java.util.List;

/**
 * Interfaccia per classi che dovranno gestire le informazioni relative alla produzione di una linea
 * lavorativa per commessa
 * @author lvita
 */
public interface IDatiCaricoLineaComm {
  
  
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con,LineaLavBean ll) throws DatiCommLineeException;
  
}
