/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware;

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
 * Dati relativi al dettaglio del container (dati anagrafici)
 * @author lvita
 */
public class MsgAs400Dett_ToVdl implements ITable{

  public final static String TABLE_NAME="ZVAOPA";
  public final static String FIELDS="ZPIDMS, ZPOPTN, ZPCONO, ZPPACT, ZPPANM, ZPPACK, ZPVOMT, ZPPACL, ZPPACW, ZPPACH";
  
  
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
    return Arrays.asList("ZPIDMS");
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    
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
