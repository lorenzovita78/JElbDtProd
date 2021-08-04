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
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class DtSaturazioneMSL implements ITable {

  
  public final static String TABNAME="ZDPSMS";
  public final static String TABFIELDS="ZSCONO,ZSDTRF,ZSDEPT,ZSDEST,ZSNT10,ZSNT14,ZSNT28,ZSRGDT,ZSRGTM ";
  public final static String TABKEYFIELDS="ZSCONO,ZSDTRF";
  
  public final static String ZSCONO="ZSCONO";
  public final static String ZSDTRF="ZSDTRF";
  
  
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
    
    types.add(Types.INTEGER);   //az
    types.add(Types.NUMERIC);      //data
    
    
    types.add(Types.CHAR); //provenienza
    types.add(Types.CHAR); //Arrivo
    
    types.add(Types.INTEGER);    //numero t10
    types.add(Types.INTEGER);    //numero t14
    types.add(Types.INTEGER);    //numero t28
    
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    
    return types;
    
  }
  
}
