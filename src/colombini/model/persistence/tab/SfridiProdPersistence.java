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
public class SfridiProdPersistence implements ITable {

  public final static String TABNAME="ZDPSFR";
  public final static String TABFIELDS="ZSCONO,ZSFACT,ZSPLAN,ZSDTRF,ZSTMRF,ZSNUMO,ZSCPNP,ZSCODI,ZSLNGT,ZSWDTH,ZSTHCK,ZSTTPD,ZSTTPP,ZSUNMS,ZSSUPD,ZSSUPU,ZSSPDF,ZSSFIS,ZSSUP1,ZSSUP2,ZSSUPS,ZSRGDT,ZSRGTM ";
  public final static String TABKEYFIELDS="ZSCONO,ZSFACT,ZSPLAN,ZSDTRF,ZSTMRF,ZSNUMO";
  
  public final static String ZSCONO="ZSCONO";
  public final static String ZSFACT="ZSFACT";
  public final static String ZSPLAN="ZSPLAN";
  public final static String ZSDTRF="ZSDTRF";
  
  
  public  final static String FACTG1="G1";
  public  final static String FACTR1="R1";
  public  final static String FACTR2="R2";
  public final static String PLANP0="P0";
  public final static String PLANP1="P1";
  public final static String PLANP2="P2";
  public final static String UNMQ="MQ";
  public final static String UNML="ML";
  
  
  
  
  public SfridiProdPersistence() {
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
    types.add(Types.CHAR);      //stab
    types.add(Types.CHAR);      //piano
    
    types.add(Types.NUMERIC); //data
    types.add(Types.INTEGER); //ora
    types.add(Types.NUMERIC); //num Ott
    
    types.add(Types.CHAR);    //codice pnp
    types.add(Types.CHAR);    //codice
    types.add(Types.NUMERIC); //
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    
    
    types.add(Types.INTEGER); //tot pan
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
 
    types.add(Types.NUMERIC);//superficie tot
    types.add(Types.NUMERIC); // superficie utilizzata
    types.add(Types.NUMERIC);//superficie con difetti
    types.add(Types.NUMERIC); //sfrido fisio
    
    types.add(Types.NUMERIC); //campi personalizzati 1
    types.add(Types.NUMERIC); //2
    
    types.add(Types.NUMERIC); //sovraproduzione
    
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    
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
