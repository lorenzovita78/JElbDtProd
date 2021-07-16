/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.costant.NomiLineeColomb;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.QueryScartiFromTAP;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import mail.MailUtil;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabScartiPerOttimizzatore extends ElabClass{

  private Date dataInizio;
  private Date dataFine;
  private List lineeToElab=new ArrayList();
  
   private final String UPD_ZTAPSP=" UPDATE MCOBMODDTA.ZTAPSP  set TSDTE2= ?"
                                   +" WHERE TSIDSP=? ";
                                   
  
   
      private final String UPD_CustomerImportParts=" UPDATE IMAIPCNET_2210000136_Transfer.dbo.Customer_Import_Parts"
                                   + " set State= 150 ,Info9=? ,Update_User='JAVA_BATCH' ,Update_TS= ?"
                                   +" WHERE Barcode = ? ";
                                   
  
   
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
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
     if(lineeToElab.contains(NomiLineeColomb.CDL_IMALOTTO1))
       elabScartiImaLotto1XOtt(con);
  }
 
  private void  elabScartiImaLotto1XOtt(Connection conAs400){
    try {
        Date dataElab=new Date();
        List infoS=new ArrayList();
        
        infoS=getListScartiFromTAP(conAs400);
        
        prepareAndSendFileCsvForOtmP4(infoS);
        updatePzOndDbOttimizzatore(infoS, dataElab);
        updateScartiOnTap(conAs400,infoS,dataElab);
        
    } catch (SQLException s ){
      _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+s.getMessage());
      addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+s.toString());
      
    } catch (ParseException ex) {
      _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+ex.getMessage());
      addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di conversione dei dati : "+ex.toString());
    } catch (QueryException ex) {
         _logger.error("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db : "+ex.getMessage());
         addError("SCARTI "+NomiLineeColomb.CDL_IMALOTTO1+" errori in fase di lettura dei dati dal db :"+ex.toString());
    }
  }
  
  
  
  private List getListScartiFromTAP(Connection conAs400) throws SQLException, ParseException, QueryException{
    List infoS=new ArrayList();
    Date dataIni=null;

    Object [] obj=ResultSetHelper.SingleRowSelect(conAs400, "select max(tsdte2) from mcobmoddta.ZTAPSP where tsplgr="+JDBCDataMapper.objectToSQL(NomiLineeColomb.CDL_IMALOTTO1));
    if(obj!=null && obj.length>0){
      dataIni=ClassMapper.classToClass(obj[0],Date.class);
    }
    if(dataIni==null){
      addError("Data Elaborazione non valorizzata per linea"+NomiLineeColomb.CDL_IMALOTTO1+" . Impossibile processare gli scarti");
    }

    QueryScartiFromTAP q=new QueryScartiFromTAP();

    //q.setFilter(QueryScartiFromTAP.FT_CDLTRANS,"01024");
    q.setFilter(QueryScartiFromTAP.FT_CDLRESP,NomiLineeColomb.CDL_IMALOTTO1);
    q.setFilter(FilterFieldCostantXDtProd.FT_DATADA,dataIni);
    //q.setFilter(FilterFieldCostantXDtProd.FT_DATAA,DateUtils.getInizioGg(giorno));
    q.setFilter(FilterFieldCostantXDtProd.FT_DATAA,DateUtils.getFineGg(dataFine));
    q.setFilter(QueryScartiFromTAP.FT_DELAB2,"Y");

    ResultSetHelper.fillListList(conAs400, q.toSQLString(), infoS); 
    
    return infoS;
  }
  
  private void updateScartiOnTap(Connection conAs400, List<List> infoS,Date dataElab) throws SQLException  {
    PreparedStatement ps = null;
    Long id=null;
    
    try{
      ps=conAs400.prepareStatement(UPD_ZTAPSP); 
      for(List el:infoS){
        try{
          id=ClassMapper.classToClass(el.get(6),Long.class);
          ps.setTimestamp(1, new java.sql.Timestamp (dataElab.getTime()));
          ps.setLong(2, id);
          ps.execute();

        } catch(SQLException s){
          _logger.error("Errore in fase di aggiornamento dello scarto sulla tabella ZTAPSP "+s.getMessage());
          addError("Errore in fase di aggiornamento dello scarto (ZTASP) con id"+id);
        }
      }
    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  private void updatePzOndDbOttimizzatore(List<List> infoS,Date data) throws SQLException  {
    Connection con=null;
    PreparedStatement ps = null;
    String barcode=null;

    
    String dateS=DateUtils.getDataSysString();
    //String oraS=DateUtils.getOraSysLong().toString();
    String oraS=DateUtils.getOraString(new Date());
    
    try{
      con=ColombiniConnections.getDbIPCNetConnection();
      ps=con.prepareStatement(UPD_CustomerImportParts); 
      for(List el:infoS){
        try{
          barcode=ClassMapper.classToString(el.get(7));
          ps.setString(1, "SCARTI_"+dateS+oraS );
          ps.setTimestamp(2, new java.sql.Timestamp(data.getTime()) );
          ps.setString(3, barcode );
          ps.execute();

        } catch(SQLException s){
          _logger.error("Errore in fase di aggiornamento dello scarto sulla tabella ZTAPSP "+s.getMessage());
          addError("Errore in fase di aggiornamento del pezzo scartato su Customer Import Parts :"+barcode);
        }
      }
    }finally{
      if(ps!=null)
        ps.close();
      if(con!=null){
        try{
          con.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione con db IpCNET -->>"+s.getMessage());
        }
      }
    }
  }
  
  private void prepareAndSendFileCsvForOtmP4(List<List> infoS) throws SQLException, ParseException{
    Connection conDesmos=null;
    Map propsElab=getElabProperties();
    
    String mailTo=(String) propsElab.get(NameElabs.CSVMAILTO_P4);
    String mailCC=(String) propsElab.get(NameElabs.CSVMAILCC_P4);
    String pathCSv=(String) propsElab.get(NameElabs.PATHFILESCSV_P4);
    
    
    String dataS=DateUtils.DateToStr(new Date(), "yyyyMMdd_HHmmss");
    String fileNameCsv=pathCSv.substring(0, pathCSv.lastIndexOf(".csv"))+dataS+".csv";
    //+"Recuperi_"+dataS+".csv";;
    
    
    try{
      conDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      String qryCodici=getSqlForCodes(infoS);
      if(qryCodici==null)
        return ;
      
      String qry=getQueryDesmosSezP4(qryCodici);
      List infoCsv=new ArrayList();
      ResultSetHelper.fillListList(conDesmos, qry, infoCsv);
     
      try {
        prepareAndSendFileCsv(infoCsv, fileNameCsv, mailTo, mailCC, "Scarti da Ottimizzare per R1P4", "In allegato il csv contenente gli scarti da riottimizzare");
      } catch (FileNotFoundException ex) {
        _logger.error("Problemi nella generazione del file csv "+fileNameCsv+" -->"+ex.getMessage());
        addError("Errore in fasedi generazione del csv per OtmP4 -->"+ex.toString()); 
      }
    } finally{
      if(conDesmos!=null){
        try{
        conDesmos.close();
        } catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione -->"+s.getMessage());
        }
      }       
    }
  }
  
  private void prepareAndSendFileCsv(List<List> infoS, String nomeFile,String mailTo,String mailCc,String object,String textMessage) throws FileNotFoundException{
    if(infoS==null || infoS.isEmpty())
      return ;
    
     XlsXCsvFileGenerator csvRecuperi=new XlsXCsvFileGenerator(nomeFile,XlsXCsvFileGenerator.FILE_CSV);
     
     File attach=csvRecuperi.generateFile(infoS);
     List toM=new ArrayList(Arrays.asList(mailTo));
     List toCC=new ArrayList(Arrays.asList(mailCc));
     List atc=new ArrayList(Arrays.asList(attach));

     MailUtil.getInstance().sendMessage("", "infoJ@colombinigroup.com",  object,textMessage,toM ,toCC, null, atc);
    //Mail Utils...file attach
      
    
  }
  
  
  
  private String getQueryDesmosSezP4(String sqlCodici){
    StringBuilder b=new StringBuilder(
       " SELECT distinct [Commessa],[DataCommessa],[Collo] ,[Linea] ").append(
          " ,case when ([Box]<= 7 OR Box=15 OR (Box>16 and Box<=21))  then 'O' else 'P' end  ").append(
          " , [Pedana], [CodArticolo],[Descrizione] ,[Colore] ,[PartNumber]").append(
          " , [CodiceComponente] ,convert(int,[DIM1_GREZZO]) ,convert(int,[DIM2_GREZZO]) , convert(int,[SPESSORE_IMPIANTO]) ").append(
          " , [VersoVena] ,[NomeEtichetta] ,[Materiale] ,[Qta] ").append(
     " \n FROM [DesmosColombini].[dbo].[LDL09_FILE_TXT_BS_SEZ_PIANO4] a").append(
             " inner join ( select distinct a as  pnumber from ( ").append(sqlCodici).append(
             " ) c )b  on a.partnumber=b.pnumber  where 1=1");        
    
    return b.toString();
  }
  
  private String getSqlForCodes(List<List> info ){
    if(info==null || info.isEmpty())
      return null;
    
    StringBuilder select =new StringBuilder();
    int i=0;
    for(List el:info){
        if(i>0){
          select.append("\n UNION \n");
        }
        String barcode=ClassMapper.classToString(el.get(7));
        select.append(" select ").append(JDBCDataMapper.objectToSQL(barcode)).append(" as a");
        i++;
    }
    
    return select.toString();
  }
  
  private static final Logger _logger = Logger.getLogger(ElabDatiProdImpImaR1.class); 

  
  private void gestScartiPrecommessaIncas() {
    Connection conFeb =null;
    
    try{
      conFeb=ColombiniConnections.getAs400FebalConnection();
      String qry="";
      
      
    }catch(SQLException s){
      
    }finally{
      try{
      if(conFeb!=null)
        conFeb.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione con As400 Febal -->"+s.getMessage());
      }
    }
    
  }
  
}
