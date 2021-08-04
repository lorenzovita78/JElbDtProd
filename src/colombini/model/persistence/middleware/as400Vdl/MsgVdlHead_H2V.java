/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.as400Vdl;

import colombini.model.persistence.middleware.AMessageHeadTable;

/**
 *
 * @author lvita
 */
public class MsgVdlHead_H2V extends AMessageHeadTable {
  
  protected final static String  TABLE="H2V_QUEUE";
  
  public final static String MESSAGEID="MESSAGEID";
  public final static String STATUS="STATUS";
  public final static String MESSAGEOBJECTTYPE="MESSAGEOBJECTTYPE";
  public final static String INSERTTS="INSERTTS";
  public final static String COMPLETIONTS="COMPLETIONTS";
  public final static String REJECTMESSAGEID="REJECTMESSAGEID";
  public final static String REJECTINFORMATION="REJECTINFORMATION";
  
  
 
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
    return TABLE;
  }

  
}
