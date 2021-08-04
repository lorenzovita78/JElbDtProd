/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;

import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.avanzamento.QueryProdGgAP4;
import colombini.query.datiComm.avanzamento.QueryProdCommAP4;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.ClassMapper;
import utils.DateUtils;



/**
 *
 * @author lvita
 */
public class InfoDatiAnteP4 {
 
   private static InfoDatiAnteP4 instance;
   
   public static InfoDatiAnteP4 getInstance(){
    if(instance==null){
      instance= new InfoDatiAnteP4();
    }
    
    return instance;
  }
   
   
  public Integer getPzProdLinea(Connection con ,Integer az,Integer comm,Long dataComm,String utRef ) throws DatiCommLineeException{
    Integer pzProd=null;
    
    Object [] obj=loadDatiCommLinea(con, az, comm, dataComm, utRef);
    if(obj!=null && obj.length>1){
      pzProd=ClassMapper.classToClass(obj[1],Integer.class);
    }
    
    return pzProd;
  } 
  
  
  public Integer getPzCommLinea(Connection con ,Integer az,Integer comm,Long dataComm,String utRef ) throws DatiCommLineeException{
    Integer pzComm=null;
    Object [] obj=loadDatiCommLinea(con, az, comm, dataComm, utRef);
    if(obj!=null && obj.length>1){
      pzComm=ClassMapper.classToClass(obj[0],Integer.class);
    }
    
    return pzComm;
  }
  
  private  Object[] loadDatiCommLinea(Connection conAs400 ,Integer az,Integer comm,Long dataComm,String utRef ) throws DatiCommLineeException{
    Object [] obj=null;
    QueryProdCommAP4 qry=new QueryProdCommAP4();
    qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, az);
    qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM,comm);
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA,dataComm);
    qry.setFilter(FilterFieldCostantXDtProd.FT_UTLINEA,utRef);
    
    try{
      obj=ResultSetHelper.SingleRowSelect(conAs400,qry.toSQLString());
      
    } catch(SQLException s){
      _logger.error("Errore in fase di esecuzione query --> "+s.getMessage());
      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
    } catch (QueryException ex) {
       _logger.error("Errore in fase di esecuzione query --> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
    }
    
    
    return obj;
  }
  
  public  List loadDatiPzProdGGLinea(Connection conAs400 ,Integer az,Date dataRif,String utRef ) throws DatiCommLineeException{
    List ll=new ArrayList();
    QueryProdGgAP4 qry=new QueryProdGgAP4();
    qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, az);
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA,DateUtils.getDataForMovex(dataRif));
    qry.setFilter(FilterFieldCostantXDtProd.FT_UTLINEA,utRef);
    
    try{
      ResultSetHelper.fillListList(conAs400,qry.toSQLString(),ll);
      
    } catch(SQLException s){
      _logger.error("Errore in fase di esecuzione query --> "+s.getMessage());
      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
    } catch (QueryException ex) {
       _logger.error("Errore in fase di esecuzione query --> "+ex.getMessage());
      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
    }
    
    
    return ll;
  }
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(InfoDatiAnteP4.class);
  
}
