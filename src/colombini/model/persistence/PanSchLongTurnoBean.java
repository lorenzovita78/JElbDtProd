/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.costant.CostantsColomb;
import colombini.costant.MisureG1P2Costant;
import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class PanSchLongTurnoBean implements IBeanPersSIMPLE{
  
  private static final String TABLE_NAME="ZDPPSL";
  
  private static final String FIELDS="ZLCONO,ZLTURN,ZLCC18,ZLFP18,ZLFG18,ZLCC25,ZLFP25,ZLFG25,ZLCC35,ZLFP35,ZLFG35,ZLNBND,ZLVOL3,ZLRGDT";
  
  private final static String ZLCONO="ZLCONO";
  private final static String ZLTURN="ZLTURN";
  private final static String ZLCC18="ZLCC18";
  private final static String ZLFP18="ZLFP18";
  private final static String ZLFG18="ZLFG18";
  private final static String ZLCC25="ZLCC25";
  private final static String ZLFP25="ZLFP25";
  private final static String ZLFG25="ZLFG25";
  private final static String ZLCC35="ZLCC35";
  private final static String ZLFP35="ZLFP35";
  private final static String ZLFG35="ZLFG35";
  private final static String ZLNBND="ZLNBND";
  private final static String ZLVOL3="ZLVOL3";
  private final static String ZLRGDT="ZLRGDT";
  
  
  private Date turno;  //espresso in minuti;
  private Integer nCicli18;
  private Integer nCicli25;
  private Integer nCicli35;
  private Integer fogli18P; 
  private Integer fogli18G;
  private Integer fogli25P;
  private Integer fogli25G;
  private Integer fogli35P;
  private Integer fogli35G;
  private Integer nBande;
  private Double volumeTot;

  
  public PanSchLongTurnoBean(Date turno){
    this.turno=turno;
    initVar();
  }
  
  public final void initVar(){
    nCicli18=Integer.valueOf(0);
    fogli18P=Integer.valueOf(0);
    fogli18G=Integer.valueOf(0);
    nCicli25=Integer.valueOf(0);
    fogli25P=Integer.valueOf(0);
    fogli25G=Integer.valueOf(0);
    nCicli35=Integer.valueOf(0);
    fogli35P=Integer.valueOf(0);
    fogli35G=Integer.valueOf(0);
    
    nBande=Integer.valueOf(0);
    volumeTot=Double.valueOf(0);
  }
  
  
  public Date getTurno() {
    return turno;
  }

  public void setTurno(Date turno) {
    this.turno = turno;
  }

  public Integer getnCicli18() {
    return nCicli18;
  }

  public void setnCicli18(Integer nCicli18) {
    this.nCicli18 = nCicli18;
  }

  public Integer getnCicli25() {
    return nCicli25;
  }

  public void setnCicli25(Integer nCicli25) {
    this.nCicli25 = nCicli25;
  }

  public Integer getnCicli35() {
    return nCicli35;
  }

  public void setnCicli35(Integer nCicli35) {
    this.nCicli35 = nCicli35;
  }

  public Integer getFogli18P() {
    return fogli18P;
  }

  public void setFogli18P(Integer fogli18P) {
    this.fogli18P = fogli18P;
  }

  public Integer getFogli18G() {
    return fogli18G;
  }

  public void setFogli18G(Integer fogli18G) {
    this.fogli18G = fogli18G;
  }

  public Integer getFogli25P() {
    return fogli25P;
  }

  public void setFogli25P(Integer fogli25P) {
    this.fogli25P = fogli25P;
  }

  public Integer getFogli25G() {
    return fogli25G;
  }

  public void setFogli25G(Integer fogli25G) {
    this.fogli25G = fogli25G;
  }

  public Integer getFogli35P() {
    return fogli35P;
  }

  public void setFogli35P(Integer fogli35P) {
    this.fogli35P = fogli35P;
  }

  public Integer getFogli35G() {
    return fogli35G;
  }

  public void setFogli35G(Integer fogli35G) {
    this.fogli35G = fogli35G;
  }

  public Integer getnBande() {
    return nBande;
  }

  public void setnBande(Integer nBande) {
    this.nBande = nBande;
  }

  public Double getVolumeTot() {
    return volumeTot;
  }

  public void setVolumeTot(Double volumeTot) {
    this.volumeTot = volumeTot;
  }
  
  
  
  public void addInfoForThickness(Integer thick,Integer cicli,Integer fogliP,Integer fogliG){
    if(MisureG1P2Costant.SPESS14.equals(thick) || MisureG1P2Costant.SPESS16.equals(thick) || MisureG1P2Costant.SPESS18.equals(thick)  ){
      nCicli18+=cicli;
      fogli18P+=fogliP;
      fogli18G+=fogliG;
    } else  if(MisureG1P2Costant.SPESS25.equals(thick)) {
      nCicli25+=cicli;
      fogli25P+=fogliP;
      fogli25G+=fogliG;
    } else  if(MisureG1P2Costant.SPESS30.equals(thick) || MisureG1P2Costant.SPESS35.equals(thick)) {
      nCicli35+=cicli;
      fogli35P+=fogliP;
      fogli35G+=fogliG;
    }
  }
  
  
  public void addBande(Integer bande){
    nBande+=bande;
  }
  
  public void addVolume(Double v){
    volumeTot+=v;
  }
  
  
  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
     Map fieldsValue=new HashMap();
    
    fieldsValue.put(ZLCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZLTURN, this.turno);
    
    fieldsValue.put(ZLCC18, this.nCicli18);
    fieldsValue.put(ZLFP18, this.fogli18P);
    fieldsValue.put(ZLFG18, this.fogli18G);
    
    fieldsValue.put(ZLCC25, this.nCicli25);
    fieldsValue.put(ZLFP25, this.fogli25P);
    fieldsValue.put(ZLFG25, this.fogli25G);
    
    fieldsValue.put(ZLCC35, this.nCicli35);
    fieldsValue.put(ZLFP35, this.fogli35P);
    fieldsValue.put(ZLFG35, this.fogli35G);
    
    fieldsValue.put(ZLNBND, this.nBande);
    fieldsValue.put(ZLVOL3, this.volumeTot);
    fieldsValue.put(ZLRGDT, new Date());
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(ZLCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(ZLTURN, this.turno);
    
    
    return fieldsValue;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(FIELDS.split(",")); 
  }

  @Override
  public List<String> getKeyFields() {
    List keys=new ArrayList();
    keys.add(ZLCONO);
    keys.add(ZLTURN);
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.TIMESTAMP); //data turno
    
    types.add(Types.INTEGER); //cicli18
    types.add(Types.INTEGER); //fogli18P
    types.add(Types.INTEGER); //fogli18G
  
    types.add(Types.INTEGER); //cicli25
    types.add(Types.INTEGER); //fogli25P
    types.add(Types.INTEGER); //fogli25G
    
    types.add(Types.INTEGER); //cicli35
    types.add(Types.INTEGER); //fogli35P
    types.add(Types.INTEGER); //fogli35G
    
    
    types.add(Types.INTEGER); //numeroBande
    types.add(Types.NUMERIC); //volume
    types.add(Types.TIMESTAMP); //data inserimento
    
    return types;
  }
  
  
  private static final Logger _logger = Logger.getLogger(PanSchLongTurnoBean.class);
  
}
