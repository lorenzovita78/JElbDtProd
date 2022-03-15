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

/**
 *
 * @author lvita
 */
public class LogCopiaLibretti implements IBeanPersSIMPLE{

  public final static String TABLENAME="LogCopiaLibretti";
  
  
  public final static String COMMESSA="Commessa";
  public final static String DATACOMMESSA="DataCommessa";
  public final static String DATAGENERAZIONE="DataGenerazione";
  public final static String NOMEFILE="NomeFile";
  public final static String NUMEROPAGINE="NumeroPagine";
  public final static String DATAORAINSERIMENTO="DataOraInserimento";
  
  
  private String commessa;
  private Date dataCommessa;
  private Date dataGenerazione;
  private String fileName;
  private Integer numeroPagine;
  

  private String pathSource;
  private String pathDest;
  
  
  
  public LogCopiaLibretti(String commessa,Date dataCommessa,String nomeFile){
    this.fileName=nomeFile;
    this.commessa=commessa;
    this.dataCommessa=dataCommessa;
    
  }

  public Date getDataGenerazione() {
    return dataGenerazione;
  }

  public void setDataGenerazione(Date dataGenerazione) {
    this.dataGenerazione = dataGenerazione;
  }

  public Integer getNumeroPagine() {
    return numeroPagine;
  }

  public void setNumeroPagine(Integer numeroPagine) {
    this.numeroPagine = numeroPagine;
  }

  public String getPathSource() {
    return pathSource;
  }

  public void setPathSource(String pathSource) {
    this.pathSource = pathSource;
  }

  public String getPathDest() {
    return pathDest;
  }

  public void setPathDest(String pathDest) {
    this.pathDest = pathDest;
  }

  public String getCommessa() {
    return commessa;
  }

  public Date getDataCommessa() {
    return dataCommessa;
  }

  public String getFileName() {
    return fileName;
  }
  
  
  
  
  public String getCompleteFileNameSource(){
    return this.pathSource+"/"+this.fileName;
  }
  
  
  public String getCompleteFileNameDest(){
    return this.pathDest+"/"+this.fileName;
  }
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(COMMESSA, this.commessa);
    fieldsValue.put(DATACOMMESSA, this.dataCommessa);
    fieldsValue.put(DATAGENERAZIONE, this.dataGenerazione);
    fieldsValue.put(NOMEFILE, this.fileName);
    fieldsValue.put(NUMEROPAGINE, this.numeroPagine);
    fieldsValue.put(DATAORAINSERIMENTO, new Date());
    
    
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fields=new HashMap();
    fields.put(COMMESSA, this.commessa);
    fields.put(DATACOMMESSA, this.dataCommessa);
    fields.put(NOMEFILE, this.dataGenerazione);
    
    
    return fields;
  }

  @Override
  public String getLibraryName() {
    return "GestoreStampe2.dbo";
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List l=new ArrayList();
    l.add(COMMESSA);l.add(DATACOMMESSA);l.add(DATAGENERAZIONE);
    l.add(NOMEFILE);l.add(NUMEROPAGINE);l.add(DATAORAINSERIMENTO);
    

    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(COMMESSA);l.add(DATACOMMESSA);l.add(NOMEFILE);
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.CHAR);   //commessa
    types.add(Types.DATE); //dataCOmmessa
    types.add(Types.DATE); //dataElaborazione
    
    types.add(Types.CHAR);      //nomeFile
    
    types.add(Types.INTEGER);    //numeroPagine
    types.add(Types.TIMESTAMP); //dataOraInserimento
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
}
