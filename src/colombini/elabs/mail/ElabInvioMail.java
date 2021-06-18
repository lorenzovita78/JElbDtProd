/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import mail.MailMessageInfoBean;
import mail.MailSender;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 * Classe generica per inviare una mail 
 * @author lvita
 */
public abstract class ElabInvioMail extends ElabClass{

  protected Date dataInizio;
  protected Date dataFine;
  private String elab;
  /**
   * Boolean per gestire invio Mail in base al risultato di un'elaborazione
   */
  private Boolean sendMsg;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    elab=this.getInfoElab().getCode();
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine == null)
      return Boolean.FALSE;
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    MailSender ms=new MailSender();
    sendMsg=Boolean.TRUE;
    MailMessageInfoBean message=ms.getInfoBaseMailMessage(con, getIdMessage());
    addInfoToMailMessage(con,message);
    
    if(sendMsg)
      ms.send(message);
               
  }
  
  /**
   * Aggiunge info al messaggio da inviare
   * Con questo metodo è possibile variare alcune proprietà del messaggio da inviare 
   * @param con
   * @param messageBase 
   */
  public abstract void addInfoToMailMessage(Connection con,MailMessageInfoBean messageBase);
  
  
  /**
   * torna il codice del messaggio 
   * @return 
   */
  public abstract String getIdMessage();

  
  public Boolean getSendMsg() {
    return sendMsg;
  }

  public void setSendMsg(Boolean sendMsg) {
    this.sendMsg = sendMsg;
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabInvioMail.class);   

  
}
