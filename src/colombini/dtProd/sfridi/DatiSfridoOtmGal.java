/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.costant.CostantsColomb;
import colombini.elabs.NameElabs;
import colombini.logFiles.G1P2.LogOttimizzatoreG1P2;
import exception.ElabException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DatiSfridoOtmGal extends  InfoSfridoCdL {

  public DatiSfridoOtmGal(String cdL) {
    super(cdL);
  }
 
    
  
 
  
  @Override
  public List getInfoSfrido(Date dataInizio,Date dataFine) throws ElabException {
    List info=new ArrayList();
    try {
      if(dataInizio==null || dataFine==null){
        _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
        return null;
      }
      
      dataInizio=DateUtils.getInizioGg(dataInizio);
      // imposto la data fine con l'orario delle 23:59:59
      dataFine=DateUtils.getFineGg(dataFine);

      List<File> filesOtt=new ArrayList();
      
      filesOtt=getOptiFiles(dataInizio,dataFine); 
      if(filesOtt==null || filesOtt.isEmpty()){
        _logger.warn("Attenzione nessuna ottimizzazione trovata per il periodo "+dataInizio + " e " +dataFine);
        return null;
      }

      info=getListInfoOttimizzatore(filesOtt);
  
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date impossibile calcolare i dati di sfrido :"+ex.getMessage());
      throw new ElabException(ex);
    }
    
    return info;
  }
  
  /**
   * Processa i file di ottimizzazione e restituisce una lista contenente le informazioni utili 
   * @param List filesOtt lista dei file relativi alle ottimizzazioni
   * @return List dati di ottimizzazione elaborati da salvare su DB
   */
  private List getListInfoOttimizzatore(List<File> filesOtt) throws ElabException{
    List info=new ArrayList();
    
    for(File f: filesOtt){
        List record=new ArrayList();
        Date data=new Date(f.lastModified());
        
        LogOttimizzatoreG1P2 flo=new LogOttimizzatoreG1P2(f);
        
         try{
           flo.elabFile();
           //escludiamo le elaborazioni dalla 757201 alla 757299(12) e dalla 757401 alla 757499(2) 
           if( (flo.getNumOttimizzazione()>=757201 && flo.getNumOttimizzazione()<=757299) || 
                (flo.getNumOttimizzazione()>=757401 && flo.getNumOttimizzazione()<=757499)   ){
             _logger.info("Ottimizzazione n."+flo.getNumOttimizzazione()+" di test.Dati non salvati");
           }else{
             record.add(CostantsColomb.AZCOLOM);
             record.add(ISfridoInfo.OTMGALP2);
             record.add(DateUtils.getDataForMovex(data));
             record.add(flo.getNumOttimizzazione());
             record.add(flo.getCodicePnp());
             record.add(flo.getCodicePan());
             record.add(Long.valueOf(0));//lunghezza
             record.add(Long.valueOf(0));
             record.add(flo.getSpessorePan());
             
             record.add(flo.getNumPannelli());
             record.add(Long.valueOf(0));
             record.add(arrotondaD(flo.getM2Totali(), 3));
             record.add(arrotondaD(flo.getM2SMLUtil(),3));
             record.add(Long.valueOf(0));
             //superficie sovraproduzione --> lo distinguo dal campo pers1 perchè in alcuni casi particolari viene azzerato
             record.add(flo.getM2SovraProd()); 
             record.add(flo.getM2SfridoRifiloIni()); 
             record.add(arrotondaD(flo.getM2SfridoFisiologico(),3));
             
             //aggiunta mq manuali su campo pers1
             record.add(flo.getM2Manual());
             //aggiunta mq Rifilo su campo pers2 
             record.add(Double.valueOf(0));
             
             //campo pers3 
             record.add(Double.valueOf(0));
             
             //campo pers4 
             record.add(Double.valueOf(0));
             
             info.add(record);
           }
         } catch (IOException e){
           _logger.error("Problemi di accesso al file "+flo.toString()+" : "+e.getMessage());
           throw new ElabException(e);
           
         }  
      }
    
    
    return info;
  }
  
  
  private Double arrotondaD(Double d, Integer ncifre){
    return Math.round( d * Math.pow( 10, ncifre ) )/Math.pow( 10, ncifre );
  }
  
  
  /**
   * Torna la lista dei file da processare in base alle date dell'oggetto (data Inizio e dataFine)
   * @return 
   */
  private List getOptiFiles(Date dataInizio,Date dataFine){
    _logger.info ("Ricerca file ottimizzazioni tra "+dataInizio.toString() + " e "+ dataFine.toString());
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBSFRIDOTMGAL);
    String pathfile=(String) getParams().get(NameElabs.PATHFILEOTTIMIZZG1P2);
    File directory=new File(pathfile);
    
    if(!directory.canRead()){
      _logger.warn("Attenzione!!Percorso di rete non raggiungibile..Impossibile calcolare dati ottimizzazioni.");
      return null;
    }
      
    
    List<File> optiFiles=new ArrayList();
    List<String> dirNames=ArrayUtils.getListFromArray(directory.list());
    Long nFile=Long.valueOf(0);
    Long nFileOpti=Long.valueOf(0);
    for(String dname:dirNames){
      nFile++;
      if( dname.endsWith(".l") && !dname.contains("_")){
        File fOtt=new File(pathfile+"/"+dname+"/0000.ncl");
        Date dataF=new Date(fOtt.lastModified());
        if(fOtt.isFile() && DateUtils.afterEquals(dataF, dataInizio) && DateUtils.beforeEquals(dataF, dataFine)){
          optiFiles.add(fOtt);
          nFileOpti++;
        }   
      }
    }
    
    _logger.info(" Numero file interrogati-->"+nFile + " ; Numero file da processare-->"+nFileOpti);
    
    return optiFiles;
  }
  
  
  
  
  public void elabSfridoManual(Date dataInizio,Date dataFine){
    try {
      if(dataInizio==null || dataFine==null){
        _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
        return;
      }
      
      dataInizio=DateUtils.getInizioGg(dataInizio);
      // imposto la data fine con l'orario delle 23:59:59
      dataFine=DateUtils.getFineGg(dataFine);

      List<File> filesOtt=new ArrayList();
      
      filesOtt=getOptiFiles(dataInizio,dataFine); 
      if(filesOtt==null || filesOtt.isEmpty()){
        _logger.warn("Attenzione nessuna ottimizzazione trovata per il periodo "+dataInizio + " e " +dataFine);
        return;
      }

      elabInfoOtmSMLManual(filesOtt);
  
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date impossibile calcolare i dati di sfrido :"+ex.getMessage());
    }
    
    
  }
  
  
  private void elabInfoOtmSMLManual(List<File> filesOtt){
    
    System.out.println("NUMOTM;DATA;MESE;MQUTIL;MQMANUALI");
    for(File f: filesOtt){
        
        Date data=new Date(f.lastModified());
        //data e ora dell'ottimizzazione
        Long dataL=DateUtils.getDataForMovex(data);
        
        
        LogOttimizzatoreG1P2 flo=new LogOttimizzatoreG1P2(f);
        
         try{
           
            Double val=flo.checkOtm();
           //escludiamo le elaborazioni dalla 757201 alla 757299(12) e dalla 757401 alla 757499(2) 
           if( (flo.getNumOttimizzazione()>=757201 && flo.getNumOttimizzazione()<=757299) || 
                (flo.getNumOttimizzazione()>=757401 && flo.getNumOttimizzazione()<=757499)   ){
             _logger.info("Ottimizzazione n."+flo.getNumOttimizzazione()+" di test.Dati non validi");
           }else if(val>0){
             
             System.out.println(flo.getNumOttimizzazione()+  " ; "+dataL+" ; "+DateUtils.DateToStr(data,"MM")+" ; "+flo.getM2SMLUtil()+" ;" + val);
             
           }
         } catch (IOException e){
           _logger.error("Problemi di accesso al file "+flo.toString()+" : "+e.getMessage());
         } catch (ParseException ex){
           _logger.error("Problemi di conversione della data per ottimizzazione n."+flo.toString()+" : "+ex.getMessage());
         } 
      }
    
  }
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoOtmGal.class); 
      
}

