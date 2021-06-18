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
import colombini.query.datiComm.carico.R1.QryNColliImbArtec;
import colombini.util.DatiCommUtils;
import java.sql.Connection;
import java.util.List;
import org.apache.log4j.Logger;


/**
 *
 * @author lvita
 */
public class DtMontImbArtec extends CaricoComOnTAP //implements IDatiCaricoLineaComm {
  {  

  public List<CaricoCommLineaBean> getDatiCommessaExt(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    QryNColliImbArtec qry=new QryNColliImbArtec();
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, ll.getCommessa());
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(ll.getDataCommessa()));
    
    List  <CaricoCommLineaBean> list=DatiCommUtils.getInstance().getListDtCommFromQuery(con, qry, ll,FilterFieldCostantXDtProd.FD_NUMCOLLI);
    
//    try{
//      Double nCl=getNumPzFebal(ll);
//      DatiCommUtils.getInstance().addInfoCaricoComBu(list,ll.getCodLineaLav(),ll.getDataCommessa(),
//              ll.getCommessa(),ll.getUnitaMisura(),CostantsColomb.BUFEBAL, nCl);
//    
//    }catch (SQLException s){
//      throw new DatiCommLineeException("Impossibile caricare i dati relativi ai colli Febal da produrre -->"+s.getMessage());
//    }
    
    
    return list;
  }
  
  
  
//  public Double getNumPzFebal(LineaLavBean ll) throws SQLException{
//    Double np=Double.valueOf(0);
//    Connection con=null;
//    
//    
//    StringBuilder qry=new StringBuilder(
//         " select count(distinct codice_collo) from DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL " +
//         "\n where 1=1 ").append(
//         "\n and Commessa=  ").append(JDBCDataMapper.objectToSQL(ll.getCommessa()) ).append(
//         "\n and DataSpedizione = ").append(JDBCDataMapper.objectToSQL(ll.getDataCommessa()) ).append(
//         "\n and Linea in ('36010','36015','36020','36025','36030','36050','36150','36200') ");        
//         
//    try {
//      con=ColombiniConnections.getDbDesmosFebalProdConnection();
//      String s=qry.toString();
//      _logger.info("Esecuzione query: "+s);
//      
//      Object[] o=ResultSetHelper.SingleRowSelect(con, s);
//      if(o!=null && o.length>0){
//        np=ClassMapper.classToClass(o[0], Double.class);
//      }
//        
//    }finally{
//      if(con!=null)
//        try {
//        con.close();
//      } catch (SQLException ex) {
//       _logger.error("Impossibile rilasciare la connessione con DBDESMOSFEBALPROD : "+ex.getMessage());
//      }
//    }
//    
//    return np;
//  }
  
  
  
  
  

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_MONTAGGIARTEC_EDPC; //To change body of generated methods, choose Tools | Templates.
  }
  
  
  private static final Logger _logger = Logger.getLogger(DtMontImbArtec.class);   
}
