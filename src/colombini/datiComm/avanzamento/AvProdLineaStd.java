/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.AvProdCommLineaBean;
import colombini.model.persistence.ProdCommLinea;
import colombini.util.DatiColliGgVDL;
import colombini.util.DatiCommUtils;
import colombini.util.DatiProdUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public abstract class AvProdLineaStd {

  private LineaLavBean infoLinea;
  private Date dataRifCalc;
  private Map parameter=null;
//  private Integer nComm=null;
//  private Date dataRifComm=null;
  
  
  public final static String LISTCOMMESSE="LISTCOMMESSE";
  public final static String DATIVDL="DATIVDL";
  
  
  public AvProdLineaStd() {
    this.parameter=new HashMap();
  }
  

  public void setInfoLinea(LineaLavBean llav) {
    this.infoLinea = llav;
  }


  public void setDataRifCalc(Date dataRifCalc) {
    this.dataRifCalc = dataRifCalc;
  }

  public void setParameter(Map parameter) {
    this.parameter = parameter;
  }

  public LineaLavBean getInfoLinea() {
    return infoLinea;
  }

  public Date getDataRifCalc() {
    return dataRifCalc;
  }

  public Map getParameter() {
    return parameter;
  }

  
  
  
  
  
  //----
  public Long getDataCommessa(Integer commessa){  
    List commesseToProduce=(List) parameter.get(LISTCOMMESSE);
    Long dataN=DatiCommUtils.getInstance().getDataCommessa(commessa, commesseToProduce);
    
    if(dataN==null){
      _logger.error("Attenzione commessa "+commessa +" non presente nella lista commesse disponibili");
      dataN=Long.valueOf(0);
    }
    
    return dataN;
  }
  
  public Date getDataForLogFile(){
    Date dataLog=getDataRifCalc();
    GregorianCalendar gc= new GregorianCalendar();
   
    gc.setTime(dataLog);
    int gg=gc.get(GregorianCalendar.DAY_OF_WEEK);
   
    if(gg==GregorianCalendar.SUNDAY){
      dataLog=DateUtils.addDays(dataLog, -2);
    }
   
    return dataLog;
  }
  
  /**
   * Metodo che torna il bean per la persistenza del dato relativo all'avanzamento di produzione di una certa linea 
   * per una data commessa
   * @param conAs400
   * @return
   * @throws DatiCommLineeException 
   */
  public AvProdCommLineaBean getAvProdCommessa(Connection conAs400) throws DatiCommLineeException {
    if(infoLinea==null || dataRifCalc==null)
      return null;
    
    if(!parameter.containsKey(LISTCOMMESSE)){
      throw new DatiCommLineeException("Lista commessa da processare vuota.Impossibile calcolare i dati di avanzamento");
    }
    
    AvProdCommLineaBean bean=new AvProdCommLineaBean(infoLinea.getAzienda(),infoLinea.getCommessa(), 
            infoLinea.getDataCommessa(), infoLinea.getCodLineaLav());
    
    //chiamata al metodo astratto che dovrebbe fornire i pzprodotti della commessa
    Integer pzProdCom=getPzProd(conAs400);
    
    bean.setPzProdComm(pzProdCom);
    
    _logger.info("Linea "+infoLinea.getCodLineaLav() +" commessa n."+infoLinea.getCommessa()+" --> pezziProdotti n."+pzProdCom);
    return bean;
    
  }
  
 
 
  /**
   * Metodo che salva su db i dati relativi ai pz prodotti di una o più commesse 
   * relative ad una certa linea in un determinato giorno lavorativo 
   * @param con
   * @param commesse
   * @throws DatiCommLineeException 
   */
  public void storeDatiProdComm(Connection con,Map commesse) throws DatiCommLineeException{
    PersistenceManager pm=null;
    try{
      pm=new PersistenceManager(con);
      if(!commesse.isEmpty()){
        Set keys=commesse.keySet();
        Iterator iter=keys.iterator();
        while(iter.hasNext()){
          Integer commessa=(Integer) iter.next();
          // modifica del 19/10/2016
//          if(commessa<360){//escludiamo le mini e le nano
            Integer pzComm=(Integer) commesse.get(commessa);
            Long dataC=getDataCommessa(commessa);
            if(dataC==null){
              _logger.error("Non trovata la data di riferimento della commessa"+commessa);
              continue;
            }
            try {
              //storeDatiProdComm(con, CostantsColomb.AZCOLOM, commessa, dataC, infoLinea.getCodLineaLav(),DateUtils.getDataForMovex(dataRifCalc),pzComm);
              ProdCommLinea bean=new ProdCommLinea(CostantsColomb.AZCOLOM, commessa, dataC, this.getInfoLinea().getCodLineaLav(), DateUtils.getDataForMovex(dataRifCalc));
              bean.setPzProdComm(pzComm);

              if(pm.checkExist(bean))
                pm.updateDt(bean);
              else
                pm.storeDtFromBean(bean);
            } catch (SQLException ex) {
              _logger.error("Errore in fase di caricamento dei pz prodotti giornalmente --> "+ex.getMessage());
              throw new DatiCommLineeException(ex);
            }
//          }
        }
      }
    } finally{
      if(pm!=null)
        pm=null;
    }
  }
  
  /**
   * 
   * @param con
   * @param comm
   * @throws DatiCommLineeException 
   */
  public void storeDatiProdComm(Connection con,List<List> comm) throws DatiCommLineeException{
    PersistenceManager pm=null;
    if(comm==null || comm.isEmpty()){
      _logger.warn(" Lista commesse vuota !! Impossibile salvare i dati");
      return;
    }
    try{
      pm=new PersistenceManager(con);
      for(List commList:comm){
          Integer commessa=ClassMapper.classToClass(commList.get(0),Integer.class);
          Long dataComm=ClassMapper.classToClass(commList.get(1),Long.class);
          Integer pzProd=ClassMapper.classToClass(commList.get(2),Integer.class);
          try {
            //storeDatiProdComm(con, CostantsColomb.AZCOLOM, commessa, dataC, infoLinea.getCodLineaLav(),DateUtils.getDataForMovex(dataRifCalc),pzComm);
            ProdCommLinea bean=new ProdCommLinea(CostantsColomb.AZCOLOM, commessa, dataComm, 
                          this.getInfoLinea().getCodLineaLav(), DateUtils.getDataForMovex(dataRifCalc));
            bean.setPzProdComm(pzProd);

            if(pm.checkExist(bean))
              pm.updateDt(bean);
            else
              pm.storeDtFromBean(bean);
          } catch (SQLException ex) {
            _logger.error("Errore in fase di caricamento dei pz prodotti giornalmente --> "+ex.getMessage());
            throw new DatiCommLineeException(ex);
          }
      }
    } finally{
      if(pm!=null)
        pm=null;
    }
  }
  
  
  
  
  /**
   * Metodo che caria nell'archivio ZDPCPR i pezzi prodotti per una certa commessa in un determinato giorno.
   * Legge le informazioni dai file csv prodotti giornalmente dal sistema vdl
   * @param con
   * @throws DatiCommLineeException 
   */
  public Map getDatiFromVDL(Connection con) throws DatiCommLineeException{
    List lineeLogiche=DatiProdUtils.getInstance().getLineeLogicheMVX(con, infoLinea.getCodLineaLav());
    
    return getDatiFromVDL(con, lineeLogiche,null);
   
  }
  
  
  /**
   * Torna una mappa la cui chiave è il numero di commessa e il valore la qta di pezzi/colli prodotti
   * @param con
   * @param lineeLogiche
   * @param bu
   * @return
   * @throws DatiCommLineeException 
   */
  public Map getDatiFromVDL(Connection con,List<String> lineeLogiche,List<String> bu ) throws DatiCommLineeException{
    Map commMap=new HashMap();
    Integer uno=Integer.valueOf(1);
    
    DatiColliGgVDL ldtc=(DatiColliGgVDL) parameter.get(DATIVDL);
    List<List> colliGg=ldtc.getListColliLineeLog4Bu(lineeLogiche,bu);
    
    if(colliGg!=null && !colliGg.isEmpty()){
      for(List infoC:colliGg){
        Integer commTmp=ClassMapper.classToClass(infoC.get(0),Integer.class);
        if(commMap.containsKey(commTmp)){
          commMap.put(commTmp, (Integer)commMap.get(commTmp)+uno);
        }else{
          commMap.put(commTmp, Integer.valueOf(1));
        }
      }
    }
    
    return commMap;
   
  }
  
  
  
  /**
   * Interroga l'archivio ZDPCPR(l'achivio contine i pz prodotti per linea , commessa in un determinato giorno) 
   * @param con
   * @param az
   * @param comm
   * @param dataComm
   * @param linea
   * @return Pz prodotti per una certa commessa della linea indicata 
   * @throws exception.DatiCommLineeException 
   */
  public Integer getPzProdComm(Connection con,Integer az,Integer comm,Long dataComm,String linea) throws DatiCommLineeException {
    Integer pz=Integer.valueOf(0);
    
    String qry= "SELECT SUM( ZPPRDC) FROM MCOBMODDTA.ZDPCPR WHERE 1=1"+
            " and ZPCONO="+JDBCDataMapper.objectToSQL(az)+
            " and ZPCOMM="+JDBCDataMapper.objectToSQL(comm)+
            " and ZPDTCO="+JDBCDataMapper.objectToSQL(dataComm)+
            " and ZPPLGR="+JDBCDataMapper.objectToSQL(linea);
    
    Object [] obj;
    try {
      obj = ResultSetHelper.SingleRowSelect(con, qry);
      if(obj[0]!=null)
        pz=ClassMapper.classToClass(obj[0], Integer.class);
    } catch (SQLException ex) {
      _logger.error("Errore in fase di lettura dei dati relativi ai pz prodotti per giorno/commessa -->"+ex.getMessage());
      throw  new DatiCommLineeException(ex);
    }
    
    
    return pz;
  }
  
  
  
  
  /**
   * Metodo che prepara i dati per gestire l'avanzamento di produzione 
   * @param conAs400
   * @throws DatiCommLineeException 
   */
  public abstract void prepareDtProd(Connection conAs400) throws DatiCommLineeException ;
  
  /**
   * Torna i pz prodotti di una certa commessa
   * @param conAs400
   * @return
   * @throws DatiCommLineeException 
   */
  protected abstract Integer getPzProd(Connection conAs400) throws DatiCommLineeException ;
 
  
  
  
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(AvProdLineaStd.class);

  
}
