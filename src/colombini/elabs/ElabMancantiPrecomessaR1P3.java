/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.model.MancantePrecommP3Bean;
import colombini.util.DatiProdUtils;
import db.ResultSetHelper;
import elabObj.ElabClass;
import fileXLS.FileXlsXPoiRW;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class ElabMancantiPrecomessaR1P3 extends ElabClass{

  
  public final static String SHEETDATI="DATI";
  
  public final static Integer NCOL_DATA=0;
  public final static Integer NCOL_ORDINE_PREL=1;
  public final static Integer NCOL_ORDINE_CLI=2;
  public final static Integer NCOL_AZIENDA=3;
  public final static Integer NCOL_ARTICOLO=4;
  public final static Integer NCOL_UM=5;
  public final static Integer NCOL_DESCR_ARTICOLO=6;
  public final static Integer NCOL_COD_FORN=7;
  public final static Integer NCOL_RAGSOC_FORN=8;
  public final static Integer NCOL_QTA_PREL=9;
  public final static Integer NCOL_QTA_GIA=10;
  public final static Integer NCOL_DESCRIZ=11;
  public final static Integer NCOL_CAUSA=12;
  public final static Integer NCOL_APVG=13;
  public final static Integer NCOL_FAMIGLIA=14;
  public final static Integer NCOL_SOTTOCAUSA=15;
  public final static Integer NCOL_DESCR_ACQUISTI=16;
  public final static Integer NCOL_QTA_CALC=17;
  public final static Integer NCOL_ANNO=18;
  public final static Integer NCOL_MESE=19;
  public final static Integer NCOL_SETT=20;
  
  
  @Override
  public Boolean configParams() {
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Connection conIncas=null;
    String pathFile=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEMANPREC));
    String nomeFile=ClassMapper.classToString(getElabProperties().get(NameElabs.NOMEFILEMANPREC));
    String pathFileBck=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILEBCKMANPREC));
    
    try{
      Date dataRif=new Date();
      
      if(DatiProdUtils.getInstance().isGgLav(con, dataRif)){
      
        conIncas=ColombiniConnections.getDbIncasConnection();

        //copia di sicurezza del file
        _logger.info("Copy file...");
        FileUtils.copyFile(pathFile+nomeFile, pathFileBck+nomeFile);
        _logger.info("Lettura file xls ");
        FileXlsXPoiRW xlsx=new FileXlsXPoiRW(pathFile+nomeFile);
        xlsx.prepareFileXls();
        Integer idx=getIdxFirstRow(xlsx);
        Map map = getMapElGgPrec(xlsx,dataRif,idx);
        
        _logger.info("Lettura dati da Incas");
        List lNew=getListElementsFromIncas(conIncas);
        
        _logger.info("Process dati...");
        processListIncas(xlsx,lNew,map,idx);
        xlsx.closeAndSaveWFile();
      }else{
        _logger.info("GG non lavorativo.");
      }
    }catch(SQLException s){
      addError("Errore in fase di connessione con db Incas -->"+s.toString());
      _logger.error("Errore in fase di connessione con db Incas "+s.getMessage());
    } catch (IOException ex) {
      addError("Errore in fase di accesso al file xls"+nomeFile+" -->"+ex.toString());
      _logger.error("Errore in fase di connessione con db Incas "+ex.getMessage());
    }finally{
      try{
        if(conIncas!=null)
          conIncas.close();
        }catch(SQLException s ){
         _logger.error("Errore in fase di chiusura con la connessione con db Incas -->"+s.getMessage()); 
        } 
    }
    
    
  }
  
  
  
  
  private Integer getIdxFirstRow(FileXlsXPoiRW xlsx){
    Integer idx=null; 
    
    XSSFSheet sheet=xlsx.getSheet(SHEETDATI);
    int nRows =sheet.getLastRowNum();
    int initialRow =sheet.getFirstRowNum();
    int i=nRows;
    
    while(i>initialRow && i<=nRows && idx==null){
      XSSFRow rowTmp=sheet.getRow(i);
      XSSFCell cellData=rowTmp.getCell(0);
      if(cellData!=null &&  cellData.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
         idx=i;
      }
      i--;
    }
    
    return idx;   
  }
  
   private Map getMapElGgPrec(FileXlsXPoiRW xlsx,Date dataRif,int posIni) {
     Map mappa=new HashMap<String,MancantePrecommP3Bean>();
    
     XSSFSheet sheet=xlsx.getSheet(SHEETDATI);
     int initialRow =sheet.getFirstRowNum();
     int i=posIni;
     Date dataPrec=null;
     Boolean dateDiff=Boolean.FALSE;
     
     while(i>initialRow && !dateDiff){
       XSSFRow rowTmp=sheet.getRow(i);
       XSSFCell cellData=rowTmp.getCell(0);
       if(cellData!=null &&  cellData.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
         Date dataCell=cellData.getDateCellValue();
         
         //valorizzo la data di riferimento la prima volta
         if(dataPrec==null){
           dataPrec=dataCell;
         }
         if(dataCell!=null && DateUtils.daysBetween(dataCell, dataPrec)==0){
           MancantePrecommP3Bean bean=getBeanFromXlsRow(rowTmp,i);
           if(bean!=null){
             mappa.put(bean.getKeyBean(),bean);
             System.out.println("Caricata riga "+(i+1));
           }  
         }else{
           dateDiff=Boolean.TRUE;
         }  
       }
        
      i--;
     }
     
     
    return mappa;
  } 
  
  private MancantePrecommP3Bean getBeanFromXlsRow(XSSFRow rowTmp,int numRow) {
    MancantePrecommP3Bean bean=null;
    
    try{
      bean=new MancantePrecommP3Bean();
      bean.setDataRiferimento(rowTmp.getCell(0).getDateCellValue());
      bean.setOrdinePrelievo(getStringFromCell(rowTmp.getCell(1)));
      bean.setOrdineCliente(getStringFromCell(rowTmp.getCell(2)));
      bean.setAzienda(ClassMapper.classToClass(rowTmp.getCell(3).getNumericCellValue(),Integer.class));
      bean.setArticolo(getStringFromCell(rowTmp.getCell(4)));
      bean.setuMisura(getStringFromCell(rowTmp.getCell(5)));
      bean.setDescrArticolo(getStringFromCell(rowTmp.getCell(6)));
      bean.setCodiceFornitore(getStringFromCell(rowTmp.getCell(7)));
      bean.setRagSocialeFornitore(getStringFromCell(rowTmp.getCell(8)));
      bean.setQtaPrelievo(rowTmp.getCell(9).getNumericCellValue());
      bean.setQtaGiacenza(rowTmp.getCell(10).getNumericCellValue());
      //bean.setDescrizioneProd(getStringFromCell(rowTmp.getCell(11)));
      bean.setCausa(getStringFromCell(rowTmp.getCell(12)));
      bean.setApvg(getStringFromCell(rowTmp.getCell(13)));
      bean.setFamiglia(getStringFromCell(rowTmp.getCell(14)));
      bean.setSottocausa(getStringFromCell(rowTmp.getCell(15)));
      bean.setDescrAcquisti(getStringFromCell(rowTmp.getCell(16)));
    }catch(Exception e){
      _logger.error("Errore in fase di trascodifica del file xls per la riga n."+numRow+" -->"+e.getMessage());
    }
    
    
    return bean;
  }
  
  
  private void processListIncas(FileXlsXPoiRW xlsx, List<MancantePrecommP3Bean> listGgCor, Map mapElGgPrec, Integer idx) {
   if(listGgCor==null|| mapElGgPrec==null)
     return;
   Integer posTmp=idx+1;
   
   for(MancantePrecommP3Bean b:listGgCor){
     MancantePrecommP3Bean btemp=null; 
     try{
       String keyTmp=b.getKeyBean();
       if(mapElGgPrec.containsKey(keyTmp)){
         btemp=(MancantePrecommP3Bean) mapElGgPrec.get(keyTmp);
         b.copyInfo(btemp);
       }
       writeBeanOnXls(xlsx,b,posTmp);
       posTmp++;
     }catch(Exception e){
       addError("Errore in fase di scrittura del bean "+btemp.getKeyBean()+" -->"+e.getMessage());
       _logger.error("Errore in fase di scrittura del bean "+btemp.getKeyBean()+" -->"+e.toString());
     }
   }
   
   
  }
  
  
  
  private String getStringFromCell(XSSFCell cell){
    if(cell==null)
      return null;
    
    switch (cell.getCellType()) {
      case XSSFCell.CELL_TYPE_STRING:
        return cell.getStringCellValue().trim();
      case XSSFCell.CELL_TYPE_NUMERIC:
        return ClassMapper.classToString(cell.getNumericCellValue());
      case XSSFCell.CELL_TYPE_BLANK:
        return "";
      default:
        break;
    }
              
    return "";          
  }
  
  
  
  

  private List<MancantePrecommP3Bean> getListElementsFromIncas(Connection conIncas) throws SQLException {
    List<List> l=new ArrayList();
    List<MancantePrecommP3Bean> manList=new ArrayList();
    String s="select a.*,b.INCATM ,b.MBRESP from [EsColombiniPre74].[dbo].vw_chk_prelievi_GIACENZA a  left outer join \n" +
                  "  ( select INCANA,MBWHLO,MBITNO,INCATM,MBRESP from [Colom].[dbo].MITBAL_COLOM  \n" +
                     "  union all" +
                     " select INCANA,MBWHLO,MBITNO,INCATM,MBRESP from [Colom].[dbo].[MITBAL_FEBAL]) B \n" +
              "   on a.COMMITTENTE=(b.INCANA collate DATABASE_DEFAULT)and a.ARTICOLO=( b.MBITNO  collate DATABASE_DEFAULT)"
            + " ORDER BY ORDINE_PRELIEVO, ORDINE_CLIENTE";
    
    ResultSetHelper.fillListList(conIncas, s, l);
    for(List ltmp:l){
      MancantePrecommP3Bean bean=new MancantePrecommP3Bean();
      bean.setDataRiferimento(new Date());
      bean.setOrdinePrelievo(ClassMapper.classToString(ltmp.get(1)));
      bean.setOrdineCliente(ClassMapper.classToString(ltmp.get(2)));
      bean.setAzienda(ClassMapper.classToClass(ltmp.get(3),Integer.class));
      bean.setArticolo(ClassMapper.classToString(ltmp.get(4)));
      bean.setuMisura(ClassMapper.classToString(ltmp.get(5)));
      bean.setDescrArticolo(ClassMapper.classToString(ltmp.get(6)));
      bean.setCodiceFornitore(ClassMapper.classToString(ltmp.get(7)));
      bean.setRagSocialeFornitore(ClassMapper.classToString(ltmp.get(8)));
      bean.setQtaPrelievo(ClassMapper.classToClass(ltmp.get(9),Double.class));
      bean.setQtaGiacenza(ClassMapper.classToClass(ltmp.get(10),Double.class));
      bean.setUbicazione(ClassMapper.classToString(ltmp.get(12)));
      bean.setApvg(ClassMapper.classToString(ltmp.get(13)));
      
      bean.loadCausa();
      //gestione campo Causa
      
      //  
        
      manList.add(bean);
    }
    
    return manList;
  }

  

  private void writeBeanOnXls(FileXlsXPoiRW xlsx, MancantePrecommP3Bean b, Integer posTmp) {
    XSSFSheet sh=xlsx.getSheet(SHEETDATI);
    XSSFRow row=sh.getRow(posTmp);
    CellStyle cellStyle = xlsx.getFileXls().createCellStyle();
    CreationHelper createHelper = xlsx.getFileXls().getCreationHelper();
    cellStyle.setDataFormat(
    createHelper.createDataFormat().getFormat("dd/mm/yy"));

  
    
    if(row==null)
      row=sh.createRow(posTmp);
    
    XSSFCell c=null;
    
    //int i=0;i<=row.getLastCellNum() && i<=NCOL_SETT;i++
    for(int i=0;i<=NCOL_SETT;i++){
      c=row.getCell(i);
      if(c ==null){
        c=row.createCell(i);
      }
      
      if(NCOL_DATA.equals(i)){
        //c.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
        c.setCellValue(b.getDataRiferimento());
        c.setCellStyle(cellStyle);
        //c.setCellValue(DateUtils.dateToStr(b.getDataRiferimento(),"dd/MM/yyyy") );
      }else if(NCOL_ORDINE_PREL.equals(i)){
        c.setCellValue(b.getOrdinePrelievo());
      }else if(NCOL_ORDINE_CLI.equals(i)){
        c.setCellType( XSSFCell.CELL_TYPE_STRING);
        c.setCellValue(b.getOrdineCliente());
      }else if(NCOL_AZIENDA.equals(i)){
        c.setCellValue(b.getAzienda());
      }else if(NCOL_ARTICOLO.equals(i)){
        c.setCellType( XSSFCell.CELL_TYPE_STRING);
        c.setCellValue(b.getArticolo());
      }else if(NCOL_UM.equals(i)){
        c.setCellValue(b.getuMisura());
      }else if(NCOL_DESCR_ARTICOLO.equals(i)){
        c.setCellValue(b.getDescrArticolo());
      }else if(NCOL_COD_FORN.equals(i)){
        c.setCellType( XSSFCell.CELL_TYPE_STRING);
        c.setCellValue(b.getCodiceFornitore());
      }else if(NCOL_RAGSOC_FORN.equals(i)){
        c.setCellValue(b.getRagSocialeFornitore());
      }else if(NCOL_QTA_PREL.equals(i)){
        c.setCellValue(b.getQtaPrelievo());
      }else if(NCOL_QTA_GIA.equals(i)){
        c.setCellValue(b.getQtaGiacenza());
      }else if(NCOL_CAUSA.equals(i)){
        c.setCellValue(b.getCausa());
      }else if(NCOL_APVG.equals(i)){
        c.setCellValue(b.getApvg());
      }else if(NCOL_FAMIGLIA.equals(i)){
        c.setCellValue(b.getFamiglia());
      }else if(NCOL_SOTTOCAUSA.equals(i)){
        c.setCellValue(b.getSottocausa());
      }else if(NCOL_DESCR_ACQUISTI.equals(i)){
        c.setCellValue(b.getDescrAcquisti());
      } else if(NCOL_QTA_CALC.equals(i)){
        c.setCellValue(b.getQtaCalc());
      }else if(NCOL_ANNO.equals(i)){
        c.setCellValue(b.getAnno());
      }else if(NCOL_MESE.equals(i)){
        c.setCellValue(b.getMese());
      }else if(NCOL_SETT.equals(i)){
        c.setCellValue(b.getSett());
      }
    }
  }


 private static final Logger _logger = Logger.getLogger(ElabMancantiPrecomessaR1P3.class);
  
}
