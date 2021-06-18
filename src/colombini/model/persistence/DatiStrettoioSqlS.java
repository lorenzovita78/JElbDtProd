/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.IBeanPersSIMPLE;
import utils.ArrayUtils;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class DatiStrettoioSqlS implements IBeanPersSIMPLE{

  public final static String TABLENAME="DatiStrettoio";
  
  public final static String DATACOMMESSA="dataCommessa";
  public final static String COMMESSA="commessa";
  public final static String CODICECOLLO="codiceCollo";
  public final static String NUMEROART="numeroArt";
  public final static String CODICEOART="codArticolo";
  public final static String DESCRART="descrArticolo";
  public final static String DIM1="dim1";
  public final static String DIM2="dim2";
  public final static String DIM3="dim3";
  public final static String NOMEPRG="nomeProgramma";
  public final static String ROTAZIONE="rotazione";
  public final static String DESTINAZIONE="destinazione";
  
  
  public final static String DATAORAINSERIMENTO="DataInserimento";
  
  
  private Date dataCommessa;
  private String commessa;
  private String codiceCollo;
  private String numeroArt;
  private String codArticolo;
  private String descrArticolo;
  private String dim1;
  private String dim2;
  private String dim3;
  private String nomePrg;
  private String rotazione;
  private String destinazione;
  private Date dataInserimento;
    
    
  public DatiStrettoioSqlS(Date dataC){
    this.dataCommessa=dataC;
    this.dataInserimento=new Date();
      
  }
  
  
  public void loadInfo(String info){
    List lst=ArrayUtils.getListFromArray(info.split(";"));
    this.commessa=ClassMapper.classToString(lst.get(0));
    this.codiceCollo=ClassMapper.classToString(lst.get(1));
    this.numeroArt=ClassMapper.classToString(lst.get(2));
    this.codArticolo=ClassMapper.classToString(lst.get(3));
    this.descrArticolo=ClassMapper.classToString(lst.get(4));
    this.dim1=ClassMapper.classToString(lst.get(5));
    this.dim2=ClassMapper.classToString(lst.get(6));
    this.dim3=ClassMapper.classToString(lst.get(7));
    this.nomePrg=ClassMapper.classToString(lst.get(8));
    this.rotazione=ClassMapper.classToString(lst.get(9));
    this.destinazione=ClassMapper.classToString(lst.get(10));
      
  }
  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(DATACOMMESSA,this.dataCommessa);
    fieldsValue.put(COMMESSA,this.commessa);
    fieldsValue.put(CODICECOLLO,this.codiceCollo);
    fieldsValue.put(NUMEROART,this.numeroArt);
    fieldsValue.put(CODICEOART,this.codArticolo);
    fieldsValue.put(DESCRART,this.descrArticolo);
    fieldsValue.put(DIM1,this.dim1);
    fieldsValue.put(DIM2,this.dim2);
    fieldsValue.put(DIM3,this.dim3);
    fieldsValue.put(NOMEPRG,this.nomePrg);
    fieldsValue.put(ROTAZIONE,this.rotazione);
    fieldsValue.put(DESTINAZIONE,this.destinazione);
    fieldsValue.put(DATAORAINSERIMENTO, new Date());
    
    
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fields=new HashMap();
    fields.put(COMMESSA, this.commessa);
    fields.put(DATACOMMESSA, this.dataCommessa);
    fields.put(CODICECOLLO, this.codiceCollo);
    
    
    return fields;
  }

  @Override
  public String getLibraryName() {
    return "StrettoioArtecSupervisore.dbo";
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List l=new ArrayList();
    l.add(DATACOMMESSA);
    l.add(COMMESSA);
    l.add(CODICECOLLO);
    l.add(NUMEROART);
    l.add(CODICEOART);
    l.add(DESCRART);
    l.add(DIM1);
    l.add(DIM2);
    l.add(DIM3);
    l.add(NOMEPRG);
    l.add(ROTAZIONE);
    l.add(DESTINAZIONE);
    l.add(DATAORAINSERIMENTO);
    
    
    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(DATACOMMESSA);l.add(COMMESSA);l.add(CODICECOLLO);
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.DATE); //dataCOmmessa
    types.add(Types.CHAR);   //commessa
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR);      
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.CHAR); 
    types.add(Types.TIMESTAMP); //dataOraInserimento
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }

  public Date getDataCommessa() {
    return dataCommessa;
  }

  public void setDataCommessa(Date dataCommessa) {
    this.dataCommessa = dataCommessa;
  }

  public String getCommessa() {
    return commessa;
  }

  public void setCommessa(String commessa) {
    this.commessa = commessa;
  }

  public String getCodiceCollo() {
    return codiceCollo;
  }

  public void setCodiceCollo(String codiceCollo) {
    this.codiceCollo = codiceCollo;
  }

  public String getNumeroArt() {
    return numeroArt;
  }

  public void setNumeroArt(String numeroArt) {
    this.numeroArt = numeroArt;
  }

  public String getCodArticolo() {
    return codArticolo;
  }

  public void setCodArticolo(String codArticolo) {
    this.codArticolo = codArticolo;
  }

  public String getDescrArticolo() {
    return descrArticolo;
  }

  public void setDescrArticolo(String descrArticolo) {
    this.descrArticolo = descrArticolo;
  }

  public String getDim1() {
    return dim1;
  }

  public void setDim1(String dim1) {
    this.dim1 = dim1;
  }

  public String getDim2() {
    return dim2;
  }

  public void setDim2(String dim2) {
    this.dim2 = dim2;
  }

  public String getDim3() {
    return dim3;
  }

  public void setDim3(String dim3) {
    this.dim3 = dim3;
  }

  public String getNomePrg() {
    return nomePrg;
  }

  public void setNomePrg(String nomePrg) {
    this.nomePrg = nomePrg;
  }

  public String getRotazione() {
    return rotazione;
  }

  public void setRotazione(String rotazione) {
    this.rotazione = rotazione;
  }

  public String getDestinazione() {
    return destinazione;
  }

  public void setDestinazione(String destinazione) {
    this.destinazione = destinazione;
  }

  public Date getDataInserimento() {
    return dataInserimento;
  }

  public void setDataInserimento(Date dataInserimento) {
    this.dataInserimento = dataInserimento;
  }
  
  
  
  
}
