/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import db.Connections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import db.persistence.IBeanPersCRUD;

/**
 * Bean per gestire le informazioni relative ai pezzi/colli prodotti per una certa BU (divisione) 
 * relativamente ad una determinata commessa
 * @author lvita
 */
public class TempiCicloForBiesseG1 implements IBeanPersCRUD{

  private Integer azienda;
  private String  lineaLav;
  private String  codArt;
  private Date    dataRif;
  private Double  tempoCiclo;
  private Date    dataMod;
  
  private List<List> fermi;
  private Integer qta;
  private Long tempoFermiTot;
  private Long tempoMicrofermi;
  private Long tempoProdArt;
  
  public final static String TABNAME="ZTCFBG";
  public final static String TABFIELDS="ZTCONO,ZTPLGR,ZTITNO,ZTITTC,ZTDTRF,ZTLMDT";
  
  public final static String TABKEYFIELDS="ZTCONO,ZTPLGR,ZTITNO,ZTDTRF";
  
  
  public final static String ZTCONO="ZTCONO";
  public final static String ZTPLGR="ZTPLGR";
  public final static String ZTITNO="ZTITNO";
  public final static String ZTITTC="ZTITTC";
  public final static String ZTDTRF="ZTDTRF";
  public final static String ZTLMDT="ZTLMDT";

  
  public TempiCicloForBiesseG1(Integer azienda,String cdl,String codArt){
    this.azienda=azienda;
    this.lineaLav=cdl;
    this.codArt=codArt;
    this.fermi=new ArrayList();
    this.tempoFermiTot=Long.valueOf(0);
    this.tempoMicrofermi=Long.valueOf(0);
    this.tempoProdArt=Long.valueOf(0);
    this.qta=Integer.valueOf(0);
  }
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public String getLineaLav() {
    return lineaLav;
  }

  public void setLineaLav(String lineaLav) {
    this.lineaLav = lineaLav;
  }

  public String getCodArt() {
    return codArt;
  }

  public void setCodArt(String codArt) {
    this.codArt = codArt;
  }

  public Date getDataRif() {
    return dataRif;
  }

  public void setDataRif(Date dataRif) {
    this.dataRif = dataRif;
  }

  public Double getTempoCiclo() {
    return tempoCiclo;
  }

  public void setTempoCiclo(Double tempoCiclo) {
    this.tempoCiclo = tempoCiclo;
  }

  public Date getDataMod() {
    return dataMod;
  }

  public void setDataMod(Date dataMod) {
    this.dataMod = dataMod;
  }

  
  public List getFermi() {
    return fermi;
  }

  public void setFermi(List fermi) {
    this.fermi = fermi;
  }

  public Long getTempoFermiTot() {
    return tempoFermiTot;
  }

  public void setTempoFermiTot(Long tempoFermiTot) {
    this.tempoFermiTot = tempoFermiTot;
  }

  public Long getTempoMicrofermi() {
    return tempoMicrofermi;
  }

  public void setTempoMicrofermi(Long tempoMicrofermi) {
    this.tempoMicrofermi = tempoMicrofermi;
  }

  public Long getTempoProdArt() {
    return tempoProdArt;
  }

  public void setTempoProdArt(Long tempoProdArt) {
    this.tempoProdArt = tempoProdArt;
  }

  public Integer getQta() {
    return qta;
  }

  public void setQta(Integer qta) {
    this.qta = qta;
  }

  
  
  /**
   * Aggiunge alla lista di fermi l'informazione del fermo.
   * infoFermo dovrà contenere Causale,DataInizio e durata
   * @param infoFermo 
   */
  public void addFermo(List infoFermo) {
    if(infoFermo==null)
      return;
    
    this.fermi.add(infoFermo);
    
    addTempoFermi(ClassMapper.classToClass(infoFermo.get(2),Long.class));
  }
  
  
  public void addTempoFermi(Long f){
    this.tempoFermiTot+=f;
  }
  
  public void addTempoMicroFermi(Long f){
    this.tempoMicrofermi+=f;
  }
  
  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZTCONO, this.azienda);
    fieldsValue.put(ZTPLGR, this.lineaLav);
    fieldsValue.put(ZTITNO, this.codArt);
    fieldsValue.put(ZTITTC, this.tempoCiclo);
    fieldsValue.put(ZTDTRF, this.dataRif);
    fieldsValue.put(ZTLMDT, this.dataMod);
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZTCONO, this.azienda);
    fieldsValue.put(ZTPLGR, this.lineaLav);
    fieldsValue.put(ZTITNO, this.codArt);
    fieldsValue.put(ZTDTRF, this.dataRif);
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZTITTC, this.tempoCiclo);
    fieldsValue.put(ZTLMDT, new Date());
    return fieldsValue;
  }
  
  
  @Override
  public Boolean validate() {
    if(tempoCiclo<0 || tempoCiclo>1000)
      return Boolean.FALSE;
    
    return Boolean.TRUE; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getLibraryName() {
    return Connections.getInstance().getLibraryPersAs400();
  }

  @Override
  public String getTableName() {
    return TABNAME;
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
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //centro di lavoro
    types.add(Types.CHAR);      //codArticolo
    types.add(Types.NUMERIC);    //tempoCiclo
    //types.add(Types.DATE);
    types.add(Types.TIMESTAMP);//data riferimento 
    types.add(Types.TIMESTAMP);    //data modifica
    
    
    return types;
  }

  
//  public boolean isExists(Connection con){
//    String qry=" Select "
//  }
  
  public List getInfoList(){
    List info=new ArrayList();
    info.add(codArt);
    info.add(qta);
    info.add(DateUtils.dateToStr(dataRif,"dd/MM/yyyy HH:mm:ss"));
    info.add(tempoProdArt);
    info.add(tempoCiclo);
    info.add(tempoFermiTot);
    info.add(tempoMicrofermi);
    
    StringBuilder dettFermi=new StringBuilder();
    for (List fermo:fermi){
      dettFermi.append(ClassMapper.classToString(fermo.get(0)));
      dettFermi.append(" : ");
      dettFermi.append(ClassMapper.classToString(fermo.get(2)));
      dettFermi.append("; ");
    }
    info.add(dettFermi.toString());
    
    return info;
  }
  
}
