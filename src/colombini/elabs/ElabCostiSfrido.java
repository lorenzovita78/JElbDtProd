/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.CalcCostiSfridoArticoliMvx;
import colombini.dtProd.sfridi.CostiSfridoArticoliOtmGal;
import colombini.dtProd.sfridi.ISfridoInfo;
import colombini.model.persistence.tab.SfridiCostiProdPersistence;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabCostiSfrido extends ElabClass{
  
  
  
  private Date dataIni;
  private Date dataFin;
 

  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
      
    

    dataIni=(Date) param.get(ALuncherElabs.DATAINIELB);
    dataFin=(Date) param.get(ALuncherElabs.DATAFINELB);
    if(dataIni==null || dataFin==null)
      return Boolean.FALSE;
    
    
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    
    PersistenceManager manP=new PersistenceManager(con);  
    
    Date tmp=dataIni;
    Integer annoRif=DateUtils.getYear(tmp);
    Integer meseRif=DateUtils.getMonth(tmp)+1;
    Integer meseFin=DateUtils.getMonth(dataFin)+1;
    Integer annoFin=DateUtils.getYear(dataFin);
    
    while( (meseRif<=meseFin) && (annoRif<=annoFin)){
      _logger.info(" Elaborazione costi sfrido anno "+annoRif+" mese "+meseRif);
    
      try{
        _logger.info(" Dati per Ottimizzatore Galazzano P2");
        CostiSfridoArticoliOtmGal cs=new CostiSfridoArticoliOtmGal(annoRif, meseRif);
        gestDatiCosti(manP, CostantsColomb.GALAZZANO, CostantsColomb.PIANO2, ISfridoInfo.OTMGALP2, annoRif, meseRif, cs.getCostiSfrido(con));

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Galazzano P2 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Galazzano P2-->"+ex.toString());
      }catch(QueryException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Galazzano P2 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Galazzano P2-->"+ex.toString());
      }
    
      try{
        _logger.info(" Dati per Ottimizzatore Nesting Galazzano P0");
        String condSfrido=" SUM(ZSSUPD+ZSSPSP-ZSSUPU -ZSSFIS-ZSSPRF)"; // non sommmiamo la sovraproduzione allo sfrido
        String condSfridoFis="SUM(ZSSUP1)";
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.GALAZZANO, CostantsColomb.PIANO0, ISfridoInfo.OTMGALP0,"",condSfridoFis,false);
        gestDatiCosti(manP, CostantsColomb.GALAZZANO, CostantsColomb.PIANO0, ISfridoInfo.OTMGALP0,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Nesting Galazzano -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Nesting -->"+ex.toString());
      }

      try{
        _logger.info(" Dati per Sezionatrice R2 ");
        //viene tolto il centrimetro di rifolo dallo sfrido di ottimizzazione
        String condSfrido=" SUM(ZSSUPD - ZSSUPU - ZSSPRF)" ; //mod del 30/05/2017 escludiamo lo sfridofisiologico in quanto non già incluso nello sfrido
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA2, CostantsColomb.PIANO1, ISfridoInfo.SEZR2P1, condSfrido,"",false);
        gestDatiCosti(manP, CostantsColomb.ROVERETA2, CostantsColomb.PIANO1, ISfridoInfo.SEZR2P1,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Sezionatrice R2 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Sezionatri-->"+ex.toString());
      }

      try{
        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 0 ");
        String condSfrido=" SUM(ZSSUPD+ZSSPSP-ZSSUPU -ZSSFIS-ZSSPRF)";
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA1, CostantsColomb.PIANO0, ISfridoInfo.OTMR1P0,condSfrido,"",false);
        gestDatiCosti(manP, CostantsColomb.ROVERETA1 , CostantsColomb.PIANO0, ISfridoInfo.OTMR1P0,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.toString());
      }

      try{
        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 1 ");
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA1, CostantsColomb.PIANO1, ISfridoInfo.OTMR1P1,"","",false);
        gestDatiCosti(manP, CostantsColomb.ROVERETA1 , CostantsColomb.PIANO1, ISfridoInfo.OTMR1P1,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.toString());
      }
      //Disativatto calcolo su WN 21/06/2022
//      try{
//        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 4 ");
//        String condSfrido=" SUM(ZSSUPD-ZSSUPU -ZSSFIS-ZSSPRF)";
//        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA1, CostantsColomb.PIANO4, ISfridoInfo.SEZR1P4,condSfrido,"",false);
//        gestDatiCosti(manP, CostantsColomb.ROVERETA1 , CostantsColomb.PIANO4, ISfridoInfo.SEZR1P4,annoRif, meseRif, lista);
//
//      }catch(SQLException ex){
//        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.getMessage());
//        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.toString());
//      }
//      
      try{
        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 4 Combicut ");  
        //String condSfrido=" SUM(ZSSUPD+ZSSPSP-ZSSUPU -ZSSFIS)";
        String condSfrido=" SUM(ZSSUPD+ZSSUP4-ZSSUPU -ZSSFIS)";
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA1, CostantsColomb.PIANO4, ISfridoInfo.COMBICUTR1P4,condSfrido,"",false);
        gestDatiCosti(manP, CostantsColomb.ROVERETA1 , CostantsColomb.PIANO4, ISfridoInfo.COMBICUTR1P4,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.toString());
      }
      
      
      postProcessing(con, annoRif, meseRif);
      
      tmp=DateUtils.addMonths(tmp, 1);
      meseRif=DateUtils.getMonth(tmp)+1;
      annoRif=DateUtils.getYear(tmp);
    } // fine ciclo while
    
  }
  

  
  private void gestDatiCosti(PersistenceManager pm,String stab,String piano,String cdLavoro,Integer anno,Integer mese , List listInfo) throws SQLException{
    if(listInfo==null || listInfo.isEmpty()){
      _logger.info(" Lista dati da processare per linea "+cdLavoro+" vuota.Nessun salvataggio possibile");
      return;
    }
    _logger.info("Cancellazione dati per "+cdLavoro);
    deleteDatiCostiStd(pm, stab,piano,cdLavoro,anno,mese );
    _logger.info("Salvataggio dati per "+cdLavoro);
    preProcessingDatiCosti(listInfo);
    saveDatiCostiStd(pm, listInfo);
    
  }
  
  
  private void deleteDatiCostiStd(PersistenceManager pm,String stab,String piano,String cdLavoro,Integer anno,Integer mese) throws SQLException{
    Map map=new HashMap();
    
    map.put(SfridiCostiProdPersistence.SCCONO,CostantsColomb.AZCOLOM);
    map.put(SfridiCostiProdPersistence.SCTIPO,SfridiCostiProdPersistence.TIPO_CONSUNTIVO);
    map.put(SfridiCostiProdPersistence.SCFACT,stab);
    map.put(SfridiCostiProdPersistence.SCPLAN,piano);
    map.put(SfridiCostiProdPersistence.SCPLGR,cdLavoro);
    map.put(SfridiCostiProdPersistence.SCANNO,anno);
    map.put(SfridiCostiProdPersistence.SCMESE,mese);
    
    pm.deleteDt(new SfridiCostiProdPersistence(), map);
  }
  
  
  private void preProcessingDatiCosti(List<List> info){
    for(List l:info){
      l.add(1,SfridiCostiProdPersistence.TIPO_CONSUNTIVO);
    }
  }
  
  
  private void saveDatiCostiStd(PersistenceManager pm,List listaInfo) throws SQLException{
    pm.saveListDt(new SfridiCostiProdPersistence(), listaInfo);
  }
  
  /**
   * Cerca tutti gli articoli che nel mese e anno forniti non hanno un costo al m2
   * Per ognuno  di essi calcola un costo medio del mese
   * @param con
   * @param anno
   * @param mese 
   */
  private void postProcessing(Connection con,Integer anno,Integer mese ){
    List<List> arti=new ArrayList();
    List errs=new ArrayList();
    List<List> medieCosti =new ArrayList();
    try{
      
      String selectneg=" SELECT DISTINCT SCMESE, SCPLGR, SCITNO  from "+ColombiniConnections.getAs400LibPersColom()+"."+SfridiCostiProdPersistence.TABNAME+
                       " WHERE SCANNO="+JDBCDataMapper.objectToSQL(anno)+
                       " and SCMESE="+  JDBCDataMapper.objectToSQL(mese)+
                       " AND SCSFRO<0"; 
      
      String selectcosti=" SELECT DISTINCT SCPLGR,SCITNO from "+ColombiniConnections.getAs400LibPersColom()+"."+SfridiCostiProdPersistence.TABNAME+
                      " WHERE SCANNO="+JDBCDataMapper.objectToSQL(anno)+
                      " and SCMESE="+  JDBCDataMapper.objectToSQL(mese)+
                      " and SCCOM2 =0 ";

      String selectmedie=" SELECT scplgr, sum(sccom2)/count(*) as media from "+ColombiniConnections.getAs400LibPersColom()+"."+SfridiCostiProdPersistence.TABNAME+
                         " WHERE SCANNO="+JDBCDataMapper.objectToSQL(anno)+
                         " and SCMESE="+  JDBCDataMapper.objectToSQL(mese)+
                         " group by scplgr ";

      
      ResultSetHelper.fillListList(con, selectneg, errs);
      if(!errs.isEmpty()){
        addWarning("Attenzione codici con m2 di sfrido di ottimizzazione negativo --> "+errs.toString());
      }

      ResultSetHelper.fillListList(con, selectcosti, arti);

// mod per P4 
//      if(arti.isEmpty())
//        return;

      ResultSetHelper.fillListList(con, selectmedie, medieCosti);
      Map mapCdlCosto=new HashMap<String,Double> ();
      
      for(List media:medieCosti){
        String cdl=ClassMapper.classToString(media.get(0));
        Double costo=ClassMapper.classToClass(media.get(1),Double.class);
        mapCdlCosto.put(cdl,costo);
      }

      for(List rec:arti){
        String articolo= ClassMapper.classToString(rec.get(1));
        String cdl= ClassMapper.classToString(rec.get(0));
        Double costoMedio=(Double) mapCdlCosto.get(cdl);
        
        updateCostoArticolo(con, cdl, articolo, anno, mese, costoMedio);
        
      }
    
    }catch(SQLException s){
      _logger.error("Errore in fase di ricerca dei codici senza costo --> "+s.getMessage() );
      addError("Errore in fase di ricerca dei codici senza costo -->"+s.toString());
    }
    
  }
  
  
  private void updateCostoArticolo(Connection con,String cdl,String articolo,Integer anno,Integer mese ,Double costo){
    String update ="UPDATE "+ColombiniConnections.getAs400LibPersColom()+"."+SfridiCostiProdPersistence.TABNAME+
                   " set SCCOM2= ? WHERE SCPLGR= ? and SCANNO=? and SCMESE=? and SCITNO=?";
    
    try {
      PreparedStatement pst=con.prepareStatement(update);
      
      pst.setDouble(1, costo);
      pst.setString(2, cdl);
      pst.setInt(3, anno);
      pst.setInt(4, mese);
      pst.setString(5, articolo);
       
      
      pst.execute(); 
    
    } catch (SQLException ex) {
      _logger.error("Errore nell'esecuzione dello statement x l'aggiornamento dell'articolo :"+articolo+" --> "+ex.getMessage() );
      addError("Errore in fase di aggiornamento del costo dell'articolo :"+articolo+" -->"+ex.toString());
    } 
    
        
  }
  
  private static final Logger _logger = Logger.getLogger(ElabCostiSfrido.class); 

  
  
}
