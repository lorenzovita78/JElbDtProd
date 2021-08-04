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
public class DtProdSSImaAnte extends As400Persistence {

  public final static String TABNAME="ZDPSSA";
  public final static String TABFIELDS="ZSCONO,ZSDTRF,ZSORRF,ZSCOMM,ZSBOXS,ZSPLCN,ZSITNO,ZSDCRN,ZSNUMP,ZSRGUT,ZSRGDT,ZSRGOR";
  public final static String TABKEYFIELDS="ZSCONO,ZSDTRF,ZSORRF";
  
  public DtProdSSImaAnte() {
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
    types.add(Types.INTEGER);//commessa
    types.add(Types.CHAR);//box
    types.add(Types.INTEGER);//placenumber
    types.add(Types.CHAR);//item
    types.add(Types.CHAR);//desc crane
    types.add(Types.INTEGER);//numPz
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    
    return types;
  }
  
 
  
}
