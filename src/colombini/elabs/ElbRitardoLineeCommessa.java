/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.RitardoCommLineaBean;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import elabObj.ElabClass;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.ArrayListSorted;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElbRitardoLineeCommessa extends ElabClass {

  
  private Integer numCommesse=Integer.valueOf(5);
  
  @Override
  public Boolean configParams() {
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    PersistenceManager pm=null;
    Long dtRifN=DateUtils.getDataForMovex(new Date());
    List infoL;
    try {
      infoL = getListAvanCommLinee(con, dtRifN);
      List beans=loadRitardoLinee(dtRifN,infoL);
      
      if(beans.size()>0){
        pm=new PersistenceManager(con);
        pm.deleteDtFromBean(new RitardoCommLineaBean(CostantsColomb.AZCOLOM, "", dtRifN, Integer.valueOf(0), numCommesse));
        pm.storeDtFromBeans(beans);
      }
    } catch (SQLException ex) {
      _logger.error("Problemi in fase di salvataggio dei dati relativi ai ritardi di commessa -->"+ex.getMessage());
      addError("Problemi in fase di salvataggio dei dati relativi ai ritardi di commessa -- "+ex.toString());
    } finally{
      if(pm!=null)
        pm=null;
    }
    
  }
  
  /**
   * Torna una lista contenente i residui per commessa di ciascuna linea lavorativa
   * @param con
   * @return 
   */
  public List getListAvanCommLinee(Connection con,Long dtrn) {
    List l=new ArrayList();
    try {
      String q =" Select trim(ZAPLGR), ZADTCO, ZACOMM,(ZATOTC-ZAPRDC) as residuo,ZLALLC from MCOBMODDTA.ZDPCAV "+
                                      " inner join MCOBMODDTA.ZDPLLC on  ZACONO=ZLCONO and ZAPLGR=ZLPLGR "+
                " where 1=1 and zacono=30 and zlflap=1 "+
                " and zadtco>="+JDBCDataMapper.objectToSQL(dtrn)+
                " order by ZAPLGR,ZADTCO ";

      ResultSetHelper.fillListList(con, q, l);
    } catch (SQLException ex) {
      _logger.error("Impossibile leggere i dati relativi all'avanzamento di commessa -->"+ex.getMessage());
      addError("Impossibile leggere le informazioni relative all'avanzamento di produzione delle linee");
    }
    
    return l;
  }

  private List<RitardoCommLineaBean> loadRitardoLinee(Long dataL,List<List> infoL) {
    List beans=new ArrayList();
    
    if(infoL==null|| infoL.isEmpty())
      return beans;
    
    String cdlOld="";
    RitardoCommLineaBean btemp=null;
    List<Integer> commesse=getListCommesseOrd(infoL);
    Integer countC;
    for(int i=0; i<infoL.size(); i++){
      List record=infoL.get(i);
      countC=null;
      String cdl=ClassMapper.classToString(record.get(0));
      Integer commTmp=ClassMapper.classToClass(record.get(2),Integer.class);
      Integer resTmp=ClassMapper.classToClass(record.get(3),Integer.class);
      Integer allComm=ClassMapper.classToClass(record.get(4),Integer.class);
      
      if(!cdlOld.equals(cdl) ){
        if(btemp!=null && i>1){
          beans.add(btemp);
        }
//        Double oreGg=ClassMapper.classToClass(record.get(1),Double.class);
//        Double prodOraria=ClassMapper.classToClass(record.get(2),Double.class);   
        btemp=new RitardoCommLineaBean(CostantsColomb.AZCOLOM,cdl, dataL, allComm, numCommesse);
//        countC=0;
      }
      
      countC=commesse.indexOf(commTmp);
      if(countC<0){
        addError("Commessa "+commTmp+ " non presente in lista per linea "+cdl);
      }else{
        btemp.addResiduoComm(countC, resTmp);
      }
      cdlOld=cdl;
    }
    
    if(btemp!=null){
      beans.add(btemp);
    }
    
    //aggiungo una riga con i dati relativi ai numeri di commessa
    if(beans.size()>0){
      btemp=new RitardoCommLineaBean(CostantsColomb.AZCOLOM,"", dataL, Integer.valueOf(0), numCommesse);
      int j=0;
      for(Integer comm:commesse){
        btemp.addResiduoComm(j, comm);
        j++;
      }
      beans.add(0,btemp);
    }
    
    return beans;  
  }
  
  
  
  private List getListCommesseOrd(List<List> infoL){
    List<Integer> commesse=new ArrayListSorted<>();
    for(List rec:infoL){
      Integer commTmp=ClassMapper.classToClass(rec.get(2),Integer.class);
      if(!commesse.contains(commTmp))
        commesse.add(commTmp);
    }
    
    return commesse;
  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(RitardoCommLineaBean.class);
  
}
