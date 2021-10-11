/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.VdlAsSqlPoe;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.DateUtils;
/**
 *
 * @author ggraziani
 */
public class MsgColloInfoHead extends  MsgUploadColloInfoDetail{
  
    
 // <editor-fold defaultstate="collapsed" desc="Variables">   
  private static final String LIBRARY_SOURCE_VDL = "COLOMBINI";
  private static final String TABLE_SOURCE_VDL = "V2H_COLLOINFO";
  
  
  private static final String LIBRARY_DESTINATION_SQLPOE = "AvanzamentoVDL.dbo";
  private static final String TABLE_DESTINATION_SQLPOE = "V2H_ColloInfo_Header";
  
  
  public final static String FSOURCE__IDCOLLO_H                 ="COLLOID";
  public final static String FSOURCE__COLLODESC_H               ="COLLODESC";
  public final static String FSOURCE__BUNDLEID_H                ="BUNDLEID";
  public final static String FSOURCE__BOXID_H                   ="BOXID";
  public final static String FSOURCE__LAYERID_H                 ="LAYERID";
  public final static String FSOURCE__PLATEID_H                 ="PLATEID";
  public final static String FSOURCE__WEIGHT_H                  ="WEIGHT";
  public final static String FSOURCE__HEIGHT_H                  ="HEIGHT";
  public final static String FSOURCE__COMMISSIONID_H            ="COMMISSIONID";  
  public final static String FSOURCE__INTERNALSEQ_H             ="INTERNALSEQ";
  public final static String FSOURCE__PRODUCTIONLINE_H          ="PRODUCTIONLINE";
  public final static String FSOURCE__WAREHOUSE_H               ="WAREHOUSE";
  public final static String FSOURCE__DELIVERYID_H              ="DELIVERYID";
  public final static String FSOURCE__CLIENTID_H                ="CLIENTID";
  public final static String FSOURCE__CUSTOMERNO_H              ="CUSTOMERNO";
  public final static String FSOURCE__CUSTOMERNAME_H            ="CUSTOMERNAME";
  public final static String FSOURCE__CUSTOMERORDERNO_H         ="CUSTOMERORDERNO";
  public final static String FSOURCE__TRUCKLOADNUMBER_H         ="TRUCKLOADNUMBER";
  public final static String FSOURCE__TRUCKLOADDATE_H           ="TRUCKLOADDATE";
  public final static String FSOURCE__TRUCKERNAME_H             ="TRUCKERNAME";
  public final static String FSOURCE__DELIVERYZONE_H            ="DELIVERYZONE";
  
  public final static String FDEST__IDCOLLO_H                 ="COLLOID";
  public final static String FDEST__COLLODESC_H               ="COLLODESC";
  public final static String FDEST__BUNDLEID_H                ="BUNDLEID";
  public final static String FDEST__BOXID_H                   ="BOXID";
  public final static String FDEST__LAYERID_H                 ="LAYERID";
  public final static String FDEST__PLATEID_H                 ="PLATEID";
  public final static String FDEST__WEIGHT_H                  ="WEIGHT";
  public final static String FDEST__HEIGHT_H                  ="HEIGHT";
  public final static String FDEST__COMMISSIONID_H            ="COMMISSIONID";  
  public final static String FDEST__INTERNALSEQ_H             ="INTERNALSEQ";
  public final static String FDEST__PRODUCTIONLINE_H          ="PRODUCTIONLINE";
  public final static String FDEST__WAREHOUSE_H               ="WAREHOUSE";
  public final static String FDEST__DELIVERYID_H              ="DELIVERYID";
  public final static String FDEST__CLIENTID_H                ="CLIENTID";
  public final static String FDEST__CUSTOMERNO_H              ="CUSTOMERNO";
  public final static String FDEST__CUSTOMERNAME_H            ="CUSTOMERNAME";
  public final static String FDEST__CUSTOMERORDERNO_H         ="CUSTOMERORDERNO";
  public final static String FDEST__TRUCKLOADNUMBER_H         ="TRUCKLOADNUMBER";
  public final static String FDEST__TRUCKLOADDATE_H           ="TRUCKLOADDATE";
  public final static String FDEST__TRUCKERNAME_H             ="TRUCKERNAME";
  public final static String FDEST__DELIVERYZONE_H            ="DELIVERYZONE";
 
  
  
    private Long IDCOLLO_H;
    private String COLLODESC_H;      
    private String BUNDLEID_H;       
    private Long BOXID_H;          
    private Long LAYERID_H;        
    private Long PLATEID_H;        
    private Long WEIGHT_H;         
    private Long HEIGHT_H;         
    private Long COMMISSIONID_H;   
    private Long INTERNALSEQ_H;    
    private String PRODUCTIONLINE_H; 
    private Long WAREHOUSE_H;      
    private Long DELIVERYID_H;     
    private String CLIENTID_H;       
    private String CUSTOMERNO_H;     
    private String CUSTOMERNAME_H;   
    private String CUSTOMERORDERNO_H;
    private Long TRUCKLOADNUMBER_H;
    private String TRUCKLOADDATE_H;  
    private String TRUCKERNAME_H;    
    private String DELIVERYZONE_H;  
  

    public MsgColloInfoHead (){
      super();
    }
    
     public MsgColloInfoHead (MsgUploadColloInfoDetail bean){
      super();
      this.IDCOLLO_H=bean.getIDCOLLO();
      this.BOXID_H=bean.getBOXID();
      this.BUNDLEID_H=bean.getBUNDLEID();
      this.CLIENTID_H=bean.getCLIENTID();
      this.COLLODESC_H=bean.getCOLLODESC();
      this.COMMISSIONID_H=bean.getCOMMISSIONID();
      this.CUSTOMERNAME_H=bean.getCUSTOMERNAME();
      this.CUSTOMERNO_H=bean.getCUSTOMERNO();
      this.CUSTOMERORDERNO_H=bean.getCUSTOMERORDERNO();
      this.DELIVERYID_H=bean.getDELIVERYID();
      this.DELIVERYZONE_H=bean.getDELIVERYZONE();
      this.HEIGHT_H=bean.getHEIGHT();
      this.INTERNALSEQ_H=bean.getINTERNALSEQ();
      this.LAYERID_H=bean.getLAYERID();
      this.PLATEID_H=bean.getPLATEID();
      this.PRODUCTIONLINE_H=bean.getPRODUCTIONLINE();
      this.TRUCKERNAME_H=bean.getTRUCKERNAME();
      this.TRUCKLOADDATE_H=bean.getTRUCKLOADDATE();
      this.TRUCKLOADNUMBER_H=bean.getTRUCKLOADNUMBER();
      this.WAREHOUSE_H=bean.getWAREHOUSE();
      this.WEIGHT_H=bean.getWEIGHT(); 
      this.setINSERTDATE();
    }

    public MsgColloInfoHead (Long idMs){
      super();
      setIdMsg(idMs);
    }

    public void setIDCOLLO_h(Long IDCOLLO_H) {
        this.IDCOLLO_H = IDCOLLO_H;
    }

    public void setCOLLODESC_H(String COLLODESC_H) {
        this.COLLODESC_H = COLLODESC_H;
    }

    public void setBUNDLELD_H(String BUNDLELD_H) {
        this.BUNDLEID_H = BUNDLELD_H;
    }

    public void setBOXID_H(Long BOXID_H) {
        this.BOXID_H = BOXID_H;
    }

    public void setLAYERID_H(Long LAYERID_H) {
        this.LAYERID_H = LAYERID_H;
    }

    public void setPLATEID_H(Long PLATEID_H) {
        this.PLATEID_H = PLATEID_H;
    }

    public void setWEIGHT_H(Long WEIGHT_H) {
        this.WEIGHT_H = WEIGHT_H;
    }

    public void setHEIGHT_H(Long HEIGHT_H) {
        this.HEIGHT_H = HEIGHT_H;
    }

    public void setCOMMISSIONID_H(Long COMMISSIONID_H) {
        this.COMMISSIONID_H = COMMISSIONID_H;
    }

    public void setINTERNALSEQ_H(Long INTERNALSEQ_H) {
        this.INTERNALSEQ_H = INTERNALSEQ_H;
    }

    public void setPRODUCTIONLINE_H(String PRODUCTIONLINE_H) {
        this.PRODUCTIONLINE_H = PRODUCTIONLINE_H;
    }

    public void setWAREHOUSE_H(Long WAREHOUSE_H) {
        this.WAREHOUSE_H = WAREHOUSE_H;
    }

    public void setDELIVERYID_H(Long DELIVERYID_H) {
        this.DELIVERYID_H = DELIVERYID_H;
    }

    public void setCLIENTID_H(String CLIENTID_H) {
        this.CLIENTID_H = CLIENTID_H;
    }

    public void setCUSTOMERNO_H(String CUSTOMERNO_H) {
        this.CUSTOMERNO_H = CUSTOMERNO_H;
    }

    public void setCUSTOMERNAME_H(String CUSTOMERNAME_H) {
        this.CUSTOMERNAME_H = CUSTOMERNAME_H;
    }

    public void setCUSTOMERORDERNO_H(String CUSTOMERORDERNO_H) {
        this.CUSTOMERORDERNO_H = CUSTOMERORDERNO_H;
    }

    public void setTRUCKLOADNUMBER_H(Long TRUCKLOADNUMBER_H) {
        this.TRUCKLOADNUMBER_H = TRUCKLOADNUMBER_H;
    }

    public void setTRUCKLOADDATE_H(String TRUCKLOADDATE_H) {
        this.TRUCKLOADDATE_H = TRUCKLOADDATE_H;
    }

    public void setTRUCKERNAME_H(String TRUCKERNAME_H) {
        this.TRUCKERNAME_H = TRUCKERNAME_H;
    }

    public void setDELIVERYZONE_H(String DELIVERYZONE_H) {
        this.DELIVERYZONE_H = DELIVERYZONE_H;
    }
  
    public static String getLIBRARY_SOURCE_VDL() {
        return LIBRARY_SOURCE_VDL;
    }

    public static String getTABLE_SOURCE_VDL() {
        return TABLE_SOURCE_VDL;
    }

    public static String getLIBRARY_DESTINATION_SQLPOE() {
        return LIBRARY_DESTINATION_SQLPOE;
    }

    public static String getTABLE_DESTINATION_SQLPOE() {
        return TABLE_DESTINATION_SQLPOE;
    }

    public Long getIDCOLLO_H() {
        return IDCOLLO_H;
    }

    public String getCOLLODESC_H() {
        return COLLODESC_H;
    }

    public String getBUNDLELD_H() {
        return BUNDLEID_H;
    }

    public Long getBOXID_H() {
        return BOXID_H;
    }

    public Long getLAYERID_H() {
        return LAYERID_H;
    }

    public Long getPLATEID_H() {
        return PLATEID_H;
    }

    public Long getWEIGHT_H() {
        return WEIGHT_H;
    }

    public Long getHEIGHT_H() {
        return HEIGHT_H;
    }

    public Long getCOMMISSIONID_H() {
        return COMMISSIONID_H;
    }

    public Long getINTERNALSEQ_H() {
        return INTERNALSEQ_H;
    }

    public String getPRODUCTIONLINE_H() {
        return PRODUCTIONLINE_H;
    }

    public Long getWAREHOUSE_H() {
        return WAREHOUSE_H;
    }

    public Long getDELIVERYID_H() {
        return DELIVERYID_H;
    }

    public String getCLIENTID_H() {
        return CLIENTID_H;
    }

    public String getCUSTOMERNO_H() {
        return CUSTOMERNO_H;
    }

    public String getCUSTOMERNAME_H() {
        return CUSTOMERNAME_H;
    }

    public String getCUSTOMERORDERNO_H() {
        return CUSTOMERORDERNO_H;
    }

    public Long getTRUCKLOADNUMBER_H() {
        return TRUCKLOADNUMBER_H;
    }

    public String getTRUCKLOADDATE_H() {
        return TRUCKLOADDATE_H;
    }

    public String getTRUCKERNAME_H() {
        return TRUCKERNAME_H;
    }

    public String getDELIVERYZONE_H() {
        return DELIVERYZONE_H;
    }
  

      // </editor-fold>

  
//  
//  private String getSqlSelectSource(){
//    String campi=ListUtils.toCommaSeparatedString(getFieldsSource());
//    String select ="Select "+campi+ 
//                  "\n from "+getLibNameSource()+" . "+getTableNameSource()+
//                  " where 1=1 "+
//                  " and "+FSOURCE__IDCOLLO_H+" = "+JDBCDataMapper.objectToSQL(this.getIDCOLLO_h());
//    
//    return select;
//  }
//  
//  public List<String> getFields() {
//    List l=new ArrayList();
//    l.add(FDEST__IDCOLLO_H);
//     
//    return l;
//  }
  
 

  @Override
  public Map<String, Object> getFieldValuesMapDestinationForDlt() {
     Map m=new HashMap();
    
    m.put(FDEST__IDCOLLO,this.getIDCOLLO_H());
    
    return m;
  }
  
   
  public List<String> getFieldValuesMapDestinationForCheck() {
        return Arrays.asList(FDEST__IDCOLLO_H);
  }


  @Override
  public Map<String, Object> getFieldValuesMapDestination() {
    Map m=new HashMap();
    m.put(FDEST__IDCOLLO_H,this.IDCOLLO_H);
    m.put(FDEST__COLLODESC_H,this.COLLODESC_H);
    m.put(FDEST__BUNDLEID_H,this.BUNDLEID_H);
    m.put(FDEST__BOXID_H,this.BOXID_H);
    m.put(FDEST__LAYERID_H,this.LAYERID_H);
    m.put(FDEST__PLATEID_H,this.PLATEID_H);
    m.put(FDEST__WEIGHT_H,this.WEIGHT_H);
    m.put(FDEST__HEIGHT_H,this.HEIGHT_H);
    m.put(FDEST__COMMISSIONID_H,this.COMMISSIONID_H);
    m.put(FDEST__INTERNALSEQ_H,this.INTERNALSEQ_H);
    m.put(FDEST__PRODUCTIONLINE_H,this.PRODUCTIONLINE_H);
    m.put(FDEST__WAREHOUSE_H,this.WAREHOUSE_H);
    m.put(FDEST__DELIVERYID_H,this.DELIVERYID_H);
    m.put(FDEST__CLIENTID_H,this.CLIENTID_H);
    m.put(FDEST__CUSTOMERNO_H,this.CUSTOMERNO_H);
    m.put(FDEST__CUSTOMERNAME_H,this.CUSTOMERNAME_H);
    m.put(FDEST__CUSTOMERORDERNO_H,this.CUSTOMERORDERNO_H);
    m.put(FDEST__TRUCKLOADNUMBER_H,this.TRUCKLOADNUMBER_H);
    m.put(FDEST__TRUCKLOADDATE_H,this.TRUCKLOADDATE_H);
    m.put(FDEST__TRUCKERNAME_H,this.TRUCKERNAME_H);
    m.put(FDEST__DELIVERYZONE_H,this.DELIVERYZONE_H);
    return m;
  }

  @Override
  public List<String> getFieldsDestination() {
    return Arrays.asList( FDEST__IDCOLLO_H,FDEST__COLLODESC_H,FDEST__BUNDLEID_H,FDEST__BOXID_H,FDEST__LAYERID_H,FDEST__PLATEID_H,FDEST__WEIGHT_H,FDEST__HEIGHT_H,FDEST__COMMISSIONID_H,FDEST__INTERNALSEQ_H,FDEST__PRODUCTIONLINE_H,FDEST__WAREHOUSE_H,FDEST__DELIVERYID_H,FDEST__CLIENTID_H,FDEST__CUSTOMERNO_H,FDEST__CUSTOMERNAME_H,FDEST__CUSTOMERORDERNO_H,FDEST__TRUCKLOADNUMBER_H,FDEST__TRUCKLOADDATE_H,FDEST__TRUCKERNAME_H,FDEST__DELIVERYZONE_H);
  }

//  @Override
//  public String getLibNameSource() {
//    return LIBRARY_SOURCE_VDL;
//  }
//
//  @Override
//  public String getTableNameSource() {
//    return TABLE_SOURCE_VDL;
//  }

  @Override
  public String getLibNameDestination() {
    return LIBRARY_DESTINATION_SQLPOE;
  }

  @Override
  public String getTableNameDestination() {
    return TABLE_DESTINATION_SQLPOE;
  }

  @Override
  public List<Integer> getFieldTypesDestination() {
    List l =new ArrayList();
    
    l.add(Types.NUMERIC);     //ID COLLO
    l.add(Types.VARCHAR);     
    l.add(Types.VARCHAR);    
    l.add(Types.NUMERIC);   
    l.add(Types.NUMERIC);       
    l.add(Types.NUMERIC);   
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.NUMERIC);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR);
    l.add(Types.VARCHAR); //DELIVERYZONE   
    return l;
  }

  @Override
  public String toString() {
    return " - IDCOLLO : "+this.getIDCOLLO_H();
  }

    public String getLibraryName() {
      return getLibNameDestination();
    }
  
    public String getTableName() {
      return getTableNameDestination();
    }
  
    
    
  
}
