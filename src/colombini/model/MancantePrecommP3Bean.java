/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

import java.util.Date;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class MancantePrecommP3Bean {
  
  private Date dataRiferimento;
  private String ordinePrelievo;
  private String ordineCliente;
  private Integer azienda;
  private String articolo;
  private String uMisura;
  private String descrArticolo;
  private String codiceFornitore;
  private String ragSocialeFornitore;
  private Double qtaPrelievo;
  private Double qtaGiacenza;
  //private String descrizioneProd;
  private String causa;
  private String apvg;
  private String ubicazione;
  private String famiglia;
  private String sottocausa;
  private String descrAcquisti;
  
  private Integer anno;
  private Integer mese;
  private Integer sett;

  
  public final static String UNMISURA_N="N";
  public final static String UNMISURA_PCS="PCS";
  
  public final static String CAUSA_RIPRISTINO="Ripristino";
  public final static String CAUSA_GESTIONALE="Gestionale";
  public final static String CAUSA_ROSSANA="Rossana";
  public final static String CAUSA_FORNITORE="Fornitore";
  
  public MancantePrecommP3Bean(){
    this.ordinePrelievo="";
    this.ordineCliente="";
    this.azienda=Integer.valueOf(0);
    this.articolo="";
    this.uMisura="";
    this.sottocausa="";
    
  }
  
  
  public Date getDataRiferimento() {
    return dataRiferimento;
  }

  public void setDataRiferimento(Date dataRiferimento) {
    this.dataRiferimento = dataRiferimento;
    this.anno=DateUtils.getYear(dataRiferimento);
    this.mese=DateUtils.getMonth(dataRiferimento)+1;
    this.sett=DateUtils.getWorkWeek(dataRiferimento);
    
  }

  public String getOrdinePrelievo() {
    return ordinePrelievo;
  }

  public void setOrdinePrelievo(String ordinePrelievo) {
    this.ordinePrelievo = ordinePrelievo;
  }

  public String getOrdineCliente() {
    return ordineCliente;
  }

  public void setOrdineCliente(String ordineCliente) {
    if(StringUtils.isEmpty(ordineCliente))
      ordineCliente="";
    
    this.ordineCliente = ordineCliente;
  }

  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  

  public String getArticolo() {
    return articolo;
  }

  public void setArticolo(String articolo) {
    this.articolo = articolo;
  }

  public String getuMisura() {
    return uMisura;
  }

  public void setuMisura(String uMisura) {
    this.uMisura = uMisura;
  }

  public String getDescrArticolo() {
    return descrArticolo;
  }

  public void setDescrArticolo(String descrArticolo) {
    this.descrArticolo = descrArticolo;
  }

  public String getCodiceFornitore() {
    return codiceFornitore;
  }

  public void setCodiceFornitore(String codiceFornitore) {
    this.codiceFornitore = codiceFornitore;
  }

  public String getRagSocialeFornitore() {
    return ragSocialeFornitore;
  }

  public void setRagSocialeFornitore(String ragSocialeFornitore) {
    this.ragSocialeFornitore = ragSocialeFornitore;
  }

  public Double getQtaPrelievo() {
    return qtaPrelievo;
  }

  public void setQtaPrelievo(Double qtaPrelievo) {
    this.qtaPrelievo = qtaPrelievo;
  }

  public Double getQtaGiacenza() {
    return qtaGiacenza;
  }

  public void setQtaGiacenza(Double qtaGiacenza) {
    this.qtaGiacenza = qtaGiacenza;
  }

//  public String getDescrizioneProd() {
//    return descrizioneProd;
//  }
//
//  public void setDescrizioneProd(String descrizioneProd) {
//    this.descrizioneProd = descrizioneProd;
//  }

  
  
  public String getCausa() {
    return causa;
  }

  public void setCausa(String causa) {
    this.causa = causa;
  }

  public void loadCausa() {
//    if(this.getOrdinePrelievo().contains("PR"))
//        this.setCausa(CAUSA_RIPRISTINO);
     if(this.getOrdinePrelievo().length()>=5){
       String appo=this.getOrdinePrelievo();
       appo=appo.substring(appo.length()-5);
       if(appo.startsWith("PR"))
         this.setCausa(CAUSA_RIPRISTINO);
       else if(appo.startsWith("P1"))
         this.setCausa(CAUSA_GESTIONALE);
     }

      if(this.getQtaGiacenza()>=this.getQtaPrelievo())
        this.setCausa(CAUSA_GESTIONALE);
      
     
      
      if(!StringUtils.isEmpty(this.getOrdineCliente())){
        Integer numOrd=Integer.valueOf(this.getOrdineCliente());
        if(numOrd>=7100000 && numOrd<=7299999)
          this.setCausa(CAUSA_ROSSANA);
      }
      
       if("ELD".equals(this.famiglia) )
        this.setCausa(CAUSA_GESTIONALE);
//      if(this.getCausa()==null)
//        this.setCausa(CAUSA_FORNITORE);
  }
  
  
  
  public String getApvg() {
    return apvg;
  }

  public void setApvg(String apvg) {
    if(StringUtils.IsEmpty(apvg))
      this.apvg = apvg;
    
    if(apvg.length()>2){
      if(apvg.contains("F_") )
        apvg=apvg.substring(2,4);
      else
        apvg=apvg.substring(0,2);
    }
    
    this.apvg = apvg;
  }

  public String getFamiglia() {
    return famiglia;
  }

  public void setFamiglia(String famiglia) {
    this.famiglia = famiglia;
  }

  public String getUbicazione() {
    return ubicazione;
  }

  public void setUbicazione(String ubicazione) {
    this.ubicazione = ubicazione;
    
    if("MP3-PCO-AS".equals(ubicazione))
      setFamiglia("COM");
    if("MP3-PCO".equals(ubicazione))
      setFamiglia("SML");
    if("M31-COM-AS".equals(ubicazione))
      setFamiglia("ELD");
    if("M31-PCO-AS".equals(ubicazione))
      setFamiglia("ELD");
    if("ELDOM".equals(ubicazione))
      setFamiglia("ELD");
  }

  public String getDescrAcquisti() {
    return descrAcquisti;
  }

  public void setDescrAcquisti(String descrAcquisti) {
    this.descrAcquisti = descrAcquisti;
  }

  
  
  public Double getQtaCalc() {
    if(StringUtils.IsEmpty(uMisura))
      return Double.valueOf(1);
    
    if(UNMISURA_N.equals(uMisura.trim()) || UNMISURA_PCS.equals(uMisura.trim()))
      return qtaPrelievo;
    else 
      return Double.valueOf(1);
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

  public Integer getSett() {
    return sett;
  }

  public void setSett(Integer sett) {
    this.sett = sett;
  }

  public String getSottocausa() {
    return sottocausa;
  }

  public void setSottocausa(String sottocausa) {
    this.sottocausa = sottocausa;
  }

  
  
  public String getKeyBean(){
    return ordinePrelievo.trim()+"-"+ordineCliente.trim()+"-"+this.azienda+"-"+this.articolo.trim()+"-"+this.uMisura.trim()+"-"+this.qtaPrelievo;
  }
  
  @Override
  public boolean equals(Object obj) {
    if( ((MancantePrecommP3Bean)obj).getKeyBean().equals(this.getKeyBean())  ){
      return Boolean.TRUE;
    } 

    return Boolean.FALSE;
  }
  
  public void copyInfo(MancantePrecommP3Bean bean){
    if(!StringUtils.isEmpty(bean.getDescrAcquisti()))
      this.descrAcquisti=bean.getDescrAcquisti();
    
    if(!StringUtils.isEmpty(bean.getCausa()))
      this.causa=bean.getCausa();
    
    if(!StringUtils.isEmpty(bean.getApvg()) && StringUtils.isEmpty(this.apvg) )
      this.apvg=bean.getApvg();
    
    if(!StringUtils.isEmpty(bean.getFamiglia()) && StringUtils.isEmpty(this.getFamiglia()))
      this.famiglia=bean.getFamiglia();
    //sottoCausa e descrizione Acquisti??
    
    if(!StringUtils.isEmpty(bean.getSottocausa()) )
      this.sottocausa=bean.getSottocausa();
  }

  
  
}
