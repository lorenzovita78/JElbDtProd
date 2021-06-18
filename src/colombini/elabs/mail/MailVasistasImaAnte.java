/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import colombini.conn.ColombiniConnections;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class MailVasistasImaAnte extends ElabInvioMail{

  public final static String ELBCTRLVASISTASANTE="ELBCTRLVASISTASANTE";
  public final static String NOMEFILEXLS="NOMEFILEXLS";
  
  private static final String MESSAGE_CHKIMAANTEVAS="CHKIMAANTEVAS";
  
  
 
  @Override
  public String getIdMessage() {
    return MESSAGE_CHKIMAANTEVAS;
  }

  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    Connection conSqlS=null;
    List l=new ArrayList();
    
    XlsXCsvFileGenerator xlsGen=null;
    try{
      conSqlS=ColombiniConnections.getDbImaAnteConnection();
      xlsGen=new XlsXCsvFileGenerator(messageBase.getFileAttachName(), XlsXCsvFileGenerator.FILE_XLSX,"foglio1");
      File attach=xlsGen.generateFile(conSqlS, getQuery(), XlsXCsvFileGenerator.COLUMNNAME);
      
      messageBase.addFileAttach(attach);
      
    } catch(SQLException s){
      addError("Errore in fase di collegmento al Db Ima Ante --> "+s.toString());
    } catch(FileNotFoundException s){
      addError("Errore in fase di generazione del file "+xlsGen.getFileName() +" --> "+s.toString());
      _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
          
    } finally{
      if(conSqlS!=null)
        try {
          conSqlS.close();
      } catch (SQLException ex) {
        
      }
    }      
  }

  private String getQuery(){
    StringBuilder s=new StringBuilder ("select ").append(getFields()).append(
                                       "\n  from Colombini_Fronten.dbo.tab_ET ").append(
                                       "\n  where 1=1 ").append(
                                       "\n and (ItemNo like '101555%' or ItemNo like '101554%') ").append(
                                       "\n and Status>71 ").append(
                                       "\n and length=316").append(
                                       "\n order by CommissionNo desc");
    
    return s.toString();
    
  }
  
  private String getFields(){
     return "Barcode , CommissionNo, PackageNo, Box, Pedana, ItemNo, ItemDecription , length , Width, Status" ;
  }
  
  
  private Logger _logger = org.apache.log4j.Logger.getLogger(MailVasistasImaAnte.class);   
  
}
