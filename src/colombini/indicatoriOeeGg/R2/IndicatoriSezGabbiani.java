/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R2;


import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.model.CausaliLineeBean;
import colombini.query.indicatoriOee.linee.QuerySezGabbianiR2DatiAlarms;
import colombini.query.indicatoriOee.linee.QuerySezGabbianiR2DatiRuntime;
import db.Connections;
import db.ConnectionsProps;
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
import utils.ArrayListSorted;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriSezGabbiani  extends AIndicatoriLineaForOee {
  
  
  private final static String PCGABBIANI="PCGABBIANI";
  private final static String DBGABBIANI="DBGABBIANI";
  private final static String USRGABBIANI="USRGABBIANI";
  private final static String PWDGABBIANI="PWDGABBIANI";

  private final static String CAUSFERMOPPRANZO="SOP";
  private final static String CAUSFERMOSTD="ASP";
 
   
  private List fermiGg=null;
  
  private List<String> errorList=null;

  public IndicatoriSezGabbiani(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
     errorList=new ArrayList();
    Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
    fermiGg=new ArrayList();
    try {
      Date dtInizio = DateUtils.getInizioGg(dataIni);
      Date dtFine=DateUtils.getFineGg(dataIni);
//      Date dtFine=DateUtils.StrToDate("21/11/2014 16:30:00","dd/MM/yyyy HH:mm:ss");
      
      Map info=elabDatiMacchina(dtInizio, dtFine, causali);
      if(info.isEmpty()){
        getIoeeBean().addWarning("Attenzione dati non presenti per la giornata indicata");
        return ;
      }
        
      getIoeeBean().setTDispImp((Long)info.get(CostantsColomb.TDISPON));
      getIoeeBean().setTImprodImp((Long)info.get(CostantsColomb.TIMPROD));
      getIoeeBean().setTRun((Long)info.get(CostantsColomb.TRUNTIME));
      getIoeeBean().setTGuasti((Long)info.get(CostantsColomb.TGUASTI));
      getIoeeBean().setNGuasti((Long)info.get(CostantsColomb.NGUASTI));
      getIoeeBean().setTPerditeGest((Long)info.get(CostantsColomb.TPERDGEST));
      getIoeeBean().setTSetup((Long)info.get(CostantsColomb.TSETUP));    
      fermiGg=(List) info.get(ICalcIndicatoriOeeLinea.LISTFERMIOEE);
      
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione delle date -->"+ex.getMessage());
      getIoeeBean().addError("Problemi in fase di decodifica delle date di inizio e fine turno");
    }
    
    
    if(!errorList.isEmpty()){
      getIoeeBean().addErrors(errorList);
    }

  }
  

  private Map elabDatiMacchina(Date dtInizio, Date dtFine,Map causali) {
    List<List> listaProd=getListFasiProd(dtInizio,dtFine);
    List<List> fermiNNCaus=new ArrayList();
    Date inizioT=null;
    Date fineT=null;
    Date inizioF=null;
    Date fineF=null;
    List listFermi=new ArrayList();
    Date dataOld=null;
    
    Map info=new HashMap();
    
    
    if(listaProd==null || listaProd.isEmpty()){
      _logger.warn("Attenzione dati di produzione non presenti per il giorno "+dtInizio);
      return info;
    }
    List<List> listaFermi=getListFermi(dtInizio,dtFine);
    if(listaFermi==null || listaFermi.isEmpty()){
      _logger.info("Lista fermi vuota");
      listaFermi=new ArrayList();
    }else{
      _logger.info("Lista fermi --> "+listaFermi);
    }
    
//    Long totFermiProd=Long.valueOf(0);
    Long tImprodImp=Long.valueOf(0);
    Long tRuntime=Long.valueOf(0);
    
    for(List infoProd : listaProd){
      Date dataInizioP=ClassMapper.classToClass(infoProd.get(4),Date.class);
      Date dataFineP=ClassMapper.classToClass(infoProd.get(5),Date.class);        
      Long secProd=ClassMapper.classToClass(infoProd.get(6),Long.class);
      Long secFermo=getDurataFermo(listaFermi, dataInizioP, dataFineP);
      

      if(secProd>3600 && secFermo==0){
        List rec =new ArrayList();
        rec.add(CAUSFERMOSTD);
        rec.add(dataInizioP);
        rec.add(dataFineP);
        fermiNNCaus.add(rec);
        
        _logger.warn("Attenzione Periodo di Produzione superiore all'ora -->possibile fermo macchina non intercettato !!"+secProd);
        _logger.warn("Associato fermo "+CAUSFERMOSTD+ " con periodo :"+dataInizioP+" -- "+dataFineP);
        secProd=Long.valueOf(0);
      }
      
      if(dataOld!=null ){
        Long sec=DateUtils.numSecondiDiff(dataOld, dataInizioP);
        
        if(sec>3600){
          Long sFermo=getDurataFermo(listaFermi, dataOld, dataInizioP);
          _logger.warn("Attenzione periodo produttivo con buco superiore all'ora --> dataFine:"+dataOld+" -- dataInizio:"+dataInizioP);
          tImprodImp+=(sec-sFermo);  
        }  
      }
      
      
//      totFermiProd+=secFermo;
      tRuntime+=(secProd-secFermo);
      if(inizioT==null)
        inizioT=dataInizioP;
      
      fineT=dataFineP;
      dataOld=dataFineP;
      
    }
    
    for(List fm: fermiNNCaus){
      listaFermi.add(fm);
    }
    
    
    for(List fermoTmp:listaFermi){
      Date iniTmpF=(Date)fermoTmp.get(1);
      
      fineF=(Date)fermoTmp.get(2);
      if(inizioF==null)
        inizioF=iniTmpF;
      
      
      Long secFermo=DateUtils.numSecondiDiff(iniTmpF, fineF);
      String causale=ClassMapper.classToString(fermoTmp.get(0));
      //escludo la causale della pausa pranzo
      if(!CAUSFERMOPPRANZO.equals(causale)){
        List fm=new ArrayList();
        fm.add(causale);
        fm.add(secFermo);
        listFermi.add(fm);
      }
    }
    
    tImprodImp+=getDurataFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_ORENONPROD);
    
    info.put(CostantsColomb.TDISPON, getTempoDisponibilita(inizioT,fineT,inizioF,fineF));
    info.put(CostantsColomb.TRUNTIME,tRuntime);
    info.put(CostantsColomb.TGUASTI, getDurataFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_GUASTO));
    info.put(CostantsColomb.NGUASTI, getNumeroFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_GUASTO));
    info.put(CostantsColomb.TPERDGEST, getDurataFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_PERDGEST));
    info.put(CostantsColomb.TSETUP, getDurataFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_SETUP));    
    info.put(CostantsColomb.TIMPROD, tImprodImp);
    
    info.put(ICalcIndicatoriOeeLinea.LISTFERMIOEE, listFermi);
    
    return info;
  }
  
  
  
  private List getListFasiProd(Date dtInizio, Date dtFine) {
   Connection conSQL=null;
   List list=new ArrayList(); 
   
   try {
      conSQL=ColombiniConnections.getDbQuidR2Connection();
      QuerySezGabbianiR2DatiRuntime qry=new QuerySezGabbianiR2DatiRuntime();
      qry.setFilter(QuerySezGabbianiR2DatiRuntime.DATAINIZIO, DateUtils.DateToStr(dtInizio, "yyyyMMdd  HH:mm:ss "));
      qry.setFilter(QuerySezGabbianiR2DatiRuntime.DATAFINE, DateUtils.DateToStr(dtFine, "yyyyMMdd  HH:mm:ss "));
      
      
      String query=qry.toSQLString();
      _logger.info(query);
      ResultSetHelper.fillListList(conSQL, query, list);
   } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
    } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBR2 + " e database "+ColomConnectionsCostant.DBQUIDP+" --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (ParseException ex) {
        _logger.error(" Problemi di conversione delle date fornite --> "+ex.getMessage());
        errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } finally {
     if(conSQL!=null)
       try {
        conSQL.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi nella chiusura della connessione con server "+ColomConnectionsCostant.SRVDBR2 + " e database "+ColomConnectionsCostant.DBQUIDP+" --> "+ex.getMessage());
      }
   }
   
   return list;
  }
  
 
 private List getListFermi(Date dtInizio, Date dtFine) {
   Connection conSQL=null;
   List list=new ArrayList(); 
   String srv="";
   String db="";
   try {
     srv=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(PCGABBIANI));
     //necessario....
     if(srv.contains("/"))
      srv=srv.replace("/", "\\");
     
     db=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(DBGABBIANI));
     String usr=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(USRGABBIANI));
     String pwd=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(PWDGABBIANI));
    
     conSQL=Connections.getMicrosoftSQLServerConnection(srv, db, usr, pwd);
     QuerySezGabbianiR2DatiAlarms qry=new QuerySezGabbianiR2DatiAlarms();
     qry.setFilter(QuerySezGabbianiR2DatiRuntime.DATAINIZIO, DateUtils.DateToStr(dtInizio, "yyyyMMdd  HH:mm:ss "));
     qry.setFilter(QuerySezGabbianiR2DatiRuntime.DATAFINE, DateUtils.DateToStr(dtFine, "yyyyMMdd  HH:mm:ss "));
      
     String query=qry.toSQLString();
     _logger.info(query);
     ResultSetHelper.fillListList(conSQL, query, list);
     
   } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      errorList.add("Impossibile individuare i fermi della linea.Problemi nell'interrogazione del database");
   } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+srv + " e database "+db+" --> "+ex.getMessage());
      errorList.add("Impossibile individuare i fermi della linea. Problemi nell'interrogazione del database");
   } catch (ParseException ex) {
        _logger.error(" Problemi di conversione delle date fornite --> "+ex.getMessage());
        errorList.add("Impossibile individuare i fermi della linea.Problemi nell'interrogazione del database");
   } finally {
     if(conSQL!=null)
       try {
        conSQL.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi nella chiusura della connessione con server"+ColomConnectionsCostant.SRVDBR2 + " e database "+ColomConnectionsCostant.DBQUIDP+" --> "+ex.getMessage());
      }
   }
   
   return elabListFermi(list);
  }
  
  private List elabListFermi(List<List> list) {
    ArrayListSorted fermi=new ArrayListSorted(1);
    ArrayListSorted fermiUnion=new ArrayListSorted(1);
    Map fermiIns=new HashMap();
    
    int idx=0;
    for(List record: list){
      String nodeId=ClassMapper.classToString(record.get(0));
      String caus=ClassMapper.classToString(record.get(4));
      Integer status=ClassMapper.classToClass(record.get(3),Integer.class);
      if(!StringUtils.IsEmpty(caus) && (status!=null && status>0) ){
        String tipoEve=ClassMapper.classToString(record.get(2));
        Date dataFine=ClassMapper.classToClass(record.get(1),Date.class);
        Date dataInizio=getInizioEvento(list,nodeId,tipoEve,(idx-1));
        if(dataInizio!=null){
          List infoFermo= new ArrayList();
          infoFermo.add(caus);
          infoFermo.add(dataInizio);
          infoFermo.add(dataFine);
          String key=caus+'-'+DateUtils.dateToStr(dataInizio, "yyyyMMddHHssmm")+'-'+DateUtils.dateToStr(dataFine, "yyyyMMddHHssmm");
          if(!fermiIns.containsKey(key)){
            fermi.add(infoFermo);
            fermiIns.put(key, "Y");
          }
        }
        
      }
      idx++;
    }
    
    //verifico se ci sono fermi sovrapposti e li unisco
    Date dataFOld=null;
    Date dataIOld=null;
    for(int i=0;i<fermi.size();i++){
      List fermo=((List)fermi.get(i));
      Date dataIni=ClassMapper.classToClass(fermo.get(1),Date.class);
      Date dataFin=ClassMapper.classToClass(fermo.get(2),Date.class);
      if(dataFOld!=null){
        
        //se la dataInizio è all'interno del periodo del vecchio fermo :
        if(dataIni.compareTo(dataFOld)<0){
        //se la dataFine del fermo corrente è maggiore della dataFine del fermo vecchio allora
        // il periodo del fermo attuale diventa dataFOld dataFine
          if(dataFin.compareTo(dataFOld)>0){
             fermo.set(1, dataFOld);
             fermiUnion.add(fermo);
          }
        //se il fermo attuale è incluso nel periodo del fermo precedente allora non lo aggiungo!!
        
        }else{
          fermiUnion.add(fermo);
        }
      }else{
      
        fermiUnion.add(fermo);
      }
      dataIOld=dataIni;
      dataFOld=dataFin;
    }
//    for(int i=0;i<fermi.size();i++){
//      List fermo=((List)fermi.get(i));
//      Date dataIni=ClassMapper.classToClass(fermo.get(1),Date.class);
//      Date dataFin=ClassMapper.classToClass(fermo.get(2),Date.class);
//      if(dataFOld!=null){
//        if(dataIni.compareTo(dataFOld)<0){
//          fermo.set(1, dataIOld);
//        }
//        if(dataFOld.compareTo(dataFin)>0){
//          fermo.set(2, dataFOld);
//        }
//      }
//      
//      fermiUnion.add(fermo);
//      dataIOld=dataIni;
//      dataFOld=dataFin;
//    }
    
    
    return fermiUnion;
  }
  
  
  private Date getInizioEvento(List<List> list,String nodeId,String evento, int idx) {
    Date dataIni=null; 
    for(int i=idx; (i>=0 && dataIni==null); i--){
      List record=list.get(i);
      Integer status=ClassMapper.classToClass(record.get(3),Integer.class);
      String eventoTmp=ClassMapper.classToString(record.get(2));
      String nodeIdTmp=ClassMapper.classToString(record.get(0));
      if(status==0 && eventoTmp.equals(evento) && nodeIdTmp.equals(nodeId))
        dataIni=ClassMapper.classToClass(record.get(1),Date.class);
    }
    
    return dataIni;
  }
  
  
 
  private Long getDurataFermo(List<List> fermi,Date dataInizioP, Date dataFineP) {
    Long durataF=Long.valueOf(0);
    int count=0;
    for(List fermoTmp : fermi ){
      Date dataInizioFermo=ClassMapper.classToClass(fermoTmp.get(1),Date.class);
      Date dataFineFermo=ClassMapper.classToClass(fermoTmp.get(2),Date.class);
       
      if(DateUtils.afterEquals(dataFineP, dataInizioFermo) && DateUtils.beforeEquals(dataInizioP, dataFineFermo) ) {
        count++;
        if( DateUtils.beforeEquals(dataInizioP, dataInizioFermo) && DateUtils.afterEquals(dataFineP, dataFineFermo)  ){ 
          // il fermo è all'interno del periodo di produzione
          durataF+=DateUtils.numSecondiDiff(dataInizioFermo, dataFineFermo);
        }else if((dataInizioFermo.before(dataInizioP) && dataFineFermo.after(dataFineP))){
          // il fermo è più grande del periodo di produzione
          durataF+=DateUtils.numSecondiDiff(dataInizioP, dataFineP);
        
        }else if(dataFineFermo.after(dataInizioP) && DateUtils.beforeEquals(dataFineFermo, dataFineP)){
          durataF+=DateUtils.numSecondiDiff(dataInizioP, dataFineFermo);
          
        }else if(DateUtils.afterEquals(dataInizioFermo, dataInizioP) && dataInizioFermo.before(dataFineP)){
          durataF+=DateUtils.numSecondiDiff(dataInizioFermo, dataFineP);
        }
   
      }
    
    }
    
    _logger.debug(" Totale sec fermo nel periodo"+durataF+ " n fermi"+count);
    return durataF;
    
  }
  
  
 
  private Long getTempoDisponibilita(Date inizioT, Date fineT, Date inizioF, Date fineF) {
    Date inizio=null;
    Date fine=null;
    if(inizioT==null || fineT==null){
      _logger.error("Attenzione Periodi di produzione non valorizzati.Impossibile calcolare le ore impianto!!");
      return Long.valueOf(0);
    }
    
    if(inizioF==null && fineF==null){
     _logger.info("Turni di lavoro --> "+inizioT+" - "+fineT+"  -- durataTot:"+DateUtils.numSecondiDiff(inizioT, fineT));
     return DateUtils.numSecondiDiff(inizioT, fineT);
    }
    
    inizio=inizioT;
    fine=fineT;
    
    _logger.info("Inizio produzione>> "+inizioT +" - fine produzione>> "+fineT);
    
    if(inizioF!=null && inizioF.before(inizioT)){
      inizio=inizioF;
      _logger.info(" Inizio fermi inferiore a inizio produzione.Apertura impianto ="+inizio);
    }
    
    
    if(fineF!=null && fineF.after(fineT)){
      fine=fineF;
      _logger.info(" Fine fermi superiore a fine produzione.Chiusura impianto ="+fine);
    }
      
    return DateUtils.numSecondiDiff(inizio, fine);
  }
  
  private Long getDurataFermiTipo(List<List> fermi,Map causali,String tipo){
    Long durata=Long.valueOf(0);
    for(List fermo:fermi){
       String caus=(String) fermo.get(0);
       Long secFermo=(Long) fermo.get(1);
       if(causali.containsKey(caus)){
         CausaliLineeBean cb=(CausaliLineeBean) causali.get(caus);
         if(tipo.equals(cb.getTipo()) ){
           durata+=secFermo;
         }
       }
    }
    return durata;
  }
  
  private Long getNumeroFermiTipo(List<List> fermi,Map causali,String tipo){
    Long num=Long.valueOf(0);
    for(List fermo:fermi){
       String caus=(String) fermo.get(0);
       if(causali.containsKey(caus)){
         CausaliLineeBean cb=(CausaliLineeBean) causali.get(caus);
         if(tipo.equals(cb.getTipo()) ){
           num+=1;
         }
       }
    }
    return num;
  }
  
  
  
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    errorList=new ArrayList();
//    Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    fermiGg=new ArrayList();
//    try {
//      Date dtInizio = DateUtils.getInizioGg(data);
//      Date dtFine=DateUtils.getFineGg(data);
////      Date dtFine=DateUtils.StrToDate("21/11/2014 16:30:00","dd/MM/yyyy HH:mm:ss");
//      
//      Map info=elabDatiMacchina(dtInizio, dtFine, causali);
//      if(info.isEmpty()){
//        ioee.addWarning("Attenzione dati non presenti per la giornata indicata");
//        return ioee;
//      }
//        
//      ioee.setTDispImp((Long)info.get(CostantsColomb.TDISPON));
//      ioee.setTImprodImp((Long)info.get(CostantsColomb.TIMPROD));
//      ioee.setTRun((Long)info.get(CostantsColomb.TRUNTIME));
//      ioee.setTGuasti((Long)info.get(CostantsColomb.TGUASTI));
//      ioee.setNGuasti((Long)info.get(CostantsColomb.NGUASTI));
//      ioee.setTPerditeGest((Long)info.get(CostantsColomb.TPERDGEST));
//      ioee.setTSetup((Long)info.get(CostantsColomb.TSETUP));    
//      fermiGg=(List) info.get(ICalcIndicatoriOeeLinea.LISTFERMIOEE);
//      
//      
//    } catch (ParseException ex) {
//      _logger.error("Problemi di conversione delle date -->"+ex.getMessage());
//      ioee.addError("Problemi in fase di decodifica delle date di inizio e fine turno");
//    }
//    
//    if(!errorList.isEmpty()){
//      ioee.addErrors(errorList);
//    }
//    
//    return ioee; 
//  }

  public List getFermiGg() {
    return fermiGg;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriSezGabbiani.class);

  

  
}
