/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;


import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.StringUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 * Bean per la mappatura dell'archivio ZTAPPI.
 * Tale archivio contiene le informazioni relative ai file pdf associati ad un determinato pezzo
 * L'archivio rientra nel progettino web Tracciatura colli/pz per commessa 
 * @author lvita
 */
public class BeanInfoAggColloComForTAP  implements IBeanPersSIMPLE {
  
  public final static String TABNAME="ZTAPPI";
  public final static String TABFIELDS="TXPLGR,TXBARP,TXCOMM,TXDTRF,TXPHFE,TXDTST,TXDTIN";
  
  public final static String TABKEYFIELDS="TXPLGR,TXBARP,TXPHFE";
  
  
  
  public final String TXPLGR="TXPLGR";
  public final String TXBARP="TXBARP";
  public final String TXCOMM="TXCOMM";
  public final String TXDTRF="TXDTRF";
  public final String TXPHFE="TXPHFE";
  public final String TXDTST="TXDTST";
  public final String TXDTIN="TXDTIN";
  
  private String cdL;
  private String barcode;
  private Integer commessa;
  private Long dataRifN;
  private String pathFile;

  
  
  public String getCdL() {
    return cdL;
  }

  public void setCdL(String cdL) {
    this.cdL = cdL;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public String getPathFile() {
    return pathFile;
  }

  public void setPathFile(String pathFile) {
    this.pathFile = pathFile;
  }
  
  public BeanInfoAggColloComForTAP(String cdL,String barcode,Integer comm,Long dt) {
    this.cdL=cdL;
    this.barcode=barcode;
    this.commessa=comm;
    this.dataRifN=dt;
    this.pathFile="";
  }
  
  
  public BeanInfoAggColloComForTAP(String cdL,String barcode,Integer comm,Long dt,String pathFile) {
    this.cdL=cdL;
    this.barcode=barcode;
    this.pathFile=pathFile;
    this.dataRifN=dt;
    this.commessa=comm;
  }
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABNAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABKEYFIELDS.split(","));
  }

  
  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.CHAR);      //centro di lavoro
    types.add(Types.CHAR);      //barcode
    types.add(Types.INTEGER); //commessa
    types.add(Types.NUMERIC); //datacommessa
   
    types.add(Types.CHAR);    //path file
    types.add(Types.TIMESTAMP); //data stampa
    types.add(Types.TIMESTAMP); //data inserimento
    
    
    return types;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(TXPLGR, this.cdL);
    fieldsValue.put(TXBARP, this.barcode);
    fieldsValue.put(TXCOMM, this.commessa);
    fieldsValue.put(TXDTRF, this.dataRifN);
    fieldsValue.put(TXPHFE, this.pathFile);
    fieldsValue.put(TXDTIN, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(TXPLGR, this.cdL);
    fieldsValue.put(TXBARP, this.barcode);
    fieldsValue.put(TXCOMM, this.commessa);
    fieldsValue.put(TXDTRF, this.dataRifN);
    fieldsValue.put(TXPHFE, this.pathFile);
    
    
    
    return fieldsValue;
  }
 
  @Override
  public Boolean validate() {
    if(StringUtils.IsEmpty(this.pathFile)){
      _logger.error("Attenzione nessun file associato al barcode: "+this.barcode+" - cdl: "+this.cdL);
      return Boolean.FALSE;        
    }
      
    
    return Boolean.TRUE;
  }
  
  
  private static final Logger _logger = Logger.getLogger(BeanInfoAggColloComForTAP.class);

  
}

