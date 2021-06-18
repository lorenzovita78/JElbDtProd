/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab;

import colombini.conn.ColombiniConnections;
import static colombini.model.persistence.tab.SfridiProdPersistence.TABKEYFIELDS;
import db.JDBCDataMapper;
import db.persistence.ITable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.ArrayUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class TabScaricoBndPanAs400 implements ITable {

  public final static String TABNAME="MMZ883PF";
  public final static String TABFIELDS="Z6PLGR,Z6ITNO,Z6TRQT,Z6UNMS,Z6RSCD,Z6RTOR,Z6DTRF,Z6SOTM,Z6STAT";
  public final static String KEYFIELDS="Z6PLGR,Z6DTRF,Z6SOTM";
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABNAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(StringUtils.split(TABFIELDS,",")); 
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(StringUtils.split(TABKEYFIELDS,","));
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    
    types.add(Types.CHAR);      //linea
    types.add(Types.CHAR);      //articolo
    
    types.add(Types.NUMERIC); //quantita
    
    types.add(Types.CHAR);    //unità di misura
    types.add(Types.CHAR);    //Lotto
    types.add(Types.CHAR);    //reason code
    
    types.add(Types.TIMESTAMP); //sovraproduzione
    
    types.add(Types.CHAR);
    types.add(Types.CHAR); //stato
    
    return types;
  }
 
  public static String getQryInfo (String cdl,Date dataRif){
    String select=" select distinct Z6SOTM,Z6DTRF from "+ColombiniConnections.getAs400LibPersColom()+"."+TABNAME+
                  " where 1=1"+
                  " and Z6PLGR="+JDBCDataMapper.objectToSQL(cdl)+
                  " and Z6DTRF >= "+JDBCDataMapper.objectToSQL(dataRif);
            
    return select;
  }
  
  
  public static List getListElmForAs400(String codCdl,String codMat,Double qta,String unM,String rCode,String lotto,Date dataRif,String codOtm){
   List l=new ArrayList();
   l.add(codCdl);
   l.add(codMat);
   l.add(qta);
   l.add(unM);
   l.add(rCode);
   l.add(lotto);
   l.add(dataRif);
   l.add(codOtm);
   l.add("");
   
   return l;
  }
  
}
