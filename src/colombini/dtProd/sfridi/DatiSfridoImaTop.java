/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.ElabException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DatiSfridoImaTop extends  DatiSfridoImpIma {
  
  
  public DatiSfridoImaTop(String cdL) {
    super(cdL);
  }

  
  @Override
  public Connection getDbConnection() throws SQLException {
    return ColombiniConnections.getDbImaTopConnection();
  }

  @Override
  public List getInfoSfrido(Date dataInizio, Date dataFine) throws ElabException {
    List l=new ArrayList();
    List l1=new ArrayList();
    l=super.getInfoSfrido(dataInizio, dataFine);
    
    
    Connection con=null;
    try{
      con=getDbConnection();
      String dtIS = DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");
      dtIS+=" 00:00:00";
      dtFS+=" 23:59:59";
      
      String query="select 30,'01035',cast(convert(varchar(8),ZPLoesch ,112) as int) as dataRif,\n" +
                    "       9999999,'', MaterialNr,0,0,0,0 as nS,COUNT(*) as Np,\n" +
                    "       0,0,0,sum(((Laenge*Breite)/1000000)) as sovraprod,\n" +
                    "       0,0,0,0,0,0  \n" +
                    "       from colombini_tops.dbo.tab_lagenhist \n" +
                    "       where SUBSTRING(barcode,1,1) = '9'\n" +
                    "		and PlatzNr <> 1082100 and PlatzNr <> 1042100"+
                    " and ZPLoesch between CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dtIS)+",120 )"+
                    " and CONVERT(datetime,"+JDBCDataMapper.objectToSQL(dtFS)+",120 )"+
                    "\n group by cast(convert(varchar(8),ZPLoesch ,112) as int) ,MaterialNr";
      
      ResultSetHelper.fillListList(con, query, l1);
      l.addAll(l1);
      
    }catch(SQLException s){
     _logger.error(" Problemi di connessione con db :"+ColomConnectionsCostant.DBIMATOP+" --> "+s.getMessage());
      throw new ElabException(s); 
    } catch (ParseException ex) {
       _logger.error(" Problemi di conversione di date --> "+ex.getMessage());
      throw new ElabException(ex);
    }finally{
      try{
        if(con!=null)
          con.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi con la chiusura della connessione con db :"+ColomConnectionsCostant.DBIMATOP+" --> "+ex.getMessage());
      }
    }
    
    return l; 
  }

 
  
 
  
  
     
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoImaTop.class); 

  
  
}
