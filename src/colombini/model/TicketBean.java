/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objBean;

import java.util.Date;

/**
 *
 * @author lvita
 */
public class TicketBean {
  
  private Long numeroTk;
  private Date dataInizio;
  private Date dataFine;
  private String stato;

  
  public Date getDataFine() {
    return dataFine;
  }

  public void setDataFine(Date dataFine) {
    this.dataFine = dataFine;
  }

  public Date getDataInizio() {
    return dataInizio;
  }

  public void setDataInizio(Date dataInizio) {
    this.dataInizio = dataInizio;
  }

  public Long getNumeroTk() {
    return numeroTk;
  }

  public void setNumeroTk(Long numeroTk) {
    this.numeroTk = numeroTk;
  }

  public String getStato() {
    return stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }
  
  
}
