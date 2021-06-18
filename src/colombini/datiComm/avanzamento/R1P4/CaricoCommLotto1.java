/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.avanzamento.R1P4;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.exception.DatiCommLineeException;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class CaricoCommLotto1 extends CaricoComLineaSuppl{

//  @Override
//  public String getCodLineaOnTAP() {
//    return TAPWebCostant.CDL_LOTTO1R1P4_EDPC; //To change body of generated methods, choose Tools | Templates.
//  }
  
  
  @Override
  protected Integer getPzComm(Connection con) throws DatiCommLineeException {
    Integer pz=Integer.valueOf(0);
    try{
      
      Object[] obj=ResultSetHelper.SingleRowSelect(con, getSelect());
      if(obj!=null && obj.length>0){
        pz=ClassMapper.classToClass(obj[0],Integer.class);
      }
    }catch(SQLException s){
     _logger.error("Errore in fase di interrogazione dei dati  per  Lotto 1 -->"+s.getMessage());
     throw new DatiCommLineeException(s);
    
    }
    return pz;
  }
 
//  private String getSelect(){
//    Date dataComm = DateUtils.strToDate(this.infoLinea.getDataCommessa().toString(), "yyyyMMdd" );
//    String commS=this.infoLinea.getCommessa().toString();
//    
//    String lancioFeb=DesmosUtils.getInstance().getLancioDesmosFebal(Long.valueOf(commS),dataComm );
//    
//    String sql=" Select count(*) from dbo.LisPan where 1=1"+
//               " and commessa in ( "+JDBCDataMapper.objectToSQL(commS)+" , "+JDBCDataMapper.objectToSQL(lancioFeb)+" )"+
//               " and DataCommessa =CONVERT (date,"+JDBCDataMapper.objectToSQL(this.infoLinea.getDataCommessa().toString())+",103)";
//                       
//    return sql;                   
//  }
  
  
  private String getSelect(){
    
    String sql=" Select count(*) from mcobmoddta.ztapci where 1=1 "+
               " and TIPLGR = "+JDBCDataMapper.objectToSQL(TAPWebCostant.CDL_LOTTO1R1P4_EDPC)+
               " and TICOMM = "+JDBCDataMapper.objectToSQL(this.infoLinea.getCommessa())+
               " and TIDTCO = "+JDBCDataMapper.objectToSQL(this.infoLinea.getDataCommessa());
                       
    return sql;                   
  }
  
  private static final Logger _logger = Logger.getLogger(CaricoCommLotto1.class);

  
}
