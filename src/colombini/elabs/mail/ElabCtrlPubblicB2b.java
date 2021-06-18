/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabCtrlPubblicB2b extends ElabInvioMail{
  
  
  public Map getMapFileOnB2b(){
    Map mapFiles=new HashMap();
    Connection conSqlS=null;
    List<List> l=new ArrayList();
    try{
      conSqlS=ColombiniConnections.getDbPortaleB2bConnection();
      ResultSetHelper.fillListList(conSqlS, getQueryB2bFiles(), l);
      
      for(List el:l){
        mapFiles.put(ClassMapper.classToString(el.get(0)).toUpperCase(), ClassMapper.classToClass(el.get(1) , Date.class));
      }
      
    }catch(SQLException s){
      _logger.error("Errore in fase di connessione al db per portale B2b -->"+s.getMessage());
      addError("Errore in fase di connessione al db per portale B2b -->"+s.getMessage());
      
    }finally{
      try{
      if(conSqlS!=null)
        conSqlS.close();
      } catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione Sql Server -->"+s.getMessage());
      }
    }
    
    return mapFiles;
  }
  
  private String getQueryB2bFiles(){
    String dataS=DateUtils.dateToStr(dataInizio, "yyyy-MM-dd");
    return "SELECT LTRIM(rtrim(fileName)) ,[STARTDATE]   FROM [q_webshare].[dbo].[WS_DOCUMENT]"+
            " where REFERENCEDATE=CONVERT (date,'"+dataS+"')" ;
  }
  
  
  private List<List> getListFilesAs400(Connection con){
    List<List> listF=new ArrayList();
    try{
       ResultSetHelper.fillListList(con, getQueryFilesAs400(), listF);
    } catch(SQLException s){
      _logger.error("Errore in fase di connessione As400 -->"+s.getMessage());
      addError("Errore in fase di connessione As400 -->"+s.getMessage());
      
    }
    return listF;
  }
  
  
  
  private String getQueryFilesAs400(){
    String dataS=DateUtils.dateToStr(dataInizio, "yyyyMMdd");
    return "select distinct trim(zlfile) , 'ZPL305' as source from mcobmoddta.ZPL305 "+
            " where zlstat=90 and zlfile<>'' "+
            " and zlrgdt=" +dataS;

  }
  
  
  public List<List> getListDifference(Map mapF , List<List> listFiles){
    List<List> diff=new ArrayList();
    if(listFiles==null || listFiles.isEmpty()){
      return diff;
    }
      
    for(List infoFile:listFiles){
      String nfile=ClassMapper.classToString(infoFile.get(0));
      String source=ClassMapper.classToString(infoFile.get(1));
      if(!mapF.containsKey(nfile.trim().toUpperCase())){
        diff.add(new ArrayList(Arrays.asList(nfile , source)));
      }
    }
    
    
    return diff;
  }
  
  
  
  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    Map mappaB2b=getMapFileOnB2b();
    String dataS=DateUtils.dateToStr(dataInizio, "yyyyMMdd");
    
    if(mappaB2b.isEmpty())
      return;
    
    //1 controllo file as400
    List<List> fileAs400=getListFilesAs400(con);
    prepareFileCsv(messageBase, mappaB2b, fileAs400, "//pegaso/scambio/elb/filePubblicazioniAs400_"+dataS+".csv");
    
    
    //2 controlli file DesmoFebal
//    List<List> fileDesmosFebal=getListFilesDF();
//    prepareFileCsv(messageBase, mappaB2b, fileDesmosFebal, "//pegaso/scambio/elb/filePubblicazioniDesmosFebal_"+dataS+".csv");
    
    
    if(messageBase.getFilesAttach().size()==0)
      //nel caso entrambi i file sono vuoti non mando la mail
       messageBase.setAddressesTo(new ArrayList());
    
  }

  
  private void prepareFileCsv(MailMessageInfoBean messageBase,Map fileMap,List<List> listFiles,String nomeFileCsv){
    if(listFiles==null || listFiles.isEmpty())
      return ;
    
    List diff=getListDifference(fileMap, listFiles);
    
    if(diff.size()>0){
      diff.add(0, new ArrayList(Arrays.asList("NomeFile" , "SorgenteDati ")));
      XlsXCsvFileGenerator csvPPS=new XlsXCsvFileGenerator(nomeFileCsv,XlsXCsvFileGenerator.FILE_CSV);
      try{
         File attach=csvPPS.generateFileCsv(diff);
         if(attach!=null)
           messageBase.addFileAttach(attach);
         
      } catch(FileNotFoundException s){
        addError("Errore in fase di generazione del file "+csvPPS.getFileName() +" --> "+s.toString());
        _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
      }
    }
    
  }
  
  
  @Override
  public String getIdMessage() {
    return "CHECKPUBBLB2B";
  }
  
  
  
  private List getListFilesDF() {
     Connection conSqlS=null;
    List<List> l=new ArrayList();
    List<List> listFileTmp=new ArrayList(); 
    try{
      conSqlS=ColombiniConnections.getDbDesmosFebalProdConnection();
      List<String> tipiLista=getListTipiListaDesmos(conSqlS);       
      for(String tipoL:tipiLista){
        listFileTmp =getListFileFromLista(conSqlS,tipoL);
        if(listFileTmp!=null)
          l.addAll(listFileTmp);
      }
      
      
    }catch(SQLException s){
      _logger.error("Errore in fase di connessione al db DesmosFebal -->"+s.getMessage());
      addError("Errore in fase di connessione al db DesmosFebal -->"+s.getMessage());
      
    }finally{
      try{
      if(conSqlS!=null)
        conSqlS.close();
      } catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione Sql Server -->"+s.getMessage());
      }
    }
    
    return l;
  }

  private List<String> getListTipiListaDesmos(Connection conSqlS) throws SQLException {
    List tipiL=new ArrayList();
    
    String query="SELECT ltrim(rtrim([TipoLista])) \n FROM [dbo].[ListePerCDL]\n " +
                 "  where CodiceStab = 'FEFO'   and TipoLista not like '%_XML_%' \n" +
                 //"  and CodiceRep not like '03_%'\n" +
                 "  group by [TipoLista]";
    
    ResultSetHelper.fillListList(conSqlS, query, tipiL);
   
    
    return tipiL;
  }

  private List<List> getListFileFromLista(Connection conSqlS, String tipoL) {
    List<List> list=new ArrayList();
    List<List> listFinal=new ArrayList();
    String query=getQueryForTipoLista(tipoL);
    if(StringUtils.IsEmpty(query)){
      addError("Query per estrazione non valorizzata correttamente");
      return null;
    }
    try{
      ResultSetHelper.fillListList(conSqlS, query, list);
      int i=0;
      for(List ltemp:list){
         String s =ClassMapper.classToString(ltemp.get(0));
         String source =ClassMapper.classToString(ltemp.get(1));
         int l=s.length();
         int l1=s.lastIndexOf(".");
         
         
         if(l1<0){
           s=s+"."+getFileExtention(tipoL);
           List lt=new ArrayList(Arrays.asList(s , source));
           listFinal.add(lt);
         }else{
           listFinal.add(ltemp);
         }    
         i++;
      }
      
    }catch (SQLException s){
      addError("Errore in fase di esecuzione della query :"+query+"  --> "+s.getMessage());
      _logger.error("Errore in fase di esecuzione della query :"+query+"  --> "+s.getMessage() );
    }
    
    return listFinal;
  }

  
  
  private String getQueryForTipoLista(String tipoLista){
   StringBuilder  qry= new StringBuilder();
   String nomeCampo="DesmosNomeFile"+getFileExtention(tipoLista);
   
   
   String subq=getSubQueryLancio();
   if(StringUtils.IsEmpty(subq))
     return "";
   
   
   qry.append("SELECT distinct ").append(nomeCampo).append(" ,  ").append(
                           JDBCDataMapper.objectToSQL(tipoLista)).append(
              " from ").append(tipoLista).append (" a");
   qry.append(" inner join (").append(subq).append(" ) b on a.DesmosLancio = b.Lancio");   
   
   
   return qry.toString();
   
  }
  
  private String getSubQueryLancio(){
    String qry="";
    try {
      String dataS=DateUtils.dateToStr(DateUtils.getInizioGg(dataInizio), "yyyy-MM-dd HH.mm.ss");
      String dataS2=DateUtils.dateToStr(DateUtils.getFineGg(dataInizio), "yyyy-MM-dd HH.mm.ss"); 
    
       qry=" select distinct lancio\n from COMANDI_UTENTE\n where  1=1 \n" +
               " and (NomeBatch like 'Ftp_Forn%' or NomeBatch like '%ComandoUtente%')";
      
      qry+=" and DataOraEsecuzione between "+JDBCDataMapper.objectToSQL(dataS)+
           " and  "+JDBCDataMapper.objectToSQL(dataS2);
   } catch (ParseException ex) {
      addError("Errore in fase di conversione delle date --> "+ ex.getMessage());
    }
   
   return qry;
   
  }
  
  
  
  private String getFileExtention(String nomeLista){
    if(nomeLista.toUpperCase().contains("PDF"))
      return "PDF";
    
    else if(nomeLista.toUpperCase().contains("TXT"))
      return "TXT";
    
    
    return "";
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabCtrlPubblicB2b.class); 

}
