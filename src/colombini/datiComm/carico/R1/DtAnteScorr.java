/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.carico.CaricoComOnTAP;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QueryProdComFromTAP;
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
public class DtAnteScorr extends CaricoComOnTAP {

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_ANTESCORR_EDPC+","+TAPWebCostant.CDL_ANTESSPEC_EDPC+","+TAPWebCostant.CDL_ANTEQUADR_EDPC; //To change body of generated methods, choose Tools | Templates.
  }
  
  private List<CaricoCommLineaBean> getDatiCommDettLocal(LineaLavBean ll, Connection con) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    List<List> lDiv=new ArrayList();
    try{
      QueryProdComFromTAP q=new QueryProdComFromTAP();
      q.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
      q.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
      q.setFilter(FilterFieldCostantXDtProd.FT_DATA, ll.getDataCommessa());
      if(LineaLavBean.UMPEZZI.equals(ll.getUnitaMisura()) )
        q.setFilter(FilterFieldCostantXDtProd.FD_NUMPEZZI, "Y");      
      
      
      q.setFilter(FilterFieldCostantXDtProd.FT_LINEE, "["+getCodLineaOnTAP()+"]");
      
      ResultSetHelper.fillListList(con, q.toSQLString(), lDiv);
      
      
      for(List rec:lDiv){
        String div=ClassMapper.classToString(rec.get(0));
        Double val=ClassMapper.classToClass(rec.get(1),Double.class);
        
        DatiCommUtils.getInstance().addInfoCaricoComBu(beans,ll.getCodLineaLav(),ll.getDataCommessa(),
                ll.getCommessa(), ll.getUnitaMisura(), div , val);
      }
      
    }catch(SQLException s){
      _logger.error(" Errore in fase di accesso al db "+s.getMessage());
      throw new DatiCommLineeException(s);
    } catch (QueryException ex) {
      _logger.error(" Errore in fase di esecuzione della query "+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }
    return beans;
  }
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    return getDatiCommDettLocal(ll, con);
  
  
  }
  
  
  private static final Logger _logger = Logger.getLogger(CaricoComOnTAP.class);   
}
