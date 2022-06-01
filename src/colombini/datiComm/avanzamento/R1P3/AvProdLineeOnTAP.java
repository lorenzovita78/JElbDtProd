/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;

import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.avanzamento.QueryProdGgFromTAP;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public abstract class AvProdLineeOnTAP extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
   try {
      List<List> commesse=new ArrayList();
      QueryProdGgFromTAP qry=new QueryProdGgFromTAP();
      Date dataIni=DateUtils.addSeconds(DateUtils.getInizioGg(this.getDataRifCalc()),1);
      Date dataFin=DateUtils.getFineGg(this.getDataRifCalc());
      
      
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATADA,dataIni);
      qry.setFilter(FilterFieldCostantXDtProd.FT_DATAA,dataFin);
      qry.setFilter(FilterFieldCostantXDtProd.FT_LINEAVP,getCodLineaOnTAP());
      String query=qry.toSQLString();
      
      ResultSetHelper.fillListList(conAs400, query, commesse);
      
      //Modifica per il P4 - da finire
//      Map<String,Integer> commNew = new HashMap<>();
//      //List commesseSom=new ArrayList();
//        for(List comm:commesse){
//            String nrocomm=(String) comm.get(0);
//            Integer pzprod=(Integer) comm.get(3);
//            if(commNew.containsKey(nrocomm.substring(nrocomm.length()-3, nrocomm.length()))){
//                
//            } else commNew.put(nrocomm, pzprod);
//            
//            
//            
//        }
      //Fin modifica
      
      String s=" no result";
      if(commesse!=null && !commesse.isEmpty())
        s=commesse.toString();
      _logger.info("QUERY AVP :"+query);
      _logger.info("RESULT AVP :" +s);
      
      //Prima di fare il store. Devo creare un metodo per sommare le commesse colom e febal (creo lista nuova e agruppo per le ultime 3 lettere)
      storeDatiProdComm(conAs400, commesse);
      
     
      
    } catch (ParseException ex) {
      _logger.error("Problemi di conversione della data riferimento calcolo-->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (SQLException ex) {
      _logger.error("Errore in fase di interrogazione del db per sapere il numero dei pz prodotti-->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (QueryException ex) {
      _logger.error("Errore in fase di interrogazione del db per sapere il numero dei pz prodotti-->"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }
  }
  
  
  
  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
    return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(), this.getInfoLinea().getCommessa(),
                         this.getInfoLinea().getDataCommessa(), this.getInfoLinea().getCodLineaLav());
  
  }
  
  
  /**
   * Torna il codice della linea utilizzato nella tracciatura pz/colli sul portale
   * @return 
   */
  public abstract String getCodLineaOnTAP();
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdLineeOnTAP.class);  
  
  
}
