/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab.R1;

import colombini.conn.ColombiniConnections;
import db.Connections;
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
public class SfridiBandeImaAnteR1 implements ITable {

  public final static String TABNAME="ZDPSBI";
  public final static String TABFIELDS="ZSCONO,ZSANNO,ZSSETT,ZSCODB,ZSPSFR,ZSNUMB,ZSRGDT,ZSRGTM ";
  public final static String TABKEYFIELDS="ZSCONO,ZSANNO,ZSSETT,ZSCODB";
  
  public final static String ZSCONO="ZSCONO";
  public final static String ZSANNO="ZSANNO";
  public final static String ZSSETT="ZSSETT";
  
  
  
  public SfridiBandeImaAnteR1() {
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
    types.add(Types.INTEGER);      //anno
    types.add(Types.INTEGER);      //sett
    
    types.add(Types.CHAR); //codice Banda
    types.add(Types.NUMERIC); //perc sfrido
    
    types.add(Types.INTEGER);    //numero bande
    
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
