/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.old;

import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.persistence.ITable;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import utils.ArrayUtils;
import utils.StringUtils;

/**
 * Dati relativi al "repacking" dei colli (RepackContainer)--> forniti da VDL 
 * @author lvita
 */
public class MsgAs400DettImballo implements ITable{

  public final static String TABLE_NAME="ZVAIIM";
  public final static String FIELDS="ZIIDMS, ZIPAII, ZIPACT, ZIPANM, ZIGRW4, ZIPACL, ZIPACW, ZIPACH, ZIDLMO, ZIORNO, ZICONO, ZICONN, ZIULRE";
  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(StringUtils.split(FIELDS, ","));
  }

  @Override
  public List<String> getKeyFields() {
    return Arrays.asList("ZIIDMS");
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    
    return types;
  }
  
  
  public String getSqlForReadMessage(BigDecimal idMsg){
    String S=" select "+FIELDS+
            "\n from "+getLibraryName()+"."+getTableName()+
            "\n where 1=1 "+
            " and ZPIDMS="+JDBCDataMapper.objectToSQL(idMsg);
    
    return S;
  }
  
}
