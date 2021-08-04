/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence.tab;

import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class RitardoProdLinee implements ITable{

  public final static String TABLENAME="ZDPRPR";
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List fields=getKeyFields();
    
    fields.add("DRPZTP");
    fields.add("DRLMDT");
 
           
            
    return fields;
  }

  @Override
  public List<String> getKeyFields() {
    List fields=new ArrayList();
    fields.add("DRCONO");
    fields.add("DRDTRF");
    fields.add("DRPLGR");
    
    
    return fields;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.INTEGER);
    types.add(Types.DATE);
    types.add(Types.CHAR);
    
    types.add(Types.INTEGER);
    
    types.add(Types.TIMESTAMP);
    
    return types;
    
  }
  
}
