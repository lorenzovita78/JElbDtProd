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
import colombini.query.datiComm.carico.QryNumPezziCommessaStd;
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
public class DtLineaTavoli implements IDatiCaricoLineaComm {

  
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    QryNumPezziCommessaStd  qry=new QryNumPezziCommessaStd();
    qry.setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA, " AND ( (CLLINP>='06141' AND  CLLINP<='06153') OR CLLINP='06040' OR CLLINP='06042' OR CLLINP='06073') " );
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    
    List<CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
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
    
    
    StringBuilder qry=new StringBuilder(
         " select qtaTot from DesmosFebal.dbo.LPM39_PDF_LISTA_TAVOLI_SCRIVANIE " +
         "\n where 1=1 ").append(
         "\n and DesmosCommessa=  ").append(JDBCDataMapper.objectToSQL(ll.getCommessa()) ).append(
         "\n and Data = ").append(JDBCDataMapper.objectToSQL(ll.getDataCommessa()) );
         
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
  
 
  private static final Logger _logger = Logger.getLogger(DtLineaTavoli.class);   
}
