/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

/**
 *
 * @author lvita
 */
public class DatiConsuntivoMVX {
  
  private Integer azienda;
  private String cdL;
  private String unMisura;
  
  private Integer anno;
  private Integer mese;
  private Integer settimana;
  private Integer giorniLav;
  
  private Double oreLav;
  private Double pzProd;
  private Double oreBudget;
  
  private Double oreSetup;
  private Double oreFermi;
  private Double oreMicrofermi;
  
  private Double orePersonale;

  
  public DatiConsuntivoMVX(Integer azienda, String cdL, String unMisura) {
    this.azienda = azienda;
    this.cdL = cdL;
    this.unMisura = unMisura;

    this.oreLav=Double.valueOf(0);
    this.pzProd=Double.valueOf(0);
    this.oreBudget=Double.valueOf(0);
    this.oreSetup=Double.valueOf(0);
    this.oreFermi=Double.valueOf(0);
    this.oreMicrofermi=Double.valueOf(0);
    this.orePersonale=Double.valueOf(0);
    
  }

  public Integer getAzienda() {
    return azienda;
  }

  public String getCdL() {
    return cdL;
  }
  
  public String getUnMisura() {
    return unMisura;
  }

  
  
  
  public Integer getAnno() {
    return anno;
  }

  public void setAnno(Integer anno) {
    this.anno = anno;
  }
  
  public Integer getMese() {
    return mese;
  }

  public void setMese(Integer mese) {
    this.mese = mese;
  }

  public Integer getSettimana() {
    return settimana;
  }

  public void setSettimana(Integer settimana) {
    this.settimana = settimana;
  }

  public Integer getGiorniLav() {
    return giorniLav;
  }

  public void setGiorniLav(Integer giorniLav) {
    this.giorniLav = giorniLav;
  }

  public Double getOreLav() {
    return oreLav;
  }

  public void setOreLav(Double oreLav) {
    this.oreLav = oreLav;
  }

  public Double getPzProd() {
    return pzProd;
  }

  public void setPzProd(Double pzProd) {
    this.pzProd = pzProd;
  }

  public Double getOreBudget() {
    return oreBudget;
  }

  public void setOreBudget(Double oreBudget) {
    this.oreBudget = oreBudget;
  }

  public Double getOreSetup() {
    return oreSetup;
  }

  public void setOreSetup(Double oreSetup) {
    this.oreSetup = oreSetup;
  }

  public Double getOreFermi() {
    return oreFermi;
  }

  public void setOreFermi(Double oreFermi) {
    this.oreFermi = oreFermi;
  }

  public Double getOreMicrofermi() {
    return oreMicrofermi;
  }

  public void setOreMicrofermi(Double oreMicrofermi) {
    this.oreMicrofermi = oreMicrofermi;
  }

  public Double getOrePersonale() {
    return orePersonale;
  }

  public void setOrePersonale(Double orePersonale) {
    this.orePersonale = orePersonale;
  }
  
  
  
  
}
