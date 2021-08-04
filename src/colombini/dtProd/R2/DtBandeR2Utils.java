/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.R2;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.DtBandeR2;
import db.JDBCDataMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DtBandeR2Utils {
  
  public final static String TABBANDER2="ZDPBR2";
  
  /**
   *
   */
  public final static String INSERTSTM=" INSERT INTO "+ColombiniConnections.getAs400LibPersColom()+"."+TABBANDER2
                 + " ( ZBCONO, ZBDTRF, ZBCODB, ZBNBAB, "
                   + " ZBNB1D, ZBNB2D, ZBNB3D, ZBNB4D, ZBLBAB, ZBLBAD, "
                   + " ZBRGUT, ZBRGDT, ZBRGOR)"
                   + " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
  
  
  private static DtBandeR2Utils instance;
  
  private DtBandeR2Utils(){
    
  }
  
  public static DtBandeR2Utils getInstance(){
    if(instance==null){
      instance= new DtBandeR2Utils();
    }
    
    return instance;
  }
  
  
  
  public void insertDatiGg(Connection con ,List<DtBandeR2> dati) throws SQLException{
    PreparedStatement ps = null;
    try{
      for(DtBandeR2 bean:dati){
      
        ps = con.prepareStatement(INSERTSTM);
        ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
        ps.setLong(2, bean.getDataRifN());
        ps.setString(3, bean.getCodBanda()); 
        ps.setInt(4, bean.getNBandeBuone());
        ps.setInt(5, bean.getNBande1Dif());
        ps.setInt(6, bean.getNBande2Dif());
        ps.setInt(7, bean.getNBande3Dif());
        ps.setInt(8, bean.getNBandePlus4Dif());
        
        ps.setDouble(9, (bean.getLunTotBuona()/1000));
        ps.setDouble(10, (bean.getLunTotDif()/1000));
        
        ps.setString(11, "UTJCOLOM");
        ps.setString(12, DateUtils.getDataSysString());
        ps.setString(13, DateUtils.getOraSysString());
        
        ps.execute();
 
      }
    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  public void insertDatiGg(Connection con ,Map<String,DtBandeR2> dati) throws SQLException{
    List list=new ArrayList();
    Set keys=dati.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      list.add(dati.get(key));
    }
    insertDatiGg(con, list);
  }
  
  public void deleteDatiGg(Connection con ,Integer azienda,Long dataRifN) throws SQLException{
    if(con==null)
      throw new SQLException("Nessuna connessione dati--> Impossibile proseguire nella cancellazione.");
    
    if( dataRifN==null)
      throw new SQLException("Dati non completi--> Impossibile proseguire nella cancellazione.");
 
    
    String delete=" delete from "+ColombiniConnections.getAs400LibPersColom()+"."+TABBANDER2
                 +" where ZBCONO="+JDBCDataMapper.objectToSQL(azienda)
                 +" and ZBDTRF="+JDBCDataMapper.objectToSQL(dataRifN);
  
   PreparedStatement st=con.prepareStatement(delete);
   st.execute(); 
  }
  
  
 
  
  
}
