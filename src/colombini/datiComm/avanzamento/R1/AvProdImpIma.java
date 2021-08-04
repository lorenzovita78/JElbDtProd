/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1;


import colombini.conn.ColombiniConnections;
import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.avanzamento.QueryProdGgImpImaFromHitS;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class AvProdImpIma extends AvProdLineaStd {

  
  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    Connection conSqLS=null;
    QueryProdGgImpImaFromHitS qry=new QueryProdGgImpImaFromHitS();
    List<List> commesse=new ArrayList();
    
    try {
      conSqLS = getConnectionToDbIma();
      String di=DateUtils.DateToStr(DateUtils.getInizioGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");
      String df=DateUtils.DateToStr(DateUtils.getFineGg(this.getDataRifCalc()), "yyyy-MM-dd HH:mm:ss ");  
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, di);
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA, df);
      qry.setFilter(QueryProdGgImpImaFromHitS.FT_TYPEPROD, QueryProdGgImpImaFromHitS.TYPEPRODCOMM);
      qry.setFilter(QueryProdGgImpImaFromHitS.FT_STATUS, QueryProdGgImpImaFromHitS.STATUSCOMCOMPLETE);
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, "Y");
      if(isImaAnteClienti()){
        qry.setFilter(QueryProdGgImpImaFromHitS.FT_CLIIMAANTE, "Y");
      }
      
      ResultSetHelper.fillListList(conSqLS, qry.toSQLString(), commesse);
      List lcommN=new ArrayList();
      for(List recC:commesse){
        Integer comm=ClassMapper.classToClass(recC.get(0), Integer.class);
        if(isImaAnteClienti()){
          comm=comm-400;
          recC.set(0, comm);
        }
        Long dtComm=getDataCommessa(comm);
        if(dtComm!=null && dtComm>-1){
          recC.add(1, dtComm);
          lcommN.add(recC);
        }
      }
      
      //per Ima Ante carichiamo i dati di produzione a magazzino 
      //impostando n.commessa a 0 e datacommessa a 0
      if(isImaAnte() && !isImaAnteClienti() ){
        qry=new QueryProdGgImpImaFromHitS();
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA, di);
        qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA, df);
        qry.setFilter(QueryProdGgImpImaFromHitS.FT_TYPEPROD, QueryProdGgImpImaFromHitS.TYPEPRODMAG);
        qry.setFilter(QueryProdGgImpImaFromHitS.FT_STATUS, QueryProdGgImpImaFromHitS.STATUSMAGCOMPLETE);
        
        Object [] obj=null;
        obj=ResultSetHelper.SingleRowSelect(conSqLS, qry.toSQLString());
        if(obj!=null && obj.length>0){
          List rec=new ArrayList();
          rec.add(Long.valueOf(0));
          rec.add(Long.valueOf(0));
          rec.add(ClassMapper.classToClass(obj[0], Integer.class));
          
          lcommN.add(rec);
        }
      }
      
      storeDatiProdComm(conAs400, lcommN);
    } catch (ParseException ex) {
      _logger.error("Problemi nella conversione dei dati di tipo data -->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (SQLException ex) {
      _logger.error("Problemi di accesso al Database Ima -->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (QueryException ex) {
      _logger.error("Problemi di interrogazione del Database Ima-->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } finally {
      if(conSqLS!=null)
        try {
          conSqLS.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura della connessione");
      }
    }
    
    
  }
  
  
   @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
     return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(),this.getInfoLinea().getCommessa() , 
            this.getInfoLinea().getDataCommessa(),this.getInfoLinea().getCodLineaLav() );
  }

  
  
//  @Override
//  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
//    Connection conSqLS=null;
//    Integer pz=Integer.valueOf(0);
//    QueryAvCommImpIma qry=new QueryAvCommImpIma();
//    
//    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, getInfoLinea().getCommessa());
//    
//    if(isImaAnteClienti()){
//      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, getInfoLinea().getCommessa()+400);
//    }
//    
//    try {
//      conSqLS = getConnectionToDbIma();
//    
//      Object [] obj=ResultSetHelper.SingleRowSelect(conSqLS, qry.toSQLString());
//      if(obj[0]!=null)
//        pz=ClassMapper.classToClass(obj[0], Integer.class);
//    
//    } catch (SQLException ex) {
//      _logger.error("Impossibile stabilire la connessione con il server -->"+ex.getMessage());
//      throw new DatiCommLineeException("Errore in fase di connessione con il database");
//    } catch (QueryException ex) {
//      _logger.error("Impossibile eseguire la query -->"+ex.getMessage());
//      throw new DatiCommLineeException("Errore in fase di esecuzione della query per il reperimento delle informazioni");
//    } finally{
//      if(conSqLS!=null)
//        try {
//          conSqLS.close();
//      } catch (SQLException ex) {
//        _logger.error("Errore in fase di rilascio della connessione -->"+ex.getMessage());
//      }
//      
//    }
//    
//    return pz;
//  }
  
  
  
  private Connection getConnectionToDbIma() throws SQLException{
    Connection con=null;
    if(isImaAnte()){
      con=ColombiniConnections.getDbImaAnteConnection();
    }else {
      con=ColombiniConnections.getDbImaTopConnection();
    
    } 
   
    return con;
  }
  
  
  public Boolean isImaAnte(){
    Boolean bool=Boolean.FALSE;
    if(getInfoLinea().getCodLineaLav().contains("01008")){
      bool=Boolean.TRUE;
    }
    
    return bool;
  }
  
  public Boolean isImaAnteClienti(){
    Boolean bool=Boolean.FALSE;
    if(getInfoLinea().getCodLineaLav().equals("01008C")){
      bool=Boolean.TRUE;
    }
    
    return bool;
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdImpIma.class);

 
  
}

