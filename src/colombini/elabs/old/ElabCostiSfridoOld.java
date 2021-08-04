/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.old;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.CalcCostiSfridoArticoliMvx;
import colombini.dtProd.sfridi.old.CostiSfridoArticoliImpImaR1;
import colombini.dtProd.sfridi.CostiSfridoArticoliOtmGal;
import colombini.dtProd.sfridi.ISfridoInfo;
import colombini.model.persistence.tab.SfridiCostiProdPersistence;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabCostiSfridoOld extends ElabClass{
  
  
  
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
        String condSfrido=" SUM(ZSSUPD-ZSSUPU -ZSSFIS) - (( (MAX(ZSLNGT)* MAX(ZSWDTH) * SUM(ZSTTPD)) /1000000 ) * (0.01)  )" ;
        String condSfridoFis="SUM(ZSSPDF)";
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.GALAZZANO, CostantsColomb.PIANO0, ISfridoInfo.OTMGALP0,condSfrido,condSfridoFis,false);
        gestDatiCosti(manP, CostantsColomb.GALAZZANO, CostantsColomb.PIANO0, ISfridoInfo.OTMGALP0,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Nesting Galazzano -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Nesting -->"+ex.toString());
      }

      try{
        _logger.info(" Dati per Sezionatrice R2 ");
        //viene tolto il centrimetro di rifolo dallo sfrido di ottimizzazione
        String condSfrido=" SUM(ZSSUPD-ZSSUPU) - (( (MAX(ZSLNGT)* MAX(ZSWDTH) * SUM(ZSTTPD)) /1000000 ) * (0.01)  )" ;
        List lista=CalcCostiSfridoArticoliMvx.getInstance().getDatiCostiSfridoPeriodo(manP.getConnection(), annoRif, meseRif, CostantsColomb.ROVERETA2, CostantsColomb.PIANO1, ISfridoInfo.SEZR2P1, condSfrido,"",false);
        gestDatiCosti(manP, CostantsColomb.ROVERETA2, CostantsColomb.PIANO1, ISfridoInfo.SEZR2P1,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Sezionatrice R2 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Sezionatri-->"+ex.toString());
      }

      try{
        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 0 ");
        CostiSfridoArticoliImpImaR1 cII=new CostiSfridoArticoliImpImaR1(ISfridoInfo.OTMR1P0, annoRif, meseRif);
        List lista=cII.getDatiSfridoImpiantiIma();
        lista=CalcCostiSfridoArticoliMvx.getInstance().aggDatiCostoArticolo(manP.getConnection(), lista);

        gestDatiCosti(manP, CostantsColomb.ROVERETA1, CostantsColomb.PIANO0, ISfridoInfo.OTMR1P0,annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.toString());
      }catch(QueryException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 0 -->"+ex.toString());
      }

      try{
        _logger.info(" Dati Ottimizzatore Rovereta 1 Piano 1");
        CostiSfridoArticoliImpImaR1 cII=new CostiSfridoArticoliImpImaR1(ISfridoInfo.OTMR1P1, annoRif, meseRif);
        List lista=cII.getDatiSfridoImpiantiIma();
        lista=CalcCostiSfridoArticoliMvx.getInstance().aggDatiCostoArticolo(manP.getConnection(), lista);

        gestDatiCosti(manP, CostantsColomb.ROVERETA1, CostantsColomb.PIANO1, ISfridoInfo.OTMR1P1, annoRif, meseRif, lista);

      }catch(SQLException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.toString());
      }catch(QueryException ex){
        _logger.error(" Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.getMessage());
        addError("Errore in fase di calcolo dei dati per Ottimizzatore Rovereta 1 Piano 1 -->"+ex.toString());
      }
      
      
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
    saveDatiCostiStd(pm, listInfo);
    
  }
  
  
  private void deleteDatiCostiStd(PersistenceManager pm,String stab,String piano,String cdLavoro,Integer anno,Integer mese) throws SQLException{
    Map map=new HashMap();
    
    map.put(SfridiCostiProdPersistence.SCCONO,CostantsColomb.AZCOLOM);
    map.put(SfridiCostiProdPersistence.SCFACT,stab);
    map.put(SfridiCostiProdPersistence.SCPLAN,piano);
    map.put(SfridiCostiProdPersistence.SCPLGR,cdLavoro);
    map.put(SfridiCostiProdPersistence.SCANNO,anno);
    map.put(SfridiCostiProdPersistence.SCMESE,mese);
    
    pm.deleteDt(new SfridiCostiProdPersistence(), map);
  }
  
  
  private void saveDatiCostiStd(PersistenceManager pm,List listaInfo) throws SQLException{
    pm.saveListDt(new SfridiCostiProdPersistence(), listaInfo);
  }
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabCostiSfridoOld.class); 

  
  
}
