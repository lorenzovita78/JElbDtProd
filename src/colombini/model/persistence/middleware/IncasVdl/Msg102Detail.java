/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.IncasVdl;

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
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class Msg102Detail extends  ABeanPersCRUD4Middleware{

  
  private static final String COD_MSG_102="102";
  public static final String DBINCAS_NAME = "EsColombiniComm74";
  private static final String TABLE_SOURCE_INCAS = "dbo.HSTVDLTX";
  //private static final String TABLE_DESTINATION_VDL = "H2V_102";
  private static final String TABLE_DESTINATION_VDL = "H2V_BundleData"; 
  
  public final static String FSOURCE__HST_ID= "Hst_ID";
  public final static String FSOURCE__HST_DATAELABORAZIONE= "Hst_DataElaborazione";
  public final static String FSOURCE__HST_TAG= "Hst_Tag";
  
  public final static String FSOURCE__HVDL_TRANSACTIONTYPE= "HVDL_TransactionType";
  public final static String FSOURCE__HVDL_SYSTEMDATE= "HVDL_SystemDate";
  public final static String FSOURCE__HVDL_SYSTEMTIME= "HVDL_SystemTime";
  public final static String FSOURCE__HVDL_COLLOID= "HVDL_ColloID";
  public final static String FSOURCE__HVDL_IDMESSAGGIO= "HVDL_IDMESSAGGIO";
  
  public final static String FDEST__MESSAGEID    ="MESSAGEID";
  public final static String FDEST__DATAKEY      ="DATAKEY";
  public final static String FDEST__SYSTEMDATE   ="SYSTEMDATE";
  public final static String FDEST__SYSTEMTIME   ="SYSTEMTIME";
  public final static String FDEST__IDCOLLO      ="IDCOLLO";
  
  public final static String SOURCE_TAG_COMPLETE="T";
  
  public final static Integer DIM_FIELDS_COLLI=Integer.valueOf(256); 
  
  private Integer actionType;
  private String dataS;
  private String oraS;
  private List<String> idColli;

  //campi  dell'archivio sorgente che necessitano di mappatura
  private Long sourceHstId;
  private Date sourceDataElab;
  private String sourceTag;
  
  
  private String dbSource;
  
//  public Msg102Detail(){
//    super();
//    idColli=Arrays.asList(new String[DIM_FIELDS_COLLI]);
//  }
  
  
  public Msg102Detail(String dbSourceName){
    super();
    idColli=Arrays.asList(new String[DIM_FIELDS_COLLI]);
    
    if(StringUtils.IsEmpty(dbSourceName))
       this.dbSource=DBINCAS_NAME;
    else
      this.dbSource=dbSourceName;
  }
  

  public Integer getActionType() {
    return actionType;
  }

  public void setActionType(Integer actionType) {
    this.actionType = actionType;
  }

  public String getDataS() {
    return dataS;
  }

  public void setDataS(String dataS) {
    this.dataS = dataS;
  }

  public String getOraS() {
    return oraS;
  }

  public void setOraS(String oraS) {
    this.oraS = oraS;
  }

 
  public Long getSourceHstId() {
    return sourceHstId;
  }

  public void setSourceHstId(Long sourceHstId) {
    this.sourceHstId = sourceHstId;
  }

  public Date getSourceDataElab() {
    return sourceDataElab;
  }

  public void setSourceDataElab(Date sourceDataElab) {
    this.sourceDataElab = sourceDataElab;
  }

  public String getSourceTag() {
    return sourceTag;
  }

  public void setSourceTag(String sourceTag) {
    this.sourceTag = sourceTag;
  }

  public List<String> getIdColli() {
    return idColli;
  }

  public void setIdColli(List<String> idColli) {
    this.idColli = idColli;
  }
  
  
  
  
  
  @Override
  public List<ABeanPersCRUD4Middleware> getListElementsFromSource(Connection con) throws SQLException {
    List<ABeanPersCRUD4Middleware> beans=new ArrayList();
    List<List> list=new ArrayList();
    
    String select=getSqlSelectSource();
    
    ResultSetHelper.fillListList(con, select, list);
    for(List l:list){
      Msg102Detail msgTmp=new Msg102Detail(this.dbSource);
      msgTmp.setSourceHstId(ClassMapper.classToClass(l.get(0), Long.class));
      msgTmp.setSourceDataElab(ClassMapper.classToClass(l.get(1), Date.class));
      msgTmp.setSourceTag(ClassMapper.classToString(l.get(2)));
      msgTmp.setActionType(ClassMapper.classToClass(l.get(3), Integer.class));
      msgTmp.setDataS(ClassMapper.classToString(l.get(4)));
      msgTmp.setOraS(ClassMapper.classToString(l.get(5)));
      String colloId=ClassMapper.classToString(l.get(6));
      
      int token=0; 
      int lunC=8;
      int i=0; //indice sulla lista
      while( token<colloId.length() && i<DIM_FIELDS_COLLI){
        String colloTmp=colloId.substring(token, token+lunC);
        msgTmp.getIdColli().set(i, colloTmp);
        token+=lunC;
        i++;
      }
      
      
      beans.add(msgTmp);
    }
    
    
    return beans;
  }

  
  private String getSqlSelectSource(){
    String campi=ListUtils.toCommaSeparatedString(getFieldsSource());
    String select ="Select "+campi+ 
                  "\n from "+getLibNameSource()+" . "+getTableNameSource()+
                  " where 1=1 "+
                  " and HVDL_RecordType="+JDBCDataMapper.objectToSQL(COD_MSG_102)+
                  " and "+FSOURCE__HST_TAG+" = '' ";//" <> "+JDBCDataMapper.objectToSQL(SOURCE_TAG_COMPLETE);
                  //" and "+FSOURCE__HST_DATAELABORAZIONE+" is null";
    return select;
  }
  
  @Override
  public List<String> getKeyFieldsSource() {
    return Arrays.asList(FSOURCE__HST_ID);
    
  }

  @Override
  public List<String> getKeyFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID);
  }

  @Override
  public Map<String, Object> getFieldValuesMapSourceForUpd() {
    Map m=new HashMap();
    m.put(FSOURCE__HST_DATAELABORAZIONE, new Date());
    m.put(FSOURCE__HST_TAG,SOURCE_TAG_COMPLETE);
    m.put(FSOURCE__HVDL_IDMESSAGGIO,this.getIdMsg());
    
    return m;
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
    
    m.put(FSOURCE__HST_ID,sourceHstId);
    m.put(FSOURCE__HST_DATAELABORAZIONE, new Date());
    m.put(FSOURCE__HST_TAG,SOURCE_TAG_COMPLETE);
    //---
    m.put(FSOURCE__HVDL_IDMESSAGGIO,this.getIdMsg());
    m.put(FSOURCE__HVDL_SYSTEMTIME,this.oraS);
    m.put(FSOURCE__HVDL_SYSTEMDATE,this.dataS); 
    m.put(FSOURCE__HVDL_TRANSACTIONTYPE,this.actionType);
    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    m.put(FDEST__DATAKEY,this.actionType);
    m.put(FDEST__SYSTEMDATE,this.dataS); 
    m.put(FDEST__SYSTEMTIME,this.oraS);
    for(int i=1;i<=idColli.size();i++){
      m.put(FDEST__IDCOLLO+"_"+i,idColli.get(i-1));
    }
    
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    List<String> l=new ArrayList();
    l.add(FDEST__MESSAGEID);
    l.add(FDEST__DATAKEY);
    l.add(FDEST__SYSTEMDATE);
    l.add(FDEST__SYSTEMTIME);
    
    for(int i=1;i<=idColli.size();i++){
      l.add(FDEST__IDCOLLO+"_"+i);
    } 
    
    return l;
  }

  @Override
  public List<String> getFieldsSource() {
    return Arrays.asList(FSOURCE__HST_ID,FSOURCE__HST_DATAELABORAZIONE,FSOURCE__HST_TAG,FSOURCE__HVDL_TRANSACTIONTYPE,FSOURCE__HVDL_SYSTEMDATE,FSOURCE__HVDL_SYSTEMTIME,
                         FSOURCE__HVDL_COLLOID,FSOURCE__HVDL_IDMESSAGGIO);
    
  }

  
  
  @Override
  public String getLibNameSource() {
    return DBINCAS_NAME;
  }
  
  
  @Override
  public String getTableNameSource() {
    return TABLE_SOURCE_INCAS;
  }
  

  @Override
  public String getLibNameDestination() {
    return "";
  }

  @Override
  public String getTableNameDestination() {
    return TABLE_DESTINATION_VDL;
  }
  

  @Override
  public List<Integer> getFieldTypesSource() {
    List l =new ArrayList();
    
    l.add(Types.INTEGER);    //id interno tabella
    l.add(Types.TIMESTAMP);  //dataElaborazione
    l.add(Types.CHAR);
    l.add(Types.CHAR);
    l.add(Types.VARCHAR);    //data Stringa
    l.add(Types.VARCHAR);    //ora Stringa
    l.add(Types.VARCHAR);    //ID Collo
    l.add(Types.NUMERIC);   //id Messaggio da As400
    
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypesDestination() {
    List l =new ArrayList();
    
    l.add(Types.NUMERIC);     //id Messaggio da As400
    l.add(Types.NUMERIC);    //actionType
    l.add(Types.VARCHAR);    //data Stringa
    l.add(Types.VARCHAR);    //ora Stringa
    
    // inserisco 256 elementi 
    for(int i=1;i<=idColli.size();i++){
      l.add(Types.VARCHAR);
    }  
    
    
    
    return l;
  }

  @Override
  public void loadInfoBeanFromSource(Connection con) throws SQLException {
    
  }

  
  
}
