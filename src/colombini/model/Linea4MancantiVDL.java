/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.MancantiVDLBean;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class Linea4MancantiVDL {
  
  
  private final static String TABNAME="ZDPVLL";
  
  private Integer progressivoVis;
  private String codiceAz;
  private String descrStab;
  private String descrLinea;
  private String codiciL;
  private List<Integer> listCodiciLinee;
  private Map mapCodiciLineeLog;
  private List<MancantiVDLBean> listBeanMancanti;
  private Integer totMancanti;
  private Integer totColliLinee;
  
  public Linea4MancantiVDL(Integer progressivoVis, String codiceAz, String descrStab, String descrLinea, String stringLinee) {
    this.progressivoVis = progressivoVis;
    this.codiceAz = codiceAz;
    this.descrStab = descrStab;
    this.descrLinea = descrLinea;
    this.codiciL = stringLinee;
    this.listCodiciLinee=new ArrayList();
    
    listBeanMancanti=new ArrayList();
    totMancanti=Integer.valueOf(0);
    totColliLinee=Integer.valueOf(0);
    
    //loadListCodiciLinee();
            
    loadMapCodici();
    
  }

  
  public Integer getProgressivoVis() {
    return progressivoVis;
  }

  public void setProgressivoVis(Integer progressivoVis) {
    this.progressivoVis = progressivoVis;
  }

  public String getCodiceAz() {
    return codiceAz;
  }

  public void setCodiceAz(String codiceAz) {
    this.codiceAz = codiceAz;
  }

  public String getDescrStab() {
    return descrStab;
  }

  public void setDescrStab(String descrStab) {
    this.descrStab = descrStab;
  }

  public String getDescrLinea() {
    return descrLinea;
  }

  public void setDescrLinea(String descrLinea) {
    this.descrLinea = descrLinea;
  }

  public String getCodiciL() {
    return codiciL;
  }

  public void setCodiciL(String codiciL) {
    this.codiciL = codiciL;
  }

  public Map getMapCodiciLineeLog() {
    return mapCodiciLineeLog;
  }

  public void setMapCodiciLineeLog(Map mapCodiciLineeLog) {
    this.mapCodiciLineeLog = mapCodiciLineeLog;
  }


  public Integer getTotMancanti() {
      return totMancanti;
    }

  public void setTotMancanti(Integer totMancanti) {
    this.totMancanti = totMancanti;
  }

  public List<MancantiVDLBean> getListBeanMancanti() {
    return listBeanMancanti;
  }

  public void setListBeanMancanti(List<MancantiVDLBean> listBeanMancanti) {
    this.listBeanMancanti = listBeanMancanti;
  }

  public List<Integer> getListCodiciLinee() {
    return listCodiciLinee;
  }

  public void setListCodiciLinee(List<Integer> listCodiciLinee) {
    this.listCodiciLinee = listCodiciLinee;
  }

  public Integer getTotColliLinee() {
    return totColliLinee;
  }

  public void setTotColliLinee(Integer totColliLinee) {
    this.totColliLinee = totColliLinee;
  }
  
  
  
  public void loadListCodiciLinee(){
    if(StringUtils.IsEmpty(codiciL))
      return;
    
    this.listCodiciLinee=ArrayUtils.getListFromArray(codiciL.split(","));
  }
  
  
  private void loadMapCodici() {
    mapCodiciLineeLog=new HashMap();
   
    if(StringUtils.IsEmpty(codiciL))
      return;
    List l=ArrayUtils.getListFromArray(codiciL.split(","));
    
    if(l==null)
      return;
    
    for(Object o:l){
      String s =ClassMapper.classToString(o);
      int idx=s.indexOf("-");
      if(idx<0){
        mapCodiciLineeLog.put(ClassMapper.classToClass(o,Integer.class), descrLinea);
        listCodiciLinee.add(ClassMapper.classToClass(o,Integer.class));
      }else{
        Integer valIni= ClassMapper.classToClass(s.substring(0, idx),Integer.class);
        Integer valFin= ClassMapper.classToClass(s.substring(idx+1),Integer.class);
        Integer valTmp=valIni;
        while(valTmp<=valFin){
          mapCodiciLineeLog.put(valTmp, descrLinea);
          listCodiciLinee.add(valTmp);
          valTmp++;
        }
      }
    }
    
  }
   
   public void addNMancanti(Integer num){
      this.totMancanti+=num;
    }
    
    public void addMancantiVdlBean(MancantiVDLBean bean){
      //aggiorno numero totale mancati
      addNMancanti(bean.getnMancanti());
      
      //setta codice azienda ,descrizione stabilimento e cdL della linea di riferimento
      bean.setDescrLinea(this.descrLinea);
      bean.setCodAzienda(this.codiceAz);
      bean.setDescrStab(this.descrStab);
      
      //aggiunge il bean della lista
      listBeanMancanti.add(bean);
    }
  
  
  
  public static Map<String,Linea4MancantiVDL> getMapLineeMancantiVDL(Connection con) throws SQLException{
    List<List> list=new ArrayList();
    Map beans=new HashMap();
    String s=" SELECT VLNORD ,VLCCON ,VLDFAC ,VLDCDL ,VLCLIN  "
            + " from "+ColombiniConnections.getAs400LibPersColom()+"."+TABNAME+
            " WHERE VLSTAT='1'";
    
    ResultSetHelper.fillListList(con, s, list);
    for(List rec:list){
      Integer prog=ClassMapper.classToClass(rec.get(0),Integer.class);
      String codAzienda=ClassMapper.classToString(rec.get(1)).trim();
      String descStab=ClassMapper.classToString(rec.get(2)).trim();
      String descLinea=ClassMapper.classToString(rec.get(3)).trim();
      String codLinee=ClassMapper.classToString(rec.get(4)).trim();
      
      Linea4MancantiVDL b =new Linea4MancantiVDL(prog, codAzienda, descStab, descLinea, codLinee);
      beans.put(descLinea,b);
      
   
    }
    
    
    return beans;
  }

  
  
  
}
