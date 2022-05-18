/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import elabObj.ElabClass;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import mail.MailSender;
import utils.ClassMapper;
import utils.FileUtils;

/**
 *
 * @author ggraziani
 */
public class ElabMailfLg extends ElabClass{
    
  private String mailto;
  private String mailtocc;
 
  private String PARAM_MAILTO="MAILTO";
  private String PARAM_MAILTOCC="MAILTOCC";


    
    
  public final static String STR_OBJ_MAIL="Files ODA LG 100";
  public final static String STR_OBJ_MAIL2="Files ODA LG 200";
  public final static String  STR_TEXT_MAIL=
    " Ciao \n" +
    "\n" +
    "In allegato, i files ODA LG 100 . \n"   
    +   " \n" 
    +"Saluti"
    ;
  
  public final static String  STR_TEXT_MAIL2=
    " Ciao \n" +
    "\n" +
    "In allegato, i files ODA LG 200 . \n" 
    +   " \n" 
    +"Saluti"
    ;
  
@Override
  public Boolean configParams() {
     Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
   
    if(parameter.get(PARAM_MAILTO)!=null){
      this.mailto=ClassMapper.classToClass(parameter.get(PARAM_MAILTO),String.class);
    } 
    if(parameter.get(PARAM_MAILTOCC)!=null){
      this.mailtocc=ClassMapper.classToClass(parameter.get(PARAM_MAILTOCC),String.class);
    } 
    
    return Boolean.TRUE;
  }

  public void exec(Connection con) {
   
      Map propsElab=getElabProperties();
//       String pathSource1="\\\\10.10.10.10\\ftp\\f-lg\\ODA1";
//       String pathSource2="\\\\10.10.10.10\\ftp\\f-lg\\ODA2";
//       String pathDest1="\\\\10.10.10.10\\ftp\\f-lg\\ODA1\\OLD";
//       String pathDest2="\\\\10.10.10.10\\ftp\\f-lg\\ODA2\\OLD";

       String pathSource1=(String) propsElab.get(NameElabs.PATHFILESOURCEODA1);
       String pathSource2=(String) propsElab.get(NameElabs.PATHFILESOURCEODA2);
       String pathDest1=(String) propsElab.get(NameElabs.PATHFILEDESTODA1);
       String pathDest2=(String) propsElab.get(NameElabs.PATHFILEDESTODA2);


       
       File dir1 = new File(pathDest1);
       File dir2 = new File(pathDest2);
       
        List<File> filesOda1 = new ArrayList();
        List<File> filesOda2 = new ArrayList();

        filesOda1=FileUtils.getListFileFolder(pathSource1);
        filesOda2=FileUtils.getListFileFolder(pathSource2);
         
        filesOda1.remove(dir1);
        filesOda2.remove(dir2);
          
          
          
          MailSender ms=new MailSender();
          MailMessageInfoBean beanMail=new MailMessageInfoBean("MAILLG");
          beanMail.setObject(STR_OBJ_MAIL);
          beanMail.setText(STR_TEXT_MAIL);
          
          MailMessageInfoBean beanMail2=new MailMessageInfoBean("MAILLG2");
          beanMail2.setObject(STR_OBJ_MAIL2);
          beanMail2.setText(STR_TEXT_MAIL2);
          
          for(File fil1:filesOda1){
             beanMail.addFileAttach(fil1);
          }
          
          for(File fil2:filesOda2){
             beanMail2.addFileAttach(fil2);
          }
          
          
          beanMail.setAddressFrom("dba@colombinigroup.com");
          beanMail.setAddressesTo(Arrays.asList(mailto.split(",")));
          beanMail.setAddressesCc(Arrays.asList(mailtocc));
          beanMail2.setAddressFrom("dba@colombinigroup.com");
          beanMail2.setAddressesCc(Arrays.asList(mailtocc));
          beanMail2.setAddressesTo(Arrays.asList(mailto.split(",")));
          //beanMail.setAddressesBbc(Arrays.asList("dba"));
          if(!filesOda1.isEmpty()){
            ms.send(beanMail);
          }
          
          if(!filesOda2.isEmpty()){
            ms.send(beanMail2);
          }
          
          for(File fil1:filesOda1){
             FileUtils.move(pathSource1+"\\"+fil1.getName(), pathDest1+"\\"+fil1.getName());
          }
          
          for(File fil2:filesOda2){
             FileUtils.move(pathSource2+"\\"+fil2.getName(), pathDest2+"\\"+fil2.getName());
          }
          
          

          _logger.info("Mail inviata a "+mailto);
    
      
      
  }
  
  
    private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabMailfLg.class);   
}



