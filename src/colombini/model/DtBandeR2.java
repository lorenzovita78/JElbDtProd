/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

/**
 *
 * @author lvita
 */
public class DtBandeR2 {
  
  private Long dataRifN;
  private String codBanda;

  
  private Integer nBandeBuone;
  private Integer nBande1Dif;
  private Integer nBande2Dif;
  private Integer nBande3Dif;
  private Integer nBandePlus4Dif;
  
  private Double lunTotBuona;
  private Double lunTotDif;

  
  public DtBandeR2(){
    this.nBandeBuone=Integer.valueOf(0);
    this.nBande1Dif=Integer.valueOf(0);
    this.nBande2Dif=Integer.valueOf(0);
    this.nBande3Dif=Integer.valueOf(0);
    this.nBandePlus4Dif=Integer.valueOf(0);
    
    this.lunTotBuona=Double.valueOf(0);
    this.lunTotDif=Double.valueOf(0);
  }
  
  public String getCodBanda() {
    return codBanda;
  }

  public void setCodBanda(String codBanda) {
    this.codBanda = codBanda;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public Double getLunTotBuona() {
    return lunTotBuona;
  }

  public void setLunTotBuona(Double lunTotBuona) {
    this.lunTotBuona = lunTotBuona;
  }

  public Double getLunTotDif() {
    return lunTotDif;
  }

  public void setLunTotDif(Double lunTotDif) {
    this.lunTotDif = lunTotDif;
  }

  public Integer getNBande1Dif() {
    return nBande1Dif;
  }

  public void setNBande1Dif(Integer nBande1Dif) {
    this.nBande1Dif = nBande1Dif;
  }

  public Integer getNBande2Dif() {
    return nBande2Dif;
  }

  public void setNBande2Dif(Integer nBande2Dif) {
    this.nBande2Dif = nBande2Dif;
  }

  public Integer getNBande3Dif() {
    return nBande3Dif;
  }

  public void setNBande3Dif(Integer nBande3Dif) {
    this.nBande3Dif = nBande3Dif;
  }

  public Integer getNBandeBuone() {
    return nBandeBuone;
  }

  public void setNBandeBuone(Integer nBandeBuone) {
    this.nBandeBuone = nBandeBuone;
  }

  public Integer getNBandePlus4Dif() {
    return nBandePlus4Dif;
  }

  public void setNBandePlus4Dif(Integer nBandePlus4Dif) {
    this.nBandePlus4Dif = nBandePlus4Dif;
  }

  
  
  /**
   * Data la lunghezza totale della banda difettosa 
   * @param Double lunghezza 
   */
  public void aggLngBandaDiff(Double lun){
    this.lunTotDif+=lun;
  }
  
  /**
   * Aggiorna la lunghezza totale della banda considerata buona  
   * @param Double lunghezza
   */
  public void aggLngBandaBuona(Double lun){
    this.lunTotBuona+=lun;
  }
  
  
  /**
   * In base al numero di difetti passato aggiorna il parametro relativo
   * @param Integer numero di difetti 
   */
  public void aggNumBandeDifettose(Integer num){
    if(num<=0)
      return;
    
    if(num==1){
      incNBande1Dif();
    }else if(num==2){
      incNBande2Dif();
    }else if(num==3){
      incNBande3Dif();
    }else if(num>3){
      incNBandePlus4Dif();
    }
  }
  
  /**
   * Incrementa di l numero di bande buone
   */
  public void incNBandeBuone(){
    this.nBandeBuone++;
  }
  
  /**
   * Incrementa il numero di bande buone con il valore passato
   * @param Integer numero di bande 
   */
  public void incNBandeBuone(Integer num){
    this.nBandeBuone+=num;
  }
  
  public void incNBande1Dif(){
    this.nBande1Dif++;
  }
  
  public void incNBande1Dif(Integer num){
    this.nBande1Dif+=num;
  }
  
  public void incNBande2Dif(){
    this.nBande2Dif++;
  }
  
  public void incNBande2Dif(Integer num){
    this.nBande2Dif+=num;
  }
  
  public void incNBande3Dif(){
    this.nBande3Dif++;
  }
  
  public void incNBande3Dif(Integer num){
    this.nBande3Dif+=num;
  }
  
  public void incNBandePlus4Dif(){
    this.nBandePlus4Dif++;
  }
  
  public void incNBandePlus4Dif(Integer num){
    this.nBandePlus4Dif+=num;
  }
  
}
