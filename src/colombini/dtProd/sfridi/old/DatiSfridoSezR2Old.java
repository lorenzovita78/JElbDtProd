/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi.old;


import colombini.dtProd.sfridi.*;
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
public class DatiSfridoSezR2Old implements ISfridoInfo {
  
  
 
  @Override
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException {
    if(dataInizio==null || dataFine==null){
      _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
      return null;
    }
    
    return loadDatiFromDb(dataInizio,dataFine);
  }
  
  
  private List loadDatiFromDb(Date dataInizio,Date dataFine) throws ElabException{
    List list=new ArrayList();
    Connection con=null;
    try {
      String dtIS = DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");
      con=ColombiniConnections.getDbQuidR2Connection();
      
      String qry=" select  "//30 as az,'R2' as stab,'P1' as piano,
              + " cast(convert(varchar(8),Date,112) as int) as dataRif,0 as oraRif ,"+
                 "\n ROW_NUMBER() OVER(ORDER BY SheetId) as nOtm, "+
                 "\n '' as codPnp,SheetId as codEl,RealLength as Lungh,RealWidth as Largh,RealThickness as Spe,"+
                 "\n Quantity as qtaTot, 0 as qtaProd," //'MQ' as UM,
                 + " (RealLength/1000*RealWidth/1000*Quantity) as MQTot,"+
                 "\n (RealLength/1000*RealWidth/1000*Quantity)-WastePanel as MQProd, 0 as dif,"+
                 "\n WastePhisiological as SfridoFisio,"+
                  " 0 as supp1, 0 as supp2,0 supp "  + //,WastePanel as SfridoPan  "
                 "\n from dbo.SheetsRejects  where 1=1"+
            " and Date >= "+JDBCDataMapper.objectToSQL(dtIS)+
            " and Date <= "+JDBCDataMapper.objectToSQL(dtFS);
    
      ResultSetHelper.fillListList(con, qry, list);
      
    } catch (SQLException ex) {
      _logger.error(" Problemi di connessione con db :"+ColomConnectionsCostant.DBQUIDP+" --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date --> "+ex.getMessage());
      throw new ElabException(ex);
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi con la chiusura della connessione con db :"+ColomConnectionsCostant.DBQUIDP+" --> "+ex.getMessage());
      }
    }
       
    return list;
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoSezR2Old.class); 
}
