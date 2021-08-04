/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls;

/**
 *
 * @author lvita
 */
public class StrutturaFileXlsDatiProd {
 
  public final static String ORE="O";
  public final static String MINUTI="M";
  
  
  public final static String TSN="0:00 - 6:00";
  public final static String TMT="6:00 - 13:15";
  public final static String TPM="13:15 - 20:30";
  public final static String TMN="20:30 - 23:59";
  
  
  private String stabilimento;
  private String piano;
  private String codLinea;
  private String pathFileC;
  
  private Integer righeGg;
  private Integer colOraIniTurnoDip;
  private Integer colOraFinTurnoDip;
  private Integer colOreStraordinario;
  private Integer colOraAperturaImp;
  private Integer colOraChiusuraImp;
//  private String  tipoOreProdImp;
  private Integer colOreImprodIni;
  private Integer colOreImprodFn;
  private String  tipoOreImprodImp;
  private Integer colPzProd;
  private Integer colPzProd2;
  private Integer colIniFermi;
  private Integer colFinFermi;
  private Integer colIniSetup;
  private Integer colFinSetup;
  private Integer colIniMicroF;
  private Integer colFinMicroF;

  private Double fattMoltMicroF;   
  
  
  
  
  public StrutturaFileXlsDatiProd(){
    
  }
  
  
  public String getCodLinea() {
    return codLinea;
  }

  public void setCodLinea(String codLinea) {
    this.codLinea = codLinea;
  }

  public Integer getColFinFermi() {
    return colFinFermi;
  }

  public void setColFinFermi(Integer colFinFermi) {
    this.colFinFermi = colFinFermi;
  }

  public Integer getColFinMicroF() {
    return colFinMicroF;
  }

  public void setColFinMicroF(Integer colFinMicroF) {
    this.colFinMicroF = colFinMicroF;
  }

  public Integer getColFinSetup() {
    return colFinSetup;
  }

  public void setColFinSetup(Integer colFinSetup) {
    this.colFinSetup = colFinSetup;
  }

  public Integer getColIniFermi() {
    return colIniFermi;
  }

  public void setColIniFermi(Integer colIniFermi) {
    this.colIniFermi = colIniFermi;
  }

  public Integer getColIniMicroF() {
    return colIniMicroF;
  }

  public void setColIniMicroF(Integer colIniMicroF) {
    this.colIniMicroF = colIniMicroF;
  }

  public Integer getColIniSetup() {
    return colIniSetup;
  }

  public void setColIniSetup(Integer colIniSetup) {
    this.colIniSetup = colIniSetup;
  }

  public Integer getColOraAperturaImp() {
    return colOraAperturaImp;
  }

  public void setColOraAperturaImp(Integer colOraAperturaImp) {
    this.colOraAperturaImp = colOraAperturaImp;
  }

  public Integer getColOraChiusuraImp() {
    return colOraChiusuraImp;
  }

  public void setColOraChiusuraImp(Integer colOraChiusuraImp) {
    this.colOraChiusuraImp = colOraChiusuraImp;
  }

  
  public Integer getColOraFinTurnoDip() {
    return colOraFinTurnoDip;
  }

  public void setColOraFinTurnoDip(Integer colOraFinTurnoDip) {
    this.colOraFinTurnoDip = colOraFinTurnoDip;
  }


  public Integer getColOraIniTurnoDip() {
    return colOraIniTurnoDip;
  }

  public void setColOraIniTurnoDip(Integer colOraIniTurnoDip) {
    this.colOraIniTurnoDip = colOraIniTurnoDip;
  }

  public Integer getColOreImprodFn() {
    return colOreImprodFn;
  }

  public void setColOreImprodFn(Integer colOreImprodFn) {
    this.colOreImprodFn = colOreImprodFn;
  }

  public Integer getColOreImprodIni() {
    return colOreImprodIni;
  }

  public void setColOreImprodIni(Integer colOreImprodIni) {
    this.colOreImprodIni = colOreImprodIni;
  }

  public Integer getColOreStraordinario() {
    return colOreStraordinario;
  }

  public void setColOreStraordinario(Integer colOreStraordinario) {
    this.colOreStraordinario = colOreStraordinario;
  }

  public Integer getColPzProd() {
    return colPzProd;
  }

  public void setColPzProd(Integer colPzProd) {
    this.colPzProd = colPzProd;
  }

  public Integer getColPzProd2() {
    return colPzProd2;
  }

  public void setColPzProd2(Integer colPzProd2) {
    this.colPzProd2 = colPzProd2;
  }

  public String getPathFileC() {
    return pathFileC;
  }

  public void setPathFileC(String pathFileC) {
    this.pathFileC = pathFileC;
  }

  public String getPiano() {
    return piano;
  }

  public void setPiano(String piano) {
    this.piano = piano;
  }

  public Integer getRigheGg() {
    return righeGg;
  }

  public void setRigheGg(Integer righeGg) {
    this.righeGg = righeGg;
  }

  public String getStabilimento() {
    return stabilimento;
  }

  public void setStabilimento(String stabilimento) {
    this.stabilimento = stabilimento;
  }

  public String getTipoOreImprodImp() {
    return tipoOreImprodImp;
  }

  public void setTipoOreImprodImp(String tipoOreImprodImp) {
    this.tipoOreImprodImp = tipoOreImprodImp;
  }

//  public String getTipoOreProdImp() {
//    return tipoOreProdImp;
//  }
//
//  public void setTipoOreProdImp(String tipoOreProdImp) {
//    this.tipoOreProdImp = tipoOreProdImp;
//  }

  public Double getFattMoltMicroF() {
    return fattMoltMicroF;
  }

  public void setFattMoltMicroF(Double fattMoltMicroF) {
    this.fattMoltMicroF = fattMoltMicroF;
  }

  
  
  //per elaborazione di test
  /**
   * Torna la colonna relativa al nome dell'operatore
   * @return 
   */
  public Integer getColOperatore(){
    return (this.colOraIniTurnoDip-1);
  }
  
  public Integer getColTurno(){
    return (this.colOraIniTurnoDip-2);
  } 
   
   
}
