/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.IBeanPersCRUD;

/**
 *
 * @author lvita
 */
public class RitardoCommLineaBean implements IBeanPersCRUD{

  
  public final static String TABNAME="ZDPCRT";
  
  //campi Tabelle
  
  public final static String ZRCONO="ZRCONO";
  public final static String ZRDTRF="ZRDTRF";
  public final static String ZRPLGR="ZRPLGR";
  public final static String ZRRSC0="ZRRSC0";
  public final static String ZRRSC1="ZRRSC1";
  public final static String ZRRSC2="ZRRSC2";
  public final static String ZRRSC3="ZRRSC3";
  public final static String ZRRSC4="ZRRSC4";
  public final static String ZRRSC5="ZRRSC5";
  public final static String ZRRSC6="ZRRSC6";
  public final static String ZRRSC7="ZRRSC7";
  public final static String ZRRSC8="ZRRSC8";
  public final static String ZRRSC9="ZRRSC9";
  
  public final static String ZRRSTT="ZRRSTT";
  public final static String ZRRSPR="ZRRSPR";
  //probabilmente da segare
  public final static String ZRROPU="ZRROPU";
  public final static String ZRROPR="ZRROPR";
  public final static String ZRGFLX="ZRGFLX";
  public final static String ZRTOUT="ZRTOUT";
  
  
  public final static String ZRLMDT="ZRLMDT";
  
  
  private Integer azienda;
  private Long dataRifN;
  private String cdL;
  
  private Double ritOrePuntuale=Double.valueOf(0);
  private Double ritOreProgressivo=Double.valueOf(0);
  private Double ggConFlex=Double.valueOf(0);
  private Double ggTurni=Double.valueOf(0);

  private Double oreG=Double.valueOf(0);; //indica il numero di ore lavorate in una giornata dalla linea
  private Double pzH=Double.valueOf(0);; //indica il numero di pezzi prodotti in un ora
  
  
  
  private Integer numCommesse; //numero commesse in essere a sistema
  private Integer allComm; //indica il numero di commesse di avanzamento della linea
  
  
  private List<Integer> residuiCommesse;
  
  
  
  public RitardoCommLineaBean(Integer azienda,String cdL,Long dataRifN,Integer allCom,Integer numComm){
    this.azienda=azienda;
    this.cdL=cdL;
    this.dataRifN=dataRifN;
    this.allComm=allCom;
    this.numCommesse=numComm;
    
    
    
    prepareListResidui();
  }
  
  
  //costruttore da eliminare
  public RitardoCommLineaBean(Integer azienda,String cdL,Long dataRifN,Double orelav,Double pzOra,Integer allCom,Integer numComm){
    this.azienda=azienda;
    this.cdL=cdL;
    this.dataRifN=dataRifN;
    this.oreG=orelav;
    this.pzH=pzOra;
    this.allComm=allCom;
    this.numCommesse=numComm;
    
    this.ritOrePuntuale=Double.valueOf(0);
    this.ritOreProgressivo=Double.valueOf(0);
    this.ggConFlex=Double.valueOf(0);
    this.ggTurni=Double.valueOf(0);
    
    prepareListResidui();
  }
  
  private void prepareListResidui(){
    residuiCommesse=new ArrayList();
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
    residuiCommesse.add(Integer.valueOf(0));
  }
  
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public String getCdL() {
    return cdL;
  }

  public void setCdL(String cdL) {
    this.cdL = cdL;
  }


  public Double getRitOrePuntuale() {
    return ritOrePuntuale;
  }

  public void setRitOrePuntuale(Double ritOrePuntuale) {
    this.ritOrePuntuale = ritOrePuntuale;
  }

  public Double getRitOreProgressivo() {
    return ritOreProgressivo;
  }

  public void setRitOreProgressivo(Double ritOreProgressivo) {
    this.ritOreProgressivo = ritOreProgressivo;
  }

  public Double getGgConFlex() {
    return ggConFlex;
  }

  public void setGgConFlex(Double ggConFlex) {
    this.ggConFlex = ggConFlex;
  }

  public Double getGgTurni() {
    return ggTurni;
  }

  public void setGgTurni(Double ggTurni) {
    this.ggTurni = ggTurni;
  }
  
  public void addResiduoComm(Integer idxComm,Integer value){
    if(idxComm==null || idxComm<0 || idxComm>9)
      return;
    
    if(value<0)
      value=Integer.valueOf(0);
    
    residuiCommesse.set(idxComm, value);
  }
  
  
//  public void calcRitardoLinea(){
//    loadRitardoPuntuale();
//    loadRitardoProgressivo();
//    loadGgFlex();
//    loadTurniOut();
//  }
  
  public Integer getResiduoTotale(){
    Integer resTot=Integer.valueOf(0);
    
    for(int i=0;i<residuiCommesse.size(); i++){
      resTot+=(Integer)residuiCommesse.get(i);
    }
    
    return resTot;
  }
  
  
  public Integer getResiduoParziale(){
    Integer resParz=Integer.valueOf(0);
    
    
    
    for(int i=0;i<=allComm; i++){
      resParz+=(Integer)residuiCommesse.get(i);
    }
    
    
    return resParz;
  }
  
  
//  public void loadRitardoPuntuale(){
//    Integer resTotale=Integer.valueOf(0);
//    for(int i=0;i<=allComm; i++){
//      resTotale+=(Integer)residuiCommesse.get(i);
//    }
//    Double rit=resTotale/pzH;
//    rit=rit-oreG;
//    
//    this.ritOrePuntuale=rit;
//  }
//  
//  
//  public void loadRitardoProgressivo(){
//    Integer resTot=Integer.valueOf(0);
//    
//    for(int i=0;i<numCommesse; i++){
//      resTot+=(Integer)residuiCommesse.get(i);
//    }
//    
//    Double rit=resTot/pzH;
//    rit=rit- ((numCommesse-allComm)*oreG);
//    
//    this.ritOreProgressivo=rit;
//    
//  }
//  
//  public void loadGgFlex(){
//    if(this.ritOreProgressivo>0)
//      this.ggConFlex=ritOreProgressivo/new Double(1.5);
//  }
//  
//  public void loadTurniOut(){
//    if(this.ritOreProgressivo>0)
//      this.ggTurni=ritOreProgressivo/new Double(7.25);
//  }
  
  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    return null;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map mappa=new HashMap();
    mappa.put(ZRCONO,azienda);
    mappa.put(ZRDTRF,dataRifN);
    mappa.put(ZRPLGR,cdL);
    
    mappa.put(ZRRSC0,residuiCommesse.get(0));
    mappa.put(ZRRSC1,residuiCommesse.get(1));
    mappa.put(ZRRSC2,residuiCommesse.get(2));
    mappa.put(ZRRSC3,residuiCommesse.get(3));
    mappa.put(ZRRSC4,residuiCommesse.get(4));
    mappa.put(ZRRSC5,residuiCommesse.get(5));
    mappa.put(ZRRSC6,residuiCommesse.get(6));
    mappa.put(ZRRSC7,residuiCommesse.get(7));
    mappa.put(ZRRSC8,residuiCommesse.get(8));
    mappa.put(ZRRSC9,residuiCommesse.get(9));
    
    mappa.put(ZRRSTT,"".equals(cdL) ? Integer.valueOf(0) : getResiduoTotale());
    mappa.put(ZRRSPR,"".equals(cdL) ? Integer.valueOf(0): getResiduoParziale());
    
    mappa.put(ZRROPU,ritOrePuntuale);
    mappa.put(ZRROPR,ritOreProgressivo);
    mappa.put(ZRGFLX,ggConFlex);        
    mappa.put(ZRTOUT,ggTurni);
    
    mappa.put(ZRLMDT,new Date());
    
    return mappa;
    
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map mappa=new HashMap();
    mappa.put(ZRCONO,azienda);
    mappa.put(ZRDTRF,dataRifN);
//    mappa.put(ZRPLGR,cdL);
    
    return mappa;
  }

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
    List fields=new ArrayList();
    fields.add(ZRCONO);    fields.add(ZRDTRF);   fields.add(ZRPLGR);
    fields.add(ZRRSC0);    fields.add(ZRRSC1);   fields.add(ZRRSC2);
    fields.add(ZRRSC3);    fields.add(ZRRSC4);   fields.add(ZRRSC5);
    fields.add(ZRRSC6);    fields.add(ZRRSC7);   fields.add(ZRRSC8);
    fields.add(ZRRSC9);    fields.add(ZRRSTT);   fields.add(ZRRSPR);
    fields.add(ZRROPU);    fields.add(ZRROPR);   fields.add(ZRGFLX);    
    fields.add(ZRTOUT);   fields.add(ZRLMDT);
    
    return fields;
  }

  @Override
  public List<String> getKeyFields() {
    List keys=new ArrayList();
    
    keys.add(ZRCONO);
    keys.add(ZRDTRF);
    keys.add(ZRPLGR);
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    types.add(Types.INTEGER);
    types.add(Types.NUMERIC);   //dataRiferimento
    types.add(Types.CHAR);      //centro di lavoro
    
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    
    types.add(Types.INTEGER);   
    types.add(Types.INTEGER);   
    
    
    types.add(Types.DOUBLE);
    types.add(Types.DOUBLE);
    types.add(Types.DOUBLE);
    types.add(Types.DOUBLE);
    
    types.add(Types.TIMESTAMP);
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
}
