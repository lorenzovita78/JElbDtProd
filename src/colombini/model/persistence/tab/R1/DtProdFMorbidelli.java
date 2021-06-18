/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dtProduzione.rovereta1.P1;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DtProdFMorbidelli {
  
  private Date dataLog;
    
  private String area;
  private String programma;
  private String descrizione;
  private Double lunghezza;
  private Double larghezza;
  private Double altezza;
//  private Date oraInizio;
//  private Date oraFine;
  private String oraInizioS;
  private String oraFineS;
  private String ora;
  private Long tempoEffettivo;
  private Long tempoTotale;
  private Integer qta;
  private Long tempoMedio;

  public final static String INSERTSTM="INSERT INTO MCOBMODDTA.ZFOMDP"
          + " (ZPDATA,ZPORA,ZPPROG,ZPDESC,ZPLNGH,ZPLARG,ZPSPES,ZPQTA,ZPTEFF,ZPTTOT,ZPUTIN,ZPDTIN,ZPORIN) "
          + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
  
  public DtProdFMorbidelli(Date data) {
    this.dataLog=data;
  }

  public void loadDataFromList(List info) throws ParseException{
    if(info==null)
      return;

    this.area=ClassMapper.classToString(info.get(0));
    String appo=ClassMapper.classToString(info.get(1));
    this.programma=appo.substring(appo.lastIndexOf("\\")+1,appo.lastIndexOf(".PGM"));
    
    this.descrizione=ClassMapper.classToString(info.get(2));
    this.lunghezza=ClassMapper.classToClass(info.get(3), Double.class);
    this.larghezza=ClassMapper.classToClass(info.get(4), Double.class);
    this.altezza=ClassMapper.classToClass(info.get(5), Double.class);
//    this.oraInizio=getOraInizio(info);
//    this.oraFine=getOraFine(info);
    this.ora=ClassMapper.classToString(info.get(6));
    this.oraInizioS=ora+":"+info.get(7)+":"+info.get(8);
    this.oraFineS=info.get(9)+":"+info.get(10)+":"+info.get(11);
    
    this.tempoEffettivo=getTempoEffettivo(info);
    this.tempoTotale=getTempoTotale(info);
    this.qta=ClassMapper.classToClass(info.get(18), Integer.class);
    this.tempoMedio=getTempoMedio(info);
  }

  
  
//  public getSql
  
  private Date getOraInizio(List info) throws ParseException{

    return getDataOra(info.get(6), info.get(7), info.get(8));
  }

  private Date getOraFine(List info) throws ParseException{

    return getDataOra(info.get(9), info.get(10), info.get(11));
  }

  private Date getDataOra(Object ora,Object min,Object sec) throws ParseException{
    String dts=DateUtils.DateToStr(dataLog, "ddMMyyyy");
    dts=dts+" "+ora+min+sec;
    return DateUtils.StrToDate(dts, "ddMMyyyy HHmmss");
  }

  private Long getTempoEffettivo(List info){

    return getTempo(info.get(12),info.get(13),info.get(14));
  }

  private Long getTempoTotale(List info){

    return getTempo(info.get(15),info.get(16),info.get(17));
  }

  private Long getTempoMedio(List info){

    return getTempo(info.get(19),info.get(20),info.get(21));
  }

  private Long getTempo(Object ora,Object min,Object sec){
    Long ssOra=ClassMapper.classToClass(ora, Long.class)*3600;
    Long ssMin=ClassMapper.classToClass(min, Long.class)*60;
    Long ss=ClassMapper.classToClass(sec, Long.class);

    return ssOra+ssMin+ss;
  }
  
  public Double getAltezza() {
    return altezza;
  }

  public void setAltezza(Double altezza) {
    this.altezza = altezza;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public Date getDataLog() {
    return dataLog;
  }

  public void setDataLog(Date dataLog) {
    this.dataLog = dataLog;
  }

  public String getDescrizione() {
    return descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public Double getLarghezza() {
    return larghezza;
  }

  public void setLarghezza(Double larghezza) {
    this.larghezza = larghezza;
  }

  public Double getLunghezza() {
    return lunghezza;
  }

  public void setLunghezza(Double lunghezza) {
    this.lunghezza = lunghezza;
  }

  public String getOra() {
    return ora;
  }


  public String getOraInizioS() {
    return oraInizioS;
  }

  public void setOraInizioS(String oraInizioS) {
    this.oraInizioS = oraInizioS;
  }

  public String getOraFineS() {
    return oraFineS;
  }

  public void setOraFineS(String oraFineS) {
    this.oraFineS = oraFineS;
  }

  public String getProgramma() {
    return programma;
  }

  public void setProgramma(String programma) {
    this.programma = programma;
  }

  public Integer getQta() {
    return qta;
  }

  public void setQta(Integer qta) {
    this.qta = qta;
  }

  public Long getTempoEffettivo() {
    return tempoEffettivo;
  }

  public void setTempoEffettivo(Long tempoEffettivo) {
    this.tempoEffettivo = tempoEffettivo;
  }

  public Long getTempoMedio() {
    return tempoMedio;
  }

  public void setTempoMedio(Long tempoMedio) {
    this.tempoMedio = tempoMedio;
  }

  public Long getTempoTotale() {
    return tempoTotale;
  }

  public void setTempoTotale(Long tempoTotale) {
    this.tempoTotale = tempoTotale;
  }
  
  
//  public Date getOraFine() {
//    return oraFine;
//  }
//
//  public void setOraFine(Date oraFine) {
//    this.oraFine = oraFine;
//  }
//
//  public Date getOraInizio() {
//    return oraInizio;
//  }
//
//  public void setOraInizio(Date oraInizio) {
//    this.oraInizio = oraInizio;
//  }
}
