/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.old;

import db.JDBCDataMapper;
import db.persistence.ITable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hsqldb.types.Types;
import utils.ArrayUtils;

/**
 *
 * @author lvita
 */
public class MsgVdlDett_V2H implements ITable{

  public final static String TABLENAME="V2H_RepackingContainerData";
  public final static String FIELDS="MESSAGEID,CONTNO,CONTCODE,CONTNAME,TOTALWEIGHT,CONTLENGTH,CONTWIDTH,CONTHEIGHT,COLLOID,ORDERNO,COMPANY,TRUCKLOADNUMBER,LASTRECORD";
  
  @Override
  public String getLibraryName() {
    return "";
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(FIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return Arrays.asList("MESSAGEID");
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.NUMERIC);
    types.add(Types.VARCHAR);
    types.add(Types.VARCHAR);
    types.add(Types.VARCHAR);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.VARCHAR);
    
    return types;
  }
 
  public String getSqlForReadMessage(BigDecimal idMsg){
    String S=" select "+FIELDS+
            "\n from "+getTableName()+
            "\n where 1=1 "+
            " and MESSAGEID="+JDBCDataMapper.objectToSQL(idMsg);
    
    return S;
  }
  
}
