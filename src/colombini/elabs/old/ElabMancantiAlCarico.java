/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.elabs.old;

import colombini.elabs.NameElabs;
import elabObj.ElabClass;
import fileXLS.FileXlsXPoiR;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import mail.MailMessageInfoBean;
import mail.MailSender;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 * Elaborazione che legge i dati relativi ai mancanti alle buche di carico alle ore 6:00 (da file xls)
 * e invia una segnalazione via mail
 * 
 * @author lvita
 */
public class ElabMancantiAlCarico  extends ElabClass{
 
   //OLD
  //public final static String ELBMAILMANCANTICARICO="ELBMAILMANCANTICARICO";
  public final static String PATHFILEMANCANTI="PATHFILEMANCANTI";
  public final static String NOMEFILEMANCANTI="NOMEFILEMANCANTI";
  
  public final static String PATHFILE="PATHFILE";
  public final static String NOMEFILE="NOMEFILE";
  public final static String CODANNO="$ANNO$";
  
//  public final static String PATHFILEMC="//pegaso/produzione/Rovereta1/Piano0/Controllo_colli_carico/$ANNO$/mancanti 6.00 13.15/";
//  public final static String NOMEFILEMC="mancanti$ANNO$_6.00_13.15.xlsx";
  
  
//  public final static String PATHFILEMC="//pegaso/produzione/Rovereta1/Piano0/Controllo_colli_carico/$ANNO$/";
//  public final static String NOMEFILEMC="mancanti.xlsx";
//  
  public ElabMancantiAlCarico() {
  }
  
  

  @Override
  public Boolean configParams() {
    return Boolean.TRUE;
    
  }

  @Override
  public void exec(Connection con) {
    _logger.info("Inizio Elaborazione Mail mancanti al Carico ore 6:00");
    MailSender mS=new MailSender();

    //Map prop=ElabsProps.getInstance().getProperties(ELBMAILMANCANTICARICO);
    String pathFile=(String) getElabProperties().get(PATHFILEMANCANTI);
    String nomeFile=(String) getElabProperties().get(NOMEFILEMANCANTI);
    
    if(StringUtils.IsEmpty(pathFile) || StringUtils.IsEmpty(nomeFile) ){
      addError("Parametri relativi al percorso del file o del nome del file non presenti . Impossibile proseguire nell'elaborazione ");
      return;
    }
   
    FileXlsXPoiR xlsX=null;
    Date ggCorrente =new Date();
    Integer anno=DateUtils.getYear(ggCorrente);
    String pathFileOrigine=pathFile.replace(CODANNO, anno.toString())+nomeFile.replace(CODANNO, anno.toString());
    String pathFileCopia=pathFile.substring(0,pathFile.lastIndexOf(CODANNO))+"temp/"+nomeFile.replace(CODANNO, anno.toString());

    _logger.info("Copia di sicurezza del file");
    try{
      FileUtils.copyFile(pathFileOrigine, pathFileCopia);
      xlsX=new FileXlsXPoiR(pathFileCopia);
      xlsX.prepareFileXls();
      
      _logger.info("Preparazione mail ---> lettura file xls");
      String text=prepareMailMessage(xlsX,ggCorrente);
      
      if(!StringUtils.IsEmpty(text)){
        MailMessageInfoBean beanInfo=mS.getInfoBaseMailMessage(con,NameElabs.MESSAGE_ELBMANCANTIBCORE6);
        beanInfo.setObject(beanInfo.getObject().trim()+" in data "+DateUtils.DateToStr(ggCorrente, "dd/MM/yyyy") );
        beanInfo.setText(text);
        mS.send(beanInfo);
          
      }else{
        addWarning("Attenzione nessun dato relativo ai mancanti per il giorno:"+ggCorrente);
      }
      
    }catch(IOException e){
      addError("Problemi in fase di accesso al file xls.Impossibile proseguire -->"+e.toString());
      
    } catch(ParseException e){
      addError("Problemi in fase di lettura del file xls.Impossibile proseguire -->"+e.toString());
    
    }  finally {
      try{
        if(xlsX!=null)
          xlsX.closeRFile();
      }catch(IOException i){
        addError("Errore in fase di chisura del file xls --> "+i.toString());
      }
    }
  }
  
  
  

  private String prepareMailMessage(FileXlsXPoiR xlsX,Date ggCorrente) {
    StringBuilder message=new StringBuilder();
    XSSFSheet sheet=xlsX.getSheet("db");
    Iterator iter=sheet.rowIterator();
    Long rowCount=Long.valueOf(0);
    String commessa=null;
    String stabilimento=null;
    String codiciLinea=null;
    String descrizioneLinea=null;
    Double mancanti=null;
    Integer totMancanti=Integer.valueOf(0);
    
    try{
    while(iter.hasNext()){
      rowCount++;
      XSSFRow row= (XSSFRow) iter.next();
      if(row!=null && rowCount>3) {
        if(row.getCell(2)!= null   &&  XSSFCell.CELL_TYPE_NUMERIC==row.getCell(2).getCellType() ){ 
          Date dataR=row.getCell(2).getDateCellValue();
          if(DateUtils.numGiorniDiff(dataR, ggCorrente)==0){
            if(row.getCell(3) != null  ){
              if(XSSFCell.CELL_TYPE_STRING==row.getCell(3).getCellType() )
                commessa=row.getCell(3).getStringCellValue();
              else if(XSSFCell.CELL_TYPE_NUMERIC==row.getCell(3).getCellType() ){
                Double d=row.getCell(3).getNumericCellValue();
                commessa=ClassMapper.classToString(d.intValue());
              }else
                commessa="";
            }
            stabilimento=( row.getCell(4) != null && XSSFCell.CELL_TYPE_STRING==row.getCell(4).getCellType() ) ? 
                         row.getCell(4).getStringCellValue() : "" ;
            codiciLinea=( row.getCell(5) != null && XSSFCell.CELL_TYPE_STRING==row.getCell(5).getCellType() ) ? 
                        row.getCell(5).getStringCellValue() : "" ;
            descrizioneLinea=( row.getCell(6) != null && XSSFCell.CELL_TYPE_STRING==row.getCell(6).getCellType() ) ? 
                             row.getCell(6).getStringCellValue() : "" ;
            mancanti=( row.getCell(7) != null  && XSSFCell.CELL_TYPE_NUMERIC==row.getCell(7).getCellType() ) ?  
                     row.getCell(7).getNumericCellValue() : Double.valueOf(0) ;
            if(message.toString().length()==0){
               message.append(" Commessa n.").append(commessa).append(" \n");
            }            
            if(mancanti.intValue()>0){
              message.append("\n Stab: ").append(stabilimento).append(
                             " - Linea : ").append(descrizioneLinea).append(
                             " --> mancanti n.").append(mancanti.intValue());
              
              totMancanti+=mancanti.intValue();
            }  
          }
        }       
      } //controllo su riga 
      
     }
    } finally{
        _logger.info(" Righe file xls lette n."+rowCount);
    }
    
    if(totMancanti>0){
      message.append("\n\n Totale mancanti ore 6:00  n.").append(totMancanti);
    }else{
      message=new StringBuilder();
    }
    
    return message.toString();
  }

  
 
  
  private static final Logger _logger = Logger.getLogger(ElabMancantiAlCarico.class); 

  
}
