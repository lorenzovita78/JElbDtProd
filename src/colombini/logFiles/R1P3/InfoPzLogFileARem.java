/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P3;

import java.util.Date;

/**
 *
 * @author lvita
 */
public class InfoPzLogFileARem {
  
  private Integer ultNumComm;  // ultimo numero relativo alla commessa elaborata
  private Integer nCollo;
  private Integer tipo; 
  private Date dataIniLav;  //
  private Date dataFineLav;
  private Double secElab;

  public InfoPzLogFileARem() {
  }

  public InfoPzLogFileARem(Integer ultNumComm, Integer nCollo, Integer tipo) {
    this.ultNumComm = ultNumComm;
    this.nCollo = nCollo;
    this.tipo = tipo;
  }

  
  
  
  public Integer getNCollo() {
    return nCollo;
  }

  public void setNCollo(Integer nCollo) {
    this.nCollo = nCollo;
  }

  public Date getDataFineLav() {
    return dataFineLav;
  }

  public void setDataFineLav(Date dataFineLav) {
    this.dataFineLav = dataFineLav;
  }

  public Date getDataIniLav() {
    return dataIniLav;
  }

  public void setDataIniLav(Date dataIniLav) {
    this.dataIniLav = dataIniLav;
  }

  
  

  public Double getSecElab() {
    return secElab;
  }

  public void setSecElab(Double secElab) {
    this.secElab = secElab;
  }

  public Integer getUltNumComm() {
    return ultNumComm;
  }

  public void setUltNumComm(Integer ultNumComm) {
    this.ultNumComm = ultNumComm;
  }

  public Integer getTipo() {
    return tipo;
  }

  public void setTipo(Integer tipo) {
    this.tipo = tipo;
  }
  
  
  
}
