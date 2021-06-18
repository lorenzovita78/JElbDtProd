/*
 * To change this template, choose Tools | Templates
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
import colombini.query.datiComm.carico.R1.QryNPziRepComm;
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
public class DtRepComm implements IDatiCaricoLineaComm{

  
  public DtRepComm(){
    
  }
  
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    
    QryNPziRepComm qry=new QryNPziRepComm();
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    List<CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMPEZZI);
    try{
      Double nPz=getNumPzFebal(ll);
      DatiCommUtils.getInstance().addInfoCaricoComBu(list,ll.getCodLineaLav(),ll.getDataCommessa(),
              ll.getCommessa(),ll.getUnitaMisura(),CostantsColomb.BUFEBAL, nPz);
    
    }catch (SQLException s){
      throw new DatiCommLineeException("Impossibile caricare i dati relativi ai colli Febal da produrre -->"+s.getMessage());
    }
    return list;
    
  }
   
  public Double getNumPzFebal(LineaLavBean ll) throws SQLException{
    Double nc=Double.valueOf(0);
    Connection con=null;
    
    String qry=" select  SUM(qta) as npz   \n from DesmosFebal.[dbo].[LDF_TXT_FILE_PER_INCAS_NEW_CAMPIONI] \n "
            + " where commessa = " + JDBCDataMapper.objectToSQL(ll.getCommessa());
              
    try {
      _logger.info("Esecuzione query: "+qry);
      con=ColombiniConnections.getDbDesmosFebalProdConnection();
      
      Object[] o=ResultSetHelper.SingleRowSelect(con, qry);
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
  
  
  private static final Logger _logger = Logger.getLogger(DtRepComm.class);   
  
//  /**
//   * Torna una lista statica di articoli da escludere in quanto non relativi al piano 3 ma agli altri piani
//   * modifica del 12/12/2012
//   * @return List articoli da escludere
//   */
//  private List<String> getArticoliNotInclude(){
//    List articoli=new ArrayList();
//    
//    articoli.add("040052");
//    articoli.add("040054");
//    articoli.add("040055");
//    articoli.add("040366");
//    articoli.add("040381");
//    articoli.add("040394");
//    articoli.add("040396");
//    articoli.add("040449");
//    articoli.add("040477");
//    articoli.add("040478");
//    articoli.add("040486");
//    articoli.add("040489");
//    articoli.add("040494");
//    articoli.add("091861-X45Y");
//    articoli.add("091861-X54X");
//    articoli.add("091861-X55A");
//    articoli.add("091861-X56D");
//    articoli.add("091861-X57G");
//    articoli.add("091861-X59P");
//    articoli.add("091861-X60M");
//    articoli.add("091861-X62T");
//    articoli.add("091861-Y55B");
//    articoli.add("091861-Y60N");
//    articoli.add("091861-Y61R");
//    articoli.add("091861-Y62U");
//    articoli.add("091861-Y63X");
//    articoli.add("091861-Y64A");
//    articoli.add("091920");
//    articoli.add("C00200");
//    articoli.add("C00201");
//    articoli.add("PR0573");
//    articoli.add("PR0631");
//    articoli.add("SP0960");
//    articoli.add("SP0961");
//    articoli.add("SP0986");
//    articoli.add("SP0987");
//    articoli.add("SP1090");
//    articoli.add("SP1091");
//    articoli.add("SP1290");
//    articoli.add("SP1291");
//    articoli.add("SP1292");
//    articoli.add("SP1293");
//    articoli.add("SP1395");
//    articoli.add("SP1396");
//    articoli.add("SP1572");
//    articoli.add("SP1953");
//    articoli.add("SP1954");
//    articoli.add("Y00000");
//    articoli.add("Y00001");
//
//    
//    return articoli;
//  }
  
  
//  private String getStringArticoliNotInclude(){
//    StringBuilder stringQry=new StringBuilder("");
//    List<String> articoli=getArticoliNotInclude();
//    for(String articolo:articoli){
//      if(stringQry.length()==0){
//        stringQry.append("'").append(articolo).append("'");
//      }else{
//        stringQry.append(" , '").append(articolo).append("'");
//      }
//    }
//    
//    return stringQry.toString();
//  }
  
  
}
