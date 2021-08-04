/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.datiProduzione;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class FermiLotto1R1P4 implements IFermiLinea{
  
  public final static String LOTTO1A="LOTTO1A";
  public final static String LOTTO1B="LOTTO1B";
  
  
  public List<InfoFermoCdL> getListFermiLinea(InfoTurniCdL itcl,Map causaliLinea)  {
    List fermi=new ArrayList();
    
    
    return fermi;
  }
  
  
  
  private List<List> getListFermiFroDb(InfoTurniCdL itcl) throws SQLException, ParseException{
    Connection con=null;
    List fermi=new ArrayList();
    try{
      con=ColombiniConnections.getDbLotto1Connection();
      ResultSetHelper.fillListList(con, getQueryForFermi(LOTTO1A, itcl.getDataRif()) , fermi);
      
    }finally{
      try{
        if(con!=null)
          con.close();
      } catch(SQLException s){
        
      }
    }
    return fermi; 
  }
  
  
  private String getQueryForFermi(String impianto,Date data) throws ParseException{
    String query=" select idCau,cod,des,datOraIni,datOraFin,Durata,DATEDIFF(ss,datOraIni,datOraFin) sec "+
                 " FROM [SirioLotto1].[dbo].[LOG_FERMI] a left outer join [SirioLotto1].[dbo].[CAUSALI_FERMI] b on a.idCau=b.id "+
                 " where 1=1 ";
    
    
    if(LOTTO1A.equals(impianto))
      query+=" and a.Imp='1'";
    else
      query+=" and a.Imp='2'";
    
    String date=DateUtils.DateToStr(data, "yyyy-MM-dd");
    
    query+=" and convert(date,DatOraIni)=convert(date," +JDBCDataMapper.objectToSQL(date)+ ") ";
    
    
    return query;
  }

  @Override
  public List<InfoFermoCdL> getListFermiLinea(Connection con, InfoTurniCdL infoTCdl, Map causaliFermi) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
}
