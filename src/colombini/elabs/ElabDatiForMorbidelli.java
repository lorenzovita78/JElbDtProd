/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.costant.CostantsColomb;
import colombini.logFiles.R1P1.LogFileProdFMorbidelli;
import db.JDBCDataMapper;
import dtProduzione.rovereta1.P1.DtProdFMorbidelli;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDatiForMorbidelli extends ElabClass{
  
  private final static String PATHPRG="//foratriceunixp1/C/Programmi/Scm Group/Xilog Plus/Report/";
  public final static String PRODUZIONE="PRO";
  public final static String DIAGNOSI="DIA";
  
  private List<Date> giorni;
  private Date dataInizio;
  private Date dataFine;
  
//  private String tipoFile;
  
  public ElabDatiForMorbidelli(){

  }
  
  @Override
  public Boolean configParams() {
     Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
     
    
    return Boolean.TRUE;
  }

  
  @Override
  public void exec(Connection con) {
    LogFileProdFMorbidelli lf=null;
    String fileName=null;
    giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    for(Date data: giorni){
      try {
        fileName=getFileName(data, PRODUZIONE);
        if(StringUtils.isEmpty(fileName))
          return;
        
        lf=new LogFileProdFMorbidelli(fileName);
        lf.initialize();
        _logger.info("Elaborazione dati gg:"+data);
        List objects=(List) lf.processLogFile(data, data);
        deleteObjects(con,data);
        saveObjectsDtProd(con, objects);
      
      } catch (SQLException ex) {
       _logger.error("Impossibile salvare i dati per il giorno : "+data.toString() +" : "+ex.getMessage());
       addError("Problemi in fase di salvataggio/cancellazione dei dati sul db : "+ex.toString());
      } catch (IOException ex) {
         _logger.error("Impossibile accedere al file "+fileName+ " :"+ex.getMessage());
         addError("Impossibile accedere al file "+fileName+ " :"+ex.toString());
      } catch (ParseException ex) {
        _logger.error("Impossibile elaborare i dati per il giorno :"+data.toString() +" : "+ex.getMessage());
        addError("Errore in fase di conversione dei dati "+ex.toString());
      } finally{
        try {
          lf.terminate();
        } catch (FileNotFoundException ex) {
          _logger.error("Impossibile chiudere il file "+fileName+ " :"+ex.getMessage());
        } catch (IOException ex) {
         _logger.error("Impossibile chiudere il file "+fileName+ " :"+ex.getMessage());
        }
      } 
    }
  }
  
 
  public String getFileName(Date data,String tipo) throws ParseException{
    if(data==null || tipo==null)
      return null;
    
    String anno=DateUtils.DateToStr(data, "yyyy");
    String dtS=DateUtils.DateToStr(data, "yyyyMMdd");
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBDTFORMORBIDELLIR1);
    String pathFilePrg=(String) getElabProperties().get(NameElabs.PATHFILEFORMORBIDELLIR1);
    if(StringUtils.isEmpty(pathFilePrg)){
      addError("Necessario indicare il path dei file di produzione nel file di configurazione delle elaborazioni");
      return null;
    }
    
    return PATHPRG+anno+"/"+dtS+"."+tipo.toLowerCase();
  }

  
 public void saveObjectsDtProd(Connection con ,List<DtProdFMorbidelli> listaObj) throws SQLException {
   if(con==null || listaObj==null)
     return ;
   PreparedStatement pstmt=null;
   try{
//   "(ZPDATA,ZPORA,ZPPROG,ZPDESC,ZPLNGH,ZPLARG,ZPSPES,ZPQTA,ZPTEFF,ZPTTOT,ZPUTIN,ZPDTIN,ZPORIN)"
     pstmt = con.prepareStatement(DtProdFMorbidelli.INSERTSTM); 
     for(DtProdFMorbidelli dt:listaObj){
       try {
         pstmt.setLong(1, DateUtils.getDataForMovex(dt.getDataLog()));
         pstmt.setInt(2, new Integer(dt.getOra()));
         pstmt.setString(3, dt.getProgramma());
         pstmt.setString(4, dt.getDescrizione());
         pstmt.setDouble(5, dt.getLunghezza());
         pstmt.setDouble(6, dt.getLarghezza());
         pstmt.setDouble(7, dt.getAltezza());
         pstmt.setInt(8,dt.getQta());
         pstmt.setLong(9,dt.getTempoEffettivo());
         pstmt.setLong(10,dt.getTempoTotale());
         pstmt.setString(11,CostantsColomb.UTDEFAULT);
         pstmt.setLong(12,DateUtils.getDataSysLong());
         pstmt.setString(13,DateUtils.getOraSysString());

         pstmt.execute();
       } catch (SQLException ex) {
          _logger.error("Impossibile salvare il dato relativo a "+dt.getProgramma()+" - "+dt.getDataLog()+" - "+dt.getOra()+" - "+dt.getTempoEffettivo()+" : "+ex.getMessage());
          addError("Impossibile salvare il dato relativo a "+dt.getProgramma()+" - "+dt.getDataLog()+" - "+dt.getOra()+" - "+dt.getTempoEffettivo());
       } 
    }
   }finally{
     if(pstmt!=null)
       pstmt.close();
   }
   
  }
  
 
  private void deleteObjects(Connection con, Date data) throws SQLException {
    
    String delete=" delete from MCOBMODDTA.ZFOMDP "
                 +" where 1=1"
                 +" and ZPDATA="+JDBCDataMapper.objectToSQL(DateUtils.getDataForMovex(data));
    
    _logger.info(delete);
    PreparedStatement st=con.prepareStatement(delete);
    st.execute(); 
  }
  
  private static final Logger _logger = Logger.getLogger(ElabDatiForMorbidelli.class);

  
}
