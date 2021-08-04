/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs.mail;

import colombini.costant.CostantsColomb;
import colombini.elabs.NameElabs;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryCommesseCoeff;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import fileXLS.XlsXFile;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class MailCoefficientiComm extends ElabInvioMail{

  
  public static final String MESSAGE_COEFFCOMMCDL="COEFFCOMMCDL";
  
  private Map mapPositionsDivi=new HashMap();
  private List listColumnsHeader=new ArrayList();

//  private Date dataInizio=null;
//  private Date dataFine=null;
  
  public Map getMapPositionsDivi() {
    return mapPositionsDivi;
  }

  public List getListColumnsHeader() {
    return listColumnsHeader;
  }
  
  public Integer getDiviPosition(String divi){
    Integer pos=Integer.valueOf(-1);
    if(mapPositionsDivi.containsKey(divi))
      pos=(Integer) mapPositionsDivi.get(divi);
    
    return pos;
  }
  
  

  @Override
  public String getIdMessage() {
    return MESSAGE_COEFFCOMMCDL; //To change body of generated methods, choose Tools | Templates.
  }
   
  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    List<List> infoCoeff=getInfoCoeffComm(con);
    if(infoCoeff==null || infoCoeff.isEmpty()){
      addWarning(" Attenzione info coefficienti non presenti . Impossibile proseguire nell'elaborazione !!");
      messageBase.setSendable(Boolean.FALSE);
      return;
    }
    String nomeFile=InfoMapLineeUtil.getLogFileName(messageBase.getFileAttachName(), new Date());
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBMAILCOEFFCOMM);
    String headerS=ClassMapper.classToString(getElabProperties().get(NameElabs.COLUMNHEADERCFC));
    String diviS=ClassMapper.classToString(getElabProperties().get(NameElabs.DIVIPOSCFC));
    Integer numColSDivi=ClassMapper.classToClass(getElabProperties().get(NameElabs.NUMCOLSTARTDIVI),Integer.class);
    setMapPositionsDivi(diviS,numColSDivi);
    setListColumnsHeader(headerS);
      
    File attach=elabData(nomeFile,infoCoeff);
    messageBase.addFileAttach(attach);
  }
  
  
  
  private XlsXFile elabData(String nomeFile,List<List> infoCoeff){
    XlsXFile fileXls=null;
    try{
      fileXls=new XlsXFile(nomeFile);
      String cdlTmp="";
      List<List> infoSheet=null;
      List row =null;
      Long dataCtmp=Long.valueOf(0);
      for(List infoR: infoCoeff){
        Long  dataC=ClassMapper.classToClass(infoR.get(0),Long.class);
        Long  numC=ClassMapper.classToClass(infoR.get(1),Long.class);
        String cdl=ClassMapper.classToString(infoR.get(2));
        String cdlDesc=ClassMapper.classToString(infoR.get(6));
        //il cambio di linea corrisponde ad un nuovo sheet
        if(!cdlTmp.equals(cdl) ){
           if(!StringUtils.IsEmpty(cdlTmp) ){
             if(row!=null)
               infoSheet.add(row);
             
             XlsXCsvFileGenerator.addSheetInfo(fileXls, cdlTmp, infoSheet);
           }
           infoSheet=new ArrayList();
           infoSheet.add(listColumnsHeader);
           dataCtmp=Long.valueOf(0);
        }
        
        String unMe=ClassMapper.classToString(infoR.get(3));
        String div=ClassMapper.classToString(infoR.get(4));
        Double coeff=ClassMapper.classToClass(infoR.get(5),Double.class);
        
        //il cambio di data(= cambio commessa) corrisponde al cambio di riga dello sheet 
        if(!dataC.equals(dataCtmp) ){
          if(dataCtmp>0){
            infoSheet.add(row);
          }
          row =getNewRowSheet(cdlDesc,dataC, numC, unMe);
        }
        
        row.set(getDiviPosition(div), coeff);
        
        dataCtmp=dataC;
        cdlTmp=cdl;  
      }
      
      if(row!=null){
        infoSheet.add(row);
        XlsXCsvFileGenerator.addSheetInfo(fileXls, cdlTmp, infoSheet);
      }
      
      
      if(fileXls!=null)
        fileXls.closeFile();
    
    } catch(IOException e){
      _logger.error(" Errore in fase di generazione del file xls -->"+e.getMessage());        
      addError("Errore in fase di generazione del file xls -->"+e.toString());
    }
      
      return fileXls;
      
  }
  
  /**
   * Prepara una riga del file xls.
   * COme assunto imposta i 3 parametri nelle prime 3 colonne
   * @param dataL
   * @param comm
   * @param unMe
   * @return 
   */  
  private List getNewRowSheet(String desc,Long dataL,Long comm,String unMe){
    List row=new ArrayList();
    row.add(desc);
    row.add(dataL);
    row.add(comm);
    row.add(unMe);
    int chc=listColumnsHeader.size();
    int rc=row.size();
    for(int i=rc;i<chc;i++){
      row.add(new Double(0));
    }
    
    return row;
  }
  
  
  private List<List> getInfoCoeffComm(Connection con ){
    List info=new ArrayList();
    
    try{
      QryCommesseCoeff qry=new QryCommesseCoeff();
      qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, CostantsColomb.AZCOLOM);
      if(this.dataInizio!=null && this.dataFine!=null){
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, DateUtils.getDataForMovex(dataInizio));
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA, DateUtils.getDataForMovex(dataFine));
      }else{
        String annoC=DateUtils.getYear(new Date() )+"0101";
        Long dataIniL=Long.valueOf(annoC);
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, dataIniL);
      }
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), info);
    
    } catch(SQLException s){
      _logger.error("Errore in fase di lettura dei dati di commessa --> "+s.getMessage());
      addError("Impossibile accedere ai dati relativi ai coefficienti per commessa :"+s.toString());
    } catch(QueryException q){
      _logger.error("Errore in fase di lettura dei dati di commessa --> "+q.getMessage());
      addError("Impossibile accedere ai dati relativi ai coefficienti per commessa :"+q.toString());
    }
    
    return info;
  }
  
  private void setListColumnsHeader(String head){
    if(StringUtils.IsEmpty(head))
      return ;
    
    String [] headers = head.split(",");
    listColumnsHeader=ArrayUtils.getListFromArray(headers);
    
  }
  
  
  private void setMapPositionsDivi(String diviS,Integer numColIni){
    if(StringUtils.IsEmpty(diviS))
      return ;
    
    String [] array=diviS.split(",");
    List<String> l=ArrayUtils.getListFromArray(array);
    Integer count=numColIni;
    for(String dv:l){
      mapPositionsDivi.put(dv, count);
      count++;
    }
    
  }
  
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(MailCoefficientiComm.class);   

  
}
