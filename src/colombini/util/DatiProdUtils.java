/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.util;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.ColliVolumiCommessaTbl;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.QryCommesseElab;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QryCSYTAB;
import colombini.query.produzione.QryCommToProduce;
import colombini.query.produzione.QrySpedizioniGg;
import colombini.query.produzione.QueryCentriDiLavoro;
import colombini.query.produzione.QueryCommToShip;
import colombini.query.produzione.QueryConteggioColliBu;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe di utilità che contiene metodi per avere informazioni relative ai dati di produzioni
 * come commesse ,linee lavorative
 * @author lvita
 */
public class DatiProdUtils {
   
  private static DatiProdUtils instance=null;
  
  private DatiProdUtils(){
    
  }
  
  public static DatiProdUtils getInstance(){
    if(instance==null){
      instance= new DatiProdUtils();
    }
    
    return instance;
  }
  
  /**
   * Torna la lista delle commesse da produrre dal giorno corrente in avanti
   * @param con
   * @param data
   * @param onlyComm
   * @param comElab
   * @return List ogni elemento è composto dalla data di spedizione commessa (yyyyMMdd) e numero commessa
   * @throws QueryException
   * @throws SQLException 
   */
  public List<List> getListGgCommesse(Connection con,Date data,Boolean onlyComm,Boolean comElab) throws QueryException, SQLException{
    List lista = new ArrayList();  
    
    
    Long dIni=DateUtils.getDataForMovex(data);
    Long dFin=DateUtils.getDataForMovex(DateUtils.addDays(data, 60));
    QryCommToProduce qry=new QryCommToProduce();
    qry.setFilter(FilterQueryProdCostant.FTDATAINI,dIni);
    qry.setFilter(FilterQueryProdCostant.FTDATAFIN,dFin);
    
    if(onlyComm!=null && onlyComm)
      qry.setFilter(QryCommToProduce.FONLYCOMM, "Y");
   
    
    if(comElab!=null && comElab)
      qry.setFilter(QryCommToProduce.FONLYCCLOSED, "Y");
    
    ResultSetHelper.fillListList(con, qry.toSQLString(), lista);
    
    return lista;
  }
  
  /**
   * Torna la lista delle commesse elaborate ad una certa data.
   * E' possibile filtrare per numero commessa o decidere di escludere le nano
   * @param con
   * @param dataRif
   * @param commessa
   * @param excludeNano
   * @param dataGU se valorizzato a TRUE considera le commesse elabotate dalla data fornita in avanti
   * @return Lista composta da commessa,datacommessa,tipo
   * @throws QueryException
   * @throws SQLException 
   */
  public List getListCommesseGgElab(Connection con,Date dataRif,Integer commessa,Boolean excludeNano,Boolean dataGU) throws QueryException, SQLException{
    List comm=new ArrayList();
    
    QryCommesseElab qry=new QryCommesseElab();
    Long dataN=DateUtils.getDataForMovex(dataRif);
    
    
    qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
    
    if(dataGU!=null && dataGU) 
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, dataN);
    else 
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, dataN);  
    
    if(commessa!=null)
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, commessa);
    
    if(excludeNano!=null && excludeNano)
      qry.setFilter(QryCommesseElab.FEXCLUDENANO, "Y");
    
    ResultSetHelper.fillListList(con, qry.toSQLString(), comm);
    
    
    return comm;
  }
  
  
  public List getListCommesseFebalGgElab(Date dataRif,Integer commessa,Boolean excludeNano,Boolean commAnticipo,Boolean dataGU) throws QueryException, SQLException{
    List comm=new ArrayList();
    Connection con=null;
    
    try{
      con=ColombiniConnections.getAs400FebalConnection();
    
      QryCommesseElab qry=new QryCommesseElab();
      Long dataN=DateUtils.getDataForMovex(dataRif);
      
      qry.setFilter(QryCommesseElab.LIBRARYPERSAS400, ColombiniConnections.getAs400LibPersFebal());
      qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZFEBAL);
      qry.setFilter(QryCommesseElab.FCOMMFEBAL, "Y");
      
      if(dataGU!=null && dataGU) 
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, dataN);
      else 
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, dataN);  

      if(commessa!=null)
        qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, commessa);

      if(excludeNano!=null && excludeNano)
        qry.setFilter(QryCommesseElab.FEXCLUDENANO, "Y");

      if(commAnticipo!=null && commAnticipo)
        qry.setFilter(QryCommesseElab.FCOMMANTICIPO, "Y");
      
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), comm);
      
    } catch(SQLException s){
      
    } finally{
      if(con!=null)
        con.close();
    }
    
    return comm;
  }
  
  
  
  
  public  List getLineeLogicheMVX(Connection conAs400,String linea){
    String qry=" SELECT TRIM(PPPLGR) FROM MPDWCT WHERE PPIIWC="+JDBCDataMapper.objectToSQL(linea);
    List linee=new ArrayList();
    try {
      ResultSetHelper.fillListList(conAs400, qry, linee);
    } catch (SQLException ex) {
      _logger.error("Attenzione errore nel caricamento delle linee logiche legate della linea fisica "+linea+" : "+ex.getMessage() );
    }
    
    return linee;
  }
  
  public  List getElsCsyTab(Connection conAs400,Integer az,String div,String tabName,String key){
    if(az==null || div==null || StringUtils.IsEmpty(tabName))
      return null;
    
    List els=new ArrayList();
    try {
      QryCSYTAB qry=new QryCSYTAB();
      qry.setFilter(FilterQueryProdCostant.FTAZIENDA, az);
      qry.setFilter(FilterQueryProdCostant.FTDIVISION, div);
      qry.setFilter(FilterQueryProdCostant.FTCONSTCSYTAB, tabName);
      if(!StringUtils.IsEmpty(key))
        qry.setFilter(FilterQueryProdCostant.FTKEYCSYTAB, key);
      
      ResultSetHelper.fillListList(conAs400, qry.toSQLString(), els);
    } catch (SQLException ex) {
      _logger.error("Attenzione errore nel caricamento degli elementi della tabella generica "+tabName+" : "+ex.getMessage() );
    } catch (QueryException q){
       _logger.error("Attenzione errore nel caricamento degli elementi della tabella generica "+ tabName+" "+q.getMessage() );      
    }
    
    return els;
  }
  
  public  List getElsCsyTab(Connection conAs400,Integer az,String div,String tabName){
   
    
    return getElsCsyTab(conAs400, az, div, tabName, null);
  }
  
  /**
   * Carica i dati relativi a numero di colli e volumi per ogni singola Bu di una data commessa sull' archivio  ZDPCCV
   * @param con
   * @param commessa
   * @param dataCommessa
   * @throws SQLException
   * @throws QueryException 
   */
  public void elabColliVolumiCommessa(Connection con,Integer commessa,Long dataCommessa) throws SQLException, QueryException{
    if(!existsInfoColliVolumiComm(con, commessa, dataCommessa)){
      List<List> l=getColliVolumiCommessa(con, commessa, dataCommessa);
      for(List e:l){
        e.add(0, CostantsColomb.AZCOLOM);
        e.add(1, dataCommessa);
        e.add(2, commessa);
        e.add("Y");
        e.add(new Date());
      }
      ColliVolumiCommessaTbl tab=new ColliVolumiCommessaTbl();
      PersistenceManager man=new PersistenceManager(con);
      man.saveListDt(tab, l);
    }
  }
  
  
  public void elabColliVolumiCommesse(Connection con,Long dataRif,Boolean noCheckGgLav) throws SQLException, QueryException{
    List finL=new ArrayList();
    List<Integer> commTodelete=new ArrayList();
    
    List<List> comms=getListCommesse(con, dataRif,noCheckGgLav);
    if(comms==null || comms.isEmpty()){
      _logger.info("Commesse non presenti per il giorno "+dataRif);
      return;
    }  
    List<List> vols=getColliVolumiCommesse(con,dataRif);
    
    
    for(List recCom:comms){
      Integer typeC=ClassMapper.classToClass(recCom.get(2),Integer.class);
      Integer commTmp=ClassMapper.classToClass(recCom.get(1),Integer.class);
      commTodelete.add(commTmp);
      
      for(List recVol:vols){
        Integer typeO=ClassMapper.classToClass(recVol.get(3),Integer.class);
        if(typeO.equals(typeC)){
          
          List rec=new ArrayList(recVol);
          rec.remove(3);
          rec.add(0,CostantsColomb.AZCOLOM);
          rec.add(1,dataRif);
          rec.add(2,commTmp); //numComm
          
          rec.add("Y");
          rec.add(new Date());
          finL.add(rec);
        }
      } 
    }
    ColliVolumiCommessaTbl tab=new ColliVolumiCommessaTbl();
    PersistenceManager man=new PersistenceManager(con);
    //cancellazione dei record delle commesse già presenti
    for(Integer cm:commTodelete){
      Map m= new HashMap();
      m.put(ColliVolumiCommessaTbl.DCCONO,CostantsColomb.AZCOLOM);
      m.put(ColliVolumiCommessaTbl.DCDTCO,dataRif);
      m.put(ColliVolumiCommessaTbl.DCCOMM,cm);
      
      man.deleteDt(tab, m);
    }
    
    man.saveListDt(tab, finL);
   
  }
  
  
  private List getListCommesse(Connection con,Long dataCorrN,Boolean noCheckcGgLav) throws SQLException, QueryException{
    List commesse =new ArrayList();
    
    QueryCommToShip qry=new QueryCommToShip();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataCorrN);
    if(noCheckcGgLav){
      qry.setFilter(QueryCommToShip.NOCHECKGGLAV, "YES");
    }


    ResultSetHelper.fillListList(con, qry.toSQLString(),commesse);
      
    
    
    return commesse;
  }
  
  
  public List<List> getColliVolumiCommesse(Connection con,Long dataCommessa) throws SQLException ,QueryException{
    List<List> l=new ArrayList();
    
    QueryConteggioColliBu query=new QueryConteggioColliBu();
    query.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    query.setFilter(FilterQueryProdCostant.FTDATACOMMN, dataCommessa);
    query.setFilter(QueryConteggioColliBu.FILTERTYPEORD, "Y");
    
    ResultSetHelper.fillListList(con, query.toSQLString(), l);
      
    return l;
   
  }
  
  /**
   * Torna info relativi ai colli della commessa della giornata
   * @param con
   * @param commessa
   * @param dataCommessa
   * @return
   * @throws SQLException
   * @throws QueryException 
   */
  public List<List> getColliVolumiCommessa(Connection con,Integer commessa,Long dataCommessa) throws SQLException ,QueryException{
    List<List> l=new ArrayList();
    
    QueryConteggioColliBu query=new QueryConteggioColliBu();
    query.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    query.setFilter(FilterQueryProdCostant.FTDATACOMMN, dataCommessa);
    query.setFilter(QueryConteggioColliBu.FILTERCOLLICOMM, "YES");
    ResultSetHelper.fillListList(con, query.toSQLString(), l);
      
    return l;
   
  }
  
  private Boolean existsInfoColliVolumiComm(Connection con,Integer commessa,Long dataCommessa) throws SQLException{
    String s="select distinct DCCOMM from " 
             +ColombiniConnections.getAs400LibPersColom()+"."+ColliVolumiCommessaTbl.TABLENAME+
            "\n WHERE DCCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+   
             " and DCCOMM ="+JDBCDataMapper.objectToSQL(commessa)+
             " and DCDTCO = "+JDBCDataMapper.objectToSQL(dataCommessa);
    
    Object [] obj=ResultSetHelper.SingleRowSelect(con, s);
    if(obj!=null && obj.length>0)
      return Boolean.TRUE;
    
    
    return Boolean.FALSE;
  }
  
  /**
   * Torna la lista dei giorni da calendario Movex compresi nel periodo indicato.
   * Se specificato solo i giorni lavorativi
   * @param conAs400
   * @param dataInizio
   * @param dataFine
   * @param onlylav
   * @return
   * @throws SQLException 
   */
  public List getListGg(Connection conAs400,Date dataInizio,Date dataFine,Boolean onlylav) throws SQLException{
    List gg=new ArrayList();
    if(dataInizio==null || dataFine==null)
      return null;
    
    String s="select cdymd8 from mvxbdta.csycal  where 1=1 "+
            "and cdcono=30 and cddivi='' "+
            " and cdymd8>="+JDBCDataMapper.objectToSQL(DateUtils.getDataForMovex(dataInizio))+
            " and cdymd8<="+JDBCDataMapper.objectToSQL(DateUtils.getDataForMovex(dataFine));
    
    if(onlylav)
      s+=" and cdwdpc=100";
            
    ResultSetHelper.fillListList(conAs400, s, gg);
    
    return gg;
  }
  
  /**
   * Verifica se una determinata data  è lavorativa per il calendario aziendale
   * @param conAs400
   * @param data
   * @return
   * @throws SQLException 
   */
  public Boolean isGgLav(Connection conAs400,Date data) throws SQLException{
    Boolean islav=Boolean.FALSE;
    
    List l=getListGg(conAs400, data, data, Boolean.TRUE);
    
    if(l.size()>0)
      islav=Boolean.TRUE;
    
    
    return islav;
  }
  
  
  public String getStringNCollo(Integer nCollo){
    String s="";
    
    if(nCollo==null)
      return s;  
    
    int len=nCollo.toString().length();
    while(len<5){
      s+="0";
      len++;
    }
    s+=nCollo.toString();
    
    return s;
  }
  
    public String getStringNArt(Integer nArt){
    String s="";
    
    if(nArt==null)
      return s;  
    
    int len=nArt.toString().length();
    while(len<2){
      s+="0";
      len++;
    }
    s+=nArt.toString();
    
    return s;
  }
  
  public String getStringNComm(Long ncomm){
    String s="";
    if(ncomm==null)
      return "";
    
    int len=ncomm.toString().length();
    while(len<3){
      s+="0";
      len++;
    }
    s+=ncomm.toString();
    
    return s;
  }
  
  
//  public String getStringNComm(Long ncomm){
//    if(ncomm==null)
//      return "";
//    
//    return (ncomm<100 ? "0" : "")+ncomm.toString();
//  }
  
  
  public List getSpedizioniColliGg(Connection con ,Long dataRifN,Boolean onlySpeMob) throws QueryException, SQLException{
    List speds =new ArrayList();
    QrySpedizioniGg qry=new QrySpedizioniGg();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataRifN);
    
    if(onlySpeMob)
      qry.setFilter(QrySpedizioniGg.SPEDMOB, "Y");
    
    ResultSetHelper.fillListList(con, qry.toSQLString(), speds);
    
    
    return speds;        
  }
  
  public List getListCentriLavoro(Connection con ,Boolean onlyCdL ) throws QueryException, SQLException{
    List centri =new ArrayList();
    QueryCentriDiLavoro qry=new QueryCentriDiLavoro();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    
    if(onlyCdL)
      qry.setFilter(QueryCentriDiLavoro.FTONLYCDL, "Y");
    
    
    ResultSetHelper.fillListList(con, qry.toSQLString(), centri);
    
    
    return centri;        
  }
  
  /**
   * Restituisce un file cercandolo 
   * @param path cartella dove cercare
   * @param nomeParziale nome parziale del file
   * @param dataMin data minima di modifica del file
   * @param dataMax data massima di modifica del file
   * @return 
   */
public String getFileName(String path,String nomeParziale,Date dataMin ,Date dataMax){
    String f=null;
    if(path==null || nomeParziale==null)
      return f;
    
    File dir=new File(path);
    if(!dir.isDirectory())
      return f;
    
    List<String> listF=ArrayUtils.getListFromArray(dir.list());
    Boolean trovato=Boolean.FALSE;
    for( String nomeF:listF ){
      if(!trovato && nomeF.contains(nomeParziale) ){
        File ftemp=new File(path+nomeF);
        Date dtFile=new Date(ftemp.lastModified());
        if(dataMin==null || (DateUtils.afterEquals(dtFile, dataMin) ) ){
          if(dataMax==null || (DateUtils.beforeEquals(dtFile, dataMax) ) ){
             trovato=Boolean.TRUE; 
             f=ftemp.toString();
          }
        }
      }
    }
    
    
    return f;
  }
  
  
  public  Date getTurno(Date data){
    Date turno=null;
    if(data==null)
      return turno;
    
    try{
      Date mezzanotte=DateUtils.getInizioGg(data);
      Date matt=DateUtils.getInizioTurnoM(data);
      Date pom=DateUtils.getFineTurnoM(data);
      Date sera=DateUtils.getFineTurnoP(data);
      if(DateUtils.afterEquals(data, mezzanotte) && data.before(matt)){
        turno=mezzanotte;
      } else if(DateUtils.afterEquals(data, matt) && data.before(pom)) {
        turno=matt;
      } else if(DateUtils.afterEquals(data, pom) && data.before(sera)) {
        turno=pom;
      } else if(DateUtils.afterEquals(data, sera)) {
        turno=sera;
      }
          
    }catch(ParseException p){
     _logger.error(" Problemi nella gestione della data --> "+p.getMessage());
    }
    
    return turno;
  }
  
  
//  public  List getDataCommessa(Connection conAs400,Integer commessa,Long dataElab){
//    String qry=" SELECT ZCCDLD FROM MPDWCT MCOBMODDTA.ZCOMME WHERE 1=1"
//            +" and ZCCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)
//            +" and ZCCCOM="+JDBCDataMapper.objectToSQL(commessa);
//            
//    List linee=new ArrayList();
//    try {
//      ResultSetHelper.fillListList(conAs400, qry, linee);
//    } catch (SQLException ex) {
//      _logger.error("Attenzione errore nel caricamento delle linee logiche legate della linea fisica "+linea+" : "+ex.getMessage() );
//    }
//    
//    return linee;
//  }
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(DatiProdUtils.class);
  
}
