package colombini.model.persistence;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import colombini.costant.CostantsColomb;
import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class BeanAvanProdLinea  implements IBeanPersSIMPLE {
  
  public final static String TABNAME="ZAVPRO";
  public final static String TABFIELDS="ZACONO,ZAPLGR,ZADTCO,ZACOMM,ZANCOL,ZANART,ZAARTI,ZADESC,"
                                       + "ZARGDT,ZAEXDT,ZAEXTM,ZALMDT";
  
  public final static String TABKEYFIELDS="ZACONO,ZAPLGR,ZADTCO,ZACOMM,ZANCOL,ZANART";
  
  
  public final String FAZIENDA="ZACONO";
  public final String FLINEA="ZAPLGR";
  
  public final String FDATACOM="ZADTCO";
  public final String FCOMM="ZACOMM";
  public final String FNCOL="ZANCOL";
  public final String FNART="ZANART";
  public final String FCODART="ZAARTI";
  public final String FDESCART="ZADESC";
  
  public final String FDTINS="ZARGDT";
  public final String FDTPROD="ZAEXDT";
  public final String FTPROD="ZAEXTM";
  
  public final String FDTELB="ZALMDT";
  
  public final static String PRGFOREX="00000000000000,00000000990000,0000000099L000,*?????00000000,000000XX000000,000000TM99L000,"
                                     +"000000XX00L000,000000XXN00000,000000XXN0L000,000000LX00L000,000000XX30L000,0000000099E000,"
                                     +"000000XX500000,000000XXM00000,000033LX00L000";
  
  private final String DATAFORMAT="dd/MM/yyyy HH:mm:ss";
  
  
  private String codLinea;
  
  private Long dataComN;
  private Integer commessa;
  private Integer collo;
  private Integer nArticolo;
  
  private String codArticolo;
  private String descArticolo;
  
  
  private Date dataProcess;
  private Double tempoElab;
  private Double tempoMvx;

  
  public BeanAvanProdLinea(String linea) {
    this.codLinea=linea;
    
    tempoElab=Double.valueOf(0);
    tempoMvx=Double.valueOf(0);
    
  }
  
  public BeanAvanProdLinea(String linea,Integer commessa, Integer collo, Integer tipo) {
    this.codLinea=linea;
    this.commessa = commessa;
    this.collo = collo;
    this.nArticolo = tipo;
    
    
    tempoElab=Double.valueOf(0);
    tempoMvx=Double.valueOf(0);
    
  }
  

  public String getKey(){
    return commessa.toString()+collo.toString()+nArticolo.toString();
  }
  
  
  public String getCodArticolo() {
    return codArticolo;
  }

  public void setCodArticolo(String codArticolo) {
    this.codArticolo = codArticolo;
  }

  public Integer getCollo() {
    return collo;
  }

  public void setCollo(Integer collo) {
    this.collo = collo;
  }

  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
  }

  public Date getDataProcess() {
    return dataProcess;
  }

  public void setDataProcess(Date dataProcess) {
    this.dataProcess = dataProcess;
  }

  public String getDescArticolo() {
    return descArticolo;
  }

  public void setDescArticolo(String descArticolo) {
    this.descArticolo = descArticolo;
  }

  public Integer getNarticolo() {
    return nArticolo;
  }

  public void setNarticolo(Integer narticolo) {
    this.nArticolo = narticolo;
  }


  public Double getTempoElab() {
    return tempoElab;
  }

  public void setTempoElab(Double tempoElab) {
    this.tempoElab = tempoElab;
  }

  public Double getTempoMvx() {
    return tempoMvx;
  }

  public void setTempoMvx(Double tempoMvx) {
    this.tempoMvx = tempoMvx;
  }

  public Long getDataComN() {
    return dataComN;
  }

  public void setDataComN(Long dataComN) {
    this.dataComN = dataComN;
  }

  public String getCodLinea() {
    return codLinea;
  }

  public void setCodLinea(String codLinea) {
    this.codLinea = codLinea;
  }

 

  
  
  public String getDataProcessString(){
    String s="";
    if(dataProcess==null)
      return s;
    
    try {
      s = DateUtils.DateToStr(dataProcess, DATAFORMAT);
    } catch (ParseException ex) {
      _logger.error("Problema nella conversione della data "+dataProcess+" -->"+ex.getMessage());
    }
    
    return s;
  }
  
//  @Override
//  public String toString() {
//    StringBuffer bf = new StringBuffer();
//    bf.append(commessa).append(";").append(
//            collo).append(";").append(
//            narticolo).append(";").append(
//            codArticolo).append(";").append(
//            descArticolo).append(";").append(
//            getDataProcessString());
//    
//    return bf.toString();
//  }
  
 
  

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABNAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABKEYFIELDS.split(","));
  }

  
  
  
  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //centro di lavoro
    
    
    types.add(Types.NUMERIC); //datacommessa
    types.add(Types.INTEGER); //commessa
    types.add(Types.INTEGER);    //numero collo
    types.add(Types.INTEGER);    //numero articolo
    
    types.add(Types.CHAR);    //articolo
    types.add(Types.CHAR);    //desc articolo
    
    types.add(Types.TIMESTAMP); //data inserimento
    
    
    types.add(Types.TIMESTAMP); //data produzione
    types.add(Types.NUMERIC); //tempo esec
    types.add(Types.TIMESTAMP); //data produzione
    
    return types;
  }

  
  
   @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FLINEA, this.codLinea);
    fieldsValue.put(FDATACOM, this.dataComN);
    fieldsValue.put(FCOMM, this.commessa);
    fieldsValue.put(FNCOL, this.collo);
    fieldsValue.put(FNART, this.nArticolo);
    
    fieldsValue.put(FCODART, this.codArticolo);
    fieldsValue.put(FDESCART, this.descArticolo);
    
    fieldsValue.put(FDTINS, new Date());
    
    fieldsValue.put(FDTPROD, this.dataProcess);
    fieldsValue.put(FTPROD, this.tempoElab);
    
    
    fieldsValue.put(FDTELB, new Date());
    
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FLINEA, this.codLinea);
    fieldsValue.put(FDATACOM, this.dataComN);
    fieldsValue.put(FCOMM, this.commessa);
    
    
    
    return fieldsValue;
  }
 
  
  
  
  public static BeanAvanProdLinea getInfoCollo(String codLinea,String riga){
    String comm=riga.substring(0, 3);
    String collo=riga.substring(3,8);
    String tipo=riga.substring(8,10);
    String prgForm=riga.substring(22,36);
    String coArt=riga.substring(44,53).trim();
    String descArt=riga.substring(53,riga.length()).trim();
    if(descArt.length()>30)
      descArt=descArt.substring(0, 30);
    
    if(PRGFOREX.contains(prgForm)){
      return null;
    }    
    
    BeanAvanProdLinea icar=new BeanAvanProdLinea(codLinea,Integer.valueOf(comm), Integer.valueOf(collo), Integer.valueOf(tipo));
    
    icar.setCodArticolo(coArt);
    icar.setDescArticolo(descArt);

    return icar;
  }
  
  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
  
  private static final Logger _logger = Logger.getLogger(BeanAvanProdLinea.class);

  
}
