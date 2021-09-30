/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.VdlAsSqlPoe;

import db.JDBCDataMapper;
import db.ResultSetHelper;
import db.persistence.ABeanPersCRUD4Middleware;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import utils.ListUtils;
/**
 *
 * @author ggraziani
 */
public class MsgUploadColloInfoDetail extends  ABeanPersCRUD4Middleware{
  
  
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_COLLOINFO";
  
  
  private static final String LIBRARY_DESTINATION_SQLPOE = "AvanzamentoVDL";
  private static final String TABLE_DESTINATION_SQLPOE = "V2H_ColloInfo_Detail";
  
  
  public final static String FSOURCE__MESSAGEID               ="MESSAGEID";
  public final static String FSOURCE__IDCOLLO                 ="COLLOID";
  public final static String FSOURCE__STATUS                  ="SATUS";
  public final static String FSOURCE__TIMESTAMP               ="TIMESTAMP";
  public final static String FSOURCE__STORAGETIMESTAMP        ="STORAGETIMESTAMP";
  public final static String FSOURCE__MFSYSTEM                ="MFSYSTEM";
  public final static String FSOURCE__CONTAINERID             ="CONTAINERID";
  public final static String FSOURCE__BOXCREATOR              ="BOXCREATOR";
  public final static String FSOURCE__AREA                    ="AREA";
  public final static String FSOURCE__AISLE                   ="AISLE";  
  public final static String FSOURCE__SIDE                    ="SIDE";
  public final static String FSOURCE__X                       ="X";
  public final static String FSOURCE__Y                       ="Y";
  public final static String FSOURCE__Z                       ="Z";
  public final static String FSOURCE__RETURNCODE              ="RETURNCODE";
  public final static String FSOURCE__SEQUENCENO              ="SEQUENCENO";
  public final static String FSOURCE__BATCHID                 ="BATCHID";
  public final static String FSOURCE__REPACK                  ="REPACK";
  public final static String FSOURCE__SHIPPINGDATESTARTTS     ="SHIPPINGDATESTARTTS";
  public final static String FSOURCE__SHIPPINGTIMESTARTTS     ="SHIPPINGTIMESTARTTS";
  public final static String FSOURCE__SHIPPINGDATEENDSTS      ="SHIPPINGDATEENDSTS";
  public final static String FSOURCE__SHIPPINGTIMEENDSTS      ="SHIPPINGTIMEENDSTS";
  public final static String FSOURCE__SHIPPINGAISLE           ="SHIPPINGAISLE";
  
  
  public final static String FDEST__MESSAGEID               ="MESSAGEID";
  public final static String FDEST__IDCOLLO                 ="COLLOID";
  public final static String FDEST__STATUS                  ="SATUS";
  public final static String FDEST__TIMESTAMP               ="TIMESTAMP";
  public final static String FDEST__STORAGETIMESTAMP        ="STORAGETIMESTAMP";
  public final static String FDEST__MFSYSTEM                ="MFSYSTEM";
  public final static String FDEST__CONTAINERID             ="CONTAINERID";
  public final static String FDEST__BOXCREATOR              ="BOXCREATOR";
  public final static String FDEST__AREA                    ="AREA";
  public final static String FDEST__AISLE                   ="AISLE";  
  public final static String FDEST__SIDE                    ="SIDE";
  public final static String FDEST__X                       ="X";
  public final static String FDEST__Y                       ="Y";
  public final static String FDEST__Z                       ="Z";
  public final static String FDEST__RETURNCODE              ="RETURNCODE";
  public final static String FDEST__SEQUENCENO              ="SEQUENCENO";
  public final static String FDEST__BATCHID                 ="BATCHID";
  public final static String FDEST__REPACK                  ="REPACK";
  public final static String FDEST__SHIPPINGDATESTARTTS     ="SHIPPINGDATESTARTTS";
  public final static String FDEST__SHIPPINGTIMESTARTTS     ="SHIPPINGTIMESTARTTS";
  public final static String FDEST__SHIPPINGDATEENDSTS      ="SHIPPINGDATEENDSTS";
  public final static String FDEST__SHIPPINGTIMEENDSTS      ="SHIPPINGTIMEENDSTS";
  public final static String FDEST__SHIPPINGAISLE           ="SHIPPINGAISLE";
  
  
  
  private Long idCollo;
  private Long nSpedizione;
  private Date dataSpedizione;
  private Long returnCode;
  private String returnInfo;
  private Date dataInserimento;
  
  
  public MsgUploadColloInfoDetail (){
    super();
  }
  
  public MsgUploadColloInfoDetail (Long idMs){
    super();
    setIdMsg(idMs);
  }
  
 
  @Override
  public void setIdMsg(Long idMsg) {
    super.setIdMsg(idMsg); //To change body of generated methods, choose Tools | Templates.
  }

  
  
  public Long getIdCollo() {
    return idCollo;
  }

  public void setIdCollo(Long idCollo) {
    this.idCollo = idCollo;
  }

  public Long getNSpedizione() {
    return nSpedizione;
  }

  public void setNSpedizione(Long nSpedizione) {
    this.nSpedizione = nSpedizione;
  }

  public Date getDataSpedizione() {
    return dataSpedizione;
  }

  public void setDataSpedizione(Date dataSpedizione) {
    this.dataSpedizione = dataSpedizione;
  }

  public Long getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(Long returnCode) {
    this.returnCode = returnCode;
  }

  public String getReturnInfo() {
    return returnInfo;
  }

  public void setReturnInfo(String returnInfo) {
    this.returnInfo = returnInfo;
  }

  public Date getDataInserimento() {
    return dataInserimento;
  }

  public void setDataInserimento(Date dataInserimento) {
    this.dataInserimento = dataInserimento;
  }
  
  
  
  @Override
  public List<ABeanPersCRUD4Middleware> getListElementsFromSource(Connection con) throws SQLException {
    
    
    return null;
  }
 
  @Override
  public void loadInfoBeanFromSource(Connection con) throws SQLException {
    String sql=getSqlSelectSource();
    List l=new ArrayList();
    
    Object [] obj= ResultSetHelper.SingleRowSelect(con, sql);
    if(obj!=null && obj.length>0){
      this.setIdCollo(ClassMapper.classToClass(obj[1],Long.class));
      this.setNSpedizione(ClassMapper.classToClass(obj[2],Long.class));
      this.setDataSpedizione(ClassMapper.classToClass(obj[3],Date.class));
      this.setReturnCode(ClassMapper.classToClass(obj[4],Long.class));
      this.setReturnInfo(ClassMapper.classToString(obj[5]));
      this.setDataInserimento(ClassMapper.classToClass(obj[6],Date.class));
    }
    
    
  }
  
  private String getSqlSelectSource(){
    String campi=ListUtils.toCommaSeparatedString(getFieldsSource());
    String select ="Select "+campi+ 
                  "\n from "+getLibNameSource()+" . "+getTableNameSource()+
                  " where 1=1 "+
                  " and "+FSOURCE__MESSAGEID+" = "+JDBCDataMapper.objectToSQL(this.getIdMsg());
    
    return select;
  }
  
  @Override
  public List<String> getKeyFieldsSource() {
    return Arrays.asList(FSOURCE__MESSAGEID);
  }

  @Override
  public List<String> getKeyFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID);
  }

  @Override
  public Map<String, Object> getFieldValuesMapSourceForUpd() {
    return null;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestinationForDlt() {
     Map m=new HashMap();
    
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapSource() {
    Map m=new HashMap();
    m.put(FSOURCE__MESSAGEID,this.getIdMsg());
    m.put(FSOURCE__IDCOLLO,this.idCollo);
    m.put(FSOURCE__RETURNCODE,this.returnCode);

    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    m.put(FDEST__IDCOLLO,this.idCollo);
    m.put(FDEST__RETURNCODE,this.returnCode);

    
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID,FDEST__IDCOLLO,FDEST__STATUS,FDEST__TIMESTAMP,FDEST__STORAGETIMESTAMP,FDEST__MFSYSTEM,FDEST__CONTAINERID,FDEST__BOXCREATOR,FDEST__AREA,FDEST__AISLE,FDEST__SIDE,FDEST__X,FDEST__Y,FDEST__Z,FDEST__RETURNCODE,FDEST__SEQUENCENO,FDEST__BATCHID,FDEST__REPACK,FDEST__SHIPPINGDATESTARTTS,FDEST__SHIPPINGTIMESTARTTS,FDEST__SHIPPINGDATEENDSTS,FDEST__SHIPPINGTIMEENDSTS,FDEST__SHIPPINGAISLE);
  }

  @Override
  public List<String> getFieldsSource() {
return Arrays.asList(FSOURCE__MESSAGEID,FSOURCE__IDCOLLO,FSOURCE__STATUS,FSOURCE__TIMESTAMP,FSOURCE__STORAGETIMESTAMP,FSOURCE__MFSYSTEM,FSOURCE__CONTAINERID,FSOURCE__BOXCREATOR,FSOURCE__AREA,FSOURCE__AISLE,FSOURCE__SIDE,FSOURCE__X,FSOURCE__Y,FSOURCE__Z,FSOURCE__RETURNCODE,FSOURCE__SEQUENCENO,FSOURCE__BATCHID,FSOURCE__REPACK,FSOURCE__SHIPPINGDATESTARTTS,FSOURCE__SHIPPINGTIMESTARTTS,FSOURCE__SHIPPINGDATEENDSTS,FSOURCE__SHIPPINGTIMEENDSTS,FSOURCE__SHIPPINGAISLE);
  }

  @Override
  public String getLibNameSource() {
    return LIBRARY_SOURCE_VDL;
  }

  @Override
  public String getTableNameSource() {
    return TABLE_SOURCE_VDL;
  }

  @Override
  public String getLibNameDestination() {
    return LIBRARY_DESTINATION_SQLPOE;
  }

  @Override
  public String getTableNameDestination() {
    return LIBRARY_DESTINATION_SQLPOE;
  }

  @Override
  public List<Integer> getFieldTypesSource() {
    List l =new ArrayList();
    
    l.add(Types.NUMERIC);     //id Messaggio da As400
    l.add(Types.NUMERIC);    //idCollo
    l.add(Types.NUMERIC);    //numero Spedizione
    l.add(Types.TIMESTAMP);    //data Spedizione
    l.add(Types.NUMERIC);    // AISLE
    l.add(Types.VARCHAR);    
    l.add(Types.TIMESTAMP);
    
    
    
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypesDestination() {
    List l =new ArrayList();
    
    l.add(Types.NUMERIC);     //id Messaggio da As400
    l.add(Types.NUMERIC);    //idCollo
    l.add(Types.NUMERIC);    //numero Spedizione
    l.add(Types.DATE);    //data Spedizione
    l.add(Types.NUMERIC);    // AISLE
    l.add(Types.VARCHAR);    
    l.add(Types.TIMESTAMP);
    
    
    
    
    return l;
  }

  @Override
  public String toString() {
    return "ID: "+this.getIdMsg()+" - IDCOLLO : "+this.getIdCollo()+" - SPEDIZIONE: "+this.getNSpedizione();
  }

  
  
}
