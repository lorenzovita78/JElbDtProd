/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.carico;

import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryCaricoComFromTAP;
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
public abstract class CaricoComOnTAP implements IDatiCaricoLineaComm {

  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=null;
    if(getUniqueCodDivi()==null)
      beans=getDatiCommDett(ll, con);
    else
      beans=getDatiCommDivi(ll, con);
    
    return beans;
  }

  private List<CaricoCommLineaBean> getDatiCommDett(LineaLavBean ll, Connection con) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    List<List> lDiv=new ArrayList();
    try{
      QueryProdComFromTAP q=new QueryProdComFromTAP();
      q.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
      q.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
      q.setFilter(FilterFieldCostantXDtProd.FT_DATA, ll.getDataCommessa());
      q.setFilter(FilterFieldCostantXDtProd.FT_LINEA, getCodLineaOnTAP());
      if(LineaLavBean.UMPEZZI.equals(ll.getUnitaMisura()) )
        q.setFilter(FilterFieldCostantXDtProd.FD_NUMPEZZI, "Y");      
      
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
  
  private List<CaricoCommLineaBean> getDatiCommDivi(LineaLavBean ll, Connection con) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    Double value=null;
    
    try{
      QryCaricoComFromTAP q=new QryCaricoComFromTAP();
      q.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
      q.setFilter(FilterFieldCostantXDtProd.FT_LINEA, getCodLineaOnTAP());
      q.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
      q.setFilter(FilterFieldCostantXDtProd.FT_DATA, ll.getDataCommessa());
      
      
      Object[] obj=ResultSetHelper.SingleRowSelect(con, q.toSQLString());
      
      
      if(obj!=null && obj[0]!=null){
        value=ClassMapper.classToClass(obj[0], Double.class);
      
        
        DatiCommUtils.getInstance().addInfoCaricoComBu(beans,ll.getCodLineaLav(),ll.getDataCommessa(),
                ll.getCommessa(), ll.getUnitaMisura(), getUniqueCodDivi() , value);
      
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
  
   public abstract String getCodLineaOnTAP();
  
   /**
    * Metodo da reimplementare nel caso in cui abbiamo una sola BU legata alla linea
    * @return 
    */
   public String getUniqueCodDivi(){
     return null;
   }
  
   private static final Logger _logger = Logger.getLogger(CaricoComOnTAP.class);   
}
