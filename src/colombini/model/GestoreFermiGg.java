/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.model.persistence.FermiGgLineaBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class GestoreFermiGg {
  
  private Map totFermiTipo; //mappa contenente per ogni tipologia del fermo la sua durata totale
  private Map fermiGgLinea; //mappa contenente il numero di fermi della singola linea per la singola giornata
  private Long dataRifN;
  private String linea;
  private Integer azienda;
  private Boolean durataTotSecondi=false;
  
  
  public final static String INSERTSTM=" INSERT INTO "+ColombiniConnections.getAs400LibPersColom()+"."+FermiGgLineaBean.TABFERMIGG 
                 + " ( ZFCONO, ZFPLGR, ZFDTRF, ZFCODC, ZFDRTM ,"
                 + "  ZFORIN ,ZFORFN ,ZFOCCN ,ZFRGUT ,ZFRGDT ,ZFRGOR  )"
                 + " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
  
  
  public GestoreFermiGg(Long dataRif,Integer azienda,String linea) {
    totFermiTipo=new HashMap();
    fermiGgLinea=new HashMap();
    this.dataRifN=dataRif;
    this.linea=linea;
    this.azienda=azienda;
  }
  
  public GestoreFermiGg(Long dataRif,Integer azienda,String linea,Boolean drtSec) {
    totFermiTipo=new HashMap();
    fermiGgLinea=new HashMap();
    this.dataRifN=dataRif;
    this.linea=linea;
    this.azienda=azienda;
    this.durataTotSecondi=drtSec;
  }
  
  
  public Map getFermiGgLinea() {
    return fermiGgLinea;
  }

  public Map getTotFermiTipo() {
    return totFermiTipo;
  }

  public Long getDataRifN() {
    return dataRifN;
  }

  public String getLinea() {
    return linea;
  }
  
  /**
   * Data la lista dei fermi giornalieri e la mappa delle causali associate ad una linea
   * costruisce le mappe con i totali dei fermi per tipologia e 
   * @param fermi
   * @param causali 
   */
  public void elabOccorrrenzeFermi (List<List> fermiGg,Map<String,CausaliLineeBean> causali){
    if(fermiGg==null || fermiGg.isEmpty())
      return;
    
    Integer nguasti=0;
    for(List fermo:fermiGg){
      String causale=ClassMapper.classToString(fermo.get(0));
      Double durata=ClassMapper.classToClass(fermo.get(1),Double.class);
      
      //attenzione causale deve essere la chiave della mappa di CausaliLineeBean
      CausaliLineeBean cl=causali.get(causale);
      if(cl!=null){
        //sommo il valore totale della tipologia del fermo(per calcolo OEE)
        loadTotalizzatoreFermi(cl.getTipo(), durata);
        loadFermoGg(cl, durata);
        if(CausaliLineeBean.TIPO_CAUS_GUASTO.equals(cl.getTipo())){
          nguasti++; 
        }
      }
    }
    totFermiTipo.put(CausaliLineeBean.TIPO_NGUASTI, nguasti);
  }
 
  
  
  
  private void loadTotalizzatoreFermi(String tipo,Double valore){
    Double valTmp=valore;
    if(totFermiTipo.containsKey(tipo)){
      valTmp+=(Double) totFermiTipo.get(tipo);
    }
    
    totFermiTipo.put(tipo,valTmp);
    
  }
  
  
  private void loadFermoGg(CausaliLineeBean cl,Double valore){
    if(fermiGgLinea.containsKey(cl.getCampoChiave())){
      FermiGgLineaBean fgTmp=(FermiGgLineaBean) fermiGgLinea.get(cl.getCampoChiave());
      fgTmp.setOccorrenze(fgTmp.getOccorrenze()+1);
      fgTmp.setDurataTot(fgTmp.getDurataTot()+valore);
      
      fermiGgLinea.put(cl.getCampoChiave(), fgTmp);
      
    }else{
      FermiGgLineaBean fg=new FermiGgLineaBean();
      fg.setAzienda(cl.getAzienda());
      fg.setCodCausale(cl.getCodice());
      fg.setDataRifN(dataRifN);
      fg.setCentroLavoro(cl.getCentroLavoro());
      fg.setDurataTot(valore);
      fg.setOccorrenze(Integer.valueOf(1));
      fg.setMinInizio(Integer.valueOf(0));
      fg.setMinFine(Integer.valueOf(0));
      
      fermiGgLinea.put(cl.getCampoChiave(), fg);
    }
  }

 
  /**
   * Prima cancella e poi inserisce i dati relativi ai fermi giornalieri della linea
   * @param con
   * @throws SQLException 
   */
  public void gestDtFLGg(Connection con) throws SQLException{
    //cancello i dati della giornata
    deleteFermiLineaGg(con);
    //inserisco i dati giornalieri
    insertFermiLineaGg(con,this.getFermiGgLinea());
  }
  
  
  public void storeDatiGgLinea(PersistenceManager pm){
    
    //cancellazione dei dati
    FermiGgLineaBean fm=new FermiGgLineaBean();
    fm.setAzienda(azienda);
    fm.setCentroLavoro(linea);
    fm.setDataRifN(dataRifN);
    try{
      pm.deleteDtFromBean(fm);
      //inserimento dati
      pm.storeDtFromBeans(getListBeansFermiGg());
    } catch(SQLException ex){
      _logger.error(" Problemi in fase di cancellazione/inserimento dei dati relativi ai fermi della linea"+ex.getMessage());
    }
  }
  
  public List getListBeansFermiGg(){
    List list=new ArrayList();
    Set keys=fermiGgLinea.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      list.add(fermiGgLinea.get(key));
    }
    
    return list;
  }
  
  
  public void insertFermiLineaGg(Connection con ,List<FermiGgLineaBean> dati) throws SQLException{
    PreparedStatement ps = null;
   
    for(FermiGgLineaBean bean:dati){
      try{
        ps = con.prepareStatement(INSERTSTM);
        ps.setInt(1, bean.getAzienda()); //fisso l'azienda 
        ps.setString(2, bean.getCentroLavoro());
        ps.setLong(3, bean.getDataRifN()); 
        ps.setString(4, bean.getCodCausale());
        Double durata=bean.getDurataTot();
        if(durataTotSecondi){  
          durata=durata/new Double(60);
        }
        ps.setDouble(5, durata);
        ps.setInt(6, bean.getMinInizio());
        ps.setInt(7, bean.getMinFine());
        ps.setInt(8, bean.getOccorrenze());
        
        ps.setString(9, "UTJCOLOM");
        ps.setString(10, DateUtils.getDataSysString());
        ps.setString(11, DateUtils.getOraSysString());
        
        ps.execute();

      }finally{
        if(ps!=null)
          ps.close();
      } 
    }
  }
  
  
  
  
  public void insertFermiLineaGg(Connection con ,Map<String,FermiGgLineaBean> dati) throws SQLException{
    List list=new ArrayList();
    Set keys=dati.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      list.add(dati.get(key));
    }
    insertFermiLineaGg(con, list);
  }
  
  public void deleteFermiLineaGg(Connection con) throws SQLException{
    if(con==null)
      throw new SQLException("Nessuna connessione dati--> Impossibile proseguire nella cancellazione.");
    
    if( dataRifN==null || linea==null)
      throw new SQLException("Dati non completi--> Impossibile proseguire nella cancellazione.");
 
    
    String delete=" delete from "+ColombiniConnections.getAs400LibPersColom()+"."+FermiGgLineaBean.TABFERMIGG
                 +" where ZFCONO = ? "
                 +" and ZFPLGR = ? "
                 +" and ZFDTRF = ? ";
    
    
//    String delete=" delete from "+Connections.LIBAS400MCOBMODDTA+"."+FermiGgLineaBean.TABFERMIGG
//                 +" where ZFCONO="+JDBCDataMapper.objectToSQL(azienda)
//                 +" and ZFPLGR="+JDBCDataMapper.objectToSQL(linea)
//                 +" and ZFDTRF="+JDBCDataMapper.objectToSQL(dataRifN);
//  
    PreparedStatement st=con.prepareStatement(delete);
    st.setInt(1, azienda);
    st.setString(2, linea);
    st.setLong(3, dataRifN); 
    
    st.execute(); 
  }
  
  
  private static final Logger _logger = Logger.getLogger(GestoreFermiGg.class);
  
}
