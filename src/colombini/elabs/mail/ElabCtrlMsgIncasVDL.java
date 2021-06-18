/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import colombini.conn.ColombiniConnections;
import colombini.elabs.NameElabs;
import colombini.model.persistence.middleware.MsgMiddlewareConstant;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.QryMsgMiddlewareVDL;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabCtrlMsgIncasVDL extends ElabInvioMail {

 
  public final static String TYPE_OBJMSG="TYPEMSG";
  
  
  
  public String typeMsg;
  
  @Override
  public Boolean configParams() {
    Boolean b=super.configParams(); //To change body of generated methods, choose Tools | Templates.
    
    Map parameter=this.getInfoElab().getParameter();
    if(parameter.get(TYPE_OBJMSG)!=null)
      this.typeMsg=ClassMapper.classToString(parameter.get(TYPE_OBJMSG));
    
    if(typeMsg==null)
      return Boolean.FALSE;
    
    
    return b;
  }

  
  
  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    Connection conVDL=null;
    _logger.info("ELABORAZIONE - Verifica Msg tra Incas e Vdl per tipo Messaggio -->"+typeMsg);
    List<List> result = new ArrayList();
    File attach1=null;
    String fileXlsName=ClassMapper.classToString(getElabProperties().get(NameElabs.FILEMSINCASVDL));
    String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileXlsName, dataInizio);
    try{
      
    
      conVDL=ColombiniConnections.getDbVDLVisionConnection();
      QryMsgMiddlewareVDL qry=new QryMsgMiddlewareVDL();
      
      
      qry.setFilter(QryMsgMiddlewareVDL.FT_STATUSMESSAGE, MsgMiddlewareConstant.STATUS_MSG_REJECTED);
      qry.setFilter(QryMsgMiddlewareVDL.FT_OBJECTTYPE, typeMsg);
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA,DateUtils.addSeconds(DateUtils.getInizioGg(dataInizio),1));
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA,DateUtils.getFineGg(dataInizio));
      
      ResultSetHelper.fillListList(conVDL, qry.toSQLString(), result);
      _logger.info("Record presenti : "+result.size());
      if(result.size()>0){
         result.add(0, getColumnsForXls());
         XlsXCsvFileGenerator xls=new XlsXCsvFileGenerator(fNameNew,XlsXCsvFileGenerator.FILE_XLSX);
        try{
          attach1=xls.generateFile(result);
          messageBase.addFileAttach(attach1);
        } catch(FileNotFoundException s){
            addError("Errore in fase di generazione del file "+xls.getFileName() +" --> "+s.toString());
            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
        }
      
      }else{
          setSendMsg(Boolean.FALSE);
      }
      
    }catch(SQLException s){
     addError("Errore in fase di collegamento al DB VDL -->"+s.getMessage());
    } catch (ParseException ex) {
      addError("Errore in fase di conversione dei dati --> "+ex.toString());
    } catch (QueryException ex) {
      addError("Errore in fase di lettura dei dati sul db VDL -->"+ex.getMessage());
    }finally{
      try{
      if(con!=null)
        conVDL.close();
      } catch(SQLException s){
        _logger.error("Errore in fase di rilascio della commessa -->"+s.getMessage());
      }
      _logger.info("ELABORAZIONE - Verifica Msg tra Incas e Vdl terminata ");
    }
       
  }

  @Override
  public String getIdMessage() {
    if(MsgMiddlewareConstant.TYPE_OBJMSG_BUNDLEDATA.equals(typeMsg))
      return "CHKMSGINSVDLCOM";
             //"CHKMSGINSVDLCOM";
    
    return"";
  }
  
   private List<String> getColumnsForXls(){
      return Arrays.asList("IdMessaggio","Stato","TipoMsg","DataIns","DataProc","IdMsgErr","DescrErr");
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabCtrlMsgIncasVDL.class);
  
}
