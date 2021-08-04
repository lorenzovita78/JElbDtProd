/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
public class DtMagReintetroImaAnte implements ITable{

  public final static String TABLENAME="ZIMRIA";
  
  public final static String FIELDS="MRTIPO,MRDTRF,MRBARC,MRITNO,MRITDS,MRBNDC,MRLNGT,MRWDTH,MRRGDT";

  public final static String  MRTIPO="MRTIPO";
  public final static String  MRDTRF="MRDTRF";
  
  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom(); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getTableName() {
    return TABLENAME; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(FIELDS.split(",")); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(MRTIPO);l.add(MRDTRF);
    
    return l; 
  }

  @Override
  public List<Integer> getFieldTypes() {
    List l=new ArrayList();
    l.add(Types.CHAR);
    l.add(Types.DATE);
    
    l.add(Types.CHAR);
    l.add(Types.CHAR);
    l.add(Types.CHAR);
    l.add(Types.CHAR);
    
    l.add(Types.NUMERIC);
    l.add(Types.NUMERIC);
    
    l.add(Types.TIMESTAMP);
    
    return l;
  }
  
}
