/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.NomiLineeColomb;
import colombini.costant.TAPWebCostant;
import colombini.logFiles.R1P3.FileTabulatoAnteRem;
import colombini.model.persistence.BeanInfoColloComForTAP;
import colombini.model.persistence.BeanInfoAggColloComForTAP;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.produzione.F1.QryPzAnteAllumFeb;
import colombini.query.produzione.F1.QryPzAnteForBiesseP3;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryColliCommessaFebal;
import colombini.query.produzione.QueryListColliPzCommessa;
import colombini.query.produzione.R1.QueryPzCommImaAnte;
import colombini.query.produzione.R1.QueryPzCommLotto1;
import colombini.util.DatiCommUtils;
import colombini.util.DatiProdUtils;
import colombini.util.DesmosUtils;
import colombini.util.InfoMapLineeUtil;
import db.CustomQuery;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 * Classe che si preoccupa di caricare i dati di produzione per commessa di diverse linee
 * Il dato viene archiviato in ZTAPCI
 * @author lvita
 */
public class ElabDatiProdCommesseNew extends ElabClass{
  
  public final static String DATAETK="$DATA$";
  public final static String COMMETK="$COMM$";
  public final static String COLLOETK="$COLLO$";
  
    

  private Date dataRif;
  private Long numComm;
  
  
  public ElabDatiProdCommesseNew(){
    super(); 
  }
  
  
  @Override
  public void exec(Connection con) {
    
    Map propsElab=getElabProperties();
    //pulizia tabelle
    puliziaTabelle(con,propsElab);
    //---
    try { 
      PersistenceManager apm=new PersistenceManager(con);
      _logger.info("Giorno rif: "+dataRif);
      List<List> commGg=DatiProdUtils.getInstance().getListGgCommesse(con, dataRif, null,Boolean.TRUE);
      _logger.info(" Commesse disponibili n. "+commGg.size()+" --> "+commGg.toString());
      Map  commEx=getMapCommessePresenti(con);
      
      loadDatiForatriceRem(apm, commGg, commEx,propsElab);
      loadDatiMontaggiArtec(apm, commGg, commEx,propsElab);
      loadDatiRiccioImaAnteR1P1(apm, commGg, commEx,propsElab);
      
      
      loadDatiAnteAllum(apm, commGg, commEx,propsElab);
      loadDatiLavMisura(apm, commGg, commEx,propsElab);

      //loadDatiFornitori(apm, commGg, commEx, propsElab);
      
      loadDatiImballoAnteSpecialiImaAnteR1P1(apm, commGg, commEx,propsElab);
      loadDatiForatriceBiesseP3(apm, commGg, commEx,propsElab);
   //   loadDatiForaturaAnteSpecialiR1P1(apm, commGg, commEx,propsElab);
      
      
    } catch (SQLException ex) {
      addError("Impossibile caricare la lista di commesse da elaborare :"+ex.getMessage());
    } catch(QueryException qe){
      addError("Impossibile caricare la lista di commesse da elaborare :"+qe.getMessage());
    } finally{
      
    }  
  }
  
  
  
  @Override
  public Boolean configParams() {
    Map parm=getInfoElab().getParameter();
    
    if(parm.get(ALuncherElabs.DATAINIELB)==null)
      return Boolean.FALSE;
    
    dataRif=(Date) parm.get(ALuncherElabs.DATAINIELB);
    
    
    
    return Boolean.TRUE;
  }

  
  private Map getMapCommessePresenti(Connection con) throws SQLException{
    Long dtL=DateUtils.getDataForMovex(dataRif);
    List<List> commesse=new ArrayList();
    Map mappa=new HashMap<String,List> ();

    String select=" select distinct trim(tiplgr) tiplgr,tidtco,ticomm from mcobmoddta.ztapci where 1=1 "+
                  " and tidtco>="+JDBCDataMapper.objectToSQL(dtL)+
                  " order by tiplgr,tidtco desc";  

    ResultSetHelper.fillListList(con, select, commesse);

    for(List comm:commesse){
      String plgr=ClassMapper.classToString(comm.get(0));
      Long dtC=ClassMapper.classToClass(comm.get(1),Long.class);
      Long ncomm=ClassMapper.classToClass(comm.get(2),Long.class);
      Map commMapCdl=new HashMap();

      if(mappa.containsKey(plgr)){
        commMapCdl=(Map) mappa.get(plgr);
      }
      commMapCdl.put(ncomm,dtC);
      mappa.put(plgr,commMapCdl);

    }

    return mappa;
  }
  
  public List<List> getListCommToSave(List<List> commesse,Map commEx,String cdL){
    Map commesseIn=(Map) commEx.get(cdL);
    List commesseOut=new ArrayList();
    for(List commDist:commesse){
      Long commTmp=ClassMapper.classToClass(commDist.get(1),Long.class);
      Long dtTmp=ClassMapper.classToClass(commDist.get(0),Long.class);
      Long dtElbTmp=ClassMapper.classToClass(commDist.get(2),Long.class);
      if((commesseIn==null || commesseIn.isEmpty()) || !commesseIn.containsKey(commTmp)){
        List recTmp=new ArrayList();
        recTmp.add(commTmp);
        recTmp.add(dtTmp);
        recTmp.add(dtElbTmp);
        commesseOut.add(recTmp);
      }
    }
    
    
    return commesseOut;
  }
    
  private List<List>getListCommForAnte(List<List> commesse){
    List commesseMod=new ArrayList();
    for(List commRec:commesse){
      List commNew=new ArrayList();
      Long commTmp=ClassMapper.classToClass(commRec.get(1),Long.class);
      Long dtTmp=ClassMapper.classToClass(commRec.get(0),Long.class);
      if(commTmp<400)
        commTmp+=400;
      
      commNew.add(dtTmp);
      commNew.add(commTmp);
      
      commesseMod.add(commNew);
    }
    
    return commesseMod;
  }
  
  /**
   * 
   * @param con
   * @param cdL
   * @param comm
   * @param dtComm
   * @param bu
   * @param lineeLog
   * @param col
   * @param condPers
   * @return 
   */
  private List<BeanInfoColloComForTAP> getListBeansFromSCXXXCol(Connection con ,String cdL,Long comm,Date dtComm,List<String> bu,List<String> lineeLog,Boolean col,String condPers){
    List beans=new ArrayList();
    List<List> listPz=new ArrayList();
    String select="";

    QueryListColliPzCommessa qry=new QueryListColliPzCommessa();
    qry.setFilter(QueryListColliPzCommessa.FLTRCOMM, comm);
    if(col)
      qry.setFilter(QueryListColliPzCommessa.FLTRCOLLO, "YES");

    if(bu!=null && bu.size()>0)
      qry.setFilter(QueryListColliPzCommessa.FLTRBUS, bu.toString());
    
    qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLog.toString());
    
    if(!StringUtils.isEmpty(condPers))
      qry.setFilter(QueryListColliPzCommessa.FLCONDPERS, condPers);
    
    try {
      select=qry.toSQLString();
      ResultSetHelper.fillListList(con, select, listPz);
      beans=getInfoColloBeansFromList(listPz, cdL, comm, dtComm);

    } catch (SQLException ex) {
      addError("Problemi in fase di esecuzione della query"+select.toString()+" --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError("Problemi in fase di generazione della query  --> "+ex.getMessage());
    }
      
    return beans;
  }
  
  
  private List<BeanInfoColloComForTAP> getListBeansFromSCXXXCol(Connection con ,String cdL,Long comm,Date dtComm,List<String> bu,List<String> lineeLog,Boolean col){
    
    return getListBeansFromSCXXXCol(con, cdL, comm, dtComm, bu, lineeLog, col, null);
  }
  
  
  /**
   * Data una lista di informazioni formattata in un certo modo ,restituisce la lista dei bean utilizzati per sa
   * @param info
   * @param cdL
   * @param comm
   * @param dtComm
   * @param pathFile
   * @return 
   */
  private List<BeanInfoColloComForTAP> getInfoColloBeansFromList(List<List> info,String cdL,Long comm,Date dtComm){
    List<BeanInfoColloComForTAP> beans=new ArrayList();
    
    if(info==null || info.isEmpty())
      return beans;
    
    for(List rec:info){
        Integer collo=ClassMapper.classToClass(rec.get(0), Integer.class);
        Integer nart=ClassMapper.classToClass(rec.get(1), Integer.class);
        BeanInfoColloComForTAP bean=new BeanInfoColloComForTAP(cdL);//, comm.intValue(), collo, nart);
        bean.setDataComN(DateUtils.getDataForMovex(dtComm));

        String linea=ClassMapper.classToString(rec.get(2));
        if(!TAPWebCostant.CDL_RICCIOIMAANTE_EDPC.equals(cdL) && linea!=null && linea.length()==4)
          linea="0"+linea;
        
        bean.setLineaLogica(linea);
        bean.setBox(ClassMapper.classToClass(rec.get(3), Integer.class));
        bean.setPedana(ClassMapper.classToClass(rec.get(4), Integer.class));
        bean.setnOrdine(ClassMapper.classToString(rec.get(5)));
        bean.setRigaOrdine(ClassMapper.classToClass(rec.get(6), Integer.class));
        bean.setCodArticolo(ClassMapper.classToString(rec.get(7)));
        bean.setDescArticolo(ClassMapper.classToString(rec.get(8)));
        bean.setDescEstesa(ClassMapper.classToString(rec.get(9)));

        //Barcode del pezzo --->NECESSARIO!!!!
        if(rec.size()>10 && rec.get(10)!=null){
          bean.setBarcode(ClassMapper.classToString(rec.get(10)));
        }else{
          bean.setBarcode(bean.getBarcodeStd());  
        }
        
        
        // MOD 17/11/2016 il pathFile non viene mai preso da un interrogazione dati
//        //gestione del file etichetta eventualmente da stampare
//        if(rec.size()>11 && rec.get(11)!=null){
//          bean.setPathFile(ClassMapper.classToString(rec.get(11)));
//        } 

        //codice colore valorizzato solo in alcuni casi
        if(rec.size()>11 && rec.get(11)!=null){
          bean.setCodColore(ClassMapper.classToClass(rec.get(11),String.class));
        }
        
        beans.add(bean);
      }
    
    return beans;
  }
      
    
    
    private String getEtkFileNameStd(String path,Integer comm,Integer collo,Long dtCo,Boolean nColWithZero){
    
      String nomeFile=path.replace(DATAETK, dtCo.toString());

      nomeFile=nomeFile.replace(COMMETK, DatiCommUtils.getInstance().getStringCommessa(comm));

      if(nColWithZero){
        nomeFile=nomeFile.replace(COLLOETK, DatiProdUtils.getInstance().getStringNCollo(collo));
      }else{
        nomeFile=nomeFile.replace(COLLOETK, collo.toString());
      }
    
    return nomeFile;
  }
  
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
  private void  loadDatiLinea ( PersistenceManager apm,List commDisp,Map commEx,Map propsElab,String linea){
    List<List> commToLoad=getListCommToSave(commDisp, commEx, linea);
    for(List infoC:commToLoad){
       Long comm=(Long) infoC.get(0);
       Long dtC=(Long) infoC.get(1);
//       List beansToSave=getListInfoToSave(comm,dtC,linea,propsElab);
//       apm.storeDtFromBean(beansToSave);
       
    }
  }
    
  private void loadDatiForatriceRem( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
    List<List> commToLoad=getListCommToSave(commDisp, commEx, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORANTEREM));
    for(List infoC:commToLoad){
      
      Long comm=(Long) infoC.get(0);
      Long dtC=(Long) infoC.get(1);
    
      _logger.info("Caricamento dati Ante Rem per commessa "+comm+" - "+dtC);
      String filName= InfoMapLineeUtil.getTabuProdNameLinea(NomiLineeColomb.FORANTEREM, comm.intValue());
      String filDest=FileUtils.getNomeFileBKC(filName);
      
      try {
        _logger.info("Copia file bck....");
        FileUtils.copyFile(filName, filDest);
      
        FileTabulatoAnteRem ft=new FileTabulatoAnteRem(filDest);
        List<BeanInfoColloComForTAP> beans=ft.processFile(dtC);
        for(BeanInfoColloComForTAP b:beans ){
          if(b.getLineaLogica().contains("36020")){
            b.loadInfoBox(apm.getConnection());
          }  
          //if(!"36092".equals(b.getLineaLogica()) ){
          if(isDtForatriceRemValid(b)){
            try{
              apm.storeDtFromBean(b);
            } catch(SQLException s){
              _logger.error("Errore in fase di salvataggio del pezzo "+b.getBarcode()+" -->"+s.getMessage());
              addError("Errore in fase di salvataggio del pezzo/collo "+b.getBarcode()+"-->"+s.toString());
            }
          }  
        }
        //apm.storeDtFromBeans((List)beans);
      } catch (IOException ex) {
        addError("File "+filName+" non trovato o inaccessibile -->"+ex.getMessage());
      }
    }  
  }  
  
   /**
    * Esclude alcuni bean dai pezzi da produrre 
    * @param b
    * @return 
    */
   private Boolean isDtForatriceRemValid(BeanInfoColloComForTAP b){
     Boolean valid=Boolean.TRUE;
     
     if("36092".equals(b.getLineaLogica()))
       valid=Boolean.FALSE;

     //richiesta di Nicola Genghini tk 31826
     if("36110".equals(b.getLineaLogica())){
       String descArt=b.getDescArticolo().toUpperCase();
       if(descArt.contains("TAMPON") || descArt.contains("COPRIF") || descArt.contains("MENSOL")){
         _logger.info("Articolo scartato  -->"+b.getCommessa()+" - "+b.getCollo()+" -->"+descArt);
         valid=Boolean.FALSE;   
       }  
     }
     
     return valid;         
   }
  
    private void loadDatiForatriceBiesseP3(PersistenceManager apm, List<List> commDisp, Map commEx, Map propsElab) {
      Connection conDesmosFeb=null;
      List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_FORANTEBIESSE_EDPC);
      try{
        conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();   
        for(List infoC:commToLoad){

          Long comm=(Long) infoC.get(0);
          Long dtC=(Long) infoC.get(1); 
          Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
          
          if(DesmosUtils.getInstance().isElabDesmosFebalFinished(conDesmosFeb, comm, dataC)){
            List<BeanInfoColloComForTAP> beansFeb=getListBeansAnteFebal(conDesmosFeb, TAPWebCostant.CDL_FORANTEBIESSE_EDPC, comm, dataC);
            //List<BeanInfoColloComForTAP> beansVD=getListBeansVediDisegno();
          }  
        }
      }catch(SQLException s){
        _logger.error("Errore in fase di collegamento al db DesmosFebal -->"+s.getMessage());
        addError("Errore in fase di collegamento al db DesmosFebal -->"+s.toString());
        
      }catch(QueryException q) {
        _logger.error("Errore in fase di interrogazione del db DesmosFebal -->"+q.getMessage());
        addError("Errore in fase di interrogazione del db DesmosFebal -->"+q.toString());
        
      }finally{
        if(conDesmosFeb!=null){
          try{
            conDesmosFeb.close();
          } catch(SQLException s){
            _logger.error("Errore in fase di chiususra della connessione al db DesmosFebal"+s.getMessage()); 
          }
        }
      }
    }

   
   private void loadDatiAnteAllum( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_ANTEALLUM_EDPC);
     Connection conDesmosFeb=null;
     List linee=new ArrayList(Arrays.asList("36090,06060"));
     List lineeAgg=new ArrayList(Arrays.asList("36092,38023"));
     String pathfile=(String) propsElab.get(NameElabs.PATHETKANTEALLUM);
     String pathfileFeb=(String) propsElab.get(NameElabs.PATHETKANTEALLUMFEB);

     try{
       conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();          
       for(List infoC:commToLoad){
         Long comm=(Long) infoC.get(0);
         Long dtC=(Long) infoC.get(1);
         Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
         _logger.info("Caricamento dati Postazione Imballo Ante Alluminio per commessa "+comm+" - "+dtC);


         
             
         try{  
           if(DesmosUtils.getInstance().isElabDesmosFebalFinished(conDesmosFeb, comm, dataC)){
             List<BeanInfoColloComForTAP> beansCol=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, linee, Boolean.FALSE);
             List<BeanInfoColloComForTAP> beansFeb=getListBeansAnteFebal(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC);
             
             List<BeanInfoColloComForTAP> beansCol2=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, lineeAgg, Boolean.TRUE);
             List<BeanInfoColloComForTAP> beansFebal2=getListBeansColliFebal(TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, lineeAgg);
             
             
             //pz standard Colombini  con etichetta
             apm.storeDtFromBeans((List)beansCol);
             saveInfoForEtkPz(apm, beansCol, pathfile,Boolean.FALSE);
             //pz standard Febal con etichetta
             apm.storeDtFromBeans((List)beansFeb);
             saveInfoForEtkPz(apm, beansFeb, pathfileFeb,Boolean.FALSE);
             
             apm.storeDtFromBeans((List)beansCol2);
             apm.storeDtFromBeans((List)beansFebal2);
           }
         } catch(SQLException s){
           _logger.error("Errore in fase di interrogazione del db DesmosFebal"+s.getMessage());
           addError("Errore in fase di interrogazione del db DesmosFebal"+s.toString());
         } catch(QueryException s1){
           _logger.error("Errore in fase di interrogazione del db DesmosFebal "+s1.getMessage());
           addError("Errore in fase di interrogazione del db DesmosFebal "+s1.toString());
         }  
       } 
     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
     } finally{
       if(conDesmosFeb!=null)
         try {
           conDesmosFeb.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
         }
     }      
   }  
  
   
   private void loadDatiFornitori( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
      List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_FORN_BST_EDPC);
      List linee=new ArrayList();
      linee.add("4020");
      
                
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        _logger.info("Caricamento dati Fornitore BST per commessa "+comm+" - "+dtC);
        
        
        
        List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_FORN_BST_EDPC, comm, dataC, null, linee, Boolean.TRUE);
        apm.storeDtFromBeans((List)beans);
        
      }
   }  
   
   
   
  private void loadDatiRiccioImaAnteR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_RICCIOIMAANTE_EDPC);
    
    String colors= (String) propsElab.get(NameElabs.LISTCODCLRTOIND);
    List<String> colorsCodList=ArrayUtils.getListFromArray(colors.split(","));
    
    colors= (String) propsElab.get(NameElabs.LISTDESCRCLRTOIND);
    List<String> colorsDescList=ArrayUtils.getListFromArray(colors.split(","));
    
     for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1); 
        //Long comm=Long.valueOf(335);
        //Long dtC=Long.valueOf(20161130);
        
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        
        if(comm<400)
          comm+=400;
        
        _logger.info("Caricamento dati Postazione Riccio Ima Ante per commessa "+comm+" - "+dtC);
        List<BeanInfoColloComForTAP> beans=getListPzFromImaAnte(TAPWebCostant.CDL_RICCIOIMAANTE_EDPC, comm, dataC,null, null, null);
        //checkColori()
        for(BeanInfoColloComForTAP bean:beans){
          String codColore=bean.getCodColore();
          int idx=colorsCodList.indexOf(codColore);
          if(!StringUtils.isEmpty(codColore) && idx>=0){
            bean.setCodColore(colorsDescList.get(idx));
          }else{
            bean.setCodColore(null);
          }
        }
        apm.storeDtFromBeans((List) beans);
     }   
    
  }
  
  private void loadDatiImballoAnteSpecialiImaAnteR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC);
    
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    
    List lineeLogiche1=new ArrayList();
    lineeLogiche1.add("06053");
    lineeLogiche1.add("06054");
    
    
    lineeLogiche1.add("06057");
    lineeLogiche1.add("06058");
    // 12/04/2018 i pezzi di questa linea a tendere non arriveranno più dalla Ima Ante ma solo dalla Lotto1
    lineeLogiche1.add("06019");
    
    
    List lineeLogiche2=new ArrayList();
//    lineeLogiche2.add("06250");
    lineeLogiche2.add("06251");
    lineeLogiche2.add("06252");
    
    for(List infoC:commToLoad){
      Long comm=(Long) infoC.get(0);
      Long commTmp=comm;
      Long dtC=(Long) infoC.get(1);
      Long dtEC=(Long) infoC.get(2);
      Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
      List beans=new ArrayList();
      Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");
      Connection conSQLS=null;
      Connection conSQLSDesmos=null;
      try{
        conSQLS=ColombiniConnections.getDbImaAnteConnection();
        conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
         //pz da Ima Ante --> prevede anche la stampa dell'etichetta
        // X Linee Interne allora solo la commessa viene maggiorata di 400
        
        //linea 06055
//        List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche0);
//          if(l!=null)
//            beans.addAll(l);
        
        if(comm<400)
          commTmp=comm+400;
          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee interne) per commessa "+commTmp+" - "+dtC);
          List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1);
          if(l!=null)
            beans.addAll(l);


          //ante esterne (sia  la minicommessa che per la commessa vengono maggiorate di 1000
          commTmp=comm+1000;
          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee esterne) per commessa "+commTmp+" - "+dtC);
          l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, dataElab,"", lineeLogiche2);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  

        
        
//        List colliToRemove=new ArrayList();
//        String queryC="select distinct cast(PackageNo as int) from dbo.tab_et where commissionNo="+commTmp+" and ProductionLine='06250'";
         
         List colliComm=new ArrayList();
         String queryC=" select distinct PackageNo  from dbo.tab_et where commissionNo in ( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) "+
                  " and year(CommissionDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataElab));
         
         ResultSetHelper.fillListList(conSQLS, queryC, colliComm);

        //condizione per fare in modo che la commessa venga caricata
        //tutta insieme---->  
        //potrebbe infatti capitare che la commessa non è stata ancora importata 
        //nel database della Ima Ante ma è già disponibile su Movex
        if(colliComm.size()>0 && DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC) ){
          
          // laccato
          beans=new ArrayList();
          List lineeL=new ArrayList();
          lineeL.add("06253");
          String cond=null;
          
          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,cond);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);

          
//          beans=new ArrayList();
//          List lineeL=new ArrayList();
//          lineeL.add("06057");
//          lineeL.add("06253");
//          
//          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.TRUE,cond);
//          if(l!=null)
//            beans.addAll(l);
//
//          apm.storeDtFromBeans(beans);
          
          //linea pz speciali
          lineeL=new ArrayList();
          lineeL.add("06058");
          lineeL.add("06059");
          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.TRUE);
          if(l!=null && l.size()>0){
            apm.storeDtFromBeans(l);
            saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
          }  
          //pezzi da Lotto1
          lineeL=new ArrayList();
          lineeL.add("6019");
          l=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, lineeL);
          if(l!=null && l.size()>0){
            apm.storeDtFromBeans(l);
            saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
          }
        }  
      } catch(SQLException s) {
         addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
      } finally{
        if(conSQLS!=null)
          try {
            conSQLS.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
          }
        if(conSQLSDesmos!=null)
          try {
            conSQLSDesmos.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
          }
        
      }  
   }   
    
  }
  
  private void loadDatiForaturaAnteSpecialiR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_FORANTESPECIALI_EDPC);
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    // 12/04/2018 
    // per la linea 06057 alla postazione di foratura sparano il barcode Ima Ante e stampano anche l'etichetta
    //mentre alla postazione di imballaggio 
    List lineeLogiche1=Arrays.asList("06057");
    
    for(List infoC:commToLoad){
      Long comm=(Long) infoC.get(0);
      Long commTmp=comm;
      Long dtC=(Long) infoC.get(1);
      Long dtEC=(Long) infoC.get(2);
      Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
      List beans=new ArrayList();
      Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");
      Connection conSQLS=null;
      try{
        conSQLS=ColombiniConnections.getDbImaAnteConnection();
        
         List colliComm=new ArrayList();
         String queryC=" select distinct PackageNo  from dbo.tab_et where commissionNo in ( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) "+
                  " and year(CommissionDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataElab));
         
         ResultSetHelper.fillListList(conSQLS, queryC, colliComm);

        //condizione per fare in modo che la commessa venga caricata
        //tutta insieme---->  
        //potrebbe infatti capitare che la commessa non è stata ancora importata 
        //nel database della Ima Ante ma è già disponibile su Movex
        if(colliComm.size()>0 ){

          if(comm<400)
            commTmp=comm+400;

          //salvataggio dati per linea 06057 senza generazione etichette
          _logger.info("Caricamento dati Postazione Foratura Ante Speciali (linee interne) per commessa "+commTmp+" - "+dtC);
          List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_FORANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
          
          //------- 06019 stampa etichette
//          beans=new ArrayList();
//          lineeLogiche1=Arrays.asList("06019");
//          _logger.info("Caricamento dati Postazione Foratura Ante Speciali (linee interne) per commessa "+commTmp+" - "+dtC);
//          l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_FORANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1);
//          if(l!=null)
//            beans.addAll(l);
//
//          apm.storeDtFromBeans(beans);
//          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
          
          //----------
          beans=new ArrayList();
          List lineeL=Arrays.asList("06253");
          String cond=null;
          
          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_FORANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,cond);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
          
        }  
      } catch(SQLException s) {
         addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
      } finally{
        if(conSQLS!=null)
          try {
            conSQLS.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
          }
      }  
   }   
    
  }
  
  private void loadDatiLavMisura(PersistenceManager apm,List commDisp,Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_LAVMISURAIMB_EDPC);
      List linee=new ArrayList();
      linee.add("06100");
      linee.add("06105");
      //linea riaggiunta dopo incontro del 4/7/2016
      linee.add("06284");
      //
      
//      linee.add("06074");
      linee.add("36110");
      linee.add("36109");
                
      List lineeFebal=new ArrayList();
      lineeFebal.add("06104");
      lineeFebal.add("06105");
      
      //linea aggiunta dopo incontro del 4/7/2016
      lineeFebal.add("06106");
      //
      
      lineeFebal.add("38024");
      //linea aggiunta su richiesta di Simone Arduino in data 15/06/2016
      lineeFebal.add("06518");
      
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        _logger.info("Caricamento dati Postazione Imballo Lavori su Misura per commessa "+comm+" - "+dtC);
        
        List<BeanInfoColloComForTAP> beansFebal=getListBeansColliFebal(TAPWebCostant.CDL_LAVMISURAIMB_EDPC, comm, dataC, lineeFebal);
        if((beansFebal!=null && beansFebal.size()>0) || comm>365){
          List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_LAVMISURAIMB_EDPC, comm, dataC, null, linee, Boolean.TRUE);
          if(beansFebal!=null)
            beans.addAll(beansFebal);
          
          apm.storeDtFromBeans((List)beans);
        }
      }
  }

  
  private void loadDatiMontaggiArtec(PersistenceManager apm,List commDisp,Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_MONTAGGIARTEC_EDPC);
      List linee=new ArrayList();
      linee.add("36010");
      linee.add("36015");
      linee.add("36020");
      linee.add("36025");
      linee.add("36030");
      linee.add("36050");
      linee.add("36150");
      linee.add("36200");
      
     for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        _logger.info("Caricamento dati Montaggio Artec "+comm+" - "+dtC);
        
        List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_MONTAGGIARTEC_EDPC, comm, dataC, null, linee, Boolean.TRUE);
        apm.storeDtFromBeans((List)beans);
     }
     
  }
 
  
  private List getListBeansColliFebal(String cdl,Long comm,Date dataC,List lineeLog){
    List l=new ArrayList();
    List beans=new ArrayList();
    try{
      Connection conFebal=ColombiniConnections.getDbDesmosFebalProdConnection();
      QueryColliCommessaFebal qry=new QueryColliCommessaFebal();
       qry.setFilter(FilterQueryProdCostant.FTNUMCOMM, comm);
       qry.setFilter(FilterQueryProdCostant.FTDATACOMMN, DateUtils.getDataForMovex(dataC));
       qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLog.toString());
       
      
       ResultSetHelper.fillListList(conFebal, qry.toSQLString(), l);
       beans=getInfoColloBeansFromList(l, cdl, comm, dataC);
       
    } catch(SQLException s){
      _logger.error("Connessione non disponibile con il server "+s.getMessage());
      addError("Errore in fase di accesso al db DesmosFebal"+s.toString());
    } catch (QueryException ex) {
      _logger.error("Errore in fase di lettura dei dati sul server :"+ex.getMessage());
      addError("Errore in fase di lettura dei dati per la commessa Febal :"+ex.toString());
    }
    return beans;
  }  
  

  private List getListPzFromImaAnte(String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche){
    Connection con=null;
    List result=new ArrayList();
    try{
      con=ColombiniConnections.getDbImaAnteConnection();
      result=getListPzFromImaAnte(con, cdL, comm, dataComm, dataElab, packType, lineeLogiche);
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
    } finally{
      if(con!=null)
        try {
          con.close();
      
        } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
        }
    }
    
    return result;
  }
  
  private List<BeanInfoColloComForTAP> getListBeansAnteFebal(Connection conDesmosFeb, String cdL, Long comm, Date dataComm) throws QueryException, SQLException {
    CustomQuery q=null;
    List result=new ArrayList();
    if(TAPWebCostant.CDL_ANTEALLUM_EDPC.equals(cdL)){
      q=new QryPzAnteAllumFeb();
      //Integer anno=DateUtils.getYear(dataComm);
      String lancio=DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataComm);//anno.toString()+(comm<100 ? "0" : "")+comm.toString();
      q.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, Long.valueOf(lancio));

    }else if(TAPWebCostant.CDL_FORANTEBIESSE_EDPC.equals(cdL)){
      q=new QryPzAnteForBiesseP3();
      String lancio=DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataComm);//anno.toString()+(comm<100 ? "0" : "")+comm.toString();
      q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, Long.valueOf(lancio));
    }
    
    
    if(q!=null){
      String select=q.toSQLString();
      ResultSetHelper.fillListList(conDesmosFeb, select, result);
    }
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm);
  }
    
  private List getListPzFromImaAnte(Connection con,String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche){
    
    List result=new ArrayList();
    try{
      
      QueryPzCommImaAnte qry=new QueryPzCommImaAnte();
      
      if(!StringUtils.IsEmpty(packType))
        qry.setFilter(QueryPzCommImaAnte.PACKTYPEEQ, packType);
      
      if(packType==null)
        qry.setFilter(QueryPzCommImaAnte.PACKTYPENOTEQ,"NO");
      
      qry.setFilter(QueryPzCommImaAnte.COMMISSIONNO, comm);
      
      if(dataElab!=null)
        qry.setFilter(QueryPzCommImaAnte.COMMISSIONYEAR, DateUtils.getAnno(dataElab));
      else
        qry.setFilter(QueryPzCommImaAnte.COMMISSIONDATE, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(QueryPzCommImaAnte.LINEELOG, lineeLogiche.toString());
      
      if(TAPWebCostant.CDL_RICCIOIMAANTE_EDPC.equals(cdL)){
        qry.setFilter(QueryPzCommImaAnte.CHANGELLOG, "Y");
        qry.setFilter(QueryPzCommImaAnte.ADDCOLOR,"Y");
      }  
      
      String s=qry.toSQLString();
      _logger.info(s);
      ResultSetHelper.fillListList(con, s, result);
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    
    if(comm>400 && comm<797){
      comm-=400;
    }
    else if(comm>1000 ){
      comm-=1000;
    }
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm);
  }
  
  
  private List getListPzFromLotto1(Connection con,String cdL,Long comm,Date dataComm,List lineeLogiche){
    
    List result=new ArrayList();
    try{
      
      QueryPzCommLotto1 qry=new QueryPzCommLotto1();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
//      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
//        qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche.toString());
      
        
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm);
  }
  
  
  private void saveInfoForEtkPz(PersistenceManager man,List<BeanInfoColloComForTAP> list,String pathFile,Boolean nColWithZero){
    List beansInfoEtk=new ArrayList();
    if(list==null || list.size()==0)
      return;
    
    for(BeanInfoColloComForTAP b:list){
       if(b.isPrintable()){
        BeanInfoAggColloComForTAP beanEtk=new BeanInfoAggColloComForTAP(b.getCdL(), b.getBarcode(),b.getCommessa(),b.getDataComN());

        if(pathFile!=null){
           beanEtk.setPathFile(getEtkFileNameStd(pathFile, b.getCommessa(), b.getCollo(), b.getDataComN(),nColWithZero));
        }else{
          beanEtk.setPathFile(b.getPathFile());
        }

        beansInfoEtk.add(beanEtk);
      }
    }
    
    man.storeDtFromBeans(beansInfoEtk);
  }
  
  /**
   * Si preoccupa di cancellare i dati obsoleti sugli archivi che gestiscono la tracciatura dei
   * pezzi a commessa
   * @param con Connection
   */
  private void puliziaTabelle(Connection con ,Map propsElab){
    
    //prevediamo la cancellazione una volta a settimana il sabato 
    Integer numGg=ClassMapper.classToClass(propsElab.get(NameElabs.GGDATIINLINEA),Integer.class);
    
    if(numGg==null || numGg<=0){
      addWarning("Parametro giorni dati in linea non valorizzato.Impossibile eseguire la pulizia delle tabelle");
      return;
    }
    Calendar c=new GregorianCalendar();
    c.setTime(new Date());
    if(c.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY)
      return;
    
    
    
    
    Date data=DateUtils.addDays(new Date(), - numGg);
    try {
      data=DateUtils.getFineGg(data);
      Long dataN=DateUtils.getDataForMovex(data);
   
//      String del4= " delete from  MCOBMODDTA.ZTAPCC  where 1=1 and tcdtap<="+JDBCDataMapper.objectToSQL(data);
      
      String del1= " delete from  MCOBMODDTA.ZTAPCP  where 1=1 and tpdtin<="+JDBCDataMapper.objectToSQL(data);
      
      String del2= " delete from  MCOBMODDTA.ZTAPPI  where 1=1 and txdtrf<="+JDBCDataMapper.objectToSQL(dataN);
     
      String del3= " delete from  MCOBMODDTA.ZTAPCI  where 1=1 and tidtco<="+JDBCDataMapper.objectToSQL(dataN);
      
      String del4= " delete from  MCOBMODDTA.ZTAPTI  where 1=1 and TTDTCO<="+JDBCDataMapper.objectToSQL(dataN);
      
      String del5= " delete from  MCOBMODDTA.ZTAPTP  where 1=1 and TYDTIN<="+JDBCDataMapper.objectToSQL(data);
      
      _logger.info("Pulizia dati letture --> "+ del1); 
      PreparedStatement ps=con.prepareStatement(del1);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati di commessa --> "+ del2); 
      ps=con.prepareStatement(del2);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati commessa info aggiuntive --> "+ del3); 
      ps=con.prepareStatement(del3);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
       
      _logger.info("Pulizia dati commessa info appoggio --> "+ del4); 
      ps=con.prepareStatement(del4);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati lettura colli incompleti --> "+ del5); 
      ps=con.prepareStatement(del5);
      ps.execute();
      _logger.info("Pulizia effettuata");
      
    } catch (ParseException ex) {
      _logger.error(" Errore in fase di conversione dati per pulizia tabelle --> "+ex.getMessage());
    } catch (SQLException ex) {
      addError(" Errore in fase di pulizia tabelle per esecuzione dello statement -->> "+ex.getMessage());
    }
   
  }
  
  
  
  private void loadDatiImballoAnteSpecialiImaAnteR1P1OLD(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC);
    
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    
    List lineeLogiche1=new ArrayList();
    lineeLogiche1.add("06053");
    lineeLogiche1.add("06054");
    
    // 12/04/2018 lineeLogiche1.add("06057");
    lineeLogiche1.add("06058");
    // 12/04/2018 i pezzi di questa linea a tendere non arriveranno più dalla Ima Ante ma solo dalla Lotto1
    lineeLogiche1.add("06019");
    
    
    List lineeLogiche2=new ArrayList();
//    lineeLogiche2.add("06250");
    lineeLogiche2.add("06251");
    lineeLogiche2.add("06252");
    
    for(List infoC:commToLoad){
      Long comm=(Long) infoC.get(0);
      Long commTmp=comm;
      Long dtC=(Long) infoC.get(1);
      Long dtEC=(Long) infoC.get(2);
      Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
      List beans=new ArrayList();
      Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");
      Connection conSQLS=null;
      Connection conSQLSDesmos=null;
      try{
        conSQLS=ColombiniConnections.getDbImaAnteConnection();
        conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
         //pz da Ima Ante --> prevede anche la stampa dell'etichetta
        // X Linee Interne allora solo la commessa viene maggiorata di 400
        if(comm<400)
          commTmp=comm+400;
          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee interne) per commessa "+commTmp+" - "+dtC);
          List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1);
          if(l!=null)
            beans.addAll(l);


          //ante esterne (sia  la minicommessa che per la commessa vengono maggiorate di 1000
          commTmp=comm+1000;
          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee esterne) per commessa "+commTmp+" - "+dtC);
          l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, dataElab,"", lineeLogiche2);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  

        
        
//        List colliToRemove=new ArrayList();
//        String queryC="select distinct cast(PackageNo as int) from dbo.tab_et where commissionNo="+commTmp+" and ProductionLine='06250'";
         
         List colliComm=new ArrayList();
         String queryC=" select distinct PackageNo  from dbo.tab_et where commissionNo in ( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) "+
                  " and year(CommissionDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataElab));
         
         ResultSetHelper.fillListList(conSQLS, queryC, colliComm);

        //condizione per fare in modo che la commessa venga caricata
        //tutta insieme---->  
        //potrebbe infatti capitare che la commessa non è stata ancora importata 
        //nel database della Ima Ante ma è già disponibile su Movex
        if(colliComm.size()>0 && DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC)){
          
          
//          beans=new ArrayList();
          //List lineeL=new ArrayList();
//          lineeL.add("06253");
          String cond=null;
//          
//          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,cond);
//          if(l!=null)
//            beans.addAll(l);
//
//          apm.storeDtFromBeans(beans);
//          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);

          
          beans=new ArrayList();
          List lineeL=new ArrayList();
          lineeL.add("06057");
          lineeL.add("06253");
          
          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.TRUE,cond);
          if(l!=null)
            beans.addAll(l);

          apm.storeDtFromBeans(beans);
          
          //linea pz speciali
          lineeL=new ArrayList();
          lineeL.add("06058");
          lineeL.add("06059");
          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.TRUE);
          if(l!=null && l.size()>0){
            apm.storeDtFromBeans(l);
            saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
          }  
          //pezzi da Lotto1
          lineeL=new ArrayList();
          lineeL.add("6019");
          l=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, lineeL);
          if(l!=null && l.size()>0){
            apm.storeDtFromBeans(l);
            saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
          }
        }  
      } catch(SQLException s) {
         addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
      } finally{
        if(conSQLS!=null)
          try {
            conSQLS.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
          }
        if(conSQLSDesmos!=null)
          try {
            conSQLSDesmos.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
          }
        
      }  
   }   
    
  }
 
  
   private static final Logger _logger = Logger.getLogger(ElabDatiProdCommesse.class);

 
  

  
}

