/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.costant.TAPWebCostant;
import colombini.datiComm.carico.CaricoComOnTAP;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.util.DatiCommUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class DtAnteRemArtec extends CaricoComOnTAP {

  

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_ANTEREM_EDPC;
  }

//  @Override
//  public String getUniqueCodDivi() {
//    return CostantsColomb.BUARTEC; //To change body of generated methods, choose Tools | Templates.
//  }

  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List <CaricoCommLineaBean> beans=new ArrayList();
    List<List> lDiv=new ArrayList();
    
    try{
      ResultSetHelper.fillListList(con, getQryPzBU(ll), lDiv);
      
      
      for(List rec:lDiv){
        String div=ClassMapper.classToString(rec.get(0));
        Double val=ClassMapper.classToClass(rec.get(1),Double.class);
        if(!StringUtils.IsEmpty(div))
          DatiCommUtils.getInstance().addInfoCaricoComBu(beans,ll.getCodLineaLav(),ll.getDataCommessa(),
                ll.getCommessa(), ll.getUnitaMisura(), div , val);
      }
      
    }catch(SQLException s){
      _logger.error(" Errore in fase di accesso al db "+s.getMessage());
      throw new DatiCommLineeException(s);
    }
    
    return beans;
  
  
  }

  
  private String getQryPzBU(LineaLavBean llb){
    StringBuilder sq=new StringBuilder();
    
    sq.append("SELECT bu,count(*) FROM (\n" +
              " SELECT CASE WHEN tincol<40000 THEN 'A04' ELSE  \n" +
              "        CASE WHEN tincol<50000 THEN 'A03' ELSE \n" +
               "       CASE WHEN tincol<60000 THEN 'FB1' ELSE '' END END END bu\n" +
            "   FROM mcobmoddta.ZTAPCI z \n" ).append(
            " WHERE 1=1");
    
    sq.append(" AND TICONO = ").append(JDBCDataMapper.objectToSQL(FilterFieldCostantXDtProd.AZCOLOMBINI));
    sq.append(" AND TICOMM = ").append(JDBCDataMapper.objectToSQL(llb.getCommessa()));
    sq.append(" AND TIPLGR = ").append(JDBCDataMapper.objectToSQL(getCodLineaOnTAP()));
    sq.append(" and TIDTCO = ").append(JDBCDataMapper.objectToSQL(llb.getDataCommessa()));
    
    
    sq.append(" ) A \n GROUP BY bu");
    
    return sq.toString();
   }       
  
  
  
  
  private static final Logger _logger = Logger.getLogger(DtAnteRemArtec.class);   
}
