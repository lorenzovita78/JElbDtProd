/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence.tab;

import colombini.conn.ColombiniConnections;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.DateUtils;
import db.persistence.IBeanPersCRUD;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class TabProcessiAs400 implements IBeanPersCRUD{

  private final static String TABLENAME="ZPRNO300";
  
  private final static String TABLEFIEDS="Z3CONO,Z3STAT,Z3PRWR,Z3PRIM,Z3DTIM,Z3TMIM,Z3TIRE,Z3TITR,Z3DATE,Z3TIME,Z3VDID,Z3IDTA,Z3ITNO,Z3TRQT,Z3RSCD,Z3HACD,Z3LNID,Z3NROR,Z3RTOR,Z3SUNO,Z3MSID,Z3MSG,Z3RGDT,Z3RGTM,Z3LMDT,Z3CHNO,Z3CHID";
  
  public final static Integer TIPORECSCARTI=669;
  public static final String REASONCODESCARTOIMAANTE = "1";
  public static final String REASONCODESCARTOIMATOP = "2";
  public static final String REASONCODESCARTOIMALOTTO1 = "3";
  public static final String STATOSCARTOTOPROC = "20";
  
  private Integer az;
  private String stato;
  private String idProcAcq;
  private String idProcImp;
  private Long dataImport;
  private Integer oraImport;
  private Integer tipoRecord;
  private Integer tipoTransazione;
  private String dataS;
  private String oraS;
  private Long idMisVdl;
  private Integer codTavola;
  private String codArticolo;
  private Long qta;
  private String reasonCod; //da 1 carattere
  private String causCod;
  private String lineId;
  private Long nOrdProd;
  private String retrOrdNum;
  private String codForn;
  private String idMsg;
  private String descrErr;

  
  public TabProcessiAs400(Integer az,String stato,String idProcAcq,String idProcImp,Integer tipoRecord,String reasonCode){
    this.az=az;
    this.stato=stato;
    
    this.idProcAcq=idProcAcq;
    this.idProcImp=idProcImp;
    
    this.tipoRecord=tipoRecord;
    this.reasonCod=reasonCode;
    
    
    this.idMisVdl=Long.valueOf(0);
    this.tipoTransazione=0;
    this.codTavola=0;
    this.nOrdProd=Long.valueOf(0);
    this.retrOrdNum="";
    this.codForn="";
    this.idMsg="";
    this.descrErr="";
    this.oraS="";
  }

  
  public Integer getAz() {
    return az;
  }

  public void setAz(Integer az) {
    this.az = az;
  }

  public String getStato() {
    return stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public String getIdProcAcq() {
    return idProcAcq;
  }

  public void setIdProcAcq(String idProcAcq) {
    this.idProcAcq = idProcAcq;
  }

  public String getIdProcImp() {
    return idProcImp;
  }

  public void setIdProcImp(String idProcImp) {
    this.idProcImp = idProcImp;
  }

  public Long getDataImport() {
    return dataImport;
  }

  public void setDataImport(Long dataImport) {
    this.dataImport = dataImport;
  }

  public Integer getOraImport() {
    return oraImport;
  }

  public void setOraImport(Integer oraImport) {
    this.oraImport = oraImport;
  }

  public Integer getTipoRecord() {
    return tipoRecord;
  }

  public void setTipoRecord(Integer tipoRecord) {
    this.tipoRecord = tipoRecord;
  }

  public Integer getTipoTransazione() {
    return tipoTransazione;
  }

  public void setTipoTransazione(Integer tipoTransazione) {
    this.tipoTransazione = tipoTransazione;
  }

  public String getDataS() {
    return dataS;
  }

  public void setDataS(String dataS) {
    this.dataS = dataS;
  }

  public String getOraS() {
    return oraS;
  }

  public void setOraS(String oraS) {
    this.oraS = oraS;
  }

  public Long getIdMisVdl() {
    return idMisVdl;
  }

  public void setIdMisVdl(Long idMisVdl) {
    this.idMisVdl = idMisVdl;
  }

  public Integer getCodTavola() {
    return codTavola;
  }

  public void setCodTavola(Integer codTavola) {
    this.codTavola = codTavola;
  }

  public String getCodArticolo() {
    return codArticolo;
  }

  public void setCodArticolo(String codArticolo) {
    this.codArticolo = codArticolo;
  }

  public Long getQta() {
    return qta;
  }

  public void setQta(Long qta) {
    this.qta = qta;
  }

  public String getReasonCod() {
    return reasonCod;
  }

  public void setReasonCod(String reasonCod) {
    this.reasonCod = reasonCod;
  }

  public String getCausCod() {
    return causCod;
  }

  public void setCausCod(String causCod) {
    this.causCod = causCod;
  }
  

  public String getLineId() {
    return lineId;
  }

  public void setLineId(String lineId) {
    this.lineId = lineId;
  }

  public Long getnOrdProd() {
    return nOrdProd;
  }

  public void setnOrdProd(Long nOrdProd) {
    this.nOrdProd = nOrdProd;
  }

  public String getRetrOrdNum() {
    return retrOrdNum;
  }

  public void setRetrOrdNum(String retrOrdNum) {
    this.retrOrdNum = retrOrdNum;
  }

  public String getCodForn() {
    return codForn;
  }

  public void setCodForn(String codForn) {
    this.codForn = codForn;
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
    return ColombiniConnections.getAs400LibPersColom(); 
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
    keys.add("Z3CONO");
    keys.add("Z3PRWR");
    keys.add("Z3NROR");
    keys.add("Z3IDTA");
    
    return keys;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC); //Z3DTIM
    types.add(Types.INTEGER); //Z3TMIM 
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);
    
    types.add(Types.CHAR);//Z3DATE
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC); //Z3VDID 
    types.add(Types.NUMERIC); 
    
    types.add(Types.CHAR);//Z3ITNO
    types.add(Types.NUMERIC); 
    
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC); //Z3NROR 
    
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    types.add(Types.CHAR);
    
    types.add(Types.NUMERIC); //Z3RGDT 
    types.add(Types.INTEGER); 
    
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER); 
    
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
    
    dataImport=DateUtils.getDataForMovex(new Date ());
    oraImport=DateUtils.getOraLong(new Date()).intValue();
    
    value.put("Z3CONO",az);
    value.put("Z3STAT",stato);
    value.put("Z3PRWR",idProcAcq);
    value.put("Z3PRIM",idProcImp);
    value.put("Z3DTIM",dataImport);
    value.put("Z3TMIM",oraImport);
    value.put("Z3TIRE",tipoRecord);
    value.put("Z3TITR",tipoTransazione);
    value.put("Z3DATE",dataS);
    value.put("Z3TIME",oraS);
    value.put("Z3VDID",idMisVdl);
    value.put("Z3IDTA",codTavola);
    value.put("Z3ITNO",codArticolo);
    value.put("Z3TRQT",qta);
    value.put("Z3RSCD",reasonCod);
    value.put("Z3HACD",causCod);
    value.put("Z3LNID",lineId);
    value.put("Z3NROR",nOrdProd);
    value.put("Z3RTOR",retrOrdNum);
    value.put("Z3SUNO",codForn);
    value.put("Z3MSID",idMsg);
    value.put("Z3MSG",descrErr);
    
    
    value.put("Z3RGDT",dataImport);
    value.put("Z3RGTM",oraImport);
    value.put("Z3LMDT",dataImport);
    value.put("Z3CHNO",Integer.valueOf(1));
    value.put("Z3CHID","JAVA_BTC");
    
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
    if(StringUtils.isEmpty(codArticolo))
      return Boolean.FALSE;
    
    return Boolean.TRUE;
  }

 
  public String getDesc() {
     String s= "BEAN ZPRNO300 --> IdProcImp:"+idProcImp==null ? "" : idProcImp
                           +" ; IdProcAcq:"+idProcAcq==null ? "" : idProcAcq
                           +" ; Stato :"+stato==null ? "" : stato
                           +" ; TipoRecord:"+tipoRecord==null ? "" : tipoRecord
                           +"\n ; DataImport:"+dataImport==null ? "" : dataImport
                           +" ; CodArticolo: "+codArticolo==null ? "" : codArticolo
                           +" ; Qta: "+qta==null ? "" : qta
                           +" ; dataScarto: "+dataS==null ? "" : dataS
                           +" ; causScarto: "+causCod==null ? "" : causCod;
     
     
    return s;
  }
  
  
  public String toString() {
     String s= "BEAN ZPRNO300 --> IdProcImp:"+idProcImp==null ? "" : idProcImp
                           +" ; IdProcAcq:"+idProcAcq==null ? "" : idProcAcq
                           +" ; Stato :"+stato==null ? "" : stato
                           +" ; TipoRecord:"+tipoRecord==null ? "" : tipoRecord
                           +"\n ; DataImport:"+dataImport==null ? "" : dataImport
                           +" ; CodArticolo: "+codArticolo==null ? "" : codArticolo
                           +" ; Qta: "+qta==null ? "" : qta
                           +" ; dataScarto: "+dataS==null ? "" : dataS
                           +" ; causScarto: "+causCod==null ? "" : causCod;
     
     
    return s;
  }
  
  
}
