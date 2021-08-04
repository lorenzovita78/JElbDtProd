/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.IBeanPersCRUD;

/**
 *
 * @author lvita
 */
public class ProdGgLinea implements IBeanPersCRUD{

  public final static String TABLENAME="ZDPDPR";
  
  
  public final static String DPCONO="DPCONO";
  public final static String DPDTPR="DPDTPR";
  public final static String DPTMPR="DPTMPR";
  public final static String DPPLGR="DPPLGR";
  public final static String DPPZPR="DPPZPR";
  public final static String DPSCPR="DPSCPR";
  public final static String DPLMDT="DPLMDT";
  
  private Integer azienda;
  private Date dataRif;
   
  private String centrodiLavoro;
  private Integer pzProdotti;
  private Integer pzScartati;

  public ProdGgLinea(Integer az,Date dataRif,String cdL){
    this.azienda=az;
    this.dataRif=dataRif;
    this.centrodiLavoro=cdL;
    
    pzProdotti=Integer.valueOf(0);
    pzScartati=Integer.valueOf(0);
  }
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public Date getDataRif() {
    return dataRif;
  }

  public void setDataRif(Date dataRif) {
    this.dataRif = dataRif;
  }

  public String getCentrodiLavoro() {
    return centrodiLavoro;
  }

  public void setCentrodiLavoro(String centrodiLavoro) {
    this.centrodiLavoro = centrodiLavoro;
  }

  public Integer getPzProdotti() {
    return pzProdotti;
  }

  public void setPzProdotti(Integer pzProdotti) {
    this.pzProdotti = pzProdotti;
  }

  public Integer getPzScartati() {
    return pzScartati;
  }

  public void setPzScartati(Integer pzScarti) {
    this.pzScartati = pzScarti;
  }
  
  public void addPzProdotti(Integer pz){
    this.pzProdotti+=pz;
  }
  
  public void addPzScartati(Integer pz){
    this.pzScartati+=pz;
  }
  
  
  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    Map fields=new HashMap();
    fields.put(DPPZPR, this.pzProdotti);
    fields.put(DPSCPR, this.pzScartati);
    
    fields.put(DPLMDT, new Date());
    
    return fields;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(DPCONO, this.azienda);
    fieldsValue.put(DPDTPR, this.dataRif);
    fieldsValue.put(DPTMPR, new Time(this.dataRif.getTime()));
    fieldsValue.put(DPPLGR, this.centrodiLavoro);
    fieldsValue.put(DPPZPR, this.pzProdotti);
    fieldsValue.put(DPSCPR, this.pzScartati);
    
    fieldsValue.put(DPLMDT, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fields=new HashMap();
    fields.put(DPCONO, this.azienda);
    fields.put(DPDTPR, this.dataRif);
    
    
    return fields;
  }

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
    List l=new ArrayList();
    l.add(DPCONO);l.add(DPDTPR);l.add(DPTMPR);
    l.add(DPPLGR);l.add(DPPZPR);l.add(DPSCPR);
    l.add(DPLMDT);

    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(DPCONO);l.add(DPDTPR);l.add(DPTMPR);l.add(DPPLGR);
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    
    types.add(Types.DATE); //dataRif
    types.add(Types.TIME); //oraRif
    
    types.add(Types.CHAR);      //centro di lavoro
    
    types.add(Types.INTEGER);    //pzPRod
    types.add(Types.INTEGER);    //pzSca
    
    types.add(Types.TIMESTAMP); //data variazione
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
}
