/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import static colombini.elabs.ElabGestMancantiFromVDL.ELABMATT;
import db.persistence.PersistenceManager;
import colombini.model.InfoMancanteVdlBeanTEST;
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
import java.io.File;

/**
 *
 * @author lvita
 */
public class ElabMancantiDettaglioTEST extends ElabClass{

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
      invioMail=Boolean.TRUE;
    
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
    String cliToExclude=ClassMapper.classToString(getElabProperties().get(NameElabs.LSTCLIEXDETTVDL));
    
    List listCli=new ArrayList();
    if(!StringUtils.isEmpty(cliToExclude))
      listCli=ArrayUtils.getListFromArray(cliToExclude.split(","));
    
    Date dataT=dataRif;
    
    while(DateUtils.beforeEquals(dataT, dataFine)){
      Long giornoCorrenteN=DateUtils.getDataForMovex(dataT);
      Date dataFileF=null;
      //se ho già elaborato passo al giorno successivo
      if(checkElab && isElabExecuted(con, dataT) )
        continue;
      
      //carico le spedizioni della giornata
      Map mappaSpe=getSpedizioniGg(con,giornoCorrenteN);
      if(mappaSpe==null || mappaSpe.isEmpty()){
        addWarning("Spedizioni non presenti per il giorno "+dataT);
        dataT=DateUtils.addDays(dataT, 1);
        continue;
      }
      //carico le info sui cdL da Movex
      loadMapCdL(con);

      try{
        //carichiamo le informazioni delle linee da gestire per i mancanti
        mapLineeVdl=Linea4MancantiVDL.getMapLineeMancantiVDL(con);
        //carichiamo i codici delle linee logiche associate alle linee dei mancanti
        loadCodiciLineeLog();
        Map mapSpeBoxs =new HashMap();
        String nomeFileDef=getFileNameMancanti(pathFile,nomeFile,extFile,dataT);
        if(!StringUtils.IsEmpty(nomeFileDef)){
            dataFileF= new Date(( new File(nomeFileDef).lastModified() ) );
        }
        //lettura file mancanti
        List mancanti=getListMancanti(nomeFileDef,mapSpeBoxs,listCli);
        checkMapSpedizioni(mappaSpe,mapSpeBoxs);
        //elaborazione dati
        if(mancanti!=null && !mancanti.isEmpty()){
          //gestione dati su db
          List<InfoMancanteVdlBeanTEST> mancantiToSave=getMancantiToSave( con,mancanti, mappaSpe,dataT);
          if(mancantiToSave!=null && mancantiToSave.size()>0){
            try{
              PersistenceManager man=new PersistenceManager(con);
              InfoMancanteVdlBeanTEST beanOne=(InfoMancanteVdlBeanTEST) (IBeanPersSIMPLE) mancantiToSave.get(0);
              man.deleteDtFromBean(beanOne);

              for(InfoMancanteVdlBeanTEST mancante:mancantiToSave){               
                man.storeDtFromBean(mancante);
              }
              
              if(invioMail){
                MailSender mS=new MailSender();
                MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_ELBMANCANTITEST);
                String orario = typeOper.equals(ELABMATT) ? "09:00" : "13:30" ;
                if(dataFileF!=null)
                  orario=DateUtils.dateToStr(dataFileF, "HH:mm");
                beanInfo.setObject(beanInfo.getObject().trim()+" in data "+DateUtils.dateToStr(beanOne.getDataRif(), "dd/MM/yyyy")+" ore "+orario );
                beanInfo.setText(prepareTextMsg(con,dataT));

                System.out.println(beanInfo.getText());
                mS.send(beanInfo);
              }
            }catch(SQLException s){
              _logger.error(" Errore in fase di cancellazione/salvataggio dei mancanti per il giorno "+giornoCorrenteN+" --> "+s.getMessage());
              addError(" Errore in fase di cancellazione/salvataggio dei mancanti per il giorno "+giornoCorrenteN+" --> "+s.toString());
            }  
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
    try{
      List<List> cdLList=DatiProdUtils.getInstance().getListCentriLavoro(con, Boolean.TRUE);
      if(cdLList==null){
        addError("Attenzione lista centri di lavoro vuota");
        return ;
      }
      
      for(List rec:cdLList){
         String codCdl=ClassMapper.classToString(rec.get(1)).trim();
         String descrCdl=ClassMapper.classToString(rec.get(2)).trim();
         
         if(!mapCdlMvxDescr.containsKey(codCdl))
           mapCdlMvxDescr.put(codCdl,descrCdl); 
         
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
    
    InfoMancanteVdlBeanTEST bean=new InfoMancanteVdlBeanTEST();
    bean.setDataRif(data);
    if(ELABMATT.equals(typeOper)){
      bean.setRifGg(InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT);
    }else{ 
      bean.setRifGg(InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);
    }
    
    List l=new ArrayList();
    l.add(InfoMancanteVdlBeanTEST.VDDTRF);
    l.add(InfoMancanteVdlBeanTEST.VDRFGG);
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
         String tipoSpe=InfoMancanteVdlBeanTEST.TIPOMANCANTEGENERICO;
         if(minutiSpe>=MINUTIPOM){
          tipoSpe=InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM;
         }else if(minutiSpe>=MINUTIMATT && minutiSpe<MINUTIPOM){
          tipoSpe=InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT;
           
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
    
    Date mezzogiorno=DateUtils.addMinutes(dataRf, 720);
    
    if(ELABMATT.equals(typeOper)){
      fName=DatiProdUtils.getInstance().getFileName(path,fNameNew , null, mezzogiorno);
    }else if(ELABPOM.equals(typeOper)){
      fName=DatiProdUtils.getInstance().getFileName(path, fNameNew, mezzogiorno, null);
    }
    
    if(fName.contains(extFile))
      return fName;
    
    return"";
  }
        
  
  private List getListMancanti(String fileName,Map <Integer,List> mapSpeBoxs,List<String> listCli ){
    if(StringUtils.isEmpty(fileName)){
      addError("Attenzione nome file VDL non valorizzato");
      return null;
    }
    
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
          InfoMancanteVdlBeanTEST bean=new InfoMancanteVdlBeanTEST();
          bean.setDataRif(ClassMapper.classToClass(l.get(0),Date.class));
          bean.setCodCollo(ClassMapper.classToString(l.get(1)));
          bean.setDescrVettore(ClassMapper.classToString(l.get(2)));
          bean.setDescrCollo(ClassMapper.classToString(l.get(3)));
          if(bean.getDescrCollo().length()>40)
            bean.setDescrCollo(bean.getDescrCollo().substring(0, 40));
          
          bean.setNOrdine(ClassMapper.classToString(l.get(4)));
          bean.setLineaLogica(ClassMapper.classToString(l.get(6)));
          bean.setCodCliente(ClassMapper.classToString(l.get(7)));
          
          Integer nBox=ClassMapper.classToClass(l.get(9),Integer.class);
          Integer nSpe=ClassMapper.classToClass(l.get(12),Integer.class);
          
          bean.setBox(nBox);
          bean.setPedana(ClassMapper.classToClass(l.get(10),Integer.class));
          bean.setNSpedizione(nSpe);
          bean.setStatoVettore(ClassMapper.classToClass(l.get(14),Integer.class));
          
          //modifica del 09/12/2016
          //verifico le spedizioni e i relativi box solo per i colli della commessa (NO MINI NO NANO!!!)
          if(InfoMancanteVdlBeanTEST.TIPOMANCATECOMMESSA.equals(bean.getTipoCommessa())) {
            List boxs=null;
            if(!mapSpeBoxs.containsKey(nSpe)){
              boxs=new ArrayList();
            }else{
              boxs=mapSpeBoxs.get(nSpe);
            }
            if(!boxs.contains(nBox))
              boxs.add(nBox);
            
            mapSpeBoxs.put(nSpe, boxs);
          }
          
          if(!listCli.contains(bean.getCodCliente().toString()) )
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
  
  private List getMancantiToSave(Connection con,List<InfoMancanteVdlBeanTEST> mancanti, Map mappaSpe,Date dataRf ) {
    List newList=new ArrayList();
    // mappa che per ogni codice cliente indica TRUE se cliente estero
    Map clienti=new HashMap();
    Date dataOraRif=getDataOraRif(dataRf,typeOper);
    
    
    
    for(InfoMancanteVdlBeanTEST bean:mancanti){
      try{
        
        
        
        bean.setDataRif(dataOraRif);
        Integer minDt=DateUtils.getMinutesTot(bean.getDataRif());
        if(minDt<MINUTIPOM){
          bean.setRifGg(InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT);
        }else{ 
          bean.setRifGg(InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);
        }
        //Integer nSped=bean.getNSpedizione();
//        Integer minutiSpe=(Integer) mappaSpe.get(nSped);
        
        // imposto la descrizione del centro di lavoro da Movex.
        bean.setDescrLineaLogica(  ClassMapper.classToString( mapCdlMvxDescr.get(bean.getLineaLogica()) )   );
        
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
        
        
        
        //1) Assegno al bean il tipo di spedizione che ho impostato nella funzione che verifica le spedizioni
        bean.setTipoSpedizione((String) mappaSpe.get(bean.getNSpedizione()));
        
        //2) verifico se il cliente di riferimento è estero 
        if(!clienti.containsKey(bean.getCodCliente())){
          Boolean isEstero=InfoMancanteVdlBeanTEST.checkClienteEstero(con, bean.getCodCliente());
          bean.setEstero(isEstero);
          clienti.put(bean.getCodCliente(),isEstero);  
        }else{
          bean.setEstero((Boolean) clienti.get(bean.getCodCliente()));
        }
      
        if(onlyVettAttivi){
          if(bean.isVettoreAttivo())
            newList.add(bean);
        }else{  
                  
        // nel caso di mancante associato ad una spedizione della mattina o ad una spedizione senza orario
        // allora il mancante deve essere sempre considerato
        // l'unica eccezione è se relativo ad un estero e il suo camion è disattivo 
          if(bean.isMancanteMattina() || bean.isMancanteGenerico() ){
             if(!bean.isEstero()){
               newList.add(bean);
             }else if(bean.isEstero() && bean.isVettoreAttivo()){
               newList.add(bean);
             }

          //nel caso il mancante sia relativo ad una spedizione del pomeriggio
          // allora deve essere preso in considerazione solo se l'elaborazione è quella pomeridiana

          }else if(bean.isMancantePomeriggio() ){
            //aggiungo alla lista da salvare solo i mancanti del pomeriggio 
            if(this.typeOper.equals(ELABPOM)){
              if(!bean.isEstero()){
               newList.add(bean);
              }else if(bean.isEstero() && bean.isVettoreAttivo()){
               newList.add(bean);
             }
            }
          }
        } 
      }catch(SQLException s){
        _logger.error("Errore in fase di verifica della nazione del cliente per il collo n. "+bean.getCodCollo()+" --> "+s.getMessage());
        addError("Errore in fase di verifica della nazione del cliente per il collo n. "+bean.getCodCollo()+" --> "+s.toString());
      }
      
    }
    
    return newList;
  }

//   /**
//    * Metodo che verifica che se la spedizione è della mattina ma ha associata almeno un collo con n box
//    * compreso tra 8 e 14 allora devo considerarla come spedizione del pomeriggio
//    * @param bean
//    * @param mappaSpe 
//    */
//   private void checkTipoSpedizione(InfoMancanteVdlBean bean, Map mappaSpe) {
//     String tipoSpe =(String) mappaSpe.get(bean.getNSpedizione());
//     if(InfoMancanteVdlBean.TIPOMANCANTEMATT.equals(tipoSpe)){
//       Integer nBox=bean.getBox();
//       if(nBox>7 && nBox<=14)
//        mappaSpe.put(bean.getNSpedizione(),InfoMancanteVdlBean.TIPOMANCANTEPOM);  
//     }
//       
//     
//   }

//     private void checkMapSpedizioni(Map <Integer,String> mappaSpe,Map <Integer,List> mappaSpeBoxs){
//       Set keys =mappaSpe.keySet();
//       Iterator iter =keys.iterator();
//       while(iter.hasNext()){
//         Integer nSpe=ClassMapper.classToClass(iter.next(),Integer.class);
//         String tipoSpe=mappaSpe.get(nSpe);
//         if(mappaSpeBoxs.containsKey(nSpe) && InfoMancanteVdlBean.TIPOMANCANTEMATT.equals(tipoSpe)){
//           List<Integer> boxs=mappaSpeBoxs.get(nSpe);
//           Boolean exBPom=Boolean.FALSE;
//           Boolean exB7=Boolean.FALSE;
//           Boolean exBMat=Boolean.FALSE;
//           for(Integer box:boxs){
//             if(box<7){
//               exBMat=Boolean.TRUE;
//             } else  if(box>7 && box<=14){
//               exBPom=Boolean.TRUE; 
//             }else if(box==7){
//               exB7=Boolean.TRUE;
//             }
//           }
//           //considero una spedizione del pomeriggio solo se c'è un box >7 e il box 7
//           //oppure se ci sono solo box del pomeriggio
//           if(exBPom && exB7 && !exBMat)
//             mappaSpe.put(nSpe, InfoMancanteVdlBean.TIPOMANCANTEPOM);
//           
//           if(exBPom && !exB7 && !exBMat)
//             mappaSpe.put(nSpe, InfoMancanteVdlBean.TIPOMANCANTEPOM);
//           
//         }
//         
//       }
//       
//     }
  private void checkMapSpedizioni(Map <Integer,String> mappaSpe,Map <Integer,List> mappaSpeBoxs){
       Set keys =mappaSpe.keySet();
       Iterator iter =keys.iterator();
       while(iter.hasNext()){
         Integer nSpe=ClassMapper.classToClass(iter.next(),Integer.class);
         String tipoSpe=mappaSpe.get(nSpe);
         if(mappaSpeBoxs.containsKey(nSpe) ){
           List<Integer> boxs=mappaSpeBoxs.get(nSpe);
           
           Boolean exBM=Boolean.FALSE;
           Boolean exBP=Boolean.FALSE;
           Boolean exB7=Boolean.FALSE;
           Boolean exB8=Boolean.FALSE;
           Boolean exB15=Boolean.FALSE;
           Boolean exB16=Boolean.FALSE;
           for(Integer box:boxs){
             //05/06/2017 aggiunto controllo sui box del carrelli
             if(box<7 || (box>=17 && box<21) ){
               exBM=Boolean.TRUE;
             } else if(box==7 || box==21){
               exB7=Boolean.TRUE;
             } else  if(box>=8 && box<=14){
//               if(box==8)
//                 exB8=Boolean.TRUE;
               
               exBP=Boolean.TRUE; 
             }else if(box==15){
               exB15=Boolean.TRUE;
             }else if(box==16){
               exB16=Boolean.TRUE;
             }
             
           }
           if(InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT.equals(tipoSpe)){
           //modifiche in base a mail di Nicola Ranocchini del 23/11/2016
            if(exB7 && exB16 && !exBM)
              mappaSpe.put(nSpe, InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);
            //03/01/2016   aggiunto controllo che non ci sia il box 7 per spostare la spedizione del pomeriggio
            if(exBP && exB15 && !exBM && !exB7)
              mappaSpe.put(nSpe, InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);

            if(exB16 && !exB15  )
              mappaSpe.put(nSpe, InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);

            if(exBP  && !exBM && !exB15  )
              mappaSpe.put(nSpe, InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM);
           
           }else{
             if(!exB16 && !exBP )
               mappaSpe.put(nSpe, InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT);
             
//             condizione non abilitata per NON SOVRASTIMARE la mattina (spedizioni a cavallo della giornata (13:00) 
//             spostate la mattina  
//             if(exB15 && exB7 && exB8)
//               mappaSpe.put(nSpe, InfoMancanteVdlBean.TIPOMANCANTEMATT);
             
           }
         }
         
       }
       
     }
 
  private String prepareTextMsg(Connection con,Date dataRif) {
   List<List> l=new ArrayList();
   StringBuilder msg=new StringBuilder();
   StringBuilder s=new StringBuilder();
   String tipoG=InfoMancanteVdlBeanTEST.TIPOMANCANTEMATT;
   
   if(typeOper.equals(ELABPOM))
     tipoG=InfoMancanteVdlBeanTEST.TIPOMANCANTEPOM;
   
   s.append(" select  substr(VDCCOL, 1, 3) comm, vlnord, TRIM(VDDFAC), TRIM(vddcdl), vdstvt , VDTPSP, count(*) as nMan ").append(
            "\n from mcobmoddta.zdpvd2 left outer join mcobmoddta.zdpvll on vdccon=vlccon and vddfac=vldfac and vddcdl=vldcdl ").append(
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
          ClassMapper.classToString(r.get(3))).append(" - Tipo Vett : ").append(
          ClassMapper.classToString(r.get(4))).append(" - Tipo Sped : ").append(
          ClassMapper.classToString(r.get(5))).append(" --> mancanti n. ").append(ClassMapper.classToString(r.get(6))).append(" \n");
               
     }
     msg.append(" \n\n Totale mancanti  n. ").append(totM);
   } 
   
   return msg.toString();
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabMancantiDettaglioTEST.class);

  

 
  
  

  
}
