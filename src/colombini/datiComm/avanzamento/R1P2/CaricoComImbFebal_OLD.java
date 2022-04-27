/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.CostantsColomb;
import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.datiComm.carico.R1.DtImballoFebal;
import colombini.exception.DatiCommLineeException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author lvita
 */
public class CaricoComImbFebal_OLD extends CaricoComLineaSuppl{
  
  
  
  @Override
  protected Integer getPzComm(Connection conAs400) throws DatiCommLineeException {
    Double totPz=Double.valueOf(0);
    String stm="SELECT SUM(ZDVALE) NPZCOM FROM MCOBMODDTA.ZDPCOM WHERE 1=1 and ZDUNME<>'V3' and ZDCONO=? and ZDCOMM = ? and ZDDTCO=? and ZDPLGR=?";
    if(AvProdImbMontFebal.LINEAFEBALIMBALLOFEB.equals(infoLinea.getCodLineaLav())){
      stm+=" and ZDDIVI = ?";
    }else{
      stm+=" and ZDDIVI <> ?";
    }
    PreparedStatement pstmt; 
    try {
      pstmt = conAs400.prepareStatement(stm);
      pstmt.setInt(1, infoLinea.getAzienda());
      pstmt.setInt(2, infoLinea.getCommessa());
      pstmt.setLong(3, infoLinea.getDataCommessa());
      pstmt.setString(4, DtImballoFebal.CDLIMBFEBAL);
      pstmt.setString(5, CostantsColomb.BUFEBAL);

      ResultSet rs=pstmt.executeQuery();
      
      
      while(rs.next()){
        totPz=rs.getDouble("NPZCOM");
      }
      
    } catch (SQLException ex) {
      _logger.error(" Errore in fase di interrogazione dei pz da produrre-->"+ex.toString());
      throw new DatiCommLineeException("Impossibile trovare i pz da produrre per la commessa"+infoLinea.getCommessaString()
              +"-->"+ex.toString());
    }
   
    
    
    return totPz.intValue();
  }
  
  
  
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricoComImbFebal_OLD.class);
}
