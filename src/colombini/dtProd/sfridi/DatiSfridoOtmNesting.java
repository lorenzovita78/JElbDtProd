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
public class DatiSfridoOtmNesting extends InfoSfridoCdL {

  public DatiSfridoOtmNesting(String cdL) {
    super(cdL);
  }
  
  
  
  
  @Override
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException {
    if(dataInizio==null || dataFine==null){
      _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
      return null;
    }
    
    return loadDatiFromDb(dataInizio,dataFine);
  }
  
  //modifica del 22/09/2016 per costi 
  //pubblicazione pinzM2 su campo difetti 
  //servirà a livello di costi per vedere i mq relativi allo sfrido dovuto alle pinze e non quantificabile tramite distinta
  
  private List loadDatiFromDb(Date dataInizio,Date dataFine) throws ElabException{
    List list=new ArrayList();
    Connection con=null;
    try {
      String dtIS = DateUtils.DateToStr(dataInizio, "yyyy-MM-dd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyyy-MM-dd");
      con=ColombiniConnections.getDbNestingGalConnection();
      
      StringBuilder qry=new StringBuilder(
              " Select 30,"+JDBCDataMapper.objectToSQL(ISfridoInfo.OTMGALP0)+" ,MIN(dataRif) as dataR, ").append(
                "  ROW_NUMBER() OVER(ORDER BY JobOrderID) as nott,'' as codPnp, rtrim(substring(SheetCode,1,15)) as codP ").append(
                " \n , MIN (Length) as length,MIN (Width) as Width,MIN(Thickness) as thickness ").append(
                " , MIN(SheetsQty) as SheetsQty,MIN(PanelsQty) as PanelsQty").append(
                "\n , MIN(sheetsM2) as supdisp, MIN(panelsM2) as suput,0 as difetti ").append(
                " , isnull(min(b.m2resti),0) restiM2").append(
                " , 0 as rifilo " ).append(// 07/06/2017 abbiamo verificato che il rifilo è già incluso nel fisiologico quindi non lo calcoliamo
                " ,SUM(lamaM2+pinzM2) as sfridoFisio,  SUM(pinzM2) as sup1 ").append(
                " ,SUM(botolM2+lateralM2) as sfridoEff ").append(
                " ,0 as sup3 ,0 as sup4 ").append(
                "\n FROM ( ").append(
                        "\n SELECT   cast(convert(varchar(8),r.RequestDate ,112) as int) as dataRif, ").append(
                                   " cast(replace(convert(varchar(8),r.RequestDate ,114),':','') as int) as oraRif, ").append(
                                   " v.RequestID as reqId,JobOrderID,SheetCode,Length,Width,Thickness, ").append(
                                   "\n SheetsVolume,SheetsQty,SheetsVolume*1000/Thickness  sheetsM2, ").append( 
                                   " PanelsQty,PanelsVolume*1000/Thickness  panelsM2, ").append(
                                   " ((SheetsVolume-PanelsVolume)*1000/Thickness) sfridoM2, ").append(
                                   "\n (sawdustwastevolume*1000)/thickness  lamaM2, ").append(
                                   " (clampzonewastevolume*1000)/thickness pinzM2, ").append(
                                   " (trapdoorwastevolume*1000)/thickness  botolM2, ").append(
                                   " (ejectedlaterallywastevolume*1000)/thickness lateralM2 ").append(
                            "\n FROM View_JobOrders v,Result r ").append(
                            "\n where v.RequestID=r.requestid ").append( 
                            " and substring(convert(char,r.RequestDate, 21),1,10) >=").append(JDBCDataMapper.objectToSQL(dtIS)).append(
                            " and substring(convert(char,r.RequestDate, 21),1,10) <=").append(JDBCDataMapper.objectToSQL(dtFS)).append(
              "\n )a ").append(
              "\n LEFT OUTER JOIN ").append(
              "\n (SELECT p.RequestID as reqId ").append(
                        " ,p.JobOrderID as jobId, ").append(
                        " COUNT(*) nPz, sum( ([Width]/1000) * ([Length]/1000) ) m2Resti").append(
                        "\n FROM PanelsForJobAndSheet p,Result r1 ").append(
                        "\n where 1=1 ").append(
                          " and p.RequestID=r1.requestid ").append(  
                          "\n and substring(convert(char,r1.RequestDate, 21),1,10) >=").append(JDBCDataMapper.objectToSQL(dtIS)).append(
                          " and substring(convert(char,r1.RequestDate, 21),1,10) <=").append(JDBCDataMapper.objectToSQL(dtFS)).append(
                          " and PanelCode='DROP' ").append(  
                          "\n group by p.RequestID,p.JobOrderID	) b ").append(  
            "\n ON a.reqId=b.reqId ").append(  
            " and a.JobOrderID=b.jobId ").append(    
              "\n group by a.ReqID,JobOrderID,SheetCode ");
              
              
              
              
      ResultSetHelper.fillListList(con, qry.toString(), list);
      
    } catch (SQLException ex) {
      _logger.error(" Problemi di connessione con db :"+ColomConnectionsCostant.DBOTMNESTING+" --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date --> "+ex.getMessage());
      throw new ElabException(ex);
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi con la chiusura della connessione con db :"+ColomConnectionsCostant.DBOTMNESTING+" --> "+ex.getMessage());
      }
    }
       
    return list;
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoOtmNesting.class); 
}
