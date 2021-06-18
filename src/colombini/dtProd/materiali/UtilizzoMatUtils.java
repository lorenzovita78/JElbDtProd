/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.materiali;

import colombini.costant.CostantsColomb;
import db.JDBCDataMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class UtilizzoMatUtils {
  public final static String AZIENDA="AZIENDA";
  public final static String CENTRO="CENTRO";
  public final static String DATA="DATA";
 
  
  public final static String VAL1="VAL1";
  public final static String VAL2="VAL2";
  public final static String VAL3="VAL3";
  public final static String VAL4="VAL4";
  public final static String UNMI="UNMI";
   
  public final static String NOTE="NOTE";
   
  public final static String DTINS="DTINS";
  public final static String ORAINS="ORAINS";
  public final static String UTINS="UTINS";
  
  
  private static final Logger _logger = Logger.getLogger(UtilizzoMatUtils.class);   
  
  
  public final static Integer NCIFREDECOEE=3;
  
  private final static String TABUTMAT="ZUTMAT"; 
 
  
 
  
  
  public final static String insertStm=" INSERT INTO MCOBMODDTA."+TABUTMAT
                 + " ( ZUCONO, ZUCNTR, ZUDATA, "
                 +   " ZUVAL1, ZUVAL2, ZUVAL3, ZUVAL4, ZUUNMI, "
                 +   " ZUNOTE, ZUUTIN, ZUDTIN, ZUORIN ) values "
                 + "  (?,?,?,?,?,?,?,?,?,?,?,? )";
  
  //classe singleton
  private static UtilizzoMatUtils instance;
  
  private UtilizzoMatUtils(){
    
  }
  
  public static UtilizzoMatUtils getInstance(){
    if(instance==null){
      instance= new UtilizzoMatUtils();
    }
    
    return instance;
  }
  
  
  
  
  public void deleteDatiUtlMaterialiGg(Connection con,Date data) throws SQLException, ParseException{
    String delete=" delete from MCOBMODDTA."+TABUTMAT
                 +" where 1=1 "
                 +" and ZUDATA="+JDBCDataMapper.objectToSQL(new Long(DateUtils.DateToStr(data, "yyyyMMdd")));
//                 +" and ZUCNTR in ('G1P20201','G1P20202','G1P20203','G1P20301','G1P20302','G1P20303')";
    
   
   PreparedStatement st=con.prepareStatement(delete);
   st.execute(); 
  }      
  
  
  public void deleteDatiUtlMaterialiGg(Connection con,String centrodiLavoro,Date data) throws SQLException, ParseException{
    String delete=" delete from MCOBMODDTA."+TABUTMAT
                 +" where ZUCNTR="+JDBCDataMapper.objectToSQL(centrodiLavoro)
                 +" and ZUDATA="+JDBCDataMapper.objectToSQL(new Long(DateUtils.DateToStr(data, "yyyyMMdd")));
    
    
//   _logger.info(delete);
   PreparedStatement st=con.prepareStatement(delete);
   st.execute(); 
  }               
  
  
  public void deleteDatiUtlMaterialiGg(Connection con,Date data,List<String> linee) throws SQLException, ParseException{
    String inlist="( ";
    for(String linea:linee){
      inlist+=JDBCDataMapper.objectToSQL(linea);
      inlist+=",";
    }
    inlist=inlist.substring(0, inlist.length()-1);
    inlist+=" )";
    
    
    String delete=" delete from MCOBMODDTA."+TABUTMAT
                 +" where ZUCNTR in "+inlist
                 +" and ZUDATA="+JDBCDataMapper.objectToSQL(new Long(DateUtils.DateToStr(data, "yyyyMMdd")));
    
    
   _logger.info(delete);
   PreparedStatement st=con.prepareStatement(delete);
   st.execute(); 
  } 
  
  
  
  public void insertDatiUtlMaterialiGg(Connection con ,List<Map> dati) throws SQLException{
    if(dati==null || dati.isEmpty())
      return;
    
    PreparedStatement ps = con.prepareStatement(insertStm);
    
    try{
      for(Map mappaValori:dati){
      
        ps = con.prepareStatement(insertStm);
        ps.setInt(1, (Integer) mappaValori.get(AZIENDA) ); //fisso l'azienda 
        ps.setString(2, (String) mappaValori.get(CENTRO));
        ps.setLong(3, (Long) mappaValori.get(DATA));
        
        ps.setDouble(4, (Double)mappaValori.get(VAL1));
        ps.setDouble(5, (Double) mappaValori.get(VAL2));
        ps.setDouble(6, (Double)mappaValori.get(VAL3));
        ps.setDouble(7, (Double)mappaValori.get(VAL4));
        ps.setString(8, (String)mappaValori.get(UNMI));
        
        ps.setString(9, (String) mappaValori.get(NOTE));
        
        
        ps.setString(10, CostantsColomb.UTDEFAULT);
        ps.setString(11, DateUtils.getDataSysString());
        ps.setString(12, DateUtils.getOraSysString());
        
        ps.execute();
 
      }
    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  public void insertDatiUtlMaterialiGg(Connection con ,Map dati) throws SQLException{
     if(dati==null || dati.isEmpty())
      return;
     
    PreparedStatement ps = con.prepareStatement(insertStm);
    
    try{
        ps = con.prepareStatement(insertStm);
        ps.setInt(1, (Integer) dati.get(AZIENDA)); //fisso l'azienda 
        ps.setString(2, (String) dati.get(CENTRO));
        ps.setLong(3, (Long) dati.get(DATA));
        
        ps.setDouble(4, (Double)dati.get(VAL1));
        ps.setDouble(5, (Double) dati.get(VAL2));
        ps.setDouble(6, (Double)dati.get(VAL3));
        ps.setDouble(7, (Double)dati.get(VAL4));
        ps.setString(8, (String)dati.get(UNMI));
        
        ps.setString(9, (String) dati.get(NOTE));
        
        ps.setString(10, CostantsColomb.UTDEFAULT);
        ps.setString(11, DateUtils.getDataSysString());
        ps.setString(12, DateUtils.getOraSysString());
        
        ps.execute();
    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  
  
  public  Map getMappaValoriInsert(Integer azienda,String centroLav,Date data,Double val1,Double val2,Double val3,Double val4,String unmis,String note) throws ParseException{
    
    Map mappaVal=new HashMap();
    Double zero=Double.valueOf(0);
    
    mappaVal.put(AZIENDA, azienda);
    mappaVal.put(CENTRO, centroLav);
    mappaVal.put(DATA, DateUtils.getDataForMovex(data));
    
    mappaVal.put(VAL1, val1);
    
    mappaVal.put(VAL2, val2!=null ? val2 : zero);
    mappaVal.put(VAL3, val3!=null ? val3 : zero);
    mappaVal.put(VAL4, val4!=null ? val4 : zero);
    mappaVal.put(UNMI, unmis);
    
    mappaVal.put(NOTE, note);
    
    return mappaVal;
  }
 
  
}
