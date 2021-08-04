/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.G1;


import colombini.conn.ColombiniConnections;
import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.exception.DatiCommLineeException;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class CaricoCommSagomati extends CaricoComLineaSuppl {

  @Override
  protected Integer getPzComm(Connection con) throws DatiCommLineeException {
    Connection conSqLS=null;
    Integer pz=Integer.valueOf(0);
    
    
    try {
      conSqLS = ColombiniConnections.getDbBitaBitConnection();
      String qry=" SELECT COUNT(*)\n" +
                "  FROM [dbo].[Commesse_Dettagli] "+
                " WHERE p7comm ="+JDBCDataMapper.objectToSQL(infoLinea.getCommessa())+
                  " and p7refd=CONVERT(datetime,'"+infoLinea.getDataCommessa()+"',112)";
                  
      
      Object [] obj=ResultSetHelper.SingleRowSelect(conSqLS, qry);
      if(obj[0]!=null)
        pz=ClassMapper.classToClass(obj[0], Integer.class);
    
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con il server -->"+ex.getMessage());
      throw new DatiCommLineeException("Errore in fase di connessione con il database");
    } finally{
      if(conSqLS!=null)
        try {
          conSqLS.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di rilascio della connessione -->"+ex.getMessage());
      }
      
    }
    
    return pz;
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricoCommSagomati.class);
}
