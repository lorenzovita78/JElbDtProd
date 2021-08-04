/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P1;

import colombini.datiComm.avanzamento.AvProdLineaStd;
import colombini.exception.DatiCommLineeException;
import colombini.logFiles.R1P1.LogFileMawFianchi2;
import colombini.util.InfoMapLineeUtil;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class AvProdLineaMawFianchi2 extends AvProdLineaStd{

  @Override
  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
    Map commesse=new HashMap<Integer, Integer>();
    List<List> lista=getListColliFromLogFile();
    for(List rec:lista){
      Integer commessa=ClassMapper.classToClass(rec.get(0),Integer.class);
      if(commesse.containsKey(commessa)){
        commesse.put(commessa,((Integer)commesse.get(commessa))+1);
      }else{
        commesse.put(commessa,Integer.valueOf(1));
      }
    }
    
    storeDatiProdComm(conAs400, commesse);
  }

  @Override
  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
     return getPzProdComm(conAs400, this.getInfoLinea().getAzienda(),this.getInfoLinea().getCommessa() , 
            this.getInfoLinea().getDataCommessa(),this.getInfoLinea().getCodLineaLav() );
  }
  
  
  
  private List<List> getListColliFromLogFile() throws DatiCommLineeException {
    LogFileMawFianchi2 lm2;
    List listColli=new ArrayList();
    String fileName="";
    try{
      fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(this.getInfoLinea().getCodLineaLav()), this.getDataRifCalc());
       //fileName=InfoMapLineeUtil.getLogFileGgLinea(InfoMapLineeUtil.getNomeLineaFromCodice(this.getInfoLinea().getCodLineaLav()), this.getDataForLogFile());
//      String fileName=getLogFileName(dtInizio);
      lm2=new LogFileMawFianchi2(fileName);
      _logger.info("Apertura file log: "+fileName);
      lm2.initialize();
      
      listColli=lm2.processLogFile(DateUtils.getInizioGg(this.getDataRifCalc()), DateUtils.getFineGg(this.getDataRifCalc()));
      
    } catch(IOException i){
      _logger.error("Errore in fase di accesso al file di log "+fileName+" --> "+i.getMessage());
      throw new DatiCommLineeException(i);
    } catch (ParseException ex) {
      _logger.error("Errore in fase di conversione dei dati --> "+ex.getMessage());
      throw new DatiCommLineeException(ex);
    }  
    
    return listColli;
  }
  
  
  private static final Logger _logger = Logger.getLogger(AvProdLineaMawFianchi2.class);
  
}
