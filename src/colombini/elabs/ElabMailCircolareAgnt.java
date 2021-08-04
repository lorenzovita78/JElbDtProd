/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.elabs.mail.ElabInvioMail;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mail.MailMessageInfoBean;
import mail.MailSender;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabMailCircolareAgnt extends ElabClass{

  public final static String STR_OBJ_MAIL="Predisposizioni per Fatturazione Elettronica San Marino";
  public final static String  STR_TEXT_MAIL=
    " San Marino, 22/10/2030 \n" +
    " Spettabile Agente\n" +
    " \n" +
    " Oggetto: Predisposizioni per Fatturazione Elettronica San Marino\n" +
    " \n" +
    " In considerazione della futura estensione della fatturazione elettronica alla Repubblica di San Marino, la cui entrata in vigore è in via di definizione, procediamo alla mappatura del codice univoco destinatario dei clienti titolari di un canale di trasmissione già accreditato presso il Sistema di Interscambio (SDI).\n" +
    " Vi chiediamo di compilare il file excel allegato specificando il Codice Univoco per ogni cliente.\n" +
    " Vi preghiamo di voler restituire il presente modulo compilato entro e non oltre il 15 dicembre 2019 inviando una mail al seguente indirizzo: \n" +
    " \n" +
    " nmazza@colombinigroup.com\n" +
    " \n" +
    " Certi dell’attenzione che riserverete alla presente, vogliate gradire cordiali saluti.\n" +
    " \n" +
    " \n" +
    " Natascia Mazza\n" +
    " Direttore Amministrazione"
    + "\n ";
  
  
  @Override
  public Boolean configParams() {
    return Boolean.TRUE; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void exec(Connection con) {
    Connection conSqlS=null;
    List<List> agenti=new ArrayList();
    try {
      conSqlS=ColombiniConnections.getDbNewBI();
      String select="SELECT distinct Agente,Email_Agenti\n FROM [newBI].[dbo].[Circolare_agenti] ";
      String selectCLi="SELECT * FROM [newBI].[dbo].[Circolare_agenti]  \n where Agente=";
      ResultSetHelper.fillListList(conSqlS, select, agenti);
      
      for(List agente:agenti){
        String codAgnt=ClassMapper.classToString(agente.get(0));
        String email=ClassMapper.classToString(agente.get(1));
        if(!StringUtils.IsEmpty(email)){
          String nomeFile="//pegaso/scambio/Elb/"+codAgnt+".xlsx";
          String sqlTmp=selectCLi+JDBCDataMapper.objectToSQL(codAgnt);
          //genero file xls
          XlsXCsvFileGenerator xlsTmp=new XlsXCsvFileGenerator(nomeFile, XlsXCsvFileGenerator.FILE_XLSX,"Lista clienti");
          File f= xlsTmp.generateFile(conSqlS, sqlTmp, XlsXCsvFileGenerator.COLUMNNAME);

          MailSender ms=new MailSender();
          MailMessageInfoBean beanMail=new MailMessageInfoBean("MAILCIRCOLAREAGNT");
          beanMail.setObject(STR_OBJ_MAIL);
          beanMail.setText(STR_TEXT_MAIL);
          beanMail.addFileAttach(f);
          beanMail.setAddressFrom("info@colombinigroup.com");
          beanMail.setAddressesTo(Arrays.asList(email));
          beanMail.setAddressesBbc(Arrays.asList("nmazza@colombinigroup.com","mserofilli@colombinigroup.com"));
          ms.send(beanMail);
          _logger.info("Mail inviata a "+email);
        }else{
          addWarning("\n Attenzione agente "+codAgnt+" senza indirizzo mail ");
        }
      }
      
    } catch(SQLException s){
      addError("Errore in fase di accesso al db-->"+s.getMessage());
    
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ElabMailCircolareAgnt.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      if(conSqlS!=null)
        try {
          conSqlS.close();
      } catch (SQLException ex) {
        
      }
      
    }
    
    }
  
    
    private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabMailCircolareAgnt.class);   
}



