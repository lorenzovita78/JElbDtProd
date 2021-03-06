/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.model.InfoMancanteVdlBean;
import colombini.util.DatiProdUtils;
import colombini.util.InfoMapLineeUtil;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import fileXLS.XlsXCsvFileGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
public class ElabGestColliEsteroInVDL extends ElabClass{

  
  private Date dataRif;
  private Boolean invioMail=null;
  
  //area relativo al carico
  public final static Integer AREACARICO=Integer.valueOf(90);
  
  //stato 550 camion avviato
  public final static Integer STVETTINPART=Integer.valueOf(550);

  
  @Override
  public Boolean configParams() {
    Map parameter =this.getInfoElab().getParameter();
    
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
    
    
    
    return Boolean.TRUE;
  }

  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Elaborazione colli esteri prensenti in VDL");
    
    
    String pathFile=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILECOLLIEXT));
    String nomeFile=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILECOLLIEXT));
    String extFile=ClassMapper.classToString(getElabProperties().get(NameElabs.EXTFILECOLLIEXT));
    
    String numIniSpe=ClassMapper.classToString(getElabProperties().get(NameElabs.NUMINICODSPE));
    //try{
    String nomeFileDef=getFileNameColliExt(pathFile, nomeFile, extFile);
    //lettura file colli
    List<InfoColloIntoVDL> colli=getListColli(con,nomeFileDef,numIniSpe);
    //elaborazione dati
    
    Map<Date, ClientiSped > mappaToMail=new HashMap();
    List<List> listGg=new ArrayListSorted(0);
    if(colli!=null && !colli.isEmpty()){
      //System.out.println("COLLO;DATAULTMOD;VETT;CODICECLI;STATOCOLLO;PROVENIENZA;DATASPE;STATOVETT");
      
      for(InfoColloIntoVDL bCollo:colli){
          //System.out.println(bCollo.printInfo());
          
          
          Date dSpe=bCollo.getDataSped();
          Integer numSpe=bCollo.getNumSpe();
          //Long dSpeN=DateUtils.getDataForMovex(bCollo.getDataSped());
          ClientiSped info;
          if(mappaToMail.containsKey(dSpe)){
            info=mappaToMail.get(dSpe);
            
          }else{
            info=new ClientiSped(dSpe);
            List rec=new ArrayList();
            rec.add(dSpe);
            listGg.add(rec);
          }
          info.addElementToList(bCollo.getNumSpe(),bCollo.getCodiceCli(), bCollo.getDescrCli(), bCollo.getProvenienza());
          if(!info.isPresentOrderCli(bCollo.getCodiceCli(), bCollo.getNumeroOrd())){
            try{
              Double volOrd=InfoMancanteVdlBean.getVolumeNumOrdine(con, bCollo.getNumeroOrd(), bCollo.getNumSpe());  
              info.addInfoOrderCli(bCollo.getCodiceCli(), bCollo.getNumeroOrd(), volOrd);
            } catch(SQLException s){
              _logger.error("Errore bìin fase di ricerca del volume dell'ordineCLiente "+bCollo.getNumeroOrd()+" -->"+s.getMessage());
              addWarning("Errore in fase di ricerca del volume dell'ordineCLiente "+bCollo.getNumeroOrd() +"  cliente "+bCollo.getCodiceCli() );
            }
            
          }
          
          mappaToMail.put(dSpe, info);

      }
      StringBuilder text=new StringBuilder(" ");
      List tot=new ArrayList();
      List columns=new ArrayList(); 
      columns.add("DataSpedizione");columns.add("NumSpedizione");columns.add("Cliente");
      columns.add("Colli in Magazzino");columns.add("Colli su Carrelli");columns.add("Colli su Pedane");
      columns.add("Tot. Colli");columns.add("Tot. Volume (m3)");
      tot.add(columns);
      for(List rec:listGg){
         Date dTmp=ClassMapper.classToClass(rec.get(0),Date.class);
         List<InfoClienteColli> lstTmp=mappaToMail.get(dTmp).getListColliClienti();
         for(InfoClienteColli cliente :lstTmp){
           tot.add(cliente.getListInfo());
         }
      }
      
      //System.out.println(text.toString());

        //invio maillvita
        if(invioMail){
          XlsXCsvFileGenerator gen=null;
          try{
            MailSender mS=new MailSender();
            MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_COLLIEXTVDL);
            String nomeFileAtt=InfoMapLineeUtil.getLogFileName(beanInfo.getFileAttachName(), dataRif);
            gen= new XlsXCsvFileGenerator(nomeFileAtt, XlsXCsvFileGenerator.FILE_CSV,"Colli Estero");

            File attach=gen.generateFile(tot);

            beanInfo.setObject(beanInfo.getObject().trim()+" alla data "+DateUtils.dateToStr(dataRif, "dd/MM/yyyy"));
            beanInfo.setText(" In allegato il quantitativo dei colli clienti esteri, \n presenti nel sistema Vanderlande, suddivisi per data di spedizione e cliente . " );
            beanInfo.addFileAttach(attach);
            System.out.println(beanInfo.getText());
            mS.send(beanInfo);
          } catch(FileNotFoundException s){
            addError("Errore in fase di generazione del file "+gen.getFileName() +" --> "+s.toString());
            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
          }
        }
      
      }else{
        addWarning ("Colli esterno non presenti in VDL per giorno "+dataRif);
      }

    
  }
  
  private String getFileNameColliExt(String path,String fileName,String extFile){
    String fName=null;
    String fNameNew=InfoMapLineeUtil.getStringReplaceWithDate(fileName, dataRif);
    fNameNew=fNameNew.replace(extFile, "");
    
    fName=DatiProdUtils.getInstance().getFileName(path,fNameNew , null, null);
    
    
    if(fName!=null && fName.contains(extFile))
      return fName;
    
    return"";
  }
  
  
  private List<InfoColloIntoVDL> getListColli(Connection con,String fileName,String numIniSpe){
    if(StringUtils.isEmpty(fileName)){
      addError("Attenzione nome file VDL non valorizzato");
      return null;
    }
    
    Map <String,String> clienti  =new HashMap  ();
    List colli=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    long countadd=0;
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      
      while(riga!=null && !riga.isEmpty()){
        if(count>1){
          List l =ArrayUtils.getListFromArray(riga.split(";"));
          InfoColloIntoVDL bean=new InfoColloIntoVDL();
          
          bean.setCodiceCollo(ClassMapper.classToString(l.get(0)));
          bean.setDataUltimaMod(ClassMapper.classToClass(l.get(1),Date.class));
          bean.setDescrizioneVett(ClassMapper.classToString(l.get(2)));
          
          bean.setNumeroOrd(ClassMapper.classToString(l.get(3)));
          String codCli=ClassMapper.classToString(l.get(4));
          bean.setCodiceCli(codCli);
          if(!clienti.containsKey(codCli)){
            try{
              String cliDesc=InfoMancanteVdlBean.getNomeClienteEstero(con, codCli);
              clienti.put(codCli,cliDesc );
              bean.setDescrCli(cliDesc);
            }catch(SQLException s){
              _logger.error("Errore in fase di verifica della tipologia del cliente "+s.toString());
              addError("Errore in fase di lettura dei dati del cliente "+codCli);
              clienti.put(codCli, "");
              bean.setDescrCli("");
            }
          }else{
            bean.setDescrCli(clienti.get(codCli));
          }
          bean.setArea(ClassMapper.classToClass(l.get(5),Integer.class));
          bean.setStatoCollo(ClassMapper.classToClass(l.get(6),Integer.class));
          bean.setProvenienza(ClassMapper.classToString(l.get(7)));
          bean.setDataSped(ClassMapper.classToClass(l.get(8),Date.class));
          String nSpeS=numIniSpe+ClassMapper.classToString(l.get(9));
          bean.setNumSpe(ClassMapper.classToClass(nSpeS,Integer.class));
          bean.setStatoVett(ClassMapper.classToClass(l.get(10),Integer.class));
          
          //considero  solo colli di clienti estero che non sono stati spediti
          if(bean.getArea()<AREACARICO && bean.getStatoVett()<STVETTINPART &&  bean.getStatoCollo()<STVETTINPART
                  && !StringUtils.isEmpty(bean.getDescrCli()) && bean.getDataSped().before(dataRif)){
            
            colli.add(bean);
            countadd++;
          }  
        }
          
        count++;
        riga=bfR.readLine();
      }
      _logger.info("File "+fileName+" righe lette:"+count--+ " righe considerate :"+countadd);
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
    
    return colli;
  }
  
 
 
  public class ClientiSped{
    private Date dataSpe;
    private Map<String,InfoClienteColli> mappaCli;
    
    
    
    public ClientiSped(Date dS ){
      this.dataSpe=dS;
      mappaCli= new HashMap();
    }

    public Date getDataSpe() {
      return dataSpe;
    }

    public void addElementToList (Integer numSpe,String codCli,String descrCli,String prov){
      InfoClienteColli infoCC=null;
      if(mappaCli.containsKey(codCli)){
        infoCC=mappaCli.get(codCli);
      }else{
        infoCC=new InfoClienteColli(dataSpe,numSpe,codCli,descrCli);
      }
      infoCC.addNumColli(prov);
      mappaCli.put(codCli,infoCC);
    }
    
    
    public List<InfoClienteColli> getListColliClienti() {
      List lsort=new ArrayListSorted("codiceCli");
      
      Set keys=mappaCli.keySet();
      Iterator iter=keys.iterator();
      while (iter.hasNext()){
        String codCli=ClassMapper.classToString(iter.next());
        lsort.add(mappaCli.get(codCli));
      }
      
      return lsort;
    }

    public InfoClienteColli getInfoClienteColli(String codCli){
      return mappaCli.get(codCli);
    }
    
    public Boolean isPresentOrderCli(String codCli,String numOrd){
      InfoClienteColli i=this.mappaCli.get(codCli);
      if(i!=null)
        return i.isOrderPresent(numOrd);
      
      return Boolean.FALSE;
    }
    
    public void addInfoOrderCli(String codCli,String numOrd,Double vol){
      InfoClienteColli i=this.mappaCli.get(codCli);
      if(i!=null)
        i.updateOrdersMap(numOrd, vol);
    }
    
  }
  
  
  public class InfoClienteColli{
    
    private String codiceCli;
    private Date dataSpe;
    private Integer numSpe;
    private String descrCli;
    private Integer numColliW;
    private Integer numColliP;
    private Integer numColliC;
    private Map <String,Double> ordersMap  ;
    private Double volTot;
    
    
    public final static String provW="W";//magazzino
    public final static String provP="P";//pedane
    public final static String provC="C";//clienti

    
    public InfoClienteColli(Date data,Integer numSpe,String cli,String descCli) {
      this.codiceCli=cli;
      this.dataSpe=data;
      this.numSpe=numSpe;
      this.descrCli=descCli;
      this.numColliW=Integer.valueOf(0);
      this.numColliP=Integer.valueOf(0);
      this.numColliC=Integer.valueOf(0);
      ordersMap=new HashMap();
      volTot=new Double(0);
    }

    
    public String getCodiceCli() {
      return codiceCli;
    }

    public String getDescrCli() {
      return descrCli;
    }

    public void setDescrCli(String descrCli) {
      this.descrCli = descrCli;
    }

    public Integer getNumColliW() {
      return numColliW;
    }

    public Integer getNumColliP() {
      return numColliP;
    }
    
    public Integer getNumColliC() {
      return numColliC;
    }
    
    public void addNumColli(String prov){
      if(provW.equals(prov)){
        numColliW++;
      }else if(provP.equals(prov)){
        numColliP++;
      }else if(provC.equals(prov)){
        numColliC++;
      }
    }
    
    public void updateOrdersMap(String numOrd,Double volOrd){
      if(!ordersMap.containsKey(numOrd)){
        ordersMap.put(numOrd, volOrd);
        volTot+=volOrd;
      }
      
    }

    public Boolean isOrderPresent(String numOrd){
      return ordersMap.containsKey(numOrd);
    }
    
    
    public Double getVolTot() {
      return volTot;
    }
    
    
    
    
    
//    public void setNumColliW(Integer numColliW) {
//      this.numColliW = numColliW;
//    }
//    public void setNumColliP(Integer numColliP) {
//      this.numColliP = numColliP;
//    }
//    public void setNumColliC(Integer numColliC) {
//      this.numColliC = numColliC;
//    }
    
    public String printInfo(){
      return "Data : "+DateUtils.dateToStr(dataSpe, "dd/MM/yyyy")+ " -  Cliente :"+
              codiceCli+" - "+descrCli +" --> colli in Mag :"+numColliW +"; colli su carrelli :"+numColliC
              +"; colli su pedane :"+numColliP; 
    }
    
    
    public List getListInfo(){
      List l=new ArrayList();
      l.add(DateUtils.RemoveTime(dataSpe));
      l.add(numSpe);
      l.add(codiceCli+descrCli);
      l.add(numColliW);
      l.add(numColliC);
      l.add(numColliP);
      l.add(numColliC+numColliW+numColliP);
      l.add(volTot);
      
      return l;
    }
    
  }
  
  
  public class InfoColloIntoVDL{
    private String codiceCollo;
    private Date dataUltimaMod;
    private String descrCli;
    private String descrizioneVett;
    private String numeroOrd;
    private String codiceCli;
    private Integer area;
    private Integer statoCollo;
    private String provenienza;
    private Date dataSped;
    private Integer numSpe;
    private Integer statoVett;

    public String getCodiceCollo() {
      return codiceCollo;
    }

    public void setCodiceCollo(String codiceCollo) {
      this.codiceCollo = codiceCollo;
    }

    public Integer getNumCommessa(){
      if(StringUtils.isEmpty(codiceCollo) && codiceCollo.length()<3)
        return null;
      
      return ClassMapper.classToClass(codiceCollo.substring(0, 3),Integer.class);
    }
    
    public Date getDataUltimaMod() {
      return dataUltimaMod;
    }

    public void setDataUltimaMod(Date dataUltimaMod) {
      this.dataUltimaMod = dataUltimaMod;
    }

    public String getDescrizioneVett() {
      return descrizioneVett;
    }

    public void setDescrizioneVett(String descrizioneVett) {
      this.descrizioneVett = descrizioneVett;
    }

    public String getNumeroOrd() {
      return numeroOrd;
    }

    public void setNumeroOrd(String numeroOrd) {
      this.numeroOrd = numeroOrd;
    }

    public String getCodiceCli() {
      return codiceCli;
    }

    public void setCodiceCli(String codiceCli) {
      this.codiceCli = codiceCli;
    }

    public String getDescrCli() {
      return descrCli;
    }

    public void setDescrCli(String descrCli) {
      this.descrCli = descrCli;
    }
    
    public Integer getArea() {
      return area;
    }

    public void setArea(Integer area) {
      this.area = area;
    }

    public Integer getStatoCollo() {
      return statoCollo;
    }

    public void setStatoCollo(Integer statoCollo) {
      this.statoCollo = statoCollo;
    }

    public String getProvenienza() {
      return provenienza;
    }

    public void setProvenienza(String provenienza) {
      this.provenienza = provenienza;
    }

    public Date getDataSped() {
      return dataSped;
    }

    public void setDataSped(Date dataSped) {
      this.dataSped = dataSped;
    }

    public Integer getNumSpe() {
      return numSpe;
    }

    public void setNumSpe(Integer numSpe) {
      this.numSpe = numSpe;
    }

    public Integer getStatoVett() {
      return statoVett;
    }

    public void setStatoVett(Integer statoVett) {
      this.statoVett = statoVett;
    }
    
    
    public String printInfo(){
      return codiceCollo+";"+DateUtils.dateToStr(dataUltimaMod, "dd/MM/yyyy HH:mm:ss")+";"+
              descrizioneVett+";"+codiceCli+";"+statoCollo+";"+provenienza+";"+
              DateUtils.dateToStr(dataSped, "dd/MM/yyyy" )+";"+statoVett;
    }
    
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabGestColliEsteroInVDL.class);

  
}
