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
import colombini.query.datiComm.carico.QryColliVolumiCommessa;
import colombini.util.DatiCommUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class DtImballoFebalOLD implements IDatiCaricoLineaComm {

   //parametro da elminare
  
  //linea imballo Febal
  public final static String CDLIMBFEBAL="06516";
  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    QryColliVolumiCommessa qry=new QryColliVolumiCommessa();
    qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, " AND ( CLLINP in ( 6516 , 6517, 6518, 36017, 36201, 36035 ) ) " );
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    qry.setFilter(FilterFieldCostantXDtProd.FT_BU_DIVERSO, CostantsColomb.BUIDEA);
    
    List<CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMCOLLI);
    try{
      Double nCl=getNumColliFebal(ll);
      DatiCommUtils.getInstance().addInfoCaricoComBu(list,ll.getCodLineaLav(),ll.getDataCommessa(),
              ll.getCommessa(),ll.getUnitaMisura(),CostantsColomb.BUFEBAL, nCl);
    
    }catch (SQLException s){
      throw new DatiCommLineeException("Impossibile caricare i dati relativi ai colli Febal da produrre -->"+s.getMessage());
    }
    
    return list;
  }
  
  
  
  public Double getNumColliFebal(LineaLavBean ll) throws SQLException{
    Double nc=Double.valueOf(0);
    Connection con=null;
    
    StringBuilder qry=new StringBuilder(" select count(*) as ncolli from (  ").append(
                      "\n SELECT codice_collo  ").append(
                        "\n FROM DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL ").append(
              "\n where commessa = ").append(JDBCDataMapper.objectToSQL(ll.getCommessa()) ).append(
              "\n and   dataSpedizione = ").append(JDBCDataMapper.objectToSQL(ll.getDataCommessa()) ).append(
              "\n and linea in ('06516','06517','06518','36017','36201','36035','36200', '36151', '36202') ").append(
              "\n group by codice_collo ) a ");
    try {
      _logger.info("Esecuzione query: "+qry.toString());
      con=ColombiniConnections.getDbDesmosFebalProdConnection();
      
      Object[] o=ResultSetHelper.SingleRowSelect(con, qry.toString());
      if(o!=null && o.length>0){
        nc=ClassMapper.classToClass(o[0], Double.class);
      }
        
    }finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
       _logger.error("Impossibile rilasciare la connessione con DBDESMOSFEBALPROD : "+ex.getMessage());
      }
    }
    
    return nc;
  }
  
 
  private static final Logger _logger = Logger.getLogger(DtImballoFebalOLD.class);   
}
