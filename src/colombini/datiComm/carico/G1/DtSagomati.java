/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.G1;

import colombini.conn.ColombiniConnections;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.Connections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public  class DtSagomati  implements IDatiCaricoLineaComm  {

  
   private final String STMDIVI=" SELECT OADIVI "
                                       +" FROM OOHEAD  "
                                       +" WHERE OACONO=?"            
                                       +" and OAORNO=?";
                                       
  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List beans=new ArrayList();
    try {
      
      List<List> info=loadDatiFromBitABit(ll.getCommessa(),ll.getCodLineaLav(),ll.getDataCommessa());
      if(info==null || info.isEmpty()){
        _logger.warn("Linea "+ll.getCodLineaLav()+ " -  Attenzione dati non presenti su Bit@Bit per commessa: "+ll.getCommessa());
        return beans;
      }
      
      Map divQta=getMapDivQta(con, info);
      if(divQta.isEmpty()){
        _logger.warn("Linea "+ll.getCodLineaLav()+ " -  Attenzione dati non presenti su Bit@Bit per commessa: "+ll.getCommessa());
        return beans;
      }
      Set keys=divQta.keySet();
      Iterator iter=keys.iterator();
      while(iter.hasNext()){
        CaricoCommLineaBean bean=new CaricoCommLineaBean();
        String div=(String)iter.next();
        Double valore=(Double) divQta.get(div);

        bean.setCommessa(ll.getCommessa());
        bean.setLineaLav(ll.getCodLineaLav());
        bean.setDataRifN(ll.getDataCommessa());
        bean.setUnitaMisura(ll.getUnitaMisura());
        bean.setDivisione(div);
        bean.setValore(valore);
        beans.add(bean);
      }
        
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con il Database : "+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }
    
    return beans;
  }
  
  
  
  public List loadDatiFromBitABit(Integer commessa,String linea,Long dataCommessa ) throws SQLException{
    List list=new ArrayList();
    Connection con=null;
    String condLinea=" and DESTINAZIONE <>'FUORI LINEA' ";
    
    if("01048".equals(linea))
      condLinea=" and DESTINAZIONE = 'FUORI LINEA'"; 
    
    String qry=" select P7NROR as NUMORD,COUNT(id_pannello) as NPZ "+
               " from dbo.Commesse_Dettagli_Esecuzione "+
               " where 1=1 "+
               "\n "+condLinea +
               " and ID_Commessa="+JDBCDataMapper.objectToSQL(commessa) +
               " and p7refd= CONVERT(date,"+JDBCDataMapper.objectToSQL(dataCommessa.toString())+",112)"+
               " group by P7NROR";
    
    String qry2=qry.replace("Commesse_Dettagli_Esecuzione", "Commesse_Dettagli_Esecuzione_Storico");
    
    
    try {
      _logger.info("Esecuzione query :"+qry+"\n UNION \n"+qry2);
      
      con=ColombiniConnections.getDbBitaBitConnection();
      ResultSetHelper.fillListList(con, qry+"\n  UNION  \n"+qry2, list);
      
    }finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
       _logger.error("Impossibile rilasciare la connessione con METRONSQL : "+ex.getMessage());
      }
    }
    
    
    return list;
  }
    
  private Map getMapDivQta(Connection con ,List<List> list) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Map mapDivQta=new HashMap();
    
    try{
      ps = con.prepareStatement(STMDIVI); 
      for(List record:list){
        String nOrd=ClassMapper.classToString(record.get(0));
        Double val=ClassMapper.classToClass(record.get(1),Double.class);
        
        ps.setInt(1, FilterFieldCostantXDtProd.AZCOLOMBINI);
        ps.setString(2, nOrd);
        
        rs=ps.executeQuery();
        if(rs.next()){
          String divi=rs.getString("OADIVI");
          if(mapDivQta.containsKey(divi)){
            mapDivQta.put(divi,(Double)mapDivQta.get(divi)+val);
          }else{
            mapDivQta.put(divi, val);
          } 
        }
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return mapDivQta;
  }
  
  
  private static final Logger _logger = Logger.getLogger(DtSagomati.class);   
}
