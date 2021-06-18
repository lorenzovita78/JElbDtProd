/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import db.ResultSetHelper;
import elabObj.ElabClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class ElabUpdateRegQual extends ElabClass{
  
  public final static String UPD_RGQ=" update mcobmoddta.zdprgq " +
    "\n set rqdtco=?  , rqitno=?  , rqdscc = ?,  rqplgr= ?, rqorno= ?,  rqriga=? " +
    "\n where RQIDCT =? ";
  
  public final static String QRY_DESMOS="select distinct cast (DataSpedizione  as int) as  DTCOMRZ, \n" + 
        " Articolo as ARTCOMRZ, rtrim(DescrizioneArticolo2) as DESCRCOMRZ, \n " + 
        " Linea as CDLRZ, ltrim(rtrim(NumeroOrdine)) as NORDRZ, 0 as RIGORDRZ \n" +
        " from dbo.LDF_TXT_FILE_PER_VDL \n" +
        " where 1=1 " + 
        " and RigaCollo='001'\n" +
        " and NumeroCollo= ?";
  
  @Override
  public Boolean configParams() {
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Connection conSql=null;
    List<List> elems=new ArrayList();
    String sqlRic= " select rqidct, rqbarc from mcobmoddta.zdprgq where rqdtco= 0 ";
    try{
    
      ResultSetHelper.fillListList(con, sqlRic, elems);
      if(elems.size()>0){
        conSql=ColombiniConnections.getDbDesmosFebalProdConnection();
        for(List l:elems){
          Long idRg=ClassMapper.classToClass(l.get(0),Long.class);
          String barcode=ClassMapper.classToString(l.get(1));
          
          List info=getInfoBarcode(conSql, barcode);
          if(info!=null && info.size()>0){
            updRgQualita(con, idRg, barcode,info);
          }else{
            addWarning("Info non presenti su DesmosFebal per barcode : "+barcode);
          }
          
        }
      }

    }catch(SQLException s){
      addError("Errore in fase di interrogazione del db -->"+s.getMessage());
      _logger.error("Errore in fase di connessione/interrogazione db-->"+s.getMessage());
    }finally{
      if(conSql!=null)
        try {
          conSql.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiususra della connessione al db-->"+ex.getMessage());
      }
    }        
  }
  
  
  private List getInfoBarcode(Connection con ,String barcode){
    List info=new ArrayList();
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      ps = con.prepareStatement(QRY_DESMOS); 
      ps.setString(1, barcode); //fisso l'azienda 
      rs=ps.executeQuery();
      while(rs.next()){
        info.add(rs.getLong(1));
        info.add(rs.getString(2));
        info.add(rs.getString(3));
        info.add(rs.getString(4));
        info.add(rs.getString(5));
        info.add(rs.getInt(6));

      }
     } catch(SQLException s){
       addError("Errore in fase di reperimento informazioni su Desmos per collo "+barcode);
       _logger.error("Errore in fase di reperimento informazioni su Desmos per collo "+barcode+" -->"+s.getMessage());
     }finally{
      try{
        if(ps!=null)
          ps.close();
        if(rs!=null)
          rs.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiusura dello statment Desmos Febal  -->"+s.getMessage());
      }
      
    } 
    
    return info;
    
  }
  
  
  private void updRgQualita(Connection con ,Long idRg , String barcode,List infos){
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(UPD_RGQ);
      
      ps.setLong(1, (Long) infos.get(0));
      ps.setString(2, (String) infos.get(1));
      ps.setString(3, (String) infos.get(2));
      ps.setString(4, (String) infos.get(3));
      ps.setString(5, (String) infos.get(4));
      ps.setInt(6, (Integer) infos.get(5));
      
      ps.setLong(7,idRg);
      
      ps.execute();
      
    }catch(SQLException s){
     addError("Impossibile aggiornare i dati per il collo: "+barcode +" id :"+idRg +" -->"+s.getMessage()); 
     _logger.error("Impossibile aggiornare i dati per il collo: "+barcode +" id :"+idRg +" -->"+s.getMessage()); 
    }finally{
     try {
        if(ps!=null)
          ps.close();
      } catch(SQLException s){
         _logger.error("Errore in fase di chiusura dello statement  -->"+s.getMessage() );
      } 
    }   
      
      
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabUpdateRegQual.class);
  
}
