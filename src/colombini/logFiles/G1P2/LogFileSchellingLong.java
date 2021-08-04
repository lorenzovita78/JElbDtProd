/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.G1P2;

import colombini.costant.MisureG1P2Costant;
import colombini.model.CausaliLineeBean;
import colombini.model.persistence.PanSchLongTurnoBean;
import colombini.util.DatiProdUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class LogFileSchellingLong {
  
  private static final String codiceMacchina="145.077";
  private static final String extFileLogStd=".mde";
  private static final String extFileLogTmp=".tmp";
  
  
  public final static Long   EV120 = Long.valueOf("120");
  public final static Long   EV9000 = Long.valueOf("9000");
  public final static Long   EV4140 = Long.valueOf("4140");
  //15 minuti espressi in secondi
  private final static Integer SECXMIN15=Integer.valueOf(900);
  
  
  private Map<String,CausaliLineeBean> causaliFermi;
  private String fileName;
  private Double tempoRun;
  private Double tempoLordo;
  private Long numCicli;
  private Long nGuasti;
  private List fermi;
  private Map tipoFermi;
  
  public LogFileSchellingLong(String pFile) {
    this.fileName=pFile;
    this.causaliFermi=new HashMap<String, CausaliLineeBean>();
    
    fermi=new ArrayList();
    tipoFermi=new HashMap();
    numCicli=Long.valueOf(0);
    nGuasti=Long.valueOf(0);
    tempoRun=Double.valueOf(0);
    tempoLordo=Double.valueOf(0);
  }
  
  
  public LogFileSchellingLong(String pFile,Map causali) {
    this.fileName=pFile;
    this.causaliFermi=causali;
    
    fermi=new ArrayList();
    tipoFermi=new HashMap();
    numCicli=Long.valueOf(0);
    nGuasti=Long.valueOf(0);
    tempoRun=Double.valueOf(0);
    tempoLordo=Double.valueOf(0);
  }

  
  public void setCausaliFermi(Map<String, CausaliLineeBean> causaliFermi) {
    this.causaliFermi = causaliFermi;
  }

  
  public Long getNumCicli() {
    return numCicli;
  }

  public Double getTempoLordo() {
    return tempoLordo;
  }

  public Double getTempoRun() {
    return tempoRun;
  }

  public Long getnGuasti() {
    return nGuasti;
  }

  public List getFermi() {
    return fermi;
  }

  public Map getTipoFermi() {
    return tipoFermi;
  }

  
  
  public void processLogFile(Date dataInizio,Date dataFine) throws IOException, ParseException {
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        List info=getListInfoRow(riga);
        Long codice=ClassMapper.classToClass(info.get(0),Long.class);
        String dataS=ClassMapper.classToString(info.get(1));
        dataS=dataS.replace(".", "/");
        dataS=dataS.replace("-", " ");
        Date dataLog=DateUtils.StrToDate(dataS, "yyyy/MM/dd HH:mm:ss");
        if(dataLog!=null && dataLog.after(dataInizio) && dataLog.before(dataFine)){
          if(EV120.equals(codice)){
            loadDatiProd(info);            
          }else if (codice!=null && (codice.equals(EV4140) || codice.compareTo(EV9000)>=0) ){
            Long tempoSec=ClassMapper.classToClass(info.get(3),Long.class);
            loadDatiCausali(codice, tempoSec,dataLog,dataInizio);
          }
        }
        riga = bfR.readLine();
        count++;
      }
     _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
  }
  
  
  
  
  
  public Double getUtilMateriale(Date data) throws IOException, ParseException {
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double lngTot=Double.valueOf(0);
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      
    while(riga!=null && !riga.isEmpty()){
      
      List info=getListInfoRow(riga);
      Long codice=ClassMapper.classToClass(info.get(0),Long.class);
      String dataS=ClassMapper.classToString(info.get(1));
      dataS=dataS.replace(".", "/");
      dataS=dataS.replace("-", " ");
      Date dataLog=DateUtils.StrToDate(dataS, "yyyy/MM/dd HH:mm:ss");
      if(dataLog!=null && DateUtils.daysBetween(dataLog, data)==0){
        if(EV120.equals(codice)){
          Integer numFolgi=ClassMapper.classToClass(info.get(8), Integer.class);
          Integer numTagli=ClassMapper.classToClass(info.get(19), Integer.class);
          Double largh=ClassMapper.classToClass(info.get(12), Double.class);
          lngTot+=numFolgi*numTagli*largh;
        }
      }
      riga = bfR.readLine();
      count++;
    } 
     _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return lngTot/new Double(1000);
  }
  
  
  /**
   * Torna una mappa di Bean PanSchLongTurnoBean
   * @param dataRif
   * @return Map
   */
  public Map processLogFileForPanels(Date dataRif)  {
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Map pannelli=new HashMap <Date,PanSchLongTurnoBean> ();
    
    long count=1;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        List info=getListInfoRow(riga);
        Long codice=ClassMapper.classToClass(info.get(0),Long.class);
        String dataS=ClassMapper.classToString(info.get(1));
        dataS=dataS.replace(".", "/");
        dataS=dataS.replace("-", " ");
        Date dataLog=DateUtils.StrToDate(dataS, "yyyy/MM/dd HH:mm:ss");
        Date turnoRif=DatiProdUtils.getInstance().getTurno(dataLog);
        if(EV120.equals(codice)){
          Integer spessore=ClassMapper.classToClass(info.get(13), Double.class).intValue();
          
          Integer nFogliC=ClassMapper.classToClass(info.get(8), Integer.class);
          Integer nBarreC=ClassMapper.classToClass(info.get(19), Integer.class);
          
          Double lungh=ClassMapper.classToClass(info.get(11), Double.class);
          Double largh=ClassMapper.classToClass(info.get(12), Double.class);
          
          Integer fogliG=Integer.valueOf(0);
          Integer fogliP=Integer.valueOf(0);
          
          if(lungh.longValue()<=3000){
            fogliP=nFogliC;
          }else{
            fogliG=nFogliC;
          }
          
          PanSchLongTurnoBean bean=new PanSchLongTurnoBean(turnoRif);
          if(pannelli.containsKey(turnoRif)){
            bean=(PanSchLongTurnoBean) pannelli.get(turnoRif);
          }
          bean.addBande(nBarreC*nFogliC);
          bean.addInfoForThickness(spessore, Integer.valueOf(1), fogliP, fogliG);
          bean.addVolume( ((lungh*largh*spessore)/1000000000 )*nFogliC);
          pannelli.put(turnoRif,bean);
        }
        
        riga = bfR.readLine();
        count++;
      }
     
    }catch(ParseException e){
      _logger.error("Problemi in fase di decodifica del file di log -->"+e.getMessage());
    }catch(IOException ex){
      _logger.error("Problemi in fase di accesso al file di log -->"+ex.getMessage());
    }  finally{
      _logger.info("File "+fileName+" righe processate:"+count);
      try{
        if(bfR!=null)
          bfR.close();
        if(fR!=null)
          fR.close();
      }catch(IOException e){
        _logger.error("Errore in fase di chiusura del file di log -->"+e.getMessage());
      }
    }
    return pannelli;
  }
  
  
  private List getListInfoRow(String riga){
    List info=new ArrayList();
    if(riga==null)
      return info;
    
    Object [] objArray=riga.split(";");
    info=ArrayUtils.getListFromArray(objArray);
    
    return info;
  }
  
  private void  loadDatiProd(List info){
    Integer spessore;
    Integer fogliCiclo;
    Integer barreCiclo;
    Integer fogliCicloTeo;
    
    fogliCiclo=ClassMapper.classToClass(info.get(8), Integer.class);
    barreCiclo=ClassMapper.classToClass(info.get(19), Integer.class);
    spessore=ClassMapper.classToClass(info.get(13), Double.class).intValue();
    Double tempoGeo=Math.ceil(getTempoGeometria(barreCiclo));
    fogliCicloTeo=getFogliCicloXSpessore(spessore);
    if(fogliCiclo>fogliCicloTeo){
      fogliCiclo=fogliCicloTeo;
    }
    Double runDouble= Math.ceil((fogliCiclo / new Double(fogliCicloTeo)) * tempoGeo);
    
    
    numCicli++;
    tempoLordo+=tempoGeo;
    
    if(runDouble!=null && runDouble>0)
      tempoRun+=runDouble;
    
  }
  
  
  private void  loadDatiCausali(Long codice,Long tempoSec,Date dataLog,Date dataInizioT){
    String tipoCausale="";
    boolean caricato=false;
    
    tipoCausale=((CausaliLineeBean)causaliFermi.get(codice.toString())).getTipo();
    
    //se la tipologia della causale è un guasto aumento il contatore relativo
    if(CausaliLineeBean.TIPO_CAUS_GUASTO.equals(tipoCausale)){
        nGuasti++;
    }
      
    //tempo minimo che può intercorrere tra fine turno di notte e inizio turno mattino (2:15)
    if(tempoSec>8100){ 
      //se l'orario del turno coincide con l'ora della riga del log o sono le 6 della mattina
      int oraLog=DateUtils.getHours(dataLog);
      int oraT=DateUtils.getHours(dataInizioT);
      if(oraT==oraLog||( oraT<=6 && oraLog==6)){
        Long secDif=DateUtils.numSecondiDiff(dataInizioT, dataLog);
        if(oraT<6 && oraLog==6){
          try {
            Date dt6=DateUtils.getInizioTurnoM(dataLog);
            secDif=DateUtils.numSecondiDiff(dt6, dataLog);
          } catch (ParseException ex) {
            _logger.error("Impossibile convertire la data"+dataLog);
          }
        }  
       secDif=Math.min(tempoSec, secDif);
       if(CausaliLineeBean.TIPO_CAUS_ORENONPROD.equals(tipoCausale)){
          _logger.warn("Attenzione ORE IMPRODUTTIVE -> data: "+dataLog +" sec: "+tempoSec +" --> Impostato a "+secDif);
//          fermi.add(getFermo(tipoCausale,codice.toString(), secDif));
          loadValoretoMap(tipoFermi, tipoCausale, secDif);
          caricato=true;
        }else if(CausaliLineeBean.TIPO_CAUS_PERDGEST.equals(tipoCausale) || CausaliLineeBean.TIPO_CAUS_GUASTO.equals(tipoCausale)){
          _logger.warn("Attenzione CAUSALE TIPOLOGIA "+tipoCausale+" -> data: "+dataLog +" sec: "+tempoSec +" --> Impostato a "+secDif);
          loadValoretoMap(tipoFermi, tipoCausale, secDif);
          fermi.add(getFermo(codice.toString(), secDif));
          caricato=true;
        }
      }
    }
    
    if(!caricato){
      loadValoretoMap(tipoFermi, tipoCausale, tempoSec);
      if(!CausaliLineeBean.TIPO_CAUS_ORENONPROD.equals(tipoCausale)){
        fermi.add(getFermo(codice.toString(), tempoSec));
      }
    }  
  }
  
  
  private void loadValoretoMap(Map map,String key,Long valore){
    Long valMap=Long.valueOf(0);
    if(map.containsKey(key)){
      valMap=  (Long) map.get(key); 
    } 
    map.put(key, new Long(valMap+valore));
  }
  
   
  private List getFermo(String codice,Long sec){
    List fermo=new ArrayList();
    fermo.add(codice);
    fermo.add(new Double(sec));
    
    return fermo;
  }
  
  public Integer getFogliCicloXSpessore(Integer spessore){
    if(MisureG1P2Costant.SPESS16.equals(spessore)){
      return Integer.valueOf(12);
    } else if(MisureG1P2Costant.SPESS18.equals(spessore)){  
      return Integer.valueOf(9);
    } else if(MisureG1P2Costant.SPESS25.equals(spessore)){  
      return Integer.valueOf(6);
    } else if(MisureG1P2Costant.SPESS35.equals(spessore)){  
      return Integer.valueOf(4);
    }
    return Integer.valueOf(0);
  }
  
  
  
 private Double getTempoGeometria(Integer barra){
   Double geom=Double.valueOf(0);  
   if (barra == 2) {
     geom=new Double("65.43");
   } else if (barra == 3){
     geom=new Double("81.68");
   } else if (barra == 4){
     geom=new Double("88.71");
   } else if (barra == 5){
     geom=new Double("116.04");
   } else if (barra == 6){
     geom=new Double("130.46");
   } else if (barra == 7){
     geom=new Double("158.62");
   }
   
   return geom;
 }

 
 
 
 /**
   * Torna il nome del file sapendo la data
   * Parte dal presupposto che sul server dove gira c'è un'unità di rete N: 
   * che si collega alla cartella dove risiedono i file di log della macchina
   * @param Date data
   * @return String nome del file
   * @throws ParseException 
   */
  public static String getFileName(Date data) throws ParseException{
    String fileN="";
    String dataFile=DateUtils.DateToStr(data, "yyyy.MM.dd");
    String percRete="N:/";
//    percRete="//pegaso/Uffici/ICT/Documenti/OEE/Logs/schelllong/";
    String pathFile=percRete+codiceMacchina+"_"+dataFile;
    String nF=pathFile+extFileLogStd;
    File f=new File(nF);
    if(f!=null && f.exists() && f.length()>0){
      fileN=pathFile+extFileLogStd;
    }else {
      nF=pathFile+extFileLogTmp;
      f=new File(nF);
      if(f!=null && f.exists() && f.length()>0)
        fileN=pathFile+extFileLogTmp;
    }
    
    return fileN;
  }
  
  private static final Logger _logger = Logger.getLogger(LogFileSchellingLong.class);
}
