/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import static colombini.elabs.ElabCaricoCommLinee.COMMESSA;
import static colombini.elabs.ElabCaricoCommLinee.DATACOMM;
import static colombini.elabs.ElabCaricoCommLinee.TIPOCOMM;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 * Classe che si preoccupa di cancellare i dati di produzione per commessa di diverse linee
 * @author ggraziani
 */


/*
public class ElabDeleteDatiProdCommesse extends ElabClass{
  
  public final static String DATAETK="$DATA$";
  public final static String COMMETK="$COMM$";
  public final static String COLLOETK="$COLLO$";
  public final static String NARTETK="$NART$";

  private Date dataRif;
  private Integer tipoComm=Integer.valueOf(0);
  private Integer nComm=null;
  private Date dataElab=null;
  
  
  public ElabDeleteDatiProdCommesse(){
    super(); 
  }
  
  
  @Override
  public void exec(Connection con) {
    
    Map propsElab=getElabProperties();
    
    //copia dati su storico
    copiaDatiSuStorico(con, propsElab);
    //pulizia tabelle
    puliziaTabelle(con,propsElab);
    //---
    try { 
      PersistenceManager apm=new PersistenceManager(con);
      _logger.info("Giorno rif: "+dataRif);
      List<List> commGg=new ArrayList();
      commGg=DatiProdUtils.getInstance().getListGgCommesse(con, dataRif, null,Boolean.TRUE);
      _logger.info(" Commesse disponibili n. "+commGg.size()+" --> "+commGg.toString());
      Map  commEx=getMapCommessePresenti(con);
    

        List commsR1P4=getListCommesseR1P4();


    } catch (SQLException ex) {
      addError("Impossibile caricare la lista di commesse da elaborare :"+ex.getMessage());
    } catch(QueryException qe){
      addError("Impossibile caricare la lista di commesse da elaborare :"+qe.getMessage());
    } finally{
      
    }  
  }
  
  
  
  @Override
  public Boolean configParams() {
    Map parm=getInfoElab().getParameter();
    
    if(parm.get(ALuncherElabs.DATAINIELB)==null)
      return Boolean.FALSE;
    
    dataRif=(Date) parm.get(ALuncherElabs.DATAINIELB);
    nComm=ClassMapper.classToClass(parm.get(COMMESSA),Integer.class);
    dataElab=ClassMapper.classToClass(parm.get(DATACOMM),Date.class);
    tipoComm=ClassMapper.classToClass(parm.get(TIPOCOMM),Integer.class);
    
    return Boolean.TRUE;
  }

  
  private Map getMapCommessePresenti(Connection con) throws SQLException{
    Long dtL=DateUtils.getDataForMovex(dataRif);
    List<List> commesse=new ArrayList();
    Map mappa=new HashMap<String,List> ();

    String select=" select distinct trim(tiplgr) tiplgr,tidtco,ticomm from mcobmoddta.ztapci where 1=1 "+
                  " and tidtco>="+JDBCDataMapper.objectToSQL(dtL)+
                  " order by tiplgr,tidtco desc";  

    ResultSetHelper.fillListList(con, select, commesse);

    for(List comm:commesse){
      String plgr=ClassMapper.classToString(comm.get(0));
      Long dtC=ClassMapper.classToClass(comm.get(1),Long.class);
      Long ncomm=ClassMapper.classToClass(comm.get(2),Long.class);
      Map commMapCdl=new HashMap();

      if(mappa.containsKey(plgr)){
        commMapCdl=(Map) mappa.get(plgr);
      }
      commMapCdl.put(ncomm,dtC);
      mappa.put(plgr,commMapCdl);

    }

    return mappa;
  }
  
  public List<List> getListCommToSave(List<List> commesse,Map commEx,String cdL){
    Map commesseIn=(Map) commEx.get(cdL);
    List commesseOut=new ArrayList();
    for(List commDist:commesse){
      Long commTmp=ClassMapper.classToClass(commDist.get(1),Long.class);
      Long dtTmp=ClassMapper.classToClass(commDist.get(0),Long.class); //data spedizione
      Long dtElbTmp=ClassMapper.classToClass(commDist.get(2),Long.class); //data elaborazione 
      Integer tipoC=ClassMapper.classToClass(commDist.get(3),Integer.class); //tipo Elab 
      if((commesseIn==null || commesseIn.isEmpty()) || !commesseIn.containsKey(commTmp)){
        List recTmp=new ArrayList();
        recTmp.add(commTmp);
        recTmp.add(dtTmp);
        recTmp.add(dtElbTmp);
        recTmp.add(tipoC);
        commesseOut.add(recTmp);
      }
    }
    
    
    return commesseOut;
  }
  
  public List<List> getListCommToSaveCkDate(List<List> commesse,Map commEx,String cdL){
    Map commesseIn=(Map) commEx.get(cdL);
    List commesseOut=new ArrayList();
    if( commesseIn==null || commesseIn.isEmpty() ){
      commesseOut.addAll(commesse);
    }else{
    
      for(List commDist:commesse){
        Long dtCommTmp=ClassMapper.classToClass(commDist.get(0),Long.class); //data spedizione
        Long commTmp=ClassMapper.classToClass(commDist.get(1),Long.class);
        Long dtElbTmp=ClassMapper.classToClass(commDist.get(2),Long.class); //data elaborazione 
        Integer tipoC=ClassMapper.classToClass(commDist.get(3),Integer.class); //tipo Elab

        Long dataCommCIn=(Long) commesseIn.get(commTmp);

        if( dataCommCIn==null || (commesseIn.containsKey(commTmp) && !dataCommCIn.equals(dtCommTmp)  )     
          ){
          List recTmp=new ArrayList();
          recTmp.add(dtCommTmp);
          recTmp.add(commTmp);
          recTmp.add(dtElbTmp);
          recTmp.add(tipoC);
          commesseOut.add(recTmp);
        }
      }
    }
    
    return commesseOut;
  }

  

   private Boolean isElabDesmosFinished(Connection con, Long comm,Date dataC,Integer tipo) throws SQLException{
     Integer anno=DateUtils.getYear(dataC);
     if(tipo.equals(Integer.valueOf(9)))
       return DesmosUtils.getInstance().isElabPrecomDesmosColomFinished(con, comm, dataC);  
     else 
       return DesmosUtils.getInstance().isElabDesmosColombiniFinished(con, comm, dataC);
   }
   
 
  
  
  
  private void copiaDatiSuStorico(Connection con ,Map propsElab){

    Calendar c=new GregorianCalendar();
    c.setTime(new Date());
    if(c.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY)
      return;
   
    try {
      StringBuilder qry=new StringBuilder(
              "INSERT INTO mcobmoddta.ztapcis0 \n ").append(
              " select A.*  from mcobmoddta.ztapci A \n").append(
                            " LEFT OUTER JOIN mcobmoddta.ztapciS0 B \n").append(
                            " ON A.TICONO=B.TICONO AND A.TIPLGR=B.TIPLGR AND A.TIBARP=B.TIBARP ").append(
                            " AND A.TICOMM=B.TICOMM AND A.TIDTCO=B.TIDTCO \n").append(
              " WHERE A.TIPLGR='CQUALITA' \n").append(
              " AND B.TIBARP IS NULL ");
                      
      String stmQry=qry.toString();
      _logger.info("Copia dati ZTAPCI per CQUALITA --> "+ stmQry); 
      PreparedStatement ps=con.prepareStatement(stmQry);
      ps.execute();
      _logger.info("Copia dati effettuata");         
    
    
    } catch (SQLException ex) {
      addError(" Errore in fase di copia dei dati di storico -->> "+ex.getMessage());
    }  
      
      
   
  }
  
  
  
   private void puliziaTabellePerDataComm(Connection con ,Map propsElab,int comm,Date dataComm){
 
    try {
      data=DateUtils.getFineGg(data);
      Long dataN=DateUtils.getDataForMovex(data);
      Long dataNNano=DateUtils.getDataForMovex(dataNano);
//      String del4= " delete from  MCOBMODDTA.ZTAPCC  where 1=1 and tcdtap<="+JDBCDataMapper.objectToSQL(data);
      
      String del1= " delete from  MCOBMODDTA.ZTAPCP  where 1=1 and tpdtin<="+JDBCDataMapper.objectToSQL(data);
      
      String del2= " delete from  MCOBMODDTA.ZTAPPI  where 1=1 and txdtrf<="+JDBCDataMapper.objectToSQL(dataN);
     
      String del3= " delete from  MCOBMODDTA.ZTAPCI  where 1=1 and tidtco<="+JDBCDataMapper.objectToSQL(dataN);
      
      //cancellazione per nanocommesse
      
      String del2a= " delete from  MCOBMODDTA.ZTAPPI  where 1=1 and txcomm>=361 and txcomm<=391 and txdtrf<="+JDBCDataMapper.objectToSQL(dataNNano);
     
      String del3a= " delete from  MCOBMODDTA.ZTAPCI  where 1=1 and ticomm>=361 and ticomm<=391 and tidtco<="+JDBCDataMapper.objectToSQL(dataNNano);
      
      // 
      
      String del4= " delete from  MCOBMODDTA.ZTAPTI  where 1=1 and TTDTCO<="+JDBCDataMapper.objectToSQL(dataN);
      
      String del5= " delete from  MCOBMODDTA.ZTAPTP  where 1=1 and TYDTIN<="+JDBCDataMapper.objectToSQL(data);
      
      _logger.info("Pulizia dati letture --> "+ del1); 
      PreparedStatement ps=con.prepareStatement(del1);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati di commessa --> "+ del2); 
      ps=con.prepareStatement(del2);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati commessa info aggiuntive --> "+ del3); 
      ps=con.prepareStatement(del3);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati nano commessa --> "+ del2a); 
      ps=con.prepareStatement(del2a);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati nano commessa info aggiuntive --> "+ del3a); 
      ps=con.prepareStatement(del3a);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
       
      _logger.info("Pulizia dati commessa info appoggio --> "+ del4); 
      ps=con.prepareStatement(del4);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati lettura colli incompleti --> "+ del5); 
      ps=con.prepareStatement(del5);
      ps.execute();
      _logger.info("Pulizia effettuata");
      
    } catch (ParseException ex) {
      _logger.error(" Errore in fase di conversione dati per pulizia tabelle --> "+ex.getMessage());
    } catch (SQLException ex) {
      addError(" Errore in fase di pulizia tabelle per esecuzione dello statement -->> "+ex.getMessage());
    }
   
  }
  
  
  

   private static final Logger _logger = Logger.getLogger(ElabDeleteDatiProdCommesse.class);


  
}

*/