/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *  Classe che ricava il tempo ciclo dato un articolo e il suo relativo ordine
 *  @author lvita
 */
public class CalcoloTempoCicloSingleton {
  
  
  
  
  
  private final String STMCOLLI="SELECT clnart,trim(CLARTI) articolo, trim(CLNROR) nordine,clriga riga"
                                      + " FROM mcobmoddta.SCXXXCOL"                             
                                      + " where CLNART<>0"
                                      + " and clcomm=?"
                                      + " and clncol=?";
  
  private final String STMCONFIG=" SELECT obcfin "
                                       +" FROM ooline  "
                                       +" WHERE OBCONO=?"            
                                       +" and OBORNO=?"       
                                       +" and OBPONR=?" 
                                       +" and OBPOSX=?";
  
  
  private final String STMFEATURE=" select QJECVS,QJFTID,QJOPTN,QJOPNV"
                                         +" from MPDCDF "
                                         +" where 1=1 "
                                         +" and QJCONO=?"
                                         +" and QJCFIN=?";
  
  
  private final String STMTCICLO=" select POPITI,PNFTID,PNOPTN,PNINCE "
                                        +" from MVXBDTA.MPDOPE left outer join MVXBDTA.MPDOMA"
                                        +" on pocono=pncono and pofaci=pnfaci and poprno=pnprno and poopno=pnopno "
                                        +" where 1=1 "
                                        +" and POCONO=? "
                                        +" and POFACI=? "
                                        +" and POPRNO=? "
                                        +" and POPLGR=? ";
  
  
  private String QRYTCICLO=" select POPITI,PNFTID,PNOPTN,PNINCE "
                                        +" from MVXBDTA.MPDOPE left outer join MVXBDTA.MPDOMA"
                                        +" on pocono=pncono and pofaci=pnfaci and poprno=pnprno and poopno=pnopno "
                                        +" where 1=1 ";
  
  private String STMTCICLO2=" select POPITI,POFACI,POPRNO,POPLGR "
                                        +" from MVXBDTA.MPDOPE "
                                        +" where 1=1 "
                                        +" and POCONO = ? "                                      
                                        +" and TRIM(POFACI) = ? ";
  
//                                        +" and POFACI=? "
//                                        +" and POPRNO=? "
//                                        +" and POPLGR=? ";
  
  private final String STMFACI= " select MWFACI FROM MPDWCT,MITWHL "
                                       +" WHERE PPCONO=MWCONO"
                                       +" and PPWHLO=MWWHLO "
                                       +"and PPCONO= ? AND  PPPLGR=?";
 
  
  
  private static CalcoloTempoCicloSingleton instance;
  
  
  
  public static CalcoloTempoCicloSingleton getInstance(){
    if(instance==null){
      instance= new CalcoloTempoCicloSingleton();
    }
    
    return instance;
  }
  
 /**
   * Data una commessa e il numero di collo cerca tutti gli articoli appartenti e calcola il tempo ciclo
   * @param commessa
   * @param numCollo
   * @return Double tempoCiclo
   * @throws SQLException
   * @throws QueryException 
   */
  public Double getTempoCicloArticolo(Long commessa,Long numCollo,String centroLavoro) throws SQLException, QueryException {
    Connection conAs400=null; 
    try { 
      conAs400=ColombiniConnections.getAs400ColomConnection();
      return getTempoCicloArticolo(conAs400,commessa, numCollo,centroLavoro);
    }finally{
      if(conAs400!=null)
        try {
        conAs400.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura di connessione al db");
      }
    }
  }
  
  public Double getTempoCicloArticolo(Connection con,Long commessa,Long numCollo,String centroLavoro) throws SQLException, QueryException{
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    List articoliList=new ArrayList();
     
    try{
      pstmt = con.prepareStatement(STMCOLLI); 
      pstmt.setLong(1, commessa);
      pstmt.setLong(2, numCollo);
      rs=pstmt.executeQuery();
      while(rs.next()){
        List info=new ArrayList();
        String articolo=rs.getString("ARTICOLO");
        String numordine=rs.getString("NORDINE");
        Integer riga=ClassMapper.classToClass(rs.getString("RIGA"), Integer.class);
        info.add(articolo.trim());
        info.add(numordine.trim());
        info.add(riga);
        articoliList.add(info);        
      }
     }finally{
      if(pstmt!=null)
        pstmt.close();
      if(rs!=null)
        rs.close();
    } 
    return getTempoCicloArticoli(con,centroLavoro,articoliList);
  } 
  
  
  private Double getTempoCicloArticoli(Connection con,String centroLavoro,List articoliList) throws QueryException, SQLException{
    Double tempoCiclo=Double.valueOf(0);
    String articoloOld="";
    String numOrdOld="";
    Integer rigaOld=Integer.valueOf(0);
    if(articoliList==null|| articoliList.isEmpty())
      return tempoCiclo;
    
    for(Object rec:articoliList){
      String artTmp=(String)((List)rec).get(0); 
      String numOTmp=(String)((List)rec).get(1);
      Integer riga=(Integer)((List)rec).get(2);
      if( (artTmp!=null && !artTmp.equals(articoloOld)) ||  (numOTmp!=null && !numOTmp.equals(numOrdOld)) 
           || (riga!=null && !riga.equals(rigaOld))   ){
        tempoCiclo+=getTempoCicloArticolo(con,centroLavoro, artTmp, numOTmp,riga);
      }
      articoloOld=artTmp;
      numOrdOld=numOTmp;
      rigaOld=riga;
    }
    
    return tempoCiclo;
  } 
  
  
  public Double getTempoCicloArticolo(Connection con,String centroLavoro,String articolo,String numOrdine,Integer nrigaOrd) throws QueryException, SQLException{
    Double tempoCiclo=Double.valueOf(0);
    List tempiCicloOperaz=new ArrayList();
    List ordFeatures=new ArrayList();
    //se per l'articolo esiste un solo tempo ciclo allora si prende quello
    //altrimenti c'è da fare il giro lungo!!
    String facility=getFacilityFromCLav(con, centroLavoro);
    tempiCicloOperaz=getListTempiCicloOper(con, centroLavoro, facility, articolo);
    if(tempiCicloOperaz!=null && tempiCicloOperaz.size()==1){
      OperEsplosione oper=(OperEsplosione)tempiCicloOperaz.get(0);
      //se la mpdoma è vuota allora posso prendere il tempo ciclo
      if(oper.getCaratteristica()==null)
        tempoCiclo=oper.getTempoCiclo();
      
    }else {
      Long confOrdine=getConfigurazioneOrdine(con, numOrdine, nrigaOrd);
      ordFeatures=getListaFeatureConfigOrd(con, confOrdine);
      tempoCiclo=compareOperationFeature(tempiCicloOperaz,ordFeatures);
    }
    return tempoCiclo;
  }
  
  
  
  public String getFacilityFromCLav(Connection con ,String centroLavoro) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String facility="";
    try{
      ps = con.prepareStatement(STMFACI); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, centroLavoro);
      
      rs=ps.executeQuery();
      while(rs.next()){
        facility=rs.getString("MWFACI");
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    return facility.trim();
  }
  
  
  
  private List<OperEsplosione> getListTempiCicloOper(Connection con,String centroLavoro,String facility,String articolo) throws SQLException{
    PreparedStatement pst = null;
//    AS400JDBCPreparedStatement st400=null;
    ResultSet rs = null;
    List listTciclo=new ArrayList();
    
    
    
    try{
//      st400=(AS400JDBCPreparedStatement)con.prepareStatement(STMTCICLO2);
      pst = con.prepareStatement(STMTCICLO2); 
      pst.setInt(1, new Integer(30)); //fisso l'azienda 
      pst.setString(2, facility);
//      pst.setString(3, JDBCDataMapper.objectToSQL(articolo));
//      pst.setString(4, JDBCDataMapper.objectToSQL(centroLavoro));
      
//      ParameterMetaData mt=st400.getParameterMetaData();
//      String prm=mt.toString();
      rs=pst.executeQuery();
     
//      POPITI,POOPNO,PNFTID,PNOPTN,PNINCE
      while(rs.next()){
        OperEsplosione oper=new OperEsplosione();
        oper.setTempoCiclo(rs.getDouble("POPITI")); //tpciclo
        oper.setCaratteristica(rs.getString("PNFTID")); //feature
        String opz=rs.getString("PNOPTN");
        if(opz!=null)
          oper.setOpzione(opz); //option 
        
        oper.setInclEscl(rs.getString("PNINCE")); //includi/escludi
        
        listTciclo.add(oper);
      }
     }finally{
      if(pst!=null)
        pst.close();
      if(rs!=null)
        rs.close();
    } 
    
    return listTciclo;
  }
  
  
  private List<OperEsplosione> getListaOperArticolo(Connection con,String centroLavoro,String facility,String articolo) throws SQLException{
    ResultSet rs = null;
    List listTciclo=new ArrayList();
    ResultSetHelper rsh=null;
    
    
    try{
      String query=QRYTCICLO;
      query+=" and POCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM);
      query+=" and POFACI="+JDBCDataMapper.objectToSQL(facility);
      query+=" and POPRNO="+JDBCDataMapper.objectToSQL(articolo);
      query+=" and POPLGR="+JDBCDataMapper.objectToSQL(centroLavoro);
      
      rsh = new ResultSetHelper(con, query);
     
//      POPITI,POOPNO,PNFTID,PNOPTN,PNINCE
      while(rsh.resultSet.next()){
        OperEsplosione oper=new OperEsplosione();
        oper.setTempoCiclo(rsh.resultSet.getDouble("POPITI")); //tpciclo
        oper.setCaratteristica(rsh.resultSet.getString("PNFTID")); //feature
        String opz=rsh.resultSet.getString("PNOPTN");
        if(opz!=null)
          oper.setOpzione(opz.trim()); //option 
        
        oper.setInclEscl(rsh.resultSet.getString("PNINCE")); //includi/escludi
        
        listTciclo.add(oper);
      }
     }finally{
      if(rsh!=null)
        rsh.close();
      if(rs!=null)
        rs.close();
    } 
    
    return listTciclo;
  }
  
  
  private Long getConfigurazioneOrdine(Connection con,String numOrdine,Integer numRiga) throws QueryException, SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Long configurazione=Long.valueOf(-1) ;
    Integer riga=numRiga/100;
    Integer sottoriga=numRiga-(riga*100);
    try{
      ps = con.prepareStatement(STMCONFIG); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, numOrdine);
      ps.setInt(3, riga); 
      ps.setInt(4, sottoriga);
      rs=ps.executeQuery();
      while(rs.next()){
        configurazione=rs.getLong("OBCFIN");
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return configurazione;
  }
  
  
  private List<ConfCaratteristica> getListaFeatureConfigOrd(Connection con,Long config) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    List features=new ArrayList();
    
    try{
      ps = con.prepareStatement(STMFEATURE); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setLong(2, config);
      rs=ps.executeQuery();
      while(rs.next()){
        ConfCaratteristica conf=new ConfCaratteristica();
        conf.setCaratteristica(rs.getString("QJFTID")); //feature
        String appoOpz=rs.getString("QJOPTN");
        if(appoOpz!=null)
          conf.setOpzione(appoOpz.trim()); //opzione
        
        
        features.add(conf);
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return features;
  }
  
  
  
  private Double compareOperationFeature(List<OperEsplosione> tempiCicloOperaz, List<ConfCaratteristica> ordFeatures) {
    Double val=Double.valueOf(0);
    for(OperEsplosione oper:tempiCicloOperaz){
      //se la caratteristica è di inclusione la confronto con le configurazioni
      if(oper.isInclusione()){
        if(existOperazFromConf(oper, ordFeatures))
          val+=oper.getTempoCiclo();
      }else{ //se la caratteristica è di esclusione
        if(notExistOperazFromConf(oper, ordFeatures))
          val+= oper.getTempoCiclo();
      }
    }

    return val;
  }
  
  /**
   * Verifica se all'operazione in distinta c'è una caratteristica corrispondente
   * @param oper
   * @param ordFeatures
   * @return true se  
   */
  private boolean existOperazFromConf(OperEsplosione oper,List<ConfCaratteristica> ordFeatures ){
    if(ordFeatures==null || ordFeatures.isEmpty())
      return false;
    
    for(ConfCaratteristica conf:ordFeatures){
      if(conf.getCaratteristica().equals(oper.getCaratteristica())){
        if(conf.getOpzione().equals(oper.getOpzione()))
          return true;
      }
    }
    
    return false;
  }
  
  private boolean notExistOperazFromConf(OperEsplosione oper,List<ConfCaratteristica> ordFeatures ){
    if(ordFeatures==null || ordFeatures.isEmpty())
      return false;
    
    for(ConfCaratteristica conf:ordFeatures){
      if(conf.getCaratteristica().equals(oper.getCaratteristica())){
        if(conf.getOpzione().equals(oper.getOpzione()))
          return false;
      }
    }
    
    
    return true;
  }
  
  
  
  public class OperEsplosione{
    private Double tempoCiclo;
    private String caratteristica;
    private String opzione;
    private String inclEscl;
    
    
    

    public Double getTempoCiclo() {
      return tempoCiclo;
    }

    public void setTempoCiclo(Double tempoCiclo) {
      this.tempoCiclo = tempoCiclo;
    }
    
    public String getCaratteristica() {
      return caratteristica;
    }

    public void setCaratteristica(String caratteristica) {
      this.caratteristica = caratteristica;
    }

    public String getInclEscl() {
      return inclEscl;
    }

    public void setInclEscl(String inclEscl) {
      this.inclEscl = inclEscl;
    }

    public String getOpzione() {
      return opzione;
    }

    public void setOpzione(String opzione) {
      this.opzione = opzione;
    }
    
    public boolean isInclusione(){
      if(inclEscl!=null && inclEscl.equals("1"))
        return true;
      
      return false;
    }
  }
  
  public class ConfCaratteristica{
    
    private String caratteristica;
    private String opzione;
    
    
    public String getCaratteristica() {
      return caratteristica;
    }

    public void setCaratteristica(String caratteristica) {
      this.caratteristica = caratteristica;
    }

    public String getOpzione() {
      return opzione;
    }

    public void setOpzione(String opzione) {
      this.opzione = opzione;
    }
     
  }
  
  
  private static final Logger _logger = Logger.getLogger(CalcoloTempoCicloSingleton.class);
}
