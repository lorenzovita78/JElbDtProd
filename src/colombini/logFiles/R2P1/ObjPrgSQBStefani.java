/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R2P1;

/**
 *
 * @author lvita
 */
public class ObjPrgSQBStefani {
  
  private String nome;
  private Double lunghezza;
  private Double larghezza;
  private Double spessore;
  private Double velocita;
  private Double cadenzamento;
  
  private Long qtaTemp;
  private Long durataTemp;
  
  
  
  
  
  public ObjPrgSQBStefani(String nome) {
    this.nome=nome;
    
    this.qtaTemp=Long.valueOf(0);
    this.durataTemp=Long.valueOf(0);
    
  }

  public Long getDurataTemp() {
    return durataTemp;
  }

  public Long getQtaTemp() {
    return qtaTemp;
  }

  
  public Double getLarghezza() {
    return larghezza;
  }

  public Double getLunghezza() {
    return lunghezza;
  }

  public String getNome() {
    return nome;
  }

  public Double getSpessore() {
    return spessore;
  }

  
  
  /**
   * 
   * @return Torna la velocità espressa in metri al minuto
   */
  public Double getVelocita() {
    return velocita;
  }

  public Double getCadenzamento() {
    return cadenzamento;
  }

  public void setDurataTemp(Long durataTemp) {
    this.durataTemp = durataTemp;
  }

  public void setQtaTemp(Long qtaTemp) {
    this.qtaTemp = qtaTemp;
  }

  public void setLarghezza(Double larghezza) {
    this.larghezza = larghezza;
  }

  public void setLunghezza(Double lunghezza) {
    this.lunghezza = lunghezza;
  }

  public void setSpessore(Double spessore) {
    this.spessore = spessore;
  }

  public void setVelocita(Double velocita) {
    this.velocita = velocita;
  }

  public void setCadenzamento(Double cadenzamento) {
    this.cadenzamento = cadenzamento;
  }

 
  
  
  
  
  public void addQta(Long qta){
    qtaTemp+=qta;
  }
  
  public void addTempo(Long sec){
    durataTemp+=sec;
  }
  
  
  
  public Double getRuntimeEff(){
    Double runtimeEff=Double.valueOf(0);
    Double num=(lunghezza+cadenzamento);
    Double den=(velocita*1000)/60; //porto la velocita in mm al secondo
    
    runtimeEff=(num*qtaTemp)/den;
    
    return runtimeEff;
  }
  
  
}
