/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P2;


import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.ElabDatiOrdiniProdMovex;
import colombini.indicatoriOee.utils.OeeUtils;
import colombini.model.CausaliLineeBean;
import colombini.model.IProdSupervisoreForBiesseBean;
import colombini.model.persistence.TempiCicloForBiesseG1;
import colombini.query.indicatoriOee.linee.QueryForBiesseG1P2DatiFermi;
import colombini.query.indicatoriOee.linee.QueryForBiesseG1P2DatiProd;
import colombini.query.indicatoriOee.linee.QueryForBiesseG1P2PeriodiProd;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public class IndicatoriForatriciBiesse  extends AIndicatoriLineaForOee {
  
 
  //public final static Integer TIPOEVENTOFERMO=Integer.valueOf(1);
  
  public final static String BIS="BIS";
  
  private List fermiGg=null;
  
  private List<String> errorList=null;

  private String cdlIni=null;
  
  //Boolean isBis=
  
  public IndicatoriForatriciBiesse(Integer azienda, Date dataRif, String cdL) {
    
    super(azienda, dataRif, cdL);
    
      
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    Boolean calcOnlySuper=Boolean.FALSE;
    Connection conSQL=null;
    List periodiProd=new ArrayList();
    List periodiChiusura=null; 
    Date oraInizio=null;
    Date oraFine=null;
    
    
    
    if(this.getIoeeBean().getCdLav().contains(BIS))
      calcOnlySuper=Boolean.TRUE;
    
    try{
      conSQL=getDbForBiesseGalConnection(this.getIoeeBean().getCdLav());
    
      oraInizio=getOraIniStd(dataIni);
      oraFine=getOraFinStd(dataFin);
    
      if(calcOnlySuper){
        periodiProd=getListPeriodiProd(conSQL, oraInizio,oraFine);
        if(periodiProd.size()>0){
          oraInizio=ClassMapper.classToClass( ((List)periodiProd.get(0) ).get(0),Date.class);
          oraFine=ClassMapper.classToClass( ((List)periodiProd.get(0) ).get(1),Date.class);
          if(oraInizio==null || oraFine==null){
            this.getIoeeBean().addError(" Turni di lavoro  non specificati per linea .");
            return ;
          }
        }else{
          this.getIoeeBean().addError(" Turni di lavoro  non specificati per linea .");
          return ;
        }
      }else{ 
        
        periodiProd=getInfoTCdl().getListTurniValidi(oraInizio,oraFine);
        periodiChiusura=getInfoTCdl().getListBuchiTurni(periodiProd);

        String oraini=DateUtils.dateToStr(oraInizio, "HHmmss");
        String orafin=DateUtils.dateToStr(oraFine, "HHmmss");
        loadDatiFromMvx( con, getIoeeBean().getCdLav(), getIoeeBean().getData(), oraini, orafin);
      
      } 

      errorList=new ArrayList();
      Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
      fermiGg=new ArrayList();
      IProdSupervisoreForBiesseBean infoS=elabDatiMacchina(con,conSQL,periodiProd,periodiChiusura, getIoeeBean().getCdLav(),oraInizio,oraFine,calcOnlySuper);

      getIoeeBean().setTDispImp(DateUtils.getDurataPeriodiSs(periodiProd));
      getIoeeBean().setTImprodImp(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_ORENONPROD));

      getIoeeBean().setTRun(infoS.gettRuntimeEff().longValue());
      getIoeeBean().setTGuasti(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_GUASTO));
      getIoeeBean().setNGuasti(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_NGUASTI));
      getIoeeBean().setNPzTot(infoS.getQtaTot());
      getIoeeBean().setTMicrofermi(infoS.gettMicrofEventi()+infoS.gettMicrof().longValue());
      getIoeeBean().setTPerditeGest(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_PERDGEST));
      getIoeeBean().setTSetup(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_SETUP));    


      _logger.info("Tempo produzione lotti "+infoS.getTempoProdSupervisore()+"  -- tempo Impianto calcolato "+getIoeeBean().getTDispImpianto());
      //nei tempi esterni salbo il tempo produzione da log (t1) e buchidiProd (t2)  ;
      //t3 serve per annullare t1 e t2 nel calcolo delle perdite non rilevate
      //ioee.setTExt1(infoS.getTempoProdSupervisore());
      getIoeeBean().setTExt1(infoS.getTempoFermiNnCausalizzati());

      if(calcOnlySuper){
        getIoeeBean().setTExt2(infoS.getTempoNnProdSupervisore());
        getIoeeBean().setTExt3(-getIoeeBean().getTExt2());
      }  
 
      if(!errorList.isEmpty()){
        getIoeeBean().addErrors(errorList);
      }
    } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (ParseException p){
      _logger.error(" Problemi nella conversione dei dati --> "+p.getMessage());
      errorList.add("Problemi nella conversione dei dati  :"+p.toString());
   } 
    finally {
     if(conSQL!=null)
       try {
        conSQL.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi nella chiusura della connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO + " --> "+ex.getMessage());
      }
   }  
      
  }
  
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    Boolean calcOnlySuper=Boolean.FALSE;
//    Connection conSQL=null;
//    List periodiProd=new ArrayList();
//    List periodiChiusura=null; 
//    Date oraInizio=null;
//    Date oraFine=null;
//            
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    XlsNuoveForatriciBiesse xls=(XlsNuoveForatriciBiesse) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Integer rigaIni=xls.getRigaInizioGg(data)+xls.getDeltaRigheXls();
//    Integer rigaFin=xls.getRigaFineGg(data)-xls.getDeltaRigheXls();
//    oraInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//    oraFine=xls.getFineTurno(data, rigaIni, rigaFin);
//        
//    if(oraInizio==null || oraFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea .");
//      return ioee;
//    }
//    
//    if(centrodiLavoro.contains(BIS)){
//      calcOnlySuper=Boolean.TRUE;
////      centrodiLavoro=centrodiLavoro.replace(BIS, "");
//    }
//    try{
//      conSQL=getDbForBiesseGalConnection(centrodiLavoro);
//    
//      if(calcOnlySuper){
//        periodiProd=getListPeriodiProd(conSQL, oraInizio,oraFine);
//        if(periodiProd.size()>0){
//          oraInizio=ClassMapper.classToClass( ((List)periodiProd.get(0) ).get(0),Date.class);
//          oraFine=ClassMapper.classToClass( ((List)periodiProd.get(0) ).get(1),Date.class);
//        }else{
//          ioee.addError(" Turni di lavoro  non specificati per linea .");
//          return ioee;
//        }
//      }else{ 
//        
//        periodiProd=xls.getPeriodiProdImpianto(data, rigaIni, rigaFin);
//        periodiChiusura=xls.getListPeriodiChiusImpianto(data, rigaIni, rigaFin);
//
//        String oraini=DateUtils.dateToStr(oraInizio, "HHmmss");
//        String orafin=DateUtils.dateToStr(oraFine, "HHmmss");
//        loadDatiFromMvx(ioee, con, centrodiLavoro, data, oraini, orafin);
//      
//      } 
//
//      errorList=new ArrayList();
//      Map causali = (Map) parameter.get(ICalcIndicatoriOeeLinea.CAUSALILINEA);
//      fermiGg=new ArrayList();
//      IProdSupervisoreForBiesseBean infoS=elabDatiMacchina(con,conSQL,periodiProd,periodiChiusura, centrodiLavoro,oraInizio,oraFine,calcOnlySuper);
//
//      ioee.setTDispImp(DateUtils.getDurataPeriodiSs(periodiProd));
//      ioee.setTImprodImp(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_ORENONPROD));
//
//      ioee.setTRun(infoS.gettRuntimeEff().longValue());
//      ioee.setTGuasti(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_GUASTO));
//      ioee.setNGuasti(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_NGUASTI));
//      ioee.setNPzTot(infoS.getQtaTot());
//      ioee.setTMicrofermi(infoS.gettMicrofEventi()+infoS.gettMicrof().longValue());
//      ioee.setTPerditeGest(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_PERDGEST));
//      ioee.setTSetup(getDurataFermiTipo(infoS.getFermiGiornata(), causali, CausaliLineeBean.TIPO_CAUS_SETUP));    
//
//
//      _logger.info("Tempo produzione lotti "+infoS.getTempoProdSupervisore()+"  -- tempo Impianto calcolato "+ioee.getTDispImpianto());
//      //nei tempi esterni salbo il tempo produzione da log (t1) e buchidiProd (t2)  ;
//      //t3 serve per annullare t1 e t2 nel calcolo delle perdite non rilevate
//      //ioee.setTExt1(infoS.getTempoProdSupervisore());
//      ioee.setTExt1(infoS.getTempoFermiNnCausalizzati());
//
//      if(calcOnlySuper){
//        ioee.setTExt2(infoS.getTempoNnProdSupervisore());
//        ioee.setTExt3(-ioee.getTExt2());
//      }  
//  //      Long te3=ioee.getTExt1()+ioee.getTExt2();
//  //      ioee.setTExt3(-te3);
//
//      if(!errorList.isEmpty()){
//        ioee.addErrors(errorList);
//      }
//    } catch (SQLException ex) {
//      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
//      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
//   } finally {
//     if(conSQL!=null)
//       try {
//        conSQL.close();
//      } catch (SQLException ex) {
//        _logger.error(" Problemi nella chiusura della connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO + " --> "+ex.getMessage());
//      }
//   }  
//    return ioee; 
//  }

  public List getFermiGg() {
    return fermiGg;
  }
  
  
  private void loadDatiFromMvx(Connection con,String cdl,Date data,String oraini,String orafin){
    Map fasiMvx=new HashMap();
      try{
        fasiMvx=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, cdl, null, data, oraini, orafin,null);
        //salvo le informazioni dei pz prodotti prendendoli da avanzamento fase di Mvx
        getIoeeBean().setNScarti(MapUtils.getNumberFromMap(fasiMvx,CostantsColomb.NSCARTI,Long.class));
        getIoeeBean().setNPzTurni(MapUtils.getNumberFromMap(fasiMvx,CostantsColomb.NPZTOT,Long.class));
    
      }catch(SQLException s){
        _logger.error("Impossibile caricare le informazioni da Movex.-->"+s.getMessage());
        getIoeeBean().addWarning("Impossibile caricare info avanzamento fase da MOvex");
      }catch(QueryException q){
        _logger.error("Impossibile caricare le informazioni da Movex.-->"+q.getMessage());
        getIoeeBean().addWarning("Impossibile caricare info avanzamento fase da Movex");
      }
  }
  
  
  
//  private void loadDatiFromMvx(IndicatoriOeeGgBean ioee,Connection con,String cdl,Date data,String oraini,String orafin){
//    Map fasiMvx=new HashMap();
//      try{
//        fasiMvx=ElabDatiOrdiniProdMovex.getInstance().loadDatiProdFromMovex(con, cdl, null, data, oraini, orafin,null);
//        //salvo le informazioni dei pz prodotti prendendoli da avanzamento fase di Mvx
//        ioee.setNScarti(MapUtils.getNumberFromMap(fasiMvx,CostantsColomb.NSCARTI,Long.class));
//        ioee.setNPzTurni(MapUtils.getNumberFromMap(fasiMvx,CostantsColomb.NPZTOT,Long.class));
//    
//      }catch(SQLException s){
//        _logger.error("Impossibile caricare le informazioni da Movex.-->"+s.getMessage());
//        ioee.addWarning("Impossibile caricare info avanzamento fase da MOvex");
//      }catch(QueryException q){
//        _logger.error("Impossibile caricare le informazioni da Movex.-->"+q.getMessage());
//        ioee.addWarning("Impossibile caricare info avanzamento fase da Movex");
//      }
//  }
  
  private IProdSupervisoreForBiesseBean elabDatiMacchina(Connection conAs400,Connection conSQL,List<List> periodiProd,List<List> periodiChiusura,String cdL,Date dataInizioT,Date dataFineT,Boolean calcOnlySuper) {
    List fermiTot=new ArrayList();
    IProdSupervisoreForBiesseBean infoSupV=new IProdSupervisoreForBiesseBean(cdL);
    
    
      
    for(List periodo:periodiProd){
      Date dtInizioP=ClassMapper.classToClass(periodo.get(0),Date.class);
      Date dtFineP=ClassMapper.classToClass(periodo.get(1),Date.class);
      //carichiamo i dati di produzione da database
      List<List> lottiProdPer=getListFasiProd(conSQL,dtInizioP,dtFineP);
      if(lottiProdPer==null || lottiProdPer.isEmpty()){
       _logger.warn("Attenzione dati di produzione non presenti nel periodo "+dtInizioP+" - "+dtFineP);
      }

      List<List> eventiPer=getListFermi(conSQL,dtInizioP,dtFineP);
      if(eventiPer==null || eventiPer.isEmpty()){
        _logger.info("Lista fermi vuota");
        eventiPer=new ArrayList();
      }else{
        addFermiToListTot(fermiTot,eventiPer,dataInizioT,dataFineT);
      }

      elabArticoliProdotti(infoSupV, dtInizioP, dtFineP, lottiProdPer, eventiPer,calcOnlySuper);
    }

    //manipolazione della lista dei fermi della giornata
    List fermiNew=checkListFermi(fermiTot,periodiChiusura);

    infoSupV.settMicrofEventi(getDurataMicrofermi(fermiNew));
    fermiGg=getListaFermiG(fermiNew);

    infoSupV.setFermiGiornata(fermiGg);


    saveDataTCiclo(conAs400, infoSupV);
//      elabMsgForDatiProd(conAs400,infoSupV,dataInizioT);
    
    return infoSupV;  
  }
  
  private void elabArticoliProdotti(IProdSupervisoreForBiesseBean infoSupV,Date dtInizioP,Date dtFineP, List<List> fasiProd,List fermiProd,Boolean calcOnlySuper){
    for(List infoProdArt:fasiProd){
       String articoloTmp=ClassMapper.classToString(infoProdArt.get(0));
       Integer qtaTmp=ClassMapper.classToClass(infoProdArt.get(1),Integer.class);
       Date dtInizioL=ClassMapper.classToClass(infoProdArt.get(2),Date.class);
       Date dtFineL=ClassMapper.classToClass(infoProdArt.get(3),Date.class);
       Long tempoL=ClassMapper.classToClass(infoProdArt.get(4),Long.class); 
       Long idProd=ClassMapper.classToClass(infoProdArt.get(5),Long.class); 
       
       //se l'id processo è stato già elaborato passiamo al successivo
       if(infoSupV.isPresentIdProdArt(idProd)){
         continue;
       }  
       
       infoSupV.addIdProdArticolo(idProd);
       
       if(DateUtils.daysBetween(dtInizioL, dtInizioP)>0)
         continue;
       
       if(dtInizioL.before(dtInizioP))
         dtInizioL=new Timestamp(dtInizioP.getTime());
      
       if(dtFineL.after(dtFineP))
         dtFineL=new Timestamp(dtFineP.getTime());
      
        Long secDiff=DateUtils.numSecondiDiff(dtInizioL, dtFineL);
        infoSupV.addTempoProdSupervisore(secDiff);
        
       
       if(infoSupV.getDtFineUltLotto()!=null)
         infoSupV.addTempoNnProdSupervisore(DateUtils.numSecondiDiff(infoSupV.getDtFineUltLotto(), dtInizioL));
         
       infoSupV.setDtFineUltLotto(dtFineL);
       infoSupV.addQta(qtaTmp);
       if(!secDiff.equals(tempoL)){
          _logger.info("Attenzione tempo produzione lotto per articolo "+articoloTmp+" variato da "+tempoL +" a "+secDiff );
          tempoL=secDiff;
       }
       
       
       TempiCicloForBiesseG1 tcb=new TempiCicloForBiesseG1(CostantsColomb.AZCOLOM, infoSupV.getCdl(), articoloTmp);
       tcb.setTempoProdArt(tempoL);
       tcb.setQta(qtaTmp);
       
       elabFermiProdArticolo(tcb,fermiProd, dtInizioL, dtFineL);
       
       Long tempoNettoArt=tempoL-tcb.getTempoFermiTot()-tcb.getTempoMicrofermi();
       Double tCicloFromLog=OeeUtils.getInstance().arrotonda(tempoNettoArt/new Double(qtaTmp),3);
       
       tcb.setDataRif(dtInizioL);
       tcb.setDataMod(new Date());
       tcb.setTempoCiclo(tCicloFromLog);
       
       if(infoSupV.isPresentTCicloArt(articoloTmp)  && !calcOnlySuper)  {
          Double tCicloArt=(Double) infoSupV.getMapTCicloArt().get(articoloTmp);
          if(tCicloArt<=tCicloFromLog){
             infoSupV.addTMicroF((tCicloFromLog-tCicloArt)*qtaTmp);
             infoSupV.addTRuntimeEff(tCicloArt*qtaTmp);
             
          }else if(tCicloArt>tCicloFromLog){
            //update o insert e aggiornamento mappa
            infoSupV.addTRuntimeEff(tCicloFromLog*qtaTmp);
              
          }
        }else{
          infoSupV.getMapTCicloArt().put(articoloTmp,tCicloFromLog);
          infoSupV.addTRuntimeEff(tCicloFromLog*qtaTmp);
        }
        if(tcb.getTempoCiclo()<0){
          errorList.add("Attenzione tempo ciclo negativo per articolo :"+tcb.getCodArt()+" ora: "+tcb.getDataRif()+" tempo: "+tcb.getTempoCiclo());
        }else{
          infoSupV.addArticolo(tcb);
        }
    }

  }
  
  private void saveDataTCiclo(Connection con, IProdSupervisoreForBiesseBean info){
    PersistenceManager man=new PersistenceManager(con);
    for(TempiCicloForBiesseG1 b:info.getArticoliBeans()){
      try{
        if(man.checkExist(b)){
          man.updateDt(b);
        }else{
          
          man.storeDtFromBean(b);
        }
      }catch(SQLException s){
        _logger.error("Errore in fase di salvataggio del tempo ciclo per articolo: "+b.getCodArt()+" -"+b.getTempoCiclo() +" --> "+s.getMessage());
        errorList.add("Errore in fase di salvataggio del tempo ciclo per articolo: "+b.getCodArt()+" -"+b.getTempoCiclo() + " --> "+s.toString());
      }
    }
    
    
  }
  
//  private void elabMsgForDatiProd(Connection con, IProdSupervisoreForBiesseBean infoSupV,Date data) {
//    
//    if(infoSupV.getListInfoArtProd()==null )
//      return;
//    
//    MailSender ms=new MailSender();
//    MailMessageInfoBean bean=ms.getInfoBaseMailMessage(con,NameElabs.MESSAGE_DTPRODFBIESSEG1);
//    
//    String nomeFile=InfoMapLineeUtil.getLogFileName(bean.getFileAttachName(), data);
//    nomeFile=nomeFile.replace("XXXXX", infoSupV.getCdl());
//    XlsXCsvFileGenerator gf=new XlsXCsvFileGenerator(nomeFile, XlsXCsvFileGenerator.FILE_XLSX);
//    
//    File attach=gf.generateFile(infoSupV.getListInfoArtProd());
//    bean.addFileAttach(attach);
//      
//    
//    ms.send(bean);
//  }
  
  
  
  private void elabFermiProdArticolo(TempiCicloForBiesseG1 tcb,List<List> fermi,Date dataInizioP, Date dataFineP) {
    for(List fermoTmp : fermi ){
      String articoloTmp=ClassMapper.classToString(fermoTmp.get(0));
      Date dataInizioFermo=ClassMapper.classToClass(fermoTmp.get(2),Date.class);
      Date dataFineFermo=ClassMapper.classToClass(fermoTmp.get(3),Date.class);
      String descCausale=ClassMapper.classToString(fermoTmp.get(6));
      Boolean isFermo=ClassMapper.classToClass(fermoTmp.get(7),Boolean.class);
      
      //non bello ma serve per non ciclare su fermi al di fuori del periodo cercato
      if(DateUtils.afterEquals(dataInizioFermo, dataFineP))
        break;
      
      if(DateUtils.afterEquals(dataFineP, dataInizioFermo) && DateUtils.beforeEquals(dataInizioP, dataFineFermo)  
              //&& tcb.getCodArt().equals(articoloTmp)
        ) {
        if( DateUtils.beforeEquals(dataInizioP, dataInizioFermo) && DateUtils.afterEquals(dataFineP, dataFineFermo)  ){ 
          // il fermo è all'interno del periodo di produzione
          gestFermoForTcBean(tcb, isFermo, descCausale, dataInizioFermo, dataFineFermo);  
        }else if((dataInizioFermo.before(dataInizioP) && dataFineFermo.after(dataFineP))){
          // il fermo è più grande del periodo di produzione
          gestFermoForTcBean(tcb, isFermo, descCausale, dataInizioP, dataFineP);
        }else if(dataFineFermo.after(dataInizioP) && DateUtils.beforeEquals(dataFineFermo, dataFineP)){
          gestFermoForTcBean(tcb, isFermo, descCausale, dataInizioP, dataFineFermo);
        }else if(DateUtils.afterEquals(dataInizioFermo, dataInizioP) && dataInizioFermo.before(dataFineP)){
          gestFermoForTcBean(tcb, isFermo, descCausale, dataInizioFermo, dataFineP);
        }
   
      }
    
    }
  }
  
  private void gestFermoForTcBean(TempiCicloForBiesseG1 tcb,Boolean isFermo,String causale,Date dataIni,Date dataFin){
    
    Long durata=DateUtils.numSecondiDiff(dataIni, dataFin);
    if(isFermo){
      List f=new ArrayList();
      f.add(causale);
      f.add(dataIni);
      f.add(durata);
      tcb.addFermo(f);
    }else{
      tcb.addTempoMicroFermi(durata);
    }
  }
  
  
  private List getListFasiProd(Connection conSQL,Date dtInizio, Date dtFine) {
   List list=new ArrayList(); 
   
   try {
      QueryForBiesseG1P2DatiProd qry=new QueryForBiesseG1P2DatiProd();
      qry.setFilter(QueryForBiesseG1P2DatiProd.DATAINIZIO, DateUtils.DateToStr(dtInizio, "yyyy-MM-dd  HH:mm:ss"));
      qry.setFilter(QueryForBiesseG1P2DatiProd.DATAFINE, DateUtils.DateToStr(dtFine, "yyyy-MM-dd  HH:mm:ss"));
      
      
      String query=qry.toSQLString();
//      _logger.info(query);
      ResultSetHelper.fillListList(conSQL, query, list);
   
   } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (ParseException ex) {
      _logger.error(" Problemi di conversione delle date fornite --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } 
   
   return list;
  }
  
 
 private List getListFermi(Connection conSQL,Date dtInizio, Date dtFine) {
   List list=new ArrayList(); 
   
   try {
     
     QueryForBiesseG1P2DatiFermi qry=new QueryForBiesseG1P2DatiFermi();
     qry.setFilter(QueryForBiesseG1P2DatiFermi.DATAINIZIO, DateUtils.DateToStr(dtInizio, "yyyy-MM-dd  HH:mm:ss"));
     qry.setFilter(QueryForBiesseG1P2DatiFermi.DATAFINE, DateUtils.DateToStr(dtFine, "yyyy-MM-dd  HH:mm:ss"));
      
     String query=qry.toSQLString();
//     _logger.info(query);
     ResultSetHelper.fillListList(conSQL, query, list);
     
   } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      errorList.add("Impossibile individuare i fermi della linea.Problemi nell'interrogazione del database");
   } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
      errorList.add("Impossibile individuare i fermi della linea. Problemi nell'interrogazione del database");
   } catch (ParseException ex) {
     _logger.error(" Problemi di conversione delle date fornite --> "+ex.getMessage());
     errorList.add("Impossibile individuare i fermi della linea.Problemi nell'interrogazione del database");
   }
   
   return list;
  }
  
 
 private List getListPeriodiProd(Connection conSQL,Date dtInizio,Date dtFine) {
   List list=new ArrayList(); 
   
   try {
      
      QueryForBiesseG1P2PeriodiProd qry=new QueryForBiesseG1P2PeriodiProd();
      qry.setFilter(QueryForBiesseG1P2PeriodiProd.DATAINIZIO, DateUtils.DateToStr(dtInizio, "yyyy-MM-dd  HH:mm:ss"));
      qry.setFilter(QueryForBiesseG1P2PeriodiProd.DATAFINE, DateUtils.DateToStr(dtFine, "yyyy-MM-dd  HH:mm:ss"));
      
      
      String query=qry.toSQLString();
      ResultSetHelper.fillListList(conSQL, query, list);
   
   } catch (QueryException ex) {
      _logger.error(" Problemi nell'esecuzione della query  --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (SQLException ex) {
      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } catch (ParseException ex) {
      _logger.error(" Problemi di conversione delle date fornite --> "+ex.getMessage());
      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
   } 
   
   return list;
  }
 
 
  private Long getDurataMicrofermi(List<List> fermi){
    Long durata=Long.valueOf(0);
    for(List fermo:fermi){
//       String caus=(String) fermo.get(1);
       Boolean isFermo= ClassMapper.classToClass(fermo.get(7),Boolean.class);
       Long secFermo=ClassMapper.classToClass(fermo.get(4),Long.class);
       
       if(!isFermo){
         durata+=secFermo;
       }
//         else if(caus.equals("0") && secFermo>1000){
//         Long id=ClassMapper.classToClass(fermo.get(5),Long.class);
//         _logger.error("Attenzione microfermo con durata maggiore di 1000 sec --> ID:"+id+" durata :"+secFermo);
//         durata+=secFermo;
//       }
    }
    
    return durata;
  }
  
  /**
   * Modifica la lista dei fermi presenti sul supervisore in base ai periodi di non produzione presenti sul file xls
   * @param fermiTot
   * @param periodiChiusura
   * @return 
   */
  private List checkListFermi(List<List> fermiTot, List<List> periodiChiusura) {
    
    if(periodiChiusura==null || periodiChiusura.isEmpty())
      return fermiTot;
    
   
    List fermiNew=new ArrayList();
    
    for(List periodoC:periodiChiusura){
      Date dataInizioC=(Date) periodoC.get(0);
      Date dataFineC=(Date) periodoC.get(1);
      
      for(List fermo:fermiTot){
        
        Date dataInizioF=ClassMapper.classToClass(fermo.get(2),Date.class);
        Date dataFineF=ClassMapper.classToClass(fermo.get(3),Date.class);
        
        
        if( DateUtils.beforeEquals(dataInizioC, dataInizioF) && DateUtils.afterEquals(dataFineC, dataFineF)  ){ 
          // il fermo è all'interno del periodo di produzione
          if(!fermiNew.contains(fermo))
            fermiNew.add(fermo);
        }else if((dataInizioF.before(dataInizioC) && dataFineF.after(dataFineC))){
          // il fermo è più grande del periodo di produzione
          //bisogna spezzare il fermo in due
          Long t1=DateUtils.numSecondiDiff(dataInizioF, dataInizioC);
          if(t1>0){
            List fermo1=new ArrayList(fermo);
            fermo1.set(3,dataInizioC);
            fermo1.set(4,t1);
            fermiNew.add(fermo1);
          }
          Long t2=DateUtils.numSecondiDiff(dataFineC, dataFineF);
          if(t2>0){
            List fermo2=new ArrayList(fermo);
            fermo2.set(3,dataFineC);
            fermo2.set(4,t2);
            fermiNew.add(fermo2);
          }
 
        }else if(dataFineF.after(dataInizioC) && DateUtils.beforeEquals(dataFineF, dataFineC)){
          List fermo1=new ArrayList(fermo);
          fermo1.set(3,dataInizioC);
          fermo1.set(4,DateUtils.numSecondiDiff(dataInizioF, dataInizioC));
          fermiNew.add(fermo1);
        }else if(DateUtils.afterEquals(dataInizioF, dataInizioC) && dataInizioF.before(dataFineC)){
          List fermo2=new ArrayList(fermo);
          fermo2.set(3,dataFineC);
          fermo2.set(4,DateUtils.numSecondiDiff(dataInizioF, dataFineC));
          fermiNew.add(fermo2);
        }else{
         if(!fermiNew.contains(fermo)) 
           fermiNew.add(fermo);
        }
      }
      
    }
    return fermiNew;
  }
  /**
   * Aggiunge la lista listaFermi alla listaTotale verificando se i periodi dei singoli fermi sforano il periodo
   * di produzione 
   * @param fermiTot
   * @param listaFermi
   * @param dataIniTurni
   * @param dataFinTurni 
   */
  private void addFermiToListTot(List fermiTot, List<List> listaFermi,Date dataIniTurni,Date dataFinTurni) {
    for(List fermoTmp:listaFermi){
     if(!fermiTot.contains(fermoTmp)){
       Date dataInizioFermo=ClassMapper.classToClass(fermoTmp.get(2),Date.class);
       Date dataFineFermo=ClassMapper.classToClass(fermoTmp.get(3),Date.class);
       
       if(DateUtils.beforeEquals(dataInizioFermo, dataIniTurni)){
         fermoTmp.set(2,dataIniTurni);
         fermoTmp.set(4,DateUtils.numSecondiDiff(dataIniTurni, dataFineFermo));
       }    
       if(DateUtils.afterEquals(dataFineFermo, dataFinTurni)){
         fermoTmp.set(3,dataFinTurni);  
         fermoTmp.set(4,DateUtils.numSecondiDiff(dataInizioFermo, dataFinTurni));
       }
           
       fermiTot.add(fermoTmp);
     }
       
    }
    
  }
  
  /**
   * Torna la lista degli eventi di fermi proposti dal supervisore contenente solo le informazioni relative al codice della causale e alla
   * durata del fermo
   * @param fermi
   * @return 
   */
  private List getListaFermiG(List<List> fermi){
    List fermiNew=new ArrayList();
    
    for(List fermo:fermi){
       String caus=(String) fermo.get(1);
       Boolean tipoEvFermo=ClassMapper.classToClass(fermo.get(7),Boolean.class);
       //causali con 
       if(tipoEvFermo){
         List newF=new ArrayList();
         newF.add(caus);
         newF.add(fermo.get(4));
         
         fermiNew.add(newF);
       }
       
    }
    return fermiNew;
  }
  
  
  
  private Long getDurataFermiTipo(List<List> fermi,Map causali,String tipo){
    Long durata=Long.valueOf(0);
    for(List fermo:fermi){
       String caus=(String) fermo.get(0);
       Long secFermo=ClassMapper.classToClass(fermo.get(1),Long.class);
       if(causali.containsKey(caus)){
         CausaliLineeBean cb=(CausaliLineeBean) causali.get(caus);
         if(tipo==null){
           durata+=secFermo;
         }else if(tipo!=null && tipo.equals(cb.getTipo()) ){
           durata+=secFermo;
         }
       }
    }
    return durata;
  }
  
  

  
  
  private Connection getDbForBiesseGalConnection(String cdL) throws SQLException {
    Connection con=null;
    if(cdL.contains(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSKERNEL))){
      con=ColombiniConnections.getDbForBiesseKernelGalConnection();
    }else if (cdL.contains(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORBSFTT))){
      con=ColombiniConnections.getDbForBiesseFTTGalConnection();
    }
    
    return con;
  }

  
  private static final Logger _logger = Logger.getLogger(IndicatoriForatriciBiesse.class);
  
  
//  private Long getNumeroFermiTipo(List<List> fermi,Map causali,String tipo){
//    Long num=Long.valueOf(0);
//    for(List fermo:fermi){
//       String caus=(String) fermo.get(0);
//       if(causali.containsKey(caus)){
//         CausaliLineeBean cb=(CausaliLineeBean) causali.get(caus);
//         if(tipo.equals(cb.getTipo()) ){
//           num+=1;
//         }
//       }
//    }
//    return num;
//  }  
  
  
//  private Map loadMapTempoCicloMvx(Connection con, String cdL ) throws SQLException {
//    List<List> listTc=new ArrayList();
//    Map mapTc=new HashMap() ;
//    //MIN(ZTITTC)
//    String select ="SELECT TRIM(ZTITNO), SUM(ZTITTC)/COUNT(*) from MCOBMODDTA.ZTCFBG where 1=1 "
//            + " and ZTCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)
//            + " and ZTPLGR="+JDBCDataMapper.objectToSQL(cdL)
//            + " group by ZTITNO";
//    
//    ResultSetHelper.fillListList(con, select, listTc);
//    for(List rec:listTc){
//      mapTc.put(ClassMapper.classToString(rec.get(0)),ClassMapper.classToClass(rec.get(1),Double.class ));
//    } 
//    
//    return mapTc;
//  }
  
  // classe di appoggio per gestire in maniera più pulita i dati letti da supervisore e riprocessati
  // per il calcolo dell'Oee.
  
  
//   private Long getDurataMicrofermi2(List<List> fermi){
//    Long durata=Long.valueOf(0);
//    for(List fermo:fermi){
//       String caus=(String) fermo.get(1);
//       Long secFermo=ClassMapper.classToClass(fermo.get(4),Long.class);
//       
//       if(caus.equals("0") && secFermo>1000){
//         Long id=ClassMapper.classToClass(fermo.get(5),Long.class);
//         _logger.error("Attenzione microfermo con durata maggiore di 1000 sec --> ID:"+id+" durata :"+secFermo);
//         durata+=secFermo;
//       }
//    }
//    
//    return durata;
//  }
//  
//  
//  
//  private Map elabDatiMacchinaOld(Connection con,List<List> periodiProd,List<List> periodiChiusura,Map causali,String cdL,Date dataInizioT,Date dataFineT) {
//    Map info=new HashMap();
//    
//    Double tRuntime=Double.valueOf(0);
//    Double tMicroTc=Double.valueOf(0); //microfermi dovuti alle differenze di tempo ciclo
//    List fermiTot=new ArrayList();
//    Long qtaTot=Long.valueOf(0);
//    Long tProdLog=Long.valueOf(0);
//    Long tOreNnProdLog=Long.valueOf(0);
//    Map idLottiProd =new HashMap();
//    Connection conSQL=null;
//    try {
////      Map mapTCicloCdl=loadMapTempoCicloMvx(con, cdL);
//      Map mapTCicloCdl=new HashMap();
//      
//      conSQL=getDbForBiesseGalConnection(cdL);
//      
//    for(List periodo:periodiProd){
//      Date dtInizioP=ClassMapper.classToClass(periodo.get(0),Date.class);
//      Date dtFineP=ClassMapper.classToClass(periodo.get(1),Date.class);
//      //carichiamo i dati di produzione da database
//      List<List> lottiProd=getListFasiProd(conSQL,dtInizioP,dtFineP,cdL);
//      if(lottiProd==null || lottiProd.isEmpty()){
//        _logger.warn("Attenzione dati di produzione non presenti nel periodo "+dtInizioP+" - "+dtFineP);
//      }
//      
//      List<List> listaFermi=getListFermi(conSQL,dtInizioP,dtFineP,cdL);
//      if(listaFermi==null || listaFermi.isEmpty()){
//        _logger.info("Lista fermi vuota");
//        listaFermi=new ArrayList();
//      }else{
//        addFermiToListTot(fermiTot,listaFermi,dataInizioT,dataFineT);
//      }
//      Date dataOld=null;
//      Long secNnProd=Long.valueOf(0);
//      for(List periodoProd:lottiProd){
//        
//        String articoloTmp=ClassMapper.classToString(periodoProd.get(0));
//        Integer qtaTmp=ClassMapper.classToClass(periodoProd.get(1),Integer.class);
//        Date dtInizioL=ClassMapper.classToClass(periodoProd.get(2),Date.class);
//        Date dtFineL=ClassMapper.classToClass(periodoProd.get(3),Date.class);
//        Long tempoL=ClassMapper.classToClass(periodoProd.get(4),Long.class); 
//        Long idProd=ClassMapper.classToClass(periodoProd.get(5),Long.class); 
//        
//        if(idLottiProd.containsKey(idProd)){
//          continue;
//        }  
//           
//        idLottiProd.put(idProd,"S");
//        //calcolo il tempo che intercorre tra la produzione di un lotto e il successivo
//        if(dataOld!=null)
//          secNnProd=DateUtils.numSecondiDiff(dataOld, dtInizioL);
//        
//        tOreNnProdLog+=secNnProd;
//        dataOld=dtFineL;
//        if(secNnProd>900){
//          _logger.warn("Attenzione tempo di produzione tra un lotto e il successivo > 15 minuti "+secNnProd);
//        }
//
//        
//        qtaTot+=qtaTmp;
//        if(dtInizioL.before(dtInizioP))
//           dtInizioL=dtInizioP;
//      
//        if(dtFineL.after(dtFineP))
//           dtFineL=dtFineP;
//      
//        Long secDiff=DateUtils.numSecondiDiff(dtInizioL, dtFineL);
//        tProdLog+=secDiff;
//        
//        if(!secDiff.equals(tempoL)){
//          _logger.info("Attenzione tempo produzione lotto per articolo "+articoloTmp+" variato da "+tempoL +" a "+secDiff );
//          tempoL=secDiff;
//        }
//        
//        Double tRunLoc=Double.valueOf(0);
//        Long secFermo=getDurataFermo(listaFermi,articoloTmp, dtInizioL, dtFineL);
//        Long tempoTotArt=tempoL-secFermo;
//        if(tempoTotArt<=0){
//          _logger.error("Attenzioni tempi fermi superiori al tempo di produzione per articolo "+articoloTmp+
//                  " nel periodo :"+dtInizioL.toString()+" - "+dtFineL.toString());
//          
//          continue;
//        }
//        
//        
//        
//        Double tCicloFromLog=OeeUtils.getInstance().arrotonda(tempoTotArt/new Double(qtaTmp),3);
//        
//        TempiCicloForBiesseG1 tcb=new TempiCicloForBiesseG1(CostantsColomb.AZCOLOM, cdL, articoloTmp);
//        PersistenceManager ph=new PersistenceManager(con);
//        tcb.setTempoCiclo(tCicloFromLog);
//        tcb.setDataRif(dtInizioL);
//        tcb.setDataMod(new Date());
//        
//        if(mapTCicloCdl.containsKey(articoloTmp)){
//          Double tCicloArt=(Double) mapTCicloCdl.get(articoloTmp);
//          if(tCicloArt<=tCicloFromLog){
//             tMicroTc+=((tCicloFromLog-tCicloArt)*qtaTmp);
//             tRunLoc=tCicloArt*qtaTmp;
//             
//          }else if(tCicloArt>tCicloFromLog){
//            //update o insert e aggiornamento mappa
//            tRunLoc=tCicloFromLog*qtaTmp;
//            //mapTCicloCdl.put(articoloTmp,tCicloFromLog);
//            if(ph.checkExist(tcb)){
////              ph.updateDt(tcb);
//            }else{
//              ph.storeDtFromBean(tcb);
//            }  
//          }
//        }else{
//          mapTCicloCdl.put(articoloTmp,tCicloFromLog);
//          tRunLoc=tCicloFromLog*qtaTmp;
//          ph.storeDtFromBean(tcb);
//        }
//        tRuntime+=(tRunLoc);
////        tRuntime+=tempoL-secFermo;
//        //dobbiamo gestire il tempo ciclo legato all'articolo.
//        
//      }
//    }
//    List fermiNew=checkListFermi(fermiTot,periodiChiusura);
//    Long tMicroEventi=getDurataMicrofermi(fermiNew);
//    fermiGg=getListaFermiG(fermiNew);
//    
//    _logger.info("Microfermi da produzione: "+tMicroTc+" ; microfermi da eventi :"+tMicroEventi);
//    
////    tImprodImp+=getDurataFermiTipo(listFermi, causali, CausaliLineeBean.TIPO_CAUS_ORENONPROD);
////    
//      info.put(CostantsColomb.TDISPON, DateUtils.getDurataPeriodiSs(periodiProd));
//      info.put(CostantsColomb.TIMPROD, getDurataFermiTipo(fermiGg, causali, CausaliLineeBean.TIPO_CAUS_ORENONPROD));
//      
//      info.put(CostantsColomb.NPZTOT, qtaTot);
//      info.put(CostantsColomb.TRUNTIME,tRuntime.longValue());
//      
//      info.put(CostantsColomb.TMICROFERMI,tMicroTc.longValue()+tMicroEventi);
//      info.put(CostantsColomb.TGUASTI, getDurataFermiTipo(fermiGg, causali, CausaliLineeBean.TIPO_CAUS_GUASTO));
//      info.put(CostantsColomb.NGUASTI, getNumeroFermiTipo(fermiGg, causali, CausaliLineeBean.TIPO_NGUASTI));
//      info.put(CostantsColomb.TPERDGEST, getDurataFermiTipo(fermiGg, causali, CausaliLineeBean.TIPO_CAUS_PERDGEST));
//      info.put(CostantsColomb.TSETUP, getDurataFermiTipo(fermiGg, causali, CausaliLineeBean.TIPO_CAUS_SETUP));    
//      
//      //da eliminare dopo i test
//      info.put(CostantsColomb.TEXT1,tProdLog );    
//      info.put(CostantsColomb.TEXT2,tOreNnProdLog);    
////    info.put(CostantsColomb.TIMPROD, tImprodImp);
////    
////    info.put(ICalcIndicatoriOeeLinea.LISTFERMIOEE, listFermi);
//    } catch (SQLException ex) {
//      _logger.error(" Impossibile stabilire una connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO +" --> "+ex.getMessage());
//      errorList.add("Impossibile reperire le informazioni relative ai dati di produzione della linea .Problemi nell'interrogazione del database");
//   } finally {
//     if(conSQL!=null)
//       try {
//        conSQL.close();
//      } catch (SQLException ex) {
//        _logger.error(" Problemi nella chiusura della connessione con server "+ColomConnectionsCostant.SRVDBGALAZZANO + " --> "+ex.getMessage());
//      }
//   }
//    
//    return info;
//  }
//  
//   private Long getDurataFermo(List<List> fermi,String codArt,Date dataInizioP, Date dataFineP) {
//    Long durataF=Long.valueOf(0);
//    int count=0;
//    for(List fermoTmp : fermi ){
//      String articoloTmp=ClassMapper.classToString(fermoTmp.get(0));
//      Date dataInizioFermo=ClassMapper.classToClass(fermoTmp.get(2),Date.class);
//      Date dataFineFermo=ClassMapper.classToClass(fermoTmp.get(3),Date.class);
//      
//      
//      //non bello ma serve per non ciclare su fermi al di fuori del periodo cercato
//      if(DateUtils.afterEquals(dataInizioFermo, dataFineP))
//        break;
//      
//      
//      if(DateUtils.afterEquals(dataFineP, dataInizioFermo) && DateUtils.beforeEquals(dataInizioP, dataFineFermo)  && codArt.equals(articoloTmp)
//              
//        ) {
//        count++;
//        if( DateUtils.beforeEquals(dataInizioP, dataInizioFermo) && DateUtils.afterEquals(dataFineP, dataFineFermo)  ){ 
//          // il fermo è all'interno del periodo di produzione
//          durataF+=DateUtils.numSecondiDiff(dataInizioFermo, dataFineFermo);
//         }else if((dataInizioFermo.before(dataInizioP) && dataFineFermo.after(dataFineP))){
//          // il fermo è più grande del periodo di produzione
//          durataF+=DateUtils.numSecondiDiff(dataInizioP, dataFineP);
//        
//        }else if(dataFineFermo.after(dataInizioP) && DateUtils.beforeEquals(dataFineFermo, dataFineP)){
//          durataF+=DateUtils.numSecondiDiff(dataInizioP, dataFineFermo);
//          
//        }else if(DateUtils.afterEquals(dataInizioFermo, dataInizioP) && dataInizioFermo.before(dataFineP)){
//          durataF+=DateUtils.numSecondiDiff(dataInizioFermo, dataFineP);
//        }
//   
//      }
//    
//    }
//    
//    _logger.debug(" Totale sec fermo nel periodo"+durataF+ " n fermi"+count);
//    return durataF;
//    
//  }
//  
//  
//  
//  private Long getTempoDisponibilita(Date inizioT, Date fineT, Date inizioF, Date fineF) {
//    Date inizio=null;
//    Date fine=null;
//    if(inizioT==null || fineT==null){
//      _logger.error("Attenzione Periodi di produzione non valorizzati.Impossibile calcolare le ore impianto!!");
//      return Long.valueOf(0);
//    }
//    
//    if(inizioF==null && fineF==null){
//     _logger.info("Turni di lavoro --> "+inizioT+" - "+fineT+"  -- durataTot:"+DateUtils.numSecondiDiff(inizioT, fineT));
//     return DateUtils.numSecondiDiff(inizioT, fineT);
//    }
//    
//    inizio=inizioT;
//    fine=fineT;
//    
//    _logger.info("Inizio produzione>> "+inizioT +" - fine produzione>> "+fineT);
//    
//    if(inizioF!=null && inizioF.before(inizioT)){
//      inizio=inizioF;
//      _logger.info(" Inizio fermi inferiore a inizio produzione.Apertura impianto ="+inizio);
//    }
//    
//    
//    if(fineF!=null && fineF.after(fineT)){
//      fine=fineF;
//      _logger.info(" Fine fermi superiore a fine produzione.Chiusura impianto ="+fine);
//    }
//      
//    return DateUtils.numSecondiDiff(inizio, fine);
//  }
//  

  
  
  
  
  
}

