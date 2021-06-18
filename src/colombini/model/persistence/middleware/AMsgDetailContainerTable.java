/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware;

import db.persistence.ITable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hsqldb.types.Types;

/**
 *
 * @author lvita
 */
public abstract class AMsgDetailContainerTable implements ITable {

  public abstract String getClmMessageId();
  public abstract String getClmDataType();
  public abstract String getClmAz();
  public abstract String getClmContainer();
  public abstract String getClmContainerDesc();
  public abstract String getClmContainerType();
  public abstract String getClmWeight();
  public abstract String getClmLenght();
  public abstract String getClmWidth();
  public abstract String getClmHeight();
  
  public abstract String getLibName();
  public abstract String getTabName();
  
  @Override
  public String getLibraryName() {
    return getLibName();
  }

  @Override
  public String getTableName() {
    return getTabName();
  }

   @Override
  public List<String> getFields() {
     return new ArrayList<String>(Arrays.asList(getClmMessageId(), getClmDataType(), getClmAz(), 
                                                getClmContainer(), getClmContainerDesc(), getClmContainerType(), 
                                                getClmWeight(),getClmLenght(),getClmWidth(),getClmHeight()));
  }

  @Override
  public List<String> getKeyFields() {
    return new ArrayList<String>(Arrays.asList(getClmMessageId()));
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
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
  
}
