/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.util.DatiColliGgVDL;
import colombini.util.DatiCommUtils;
import colombini.util.DatiProdUtils;
import db.persistence.IBeanPersCRUD;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabAvProdCommLinee extends ElabClass{
  
  
  
  private Date dataElab=null;
  private List lineeToElab=null;

  @Override
  public List<MailMessageInfoBean> getListMailsErrorMessage(Connection con) {
    String err=getMsgErrorElab();
    String warn=getMsgWarningElab();
    List mails=new ArrayList();
    if(!StringUtils.isEmpty(err) || !StringUtils.isEmpty(warn)){
      MailMessageInfoBean mailM=new MailMessageInfoBean(NameElabs.MESSAGE_AVPRODLINEECOM);
      mailM.retriveInfo(con);
      
      mailM.setText(err+"\n\n\n"+warn);
      mails.add(mailM);
      
      return mails;
    }else {
      return null;
    }
  }
  
  
  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
      
    dataElab=(Date) param.get(ALuncherElabs.DATAINIELB);
    
    if(dataElab==null ){
      return Boolean.FALSE;
    }
    
    String linee="";
    if(param.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(param.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con){
    PersistenceManager pm=null;
    
    try{
      _logger.info(" ----- #### INIZIO ELABORAZIONE AVANZAMENTO PRODUZIONE COMMESSE #### -----");
      _logger.info("Data riferimento calcolo :"+dataElab);
     
      //lista delle commesse da processare per avanzamento
      // sono incluse solo le commesse
      List ggComm=DatiProdUtils.getInstance().getListGgCommesse(con, dataElab , Boolean.TRUE,Boolean.FALSE);
      _logger.info(" Commesse da processare ... : "+ggComm.toString());
      
      List<LineaLavBean> lineeToProcess=DatiCommUtils.getInstance().loadLineeForAvanProd(con, Boolean.TRUE, null, null,lineeToElab);  
      pm=new PersistenceManager(con);
      
      elabDataAvP(pm, ggComm, lineeToProcess,dataElab);
      
    } catch (QueryException ex) {
      _logger.error("Impossibile accedere alla query  --> "+ex.getMessage());  
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con AS400 --> "+ex.getMessage());
    } finally{
      _logger.info(" ----- ####  FINE ELABORAZIONE AVANZAMENTO PRODUZIONE COMMESSE  #### -----");
      
    }  
      
  }
  
   public void elabDataAvP(PersistenceManager man,List<List> commesseToElab,List<LineaLavBean> lineeToElab,Date dataRifCalc){
    if(commesseToElab==null || commesseToElab.isEmpty()){
      addWarning("Attenzione lista commesse vuota. Impossibile calcolare l'avanzamento Produzione Linee");
      return ;
    }
    if(lineeToElab==null || lineeToElab.isEmpty()){
      addWarning("Attenzione lista linee da elaborare vuota. Impossibile calcolare l'avanzamento Produzione Linee");
     return ;
    }  
    
    Map parameter=new HashMap();
    Date elabDt=getDataRifCalcMod(dataRifCalc);
    DatiColliGgVDL ldtc=new DatiColliGgVDL(elabDt);
    //modifca del 18/10 -> lista delle commesse con inclusione delle mini e delle nano.
    //la lista serve per caricare i dati sull'archivio della produzione giornaliera a commessa
    List commesseInLav=null;
    
    try {
      commesseInLav = DatiProdUtils.getInstance().getListGgCommesse(man.getConnection(), dataElab , null,null);
    } catch (QueryException ex) {
      _logger.error(" Errore in fase di caricamento  delle commesse da produrre in base alla data elaborazione");
      addError(" Errore in fase di caricamento  delle commesse da produrre" );
    } catch (SQLException ex) {
      _logger.error(" Errore in fase di caricamento  delle commesse da produrre in base alla data elaborazione");
      addError(" Errore in fase di caricamento  delle commesse da produrre" );
    }
    if(commesseInLav==null)
      commesseInLav=commesseToElab;
    
    parameter.put(AvProdLineaStd.LISTCOMMESSE,commesseInLav);
    parameter.put(AvProdLineaStd.DATIVDL,ldtc);
    
    List  listToUpd=new ArrayList();
    
    //ciclo sulle linee
    for(LineaLavBean bean:lineeToElab){  
      
      try {  
        String classeS=bean.getClassExecAvP();
        if(!StringUtils.IsEmpty(classeS)){
          _logger.info("Elaborazione linea "+bean.getCodLineaLav());
          Object classJ = Class.forName(classeS).newInstance();
          //istanzio la classe 
          AvProdLineaStd avpL= (AvProdLineaStd) classJ;
          avpL.setInfoLinea(bean);
          avpL.setDataRifCalc(elabDt);
          avpL.setParameter(parameter);
          avpL.prepareDtProd(man.getConnection());
          
          for(List infoCom:commesseToElab){
             String infoC="";
             Long dataN=ClassMapper.classToClass(infoCom.get(0),Long.class);
             Integer comm=ClassMapper.classToClass(infoCom.get(1),Integer.class);
             infoC=" - Commessa n."+comm+ " dataRif: "+dataN ;
             
             _logger.info(infoC);
             //imposto i dati relativi al numero di commessa e data sul bean relativo alle info della Linea
             //è necessario per definire il dato corretto sull'avanzamento di produzione
             bean.setCommessa(comm);
             bean.setDataCommessa(dataN);
             try{
               avpL.setInfoLinea(bean);
               listToUpd.add(avpL.getAvProdCommessa(man.getConnection()));
             
             } catch(DatiCommLineeException ex){
                addError(" Errore generazione dati avanzamento produzione - "+bean.getCodLineaLav()+infoC);
             }
          }
          
        }else{
          addWarning(" Attenzione classe per generazione dati avanzamento produzione non presente "+bean.getCodLineaLav());
        }
      } catch (ClassNotFoundException ex) {
        
        addError(" Classe di esecuzione per la generazione dei dati non trovata per linea "+bean.getCodLineaLav() +" --> "+ex.toString() );
      } catch (InstantiationException ex) {
        _logger.error("Problemi di esecuzione della classe -->"+ex.getMessage());
        addError( " Problemi di instanzazione della classe per linea "+bean.getCodLineaLav()+" --> "+ex.toString() );
      } catch (IllegalAccessException ex) {
        
        addError("Problemi di accesso ai metodi della classe per linea "+bean.getCodLineaLav()+" --> "+ex.toString() );
      } catch (DatiCommLineeException ex) {
        
        addError("Errore generazione dati propedeutici all'avanzamento produzione per linea "+bean.getCodLineaLav()+" --> "+ex.toString() );
      }
    }
    
    if(!listToUpd.isEmpty())
      _logger.info("Aggiornamento dati...");
        
    for(Object bean:listToUpd){
      try{
        man.updateDt((IBeanPersCRUD) bean);
      } catch(SQLException s){
        addError("Errore in fase di aggiornamento dei dati per "+bean.toString()+" --> "+ s.toString());
      }
    }
    
      
    
    
 }
  
  
 /**
   * Torna la data di riferimento del calcolo 
   * Se il giorno è domenica(cioè sta girando il lunedì) 
   * allora ricalcoliamo venerdì)
   * @param data
   */  
  public static Date getDataRifCalcMod(Date data){
    Date dataLog=data;
    GregorianCalendar gc= new GregorianCalendar();
   
    gc.setTime(dataLog);
    int gg=gc.get(GregorianCalendar.DAY_OF_WEEK);
   
//    if(gg==GregorianCalendar.SUNDAY){
//      dataLog=DateUtils.addDays(dataLog, -2);
//    }
    
    return dataLog;
  }
 
  
  private static final Logger _logger = Logger.getLogger(ElabAvProdCommLinee.class);

  

  
}
