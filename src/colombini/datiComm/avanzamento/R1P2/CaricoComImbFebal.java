/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.conn.ColombiniConnections;
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
public class CaricoComImbFebal extends CaricoComLineaSuppl{
  
  
  
  @Override
  protected Integer getPzComm(Connection conAs400) throws DatiCommLineeException {
    Double totPz=Double.valueOf(0);
    String stm="select count(distinct ColloID) NPZCOM \n" +
                "FROM [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail]\n" +
                "inner join (select MAX(SYSTEMDATE) dat from [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Detail] group by ColloID) as DetMax on detMax.dat = SYSTEMDATE \n" +
                "where 1=1 and CommissionId=? and TruckloadDate= ? and ProductionLine in ('06516','06517','06518','36017','36201','36035','36200', '36151', '36202')";
    if(AvProdImbMontFebal.LINEAFEBALIMBALLOFEB.equals(infoLinea.getCodLineaLav())){
      stm+=" and ClientId in (?,?,?)";
    }else{
      stm+=" and ClientId not in (?,?,?)";
    }
    PreparedStatement pstmt; 
    try {
      Connection conSqlS=null;   
      conSqlS=ColombiniConnections.getDbAvanzamentoProdConnection();  
      pstmt = conSqlS.prepareStatement(stm);
      pstmt.setInt(1, infoLinea.getCommessa());
      pstmt.setString(2, infoLinea.getDataCommessa().toString());
      pstmt.setString(3, "B");
      pstmt.setString(4, "D");
      pstmt.setString(5, "N");


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
  
  
  
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricoComImbFebal.class);
}
