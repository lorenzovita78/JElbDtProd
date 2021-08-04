/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.ColomConnectionsCostant;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import db.Connections;
import db.ConnectionsProps;
import db.ResultSetHelper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class AvProdLineaCappelli extends AvProdLineaStd{
  
  
  
 private Map getMapCommColliProd() throws DatiCommLineeException{ 
   
   List<String> listC=new ArrayList();
   Map commMap=new HashMap();
   Connection con=null;
   String urlOdbc="";
   String dataS="";
   try{
      //dataS=DateUtils.DateToStr(getDataForLogFile(), "dd/MM/yyyy");
      dataS=DateUtils.DateToStr(getDataRifCalc(), "dd/MM/yyyy");
      //String dataSF=DateUtils.DateToStr(DateUtils.addDays(getDataForLogFile(),1), "dd/MM/yyyy");
      String dataSF=DateUtils.DateToStr(DateUtils.addDays(getDataRifCalc(),1), "dd/MM/yyyy");
      urlOdbc=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.ODBC_IMBCAPPELLI));
      con=Connections.getAccessDBConnection(urlOdbc, null, null);
      
  //    Connection con =Connections.getAccessDBConnection("D:\\temp\\oee\\History.mdb", null, null);
       String query="SELECT  distinct [paper code] \n" +
                " FROM Production\n" +
               " where [date] >=dateValue('"+dataS+"') \n" +
                 " and [date] < dateValue('"+dataSF+"')";          

      _logger.info("Query per estrazione dati:"+query); 
      ResultSetHelper.fillListList(con, query, listC);
   }catch(SQLException s){
     _logger.error("Errore in fase di lettura dei dati dal db "+urlOdbc+" --> "+s.getMessage());
     throw new DatiCommLineeException("Errore in fase di lettura del database "+urlOdbc+" per il giorno "+dataS);
   } catch(ParseException p){
      _logger.error("Errore in fase di conversione dei dati --> "+p.getMessage());
     throw new DatiCommLineeException("Errore in fase di conversione dei dati");
   } catch (Exception e) {
     _logger.error("Errore generico --> "+e.getMessage());
     throw new DatiCommLineeException("Errore in fase di lettura dati");
   }  finally{
      if(con!=null){
        try {
          con.close();
        } catch (SQLException ex) {
          
        }
      }
    }   
   if(!listC.isEmpty()){
     for(String s:listC){
       if(!StringUtils.IsEmpty(s) && s.length()>=3){
         Integer comm=ClassMapper.classToClass(s.substring(0,3),Integer.class);
         if(commMap.containsKey(comm)){
           commMap.put(comm,((Integer)commMap.get(comm))+1);
         }else{
           commMap.put(comm,Integer.valueOf(1));
         }
       }
     }
   }
   _logger.info("Mappa commesse"+commMap);
   
   return commMap;
 }

 
 private void copyFileMdb() throws DatiCommLineeException{
   String fSource=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.FSOURCE_IMBCAPPELLI));
   String pathLocal=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ColomConnectionsCostant.PATHLOCALE_IMBCAPPELLI));
   try {
     _logger.info("Copia file "+fSource +" in corso...");
     FileUtils.copyFile(fSource, pathLocal+FileUtils.getShortFileName(fSource));
     _logger.info("File copiato");
   } catch (IOException ex) {
     _logger.error("Errore in fase di copia del file access mdb");
     throw new DatiCommLineeException("Errore in fase di copia del file access mdb");
   }
   
 }
  
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    _logger.info("Caricamento dei dati prodotti dalla linea "+getInfoLinea().getCodLineaLav()+" con riferimento data "+getDataRifCalc()
    +" data per lettura dati"+getDataForLogFile());
    
    copyFileMdb();
    Map commesse=getMapCommColliProd();
    storeDatiProdComm(conAs400, commesse);
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(),this.getInfoLinea().getCommessa() , 
            this.getInfoLinea().getDataCommessa(),this.getInfoLinea().getCodLineaLav() );
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdLineaCappelli.class);  
}

