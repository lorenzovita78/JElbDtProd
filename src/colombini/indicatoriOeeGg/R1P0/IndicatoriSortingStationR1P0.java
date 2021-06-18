/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P0;

import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.query.indicatoriOee.linee.QuerySortingStatImaR1P0PzProd;
import colombini.query.indicatoriOee.linee.QuerySortingStatImaR1P0TProd;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
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
public class IndicatoriSortingStationR1P0 extends AIndicatoriLineaForOee {
  
  //per la sorting station prendiamo i dati 
  public final static String CDLIMBALLO="01035I"; 
  
  private final static Double SECPZ=Double.valueOf(14.0);

  private String cdlSorting;
  
  
  
  public IndicatoriSortingStationR1P0(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, CDLIMBALLO);
    
    this.cdlSorting=cdL;
    
  }

  
  
  
   @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    try{
      cancelDtProd();
      Map info=loadDatiProd(dataIni,dataFin);
      getIoeeBean().setNPzTot((Long)info.get(CostantsColomb.NPZTOT));
      getIoeeBean().setNPzTurni((Long)info.get(CostantsColomb.NPZTURNI));
      getIoeeBean().setTRun((Long)info.get(CostantsColomb.TRUNTIME));
      
      Long tDispTemp=(Long)info.get(CostantsColomb.TDISPON);

      //associamo il tempo della macchina solo se ha lavorato meno all'interno dei 2 turni
      if(tDispTemp<getIoeeBean().getTDispImpianto())
         getIoeeBean().setTDispImp(tDispTemp);
      
      getIoeeBean().setCdLavoro(cdlSorting);
    }catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
    }
  }

  
  private void cancelDtProd(){
   
    getIoeeBean().setTImprodImp(Long.valueOf(0));
    getIoeeBean().setTGuasti(Long.valueOf(0));
    getIoeeBean().setTSetup(Long.valueOf(0));
    getIoeeBean().setTMicrofermi(Long.valueOf(0));
    getIoeeBean().setTPerditeGest(Long.valueOf(0));
    getIoeeBean().setTVelRidotta(Long.valueOf(0));
    getIoeeBean().setTScarti(Long.valueOf(0));
    getIoeeBean().setTRilavorazioni(Long.valueOf(0));
    
    getIoeeBean().setNPzTot(Long.valueOf(0));
    getIoeeBean().setNGuasti(Long.valueOf(0));
  }
  
  
  
  private Map loadDatiProd(Date dataIni,Date dataFine) throws OEEException {
    Connection con=null;
    Map mapInfo=new HashMap();
    Long tDispImpTmp=Long.valueOf(0);
    Long nPzTot=Long.valueOf(0);
    Long nPzTurn=Long.valueOf(0);
    Long tRuntime=Long.valueOf(0);
      
    try {
      con=ColombiniConnections.getDbImaTopConnection();
      String dataS=DateUtils.DateToStr(dataIni, "yyyyMMdd");
      
      QuerySortingStatImaR1P0TProd qry=new QuerySortingStatImaR1P0TProd();
      qry.setFilter(QuerySortingStatImaR1P0TProd.FDATARIF, dataS);
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      
      if(obj!=null && obj.length>0)
        tDispImpTmp=ClassMapper.classToClass(obj[0], Long.class);
      
//      if(tDispImpTmp>tDispImp)
//        tDispImp=tDispImpTmp;
//      
//      if(tDispImp==0){
//        _logger.warn("Giorno non lavorativo . Impossibile elaborare i dati per il giorno :"+dataS);
//        return;
//      }
        
      
      QuerySortingStatImaR1P0PzProd qry2=new QuerySortingStatImaR1P0PzProd();
      qry2.setFilter(QuerySortingStatImaR1P0PzProd.FDATARIF, dataS);
      qry2.setFilter(QuerySortingStatImaR1P0PzProd.FDATAINI, DateUtils.DateToStr(dataIni,  "yyyyMMdd HH:mm:ss"));
      qry2.setFilter(QuerySortingStatImaR1P0PzProd.FDATAFIN, DateUtils.DateToStr(dataFine, "yyyyMMdd HH:mm:ss"));
      
      List pzP=new ArrayList();
      ResultSetHelper.fillListList(con, qry2.toSQLString(),pzP);
      if(pzP!=null && pzP.size()>0){
        nPzTot=ClassMapper.classToClass((  (List) pzP.get(0)).get(0), Long.class);
        nPzTurn=ClassMapper.classToClass(( (List) pzP.get(0)).get(1), Long.class);
      }
      
      
      tRuntime= new Double(nPzTot*SECPZ).longValue();
      _logger.info("Runtime totale :"+tRuntime);
      
      tRuntime= new Double(nPzTurn*SECPZ).longValue();
      
      mapInfo.put(CostantsColomb.TDISPON, tDispImpTmp);
      mapInfo.put(CostantsColomb.TRUNTIME, tRuntime);
      mapInfo.put(CostantsColomb.NPZTOT, nPzTot);
      mapInfo.put(CostantsColomb.NPZTURNI, nPzTurn);
      
      
    } catch (QueryException ex) {
      _logger.error("Impossibile reperire dati :Problemi di conversione dei dati "+ex.getMessage());
      throw new OEEException("Impossibile reperire dati :Problemi di conversione dei dati ");
    } catch (SQLException ex) {
       _logger.error("Impossibile interrogare il server "+ColomConnectionsCostant.SRVFORIMATOP+" :"+ex.getMessage());
       throw new OEEException("Problemi di accesso al server"+ColomConnectionsCostant.SRVFORIMATOP);
    } catch (ParseException ex) {
      _logger.error("Impossibile reperire dati :Problemi di conversione di data");
      throw new OEEException("Impossibile reperire dati :Problemi di conversione di data");
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error("Impossibile rilasciare la connessione con il server:"+ColomConnectionsCostant.SRVFORIMATOP);
      }
    }
    
    return mapInfo;
    
   
  }
 
  

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsImaTop xls=(XlsImaTop) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//      
//    Date dataInizio=xls.getInizioTurnoStdGg(data);
//    Date dataFine=xls.getFineTurnoStdGg(data);
//
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea .");
//      return ioee;
//    }
//    Map causaliOreImprod=new HashMap();
//    causaliOreImprod.put("Assemblea","IM");
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data, causaliOreImprod)*60); 
//    
//    try{
//      Map info=loadDatiProd(dataInizio,dataFine);
//      ioee.setNPzTot((Long)info.get(CostantsColomb.NPZTOT));
//      ioee.setNPzTurni((Long)info.get(CostantsColomb.NPZTURNI));
//      ioee.setTRun((Long)info.get(CostantsColomb.TRUNTIME));
//      
//      Long tDispTemp=(Long)info.get(CostantsColomb.TDISPON);
//
//      //associamo il tempo della macchina solo se ha lavorato meno all'interno dei 2 turni
//      if(tDispTemp<ioee.getTDispImpianto())
//         ioee.setTDispImp(tDispTemp);
//      
//      
//    }catch(OEEException e){
//      ioee.addError(e.getMessage());
//    }
//    
//    
//    return ioee;
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSortingStationR1P0.class);

 
}
