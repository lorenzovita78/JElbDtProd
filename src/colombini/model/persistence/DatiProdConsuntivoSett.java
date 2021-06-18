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
public class DatiProdConsuntivoSett implements IBeanPersSIMPLE{

  public final static String TABLENAME="PDZ844F";
  
  public final static String P6CONO ="P6CONO";
  public final static String P6ANNO ="P6ANNO";
  public final static String P6MESE ="P6MESE";
  public final static String P6SETT ="P6SETT";
  public final static String P6GGLV ="P6GGLV";  
  public final static String P6PLGR ="P6PLGR";   
  public final static String P6OREL ="P6OREL";
  public final static String P6PZLV ="P6PZLV";
  public final static String P6UNML ="P6UNML";
  public final static String P6ORBG ="P6ORBG";
  public final static String P6ORST ="P6ORST";
  public final static String P6ORFM ="P6ORFM";
  public final static String P6ORMC ="P6ORMC";
  public final static String P6OROP="P6OROP";
  public final static String P6DTIN ="P6DTIN";
  
  
  DatiConsuntivoMVX dtMvx=null;
  
  public DatiProdConsuntivoSett(DatiConsuntivoMVX dtMvx) {
    this.dtMvx=dtMvx;
  }
  
  @Override
  public String getLibraryName() {
    return  ColombiniConnections.getAs400LibPersColom(); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List fields=new ArrayList();
    fields.add(P6CONO);
    fields.add(P6ANNO);
    fields.add(P6MESE);
    fields.add(P6SETT);
    fields.add(P6GGLV);
    fields.add(P6PLGR);
    
    fields.add(P6OREL);
    fields.add(P6PZLV);
    fields.add(P6UNML);
    fields.add(P6ORBG);
    fields.add(P6ORST);
    fields.add(P6ORFM);
    fields.add(P6ORMC);
    fields.add(P6OROP);
    fields.add(P6DTIN);
    
    
    return fields; 
  }

  @Override
  public List<String> getKeyFields() {
    List fields=new ArrayList();
    fields.add(P6CONO);
    fields.add(P6ANNO);
    fields.add(P6MESE);
    fields.add(P6SETT);
    fields.add(P6PLGR);
    
    
    
    
    return fields; 
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.INTEGER);
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
    
    fieldValues.put(P6CONO,this.dtMvx.getAzienda());
    fieldValues.put(P6ANNO,this.dtMvx.getAnno());
    fieldValues.put(P6MESE,this.dtMvx.getMese());
    fieldValues.put(P6SETT,this.dtMvx.getSettimana());
    fieldValues.put(P6PLGR,this.dtMvx.getCdL());
    
    fieldValues.put(P6GGLV,this.dtMvx.getGiorniLav());
    fieldValues.put(P6OREL,this.dtMvx.getOreLav());
    fieldValues.put(P6PZLV,this.dtMvx.getPzProd());
    fieldValues.put(P6UNML,this.dtMvx.getUnMisura());
    fieldValues.put(P6ORBG,this.dtMvx.getOreBudget());
    fieldValues.put(P6ORST,this.dtMvx.getOreSetup());
    fieldValues.put(P6ORFM,this.dtMvx.getOreFermi());
    fieldValues.put(P6ORMC,this.dtMvx.getOreMicrofermi());
    fieldValues.put(P6OROP,this.dtMvx.getOrePersonale());
    
    fieldValues.put(P6DTIN,new Date());
    
    return fieldValues;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldValues=new HashMap();
    
    fieldValues.put(P6CONO,this.dtMvx.getAzienda());
    fieldValues.put(P6ANNO,this.dtMvx.getAnno());
    //fieldValues.put(P6MESE,this.dtMvx.getMese());
    fieldValues.put(P6SETT,this.dtMvx.getSettimana());
    //fieldValues.put(P6PLGR,this.dtMvx.getCdL());
    
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
    return this.dtMvx.getCdL()+" --> ANNO: "+ dtMvx.getAnno()+" - MESE: "+this.dtMvx.getMese()+" - SETT: "+dtMvx.getSettimana();
  }
  
  
  
  
}
