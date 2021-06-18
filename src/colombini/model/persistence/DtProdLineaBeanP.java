/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import colombini.costant.CostantsColomb;
import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.DateUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class DtProdLineaBeanP implements IBeanPersSIMPLE {
  
  
  private final String TABLENAME="ZDPTLG";  //tempi linea giornalieri
  
  private final String FAZIENDA="ZTCONO";
  private final String FLINEA="ZTPLGR";
  private final String FDATARIF="ZTDTRF";
  private final String FTOTMINDIP="ZTTMDP";
  private final String FTOTMINSTR="ZTTMST";
  private final String FTOTMINPI="ZTTMPI";
  private final String FTOTMINNPR="ZTTMNP";
  
  private final String FTOTMINSET="ZTTMSE";
  private final String FTOTMINPG="ZTTMPG";
  private final String FTOTMINMF="ZTTMMF";
  private final String FTOTMINGU="ZTTMGU";
  
  private final String FTOTPZPROD="ZTTPZP";
  
  private final String FDTINS="ZTRGDT";
 
  
  private final String FIELDS=FAZIENDA+","+FDATARIF+","+FLINEA+","+FTOTMINDIP+","+FTOTMINSTR+","
                                +FTOTMINPI+","+FTOTMINNPR+","+FTOTMINSET+","+FTOTMINPG+","
                                +FTOTMINMF+","+FTOTMINGU+","+FTOTPZPROD+","+FDTINS;
  
  private String linea;
  private Date dataRif;
  private Long dataRifN;
  private Double totMinutiTurniDip;
  private Double totMinutiStraordinario;
  private Double totMinutiProduzione;
  private Double totMinutiNnProduzione;
  private Long totPzProd;
  private Double totMinutiGuasti;
  private Double totMinutiSetup;
  private Double totMinutiMicrofermi;
  private Double totMinutiPerditeGest;

  
  public DtProdLineaBeanP(String linea,Date dataRif) {
    this.linea=linea;
    this.dataRif=dataRif;
    this.dataRifN=DateUtils.getDataForMovex(dataRif);
    
    totMinutiGuasti=Double.valueOf(0);
    totMinutiSetup=Double.valueOf(0);
    totMinutiMicrofermi=Double.valueOf(0);
    totMinutiPerditeGest=Double.valueOf(0);
    totPzProd=Long.valueOf(0);
    totMinutiTurniDip=Double.valueOf(0);
    totMinutiStraordinario=Double.valueOf(0);
    totMinutiProduzione=Double.valueOf(0);
    totMinutiNnProduzione=Double.valueOf(0);

  }

  
  public Boolean isDtValid(){
    return totMinutiProduzione>0 && totMinutiTurniDip>0 && dataRifN>0;
  }
  
  public Date getDataRif() {
    return dataRif;
  }

  public void setDataRif(Date dataRif) {
    this.dataRif = dataRif;
  }

  public String getLinea() {
    return linea;
  }

  public void setLinea(String linea) {
    this.linea = linea;
  }

  public Double getTotMinutiStraordinario() {
    return totMinutiStraordinario;
  }

  public void setTotMinutiStraordinario(Double totMinutiStraordinario) {
    this.totMinutiStraordinario = totMinutiStraordinario;
  }


  public Double getTotMinutiGuasti() {
    return totMinutiGuasti;
  }

  public void setTotMinutiGuasti(Double totMinutiGuasti) {
    this.totMinutiGuasti = totMinutiGuasti;
  }

  public Double getTotMinutiMicrofermi() {
    return totMinutiMicrofermi;
  }

  public void setTotMinutiMicrofermi(Double totMinutiMicrofermi) {
    this.totMinutiMicrofermi = totMinutiMicrofermi;
  }

  public Double getTotMinutiNnProduzione() {
    return totMinutiNnProduzione;
  }

  public void setTotMinutiNnProduzione(Double totMinutiNnProduzione) {
    this.totMinutiNnProduzione = totMinutiNnProduzione;
  }

  public Double getTotMinutiPerditeGest() {
    return totMinutiPerditeGest;
  }

  public void setTotMinutiPerditeGest(Double totMinutiPerditeGest) {
    this.totMinutiPerditeGest = totMinutiPerditeGest;
  }

  public Double getTotMinutiProduzione() {
    return totMinutiProduzione;
  }

  public void setTotMinutiProduzione(Double totMinutiProduzione) {
    this.totMinutiProduzione = totMinutiProduzione;
  }

  public Double getTotMinutiSetup() {
    return totMinutiSetup;
  }

  public void setTotMinutiSetup(Double totMinutiSetup) {
    this.totMinutiSetup = totMinutiSetup;
  }

  public Double getTotMinutiTurniDip() {
    return totMinutiTurniDip;
  }

  public void setTotMinutiTurniDip(Double totMinutiTurniDip) {
    this.totMinutiTurniDip = totMinutiTurniDip;
  }

  public Long getTotPzProd() {
    return totPzProd;
  }

  public void setTotPzProd(Long totPzProd) {
    this.totPzProd = totPzProd;
  }

  public void addMinutiMicroF(Double minuti){
    this.totMinutiMicrofermi+=minuti;
  }
  
  public void addMinutiSetup(Double minuti){
    this.totMinutiSetup+=minuti;
  }
  
  public void addMinutiGuasti(Double minuti){
    this.totMinutiGuasti+=minuti;
  }
  
  public void addMinutiPerdGest(Double minuti){
    this.totMinutiPerditeGest+=minuti;
  }
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(FIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    List keys=new ArrayList();
    keys.add(FAZIENDA);
    keys.add(FLINEA);
    keys.add(FDATARIF);
    
    
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.NUMERIC); //data
    types.add(Types.CHAR);      //linea
    
    types.add(Types.NUMERIC); //tot min operatori
    types.add(Types.NUMERIC); //tot min straordinario
    types.add(Types.NUMERIC); //tot min impianto
    types.add(Types.NUMERIC); //tot min non prod
    
    types.add(Types.NUMERIC); //tot min setup
    types.add(Types.NUMERIC); //tot min perdite gest
    types.add(Types.NUMERIC); //tot min microf
    types.add(Types.NUMERIC); //tot min guasti
    types.add(Types.INTEGER); //tot pz prod
    
    
    types.add(Types.TIMESTAMP);
    
    
    return types;
  }

  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FDATARIF, this.dataRifN);
    fieldsValue.put(FLINEA, this.linea);
    fieldsValue.put(FTOTMINDIP, this.totMinutiTurniDip);
    fieldsValue.put(FTOTMINSTR, this.totMinutiStraordinario);
    fieldsValue.put(FTOTMINPI, this.totMinutiProduzione);
    fieldsValue.put(FTOTMINNPR, this.totMinutiNnProduzione);
    fieldsValue.put(FTOTMINSET, this.totMinutiSetup);
    fieldsValue.put(FTOTMINPG, this.totMinutiPerditeGest);
    fieldsValue.put(FTOTMINMF, this.totMinutiMicrofermi);
    fieldsValue.put(FTOTMINGU, this.totMinutiGuasti);
    fieldsValue.put(FTOTPZPROD, this.totPzProd);
    
    fieldsValue.put(FDTINS, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FDATARIF, this.dataRifN);
    fieldsValue.put(FLINEA, this.linea);
    
    
    return fieldsValue;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
          
          
         
}
