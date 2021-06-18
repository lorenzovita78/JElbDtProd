/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.model.CausaliLineeBean;
import colombini.model.DatiConsuntivoMVX;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.persistence.DatiProdPWGeneral;
import colombini.model.persistence.ColliVolumiCommessaTbl;
import colombini.model.persistence.DatiProdConsuntivoMese;
import colombini.model.persistence.DatiProdConsuntivoSett;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCdL4DtProd;
import colombini.util.DatiProdUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;
import db.persistence.IBeanPersSIMPLE;

/**
 * Classe che storicizza i dati di Produzione settimanalmente o manualmente
 * @author lvita
 */
public class ElabDtProdPWConsolidamento extends ElabClass{

  
  public final static String WEEK="WEEK";
  public final static String MONTH="MONTH";
  
  public final static String CAPACITA_OREIMPIANTO ="1";
  public final static String CAPACITA_OREUOMO ="2";
  
  public final static String UNMISURA_PEZZI ="PZ";
  public final static String UNMISURA_COLLI ="CL";
  
  
  private Date dataInizio;
  private Date dataFine;
  private String tipoCalc;
  
  
  private List<String> lineeElab;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(NameElabs.ELBSTOREDTPRODWK.equals(this.getInfoElab().getCode()))
      this.tipoCalc=WEEK;
    else if(NameElabs.ELBSTOREDTPRODMT.equals(this.getInfoElab().getCode()))
      this.tipoCalc=MONTH;
    
    if(tipoCalc==null || tipoCalc.isEmpty()){
      _logger.error("Tipo Calcolo Consuntivo Dati Produzione non specificato  . Impossibile proseguire");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
     
     if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
     
    if(dataInizio==null)
      return Boolean.FALSE;
    
    
    if(dataFine==null)
      dataFine=dataInizio;
    
    //---parametri non obbligatori
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeElab=ArrayUtils.getListFromArray(linee.split(","));
    }
  
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    
   
    //caricamento linee da processare
    List<List> listLinee=getListCdl(con);
    //mappa che indica per ogni Bu le commesse elaborate nel Periodo
    
    //se il calcolo settimanale e il periodo è più di una settimana ciclo sulle week
    if(tipoCalc.equals(WEEK)){
      Date dataIniSett=dataInizio;
      Date dataFinSett=DateUtils.addDays(dataInizio, 6);
      while(DateUtils.beforeEquals(dataFinSett, dataFine)){
        _logger.info("Consuntivo settimanale dati di produzione per il periodo "+dataIniSett+ " - "+dataFinSett);
        elabDataPeriodo(con, dataIniSett, dataFinSett, listLinee);
        dataIniSett=DateUtils.addDays(dataIniSett, 7);
        dataFinSett=DateUtils.addDays(dataFinSett, 7);
      }
    }else{
      Date dataIniMese=dataInizio;
      Date dataFinMese=DateUtils.addDays(DateUtils.addMonths(dataInizio, 1),-1);
      while(DateUtils.beforeEquals(dataFinMese, dataFine)){
        _logger.info("Consuntivo mensile dati di produzione per il periodo "+dataIniMese+ " - "+dataFinMese);
        elabDataPeriodo(con, dataIniMese, dataFinMese, listLinee);
        dataIniMese=DateUtils.addMonths(dataIniMese, 1);
        dataFinMese=DateUtils.addDays(DateUtils.addMonths(dataIniMese, 1),-1);
      }
    }
    
  }
  
  private void elabDataPeriodo(Connection con,Date dataIni,Date dataFin,List listLinee){
    //caricamento giorni lavorativi
    try{
      List<Long> ggLavPeriodo=DatiProdUtils.getInstance().getListGg(con, dataIni, dataFin, Boolean.TRUE);
      Map buCommPeriodo=getBuCommPeriodo(con, dataIni, dataFin);
      
      if(ggLavPeriodo==null || buCommPeriodo==null)
        return;
      
      List infoToSave=null;
      
      if(tipoCalc.equals(MONTH)){
        infoToSave=getListToSaveMonth(con, dataIni,dataFin,listLinee, buCommPeriodo);
      }else{
        infoToSave=getListToSaveWeek(con, dataIni,dataFin,listLinee, buCommPeriodo);
      }
        
      gestData(con,dataIni, dataFin, infoToSave);
       
    }catch(SQLException s){
      _logger.error("Errore in fase di reperimento dei dati per l'elaborazione-->"+s.getMessage());
      addError("Errore in fase di reperimento dei dati per l'elaborazione -->"+s.toString());  
    } catch(QueryException q){
      _logger.error("Errore in fase di reperimento dei dati per l'elaborazione-->"+q.getMessage());
      addError("Errore in fase di reperimento dei dati per l'elaborazione -->"+q.toString()); 
    }
  }
  
  /**
   * Torna una mappa che per ogni Bu indica il numero di commesse prodotte nel periodo fornito come perimetro
   * @param con
   * @param dataInizio
   * @param dataFine
   * @return Map
   * @throws SQLException
   * @throws QueryException 
   */
  private Map getBuCommPeriodo(Connection con,Date dataInizio,Date dataFine ) throws SQLException, QueryException{
    Map buCommPeriodo=new HashMap();
    ColliVolumiCommessaTbl cvc=new ColliVolumiCommessaTbl();
    
    Long dtIniN=DateUtils.getDataForMovex(dataInizio);
    Long dtFinN=DateUtils.getDataForMovex(dataFine);
    List <ColliVolumiCommessaTbl>  listCommPeriodo=cvc.retrieveDatiComm(con, CostantsColomb.AZCOLOM, null, null, dtIniN, dtFinN, Boolean.TRUE);
    if(listCommPeriodo==null || listCommPeriodo.isEmpty())
      return null;
    
    for(ColliVolumiCommessaTbl cvct:listCommPeriodo){
        String bu=cvct.getDivisione();
        Integer occ=Integer.valueOf(1);
        if(buCommPeriodo.containsKey(bu)){
           occ+=(Integer) buCommPeriodo.get(bu);
        }
        buCommPeriodo.put(bu,occ);
    }
    
    return buCommPeriodo;
    
  }
  
  
//  private List getListDtMonth(Connection con,List<List> listLinee,Map buCommPeriodo){
//    List infoToSave=new ArrayList();
//    String cdlRifOld=null;
//    List lineeAppo=new ArrayList();
//    Integer azOld=null;
//    Integer nCommPeriodoOld=null;
//    String cdlRif= null;
//    int cont=0;
//    for(List recLinea : listLinee){
//      cont++;
//      cdlRif= ClassMapper.classToString(recLinea.get(7)).trim();
//      String cdlTmp=ClassMapper.classToString(recLinea.get(3));
//      String bucdL= ClassMapper.classToString(recLinea.get(5));
//      
//
//      if(cdlRifOld!=null &&!cdlRif.equals(cdlRifOld)){
//
//        InfoWrkC wrkC=getInfoAggCdl(con, azOld, cdlRifOld);
//        if(wrkC!=null){
//          _logger.info("Elaborazione CdL "+cdlRifOld+ " e linee associate "+lineeAppo.toString());
//          wrkC.setLineePW(lineeAppo);
//          DatiConsuntivoMVX dtM=getDtConsuntivoCdLPer(con,wrkC,dataInizio,dataFine,nCommPeriodoOld);
//          infoToSave.add(dtM);
//        }
//        lineeAppo=new ArrayList();
//      }
//      lineeAppo.add(cdlTmp);
//      cdlRifOld=cdlRif;
//      azOld=ClassMapper.classToClass(recLinea.get(0),Integer.class);
//      nCommPeriodoOld=getNumCommCdlBu(buCommPeriodo, bucdL);
//    }
//    _logger.info("Processate n "+cont+"linee ");
//    if(cdlRif!=null && cdlRif.equals(cdlRifOld)){
//      InfoWrkC wrkC=getInfoAggCdl(con, azOld, cdlRifOld);
//      if(wrkC!=null){
//        _logger.info("Elaborazione CdL "+cdlRifOld+ " e linee associate "+lineeAppo.toString());
//        wrkC.setLineePW(lineeAppo);
//        DatiConsuntivoMVX dtM1=getDtConsuntivoCdLPer(con,wrkC,dataInizio,dataFine,nCommPeriodoOld);
//        infoToSave.add(dtM1); 
//      }
//    }
//    
//    return infoToSave;
//  }
  
  
  private List getListToSaveWeek(Connection con,Date dataIni,Date dataFin,List<List> listLinee,Map buCommPeriodo){
    List infoToSave=new ArrayList();
    int cont=0;
    for(List recLinea : listLinee){
      cont++;
      Integer az=ClassMapper.classToClass(recLinea.get(0),Integer.class);
      String cdl=ClassMapper.classToString(recLinea.get(3));
      String cdlRif=ClassMapper.classToString(recLinea.get(7)).trim();
      //pezza per gestire quelle linee che non devono essere calcolate nel consuntivo mensile
      // ma dobbiamo calcolare settimanalmente e i parametri a livello di mPDWCT
      if(StringUtils.isEmpty(cdlRif)){
        cdlRif=cdl;
        if(cdlRif.length()>6)
          cdlRif=cdlRif.substring(0, 6);
      }
        
      String bucdL= ClassMapper.classToString(recLinea.get(5));
      Integer nCommPeriodo=getNumCommCdlBu(buCommPeriodo, bucdL);
      InfoWrkC wrkC=getInfoAggCdl(con, az, cdlRif);
      _logger.info("Elaborazione CdL "+cdl+ " -->linea Rif : "+cdlRif);
      wrkC.addLineaPW(cdl);
      wrkC.setCodCdl(cdl);
      DatiConsuntivoMVX dtM=getDtConsuntivoCdLPer(con,wrkC,dataIni,dataFin,nCommPeriodo);
      infoToSave.add(dtM);
       
    }
    
    return infoToSave;  
  }  
  
  
  private List getListToSaveMonth(Connection con,Date dataIni,Date dataFin,List<List> listLinee,Map buCommPeriodo){
    List infoToSave=new ArrayList();
    String cdlRifOld=null;
    List lineeAppo=new ArrayList();
    Integer azOld=null;
    Integer nCommPeriodoOld=null;
    String cdlRif= null;
    int cont=0;
    System.out.println("LINEA;PZ;COLLI;UNM");
    for(List recLinea : listLinee){
      cont++;
      cdlRif= ClassMapper.classToString(recLinea.get(7)).trim();
      String cdlTmp=ClassMapper.classToString(recLinea.get(3));
      String bucdL= ClassMapper.classToString(recLinea.get(5));
      
      
      if(!StringUtils.isEmpty(cdlRifOld)  && !cdlRif.equals(cdlRifOld)){
        InfoWrkC wrkC=getInfoAggCdl(con, azOld, cdlRifOld);
        cont++;
        _logger.info("Elaborazione CdL "+cdlRifOld+ " e linee associate "+lineeAppo.toString());
        wrkC.setLineePW(lineeAppo);
        DatiConsuntivoMVX dtM=getDtConsuntivoCdLPer(con,wrkC,dataIni,dataFin,nCommPeriodoOld);
        infoToSave.add(dtM);
        lineeAppo=new ArrayList();
      }
      lineeAppo.add(cdlTmp);
      cdlRifOld=cdlRif;
      azOld=ClassMapper.classToClass(recLinea.get(0),Integer.class);
      nCommPeriodoOld=getNumCommCdlBu(buCommPeriodo, bucdL);
    }
    
    if(!StringUtils.isEmpty(cdlRif) && cdlRif.equals(cdlRifOld)){
      cont++;
      InfoWrkC wrkC=getInfoAggCdl(con, azOld, cdlRifOld);
      _logger.info("Elaborazione CdL "+cdlRifOld+ " e linee associate "+lineeAppo.toString());
      wrkC.setLineePW(lineeAppo);
      DatiConsuntivoMVX dtM1=getDtConsuntivoCdLPer(con,wrkC,dataIni,dataFin,nCommPeriodoOld);
      infoToSave.add(dtM1); 
    }
    
    _logger.info("Processate n "+cont+"linee ");
    return infoToSave;
  }
  
  
  private void gestData(Connection con,Date dataIni,Date dataFin,List<DatiConsuntivoMVX> infoToSave){
    PersistenceManager man=null;
    if(infoToSave.size()>0){
      man=new PersistenceManager(con);
      try{
        deleteOldData(man,infoToSave);
        IBeanPersSIMPLE beanToSave=null;
        for(DatiConsuntivoMVX dt:infoToSave){
          try{
            if(tipoCalc.equals(WEEK)){
              beanToSave=new DatiProdConsuntivoSett(dt);
            }else{
              beanToSave=new DatiProdConsuntivoMese(dt);
            }
            if(!beanToSave.validate()){
              addError("\nAttenzione Ore Lavorate pari a ZERO nel periodo "+dataIni+" - "+dataFin+" per linea: "+beanToSave.toString() );
            }else{
              man.storeDtFromBean(beanToSave);
            }
          }catch(SQLException s){
           _logger.error("Errore in fase di salvataggio del dato "+beanToSave.toString()+" -->"+s.getMessage());
           addError("Errore in fase di salvataggio del dato "+beanToSave.toString()+" -->"+s.toString());
          }
       }
      }catch(SQLException s){
         _logger.error("Errore in fase di cancellazione dei dati per il periodo :"+dataIni+" - "+dataFin+" -->"+s.getMessage());
         addError("Errore in fase di cancellazione dei dati per il periodo :"+dataIni+" - "+dataFin+" -->"+s.toString());
      }finally{
        man=null;
      }
    }
  }  
  
   private void deleteOldData(PersistenceManager man, List<DatiConsuntivoMVX> infoToSave) throws SQLException {
     IBeanPersSIMPLE beanAppo=null;
     if(tipoCalc.equals(WEEK)){
       beanAppo=new DatiProdConsuntivoSett(infoToSave.get(0));
     }else{
       beanAppo=new DatiProdConsuntivoMese(infoToSave.get(0));
     } 
     
     man.deleteDtFromBean(beanAppo);
   }
  
  
  
  
  private DatiConsuntivoMVX  getDtConsuntivoCdLPer(Connection con,InfoWrkC wrkC,Date dataInizio,Date dataFine,Integer commElabPeriodo) {
    DatiConsuntivoMVX dtMvx=null;
    Double minProdImp=Double.valueOf(0);
    Double pzProd=Double.valueOf(0);
    Double clProd=Double.valueOf(0);
    Double minSetUp=Double.valueOf(0);
    Double minFermi=Double.valueOf(0);
    Double minMicrofermi=Double.valueOf(0);
    Double minPersonale=Double.valueOf(0);
    
    List tipiCaus=new ArrayList();
    tipiCaus.add(CausaliLineeBean.TIPO_CAUS_GUASTO);
    tipiCaus.add(CausaliLineeBean.TIPO_CAUS_PERDGEST);
    
     
    
    for(String cdLTmp:wrkC.getLineePW()){
      Date dataTmp=dataInizio;
      while(DateUtils.beforeEquals(dataTmp, dataFine)){
        try{
          DatiProdPWGeneral dtpt=new DatiProdPWGeneral(wrkC.getAzienda(), dataTmp, cdLTmp);
          dtpt.retriveDataCdl(con);
          if(dtpt.isValid()){  
            //ore Impianto
            List<InfoFermoCdL> listOreIm=dtpt.getFermiGg(cdLTmp,CausaliLineeBean.TIPO_CAUS_ORENONPROD);
            
            minPersonale+=dtpt.getMinProdOperatori();
            if(CAPACITA_OREUOMO.equals(wrkC.getTipoCapacita())){
              minProdImp+=dtpt.getMinTurniOperatori(listOreIm);
            }else{
              minProdImp+=dtpt.getMinProdImpianto(null, null);
              Double minNNProd=dtpt.getMinutiNnProduttivi(null);
              minProdImp-=minNNProd;
            }
            //Pz Prod
            //FORZATURA PER IMA ANTE!!!!!!
//            if(wrkC.getCodCdl().equals("01008") && tipoCalc.equals(MONTH)){
//              DatiProdPWGeneral dtpt1=new DatiProdPWGeneral(wrkC.getAzienda(), dataTmp, "01008C1");
//              dtpt1.retriveDataCdl(con);
//              if(dtpt1.isValid())
//                pzProd+=dtpt1.getInfoTCdl().getNumPzTot();
//              
//              DatiProdPWGeneral dtpt2=new DatiProdPWGeneral(wrkC.getAzienda(), dataTmp, "01008C2");
//              dtpt2.retriveDataCdl(con);
//              if(dtpt2.isValid())
//                pzProd+=dtpt2.getInfoTCdl().getNumPzTot();
//              
//            }else {
//              pzProd+=dtpt.getInfoTCdl().getNumPzTot();
//              clProd+=dtpt.getInfoTCdl().getNumClTot();
//            }
            //Pz Prod
            pzProd+=dtpt.getInfoTCdl().getNumPzTot();
            clProd+=dtpt.getInfoTCdl().getNumClTot();
            //min Fermi
            minSetUp+=dtpt.getMinutiSetup(null);
            minFermi+=dtpt.getMinFermi(tipiCaus, null, null, null);
            minMicrofermi+=dtpt.getMinutiMicrofermi(null);
          }
        } catch (SQLException s){
          _logger.error("Errore in fase di lettura  dei dati di produzione da Portale x linea "+cdLTmp+" --> "+s.getMessage());
          addError("Errore in fase di lettura  dei dati di produzione x linea "+cdLTmp+" e periodo "+dataInizio+" - "+dataFine+" --> "+s.toString());
        }  
        
        dataTmp=DateUtils.addDays(dataTmp,1);  
      }
    }
    
    
    
    dtMvx=new DatiConsuntivoMVX(wrkC.getAzienda(), wrkC.getCodCdl(), "PZ");
    dtMvx.setAnno(DateUtils.getYear(dataInizio));
    dtMvx.setMese(DateUtils.getMonth(dataInizio)+1);
    if(tipoCalc.equals(WEEK))
      dtMvx.setSettimana(DateUtils.getWorkWeek(dataInizio));
    
    dtMvx.setGiorniLav(commElabPeriodo);
    dtMvx.setOreLav(minProdImp/60);
    if(UNMISURA_PEZZI.equals(wrkC.getUnMisura())){
        dtMvx.setPzProd(pzProd);
    }else if(UNMISURA_COLLI.equals(wrkC.getUnMisura())){
        dtMvx.setPzProd(clProd);
    }else{
        dtMvx.setPzProd(Math.max(pzProd,clProd));
        System.out.println("Attenziona unità di misura non definita correttamente per cdl-->"+wrkC.getCodCdl()+" ; "+pzProd+" ; "+clProd+";"+ (pzProd>clProd ? "PZ" : "CL"));
    }
    //dtMvx.setPzProd(Math.max(pzProd,clProd));
    //System.out.println(wrkC.getCodCdl()+" ; "+pzProd+" ; "+clProd+";"+ (pzProd>clProd ? "PZ" : "CL")); 
    dtMvx.setOreBudget(wrkC.getOreBudget(commElabPeriodo));
    dtMvx.setOreSetup(minSetUp/60);
    dtMvx.setOreFermi(minFermi/60);
    dtMvx.setOreMicrofermi(minMicrofermi/60);
    dtMvx.setOrePersonale(minPersonale/60);
    
    return dtMvx;
  }
  
  
  private Integer getNumCommCdlBu(Map buComm,String buCdl){
    Integer numComm=Integer.valueOf(0);
    if(StringUtils.IsEmpty(buCdl))
      return numComm;
    
    List<String> buCdlList=ArrayUtils.getListFromArray(buCdl.split(","));
    for(String bu:buCdlList){
      if(buComm.containsKey(bu)){
        Integer appo=(Integer) buComm.get(bu);
        numComm=Math.max(numComm, appo);
      }
    }
    
    return numComm;
  }
  
  
  private List getListCdl(Connection con ) {
    List<List> l=new ArrayList();
    
    try{
      QueryCdL4DtProd qry=new QueryCdL4DtProd();
      qry.setFilter(QueryCdL4DtProd.FTGESTTURNI, "Y");
      if(tipoCalc.equals(MONTH))
        qry.setFilter(QueryCdL4DtProd.FTWRKC, "Y");
      
      if(lineeElab!=null && !lineeElab.isEmpty())
        qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeElab.toString());
      

      ResultSetHelper.fillListList(con, qry.toSQLString(), l);
      
    } catch (SQLException s){
      addError("Impossibile leggere la lista delle linee da verificare --> "+s.toString());
      _logger.error("Impossibile leggere la lista delle linee da verificare --> "+s.getMessage());
    } catch(QueryException q){
      addError("Impossibile leggere la lista delle linee da verificare --> "+q.getMessage());
      _logger.error("Impossibile leggere la lista delle linee da verificare --> "+q.getMessage());
    }
    
    return l;
  }
  
  
  
    /**
     * Torna una lista con alcuni parametri settati nella tabella MPDWCT
     * pos2-->PPPCAP :tipoCapacità (oreMacchina,oreUomo)
     * pos3-->PPWCNP :numero persone
     * pos4-->PPCPTY :capacità linea
     * pos5-->PPSHFT :numero turni
     * @param con
     * @param cdl
     * @return
     * @throws SQLException 
     */
    private InfoWrkC getInfoAggCdl(Connection con ,Integer az,String cdl) {
      if(StringUtils.isEmpty(cdl))
        return null;
      
      InfoWrkC wrC=null;
      try{
        String qry="SELECT PPPCAP,PPWCNP,PPCPTY,PPSHFT,trim(CLUNMS) " +
                    "\n from mvxbdta.mpdwct inner join mcobmoddta.zdpwcl on ppcono=clcono and ppplgr=clplgr " +
                    "\n where 1=1 "+
                    " and ppcono = "+JDBCDataMapper.objectToSQL(az)+
                    " and ppplgr = "+JDBCDataMapper.objectToSQL(cdl)+
                    " and PPCPTY>0";

        List l=new ArrayList();                    
        ResultSetHelper.fillListList(con, qry, l);
        
        wrC=new InfoWrkC(az,cdl);
        
        if(l.isEmpty()){
          addWarning("Parametri non valorizzati correttamente su MPDWCT per linea"+cdl+" \n");
        }else{
          wrC.setTipoCapacita(ClassMapper.classToString(((List)l.get(0)).get(0)));
          wrC.setNumPersone(ClassMapper.classToClass(((List)l.get(0)).get(1),Double.class));
          wrC.setCapacita(ClassMapper.classToClass(((List)l.get(0)).get(2),Double.class));
          wrC.setnTurni(ClassMapper.classToClass(((List)l.get(0)).get(3),Double.class));
          wrC.setUnMisura(ClassMapper.classToString(((List)l.get(0)).get(4)));
        }
      } catch(SQLException s){
        _logger.error("Impossibile leggere i dati da MPDWCT del Centro di Lavoro :"+cdl +" -->"+s.getMessage());
        addError("Impossibile leggere i dati da MPDWCT del Centro di Lavoro :"+cdl +" -->"+s.toString());
      } 
      
      return wrC;
      
    }

 
  private class InfoWrkC {
    private Integer az;
    private String codCdl;
    private String tipoCapacita;
    private Double numPersone;
    private Double capacita;
    private Double nTurni;
    
    private String unMisura;
    

    private List<String> lineePW;
    
    
    public InfoWrkC(Integer az,String cdl){
      this.az=az;
      this.codCdl=cdl;
      this.lineePW=new ArrayList();
      this.numPersone=Double.valueOf(0);
      this.capacita=Double.valueOf(0);
      this.nTurni=Double.valueOf(0);
    }

    
    public Integer getAzienda() {
      return az;
    }

    public String getCodCdl() {
      return codCdl;
    }

    public void setCodCdl(String codCdl) {
      this.codCdl = codCdl;
    }
    
    
    public String getTipoCapacita() {
      return tipoCapacita;
    }

    public void setTipoCapacita(String tipoCapacita) {
      this.tipoCapacita = tipoCapacita;
    }

    public Double getNumPersone() {
      return numPersone;
    }

    public void setNumPersone(Double numPersone) {
      this.numPersone = numPersone;
    }

    public Double getCapacita() {
      return capacita;
    }

    public void setCapacita(Double capacita) {
      this.capacita = capacita;
    }

    public Double getnTurni() {
      return nTurni;
    }

    public void setnTurni(Double nTurni) {
      this.nTurni = nTurni;
    }
    
    public Double getOreBudget(Integer nCommPeriodo){
      Double oreBdgPeriodo=nCommPeriodo*this.capacita*this.nTurni;

      if(CAPACITA_OREUOMO.equals(tipoCapacita))
        oreBdgPeriodo=oreBdgPeriodo*this.numPersone;
      
      
      return oreBdgPeriodo;
    }
    
    
    public void addLineaPW(String codLinea){
      lineePW.add(codLinea);
    }

    public List<String> getLineePW() {
      return lineePW;
    }

    public void setLineePW(List<String> lineePW) {
      this.lineePW = lineePW;
    }

    public String getUnMisura() {
        return unMisura;
    }

    public void setUnMisura(String unMisura) {
        this.unMisura = unMisura;
    }
    
    
    
    
  } 
    
    
  
  private static final Logger _logger = Logger.getLogger(ElabDtProdPWConsolidamento.class); 

}
