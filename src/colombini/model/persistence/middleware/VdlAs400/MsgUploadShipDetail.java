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
 * @author lvita
 */
public class MsgUploadShipDetail extends  ABeanPersCRUD4Middleware{
  
  
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_UPLOADSHIPPINGDATA";
  
  
  private static final String LIBRARY_DESTINATION_AS400 = "MCOBMODDTA";
  private static final String TABLE_DESTINATION_AS400 = "ZVAISP";
  
  
  public final static String FSOURCE__MESSAGEID         ="MESSAGEID";
  public final static String FSOURCE__IDCOLLO           ="IDCOLLO";
  public final static String FSOURCE__TRUCKLOADNUMBER   ="TRUCKLOADNUMBER";
  public final static String FSOURCE__TRUCKLOADDATE     ="TRUCKLOADDATE";
  public final static String FSOURCE__RETURNCODE        ="RETURNCODE";
  public final static String FSOURCE__RETURNINFO        ="RETURNINFO";
  public final static String FSOURCE__SHIPPINGTS        ="SHIPPINGTS";
  
  
  public final static String FDEST__MESSAGEID         ="ZSIDMS";
  public final static String FDEST__IDCOLLO           ="ZSIDCL";
  public final static String FDEST__TRUCKLOADNUMBER   ="ZSCONN";
  public final static String FDEST__TRUCKLOADDATE     ="ZSDTCO";
  public final static String FDEST__RETURNCODE        ="ZSRTCO";
  public final static String FDEST__RETURNINFO        ="ZSRTIN";
  public final static String FDEST__SHIPPINGTS        ="ZSDTIN";
  
  
  
  private Long idCollo;
  private Long nSpedizione;
  private Date dataSpedizione;
  private Long returnCode;
  private String returnInfo;
  private Date dataInserimento;
  
  
  public MsgUploadShipDetail (){
    super();
  }
  
  public MsgUploadShipDetail (Long idMs){
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
    m.put(FSOURCE__TRUCKLOADNUMBER,this.nSpedizione);
    m.put(FSOURCE__TRUCKLOADDATE,this.dataSpedizione);
    m.put(FSOURCE__RETURNCODE,this.returnCode);
    m.put(FSOURCE__RETURNINFO,this.returnInfo);
    m.put(FSOURCE__SHIPPINGTS,this.dataInserimento);
    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    m.put(FDEST__IDCOLLO,this.idCollo);
    m.put(FDEST__TRUCKLOADNUMBER,this.nSpedizione);
    m.put(FDEST__TRUCKLOADDATE,this.dataSpedizione);
    m.put(FDEST__RETURNCODE,this.returnCode);
    m.put(FDEST__RETURNINFO,this.returnInfo);
    m.put(FDEST__SHIPPINGTS,this.dataInserimento);
    
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID,FDEST__IDCOLLO,FDEST__TRUCKLOADNUMBER,FDEST__TRUCKLOADDATE,FDEST__RETURNCODE,FDEST__RETURNINFO,FDEST__SHIPPINGTS);
  }

  @Override
  public List<String> getFieldsSource() {
return Arrays.asList(FSOURCE__MESSAGEID,FSOURCE__IDCOLLO,FSOURCE__TRUCKLOADNUMBER,FSOURCE__TRUCKLOADDATE,FSOURCE__RETURNCODE,FSOURCE__RETURNINFO,FSOURCE__SHIPPINGTS);
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
