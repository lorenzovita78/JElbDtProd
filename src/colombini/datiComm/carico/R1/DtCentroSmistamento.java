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
import colombini.util.DesmosUtils;
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
public  class DtCentroSmistamento implements IDatiCaricoLineaComm {

  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    QryColliVolumiCommessa  qry=new QryColliVolumiCommessa();
    qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, "  and cltpus= "+JDBCDataMapper.objectToSQL(getCondLinea(ll)) );
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    
    List<CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMCOLLI);
    try{
      Double nCl=getNumPzFebal(ll);
      DatiCommUtils.getInstance().addInfoCaricoComBu(list,ll.getCodLineaLav(),ll.getDataCommessa(),
              ll.getCommessa(),ll.getUnitaMisura(),CostantsColomb.BUFEBAL, nCl);
    
    }catch (SQLException s){
      throw new DatiCommLineeException("Impossibile caricare i dati relativi ai colli Febal da produrre -->"+s.getMessage());
    }
    return list;
  }
  
  
  
  public Double getNumPzFebal(LineaLavBean ll) throws SQLException{
    Double np=Double.valueOf(0);
    Connection con=null;
    
    String lancio=DesmosUtils.getInstance().getLancioDesmosFebal(new Long(ll.getCommessa()), ll.getDateCommessa());
    
    //String lancio=ll.getDataCommessa().toString();
    //lancio=lancio.substring(0,4);
    //lancio=lancio+ll.getCommessaString();
    
    StringBuilder qry=new StringBuilder(
         " SELECT count(*)   FROM [TesyFactory_FEBAL_PROD].[dbo].[LYPTRN] " +
         "\n where 1=1 ").append(
         "\n and [£RPROJ]  =  ").append(JDBCDataMapper.objectToSQL(lancio)).append(
         " and cast(SUBSTRING([£RDLMO],4,5) as bigint) >=50000 ").append(
         " and SUBSTRING([£RDLMO],13,2)=").append(JDBCDataMapper.objectToSQL(getCondLinea(ll)));
         
    try {
      con=ColombiniConnections.getDbDesmosFebalProdConnection();
      String s=qry.toString();
      _logger.info("Esecuzione query: "+s);
      
      Object[] o=ResultSetHelper.SingleRowSelect(con, s);
      if(o!=null && o.length>0){
        np=ClassMapper.classToClass(o[0], Double.class);
      }
        
    }finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
       _logger.error("Impossibile rilasciare la connessione con DBDESMOSFEBALPROD : "+ex.getMessage());
      }
    }
    
    return np;
  }
  
  
  private String getCondLinea(LineaLavBean ll){
    if("01051".equals(ll.getCodLineaLav()) ){
      return "C1";
    } else if("01053".equals(ll.getCodLineaLav()) ) {
      return "VL";
    } else if("01052".equals(ll.getCodLineaLav()) ) {
      return "C3";
    } 
    return "";
  }
  
 
  private static final Logger _logger = Logger.getLogger(DtCentroSmistamento.class);   
}
