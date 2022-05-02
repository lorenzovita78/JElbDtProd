/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P1;

import colombini.conn.ColombiniConnections;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.avanzamento.QueryProdCommAvzVDL;
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
public class AvProdFiancheMaw2 extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
     _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
     
     Connection conSqlS=null;
    List pzComm=new ArrayList();
    try{
      conSqlS=ColombiniConnections.getDbDesmosColProdConnection();
      

      
      QueryProdCommAvzVDL qry=new QueryProdCommAvzVDL();
      String di=DateUtils.DateToStr(DateUtils.getInizioGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");
      String df=DateUtils.DateToStr(DateUtils.getFineGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");  
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, di);
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA, df);
     // qry.setFilter(FilterFieldCostantXDtProd.FT_LINEA, lineeLogiche.toString());
      
      ResultSetHelper.fillListList(conSqlS, qry.toSQLString(), pzComm);
      storeDatiProdComm(conAs400, pzComm);
      
    }catch(SQLException s){
     _logger.error("Errore in fase di interrogazione del DB avanzamentoVDL -->"+s.getMessage());
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
          _logger.error("Errore in fase di chiusura della connessione al db avanzamentoVDL "+s.getMessage());
        }
    }   

  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400,getInfoLinea().getAzienda(), getInfoLinea().getCommessa(), 
                         getInfoLinea().getDataCommessa(),getInfoLinea().getCodLineaLav());
  }
 
  

  
  
  private static final Logger _logger = Logger.getLogger(AvProdFiancheMaw2.class);
}
