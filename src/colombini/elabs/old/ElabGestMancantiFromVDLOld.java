/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs.old;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.elabs.NameElabs;
import colombini.model.persistence.MancantiVDLBeanOld;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCommToShip;
import colombini.util.InfoMapLineeUtil;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import utils.ArrayListSorted;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FilePropUtils;

/**
 *
 * @author lvita
 */
public class ElabGestMancantiFromVDLOld extends ElabClass{

  private String typeOper="";
  private Date dataRif;
  
  //mappa che contiene per ciascuna linea logica la descrizione relativa del raggruppamento
  private Map<Integer,String> mapLineeCodici=null;
  
  //lista dei raggruppamenti delle linee da visualizzare in fase di stampa 
  private ArrayListSorted<LineaInfo> lineeBeans=null;
  
  private Map<String,Integer> mapPosLineeRaggr=null;
  
  public final static String ELABMATT="MATT";
  public final static String ELABPOM="POM";
  
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
    
    
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Elaborazione gestione mancanti da VDL");
    
    mapLineeCodici=new HashMap();
    mapPosLineeRaggr=new HashMap();
    
    lineeBeans=new ArrayListSorted<>("position");
    
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBMANCANTIFROMVDL);
    String nomeFileMatt=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEVDLMT));
    String nomeFilePom=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEVDLPM));
    String fileProps="";//ClassMapper.classToString(prop.get(NameElabs.FILEPROPERTIES));
    
    String comm=getCommessa(con);
    if(comm==null){
      addWarning("Commessa non presente per il giorno "+dataRif);
      return;
    }
    //caricamento mappe x linee da file prop
    loadLineeMaps(fileProps);
    //lettura file csv
    String nomeFile=nomeFileMatt;
    if(typeOper.equals(ELABPOM))
      nomeFile=nomeFilePom;
    
    
    List mancanti=getListMancanti(nomeFile);
    //elaborazione dati
    if(mancanti!=null && !mancanti.isEmpty()){
      Date dataOraRif=getDataOraRif();
      //gestione dati su db
      List listToSave=elabDatiMancanti(mancanti,comm,dataOraRif);     
      PersistenceManager man=new PersistenceManager(con);
      try{
        MancantiVDLBeanOld bDel=new MancantiVDLBeanOld(dataOraRif, comm, Integer.valueOf(0));
        man.deleteDtFromBean(bDel);
        man.storeDtFromBeans(listToSave);
      
      }catch(SQLException s){
        _logger.error("Errore in fase di cancellazione/salvataggio dati -->"+s.getMessage());
        addError("Errore in fase di cancellazione/salvataggio dati -->"+s.toString());
      }
      //invio mail
      MailSender mS=new MailSender();
      MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_ELBMANCANTI);
      beanInfo.setObject(beanInfo.getObject().trim()+" in data "+DateUtils.dateToStr(dataOraRif, "dd/MM/yyyy")+" ore "+DateUtils.dateToStr(dataOraRif, "HH:mm") );
      beanInfo.setText(prepareTextMsg(comm));
      
      System.out.println(beanInfo.getText());
      mS.send(beanInfo);
      
    }else{
      addWarning ("Mancanti non trovati per giorno "+dataRif);
    }
  }
  
  
  private void loadLineeMaps(String fileProps){
    try {
      Map props=new HashMap();
      props = FilePropUtils.getInstance().readFilePropKeyVal(fileProps);
      Set keys=props.keySet();
      Iterator iter=keys.iterator();
      while (iter.hasNext()){
        String key=ClassMapper.classToString(iter.next());
        String values=ClassMapper.classToString(props.get(key));
        
        if(key.contains(FilePropUtils.STRSEPMULTIPROP2)){
          int idx=key.indexOf(FilePropUtils.STRSEPMULTIPROP2);
          String descLineaCompleta=key.substring(0, idx);
          String stab=key.substring(idx+FilePropUtils.STRSEPMULTIPROP2.length());
          String descLinea=descLineaCompleta.substring(3);
          String  posS=descLineaCompleta.substring(0, 2);

          LineaInfo linBean=new LineaInfo(posS,descLinea, stab);
          lineeBeans.add(linBean);
          mapPosLineeRaggr.put(descLinea,Integer.valueOf(posS));
          
          loadCodiciLinee(descLinea, values);
        }        
      }
    } catch (IOException ex) {
      _logger.error(" Errore in fase di accesso al file di configurazione delle linee per VDL -->"+ex.getMessage());
      addError("Errore in fase di accesso al file di configurazione delle linee per VDL -->"+ex.toString());
    }  
  }
  
  private void loadCodiciLinee(String descLinea,String valori){
    if(StringUtils.isEmpty(valori))
      return;
    
    //casistica di un solo elemento per linea
    if(!valori.contains("-") &&  !valori.contains(",")){
      List l=new ArrayList();
      l.add(Integer.valueOf(valori.trim()));
      loadCodiciLinee(descLinea, l);
    }else if( valori.contains(",") ){
      List<Integer> l2=new ArrayList();
      List<String> l1=ArrayUtils.getListFromArray(valori.split(","));
      for(String val:l1){
        int idx=val.indexOf("-");
        if(idx<0){
          l2.add(Integer.valueOf(val));
        }else{
         Integer valIni= ClassMapper.classToClass(val.substring(0, idx),Integer.class);
         Integer valFin= ClassMapper.classToClass(val.substring(idx+1),Integer.class);
         Integer valTmp=valIni;
         while(valTmp<=valFin){
           l2.add(valTmp);
           valTmp++;
         }
        }
      }  
      loadCodiciLinee(descLinea, l2);
    }
  }
  
  private void loadCodiciLinee(String descLinea,List<Integer> codiciLinee){
    if(codiciLinee==null || codiciLinee.isEmpty())
      return;
    
    for(Integer codLinea:codiciLinee){
      if(mapLineeCodici.containsKey(codLinea)){
        addError("Attenzione codice linea presente più volte "+codLinea);
      }else{
        mapLineeCodici.put(codLinea, descLinea);
      }
    }
    
  }
  
  private String getCommessa(Connection con){
    String comm=null;
    try{
    QueryCommToShip qry=new QueryCommToShip();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, DateUtils.getDataForMovex(dataRif));

    Object []obj = ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
    if(obj!=null && obj.length>0)
      comm=ClassMapper.classToString(obj[0]);
    
    } catch(QueryException q){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+q.getMessage());
      addError("Impossibile reperire il numero di commessa -->"+q.toString());
      
    } catch(SQLException s){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+s.getMessage());
      addError("Impossibile reperire il numero di commessa -->"+s.toString());
      
    }
    
    return comm;
  }
  
  
  private List getListMancanti(String fileName){
    if(StringUtils.isEmpty(fileName)){
      addError("Attenzione nome file VDL non valorizzato");
      return null;
    }
    
    String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileName, dataRif);
    
    List mancanti=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    
    long count=1;
    try{
      fR = new FileReader(fNameNew);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      
      while(riga!=null && !riga.isEmpty()){
        if(count>1){
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
  
  private List elabDatiMancanti(List<List> mancanti,String comm,Date dataOraRif){
    List list=new ArrayList();
    
    
    if(mancanti==null|| mancanti.isEmpty()){
      addWarning("Attenzione lista mancanti da VDL vuota");
      return list;
    }
    for(List record:mancanti){
      Integer codLinea=ClassMapper.classToClass(record.get(0),Integer.class);
      MancantiVDLBeanOld mb=new MancantiVDLBeanOld(dataOraRif, comm, codLinea);
      
      String descrL="";
      mb.setnMancanti(ClassMapper.classToClass(record.get(1),Integer.class));
      
      if(!mapLineeCodici.containsKey(codLinea)){
        addError("Attenzione linea presente in file mancanti non codificata "+codLinea);
      }else{
        descrL=mapLineeCodici.get(codLinea);
      }
      
      mb.setDescrLinea(descrL);
      list.add(mb);
      
      if(!StringUtils.isEmpty(descrL)){
        Integer pos=mapPosLineeRaggr.get(descrL);
        LineaInfo b=lineeBeans.get(pos-1);
        b.addMancantiVdlBean(mb);
      }
    }
    
    
    return list;
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
  
  private String prepareTextMsg(String commessa) {
    StringBuilder message=new StringBuilder();
    Integer totMancantiComm=Integer.valueOf(0);
    message.append(" Commessa n.").append(commessa).append(" \n\n");
    for(LineaInfo l:lineeBeans){
      Integer totMancantiLinea=l.getTotMancanti();
      if(totMancantiLinea>0){
        message.append("\n Stab: ").append(l.getStabilimento()).append(
                             " - Linea : ").append(l.getDescrLinea()).append(
                             " --> mancanti n.").append(totMancantiLinea);
              
       totMancantiComm+=totMancantiLinea;
      }
    }
    
    message.append("\n\n Totale mancanti  n.").append(totMancantiComm);
    
    return message.toString();
    
  }
  
  
  public class LineaInfo {
    private String position;
    private String descrLinea;
    private String stabilimento;
    private List <MancantiVDLBeanOld> mancantiList;
    private Integer totMancanti;

    public LineaInfo(String pos,String descrLinea,String stabilimento) {
      this.position=pos;
      this.descrLinea = descrLinea;
      this.stabilimento=stabilimento;
      mancantiList=new ArrayList();
      totMancanti=Integer.valueOf(0);
    }

    public String getDescrLinea() {
      return descrLinea;
    }

//    public void setDescrLinea(String descrLinea) {
//      this.descrLinea = descrLinea;
//    }

    public String getStabilimento() {
      return stabilimento;
    }

//    public void setStabilimento(String stabilimento) {
//      this.stabilimento = stabilimento;
//    }
    public String getPosition() {
      return position;
    }
    
    
    List <MancantiVDLBeanOld> getMancantiList() {
      return mancantiList;
    }

    public void MancantiVDLBeanOld(List<MancantiVDLBeanOld> mancantiList) {
      this.mancantiList = mancantiList;
    }
    
   
    public Integer getTotMancanti() {
      return totMancanti;
    }

    public void setTotMancanti(Integer totMancanti) {
      this.totMancanti = totMancanti;
    }
    
    public void addNMancanti(Integer num){
      this.totMancanti+=num;
    }
    
    public void addMancantiVdlBean(MancantiVDLBeanOld bean){
      addNMancanti(bean.getnMancanti());
      mancantiList.add(bean);
    }
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestMancantiFromVDLOld.class);

  
}
