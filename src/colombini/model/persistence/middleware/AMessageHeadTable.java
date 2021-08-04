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
 * Classe astratta che identifica la struttura della tabella di testata dei messaggi
 * Tale tabella nelle sue declinazioni è presente sia su As400 che su Vdl
 * 
 * @author lvita
 */
public abstract class AMessageHeadTable implements ITable {

  
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
     return new ArrayList<String>(Arrays.asList(getClmMessageId(), getClmStatus(), getClmMsgObjType(), 
                                                getClmInsertTime(), getClmCompletionTime(), getClmRjctMsgId(), getClmRjctMsgInfo()));
  }

  @Override
  public List<String> getKeyFields() {
    return new ArrayList<String>(Arrays.asList(getClmMessageId()));
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.NUMERIC);
    types.add(Types.VARCHAR);
    types.add(Types.VARCHAR);
    types.add(Types.TIMESTAMP);
    types.add(Types.TIMESTAMP);
    types.add(Types.NUMERIC);
    types.add(Types.VARCHAR);
    
    return types;
  }
  
  
  public abstract String getClmMessageId();
  public abstract String getClmStatus();
  public abstract String getClmMsgObjType();
  public abstract String getClmInsertTime();
  public abstract String getClmCompletionTime();
  public abstract String getClmRjctMsgId();
  public abstract String getClmRjctMsgInfo();
  
  public abstract String getLibName();
  public abstract String getTabName();
  
  
}
