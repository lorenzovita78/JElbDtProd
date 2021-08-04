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
 *
 * @author lvita
 */
public class AvProdCommLineaBean implements IBeanPersCRUD{
  
  public final static String TABNAME="ZDPCAV";
  public final static String TABFIELDS="ZACONO,ZACOMM,ZADTCO,ZAPLGR,ZATOTC,ZAPRDC,ZALMUT,ZALMDT";
  
  public final static String TABKEYFIELDS="ZACONO,ZACOMM,ZADTCO,ZAPLGR";
  
  
  public final static String ZACONO="ZACONO";
  public final static String ZACOMM="ZACOMM";
  public final static String ZADTCO="ZADTCO";
  public final static String ZAPLGR="ZAPLGR";
  public final static String ZATOTC="ZATOTC";
  public final static String ZAPRDC="ZAPRDC";
  public final static String ZALMUT="ZALMUT";
  public final static String ZALMDT="ZALMDT";
  
 
  private Integer commessa;
  private Long dataComN;
  private String codLinea;
  
  
  private Integer pzComm;
  private Integer pzProdComm;

  public AvProdCommLineaBean(Integer azienda,Integer comm,Long dtComm,String linea){
    this.commessa=comm;
    this.dataComN=dtComm;
    this.codLinea=linea;
    
    this.pzComm=Integer.valueOf(0);
    this.pzProdComm=Integer.valueOf(0);
   
  }
  
  public AvProdCommLineaBean(Integer azienda,Integer comm,Long dtComm,String linea,Integer pz){
    this.commessa=comm;
    this.dataComN=dtComm;
    this.codLinea=linea;
    
    this.pzComm=pz;
    this.pzProdComm=Integer.valueOf(0);
   
  } 

  @Override
  public String toString() {
    return "Linea :"+codLinea+"; Commessa :"+commessa+" ; Data Commessa:"+dataComN.toString();
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

  public Integer getPzComm() {
    return pzComm;
  }

  public void setPzComm(Integer pzComm) {
    this.pzComm = pzComm;
  }

  public Integer getPzProdComm() {
    return pzProdComm;
  }

  public void setPzProdComm(Integer pzProdComm) {
    this.pzProdComm = pzProdComm;
  }

  public Integer getResComm(){
    return pzComm-pzProdComm;
  }
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZACONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZAPLGR, this.codLinea);
    fieldsValue.put(ZADTCO, this.dataComN);
    fieldsValue.put(ZACOMM, this.commessa);
    fieldsValue.put(ZATOTC, this.pzComm);
    fieldsValue.put(ZAPRDC, this.pzProdComm);
    
    fieldsValue.put(ZALMUT, CostantsColomb.UTDEFAULT);
    fieldsValue.put(ZALMDT, new Date());

    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZACONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZAPLGR, this.codLinea);
    fieldsValue.put(ZADTCO, this.dataComN);
    fieldsValue.put(ZACOMM, this.commessa);
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    Map fieldsValue=new HashMap();
    

    fieldsValue.put(ZAPRDC, this.pzProdComm);
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
    
    types.add(Types.INTEGER);    //numero pz commessa da produrre
    types.add(Types.INTEGER);    //numero pz prodotti
    
    types.add(Types.CHAR);    //utente modifica
    types.add(Types.TIMESTAMP); //data variazione
    
    
    return types;
  }

  
  
  
}

