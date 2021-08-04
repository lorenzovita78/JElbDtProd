/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.avanzamento.R1P4;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.avanzamento.R1P3.AvProdLineeOnTAP;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class AvProdLotto1 extends AvProdLineeOnTAP  {

  
   @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_LOTTO1R1P4_EDPC;  //To change body of generated methods, choose Tools | Templates.
  }
  
  
//  @Override
//  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
//    Connection conSqlS=null;
//    List pzComm=new ArrayList();
//    try{
//      conSqlS=ColombiniConnections.getDbLotto1Connection();
//      ResultSetHelper.fillListList(conSqlS, getQueryProdGg(), pzComm);
//      storeDatiProdComm(conAs400, pzComm);
//      
//    }catch(SQLException s){
//     _logger.error("Errore in fase di interrogazione del db Sirio Lotto 1 -->"+s.getMessage());
//     throw new DatiCommLineeException(s);
//    } catch (ParseException ex) {
//      _logger.error("Errore in fase di conversione dei dati per Sirio Lotto 1 -->"+ex.getMessage());
//     throw new DatiCommLineeException(ex);
//    }finally{
//      if(conSqlS!=null)
//        try{
//          conSqlS.close();
//        }catch(SQLException s){
//          _logger.error("Errore in fase di chiusura della connessione al db Sirio Lotto1 "+s.getMessage());
//        }
//    }    
//  }
//
//  @Override
//  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
//     return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
//                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
//  }
//  
//  
//  
//  private String getQueryProdGg() throws ParseException{
//    String campiConv=" case when(len([Commessa])>3) then SUBSTRING(commessa,5,3) else Commessa end " +
//                     "   , convert(varchar(8),[DataCommessa],112) ";
//
//    String dataIniStr=DateUtils.DateToStr(DateUtils.getInizioGg(this.getDataRifCalc()),"yyyy-MM-dd HH:mm:ss");
//    String dataFinStr=DateUtils.DateToStr(DateUtils.getFineGg(this.getDataRifCalc()),"yyyy-MM-dd HH:mm:ss");
//    
//    String sql=" SELECT "+campiConv+ " ,count(*) \n"
//            + " FROM [dbo].[LOG_PRODUZIONE]"
//            + "where 1=1 "
//            +" and Commessa <>''"
//            +" and Commessa not like 'SCHN%'"
//            +" and DatOraInsRec>=CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dataIniStr)+", 120 )"
//            +" and DatOraInsRec<=CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dataFinStr)+", 120 )"
//            +" group by "+campiConv;
//            
//    return sql;        
//  }
// 
//  
  
  private static final Logger _logger = Logger.getLogger(AvProdLotto1.class);

 
}
