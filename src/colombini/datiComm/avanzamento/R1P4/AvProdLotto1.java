/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.avanzamento.R1P4;

import colombini.conn.ColombiniConnections;
import colombini.costant.TAPWebCostant;
import colombini.datiComm.avanzamento.R1P3.AvProdLineeOnTAP;
import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.avanzamento.QueryProdCommP4AvzProd;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class AvProdLotto1 extends AvProdLineeOnTAP  {

  
//   @Override
//  public String getCodLineaOnTAP() {
//    return TAPWebCostant.CDL_LOTTO1R1P4_EDPC;  //To change body of generated methods, choose Tools | Templates.
//  }
   public final static String CENTRO="01084";  
    
   @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_LOTTO1R1P4_EDPC;  //To change body of generated methods, choose Tools | Templates.
  }
  
  
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    Connection conSqlS=null;
    List pzComm=new ArrayList();
    try{
      conSqlS=ColombiniConnections.getDbAvanzamentoProdConnection();
      
      QueryProdCommP4AvzProd qry=new QueryProdCommP4AvzProd();
      String di=DateUtils.DateToStr(DateUtils.getInizioGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");
      String df=DateUtils.DateToStr(DateUtils.getFineGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");  
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, di);
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA, df);
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEA,CENTRO);
      ResultSetHelper.fillListList(conSqlS, qry.toSQLString(), pzComm);
      storeDatiProdComm(conAs400, pzComm);
      
    }catch(SQLException s){
     _logger.error("Errore in fase di interrogazione del db avanzamentoProd -->"+s.getMessage());
     throw new DatiCommLineeException(s);
     
    }
     catch (ParseException ex) {
      _logger.error("Problemi di conversione della data riferimento calcolo-->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }
    catch (QueryException ex) {
       _logger.error("Errore in fase di esecuzione query --> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
    }
        finally{
      if(conSqlS!=null)
        try{
          conSqlS.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione al db avanzamentoProd "+s.getMessage());
        }
    }    
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
     return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
  }
  
  
  
//  private String getQueryProdGg() throws ParseException{
//    String campiConv=" case when(len([Commessa])>3) then SUBSTRING(commessa,5,3) else Commessa end " +
//                     "   , convert(varchar(8),[DataCommessa],112) ";
//
//   // String dataIniStr=DateUtils.DateToStr(DateUtils.getInizioGg(this.getDataRifCalc()),"yyyy-MM-dd HH:mm:ss");
//   // String dataFinStr=DateUtils.DateToStr(DateUtils.getFineGg(this.getDataRifCalc()),"yyyy-MM-dd HH:mm:ss");
//    
//    String sql=" SELECT "+campiConv+ " ,count(*) \n"
//            + " [dbo].[TBL_Avanzamento_Dett] \n" 
//            + "  inner join [AvanzamentoProd].[dbo].[TBL_DatiProduzione] on barcode=partnumber"
//            + "where 1=1 "
//            +" and Commessa <>''"
//            +" and centro='"+CENTRO+"'"
//       //     +" and DatOraInsRec>=CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dataIniStr)+", 120 )"
//       //     +" and DatOraInsRec<=CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dataFinStr)+", 120 )"
//            +" group by "+campiConv;
//            
//    return sql;        
//  }
 
  
  
  private static final Logger _logger = Logger.getLogger(AvProdCombicut.class);


 
}
