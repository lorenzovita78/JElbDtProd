/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab.R1;

import as400.persistence.As400Persistence;
import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import utils.ArrayUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class DtProdImaTopGg extends As400Persistence {

  public final static String TABNAME="ZDPTPD";
  public final static String TABFIELDS="ZTCONO, ZTDTRF, ZTPTOT, ZTPFDF, ZTPFSP, ZTPPNT, ZTPSPL, ZTRGUT, ZTRGDT, ZTRGOR";
  public final static String TABKEYFIELDS="ZTCONO, ZTDTRF";
  
  public DtProdImaTopGg() {
    fields=ArrayUtils.getListFromArray(StringUtils.split(TABFIELDS,","));
    keyFields=ArrayUtils.getListFromArray(StringUtils.split(TABKEYFIELDS,","));
    fieldsType=getFieldsType();
    table=TABNAME;
    library=ColombiniConnections.getAs400LibPersColom();
  }
  
  
  private List<Integer> getFieldsType(){
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    
    
    return types;
  }
  
 
  
}
