/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence.tab.R1;

import colombini.costant.ColomConnectionsCostant;
import db.ConnectionsProps;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class DtProdImaAnte implements ITable {

  public final static String TABNAME="ZDPBIA";
  public final static String TABFIELDS="ZACONO,ZADTRF,ZAPSTK,ZAPCOM,ZAPREI,ZAPSCA,ZAPCTL,ZAPCB1,ZAPCB2,ZAPSC1,ZAPSC2,"+
                                       "ZAS1B1,ZAS2B1,ZAS1B2,ZAS2B2,ZAS1B3,ZAS2B3,ZAS1B8,ZAS2B8,ZAS1G5,ZAS2G5,ZAS1S1,ZAS2S1,"+
                                       "ZAS1S2,ZAS2S2,ZAS1D0,ZAS2D0,ZAS1F1,ZAS2F1,ZAS1G4,ZAS2G4,ZAS1G8,ZAS2G8,"+
                                       "ZARGDT";
  
  public final static String TABKEYFIELDS="ZACONO, ZADTRF";
  
  public final static String ZACONO="ZACONO";
  public final static String ZADTRF="ZADTRF";
  public final static String ZAPSTK="ZAPSTK";
  public final static String ZAPCOM="ZAPCOM";
  public final static String ZAPREI="ZAPREI";
  public final static String ZAPSCA="ZAPSCA";
  public final static String ZAPCTL="ZAPCTL";
  public final static String ZAPCB1="ZAPCB1";
  public final static String ZAPCB2="ZAPCB2";
  public final static String ZAPSC1="ZAPSC1";
  public final static String ZAPSC2="ZAPSC2";
  
  public final static String ZAS1B1 ="ZAS1B1";
  public final static String ZAS2B1 ="ZAS2B1";
  public final static String ZAS1B2 ="ZAS1B2";
  public final static String ZAS2B2 ="ZAS2B2";
  public final static String ZAS1B3 ="ZAS1B3";
  public final static String ZAS2B3 ="ZAS2B3";
  public final static String ZAS1B8 ="ZAS1B8";
  public final static String ZAS2B8 ="ZAS2B8";
  public final static String ZAS1G5 ="ZAS1G5";
  public final static String ZAS2G5 ="ZAS2G5";
  public final static String ZAS1S1 ="ZAS1S1";
  public final static String ZAS2S1 ="ZAS2S1";
  public final static String ZAS1S2 ="ZAS1S2";
  public final static String ZAS2S2 ="ZAS2S2";
  public final static String ZAS1D0 ="ZAS1D0";
  public final static String ZAS2D0 ="ZAS2D0";
  public final static String ZAS1F1 ="ZAS1F1";
  public final static String ZAS2F1 ="ZAS2F1";
  public final static String ZAS1G4 ="ZAS1G4";
  public final static String ZAS2G4 ="ZAS2G4";
  public final static String ZAS1G8 ="ZAS1G8";
  public final static String ZAS2G8 ="ZAS2G8";
  
  public final static String ZARGDT="ZARGDT";

  @Override
  public String getLibraryName() {
    return (String) ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_LIB_PERS); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getTableName() {
   return TABNAME; //To change body of generated methods, choose Tools | Templates.
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
    
    types.add(Types.INTEGER);   
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.TIMESTAMP);
    
    
    return types;
  }
  
  
  public Map getEmptyMapFieds(Integer az,Long dtRf){
    Map map=new HashMap();
    
    map.put(ZACONO,az);
    map.put(ZADTRF,dtRf);
    map.put(ZAPSTK,Integer.valueOf(0));
    map.put(ZAPCOM,Integer.valueOf(0));
    map.put(ZAPREI,Integer.valueOf(0));
    map.put(ZAPSCA,Integer.valueOf(0));
    map.put(ZAPCTL,Integer.valueOf(0));
    map.put(ZAPCB1,Integer.valueOf(0));
    map.put(ZAPCB2,Integer.valueOf(0));
    map.put(ZAPSC1,Integer.valueOf(0));
    map.put(ZAPSC2,Integer.valueOf(0));
    
    map.put(ZAS1B1,Integer.valueOf(0));
    map.put(ZAS2B1,Integer.valueOf(0));
    map.put(ZAS1B2,Integer.valueOf(0));
    map.put(ZAS2B2,Integer.valueOf(0));
    map.put(ZAS1B3,Integer.valueOf(0));
    map.put(ZAS2B3,Integer.valueOf(0));
    map.put(ZAS1B8,Integer.valueOf(0));
    map.put(ZAS2B8,Integer.valueOf(0));
    map.put(ZAS1G5,Integer.valueOf(0));
    map.put(ZAS2G5,Integer.valueOf(0));
    map.put(ZAS1S1,Integer.valueOf(0));
    map.put(ZAS2S1,Integer.valueOf(0));
    map.put(ZAS1S2,Integer.valueOf(0));
    map.put(ZAS2S2,Integer.valueOf(0));
    map.put(ZAS1D0,Integer.valueOf(0));
    map.put(ZAS2D0,Integer.valueOf(0));
    map.put(ZAS1F1,Integer.valueOf(0));
	map.put(ZAS2F1,Integer.valueOf(0));
    map.put(ZAS1G4,Integer.valueOf(0));
	map.put(ZAS2G4,Integer.valueOf(0));
    map.put(ZAS1G8,Integer.valueOf(0));
    map.put(ZAS2G8,Integer.valueOf(0));
    
    map.put(ZARGDT,new Date());
    
    return map;
  }
  
  
  public Boolean isValidDt(Map values){
    Boolean valid=Boolean.TRUE;
    Integer pzS=values.get(ZAPSTK)!=null ? (Integer) values.get(ZAPSTK) :Integer.valueOf(0);
    Integer pzC=values.get(ZAPCOM)!=null ? (Integer) values.get(ZAPCOM) :Integer.valueOf(0);
    //verifichiamo se non sono stati prodotti ne pezzi a stock ne pezzi a commessa allora il dato non è interessante
    if(pzS==0 && pzC==0)
      valid=Boolean.FALSE;
    
    
    return valid;
  }
  
  
}
