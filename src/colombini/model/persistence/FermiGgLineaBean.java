/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import db.Connections;
import java.sql.Types;
import java.util.ArrayList;
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
public class FermiGgLineaBean implements IBeanPersSIMPLE {
 
  public final static String TABFERMIGG="ZDPFMG";
  
  private final String FAZIENDA="ZFCONO";
  private final String FLINEA="ZFPLGR";
  private final String FDATARIF="ZFDTRF";
  private final String FCODCAUS="ZFCODC";
  private final String FMINUTITOT="ZFDRTM";
  private final String FORAINI="ZFORIN";
  private final String FORAFIN="ZFORFN";
  private final String FOCCORRENZE="ZFOCCN";
  private final String FUTINS="ZFRGUT";
  private final String FDTINS="ZFRGDT";
  private final String FORAINS="ZFRGOR";
  
  
  public final String FIELDS=FAZIENDA+","+FLINEA+","+FDATARIF+","+FCODCAUS+","+FMINUTITOT+","
                            +FORAINI+","+FORAFIN+","+FOCCORRENZE+","+FUTINS+","+FDTINS+","+FORAINS;
  
//  public final static String INSERTSTM=" INSERT INTO "+Connections.LIBAS400MCOBMODDTA+"."+FermiGgLineaBean.TABFERMIGG 
//                 + " ( ZFCONO, ZFPLGR, ZFDTRF, ZFCODC, ZFDRTM ,"
//                 + "  ZFORIN ,ZFORFN ,ZFOCCN ,ZFRGUT ,ZFRGDT ,ZFRGOR  )"
//                 + " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
  
  private Integer azienda;
  private String centroLavoro;
  private Long dataRifN;
  private String codCausale;
  private Double durataTot;
  private Integer minInizio;
  private Integer minFine;
  private Integer occorrenze;

  
  //proprieta che non viene salvata sul db.
  private String tipo;
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public String getCodCausale() {
    return codCausale;
  }

  public void setCodCausale(String codCausale) {
    this.codCausale = codCausale;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public Double getDurataTot() {
    return durataTot;
  }

  public void setDurataTot(Double durataTot) {
    this.durataTot = durataTot;
  }

  public String getCentroLavoro() {
    return centroLavoro;
  }

  public void setCentroLavoro(String centroLavoro) {
    this.centroLavoro = centroLavoro;
  }

  
  public Integer getMinFine() {
    return minFine;
  }

  public void setMinFine(Integer minFine) {
    this.minFine = minFine;
  }

  public Integer getMinInizio() {
    return minInizio;
  }

  public void setMinInizio(Integer minInizio) {
    this.minInizio = minInizio;
  }

  public Integer getOccorrenze() {
    return occorrenze;
  }

  public void setOccorrenze(Integer occorrenze) {
    this.occorrenze = occorrenze;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABFERMIGG;
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
    keys.add(FCODCAUS);
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //linea
    types.add(Types.NUMERIC); //data
    
    
    types.add(Types.CHAR); //codice causale
    types.add(Types.NUMERIC); //durata
    types.add(Types.INTEGER); //ora inizio
    types.add(Types.INTEGER); //ora fine
    types.add(Types.INTEGER); //occorrenze
    
    types.add(Types.CHAR); //utente
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
    
    return types;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FLINEA, this.centroLavoro);
    fieldsValue.put(FDATARIF, this.dataRifN);
    fieldsValue.put(FCODCAUS, this.codCausale);
    fieldsValue.put(FMINUTITOT, this.durataTot);
    fieldsValue.put(FORAINI, this.minInizio);
    fieldsValue.put(FORAFIN, this.minFine);
    fieldsValue.put(FOCCORRENZE, this.occorrenze);
    fieldsValue.put(FUTINS, CostantsColomb.UTDEFAULT);
    fieldsValue.put(FDTINS, DateUtils.getDataSysLong());
    fieldsValue.put(FORAINS, DateUtils.getOraSysString());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FAZIENDA, CostantsColomb.AZCOLOM);
    fieldsValue.put(FLINEA, this.centroLavoro);
    fieldsValue.put(FDATARIF, this.dataRifN);
    
    
    
    return fieldsValue;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
  
  
}

