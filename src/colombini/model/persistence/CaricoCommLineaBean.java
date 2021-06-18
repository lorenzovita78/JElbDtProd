/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import colombini.costant.CostantsColomb;
import db.Connections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.DateUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 * Bean per gestire le informazioni relative ai pezzi/colli prodotti per una certa BU (divisione) 
 * relativamente ad una determinata commessa
 * @author lvita
 */
public class CaricoCommLineaBean implements IBeanPersSIMPLE{

  private Integer commessa;
  private Long    dataRifN;
  private String  divisione;
  private String  lineaLav;
  private Double  valore;
  private String  unitaMisura;

  public final static String TABNAME="ZDPCOM";
  public final static String TABFIELDS="ZDCONO,ZDCOMM,ZDDTCO,ZDDIVI,ZDPLGR,ZDVALE,ZDUNME,ZDRGUT,ZDRGDT,ZDRGOR";
  
  public final static String TABKEYFIELDS="ZDCONO,ZDCOMM,ZDDTCO,ZDPLGR,ZDDIVI";
  
  
  public final static String ZDCONO="ZDCONO";
  public final static String ZDCOMM="ZDCOMM";
  public final static String ZDDTCO="ZDDTCO";
  public final static String ZDPLGR="ZDPLGR";
  public final static String ZDDIVI="ZDDIVI";
  public final static String ZDVALE="ZDVALE";
  public final static String ZDUNME="ZDUNME";
  public final static String ZDRGUT="ZDRGUT";
  public final static String ZDRGDT="ZDRGDT";
  public final static String ZDRGOR="ZDRGOR";
  
  
  public CaricoCommLineaBean(){
    dataRifN=Long.valueOf(0);
    valore=Double.valueOf(0);
  }
  
  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public String getDivisione() {
    return divisione;
  }

  public void setDivisione(String divisione) {
    this.divisione = divisione;
  }

  public String getLineaLav() {
    return lineaLav;
  }

  public void setLineaLav(String lineaLav) {
    this.lineaLav = lineaLav;
  }

  public Double getValore() {
    return valore;
  }

  public void setValore(Double valore) {
    this.valore = valore;
  }


  public String getUnitaMisura() {
    return unitaMisura;
  }

  public void setUnitaMisura(String unitaMisura) {
    this.unitaMisura = unitaMisura;
  }

  @Override
  public String toString() {
    return " Commessa : "+commessa+" dataCom :"+dataRifN+" centroL : "+lineaLav+" divisione : "+divisione
            +" valore :"+valore.toString();
  }

  
  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZDCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZDPLGR, this.lineaLav);
    fieldsValue.put(ZDDTCO, this.dataRifN);
    fieldsValue.put(ZDCOMM, this.commessa);
    fieldsValue.put(ZDDIVI, this.divisione);
    fieldsValue.put(ZDUNME, this.unitaMisura);
    fieldsValue.put(ZDVALE, this.valore);
    
    fieldsValue.put(ZDRGUT, "UTJCOLOM");
    fieldsValue.put(ZDRGDT, DateUtils.getDataSysLong());
    fieldsValue.put(ZDRGOR, DateUtils.getOraSysString());

    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZDCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZDPLGR, this.lineaLav);
    fieldsValue.put(ZDDTCO, this.dataRifN);
    fieldsValue.put(ZDCOMM, this.commessa);
    
    return fieldsValue;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getLibraryName() {
    return Connections.getInstance().getLibraryPersAs400();
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
    
    types.add(Types.INTEGER);   //az
    types.add(Types.INTEGER); //commessa
    types.add(Types.NUMERIC); //datacommessa
    
    types.add(Types.CHAR);      //centro di lavoro
    types.add(Types.CHAR);      //divisione
    
    types.add(Types.NUMERIC);    //valore
    types.add(Types.CHAR);    //unità di misura
    
    types.add(Types.CHAR);    //utente inserimento
    types.add(Types.NUMERIC); //data inserimento
    types.add(Types.CHAR);    //ora inserimento
    
    
    
    return types;
  }
  
}
