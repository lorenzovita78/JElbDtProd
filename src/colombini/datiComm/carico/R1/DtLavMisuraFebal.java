/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.carico.R1;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.R1.QryPzProdLineaSuMisuraFebal;
import colombini.util.DatiCommUtils;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class DtLavMisuraFebal implements IDatiCaricoLineaComm {

  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List l=new ArrayList();
    
    Double numPz=getPzToProduce(ll);
    
    if(numPz!=null && numPz>0){
      CaricoCommLineaBean bean=DatiCommUtils.getInstance().getBeanCaricoComLinea(ll.getCodLineaLav(),ll.getDataCommessa(),ll.getCommessa(),ll.getUnitaMisura(), CostantsColomb.BUFEBAL,  numPz);
//      new CaricoCommLineaBean();
//      bean.setCommessa(ll.getCommessa());
//      bean.setLineaLav(ll.getCodLineaLav());
//      bean.setDataRifN(ll.getDataCommessa());
//      bean.setUnitaMisura(ll.getUnitaMisura());
//      bean.setDivisione("FB1");
//      bean.setValore(numPz);
      l.add(bean);
    }  
    
    return l;
  }
  
  public Double getPzToProduce(LineaLavBean ll) throws DatiCommLineeException{
    Double d=Double.valueOf(0);
    Connection con=null;
    try{
      con=ColombiniConnections.getDbDesmosFebalProdConnection();
      QryPzProdLineaSuMisuraFebal qry=new QryPzProdLineaSuMisuraFebal();
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, ll.getDataCommessa());
      String s=qry.toSQLString();
      _logger.info(s);
      Object [] obj=ResultSetHelper.SingleRowSelect(con, s);
      if(obj!=null && obj.length>0)
        d=ClassMapper.classToClass(obj[0], Double.class);
      
      
    } catch (SQLException s){
     _logger.error("Errore in fase di interrogazione del db DesmosFebal -->"+s.toString());
     throw new DatiCommLineeException("Impossibile interrogare il db DesmosFebal -->"+s.toString());
    } catch (QueryException s){
     _logger.error("Errore in fase di esecuzione della query per estrazione pz Linea Lav Misura Febal -->"+s.toString());
     throw new DatiCommLineeException("Errore in fase di esecuzione della query per estrazione pz Linea Lav Misura Febal -->"+s.toString());   
    }
    
    return d;
  }
 
  
  
  private static final Logger _logger = Logger.getLogger(DtLavMisuraFebal.class); 
  
}
