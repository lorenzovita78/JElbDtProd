/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.model.CausaliLineeBean;
import colombini.model.persistence.FermiGgLineaBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class FermiGgUtils {
  
  
  private static FermiGgUtils instance;
  
  private FermiGgUtils(){
    
  }
  
  public static FermiGgUtils getInstance(){
    if(instance==null){
      instance= new FermiGgUtils();
    }
    
    return instance;
  }
  
  
  public List<FermiGgLineaBean> getListBeanFermi(Date data,List<List> fermiGg,Map<String,CausaliLineeBean> causali) {
    if(fermiGg==null || fermiGg.isEmpty())
      return null;
    
    if(causali==null || causali.isEmpty())
      return null;
    
    List beans=new ArrayList();
    Map fmgMap=new HashMap();
    Long dataRifN=DateUtils.getDataForMovex(data);
    for(List fermo:fermiGg){
      String causale=ClassMapper.classToString(fermo.get(0));
      Double durata=ClassMapper.classToClass(fermo.get(1),Double.class);
      
      //attenzione causale deve essere la chiave della mappa di CausaliLineeBean
      CausaliLineeBean cl=causali.get(causale);
      if(cl!=null){
        if(fmgMap.containsKey(cl.getCampoChiave())){
          FermiGgLineaBean fgTmp=(FermiGgLineaBean) fmgMap.get(cl.getCampoChiave());
          fgTmp.setOccorrenze(fgTmp.getOccorrenze()+1);
          fgTmp.setDurataTot(fgTmp.getDurataTot()+durata);
      
          fmgMap.put(cl.getCampoChiave(), fgTmp);
      
        }else{
          FermiGgLineaBean fg=new FermiGgLineaBean();
          fg.setAzienda(cl.getAzienda());
          fg.setCodCausale(cl.getCodice());
          fg.setDataRifN(dataRifN);
          fg.setCentroLavoro(cl.getCentroLavoro());
          fg.setDurataTot(durata);
          fg.setOccorrenze(Integer.valueOf(1));
          fg.setMinInizio(Integer.valueOf(0));
          fg.setMinFine(Integer.valueOf(0));
          
          fg.setTipo(cl.getTipo());

          fmgMap.put(cl.getCampoChiave(), fg);
        }  
      }
    }
    //trasforma la mappa in lista
    Set keys=fmgMap.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      beans.add(fmgMap.get(key));
    }

    return beans;
  }

  
  public List<FermiGgLineaBean> getListBeansDurataMin(List<FermiGgLineaBean> beans){
    
    if(beans==null || beans.isEmpty())
      return beans;
    
    for(FermiGgLineaBean fg: beans){
      fg.setDurataTot(fg.getDurataTot()/60);
    }
    
    return beans;
  }
    
  
  
  public Map getMapTotaliFermiTipo(List<FermiGgLineaBean> beans ){
    if(beans==null)
      return null;
    
    Map fermiTipo=new HashMap();
    Integer nguasti=Integer.valueOf(0);
    
    for(FermiGgLineaBean bean:beans){
       String tipo=bean.getTipo();
       Double valTmp=bean.getDurataTot();
       
       if(fermiTipo.containsKey(tipo)){
         valTmp+=(Double) fermiTipo.get(tipo);
       }
       
       fermiTipo.put(tipo,valTmp);
       
       if(CausaliLineeBean.TIPO_CAUS_GUASTO.equals(tipo)){
         nguasti+=bean.getOccorrenze();
       }
    
    }
    
    fermiTipo.put(CausaliLineeBean.TIPO_NGUASTI,nguasti);
    
    return fermiTipo;
  }
  
  
}