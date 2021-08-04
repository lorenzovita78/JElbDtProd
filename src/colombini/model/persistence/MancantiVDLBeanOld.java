/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
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
public class MancantiVDLBeanOld implements IBeanPersCRUD{
  
  public final static String TABNAME="ZDPMVL";
//  public final static String TABFIELDS="ZACONO,ZACOMM,ZADTCO,ZAPLGR,ZATOTC,ZAPRDC,ZALMUT,ZALMDT";
//  
//  public final static String TABKEYFIELDS="ZACONO,ZACOMM,ZADTCO,ZAPLGR";
  
  
  public final static String ZMDTRF="ZMDTRF";
  public final static String ZMCOMM="ZMCOMM";
  public final static String ZMLINP="ZMLINP";
  public final static String ZMPLDE="ZMPLDE";
  public final static String ZMNMAN="ZMNMAN";
  public final static String ZMLMDT="ZMLMDT";
  
  public final static String ZMCONO="ZMCONO";
  
 
  private Date dataOraRif;
  private String commessa;
  private Integer codLinea;
  private String descrLinea;
  private String stabilimento;
  private Integer nMancanti;
  private String azienda;

  
  
  public MancantiVDLBeanOld(Date dataOraRif, String commessa, Integer codLinea) {
    this.dataOraRif = dataOraRif;
    this.commessa = commessa;
    this.codLinea = codLinea;
  }

  public Date getDataOraRif() {
    return dataOraRif;
  }

  public void setDataOraRif(Date dataOraRif) {
    this.dataOraRif = dataOraRif;
  }

  public String getCommessa() {
    return commessa;
  }

  public void setCommessa(String commessa) {
    this.commessa = commessa;
  }

  public Integer getCodLinea() {
    return codLinea;
  }

  public void setCodLinea(Integer codLinea) {
    this.codLinea = codLinea;
  }

  public String getDescrLinea() {
    return descrLinea;
  }

  public void setDescrLinea(String descrLinea) {
    this.descrLinea = descrLinea;
  }

  public Integer getnMancanti() {
    return nMancanti;
  }

  public void setnMancanti(Integer nMancanti) {
    this.nMancanti = nMancanti;
  }

  public String getStabilimento() {
    return stabilimento;
  }

  public void setStabilimento(String stabilimento) {
    this.stabilimento = stabilimento;
  }

//  @Override
//  public String toString() {
//    return "Stab : "+this.getStabilimento()+" - Linea : "+this.getDescrLinea()+" --> mancanti n."+this.getnMancanti().toString();
//  }
  
  
  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
     Map fieldsValue=new HashMap();
    

    fieldsValue.put(ZMPLDE, this.descrLinea);
    fieldsValue.put(ZMNMAN, this.nMancanti);
    fieldsValue.put(ZMLMDT, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    
    fieldsValue.put(ZMDTRF, this.dataOraRif);
    fieldsValue.put(ZMCOMM, this.commessa);
    fieldsValue.put(ZMLINP, this.codLinea);
    fieldsValue.put(ZMPLDE, this.descrLinea);
    fieldsValue.put(ZMNMAN, this.nMancanti);
    
    fieldsValue.put(ZMLMDT, new Date());

    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    
    fieldsValue.put(ZMDTRF, this.dataOraRif);
    fieldsValue.put(ZMCOMM, this.commessa);
    
    return fieldsValue;
  }

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
    List l=new ArrayList();
    l.add(ZMDTRF);l.add(ZMCOMM);l.add(ZMLINP);
    l.add(ZMPLDE);l.add(ZMNMAN);l.add(ZMLMDT);
    
    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(ZMDTRF);l.add(ZMCOMM);l.add(ZMLINP);
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.TIMESTAMP);   //az
    types.add(Types.CHAR);        //commessa
    types.add(Types.INTEGER);        //centro di lavoro
    types.add(Types.CHAR);        //descrizione cdl
    types.add(Types.INTEGER);     //numero mancanti
    
    types.add(Types.TIMESTAMP); //data variazione
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
}
