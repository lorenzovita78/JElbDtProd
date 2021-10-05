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
public class MsgColloInfoHead extends  ABeanPersCRUD4Middleware{
  
  
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_COLLOINFO";
  
  
  private static final String LIBRARY_DESTINATION_SQLPOE = "AvanzamentoVDL";
  private static final String TABLE_DESTINATION_SQLPOE = "V2H_ColloInfo_Header";
  
  
  public final static String FSOURCE__MESSAGEID               ="MESSAGEID";
  public final static String FSOURCE__IDCOLLO                 ="COLLOID";
  public final static String FSOURCE__COLLODESC               ="COLLODESC";
  public final static String FSOURCE__BUNDLELD                ="BUNDLELD";
  public final static String FSOURCE__BOXID                   ="BOXID";
  public final static String FSOURCE__LAYERID                 ="LAYERID";
  public final static String FSOURCE__PLATEID                 ="PLATEID";
  public final static String FSOURCE__WEIGHT                  ="WEIGHT";
  public final static String FSOURCE__HEIGHT                  ="HEIGHT";
  public final static String FSOURCE__COMMISSIONID            ="COMMISSIONID";  
  public final static String FSOURCE__INTERNALSEQ             ="INTERNALSEQ";
  public final static String FSOURCE__PRODUCTIONLINE          ="PRODUCTIONLINE";
  public final static String FSOURCE__WAREHOUSE               ="WAREHOUSE";
  public final static String FSOURCE__DELIVERYID              ="DELIVERYID";
  public final static String FSOURCE__CLIENTID                ="CLIENTID";
  public final static String FSOURCE__CUSTOMERNO              ="CUSTOMERNO";
  public final static String FSOURCE__CUSTOMERNAME            ="CUSTOMERNAME";
  public final static String FSOURCE__CUSTOMERORDERNO         ="CUSTOMERORDERNO";
  public final static String FSOURCE__TRUCKLOADNUMBER         ="TRUCKLOADNUMBER";
  public final static String FSOURCE__TRUCKLOADDATE           ="TRUCKLOADDATE";
  public final static String FSOURCE__TRUCKERNAME             ="TRUCKERNAME";
  public final static String FSOURCE__DELIVERYZONE            ="DELIVERYZONE";
  
  
  public final static String FDEST__MESSAGEID               ="MESSAGEID";
  public final static String FDEST__IDCOLLO                 ="COLLOID";
  public final static String FDEST__COLLODESC               ="COLLODESC";
  public final static String FDEST__BUNDLELD                ="BUNDLELD";
  public final static String FDEST__BOXID                   ="BOXID";
  public final static String FDEST__LAYERID                 ="LAYERID";
  public final static String FDEST__PLATEID                 ="PLATEID";
  public final static String FDEST__WEIGHT                  ="WEIGHT";
  public final static String FDEST__HEIGHT                  ="HEIGHT";
  public final static String FDEST__COMMISSIONID            ="COMMISSIONID";  
  public final static String FDEST__INTERNALSEQ             ="INTERNALSEQ";
  public final static String FDEST__PRODUCTIONLINE          ="PRODUCTIONLINE";
  public final static String FDEST__WAREHOUSE               ="WAREHOUSE";
  public final static String FDEST__DELIVERYID              ="DELIVERYID";
  public final static String FDEST__CLIENTID                ="CLIENTID";
  public final static String FDEST__CUSTOMERNO              ="CUSTOMERNO";
  public final static String FDEST__CUSTOMERNAME            ="CUSTOMERNAME";
  public final static String FDEST__CUSTOMERORDERNO         ="CUSTOMERORDERNO";
  public final static String FDEST__TRUCKLOADNUMBER         ="TRUCKLOADNUMBER";
  public final static String FDEST__TRUCKLOADDATE           ="TRUCKLOADDATE";
  public final static String FDEST__TRUCKERNAME             ="TRUCKERNAME";
  public final static String FDEST__DELIVERYZONE            ="DELIVERYZONE";
  
  
  
  private Long idCollo;
  private Long nSpedizione;
  private Date dataSpedizione;
  private Long returnCode;
  private String returnInfo;
  private Date dataInserimento;
  
  
  public MsgColloInfoHead (){
    super();
  }
  
  public MsgColloInfoHead (Long idMs){
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
  //  m.put(FSOURCE__RETURNCODE,this.returnCode);

    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    m.put(FDEST__IDCOLLO,this.idCollo);
   // m.put(FDEST__RETURNCODE,this.returnCode);

    
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID,FDEST__IDCOLLO,FDEST__COLLODESC,FDEST__BUNDLELD,FDEST__BOXID,FDEST__LAYERID,FDEST__PLATEID,FDEST__WEIGHT,FDEST__HEIGHT,FDEST__COMMISSIONID,FDEST__INTERNALSEQ,FDEST__PRODUCTIONLINE,FDEST__WAREHOUSE,FDEST__DELIVERYID,FDEST__CLIENTID,FDEST__CUSTOMERNO,FDEST__CUSTOMERNAME,FDEST__CUSTOMERORDERNO,FDEST__TRUCKLOADNUMBER,FDEST__TRUCKLOADDATE,FDEST__TRUCKERNAME,FDEST__DELIVERYZONE);
  }

  @Override
  public List<String> getFieldsSource() {
    return Arrays.asList(FSOURCE__MESSAGEID,FSOURCE__IDCOLLO,FSOURCE__COLLODESC,FSOURCE__BUNDLELD,FSOURCE__BOXID,FSOURCE__LAYERID,FSOURCE__PLATEID,FSOURCE__WEIGHT,FSOURCE__HEIGHT,FSOURCE__COMMISSIONID,FSOURCE__INTERNALSEQ,FSOURCE__PRODUCTIONLINE,FSOURCE__WAREHOUSE,FSOURCE__DELIVERYID,FSOURCE__CLIENTID,FSOURCE__CUSTOMERNO,FSOURCE__CUSTOMERNAME,FSOURCE__CUSTOMERORDERNO,FSOURCE__TRUCKLOADNUMBER,FSOURCE__TRUCKLOADDATE,FSOURCE__TRUCKERNAME,FSOURCE__DELIVERYZONE);
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
    return TABLE_DESTINATION_SQLPOE;
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
