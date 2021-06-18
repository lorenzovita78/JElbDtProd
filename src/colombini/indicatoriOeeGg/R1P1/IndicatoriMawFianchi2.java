/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P1;

import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.CalcoloTempoCiclo;
import colombini.logFiles.R1P1.LogFileMawFianchi2;
import colombini.util.InfoMapLineeUtil;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
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
public class IndicatoriMawFianchi2 extends AIndicatoriLineaForOee  {
  
   
   private static final Integer NUMOCCORENZEFIANCHI=new Integer(2);
   private static final Long LIMINFSEC=new Long(60);
   private static final Long LIMMAXSEC=new Long(240);

  
   
   public IndicatoriMawFianchi2(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
   }
   

  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    
    try{
      Map mapT=calcolaTempiFromLogFile(con, dataIni, dataFin, getInfoTCdl().getCdl());

      getIoeeBean().setTRilavorazioni((Long)mapT.get(CostantsColomb.TRILAVORAZIONI));
      getIoeeBean().setTScarti((Long)mapT.get(CostantsColomb.TSCARTI));
      getIoeeBean().setTMicrofermi((Long) mapT.get(CostantsColomb.TMICROFERMI));
      getIoeeBean().setTRun((Long) mapT.get(CostantsColomb.TRUNTIME));
      getIoeeBean().setNPzTot((Long) mapT.get(CostantsColomb.NPZTOT));
      getIoeeBean().setNScarti((Long) mapT.get(CostantsColomb.NSCARTI));


      Long tMic=getIoeeBean().getTMicroFermi();
      _logger.info("Tempo Microfermi iniziale:"+tMic);
      Long tmpNNRil=getIoeeBean().getTPerditeNnRilevate();
      if(tmpNNRil<0){
        tMic+=tmpNNRil;
        _logger.info("Tempo Microfermi finale:"+tMic);
        getIoeeBean().setTMicrofermi(tMic);
      }
    
    } catch(OEEException e){
      getIoeeBean().addError(e.getMessage());
    }
    
    
  
  }
   
  
  private Map calcolaTempiFromLogFile(Connection con,Date dtInizio,Date dtFine,String cdLavoro) throws OEEException {
    LogFileMawFianchi2 lm2=null;
    Double tcicloT=Double.valueOf(0);
    Double tempoMedio=Double.valueOf(0);
    
    Map tempi=new HashMap();
    Long tMicrofermi=Long.valueOf(0);
    Long tRuntime=Long.valueOf(0);
    Long tRilavorazioni=Long.valueOf(0);
    Long nPz=Long.valueOf(0);
    Long nScarti=Long.valueOf(0);
    Long tScarti=Long.valueOf(0);
    String fileName="";
    try{
      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(cdLavoro), dtInizio);
//      String fileName=getLogFileName(dtInizio);
      lm2=new LogFileMawFianchi2(fileName);
      _logger.info("Apertura file log: "+fileName);
      lm2.initialize();
      
      List listColli=lm2.processLogFile(dtInizio, dtFine);
      if(listColli!=null){
        tMicrofermi=getTempoMicrofermi(listColli);
        
        CalcoloTempoCiclo ct=new CalcoloTempoCiclo(cdLavoro);
        tcicloT=ct.getTempoCiclo(con, listColli,NUMOCCORENZEFIANCHI);
        tRuntime=tcicloT.longValue();
        tRilavorazioni=ct.gettRilavorazioni().longValue();
        nPz=new Long(listColli.size());
      }
      
      if(tRuntime>0 && nPz>0){
        nScarti=getNumPzScarto(con, dtInizio,cdLavoro);
        
        tempoMedio=tRuntime/new Double(nPz);        
        tScarti=new Double(nScarti*tempoMedio).longValue();
      }
      
      tempi.put(CostantsColomb.TMICROFERMI, tMicrofermi);
      tempi.put(CostantsColomb.TRUNTIME, tRuntime);
      tempi.put(CostantsColomb.TRILAVORAZIONI, tRilavorazioni);
      tempi.put(CostantsColomb.TSCARTI, tScarti);
      tempi.put(CostantsColomb.NPZTOT,nPz);
      tempi.put(CostantsColomb.NSCARTI,nScarti);
      
      
      _logger.info("Runtime: "+tRuntime + " ; ncolli:"+nPz + " ; tScarto:"+tScarti);
    } catch (SQLException ex) {
      _logger.error("Errore in fase di accesso al db :"+ex.getMessage());
      throw new OEEException("Errore in fase di accesso al db x la lettura dei tempi ciclo");
    } catch (FileNotFoundException ex) {
      _logger.error("Impossibile accedere al file :"+ex.getMessage());
      throw new OEEException("Impossibile accedere al file di log"+fileName);
    } catch (IOException ex) {
      _logger.error("Problemi di I/O con il log file :"+ex.getMessage());
      throw new OEEException("Problemi di accesso al file di log"+fileName);
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione di date :"+ex.getMessage());
      throw new OEEException("Problemi in fase di lettura del file di log"+fileName);
    }finally{
      try {
        if(lm2!=null)
          lm2.terminate();
      } catch (FileNotFoundException ex) {
        _logger.error("Problemi di accesso al log di file in chiusura:"+ex.getMessage());
      } catch (IOException ex) {
        _logger.error("Problemi di I/O con il log file in chiusura:"+ex.getMessage());
      }
    }
      return tempi;
  }
  

  private Long getNumPzScarto(Connection con,Date data,String cdLavoro) throws ParseException, SQLException{
    Long pzScarti=Long.valueOf(0);
    String dataS=DateUtils.DateToStr(data,"yyyyMMdd");
    String sql=" select ifnull(sum(Z9TRQT),0) as scarto from mcobmoddta.pmz790pf "
              + " where Z9PLGR="+JDBCDataMapper.objectToSQL(cdLavoro)
              + " and Z9TRDT = "+JDBCDataMapper.objectToSQL(new Long(dataS));

    Object [] obj=ResultSetHelper.SingleRowSelect(con, sql);
    
    if(obj!=null && obj.length>0)
      pzScarti=ClassMapper.classToClass(obj[0], Long.class);
    
    return pzScarti;
  } 
  
  
  private Long getTempoMicrofermi(List<List> colli){
    Long microF=Long.valueOf(0);
    Date dataOld=null;
    for(List record: colli){
      Date dataNew=ClassMapper.classToClass(record.get(2), Date.class);
      if(dataOld!=null && dataNew!=null){
        Long valTmp=DateUtils.numSecondiDiff(dataOld, dataNew);
        valTmp=valTmp-LIMINFSEC;
        if(valTmp>0 && valTmp<=LIMMAXSEC)
          microF+=valTmp;
      }  
      dataOld=dataNew ;
    }
    
    return microF;
  }
  
 
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsMawFianchi2 xls=(XlsMawFianchi2) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    //istanziamo l'oggetto per l'indicatore
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    
//    Date dataInizio=xls.getInizioTurnoGg(data);
//    Date dataFine=xls.getFineTurnoGg(data);
// 
//    if(dataInizio==null || dataFine==null){
//      ioee.addError(" Turni di lavoro  non specificati . ");
//      return ioee;
//    }
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data)*60);
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    // non prendo i microfermi dalle causali impostate sul file xls ma verranno calcolati da file log
//    // i microfermi indicati nel file xls vengono cmq salvati nel tabella dei fermi giornalieri
//    //tMicrofermi=MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_MICROFRM, Long.class)*60;
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    try{
//      Map mapT=calcolaTempiFromLogFile(con, dataInizio, dataFine, centrodiLavoro);
//
//      ioee.setTRilavorazioni((Long)mapT.get(CostantsColomb.TRILAVORAZIONI));
//      ioee.setTScarti((Long)mapT.get(CostantsColomb.TSCARTI));
//      ioee.setTMicrofermi((Long) mapT.get(CostantsColomb.TMICROFERMI));
//      ioee.setTRun((Long) mapT.get(CostantsColomb.TRUNTIME));
//      ioee.setNPzTot((Long) mapT.get(CostantsColomb.NPZTOT));
//      ioee.setNScarti((Long) mapT.get(CostantsColomb.NSCARTI));
//
//
//      Long tMic=ioee.getTMicroFermi();
//      _logger.info("Tempo Microfermi iniziale:"+tMic);
//      Long tmpNNRil=ioee.getTPerditeNnRilevate();
//      if(tmpNNRil<0){
//        tMic+=tmpNNRil;
//        _logger.info("Tempo Microfermi finale:"+tMic);
//        ioee.setTMicrofermi(tMic);
//      }
//    
//    } catch(OEEException e){
//      ioee.addError(e.getMessage());
//    }
//    
//    return ioee;
//  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriMawFianchi2.class);

  
}
