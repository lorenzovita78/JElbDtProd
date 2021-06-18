/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1;

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
public class CaricoComImpIma extends CaricoComLineaSuppl{

  @Override
  protected Integer getPzComm(Connection conAs400) throws DatiCommLineeException {
    Connection con=null;
    Integer pz=Integer.valueOf(0);
    String qry="SELECT COUNT(*) FROM dbo.Orders where CommissionNo =";
    String commString="";
    
    Integer comm=infoLinea.getCommessa();
    if(isImaAnteClienti())        
      comm+=400;
    
    commString=comm.toString();
    
    //lo zero davanti alle commesse con due cifre decimali vale solo per la Ima Ante 
    //non per la Ima Top 
    if(comm.toString().length()<=2 && isImaAnte())
      commString="0"+comm.toString();
    
    qry+=JDBCDataMapper.objectToSQL(commString);
    
    
    try {
      con = getDbOrdersConnection();
    
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry);
      if(obj[0]!=null)
        pz=ClassMapper.classToClass(obj[0], Integer.class);
    
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con il server -->"+ex.getMessage());
      throw new DatiCommLineeException("Errore in fase di connessione con il database");
    } finally{
      if(con!=null)
        try {
          con.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di rilascio della connessione -->"+ex.getMessage());
      }
      
    }
    
    return pz;
  }
  
  
  private Connection getDbOrdersConnection() throws SQLException{
    Connection con=null;
    if(isImaAnte()){
      con=ColombiniConnections.getDbImaAnteOrdersConnection();
    }else {
      con=ColombiniConnections.getDbImaTopOrdersConnection();
    
    } 
   
    return con;
  }
  
  
  
  public Boolean isImaAnte(){
    Boolean bool=Boolean.FALSE;
    if(infoLinea.getCodLineaLav().contains("01008")){
      bool=Boolean.TRUE;
    }
    
    return bool;
  }
  
  public Boolean isImaAnteClienti(){
    Boolean bool=Boolean.FALSE;
    if(infoLinea.getCodLineaLav().equals("01008C")){
      bool=Boolean.TRUE;
    }
    
    return bool;
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricoComLineaSuppl.class);
}
