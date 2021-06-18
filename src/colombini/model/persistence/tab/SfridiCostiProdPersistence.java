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
public class SfridiCostiProdPersistence implements ITable {

  public final static String TABNAME="ZDPSFC";
  public final static String TABFIELDS="SCCONO,SCTIPO,SCFACT,SCPLAN,SCPLGR,SCANNO,SCMESE,SCPNPR,SCITNO,"
                                      +"SCSUPT,SCSFRO,SCSFRF,SCCOM2,SCRGDT";
 
  public final static String TABKEYFIELDS="SCCONO,SCTIPO,SCFACT,SCPLAN,SCPLGR,SCANNO,SCMESE";
  
  public final static String SCCONO="SCCONO";
  public final static String SCTIPO="SCTIPO";
  public final static String SCFACT="SCFACT";
  public final static String SCPLAN="SCPLAN";
  public final static String SCPLGR="SCPLGR";
  public final static String SCANNO="SCANNO";
  public final static String SCMESE="SCMESE";
  
  
//  public final static String TABNAME="ZDPSFD";
//  public final static String TABFIELDS="ZDCONO,ZDFACT,ZDPLGR,ZDANNO,ZDMESE,ZDPNPR,ZDITNO,"
//                                      +"ZDSFRD,ZDSFIS,ZDSDR2,ZDSFRI,ZDCOM2,ZDRGDT";
//  
//  public final static String TABKEYFIELDS="ZDCONO,ZDFACT,ZDPLGR,ZDANNO,ZDMESE";
//  
//  public final static String ZDCONO="ZDCONO";
//  public final static String ZDFACT="ZDFACT";
//  public final static String ZDPLGR="ZDPLGR";
//  public final static String ZDANNO="ZDANNO";
//  public final static String ZDMESE="ZDMESE";
  
  
  
  public  final static String FACTG1="G1";
  public  final static String FACTR1="R1";
  public  final static String FACTR2="R2";
 
  public  final static String TIPO_CONSUNTIVO="CON";
  public  final static String TIPO_BUDGET="BDG";
  
  public SfridiCostiProdPersistence() {
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
    types.add(Types.CHAR);      //tipo
    types.add(Types.CHAR);      //stab
    types.add(Types.CHAR);      //piano
    types.add(Types.CHAR);      //centro
    
    types.add(Types.INTEGER); //anno 
    types.add(Types.INTEGER); //mese
    types.add(Types.CHAR);    //pnp rif
    types.add(Types.CHAR);    //articolo
    
    types.add(Types.NUMERIC);   //m2tot
    types.add(Types.NUMERIC);   //m2Sfrido ottimizzazione
    types.add(Types.NUMERIC);   //m2sfrido fisio
    types.add(Types.NUMERIC);   //costo itno m2 
    
    
    
    
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

//  public List<Integer> getFieldTypes(){
//    List<Integer> types=new ArrayList();
//    
//    types.add(Types.INTEGER);   //az
//    types.add(Types.CHAR);      //stab
//    types.add(Types.CHAR);      //centro
//    
//    types.add(Types.INTEGER); //anno 
//    types.add(Types.INTEGER); //mese
//    types.add(Types.CHAR);    //pnp rif
//    types.add(Types.CHAR);    //articolo
//    
//    types.add(Types.NUMERIC);   //sfrido ottimizzazione
//    types.add(Types.NUMERIC);   //sfrido fisiologico
//    types.add(Types.NUMERIC);   //sfrido difR2 
//    types.add(Types.NUMERIC);   //sfrido rid 
//    
//    
//    types.add(Types.NUMERIC); //costo itno m2
//    
//    types.add(Types.TIMESTAMP);
//    
//    return types;
//  }
 
 
  
  
}
