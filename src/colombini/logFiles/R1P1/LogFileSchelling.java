/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;

import colombini.costant.NomiLineeColomb;
import colombini.logFiles.ALogFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileSchelling extends ALogFile {
   
  private Map tempiBande=null;
  private Map tempiRifilo=null;
  private Map fattPerc=null;
  
  private static final Double SECXMM=Double.valueOf("0.0068");
  
  private static final Integer BANDA1=Integer.valueOf(1);
  private static final Integer BANDA2=Integer.valueOf(2);
  private static final Integer BANDA3=Integer.valueOf(3);
  
  public static final Long CMTAGLIOIMAANTE=Long.valueOf(550);
//Valore x anno 2012-->  public static final Long CMTAGLIOIMATOP=Long.valueOf(410);
  public static final Long CMTAGLIOIMATOP=Long.valueOf(615); //valore x anno 2013
  
  public static final String TEMPOLORDO="TEMPOLORDO";
  public static final String TEMPOSCARTO="TEMPOSCARTO";
  public static final String TEMPORUN="TEMPORUN";
  public static final String TEMPOMICRO="TEMPOMICRO";
  
  public static final String CICLITOT="CICLITOT";
  public static final String BANDETOT="BANDETOT";
  
  
  
  private static final Logger _logger = Logger.getLogger(LogFileSchelling.class); 
  
  
  public String dateFormat;
  
  public LogFileSchelling(String pFile) {
    super(pFile);
    this.dateFormat="dd/MM/yyyy";
  }

  public LogFileSchelling(String pFile,String dateFormat) {
    super(pFile);
    this.dateFormat=dateFormat;
  }
  
  
  @Override
  public void initialize() throws FileNotFoundException {
    super.initialize();
    initializeMappeTempi();
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }
  
  
  private void initializeMappeTempi(){
    tempiBande=new HashMap();
    tempiRifilo=new HashMap();
    fattPerc=new HashMap();
    
    tempiBande.put(BANDA1, Double.valueOf("4.64"));
    tempiBande.put(BANDA2, Double.valueOf("5.01"));
    tempiBande.put(BANDA3, Double.valueOf("6.13"));
    
    tempiRifilo.put(BANDA1, Double.valueOf("6.57"));
    tempiRifilo.put(BANDA2, Double.valueOf("7.36"));
    tempiRifilo.put(BANDA3, Double.valueOf("7.97"));
    
    fattPerc.put(BANDA1, Double.valueOf("0.333"));
    fattPerc.put(BANDA2, Double.valueOf("0.666"));
    fattPerc.put(BANDA3, Double.valueOf("1"));
  }
 
  
  
  @Override
  public Map processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException{
    if(getBfR()==null)
      return null;
    
    String riga;  
    Date fineGg=DateUtils.getFineGg(dataInizio);
    Integer tagli=Integer.valueOf(0);
    Integer tagliScarti=Integer.valueOf(0);
    Long cicliTot= Long.valueOf(0);
    Long bandeTot= Long.valueOf(0);
    Integer numBanda=Integer.valueOf(0);
    Integer dimBandaBuona=Integer.valueOf(0);
    Integer dimBandaScarto=Integer.valueOf(0);
    
    Double tempoLordo=Double.valueOf(0);
    Double tempoNetto=Double.valueOf(0);
    Double tempoScarto=Double.valueOf(0);
    Double tempoMicro=Double.valueOf(0);
    RowInfoLogSchelling rSOld=null;
    
    //    -- Test
    Date dtFinSez=null;
    Date dtIniSez=null;
    Long diffSec=null;
    Long count=Long.valueOf(0);
    //   --  Test
//    System.out.println(" DIFF 2 FOR ; TNETTO ; TLORDO ;TSCARTO ; A-C ; A-(C+D) ");
    
    riga = getBfR().readLine();  
    //scorriamo il file
    while(riga!=null && !riga.isEmpty()){
      RowInfoLogSchelling rS=new RowInfoLogSchelling(riga,dateFormat);
      Date dataTmp=rS.getDataOra();
      //controlliamo che la data non sia successiva a quella che dobbiamo processare
      if(dataTmp!=null && dataTmp.after(fineGg)){
        break;
      } 
        
      if(dataTmp!=null && dataTmp.after(dataInizio) && dataTmp.before(dataFine)){
        count++;
        //carico le info
        rS.loadInfoRow();
        //verifica se siamo nel primo pezzo della giornata
        if(rS.isPresentSezionatura() && (rSOld!=null && rSOld.isAssenteSezionatura()) ){
          if(rS.isPezzoScarto()){
            tagli=Integer.valueOf(0);
            tagliScarti = Integer.valueOf(-1);
          }else{
            tagli=Integer.valueOf(1);
            tagliScarti = Integer.valueOf(0);
          }
          numBanda=0;
          dimBandaBuona=Integer.valueOf(0);
          dimBandaScarto=Integer.valueOf(0);
          dtIniSez=dataTmp;
          if(dtFinSez!=null && dtFinSez.before(dtIniSez)){
            diffSec=DateUtils.numSecondiDiff(dtFinSez, dtIniSez);
          }
        }
        //controlliamo se siamo usciti dalla sezionatura
        if(rS.isAssenteSezionatura() && (rSOld!=null && rSOld.isPresentSezionatura())){
          if(rSOld.isPezzoScarto()){
            tagli--;
            tagliScarti--;
          }
          Integer tagliBuoni=tagli-tagliScarti-2;
          
          Double tmpTempoN= getTempoNetto(tagliBuoni, dimBandaBuona, numBanda);  
          Double tmpTempoL= getTempoLordo(tagliBuoni, dimBandaBuona, numBanda); 
          Double tmpTempoS=getTempoScarto(tagliScarti, dimBandaScarto, numBanda);
          tempoScarto+=tmpTempoS;
          tempoLordo+=tmpTempoL;
          tempoNetto+=tmpTempoN;
          
          if(diffSec!=null){
            Double appo=diffSec-(tmpTempoL+tmpTempoS);
            if(appo>0 && appo<180)
              tempoMicro+=appo;

//            System.out.println(diffSec+ " ; "+tmpTempoN.longValue()+" ; "+tmpTempoL.longValue() +
//                               " ; "+tmpTempoS.longValue()+ " ; "+(diffSec-tmpTempoL.longValue())+  
//                               " ; "+(diffSec-(tmpTempoL.longValue()+tmpTempoS.longValue())) );
            diffSec=null;
          } 
          cicliTot++;
          bandeTot+=numBanda;
          dtFinSez=dataTmp; 
        }
        if(rS.isPresentSezionatura()){
          numBanda=rS.getNumBanda();
          if(rS.getNumBanda()==1){
            tagli++;
            if(rS.isPezzoScarto()){
              tagliScarti++;
              dimBandaScarto+=rS.getDimensionePezzo().intValue();
            }else{
              dimBandaBuona+=rS.getDimensionePezzo().intValue();
            }
          }
        }
        rSOld=rS;
      } 
      //fine while mi sposto di 1 riga
      
      riga = getBfR().readLine();
    }
    _logger.info("Righe processate "+count);
    Map mappaValori=new HashMap();
    mappaValori.put(TEMPOLORDO, tempoLordo);
    mappaValori.put(TEMPOSCARTO, tempoScarto);
    mappaValori.put(TEMPORUN, tempoNetto);
    mappaValori.put(TEMPOMICRO,tempoMicro);
    mappaValori.put(BANDETOT, bandeTot);
    mappaValori.put(CICLITOT, cicliTot);
    
    return mappaValori;
  }
  
 
  
  private Double getTempoLordo(Integer tagliBuoni,Integer dimBandaBuona,Integer numBanda){
    Double tempoLordo=Double.valueOf(0);
    Double tempoBanda=(Double) tempiBande.get(numBanda);
    Double tempoRifilo=(Double) tempiRifilo.get(numBanda);
    tempoLordo=(tagliBuoni*tempoBanda)+(2*tempoRifilo)+(dimBandaBuona+100)*SECXMM;
    
    return tempoLordo;
  }
  
  private Double getTempoScarto(Integer tagliScarti,Integer dimBandaScarto,Integer numBanda){
    Double tempoScarto=Double.valueOf(0);
    Double tempoBanda=(Double) tempiBande.get(numBanda);
    
    tempoScarto=(tagliScarti*tempoBanda)+(dimBandaScarto*SECXMM);
    
    return tempoScarto;
  }
  
  private Double getTempoNetto(Integer tagliBuoni,Integer dimBandaBuona,Integer numBanda){
    Double tempoNetto=Double.valueOf(0);
    Integer tre=new Integer(3);
    Double tempoBanda=(Double) tempiBande.get(tre);
    Double tempoRifilo=(Double) tempiRifilo.get(tre);
    Double perc=(Double) fattPerc.get(numBanda);
    
    tempoNetto=(  (tagliBuoni*tempoBanda)+(2*tempoRifilo)+( (dimBandaBuona+100)*SECXMM)   )*perc;
    
    return tempoNetto;
  }
  
  
  
  @Override
  public String createFileLogGg(Date data,PrintStream ps) throws IOException, ParseException{
    if(getBfR()==null)
      return "";
    
    String riga;  
    Date dataFine=DateUtils.getFineGg(data);
    Date mezzanotte=DateUtils.getMezzanotte(data);
    riga = getBfR().readLine();  
    long countG=0;
    long countP=0;
    //scorriamo il file
    while(riga!=null){ 
      countP++;
      if(!riga.isEmpty()){
        RowInfoLogSchelling rS=new RowInfoLogSchelling(riga,dateFormat);
        Date dataTmp=rS.getData();
        //controlliamo che la data non sia successiva a quella che dobbiamo processare
        if(dataTmp!=null && dataTmp.after(dataFine)){
          break;
        } 
        if(dataTmp!=null && dataTmp.compareTo(mezzanotte)==0){
            ps.println(riga);
            countG++;
        }  
      }
      riga = getBfR().readLine();
    }
    _logger.info("Righe processate: "+countP+" Righe generate: "+countG);
    
    return "Righe processate: "+countP+" Righe generate: "+countG;
  }

  
  /**
   * Torna la quantità di materiale utilizzato dall'utensile associato alla sezionatrice
   * @param data
   * @param larghPann
   * @return Double valore di materiale utilizzato
   * @throws IOException
   * @throws ParseException 
   */
  public Double getUtilMateriale(Date data,Long larghPann) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    
    String riga;  
    RowInfoLogSchelling rSOld=null;
    RowInfoLogSchelling rS=null;
    riga = getBfR().readLine();  
    Long count=Long.valueOf(1);
    Date fineGg=DateUtils.getFineGg(data);
    Integer numPn=Integer.valueOf(0);
    Integer numTg=Integer.valueOf(0);
    Map materiali=new HashMap();
    Long valTaglio=Long.valueOf(0);
    
    while(riga!=null && !riga.isEmpty()){
      rS=new RowInfoLogSchelling(riga,dateFormat);
      Date dataLog=rS.getDataOra();

      if(dataLog!=null && dataLog.after(fineGg)){
        break;
      } 
      if(dataLog!=null && DateUtils.daysBetween(dataLog, data)==0){
        //calcolo utilizzo materiali\\
        rS.loadInfoRow();
        
        if(rS.isAssenteSezionatura() && (rSOld!=null && rSOld.isPresentSezionatura())){
          numPn=rSOld.getNumBanda();
          numTg=rSOld.getNumTagli()+1;
          valTaglio+=numPn*numTg*larghPann;
        }
        
        rSOld=rS;
      }
      
      riga = getBfR().readLine();
      count++;
    } 
    
    _logger.info("File "+getFileName()+" righe processate:"+count);
    
    return valTaglio/new Double(1000);
  }

  

  
}
