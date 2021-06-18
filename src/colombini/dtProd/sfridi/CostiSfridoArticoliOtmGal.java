/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.costant.CostantsColomb;
import colombini.query.produzione.G1.QuerySfridoOtmG1P2XCosti;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class CostiSfridoArticoliOtmGal {
  
   private Integer annoRif;
   private Integer meseRif;

  public CostiSfridoArticoliOtmGal(Integer annoRif, Integer meseRif) {
    this.annoRif = annoRif;
    this.meseRif = meseRif;
  }
   
  
  public List getCostiSfrido(Connection con) throws SQLException, QueryException{
    List costi=new ArrayList();
    List<List> datiPnp=new ArrayList();
    QuerySfridoOtmG1P2XCosti query=new QuerySfridoOtmG1P2XCosti();
    query.setFilter(QuerySfridoOtmG1P2XCosti.ANNO, annoRif);
    query.setFilter(QuerySfridoOtmG1P2XCosti.MESE, meseRif);
    
    String q=query.toSQLString();
    _logger.info("Esecuzione query :"+q);
    ResultSetHelper.fillListList(con, q, datiPnp);
    List<List> tmp=new ArrayList();
    String pnpOld="JFDKHLKJASDFHLKJ";
    Long qtaTot=Long.valueOf(0);
    for(List record:datiPnp){
      String pnp=ClassMapper.classToString(record.get(0));
      Long qta=ClassMapper.classToClass(record.get(3),Long.class);
      List infoPan=new ArrayList();
      infoPan.add(CostantsColomb.AZCOLOM); //0
      infoPan.add(CostantsColomb.GALAZZANO);
      infoPan.add(CostantsColomb.PIANO2);
      infoPan.add(CalcCostiSfridoArticoliMvx.OTMGALP2);
      infoPan.add(annoRif);
      infoPan.add(meseRif);
      infoPan.add(pnp);
      infoPan.add(ClassMapper.classToString(record.get(1))); //7 PAN
      infoPan.add(ClassMapper.classToClass(record.get(5),Double.class));  // 8 supTot
      infoPan.add(ClassMapper.classToClass(record.get(4),Double.class));  // 9 sfrido OTM
      infoPan.add(Long.valueOf(0));  //campo per sfrido fisiologico da settare a zero x i costi
      infoPan.add(qta);  // qtapannelli da distinta..
      
      
      if(pnp.equals(pnpOld) || StringUtils.IsEmpty(pnpOld)){
        tmp.add(infoPan);
        qtaTot+=qta;
      } 
      else if(!pnp.equals(pnpOld) ){
        if(tmp.size()==1){
          List rc=tmp.get(0);
          rc.remove(11);
          
          costi.add(rc); 
        }else{
          //ciclo su tutti i pan del pnp precendente per calcolare i mqs di frido in proporzione
          //alle qta dei pan
          for(List mod:tmp){
            Long qtaLoc=(Long) mod.get(11);
            Double perc=Double.valueOf(1);
            if(qtaTot>0)
              perc=qtaLoc/new Double(qtaTot); 
            
            mod.set(8, (Double) mod.get(8)*perc);
            mod.set(9, (Double) mod.get(9)*perc);
            
            mod.remove(11);
            
            costi.add(mod);
          }
        }
        tmp=new ArrayList();
        tmp.add(infoPan);
        qtaTot=qta;
     }
       
      pnpOld=pnp;
    }
    //processo l'ultimo record
    if(tmp.size()==1){
      List rc=tmp.get(0);
      rc.remove(11);

      costi.add(rc); 
    }else{
       //ciclo su tutti i pan del pnp precendente per calcolare i mqs di frido in proporzione
       //alle qta dei pan
       for(List mod:tmp){
         Long qtaLoc=(Long) mod.get(11);
         Double perc=qtaLoc/new Double(qtaTot); 
         mod.set(8, (Double) mod.get(8)*perc);
         mod.set(9, (Double) mod.get(9)*perc);

         mod.remove(11);

         costi.add(mod);
      }
    }
    
    
    return CalcCostiSfridoArticoliMvx.getInstance().aggDatiCostoArticolo(con, costi);
  } 
  
//  public List getCostiSfrido(Connection con) throws SQLException, QueryException{
//    List costi=new ArrayList();
//    List<List> datiPnp=new ArrayList();
//    QuerySfridoOtmG1P2XCosti query=new QuerySfridoOtmG1P2XCosti();
//    query.setFilter(QuerySfridoOtmG1P2XCosti.ANNO, annoRif);
//    query.setFilter(QuerySfridoOtmG1P2XCosti.MESE, meseRif);
//    
//    String q=query.toSQLString();
//    _logger.info("Esecuzione query :"+q);
//    ResultSetHelper.fillListList(con, q, datiPnp);
//    List<List> tmp=new ArrayList();
//    String pnpOld="";
//    Long qtaTot=Long.valueOf(0);
//    for(List record:datiPnp){
//      String pnp=ClassMapper.classToString(record.get(0));
//      Long qta=ClassMapper.classToClass(record.get(3),Long.class);
//      List infoPan=new ArrayList();
//      infoPan.add(CostantsColomb.AZCOLOM); //0
//      infoPan.add(CostantsColomb.GALAZZANO);
//      infoPan.add(CalcCostiSfridoArticoliMvx.OTMGALP2);
//      infoPan.add(annoRif);
//      infoPan.add(meseRif);
//      infoPan.add(pnp);
//      infoPan.add(ClassMapper.classToString(record.get(1))); //6 PAN
//      infoPan.add(ClassMapper.classToClass(record.get(4),Double.class));  //sfrido OTM
//      infoPan.add(ClassMapper.classToClass(record.get(5),Double.class));  //sfrido FISIO
//      infoPan.add(Long.valueOf(0));
//      infoPan.add(Long.valueOf(0));
//      infoPan.add(qta);
//      
//      
//      if(pnp.equals(pnpOld) || StringUtils.IsEmpty(pnpOld)){
//        tmp.add(infoPan);
//        qtaTot+=qta;
//      } 
//      else if(!pnp.equals(pnpOld) ){
//        if(tmp.size()==1){
//          List rc=tmp.get(0);
//          rc.remove(11);
//          
//          costi.add(rc); 
//        }else{
//          //ciclo su tutti i pan del pnp precendente per calcolare i mqs di frido in proporzione
//          //alle qta dei pan
//          for(List mod:tmp){
//            Long qtaLoc=(Long) mod.get(11);
//            Double perc=qtaLoc/new Double(qtaTot); 
//            mod.set(7, (Double) mod.get(7)*perc);
//            mod.set(8, (Double) mod.get(8)*perc);
//            
//            mod.remove(11);
//            
//            costi.add(mod);
//          }
//        }
//        tmp=new ArrayList();
//        tmp.add(infoPan);
//        qtaTot=qta;
//     }
//       
//      pnpOld=pnp;
//    }
//    //processo l'ultimo record
//    if(tmp.size()==1){
//      List rc=tmp.get(0);
//      rc.remove(11);
//
//      costi.add(rc); 
//     }else{
//       //ciclo su tutti i pan del pnp precendente per calcolare i mqs di frido in proporzione
//       //alle qta dei pan
//       for(List mod:tmp){
//         Long qtaLoc=(Long) mod.get(11);
//         Double perc=qtaLoc/new Double(qtaTot); 
//         mod.set(7, (Double) mod.get(7)*perc);
//         mod.set(8, (Double) mod.get(8)*perc);
//
//         mod.remove(11);
//
//         costi.add(mod);
//      }
//    }
//    
//    
//    return CalcCostiSfridoArticoliMvx.getInstance().aggDatiCostoArticolo(con, costi);
//  } 
  
  
  private static final Logger _logger = Logger.getLogger(CostiSfridoArticoliOtmGal.class); 
  
}
