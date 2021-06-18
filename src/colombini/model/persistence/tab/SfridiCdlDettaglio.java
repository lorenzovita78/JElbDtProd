/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab;

import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import utils.ArrayUtils;
import utils.StringUtils;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class SfridiCdlDettaglio implements ITable {

  public final static String TABNAME="ZDPSFD";
  public final static String TABFIELDS="ZSCONO,ZSPLGR,ZSDTRF,ZSNUMO,ZSCPNP,ZSITNO,ZSLNGT,ZSWDTH,ZSTHCK,ZSTTPD,ZSTTPP,ZSSUPD,ZSSUPU,ZSSPDF,ZSSPSP,ZSSPRF,ZSSFIS,ZSSUP1,ZSSUP2,ZSSUP3,ZSSUP4,ZSLMDT";
  public final static String TABKEYFIELDS="ZSCONO,ZSPLGR,ZSDTRF,ZSNUMO,ZSITNO";
  
  public final static String ZSCONO="ZSCONO";
  public final static String ZSPLGR="ZSPLGR";
  public final static String ZSDTRF="ZSDTRF";
  
  
  
  
  
  public SfridiCdlDettaglio() {
  }
  

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
    //return "LVITA";
  }

  @Override
  public String getTableName() {
    return TABNAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(StringUtils.split(TABFIELDS,","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(StringUtils.split(TABKEYFIELDS,","));
  }

  @Override
  public List<Integer> getFieldTypes(){
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //cdl
    
    types.add(Types.NUMERIC); //data
    types.add(Types.NUMERIC); //num Ott
    
    types.add(Types.CHAR);    //codice pnp
    types.add(Types.CHAR);    //codice
    types.add(Types.NUMERIC); 
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    
    
    types.add(Types.INTEGER); //tot pan
    types.add(Types.INTEGER);
 
    types.add(Types.NUMERIC); //superficie tot
    types.add(Types.NUMERIC); //superficie utilizzata
    types.add(Types.NUMERIC);//superficie con difetti
    types.add(Types.NUMERIC); //sovraproduzione
    types.add(Types.NUMERIC); //superficie rifilo
    types.add(Types.NUMERIC); //sfrido fisio
    
    types.add(Types.NUMERIC); //campi personalizzati 1
    types.add(Types.NUMERIC); //2
    types.add(Types.NUMERIC); //3 
    types.add(Types.NUMERIC); //4
    
    types.add(Types.TIMESTAMP);
   
    
    return types;
  }

//  @Override
//  public Map<String, Object> getFieldsMapForDelete() {
//    return mapFieldsVal;
//  }
//
//  @Override
//  public void setFieldsMapForDelete(Map<String, Object> map) {
//    this.mapFieldsVal=map;
//  }
//
//  @Override
//  public List<String> getListFieldForDelete() {
//    throw new UnsupportedOperationException("Not supported yet.");
//  }

  
 
 
  
  
}
