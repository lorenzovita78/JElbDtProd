/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.conn.ColombiniConnections;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Classe per calcolare lo sfrido prodotto dalla linea Ima Ante
 * @author lvita
 */
public class DatiSfridoImaAnte extends  DatiSfridoImpIma {
  
  
  public DatiSfridoImaAnte(String cdL) {
    super(cdL);
  }

  @Override
  public Connection getDbConnection() throws SQLException {
    return ColombiniConnections.getDbImaAnteConnection();
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoImaAnte.class); 
}
