/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.costant.CostantsColomb;
import db.Connections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import db.persistence.IBeanPersCRUD;

/**
 * Bean per gestire la persistenza dei dati relativi alla produzione giornaliera di una linea per commessa
 * @author lvita
 */
public class ProdCommLinea implements IBeanPersCRUD{
  
  public final static String TABNAME="ZDPCPR";
  public final static String TABFIELDS="ZPCONO,ZPCOMM,ZPDTCO,ZPPLGR,ZPDTRF,ZPPRDC,ZPLMDT";
  
  public final static String TABKEYFIELDS="ZPCONO,ZPCOMM,ZPDTCO,ZPPLGR,ZPDTRF";
  
  
  public final static String ZPCONO="ZPCONO";
  public final static String ZPCOMM="ZPCOMM";
  public final static String ZPDTCO="ZPDTCO";
  public final static String ZPPLGR="ZPPLGR";
  public final static String ZPDTRF="ZPDTRF";
  public final static String ZPPRDC="ZPPRDC";
  public final static String ZALMDT="ZPLMDT";
  
 
  private Integer commessa;
  private Long dataComN;
  private String codLinea;
  private Long dataRifN;
  
  private Integer pzProdComm;

  public ProdCommLinea(Integer azienda,Integer comm,Long dtComm,String linea,Long dtRf){
    this.commessa=comm;
    this.dataComN=dtComm;
    this.codLinea=linea;
    this.dataRifN=dtRf;
    
    this.pzProdComm=Integer.valueOf(0);
   
  }
  
 
  
  public Integer getCommessa() {
    return commessa;
  }

  public Long getDataComN() {
    return dataComN;
  }

  public String getCodLinea() {
    return codLinea;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

 
  public Integer getPzProdComm() {
    return pzProdComm;
  }

  public void setPzProdComm(Integer pzProdComm) {
    this.pzProdComm = pzProdComm;
  }

  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZPCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZPPLGR, this.codLinea);
    fieldsValue.put(ZPDTCO, this.dataComN);
    fieldsValue.put(ZPCOMM, this.commessa);
    fieldsValue.put(ZPDTRF, this.dataRifN);
    fieldsValue.put(ZPPRDC, this.pzProdComm);
    
    fieldsValue.put(ZALMDT, new Date());

    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZPCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZPPLGR, this.codLinea);
    fieldsValue.put(ZPDTCO, this.dataComN);
    fieldsValue.put(ZPCOMM, this.commessa);
    fieldsValue.put(ZPDTRF, this.dataRifN);
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    Map fieldsValue=new HashMap();
    
//    fieldsValue.put(ZVRESC, this.pzComm);
    fieldsValue.put(ZPPRDC, this.pzProdComm);
    fieldsValue.put(ZALMDT, new Date());
    
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
    
    types.add(Types.NUMERIC);    //data riferimento
    types.add(Types.INTEGER);    //numero pz prodotti
    
    types.add(Types.TIMESTAMP); //data variazione
    
    
    return types;
  }

  
}