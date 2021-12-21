/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import as400.Utility400;
import colombini.conn.ColombiniConnections;
import colombini.model.persistence.middleware.IncasVdl.Msg103Detail;
import colombini.model.persistence.middleware.IncasVdl.Msg102Detail;
import colombini.model.persistence.middleware.TabMsgHAs400_FromVdl;
import colombini.model.persistence.middleware.TabMsgHAs400_ToVdl;
import colombini.model.persistence.middleware.MsgMiddlewareConstant;
import colombini.model.persistence.middleware.as400Vdl.MsgVdlDett_H2V;
import colombini.model.persistence.middleware.as400Vdl.MsgVdlHead_H2V;
import colombini.model.persistence.middleware.as400Vdl.MsgVdlHead_V2H;
import colombini.model.persistence.middleware.MsgAs400Dett_ToVdl;
import colombini.model.persistence.middleware.VdlAs400.MsgRepackingContainer;
import colombini.model.persistence.middleware.VdlAs400.MsgUploadShipDetail;
import colombini.model.persistence.middleware.VdlAsSqlPoe.MsgColloInfoHead;
import colombini.model.persistence.middleware.VdlAsSqlPoe.MsgUploadColloInfoDetail;
import db.ResultSetHelper;
import db.persistence.ABeanPersCRUD4Middleware;
import db.persistence.PersistenceManager;
import elabObj.ElabClass;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class ElabMiddlewareAsVDL extends ElabClass{

  @Override
  public Boolean configParams() {
    return true;
  }

  @Override
  public void exec(Connection con) {
     Connection conVdl=null;
     Connection conSql=null;
    _logger.info("Inizio elaborazione ..trasferimento dati da As400 a ");
    try{
      //conVdl=Connections.getOracleConnection("192.168.112.13", "ORCL", "DBS_HV", "DBS_HV");
      conVdl=ColombiniConnections.getDbVDLVisionConnection();
      conSql=ColombiniConnections.getDbAvanzamentoVdlConnection();
      //test(conVdl);
      _logger.info("##########--------- Da AS400 a VDL ---------##########");
       asToVdl(con, conVdl);
      
      _logger.info("##########--------- Da VDL ad AS400 ---------##########");
       vdlToAs(con, conVdl);
    
      _logger.info("##########--------- Da Incas a VDL ---------##########");
       incasToVdl(con,conVdl);
      
      _logger.info("##########--------- Da VDL a SQL ---------##########");
       vdlToSql(conSql,conVdl);
      
    }catch(SQLException s){
      addError("Attenzione impossibile stabilire la connessione con db VDL->"+s.getMessage()+" - "+s.getSQLState());
    } finally{
      try{
      if(conVdl!=null)
         conVdl.close();
      } catch(SQLException s){
        _logger.error(s.getStackTrace());
        addError("Problemi in fase di chiusura della connessione con connessione VDL -->"+s.getMessage());
      }
    }
    
  }
  
  /**
   * Legge i dati su as400 e popola le tabelle su VDL
   * @param conAs400
   * @param conVdl 
   */
  private void asToVdl(Connection conAs400 , Connection conVdl){
    PersistenceManager pm=new PersistenceManager(conVdl);
    List<List> msgsH400=new ArrayList();
    Boolean insertDett=Boolean.FALSE;
    Boolean insertHead=Boolean.FALSE;
    try{
      
      ResultSetHelper.fillListList(conAs400, getSqlReadMsgHfromAs400(MsgMiddlewareConstant.STATUS_MSG_RELEASED), msgsH400);
      if(msgsH400.size()==0){
        _logger.info("Nessun messaggio da processare su As400 ");
        return;
      }
        
      //ciclo sulle testate dei messaggi
      for(List msg400:msgsH400 ){
        BigDecimal idMsg=ClassMapper.classToClass(msg400.get(0), BigDecimal.class);
        _logger.info("Processing msg head-->"+idMsg+"  :  "+msg400.toString());
         
        //1) lettura righe e insert in vdl
        _logger.info("Read row....");
        List lstMsgDett=new ArrayList();
        ResultSetHelper.fillListList(conAs400, getSqlReadMsgDettfromAs400(idMsg), lstMsgDett);
        if(lstMsgDett.size()>0){
          _logger.info("Row-->"+lstMsgDett.get(0));
          
          _logger.info("Saving  head on VDL");
          List h=new ArrayList();
          h.add(msg400);
          pm.saveListDt(new MsgVdlHead_H2V(), h);
          insertHead=Boolean.TRUE;
          
          if(insertHead){
            _logger.info("Saving detail on VDL");
            pm.saveListDt(new MsgVdlDett_H2V(), lstMsgDett);
            insertDett=Boolean.TRUE;
          
            if(insertDett ){
              _logger.info("Update state msg on As400...");  
              updateStatusMsgAs400(conAs400, idMsg, MsgMiddlewareConstant.STATUS_MSG_PROCESSED);
            }
          }
        }  
      } 
      
    }catch(SQLException s){
      _logger.error(s.getStackTrace());
      _logger.error("Errore SQL  -->"+s.getMessage());
    }
  }
  
  
  private void vdlToAs(Connection conAs400 , Connection conVdl){
    
    elabMsgRepacking(conAs400,conVdl);
    
    elabMsgUploadShipping(conAs400,conVdl);
    
    elabMsgError(conAs400,conVdl);
  }
  
  
  private void vdlToSql(Connection conSql , Connection conVdl){
    
    elabMsgUploadColloInfo(conSql,conVdl); 
    
  }
    
  private void elabMsgRepacking(Connection conAs400 , Connection conVdl){
    MsgRepackingContainer bean=new MsgRepackingContainer();
    elabMsgFromVdlToAs(conAs400, conVdl, bean, MsgVdlHead_V2H.MSG_RepackingContainerData);
    
  }
  
  private void elabMsgUploadShipping(Connection conAs400 , Connection conVdl){
   
    MsgUploadShipDetail bean=new MsgUploadShipDetail();
    elabMsgFromVdlToAs(conAs400, conVdl, bean, MsgVdlHead_V2H.MSG_UploadShippingData);
    
  }
  
  
  private void elabMsgError(Connection conAs400 , Connection conVdl){
   
    
    elabMsgFromVdlToAs(conAs400, conVdl, null, MsgVdlHead_V2H.MSG_Error);
    
  }
  
  private void elabMsgUploadColloInfo(Connection conSql , Connection conVdl){
   
    MsgUploadColloInfoDetail bean=new MsgUploadColloInfoDetail();
    elabMsgFromVdlToSql(conSql, conVdl, bean, MsgVdlHead_V2H.MSG_UploadColloInfo);
    
  }
    
//   private void elabMsgColloInfo(Connection conSql , Connection conVdl){
//   
//    MsgColloInfoHead bean=new MsgColloInfoHead();
//    elabMsgFromVdlToSql(conSql, conVdl, bean, MsgVdlHead_V2H.MSG_UploadColloInfo);
//    
//  }
  
  private void elabMsgFromVdlToAs(Connection conAs400 , Connection conVdl,ABeanPersCRUD4Middleware bean ,String typeMsg){
    Statement stm=null;
    Integer totR=Integer.valueOf(0);
    Integer proc=Integer.valueOf(0);
    
    String qryVdl=getSqlReadMsgHfromVdl(MsgMiddlewareConstant.STATUS_MSG_RELEASED,typeMsg);
    MsgVdlHead_V2H vdlMsgH=new MsgVdlHead_V2H();
    PersistenceManager pmAs400=new PersistenceManager(conAs400);
    
    try{
      stm=conVdl.createStatement();
      _logger.info("Read msg Vdl --> "+qryVdl);
      ResultSet rs=stm.executeQuery(qryVdl);
      Long idMsg=Long.valueOf(0);
      try{
        while(rs.next()){
          totR++;
          idMsg=rs.getLong(vdlMsgH.getClmMessageId());
          _logger.info("Processing Msg Id-->"+idMsg);
          
          List elHead=getListInfoForHMsgAs400(idMsg, rs);
          
          //carico le info di dettaglio se mi è stato fornito un bean valido
          if(bean!=null){
            bean.setIdMsg(idMsg);
            bean.loadInfoBeanFromSource(conVdl);
            bean.setTypeObj(ABeanPersCRUD4Middleware.TYPE_DESTINATION);
          }
         // _logger.info("Save Head >>"+elHead.toString());
         // pmAs400.saveListDt(new TabMsgHAs400_FromVdl(), Arrays.asList(elHead));
          
          
          //salvo le info di dettaglio se mi è stato fornito un bean valido
          if(bean!=null){ 
            _logger.info("Save Detail >>"+bean.toString());
            pmAs400.storeDtFromBean(bean);
          }
          
          _logger.info("Update State on VDL");
          updateStatusMsgVdl(conVdl, idMsg, MsgMiddlewareConstant.STATUS_MSG_PROCESSED);
          proc++;
        }  
          
      } catch(SQLException s){
        _logger.error("Errore in fase di processing msg "+idMsg+" -->"+s.getMessage());
        addError("Errore in fase di processig del msg : "+idMsg+" -->"+s.toString());
      } catch(Exception e ){
        _logger.error("Attenzione eccezione generica -->"+e.getMessage());
        addError(" Attenzione eccezione generica -->"+e.getMessage());
      } finally{
        try{
        rs.close();
        }catch(SQLException s){
          addWarning("Errore in fase di chiusura del resultset ");
        }
      }       
          
    }catch(SQLException s){
      addError("Impossibile eseguire uno statment su db Oracle -->"+s.getMessage());
    }finally{
      _logger.info("Tipo messaggio"+ typeMsg +"Righe lette -->"+totR+" righe processate -->"+proc);
      try{
      if(stm!=null)
        stm.close();
      if(pmAs400!=null)
        pmAs400=null;
      }catch(SQLException s){
        addWarning("Errore in fase di chiusura dello Statment");
      }
    }  
  }
  
  //GG Nuovo per infocollo
    private void elabMsgFromVdlToSql(Connection Sql , Connection conVdl,ABeanPersCRUD4Middleware bean ,String typeMsg){
    Statement stm=null;
    Integer totR=Integer.valueOf(0);
    Integer proc=Integer.valueOf(0);
    
    String qryVdl=getSqlReadMsgHfromVdl(MsgMiddlewareConstant.STATUS_MSG_RELEASED,typeMsg);
    MsgVdlHead_V2H vdlMsgH=new MsgVdlHead_V2H();
    PersistenceManager pmSqlPoe=new PersistenceManager(Sql);
    
    try{
      stm=conVdl.createStatement();
      _logger.info("Read msg Vdl --> "+qryVdl);
      ResultSet rs=stm.executeQuery(qryVdl);
      Long idMsg=Long.valueOf(0);
      try{
        while(rs.next()){
          totR++;
          idMsg=rs.getLong(vdlMsgH.getClmMessageId());
          _logger.info("Processing Msg Id-->"+idMsg);
          
          List elHead=getListInfoForHMsgSql(idMsg, rs);
          
          //carico le info di dettaglio se mi è stato fornito un bean valido
          if(bean!=null){
            bean.setIdMsg(idMsg);
            bean.loadInfoBeanFromSource(conVdl);
            bean.setTypeObj(ABeanPersCRUD4Middleware.TYPE_DESTINATION);
          }
         // _logger.info("Save Head >>"+elHead.toString());
         //  pmSqlPoe.saveListDt(new TabMsgHAs400_FromVdl(), Arrays.asList(elHead));
          
         //Copio i dati del bean to Bean Header
          if(bean!=null){
              MsgColloInfoHead beanHeader=new MsgColloInfoHead((MsgUploadColloInfoDetail) bean);
              beanHeader.setTypeObj(ABeanPersCRUD4Middleware.TYPE_DESTINATION);
              if(!pmSqlPoe.checkExist(beanHeader,beanHeader.getFieldValuesMapDestinationForCheck())){
                   _logger.info("Save Testata >>"+beanHeader.toString());
                    try{
                        pmSqlPoe.storeDtFromBean(beanHeader);
                    } catch(SQLException s){
                            addError("Errore in fase di processig del msg : "+idMsg+" -->"+s.toString());
                            updateStatusMsgVdl(conVdl, idMsg, MsgMiddlewareConstant.STATUS_MSG_REJECTED);
                    }
              }
          }
         
          //salvo le info di dettaglio se mi è stato fornito un bean valido
          if(bean!=null){ 
            _logger.info("Save Detail >>"+bean.toString());
            try{
                pmSqlPoe.storeDtFromBean(bean);
                _logger.info("Update State on VDL");
                updateStatusMsgVdl(conVdl, idMsg, MsgMiddlewareConstant.STATUS_MSG_PROCESSED);
            } catch(SQLException s){
                            addError("Errore in fase di processig (insert) del msg : "+idMsg+" -->"+s.toString());
                            updateStatusMsgVdl(conVdl, idMsg, MsgMiddlewareConstant.STATUS_MSG_REJECTED);
            }
          }
          
          //_logger.info("Update State on VDL");
          //updateStatusMsgVdl(conVdl, idMsg, MsgMiddlewareConstant.STATUS_MSG_PROCESSED);
          proc++;
        }  
          
      } catch(SQLException s){
        System.out.println("Errore in fase di processing msg "+idMsg+" -->"+s.getMessage());
        addError("Errore in fase di processig del msg : "+idMsg+" -->"+s.toString());
      } catch(Exception e ){
        _logger.error("Attenzione eccezione generica -->"+e.getMessage());
        addError(" Attenzione eccezione generica -->"+e.getMessage());
      } finally{
        try{
        rs.close();
        }catch(SQLException s){
          addWarning("Errore in fase di chiusura del resultset ");
        }
      }       
          
    }catch(SQLException s){
      addError("Impossibile eseguire uno statment su db Oracle -->"+s.getMessage());
    }finally{
      _logger.info("Tipo messaggio"+ typeMsg +"Righe lette -->"+totR+" righe processate -->"+proc);
      try{
      if(stm!=null)
        stm.close();
      if(pmSqlPoe!=null)
        pmSqlPoe=null;
      }catch(SQLException s){
        addWarning("Errore in fase di chiusura dello Statment");
      }
    }  
  }
  
  
  private List getListInfoForHMsgAs400(Long idMsg,ResultSet rs ) throws SQLException{
    List listEHead=new ArrayList();
    
    listEHead.add(idMsg);
    listEHead.add(rs.getString(2));
    listEHead.add(rs.getString(3));
    listEHead.add(rs.getTimestamp(4));
    listEHead.add(null); //datafine
    listEHead.add(null);
    listEHead.add(null);
    
    return listEHead;
  }
  
  
   private List getListInfoForHMsgSql(Long idMsg,ResultSet rs ) throws SQLException{
    List listEHead=new ArrayList();
    
    listEHead.add(idMsg);
    listEHead.add(rs.getString(2));
    listEHead.add(rs.getString(3));
    listEHead.add(rs.getTimestamp(4));
    listEHead.add(null); //datafine
    listEHead.add(null);
    listEHead.add(null);

    return listEHead;
  }
  
  private void incasToVdl(Connection conAs400, Connection conVdl) {
    Connection conDbIncas=null;
    try{
       conDbIncas=ColombiniConnections.getDbIncasConnection();
       
       msg102IncasToVdl(conDbIncas,conAs400,conVdl);
       
       msg103IncasToVdl(conDbIncas,conAs400,conVdl);
        
    } catch (SQLException s){
      _logger.error("Errore in fase di connessione al db incas .Impossibile proseguire -->"+s.getMessage());
      addError("Errore in fase di collegamento del db Incas "+s.toString());  
    } finally{
      if(conDbIncas!=null){
        try{
          conDbIncas.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiusura della connessione con il db Incas-->"+s.getMessage());
        }  
      }
    }       
  }
  
  private void msg102IncasToVdl(Connection conDbIncas, Connection conAs400, Connection conVdl) {
    //procedura deve leggere tutti i msg su db incas
    _logger.info("Proccessing msg 102 -- Commercializzati ");
    Msg102Detail msg102=new Msg102Detail(null);
    elabMsgIncasToVdl(conDbIncas, conAs400, conVdl, msg102, MsgMiddlewareConstant.TYPE_OBJMSG_BUNDLEDATA);
  }
  
  
  private void msg103IncasToVdl(Connection conDbIncas, Connection conAs400, Connection conVdl) {
    //procedura deve leggere tutti i msg su db incas
    _logger.info("Proccessing msg 103 -- Precommessa ");
    Msg103Detail msg103=new Msg103Detail(null);
    elabMsgIncasToVdl(conDbIncas, conAs400, conVdl, msg103, MsgMiddlewareConstant.TYPE_OBJMSG_CONNECTIONCARTCOLLO);
    
    _logger.info("Proccessing msg 103 -- da Commercializzati ");
    msg103=new Msg103Detail(Msg102Detail.DBINCAS_NAME);
    elabMsgIncasToVdl(conDbIncas, conAs400, conVdl, msg103, MsgMiddlewareConstant.TYPE_OBJMSG_CONNECTIONCARTCOLLO);
  }
  
 
  
  private void elabMsgIncasToVdl(Connection conDbIncas, Connection conAs400, Connection conVdl,ABeanPersCRUD4Middleware msgBean,String typeMsg) {
    // per ogni msg  --> 1) staccare id da As400 2) creare testata su VDL  3 ) creare dettaglio su vdl 4) aggiornare dato su Incas
    Long idMessaggio=null;
    Integer count=Integer.valueOf(0);
    try{
      List<ABeanPersCRUD4Middleware> beans=msgBean.getListElementsFromSource(conDbIncas);
      _logger.info("Messaggi da processare --> "+beans.size());
      PersistenceManager pmVdl=new PersistenceManager(conVdl);
      PersistenceManager pmIncas=new PersistenceManager(conDbIncas);
      
      for(ABeanPersCRUD4Middleware bean:beans){
        try{
          //reperisco idMessaggio da As400
          idMessaggio=getIdMsgFromAs400(conAs400);
          bean.setIdMsg(idMessaggio);
          bean.setTypeMsg(typeMsg);
          
          //salvataggio dati su VDL
          bean.setTypeObj(ABeanPersCRUD4Middleware.TYPE_DESTINATION);
          //crea testata su Vdl
          createNewHeadMsgVdl(pmVdl, bean.getIdMsg(), bean.getTypeMsg());
          //salva msg su Vdl
          pmVdl.storeDtFromBean(bean);
          
          //aggiornamento dati su Incas
          bean.setTypeObj(ABeanPersCRUD4Middleware.TYPE_SOURCE);
          pmIncas.updateDt(bean);
          count++;
          
        } catch (SQLException s){
          addError("Errore in fase di salvataggio del messaggio :  "+typeMsg+" - "+idMessaggio+" -->"+s.toString());
          _logger.error("Errore in fase di salvataggio del messaggio "+idMessaggio+" -->"+s.getMessage());
          
        }
      }
    }catch(SQLException s){
      addError("Errore in fase di lettura su db Incas. Impossibile proseguire -->"+s.toString());
      _logger.error("Errore in fase di lettura su db Incas. Impossibile proseguire -->"+s.getMessage());
    } finally {
      _logger.info("Messaggi processati  --> "+count);
    }
    
  }
  
  private Long getIdMsgFromAs400(Connection conAs400) throws SQLException{
    TabMsgHAs400_ToVdl msg=new TabMsgHAs400_FromVdl();
    
    return Utility400.getSequenceDbValue(conAs400, msg.getSequenceName());
  }
  
  
  private void createNewHeadMsgVdl(PersistenceManager pm,Long idMessaggio,String typeMsg) throws SQLException{
    Map msgMap=new HashMap ();
    MsgVdlHead_H2V vdlMsg =new MsgVdlHead_H2V();
    msgMap.put(vdlMsg.getClmMessageId(),idMessaggio);
    msgMap.put(vdlMsg.getClmStatus(),MsgMiddlewareConstant.STATUS_MSG_RELEASED);
    msgMap.put(vdlMsg.getClmMsgObjType(),typeMsg);
    msgMap.put(vdlMsg.getClmInsertTime(),new Date());
    msgMap.put(vdlMsg.getClmCompletionTime(),null);
    msgMap.put(vdlMsg.getClmRjctMsgId(),null);
    msgMap.put(vdlMsg.getClmRjctMsgInfo(),null);
    
    pm.saveMapDt(vdlMsg, msgMap);
    
  }
  
  
 
  
  
  /**
   * aggiorna lo stato del messaggio ad inviato se l'insert in VDL è andato a buon fine
   * Da prevedere un'aggiornamento anche dopo lettura di processato su VDL???
   * @param con
   * @param idMsg 
   */
  private void updateStatusMsgAs400(Connection con , BigDecimal idMsg,String state){
    TabMsgHAs400_ToVdl msg=new TabMsgHAs400_ToVdl();
    
    String upd =msg.getUpdateMsgString(idMsg, state);
    
    try {
      PreparedStatement ps=con.prepareStatement(upd);
      ps.setBigDecimal(1, idMsg);
      ps.execute();
      
    } catch (SQLException ex) {
      _logger.error("Attenzione errore in fase di aggiornamento dello stato dei msg su As400 passati a VDL . IdMessaggio :"+idMsg+" --> "+ex.getMessage());
      addError(" Errore in fase di aggiornamento del msg su As400 :"+idMsg);
    }
      
  }
  
  
  
  
  private void updateStatusMsgVdl(Connection con , Long idMsg,String state){
    MsgVdlHead_V2H msgV=new MsgVdlHead_V2H();
    PreparedStatement ps=null;
    try {
      ps=con.prepareStatement(msgV.getUpdateMsgString(state));
      ps.setLong(1, idMsg);
      ps.execute();
      
    } catch (SQLException ex) {
      _logger.error("Attenzione errore in fase di aggiornamento dello stato del msg VDL . IdMessaggio :"+idMsg+" --> "+ex.getMessage());
      addError(" Errore in fase di aggiornamento del msg VDL :"+idMsg);
    } finally{
      try{
        if(ps!=null)
          ps.close();
      }catch(SQLException s){
        addWarning("Errore in fase di chiusura dello statment per aggiornamento stato idMsg"+idMsg);
      }
    }
      
  }
  
  private String getSqlReadMsgHfromAs400(String status){
 
    TabMsgHAs400_ToVdl msg=new TabMsgHAs400_ToVdl();
    return msg.getSqlForReadMessage(status);
  }
  
  private String getSqlReadMsgDettfromAs400(BigDecimal idMsg){

    MsgAs400Dett_ToVdl tbl=new MsgAs400Dett_ToVdl();
    return tbl.getSqlForReadMessage(idMsg);
  }
  
  
  private String getSqlReadMsgHfromVdl(String status,String type){
    MsgVdlHead_V2H msgV2H=new MsgVdlHead_V2H();

    return msgV2H.getSqlForReadMessage(status,type);
  }
  
//  private String getSqlReadMsgDettfromVdl(BigDecimal idMsg){
//    MsgVdlDett_V2H msgV2D=new MsgVdlDett_V2H();
//    
//    return msgV2D.getSqlForReadMessage(idMsg);
//  }
  

  
  private static final Logger _logger = Logger.getLogger(ElabMiddlewareAsVDL.class);

  
  
//  private void vdlToAsOld(Connection conAs400 , Connection conVdl){
//    Statement stm=null;
//    Integer totR=Integer.valueOf(0);
//    Integer proc=Integer.valueOf(0);
//    
//    String qryVdl=getSqlReadMsgHfromVdl(MsgConstant.STATUS_MSG_RELEASED,"");
//    _logger.info("Query msg da importare su As400 : "+qryVdl);
//    MsgVdlHead_V2H vdlMsgH=new MsgVdlHead_V2H();
//    PersistenceManager pmAs400=new PersistenceManager(conAs400);
//    
//    try{
//      stm=conVdl.createStatement();
//      _logger.info("Read msg Vdl ");
//      ResultSet rs=stm.executeQuery(qryVdl);
//      List elHead=null;
//      List elDett=null;
//      BigDecimal idMsg=null;
//      //processo un smg per volta
//      
//      
//      while(rs.next()){
//        try{
//          totR++;
//          elHead=new ArrayList();
//          elDett=new ArrayList();
//          
//          idMsg=rs.getBigDecimal(vdlMsgH.getClmMessageId());
//          _logger.info("Processing Msg Id-->"+idMsg);
//          elHead.add(idMsg);
//          elHead.add(rs.getString(2));
//          elHead.add(rs.getString(3));
//          elHead.add(rs.getTimestamp(4));
//          elHead.add(null); //datafine
//          elHead.add(null);
//          elHead.add(null);
//          
//          
//          ResultSetHelper.fillListList(conVdl, getSqlReadMsgDettfromVdl(idMsg), elDett);
//          
//          _logger.info("Save Head >>"+elHead.toString());
//          pmAs400.saveListDt(new TabMsgHAs400_FromVdl(), Arrays.asList(elHead));
//         
//          _logger.info("Save Detail>>"+elDett.toString());
//          pmAs400.saveListDt(new MsgAs400DettImballo(), elDett);
//          
//          _logger.info("Update State on VDL");
//          updateStatusMsgVdl(conVdl, idMsg, MsgConstant.STATUS_MSG_PROCESSED);
//          proc++;
//          
//        }catch(SQLException s){
//          _logger.error("Errore in fase di processing msg "+idMsg+" -->"+s.getMessage());
//          addError("Errore in fase di processig del msg : "+idMsg+" -->"+s.toString());
//        }catch(Exception e ){
//          _logger.error("Attenzione eccezione generica -->"+e.getMessage());
//          addError(" Attenzione eccezione generica -->"+e.getMessage());
//        }        
//      }
//    }catch(SQLException s){
//      addError("Impossibile eseguire uno statment su db Oracle -->"+s.getMessage());
//    }finally{
//      _logger.info("Righe lette -->"+totR+" righe processate -->"+proc);
//      try{
//      if(stm!=null)
//        stm.close();
//      if(pmAs400!=null)
//        pmAs400=null;
//      }catch(SQLException s){
//        addWarning("Errore in fase di chiusura dello Statment");
//      }
//    }
//  }

  
}
