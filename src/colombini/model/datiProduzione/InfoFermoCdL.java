/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.datiProduzione;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.DatiProdPWGeneral;
import db.ConnectionsProps;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import db.persistence.IBeanPersCRUD;
import db.persistence.ISequence;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class InfoFermoCdL implements Cloneable,IBeanPersCRUD,ISequence {
  
  private Integer id;
  private Integer idTurno;
  private String cdl;
  private Date dataRif;
  private Date oraInizio;
  private Date oraFine;
  private String note;
 
  private String tipo;
  private Integer idCausale;
  private String descCausale;
  private Double perc;

  public final static String FGIDFM="FGIDFM";
  public final static String FGIDCO="FGIDCO";
  public final static String FGCONO="FGCONO";
  public final static String FGDTRF="FGDTRF";
  public final static String FGIDCA="FGIDCA";
  public final static String FGTMIN="FGTMIN";
  public final static String FGTMFN="FGTMFN";
  public final static String FGNOTE="FGNOTE";
  public final static String FGLMUT="FGLMUT";
  public final static String FGLMDT="FGLMDT";
  
  public final static String TABLE_NAME="ZDPWFG";
  public final static String SEQUENCE_NAME="SEQ_ZDPWFG";
  
  
  public InfoFermoCdL(Integer idTurno, String cdl, Date dataRif) {
    this.idTurno = idTurno;
    this.cdl = cdl;
    this.dataRif = dataRif;
  }

  public Integer getIdTurno() {
    return idTurno;
  }

  public String getCdl() {
    return cdl;
  }

  public Date getDataRif() {
    return dataRif;
  }

  
  public Date getOraInizio() {
    return oraInizio;
  }

  public void setOraInizio(Date oraInizio) {
    this.oraInizio = oraInizio;
  }

  public Date getOraFine() {
    return oraFine;
  }

  public void setOraFine(Date oraFine) {
    this.oraFine = oraFine;
  }

  public Long getSec() {
    if(oraInizio!=null && oraFine!=null)
      return DateUtils.numSecondiDiff(oraInizio, oraFine);
      
    return Long.valueOf(0);
  }

  
  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Integer getIdCausale() {
    return idCausale;
  }

  public void setIdCausale(Integer idCausale) {
    this.idCausale = idCausale;
  }

  public String getDescCausale() {
    return descCausale;
  }

  public void setDescCausale(String descCausale) {
    this.descCausale = descCausale;
  }

  public Double getPerc() {
    return perc;
  }

  public void setPerc(Double perc) {
    this.perc = perc;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
  
  
  @Override
  public String toString() {
    return id.toString()+" - IDTURNO : "+idTurno.toString()+" - IDCAUSALE : "+idCausale+" - DATARIF : "+dataRif.toString(); //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
  public static List<InfoFermoCdL> retrieveFermi(Connection con,Integer idTurnoCdl) throws SQLException{
    List<List> fermiGg=new ArrayList();
    List <InfoFermoCdL> l= new ArrayList();
    
    if(idTurnoCdl==null)
      return l;
    
    String library=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ConnectionsProps.AS400_LIB_PERS));
    
    String select=" select fgdtrf, fgtmin, fgtmfn, "+
                   "       zctipo, zcidca, zcdesc, zcperc, trim(zcplgr) ,"+
                   " (((hour(fgtmfn)*60+minute(fgtmfn))-(hour(fgtmin)*60+minute(fgtmin))))*60 as totSec "+
                   " from "+ library+ ".zdpwfg inner join "+ library+ ".zdplca on fgidca=zcidca "+
                   " where 1=1"+
                   " and fgidco="+JDBCDataMapper.objectToSQL(idTurnoCdl);
    
    ResultSetHelper.fillListList(con, select, fermiGg);
    for(List record:fermiGg){
      Date dataR=ClassMapper.classToClass(record.get(0),Date.class);
      String cdlRif=ClassMapper.classToString(record.get(7));
      InfoFermoCdL bean=new InfoFermoCdL(idTurnoCdl, cdlRif, dataR);
      Date oraIni=ClassMapper.classToClass(record.get(1),Date.class);  
      Date oraFin=ClassMapper.classToClass(record.get(2),Date.class);
      oraIni=DateUtils.AddTime(dataR, oraIni);
      oraFin=DateUtils.AddTime(dataR, oraFin);
      bean.setOraInizio(oraIni);
      bean.setOraFine(oraFin);
      bean.setTipo(ClassMapper.classToString(record.get(3)));
      bean.setIdCausale(ClassMapper.classToClass(record.get(4),Integer.class));
      bean.setDescCausale(ClassMapper.classToString(record.get(5)));
      bean.setPerc(ClassMapper.classToClass(record.get(6),Double.class));
      l.add(bean);
    }
      
    
    
    return l;
  }
 
  
  @Override
  public InfoFermoCdL clone(){
    InfoFermoCdL newFermo=new InfoFermoCdL(this.idTurno, this.cdl, this.dataRif);
    newFermo.setTipo(this.getTipo());
    newFermo.setIdCausale(this.getIdCausale());
    newFermo.setDescCausale(this.getDescCausale());
    newFermo.setPerc(this.getPerc());
    newFermo.setOraInizio(this.getOraInizio());
    newFermo.setOraFine(this.getOraFine());
    
    return newFermo;
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiProdPWGeneral.class);

  @Override
  public Map<String, Object> getFieldValueMapForUpdate() {
    return null; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FGIDFM, this.id );
    fieldsValue.put(FGIDCO, this.idTurno);
    fieldsValue.put(FGCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(FGDTRF, this.dataRif);
    fieldsValue.put(FGIDCA, this.idCausale);
    fieldsValue.put(FGTMIN, this.oraInizio);
    fieldsValue.put(FGTMFN, this.oraFine);
    fieldsValue.put(FGNOTE, this.note);
    fieldsValue.put(FGLMUT, CostantsColomb.UTDEFAULT);
    fieldsValue.put(FGLMDT, DateUtils.getDataSysLong());
    
    
    return fieldsValue; 
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(FGIDFM, this.id );
    
    return fieldsValue;
  }

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLE_NAME; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getFields() {
    return Arrays.asList(FGIDFM,FGIDCO,FGCONO,FGDTRF,FGIDCA,FGTMIN,FGTMFN,FGNOTE,FGLMUT,FGLMDT); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getKeyFields() {
    return Arrays.asList(FGIDFM); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.NUMERIC);   //az
    types.add(Types.NUMERIC); //id Orario CDL
    types.add(Types.INTEGER); //azienda
    
    types.add(Types.DATE);      //data riferimento
    
    types.add(Types.INTEGER);    //id causale
    
    types.add(Types.TIME);    //ora Inizio
    types.add(Types.TIME);    //ora Fine
    
    types.add(Types.VARCHAR);    //note
    
    types.add(Types.CHAR);    //utente modifica
    types.add(Types.TIMESTAMP); //data variazione
  
    return types;
  }
  
  

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }

  @Override
  public String getSequenceName() {
    return SEQUENCE_NAME; //To change body of generated methods, choose Tools | Templates.
  }
}
