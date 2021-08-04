/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.G1;


import colombini.conn.ColombiniConnections;
import colombini.datiComm.avanzamento.AvProdLineaStd;
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
public class AvanProdSagomati extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    Connection conSqLS=null;
    Integer pz=Integer.valueOf(0);
    
    
    try {
      conSqLS = ColombiniConnections.getDbBitaBitConnection();
      String qry=" SELECT COUNT(*) \n" +
                "   from commesse_dettagli_esecuzione a inner join commesse_anagrafica b on a.id_commessa=b.id"+
                " \n WHERE id_commessa = "+JDBCDataMapper.objectToSQL(getInfoLinea().getCommessa())+
                  " \n and b.data_spedizione=CONVERT(datetime,'"+getInfoLinea().getDataCommessa()+"',112)"+
                  " and a.STATO='TERMINATO' ";
                  
      
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

  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvanProdSagomati.class);
}
