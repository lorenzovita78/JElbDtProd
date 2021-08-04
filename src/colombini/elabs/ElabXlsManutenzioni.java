/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.xls.cartelliniKaizen.XlsManutenzioniStd;
import colombini.xls.cartelliniKaizen.XlsMntzCrusscotto;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import objBean.TicketSettBean;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FilePropUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabXlsManutenzioni extends ElabClass{

  public final static String PATHDEST="pathMAN.backup";
  public final static String PREFIXFILER1="R1_";
  public final static String PREFIXFILER2="R2_";
  public final static String PREFIXFILEG1="G1_";
  public final static String FILECRUSCOTTO="fileMANCruscotto";
  
  public final static String PREFIXMANUTENZIONE="fileMAN";
  
  private Long tkAR1P0;
  private Long tkAR1P1;
  private Long tkAR1P2;
  private Long tkAR1P3;
  private Long tkAR1P4;
  private Long tkAR1ARTEC;
  private Long tkAR1FEBAL;
  
  private Long tkAG1P0;
  private Long tkAR2P1;
  private Long tkAG1P2;
  
  private Long tkCR1P0;
  private Long tkCR1P1;
  private Long tkCR1P2;
  private Long tkCR1P3;
  private Long tkCR1P4;
  private Long tkCR1ARTEC;
  private Long tkCR1FEBAL;
  
  private Long tkCG1P0;
  private Long tkCR2P1;
  private Long tkCG1P2;
  
  
  private Date dataRif;
  
  public ElabXlsManutenzioni() {
    
    tkAR1P0=Long.valueOf(0);
    tkAR1P1=Long.valueOf(0);
    tkAR1P2=Long.valueOf(0);
    tkAR1P3=Long.valueOf(0);
    tkAR1P4=Long.valueOf(0);
    tkAR1ARTEC=Long.valueOf(0);
    tkAR1FEBAL=Long.valueOf(0);

    tkAG1P0=Long.valueOf(0);
    tkAR2P1=Long.valueOf(0);
    tkAG1P2=Long.valueOf(0);

    tkCR1P0=Long.valueOf(0);
    tkCR1P1=Long.valueOf(0);
    tkCR1P2=Long.valueOf(0);
    tkCR1P3=Long.valueOf(0);
    tkCR1P4=Long.valueOf(0);
    tkCR1ARTEC=Long.valueOf(0);
    tkCR1FEBAL=Long.valueOf(0);

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
    _logger.info(" Inizio elaborazione files manutenzioni -- GG :"+dataRif);
    Map mappa;
    try {
      mappa = FilePropUtils.getInstance().readFilePropKeyVal("../props/XlsManutSicur.properties");
//      mappa = FilePropUtils.getInstance().readFilePropKeyVal("D:/testjars/manutenzioni.properties");
      String pathDest=(String) mappa.get(PATHDEST);
      String pathFileCruscotto=(String) mappa.get(FILECRUSCOTTO);
      if(StringUtils.IsEmpty(pathDest) || StringUtils.IsEmpty(pathFileCruscotto)){
        _logger.error("Proprietà "+PATHDEST+" e "+FILECRUSCOTTO+" non valorizzata. Impossibile proseguire nell'elaborazione!");
        return;
      }
      //copia preventiva dei file
      copyFiles(mappa, pathDest);
      
      if(mappa!=null && !mappa.isEmpty())
        elabFilesManLinee(mappa);
      
      elabFileCruscottoMan(pathFileCruscotto);
      
    } catch (FileNotFoundException ex) {
      addError("Impossibile completare elaborazione.File manutenzioni.properties non trovato -> "+ex.toString());
    } catch (IOException ex) {
      addError("Impossibile completare elaborazione.Problemi di accesso al file manutenzioni.properties -> "+ex.toString());
    }
    
  }


  
  private void copyFiles(Map mappaFiles,String pathDest){
    Set keys=mappaFiles.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()){
      String codice=ClassMapper.classToString(iter.next());
      if(codice!=null && codice.contains(PREFIXMANUTENZIONE)){
        String filePathCompleto=ClassMapper.classToString(mappaFiles.get(codice));
        String fileName=FileUtils.getShortFileName(filePathCompleto);
        try{
          //copia di sicurezza del file
          FileUtils.copyFile(filePathCompleto, pathDest+fileName);
        }catch(IOException ex){
          addError("Impossibile salvare il file"+fileName+ " nel percorso "+pathDest+" --> "+ex.toString());
        }  
      }
    }
  }
  
  
  private void elabFilesManLinee(Map mappaFiles){
    Set keys=mappaFiles.keySet();
    Iterator iter=keys.iterator();
    while (iter.hasNext()){
      String codice=ClassMapper.classToString(iter.next());
      if(codice!=null && codice.contains(PREFIXMANUTENZIONE) ){
        String filePathCompleto=ClassMapper.classToString(mappaFiles.get(codice));
        if(codice.contains(PREFIXFILER1)|| codice.contains(PREFIXFILEG1)|| codice.contains(PREFIXFILER2)){  //||
          elabFileManLineaStd(filePathCompleto);
        }
      }
    }
  }
  
  
  private void elabFileManLineaStd(String pathFileCompleto) {
    XlsManutenzioniStd xls=null;
    try{
      _logger.info("Inizio elaborazione file "+pathFileCompleto);
      
      xls=new XlsManutenzioniStd(pathFileCompleto);
      xls.prepareFileXls();
      
      
      TicketSettBean ts=xls.getInfoTicketsSett(dataRif);
      aggDatiTot(pathFileCompleto,ts);
      xls.elabDatiSett(dataRif, ts);
      _logger.info("Caricamento ed elaborazione dati file "+pathFileCompleto + " completati.");
      xls.closeAndSaveWFile();
      _logger.info("Salvataggio file "+pathFileCompleto + " completato.");
      
    } catch (ParseException ex) {
      addError("Problemi durante l'elaborazione del file "+pathFileCompleto+" --> "+ex.toString());
    } catch(IOException ex){
      addError("Impossibile trovare il file"+pathFileCompleto+" --> "+ex.toString());
    } catch(RuntimeException ex){
      addError("Errore a runtime --> "+ex.toString());
    } finally{
      try {
        xls.closeFiles();
        _logger.info("Fine elaborazione file "+pathFileCompleto);
      } catch (IOException ex) {
        _logger.info("Errore chiusura file "+pathFileCompleto);
      }
    }
  }
  
  
  private void elabFileCruscottoMan(String pathFileC){
    try{
       _logger.info("Elaborazione file cruscotto : "+pathFileC);
      XlsMntzCrusscotto xls=new XlsMntzCrusscotto(pathFileC);
      xls.prepareFileXls();
     
      
      Integer sett=DateUtils.getWorkWeek(dataRif);
      //Aggiorno Sheet R1
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1P0, tkAR1P0, tkCR1P0);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1P1, tkAR1P1, tkCR1P1);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1P2, tkAR1P2, tkCR1P2);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1P3, tkAR1P3, tkCR1P3);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1P4, tkAR1P4, tkCR1P4);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1ARTEC, tkAR1ARTEC, tkCR1ARTEC);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETR1STORICO, XlsMntzCrusscotto.R1FEBAL, tkAR1FEBAL, tkCR1FEBAL);
      //Aggiorno Sheet G1 R2
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETG1R2STORICO,XlsMntzCrusscotto.G1P0, tkAG1P0, tkCG1P0);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETG1R2STORICO,XlsMntzCrusscotto.G1P2, tkAG1P2, tkCG1P2);
      xls.aggSheetConsuntivoPiano(sett, XlsMntzCrusscotto.SHEETG1R2STORICO,XlsMntzCrusscotto.R2P1, tkAR2P1, tkCR2P1);
      
   
      xls.closeAndSaveWFile();
      _logger.info("Salvataggio file "+pathFileC+ " completato.");
    }catch(IOException ex){
      addError("Impossibile trovare il file"+pathFileC+" --> "+ex.toString());
    }
  }
  
  
  private void aggDatiTot(String pathFileCompleto, TicketSettBean ts) {
    if(pathFileCompleto.contains(XlsMntzCrusscotto.R1P0)){
      tkAR1P0+=ts.getTkAperti();
      tkCR1P0+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1P1)){
      tkAR1P1+=ts.getTkAperti();
      tkCR1P1+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1P2)){
      tkAR1P2+=ts.getTkAperti();
      tkCR1P2+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1P3)){
      tkAR1P3+=ts.getTkAperti();
      tkCR1P3+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1P4)){
      tkAR1P4+=ts.getTkAperti();
      tkCR1P4+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1ARTEC)){
      tkAR1ARTEC+=ts.getTkAperti();
      tkCR1ARTEC+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R1FEBAL)){
      tkAR1FEBAL+=ts.getTkAperti();
      tkCR1FEBAL+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.G1P0)){
      tkAG1P0+=ts.getTkAperti();
      tkCG1P0+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.G1P2)){
      tkAG1P2+=ts.getTkAperti();
      tkCG1P2+=ts.getTkChiusi();
    }else if(pathFileCompleto.contains(XlsMntzCrusscotto.R2P1)){
      tkAR2P1+=ts.getTkAperti();
      tkCR2P1+=ts.getTkChiusi();
    } 
  }
  
  
  private static final Logger _logger = Logger.getLogger(ElabXlsManutenzioni.class);

  
}
