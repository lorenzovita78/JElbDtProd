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
public class MancantiVDLBean implements IBeanPersCRUD{
  
  public final static String TABNAME="ZDPVLM";

  public final static String VMDTRF="VMDTRF";
  public final static String VMCOMM="VMCOMM";
  public final static String VMLINP="VMLINP";
  
  public final static String VMCCON="VMCCON";
  public final static String VMDFAC="VMDFAC";
  public final static String VMDCDL="VMDCDL";
  
  public final static String VMNMAN="VMNMAN";
  
  public final static String VMNCOL="VMNCOL";
  
  public final static String VMLMDT="VMLMDT";
  
  
 
  private Date dataOraRif;
  private String codAzienda;
  private String descrStab;
  private String descrLinea;
  private Integer codLineaLog;
  
  private Integer commessa;
  private Integer nMancanti;
  private Integer nColli;
  
  
  public MancantiVDLBean(){
    
  }
  
  
  public MancantiVDLBean(Date dataOraRif, Integer commessa, Integer codLineaLog) {
    this.dataOraRif = dataOraRif;
    this.commessa = commessa;
    this.codLineaLog = codLineaLog;
    this.nMancanti=Integer.valueOf(0);
    this.nColli=Integer.valueOf(0);
  }

  public Date getDataOraRif() {
    return dataOraRif;
  }

  public void setDataOraRif(Date dataOraRif) {
    this.dataOraRif = dataOraRif;
  }

  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
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

  public String getCodAzienda() {
    return codAzienda;
  }

  public void setCodAzienda(String codAzienda) {
    this.codAzienda = codAzienda;
  }

  public String getDescrStab() {
    return descrStab;
  }

  public void setDescrStab(String descrStab) {
    this.descrStab = descrStab;
  }

  public Integer getCodLineaLog() {
    return codLineaLog;
  }

  public void setCodLineaLog(Integer codLineaLog) {
    this.codLineaLog = codLineaLog;
  }

  public Integer getnColli() {
    return nColli;
  }

  public void setnColli(Integer nColli) {
    this.nColli = nColli;
  }
  
  
  
  
  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
     Map fieldsValue=new HashMap();
    
    fieldsValue.put(VMCCON, this.codAzienda);
    fieldsValue.put(VMDFAC, this.descrStab);
    fieldsValue.put(VMDCDL, this.descrLinea);
    
    fieldsValue.put(VMNMAN, this.nMancanti);
    
    fieldsValue.put(VMLMDT, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(VMDTRF, this.dataOraRif);
    fieldsValue.put(VMCOMM, this.commessa);
    fieldsValue.put(VMLINP, this.codLineaLog);
    
    fieldsValue.put(VMCCON,this.codAzienda);
    fieldsValue.put(VMDFAC,this.descrStab);
    fieldsValue.put(VMDCDL, this.descrLinea);
    
    fieldsValue.put(VMNMAN, this.nMancanti);
    fieldsValue.put(VMNCOL, this.nColli);
    
    fieldsValue.put(VMLMDT, new Date());

    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    
    fieldsValue.put(VMDTRF, this.dataOraRif);
    fieldsValue.put(VMCOMM, this.commessa);
    fieldsValue.put(VMLINP, this.codLineaLog);
    
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
    l.add(VMDTRF);l.add(VMCOMM);l.add(VMLINP);
    l.add(VMCCON);l.add(VMDFAC);
    l.add(VMDCDL);l.add(VMNMAN);
    l.add(VMNCOL);l.add(VMLMDT);
    
    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(VMDTRF);l.add(VMCOMM);l.add(VMLINP);
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.TIMESTAMP);   //az
    types.add(Types.CHAR);        //commessa
    types.add(Types.INTEGER);     //centro di lavoro
    types.add(Types.CHAR);        //codice azienda
    types.add(Types.CHAR);        //descrizione stabilimento
    types.add(Types.CHAR);        //descrizione cdl
    types.add(Types.INTEGER);     //numero mancanti
    types.add(Types.INTEGER);     //numero colli
    
    types.add(Types.TIMESTAMP);   //data variazione
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    if(this.dataOraRif==null || this.commessa==null || this.codLineaLog==null || this.nMancanti==null)
      return Boolean.FALSE;
    
    return Boolean.TRUE;
  }
  
}
