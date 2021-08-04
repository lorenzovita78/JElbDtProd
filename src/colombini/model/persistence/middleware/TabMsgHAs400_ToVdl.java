/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.persistence.ISequence;
import java.math.BigDecimal;

/**
 *
 * @author lvita
 */
public class TabMsgHAs400_ToVdl extends AMessageHeadTable implements ISequence{

  protected final static String TABLE_NAME="ZVAOMS";
  public final static String FIELDS="ZMIDMS, ZMSTAT, ZMTPMS, ZMRGDT, ZMFIDT, ZMIDME, ZMMSG";
  
  public final static String ZMIDMS="ZMIDMS";
  public final static String ZMSTAT="ZMSTAT";
  public final static String ZMTPMS="ZMTPMS";
  public final static String ZMRGDT="ZMRGDT";
  public final static String ZMFIDT="ZMFIDT";
  public final static String ZMIDME="ZMIDME";
  public final static String ZMMSG="ZMMSG";
  

  

  @Override
  public String getClmMessageId() {
    return ZMIDMS;
  }

  @Override
  public String getClmStatus() {
    return ZMSTAT;
  }

  @Override
  public String getClmMsgObjType() {
    return ZMTPMS;
  }

  @Override
  public String getClmInsertTime() {
    return ZMRGDT;
  }

  @Override
  public String getClmCompletionTime() {
    return ZMFIDT;
  }

  @Override
  public String getClmRjctMsgId() {
    return ZMIDME;
  }

  @Override
  public String getClmRjctMsgInfo() {
    return ZMMSG;
  }

  @Override
  public String getLibName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTabName() {
    return TABLE_NAME;
  }
 
  
  
  public String getTableNameComplete(){
    return getLibraryName()+"."+getTableName();
  }
  
  
  public String getSqlForReadMessage(String status){
    String S= "select " +TabMsgHAs400_ToVdl.FIELDS +
            "\n  from "+getTableNameComplete()
          + "\n  where 1=1 "
          + "\n and ZMSTAT="+JDBCDataMapper.objectToSQL(status)
          + "\n order by 1";  
            
    
    return S; 
  }
          
  public String getUpdateMsgString(BigDecimal idMsg,String status){
    String upd =" UPDATE "+getTableNameComplete()+
                "\n SET ZMSTAT="+JDBCDataMapper.objectToSQL(status)+
                " , ZMFIDT= (current timestamp) "+
                "\n WHERE 1=1  " +
                " and ZMIDMS = ?";
    
    return upd;
  }
  
  
  

  @Override
  public String getSequenceName() {
    return  getLibName()+"."+"SEQ_ZVAOMS";

  }
  
}
