/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.util;

import colombini.costant.CostantsColomb;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.AvProdCommLineaBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.QryLineeLavToElab;
import colombini.query.datiComm.carico.QryFacilityBU;
import db.CustomQuery;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.MapsMessagesElab;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mail.MailMessageInfoBean;
import mail.MailSender;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;


/**
 *
 * @author lvita
 */
public class DatiCommUtils {
  
  public static final String TABDTLINEELAV = "ZDPLLC";
  public final static String VOLTOTCOMMESSA="V3TOTCOM";
  public final static String NORDTOTCOMMESSA="NORDTOTCOM";
  
  private static DatiCommUtils instance;
  
  
  
  
  public static DatiCommUtils getInstance(){
    if(instance==null){
      instance= new DatiCommUtils();
    }
    
    return instance;
  }
  
  public Long getDataCommessa(Integer commessa,List<List> ggCom){
    Long dataC=null;
    
    for(List infoC:ggCom){
      Integer comm=ClassMapper.classToClass(infoC.get(1),Integer.class);
      
      if(comm.equals(commessa))
        dataC=ClassMapper.classToClass(infoC.get(0),Long.class);
    }
    
    return dataC;
  }
  
  
  public String getStringCommessa(Integer commessa){
    String s="";
    if(commessa==null)
      return s;  
    
    if(commessa<100){
      s="0"+commessa.toString();
    }else{
      s=commessa.toString();
    }
    
    return s;
  }
  
  
  /**
   * Restituisce un bean di tipo <b> CaricoCommLineaBean </b> in base ai parametri forniti
   * @param codLinea
   * @param dataComm
   * @param comm
   * @param um
   * @param div
   * @param val
   * @return 
   */
  public CaricoCommLineaBean getBeanCaricoComLinea(String codLinea,Long dataComm,Integer comm,String um,String div,Double val){
    if(val==null || val<=0)
      return null;
    
    CaricoCommLineaBean bean=new CaricoCommLineaBean();
    bean.setCommessa(comm);
    bean.setLineaLav(codLinea);
    bean.setDataRifN(dataComm);
    bean.setDivisione(div);
    bean.setUnitaMisura(um);
    bean.setValore(val);
      
    return bean;  
  }
  
  /**
   * Aggiunge alla lista fornita un nuovo bean di tipo <b> CaricoCommLineaBean </b>
   * @param list
   * @param codLinea
   * @param dataComm
   * @param comm
   * @param um
   * @param div
   * @param val 
   */
  public void addInfoCaricoComBu(List<CaricoCommLineaBean> list,String codLinea,Long dataComm,Integer comm, String um,String div,Double val) {
       
    CaricoCommLineaBean b=getBeanCaricoComLinea(codLinea, dataComm, comm,  um, div, val);
    if(b!=null)
      list.add(b); 
    
  } 
  
  public List <CaricoCommLineaBean> getListDtCommFromQuery(Connection con ,CustomQuery qry,LineaLavBean ll,String tipoInfo) throws DatiCommLineeException{
    List dati=new ArrayList();
    ResultSetHelper rsh=null;
    ResultSet rs = null;
    
    if(con==null || ll==null || qry==null)
      return null;
    
    try {  
      String sql=qry.toSQLString();
      _logger.info(sql);
      rsh = new ResultSetHelper(con, sql);
      rs=rsh.resultSet;
      
      while(rs.next()){
        addInfoCaricoComBu(dati,ll.getCodLineaLav(),ll.getDataCommessa(),ll.getCommessa(),ll.getUnitaMisura(),
                rs.getString(1), rs.getDouble(tipoInfo));

      }
      
    } catch (SQLException ex) {
      _logger.error("Impossibile caricare i dati di produzione per la linea:"+ll.getCodLineaLav()+" ->"+ex.getMessage()); 
      throw new DatiCommLineeException(ex);
      
    } catch (QueryException ex) {
      _logger.error("Impossibile caricare i dati di produzione per la linea:"+ll.getCodLineaLav()+" ->"+ex.getMessage()); 
    }
    
    return dati;
  }
  
  
  public Long getDataCommessa(Long dataElab){
    String appoDt=dataElab.toString();
    Long dataC=new Long(appoDt.substring(2));
    
    return dataC;
  }
  
  
  
  /**
   * Restituisce una mappa in cui la chiave è la divisione ed il valore il volume totale per una data commessa
   * @param  con
   * @return Map mappa contentente i valori
   * @throws QueryException
   * @throws SQLException 
   */
  public Map <String,Double> loadVolumiCommessa(Connection con ,Integer commessa,Long dataC) throws QueryException, SQLException{
    Map mapVolumi=new HashMap();
    ResultSetHelper rsh=null;
    ResultSet rs = null;
    Double volTot=Double.valueOf(0);
    
    if(con==null )
      return null;
    
//    QryColliVolumiCommessa qry=new QryColliVolumiCommessa();
//    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DatiCommUtils.getInstance().getDataCommessa(dataC));
    
//      QryVolumiComm qry=new QryVolumiComm();
//      qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, CostantsColomb.AZCOLOM);
//      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, commessa);
//      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, dataC);
    
//    String qryS= qry.toSQLString();
//    _logger.info(" Query Volumi :"+qryS);
    
    String sql= " select cfdivi as "+FilterFieldCostantXDtProd.FD_DIVISIONE+ ", zsvoas as "+FilterFieldCostantXDtProd.FD_VOLUME+
                " from mcobmoddta.zcocar inner join CFACIL on zscono=cfcono and zswhlo=cfwhlo "+
                " where 1=1 "+
                " and zscono="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+
                " and zscdld="+JDBCDataMapper.objectToSQL(dataC)+
                " and zsccom="+JDBCDataMapper.objectToSQL(commessa)+
                " order by 1";
 
    
    
    
    rsh = new ResultSetHelper(con, sql);
    rs=rsh.resultSet;

    while(rs.next()){
      String divisione=rs.getString(FilterFieldCostantXDtProd.FD_DIVISIONE);
      Double volume=rs.getDouble(FilterFieldCostantXDtProd.FD_VOLUME);
      volTot+=volume;
      
      mapVolumi.put(divisione,volume);  
    }
    if(volTot>0)
      mapVolumi.put(VOLTOTCOMMESSA, volTot);
    
    return mapVolumi;
  }
  
  
  public Map <String,Double> loadNOrdCommessa(Connection con ,Integer commessa,Long dataC) throws QueryException, SQLException{
    Map mapOrdini=new HashMap();
    ResultSetHelper rsh=null;
    ResultSet rs = null;
    Long valTot=Long.valueOf(0);
    String condPrior="\n ";
    if(con==null )
      return null;
    
    if(commessa<=360){//commessa Std
      condPrior+=" AND (EAOPRI = 1 or EAOPRI = 2 or EAOPRI = 5) ";
    }else if(commessa>360 && commessa<392){//nano
      condPrior+=" AND EAOPRI = 7 ";
    }else if(commessa>796){//mini
      condPrior+=" AND EAOPRI = 0 ";
    }
    
//   
    StringBuffer sql= new StringBuffer(" select oadivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                                             ", COUNT(DISTINCT OAORNO) as ").append(FilterFieldCostantXDtProd.FD_NUMORD).append(
                " FROM MCOBMODDTA.ZCOMME,MVXBDTA.DCONSI,MVXBDTA.DOHEAD,MVXBDTA.OOHEAD ").append(
                " where 1=1 ").append(
                " AND ZCCONO=DACONO ").append(
                " AND ZCCDLD=DACDLD " ).append(
                " AND DACONO=EACONO " ).append(
                " AND DACONN=EACONN " ).append(
                " AND EACONO=OACONO " ).append(
                " and EAWHLO=OAWHLO " ).append(
                " AND EAORNO=OAORNO ").append(
                " and zccono=").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(
                " and zccdld=").append(JDBCDataMapper.objectToSQL(dataC)).append(
                " and zcccom=").append(JDBCDataMapper.objectToSQL(commessa)).append(
                condPrior).append(        
                " group by oadivi  order by 1");
 
    
    rsh = new ResultSetHelper(con, sql.toString());
    rs=rsh.resultSet;

    while(rs.next()){
      String divisione=rs.getString(FilterFieldCostantXDtProd.FD_DIVISIONE);
      Long valore=rs.getLong(FilterFieldCostantXDtProd.FD_NUMORD);
      valTot+=valore;
      
      mapOrdini.put(divisione,valore);  
    }
    if(valTot>0)
      mapOrdini.put(NORDTOTCOMMESSA, valTot);
    
    return mapOrdini;
  }
  
  
  /**
   * Torna una mappa per la corrispondenza Facility/divisione
   * @param Connection con
   * @return Map mappa
   * @throws SQLException
   * @throws QueryException 
   */
  public Map loadMapFADIV(Connection con) throws SQLException, QueryException{
    Map mappa=new HashMap();
    ResultSetHelper rsh;
    ResultSet rs;
    
    QryFacilityBU qry=new QryFacilityBU();
    qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
    rsh = new ResultSetHelper(con, qry.toSQLString());
    rs=rsh.resultSet;        
    while (rs.next()){
      mappa.put(rs.getString(FilterFieldCostantXDtProd.FD_FACILITY),rs.getString(FilterFieldCostantXDtProd.FD_DIVISIONE));
    }
    
    return mappa;         
  }
  
  public String getDivFromFacility(String facility,Map mappa){
    
    if(mappa.containsKey(facility))
      return (String)mappa.get(facility);
    
    return "";
  }
  
  
  public Map loadMapDittDIV(Connection con) throws SQLException, QueryException{
    Map mappa=new HashMap();
    
    ResultSetHelper rsh;
    ResultSet rs;
    
    String qry=" select cffacn as  "+FilterFieldCostantXDtProd.FD_DITTA +" ,cfdivi as "+FilterFieldCostantXDtProd.FD_DIVISIONE+
               " from mcobmoddta.ZPILCOM";
    
    
    rsh = new ResultSetHelper(con, qry);
    rs=rsh.resultSet;        
    while (rs.next()){
      String dittaL=rs.getString(FilterFieldCostantXDtProd.FD_DITTA);
      String d=dittaL.substring(20, 21);
      
      mappa.put(d,rs.getString(FilterFieldCostantXDtProd.FD_DIVISIONE));
    }
    
    
    return mappa; 
  }
  
  
  /**
   * Restituisce una lista di bean relativi alle linee lavorative che devono essere elaborate per il carico di lavoro 
   * della commessa
   * @param con
   * @param commessa
   * @param dataCommessa
   * @param lineeToElab
   * @return List di <LineaLavBean>
   */
  public List loadLineeForCaricoCom(Connection con, Integer commessa, Long dataCommessa,List lineeToElab) throws SQLException, QueryException {
    ResultSetHelper rsh = null;
    ResultSet rs = null;
    if (con == null) {
      return null;
    }
    QryLineeLavToElab qry = new QryLineeLavToElab();
    qry.setFilter(FilterFieldCostantXDtProd.FT_LINEATT, "Y");
    if(lineeToElab!=null && lineeToElab.size()>0)
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEETOELAB, lineeToElab.toString());
    
    return getLineeLavCom(con, qry, commessa, dataCommessa);
  }
  
  
  /**
   * Restituisce una lista di bean <B>LineaLavBean</B> relativi alle linee lavorative che devono essere elaborate per l'avanzamento della produzione
   * della commessa
   * @param con
   * @param execClass
   * @param comm
   * @param dataRif
   * @return List di <LineaLavBean>
   * @throws SQLException
   * @throws QueryException
   */
  public List loadLineeForAvanProd(Connection con, Boolean execClass, Integer comm, Long dataRif,List lineeToElab) throws SQLException, QueryException {
    if (con == null) {
      return null;
    }
    QryLineeLavToElab qry = new QryLineeLavToElab();
    qry.setFilter(FilterFieldCostantXDtProd.FT_LINEAVP, "Y");
    if (execClass) {
      qry.setFilter(FilterFieldCostantXDtProd.FT_LOADAVP, "Y");
    }
    if(lineeToElab!=null && lineeToElab.size()>0)
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEETOELAB, lineeToElab.toString());
    
    return getLineeLavCom(con, qry, comm, dataRif);
  }

  
  public List getLineeLavCom(Connection con, CustomQuery qry, Integer comm, Long dtCom) throws SQLException, QueryException {
    List beans = new ArrayList();
    ResultSetHelper rsh = new ResultSetHelper(con, qry.toSQLString());
    ResultSet rs = rsh.resultSet;
    while (rs.next()) {
      LineaLavBean linea = new LineaLavBean();
      linea.setAzienda(rs.getInt(FilterFieldCostantXDtProd.FD_AZIENDA));
      if (comm != null) {
        linea.setCommessa(comm);
      }
      if (dtCom != null) {
        linea.setDataCommessa(dtCom);
      }
      linea.setAzienda(rs.getInt(FilterFieldCostantXDtProd.FD_AZIENDA));
      String st = rs.getString(FilterFieldCostantXDtProd.FD_CODLINEA);
      linea.setCodLineaLav(st.trim());
      String st2 = rs.getString(FilterFieldCostantXDtProd.FD_DESCLINEA);
      linea.setDescLineaLav(st2.trim());
      linea.setUnitaMisura(rs.getString(FilterFieldCostantXDtProd.FD_UNMIS));
      
      linea.setNumOpeTurno(rs.getDouble(FilterFieldCostantXDtProd.FD_NUMOPERTURNO));
      linea.setNumOreStd(rs.getDouble(FilterFieldCostantXDtProd.FD_NUMORESTD));
      linea.setNumTurni(rs.getDouble(FilterFieldCostantXDtProd.FD_NUMTURNI));
      linea.setOreFlexGg(rs.getDouble(FilterFieldCostantXDtProd.FD_OREFLEXGG));
      linea.setOreFlexTrn(rs.getDouble(FilterFieldCostantXDtProd.FD_OREFLEXTR));
      
      linea.setCadenzaOraria(rs.getDouble(FilterFieldCostantXDtProd.FD_CADORARIA));
      linea.setAllineamentoCom(rs.getInt(FilterFieldCostantXDtProd.FD_ALLCOMM));
      linea.setAnticipoComm(rs.getDouble(FilterFieldCostantXDtProd.FD_ANTCOMM));
      
      linea.setFlagL(rs.getInt(FilterFieldCostantXDtProd.FD_FLAG));
      linea.setFlagAvP(rs.getInt(FilterFieldCostantXDtProd.FD_FLAGAVP));
      String st3 = rs.getString(FilterFieldCostantXDtProd.FD_CONDQUERY);
      if (st3 != null && !StringUtils.IsEmpty(st3.trim())) {
        linea.setQueryCondition(st3.trim());
      }
      String st4 = rs.getString(FilterFieldCostantXDtProd.FD_CLASSEXEC);
      if (st4 != null && !StringUtils.IsEmpty(st4.trim())) {
        linea.setClassExec(st4.trim());
      }
      String st5 = rs.getString(FilterFieldCostantXDtProd.FD_CLASSAVP);
      if (st5 != null && !StringUtils.IsEmpty(st5.trim())) {
        linea.setClassExecAvP(st5.trim());
      }
      beans.add(linea);
    }
    return beans;
  }

  public void gestMessageMail(Connection con, String codMessage, MapsMessagesElab m, Integer comm, Long dataRif) {
    if (m.getMapErrorsElab().isEmpty() && m.getMapWarningsElab().isEmpty()) {
      _logger.info("Nessun messaggio di errore e warning presente. Invio mail non necessario");
      return;
    }
    MailSender ms = new MailSender();
    MailMessageInfoBean mailM = new MailMessageInfoBean(codMessage);
    mailM.retriveInfo(con);
    StringBuilder text = new StringBuilder();
    text.append(gestMapMessage(m.getMapWarningsElab(), comm, dataRif, " WARNINGS "));
    text.append(gestMapMessage(m.getMapErrorsElab(), comm, dataRif, " ERRORI "));
    mailM.setText(text.toString());
    ms.send(mailM);
  }

  private String gestMapMessage(Map mapMess, Integer comm, Long dataRif, String type) {
    StringBuilder text;
    text = new StringBuilder("");
    Set keysE = mapMess.keySet();
    Iterator iter = keysE.iterator();
    int i = 0;
    while (iter.hasNext()) {
      if (i == 0 && comm != null && dataRif != null) {
        text.append(type).append(" rilevati durante l'eleaborazione per commessa: ").append(comm).append(" e dataRif: ").append(dataRif).append(" --> \n ");
      }
      i++;
      String linea = (String) iter.next();
      text.append("\n Linea ").append(linea).append(" : \n");
      List<String> errorList = (List<String>) mapMess.get(linea);
      for (String msE : errorList) {
        text.append(" - ").append(msE);
      }
      text.append(" ; ");
    }
    return text.toString();
  }

  public Map getColliCommLineeLavColom(Connection conAs400 ,Integer comm,Date dataRif) throws SQLException{
    Map lineeColli=new HashMap<Integer,String>();
    List<List> result=new ArrayList();
    String select ="select cllinp, count(*) as totC from mcobfilp.sc"+comm+"col where  clnart=0 group by cllinp";
    ResultSetHelper.fillListList(conAs400, select, result);
    
    
    for(List rc:result){
      lineeColli.put( ClassMapper.classToClass(rc.get(0),Integer.class), ClassMapper.classToClass(rc.get(1),Integer.class) );
    }
    return lineeColli;
  }
  
  public Map getColliCommLineeLavFebal(Connection conSqlDesmosFeb, Integer comm,Date dataRif) throws SQLException {
    PreparedStatement ps=null;
    ResultSet rs=null;
    Map commFeb=new HashMap();
    
    String commessa=getStringCommessa(comm);
    Integer anno=DateUtils.getYear(dataRif);
    String dataAppo=anno+"0101";
    
    StringBuilder qry=new StringBuilder("  SELECT linea, COUNT(distinct codice_collo) ").append(
                      "\n FROM DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL   ").append(
                      "\n where 1=1").append(
                      " and commessa = ?").append(
                      "\n and dataSpedizione >= ?").append(
                      "\n group by linea ");  
    
    try{
      ps=conSqlDesmosFeb.prepareStatement(qry.toString());
      ps.setString(1, commessa);
      ps.setString(2, dataAppo.toString());
     
      
      rs=ps.executeQuery();
      
      while(rs.next()){
        commFeb.put(ClassMapper.classToClass(rs.getString(1),Integer.class),rs.getInt(2));
      }
      
    } finally{
      try{
        if(ps!=null)
          ps.close();
        if(rs!=null)
          rs.close();
       
      }catch(SQLException s){
        _logger.error("Errore in fase di chiususra dello statement --> "+s.getMessage()); 
      }
    }
    
    return commFeb;
  }
  
  /**
   * Torna  un AvProdCommLineaBean con valorizzato il campo relativo ai pz da produrre per una certa commessa
   * @param con
   * @param linea
   * @return
   * @throws SQLException 
   */
  public AvProdCommLineaBean loadDatiAvCStd(Connection con ,LineaLavBean linea) throws SQLException {
    AvProdCommLineaBean avBean=null;
    
    String stm="SELECT SUM(ZDVALE) NPZCOM FROM MCOBMODDTA.ZDPCOM WHERE 1=1 and ZDUNME<>'V3' and ZDCONO=? and ZDCOMM = ? and ZDDTCO=? and ZDPLGR=?";
    PreparedStatement pstmt = con.prepareStatement(stm); 
    pstmt.setInt(1, linea.getAzienda());
    pstmt.setInt(2, linea.getCommessa());
    pstmt.setLong(3, linea.getDataCommessa());
    pstmt.setString(4, linea.getCodLineaLav());

    ResultSet rs=pstmt.executeQuery();
    Double totPz=Double.valueOf(0);
    while(rs.next()){
      totPz=rs.getDouble("NPZCOM");
    }
    if(totPz>0)
      avBean=new AvProdCommLineaBean(linea.getAzienda(),linea.getCommessa(), linea.getDataCommessa(), linea.getCodLineaLav(), totPz.intValue());
      
    
    
    return avBean;
    
   }
  
   public Boolean isLotto1Valorizzata(Connection conDb,Long lancio) throws SQLException{
   Boolean finish=Boolean.FALSE;
   
   String qry="select top 10 * from tab_ET where commissionNo=" + JDBCDataMapper.objectToSQL(lancio);
   Object [] obj=ResultSetHelper.SingleRowSelect(conDb, qry);
   if(obj!=null && obj.length>0){
      finish=Boolean.TRUE;
   }
   
   return finish;
  }
   
  public Boolean isImaAnteValorizzata(Connection conDb,String lancio) throws SQLException{
   Boolean finish=Boolean.FALSE;
   
   String qry="select top 10 * from tab_ET where commissionNo=" + JDBCDataMapper.objectToSQL(lancio);
   Object [] obj=ResultSetHelper.SingleRowSelect(conDb, qry);
   if(obj!=null && obj.length>0){
      finish=Boolean.TRUE;
   }
   
   return finish;
  }
  
  public static Boolean isCommessaFebal(Connection conn,Long comm,String dataComm) throws SQLException{
  //Metodo per verificare se una commessa ha ordini febal - fare il join con co_mxdivi
  Boolean finish=Boolean.FALSE;
  String sql= " SELECT * FROM mcobmoddta.ZCOMOR  INNER JOIN MVXBDTA.OOHEAD                 \n" +
                                        "ON ZOCONO=OACONO AND ZOORNO=OAORNO                             \n" +
                                        "WHERE 1=1 \n" +
                                        "AND ZOCCOM="+JDBCDataMapper.objectToSQL(comm)+"  \n" +
                                        "AND ZOCDLD="+JDBCDataMapper.objectToSQL(dataComm)+"\n" +
                                        "AND ZOCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+ 
                                        "AND OADIVI IN (SELECT  CTSTKY FROM MVXBDTA.CSYTAB WHERE CTCONO=030     \n" +
                                        "AND CTSTCO='CO_MXDIVI')";
  
  Object [] obj=ResultSetHelper.SingleRowSelect(conn, sql);
  if(obj!=null && obj.length>0){
      finish=Boolean.TRUE;
   }
   
   return finish;
  }
  
  public static Boolean isCommessaColombini(Connection conn,Long comm,String dataComm) throws SQLException{
  //Metodo per verificare se una commessa ha ordini febal - fare il join con co_mxdivi
  Boolean finish=Boolean.FALSE;
  String sql= " SELECT * FROM mcobmoddta.ZCOMOR  INNER JOIN MVXBDTA.OOHEAD                 \n" +
                                        " ON ZOCONO=OACONO AND ZOORNO=OAORNO                             \n" +
                                        " WHERE 1=1 \n" +
                                        " AND ZOCCOM="+JDBCDataMapper.objectToSQL(comm)+"  \n" +
                                        " AND ZOCDLD="+JDBCDataMapper.objectToSQL(dataComm)+"\n" +
                                        " AND ZOCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+ 
                                        " AND OADIVI NOT IN (SELECT  CTSTKY FROM MVXBDTA.CSYTAB WHERE CTCONO=030     \n" +
                                        " AND CTSTCO='CO_MXDIVI')";
  
  Object [] obj=ResultSetHelper.SingleRowSelect(conn, sql);
  if(obj!=null && obj.length>0){
      finish=Boolean.TRUE;
   }
   
   return finish;
  }
    
  private static final Logger _logger = Logger.getLogger(DatiCommUtils.class);   
  
}
