/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

import java.util.Date;
import utils.DateUtils;

/**
 * Classe che contiene le informazioni di base per poter caricare le informazioni sulla produzione 
 * a commessa di una linea lavorativa .
 * La classe è una mappatura dell'archivio <ZDPLEL>
 * @author lvita
 */
public class LineaLavBean {
  
  public final static String UMPEZZI="PZ";//pezzi
  public final static String UMCOLLI="CL";//colli
  public final static String UMVOL="V3";//elementi per M3
  
  private Integer azienda;
  
  private Integer commessa;
  private String commessaString;
  private Long   dataCommessa;
  private String codLineaLav;
  private String descLineaLav;
  private String unitaMisura;
  
  private Double numOpeTurno;
  private Double numOreStd;
  private Double numTurni;
  private Double oreFlexGg;
  private Double oreFlexTrn;
  
  private Double cadenzaOraria;
  private Integer allineamentoCom;
  private Double anticipoComm;
  
  private Integer flagL;
  
  
  // entrambe potrebbero essere non valorizzati-> unità di misura scatena la classe da eseguire
  private String queryCondition;
  private String classExec;

  private Integer flagAvP;
  private String classExecAvP;
  
  public LineaLavBean(){
    this.flagL=Integer.valueOf(0);
    this.flagAvP=Integer.valueOf(0);
    this.allineamentoCom=Integer.valueOf(0);
    
  }
  
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public String getClassExec() {
    return classExec;
  }

  public void setClassExec(String classExec) {
    this.classExec = classExec;
  }

  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
    
    setCommessaString(commessa.toString());
    if(commessa<100){
      setCommessaString("0"+commessa.toString());
    }
  }

  public String getCodLineaLav() {
    return codLineaLav;
  }

  public void setCodLineaLav(String codLineaLav) {
    this.codLineaLav = codLineaLav;
  }

  public String getDescLineaLav() {
    return descLineaLav;
  }

  public void setDescLineaLav(String descLineaLav) {
    this.descLineaLav = descLineaLav;
  }

  public Double getNumOpeTurno() {
    return numOpeTurno;
  }

  public void setNumOpeTurno(Double numOpeTurno) {
    this.numOpeTurno = numOpeTurno;
  }
   
  public Double getNumOreStd() {
    return numOreStd;
  }

  public Double getNumTurni() {
    return numTurni;
  }

  public void setNumTurni(Double numTurni) {
    this.numTurni = numTurni;
  }
  
  
  public void setNumOreStd(Double numOreStd) {
    this.numOreStd = numOreStd;
  }

  public Double getOreFlexGg() {
    return oreFlexGg;
  }

  public void setOreFlexGg(Double oreFlexGg) {
    this.oreFlexGg = oreFlexGg;
  }

  public Double getOreFlexTrn() {
    return oreFlexTrn;
  }

  public void setOreFlexTrn(Double oreFlexTrn) {
    this.oreFlexTrn = oreFlexTrn;
  }
 
  public Integer getAllineamentoCom() {
    return allineamentoCom;
  }
  
  public Double getCadenzaOraria() {
    return cadenzaOraria;
  }

  public void setCadenzaOraria(Double cadenzaOraria) {
    this.cadenzaOraria = cadenzaOraria;
  }

  
  public void setAllineamentoCom(Integer allineamentoCom) {
    this.allineamentoCom = allineamentoCom;
  }
  
  public Double getAnticipoComm() {
    return anticipoComm;
  }

  public void setAnticipoComm(Double anticipoComm) {
    this.anticipoComm = anticipoComm;
  }

  public String getQueryCondition() {
    return queryCondition;
  }

  public void setQueryCondition(String queryCondition) {
    this.queryCondition = queryCondition;
  }

  public String getUnitaMisura() {
    return unitaMisura;
  }

  public void setUnitaMisura(String unitaMisura) {
    this.unitaMisura = unitaMisura;
  }

  public Long getDataCommessa() {
    return dataCommessa;
  }

  public void setDataCommessa(Long dataCommessa) {
    this.dataCommessa = dataCommessa;
  }


  public Date getDateCommessa(){
    return DateUtils.strToDate(dataCommessa.toString(), "yyyyMMdd");
  }
  
  public Integer getFlagL() {
    return flagL;
  }

  public void setFlagL(Integer flagL) {
    this.flagL = flagL;
  }
  
  
  public Boolean isUMPezzi(){
    if(UMPEZZI.equals(unitaMisura))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  public Boolean isStandard(){
    if(classExec==null || classExec.isEmpty())
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }

  public String getCommessaString() {
    return commessaString;
  }

  private void setCommessaString(String commessaString) {
    this.commessaString = commessaString;
  }

  public Integer getFlagAvP() {
    return flagAvP;
  }

  public void setFlagAvP(Integer flagAvP) {
    this.flagAvP = flagAvP;
  }

  public String getClassExecAvP() {
    return classExecAvP;
  }

  public void setClassExecAvP(String classExecAvP) {
    this.classExecAvP = classExecAvP;
  }

  
  
  
  
}
