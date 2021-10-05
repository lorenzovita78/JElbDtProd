/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.conn;


import as400.db.As400Connection;
import colombini.costant.ColomConnectionsCostant;
import db.Connections;
import db.ConnectionsProps;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import utils.ClassMapper;



/**
 *
 * @author lvita
 */
public class ColombiniConnections {
  
  

  /**
   * Torna la connessione al database as400 di Colombini
   * ATTENZIONE UTILIZZARE SEMPRE IN UN BLOCCO TRY FINALLY chiudendo la connessione alla fine dell'utilizzo
   * @return Connection
   * @throws SQLException
   */
  public static Connection getAs400ColomConnection() throws SQLException {
    String ip=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_IP4));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_USER));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_PWD));
    String lib=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_COLOM_LIBRARIES));
    
    
    return As400Connection.getConnection(ip, usr, pwd, lib,As400Connection.NAMING_TYPE_SQL);
  }

  
  public static Connection getAs400FebalConnection() throws SQLException{
    
    String ip=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_FEBAL_IP4));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_FEBAL_USER));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_FEBAL_PWD));
    String lib=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.AS400_FEBAL_LIBRARIES));
    
    
    return As400Connection.getConnection(ip, usr, pwd, lib,As400Connection.NAMING_TYPE_SQL);
  }
  
  
  public static String getAs400LibStdColom() {
    String lib=null;
    try{
      lib=Connections.getInstance().getLibraryNameAs400(ColomConnectionsCostant.AS400_COLOM_LIB_STD);
    }catch(SQLException s){
     _logger.error("Attenzione libreria "+ColomConnectionsCostant.AS400_COLOM_LIB_STD+" non definita nel file properties" );  
    }
    
    return lib;
  }
  
  
  public static String getAs400LibPersColom() {
    String lib=null;
    try{
      lib=Connections.getInstance().getLibraryNameAs400(ColomConnectionsCostant.AS400_COLOM_LIB_PERS);
    }catch(SQLException s){
     _logger.error("Attenzione libreria "+ColomConnectionsCostant.AS400_COLOM_LIB_STD+" non definita nel file properties" );  
    }
    
    return lib;
    
  }
  
  /**
   * Torna la connessione ad una libreria personalizzata temporanea ( i dati vengono cancellati periodicamente)
   * Nel caso del mondo Colombini torna la libreria MCOBFILP
   * @return 
   */
  public static String getAs400LibTemp() {
    String lib=null;
    try{
      lib=Connections.getInstance().getLibraryNameAs400(ColomConnectionsCostant.AS400_COLOM_LIB_TMP);
    }catch(SQLException s){
     _logger.error("Attenzione libreria "+ColomConnectionsCostant.AS400_COLOM_LIB_STD+" non definita nel file properties" );  
    }
    
    return lib;
  }
  
  
  public static String getAs400LibStdFebal() {
    String lib=null;
    try{
      lib=Connections.getInstance().getLibraryNameAs400(ColomConnectionsCostant.AS400_FEBAL_LIB_STD);
    }catch(SQLException s){
     _logger.error("Attenzione libreria "+ColomConnectionsCostant.AS400_FEBAL_LIB_STD+" non definita nel file properties" );  
    }
    
    return lib;
  }
  
  
  public static String getAs400LibPersFebal() {
    String lib=null;
    try{
      lib=Connections.getInstance().getLibraryNameAs400(ColomConnectionsCostant.AS400_FEBAL_LIB_PERS);
    }catch(SQLException s){
     _logger.error("Attenzione libreria "+ColomConnectionsCostant.AS400_FEBAL_LIB_STD+" non definita nel file properties" );  
    }
    
    return lib;
    
  }
  
 
  public static Connection getDbBitaBitConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDBGALAZZANO));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBBITABIT));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBGALAZZANO));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBGALAZZANO));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbNestingGalConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDBGALAZZANO));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBOTMNESTING));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBGALAZZANO));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBGALAZZANO));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbForBiesseFTTGalConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDBGALAZZANO));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBSPVFORBSFTT));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBGALAZZANO));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBGALAZZANO));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbForBiesseKernelGalConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDBGALAZZANO));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBSPVFORBSKERNEL));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBGALAZZANO));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBGALAZZANO));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  
  
  
  public static Connection getDbQuidR2Connection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDBR2));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBQUIDP));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBQUIDP));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBQUIDP));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbImaTopConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVFORIMATOP));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBIMATOP));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBIMATOP));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBIMATOP));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbPanotecImbTop() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVPANOTECIMBTOP));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBPANOTECIMBTOP));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRPANOTECIMBTOP));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDPANOTECIMBTOP));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbNewBI() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVNEWBI));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBNEWBI));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRNEWBI));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDNEWBI));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbImaTopOrdersConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVORDERSIMATOP));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBORDERSIMATOP));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBORDERSIMATOP));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBORDERSIMATOP));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  
  public static Connection getDbImaAnteConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVFORIMAANTE));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBIMAANTE));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBIMAANTE));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBIMAANTE));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbImaAnteOrdersConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVORDERSIMAANTE));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBORDERSIMAANTE));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDBORDERSIMAANTE));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDBORDERSIMAANTE));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  
  public static Connection getSrvDesmosProdConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty("SRVDESMOS"));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty("DBDESMOS"));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty("USRDESMOS"));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty("PWDDESMOS"));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
  public static Connection getDbDesmosColProdConnection() throws SQLException{
    String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDESMOSPRODCOL));
    String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBDESMOSPRODCOL));
    String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDESMOSPRODCOL));
    String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDESMOSPRODCOL));
      
    return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
  
 public static Connection getDbDesmosFebalProdConnection() throws SQLException{
   String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVDESMOSPRODFEB));
   String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBDESMOSPRODFEB));
   String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRDESMOSPRODFEB));
   String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDDESMOSPRODFEB));
      
   
   return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }

 public static Connection getDbIncasConnection() throws SQLException{
   String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVINCASDB));
   String db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBINCAS));
   String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRINCAS));
   String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDINCAS));
      
   
   return Connections.getMicrosoftSQLServerConnection(srv,db,usr, pwd);
  }
 
  public static Connection getDbVDLVisionConnection() throws SQLException{
   String srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVVDL));
   String sid=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SIDVDL));
   String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRVDL));
   String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDVDL));
      
   
   return Connections.getOracleConnection(srv,sid,usr, pwd);
  }
  
  public static Connection getDbLotto1Connection() throws SQLException{
    String srvName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVLOTTO1));
    String dbName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBLOTTO1));
    String usrName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRLOTTO1));
    String pwdUsr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDLOTTO1));        
   
    _logger.info(" Dati connessione db lotto1 :"+srvName+" - "+dbName+" - "+usrName+" - "+pwdUsr);
    return Connections.getSqlServerConnection(srvName, dbName, usrName, pwdUsr);
    
  }
  
  public static Connection getDbAvanzamentoVdlConnection() throws SQLException{
    String srvName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVAVANVDL));
    String dbName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBAVANVDL));
    String usrName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRAVANDVL));
    String pwdUsr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDAVANVDL));        
   
    _logger.info(" Dati connessione db lotto1 :"+srvName+" - "+dbName+" - "+usrName+" - "+pwdUsr);
    return Connections.getSqlServerConnection(srvName, dbName, usrName, pwdUsr);
    
  }
  
  public static Connection getDbIPCNetConnection() throws SQLException{
    String srvName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVIPCNET));
    String dbName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBIPCNET));
    String usrName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRIPCNET));
    String pwdUsr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDIPCNET));        
   
    _logger.info(" Dati connessione db lotto1 :"+srvName+" - "+dbName+" - "+usrName+" - "+pwdUsr);
    return Connections.getSqlServerConnection(srvName, dbName, usrName, pwdUsr);
    
  }
  
  
  public static Connection getDbPortaleB2bConnection() throws SQLException{
    String srvName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.SRVB2BDB));
    String dbName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.DBB2B));
    String usrName=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.USRB2B));
    String pwdUsr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PWDB2B));        
   
    _logger.info(" Dati connessione db lotto1 :"+srvName+" - "+dbName+" - "+usrName+" - "+pwdUsr);
    return Connections.getSqlServerConnection(srvName, dbName, usrName, pwdUsr);
    
  }
  
  
  public static Connection getDbStrettoioArtec() throws SQLException{
    String srvName="dbstrettoioartec";
    String dbName="StrettoioArtecSupervisore";
    String usrName="utstrartec";
    String pwdUsr="str3t4rt3c";
   
    _logger.info(" Dati connessione db Strettoio Artec :"+srvName+" - "+dbName+" - "+usrName+" - "+pwdUsr);
    return Connections.getSqlServerConnection(srvName, dbName, usrName, pwdUsr);
    
  } 
  
  
  private static final Logger _logger = Logger.getLogger(ColombiniConnections.class);
}
