/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;

import colombini.logFiles.ALogFile;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;

/**
 * Classe per la gestione del file di log delle Combima Ima Ante (forse anche Ima top)
 * @author lvita
 */
public class LogFileCombima extends ALogFile{
  
  private static final Long  TPMEDPZSTD=Long.valueOf(10);
  private static final Long  TPMEDRIDUZ=Long.valueOf(20);
  private static final Long  TPMEDLAVCR=Long.valueOf(10);
  
  //limite superiore e inferiore per considerare un tempo microfermo
  //ad ora vengono settati uguali 
  private static final Long  TMICROLSUP=Long.valueOf(180);
  private static final Long  TMICROLINF=Long.valueOf(180);
  
  public static final String  TEMPORUN="TEMPORUN";
  public static final String  TEMPOSCARTO="TEMPOSCARTO";
  public static final String  TEMPOPASSINUTILE="TEMPOPASSINUTILE";
  public static final String  TOTPEZZI="TOTPEZZI";
  public static final String  TOTPZTURNO="TOTPEZZITURNO";
  public static final String  TOTSCARTI="TOTSCARTI";
  public static final String  MICROFERMI="MICROFERMI";
  
  public static final String LAVSP1_L="LAVLUCIDASPALLA1";
  public static final String LAVSP1_N="LAVNORMALESPALLA1";
  public static final String LAVSP2_L="LAVLUCIDASPALLA2";
  public static final String LAVSP2_N="LAVNORMALESPALLA2";
  
  public static final String MATERIALI="MATERIALI";
  
  
  
  private static final Logger _logger = Logger.getLogger(LogFileCombima.class); 
  
  
  public LogFileCombima(String pFile) {
    super(pFile);
  }
  
  
  
  /**
   * Torna una lista contenente tutte le informazioni del file di log utili per 
   * @param String riga
   * @return List 
   * @throws ParseException 
   */
  private List getListInfoRow(String riga) throws ParseException{
    if(riga==null)
      return null;
    
    String[] logArray=null;
    
    if(riga.indexOf("[")<0)
      return null;
    
    String newInfo=riga.substring(riga.indexOf("[")+1, riga.lastIndexOf("]"));
    newInfo=newInfo.replace("][", "-");
    logArray=newInfo.split("-", 40);
    
    return ArrayUtils.getListFromArray(logArray);
  }
  
  
  

  
  /**
   * Processa il file di log caricando in una mappa tutte le informazioni necessarie per il calcolo degli OEE
   * @param Date data
   * @return Map 
   * @throws IOException
   * @throws ParseException 
   */
  @Override
  public Map processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    
    if(dataInizio==null || dataFine==null)
      return null;
    
    String riga;  
    
    Date fineGg=DateUtils.getFineGg(dataInizio);
    riga = getBfR().readLine();  
    RowInfoLogCombima rigaOld=null;
    RowInfoLogCombima rigaCorr=null;
    //valori per calcoli
    Long tempoRun=Long.valueOf(0);
    Long tempoPassInutile=Long.valueOf(0);
    Long tempoScarto=Long.valueOf(0);
    Long numPzTurno=Long.valueOf(0); 
    Long numPzTot=Long.valueOf(0);
    Long numScarti=Long.valueOf(0);
    Long tempoMicroF=Long.valueOf(0);
    
//    Map materiali=new HashMap();
    
    //variabili di appoggio
    Long tmptemporid=Long.valueOf(0);
    Long tmptemporun=Long.valueOf(0);
    Long deltaTempo=Long.valueOf(0);
    Boolean trattato=Boolean.FALSE;
    String lavOld="";
    String lavNew="";
    long count=0;
    long cambioBanda=0;
    //chiedere poi meglio a Alessandro cosa intende con questo flag.
    boolean riduzione=false;
    //scorriamo il file
    try{
//    System.out.println("RIGA ; TEMPORUNTIME ; TIPOLOGIA ; TEMPOULTERIORE");

    while(riga!=null && !riga.isEmpty()){
        count++;
        String ggLog=riga.substring(0,17);
        ggLog=ggLog.replace(".", "/");
        Date dateLog=DateUtils.StrToDate(ggLog, "dd/MM/yy HH:mm:ss");
        List info= getListInfoRow(riga);
        rigaCorr=new RowInfoLogCombima(dateLog,info);
        
        //controlliamo che la data non sia successiva a quella che dobbiamo processare
        if(dateLog!=null && dateLog.after(fineGg)){
          break;
        }  
        //controllo che sia cmq lo stesso giorno
        if(dateLog!=null && DateUtils.daysBetween(dateLog, dataInizio)==0){
          if(rigaCorr.getNumPasssaggio()==1){
            numPzTot++;
            if(RowInfoLogCombima.SCARTO.equals(rigaCorr.getTipoBanda())){
              numScarti++;
            }  
          }
      
        }
        if(dateLog!=null && dateLog.after(dataInizio) && dateLog.before(dataFine)){
          numPzTurno++;
          //se cambia il numero di bande non è consecutivo  allora sta cambiando la lavorazione e sommo i tempi
          if(rigaOld!=null && rigaOld.getNumeroBanda()+1!=rigaCorr.getNumeroBanda()){
            if(riduzione){ //
               tempoRun+=tmptemporid;
               tempoPassInutile+=tmptemporun;
               riduzione=false;
  //             System.out.println(count+" ; "+tmptemporid+ "; RID ; "+tmptemporun);
            }else{
              tempoRun+=tmptemporun;
    //          System.out.println(count+" ; "+tmptemporun+ "; NORM ");
            }
            tmptemporid=Long.valueOf(0);
            tmptemporun=Long.valueOf(0);
            cambioBanda++;
          }  
          
          if(rigaCorr.isRiduzione())
            riduzione=true;
          
          trattato=Boolean.FALSE;
          if(rigaOld!=null)
            deltaTempo=DateUtils.numSecondiDiff(rigaOld.getData(), rigaCorr.getData());
          
          //se il pz n_esimo (quello che si sta leggendo adesso) o il pz n-1_esimo ha avuto una riduzione (solo al primo passaggio)
          if( rigaOld!=null && (rigaCorr.isRiduzione() != rigaOld.isRiduzione() ) && deltaTempo>getTempoMedioRiduzioni()
             && rigaCorr.getNumPasssaggio()==1 ){
            tempoMicroF+=loadTempoMicroF(deltaTempo, getTempoMedioRiduzioni());
            deltaTempo=getTempoMedioRiduzioni();
            trattato=Boolean.TRUE;
          }
          lavNew="";
          if(rigaCorr.isLavLucida()){
            lavNew=rigaCorr.getTipoLavorazione18();
          }

          if(!lavOld.equals(lavNew) && deltaTempo>getTempoMedioLavCritiche()){
            //trattato  indica se il tempo ha già subito una riduzione. in caso positivo non verrà più trattato anche in seguito
            trattato=Boolean.TRUE;
            tempoMicroF+=loadTempoMicroF(deltaTempo, getTempoMedioLavCritiche());
            deltaTempo=getTempoMedioLavCritiche();
          }
          //se comunque c'e' un tempo troppo alto tra i pz
          if(deltaTempo > getTempoMedioPzStd() && !trattato){
            tempoMicroF+=loadTempoMicroF(deltaTempo, getTempoMedioPzStd());
            deltaTempo = getTempoMedioPzStd();
          }

          if(RowInfoLogCombima.SCARTO.equals(rigaCorr.getTipoBanda())){
            tempoScarto+=deltaTempo;
  //           System.out.println(count+" ; "+deltaTempo+ "; SCA");
          }else { 
            if(rigaCorr.getNumPasssaggio()==2){
               tempoRun+=deltaTempo;
   //            System.out.println(count+" ; "+deltaTempo+ "; PS2");
            }else if(rigaCorr.isRiduzione()){
              tmptemporid+=deltaTempo;
            }else {
              tmptemporun+=deltaTempo;
            }
          }
          lavOld="";
          if(rigaOld!=null && rigaOld.isLavLucida()){
            lavOld=rigaOld.getTipoLavorazione18();
          }          
        }
    rigaOld=rigaCorr;  
    //fine while mi sposto di 1 riga
    riga = getBfR().readLine();
    } 
    }catch(Exception ex){
     _logger.error(ex.getMessage()); 
    }finally{ 
      _logger.error("contatore"+count);
    }
    Map mappaValori=new HashMap();
    mappaValori.put(TEMPORUN,tempoRun);
    mappaValori.put(TEMPOPASSINUTILE,tempoPassInutile);
    mappaValori.put(TEMPOSCARTO,tempoScarto);
    mappaValori.put(TOTPEZZI,numPzTot);
    mappaValori.put(TOTPZTURNO,numPzTurno);
    mappaValori.put(TOTSCARTI,numScarti);
    mappaValori.put(MICROFERMI,tempoMicroF);

    
    return mappaValori;
  }
  
  
 
  @Override
  public String createFileLogGg(Date data,PrintStream ps) throws IOException, ParseException{
    if(getBfR()==null)
      return "";
    
    String riga;  
    Date dataInizio=DateUtils.getInizioGg(data);
    Date dataFine=DateUtils.getFineGg(data);
    riga = getBfR().readLine();  
    
    long countG=0;
    long countP=0;
    //scorriamo il file
    while(riga!=null){ 
      countP++;
      if(!riga.isEmpty()){
        String ggLog=riga.substring(0,17);
        ggLog=ggLog.replace(".", "/");
        Date dateLog=DateUtils.StrToDate(ggLog, "dd/MM/yy HH:mm:ss");  
        if(dateLog!=null && dateLog.after(dataFine))
          break;

        if(dateLog!=null && dateLog.after(dataInizio) && dateLog.before(dataFine)){
          countG++;
          ps.println(riga);
        }
      }
      //fine while mi sposto di 1 riga
      riga = getBfR().readLine();
    }
    _logger.info("Righe processate: "+countP+" Righe generate: "+countG);
    
    return " righe lette: "+countP+" righe scritte: "+countG;
  }
  
  
 /**
   * 
   * @param tempoP tempo intercorso tra un pezzo ed un altro
   * @param tempoRif tempo di riferimento della lavorazione(normale,lucida,riduzione)
   * @param tMicrofermi tempo microfermi  
   */
  private Long loadTempoMicroF(Long tempoP,Long tempoRif){
    Long difftempo=tempoP-tempoRif;
    //prevediamo un limite superiore e inferiore per i microfermi
    //questo perchè se si volessero considerare microfermi  tempi superiori ai 3 minuti ma inferiori a 10
    if(difftempo>0 && difftempo<=TMICROLSUP ){ 
      return Math.min(difftempo, TMICROLINF);
    }
    
    return Long.valueOf(0);
  }
  
  public Long getTempoMedioPzStd(){
    return TPMEDPZSTD;
  }
   
  public Long getTempoMedioRiduzioni(){
    return TPMEDRIDUZ;
  }
  
  public Long getTempoMedioLavCritiche(){
    return TPMEDLAVCR;
  }

  
  
  /**
   * Torna una mappa contenente le informazioni relative all'utilizzo degli utensili
   * @param data
   * @return
   * @throws IOException
   * @throws ParseException 
   */  
  public Map getMapUtilMateriali(Date data) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    
    String riga;  
    
    riga = getBfR().readLine();  
    Long count =Long.valueOf(1);
    RowInfoLogCombima rigaOld=null;
    RowInfoLogCombima rigaCorr=null;
    Date fineGg=DateUtils.getFineGg(data);
    
    Map materiali=new HashMap();
    
    while(riga!=null && !riga.isEmpty()){
        String ggLog=riga.substring(0,17);
        ggLog=ggLog.replace(".", "/");
        Date dateLog=DateUtils.StrToDate(ggLog, "dd/MM/yy HH:mm:ss");
        List info= getListInfoRow(riga);
        rigaCorr=new RowInfoLogCombima(dateLog,info);
        
        if(dateLog!=null && dateLog.after(fineGg)){
          break;
        } 
        if(dateLog!=null && DateUtils.daysBetween(dateLog, data)==0){
          //calcolo utilizzo materiali\\
          calcUtilizzoMateriale(rigaCorr, materiali);
        }
        riga = getBfR().readLine();
        count++;
    }
    _logger.info("File "+getFileName()+" righe processate:"+count);
    
    
    return materiali;
  }
  /**
   * Metodo che carica nella mappa dei materiali l'utilizzo del bordo
   * @param RowInfoLogCombima rigaLog
   * @param Map materiali 
   */
  private void  calcUtilizzoMateriale(RowInfoLogCombima rigaLog,Map <String,Double>materiali){
    if(rigaLog==null)
      return;
    
    //se la riga indica che ha processato uno scarto 
    if(RowInfoLogCombima.SCARTO.equals(rigaLog.getTipoBanda()))
      return;
    
    Double val1=Double.valueOf(0);
 
    //caso 1 :su entrambe le spalle il pezzo ha subito una riduzione-->
    if(rigaLog.isPresent1Rid() && rigaLog.isPresent2Rid() && !rigaLog.isProgTagliare() ){
      val1=rigaLog.getLargBandaRid();
      loadValMapMateriali(materiali, val1, rigaLog, "A");
    //caso 2  
    }else if (rigaLog.isPresent1Rid() && rigaLog.isPresent2Rid() && rigaLog.isProgTagliare()) {
      val1=rigaLog.getLungBanda();
      loadValMapMateriali(materiali, val1, rigaLog, "A");
    //caso 3  
    }else if(!rigaLog.isPresent1Rid() && rigaLog.isPresent2Rid() && rigaLog.isProgTagliare()){
      val1=rigaLog.getLungBanda();
      loadValMapMateriali(materiali, val1, rigaLog, "2");
    }
  }
  
  /**
   * Assegna il valore passato alla mappa controllando se la lavorazione sia lucida o meno e inserendolo per la spalla indicata
   * @param Map materiali
   * @param Double val valore da assegnare 
   * @param RowInfoLogCombima rigaLog rappresentazione della riga di log della Combima
   * @param String spalla     1-> spalla 1  2->spalla 2 A-> entrambe le spalle
   */
  private void loadValMapMateriali(Map <String,Double> materiali,Double val,RowInfoLogCombima rigaLog,String spalla){
    Double tmp1=Double.valueOf(0);
    Double tmp2=Double.valueOf(0);
    if(rigaLog.isLavLucida()){
      if( materiali.containsKey(LAVSP1_L) ){
        tmp1=materiali.get(LAVSP1_L);
      }
      if( materiali.containsKey(LAVSP2_L)  ){  
        tmp2=materiali.get(LAVSP2_L);
      }
      if("A".equals(spalla)){
        materiali.put(LAVSP1_L,tmp1+val);
        materiali.put(LAVSP2_L,tmp2+val);
      }else if ("2".equals(spalla) ){
        materiali.put(LAVSP2_L,tmp2+val);
      }else if ("1".equals(spalla) ){
        materiali.put(LAVSP1_L,tmp1+val);
      }
      
    }else{//lavorazioni non lucide
      if( materiali.containsKey(LAVSP1_N) ){
        tmp1=materiali.get(LAVSP1_N);
      }
      if( materiali.containsKey(LAVSP2_N)  ){  
        tmp2=materiali.get(LAVSP2_N);
      }
      if("A".equals(spalla)){
        materiali.put(LAVSP1_N,tmp1+val);
        materiali.put(LAVSP2_N,tmp2+val);
      }else if ("2".equals(spalla) ){
        materiali.put(LAVSP2_N,tmp2+val);
      }else if ("1".equals(spalla) ){
        materiali.put(LAVSP1_N,tmp1+val);
      }
    }
  }
  
  
 
  
} 


