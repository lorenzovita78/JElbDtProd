/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;


import colombini.xls.cartelliniKaizen.XlsCrusscottoKaizen;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import fileXLS.FileXlsXPoiR;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FilePropUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabXlsSicurezza extends ElabClass {

  public final static String PATHDEST="pathSIC.backup";
  public final static String PREFIXFILER1="R1_";
  public final static String PREFIXFILER2="R2_";
  public final static String PREFIXFILEG1="G1_";
  public final static String FILECRUSCOTTO="fSICCruscotto";
  
  public final static String PREFIXSICUREZZA="fSIC";
  
  //Foglio excel Sicurezza
  public final static Integer RG_INIZIO_TK=Integer.valueOf(3);
//  public final static Integer CL_STATO_TK=Integer.valueOf(14);
  public final static Integer CL_STATO_TK=Integer.valueOf(3);
  
  public final static String TKCHIUSO="CHIUSO";
  public final static String SHEETTKSIC= "Migl+Inv";
  
  private Long tkAR1P0;
  private Long tkAR1P1;
  private Long tkAR1P2;
  private Long tkAR1P3;
  private Long tkAR1P4;
  private Long tkAR1ARTEC;
  
  private Long tkAG1P0;
  private Long tkAR2P1;
  private Long tkAG1P2;
  
  private Long tkCR1P0;
  private Long tkCR1P1;
  private Long tkCR1P2;
  private Long tkCR1P3;
  private Long tkCR1P4;
  private Long tkCR1ARTEC;
  
  private Long tkCG1P0;
  private Long tkCR2P1;
  private Long tkCG1P2;
  
  
  private Date dataRif;
  
  
  public ElabXlsSicurezza() {
    tkAR1P0=Long.valueOf(0);
    tkAR1P1=Long.valueOf(0);
    tkAR1P2=Long.valueOf(0);
    tkAR1P3=Long.valueOf(0);
    tkAR1P4=Long.valueOf(0);
    tkAR1ARTEC=Long.valueOf(0);

    tkAG1P0=Long.valueOf(0);
    tkAR2P1=Long.valueOf(0);
    tkAG1P2=Long.valueOf(0);

    tkCR1P0=Long.valueOf(0);
    tkCR1P1=Long.valueOf(0);
    tkCR1P2=Long.valueOf(0);
    tkCR1P3=Long.valueOf(0);
    tkCR1P4=Long.valueOf(0);
    tkCR1ARTEC=Long.valueOf(0);

    tkCG1P0=Long.valueOf(0);
    tkCR2P1=Long.valueOf(0);
    tkCG1P2=Long.valueOf(0);
  
  }
  
  @Override
  public Boolean configParams() {
   
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
   
    dataRif=(Date) param.get(ALuncherElabs.DATAINIELB);
    if(dataRif==null)
      return Boolean.FALSE;
    
    return  Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    
    _logger.info(" Inizio elaborazione files sicurezza -- GG :"+dataRif);
    Map mappa;
    try {
      mappa = FilePropUtils.getInstance().readFilePropKeyVal("../props/XlsManutSicur.properties");
      String pathDest=(String) mappa.get(PATHDEST);
      String pathFileCruscotto=(String) mappa.get(FILECRUSCOTTO);
      if(StringUtils.IsEmpty(pathDest) || StringUtils.IsEmpty(pathFileCruscotto)){
        _logger.error("Proprietà "+PATHDEST+" e "+FILECRUSCOTTO+" non valorizzata. Impossibile proseguire nell'elaborazione!");
        return;
      }
      //copia preventiva del file cruscotto
      String fileNameC=FileUtils.getShortFileName(pathFileCruscotto);
      FileUtils.copyFile(pathFileCruscotto, pathDest+fileNameC);
      
      if(mappa!=null && !mappa.isEmpty())
        elabFilesPiani(mappa);
      
      elabFileCruscotto(pathFileCruscotto);
      
    } catch (FileNotFoundException ex) {
      addError("Impossibile completare elaborazione.File manutenzioni.properties non trovato -> "+ex.getMessage());
    } catch (IOException ex) {
      addError("Impossibile completare elaborazione.Problemi di accesso al file manutenzioni.properties -> "+ex.getMessage());
    }
    
  }
  
  
 
  
  private void elabFilesPiani(Map mappaFiles){
    Set keys=mappaFiles.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()){
      String codice=ClassMapper.classToString(iter.next());
      if(codice!=null && codice.contains(PREFIXSICUREZZA)){
        String filePathCompleto=ClassMapper.classToString(mappaFiles.get(codice));
        if(codice.contains(PREFIXFILER1)|| codice.contains(PREFIXFILEG1)|| codice.contains(PREFIXFILER2)){  //||
          elabFileSicurezza(filePathCompleto);
        }
      }
    }
  }
  
  
  private void elabFileSicurezza(String pathFileCompleto) {
    FileXlsXPoiR xls=null;
    Long tkAperti=Long.valueOf(0);
    Long tkChiusi=Long.valueOf(0);
    int rows=0;
    int i=0;
    try{
      _logger.info("Inizio elaborazione file "+pathFileCompleto);
      xls=new FileXlsXPoiR(pathFileCompleto);
      xls.prepareFileXls();
      XSSFSheet sheet=xls.getSheet(SHEETTKSIC);
      rows=sheet.getLastRowNum();
      i=RG_INIZIO_TK;
      while(i<rows){
        XSSFRow riga=sheet.getRow(i);    
        if(riga!=null){
          XSSFCell cell =riga.getCell(CL_STATO_TK);
          if(cell!=null){
            String stato=riga.getCell(CL_STATO_TK).getStringCellValue();  
            if(stato!=null && !stato.isEmpty()){
              if(TKCHIUSO.equals(stato.toUpperCase())){
                tkChiusi++;
              }else{
                tkAperti++;
              }
            }
          }
        }
       i++; 
      }  
      aggDatiTot(pathFileCompleto, tkAperti, tkChiusi);
      
   
    } catch(IOException ex){
      addError("Impossibile trovare il file"+pathFileCompleto+" --> "+ex.getMessage());
    } finally{
      _logger.info("Righe file :"+rows+ " righe processate:"+i);
      try {
        xls.closeRFile();
        _logger.info("Fine elaborazione file "+pathFileCompleto);
      } catch (IOException ex) {
        _logger.info("Errore chiusura file "+pathFileCompleto);
      }
    }
  }
  
  
  private void elabFileCruscotto(String pathFileC){
    try{
       _logger.info("Elaborazione file cruscotto : "+pathFileC);
      XlsCrusscottoKaizen xls=new XlsCrusscottoKaizen(pathFileC);
      xls.prepareFileXls();
     
      

      Integer sett=DateUtils.getWorkWeek(dataRif);
      //Aggiorno Sheet R1
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1P0, tkAR1P0, tkCR1P0);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1P1, tkAR1P1, tkCR1P1);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1P2, tkAR1P2, tkCR1P2);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1P3, tkAR1P3, tkCR1P3);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1P4, tkAR1P4, tkCR1P4);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETR1STORICO, XlsCrusscottoKaizen.R1ARTEC, tkAR1ARTEC, tkCR1ARTEC);
      //Aggiorno Sheet G1 R2
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETG1R2STORICO,XlsCrusscottoKaizen.G1P0, tkAG1P0, tkCG1P0);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETG1R2STORICO,XlsCrusscottoKaizen.G1P2, tkAG1P2, tkCG1P2);
      xls.aggSheetConsuntivoPiano(sett, XlsCrusscottoKaizen.SHEETG1R2STORICO,XlsCrusscottoKaizen.R2P1, tkAR2P1, tkCR2P1);
      
   
       
      xls.closeAndSaveWFile();
      _logger.info("Salvataggio file "+pathFileC+ " completato.");
    }catch(IOException ex){
      addError("Impossibile trovare il file"+pathFileC+" --> "+ex.getMessage());
    }
  }
  
  
   
  private void aggDatiTot(String pathFileCompleto, Long tkA,Long tkC) {
    if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.P0)){
      tkAR1P0+=tkA;
      tkCR1P0+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.P1)){
      tkAR1P1+=tkA;
      tkCR1P1+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.P2)){
      tkAR1P2+=tkA;
      tkCR1P2+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.P3)){
      tkAR1P3+=tkA;
      tkCR1P3+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.ARTEC)){
      tkAR1ARTEC+=tkA;
      tkCR1ARTEC+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R1+"_"+XlsCrusscottoKaizen.P4)){
      tkAR1P4+=tkA;
      tkCR1P4+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.G1+"_"+XlsCrusscottoKaizen.P0)){
      tkAG1P0+=tkA;
      tkCG1P0+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.G1+"_"+XlsCrusscottoKaizen.P2)){
      tkAG1P2+=tkA;
      tkCG1P2+=tkC;
    }else if(pathFileCompleto.contains(XlsCrusscottoKaizen.R2)){
      tkAR2P1+=tkA;
      tkCR2P1+=tkC;
    } 
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabXlsSicurezza.class);

  
}
