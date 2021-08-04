/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.datiComm.avanzamento.CaricoComLineaSuppl;
import colombini.datiComm.carico.DtColliCommessaStd;
import colombini.datiComm.carico.DtPezziCommessaStd;
import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.datiComm.carico.R1.DtMontImbArtec;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.AvProdCommLineaBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.model.persistence.CaricoCommLineaExtBean;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.QryCommesseElab;
import colombini.util.DatiCommUtils;
import db.ResultSetHelper;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe che si preoccupa di lanciare il calcolo delle informazioni relative alle linee lavorative
 * @author lvita
 */
public class ElabCaricoCommLinee extends ElabClass{
  
  public final static String COMMESSE="COMMESSE";
  public final static String COMMESSA="COMMESSA";
  public final static String DATAELAB="DATAELAB";
  public final static String DATACOMM="DATACOMM";
  public final static String TIPOCOMM="TIPOCOMM";
  public final static String ONLYXAVP="ONLYXAVP";
  
  public final static Integer MINICOMMT = Integer.valueOf(1);
  public final static Integer COMMT = Integer.valueOf(0);
  
  private final static Double VOLMIN=new Double(25);
  
  private List<List> commesseToElab=null;
  private Integer nComm=null;
  private Integer tipoComm=Integer.valueOf(0);
  private Date dataComm=null;
  private Date dataElab=null;
  private Boolean onlyAvp=Boolean.FALSE; //per gestire solo il caricamento dei dati di avanzamento di produzione (PZ da fare )
  private List lineeToElab=null;
  
  
  @Override
  public List<MailMessageInfoBean> getListMailsErrorMessage(Connection con) {
    String err=getMsgErrorElab();
    String warn=getMsgWarningElab();
    List mails=new ArrayList();
    if(!StringUtils.isEmpty(err) || !StringUtils.isEmpty(warn)){
      MailMessageInfoBean mailM=new MailMessageInfoBean(NameElabs.MESSAGE_CARICOCOMLINEE);
      mailM.retriveInfo(con);
      
      mailM.setText(err+" \n\n\n "+warn);
      mails.add(mailM);
      
      return mails;
    }else {
      return null;
    }
  }

  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
      
    nComm=ClassMapper.classToClass(param.get(COMMESSA),Integer.class);
    dataComm=ClassMapper.classToClass(param.get(DATACOMM),Date.class);
    tipoComm=ClassMapper.classToClass(param.get(TIPOCOMM),Integer.class);
    if(param.get(ONLYXAVP)!=null)
      onlyAvp=ClassMapper.classToClass(param.get(ONLYXAVP),Boolean.class);
    
    
    dataElab=(Date) param.get(ALuncherElabs.DATAINIELB);
    
    //parametro utilizzato per lancio da classe e non da riga di comando
    commesseToElab=(List) param.get(COMMESSE);
        
    String linee="";
    if(param.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(param.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    
    if(nComm==null || dataComm==null || tipoComm==null) {
      if(dataElab==null && commesseToElab==null){
        return Boolean.FALSE;
      }
    }
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    //se valorizzati i parametri relativi alla commessa 
      if(nComm!=null && dataComm!=null && tipoComm!=null){
        Long dtLC=DateUtils.getDataForMovex(dataComm);
        addInfo("Elaborazione per commessa: "+nComm +" data: "+dtLC );
        if(!onlyAvp){
          elabDatiCaricoComm(con,nComm, dtLC,tipoComm,lineeToElab);
        }
        prepareDatiAvComm(con, nComm, dtLC, tipoComm,lineeToElab);
      }else {
        if(commesseToElab==null || commesseToElab.isEmpty()){
          try {
            commesseToElab=getCommesseToElab(con, dataElab);
            if(commesseToElab.isEmpty()){
              addInfo("Nessuna commessa disponibile per il giorno: "+dataElab);
              return;
            }
            for(List record:commesseToElab){
              Integer commessa=ClassMapper.classToClass(record.get(0), Integer.class);
              Long dataRifC=ClassMapper.classToClass(record.get(1), Long.class);
              Integer tipo=ClassMapper.classToClass(record.get(2), Integer.class);

              if(!onlyAvp){
                elabDatiCaricoComm(con, commessa, dataRifC, tipo,lineeToElab);
              }
              prepareDatiAvComm(con, commessa, dataRifC, tipo,lineeToElab);
            }
            
          } catch (QueryException ex) {
            addError("Problemi nella ricerca delle commesse da elaborare -->"+ex.toString());
          } catch (SQLException ex) {
            addError("Problemi nella ricerca delle commesse da elaborare -->"+ex.toString());
          }
        }
      }
    }   
  
    
  private List getCommesseToElab(Connection con,Date data) throws QueryException, SQLException{
    List comm=new ArrayList();
    
    QryCommesseElab qry=new QryCommesseElab();
    Long dataN=DateUtils.getDataForMovex(data);
    qry.setFilter(FilterFieldCostantXDtProd.FT_DATA, dataN);
    qry.setFilter(FilterFieldCostantXDtProd.FT_AZIENDA, FilterFieldCostantXDtProd.AZCOLOMBINI);
    
    ResultSetHelper.fillListList(con, qry.toSQLString(), comm);
    
    
    return comm;
  }
  
  
  
  private void elabDatiCaricoComm(Connection con,Integer commessa,Long dataCommL,Integer tipo,List lineeToElab){
    if(con==null){     
      _logger.error("Attenzione connessione al database non presente . Elaborazione terminata!!");
      return; 
    }
    
    PersistenceManager man=new PersistenceManager(con);
    try {
      _logger.info(" INIZIO ELAB DATI PRODUZIONE LINEE - COMMESSA :"+commessa+ " dataRif: "+dataCommL);
      Map <String,Double> volumi=DatiCommUtils.getInstance().loadVolumiCommessa(con, commessa,dataCommL);
      Double volT=volumi.get(DatiCommUtils.VOLTOTCOMMESSA);
      //se il volume è <500 mq e non stiamo parlando di mini commessa -> non eseguiamo il calcolo.
      if(volT!=null && volT<VOLMIN && tipo<1){
        addInfo("COMMESSA N."+commessa+ " FARLOCCA . Calcolo non eseguito !!!");
        return;
      }
      
      if(tipo>1){
        addInfo("NANOCOMMESSA N."+commessa+"  da non calcolare");
        return;
      }
      
      List<LineaLavBean> linee=DatiCommUtils.getInstance().loadLineeForCaricoCom(con, commessa, dataCommL,lineeToElab);
      
      for(LineaLavBean bean:linee){
        elabDatiLinea(man, bean, volumi);
   
      }
      
    } catch (SQLException ex) {
      _logger.error("Impossibile caricare le informazioni relative alla linee da elaborare ->"+ex.toString());  
      addError("Elaborazione annullata .Problemi nel reperimento delle informazioni");
    }  catch (QueryException ex) {
      _logger.error("Impossibile caricare le informazioni relative alla linee da elaborare ->"+ex.toString());  
      addError("Elaborazione annullata .Problemi nel reperimento delle informazioni");
    } finally{
      _logger.info(" Fine Elaborazioni dati produzione linee ");
      if(man!=null)
          man=null;
    }   
      
  } 
   
  private void elabDatiLinea(PersistenceManager man,LineaLavBean bean,Map volumi){
    List<CaricoCommLineaBean> infoDt=null;
    String msg="";
    try {
      _logger.info("Elaborazione dati per linea : "+bean.getCodLineaLav());
      msg="Linea "+bean.getCodLineaLav()+" . Impossibile caricare le informazioni per la commessa  n." +bean.getCommessaString()+" in spedizione il "+bean.getDataCommessa()+" -> ";
      infoDt=getDatiCommessaLinea(man.getConnection(), bean);
      //aggiungo le informarzioni relative ai pezzi/colli per volume
      if(infoDt!=null && !infoDt.isEmpty()){
        //cancello i dati per la linea (prendo il primo elemento )
        man.deleteDtFromBean((CaricoCommLineaBean)infoDt.get(0));
        infoDt.addAll(loadDatiVolumi(infoDt,volumi));
        for(CaricoCommLineaBean b:infoDt){
          try {
            man.storeDtFromBean(b);
          } catch (SQLException ex) {
            addError(msg+ex.getMessage());
          }
        
        }
      }
      //elaborazione per ordini estero : è attiva solo per la linea 03006
      loadDatiOrdiniExt(man, bean, volumi);
      
      _logger.info("Fine elaborazione  dati per linea "+bean.getCodLineaLav());
    } catch (ClassNotFoundException ex) {
      addError(msg+ex.getMessage()+ex.toString());
    } catch (QueryException ex) {
      addError(msg+ex.getMessage()+ex.toString());
    } catch (DatiCommLineeException ex) {
      addError(msg+ex.getMessage()+ex.toString());
    } catch (SQLException ex) {
      addError(msg+ex.getMessage()+ex.toString());
    } 
  }
  
  
  
  
  
  
  
  private List<CaricoCommLineaBean> getDatiCommessaLinea(Connection con,LineaLavBean linea) throws QueryException,DatiCommLineeException,ClassNotFoundException {
    List infoDat=null;
    
    if(linea==null)
      return null;
    
    //se la linea è standard allora chiamo le classi di base 
    if(linea.isStandard()){
      if(linea.getQueryCondition()==null || linea.getQueryCondition().isEmpty())
        throw new QueryException("Info base per la linea "+linea.getCodLineaLav()+ " NON CORRETTI!! Impossibile proseguire");
      
      if((LineaLavBean.UMPEZZI).equals(linea.getUnitaMisura())){//pezzi
        DtPezziCommessaStd dtP=new DtPezziCommessaStd();
        infoDat=dtP.getDatiCommessa(con, linea);
      }else{//colli
        DtColliCommessaStd dtC=new DtColliCommessaStd();
        infoDat=dtC.getDatiCommessa(con, linea);
      }
    }else{ //gestione della classe java   
      try {
        String classeS=linea.getClassExec();
        Object classJ = Class.forName(classeS).newInstance();
        IDatiCaricoLineaComm dpc= (IDatiCaricoLineaComm) classJ;
        infoDat=dpc.getDatiCommessa(con, linea);
      } catch (InstantiationException ex) {
        throw new ClassNotFoundException("Impossibile istanziare la classe "+linea.getClassExec(), ex); 
      } catch (IllegalAccessException ex) {
        throw new ClassNotFoundException("Impossibile accedere alla classe "+linea.getClassExec(), ex); 
      }
    }
    
    return infoDat;
  }
  
  
  
 
  
  
  
 
  private List<CaricoCommLineaBean> loadDatiVolumi(List<CaricoCommLineaBean> infoDt, Map volumi) {
    List volLinea=new ArrayList();
    for(CaricoCommLineaBean lL : infoDt){
       Double vol=ClassMapper.classToClass(volumi.get(lL.getDivisione()),Double.class);
       if(vol!=null && vol>0){
         Double pzM3=lL.getValore()/vol;
       
         DatiCommUtils.getInstance().addInfoCaricoComBu(volLinea,lL.getLineaLav(),lL.getDataRifN(),lL.getCommessa(),
                                                        LineaLavBean.UMVOL, lL.getDivisione(), pzM3);
        

       }
    }
    
    return volLinea;
  }
  
  
  
  private void loadDatiOrdiniExt(PersistenceManager pm , LineaLavBean beanLinea,Map volumi){
    List<CaricoCommLineaBean> infoDtExt=null;
    if("03006".equals(beanLinea.getCodLineaLav()) ){  
      _logger.info(" Calcolo coefficiente per Ordini estero linea 03006");
      
      try {
        DtMontImbArtec dt=new DtMontImbArtec();      
        infoDtExt=dt.getDatiCommessaExt(pm.getConnection(), beanLinea);
        if(infoDtExt!=null && !infoDtExt.isEmpty() ){
          infoDtExt.addAll(loadDatiVolumi(infoDtExt,volumi));
          //trasmormo il bean nell'oggetto utile alla persistenza
          ArrayList beansNew=new ArrayList();
          for(CaricoCommLineaBean l : infoDtExt){
            beansNew.add(new CaricoCommLineaExtBean(l));            
          }
          pm.deleteDtFromBean((CaricoCommLineaExtBean)beansNew.get(0));
          pm.storeDtFromBeans(beansNew);
          
        }
    
      } catch (DatiCommLineeException ex) {
        _logger.error("Impossibile caricare i dati relativi ad ordini estero per la linea 03006 e commessa "+beanLinea.getCommessaString()+" -->"
                +ex.toString());
      } catch (SQLException ex) {
            _logger.error("Impossibile caricare i dati relativi ad ordini estero per la linea 03006 e commessa "+beanLinea.getCommessaString()+" -->"
                +ex.toString());
      
      } finally {
        _logger.info("Fine calcolo coefficiente per Ordini estero ");
      }
    }  
  } 
  
  
  
  
  public void prepareDatiAvComm(Connection con,Integer comm,Long dataRif,Integer tipo,List lineeToElab){
    _logger.info("Elaborazione dati per AVANZAMENTO PRODUZIONE --> pz da produrre");
    if(tipo>0){
      _logger.info("Elaborazione relativa non a commessa standard. Dati per avanzamento produzione nn caricati");
      return ;
    }
    PersistenceManager man=null;
    try{  
      List<LineaLavBean> lineeAvp=DatiCommUtils.getInstance().loadLineeForAvanProd(con,Boolean.FALSE ,comm, dataRif,lineeToElab);
      man=new PersistenceManager(con);

      for(LineaLavBean bean:lineeAvp){
        prepareDatiAvCommLinea(man, bean);  
      }
    
    } catch (SQLException s){
      _logger.error("Errore in fase di accesso ai dati -->"+s.toString());
      addError("  Impossibile generare i dati relativi per la gestione dell'avanzamento della produzione ");
    } catch (QueryException q){
      _logger.error("Errore in fase di accesso ai dati -->"+q.toString());
      addError("  Impossibile generare i dati relativi per la gestione dell'avanzamento della produzione ");
    }
    
  }
  
  
  /**
   * Carica i dati per la gestione dell'avanzamento produzione.
   * Come standard prende i dati appena generati . In alcuni casi esegue una classe specifica 
   * @param man
   * @param bean 
   */
  private void prepareDatiAvCommLinea(PersistenceManager  man, LineaLavBean beanLinea) {
    AvProdCommLineaBean avBean=null;
    String infoC="- Commessa n."+beanLinea.getCommessaString()+ " dataRif: "+beanLinea.getDataCommessa() ;
    _logger.info(infoC+" --> Linea :"+beanLinea.getDescLineaLav()); 
    try {
      if(beanLinea.getFlagAvP()>0 && beanLinea.getFlagL()>0){
        avBean=DatiCommUtils.getInstance().loadDatiAvCStd(man.getConnection(), beanLinea);
      }else if(beanLinea.getFlagAvP()>0 ){
      
        String classeS=beanLinea.getClassExec();
        Object classJ = Class.forName(classeS).newInstance();
        CaricoComLineaSuppl avpL= (CaricoComLineaSuppl) classJ;
        avpL.setInfoLinea(beanLinea);
        avBean=avpL.getAvProdCommessa(man.getConnection(), beanLinea);
      }
    } catch (ClassNotFoundException ex) {
      addError(" Problemi nella generazione dei dati relarivi ai pezzi da produrre per linea "+beanLinea.getCodLineaLav()+infoC+" --> "+ex.toString());
    } catch (InstantiationException ex) {
      addError(" Problemi nella generazione dei dati relarivi ai pezzi da produrre per linea "+beanLinea.getCodLineaLav()+infoC+" --> "+ex.toString()); 
    } catch (IllegalAccessException ex) {
      addError(" Problemi nella generazione dei dati relarivi ai pezzi da produrre per linea "+beanLinea.getCodLineaLav()+infoC+" --> "+ex.toString());
    } catch (DatiCommLineeException ex) {
       addError(" Problemi nella generazione dei dati relarivi ai pezzi da produrre per linea "+beanLinea.getCodLineaLav()+infoC+" --> "+ex.toString());
    } catch (SQLException ex) {
       addError(" Problemi nella generazione dei dati relarivi ai pezzi da produrre per linea "+beanLinea.getCodLineaLav()+infoC +" --> "+ex.toString());
    }  
    
    
    try{
      if(avBean!=null) {
        Integer pzC=avBean.getPzComm();
        if(man.checkExist(avBean) && pzC>0){
          _logger.info("Aggiornamento dati commessa -->pz da produrre: "+pzC);
          man.updateDt(avBean, AvProdCommLineaBean.ZATOTC, pzC);
        }else if(pzC>0){
          man.storeDtFromBean(avBean);
        }
      }else
        _logger.info("Dati relativi ai pz da produrre per commessa "+beanLinea.getCommessa()+ " e linea "+beanLinea.getCodLineaLav()+
                "  non significativi(pzcom=0)");
    }catch(SQLException s){
      String  msg = "Impossibile salvare i dati per avanzamento produzione per linea "+beanLinea.getCodLineaLav()+ " e commessa "+beanLinea.getCommessaString()+
              s.toString();
      addError(msg);
    }
  }

  
  


  private static final Logger _logger = Logger.getLogger(ElabCaricoCommLinee.class);
  
}
