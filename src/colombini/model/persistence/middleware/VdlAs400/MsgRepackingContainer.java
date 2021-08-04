/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.VdlAs400;

import db.JDBCDataMapper;
import db.ResultSetHelper;
import db.persistence.ABeanPersCRUD4Middleware;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hsqldb.types.Types;
import utils.ClassMapper;
import utils.ListUtils;

/**
 *
 * @author lvita
 */
public class MsgRepackingContainer extends  ABeanPersCRUD4Middleware{
  
  
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_RepackingContainerData";
  
  
  private static final String LIBRARY_DESTINATION_AS400 = "MCOBMODDTA";
  private static final String TABLE_DESTINATION_AS400 = "ZVAIIM";
  
  
  public final static String FSOURCE__MESSAGEID          ="MESSAGEID";
  public final static String FSOURCE__CONTNO             ="CONTNO";
  public final static String FSOURCE__CONTCODE           ="CONTCODE";
  public final static String FSOURCE__CONTNAME           ="CONTNAME";
  public final static String FSOURCE__TOTALWEIGHT        ="TOTALWEIGHT";
  public final static String FSOURCE__CONTLENGTH         ="CONTLENGTH";
  public final static String FSOURCE__CONTWIDTH          ="CONTWIDTH";
  
  public final static String FSOURCE__CONTHEIGHT          ="CONTHEIGHT";
  public final static String FSOURCE__COLLOID          ="COLLOID";
  public final static String FSOURCE__ORDERNO          ="ORDERNO";
  public final static String FSOURCE__COMPANY          ="COMPANY";
  public final static String FSOURCE__TRUCKLOADNUMBER  ="TRUCKLOADNUMBER";
  public final static String FSOURCE__LASTRECORD      ="LASTRECORD";
  
  
  
  public final static String FDEST__MESSAGEID          ="ZIIDMS";
  public final static String FDEST__CONTNO             ="ZIPAII";
  public final static String FDEST__CONTCODE           ="ZIPACT";
  public final static String FDEST__CONTNAME           ="ZIPANM";
  public final static String FDEST__TOTALWEIGHT        ="ZIGRW4";
  public final static String FDEST__CONTLENGTH         ="ZIPACL";
  public final static String FDEST__CONTWIDTH          ="ZIPACW";
  public final static String FDEST__CONTHEIGHT         ="ZIPACH";
  public final static String FDEST__COLLOID            ="ZIDLMO";
  public final static String FDEST__ORDERNO            ="ZIORNO";
  public final static String FDEST__COMPANY            ="ZICONO";
  public final static String FDEST__TRUCKLOADNUMBER    ="ZICONN";
  public final static String FDEST__LASTRECORD         ="ZIULRE";
  
  
  private String containerNo;
  private String containerCode;
  private String containerName;
  private Long totalWeight;
  private Integer length;
  private Integer width;
  private Integer height;
  private String colloId; //private Integer colloId;
  private Integer orderNo;
  private Integer company;
  private Integer truckLoadNumber;
  private String lastRecord;

  
  public MsgRepackingContainer(){
    super();
  }
  
   
  
  public String getContainerNo() {
    return containerNo;
  }

  public void setContainerNo(String containerNo) {
    this.containerNo = containerNo;
  }

  public String getContainerCode() {
    return containerCode;
  }

  public void setContainerCode(String containerCode) {
    this.containerCode = containerCode;
  }

  public String getContainerName() {
    return containerName;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

  public Long getTotalWeight() {
    return totalWeight;
  }

  public void setTotalWeight(Long totalWeight) {
    this.totalWeight = totalWeight;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

//  public Integer getColloId() {
//    return colloId;
//  }
//
//  public void setColloId(Integer colloId) {
//    this.colloId = colloId;
//  }

  public String getColloId() {
    return colloId;
  }

  public void setColloId(String colloId) {
    this.colloId = colloId;
  }
  
  
  
  public Integer getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(Integer orderNo) {
    this.orderNo = orderNo;
  }

  public Integer getCompany() {
    return company;
  }

  public void setCompany(Integer company) {
    this.company = company;
  }
  
  
  
  public Integer getTruckLoadNumber() {
    return truckLoadNumber;
  }

  public void setTruckLoadNumber(Integer truckLoadNumber) {
    this.truckLoadNumber = truckLoadNumber;
  }

  public String getLastRecord() {
    return lastRecord;
  }

  public void setLastRecord(String lastRecord) {
    this.lastRecord = lastRecord;
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
      this.containerNo=ClassMapper.classToString(obj[1]);
      this.containerCode=ClassMapper.classToString(obj[2]);
      this.containerName=ClassMapper.classToString(obj[3]);
      
      this.totalWeight=ClassMapper.classToClass(obj[4], Long.class);
      this.length=ClassMapper.classToClass(obj[5], Integer.class);
      this.width=ClassMapper.classToClass(obj[6], Integer.class);
      this.height=ClassMapper.classToClass(obj[7], Integer.class);
      this.colloId=ClassMapper.classToString(obj[8]); //this.colloId=ClassMapper.classToClass(obj[8], Integer.class);
      this.orderNo=ClassMapper.classToClass(obj[9], Integer.class);
      this.company=ClassMapper.classToClass(obj[10], Integer.class);
      this.truckLoadNumber=ClassMapper.classToClass(obj[11], Integer.class);
      this.lastRecord=ClassMapper.classToString(obj[12]);
    }
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
    Map map=new HashMap();
    map.put(FSOURCE__MESSAGEID  ,this.getIdMsg());    
    map.put(FSOURCE__CONTNO     ,this.containerNo);    
    map.put(FSOURCE__CONTCODE   ,this.containerCode);    
    map.put(FSOURCE__CONTNAME   ,this.containerName);    
    map.put(FSOURCE__TOTALWEIGHT,this.totalWeight);    
    map.put(FSOURCE__CONTLENGTH ,this.length);    
    map.put(FSOURCE__CONTWIDTH  ,this.width);      
    map.put(FSOURCE__CONTHEIGHT ,this.height);    
    map.put(FSOURCE__COLLOID    ,this.colloId);    
    map.put(FSOURCE__ORDERNO    ,this.orderNo);    
    map.put(FSOURCE__COMPANY    ,this.company);    
    map.put(FSOURCE__TRUCKLOADNUMBER ,this.truckLoadNumber);
    map.put(FSOURCE__LASTRECORD    ,this.lastRecord); 
    
    return map;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map map=new HashMap();
    map.put(FDEST__MESSAGEID  ,this.getIdMsg());    
    map.put(FDEST__CONTNO     ,this.containerNo);    
    map.put(FDEST__CONTCODE   ,this.containerCode);    
    map.put(FDEST__CONTNAME   ,this.containerName);    
    map.put(FDEST__TOTALWEIGHT,this.totalWeight !=null ? this.totalWeight : Long.valueOf(0));    
    map.put(FDEST__CONTLENGTH ,this.length !=null ? this.length : Integer.valueOf(0) );    
    map.put(FDEST__CONTWIDTH  ,this.width  !=null ? this.width : Integer.valueOf(0) );      
    map.put(FDEST__CONTHEIGHT ,this.height !=null ? this.height : Integer.valueOf(0) );    
    map.put(FDEST__COLLOID    ,this.colloId !=null ? this.colloId : "");    
    map.put(FDEST__ORDERNO    ,this.orderNo !=null ? this.orderNo : Long.valueOf(0) );    
    map.put(FDEST__COMPANY    ,this.company !=null ? this.company : Integer.valueOf(0) );    
    map.put(FDEST__TRUCKLOADNUMBER ,this.truckLoadNumber!=null ? this.truckLoadNumber : Integer.valueOf(0) );
    map.put(FDEST__LASTRECORD    ,this.lastRecord); 
    
    return map;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID,FDEST__CONTNO,FDEST__CONTCODE,FDEST__CONTNAME,FDEST__TOTALWEIGHT,FDEST__CONTLENGTH,FDEST__CONTWIDTH,FDEST__CONTHEIGHT,FDEST__COLLOID,FDEST__ORDERNO,FDEST__COMPANY,FDEST__TRUCKLOADNUMBER,FDEST__LASTRECORD );
  }

  @Override
  public List<String> getFieldsSource() {
    return Arrays.asList(FSOURCE__MESSAGEID,FSOURCE__CONTNO,FSOURCE__CONTCODE,FSOURCE__CONTNAME,FSOURCE__TOTALWEIGHT,FSOURCE__CONTLENGTH,FSOURCE__CONTWIDTH,FSOURCE__CONTHEIGHT,FSOURCE__COLLOID,FSOURCE__ORDERNO,FSOURCE__COMPANY,FSOURCE__TRUCKLOADNUMBER,FSOURCE__LASTRECORD );
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
    return LIBRARY_DESTINATION_AS400;
  }

  @Override
  public String getTableNameDestination() {
    return TABLE_DESTINATION_AS400;
  }

  @Override
  public List<Integer> getFieldTypesSource() {
    List types=new ArrayList();
    types.add(Types.NUMERIC);
    types.add(Types.VARCHAR);
    types.add(Types.VARCHAR);
    types.add(Types.VARCHAR);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.VARCHAR);
    
    return types;
  }

  @Override
  public List<Integer> getFieldTypesDestination() {
    List types=new ArrayList();
    
    types.add(java.sql.Types.NUMERIC);
    types.add(java.sql.Types.CHAR);
    types.add(java.sql.Types.CHAR);
    types.add(java.sql.Types.CHAR);
    
    types.add(java.sql.Types.NUMERIC);
    types.add(java.sql.Types.INTEGER);
    types.add(java.sql.Types.INTEGER);
    types.add(java.sql.Types.INTEGER);
    types.add(java.sql.Types.CHAR);
    types.add(java.sql.Types.NUMERIC);
    types.add(java.sql.Types.INTEGER);
    types.add(java.sql.Types.INTEGER);
    types.add(java.sql.Types.CHAR);
    
    return types;
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
  public String toString() {
    return "ID: "+this.getIdMsg()+ " - CONTAINERNO :"+this.getContainerNo()+" - CONTAINERCODE :"+this.getContainerCode()+
            " - CONTAINERNAME :"+this.getContainerName();
  }

   
   
}
