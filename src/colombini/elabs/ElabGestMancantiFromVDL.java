/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.conn.ColombiniConnections;
import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.model.Linea4MancantiVDL;
import colombini.model.persistence.MancantiVDLBean;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCommToShip;
import colombini.util.DatiCommUtils;
import colombini.util.DatiProdUtils;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import utils.ArrayListSorted;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabGestMancantiFromVDL extends ElabClass{

  private String typeOper="";
  private Date dataRif;
  private Boolean invioMail=null;
  private Integer nCommForce = null; //variabile per gestire lo slittamento dei carichi 
                                     // valorizzato dal parametro che vi   
  private String descrComm="";
  //mappa che contiene per ciascuna linea logica la descrizione relativa del raggruppamento
  private Map<Integer,String> mapCodiciLineeLav=null;
  //mappa contenente per ciascuna Linea descrittiva il bean associato L
  private Map<String,Linea4MancantiVDL> mapLineeVdl=null;
  

  public final static String ELABMATT="MATT";
  public final static String ELABPOM="POM";
  
  public final static String FORCENCOMM="FRCNCOMM";
  public final static String DESCRCOMM="DESCRCOMM";
  
  public final static String TIMEMATTINA="06:00:00";
  public final static String TIMEPOMERIGGIO="13:00:00";
  
  
  
  @Override
  public Boolean configParams() {
    Map parameter =this.getInfoElab().getParameter();
    if(parameter.get(ALuncherElabs.TYPEOPR)!=null){
      this.typeOper=ClassMapper.classToString(parameter.get(ALuncherElabs.TYPEOPR));
    }
    if(StringUtils.isEmpty(typeOper))
      return Boolean.FALSE;
    
    
    dataRif=(Date) parameter.get(ALuncherElabs.DATAINIELB);
    if(dataRif==null)
      return Boolean.FALSE;
    
    //per gestire un eventuale non invio della mail
    if(parameter.get(ALuncherElabs.MAIL)!=null){
      this.invioMail=ClassMapper.classToClass(parameter.get(ALuncherElabs.MAIL),Boolean.class);
    }
    //se non viene specificato il default è YEES
    if(invioMail==null)
      invioMail=Boolean.TRUE;
    //-----------------------
    
    if(parameter.get(FORCENCOMM)!=null){
       nCommForce=ClassMapper.classToClass(parameter.get(FORCENCOMM),Integer.class);
    }  
    
    if(parameter.get(DESCRCOMM)!=null){
       descrComm=ClassMapper.classToString(parameter.get(DESCRCOMM));
    }
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Elaborazione gestione mancanti da VDL");
    
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBMANCANTIFROMVDL);
    String pathFile=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEVDL));
    String nomeFileMatt=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEVDLMT));
    String nomeFilePom=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEVDLPM));
    String extFile=ClassMapper.classToString(getElabProperties().get(NameElabs.EXTFILEVDL));
    Long giornoCorrenteN=DateUtils.getDataForMovex(dataRif);
    
    Date  dataFileF=null;
    Integer comm=null;
    //gestione della forzatura per il numero commessa
    if(nCommForce!=null && (nCommForce>0 && nCommForce<365) ){
      comm=nCommForce;  
    }else{
      comm=getCommessa(con,giornoCorrenteN);
      //comm=getCommessa(con,giornoCorrenteN-1); //per casi dove la Data commessa è spostata

    }
    
    if(comm==null){
        addWarning("Commessa non presente per il giorno "+dataRif);
        return;
    }
    
    //verifica se i dati sono già presenti  a sistema
    //checkDatiComm(con,comm,giornoCorrenteN);
    //carico il numero di colli relativi alla commessa
    elabColliCommessa(con, comm,giornoCorrenteN);
    try{
      //carichiamo le informazioni delle linee da gestire per i mancanti
      mapLineeVdl=Linea4MancantiVDL.getMapLineeMancantiVDL(con);
      //carichiamo i codici delle linee logiche associate alle linee dei mancanti
      loadCodiciLineeLog();
      
      gestColliLineeVdl(con,comm,dataRif);
      
      String nomeFile=nomeFileMatt;
      if(typeOper.equals(ELABPOM))
        nomeFile=nomeFilePom;
    
      String nomeFileDef=getFileNameMancanti(pathFile,nomeFile,extFile);
      
      if(!StringUtils.IsEmpty(nomeFileDef)){
        dataFileF= new Date(( new File(nomeFileDef).lastModified() ) );
     }
      //lettura file mancanti
      List mancanti=getListMancanti(nomeFileDef);
      //elaborazione dati
      if(mancanti!=null && !mancanti.isEmpty()){
        Date dataOraRif=getDataOraRif();
        //gestione dati su db
        List listToSave=elabDatiMancanti(con,mancanti,comm,dataOraRif);     
        
        PersistenceManager man=new PersistenceManager(con);
        Map mapDel=new HashMap();
        mapDel.put(MancantiVDLBean.VMDTRF,dataOraRif); mapDel.put(MancantiVDLBean.VMCOMM,comm);
        man.deleteDt(new MancantiVDLBean(), mapDel);
        man.storeDtFromBeans(listToSave);
      
        //invio mail
        if(invioMail){
          ArrayListSorted<Linea4MancantiVDL> lineeBeans=new ArrayListSorted<>("progressivoVis");
          lineeBeans=prepareListToPrint();
          MailSender mS=new MailSender();
          MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_ELBMANCANTI);
          String orario = typeOper.equals(ELABMATT) ? "09:00" : "13:30" ;
          if(dataFileF!=null)
            orario=DateUtils.dateToStr(dataFileF, "HH:mm");
          beanInfo.setObject(beanInfo.getObject().trim()+" in data "+DateUtils.dateToStr(dataOraRif, "dd/MM/yyyy")+" ore "+orario);//+DateUtils.dateToStr(dataOraRif, "HH:mm") 
          beanInfo.setText(prepareTextMsg(comm,lineeBeans));

          System.out.println(beanInfo.getText());
          mS.send(beanInfo);
        }
      
      }else{
        addWarning ("Mancanti non trovati per giorno "+dataRif);
      }
    }catch(SQLException s){
       _logger.error("Errore in fase di cancellazione/salvataggio dati -->"+s.getMessage());
       addError("Errore in fase di cancellazione/salvataggio dati -->"+s.toString());
    }
    
  }
  
  private String getFileNameMancanti(String path,String fileName,String extFile){
    String fName=null;
    String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileName, dataRif);
    fNameNew=fNameNew.replace(extFile, "");
    
    fName=DatiProdUtils.getInstance().getFileName(path,fNameNew , null, null);
    
    
    if(fName!=null && fName.toLowerCase().contains(extFile))
      return fName;
    
    return"";
  }
 
  
  private Integer getCommessa(Connection con,Long dataCorrN){
    Integer comm=null;
    try{
    QueryCommToShip qry=new QueryCommToShip();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataCorrN);
    qry.setFilter(FilterQueryProdCostant.FTONLYCOMM, "YES");

    Object []obj = ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
    if(obj!=null && obj.length>0)
      comm=ClassMapper.classToClass(obj[1],Integer.class);
    
    } catch(QueryException q){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+q.getMessage());
      addError("Impossibile reperire il numero di commessa -->"+q.toString());
      
    } catch(SQLException s){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+s.getMessage());
      addError("Impossibile reperire il numero di commessa -->"+s.toString());
      
    }
    
    return comm;
  }
  
  
  private void elabColliCommessa(Connection con,Integer commessa,Long dataCommessa){
    try{
      DatiProdUtils.getInstance().elabColliVolumiCommessa(con, commessa, dataCommessa);
      
    } catch(SQLException s){
      _logger.error("Errore in fase di caricamento dei dati relativi al numero di colli della commessa "+commessa+" -->"+s.getMessage());
      addError("Errore in fase di caricamento dei dati relativi al numero di colli della commessa "+commessa+" -->"+s.toString());
    } catch (QueryException q){
      _logger.error("Errore in fase di caricamento dei dati relativi al numero di colli della commessa "+commessa+" -->"+q.getMessage());
      addError("Errore in fase di caricamento dei dati relativi al numero di colli della commessa "+commessa+" -->"+q.toString());
    }
  }
  
  
  
  private List getListMancanti(String fileName){
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
          mancanti.add(l);
        }
        count++;
        riga=bfR.readLine();
      }
      _logger.info("File "+fileName+" righe processate:"+count--);
    } catch(IOException i){
      _logger.error("Errore in fase di accesso al file dei mancanti da VDL -->"+i.getMessage());
      addError("Errore in fase di accesso al file dei mancanti prodotto da VDL"+i.toString());
      
    }  finally{
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
  
  
  
  private List elabDatiMancanti(Connection con,List<List> mancanti,Integer comm,Date dataOraRif){
    List list=new ArrayList();
    
    
    if(mancanti==null|| mancanti.isEmpty()){
      addWarning("Attenzione lista mancanti da VDL vuota");
      return list;
    }
    for(List record:mancanti){
      String descrL="";
      Linea4MancantiVDL lVDL=null;
      MancantiVDLBean mb=null;
      
      Integer codLinea=ClassMapper.classToClass(record.get(0),Integer.class);
      
      mb=new MancantiVDLBean(dataOraRif, comm, codLinea);
      mb.setnMancanti(ClassMapper.classToClass(record.get(1),Integer.class));
      //Integer colliLinea=getNumColliLinea(con, comm, codLinea,dataOraRif);
      //mb.setnColli(colliLinea);
      if(!mapCodiciLineeLav.containsKey(codLinea)){
        addError("Attenzione linea presente in file mancanti non codificata : "+codLinea + " - n. mancanti :"+mb.getnMancanti());
        mb.setCodAzienda("");mb.setDescrStab("");mb.setDescrLinea("");
      }else{
        descrL=mapCodiciLineeLav.get(codLinea);
        lVDL=mapLineeVdl.get(descrL);
        //aggiorno il bean alla linea (x gestione mail)
        lVDL.addMancantiVdlBean(mb);
        mb.setnColli(lVDL.getTotColliLinee());
      }
      list.add(mb);
    }
    
    
    return list;
  }
  
  private void gestColliLineeVdl(Connection con,Integer numcomm,Date dataRif ) {
    try{
      Map mapLineeColliColom=DatiCommUtils.getInstance().getColliCommLineeLavColom(con, numcomm, dataRif);
      Map mapLineeColliFebal=getColliCommessaFebal( numcomm, dataRif);
      
      Set s=mapLineeVdl.keySet();
      Iterator iter= s.iterator();
      while(iter.hasNext()){
        String sLinea=ClassMapper.classToString(iter.next());
        Linea4MancantiVDL linea =mapLineeVdl.get(sLinea);
        List<Integer> linee=linea.getListCodiciLinee();
        Integer totClLinea=Integer.valueOf(0);
        for(Integer codLinea:linee){
          if(mapLineeColliColom.containsKey(codLinea))
            totClLinea+=(Integer)mapLineeColliColom.get(codLinea);
          if(mapLineeColliFebal.containsKey(codLinea))
            totClLinea+=(Integer)mapLineeColliFebal.get(codLinea);
        }
        linea.setTotColliLinee(totClLinea);
        
      }
      
    }catch (SQLException s){
      _logger.error("Errore in fase di estrazione dei dati della commessa"+numcomm+" -->" + s.getMessage());
      addError("Errore in fase di estrazione dei dati della commessa"+numcomm);
    }
  }

  
  
//  private Integer getNumColliLinea(Connection con ,Integer comm, Integer codLinea,Date dataRif){
//    Integer colliTot=Integer.valueOf(0);
//    PreparedStatement ps=null;
//    ResultSet rs=null;
//    String qry=" select count(*) as tot from mcobfilp.sc"+comm+"col where  clnart=0 and cllinp=? ";
//    try{
//      ps=con.prepareStatement(qry);
//      ps.setInt(1, codLinea);
//      
//      rs=ps.executeQuery();
//      
//      while(rs.next()){
//        colliTot=rs.getInt(1);
//      }
//    } catch(SQLException s){
//      _logger.error(" Errore in fase di esecuzione dello statement per la linea "+codLinea+" --> "+s.getMessage());
//    } finally{
//      try{
//        if(ps!=null)
//          ps.close();
//        if(rs!=null)
//          rs.close();
//      }catch(SQLException s){
//        _logger.error("Errore in fase di chiususra dello statement --> "+s.getMessage()); 
//      }
//    } 
//    if(colliTot==0){
//      //colliTot=getNumColliLineaFebal( comm, codLinea,dataRif);
//    }
//    return colliTot; 
//  }
  
  
  private Map getColliCommessaFebal(Integer comm,Date dataRif) {
    Integer nc=Integer.valueOf(0);
    Connection con=null;
    PreparedStatement ps=null;
    ResultSet rs=null;
    Map colliFeb=new HashMap();
    
    
    try{
      con=ColombiniConnections.getDbDesmosFebalProdConnection();
      colliFeb=DatiCommUtils.getInstance().getColliCommLineeLavFebal(con, comm, dataRif); 
       
    } catch(SQLException s){
      _logger.error(" Errore in fase di esecuzione dello statement  --> "+s.getMessage());
    } finally{
      try{
        if(ps!=null)
          ps.close();
        if(rs!=null)
          rs.close();
        if(con!=null)
          con.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiususra dello statement --> "+s.getMessage()); 
      }
    }
    
    
    return colliFeb;
  }
  
  private Date getDataOraRif(){
    Date dataTM=null;
    try{
      String dtCS=DateUtils.DateToStr(this.dataRif, "ddMMyyyy");
      String ora=TIMEMATTINA;
      if(typeOper.equals(ELABPOM))
         ora=TIMEPOMERIGGIO;     
    
      dataTM=DateUtils.StrToDate(dtCS+" "+ora, "ddMMyyyy HH:mm:ss");
              
      
    } catch(ParseException p){
      _logger.error("Problemi in fase di conversione dei dati --> "+p.getMessage());
      addError(" Errore in fase di conversione dei dati --> "+p.getMessage());
    }
    return dataTM;
  }
  
  
  private ArrayListSorted<Linea4MancantiVDL> prepareListToPrint() {
    ArrayListSorted list=new ArrayListSorted("progressivoVis");
    if(mapLineeVdl==null || mapLineeVdl.isEmpty())
      return list;
    
    Set keys=mapLineeVdl.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()){
      String keyLinea=(String) iter.next();
      Linea4MancantiVDL v=mapLineeVdl.get(keyLinea);
      if(v.getTotMancanti()>0)
        list.add(v);
    }
    
    
    return list;
  }

  
  
  
  private String prepareTextMsg(Integer commessa,List<Linea4MancantiVDL>lineeBeans ) {
    StringBuilder message=new StringBuilder();
    Integer totMancantiComm=Integer.valueOf(0);
    message.append(" Commessa n.").append(commessa);
    if(!StringUtils.IsEmpty(descrComm))
      message.append(" - ").append(descrComm);
    message.append(" \n\n");
    for(Linea4MancantiVDL l:lineeBeans){
      Integer totMancantiLinea=l.getTotMancanti();
      message.append("\n Stab: ").append(l.getDescrStab()).append(
                             " - Linea : ").append(l.getDescrLinea()).append(
                             " --> mancanti n.").append(totMancantiLinea);
              
      totMancantiComm+=totMancantiLinea;
      
    }
    
    message.append("\n\n Totale mancanti  n.").append(totMancantiComm);
    
    return message.toString();
    
  }

 
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestMancantiFromVDL.class);

  
  
  

  
}
