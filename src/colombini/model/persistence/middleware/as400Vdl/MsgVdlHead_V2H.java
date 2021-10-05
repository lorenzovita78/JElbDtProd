/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.as400Vdl;

import colombini.model.persistence.middleware.AMessageHeadTable;
import colombini.model.persistence.middleware.MsgMiddlewareConstant;
import db.JDBCDataMapper;
import utils.ListUtils;

/**
 *
 * @author lvita
 */
public class MsgVdlHead_V2H extends AMessageHeadTable {
  
  public final static String  TABLE_NAME="V2H_QUEUE";
  
  public final static String MESSAGEID="MESSAGEID";
  public final static String STATUS="STATUS";
  public final static String MESSAGEOBJECTTYPE="MESSAGEOBJECTTYPE";
  public final static String INSERTTS="INSERTTS";
  public final static String COMPLETIONTS="COMPLETIONTS";
  public final static String REJECTMESSAGEID="REJECTMESSAGEID";
  public final static String REJECTINFORMATION="REJECTINFORMATION";
  
  
  public final static String MSG_RepackingContainerData="RepackingContainerData";
  public final static String MSG_UploadShippingData="UploadShippingData";
  public final static String MSG_Error="ERROR";
  public final static String MSG_UploadColloInfo="ColloInfo";
  
  
  @Override
  public String getClmMessageId() {
    return MESSAGEID;
  }

  @Override
  public String getClmStatus() {
    return STATUS;
  }

  @Override
  public String getClmMsgObjType() {
    return MESSAGEOBJECTTYPE;
  }

  @Override
  public String getClmInsertTime() {
    return INSERTTS;
  }

  @Override
  public String getClmCompletionTime() {
    return COMPLETIONTS;
  }

  @Override
  public String getClmRjctMsgId() {
    return REJECTMESSAGEID;
  }

  @Override
  public String getClmRjctMsgInfo() {
    return REJECTINFORMATION;
  }

  @Override
  public String getLibName() {
    return "";
  }

  @Override
  public String getTabName() {
    return TABLE_NAME;
  }

  
  public String getUpdateMsgString(String state){
    String upd=" UPDATE "+TABLE_NAME+
                "\n SET status="+JDBCDataMapper.objectToSQL(state)+" , CompletionTs = SYSDATE "+
                "\n WHERE 1=1  " +
                " and messageId = ?";
    
    return upd;
  }

  
  public String getSqlForReadMessage(String state,String typeMsg){
    String S= "select " +ListUtils.toCommaSeparatedString(this.getFields()) +
            "\n  from "+TABLE_NAME
          + "\n  where 1=1 "
          + "\n and Status="+JDBCDataMapper.objectToSQL(MsgMiddlewareConstant.STATUS_MSG_RELEASED)
          + "\n and MESSAGEOBJECTTYPE="+JDBCDataMapper.objectToSQL(typeMsg) 
          + "\n and rownum<=5000"   
          + "\n order by 1";  
    
    return S;
  }
  
}
