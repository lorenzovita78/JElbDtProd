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
 * relativamente ad una determinata commessa e agli ordini estero
 * @author lvita
 */
public class CaricoCommLineaExtBean  implements IBeanPersSIMPLE{

  public final static String TABLEDTCOMEXT="ZDPCME";
  
  public final static String FIELDS="ZECONO,ZECOMM,ZEDTCO,ZEPLGR,ZEDIVI,ZEVALE,ZEUNME,ZERGDT,ZERGOR";
  public final static String TABKEYFIELDS="ZECONO,ZECOMM,ZEDTCO,ZEPLGR,ZEDIVI,ZEUNME";
          
  public final static String ZECONO="ZECONO"; 
  public final static String ZECOMM="ZECOMM"; 
  public final static String ZEDTCO="ZEDTCO"; 
  public final static String ZEPLGR="ZEPLGR"; 
  public final static String ZEDIVI="ZEDIVI"; 
  public final static String ZEVALE="ZEVALE"; 
  public final static String ZEUNME="ZEUNME"; 
  public final static String ZERGDT="ZERGDT"; 
  public final static String ZERGOR="ZERGOR"; 

  private CaricoCommLineaBean cb;
      
  public CaricoCommLineaExtBean(){
    cb=new CaricoCommLineaBean();
  }
  
  public CaricoCommLineaExtBean(CaricoCommLineaBean b){
    super();
    cb=b;
  }
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(ZECONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZECOMM, cb.getCommessa());
    fieldsValue.put(ZEDTCO, cb.getDataRifN());
    fieldsValue.put(ZEPLGR, cb.getLineaLav());
    
    fieldsValue.put(ZEDIVI, cb.getDivisione());
    fieldsValue.put(ZEVALE, cb.getValore());
    fieldsValue.put(ZEUNME, cb.getUnitaMisura());
    
    fieldsValue.put(ZERGDT, DateUtils.getDataSysLong());
    fieldsValue.put(ZERGOR, DateUtils.getOraSysString());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(ZECONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZECOMM, cb.getCommessa());
    fieldsValue.put(ZEDTCO, cb.getDataRifN());
    fieldsValue.put(ZEPLGR, cb.getLineaLav());
    
    return fieldsValue;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }

  @Override
  public String getLibraryName() {
    return Connections.getInstance().getLibraryPersAs400();
  }

  @Override
  public String getTableName() {
    return TABLEDTCOMEXT;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(FIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
   return ArrayUtils.getListFromArray(TABKEYFIELDS.split(","));
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.INTEGER);   //commessa
    
    
    types.add(Types.NUMERIC); //datacommessa
    types.add(Types.CHAR);     //linea 
    types.add(Types.CHAR);    //divisione
    types.add(Types.NUMERIC);    //valore
    
    types.add(Types.CHAR);    //unita di misura
    
    types.add(Types.NUMERIC); //data inserimento
    types.add(Types.CHAR); //ora inserimento
    
    return types;
  }

  
  
}
