/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.costant.TAPWebCostant;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.R1.QueryPzToProdSisTraccPz;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import elabObj.ElabClass;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import mail.MailSender;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 * Classe che fornisce un file xls con i pz non ancora usciti al riccio 
 * delle commesse in essere.
 * Per ogni pezzo non ancora prodotto da anche un tempo medio di preparazione.
 * Tale tempo viene preso da file di configurazione
 * @author lvita
 */
public class ElabDatiPzToProdImaCliR1P1 extends ElabClass {

  
  Map mapStatiOre=null;
  @Override
  public Boolean configParams() {
   return Boolean.TRUE;
  }
  
  
  

  @Override
  public void exec(Connection con) {
    mapStatiOre=new HashMap();
    loadMapStati();  
    MailSender ms=new MailSender();
    MailMessageInfoBean bean=ms.getInfoBaseMailMessage(con,NameElabs.MESSAGE_PZPRRICCIOR1P1);
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBPZTOPRODIMACLIR1P1);
    Integer nMail=ClassMapper.classToClass(getElabProperties().get(NameElabs.NMAILTOSEND),Integer.class);
    String nomeFile=InfoMapLineeUtil.getLogFileName(bean.getFileAttachName(), new Date());
    
    List infos=getListTotPzToProd(con);
    if(infos==null|| infos.isEmpty()){
      addError("Lista pz da produrre/prodotti vuota . Impossibile proseguire");
      return;
    }
    
    for(Integer i=1;i<=nMail;i++){
       String pMailTmp=ClassMapper.classToString(getElabProperties().get(NameElabs.LLMAIL+i));
       String fileLlog=nomeFile.replace("NM", i.toString());
       List<String> paramTmp=ArrayUtils.getListFromArray(pMailTmp.split(":"));
       if(paramTmp!=null && paramTmp.size()>1){
         String llogTmp=paramTmp.get(0);
         String mailsToTmp=paramTmp.get(1);
         XlsXCsvFileGenerator gf=new XlsXCsvFileGenerator(fileLlog, XlsXCsvFileGenerator.FILE_CSV);
         List lformail=getListForMail(infos, llogTmp);
         if(lformail!=null && lformail.size()>0){
           try{
             File attach=gf.generateFile(lformail);
             //bean.setFileAttachName(attach.getAbsolutePath());
             bean.setFilesAttach(Arrays.asList(attach));
             bean.setAddressesTo(ArrayUtils.getListFromArray(mailsToTmp.split(","))  );
 //           bean.setAddressesCc(null);
             ms.send(bean);
           } catch(FileNotFoundException s){
             addError("Errore in fase di generazione del file "+gf.getFileName() +" --> "+s.toString());
             _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
          }
         }else{
           addWarning("Nessun pezzo associato alla linea : "+llogTmp);
         }
       }else{
         addError("Parametri per invio mail non configurati correttamente");
       }
       
    }
    
    
//    XlsXCsvFileGenerator gf=new XlsXCsvFileGenerator(nomeFile, XlsXCsvFileGenerator.FILE_CSV);
//    File attach=gf.generateFile(infos);
      
    
    
    
  }
  
 
  private List getListTotPzToProd(Connection con){
    List<List> infoB=new ArrayList();
    try {
      QueryPzToProdSisTraccPz qry=new QueryPzToProdSisTraccPz();
      qry.setFilter(FilterQueryProdCostant.FTCDL, TAPWebCostant.CDL_RICCIOIMAANTE_EDPC);
      qry.setFilter(FilterQueryProdCostant.FTDATACOMMN, DateUtils.getDataForMovex(new Date()));
      qry.setFilter(FilterQueryProdCostant.FTPZNONPROD,"Y");
//      List columns=ArrayUtils.getListFromArray(qry.getFieldCaptions());
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), infoB);
      
      Connection conSql=null;
      try{
        conSql=ColombiniConnections.getDbImaAnteConnection();
        _logger.info("Inizio interrogazione db Ima Ante ");
        for(List rec:infoB){
          String barcode=ClassMapper.classToString(rec.get(0));
          Integer statusBarc=getStatusBarcode(conSql,barcode);
          rec.add(statusBarc);
          rec.add(mapStatiOre.get(statusBarc)); 
        } 
        
      }catch(SQLException s){
        
      } finally{
        if(conSql!=null)
          conSql.close();
        _logger.info("Fine interrogazione db Ima Ante ");
      }
//      columns.add("Stato Pz.");
//      columns.add("Ore per Prod.");
//      infoB.add(0, columns);
      
     
      
    } catch (QueryException ex) {
      addError("Errore in fase di lettura dei dati da As400 -->"+ex.toString());
    } catch (SQLException ex) {
      addError("Errore in fase di lettura dei dati da As400 -->"+ex.toString());
    }
    
    return infoB;
  }

  private Integer getStatusBarcode(Connection conSql, String barcode) {
    Integer status=null;
    String q="select Status  FROM [Colombini_Fronten].[dbo].[tab_ET] where Barcode=?";
    
    try { 
      PreparedStatement pstmt = conSql.prepareStatement(q);
      pstmt.setString(1, barcode);
      ResultSet rs=pstmt.executeQuery();
      while(rs.next()){
        status=rs.getInt("Status");
      }
      
    } catch (SQLException ex) {
      _logger.error("Impossibile individuare lo stato del pz :"+barcode+" --> "+ex.getMessage());
      addError("Impossibile individuare lo stato del pz :"+barcode+" - "+ex.toString());
      
    }
    
    return status;
  }
    
    

  private void loadMapStati() {
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBPZTOPRODIMACLIR1P1);
    String statiS=(String) getElabProperties().get(NameElabs.LISTSTATIPZ);
    String orestatiS=(String) getElabProperties().get(NameElabs.LISTORETOPRODSTATIPZ);
    List statiL=ArrayUtils.getListFromArray(statiS.split(","));
    List orestatiL=ArrayUtils.getListFromArray(orestatiS.split(","));

    int idx=0;
    for(Object statoS:statiL){
      mapStatiOre.put(ClassMapper.classToClass(statoS,Integer.class) ,
              ClassMapper.classToString(orestatiL.get(idx)));
      idx++;
    }
     
  }
  
  private List getListForMail(List<List> infoS,String lineeRif){
    List newList=new ArrayList();
    List columns=getColumnsList();
    
    for(List infoR:infoS){
      String lineaRif=ClassMapper.classToString(infoR.get(5)).trim();
      if(lineeRif.contains(lineaRif))
        newList.add(infoR);
    }
    
    if(newList.size()>0)
      newList.add(0, columns);
    
    return newList;
  }
  
  private List getColumnsList(){
    List columns=ArrayUtils.getListFromArray(new QueryPzToProdSisTraccPz().getFieldCaptions());
    columns.add("Stato Pz.");
    columns.add("Ore per Prod.");
    
    
    return columns;
  }
  
  
  
 private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabDatiPzToProdImaCliR1P1.class);   
}
