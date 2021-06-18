/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objBean;

/**
 * Rappresentazione dei dati settimanali delle manutenzioni in opera su una data linea
 * @author lvita
 */
public class TicketSettBean {
  
  private Integer anno;
  private Integer mese;
  private Integer settimana;
  private String linea;
  private Long tkAperti;
  private Long tkChiusi;
  private Double tempoMedioGg;
  private Long tkMeseC;
  private Long tkMese_1;
  private Long tkMese_2;
  private Long tkMese_3;
  private Long tkMese_4;
  private Long tkMese_5;
  private Long tkMese_6;
  private Long tkMese_7;
  private Long tkMese_8;
  private Long tkMese_9;
  private Long tkMese_10;
  private Long tkMese_11;
  private Long tkMese_12;

  public TicketSettBean() {
   this.anno=Integer.valueOf(0);
   this.mese=Integer.valueOf(0);
   this.settimana=Integer.valueOf(0);
   
   tkAperti=Long.valueOf(0);
   tkChiusi=Long.valueOf(0);
   
   tkMeseC=Long.valueOf(0);
   tkMese_1=Long.valueOf(0);
   tkMese_2=Long.valueOf(0);
   tkMese_3=Long.valueOf(0);
   tkMese_4=Long.valueOf(0);
   tkMese_5=Long.valueOf(0);
   tkMese_6=Long.valueOf(0);
   tkMese_7=Long.valueOf(0);
   tkMese_8=Long.valueOf(0);
   tkMese_9=Long.valueOf(0);
   tkMese_10=Long.valueOf(0);
   tkMese_11=Long.valueOf(0);
   tkMese_12=Long.valueOf(0);
   
   tempoMedioGg=Double.valueOf(0);    
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

  public String getLinea() {
    return linea;
  }

  public void setLinea(String linea) {
    this.linea = linea;
  }
  
  public Double getTempoMedioGg() {
    return tempoMedioGg;
  }

  public void setTempoMedioGg(Double tempoMedioGg) {
    this.tempoMedioGg = tempoMedioGg;
  }

  public Long getTkAperti() {
    return tkAperti;
  }

  public void setTkAperti(Long tkAperti) {
    this.tkAperti = tkAperti;
  }

  public Long getTkChiusi() {
    return tkChiusi;
  }

  public void setTkChiusi(Long tkChiusi) {
    this.tkChiusi = tkChiusi;
  }

  public Long getTkMeseC() {
    return tkMeseC;
  }

  public void setTkMeseC(Long tkMeseC) {
    this.tkMeseC = tkMeseC;
  }

  

  public Long getTkMese_1() {
    return tkMese_1;
  }

  public void setTkMese_1(Long tkMese_1) {
    this.tkMese_1 = tkMese_1;
  }

  public Long getTkMese_10() {
    return tkMese_10;
  }

  public void setTkMese_10(Long tkMese_10) {
    this.tkMese_10 = tkMese_10;
  }

  public Long getTkMese_11() {
    return tkMese_11;
  }

  public void setTkMese_11(Long tkMese_11) {
    this.tkMese_11 = tkMese_11;
  }

  public Long getTkMese_12() {
    return tkMese_12;
  }

  public void setTkMese_12(Long tkMese_12) {
    this.tkMese_12 = tkMese_12;
  }

  public Long getTkMese_2() {
    return tkMese_2;
  }

  public void setTkMese_2(Long tkMese_2) {
    this.tkMese_2 = tkMese_2;
  }

  public Long getTkMese_3() {
    return tkMese_3;
  }

  public void setTkMese_3(Long tkMese_3) {
    this.tkMese_3 = tkMese_3;
  }

  public Long getTkMese_4() {
    return tkMese_4;
  }

  public void setTkMese_4(Long tkMese_4) {
    this.tkMese_4 = tkMese_4;
  }

  public Long getTkMese_5() {
    return tkMese_5;
  }

  public void setTkMese_5(Long tkMese_5) {
    this.tkMese_5 = tkMese_5;
  }

  public Long getTkMese_6() {
    return tkMese_6;
  }

  public void setTkMese_6(Long tkMese_6) {
    this.tkMese_6 = tkMese_6;
  }

  public Long getTkMese_7() {
    return tkMese_7;
  }

  public void setTkMese_7(Long tkMese_7) {
    this.tkMese_7 = tkMese_7;
  }

  public Long getTkMese_8() {
    return tkMese_8;
  }

  public void setTkMese_8(Long tkMese_8) {
    this.tkMese_8 = tkMese_8;
  }

  public Long getTkMese_9() {
    return tkMese_9;
  }

  public void setTkMese_9(Long tkMese_9) {
    this.tkMese_9 = tkMese_9;
  }
  
  
  /**
   * Torna il numero totale di tk
   * @return 
   */  
  public Long getNumTotTicket(){
    return this.tkAperti+this.tkChiusi;
  }
  
  public void add1TkChiuso(){
    this.tkChiusi++;
  }
  
  public void add1TkAperto(){
    this.tkAperti++;
  }
  
  
  public void addTkMeseRif(Integer mese){
    switch(mese){
      case 0 : tkMeseC++;
               break; 
      case 1 : tkMese_1++;
               break;
      case 2 : tkMese_2++;
               break;
      case 3 : tkMese_3++;
               break;
      case 4 : tkMese_4++;
               break; 
      case 5 : tkMese_5++;
               break;
      case 6 : tkMese_6++;
               break;
      case 7 : tkMese_7++;
               break;
      case 8 : tkMese_8++;
               break;
      case 9 : tkMese_9++;
               break; 
      case 10 : tkMese_10++;
                break;
      case 11 : tkMese_11++;
                break;
    }
    if(mese>=12)
      tkMese_12++;
  }
  
  public Long getValTkMeseRif(Integer mese){
    switch(mese){
      case 0 :  return tkMeseC;
      case 1 : return tkMese_1;
      case 2 : return tkMese_2;
      case 3 : return tkMese_3;
      case 4 : return tkMese_4;
      case 5 : return tkMese_5;
      case 6 : return tkMese_6;
      case 7 : return tkMese_7;
      case 8 : return tkMese_8;
      case 9 : return tkMese_9;
      case 10 : return tkMese_10;
      case 11 : return tkMese_11;
    }
    if(mese>=12)
      return tkMese_12;
    
    return Long.valueOf(0);
  }
  
  
}
