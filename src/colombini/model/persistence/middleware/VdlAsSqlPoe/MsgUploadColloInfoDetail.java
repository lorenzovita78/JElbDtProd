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
  

 
    
  // <editor-fold defaultstate="collapsed" desc="Variables">
    
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_COLLOINFO";
  
  
  private static final String LIBRARY_DESTINATION_SQLPOE = "AvanzamentoVDL";
  private static final String TABLE_DESTINATION_SQLPOE = "V2H_ColloInfo_Detail";
  
  
  public final static String FSOURCE__MESSAGEID               ="MESSAGEID";
  public final static String FSOURCE__SYSTEMTIME              ="SYSTEMTIME";
  public final static String FSOURCE__IDCOLLO                 ="COLLOID";
  public final static String FSOURCE__COLLODESC               ="COLLODESC";
  public final static String FSOURCE__STATUS                  ="SATUS";
  public final static String FSOURCE__TIMESTAMP               ="TIMESTAMP";
  public final static String FSOURCE__STORAGETIMESTAMP        ="STORAGETIMESTAMP";
  public final static String FSOURCE__MFSYSTEM                ="MFSYSTEM";
  public final static String FSOURCE__CONTAINERID             ="CONTAINERID";
  public final static String FSOURCE__BUNDLELD                ="BUNDLELD";
  public final static String FSOURCE__BOXID                   ="BOXID";
  public final static String FSOURCE__LAYERID                 ="LAYERID";
  public final static String FSOURCE__PLATEID                 ="PLATEID";
  public final static String FSOURCE__WEIGHT                  ="WEIGHT";
  public final static String FSOURCE__HEIGHT                  ="HEIGHT";
  public final static String FSOURCE__BOXCREATOR              ="BOXCREATOR";
  public final static String FSOURCE__COMMISSIONID            ="COMMISSIONID";  
  public final static String FSOURCE__INTERNALSEQ             ="INTERNALSEQ";
  public final static String FSOURCE__PRODUCTIONLINE          ="PRODUCTIONLINE";
  public final static String FSOURCE__WAREHOUSE               ="WAREHOUSE";
  public final static String FSOURCE__AREA                    ="AREA";
  public final static String FSOURCE__AISLE                   ="AISLE";  
  public final static String FSOURCE__SIDE                    ="SIDE";
  public final static String FSOURCE__X                       ="X";
  public final static String FSOURCE__Y                       ="Y";
  public final static String FSOURCE__Z                       ="Z";
  public final static String FSOURCE__RETURNCODE              ="RETURNCODE";
  public final static String FSOURCE__RETURNINFO              ="RETURNINFO";
  public final static String FSOURCE__DELIVERYID              ="DELIVERYID";
  public final static String FSOURCE__CLIENTID                ="CLIENTID";
  public final static String FSOURCE__CUSTOMERNO              ="CUSTOMERNO";
  public final static String FSOURCE__CUSTOMERNAME            ="CUSTOMERNAME";
  public final static String FSOURCE__CUSTOMERORDERNO         ="CUSTOMERORDERNO";
  public final static String FSOURCE__SEQUENCENO              ="SEQUENCENO";
  public final static String FSOURCE__BATCHID                 ="BATCHID";
  public final static String FSOURCE__TRUCKLOADNUMBER         ="TRUCKLOADNUMBER";
  public final static String FSOURCE__TRUCKLOADDATE           ="TRUCKLOADDATE";
  public final static String FSOURCE__TRUCKERNAME             ="TRUCKERNAME";
  public final static String FSOURCE__REPACK                  ="REPACK";
  public final static String FSOURCE__SHIPPINGDATESTARTTS     ="SHIPPINGDATESTARTTS";
  public final static String FSOURCE__SHIPPINGTIMESTARTTS     ="SHIPPINGTIMESTARTTS";
  public final static String FSOURCE__SHIPPINGDATEENDSTS      ="SHIPPINGDATEENDSTS";
  public final static String FSOURCE__SHIPPINGTIMEENDSTS      ="SHIPPINGTIMEENDSTS";
  public final static String FSOURCE__SHIPPINGAISLE           ="SHIPPINGAISLE";
  public final static String FSOURCE__DELIVERYZONE            ="DELIVERYZONE";
  
  
  public final static String FDEST__MESSAGEID               ="MESSAGEID";
  public final static String FDEST__SYSTEMTIME              ="SYSTEMTIME";
  public final static String FDEST__IDCOLLO                 ="COLLOID";
  public final static String FDEST__COLLODESC               ="COLLODESC";
  public final static String FDEST__STATUS                  ="SATUS";
  public final static String FDEST__TIMESTAMP               ="TIMESTAMP";
  public final static String FDEST__STORAGETIMESTAMP        ="STORAGETIMESTAMP";
  public final static String FDEST__MFSYSTEM                ="MFSYSTEM";
  public final static String FDEST__CONTAINERID             ="CONTAINERID";
  public final static String FDEST__BUNDLELD                ="BUNDLELD";
  public final static String FDEST__BOXID                   ="BOXID";
  public final static String FDEST__LAYERID                 ="LAYERID";
  public final static String FDEST__PLATEID                 ="PLATEID";
  public final static String FDEST__WEIGHT                  ="WEIGHT";
  public final static String FDEST__HEIGHT                  ="HEIGHT";
  public final static String FDEST__BOXCREATOR              ="BOXCREATOR";
  public final static String FDEST__COMMISSIONID            ="COMMISSIONID";  
  public final static String FDEST__INTERNALSEQ             ="INTERNALSEQ";
  public final static String FDEST__PRODUCTIONLINE          ="PRODUCTIONLINE";
  public final static String FDEST__WAREHOUSE               ="WAREHOUSE";
  public final static String FDEST__AREA                    ="AREA";
  public final static String FDEST__AISLE                   ="AISLE";  
  public final static String FDEST__SIDE                    ="SIDE";
  public final static String FDEST__X                       ="X";
  public final static String FDEST__Y                       ="Y";
  public final static String FDEST__Z                       ="Z";
  public final static String FDEST__RETURNCODE              ="RETURNCODE";
  public final static String FDEST__RETURNINFO              ="RETURNINFO";
  public final static String FDEST__DELIVERYID              ="DELIVERYID";
  public final static String FDEST__CLIENTID                ="CLIENTID";
  public final static String FDEST__CUSTOMERNO              ="CUSTOMERNO";
  public final static String FDEST__CUSTOMERNAME            ="CUSTOMERNAME";
  public final static String FDEST__CUSTOMERORDERNO         ="CUSTOMERORDERNO";
  public final static String FDEST__SEQUENCENO              ="SEQUENCENO";
  public final static String FDEST__BATCHID                 ="BATCHID";
  public final static String FDEST__TRUCKLOADNUMBER         ="TRUCKLOADNUMBER";
  public final static String FDEST__TRUCKLOADDATE           ="TRUCKLOADDATE";
  public final static String FDEST__TRUCKERNAME             ="TRUCKERNAME";
  public final static String FDEST__REPACK                  ="REPACK";
  public final static String FDEST__SHIPPINGDATESTARTTS     ="SHIPPINGDATESTARTTS";
  public final static String FDEST__SHIPPINGTIMESTARTTS     ="SHIPPINGTIMESTARTTS";
  public final static String FDEST__SHIPPINGDATEENDSTS      ="SHIPPINGDATEENDSTS";
  public final static String FDEST__SHIPPINGTIMEENDSTS      ="SHIPPINGTIMEENDSTS";
  public final static String FDEST__SHIPPINGAISLE           ="SHIPPINGAISLE";
  public final static String FDEST__DELIVERYZONE            ="DELIVERYZONE";
    
    
    
  private Date SYSTEMTIME;             
  private Long IDCOLLO;
  private String COLLODESC;              
  private Long STATUS;                 
  private Date TIMESTAMP;              
  private Date STORAGETIMESTAMP;       
  private String MFSYSTEM;               
  private String CONTAINERID;            
  private String BUNDLELD;               
  private Long BOXID;                  
  private Long LAYERID;                
  private Long PLATEID;                
  private Long WEIGHT;                 
  private Long HEIGHT;                 
  private String BOXCREATOR;             
  private Long COMMISSIONID;           
  private Long INTERNALSEQ;            
  private String PRODUCTIONLINE;         
  private Long WAREHOUSE;              
  private Long AREA;                   
  private Long AISLE;                  
  private Long SIDE;                   
  private Long X;                      
  private Long Y;                      
  private Long Z;                      
  private Long RETURNCODE;             
  private String RETURNINFO;             
  private Long DELIVERYID;             
  private String CLIENTID;               
  private String CUSTOMERNO;             
  private String CUSTOMERNAME;           
  private String CUSTOMERORDERNO;        
  private Long SEQUENCENO;             
  private Long BATCHID;                
  private Long TRUCKLOADNUMBER;        
  private Date TRUCKLOADDATE;          
  private String TRUCKERNAME;            
  private String REPACK;                 
  private String SHIPPINGDATESTARTTS;    
  private String SHIPPINGTIMESTARTTS;    
  private String SHIPPINGDATEENDSTS;     
  private String SHIPPINGTIMEENDSTS;     
  private Long SHIPPINGAISLE;          
  private String DELIVERYZONE; 
  


    public Date getSYSTEMTIME() {
        return SYSTEMTIME;
    }

    public Long getIDCOLLO() {
        return IDCOLLO;
    }

    public String getCOLLODESC() {
        return COLLODESC;
    }

    public Long getSTATUS() {
        return STATUS;
    }

    public Date getTIMESTAMP() {
        return TIMESTAMP;
    }

    public Date getSTORAGETIMESTAMP() {
        return STORAGETIMESTAMP;
    }

    public String getMFSYSTEM() {
        return MFSYSTEM;
    }

    public String getCONTAINERID() {
        return CONTAINERID;
    }

    public String getBUNDLELD() {
        return BUNDLELD;
    }

    public Long getBOXID() {
        return BOXID;
    }

    public Long getLAYERID() {
        return LAYERID;
    }

    public Long getPLATEID() {
        return PLATEID;
    }

    public Long getWEIGHT() {
        return WEIGHT;
    }

    public Long getHEIGHT() {
        return HEIGHT;
    }

    public String getBOXCREATOR() {
        return BOXCREATOR;
    }

    public Long getCOMMISSIONID() {
        return COMMISSIONID;
    }

    public Long getINTERNALSEQ() {
        return INTERNALSEQ;
    }

    public String getPRODUCTIONLINE() {
        return PRODUCTIONLINE;
    }

    public Long getWAREHOUSE() {
        return WAREHOUSE;
    }

    public Long getAREA() {
        return AREA;
    }

    public Long getAISLE() {
        return AISLE;
    }

    public Long getSIDE() {
        return SIDE;
    }

    public Long getX() {
        return X;
    }

    public Long getY() {
        return Y;
    }

    public Long getZ() {
        return Z;
    }

    public Long getRETURNCODE() {
        return RETURNCODE;
    }

    public String getRETURNINFO() {
        return RETURNINFO;
    }

    public Long getDELIVERYID() {
        return DELIVERYID;
    }

    public String getCLIENTID() {
        return CLIENTID;
    }

    public String getCUSTOMERNO() {
        return CUSTOMERNO;
    }

    public String getCUSTOMERNAME() {
        return CUSTOMERNAME;
    }

    public String getCUSTOMERORDERNO() {
        return CUSTOMERORDERNO;
    }

    public Long getSEQUENCENO() {
        return SEQUENCENO;
    }

    public Long getBATCHID() {
        return BATCHID;
    }

    public Long getTRUCKLOADNUMBER() {
        return TRUCKLOADNUMBER;
    }

    public Date getTRUCKLOADDATE() {
        return TRUCKLOADDATE;
    }

    public String getTRUCKERNAME() {
        return TRUCKERNAME;
    }

    public String getREPACK() {
        return REPACK;
    }

    public String getSHIPPINGDATESTARTTS() {
        return SHIPPINGDATESTARTTS;
    }

    public String getSHIPPINGTIMESTARTTS() {
        return SHIPPINGTIMESTARTTS;
    }

    public String getSHIPPINGDATEENDSTS() {
        return SHIPPINGDATEENDSTS;
    }

    public String getSHIPPINGTIMEENDSTS() {
        return SHIPPINGTIMEENDSTS;
    }

    public Long getSHIPPINGAISLE() {
        return SHIPPINGAISLE;
    }

    public String getDELIVERYZONE() {
        return DELIVERYZONE;
    }
  
    public void setSYSTEMTIME(Date SYSTEMTIME) {
        this.SYSTEMTIME = SYSTEMTIME;
    }

    public void setIDCOLLO(Long IDCOLLO) {
        this.IDCOLLO = IDCOLLO;
    }

    public void setCOLLODESC(String COLLODESC) {
        this.COLLODESC = COLLODESC;
    }

    public void setSTATUS(Long STATUS) {
        this.STATUS = STATUS;
    }

    public void setTIMESTAMP(Date TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public void setSTORAGETIMESTAMP(Date STORAGETIMESTAMP) {
        this.STORAGETIMESTAMP = STORAGETIMESTAMP;
    }

    public void setMFSYSTEM(String MFSYSTEM) {
        this.MFSYSTEM = MFSYSTEM;
    }

    public void setCONTAINERID(String CONTAINERID) {
        this.CONTAINERID = CONTAINERID;
    }

    public void setBUNDLELD(String BUNDLELD) {
        this.BUNDLELD = BUNDLELD;
    }

    public void setBOXID(Long BOXID) {
        this.BOXID = BOXID;
    }

    public void setLAYERID(Long LAYERID) {
        this.LAYERID = LAYERID;
    }

    public void setPLATEID(Long PLATEID) {
        this.PLATEID = PLATEID;
    }

    public void setWEIGHT(Long WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public void setHEIGHT(Long HEIGHT) {
        this.HEIGHT = HEIGHT;
    }

    public void setBOXCREATOR(String BOXCREATOR) {
        this.BOXCREATOR = BOXCREATOR;
    }

    public void setCOMMISSIONID(Long COMMISSIONID) {
        this.COMMISSIONID = COMMISSIONID;
    }

    public void setINTERNALSEQ(Long INTERNALSEQ) {
        this.INTERNALSEQ = INTERNALSEQ;
    }

    public void setPRODUCTIONLINE(String PRODUCTIONLINE) {
        this.PRODUCTIONLINE = PRODUCTIONLINE;
    }

    public void setWAREHOUSE(Long WAREHOUSE) {
        this.WAREHOUSE = WAREHOUSE;
    }

    public void setAREA(Long AREA) {
        this.AREA = AREA;
    }

    public void setAISLE(Long AISLE) {
        this.AISLE = AISLE;
    }

    public void setSIDE(Long SIDE) {
        this.SIDE = SIDE;
    }

    public void setX(Long X) {
        this.X = X;
    }

    public void setY(Long Y) {
        this.Y = Y;
    }

    public void setZ(Long Z) {
        this.Z = Z;
    }

    public void setRETURNCODE(Long RETURNCODE) {
        this.RETURNCODE = RETURNCODE;
    }

    public void setRETURNINFO(String RETURNINFO) {
        this.RETURNINFO = RETURNINFO;
    }

    public void setDELIVERYID(Long DELIVERYID) {
        this.DELIVERYID = DELIVERYID;
    }

    public void setCLIENTID(String CLIENTID) {
        this.CLIENTID = CLIENTID;
    }

    public void setCUSTOMERNO(String CUSTOMERNO) {
        this.CUSTOMERNO = CUSTOMERNO;
    }

    public void setCUSTOMERNAME(String CUSTOMERNAME) {
        this.CUSTOMERNAME = CUSTOMERNAME;
    }

    public void setCUSTOMERORDERNO(String CUSTOMERORDERNO) {
        this.CUSTOMERORDERNO = CUSTOMERORDERNO;
    }

    public void setSEQUENCENO(Long SEQUENCENO) {
        this.SEQUENCENO = SEQUENCENO;
    }

    public void setBATCHID(Long BATCHID) {
        this.BATCHID = BATCHID;
    }

    public void setTRUCKLOADNUMBER(Long TRUCKLOADNUMBER) {
        this.TRUCKLOADNUMBER = TRUCKLOADNUMBER;
    }

    public void setTRUCKLOADDATE(Date TRUCKLOADDATE) {
        this.TRUCKLOADDATE = TRUCKLOADDATE;
    }

    public void setTRUCKERNAME(String TRUCKERNAME) {
        this.TRUCKERNAME = TRUCKERNAME;
    }

    public void setREPACK(String REPACK) {
        this.REPACK = REPACK;
    }

    public void setSHIPPINGDATESTARTTS(String SHIPPINGDATESTARTTS) {
        this.SHIPPINGDATESTARTTS = SHIPPINGDATESTARTTS;
    }

    public void setSHIPPINGTIMESTARTTS(String SHIPPINGTIMESTARTTS) {
        this.SHIPPINGTIMESTARTTS = SHIPPINGTIMESTARTTS;
    }

    public void setSHIPPINGDATEENDSTS(String SHIPPINGDATEENDSTS) {
        this.SHIPPINGDATEENDSTS = SHIPPINGDATEENDSTS;
    }

    public void setSHIPPINGTIMEENDSTS(String SHIPPINGTIMEENDSTS) {
        this.SHIPPINGTIMEENDSTS = SHIPPINGTIMEENDSTS;
    }

    public void setSHIPPINGAISLE(Long SHIPPINGAISLE) {
        this.SHIPPINGAISLE = SHIPPINGAISLE;
    }

    public void setDELIVERYZONE(String DELIVERYZONE) {
        this.DELIVERYZONE = DELIVERYZONE;
    }
    
    // </editor-fold>
    
  
  
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
      this.setSYSTEMTIME(ClassMapper.classToClass(obj[1],Date.class));
      this.setIDCOLLO(ClassMapper.classToClass(obj[2],Long.class));
      this.setCOLLODESC(ClassMapper.classToClass(obj[3],String.class));
      this.setSTATUS(ClassMapper.classToClass(obj[4],Long.class));
      this.setTIMESTAMP(ClassMapper.classToClass(obj[5],Date.class));
      this.setSTORAGETIMESTAMP(ClassMapper.classToClass(obj[6],Date.class));
      this.setMFSYSTEM(ClassMapper.classToClass(obj[7],String.class));
      this.setCONTAINERID(ClassMapper.classToClass(obj[8],String.class));      
      this.setBUNDLELD(ClassMapper.classToClass(obj[9],String.class));
      this.setBOXID(ClassMapper.classToClass(obj[10],Long.class));
      this.setLAYERID(ClassMapper.classToClass(obj[11],Long.class));
      this.setPLATEID(ClassMapper.classToClass(obj[12],Long.class));
      this.setWEIGHT(ClassMapper.classToClass(obj[13],Long.class));
      this.setHEIGHT(ClassMapper.classToClass(obj[14],Long.class));
      this.setBOXCREATOR(ClassMapper.classToClass(obj[15],String.class));
      this.setCOMMISSIONID(ClassMapper.classToClass(obj[16],Long.class));
      this.setINTERNALSEQ(ClassMapper.classToClass(obj[17],Long.class));
      this.setPRODUCTIONLINE(ClassMapper.classToClass(obj[18],String.class));
      this.setWAREHOUSE(ClassMapper.classToClass(obj[19],Long.class));
      this.setAREA(ClassMapper.classToClass(obj[20],Long.class));
      this.setAISLE(ClassMapper.classToClass(obj[21],Long.class));
      this.setSIDE(ClassMapper.classToClass(obj[22],Long.class));
      this.setX(ClassMapper.classToClass(obj[23],Long.class));
      this.setY(ClassMapper.classToClass(obj[24],Long.class));
      this.setZ(ClassMapper.classToClass(obj[25],Long.class));
      this.setRETURNCODE(ClassMapper.classToClass(obj[26],Long.class));
      this.setRETURNINFO(ClassMapper.classToClass(obj[27],String.class));
      this.setDELIVERYID(ClassMapper.classToClass(obj[28],Long.class));
      this.setCLIENTID(ClassMapper.classToClass(obj[27],String.class));
      this.setCUSTOMERNO(ClassMapper.classToClass(obj[28],String.class));
      this.setCUSTOMERNAME(ClassMapper.classToClass(obj[29],String.class));
      this.setCUSTOMERORDERNO(ClassMapper.classToClass(obj[30],String.class));
      this.setSEQUENCENO(ClassMapper.classToClass(obj[31],Long.class));
      this.setBATCHID(ClassMapper.classToClass(obj[32],Long.class));
      this.setTRUCKLOADNUMBER(ClassMapper.classToClass(obj[33],Long.class));
      this.setTRUCKLOADDATE(ClassMapper.classToClass(obj[34],Date.class));
      this.setTRUCKERNAME(ClassMapper.classToClass(obj[35],String.class));
      this.setREPACK(ClassMapper.classToClass(obj[36],String.class));
      this.setSHIPPINGDATESTARTTS(ClassMapper.classToClass(obj[37],String.class));
      this.setSHIPPINGTIMESTARTTS(ClassMapper.classToClass(obj[38],String.class));
      this.setSHIPPINGDATEENDSTS(ClassMapper.classToClass(obj[39],String.class));
      this.setSHIPPINGTIMEENDSTS(ClassMapper.classToClass(obj[40],String.class));
      this.setSHIPPINGAISLE(ClassMapper.classToClass(obj[41],Long.class));
      this.setDELIVERYZONE(ClassMapper.classToClass(obj[42],String.class));
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
    m.put(FSOURCE__SYSTEMTIME,this.getSYSTEMTIME());
    m.put(FSOURCE__IDCOLLO,this.IDCOLLO);
    m.put(FSOURCE__COLLODESC, this.COLLODESC);
    m.put(FSOURCE__STATUS, this.STATUS);
    m.put(FSOURCE__TIMESTAMP, this.TIMESTAMP);
    m.put(FSOURCE__STORAGETIMESTAMP, this.STORAGETIMESTAMP);
    m.put(FSOURCE__MFSYSTEM, this.MFSYSTEM);
    m.put(FSOURCE__CONTAINERID, this.CONTAINERID);  
    m.put(FSOURCE__BUNDLELD, this.BUNDLELD);
    m.put(FSOURCE__BOXID, this.BOXID);
    m.put(FSOURCE__LAYERID, this.LAYERID);
    m.put(FSOURCE__PLATEID, this.PLATEID);
    m.put(FSOURCE__WEIGHT, this.WEIGHT);
    m.put(FSOURCE__HEIGHT, this.HEIGHT);
    m.put(FSOURCE__BOXCREATOR, this.BOXCREATOR);
    m.put(FSOURCE__COMMISSIONID, this.COMMISSIONID);
    m.put(FSOURCE__INTERNALSEQ, this.INTERNALSEQ);
    m.put(FSOURCE__PRODUCTIONLINE, this.PRODUCTIONLINE);
    m.put(FSOURCE__WAREHOUSE, this.WAREHOUSE);
    m.put(FSOURCE__AREA, this.AREA);
    m.put(FSOURCE__AISLE, this.AISLE);
    m.put(FSOURCE__SIDE, this.SIDE);
    m.put(FSOURCE__X, this.X);
    m.put(FSOURCE__Y, this.Y);
    m.put(FSOURCE__Z, this.Z);
    m.put(FSOURCE__RETURNCODE, this.RETURNCODE);
    m.put(FSOURCE__RETURNINFO, this.RETURNINFO);
    m.put(FSOURCE__DELIVERYID, this.DELIVERYID);
    m.put(FSOURCE__CLIENTID, this.CLIENTID);
    m.put(FSOURCE__CUSTOMERNO, this.CUSTOMERNO);
    m.put(FSOURCE__CUSTOMERNAME, this.CUSTOMERNAME);
    m.put(FSOURCE__CUSTOMERORDERNO, this.CUSTOMERORDERNO);
    m.put(FSOURCE__SEQUENCENO, this.SEQUENCENO);
    m.put(FSOURCE__BATCHID, this.BATCHID);
    m.put(FSOURCE__TRUCKLOADNUMBER, this.TRUCKLOADNUMBER);
    m.put(FSOURCE__TRUCKLOADDATE, this.TRUCKLOADDATE);
    m.put(FSOURCE__TRUCKERNAME, this.TRUCKERNAME);
    m.put(FSOURCE__REPACK, this.REPACK);
    m.put(FSOURCE__SHIPPINGDATESTARTTS, this.SHIPPINGDATESTARTTS);
    m.put(FSOURCE__SHIPPINGTIMESTARTTS, this.SHIPPINGTIMESTARTTS);
    m.put(FSOURCE__SHIPPINGDATEENDSTS, this.SHIPPINGDATEENDSTS);
    m.put(FSOURCE__SHIPPINGTIMEENDSTS, this.SHIPPINGTIMEENDSTS);
    m.put(FSOURCE__SHIPPINGAISLE, this.SHIPPINGAISLE);
    m.put(FSOURCE__DELIVERYZONE, this.DELIVERYZONE);


    
    return m;
  }

  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    m.put(FDEST__MESSAGEID,this.getIdMsg());
    m.put(FDEST__SYSTEMTIME,this.getSYSTEMTIME());
    m.put(FDEST__IDCOLLO,this.IDCOLLO);
    m.put(FDEST__COLLODESC, this.COLLODESC);
    m.put(FDEST__STATUS, this.STATUS);
    m.put(FDEST__TIMESTAMP, this.TIMESTAMP);
    m.put(FDEST__STORAGETIMESTAMP, this.STORAGETIMESTAMP);
    m.put(FDEST__MFSYSTEM, this.MFSYSTEM);
    m.put(FDEST__CONTAINERID, this.CONTAINERID);  
    m.put(FDEST__BUNDLELD, this.BUNDLELD);
    m.put(FDEST__BOXID, this.BOXID);
    m.put(FDEST__LAYERID, this.LAYERID);
    m.put(FDEST__PLATEID, this.PLATEID);
    m.put(FDEST__WEIGHT, this.WEIGHT);
    m.put(FDEST__HEIGHT, this.HEIGHT);
    m.put(FDEST__BOXCREATOR, this.BOXCREATOR);
    m.put(FDEST__COMMISSIONID, this.COMMISSIONID);
    m.put(FDEST__INTERNALSEQ, this.INTERNALSEQ);
    m.put(FDEST__PRODUCTIONLINE, this.PRODUCTIONLINE);
    m.put(FDEST__WAREHOUSE, this.WAREHOUSE);
    m.put(FDEST__AREA, this.AREA);
    m.put(FDEST__AISLE, this.AISLE);
    m.put(FDEST__SIDE, this.SIDE);
    m.put(FDEST__X, this.X);
    m.put(FDEST__Y, this.Y);
    m.put(FDEST__Z, this.Z);
    m.put(FDEST__RETURNCODE, this.RETURNCODE);
    m.put(FDEST__RETURNINFO, this.RETURNINFO);
    m.put(FDEST__DELIVERYID, this.DELIVERYID);
    m.put(FDEST__CLIENTID, this.CLIENTID);
    m.put(FDEST__CUSTOMERNO, this.CUSTOMERNO);
    m.put(FDEST__CUSTOMERNAME, this.CUSTOMERNAME);
    m.put(FDEST__CUSTOMERORDERNO, this.CUSTOMERORDERNO);
    m.put(FDEST__SEQUENCENO, this.SEQUENCENO);
    m.put(FDEST__BATCHID, this.BATCHID);
    m.put(FDEST__TRUCKLOADNUMBER, this.TRUCKLOADNUMBER);
    m.put(FDEST__TRUCKLOADDATE, this.TRUCKLOADDATE);
    m.put(FDEST__TRUCKERNAME, this.TRUCKERNAME);
    m.put(FDEST__REPACK, this.REPACK);
    m.put(FDEST__SHIPPINGDATESTARTTS, this.SHIPPINGDATESTARTTS);
    m.put(FDEST__SHIPPINGTIMESTARTTS, this.SHIPPINGTIMESTARTTS);
    m.put(FDEST__SHIPPINGDATEENDSTS, this.SHIPPINGDATEENDSTS);
    m.put(FDEST__SHIPPINGTIMEENDSTS, this.SHIPPINGTIMEENDSTS);
    m.put(FDEST__SHIPPINGAISLE, this.SHIPPINGAISLE);
    m.put(FDEST__DELIVERYZONE, this.DELIVERYZONE);

    
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList(FDEST__MESSAGEID,FDEST__SYSTEMTIME,FDEST__IDCOLLO,FDEST__COLLODESC,FDEST__STATUS,FDEST__TIMESTAMP,FDEST__STORAGETIMESTAMP,FDEST__MFSYSTEM,FDEST__CONTAINERID,FDEST__BUNDLELD,FDEST__BOXID,FDEST__LAYERID,FDEST__PLATEID,FDEST__WEIGHT,FDEST__HEIGHT,FDEST__BOXCREATOR,FDEST__COMMISSIONID,FDEST__INTERNALSEQ,FDEST__PRODUCTIONLINE,FDEST__WAREHOUSE,FDEST__AREA,FDEST__AISLE,FDEST__SIDE,FDEST__X,FDEST__Y,FDEST__Z,FDEST__RETURNCODE,FDEST__RETURNINFO,FDEST__DELIVERYID,FDEST__CLIENTID,FDEST__CUSTOMERNO,FDEST__CUSTOMERNAME,FDEST__CUSTOMERORDERNO,FDEST__SEQUENCENO,FDEST__BATCHID,FDEST__TRUCKLOADNUMBER,FDEST__TRUCKLOADDATE,FDEST__TRUCKERNAME,FDEST__REPACK,FDEST__SHIPPINGDATESTARTTS,FDEST__SHIPPINGTIMESTARTTS,FDEST__SHIPPINGDATEENDSTS,FDEST__SHIPPINGTIMEENDSTS,FDEST__SHIPPINGAISLE,FDEST__DELIVERYZONE);
  }

  @Override
  public List<String> getFieldsSource() {
    return Arrays.asList(FSOURCE__MESSAGEID,FSOURCE__SYSTEMTIME,FSOURCE__IDCOLLO,FSOURCE__COLLODESC,FSOURCE__STATUS,FSOURCE__TIMESTAMP,FSOURCE__STORAGETIMESTAMP,FSOURCE__MFSYSTEM,FSOURCE__CONTAINERID,FSOURCE__BUNDLELD,FSOURCE__BOXID,FSOURCE__LAYERID,FSOURCE__PLATEID,FSOURCE__WEIGHT,FSOURCE__HEIGHT,FSOURCE__BOXCREATOR,FSOURCE__COMMISSIONID,FSOURCE__INTERNALSEQ,FSOURCE__PRODUCTIONLINE,FSOURCE__WAREHOUSE,FSOURCE__AREA,FSOURCE__AISLE,FSOURCE__SIDE,FSOURCE__X,FSOURCE__Y,FSOURCE__Z,FSOURCE__RETURNCODE,FSOURCE__RETURNINFO,FSOURCE__DELIVERYID,FSOURCE__CLIENTID,FSOURCE__CUSTOMERNO,FSOURCE__CUSTOMERNAME,FSOURCE__CUSTOMERORDERNO,FSOURCE__SEQUENCENO,FSOURCE__BATCHID,FSOURCE__TRUCKLOADNUMBER,FSOURCE__TRUCKLOADDATE,FSOURCE__TRUCKERNAME,FSOURCE__REPACK,FSOURCE__SHIPPINGDATESTARTTS,FSOURCE__SHIPPINGTIMESTARTTS,FSOURCE__SHIPPINGDATEENDSTS,FSOURCE__SHIPPINGTIMEENDSTS,FSOURCE__SHIPPINGAISLE,FSOURCE__DELIVERYZONE);
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
    l.add(Types.TIMESTAMP);    //SYSTEMTIME
    l.add(Types.NUMERIC);    //IDCOLLO
    l.add(Types.VARCHAR);    //COLLO DESC
    l.add(Types.NUMERIC);    // STATUS
    l.add(Types.TIMESTAMP);    //TIMESTAMP
    l.add(Types.TIMESTAMP);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);   //BOXID
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR); //productionLine
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);   //X
    l.add(Types.NUMERIC);   //Y
    l.add(Types.NUMERIC);   //Z
    l.add(Types.NUMERIC);   
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);  //CUSTOMERORDERNO
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.TIMESTAMP);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);  //DELIVERYZONE
    
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypesDestination() {
    List l =new ArrayList();
    
    l.add(Types.NUMERIC);     //id Messaggio da As400
    l.add(Types.TIMESTAMP);    //SYSTEMTIME
    l.add(Types.NUMERIC);    //IDCOLLO
    l.add(Types.VARCHAR);    //COLLO DESC
    l.add(Types.NUMERIC);    // STATUS
    l.add(Types.TIMESTAMP);    //TIMESTAMP
    l.add(Types.TIMESTAMP);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);   //BOXID
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR); //productionLine
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);   //X
    l.add(Types.NUMERIC);   //Y
    l.add(Types.NUMERIC);   //Z
    l.add(Types.NUMERIC);   
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);  //CUSTOMERORDERNO
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.TIMESTAMP);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);  //DELIVERYZONE
    
    
    
    
    return l;
  }

  @Override
  public String toString() {
    return "ID: "+this.getIdMsg()+" - IDCOLLO : "+this.getIDCOLLO();
  }

  
  
}
