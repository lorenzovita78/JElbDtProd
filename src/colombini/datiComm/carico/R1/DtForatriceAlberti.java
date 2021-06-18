/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class DtForatriceAlberti  extends DtFromDesmos  {

  
  @Override
  public List loadDatiFromDesmos(Integer commessa,String lancio) throws SQLException{
    List list=new ArrayList();
    Connection con=null;
    
    Integer int1=Integer.valueOf(0);
    
    String qry=" select bu ,sum(cassetti) "
             +" from DesmosCOLOMBINI.dbo.LPM_Conteggio_Cassetti_BU_PDF_"
             +" where lancio="+JDBCDataMapper.objectToSQL(lancio) 
             +" and desmosLancio="+JDBCDataMapper.objectToSQL(commessa)
            //pezza per escludere i pezzi di Morolli altrimenti sarebbero conteggiati 2 volte
             +"\n and SUBSTRING(NomeLista,LEN(NomeLista)-1,2)<>'SI' " 
             +" and SUBSTRING(NomeLista,LEN(NomeLista)-1,2)<>'NO' " 
            //
            +" group by bu";
    
    try {
      _logger.info("Esecuzione query: "+qry);
      
      con=ColombiniConnections.getDbDesmosColProdConnection();
      ResultSetHelper.fillListList(con, qry, list);
      
    }finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
       _logger.error("Impossibile rilasciare la connessione con METRONSQL : "+ex.getMessage());
      }
    }
    
    
    return list;
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(DtForatriceAlberti.class);

  
}
