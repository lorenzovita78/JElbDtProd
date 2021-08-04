/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence.tab.G1;

import colombini.costant.ColomConnectionsCostant;
import db.ConnectionsProps;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class DtScartiGalP2 implements ITable{

  public final static String TABNAME="ZDPSG1";
  
  
  
  
  public final static String PREFIX="ZGPS";
  
  public final static String ZGCONO="ZGCONO";
  public final static String ZGDTRF="ZGDTRF";
  public final static String ZGSCTP="ZGSCTP";
  public final static String ZGPZPR="ZGPZPR";
  public final static String ZGPSG1="ZGPSG1";
  public final static String ZGPSG2="ZGPSG2";
  public final static String ZGPSG3="ZGPSG3";
  public final static String ZGPSH3="ZGPSH3";
  public final static String ZGPSH2="ZGPSH2";
  public final static String ZGPSH4="ZGPSH4";
  public final static String ZGPSB0="ZGPSB0";
  public final static String ZGPSB3="ZGPSB3";
  public final static String ZGPSG9="ZGPSG9";
  public final static String ZGPSA2="ZGPSA2";
  public final static String ZGPSF1="ZGPSF1";
  public final static String ZGPSS0="ZGPSS0";
  public final static String ZGPSS4="ZGPSS4";
  public final static String ZGPSA9="ZGPSA9";
  public final static String ZGPSA1="ZGPSA1";
  public final static String ZGPSA4="ZGPSA4";
  public final static String ZGPSA5="ZGPSA5";
  public final static String ZGPSA6="ZGPSA6";
  public final static String ZGPSB9="ZGPSB9";
  public final static String ZGPSD0="ZGPSD0";
  public final static String ZGPSD1="ZGPSD1";
  public final static String ZGPSE4="ZGPSE4";
  public final static String ZGPSE6="ZGPSE6";
  public final static String ZGPSF0="ZGPSF0";
  public final static String ZGPSH1="ZGPSH1";
  public final static String ZGPSM0="ZGPSM0";
  public final static String ZGPSM1="ZGPSM1";
  public final static String ZGPSM2="ZGPSM2";
  public final static String ZGPSM3="ZGPSM3";
  public final static String ZGPSM4="ZGPSM4";
  public final static String ZGPSM5="ZGPSM5";
  public final static String ZGPSM9="ZGPSM9";
  public final static String ZGPSN1="ZGPSN1";
  public final static String ZGPSN2="ZGPSN2";
  public final static String ZGPST1="ZGPST1";
  public final static String ZGPST0="ZGPST0";
  public final static String ZGPST2="ZGPST2";
  public final static String ZGPST4="ZGPST4";
  public final static String ZGRGDT="ZGRGDT";
  
  
  @Override
  public String getLibraryName() {
     return (String) ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_LIB_PERS);
  }

  @Override
  public String getTableName() {
    return TABNAME; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getFields() {
    List list =new ArrayList();
    
    list.add(ZGCONO);
    list.add(ZGDTRF);
    list.add(ZGSCTP);
    list.add(ZGPZPR);
    list.add(ZGPSG1);
    list.add(ZGPSG2);
    list.add(ZGPSG3);
    list.add(ZGPSH3);
    list.add(ZGPSH2);
    list.add(ZGPSH4);
    list.add(ZGPSB0);
    list.add(ZGPSB3);
    list.add(ZGPSG9);
    list.add(ZGPSA2);
    list.add(ZGPSF1);
    list.add(ZGPSS0);
    list.add(ZGPSS4);
    list.add(ZGPSA9);
    list.add(ZGPSA1);
    list.add(ZGPSA4);
    list.add(ZGPSA5);
    list.add(ZGPSA6);
    list.add(ZGPSB9);
    list.add(ZGPSD0);
    list.add(ZGPSD1);
    list.add(ZGPSE4);
    list.add(ZGPSE6);
    list.add(ZGPSF0);
    list.add(ZGPSH1);
    list.add(ZGPSM0);
    list.add(ZGPSM1);
    list.add(ZGPSM2);
    list.add(ZGPSM3);
    list.add(ZGPSM4);
    list.add(ZGPSM5);
    list.add(ZGPSM9);
    list.add(ZGPSN1);
    list.add(ZGPSN2);
    list.add(ZGPST1);
    list.add(ZGPST0);
    list.add(ZGPST2);
    list.add(ZGPST4);
    list.add(ZGRGDT);
    
    return list;
  }

  @Override
  public List<String> getKeyFields() {
    List keyF=new ArrayList();
    keyF.add(ZGCONO);
    keyF.add(ZGDTRF);
    
    return keyF;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
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
  
  
  
  public Map getEmptyMapFieds(Integer az,Long dtRf,String tipo,Long pzProd){
    Map map=new HashMap();
    
    map.put(ZGCONO,az);
    map.put(ZGDTRF,dtRf);
    map.put(ZGSCTP,tipo);
    map.put(ZGRGDT,new Date());
    
    map.put(ZGPZPR,pzProd);
    map.put(ZGPSG1,Integer.valueOf(0));
    map.put(ZGPSG2,Integer.valueOf(0));
    map.put(ZGPSG3,Integer.valueOf(0));
    map.put(ZGPSH3,Integer.valueOf(0));
    map.put(ZGPSH2,Integer.valueOf(0));
    map.put(ZGPSH4,Integer.valueOf(0));
    map.put(ZGPSB0,Integer.valueOf(0));
    map.put(ZGPSB3,Integer.valueOf(0));
    map.put(ZGPSG9,Integer.valueOf(0));
    map.put(ZGPSA2,Integer.valueOf(0));
    map.put(ZGPSF1,Integer.valueOf(0));
    map.put(ZGPSS0,Integer.valueOf(0));
    map.put(ZGPSS4,Integer.valueOf(0));
    map.put(ZGPSA9,Integer.valueOf(0));
    map.put(ZGPSA1,Integer.valueOf(0));
    map.put(ZGPSA4,Integer.valueOf(0));
    map.put(ZGPSA5,Integer.valueOf(0));
    map.put(ZGPSA6,Integer.valueOf(0));
    map.put(ZGPSB9,Integer.valueOf(0));
    map.put(ZGPSD0,Integer.valueOf(0));
    map.put(ZGPSD1,Integer.valueOf(0));
    map.put(ZGPSE4,Integer.valueOf(0));
    map.put(ZGPSE6,Integer.valueOf(0));
    map.put(ZGPSF0,Integer.valueOf(0));
    map.put(ZGPSH1,Integer.valueOf(0));
    map.put(ZGPSM0,Integer.valueOf(0));
    map.put(ZGPSM1,Integer.valueOf(0));
    map.put(ZGPSM2,Integer.valueOf(0));
    map.put(ZGPSM3,Integer.valueOf(0));
    map.put(ZGPSM4,Integer.valueOf(0));
    map.put(ZGPSM5,Integer.valueOf(0));
    map.put(ZGPSM9,Integer.valueOf(0));
    map.put(ZGPSN1,Integer.valueOf(0));
    map.put(ZGPSN2,Integer.valueOf(0));
    map.put(ZGPST1,Integer.valueOf(0));
    map.put(ZGPST0,Integer.valueOf(0));
    map.put(ZGPST2,Integer.valueOf(0));
    map.put(ZGPST4,Integer.valueOf(0));
    

    return map;
  }
  
  
}
