/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lvita
 */
public class InfoColloVDL {
 
  private Date oraIn;
  private Integer commessa;
  private String lineaLogicaMvx;
  private Long  nCollo;
  
  
  private String codiceBU;
  private String rifSpunta;  //W magazzino C carrelli P pedane
  
  private List<String> articoli;//

  public InfoColloVDL() {
    articoli=new ArrayList();
  }

  public InfoColloVDL(Date oraIn, Integer commessa, String lineaLogicaMvx, Long nCollo) {
    this.oraIn = oraIn;
    this.commessa = commessa;
    this.lineaLogicaMvx = lineaLogicaMvx;
    this.nCollo = nCollo;
    articoli=new ArrayList();
  }
  
  
  

  public String getKey(){
    if(commessa==null || nCollo==null )
      return "";
    
    return commessa.toString()+nCollo.toString();
  }
  
  
  public List getArticoli() {
    return articoli;
  }

  public void setArticoli(List articoli) {
    this.articoli = articoli;
  }

  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
  }

  public Long getnCollo() {
    return nCollo;
  }

  public void setnCollo(Long nCollo) {
    this.nCollo = nCollo;
  }

  public Date getOraIn() {
    return oraIn;
  }

  public void setOraIn(Date oraIn) {
    this.oraIn = oraIn;
  }

  public String getLineaLogicaMvx() {
    return lineaLogicaMvx;
  }

  public void setLineaLogicaMvx(String lineaLogicaMvx) {
    this.lineaLogicaMvx = lineaLogicaMvx;
  }
 
  
  public void addArticolo(String articolo){
    articoli.add(articolo);
  }
  
  public void addArticoli(List<String> articoliList){
    for(String articolo : articoliList){
      articoli.add(articolo);
    }
  }
  
  
  public Integer getNumeroArticoli(){
    
    return articoli.size();
    
  }

  @Override
  public boolean equals(Object obj) {
    if(obj==null)
      return false;
    
    if (!(obj instanceof InfoColloVDL))
       return false;
    
    if( ((InfoColloVDL)obj).getKey().equals(this.getKey()) )
      return true;
    
    return false;
  }

  public String getCodiceBU() {
    return codiceBU;
  }

  public void setCodiceBU(String codiceBU) {
    this.codiceBU = codiceBU;
  }

  

  public String getRifSpunta() {
    return rifSpunta;
  }

  public void setRifSpunta(String rifSpunta) {
    this.rifSpunta = rifSpunta;
  }
  
  
  
  
}
