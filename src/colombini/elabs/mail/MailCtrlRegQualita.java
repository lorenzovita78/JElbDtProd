/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import colombini.costant.CostantsColomb;
import colombini.costant.TAPWebCostant;
import colombini.elabs.NameElabs;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCommToShip;
import colombini.query.produzione.QueryProdOrNnFromTAP;
import colombini.util.InfoMapLineeUtil;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 * Classe che esegue la verifica per il Supply :
 * Come primo controllo verifichiamo se ci sono movimenti con articoli senza anagrafica.
 * Il controllo deve essere fatto sia su as400Colombini che as400Febal
 * @author lvita
 */
public class MailCtrlRegQualita extends ElabInvioMail{

  public static final String MESSAGE_CHKSOSTNNCTRL="CHKSOSTNNCTRL";
  
  
  
  
 

   
    


  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    _logger.info("Invio colli non ricevuti da  CQ");
    String fileXlsName=ClassMapper.classToString(getElabProperties().get(NameElabs.FILESOSNNCTTXLS));
    List<List> dati=new ArrayList();
    File attach1=null;
    
    try {
     
      Long dataComm=getDataCommessa(con);
      String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileXlsName, DateUtils.strToDate(dataComm.toString(), "yyyyMMdd"));
      //Map prop=ElabsProps.getInstance().getProperties(ELBCTRLSUPPLY);
      //String pathFiles=ClassMapper.classToString(getElabProperties().get(PATHFILESCSV));
      //deleteOldData(con,dataComm);
      if(existdData(con, dataComm)){
        setSendMsg(Boolean.FALSE);
        return;
      }
        
      
      loadPzNnSpuntati(con, dataComm);
      
      ResultSetHelper.fillListList(con, getQueryForExtract(dataComm), dati);
      if(dati.size()>0){
         dati.add(0, getColumnsForXls());
         XlsXCsvFileGenerator xls=new XlsXCsvFileGenerator(fNameNew,XlsXCsvFileGenerator.FILE_XLSX);
        try{
          attach1=xls.generateFile(dati);
          messageBase.addFileAttach(attach1);
        } catch(FileNotFoundException s){
            addError("Errore in fase di generazione del file "+xls.getFileName() +" --> "+s.toString());
            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
        }
      
      }else{
          setSendMsg(Boolean.FALSE);
      }
      
     
     
    } catch (QueryException ex) {
      _logger.error("Errore in fase di interrorazione del database -->"+ex.getMessage());
      addError("Errore in fase di interrorazione del database -->"+ex.toString());
    } catch (SQLException ex) {
      _logger.error("Errore in fase di interrorazione del database -->"+ex.getMessage());
      addError("Errore in fase di interrorazione del database -->"+ex.toString());
    } finally{
      
    }
  }

  @Override
  public String getIdMessage() {
    return MESSAGE_CHKSOSTNNCTRL;
  }
  
  
  private Long getDataCommessa(Connection con ) throws QueryException, SQLException{
    List<List> comms=new ArrayList();
    Long dataL =DateUtils.getDataForMovex(dataInizio);

    QueryCommToShip q=new QueryCommToShip();
    q.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    q.setFilter(FilterQueryProdCostant.FTDATAFIN, dataL);
    q.setFilter(QueryCommToShip.ORDERBYDATADESC,"Y");
    ResultSetHelper.fillListList(con, q.toSQLString(), comms);

     //prendiamo il primo recordo disponibile
    return ClassMapper.classToClass( ((List)comms.get(0)).get(0), Long.class); 
  }
  
  private void loadPzNnSpuntati(Connection con , Long dataMvx ) throws QueryException, SQLException{
    QueryProdOrNnFromTAP q1=new QueryProdOrNnFromTAP();

    q1.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    q1.setFilter(FilterQueryProdCostant.FTCDL, TAPWebCostant.CDL_CQUALITA_EDPC);
    q1.setFilter(FilterQueryProdCostant.FTDATACOMMN,dataMvx);
    q1.setFilter(QueryProdOrNnFromTAP.FT_PZNONPROD, "Y");


    String script= "INSERT INTO MCOBMODDTA.ZTAPMI "+q1.toSQLString()+" ";
    
    PreparedStatement ps=con.prepareStatement(script);
    Boolean exec=ps.execute();
    _logger.info(exec.toString());
  }
  
  private void deleteOldData(Connection con , Long dataMvx ) throws QueryException, SQLException{
    String script= "DELETE FROM MCOBMODDTA.ZTAPMI WHERE TMCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+
                    " AND TMPLGR = "+JDBCDataMapper.objectToSQL(TAPWebCostant.CDL_CQUALITA_EDPC)+
                    " AND TMDTCO = "+ JDBCDataMapper.objectToSQL(dataMvx);
    
    PreparedStatement ps=con.prepareStatement(script);
    Boolean exec=ps.execute();
    _logger.info(exec.toString());
  }
  
  private boolean existdData(Connection con , Long dataMvx ) throws QueryException, SQLException{
    Integer value=Integer.valueOf(0);
    String script= "SELECT COUNT(*)  FROM MCOBMODDTA.ZTAPMI WHERE TMCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+
                    " AND TMPLGR = "+JDBCDataMapper.objectToSQL(TAPWebCostant.CDL_CQUALITA_EDPC)+
                    " AND TMDTCO = "+ JDBCDataMapper.objectToSQL(dataMvx);
    
    Object[] obj=ResultSetHelper.SingleRowSelect(con, script);
    if(obj!=null && obj.length>0){
        value=ClassMapper.classToClass(obj[0],Integer.class);    
    }
    
    if(value>0)
        return Boolean.TRUE;
    
    return Boolean.FALSE;
    
    
  }
  
  
  private String getQueryForExtract(Long dataMvx){
      StringBuffer sql=new StringBuffer("select tmdtco ,tmcomm  ,tmncol , tmstrd ,").append(
                                        " tmlinp , tmlinm , tmorno  , stab , plan , respcdl  \n ").append(
                                         " from mcobmoddta.ztapmi left outer join ").append(
                                        "  ( select distinct a.ppplgr, b.clfact stab,b.clplan plan, a.ppdept cdccdl , a.ppwcre respcdl \n" +
                                        "      from  mvxbdta.mpdwct a  inner join mcobmoddta.zdpwcl b  on a.ppplgr=b.clplgr\n" +
                                        "      where 1=1 "
                                        + " and ppwcre<>'TSTCMP' and ppwcre<>'APIRONI'"        
                                        + " and a.ppcono="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+ "  ) c on tmcdlr=c.ppplgr  \n" +
                                       "\n where 1=1 " +
                                       "\n and tmcono="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+
                                       "\n and tmdtco="+JDBCDataMapper.objectToSQL(dataMvx)+
                                       "\n and tmplgr="+JDBCDataMapper.objectToSQL(TAPWebCostant.CDL_CQUALITA_EDPC));
      
      return sql.toString();
  }
  
  private List<String> getColumnsForXls(){
      return Arrays.asList("DataComm","Commessa","Collo","Descrizione","LineaLogica","DescrLineaLogica","OdV","Stab","Piano","Responsabile");
  }
  
  
  private static final Logger _logger = Logger.getLogger(MailCtrlRegQualita.class);
}
