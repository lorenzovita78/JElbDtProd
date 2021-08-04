/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
import colombini.model.DatiConsuntivoMVX;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class DatiProdConsuntivoMese  implements IBeanPersSIMPLE {

  public final static String TABLENAME="PDZ854F";
  public final static String P7CONO ="P7CONO";
  public final static String P7ANNO ="P7ANNO";
  public final static String P7MESE ="P7MESE";
  public final static String P7GGLV ="P7GGLV";  
  public final static String P7PLGR ="P7PLGR";   
  public final static String P7OREL ="P7OREL";
  public final static String P7PZLV ="P7PZLV";
  public final static String P7UNML ="P7UNML";
  public final static String P7ORBG ="P7ORBG";
  public final static String P7ORST ="P7ORST";
  public final static String P7ORFM ="P7ORFM";
  public final static String P7ORMC ="P7ORMC";
  public final static String P7OROP ="P7OROP";
  public final static String P7DTIN ="P7DTIN";

  
  DatiConsuntivoMVX dtMvx=null;
  
  public DatiProdConsuntivoMese(DatiConsuntivoMVX dtMvx) {
    this.dtMvx=dtMvx;
  }
  
  
  @Override
  public String getLibraryName() {
    return  ColombiniConnections.getAs400LibPersColom();  //"LVITA";//
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List fields=new ArrayList();
    fields.add(P7CONO);
    fields.add(P7ANNO);
    fields.add(P7MESE);
    fields.add(P7GGLV);
    fields.add(P7PLGR);
    
    fields.add(P7OREL);
    fields.add(P7PZLV);
    fields.add(P7UNML);
    fields.add(P7ORBG);
    fields.add(P7ORST);
    fields.add(P7ORFM);
    fields.add(P7ORMC);
    fields.add(P7OROP);
    fields.add(P7DTIN);
    
    
    return fields; 
  }

  @Override
  public List<String> getKeyFields() {
    List fields=new ArrayList();
    fields.add(P7CONO);
    fields.add(P7ANNO);
    fields.add(P7MESE);
    fields.add(P7PLGR);
    
    
    
    return fields; 
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);
    
    types.add(Types.TIMESTAMP);
    
    return types;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldValues=new HashMap();
    
    fieldValues.put(P7CONO,this.dtMvx.getAzienda());
    fieldValues.put(P7ANNO,this.dtMvx.getAnno());
    fieldValues.put(P7MESE,this.dtMvx.getMese());
    fieldValues.put(P7PLGR,this.dtMvx.getCdL());
    
    fieldValues.put(P7GGLV,this.dtMvx.getGiorniLav());
    fieldValues.put(P7OREL,this.dtMvx.getOreLav());
    fieldValues.put(P7PZLV,this.dtMvx.getPzProd());
    fieldValues.put(P7UNML,this.dtMvx.getUnMisura());
    fieldValues.put(P7ORBG,this.dtMvx.getOreBudget());
    fieldValues.put(P7ORST,this.dtMvx.getOreSetup());
    fieldValues.put(P7ORFM,this.dtMvx.getOreFermi());
    fieldValues.put(P7ORMC,this.dtMvx.getOreMicrofermi());
    
    fieldValues.put(P7OROP,this.dtMvx.getOrePersonale());
    
    fieldValues.put(P7DTIN,new Date());
    
    return fieldValues;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldValues=new HashMap();
    
    fieldValues.put(P7CONO,this.dtMvx.getAzienda());
    fieldValues.put(P7ANNO,this.dtMvx.getAnno());
    fieldValues.put(P7MESE,this.dtMvx.getMese());
    //fieldValues.put(P7PLGR,this.dtMvx.getCdL());
    
    return fieldValues;
  }

  @Override
  public Boolean validate() {
    if(this.dtMvx.getOreLav()>0){
      return Boolean.TRUE;
    }
      
    return Boolean.FALSE;
  }
  
  
  @Override
  public String toString() {
    return this.dtMvx.getCdL()+" --> ANNO: "+ dtMvx.getAnno()+" - MESE: "+this.dtMvx.getMese();
  }
  
}
