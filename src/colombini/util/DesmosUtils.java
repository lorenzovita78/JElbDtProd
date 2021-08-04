/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.util;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class DesmosUtils {
  
  public final static String ELAB_GENDATI_DESMOS_COLOMBINI="Etichette_Generazione.dsm";
  
  public final static String ELAB_GENDATI_DESMOS_FEBAL="Generazione_Globale.dsm";
  
  public final static String ELAB_STAMPE_DESMOS_FEBAL="Stampe_produzione.dsm";
  
  public final static String ELAB_LIBRETTI_DESMOS_COLOMBINI="Stampa_LibrettiSCH2.dsm";
  
  public final static String ELAB_PRECOMESSA_DESMOS_COLOMBINI="ProduzionePrecommessa.dsm";
  
  
  private static DesmosUtils instance;
  
  
  public static DesmosUtils getInstance(){
    if(instance==null){
      instance= new DesmosUtils();
    }
    
    return instance;
  }
  
  
  public String getLancioDesmosColombini(Long commessa,Date dataComm){
    Integer anno=DateUtils.getYear(dataComm);
    String commessaS=DatiProdUtils.getInstance().getStringNComm(commessa);
    
    //return anno.toString()+"__"+(commessa<100 ? "0" : "")+commessa.toString();
    return anno.toString()+"__"+commessaS;
  }
  
  
  public String getLancioDesmosFebal(Long commessa,Date dataComm){
    return getLancioDesmosFebal(commessa,dataComm,Boolean.FALSE);
  }
  
  
  
  public String getLancioDesmosFebal(Long commessa,Date dataComm,Boolean commAnticipo){
    if(!commAnticipo){
      Integer anno=DateUtils.getYear(dataComm);
      String commessaS=DatiProdUtils.getInstance().getStringNComm(commessa);
    
      String lancio=anno.toString()+commessaS;
    
      if(commessa>365){
        String wkS="";
        Integer wk =DateUtils.getWorkWeek(dataComm);
            wkS=wk.toString();
        if(wk<10)
          wkS="0"+wk.toString();
        lancio=anno.toString().substring(2,4)+wkS+commessa.toString();
      }
      
      return lancio;
      
    }else{
      return commessa.toString();
    }
    
  }
  
  /**
   * Verifica innanzi tutto se per una data commessa ci sono mq relativi a febal o rossana
   * Se sono presenti allora verifica se su DesmosFebal le elaborazioni hanno terminate
   * @param conAs400
   * @param commNTmp
   * @param dataCommN
   * @return
   * @throws QueryException
   * @throws SQLException 
   */
  public Boolean isElabsDesmosFebalFinish(Connection conAs400 , Long commNTmp,Long dataCommN) throws QueryException, SQLException{
    Connection conDbDesmosFebal=null;
    Date dataComm=DateUtils.strToDate(dataCommN.toString(), "yyyyMMdd");
    Map nOrdComm =DatiCommUtils.getInstance().loadNOrdCommessa(conAs400, commNTmp.intValue(), dataCommN);
    Double noFb1=ClassMapper.classToClass(nOrdComm.get("FB1"),Double.class); 
    Double noRs1=ClassMapper.classToClass(nOrdComm.get("RS1"),Double.class); 
    
    Double no200=(noFb1 != null ? noFb1 : 0) +  (noRs1 != null ? noRs1 : 0);
    if( no200.doubleValue()>0){
      try{
        conDbDesmosFebal=ColombiniConnections.getDbDesmosFebalProdConnection();
        Boolean bElb1=DesmosUtils.getInstance().isElabDesmosFebalFinishedWithCheck(conDbDesmosFebal,commNTmp, dataComm); 
        if(bElb1){
          Boolean bElb2=DesmosUtils.getInstance().isElabStampeDesmosFebalFinished(conDbDesmosFebal, commNTmp, dataComm);               
          return bElb2;
        }else {
         return bElb1;
        } 
      } finally{
        if(conDbDesmosFebal!=null)
          conDbDesmosFebal.close();
      }
    } else { //se la commessa non ha volume Febal allora la consideriamo terminata
      return Boolean.TRUE;
    } 
    
    //return Boolean.FALSE;
  }
  
  
  
  public Boolean isElabDesmosColombiniFinished(Connection conDbDesmosCol,Long comm,Date dataComm) throws SQLException{
    
    String lancio=getLancioDesmosColombini(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosCol, lancio, ELAB_GENDATI_DESMOS_COLOMBINI);
  } 
  
   public Boolean isElabDesmosColombiniFinishedWithCheck(Connection conDbDesmosCol,Long comm,Date dataComm) throws SQLException{
    
    String lancio=getLancioDesmosColombini(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosCol, lancio, ELAB_GENDATI_DESMOS_COLOMBINI,Boolean.TRUE);
  } 
  
  
  public Boolean isElabDesmosFebalFinished(Connection conDbDesmosFeb,Long comm,Date dataComm) throws SQLException{
    String lancio=getLancioDesmosFebal(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosFeb, lancio, ELAB_GENDATI_DESMOS_FEBAL);
  }
  
  
  public Boolean isElabDesmosFebalFinishedWithCheck(Connection conDbDesmosFeb,Long comm,Date dataComm) throws SQLException{
    String lancio=getLancioDesmosFebal(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosFeb, lancio, ELAB_GENDATI_DESMOS_FEBAL,Boolean.TRUE);
  }
  
  
  public Boolean isElabStampeDesmosFebalFinished(Connection conDbDesmosFeb,Long comm,Date dataComm) throws SQLException{
    String lancio=getLancioDesmosFebal(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosFeb, lancio, ELAB_STAMPE_DESMOS_FEBAL);
  }
  
  public Boolean isElabStampeDesmosFebalFinishedWithCheck(Connection conDbDesmosFeb,Long comm,Date dataComm) throws SQLException{
    String lancio=getLancioDesmosFebal(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosFeb, lancio, ELAB_STAMPE_DESMOS_FEBAL,Boolean.TRUE);
  }
  
  
   public Boolean isElabLibrettiDesmosColomFinished(Connection conDbDesmosCol,Long comm,Date dataComm) throws SQLException{
    
    String lancio=getLancioDesmosColombini(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosCol, lancio, ELAB_LIBRETTI_DESMOS_COLOMBINI);
  }
  
  public Boolean isElabPrecomDesmosColomFinished(Connection conDbDesmosCol,Long comm,Date dataComm) throws SQLException{
    
    String lancio=getLancioDesmosColombini(comm, dataComm);
    
    return isElabDesmosFinish(conDbDesmosCol, lancio, ELAB_PRECOMESSA_DESMOS_COLOMBINI);
  }
  
  public Boolean isElabDesmosColombiniFinished(Long comm,Date dataComm) throws SQLException{
    Connection conDbDesmos=null;
    try{
     conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
     return isElabDesmosColombiniFinished(conDbDesmos, comm, dataComm);
      
    } finally{
      if(conDbDesmos!=null)
        conDbDesmos.close();
    } 
  } 
   
  public Boolean isElabDesmosFebalFinished(Long comm,Date dataComm) throws SQLException{
    Connection conDbDesmos=null;
    try{
     conDbDesmos=ColombiniConnections.getDbDesmosFebalProdConnection();
     return isElabDesmosColombiniFinished(conDbDesmos, comm, dataComm);
      
    } finally{
      if(conDbDesmos!=null)
        conDbDesmos.close();
    } 
  }
  
  public Boolean isElabStampeDesmosFebalFinished(Long comm,Date dataComm) throws SQLException{
    Connection conDbDesmos=null;
    
    try{
     conDbDesmos=ColombiniConnections.getDbDesmosFebalProdConnection();
     return isElabStampeDesmosFebalFinished(conDbDesmos, comm, dataComm);
      
    } finally{
      if(conDbDesmos!=null)
        conDbDesmos.close();
    } 
  }
  
  /**
   * Verifica se un'elaborazione Desmos è terminata. Se non trova il record torna cmq TRUE ( il record potrebbe non esistere)
   * @param conDbDesmos
   * @param lancio
   * @param nomeBatch
   * @return
   * @throws SQLException 
   */ 
  public Boolean isElabDesmosFinish(Connection conDbDesmos,String lancio,String nomeBatch) throws SQLException{
    Boolean finish=Boolean.FALSE;
    
   
    String qry=" select DataOraEsecuzione,DataOraInserimento "+
               " from dbo.COMANDI_UTENTE " +
               " where Lancio ="+JDBCDataMapper.objectToSQL(lancio)+
               " and NomeBatch="+JDBCDataMapper.objectToSQL(nomeBatch);
    
    Object [] obj=ResultSetHelper.SingleRowSelect(conDbDesmos, qry);
    if(obj==null){
      _logger.warn("Attenzione non trovata elaborazione  +"+nomeBatch+"per commessa :"+lancio);
      finish=Boolean.TRUE;
    } else if( obj.length>0){
      String data=ClassMapper.classToString(obj[0]).trim();
      if(!StringUtils.IsEmpty(data))
        finish=Boolean.TRUE;
    }
    
    return finish;
  }
  
  
  /**
   * Verifica se una elaborazione Desmos è terminata.
   * 
   * @param conDbDesmos
   * @param lancio
   * @param nomeBatch
   * @param checkExist Boolean per far in modo che se il record non è presente torna FALSE
   * @return
   * @throws SQLException 
   */
  public Boolean isElabDesmosFinish(Connection conDbDesmos,String lancio,String nomeBatch,Boolean checkExist) throws SQLException{
    Boolean finish=Boolean.FALSE;
    
   
    String qry=" select DataOraEsecuzione,DataOraInserimento "+
               " from dbo.COMANDI_UTENTE " +
               " where Lancio ="+JDBCDataMapper.objectToSQL(lancio)+
               " and NomeBatch="+JDBCDataMapper.objectToSQL(nomeBatch);
    
    Object [] obj=ResultSetHelper.SingleRowSelect(conDbDesmos, qry);
    if(obj==null){
      _logger.warn("Attenzione non trovata elaborazione  +"+nomeBatch+"per commessa :"+lancio);
      if(!checkExist)
        finish=Boolean.TRUE;
    } else if( obj.length>0){
      String data=ClassMapper.classToString(obj[0]).trim();
      if(!StringUtils.IsEmpty(data))
        finish=Boolean.TRUE;
    }
    
    return finish;
  }
  
  
// FROM ElabDatiProdCOmmesse
//  private Boolean isElabDesmosFebalFinish(Connection conDesmosFeb,Long comm,Date dataComm) throws SQLException{
//    Boolean finish=Boolean.FALSE;
//    Integer anno=DateUtils.getYear(dataComm);
//    String lancio=lancio=anno.toString()+(comm<100 ? "0" : "")+comm.toString();
//    
//   
//    if(comm>365){
//      Integer wk =DateUtils.getWorkWeek(dataComm);
//      lancio=anno.toString().substring(2,4)+wk.toString()+comm.toString();
//    }
//    
//    String qry=" select DataOraEsecuzione,DataOraInserimento "+
//               " from DesmosFebal.dbo.COMANDI_UTENTE where Lancio ="+JDBCDataMapper.objectToSQL(lancio)+
//               " and NomeBatch='Generazione_Globale.dsm'";
//    
//    Object [] obj=ResultSetHelper.SingleRowSelect(conDesmosFeb, qry);
//    if(obj==null){
//      _logger.warn("Attenzione non trovata elaborazione febal per commessa :"+lancio);
//      finish=Boolean.TRUE;
//    } else if( obj.length>0){
//      String data=ClassMapper.classToString(obj[0]).trim();
//      if(!StringUtils.IsEmpty(data))
//        finish=Boolean.TRUE;
//    }
//    
//    return finish;
//  }
  
//  private Boolean isElabDesmosColombiniFinish(Connection conDesmosCol,Long comm,Date dataComm) throws SQLException{
//    Boolean finish=Boolean.FALSE;
//    Integer anno=DateUtils.getYear(dataComm);
//    String lancio=lancio=anno.toString()+"__"+(comm<100 ? "0" : "")+comm.toString();
//    
//   
//    String qry=" select DataOraEsecuzione,DataOraInserimento "+
//               " from DesmosColombini.dbo.COMANDI_UTENTE where Lancio ="+JDBCDataMapper.objectToSQL(lancio)+
//               " and NomeBatch='Etichette_Generazione.dsm'";
//    
//    Object [] obj=ResultSetHelper.SingleRowSelect(conDesmosCol, qry);
//    if(obj==null){
//      _logger.warn("Attenzione non trovata elaborazione febal per commessa :"+lancio);
//      finish=Boolean.TRUE;
//    } else if( obj.length>0){
//      String data=ClassMapper.classToString(obj[0]).trim();
//      if(!StringUtils.IsEmpty(data))
//        finish=Boolean.TRUE;
//    }
//    
//    return finish;
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(DesmosUtils.class);   
}
