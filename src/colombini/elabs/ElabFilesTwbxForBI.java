/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.util.File4PortalB2b;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.ExecUtil;
import utils.StringUtils;

/**
 * Classe che si preoccupa di lanciare la generazione dei file twbx per la BI.
 * La classe si collegherà al db della Bi per andare a leggere la lista degli elementi su cui ciclare.
 * @author lvita
 */
public class ElabFilesTwbxForBI extends ElabClass{
  //comandi
  public final static String CMD_LOGIN= " $TABCMD$ login -s http://$IPSRVTBL$:80 -u $UTSRVTBL$ -p $PWDSRVTBL$ ";
  public final static String CMD_REFRESHEXTRACT= " tabcmd refreshextracts --synchronous --workbook $WRBKNM$ --project $PRJNM$ ";
 // public final static String CMD_COPYTWBX=" tabcmd  get \"http://$IPSRVTBL$:80//workbooks/$WRBKNM$.twbx\" -f $PATHFILEDEST$.twbx ";
 // public final static String CMD_COPYTWBX=" tabcmd  get \"http://$IPSRVTBL$:80//workbooks/$WRBKNM$.twbx\" -f $PATHFILEDEST$.pdf ";
  

  //tabcmd export "Provvigioni_agenzia/Prov_Italia2" --pdf -f
  public final static String CMD_COPYTWBX=" tabcmd  export \"$WRBKNM$/$DASH$\" --$EXT$ -f $PATHFILEDEST$.pdf ";

  
  public final static String CMD_LOGOUT= " $TABCMD$ logout";
  
 
  public final static String EXTFILETWBX=".twbx";
  //costanti per gestire le variabili da modificare all'interno dei comandi
  public final static String $TABCMD$="$TABCMD$";
  public final static String $IPSRVTBL$="$IPSRVTBL$";
  public final static String $UTSRVTBL$="$UTSRVTBL$";
  public final static String $PWDSRVTBL$="$PWDSRVTBL$";
  public final static String $WRBKNM$="$WRBKNM$";
  public final static String $PATHFILEDEST$="$PATHFILEDEST$";
  public final static String $DASH$="$DASH$";
  public final static String $EXT$="$EXT$";
  
  //proprietà da valorizzare nel file xml
  public final static String $DATEFILE$="$DATEFILE$";
  public final static String $FILENAME$="$FILENAME$";
  public final static String $UTENTE$="$UTENTE$";
  public final static String $TARGETTYPE$="$TARGETTYPE$";
  public final static String STRING_TARGET_TYPE_XML="<target type=\"U\" dn=\"$UTENTE$\" read=\"true\" write=\"false\" remove=\"false\" emailnotification=\"false\" emailattachment=\"false\" emailaddress=\"$UTENTE$@colombinigroup.com\"/>";
  
//parametri da lancio 
  public final static String PWORKBOOK="WKBKTABLEAU";
  public final static String PPROJECT="PROJTABLEAU";
  public final static String PPATHFILEDEST="PATHFILEDEST";
  public final static String PFILEXMLPORT="FILEXMLPORT";
  public final static String PTYPEDEST="TYPEDEST"; // tipologia destinatario A - Agente ; M- Area Manager ;K -Key account
  public final static String PMACROMERC="MACROM";  // tipologia macromercato I - Italia ; E - Estero;
  public final static String CODDESTINATARIO="CODDEST";
  public final static String DASHBOARD="DASHBOARDTABLEAU";
  public final static String EXT="EXT";

  
  private String workbookName=null;
  private String projectName=null;
  private String dashboard=null;
  private String extension=null;
  
  private String macroM=null;
  private String tipoDestinatario=null;
  private String fileXml=null;
  
  private String codDestinatario=null;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(PWORKBOOK)!=null){
      this.workbookName=ClassMapper.classToString(parameter.get(PWORKBOOK));
    }  
    
    if(parameter.get(PPROJECT)!=null){
      this.projectName=ClassMapper.classToString(parameter.get(PPROJECT));
    }  
    
    if(parameter.get(PFILEXMLPORT)!=null){
      this.fileXml=ClassMapper.classToString(parameter.get(PFILEXMLPORT));
    }
    
    if(parameter.get(PTYPEDEST)!=null){
      this.tipoDestinatario=ClassMapper.classToString(parameter.get(PTYPEDEST));
    }
    
    if(parameter.get(PMACROMERC)!=null){
      this.macroM=ClassMapper.classToString(parameter.get(PMACROMERC));
    }
    
    if(parameter.get(CODDESTINATARIO)!=null){
      this.codDestinatario=ClassMapper.classToString(parameter.get(CODDESTINATARIO));
    }
    
    if(StringUtils.IsEmpty(workbookName) || StringUtils.isEmpty(projectName) 
            ||StringUtils.IsEmpty(tipoDestinatario) || StringUtils.IsEmpty(macroM) ){
      
      _logger.warn("Parametri non valorizzati correttamente >> "+parameter.toString());
      return Boolean.FALSE;
    }
    
    if(parameter.get(DASHBOARD)!=null){
      this.dashboard=ClassMapper.classToString(parameter.get(DASHBOARD));
    } 
    
    if(parameter.get(EXT)!=null){
      this.extension=ClassMapper.classToString(parameter.get(EXT));
    } 
    
    
    return Boolean.TRUE;
            
  }

  @Override
  public void exec(Connection con) {
    Boolean logIn=Boolean.FALSE;
    List<List> elements=null;
    Connection conBI=null;
    
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBTWBXFORBI );
    String ipSrvTableau=ClassMapper.classToString(getElabProperties().get(NameElabs.IP_SERVER_TABLEAU));
    String utSrv=ClassMapper.classToString(getElabProperties().get(NameElabs.IP_UT_SRVTABLEAU));
    String pwdSrv=ClassMapper.classToString(getElabProperties().get(NameElabs.IP_PWD_SRVTABLEAU));
    //stringa per richiamare il comando tabcmd
    String pathtabcmd=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHTABCMD));
    String pathfilesBat=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESBAT));
    
    _logger.info("Login su server");
    String cmdLogin=CMD_LOGIN;
    cmdLogin=cmdLogin.replace($TABCMD$, pathtabcmd+"tabcmd" );
    cmdLogin=cmdLogin.replace($IPSRVTBL$, ipSrvTableau);
    cmdLogin=cmdLogin.replace($UTSRVTBL$, utSrv);
    cmdLogin=cmdLogin.replace($PWDSRVTBL$, pwdSrv);
    try{
      conBI=ColombiniConnections.getDbNewBI();
       
      //login al server Tableau
      elements=getListToProcess(conBI,tipoDestinatario,macroM,codDestinatario);
      if(elements==null || elements.isEmpty()){
        addError("Lista elementi da processare vuota. Impossibile continuare");
        return;
      }
       
      //login al server 
//      _logger.info("Login : "+cmdLogin);
//      int e=ExecUtil.ExecuteCommand(cmdLogin);
//      if(e==0){
//        logIn=Boolean.TRUE;
//      }else{
//        addError("Errore in fase di login al server. Impossibile proseguire !!");
//        return;
//      }
      String destTmp="";
      String pathTmp="";
      String descrDest="";
      
      for(List element:elements){
        try{
          List listCodici=new ArrayList();
          destTmp=ClassMapper.classToString(element.get(0)); 
          pathTmp=ClassMapper.classToString(element.get(1));
          descrDest=ClassMapper.classToString(element.get(2));
          _logger.info(" Processo destinatario :"+destTmp+" - "+descrDest );
          String codiciMultipli=ClassMapper.classToString(element.get(3));
          if(!StringUtils.isEmpty(codiciMultipli) && codiciMultipli.contains(",")){
            listCodici=ArrayUtils.getListFromArray(codiciMultipli.split(","));
            _logger.info(" Codici Legati al destinatario :"+listCodici);
          }
          String utPortale=ClassMapper.classToString(element.get(4));
          if(StringUtils.isEmpty(pathTmp)){
            addError(" Path salvataggio workbook non specificato per destinatario : "+destTmp+" - "+descrDest +". \n");
            continue;
          }
            
          //aggiorna tabella appoggio  
          if(listCodici.size()>0){
            cleanTableAppoggio(conBI);
            insertTableAppoggio(conBI, listCodici);
          }else{
            prepareTableAppoggio(conBI,destTmp);
          }
          //prepara la lista di comandi
          String fileNameTwbx=getFileName(descrDest);
          String command=prepareCommands(ipSrvTableau, pathtabcmd ,fileNameTwbx, pathTmp);
          command=cmdLogin+"\n"+command + pathtabcmd +"tabcmd logout ";
          String fileBatTmp=pathfilesBat+destTmp;
          String filebat=createFileBat(fileBatTmp, command);
          
          //gestione pubblicazione su portale con file xml
          if(fileXml!=null){
            if(!StringUtils.isEmpty(utPortale)){
            //genero xml
              Map parms4Xml=new HashMap();
              parms4Xml.put($DATEFILE$, DateUtils.dateToStr(new Date(), "dd/MM/yyyy"));
              parms4Xml.put($FILENAME$, fileNameTwbx);
              String targetT=getStringTargetForXml(utPortale);
              parms4Xml.put($TARGETTYPE$, targetT);
              File4PortalB2b.getInstance().createXmlForPortal(fileXml, fileNameTwbx, pathTmp, parms4Xml);
            }else{
              addError("Utente non specificato per pubblicazione su portale  per destinatario -->"+destTmp);
            }
          }
          //eseguo i comandi 
          String log=ExecUtil.ExecuteCommandWithLog(filebat);
          _logger.info("Comando generazione file : "+command);
          if(!log.startsWith("0")){
            addError("Errore in fase di generazione del file twbx per codice : "+destTmp);
            _logger.info(" ATTENZIONE ELABORAZIONE PER"+ destTmp +" -->" +log);
          }
          //pulizia finale
          cleanTableAppoggio(conBI);
          _logger.info(" Fine processo destinatario :"+destTmp+" - "+descrDest );
         //executeCommands(agente, commands);
         //String workBcolnet
        } catch(IOException ioe ){
          _logger.error(" Errore in fase di esecuzione del comando --> "+ioe.getMessage());
          addError("Errore in fase di esecuzione batch per "+destTmp+" -->"+ioe.toString());
//        } catch (InterruptedException ex) {
//          _logger.error(" Errore in fase di esecuzione del comando --> "+ex.getMessage());
//          addError("Errore in fase di esecuzione batch per "+destTmp+" -->"+ex.toString());
        } 
      }
//    } catch(IOException ioe ){
//      _logger.error(" Errore in fase di login al server Tableau--> "+ioe.getMessage());
//      addError(" Errore in fase di login al server  Tableau -->"+ioe.toString());
    } catch (InterruptedException ex) {
      _logger.error(" Errore in fase di login al Tableau --> "+ex.getMessage());
      addError("Errore in fase di login al Tableau-->"+ex.toString());
    } catch (SQLException ex) {
      _logger.error(" Errore in fase di connessione al db --> "+ex.getMessage());
      addError("Errore in fase di connessione al db -->"+ex.toString());
    }finally{
//      if(logIn){
//        try {
//          ExecUtil.ExecuteCommand(pathtabcmd+"tabcmd logout ");
//        } catch (IOException ex) {
//          _logger.error(" Errore in fase di logout al server --> "+ex.getMessage());
//          addError(" Errore in fase di logout al server  -->"+ex.toString());
//        } catch (InterruptedException ex) {
//          _logger.error(" Errore in fase di logout al server --> "+ex.getMessage());
//          addError(" Errore in fase di logout al server  -->"+ex.toString());
//        }
//      }
      
      if(conBI!=null)
        try {
          conBI.close();
      } catch (SQLException ex) {
        _logger.error(" Errore in fase di chiusura della connessione --> "+ex.getMessage());
      }
    }   
     
    
    
  }
  
  private String getStringTargetForXml(String utPortale){
    StringBuilder targetString=new StringBuilder();
    String targetTmp=STRING_TARGET_TYPE_XML;
    if(utPortale.contains(",")){
      List<String> utentiP=ArrayUtils.getListFromArray(utPortale.split(","));
      int sizeL=utentiP.size();
      int i=1;
      for(String utente :utentiP){    
        targetString.append(targetTmp.replace($UTENTE$, utente));
        if(i<sizeL)
          targetString.append("\n");
        
        i++;
      }
    }else{
      targetString.append(targetTmp.replace($UTENTE$, utPortale));
    }
    
    return targetString.toString();
  }
  
  
  private void prepareTableAppoggio(Connection conSqlS,String cod) throws SQLException{
    cleanTableAppoggio(conSqlS);
    
    insertTableAppoggio(conSqlS, cod);
    
  }
  
  private void cleanTableAppoggio(Connection conSqlS) throws SQLException{
    PreparedStatement ps=null;
    try{
      //truncate tab
      ps=conSqlS.prepareStatement("TRUNCATE TABLE dbo.DestinatariFiltro ;");
      ps.execute();
    } finally{
      if(ps!=null)
        ps.close();
    }
    
  }
  
  private void insertTableAppoggio(Connection conSqlS,String cod) throws SQLException{
    PreparedStatement ps=null;
    try{
      // inserisco il nuovo record 
      ps=conSqlS.prepareStatement("INSERT INTO dbo.[DestinatariFiltro] (DestinatarioFiltro) values ( ? ) ;");
      ps.setString(1, cod);
      ps.execute();
      
    } finally{
      if(ps!=null)
        ps.close();
    }
    
  }
  
  private void insertTableAppoggio(Connection conSqlS,List<String> codici) throws SQLException{
    if(codici==null || codici.isEmpty())
      return;
    
    PreparedStatement ps=null;
    for(String cod:codici){
      try{
        // inserisco il nuovo record 
        ps=conSqlS.prepareStatement("INSERT INTO dbo.[DestinatariFiltro] (DestinatarioFiltro) values ( ? ) ;");
        ps.setString(1, cod);
        ps.execute();
      
      } finally{
        if(ps!=null)
        ps.close();
      }
    }
  }
  
  
  
  
  private String prepareCommands(String ipSrvTableau, String pathTabcmd, String fileName, String pathFileDest) {
    String commands=" ";
    
    //comando per estrazione extract
    String extract=CMD_REFRESHEXTRACT;
    //extract=extract.replace($TABCMD$, tabcmd);
    extract=extract.replace($WRBKNM$, workbookName);
    
    
    //commands.add(extract);
    
    //MODIFICA DEL 3/04/2017
    //String fileName =preC;
    //fileName+="_"+codDest;
    
    //String fileName=getFileName(descrDest);
    String pathFileComplete=pathFileDest+"\\"+fileName;
    String copyFile=CMD_COPYTWBX;
    //copyFile=copyFile.replace($TABCMD$, tabcmd);
    copyFile=copyFile.replace($IPSRVTBL$, ipSrvTableau);
    copyFile=copyFile.replace($WRBKNM$, workbookName.replace("\"", ""));
    copyFile=copyFile.replace($PATHFILEDEST$, pathFileComplete );
    copyFile=copyFile.replace($DASH$, dashboard);
    copyFile=copyFile.replace($EXT$, extension);


    //comando per copia file
    //commands.add(extract+" \n "+copyFile);
    //creazione file bat
   
    commands = pathTabcmd+"\n" +extract +"\n" + copyFile +"\n exit ";
    
    
    return commands;
    
  }
  
  
  private String getFileName(String descDest){
    String fN=workbookName.replace("_","")+"-"+descDest;
    _logger.info("Nome file twbx :"+fN);
    
    return fN;
    //return workbookName.replace("_","")+"-"+tipoDestinatario+macroM+"-"+codDest;
    
  }
  
  
  private String  createFileBat (String fileName,String command) throws FileNotFoundException, IOException{
    FileOutputStream file = null;
    PrintStream output=null;
    
    file=new FileOutputStream(fileName+".bat");
    output = new PrintStream(file);
    output.print(command);
    output.flush();
    
    if(output!=null)
      output.close();
    
    if(file!=null)
      file.close();
    
    
    return fileName+".bat";
  }
  
  
  
  
  private List<List> getListToProcess(Connection conBI, String tipo,String macroM,String destinatario ) {
    List list=new ArrayList();
    
    String qry=" select distinct ltrim(rtrim(idDestinatario))  as idDestinatario,ltrim(rtrim(path))  as path ,ltrim(rtrim(Destinatario)) as descrDest,"
            + " ltrim(rtrim(ListaCodici)) as listCodici,"
            + " ltrim(rtrim(utentePortale)) as utPortale "+ //distinct ltrim(rtrim("+columnNameSource+")) 
               " from dbo.Destinatari"   + //+ tableNameSource+
               " where 1=1 "+
               " and TipoDestinatario = "+ JDBCDataMapper.objectToSQL(tipo)+
               " and idMacromercato = "+JDBCDataMapper.objectToSQL(macroM) ;
    
        if(destinatario!=null)
          qry+=" and idDestinatario = "+JDBCDataMapper.objectToSQL(destinatario) ;
    
        
          qry+=" order by idDestinatario ";
               
               
    try{ 
      ResultSetHelper.fillListList(conBI, qry, list);
    
    }catch(SQLException s){
      _logger.error("Errore in fase di interrogazione della tabella dbo.Destinatari  --> "+s.getMessage());
      addError("Errore in fase di interrogazione della tabella dbo.Destinatari --> "+s.toString());
    }
            
    return list;
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabFilesTwbxForBI.class);

  

  
}
