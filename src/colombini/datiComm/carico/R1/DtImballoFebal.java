/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.carico.R1;

import colombini.conn.ColombiniConnections;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryColliCaricoVDL;
import colombini.util.DatiCommUtils;
import db.JDBCDataMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public  class DtImballoFebal implements IDatiCaricoLineaComm {
  //linea imballo Febal
  public final static String CDLIMBFEBAL="06516";
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
      
   Connection conSqlS=null;   
   try{
    conSqlS=ColombiniConnections.getDbAvanzamentoProdConnection();  
   
    QryColliCaricoVDL  qry=new QryColliCaricoVDL();
    //qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, "  and cltpus= "+JDBCDataMapper.objectToSQL(getCondLinea(ll)) );
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, "and ProductionLine in ('06516','06517','06518','36017','36201','36035','36200', '36151', '36202')" );
   
    
    List<CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(conSqlS, qry, ll,FilterFieldCostantXDtProd.FD_NUMCOLLI);
    return list;
            
   }catch(SQLException s){
     throw new DatiCommLineeException(s);
    }
   
   finally{
      if(conSqlS!=null)
        try{
          conSqlS.close();
        }catch(SQLException s){
        }
    }    
  }
        

  
  private String getCondLinea(LineaLavBean ll){
    if("01051".equals(ll.getCodLineaLav()) ){
      return "P";
    } else if("01053".equals(ll.getCodLineaLav()) ) {
      return "W";
    } else if("01052".equals(ll.getCodLineaLav()) ) {
      return "C";
    } 
    return "";
  }
  
 
  private static final Logger _logger = Logger.getLogger(DtImballoFebal.class);   
}
