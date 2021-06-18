/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.R1;

import colombini.conn.ColombiniConnections;
import colombini.model.datiProduzione.IFermiLinea;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.datiProduzione.InfoTurniCdL;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.ElabException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class FermiLotto1R1P4 implements IFermiLinea {

  
  private final static String LOTTO1A="01084A";
  private final static String  LOTTO1A_IMP="1";
  private final static String LOTTO1B="01084B";
  private final static String  LOTTO1B_IMP="2";
  private final static String LOTTO1C="01084C";
  private final static String  LOTTO1C_IMP="3";
  
  @Override
  public List<InfoFermoCdL> getListFermiLinea(Connection con,InfoTurniCdL infoTCdl, Map causaliFermi) {
    List<InfoFermoCdL> listFBenas=new ArrayList();
    List<List> listF=new ArrayList();
    try {
     
      listF=getListFermi(infoTCdl.getDataRif(), infoTCdl.getCdl());
      for(List fermo:listF){
        InfoFermoCdL fermoBean=new InfoFermoCdL(infoTCdl.getIdTurno(), infoTCdl.getCdl(), infoTCdl.getDataRif());
        String codCaus=ClassMapper.classToString(fermo.get(1));
        Integer idCaus=ClassMapper.classToClass(causaliFermi.get(codCaus),Integer.class);
        Date dataIni=ClassMapper.classToClass(fermo.get(3), Date.class);
        Date dataFin=ClassMapper.classToClass(fermo.get(4), Date.class);
        String note=ClassMapper.classToString(fermo.get(5));
        
        
      }
      
      
    } catch (SQLException ex) {
     // throws new ElabException(ex.getMessage());
    } catch (ParseException ex) {
      Logger.getLogger(FermiLotto1R1P4.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return listFBenas;
  }
  
  
  private List getListFermi(Date dataRif,String cdl) throws SQLException, ParseException{
    Connection con =null;
    List lfermi=new ArrayList();
    
    try{
      con=ColombiniConnections.getDbLotto1Connection();
      ResultSetHelper.fillListList(con, getQueryListFermi(dataRif,cdl), lfermi);
     
    }finally{
      if(con!=null){
        try{
        con.close();
        } catch(SQLException s){
          _logger.warn("Errore in fase di chiusura della connessione "+s.getMessage());
        }
      }
    }
    return lfermi;
    
  }
  
  
  
  private String getQueryListFermi(Date dataRif,String cdl) throws ParseException{
    StringBuffer s= new StringBuffer();
    String dataS= DateUtils.DateToStr(dataRif, "yyyy-MM-dd");
    
    
    s.append(" SELECT idCau , Cod  ,a.Imp   ,convert(date,(datOraIni)) ,convert(time,[DatOraIni]) ,convert(time,[DatOraFin])[Note]      " ).append(
             "  FROM [dbo].[LOG_FERMI] a left outer join CAUSALI_FERMI b ON idCau=b.id\n").append(
             "  where convert(date,(datOraIni))= ").append(JDBCDataMapper.objectToSQL(dataS)).append(
             " and Imp=").append(JDBCDataMapper.objectToSQL(getCodiceImp(cdl)));        
    
    return s.toString();
    
  }
  
  
  private String getCodiceImp(String cdl){
    if(LOTTO1A.equals(cdl))
      return LOTTO1A_IMP;
    else if(LOTTO1B.equals(cdl))
      return LOTTO1B_IMP;
    else if(LOTTO1C.equals(cdl))
      return LOTTO1C_IMP;
    
    return "";
  }
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FermiLotto1R1P4.class); 
  
}
