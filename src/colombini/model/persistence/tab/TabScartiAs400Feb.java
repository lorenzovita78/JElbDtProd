/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence.tab;

import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import db.persistence.IBeanPersCRUD;
import java.util.Date;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class TabScartiAs400Feb implements IBeanPersCRUD{

  private final static String TABLENAME="ZPM020";
  
  private final static String TABLEFIEDS="Z2CONO,Z2MFNO,Z2PRNO,Z2SCQT,Z2SCRE,Z2PLGR,Z2PROJ,Z2ELNO,Z2TRDT,Z2TRTM,Z2FIDT,Z2FITM,Z2STAT,Z2MSID,Z2MSG";
  
  public static final String STATOSCARTOTOPROC = "10";
  
  private Integer az;
  private Integer odP;
  private String codArt;
  private Double qta;
  private String causale;
  private String cdLTrns;
  private String commessa;
  private String collo;
  private Long dataTrnN;
  private Integer oraTrnN;
  private Long dataFineN;
  private Integer oraFineN;
  private String stato;
  
  
  private String idMsg;
  private String descrErr;

  private Date dataTransazione;
  
  
  public TabScartiAs400Feb(Integer az,String stato,Integer ordineProd,Date dataTransazione){
    this.az=az;
    this.odP=ordineProd;
    this.dataTransazione=dataTransazione;
    this.stato=stato;
    
    dataTrnN=DateUtils.getDataForMovex(dataTransazione);
    oraTrnN=DateUtils.getOraForMovex(dataTransazione);
    
    this.commessa="";
    this.collo="";
    dataFineN=Long.valueOf(0);
    oraFineN=Integer.valueOf(0);
    this.idMsg="";
    this.descrErr="";
    
  }

  
  public Integer getAz() {
    return az;
  }

  public void setAz(Integer az) {
    this.az = az;
  }

  public Integer getOdP() {
    return odP;
  }

  public void setOdP(Integer odP) {
    this.odP = odP;
  }

  public String getCodArt() {
    return codArt;
  }

  public void setCodArt(String codArt) {
    this.codArt = codArt;
  }

  public Double getQta() {
    return qta;
  }

  public void setQta(Double qta) {
    this.qta = qta;
  }

  public String getCausale() {
    return causale;
  }

  public void setCausale(String causale) {
    this.causale = causale;
  }

  public String getCdLTrns() {
    return cdLTrns;
  }

  public void setCdLTrns(String cdLTrns) {
    this.cdLTrns = cdLTrns;
  }

  public String getCommessa() {
    return commessa;
  }

  public void setCommessa(String commessa) {
    this.commessa = commessa;
  }

  public String getCollo() {
    return collo;
  }

  public void setCollo(String collo) {
    this.collo = collo;
  }

  public Long getDataTrnN() {
    return dataTrnN;
  }

  public void setDataTrnN(Long dataTrnN) {
    this.dataTrnN = dataTrnN;
  }

  public Integer getOraTrnN() {
    return oraTrnN;
  }

  public void setOraTrnN(Integer oraTrnN) {
    this.oraTrnN = oraTrnN;
  }

  public Long getDataFineN() {
    return dataFineN;
  }

  public void setDataFineN(Long dataFineN) {
    this.dataFineN = dataFineN;
  }

  public Integer getOraFineN() {
    return oraFineN;
  }

  public void setOraFineN(Integer oraFineN) {
    this.oraFineN = oraFineN;
  }

  
  public String getStato() {
    return stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public String getIdMsg() {
    return idMsg;
  }

  public void setIdMsg(String idMsg) {
    this.idMsg = idMsg;
  }

  public String getDescrErr() {
    return descrErr;
  }

  public void setDescrErr(String descrErr) {
    this.descrErr = descrErr;
  }
  
  
  @Override
  public String getLibraryName() {
//    return "MCOBMODDEM";
    return ColombiniConnections.getAs400LibPersFebal();
  }

  @Override
  public String getTableName() {
    return TABLENAME; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABLEFIEDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    List keys=new ArrayList();
    keys.add("Z2CONO");
    keys.add("Z2MFNO");
    keys.add("Z2PRNO");
    keys.add("Z2TRDT");
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    types.add(Types.NUMERIC);
    
    types.add(Types.CHAR); //causale
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    // 
    types.add(Types.INTEGER); //date
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    
    
    types.add(Types.CHAR);//stato
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    
    
    return types;
  }

  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    return null;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map value=new HashMap<String,Object>();
    
    
    value.put("Z2CONO",az);
    value.put("Z2MFNO", odP);
    value.put("Z2PRNO",codArt);
    value.put("Z2SCQT",qta);
    value.put("Z2SCRE",causale);
    value.put("Z2PLGR",cdLTrns);
    value.put("Z2PROJ",commessa);
    value.put("Z2ELNO",collo);
    value.put("Z2TRDT",dataTrnN);
    value.put("Z2TRTM",oraTrnN);
    value.put("Z2FIDT",dataFineN);
    value.put("Z2FITM",oraFineN);
    value.put("Z2STAT",stato);
    
    value.put("Z2MSID",idMsg);
    value.put("Z2MSG",descrErr);
    
    
    
    return value;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map keys=new HashMap<String,Object>();
//    keys.add("Z3CONO");
//    keys.add("Z3STAT");
//    keys.add("Z3TIRE");
//    keys.add("Z3LNID");
//    keys.add("Z3DATE");
    
    return keys;
  }

  @Override
  public Boolean validate() {
    if(StringUtils.isEmpty(codArt) || dataTrnN==null )
      return Boolean.FALSE;
    
    return Boolean.TRUE;
  }

 
  public String getDesc() {
     String s= "BEAN ZPM020 --> ";
     
    return s;
  }
  
  
  
  
  
}
