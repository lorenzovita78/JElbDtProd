/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.model.persistence.DatiProdPWGeneral;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCdL4DtProd;
import colombini.util.DatiProdUtils;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mail.MailCostants;
import mail.MailMessageInfoBean;
import mail.MailSender;
import org.apache.log4j.Logger;
import utils.ArrayListSorted;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDtProdPWCheck extends ElabClass{

  
  public final static String PSTABILIMENTO="STAB";
  public final static String PPIANO="PIANO";
  
  private Date dataInizio;
  private Date dataFine;
  
  
  private List<String> lineeElab;
  private String stabilimento=null;
  private String piano=null;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(dataInizio==null)
      return Boolean.FALSE;
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataFine==null)
      dataFine=dataInizio;
    
    
    //---parametri non obbligatori
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    
    if(parameter.get(PSTABILIMENTO)!=null){
      stabilimento=ClassMapper.classToString(parameter.get(PSTABILIMENTO));   
    }  
    
    if(parameter.get(PPIANO)!=null){
      piano=ClassMapper.classToString(parameter.get(PPIANO));   
    }
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    _logger.info("Check dati di Produzione");
    int sat=Calendar.SATURDAY;
    
    
    
    Map mapAnomalie=new HashMap<String,ArrayListSorted>();
    List<Long> ggLav=null;
    
    //caricamento linee da processare
    List<List> listLinee=getListCdl(con);
    
    //caricamento giorni lavorativi
    try{
      ggLav=DatiProdUtils.getInstance().getListGg(con, dataInizio, dataFine, Boolean.TRUE);
      
    }catch(SQLException s){
      _logger.error("Errore in fase di interrogazione dei giorni lavorativi-->"+s.toString());
      addError("Errore in fase di estrazione dei giorni lavorativi nel periodo");  
    }
    if(ggLav==null)
      return;
    
    for(List recLinea : listLinee){
      Date dataTmp=dataInizio;
      while(DateUtils.beforeEquals(dataTmp, dataFine)){
        int ddataR=DateUtils.getDayOfWeek(dataTmp);
        
        Long dataLongT=DateUtils.getDataForMovex(dataTmp);
        Integer azT=ClassMapper.classToClass(recLinea.get(0), Integer.class);
        String cdlT=ClassMapper.classToString(recLinea.get(3)).trim();
        String cdlDescT=ClassMapper.classToString(recLinea.get(4)).trim();
        String mail=ClassMapper.classToString(recLinea.get(6));
        String key=cdlT+"_"+dataLongT;
        System.out.println("Controllo dati per  "+cdlDescT+"  --> "+key);
        
        try{
          DatiProdPWGeneral dtpt=new DatiProdPWGeneral(azT, dataTmp, cdlT);
          dtpt.retriveDataCdl(con);
          List<Integer> minTurni=dtpt.getMinProdOperatorixTurno();
          
          if((DateUtils.getDayOfWeek(dataTmp)==Calendar.SUNDAY || ddataR==Calendar.SATURDAY) && dtpt.getInfoTCdl().getIdTurno()==null){
            dataTmp=DateUtils.addDays(dataTmp,1);  
            continue;
          }
          //se non trovo il turno per la giornata mando cmq una segnalazione escludendo il sabato
          if(dtpt.getInfoTCdl().getIdTurno()==null &&  ggLav.contains(new BigDecimal(dataLongT)) ){
            addSegnalazione(mapAnomalie,mail,key, "Linea :"+cdlT+"- gg :"+dataLongT+" -> Dati di produzione non presenti per il giorno indicato ");
            dataTmp=DateUtils.addDays(dataTmp,1);  
            continue; 
          }
          
          if(dtpt.getInfoTCdl().getIdTurno()!=null ){
//            if(dtpt.getMinProdImpianto(null, null)>dtpt.getMinProdOperatori()){
//              addSegnalazione(key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Ore totali operatori minori di ore totali impianto ",segnalazioni);
//              
//            }
            if(dtpt.getInfoTCdl().existT1()){
              if(dtpt.getInfoTCdl().getNumPzT1()+dtpt.getInfoTCdl().getNumClT1()+dtpt.getInfoTCdl().getNumPileBandeT1()<4){
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Numero pezzi o colli o bande non valorizzato correttamente per il T1 ");
              }
              if((dtpt.getInfoTCdl().getMinutiTotOperatoriT1()+minTurni.get(1) )< dtpt.getInfoTCdl().getMinutiT1() ) {
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Ore totali operatori minori delle ore impianto per il turno "+dtpt.getInfoTCdl().getT1String());
              }
                
            }
            if(dtpt.getInfoTCdl().existT2()){
              if(dtpt.getInfoTCdl().getNumPzT2()+dtpt.getInfoTCdl().getNumClT2()+dtpt.getInfoTCdl().getNumPileBandeT2()<4){
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Numero pezzi o colli o bande non valorizzato correttamente per il T2");
              }
              if((dtpt.getInfoTCdl().getMinutiTotOperatoriT2()+minTurni.get(2) )< dtpt.getInfoTCdl().getMinutiT2() ) {
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Ore totali operatori minori delle ore  impianto per il turno  "+dtpt.getInfoTCdl().getT2String());
              }
            }
            if(dtpt.getInfoTCdl().existT3()){
              if(dtpt.getInfoTCdl().getNumPzT3()+dtpt.getInfoTCdl().getNumClT3()+dtpt.getInfoTCdl().getNumPileBandeT3()<4){
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Numero pezzi o colli o bande non valorizzato correttamente per il T3");
              }
              if((dtpt.getInfoTCdl().getMinutiTotOperatoriT3()+minTurni.get(3) )< dtpt.getInfoTCdl().getMinutiT3() ) {
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" -> Ore totali operatori minori delle ore impianto per il turno  "+dtpt.getInfoTCdl().getT3String());
              }
            }
            if(dtpt.getInfoTCdl().existT4()){
              if(dtpt.getInfoTCdl().getNumPzT4()+dtpt.getInfoTCdl().getNumClT4()+dtpt.getInfoTCdl().getNumPileBandeT4()<4){
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" ->Numero pezzi o colli o bande non valorizzato correttamente per il T4");
              }
              if((dtpt.getInfoTCdl().getMinutiTotOperatoriT4()+minTurni.get(4) )< dtpt.getInfoTCdl().getMinutiT4() ) {
                addSegnalazione(mapAnomalie,mail,key,"Linea :"+cdlT+"- gg :"+dataLongT+" ->Ore totali operatori minori delle ore impianto per il turno  "+dtpt.getInfoTCdl().getT4String());
              }
            }
          }
        } catch (SQLException s){
          _logger.error("Errore in fase di lettura  dei dati di produzione -->"+s.getMessage());
          addError("Errore in fase di lettura  dei dati di produzione -->"+s.toString());
        }  
        dataTmp=DateUtils.addDays(dataTmp,1);  
      }
      
    }
    List<MailMessageInfoBean> mails=getListMails(mapAnomalie);
    if(mails!=null && !mails.isEmpty()){
      _logger.info("Invio E-mail segnalazioni......");
      MailSender m=new MailSender();
      m.send(mails);
    }
  }
  
  
  
  private List getListCdl(Connection con ) {
    List<List> l=new ArrayList();
    
    try{
      QueryCdL4DtProd qry=new QueryCdL4DtProd();
      qry.setFilter(QueryCdL4DtProd.FTGESTTURNI, "Y");
      
      if(lineeElab!=null && !lineeElab.isEmpty())
        qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeElab.toString());
      
      if(stabilimento!=null)
        qry.setFilter(FilterQueryProdCostant.FTSTAB, stabilimento);
      
      if(piano!=null)
        qry.setFilter(FilterQueryProdCostant.FTPLAN, piano);
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), l);
      
    } catch (SQLException s){
      addError("Impossibile leggere la lista delle linee da verificare --> "+s.toString());
      _logger.error("Impossibile leggere la lista delle linee da verificare --> "+s.getMessage());
    } catch(QueryException q){
      addError("Impossibile leggere la lista delle linee da verificare --> "+q.getMessage());
      _logger.error("Impossibile leggere la lista delle linee da verificare --> "+q.getMessage());
    }
    
    return l;
  }
  
  
  private void addSegnalazione(Map <String,ArrayListSorted> map,String mail,String keyList,String msg){
    ArrayListSorted segnalazioni =null;
    
    if(map.containsKey(mail)){
      segnalazioni=map.get(mail);
    }else{
      segnalazioni = new ArrayListSorted(0);
    } 
    List rec=new ArrayList();
    rec.add(keyList);
    rec.add(msg);
    
    segnalazioni.add(rec);
    
    map.put(mail,segnalazioni);
    
  }
  
  private  List<MailMessageInfoBean> getListMails(Map <String,ArrayListSorted> map ){
    List mails=new ArrayList();
    
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBCHECKDTPROD );
    String ccElab=ClassMapper.classToString(getElabProperties().get(MAIL_CC_INFOELAB));
    
    String object="Elenco Anomalie dati di Produzione per il periodo "+DateUtils.dateToStr(dataInizio, "dd/MM/yyyy")+" - "+DateUtils.dateToStr(dataFine, "dd/MM/yyyy");
    Set keys=map.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String indM=(String) iter.next();
      List segnalazioni=map.get(indM);
      String text=getStringFromList(segnalazioni);
      int idx=indM.lastIndexOf(",");
      String cc="";
      
      if(idx>0){
        cc=indM.substring(idx+1);
        indM=indM.substring(0, idx);
      }
      if(cc.isEmpty()){
        cc=ccElab;
      }else{
        cc+=","+ccElab;
      }
      MailMessageInfoBean mm=getMessageMailElab(MailCostants.MAIL_FROM_DEFAULT, indM, cc, object, text);
      mails.add(mm);
    }
    
    return mails;
  }
  
  
  private String getStringFromList(List<List> list){
    StringBuilder s=new StringBuilder();
    for(List rec:list){
      s.append("\n\n ").append(ClassMapper.classToString(rec.get(1)));
    }
    
    return s.toString();
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabDtProdPWCheck.class); 

}
