/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QryDeleteZtapci;
import colombini.query.produzione.QryDeleteZtappi;
import db.persistence.PersistenceManager;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.StringUtils;

/**
 * Classe che si preoccupa di cancellare i dati di produzione per data Commessa 
 * @author ggraziani
 */
public class ElabDeleteDatiProdCommesse extends ElabClass{
  
  public final static String DATAETK="$DATA$";
  public final static String COMMETK="$COMM$";
  public final static String COLLOETK="$COLLO$";
  public final static String NARTETK="$NART$";
  
  
  public final static String COMMESSA="COMMESSA";
  public final static String DATACOMM="DATACOMM";
  public final static String TIPOCOMM="TIPOCOMM";
  public final static String DATAREF="DATAINI";

  

  
  private Long nComm=null;
  private Integer tipoComm=null;
  private Date dataComm=null; 
  private Date dataRef=null; 
  private List lineeToElab=null;
  
  
  public ElabDeleteDatiProdCommesse(){
    super(); 
  }
  
  
  @Override
  public void exec(Connection con) {
    
    Map propsElab=getElabProperties();
    
    //pulizia tabelle
   // puliziaTabelle(con,propsElab);
    //---
    try { 
      PersistenceManager apm=new PersistenceManager(con);
      
      //Se la data commessa non è un parametro, cerco la data dell'ultima commessa elaborata (tipo 0)
      if(dataComm == null && nComm == null ){
          dataComm=GetDataUltComm(con);  
        }
      
      if(nComm == null){
          nComm=Long.valueOf(0);
      }
      
      if(dataComm!=null){
          
        List<List> commDaCancel=new ArrayList();
        commDaCancel=DatiProdUtils.getInstance().getListCommesseToCancel(con, dataComm, nComm ,tipoComm);
        _logger.info(" Commesse da cancellare n. "+commDaCancel.size()+" --> "+commDaCancel.toString());

        puliziaDatiProd(con,commDaCancel,nComm,dataComm,lineeToElab,tipoComm);
      }
      else {
            _logger.info(" DataCommessa non trovata ");
        }
    } catch (SQLException ex) {
      addError("Impossibile caricare la lista di commesse da elaborare :"+ex.getMessage());
    } catch(QueryException qe){
      addError("Impossibile caricare la lista di commesse da elaborare :"+qe.getMessage());
    } finally{
      
    }  
  }
  
  
  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
    
      
    nComm=ClassMapper.classToClass(param.get(COMMESSA),Long.class);
    dataComm=ClassMapper.classToClass(param.get(DATACOMM),Date.class);
    tipoComm=ClassMapper.classToClass(param.get(TIPOCOMM),Integer.class);
    dataRef=ClassMapper.classToClass(param.get(DATAREF),Date.class);
    
    String linee="";
    if(param.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(param.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    if(param.get(COMMESSA)!=null && param.get(DATACOMM)==null )
        return Boolean.FALSE;
    
    return Boolean.TRUE;
  }
   

   private Date GetDataUltComm(Connection con){
      Date dataUltComm=null;
      String dataUltCommString=null; 
      String query="select zccdld data  FROM MCOBMODDTA.ZJBLOG ,MCOBMODDTA.ZCOMME  where zcrgdt<VARCHAR_FORMAT(CURRENT TIMESTAMP, 'YYYYMMDD') "
              + " and zcrgdt>VARCHAR_FORMAT(CURRENT TIMESTAMP, 'YYYYMMDD') -3 and zcmccd=0 order by zcrgdt desc FETCH FIRST 1 ROWS ONLY";
      
       try {
      PreparedStatement ps=con.prepareStatement(query);
      ResultSet rs = ps.executeQuery();
      DateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        if (rs.next())
        {
            dataUltCommString = rs.getString(1);    
            dataUltComm = formatter.parse(dataUltCommString);
        }
        else return null;
                
        } 
        catch (ParseException ex) {
             _logger.error(" Errore in fase di conversione dati per pulizia tabelle --> "+ex.getMessage());
         }
        catch (SQLException ex) {
               addError(" Errore in fase trovare l'ultima data commessa -->> "+ex.getMessage());
         }
       
   return  dataUltComm;
   }
   
  
  /**
  Cancello i dati delle tabelle ztapci e ztappi per data commessa (oppure per nro commessa se il parametro nrocomm è compilato)
   */
  private void puliziaDatiProd(Connection con ,List<List> CommDaCancel,Long NroComm, Date DataCommRef, List lineeToElab, Integer TipoComm){
    
    //Verifica se ci sono commesse da cancellare
    if (CommDaCancel.isEmpty()){
        _logger.info("Non ci sono commesse da cancellare ");
        return;
    }
    //Lista data commesse a cancellare - Non può essere null
    //List<String> DatacommDaCancel=new ArrayList();
   
    String DataCommDaCancel=null;
  
     for (List<String> comm:CommDaCancel.subList(0, 1)){
      DataCommDaCancel=ClassMapper.classToClass(comm.get(0),String.class);
     }
     
     //Verifica se ci sono data commesse da cancellare
    if (DataCommDaCancel==null){
        _logger.info("Non ci sono commesse da cancellare ");
        return;
    }
     
    try {
     String QueryZtapci=null;
     String QueryZtappi=null;   
     
     //Query delete Ztapci
     QryDeleteZtapci q=new QryDeleteZtapci();   
     q.setFilter(FilterQueryProdCostant.FTDATACOMMN, DataCommDaCancel);
     if(NroComm!=0){
        q.setFilter(FilterQueryProdCostant.FTNUMCOMM, Long.toString(NroComm));
     }
     if(lineeToElab!=null && lineeToElab.size()>0){
        q.setFilter(FilterFieldCostantXDtProd.FT_LINEETOELAB, lineeToElab.toString());
     }
     
     //Query delete Ztappi
     QryDeleteZtappi q2=new QryDeleteZtappi();   
     q2.setFilter(FilterQueryProdCostant.FTDATACOMMN, DataCommDaCancel);
     if(NroComm!=0){
        q2.setFilter(FilterQueryProdCostant.FTNUMCOMM, Long.toString(NroComm));
     }
     if(lineeToElab!=null && lineeToElab.size()>0){
        q2.setFilter(FilterFieldCostantXDtProd.FT_LINEETOELAB, lineeToElab.toString());
     }
     
     QueryZtapci=q.toSQLString();
     QueryZtappi=q2.toSQLString();

      
      _logger.info("Pulizia dati ztapci --> "+ QueryZtapci); 
      PreparedStatement ps=con.prepareStatement(QueryZtapci);
      ps.execute();
      _logger.info("Pulizia ztapci effettuata");
      
      _logger.info("Pulizia dati ztappi --> "+ QueryZtappi); 
      PreparedStatement ps2=con.prepareStatement(QueryZtappi);
      ps2.execute();
      _logger.info("Pulizia ztappi effettuata");
      
    } catch (SQLException ex) {
      addError(" Errore in fase di pulizia tabelle per esecuzione dello statement -->> "+ex.getMessage());
    }
    catch (QueryException e) {
      addError(" Errore in fase di pulizia tabelle per esecuzione dello statement -->> "+e.getMessage());
    }
   
  }
  
  
  

   private static final Logger _logger = Logger.getLogger(ElabDeleteDatiProdCommesse.class);


  
}

