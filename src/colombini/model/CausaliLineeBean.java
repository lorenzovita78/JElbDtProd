package colombini.model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */











import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Classe per la gestione delle informazioni delle causali delle linee -->tabella MCOBMODDTA.ZDPLCA
 * @author lvita
 */
public class CausaliLineeBean {
  
  public final static String TABCAUSLINEE="ZDPLCA";
  
  public final static String FLAGCODICE="COD";
  public final static String FLAGDESC="DESC";
  
  
  //tipologie causali
  public final static String TIPO_CAUS_ORENONPROD="IM";
  public final static String TIPO_CAUS_GUASTO="GU";
  public final static String TIPO_CAUS_PERDGEST="PG";
  public final static String TIPO_CAUS_SETUP="SE";
  public final static String TIPO_CAUS_SETUP_AGG="S1"; //setup aggiuntivo solo per alcune linee
  public final static String TIPO_CAUS_VELRID="VR";
  public final static String TIPO_CAUS_MICROFRM="MF";
  
  public final static String TIPO_CAUS_SCARTO="SC";
  public final static String TIPO_CAUS_RECUPERI="RE";
  public final static String TIPO_CAUS_RIPASSI="RI";
  
  public final static String TIPO_CAUS_NONDEFINITA="NN";
  
  public final static String TIPO_NGUASTI="NG";
  
  private Long idCausale;
  private Integer azienda;
  private String centroLavoro;
  private String codice;
  private String descrizione;
  private String tipo;
  private String flagKey; //flag per indicare se la chiave sarà il codice o la descrizione della causale
 
  
  public Integer getAzienda() {
    return azienda;
  }

  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public String getCentroLavoro() {
    return centroLavoro;
  }

  public void setCentroLavoro(String centroLavoro) {
    this.centroLavoro = centroLavoro;
  }

  public String getCodice() {
    return codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public String getDescrizione() {
    return descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getFlagKey() {
    return flagKey;
  }

  public void setFlagKey(String flagKey) {
    this.flagKey = flagKey;
  }
  
  
  public String getCampoChiave(){
    if(FLAGDESC.equals(flagKey)){
      return descrizione;
    }
    
    return codice;
  } 

  public Long getIdCausale() {
    return idCausale;
  }

  public void setIdCausale(Long idCausale) {
    this.idCausale = idCausale;
  }
  
  
  /**
   * Torna la lista delle causali legate ad una determinata linea di lavoro
   * @param Connection con
   * @param Integer azienda
   * @param String linea
   * @param String flagK
   * @return List lista
   * @throws SQLException 
   */
  public static synchronized List<CausaliLineeBean> getCausaliLinea(Connection con ,Integer azienda,String linea,String flagK) throws SQLException{
    List lista=new ArrayList();
    ResultSetHelper rsh=null;
    ResultSet rs = null;
    
    String sql=" select ZCIDCA,ZCCODI, ZCDESC ,ZCTIPO from "+ColombiniConnections.getAs400LibPersColom()+"."+CausaliLineeBean.TABCAUSLINEE+" where 1=1 "+
               " and ZCCONO="+JDBCDataMapper.objectToSQL(azienda)+
               " and ZCPLGR="+JDBCDataMapper.objectToSQL(linea);
    
    if(con==null)
      throw new SQLException("Connessione nulla -> Impossibile eseguire la connessione al database!!");
    
    rsh = new ResultSetHelper(con, sql);
    rs=rsh.resultSet;
      
    while(rs.next()){
        CausaliLineeBean bean=new CausaliLineeBean();
        bean.setAzienda(azienda);
        bean.setCentroLavoro(linea);
        bean.setIdCausale(rs.getLong("ZCIDCA"));
        bean.setCodice(rs.getString("ZCCODI").trim());
        bean.setDescrizione(rs.getString("ZCDESC").trim());
        bean.setTipo(rs.getString("ZCTIPO"));
        bean.setFlagKey(flagK);
        lista.add(bean);
     }
    
    return lista;
  }

  /**
   * Torna una mappa dove la chiave rappresenta il codice e/o la descrizione della causale e il valore è l'oggetto stesso.
   * Se ci dovessero essere problemi nel reperimento delle causali gestisce l'eccezione e torna una mappa vuota
   * @param con
   * @param azienda
   * @param linea
   * @param flagK
   * @return Map mappa
   */
  public static synchronized Map getMapCausaliLinea(Connection con,Integer azienda,String linea,String flagK) {
    Map mappa=new HashMap();
    try {
      List<CausaliLineeBean> list=getCausaliLinea(con, azienda, linea, flagK);
      if(list==null || list.isEmpty())
        _logger.warn("Causali associati alla linea "+linea +" non presenti su database!! ");
      
      for(CausaliLineeBean cl:list){
        mappa.put(cl.getCampoChiave(), cl);
      }
    
    } catch (SQLException ex) {
      _logger.error("Errore in fase di caricamento delle causali associate alla linea "+linea+" ->"+ex.getMessage());
    }
    
    return mappa;
  }
  
  private static final Logger _logger = Logger.getLogger(CausaliLineeBean.class);
}
