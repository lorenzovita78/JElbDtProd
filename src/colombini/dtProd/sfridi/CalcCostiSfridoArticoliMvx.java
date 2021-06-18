/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.query.produzione.QuerySfridoPeriodoXCosti;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe di utilità che dato un articolo e il suo stabilimento restituisce il costo.
 * 
 * @author lvita
 */
public class CalcCostiSfridoArticoliMvx {
  
  public final static String OTMR1P1="01008";
  public final static String OTMR1P0="01035";
  public final static String OTMGALP2="01000";
  public final static String OTMGALP0="01XXX";
  public final static String SEZR2P1="01004";
  
  
  private static String STMCOSTO="select M9SOCO from MCOBMODDTA.ZMITFAC  where 1=1 "+
                                 " and M9CONO=? and M9FACI=? and M9ITNO=?";

  private static String STMDIM="select MMDIM1,MMDIM2,MMDIM3 from MVXBDTA.MITMAD  where 1=1 "+
                                 " and MMCONO=? and MMITNO=?";
  
  
  private static String STMCOSTOPER="select KOCSU6 from MVXBDTA.mchead  where 1=1 "+
                                 " and KOCONO=? and KOFACI=? and KOITNO=? and KOPCDT between ?  and ?";

  
//  private static String STMDIM="select KOASU6 from MVXBDTA.mchead  where 1=1 "+
//                                 " and KOCONO=? and KOFACI=? and KOITNO=? and KOPCDT=? ";
  
  
  public static CalcCostiSfridoArticoliMvx getInstance(){
    if(instance==null){
      instance= new CalcCostiSfridoArticoliMvx();
      
    }
    
    return instance;
  }
  
  private static CalcCostiSfridoArticoliMvx instance;
  
  
  
  
  public Double getCostoArticolo(Connection con ,Integer azienda, String stabilimento, String articolo){
    Double ct=Double.valueOf(0.0);
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    try{
        pstmt = con.prepareStatement(STMCOSTO); 
        pstmt.setInt(1, azienda);
        pstmt.setString(2, stabilimento);
        pstmt.setString(3, articolo);
        rs=pstmt.executeQuery();
        while(rs.next()){
          ct=rs.getDouble("M9SOCO");
        }
        
    } catch (SQLException ex) {
      _logger.error("Impossibile estrarre informazioni sui costi per articolo  :"+articolo+" stabilimento :"+stabilimento+" -->"+ex.getMessage());
    } finally{
      try {
        if(pstmt!=null)
          pstmt.close();
        if(rs!=null)
          rs.close();
      } catch (SQLException ex) {
        _logger.error("Errore nella chiusura dello statment per articolo  :"+articolo+" stabilimento :"+stabilimento+" -->"+ex.getMessage());
      }
    } 
    
    return ct;
  }
  
  private Double getCostoArticoloPeriodo(Connection con, Integer azienda, String stabilimento, String articolo, Integer anno, Integer mese) {
    Double ct=Double.valueOf(0.0);
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    Long dataIniN=DateUtils.getNumericData(anno, mese, Integer.valueOf(1));
    Long dataFinN=DateUtils.getNumericData(anno, mese, Integer.valueOf(31));
    
    try{
        pstmt = con.prepareStatement(STMCOSTOPER); 
        pstmt.setInt(1, azienda);
        pstmt.setString(2, stabilimento);
        pstmt.setString(3, articolo);
        pstmt.setLong(4, dataIniN);
        pstmt.setLong(5, dataFinN);
        rs=pstmt.executeQuery();
        while(rs.next()){
          ct=rs.getDouble("KOCSU6");//KOASU6
        }
        
    } catch (SQLException ex) {
      _logger.error("Impossibile estrarre informazioni sui costi per articolo : "+articolo+" - stabilimento : "+stabilimento+" - data : "+dataIniN +" -->"+ex.getMessage());
    } finally{
      try {
        if(pstmt!=null)
          pstmt.close();
        if(rs!=null)
          rs.close();
      } catch (SQLException ex) {
        _logger.error("Errore nella chiusura dello statment per articolo  :"+articolo+" stabilimento :"+stabilimento+" -->"+ex.getMessage());
      }
    } 
    
    return ct;
  }
  
  
  
  
  public List<Double> getDimArticolo(Connection con ,Integer azienda, String articolo){
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    List dims=new ArrayList();
    try{
        pstmt = con.prepareStatement(STMDIM); 
        pstmt.setInt(1, azienda);
        pstmt.setString(2, articolo);
        rs=pstmt.executeQuery();
        while(rs.next()){
          dims.add(rs.getDouble("MMDIM1")/1000);
          dims.add(rs.getDouble("MMDIM2")/1000);
          dims.add(rs.getDouble("MMDIM3")/1000);
        }
        
      } catch (SQLException ex) {
        _logger.error("Impossibile estrarre informazioni relative alle dimensioni dell'articolo  :"+articolo+" --> "+ex.getMessage());
      } finally{
        try {
          if(pstmt!=null)
            pstmt.close();
          if(rs!=null)
            rs.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura dello statment per articolo  :"+articolo+" --> "+ex.getMessage());
        }
      } 
    
    return dims;
  }
  
  
  /**
   * Torna il costo rapportato ai m2 dell'articolo fornito
   * @param  con Connection
   * @param  azienda Integer
   * @param stabilimento Integer 
   * @param articolo String 
   * @return costo Double 
   * 
   */
  public Double getCostoM2Articolo(Connection con ,Integer azienda, String stabilimento,Integer anno,Integer mese, String articolo){
    Double costoXdim=Double.valueOf(0);
    Double ct=getCostoArticoloPeriodo(con, azienda, stabilimento,articolo ,anno ,mese );
    if(ct==null ){
      _logger.warn("Attenzione costo articolo "+articolo+" non presente.");
      ct=Double.valueOf(0);
    }
    
     
    List dims=getDimArticolo(con, azienda, articolo);
    if(dims!=null && dims.size()>1 ){
      Double dim1=ClassMapper.classToClass(dims.get(0),Double.class); 
      Double dim2=ClassMapper.classToClass(dims.get(1),Double.class); 
      Double area=dim1*dim2;
      costoXdim=ct/area;
    }
     
    return costoXdim; 
  }
  
  
  public Double getCostoM2ArticoloXPeriodo(Connection con ,Integer azienda, String stabilimento, String articolo,Integer anno,Integer mese){
    Double costoXdim=Double.valueOf(0);
    Double ct=getCostoArticoloPeriodo(con, azienda, stabilimento, articolo, anno, mese);
    if(ct==null ){
      _logger.warn("Attenzione costo articolo "+articolo+" non presente.");
      ct=Double.valueOf(0);
    }
    
     
    List dims=getDimArticolo(con, azienda, articolo);
    if(dims!=null && dims.size()>1 ){
      Double dim1=ClassMapper.classToClass(dims.get(0),Double.class); 
      Double dim2=ClassMapper.classToClass(dims.get(1),Double.class); 
      Double area=dim1*dim2;
      costoXdim=ct/area;
    }
     
    return costoXdim; 
  }
  
  
  /**
   * Aggiorna la lista fornita inserendo come ultima colonna il prezzo dell'articolo
   * @param Connection con
   * @param List list lista contenente gli articoli . La lista deve avere pos(0)-> azienda pos(1)->stab pos(6)->articolo
   * @return List
   */  
  public List aggDatiCostoArticolo(Connection con,List<List> list){
    if(list==null || list.isEmpty())
      return list;
    
    int i=-1;
    for (List record:list){
      i++;
      Integer azienda=ClassMapper.classToClass(record.get(0),Integer.class);
      String stab=ClassMapper.classToString(record.get(1));
      Integer anno =ClassMapper.classToClass(record.get(4),Integer.class);
      Integer mese =ClassMapper.classToClass(record.get(5),Integer.class);
      
      //i costi su MCHEAD hanno una data di riferimento di maggiorata di un mese
      //es i costi di settembre hanno come data riferimento il 1/10
//      if(mese<12){
//        mese++;
//      
//      }
//      else if (mese==12){
//        mese=Integer.valueOf(1);
//        anno++;
//      }
      String articolo=ClassMapper.classToString(record.get(7)).trim();
      if(articolo.lastIndexOf("_")>0){
        articolo=articolo.substring(0,articolo.length()-2);
        record.set(7, articolo);
      }
      Double prz=getCostoM2Articolo(con, azienda, stab,anno,mese ,articolo);
      
      record.add(prz);
      record.add(new Date());
      
    }
    
    
    
    return list;
  }
  
  /**
   * Fornisce i dati di sfrido/costi di una determinata linea in un determinato periodo
   * @param con
   * @param anno
   * @param mese
   * @param stab
   * @param piano
   * @param centroL
   * @param condSfrido
   * @param condSfridoFisio
   * @param pnp
   * @return 
   */
  public List getDatiCostiSfridoPeriodo(Connection con, Integer anno, Integer mese,String stab,String piano,String centroL,String condSfrido,String condSfridoFisio,boolean pnp) {
    List lista=new ArrayList();
    QuerySfridoPeriodoXCosti qry=new QuerySfridoPeriodoXCosti();
    qry.setFilter(QuerySfridoPeriodoXCosti.ANNO, anno);
    qry.setFilter(QuerySfridoPeriodoXCosti.MESE, mese);
    qry.setFilter(QuerySfridoPeriodoXCosti.CDL,centroL);
    qry.setFilter(QuerySfridoPeriodoXCosti.STABILIMENTO, stab);
    qry.setFilter(QuerySfridoPeriodoXCosti.PIANO, piano);
    
    //gestione filtri opzionali
    if(pnp)
      qry.setFilter(QuerySfridoPeriodoXCosti.PNP, pnp);
    
    if(!StringUtils.IsEmpty(condSfrido))
      qry.setFilter(QuerySfridoPeriodoXCosti.CALCSFRIDO, condSfrido);
    
    if(!StringUtils.IsEmpty(condSfridoFisio))
      qry.setFilter(QuerySfridoPeriodoXCosti.CALCSFRIDOFISIO, condSfridoFisio);
    //----
    
    
    try {
      String q=qry.toSQLString();
      _logger.info("Esecuzione query: "+q);
      ResultSetHelper.fillListList(con, q, lista);
      
      lista=CalcCostiSfridoArticoliMvx.getInstance().aggDatiCostoArticolo(con, lista);
      
    } catch (SQLException ex) {
      _logger.error("Attenzione errore in fase di reperimento dei dati di sfrido per linea "+centroL+
              " - anno : "+anno+" mese: "+mese+" --> \n " +ex.getMessage());
    } catch (QueryException ex) {
      _logger.error("Attenzione errore in fase di reperimento dei dati di sfrido per linea "+centroL+
              " - anno : "+anno+" mese: "+mese+" --> \n " +ex.getMessage());
    }
    
    return lista;
  }
  
  
  
     
  
   
   private static final Logger _logger = Logger.getLogger(CalcCostiSfridoArticoliMvx.class);

  
}
