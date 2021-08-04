/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;

import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryCaricoComFromTAP;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public abstract class CaricoCommFromTAP extends CaricoComLineaSuppl {

  @Override
  protected Integer getPzComm(Connection con) throws DatiCommLineeException {
    Integer pzComm=null;
    try{
      Object [] obj=null;
      QryCaricoComFromTAP qry=new QryCaricoComFromTAP();
      qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, infoLinea.getAzienda());
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEA, getCodiceLineaForTAP());
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, infoLinea.getCommessa());
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, infoLinea.getDataCommessa());

      obj=ResultSetHelper.SingleRowSelect(con,qry.toSQLString());

      if(obj!=null && obj.length>0){
        pzComm=ClassMapper.classToClass(obj[0],Integer.class);
      }  
    } catch(QueryException e){
      _logger.error("Problemi nell'esecuzione della query "+e.getMessage());
      throw new DatiCommLineeException(e);
    } catch(SQLException e ){
      _logger.error("Problemi di accesso al database "+e.getMessage());
      throw new DatiCommLineeException(e);
    }
    
    return pzComm;
    
  }
  
  
  public abstract String getCodiceLineaForTAP();
  
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(CaricoCommFromTAP.class);
}
