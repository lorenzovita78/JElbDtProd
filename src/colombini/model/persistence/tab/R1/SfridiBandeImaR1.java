/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab.R1;

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
public class SfridiBandeImaR1 implements ITable {

  public final static String TABNAME="ZDPSR1";
  public final static String TABFIELDS="ZICONO,ZIDTRF,ZIPLGR,ZICODB,ZINUMB,ZINUMP,ZILNGT,ZILNGP,ZIDIFT,ZISFIS,ZISPPC,ZSRGDT";
  public final static String TABKEYFIELDS="ZICONO,ZIDTRF,ZIPLGR,ZICODB,";
  
  public final static String ZICONO="ZICONO";
  public final static String ZIDTRF="ZIDTRF";
  public final static String ZIPLGR="ZIPLGR";
  
  
  
  public SfridiBandeImaR1() {
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
    types.add(Types.NUMERIC);      //data
    types.add(Types.CHAR);      //cdl
    
    types.add(Types.CHAR); //codice Banda
    types.add(Types.INTEGER);    //numero bande
    types.add(Types.INTEGER); //numero pz
    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    
    
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
