/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.model.InfoMancanteVdlBean;
import colombini.model.Linea4MancantiVDL;
import colombini.util.DatiProdUtils;
import colombini.util.InfoMapLineeUtil;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mail.MailMessageInfoBean;
import mail.MailSender;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class ElabGestMancantiDettaglioFromVDL extends ElabClass{

  private String typeOper="";
  private Date dataRif;
  private Date dataFine;
  private Boolean invioMail;
  private Boolean checkElab;
  private Boolean onlyVettAttivi;
  
  //mappa che contiene per ciascuna linea logica la descrizione relativa del raggruppamento
  private Map<Integer,String> mapCodiciLineeLav=null;
  //mappa contenente per ciascuna Linea descrittiva il bean associato L
  private Map<String,Linea4MancantiVDL> mapLineeVdl=null;
  //mappa contenente per ciascuna Linea Logica la sua descrizione su Movex
  private Map<String,String> mapCdlMvxDescr=null;
  //mappa contenente per ciascuna Linea Logica la sua descrizione su Movex
  private Map<String,String> mapFornMvx=null;
  
  //mappa contenente per ciascuna spedizione l'ora di spedizione
  private Map<String,Integer> mapSpedizioniGg=null;
  
  public final static String ELABMATT="MATT";
  public final static String ELABPOM="POM";
  
  public final static String PVETATTIVI="VETATT";
  
  
  
  public final static String TIMEMATTINA="06:00:00";
  public final static String TIMEPOMERIGGIO="13:00:00";
  
  public final static Integer MINUTIPOM=Integer.valueOf(780); //13*60
  public final static Integer MINUTIMATT=Integer.valueOf(360); //6*60
  
  
  @Override
  public Boolean configParams() {
    Map parameter =this.getInfoElab().getParameter();
    if(parameter.get(ALuncherElabs.TYPEOPR)!=null){
      this.typeOper=ClassMapper.classToString(parameter.get(ALuncherElabs.TYPEOPR));
    }
    if(StringUtils.isEmpty(typeOper))
      return Boolean.FALSE;
    
    
    dataRif=(Date) parameter.get(ALuncherElabs.DATAINIELB);
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }
    
    if(dataRif==null)
      return Boolean.FALSE;
    
    if(dataFine==null)
      dataFine=dataRif;
    
    //per gestire un eventuale non invio della mail
    if(parameter.get(ALuncherElabs.MAIL)!=null){
      this.invioMail=ClassMapper.classToClass(parameter.get(ALuncherElabs.MAIL),Boolean.class);
    }
    //se non viene specificato il default è YEES
    if(invioMail==null)
      invioMail=Boolean.FALSE;
    
    if(parameter.get(ALuncherElabs.CHECKELAB)!=null){
      this.checkElab=ClassMapper.classToClass(parameter.get(ALuncherElabs.CHECKELAB),Boolean.class);
    }
    
    if(parameter.get(PVETATTIVI)!=null){
      this.onlyVettAttivi=ClassMapper.classToClass(parameter.get(PVETATTIVI),Boolean.class);
    }
    
    if(onlyVettAttivi==null)
      onlyVettAttivi=Boolean.FALSE;
    
    if(checkElab==null)
      checkElab=Boolean.FALSE;
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Elaborazione dettaglio mancanti da VDL");
    
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBDETTMANCANTIVDL);
    String pathFile=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEDETTVDL));
    String nomeFile=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEPARZDETTVDL));
    String extFile=ClassMapper.classToString(getElabProperties().get(NameElabs.EXTFILEPARZDETTVDL));
    
    Date dataT=dataRif;
    
    while(DateUtils.beforeEquals(dataT, dataFine)){
      Long giornoCorrenteN=DateUtils.getDataForMovex(dataT);

      //se ho già elaborato passo al giorno successivo
      if(checkElab && isElabExecuted(con, dataT) )
        continue;
      
      

      try{
        loadMapCdL(con);
        //carichiamo le informazioni delle linee da gestire per i mancanti
        mapLineeVdl=Linea4MancantiVDL.getMapLineeMancantiVDL(con);
        //carichiamo i codici delle linee logiche associate alle linee dei mancanti
        loadCodiciLineeLog();
        String nomeFileDef=getFileNameMancanti(pathFile,nomeFile,extFile,dataT);
        //lettura file mancanti
        List<InfoMancanteVdlBean> mancanti=getListMancanti(nomeFileDef,dataT);
        
        //elaborazione dati
        if(mancanti!=null && !mancanti.isEmpty()){
          //gestione dati su db
          
          try{
            PersistenceManager man=new PersistenceManager(con);
            InfoMancanteVdlBean beanOne=(InfoMancanteVdlBean) (IBeanPersSIMPLE) mancanti.get(0);
            man.deleteDtFromBean(beanOne);

            for(InfoMancanteVdlBean mancante:mancanti){               
              man.storeDtFromBean(mancante);
            }

            if(invioMail){
              MailSender mS=new MailSender();
              MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_ELBMANCANTINEW);
              beanInfo.setObject(beanInfo.getObject().trim()+" in data "+DateUtils.dateToStr(beanOne.getDataRif(), "dd/MM/yyyy")+" ore "+DateUtils.dateToStr(beanOne.getDataRif(), "HH:mm") );
              beanInfo.setText(prepareTextMsg(con,dataT));

              System.out.println(beanInfo.getText());
              mS.send(beanInfo);
            }
          }catch(SQLException s){
            _logger.error(" Errore in fase di cancellazione/salvataggio dei mancanti per il giorno "+giornoCorrenteN+" --> "+s.getMessage());
            addError(" Errore in fase di cancellazione/salvataggio dei mancanti per il giorno "+giornoCorrenteN+" --> "+s.toString());
          }  
          
        }else{
          addWarning ("Mancanti non presenti per giorno "+dataT);
        }
      } catch(SQLException s){
         _logger.error(" Errore in fase di caricamento delle linee lavorative per il giorno "+giornoCorrenteN+" --> "+s.getMessage());
         addError(" Errore in fase di caricamento dei dati per il giorno "+giornoCorrenteN+" --> "+s.toString());
      }
      dataT=DateUtils.addDays(dataT, 1);
    }
  }
  
 
  private void loadMapCdL(Connection con) {
    mapCdlMvxDescr=new HashMap();
    mapFornMvx=new HashMap();
    try{
      List<List> cdLList=DatiProdUtils.getInstance().getListCentriLavoro(con, Boolean.TRUE);
      if(cdLList==null){
        addError("Attenzione lista centri di lavoro vuota");
        return ;
      }
      
      for(List rec:cdLList){
         String codCdl=ClassMapper.classToString(rec.get(1)).trim();
         String descrCdl=ClassMapper.classToString(rec.get(2)).trim();
         String codForn=ClassMapper.classToString(rec.get(8)).trim();
         
         if(!mapCdlMvxDescr.containsKey(codCdl))
           mapCdlMvxDescr.put(codCdl,descrCdl); 
         
         if(!StringUtils.isEmpty(codForn) && !mapFornMvx.containsKey(codCdl))
           mapFornMvx.put(codCdl,codForn);
         
         
      }
    } catch(QueryException q){
      _logger.error("Impossibile caricare i centri di Lavoro ->"+q.getMessage());
      addError("Impossibile caricare i centri di Lavoro  ->"+q.toString());
    } catch(SQLException s){
      _logger.error("Impossibile caricare i centri di Lavoro  ->"+s.getMessage());
      addError("Impossibile caricare i centri di Lavoro  ->"+s.toString());
    }
  }
  
  
  
  
  private Boolean isElabExecuted(Connection con,Date data) {
    PersistenceManager pm=null;
    Boolean b=Boolean.FALSE;
    
    InfoMancanteVdlBean bean=new InfoMancanteVdlBean();
    bean.setDataRif(data);
    if(ELABMATT.equals(typeOper)){
      bean.setRifGg(InfoMancanteVdlBean.TIPOMANCANTEMATT);
    }else{ 
      bean.setRifGg(InfoMancanteVdlBean.TIPOMANCANTEPOM);
    }
    
    List l=new ArrayList();
    l.add(InfoMancanteVdlBean.VDDTRF);
    l.add(InfoMancanteVdlBean.VDRFGG);
    try{
      pm=new PersistenceManager(con);
      b=pm.checkExist(bean, l);
      
    }catch (SQLException s){
      _logger.error("Errore in fase di verifica se l'elaborazione è già stata eseguita -->"+s.getMessage());
      addError(" Errore in fase di verifica se l'elaborazione è già stata eseguita ");
    }finally{
      if(pm!=null)
        pm=null;
    }
    
    return b;
  }
  
  
  private Map getSpedizioniGg(Connection con, Long giornoCorrenteN) {
    Map spedi=new HashMap();
    try{
      List<List> spedList=DatiProdUtils.getInstance().getSpedizioniColliGg(con, giornoCorrenteN,Boolean.TRUE);
      if(spedList==null)
        return spedi;
      
      for(List rec:spedList){
         
         String speTmp=ClassMapper.classToString(rec.get(0));
         //le spedizioni in VDL sono al massimo un numerico di 5
         Integer speVDL=ClassMapper.classToClass(speTmp.substring(1), Integer.class);
         Integer hhSpe=ClassMapper.classToClass(rec.get(2), Integer.class);
         Integer mmSpe=ClassMapper.classToClass(rec.get(3), Integer.class);
         Integer minutiSpe=hhSpe*60+mmSpe;
         String tipoSpe=InfoMancanteVdlBean.TIPOMANCANTEGENERICO;
         if(minutiSpe>=MINUTIPOM){
          tipoSpe=InfoMancanteVdlBean.TIPOMANCANTEPOM;
         }else if(minutiSpe>=MINUTIMATT && minutiSpe<MINUTIPOM){
          tipoSpe=InfoMancanteVdlBean.TIPOMANCANTEMATT;
           
         }
         spedi.put(speVDL, tipoSpe);
         
         //spedi.put(speVDL, minutiSpe);
         
      }
    } catch(QueryException q){
      _logger.error("Impossibile verificare le spedizioni della giornata  ->"+q.getMessage());
      addError("Impossibile verificare le spedizioni della giornata  ->"+q.toString());
    } catch(SQLException s){
      _logger.error("Impossibile verificare le spedizioni della giornata  ->"+s.getMessage());
      addError("Impossibile verificare le spedizioni della giornata  ->"+s.toString());
    }
    
    return spedi;
  }
  
  private String getFileNameMancanti(String path,String fileName,String extFile,Date dataRf){
    String fName=null;
    String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileName, dataRf);
    fNameNew=fNameNew.replace(extFile, "");
    
//    Date dataIni=getDataOraRif(dataRf,ELABMATT);
//    Date dataFin=getDataOraRif(dataRf,ELABPOM);
    
    Date diecitrenta=DateUtils.addMinutes(dataRf, 700);
    Date tredicitrenta=DateUtils.addMinutes(dataRf, 780);
    Date sedici=DateUtils.addMinutes(dataRf, 960);
    
    if(ELABMATT.equals(typeOper)){
      fName=DatiProdUtils.getInstance().getFileName(path,fNameNew , null, diecitrenta);
    }else if(ELABPOM.equals(typeOper)){
      fName=DatiProdUtils.getInstance().getFileName(path, fNameNew, tredicitrenta, null);
    }
    
    
    
    if(fName!=null && fName.contains(extFile))
      return fName;
    
    return"";
  }
        
  
  private List getListMancanti(String fileName,Date dataOraRif){
    if(StringUtils.isEmpty(fileName)){
      addError("Attenzione nome file VDL non valorizzato");
      return null;
    }
    Map clienti=new HashMap();
    List mancanti=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      
      while(riga!=null && !riga.isEmpty()){
        if(count>1){
          if(riga.contains("\"")){
            riga=riga.replaceAll("\"","");
          }
          List l =ArrayUtils.getListFromArray(riga.split(";"));
          InfoMancanteVdlBean bean=new InfoMancanteVdlBean(dataOraRif);
          //bean.setDataRif(ClassMapper.classToClass(l.get(0),Date.class));
          bean.setCodCollo(ClassMapper.classToString(l.get(0)));
          bean.setDescrVettore(ClassMapper.classToString(l.get(1)));
          bean.setDescrCollo(ClassMapper.classToString(l.get(2)));
          if(bean.getDescrCollo().length()>40)
            bean.setDescrCollo(bean.getDescrCollo().substring(0, 40));
          
          bean.setNOrdine(ClassMapper.classToString(l.get(3)));
          bean.setLineaLogica(ClassMapper.classToString(l.get(5)));
          bean.setCodCliente(ClassMapper.classToString(l.get(6)));
          
          Integer nBox=ClassMapper.classToClass(l.get(8),Integer.class);
          Integer nSpe=ClassMapper.classToClass(l.get(11),Integer.class);
          
          bean.setBox(nBox);
          bean.setPedana(ClassMapper.classToClass(l.get(9),Integer.class));
          bean.setNSpedizione(nSpe);
          
          // imposto la descrizione del centro di lavoro da Movex.
          bean.setDescrLineaLogica(  ClassMapper.classToString( mapCdlMvxDescr.get(bean.getLineaLogica()) )   );

          if(mapFornMvx.containsKey(bean.getLineaLogica()))
            bean.setCodFornitore(mapFornMvx.get(bean.getLineaLogica()));
          
          
          bean.setRifGg(InfoMancanteVdlBean.TIPOMANCANTEPOM);
          if(ELABMATT.equals(typeOper)){
            bean.setRifGg(InfoMancanteVdlBean.TIPOMANCANTEMATT);
          }
          
          bean.setTipoSpedizione("N");
          bean.setEstero(Boolean.FALSE);
          
          //imposto le info della Linea secondo VDL
          Integer lineaLavN=bean.getLineaLogNumber();
          
          if(mapCodiciLineeLav.containsKey(lineaLavN)){
            String descLineaVdl=mapCodiciLineeLav.get(lineaLavN);
            bean.setDescrLineaVdl(descLineaVdl);

            Linea4MancantiVDL lVdl=mapLineeVdl.get(descLineaVdl);
            bean.setCodAziendaVdl(lVdl.getCodiceAz());
            bean.setDescrStabVdl(lVdl.getDescrStab());
           
          }else{
            addWarning("Attenzioneper il giorno "+ dataOraRif+ "collo n."+bean.getCodCollo()+" associato alla linea: "+bean.getLineaLogica()+ " NON CODIFICATA !!");
          }
           mancanti.add(bean);
         
          
          
         
        }
        count++;
        riga=bfR.readLine();
      }
      _logger.info("File "+fileName+" righe processate:"+count--);
    } catch(IOException i){
      _logger.error("Errore in fase di accesso al file dei mancanti da VDL -->"+i.getMessage());
      addError("Errore in fase di accesso al file dei mancanti prodotto da VDL"+i.toString());
      
              
    } finally{
       try {
        if(bfR!=null)
          bfR.close();
      
         if(fR!=null)
          fR.close();
        } catch (IOException ex) {
          _logger.error("Errore in fase di chiusura del file "+fileName);
        }  
    }  
    
    return mancanti;
  }
  
  private void loadCodiciLineeLog() {
    mapCodiciLineeLav=new HashMap();
    if(mapLineeVdl==null || mapLineeVdl.isEmpty())
      return;
    
    Set keys=mapLineeVdl.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()){
      String descrLinea =(String) iter.next();
      Linea4MancantiVDL lV=mapLineeVdl.get(descrLinea);
      mapCodiciLineeLav.putAll(lV.getMapCodiciLineeLog());
    }
    
  }
  
  
  private Date getDataOraRif(Date dataRf,String typeOra){
    Date dataTM=null;
    
    if(dataRf==null)
      return dataTM;
    
    try{
      String dtCS=DateUtils.DateToStr(dataRf, "ddMMyyyy");
      String ora=TIMEMATTINA;
      if(typeOra.equals(ELABPOM))
         ora=TIMEPOMERIGGIO;     
    
      dataTM=DateUtils.StrToDate(dtCS+" "+ora, "ddMMyyyy HH:mm:ss");
              
      
    } catch(ParseException p){
      _logger.error("Problemi in fase di conversione dei dati --> "+p.getMessage());
      addError(" Errore in fase di conversione dei dati --> "+p.getMessage());
    }
    return dataTM;
  }
  

  private String prepareTextMsg(Connection con,Date dataRif) {
   List<List> l=new ArrayList();
   StringBuilder msg=new StringBuilder();
   StringBuilder s=new StringBuilder();
   String tipoG=InfoMancanteVdlBean.TIPOMANCANTEMATT;
   
   if(typeOper.equals(ELABPOM))
     tipoG=InfoMancanteVdlBean.TIPOMANCANTEPOM;
   
   s.append(" select  substr(VDCCOL, 1, 3) comm,vlnord,TRIM(VDDFAC), TRIM(vddcdl), vdstvt , VDTPSP, count(*) as nMan ").append(
            "\n from mcobmoddta.zdpvld left outer join mcobmoddta.zdpvll on vdccon=vlccon and vddfac=vldfac and vddcdl=vldcdl ").append(
            "\n where 1=1").append(
            " and vddtrf=").append(JDBCDataMapper.objectToSQL(dataRif)).append(
            " and vdrfgg=").append(JDBCDataMapper.objectToSQL(tipoG)).append(
            " and vdtpco='C' ").append( //per ora filtriamo solo i mancanti della commessa principale
            " group by   substr(VDCCOL, 1, 3) ,vlnord,VDDFAC ,vddcdl,vdstvt,VDTPSP ").append(
            " order by 6,1,2 " );
   
   
    try {
      ResultSetHelper.fillListList(con, s.toString(), l);
    } catch (SQLException ex) {
      _logger.error("Errore in fase di lettura dei mancanti della giornata per invio mail -->"+ex.getMessage());
      addError("Impossibile inviare la mail relativa ai mancanti della giornata --> Errore in fase di lettura dei dati");
    }
   if(l!=null && !l.isEmpty()){
     msg.append("-- Mancanti in data ").append(DateUtils.dateToStr(dataRif, "dd/MM/yyyy")).append(" -- \n\n");
     Integer totM=Integer.valueOf(0);
     
     for(List r:l){
       totM+=ClassMapper.classToClass(r.get(6),Integer.class);
       msg.append(" - Commessa :").append(ClassMapper.classToString(r.get(0))).append(" - Stab : ").append(
          ClassMapper.classToString(r.get(2))).append(" - Linea : ").append(
          //ClassMapper.classToString(r.get(3))).append(" - Tipo Vett : ").append(
          //ClassMapper.classToString(r.get(4))).append(" - Tipo Sped : ").append(
          ClassMapper.classToString(r.get(5))).append(" --> mancanti n. ").append(ClassMapper.classToString(r.get(6))).append(" \n");
               
     }
     msg.append(" \n\n Totale mancanti  n. ").append(totM);
   } 
   
   return msg.toString();
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestMancantiDettaglioFromVDL.class);

  

 
  
  

  
}
