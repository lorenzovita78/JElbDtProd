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
import colombini.query.produzione.F1.QryPzAnteAllumArtecInFeb;
import colombini.query.produzione.F1.QryPzAnteAllumDesmosPortale;
import colombini.query.produzione.F1.QryPzAnteAllumFeb;
import colombini.query.produzione.F1.QryPzAnteForBiesseP3;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryColliCommessaFebal;
import colombini.query.produzione.QueryColliSostFromDesmosColomFebal;
import colombini.query.produzione.QueryListColliPzCommessa;
import colombini.query.produzione.R1.QueryPzCommAntesScorrDatiProd;
import colombini.query.produzione.R1.QueryPzCommCucineR1P4;
import colombini.query.produzione.R1.QueryPzCommFornitori;
import colombini.query.produzione.R1.QueryPzCommImaAnte;
import colombini.query.produzione.R1.QueryPzCommImaTop;
import colombini.query.produzione.R1.QueryPzCommLotto1;
import colombini.query.produzione.R1.QueryPzHomagR1P1;
import colombini.query.produzione.R1.QueryPzR1P4;
import colombini.query.produzione.R1.QueryPzR1P41LSM;
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
import java.io.File;
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
public class ElabDatiProdCommesse extends ElabClass{
  
  public final static String DATAETK="$DATA$";
  public final static String COMMETK="$COMM$";
  public final static String COLLOETK="$COLLO$";
  public final static String NARTETK="$NART$";
  
    

  private Date dataRif;
 
  
  
  public ElabDatiProdCommesse(){
    super(); 
  }
  
  
  @Override
  public void exec(Connection con) {
    
    Map propsElab=getElabProperties();
    
    //copia dati su storico
    copiaDatiSuStorico(con, propsElab);
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
        loadDatiRiccioImaAnteR1P1(apm, commGg, commEx,propsElab);
        loadDatiImaTop(apm, commGg, commEx,propsElab);
        loadDatiFornitoriP2(apm, commGg, commEx,propsElab,TAPWebCostant.CDL_CASADEI_EDPC);
        loadDatiFornitoriP2(apm, commGg, commEx,propsElab,TAPWebCostant.CDL_MOROLLI_EDPC);
        
        
        loadDatiAnteAllum(apm, commGg, commEx,propsElab);
        loadDatiImbLavMisura(apm, commGg, commEx,propsElab);
        loadDatiImballoAnteSpecialiImaAnteR1P1(apm, commGg, commEx,propsElab);
        loadDatiImballoEresemR1P1(apm, commGg, commEx, propsElab);
        loadDatiForaturaAnteSpecialiR1P1(apm, commGg, commEx, propsElab);
        loadDatiAnteGolaR1P2(apm, commGg, commEx, propsElab);
        loadDatiEtichettaturaP3(apm, commGg, commEx,propsElab);
         
       
        loadDatiAnteScorrevoli(apm, TAPWebCostant.CDL_ANTESCORR_EDPC, commGg, commEx,propsElab);
        loadDatiAnteScorrevoli(apm, TAPWebCostant.CDL_ANTESSPEC_EDPC, commGg, commEx,propsElab);
        loadDatiAnteScorrevoli(apm, TAPWebCostant.CDL_ANTEQUADR_EDPC, commGg, commEx,propsElab);           
        
        loadDatiForatriceBiesseP3(apm, commGg, commEx, propsElab);

        loadDatiCtrlQualita(apm, commGg, commEx, propsElab);
        loadDatiMontaggiArtec(apm, commGg, commEx,propsElab);
        loadDatiMontaggiFebal(apm, commGg, commEx, propsElab);

        List commsR1P4=getListCommesseR1P4();
        loadDatiLotto1New(apm, commsR1P4, commEx, propsElab);
        loadDatiP4New(apm,TAPWebCostant.CDL_SKIPPERR1P4_EDPC,commsR1P4, commEx, propsElab,"(ultima_faseP4 like 'P4 SKIPPER%' or ultima_faseP4 = 'P4 FOR. HOMAG' )");      
        loadDatiP4New(apm,TAPWebCostant.CDL_SPINOMALR1P4_EDPC,commsR1P4, commEx, propsElab,"ultima_faseP4 = 'P4 SPIN.OMAL' ");
        loadDatiP4New(apm,TAPWebCostant.CDL_STEMAPASCIAR1P4_EDPC,commsR1P4, commEx, propsElab,"ultima_faseP4='P4 STEMA PASCIA' ");
        loadDatiP4New(apm,TAPWebCostant.CDL_LSMCARRP4_EDPC,commsR1P4, commEx, propsElab," (ultima_faseP4 like '%LSM%' or ultima_faseP4='?') ");

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
      Long dtTmp=ClassMapper.classToClass(commDist.get(0),Long.class); //data spedizione
      Long dtElbTmp=ClassMapper.classToClass(commDist.get(2),Long.class); //data elaborazione 
      Integer tipoC=ClassMapper.classToClass(commDist.get(3),Integer.class); //tipo Elab 
      if((commesseIn==null || commesseIn.isEmpty()) || !commesseIn.containsKey(commTmp)){
        List recTmp=new ArrayList();
        recTmp.add(commTmp);
        recTmp.add(dtTmp);
        recTmp.add(dtElbTmp);
        recTmp.add(tipoC);
        commesseOut.add(recTmp);
      }
    }
    
    
    return commesseOut;
  }
  
  public List<List> getListCommToSaveCkDate(List<List> commesse,Map commEx,String cdL){
    Map commesseIn=(Map) commEx.get(cdL);
    List commesseOut=new ArrayList();
    if( commesseIn==null || commesseIn.isEmpty() ){
      commesseOut.addAll(commesse);
    }else{
    
      for(List commDist:commesse){
        Long dtCommTmp=ClassMapper.classToClass(commDist.get(0),Long.class); //data spedizione
        Long commTmp=ClassMapper.classToClass(commDist.get(1),Long.class);
        Long dtElbTmp=ClassMapper.classToClass(commDist.get(2),Long.class); //data elaborazione 
        Integer tipoC=ClassMapper.classToClass(commDist.get(3),Integer.class); //tipo Elab

        Long dataCommCIn=(Long) commesseIn.get(commTmp);

        if( dataCommCIn==null || (commesseIn.containsKey(commTmp) && !dataCommCIn.equals(dtCommTmp)  )     
          ){
          List recTmp=new ArrayList();
          recTmp.add(dtCommTmp);
          recTmp.add(commTmp);
          recTmp.add(dtElbTmp);
          recTmp.add(tipoC);
          commesseOut.add(recTmp);

        }
      }
    }
    
    return commesseOut;
  }
  
  
  
  
  private List<List> getListCommesseR1P4(){
    Connection conDbDesmos=null;
    List<List> list=new ArrayList();
    
    try{
      String dataS=DateUtils.dateToStr(dataRif, "yyyyMMdd");
      conDbDesmos =ColombiniConnections.getDbDesmosColProdConnection();
      String qry=" select distinct DataCommessa,REPLACE(Commessa,'P','9'),datacommessa,99 from dbo.[DatiProduzione]"+ //LDL05_BASE_SIRIO_IMA "+
                 "\n where 1=1 and DataCommessa>="+JDBCDataMapper.objectToSQL(dataS)+
              " order by 1";
      
      ResultSetHelper.fillListList(conDbDesmos, qry, list);
      
      
    }catch(SQLException s){
     _logger.error("Attenzione errore in fase di connessione con DbDesmosColombini "+s.getMessage());
     addError("Attenzione errore in fase di connessione con DbDesmosColombini "+s.toString());
    }finally{
      try{
        if(conDbDesmos!=null){
          conDbDesmos.close();
        }
      } catch(SQLException s){
        _logger.error("Attenzione errore in fase di chiusura della connessione con DbDesmosColombini "+s.getMessage());
      }
    }
    return list;
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
  
  
  private List<BeanInfoColloComForTAP> getListBeansFromSCXXXCol(Connection con ,String cdL,Long comm,Date dtComm,List<String> bu,List<String> lineeLog,Boolean collo){
    
    return getListBeansFromSCXXXCol(con, cdL, comm, dtComm, bu, lineeLog, collo, null,Boolean.FALSE);
  }
  
  private List<BeanInfoColloComForTAP> getListBeansFromSCXXXCol(Connection con ,String cdL,Long comm,Date dtComm,List<String> bu,List<String> lineeLog,Boolean collo,String condition){
    
    return getListBeansFromSCXXXCol(con, cdL, comm, dtComm, bu, lineeLog, collo, condition ,Boolean.FALSE);
  }
  
  /**
   * 
   * @param con
   * @param cdL
   * @param comm
   * @param dtComm
   * @param bu
   * @param lineeLog
   * @param collo
   * @param condPers
   * @param isPrintable
   * @return 
   */
  private List<BeanInfoColloComForTAP> getListBeansFromSCXXXCol(Connection con ,String cdL,Long comm,Date dtComm,List<String> bu,List<String> lineeLog,Boolean collo,String condPers,Boolean isPrintable){
    List beans=new ArrayList();
    List<List> listPz=new ArrayList();
    String select="";

    QueryListColliPzCommessa qry=new QueryListColliPzCommessa();
    qry.setFilter(QueryListColliPzCommessa.FLTRCOMM, comm);
    if(collo)
      qry.setFilter(QueryListColliPzCommessa.FLTRCOLLO, "YES");

    if(bu!=null && bu.size()>0)
      qry.setFilter(QueryListColliPzCommessa.FLTRBUS, bu.toString());
    
    qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLog.toString());
    
    if(!StringUtils.isEmpty(condPers))
      qry.setFilter(QueryListColliPzCommessa.FLCONDPERS, condPers);
    
    try {
      select=qry.toSQLString();
      ResultSetHelper.fillListList(con, select, listPz);
      beans=getInfoColloBeansFromList(listPz, cdL, comm, dtComm,isPrintable);

    } catch (SQLException ex) {
      addError("Problemi in fase di esecuzione della query"+select.toString()+" --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError("Problemi in fase di generazione della query  --> "+ex.getMessage());
    }
      
    return beans;
  }
  
  
  
  
  /**
   * Data una lista di informazioni formattata in un certo modo ,restituisce la lista dei bean utilizzati per sa
   * @param info
   * @param cdL
   * @param comm
   * @param dtComm
   * @param withEtk indica se il bean deve poi avere un etichetta associata che sarà stampata in fase di spunta
   * @return 
   */
  private List<BeanInfoColloComForTAP> getInfoColloBeansFromList(List<List> info,String cdL,Long comm,Date dtComm,Boolean withEtk){
    List<BeanInfoColloComForTAP> beans=new ArrayList();
    
    if(info==null || info.isEmpty())
      return beans;
    
    for(List rec:info){
        Integer collo=ClassMapper.classToClass(rec.get(0), Integer.class);
        Integer nart=ClassMapper.classToClass(rec.get(1), Integer.class);
        BeanInfoColloComForTAP bean=new BeanInfoColloComForTAP(cdL, comm.intValue(), collo, nart,withEtk);
        bean.setDataComN(DateUtils.getDataForMovex(dtComm));

        String linea=ClassMapper.classToString(rec.get(2));
        //14/01/22 Modifica per antescorr - Gaston
        if(TAPWebCostant.CDL_ANTESCORR_EDPC.equals(cdL) || TAPWebCostant.CDL_ANTESSPEC_EDPC.equals(cdL) || TAPWebCostant.CDL_ANTEQUADR_EDPC.equals(cdL))
        linea=linea.replace(".", "");
        
        if(!TAPWebCostant.CDL_RICCIOIMAANTE_EDPC.equals(cdL) && linea!=null && linea.length()==4)
          linea="0"+linea;
        
        bean.setLineaLogica(linea);
        Integer bx=ClassMapper.classToClass(rec.get(3), Integer.class);
        Integer pd=ClassMapper.classToClass(rec.get(4), Integer.class);
        bean.setBox(bx==null ? Integer.valueOf(1) : bx);
        bean.setPedana(pd==null ? Integer.valueOf(1) : pd);
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
        
        //valorizzo pathfile con il codiceIdnr
        if(rec.size()>12 && rec.get(12)!=null){
          bean.setPathFile(ClassMapper.classToClass(rec.get(12),String.class));
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
    
     private String getEtkFileNameStd2(String path,Integer comm,Integer collo,Long dtCo,Boolean nColWithZero, Integer nArt){
    
      String nomeFile=path.replace(DATAETK, dtCo.toString());
      Long comml = new Long(comm);
      nomeFile=nomeFile.replace(COMMETK, DatiProdUtils.getInstance().getStringNComm(comml));
      nomeFile=nomeFile.replace(NARTETK, DatiProdUtils.getInstance().getStringNArt(nArt));


      if(nColWithZero){
        nomeFile=nomeFile.replace(COLLOETK, DatiProdUtils.getInstance().getStringNCollo(collo));
      }else{
        nomeFile=nomeFile.replace(COLLOETK, collo.toString());
      }
    
    return nomeFile;
  }
  
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  
  
  private void loadDatiForatriceRem( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
    List<List> commToLoad=getListCommToSave(commDisp, commEx, InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORANTEREM));
    Connection conSqlS=null;
    for(List infoC:commToLoad){
      
      Long comm=(Long) infoC.get(0);
      Long dtC=(Long) infoC.get(1);
    
      _logger.info("Caricamento dati Ante Rem per commessa "+comm+" - "+dtC);
      String filName= InfoMapLineeUtil.getTabuProdNameLinea(NomiLineeColomb.FORANTEREM, comm.intValue());
      String filDest=FileUtils.getNomeFileBKC(filName);
      
      try {
        _logger.info("Copia file bck....");
        FileUtils.copyFile(filName, filDest);
        conSqlS=ColombiniConnections.getDbDesmosFebalProdConnection();
        
        FileTabulatoAnteRem ft=new FileTabulatoAnteRem(filName);
        List<BeanInfoColloComForTAP> beans=ft.processFile(dtC);
        for(BeanInfoColloComForTAP b:beans ){
          if(b.getLineaLogica().contains("36020") || b.getLineaLogica().contains("36050")){
            b.loadInfoBox(apm.getConnection());
            if(b.getBox()==null || b.getBox()<=0){
              b.loadInfoBoxFebal(conSqlS);
            }
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
      } catch(SQLException s){
        addError("Errore in fase di connessione al db DesmosFebal -->"+s.getMessage());
      } finally {
        if(conSqlS!=null)
          try {
            conSqlS.close();
        } catch (SQLException ex) {
          _logger.warn("Errore in fase di chiusura della connessione con il Db Desmos Febal -->"+ex.getMessage());
        }
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
      String pathfileVediDis=(String) propsElab.get(NameElabs.PATHVDDISANTEFEB);
      
      try{
        conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();   
        for(List infoC:commToLoad){

          Long comm=(Long) infoC.get(0);
          Long dtC=(Long) infoC.get(1); 
          Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
          Integer anno=DateUtils.getAnno(dataC);
         // non esistono fuori disegno per le mini e le nano
          Boolean dirOk=Boolean.TRUE;
          File dirVd =null;
          File fileOkVd =null;
          if (comm<=360){
            dirOk=Boolean.FALSE;
            String dtDir=DateUtils.dateToStr(dataC, "dd-MM-yy");

            String pathComm=pathfileVediDis+"\\"+anno+"\\"+DatiProdUtils.getInstance().getStringNComm(comm)+"_"+dtDir;
            dirVd =new File(pathComm);
            fileOkVd =new File(pathComm+"\\ok.txt");
            dirOk=(fileOkVd.exists());
          }
          
          
          if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC) && dirOk ){
              _logger.info("Caricamento dati per Foratrice Biesse Febal  per commessa "+comm+" - "+dtC);
              List<BeanInfoColloComForTAP> beansVD=new ArrayList();
              
              List<BeanInfoColloComForTAP> beansFeb=getListBeansAnteFebal(conDesmosFeb, TAPWebCostant.CDL_FORANTEBIESSE_EDPC, comm, dataC,Boolean.FALSE);
              if(comm<=360)
                beansVD=getListBeansFebalVediDisegno(dirVd, TAPWebCostant.CDL_FORANTEBIESSE_EDPC,TAPWebCostant.CDL_MONTAGGIFEBAL_EDPC, comm, dtC);

              apm.storeDtFromBeans((List) beansFeb);
              apm.storeDtFromBeans((List) beansVD);
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
   
   private List<BeanInfoColloComForTAP> getListBeansFebalVediDisegno(File dirComm,String cdl,String linLogica,Long numComm,Long dtC) {
     List beans=new ArrayList();
     List<String> files=ArrayUtils.getListFromArray(dirComm.list());
     for(String fileTmp:files){
       
       if(fileTmp.contains(".dwg")){
         try{
          String nomeFile=fileTmp.replace(".dwg", "");
          String numOrd=nomeFile.substring(0,7);
          String numCollo=nomeFile.substring(7,12);
          String art=nomeFile.substring(12,13);
          Integer nart=Integer.valueOf(1);
          if("B".equals(art))
            nart=2;
          else if("C".equals(art))
            nart=3;
           
         
          BeanInfoColloComForTAP bean=new BeanInfoColloComForTAP(cdl, numComm.intValue(), Integer.valueOf(numCollo), nart,Boolean.FALSE);
          bean.setDataComN(dtC);

        
          bean.setLineaLogica(linLogica);
          bean.setBox(Integer.valueOf(0));
          bean.setPedana(Integer.valueOf(0));
          bean.setnOrdine(numOrd);
          bean.setRigaOrdine(Integer.valueOf(0));
          bean.setCodArticolo("VDDIS");
          bean.setDescArticolo("VEDI DISEGNO");
          bean.setDescEstesa("VEDI DISEGNO ORDINE N."+numOrd);

        //Barcode del pezzo --->NECESSARIO!!!!
        
          bean.setBarcode("FM-"+nomeFile);
          beans.add(bean);
         }catch(Exception e){
           addWarning("Attenzione errore nella generazione dei dati vediDisegno per foratrice Biesse P3 ;Commessa  "+numComm+" -->"+fileTmp);
         } 
       }
     }
     
     return beans;
   }
  
   private void loadDatiMontaggiFebal(PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     Connection conDesmosFeb=null;
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_MONTAGGIFEBAL_EDPC);
     List lineeFebal=new ArrayList();
     lineeFebal.add("06516");
     lineeFebal.add("06517"); //da togliere??
     lineeFebal.add("06518"); //da togliere?? 
     lineeFebal.add("36035");
     lineeFebal.add("36151");
     lineeFebal.add("36017");
     lineeFebal.add("36201");
     lineeFebal.add("36202");
     
     
     try{
        conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();
     
        for(List infoC:commToLoad){
          Long comm=(Long) infoC.get(0);
          Long dtC=(Long) infoC.get(1);
          Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
          if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC)){
            _logger.info("Caricamento dati  Imballo Febal  per commessa "+comm+" - "+dtC);
           
          
          List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_MONTAGGIFEBAL_EDPC, comm, dataC, null, lineeFebal, Boolean.TRUE);
          apm.storeDtFromBeans((List)beans);
          _logger.info("Caricati colli Colombini : "+beans!=null ? beans.size() : "0");
          
          List<BeanInfoColloComForTAP> beansFebal=getListBeansColliFebal(conDesmosFeb,TAPWebCostant.CDL_MONTAGGIFEBAL_EDPC, comm, dataC, lineeFebal,Boolean.FALSE);
          apm.storeDtFromBeans((List)beansFebal);
          _logger.info("Caricati colli Febal : "+beansFebal!=null ? beansFebal.size() : "0");
          
        
          }
        }
      } catch(SQLException s ){
        _logger.error(" Errore in fase di collegamento al db Desmos Febal -->"+s.getMessage()); 
      } catch(QueryException q){
        _logger.error(" Errore in fase di collegamento al db Desmos Febal -->"+q.getMessage()); 
      } finally { 
        if(conDesmosFeb!=null)
         try {
           conDesmosFeb.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db DesmosFebal --> "+ex.getMessage());
         }
   
     }
   }
   
     private void loadDatiAnteScorrevoli(PersistenceManager apm,String cdl,List commDisp,Map commEx,Map propsElab){
     Connection conDesmosCol=null;
     Connection conImaAnte=null;
     Connection conImaTop=null;
     String pathfileScorr=(String) propsElab.get(NameElabs.PATHETKANTESCORR);

     
     List<List> commToLoad=getListCommToSave(commDisp, commEx, cdl);
     List linee=new ArrayList();
     List linee2=new ArrayList();
     //Verifica CDL e dipende qual'è prendo la linea logica
     //if(!TAPWebCostant.CDL_RICCIOIMAANTE_EDPC.equals(cdl))
     switch(cdl){
         case TAPWebCostant.CDL_ANTESCORR_EDPC:
             linee.add("6050");
             linee2.add("06050");
             break;
         case TAPWebCostant.CDL_ANTEQUADR_EDPC:
             linee.add("6051");
             linee.add("6052");
             linee2.add("06051");
             linee2.add("06052");
             break;
         case TAPWebCostant.CDL_ANTESSPEC_EDPC:
             linee.add("6049");
             linee2.add("06049");
             break;
     }
         
         
     //Paso 1 -->Lotto1 - Ima Top
     //Paso 2-->Ima Ante
     //Paso 3-->DatiProduzione
     //Paso 4-->SCXXXCOL
   
     
     try{
        conDesmosCol=ColombiniConnections.getDbDesmosColProdConnection();
        conImaAnte=ColombiniConnections.getDbImaAnteConnection();
        conImaTop=ColombiniConnections.getDbImaTopConnection();

        for(List infoC:commToLoad){
          Long comm=(Long) infoC.get(0);
          Long dtC=(Long) infoC.get(1);
          Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
          String commS=DatiProdUtils.getInstance().getStringNComm(comm);

          if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(comm, dataC)){
            if(DesmosUtils.getInstance().isDatiProduzioneValorizzata(conDesmosCol,commS) && DatiCommUtils.getInstance().isImaAnteValorizzata(conImaAnte, commS) && DatiCommUtils.getInstance().isLotto1Valorizzata(conImaTop, comm)){
              _logger.info("Caricamento dati  AnteScorr  per commessa "+comm+" - "+dtC);

          //Paso 1 --> Lotto 1 Ima Top
          List<BeanInfoColloComForTAP> beans=getListPzFromImaTopScorr(cdl, comm, dataC,null, null, linee,Boolean.TRUE);
          
          //Paso 2 --> ImaAnte
          List<BeanInfoColloComForTAP> beans2=getListPzFromImaAnte(cdl, comm, dataC,null, null, linee2,Boolean.TRUE);

          //Paso 3 --> DatiProduzione Desmos
          List<BeanInfoColloComForTAP> beans3=getListPzAnteScorrDatiProd(conDesmosCol, cdl, commS, linee, dataC ,Boolean.TRUE,Boolean.FALSE);
          
          //Paso 4 --> SCxxCOL
          //List<BeanInfoColloComForTAP> beans4=getListBeansFromSCXXXCol(apm.getConnection(), cdl, comm, dataC, null, linee2, Boolean.FALSE);
          List<BeanInfoColloComForTAP> beans4=getListBeansFromSCXXXCol(apm.getConnection(), cdl, comm, dataC, null, linee2, Boolean.FALSE, null,Boolean.TRUE);
          
          //Metto insieme i beans
          beans.addAll(beans2);
          beans.addAll(beans3);
          
          Map colli=new HashMap();

          //Aggiungo redors da scxxxcol che non ci sono sulle altre tabelle
          if(beans!=null && beans.size()>0){
                for(Object b:beans){
                  colli.put(((BeanInfoColloComForTAP)b).getKeyCommColl(),"Y");
                }  
           }
          
           if(beans4!=null){
              for(Object b:beans4){
                if(!colli.containsKey(((BeanInfoColloComForTAP)b).getKeyCommColl()) )
                  beans.add((BeanInfoColloComForTAP)b);
              }
            }  
          
                    
          //Inserisco Benas
          apm.storeDtFromBeans((List)beans);
          //Inseristo etichette
          saveInfoForEtkPz(apm, beans, pathfileScorr,Boolean.FALSE);

           }
          }
        }
      } catch(SQLException s ){
        _logger.error(" Errore in fase di collegamento al db Desmos Col  -->"+s.getMessage()); 
      } finally { 
        if(conDesmosCol!=null)
         try {
           conDesmosCol.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db DesmosCol --> "+ex.getMessage());
         }
   
     }
   }
   
   
   
   private void loadDatiAnteAllum( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_ANTEALLUM_EDPC);
     Connection conDesmosFeb=null;
     Connection conDesmosCol=null;
     List linee=new ArrayList(Arrays.asList("36090,6060"));
     List lineeF=new ArrayList(Arrays.asList("06590"));
     List lineeAgg=new ArrayList(Arrays.asList("38023","36092"));
     List lineeNew=new ArrayList(Arrays.asList("36090"));
     
     
     //List linee=new ArrayList(Arrays.asList("36090,06060"));
     //List lineeAgg=new ArrayList(Arrays.asList("36092,38023"));
     //List lineeNew=new ArrayList(Arrays.asList("36090"));
     
     String pathfile=(String) propsElab.get(NameElabs.PATHETKANTEALLUM);
     String pathfileFeb=(String) propsElab.get(NameElabs.PATHETKANTEALLUMFEB);
   
     try{
       conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();  
       conDesmosCol=ColombiniConnections.getDbDesmosColProdConnection();
       for(List infoC:commToLoad){
         Long comm=(Long) infoC.get(0);
         Long dtC=(Long) infoC.get(1);
         Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
         _logger.info("Caricamento dati Postazione Imballo Ante Alluminio per commessa "+comm+" - "+dtC);

         try{  
           if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC)){
              
               
//            List<BeanInfoColloComForTAP> beansCol=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, linee, Boolean.FALSE,null,Boolean.TRUE);
//            List<BeanInfoColloComForTAP> beansFeb=getListBeansAnteFebal(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,Boolean.TRUE);
//            List<BeanInfoColloComForTAP> beansArtecFeb=getListBeansAnteArtecInFebal(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,lineeNew,Boolean.TRUE);
//            List<BeanInfoColloComForTAP> beansCol2=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, lineeAgg, Boolean.TRUE);
//            List<BeanInfoColloComForTAP> beansFebal2=getListBeansColliFebal(TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, lineeAgg,Boolean.FALSE);

             
             List<BeanInfoColloComForTAP> beansCol=getListBeansDesmosPortale(conDesmosCol, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, linee, Boolean.TRUE,Boolean.FALSE,Boolean.FALSE);
             List<BeanInfoColloComForTAP> beansFeb=getListBeansDesmosPortale(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,lineeF, Boolean.TRUE, Boolean.TRUE,Boolean.TRUE);
             List<BeanInfoColloComForTAP> beansArtecFeb=getListBeansDesmosPortale(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,lineeNew,Boolean.TRUE, Boolean.TRUE,Boolean.TRUE);
             List<BeanInfoColloComForTAP> beansCol2=getListBeansDesmosPortale(conDesmosCol, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, lineeAgg, Boolean.FALSE, Boolean.FALSE,Boolean.TRUE);
             List<BeanInfoColloComForTAP> beansFebal2=getListBeansDesmosPortale(conDesmosFeb,TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, lineeAgg,Boolean.FALSE,Boolean.TRUE,Boolean.TRUE);
             
     
             
             //pz standard Colombini  con etichetta
             apm.storeDtFromBeans((List)beansCol);
             saveInfoForEtkPz(apm, beansCol, pathfile,Boolean.FALSE);
             //pz standard Febal con etichetta
             apm.storeDtFromBeans((List)beansFeb);
             saveInfoForEtkPz(apm, beansFeb, pathfileFeb,Boolean.FALSE);
             //pz Artec in Febal con etichetta
             apm.storeDtFromBeans((List)beansArtecFeb);
             saveInfoForEtkPz(apm, beansArtecFeb, pathfile,Boolean.FALSE);
             
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
          _logger.error("Errore in fase di chiusura della connessione febal--> "+ex.getMessage());
         }
        if(conDesmosCol!=null)
         try {
           conDesmosCol.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione colombini--> "+ex.getMessage());
         }
     }      
   }  
  
   
    private void loadDatiEtichettaturaP3( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_ETICHETTATURAP3_EDPC);
     Connection conDesmosCol=null;
     
     String pathfile=(String) propsElab.get(NameElabs.PATHETKP3);
   
     try{
       conDesmosCol=ColombiniConnections.getDbDesmosColProdConnection();
       for(List infoC:commToLoad){
         Long dtC=(Long) infoC.get(1);
         Long comm=(Long) infoC.get(0);
         Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
         String commFebal =ClassMapper.classToClass(DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataC),String.class);
         String commS=DatiProdUtils.getInstance().getStringNComm(comm);
         _logger.info("Caricamento dati Cucine R1P4 per commessa "+comm+" - "+dtC);
        
         
         try{  
           if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC)){
             List<BeanInfoColloComForTAP> beansFebal=getListPzEtichettaturaP3(conDesmosCol, TAPWebCostant.CDL_ETICHETTATURAP3_EDPC, commFebal, dataC ,Boolean.TRUE,Boolean.FALSE);
             List<BeanInfoColloComForTAP> beans=getListPzEtichettaturaP3(conDesmosCol, TAPWebCostant.CDL_ETICHETTATURAP3_EDPC, commS, dataC ,Boolean.TRUE,Boolean.FALSE);


             //pz standard Febal  con etichetta
             apm.storeDtFromBeans((List)beans);
             saveInfoForEtkPz2(apm, beans, pathfile,Boolean.TRUE);
             
             //pz standard Febal  con etichetta
             apm.storeDtFromBeans((List)beansFebal);
             saveInfoForEtkPz2(apm, beansFebal, pathfile,Boolean.TRUE);
             
           }
         } catch(SQLException s){
           _logger.error("Errore in fase di interrogazione del db DesmosCol"+s.getMessage());
           addError("Errore in fase di interrogazione del db DesmosCol"+s.toString());
         } catch(QueryException s1){
           _logger.error("Errore in fase di interrogazione del db DesmosCol "+s1.getMessage());
           addError("Errore in fase di interrogazione del db DesmosCol "+s1.toString());
         }  
       } 
     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosCol"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosCol"+s.toString());
     } finally{
        if(conDesmosCol!=null)
         try {
           conDesmosCol.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione colombini--> "+ex.getMessage());
         }
     }      
   }  
   
//    private void loadDatiAnteAllumOLD( PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
//     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_ANTEALLUM_EDPC);
//     Connection conDesmosFeb=null;
//     List linee=new ArrayList(Arrays.asList("36090,06060"));
//     List lineeAgg=new ArrayList(Arrays.asList("36092,38023"));
//     List lineeNew=new ArrayList(Arrays.asList("36090"));
//     String pathfile=(String) propsElab.get(NameElabs.PATHETKANTEALLUM);
//     String pathfileFeb=(String) propsElab.get(NameElabs.PATHETKANTEALLUMFEB);
//   
//     try{
//       conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();          
//       for(List infoC:commToLoad){
//         Long comm=(Long) infoC.get(0);
//         Long dtC=(Long) infoC.get(1);
//         Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
//         _logger.info("Caricamento dati Postazione Imballo Ante Alluminio per commessa "+comm+" - "+dtC);
//
//         try{  
//           if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC)){
//             List<BeanInfoColloComForTAP> beansCol=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, linee, Boolean.FALSE,null,Boolean.TRUE);
//           
//             
//             List<BeanInfoColloComForTAP> beansFeb=getListBeansAnteFebal(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,Boolean.TRUE);
//             List<BeanInfoColloComForTAP> beansArtecFeb=getListBeansAnteArtecInFebal(conDesmosFeb, TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC,lineeNew,Boolean.TRUE);
//             
//             List<BeanInfoColloComForTAP> beansCol2=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, null, lineeAgg, Boolean.TRUE);
//             List<BeanInfoColloComForTAP> beansFebal2=getListBeansColliFebal(TAPWebCostant.CDL_ANTEALLUM_EDPC, comm, dataC, lineeAgg,Boolean.FALSE);
//             
//             
//             //pz standard Colombini  con etichetta
//             apm.storeDtFromBeans((List)beansCol);
//             saveInfoForEtkPz(apm, beansCol, pathfile,Boolean.FALSE);
//             //pz standard Febal con etichetta
//             apm.storeDtFromBeans((List)beansFeb);
//             saveInfoForEtkPz(apm, beansFeb, pathfileFeb,Boolean.FALSE);
//             //pz Artec in Febal con etichetta
//             apm.storeDtFromBeans((List)beansArtecFeb);
//             saveInfoForEtkPz(apm, beansArtecFeb, pathfile,Boolean.FALSE);
//             
//             apm.storeDtFromBeans((List)beansCol2);
//             apm.storeDtFromBeans((List)beansFebal2);
//           }
//         } catch(SQLException s){
//           _logger.error("Errore in fase di interrogazione del db DesmosFebal"+s.getMessage());
//           addError("Errore in fase di interrogazione del db DesmosFebal"+s.toString());
//         } catch(QueryException s1){
//           _logger.error("Errore in fase di interrogazione del db DesmosFebal "+s1.getMessage());
//           addError("Errore in fase di interrogazione del db DesmosFebal "+s1.toString());
//         }  
//       } 
//     } catch(SQLException s){
//       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
//       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
//     } finally{
//       if(conDesmosFeb!=null)
//         try {
//           conDesmosFeb.close();
//         } catch (SQLException ex) {
//          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
//         }
//     }      
//   }  
   
   private void loadDatiLotto1(PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_LOTTO1R1P4_EDPC);
     Connection conDbDesmos=null;
     _logger.error("Caricamento dati per Sirio Lotto 1 ");
     try{
        
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        Long commFebal =ClassMapper.classToClass(DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataC)
                ,Long.class);
        
        Integer tipo=(Integer) infoC.get(3);
        String query ="select count(*) from "+QueryPzCommLotto1.TAB_DESMOS_LOTTO1+
                      " where Commessa in ('"+JDBCDataMapper.objectToSQL(comm)+"'"+","+commFebal+" )"+
                      " and DataCommessa=CONVERT(date,'"+JDBCDataMapper.objectToSQL(dtC)+"' ) ";
        
        try{
          if(isElabDesmosFinished(conDbDesmos, comm, dataC,tipo)){
          //if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conDbDesmos, comm, dataC) ){  
            _logger.info("Commessa "+comm);
            List beans2=getListPzFromLotto1(conDbDesmos, TAPWebCostant.CDL_LOTTO1R1P4_EDPC, commFebal.toString(), dataC, null, Boolean.FALSE,Boolean.TRUE,null,Boolean.FALSE);
            apm.storeDtFromBeans(beans2);
            String commS=DatiProdUtils.getInstance().getStringNComm(comm);
            List beans=getListPzFromLotto1(conDbDesmos, TAPWebCostant.CDL_LOTTO1R1P4_EDPC, commS, dataC, null, Boolean.FALSE,Boolean.TRUE,null,Boolean.FALSE);
            apm.storeDtFromBeans(beans);
            //per commessa +400
            //List beans2=getListPzFromLotto1(conDbDesmos, TAPWebCostant.CDL_LOTTO1R1P4_EDPC, comm+400, dataC, null, Boolean.FALSE,Boolean.TRUE,null);
            //apm.storeDtFromBeans(beans2);
             
            Integer totPzLoad=beans.size();//+beans2.size();
            Integer totPzComm=Integer.valueOf(0);
            Object[] obj=ResultSetHelper.SingleRowSelect(conDbDesmos, query);
            if(obj!=null && obj.length>0){
              totPzComm=ClassMapper.classToClass(obj[0],Integer.class);
            }
            _logger.info("Commessa "+comm+" . Totale pezzi caricati "+totPzLoad+" . Pezzi commessa"+totPzComm);
            
            if(totPzLoad.intValue()>0){
              addInfo("Linea Lotto1 -  Commessa "+comm+"  -> Pezzi caricati su portale B2b( sistema tracciatura ) : "+totPzLoad);
            }
            Integer diff=totPzComm-totPzLoad;
            if(diff>0){
              addInfo("\n **** ATTENZIONE per commessa "+comm+" n. pezzi con destinazione non corretta :"+diff +" **** ");
            }
          }

        }catch(SQLException s)  {
          _logger.error("Errore di caricamento dati per Lotto 1 per la commessa "+comm+" --> "+s.getMessage());
          addError("Errore in fase di caricamento dati per Lotto 1 per la commessa "+comm+" --> "+s.toString());
        }
      }
     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
     } finally{
       if(conDbDesmos!=null)
         try {
           conDbDesmos.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
         }
     }   
      
   }
   private void loadDatiLotto1New(PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSaveCkDate(commDisp, commEx, TAPWebCostant.CDL_LOTTO1R1P4_EDPC);
     Connection conDbDesmos=null;
     String pathfile=(String) propsElab.get(NameElabs.PATHETKMAW2);
     _logger.info("Caricamento dati per Sirio Lotto 1 ");
     try{
        
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long dtC=ClassMapper.classToClass(infoC.get(0),Long.class);
        Long comm=ClassMapper.classToClass(infoC.get(1),Long.class);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        
        _logger.info("Commessa "+comm);
        String commS=DatiProdUtils.getInstance().getStringNComm(comm);
        if(commS.startsWith("9") && comm>1000){
          commS="P"+commS.substring(1);
        }
          
        List beans=getListPzFromLotto1(conDbDesmos, TAPWebCostant.CDL_LOTTO1R1P4_EDPC, commS, dataC, null, Boolean.FALSE,Boolean.TRUE,null,Boolean.TRUE);
        apm.storeDtFromBeans(beans);
      
        //  28/09/2021 carico etichette MAW2 06029
        List<BeanInfoColloComForTAP> beanEtk=getListPzFromLotto1Etk(beans);
        if(beanEtk.size()>0){
        saveInfoForEtkPz(apm, beanEtk, pathfile,Boolean.TRUE);
        }
        
        Integer totPzLoad=beans.size();//+beans2.size();
        
        if(totPzLoad.intValue()>0){
          addInfo("Linea Lotto1 -  Commessa "+comm+"  -> Pezzi caricati su portale B2b( sistema tracciatura ) : "+totPzLoad);
        }
       }  
          

     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
     } finally{
       if(conDbDesmos!=null)
         try {
           conDbDesmos.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
         }
     }   
      
   }
   
   private void loadDatiForatriciP4New(PersistenceManager apm,String cdl ,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSaveCkDate(commDisp, commEx, cdl);
     Connection conDbDesmos=null;
     try{
        
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long dtC=ClassMapper.classToClass(infoC.get(0),Long.class);
        Long comm=ClassMapper.classToClass(infoC.get(1),Long.class);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        String dest30="";
        
        if(TAPWebCostant.CDL_SKIPPERR1P4_EDPC.equals(cdl)){
          dest30="P4 SKIPPER";
        } else if(TAPWebCostant.CDL_SPINOMALR1P4_EDPC.equals(cdl)){
          dest30="P4 SPIN.OMAL";
        }else{
          dest30="P4 STEMA PASCIA";
        } 
        
        String commS=DatiProdUtils.getInstance().getStringNComm(comm);
        List beans=getListPzFromLotto1(conDbDesmos, cdl, commS, dataC, null, Boolean.FALSE,Boolean.FALSE,dest30,Boolean.TRUE);
        apm.storeDtFromBeans(beans);
                        
      }

     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
     } finally{
       if(conDbDesmos!=null)
         try {
           conDbDesmos.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
         }
     }   
      
   }
   
   
   private void loadDatiP4New(PersistenceManager apm,String cdl ,List commDisp,Map commEx,Map propsElab,String condFaseP4){
     List<List> commToLoad=getListCommToSaveCkDate(commDisp, commEx, cdl);
     Connection conDbDesmos=null;
     Connection conDbAs400=null;
     try{
        
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      conDbAs400=ColombiniConnections.getAs400ColomConnection();
      for(List infoC:commToLoad){
        Long dtC=ClassMapper.classToClass(infoC.get(0),Long.class);
        Long comm=ClassMapper.classToClass(infoC.get(1),Long.class);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
//        String dest30="";
//        
//        if(TAPWebCostant.CDL_SKIPPERR1P4_EDPC.equals(cdl)){
//          dest30="P4 SKIPPER";
//        } else if(TAPWebCostant.CDL_SPINOMALR1P4_EDPC.equals(cdl)){
//          dest30="P4 SPIN.OMAL";
//        }else{
//          dest30="P4 STEMA PASCIA";
//        } 
        
        String commS=DatiProdUtils.getInstance().getStringNComm(comm);
        List beans=getListPzR1P4New(conDbDesmos, cdl, commS, dataC, null, Boolean.FALSE,Boolean.TRUE,condFaseP4);
        apm.storeDtFromBeans(beans);
        
        //LSM --> cucine 25/10/21
        if(TAPWebCostant.CDL_LSMCARRP4_EDPC.equals(cdl)){
            List beans2=getListPzR1P4LSM(conDbAs400, cdl, commS, dataC, null, Boolean.FALSE,Boolean.TRUE,condFaseP4);
            apm.storeDtFromBeans(beans2);
        }
                        
      }

     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  al db DesmosFebal"+s.getMessage());
       addError("Errore in fase di collegamento  al db DesmosFebal"+s.toString());
     } finally{
       if(conDbDesmos!=null)
         try {
           conDbDesmos.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione --> "+ex.getMessage());
         }
     }   
      
   }
   
   
   private Boolean isElabDesmosFinished(Connection con, Long comm,Date dataC,Integer tipo) throws SQLException{
     Integer anno=DateUtils.getYear(dataC);
     if(tipo.equals(Integer.valueOf(9)))
       return DesmosUtils.getInstance().isElabPrecomDesmosColomFinished(con, comm, dataC);  
     else 
       return DesmosUtils.getInstance().isElabDesmosColombiniFinished(con, comm, dataC);
   }
   
   private List getListPreCommColombini(Connection con){
     List commList=new ArrayList();
     
     Long dataN=DateUtils.getDataForMovex(dataRif);
     String sql="SELECT P1DATE,substring(P1PROJ , 5 , 7),P1DATE , 9 from mcobmoddta.PMZ021F where P1DATE>="+dataN;
     try{
      ResultSetHelper.fillListList(con, sql, commList);
     }catch(SQLException s){
      addError("Errore in fase di lettura delle precommesse -->"+s.getMessage()); 
     }
     
     return commList;
   }
   
   
   
   
   private void loadDatiCtrlQualita(PersistenceManager apm,List commDisp,Map commEx,Map propsElab){
     List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_CQUALITA_EDPC);
     //List lineeColo=Arrays.asList(4150,4155,4190,38020,38023,38024,38025,38026,12000,12001,10005);
     //List lineeFeb=Arrays.asList(38020,38023,38024,38025,38026,12000,12001,10005);
     String linColS=(String) propsElab.get(NameElabs.LINEECOLOMCQUALEXCL);
     String linFebS=(String) propsElab.get(NameElabs.LINEECOLOMCQUALEXCL);
     List lineeColo=Arrays.asList(linColS);
     List lineeFeb=Arrays.asList(linFebS);
     
     Connection conDbDesmos=null;
     try{
        
      conDbDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        
        try{
          if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conDbDesmos, comm, dataC) 
            && DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC) ){
            List lColli=new ArrayList();
            QueryColliSostFromDesmosColomFebal qry= new QueryColliSostFromDesmosColomFebal();
            qry.setFilter(FilterQueryProdCostant.FTNUMCOMM, comm);
            qry.setFilter(FilterQueryProdCostant.FTDATACOMMN, DateUtils.getDataForMovex(dataC));
            qry.setFilter(QueryColliSostFromDesmosColomFebal.LINEETOEXCLUDECOLOM,lineeColo.toString());
            qry.setFilter(QueryColliSostFromDesmosColomFebal.LINEETOEXCLUDEFEBAL,lineeFeb.toString());
            ResultSetHelper.fillListList(conDbDesmos, qry.toSQLString(), lColli);
            
            
            List beans=getInfoColloBeansFromList(lColli, TAPWebCostant.CDL_CQUALITA_EDPC, comm, dataC,Boolean.FALSE);
            apm.storeDtFromBeans(beans);
            
          }

        }catch(SQLException s)  {
          _logger.error("Errore di caricamento dati per C.Qualita per la commessa "+comm+" --> "+s.getMessage());
          addError("Errore in fase di caricamento dati per C.Qualita per la commessa "+comm+" --> "+s.toString());
        } catch (QueryException ex) {
          _logger.error("Errore di caricamento dati per C.Qualita per la commessa "+comm+" --> "+ex.getMessage());
          addError("Errore in fase di caricamento dati per C.Qualita per la commessa "+comm+" --> "+ex.toString());
        }
      }
     } catch(SQLException s){
       _logger.error("Errore in fase di collegamento  ai db Desmos"+s.getMessage());
       addError("Errore in fase di collegamento  al db Desmos"+s.toString());
     } finally{
       if(conDbDesmos!=null)
         try {
           conDbDesmos.close();
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
        List<BeanInfoColloComForTAP> beans=getListPzFromImaAnte(TAPWebCostant.CDL_RICCIOIMAANTE_EDPC, comm, dataC,null, null, null,Boolean.FALSE);
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
  
  private void loadDatiImaTop(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_IMATOP_EDPC);
    Connection conSQLSDesmos=null;
    try{
    conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
    
     for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1); 
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC))
        {
          _logger.info("Caricamento dati Postazione Ima Tops per commessa "+comm+" - "+dtC);
          List<BeanInfoColloComForTAP> beans=getListPzFromImaTops(TAPWebCostant.CDL_IMATOP_EDPC, comm, dataC,null, null, null,Boolean.FALSE);
          apm.storeDtFromBeans((List) beans);
        }  
     
     }
     
      } catch(SQLException s) {
         addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
      }  finally{
        if(conSQLSDesmos!=null)
          try {
            conSQLSDesmos.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
          }
        
      }  
  }
   private void loadDatiFornitoriP2(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab, String cdl_fornitore) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, cdl_fornitore);
    Connection conSQLSDesmos=null;
    try{
    conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
     for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1); 
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        
        if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC))
        {
          _logger.info("Caricamento dati Postazione Fornitore per commessa "+comm+" - "+dtC);
         List<BeanInfoColloComForTAP> beans = null;

         if(TAPWebCostant.CDL_CASADEI_EDPC.equals(cdl_fornitore)) //Se non è CASADEI, è MOROLLI
           beans=getListPzFromFornitoriP2(TAPWebCostant.CDL_CASADEI_EDPC, comm, dataC,null,Boolean.FALSE);
          else
           beans=getListPzFromFornitoriP2(TAPWebCostant.CDL_MOROLLI_EDPC, comm, dataC,null,Boolean.FALSE);
          apm.storeDtFromBeans((List) beans);
         }
      }
       
    } catch(SQLException s) {
       addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
      }  
      finally{
        if(conSQLSDesmos!=null)
          try {
            conSQLSDesmos.close();
                } 
          catch (SQLException ex)
              {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
                }
          }  
  }
  
  
    
  private void loadDatiImballoAnteSpecialiImaAnteR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC);
    Connection conSQLS=null;
    Connection conSQLSDesmos=null;
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    
    List lineeLogiche1=new ArrayList();
    lineeLogiche1.add("06053");
    
    
    lineeLogiche1.add("06057");
    lineeLogiche1.add("06058");
    
    // 12/04/2018 i pezzi di questa linea a tendere non arriveranno più dalla Ima Ante ma solo dalla Lotto1
    //lineeLogiche1.add("06059");
    lineeLogiche1.add("06019");
    
    
    List lineeLogiche2=new ArrayList();
//    lineeLogiche2.add("06250");
    lineeLogiche2.add("06251");
    lineeLogiche2.add("06252");
    try{
      conSQLS=ColombiniConnections.getDbImaAnteConnection();
      conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long commTmp=comm;
        Long dtC=(Long) infoC.get(1);
        Long dtEC=(Long) infoC.get(2);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        List beans=new ArrayList();
        Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");


           //pz da Ima Ante --> prevede anche la stampa dell'etichetta
          // X Linee Interne allora solo la commessa viene maggiorata di 400

          //linea 06055
  //        List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche0);
  //          if(l!=null)
  //            beans.addAll(l);

  // $$$ MODIFICATO IL 17/08/2020
  //        if(comm<400)
  //          commTmp=comm+400;
  //          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee interne) per commessa "+commTmp+" - "+dtC);
  //          List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1,Boolean.TRUE);
  //          if(l!=null)
  //            beans.addAll(l);
  // $$$ MODIFICATO IL 17/08/2020

            //ante esterne (sia  la minicommessa che per la commessa vengono maggiorate di 1000
            //@MOD del 16/07/2020
  //          commTmp=comm+1000;
  //          _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee esterne) per commessa "+commTmp+" - "+dtC);
  //          l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, dataElab,"", lineeLogiche2,Boolean.TRUE);
  //          if(l!=null)
  //            beans.addAll(l);
  //
  // $$$ MODIFICATO IL 17/08/2020
  //          apm.storeDtFromBeans(beans);
  //          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  
  // $$$ MODIFICATO IL 17/08/2020


  //        List colliToRemove=new ArrayList();
  //        String queryC="select distinct cast(PackageNo as int) from dbo.tab_et where commissionNo="+commTmp+" and ProductionLine='06250'";

           String inCondition="( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) ";
            if(comm>365)
              inCondition="( "+comm+" )";

           List colliComm=new ArrayList();
           // Gaston10/01/2022 cambiato queryC --> dataC invece di dataElab 

           String queryC=" select distinct PackageNo  from dbo.tab_et where commissionNo in  "+inCondition+ 
                    " and year(CommissionDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataC));

           ResultSetHelper.fillListList(conSQLS, queryC, colliComm);
           String lancioD=DesmosUtils.getInstance().getLancioDesmosColombini(comm, dataC);
          //condizione per fare in modo che la commessa venga caricata
          //tutta insieme---->  
          //potrebbe infatti capitare che la commessa non è stata ancora importata 
          //nel database della Ima Ante ma è già disponibile su Movex
          if(colliComm.size()>0 && DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC)  
                  && DesmosUtils.getInstance().isElabDesmosFinish(conSQLSDesmos, lancioD, "HOMAG_Generazione_ScannerMode.dsm",Boolean.TRUE)){
            //TEST del 18/08/2020
            if(comm<400)
            commTmp=comm+400;
            _logger.info("Caricamento dati Postazione Imballo Ante Speciali Ima Ante (linee interne) per commessa "+commTmp+" - "+dtC);
            List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1,Boolean.TRUE,Boolean.FALSE);
            if(l!=null)
              beans.addAll(l);

            apm.storeDtFromBeans(beans);
            saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  
            //TEST del 18/08/2020/

            Map colli6253=new HashMap();
            List beansDesm=null;  
            //Linea 06254 con ETK HOMAG
           
            QueryPzHomagR1P1 q=new  QueryPzHomagR1P1();
            List ll2=Arrays.asList("06054","06253");  //MOD 01062021 --> aggiunta linea 6253 togliendola da queryLotto1
            q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, lancioD);
            q.setFilter(FilterFieldCostantXDtProd.FT_LINEE,ll2.toString() );
            List lH =new ArrayList();
            ResultSetHelper.fillListList(conSQLSDesmos, q.toSQLString(), lH);
            if(lH!=null){
              beansDesm=getInfoColloBeansFromList(lH, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC,Boolean.TRUE);

            
              apm.storeDtFromBeans(beansDesm);
              saveInfoForEtkPz(apm, beansDesm, pathfile,Boolean.TRUE);  
              
              if(beansDesm!=null && beansDesm.size()>0){
                for(Object b:beansDesm){
                  colli6253.put(((BeanInfoColloComForTAP)b).getKeyCommColl(),"Y");
                }  
              }
            }   
            //MOD 01062021 --> aggiunta pezzi Ante Gola con dim1>=1276
            q=new  QueryPzHomagR1P1();
            List ll3=Arrays.asList("06257","06258");  
            q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, lancioD);
            q.setFilter(FilterFieldCostantXDtProd.FT_LINEE,ll3.toString() );
            q.setFilter(QueryPzHomagR1P1.FT_DIM1_LE, 1276 );
            lH =new ArrayList();
            ResultSetHelper.fillListList(conSQLSDesmos, q.toSQLString(), lH);
            if(lH!=null){
              beansDesm=getInfoColloBeansFromList(lH, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC,Boolean.TRUE);

              apm.storeDtFromBeans(beansDesm);
              saveInfoForEtkPz(apm, beansDesm, pathfile,Boolean.TRUE);  
              
            }
            
            //linea pz speciali
            List lineeL=new ArrayList();
            lineeL.add("06058");
            lineeL.add("06255"); //mod del 15/05/2019


            l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.TRUE);
            if(l!=null && l.size()>0){
              apm.storeDtFromBeans(l);
              saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
            }

            l=new ArrayList();
            lineeL=new ArrayList();
            lineeL.add("06251"); //mod del 16/07/2020
            lineeL.add("06252"); //mod del 16/07/2020
            l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,null,Boolean.TRUE);
            if(l!=null && l.size()>0){
              apm.storeDtFromBeans(l);
              saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
            }

            //MOD 01062021 --> tolta linea 06253  queryLotto1
//            //pezzi da Lotto1
//            lineeL=new ArrayList();
//            //lineeL.add("6019");
//            //lineeL.add("6059");
//            lineeL.add("6253");
//            String commS=DatiProdUtils.getInstance().getStringNComm(comm);
//            l=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, commS, dataC, lineeL,Boolean.TRUE,Boolean.FALSE,null,Boolean.FALSE);
//            if(l!=null && l.size()>0){
//              for(Object b:l){
//                colli6253.put(((BeanInfoColloComForTAP)b).getKeyCommColl(),"Y");
//              }
//              apm.storeDtFromBeans(l);
//              saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
//            }

            // laccato da SCXXCOL        
            lineeL=new ArrayList();
            lineeL.add("06253");

            beans=new ArrayList();
            l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,null,Boolean.TRUE);
            if(l!=null){
              for(Object b:l){
                if(!colli6253.containsKey(((BeanInfoColloComForTAP)b).getKeyCommColl()) )
                  beans.add(b);
              }
              //beans.addAll(l);
            }  
            apm.storeDtFromBeans(beans);
            saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
          }
        } 
      } catch(SQLException s) {
         addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
      } catch (QueryException ex) {
        addError(" Errore in fase di esecuzione della query su dbDEsmos --> "+ex.getMessage());
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
  
  
    
  
  
  private void loadDatiImballoEresemR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_IMBERESEM_EDPC);
    Connection conSQLS=null;
    Connection conSQLSDesmos=null;
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    
//    List lineeLogiche1=new ArrayList();
//    lineeLogiche1.add("06053");
//    lineeLogiche1.add("06054");
//    
//    
//    lineeLogiche1.add("06057");
//    lineeLogiche1.add("06058");
//    
//    // 12/04/2018 i pezzi di questa linea a tendere non arriveranno più dalla Ima Ante ma solo dalla Lotto1
//    lineeLogiche1.add("06059");
//    lineeLogiche1.add("06019");
//    
//    
//    List lineeLogiche2=new ArrayList();
////    lineeLogiche2.add("06250");
//    lineeLogiche2.add("06251");
//    lineeLogiche2.add("06252");


    try{
      conSQLS=ColombiniConnections.getDbLotto1Connection();
      conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long commTmp=comm;
        Long dtC=(Long) infoC.get(1);
        Long dtEC=(Long) infoC.get(2);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        List beans=new ArrayList();
        Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");
        
        String inCondition="( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) ";
         if(comm>365)
           inCondition="( "+comm+" )";

        List colliComm=new ArrayList();
        String queryC=" select distinct Barcode  from IMAIPCNET_2210000136_Transfer.dbo.Customer_Import_Parts where Info1 = "
                 +JDBCDataMapper.objectToSQL(DatiProdUtils.getInstance().getStringNComm(comm))+ 
                 " and year(shipDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataC));

        ResultSetHelper.fillListList(conSQLS, queryC, colliComm);

       //condizione per fare in modo che la commessa venga caricata
       //tutta insieme---->  
       //potrebbe infatti capitare che la commessa non è stata ancora importata 
       //nel database della Ima Ante ma è già disponibile su Movex
       if(colliComm.size()>0 && DesmosUtils.getInstance().isElabDesmosColombiniFinishedWithCheck(conSQLSDesmos, comm, dataC) ){
         List l=new ArrayList();
//TEST del 18/08/2020
//         if(comm<400)
//         commTmp=comm+400;
//         _logger.info("Caricamento dati Postazione Imballo Eresem (linee interne) per commessa "+commTmp+" - "+dtC);
//         List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_IMBERESEM_EDPC, commTmp, dataC, null,"", lineeLogiche1,Boolean.TRUE,Boolean.FALSE);
//         if(l!=null)
//           beans.addAll(l);
//
//         apm.storeDtFromBeans(beans);
//         saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  
//         //TEST del 18/08/2020/
//
         Map colliLotto1=new HashMap();
//

         List lineeL=new ArrayList();

         //linea pz speciali
//         lineeL=new ArrayList();
//         lineeL.add("06058");
//         lineeL.add("06255"); //mod del 15/05/2019
//
//
//         l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBERESEM_EDPC, comm, dataC, null, lineeL, Boolean.TRUE);
//         if(l!=null && l.size()>0){
//           apm.storeDtFromBeans(l);
//           saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
//         }
//
//         l=new ArrayList();
//         lineeL=new ArrayList();
//         lineeL.add("06251"); //mod del 16/07/2020
//         lineeL.add("06252"); //mod del 16/07/2020
//         l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBERESEM_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,null,Boolean.TRUE);
//         if(l!=null && l.size()>0){
//           apm.storeDtFromBeans(l);
//           saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
//         }

         //pezzi da Lotto1
         lineeL=new ArrayList();
         lineeL.add("6019");
         //lineeL.add("6059");
         //lineeL.add("6253");
         String commS=DatiProdUtils.getInstance().getStringNComm(comm);
       //  l=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_IMBERESEM_EDPC, commS, dataC, lineeL,Boolean.TRUE,Boolean.FALSE,null,Boolean.FALSE);
         l=getListPzR1P4New(conSQLSDesmos, TAPWebCostant.CDL_IMBERESEM_EDPC, commS, dataC, lineeL, Boolean.TRUE, Boolean.FALSE, null);
         if(l!=null && l.size()>0){
           for(Object b:l){
             colliLotto1.put(((BeanInfoColloComForTAP)b).getKeyCommColl(),"Y");
           }
           apm.storeDtFromBeans(l);
           saveInfoForEtkPz(apm, l, pathfile,Boolean.TRUE);
         }

         // laccato da SCXXCOL        
//         lineeL=new ArrayList();
//         lineeL.add("06253");
//
//         beans=new ArrayList();
//         l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBERESEM_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,null,Boolean.TRUE);
//         if(l!=null){
//           for(Object b:l){
//             if(!colli6253.containsKey(((BeanInfoColloComForTAP)b).getKeyCommColl()) )
//               beans.add(b);
//           }
//           //beans.addAll(l);
//         }  
//         apm.storeDtFromBeans(beans);
//         saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
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
  
  private void loadDatiAnteGolaR1P2(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_ANTEGOLAP2_EDPC);
    Connection conSQLS=null;
    Connection conSQLSDesmos=null;
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTEGOLAP2);
    
    List lineeLogiche1=new ArrayList();
    lineeLogiche1.add("06257");
    lineeLogiche1.add("06258");
    
    try{
      conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();
      
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long commTmp=comm;
        Long dtC=(Long) infoC.get(1);
        Long dtEC=(Long) infoC.get(2);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        List beans=new ArrayList();
        Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");

        
        
        if(DesmosUtils.getInstance().isElabDesmosColombiniFinishedWithCheck(conSQLSDesmos, comm, dataC) ){
          //TEST del 18/08/2020
          String lancioD=DesmosUtils.getInstance().getLancioDesmosColombini(commTmp, dataC);
          QueryPzHomagR1P1 q=new  QueryPzHomagR1P1();
          
          q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, lancioD);
          q.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche1.toString());
          q.setFilter(QueryPzHomagR1P1.FT_DIM1_GT, 1276 );    
          List l =new ArrayList();
          ResultSetHelper.fillListList(conSQLSDesmos, q.toSQLString(), l);
          if(l!=null){
            beans=getInfoColloBeansFromList(l, TAPWebCostant.CDL_ANTEGOLAP2_EDPC, comm, dataC,Boolean.TRUE);
          
          apm.storeDtFromBeans(beans);
          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);  
          //TEST del 18/08/2020/
          }
          
          
          
          //pezzi da Lotto1
          
          
          // laccato da SCXXCOL        
//          lineeL=new ArrayList();
//          lineeL.add("06253");
//          
//          beans=new ArrayList();
//          l=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_IMBANTESPECIALI_EDPC, comm, dataC, null, lineeL, Boolean.FALSE,null,Boolean.TRUE);
//          if(l!=null){
//            for(Object b:l){
//              if(!colli6253.containsKey(((BeanInfoColloComForTAP)b).getKeyCommColl()) )
//                beans.add(b);
//            }
//            //beans.addAll(l);
//          }  
//          apm.storeDtFromBeans(beans);
//          saveInfoForEtkPz(apm, beans, pathfile,Boolean.TRUE);
        }  
      }
    } catch(SQLException s) {
         addError(" Errore in fase di connessione al database DESMOS Colombini --> "+s.getMessage());
      } catch (QueryException ex) {
       addError(" Errore in fase di esecuzione della query--> "+ex.getMessage());
    } finally{
        if(conSQLSDesmos!=null)
          try {
            conSQLSDesmos.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
          }
        
      }  
    
  }
  
  
  
  
  private void loadDatiForaturaAnteSpecialiR1P1(PersistenceManager apm, List<List> commGg, Map commEx,Map propsElab) {
    List<List> commToLoad=getListCommToSave(commGg, commEx, TAPWebCostant.CDL_FORANTESPECIALI_EDPC);
    
    String pathfile=(String) propsElab.get(NameElabs.PATHETKANTESPECIALI);
    String pathfile2=(String) propsElab.get(NameElabs.PATHETKANTESPECIALIHOMAG);
    // 12/04/2018 
    // per la linea 06057 alla postazione di foratura sparano il barcode Ima Ante e stampano anche l'etichetta
    //mentre alla postazione di imballaggio 
    
    
    Connection conSQLS=null;
      Connection conDesmosCol=null;
      try{
        conSQLS=ColombiniConnections.getDbImaAnteConnection();
        conDesmosCol=ColombiniConnections.getDbDesmosColProdConnection();
        
        for(List infoC:commToLoad){
          Long comm=(Long) infoC.get(0);
          Long commTmp=comm;
          Long dtC=(Long) infoC.get(1);
          Long dtEC=(Long) infoC.get(2);
          Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
          List beans=new ArrayList();
          Date dataElab=DateUtils.strToDate(dtEC.toString(), "yyyyMMdd");

          String inCondition="( "+comm+" , "+(comm+400)+" , "+(comm+1000)+" ) ";
          if(comm>365)
            inCondition="( "+comm+" )";
        
          List colliComm=new ArrayList();
          // Gaston10/01/2022 cambiato queryC --> dataC invece di dataElab 
          String queryC=" select distinct PackageNo  from dbo.tab_et where commissionNo in "+inCondition +
                    " and year(CommissionDate) = "+JDBCDataMapper.objectToSQL(DateUtils.getYear(dataC));
          
          

          ResultSetHelper.fillListList(conSQLS, queryC, colliComm);

          //condizione per fare in modo che la commessa venga caricata
          //tutta insieme---->  
          //potrebbe infatti capitare che la commessa non è stata ancora importata 
          //nel database della Ima Ante ma è già disponibile su Movex
          if(colliComm.size()>0  && DesmosUtils.getInstance().isElabDesmosColombiniFinished(comm, dataC) ){

            if(comm<400)
              commTmp=comm+400;

            

           //NEW PER HOMAG 
           beans=new ArrayList();
           List lineeLogiche1=Arrays.asList("06257","06057","06053","06054");
           _logger.info("Caricamento dati Postazione Foratura Ante Speciali (linee interne) per commessa "+commTmp+" - "+dtC);
           List l=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_FORANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1,Boolean.TRUE,Boolean.TRUE);
            if(l!=null)
              beans.addAll(l);
            
            for(Object b:beans){
                String Idnr=((BeanInfoColloComForTAP) b).getPathFile();
                String commS=DatiProdUtils.getInstance().getStringNComm(commTmp);
                String newPathFile=pathfile2+"I"+commS+Idnr+"_1.jpg";
                ((BeanInfoColloComForTAP) b).setPathFile(newPathFile);
                
            }
            apm.storeDtFromBeans(beans);
            saveInfoForEtkPz(apm, beans, null,null);

            //salvataggio dati per linea 06057 senza generazione etichette
//            beans=new ArrayList();
//             
//           lineeLogiche1=Arrays.asList("06057","06053","06054");
//            _logger.info("Caricamento dati Postazione Foratura Ante Speciali (linee interne) per commessa "+commTmp+" - "+dtC);
//            List l1=getListPzFromImaAnte(conSQLS,TAPWebCostant.CDL_FORANTESPECIALI_EDPC, commTmp, dataC, null,"", lineeLogiche1,Boolean.TRUE,Boolean.FALSE);
//            if(l1!=null)
//              beans.addAll(l1);
//
//            Map<String,String> etkPz=getMapEtkFromDesmos(conDesmosCol,comm,dataC);
//
//            for(Object b:beans){
//              String barcodeStd=((BeanInfoColloComForTAP) b).getBarcodeStd();
//              String nomeFile=etkPz.get(barcodeStd);
//              ((BeanInfoColloComForTAP) b).setPathFile(nomeFile);
//            }
//            apm.storeDtFromBeans(beans);
//            saveInfoForEtkPz(apm, beans, null,null);
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
        if(conDesmosCol!=null)
          try {
            conDesmosCol.close();
          } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione a DesmosColombini--> "+ex.getMessage());
          }
      }  
     
    
  }
  
  private Map getMapEtkFromDesmos(Connection conDesmosCol, Long comm, Date dataC) throws SQLException {
    List<List> l=new ArrayList();
    Map m=new HashMap();
    
    String lancio=DesmosUtils.getInstance().getLancioDesmosColombini(comm, dataC);
    String sql="select distinct DesmosNomeFilePDF  , DesmosPathPDF+DesmosNomeFilePDF+'.pdf' from ETK176_PDF_ETK_ANTE_SKIPPER_A6"+
               " where DesmosLancio="+JDBCDataMapper.objectToSQL(lancio);
    
    ResultSetHelper.fillListList(conDesmosCol, sql, l);
    
    for(List l1:l){
      m.put(l1.get(0),l1.get(1));
    }
    
    return m;
  }
  
  
  private void loadDatiImbLavMisura(PersistenceManager apm,List commDisp,Map commEx,Map propsElab) {
    String pathfileLavMisura=(String) propsElab.get(NameElabs.PATHETKLAVMISURA);
    List<List> commToLoad=getListCommToSave(commDisp, commEx, TAPWebCostant.CDL_LAVMISURAIMB_EDPC);
    Connection conSQLSDesmos=null;
    
    List linee=new ArrayList();
    linee.add("06100");
    linee.add("06105");
    //linea riaggiunta dopo incontro del 4/7/2016
    linee.add("06284");
    //
    //aggiunte il 22/07/2019
    linee.add("06102");
    //linee.add("36111"); MOD del 07/01/2021
//      linee.add("06074");
    linee.add("36110");
    linee.add("36109");
    linee.add("36113"); //MOD del 5/5/2021 -->  tolte dalla lista dei pz lotto 1 : solo spunta collo

    List lineeFebal=new ArrayList();
    lineeFebal.add("06104");
    lineeFebal.add("06105");

    //linea aggiunta dopo incontro del 4/7/2016
    lineeFebal.add("06106");
    //

    lineeFebal.add("38024");
    //linea aggiunta su richiesta di Simone Arduino in data 15/06/2016
    lineeFebal.add("06518");
    lineeFebal.add("36113"); //MOD del 5/5/2021 -->  tolte dalla lista dei pz lotto 1 : solo spunta collo
    lineeFebal.add("36110");//Fare debug ultima commessa
    try{
      conSQLSDesmos=ColombiniConnections.getDbDesmosColProdConnection();

    for(List infoC:commToLoad){
      Long comm=(Long) infoC.get(0);
      Long dtC=(Long) infoC.get(1);
      Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
      _logger.info("Caricamento dati Postazione Imballo Lavori su Misura per commessa "+comm+" - "+dtC);
      String lancioD=DesmosUtils.getInstance().getLancioDesmosColombini(comm, dataC);

      if(DesmosUtils.getInstance().isElabDesmosColombiniFinished(conSQLSDesmos, comm, dataC)  
          && DesmosUtils.getInstance().isElabDesmosFinish(conSQLSDesmos, lancioD, "EtichetteLotto1BSP4.dsm",Boolean.TRUE)){


        List<BeanInfoColloComForTAP> beansFebal=getListBeansColliFebal(TAPWebCostant.CDL_LAVMISURAIMB_EDPC, comm, dataC, lineeFebal,Boolean.FALSE);
        List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_LAVMISURAIMB_EDPC, comm, dataC, null, linee, Boolean.TRUE);
        if(beansFebal!=null && beansFebal.size()>0)  
          beans.addAll(beansFebal);

        apm.storeDtFromBeans((List)beans);
        String commS=DatiProdUtils.getInstance().getStringNComm(comm);
        String lancioFebal=DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataC);
                
        //List beansLt1=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_LAVMISURAIMB_EDPC, commS, dataC, Arrays.asList("36111","6059"), Boolean.TRUE, Boolean.FALSE, null, Boolean.FALSE);
        List beansLt1=getListPzR1P4New(conSQLSDesmos, TAPWebCostant.CDL_LAVMISURAIMB_EDPC, commS, dataC, Arrays.asList("36111","6059"), Boolean.TRUE, Boolean.FALSE, null);
        //List beansLt1Febal=getListPzFromLotto1(conSQLSDesmos, TAPWebCostant.CDL_LAVMISURAIMB_EDPC, lancioFebal, dataC, Arrays.asList("36111","6059"), Boolean.TRUE, Boolean.FALSE, null, Boolean.FALSE);
        List beansLt1Febal=getListPzR1P4New(conSQLSDesmos, TAPWebCostant.CDL_LAVMISURAIMB_EDPC, lancioFebal, dataC, Arrays.asList("36111","6059"), Boolean.TRUE, Boolean.FALSE, null);
        if(beansLt1Febal!=null && beansLt1Febal.size()>0)
          beansLt1.addAll(beansLt1Febal);
        
        apm.storeDtFromBeans(beansLt1);
        saveInfoForEtkPz(apm, beansLt1, pathfileLavMisura,Boolean.TRUE);
      }
    }
    }catch(SQLException s){
     _logger.error("Errore in fase di esecuzione della query --> "+s.getMessage());
     addError("Errore in fase di caricamento dei dati per Lav Misura-->"+s.getMessage());
    } finally{
      if(conSQLSDesmos!=null)
        try{
        conSQLSDesmos.close();
        }catch(SQLException s){
          _logger.error("Errore in fase di chiususra della connessione con db desmos--> "+s.getMessage());
     
        }
    }
  }     

  
  private void loadDatiMontaggiArtec(PersistenceManager apm,List commDisp,Map commEx,Map propsElab) {
    Connection conDesmosFeb=null;
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
    
    try{
      conDesmosFeb=ColombiniConnections.getDbDesmosFebalProdConnection();
        
      for(List infoC:commToLoad){
        Long comm=(Long) infoC.get(0);
        Long dtC=(Long) infoC.get(1);
        Date dataC=DateUtils.strToDate(dtC.toString(), "yyyyMMdd");
        
        if(DesmosUtils.getInstance().isElabsDesmosFebalFinish(apm.getConnection(), comm, dtC)){
          _logger.info("Caricamento dati Montaggio Artec "+comm+" - "+dtC);  
          
          List<BeanInfoColloComForTAP> beans=getListBeansFromSCXXXCol(apm.getConnection(), TAPWebCostant.CDL_MONTAGGIARTEC_EDPC, comm, dataC, null, linee, Boolean.TRUE);
          apm.storeDtFromBeans((List)beans);
          
          List<BeanInfoColloComForTAP> beansFebal=getListBeansColliFebal(conDesmosFeb,TAPWebCostant.CDL_MONTAGGIARTEC_EDPC, comm, dataC, linee,Boolean.FALSE);
          apm.storeDtFromBeans((List)beansFebal);

        }
      }
     
     } catch(QueryException q){
        _logger.error(" Errore in fase di collegamento al db Desmos Febal -->"+q.getMessage()); 
     } catch(SQLException s){
        _logger.error(" Errore in fase di collegamento al db Desmos Febal -->"+s.getMessage()); 
     } finally { 
        if(conDesmosFeb!=null)
         try {
           conDesmosFeb.close();
         } catch (SQLException ex) {
          _logger.error("Errore in fase di chiusura della connessione al db DesmosFebal --> "+ex.getMessage());
         }
     }
     
  }
 
  
  private List getListBeansColliFebal(String cdl,Long comm,Date dataC,List lineeLog,Boolean printable){
    List beans=new ArrayList();
    Connection conFebal=null;
    
    try{
      conFebal=ColombiniConnections.getDbDesmosFebalProdConnection();
      
      beans=getListBeansColliFebal(conFebal, cdl, comm, dataC, lineeLog, printable);
       
    } catch(SQLException s){
      _logger.error("Connessione non disponibile con il server "+s.getMessage());
      addError("Errore in fase di accesso al db DesmosFebal"+s.toString());
    } finally{
      try{
        if(conFebal!=null)
          conFebal.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione con Desmos Febal --> "+s.getMessage());
      }
    }
    
    return beans;
  }  
  
  private List getListBeansColliFebal(Connection conDesmosFebal,String cdl,Long comm,Date dataC,List lineeLog,Boolean printable){
    List l=new ArrayList();
    List beans=new ArrayList();
    try{
      
       QueryColliCommessaFebal qry=new QueryColliCommessaFebal();
       qry.setFilter(FilterQueryProdCostant.FTNUMCOMM, comm);
       qry.setFilter(FilterQueryProdCostant.FTDATACOMMN, DateUtils.getDataForMovex(dataC));
       qry.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLog.toString());
       
       ResultSetHelper.fillListList(conDesmosFebal, qry.toSQLString(), l);
       beans=getInfoColloBeansFromList(l, cdl, comm, dataC,printable);
       
    } catch(SQLException s){
      _logger.error("Connessione non disponibile con il server "+s.getMessage());
      addError("Errore in fase di accesso al db DesmosFebal"+s.toString());
    } catch (QueryException ex) {
      _logger.error("Errore in fase di lettura dei dati sul server :"+ex.getMessage());
      addError("Errore in fase di lettura dei dati per la commessa Febal :"+ex.toString());
    }
    return beans;
  }  
  
  
  
  private List getListPzFromImaAnte(String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk){
    Connection con=null;
    List result=new ArrayList();
    try{
      con=ColombiniConnections.getDbImaAnteConnection();
      result=getListPzFromImaAnte(con, cdL, comm, dataComm, dataElab, packType, lineeLogiche,withEtk,Boolean.FALSE);
      
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
  
  
  
    private List getListPzFromImaTops(String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk){
    Connection con=null;
    List result=new ArrayList();
    try{
      con=ColombiniConnections.getDbImaTopConnection();
      result=getListPzFromImaTops(con, cdL, comm, dataComm, dataElab, packType, lineeLogiche,withEtk);
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
    
   private List getListPzFromImaTopScorr(String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk){
    Connection con=null;
    List result=new ArrayList();
    try{
      con=ColombiniConnections.getDbImaTopConnection();
      result=getListPzFromImaTopScorr(con, cdL, comm, dataComm, dataElab, packType, lineeLogiche,withEtk);
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
      
    private List getListPzFromFornitoriP2(String cdL,Long comm,Date dataComm,Date dataElab,Boolean withEtk){
    Connection con=null;
    List result=new ArrayList();
    try{
      con=ColombiniConnections.getDbDesmosColProdConnection();
      result=getListPzFromFornitori(con, cdL, comm, dataComm, dataElab,withEtk);
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Colombini --> "+s.getMessage());
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
        
  private List<BeanInfoColloComForTAP> getListBeansAnteFebal(Connection conDesmosFeb, String cdL, Long comm, Date dataComm,Boolean withEtk) throws QueryException, SQLException {
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
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
  
  private List<BeanInfoColloComForTAP> getListBeansDesmosPortale(Connection conDesmos, String cdL, Long comm, Date dataComm, List lineeLogiche,Boolean withEtk, Boolean isFebal, Boolean numArt1) throws QueryException, SQLException {
    CustomQuery q=null;
    List result=new ArrayList();
    if(TAPWebCostant.CDL_ANTEALLUM_EDPC.equals(cdL)){
      q=new QryPzAnteAllumDesmosPortale();
      //Integer anno=DateUtils.getYear(dataComm);
      String lancio=DesmosUtils.getInstance().getLancioDesmosColombini(comm, dataComm);

      if(isFebal){lancio=DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataComm);}
      //anno.toString()+(comm<100 ? "0" : "")+comm.toString();
      q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, lancio);
      q.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLogiche.toString());
      if(isFebal){q.setFilter(QryPzAnteAllumDesmosPortale.isFebal, isFebal);}
      if(numArt1){q.setFilter(QryPzAnteAllumDesmosPortale.numArt1, numArt1);}
    }
    
    
    if(q!=null){
      String select=q.toSQLString();
      ResultSetHelper.fillListList(conDesmos, select, result);
    }
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
    
  
  private List<BeanInfoColloComForTAP> getListBeansAnteArtecInFebal(Connection conDesmosFeb, String cdL, Long comm, Date dataComm,List lineeLog,Boolean withEtk) throws QueryException, SQLException {
    CustomQuery q=null;
    List result=new ArrayList();
    if(TAPWebCostant.CDL_ANTEALLUM_EDPC.equals(cdL)){
      q=new QryPzAnteAllumArtecInFeb();
      //Integer anno=DateUtils.getYear(dataComm);
      String lancio=DesmosUtils.getInstance().getLancioDesmosFebal(comm, dataComm);//anno.toString()+(comm<100 ? "0" : "")+comm.toString();
      q.setFilter(FilterFieldCostantXDtProd.FT_LANCIO_DESMOS, lancio);
      q.setFilter(FilterQueryProdCostant.FTLINEELAV, lineeLog.toString());

    }
    
    if(q!=null){
      String select=q.toSQLString();
      ResultSetHelper.fillListList(conDesmosFeb, select, result);
    }
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
  
  
  private List getListPzFromImaAnte(Connection con,String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk,Boolean idNr4Brc){
    
    List result=new ArrayList();
    try{
      
      QueryPzCommImaAnte qry=new QueryPzCommImaAnte();
      
      if(!StringUtils.IsEmpty(packType))
        qry.setFilter(QueryPzCommImaAnte.PACKTYPEEQ, packType);
      
//      if(packType==null)
//        qry.setFilter(QueryPzCommImaAnte.PACKTYPENOTEQ,"NO");
      
      qry.setFilter(QueryPzCommImaAnte.COMMISSIONNO, comm);
      
      if(dataElab!=null)
        qry.setFilter(QueryPzCommImaAnte.COMMISSIONYEAR, DateUtils.getAnno(dataElab));
      else
        qry.setFilter(QueryPzCommImaAnte.COMMISSIONDATE, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(QueryPzCommImaAnte.LINEELOG, lineeLogiche.toString());
      
      if(TAPWebCostant.CDL_RICCIOIMAANTE_EDPC.equals(cdL)){
//        qry.setFilter(QueryPzCommImaAnte.CHANGELLOG, "Y");
//        qry.setFilter(QueryPzCommImaAnte.ADDCOLOR,"Y");
//        qry.setFilter(QueryPzCommImaAnte.PACKTYPENOTEQ,"NO");
          qry.setFilter(QueryPzCommImaAnte.ISRICCIO,"YES");
      }  
      
      
      if(idNr4Brc!=null && idNr4Brc){
        qry.setFilter(QueryPzCommImaAnte.SOSTBARCODEIDNR, "Y");
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
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
  
   private List getListPzFromImaTops(Connection con,String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk){

    List result=new ArrayList();
    try{
      
      QueryPzCommImaTop qry=new QueryPzCommImaTop();
      
      if(!StringUtils.IsEmpty(packType))
        qry.setFilter(QueryPzCommImaTop.PACKTYPEEQ, packType);

      qry.setFilter(QueryPzCommImaTop.COMMISSIONNO, comm);
      
      if(dataElab!=null)
        qry.setFilter(QueryPzCommImaTop.COMMISSIONYEAR, DateUtils.getAnno(dataElab));
      else
        qry.setFilter(QueryPzCommImaTop.COMMISSIONDATE, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(QueryPzCommImaTop.LINEELOG, lineeLogiche.toString());
      
      String s=qry.toSQLString();
      _logger.info(s);
      ResultSetHelper.fillListList(con, s, result);
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database ImaTop--> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
   
   private List getListPzFromImaTopScorr(Connection con,String cdL,Long comm,Date dataComm,Date dataElab,String packType,List lineeLogiche,Boolean withEtk){

    List result=new ArrayList();
    try{
      
      QueryPzCommImaTop qry=new QueryPzCommImaTop();
      
      if(!StringUtils.IsEmpty(packType))
      qry.setFilter(QueryPzCommImaTop.PACKTYPEEQ, packType);

      qry.setFilter(QueryPzCommImaTop.COMMISSIONNO, comm);
      
      if(dataElab!=null)
        qry.setFilter(QueryPzCommImaTop.COMMISSIONYEAR, DateUtils.getAnno(dataElab));
      else
        qry.setFilter(QueryPzCommImaTop.COMMISSIONDATE, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(QueryPzCommImaTop.LINEELOG, lineeLogiche.toString());
      
      if(TAPWebCostant.CDL_ANTESCORR_EDPC.equals(cdL) || TAPWebCostant.CDL_ANTEQUADR_EDPC.equals(cdL) || TAPWebCostant.CDL_ANTESSPEC_EDPC.equals(cdL))
        qry.setFilter(QueryPzCommImaTop.ISSCORR,"YES");
       
       
      String s=qry.toSQLString();
      _logger.info(s);
      ResultSetHelper.fillListList(con, s, result);
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database ImaTop--> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
   
   private List getListPzFromFornitori(Connection con,String cdL,Long comm,Date dataComm,Date dataElab,Boolean withEtk){

    List result=new ArrayList();
    try{
      
      QueryPzCommFornitori qry=new QueryPzCommFornitori();


      qry.setFilter(QueryPzCommFornitori.COMMISSIONNO, comm);
      
      qry.setFilter(QueryPzCommFornitori.FLUSSO, "P2");
      
      
      if(TAPWebCostant.CDL_CASADEI_EDPC.equals(cdL)) //Se non è CASADEI, è MOROLLI
         qry.setFilter(QueryPzCommFornitori.FORNITORE, TAPWebCostant.FRN_CASADEI_EDPC);
      else
         qry.setFilter(QueryPzCommFornitori.FORNITORE, TAPWebCostant.FRN_MOROLLI_EDPC);
      
      if(dataElab!=null)
        qry.setFilter(QueryPzCommFornitori.COMMISSIONYEAR, DateUtils.getAnno(dataElab));
      else
        qry.setFilter(QueryPzCommFornitori.COMMISSIONDATE, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      
     
      
      String s=qry.toSQLString();
      _logger.info(s);
      ResultSetHelper.fillListList(con, s, result);
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Colombini--> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    
    
    
    return getInfoColloBeansFromList(result, cdL, comm, dataComm,withEtk);
  }
  
   
  private List getListPzFromLotto1(Connection con,String cdL,String comm,Date dataComm,List lineeLogiche,Boolean withEtk,Boolean isForLotto1,String fase30,Boolean nComm4P4){
    
    List result=new ArrayList();
    try{
      
      QueryPzCommLotto1 qry=new QueryPzCommLotto1();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
      
      //qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyy-MM-dd"));
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyyMMdd"));
      if(isForLotto1)
        qry.setFilter(QueryPzCommLotto1.FT_FORLINEA_LOTTO1,Boolean.TRUE);
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche.toString());
      
      if(!StringUtils.isEmpty(fase30))
        qry.setFilter(QueryPzCommLotto1.FT_DESCFS30_LIKE, fase30);
      
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
      
      
      
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Ima --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    

    //per convertire comessa Febal in numerazione Colombini
    if(comm.length()==7 && !nComm4P4){
      String scomm=comm.toString().substring(4, 7);
      comm=(scomm);
    }
    if(comm.startsWith("P") && nComm4P4){
      comm=comm.replace("P", "9");
    }
//    if(comm>400 && comm<797){
//      comm-=400;
//    }
    
    return getInfoColloBeansFromList(result, cdL, Long.valueOf(comm), dataComm,withEtk);
  }
  
  
  private List getListPzFromLotto1Etk (List<BeanInfoColloComForTAP> beans){

     List<BeanInfoColloComForTAP> result=new ArrayList(); 
      
    if(beans!=null && beans.size()>0){
                for(Object b:beans){
                    if(((BeanInfoColloComForTAP)b).getLineaLogica().contains("P1FIMAW2")){
                      ((BeanInfoColloComForTAP)b).setEtkPresent(Boolean.TRUE);
                      result.add((BeanInfoColloComForTAP)b);
                    }
                }  
              }
    return result;
  }
  

  
  private List getListPzR1P4New(Connection con,String cdL,String comm,Date dataComm,List lineeLogiche,Boolean withEtk,Boolean nComm4P4,String ultimaFaseCond){
    
    List result=new ArrayList();
    try{

        QueryPzR1P4 qry=new QueryPzR1P4();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
       
    
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyyMMdd"));
      if(ultimaFaseCond!=null && !ultimaFaseCond.isEmpty())
        qry.setFilter(QueryPzR1P4.FT_ULTIMAFASEP4, ultimaFaseCond);
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche.toString());
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
     
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    

    //per convertire comessa Febal in numerazione Colombini
    if(comm.length()==7 && !nComm4P4){
      String scomm=comm.toString().substring(4, 7);
      comm=(scomm);
    }
    if(comm.startsWith("P") && nComm4P4){
      comm=comm.replace("P", "9");
    }
//    if(comm>400 && comm<797){
//      comm-=400;
//    }
    
    return getInfoColloBeansFromList(result, cdL, Long.valueOf(comm), dataComm,withEtk);
  }
  
  private List getListPzEtichettaturaP3(Connection con,String cdL,String comm,Date dataComm,Boolean withEtk,Boolean nComm4P4){
    
    List result=new ArrayList();
    try{

      QueryPzCommCucineR1P4 qry=new QueryPzCommCucineR1P4();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyyMMdd"));
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
     
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    

    //per convertire comessa Febal in numerazione Colombini
    if(comm.length()==7 && !nComm4P4){
      String scomm=comm.toString().substring(4, 7);
      comm=(scomm);
    }
    if(comm.startsWith("P") && nComm4P4){
      comm=comm.replace("P", "9");
    }
//    if(comm>400 && comm<797){
//      comm-=400;
//    }
    
    return getInfoColloBeansFromList(result, cdL, Long.valueOf(comm), dataComm,withEtk);
  }
  
  private List getListPzAnteScorrDatiProd(Connection con,String cdL,String comm,List lineeLogiche,Date dataComm,Boolean withEtk,Boolean nComm4P4){
    
    List result=new ArrayList();
    try{

      QueryPzCommAntesScorrDatiProd qry=new QueryPzCommAntesScorrDatiProd();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche.toString());
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyyMMdd"));
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
     
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    

    //per convertire comessa Febal in numerazione Colombini
    if(comm.length()==7 && !nComm4P4){
      String scomm=comm.toString().substring(4, 7);
      comm=(scomm);
    }
    if(comm.startsWith("P") && nComm4P4){
      comm=comm.replace("P", "9");
    }
//    if(comm>400 && comm<797){
//      comm-=400;
//    }
    
    return getInfoColloBeansFromList(result, cdL, Long.valueOf(comm), dataComm,withEtk);
  }
 
    private List getListPzR1P4LSM(Connection con,String cdL,String comm,Date dataComm,List lineeLogiche,Boolean withEtk,Boolean nComm4P4,String ultimaFaseCond){
    
    List result=new ArrayList();
    try{
      
      QueryPzR1P41LSM qry=new QueryPzR1P41LSM();
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_NUMCOMM, comm);
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, DateUtils.DateToStr(dataComm, "yyyyMMdd"));
      if(ultimaFaseCond!=null && !ultimaFaseCond.isEmpty())
        qry.setFilter(QueryPzR1P4.FT_ULTIMAFASEP4, ultimaFaseCond);
      
      if(lineeLogiche!=null && !lineeLogiche.isEmpty())
        qry.setFilter(FilterFieldCostantXDtProd.FT_LINEE, lineeLogiche.toString());
      
      ResultSetHelper.fillListList(con, qry.toSQLString(), result);
     
    }catch(SQLException s){
      addError(" Errore in fase di connessione al database Desmos --> "+s.getMessage());
    } catch (ParseException ex) {
      addError(" Errore in fase di conversione della data commessa --> "+ex.getMessage());
    } catch (QueryException ex) {
      addError(" Errore in fase di esecuzione della query  --> "+ex.getMessage());
    }
    

    //per convertire comessa Febal in numerazione Colombini
    if(comm.length()==7 && !nComm4P4){
      String scomm=comm.toString().substring(4, 7);
      comm=(scomm);
    }
    if(comm.startsWith("P") && nComm4P4){
      comm=comm.replace("P", "9");
    }
//    if(comm>400 && comm<797){
//      comm-=400;
//    }
    
    return getInfoColloBeansFromList(result, cdL, Long.valueOf(comm), dataComm,withEtk);
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
  
  private void saveInfoForEtkPz2(PersistenceManager man,List<BeanInfoColloComForTAP> list,String pathFile,Boolean nColWithZero){
    List beansInfoEtk=new ArrayList();
    if(list==null || list.size()==0)
      return;
    
    for(BeanInfoColloComForTAP b:list){
       if(b.isPrintable()){
        BeanInfoAggColloComForTAP beanEtk=new BeanInfoAggColloComForTAP(b.getCdL(), b.getBarcode(),b.getCommessa(),b.getDataComN());

        if(pathFile!=null){
           beanEtk.setPathFile(getEtkFileNameStd2(pathFile, b.getCommessa(), b.getCollo(), b.getDataComN(),nColWithZero,b.getRigaOrdine()));
        }else{
          beanEtk.setPathFile(b.getPathFile());
        }

        beansInfoEtk.add(beanEtk);
      }
    }
    
    man.storeDtFromBeans(beansInfoEtk);
  }
  
  private void copiaDatiSuStorico(Connection con ,Map propsElab){

    Calendar c=new GregorianCalendar();
    c.setTime(new Date());
    if(c.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY)
      return;
   
    try {
      StringBuilder qry=new StringBuilder(
              "INSERT INTO mcobmoddta.ztapcis0 \n ").append(
              " select A.*  from mcobmoddta.ztapci A \n").append(
                            " LEFT OUTER JOIN mcobmoddta.ztapciS0 B \n").append(
                            " ON A.TICONO=B.TICONO AND A.TIPLGR=B.TIPLGR AND A.TIBARP=B.TIBARP ").append(
                            " AND A.TICOMM=B.TICOMM AND A.TIDTCO=B.TIDTCO \n").append(
              " WHERE A.TIPLGR='CQUALITA' \n").append(
              " AND B.TIBARP IS NULL ");
                      
      String stmQry=qry.toString();
      _logger.info("Copia dati ZTAPCI per CQUALITA --> "+ stmQry); 
      PreparedStatement ps=con.prepareStatement(stmQry);
      ps.execute();
      _logger.info("Copia dati effettuata");         
    
    
    } catch (SQLException ex) {
      addError(" Errore in fase di copia dei dati di storico -->> "+ex.getMessage());
    }  
      
      
   
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
    Date dataNano=DateUtils.addDays(new Date(), - 28);
    
    try {
      data=DateUtils.getFineGg(data);
      Long dataN=DateUtils.getDataForMovex(data);
      Long dataNNano=DateUtils.getDataForMovex(dataNano);
//      String del4= " delete from  MCOBMODDTA.ZTAPCC  where 1=1 and tcdtap<="+JDBCDataMapper.objectToSQL(data);
      
      String del1= " delete from  MCOBMODDTA.ZTAPCP  where 1=1 and tpdtin<="+JDBCDataMapper.objectToSQL(data);
      
      String del2= " delete from  MCOBMODDTA.ZTAPPI  where 1=1 and txdtrf<="+JDBCDataMapper.objectToSQL(dataN);
     
      String del3= " delete from  MCOBMODDTA.ZTAPCI  where 1=1 and tidtco<="+JDBCDataMapper.objectToSQL(dataN);
      
      //cancellazione per nanocommesse
      
      String del2a= " delete from  MCOBMODDTA.ZTAPPI  where 1=1 and txcomm>=361 and txcomm<=391 and txdtrf<="+JDBCDataMapper.objectToSQL(dataNNano);
     
      String del3a= " delete from  MCOBMODDTA.ZTAPCI  where 1=1 and ticomm>=361 and ticomm<=391 and tidtco<="+JDBCDataMapper.objectToSQL(dataNNano);
      
      // 
      
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
      
      _logger.info("Pulizia dati nano commessa --> "+ del2a); 
      ps=con.prepareStatement(del2a);
      ps.execute();
      _logger.info("Pulizia effettuata"); 
      
      _logger.info("Pulizia dati nano commessa info aggiuntive --> "+ del3a); 
      ps=con.prepareStatement(del3a);
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
  
  
  

   private static final Logger _logger = Logger.getLogger(ElabDatiProdCommesse.class);


  
}

