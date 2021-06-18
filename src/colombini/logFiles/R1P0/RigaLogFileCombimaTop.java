/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P0;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class RigaLogFileCombimaTop {
  
  private Date dataRif;
  private List infoLog;
  
  // PER IMA ANTE
  public static final String SCARTO="SCARTO";
  public static final String BUONO="BUONO";
  public static final String TAGLIARE="tagliare";
  
  private static final String  LAVORAZCRSSTRING="B;C;J;G";
  // PER IMA ANTE
  
  public RigaLogFileCombimaTop(String riga){
    dataRif=getDateRifRigaLog(riga);
    infoLog=getListInfoRigaLog(riga);  
  }
  
  public Date getDataRif() {
    return dataRif;
  }

  public List getInfoLog() {
    return infoLog;
  }
  
  
  private  List getListInfoRigaLog(String riga) {
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

  
  private  Date getDateRifRigaLog(String riga){
    Date dataLog=null;
    try {
      String ggLog=riga.substring(0,17);
      ggLog=ggLog.replace(".", "/");
      dataLog=DateUtils.StrToDate(ggLog, "dd/MM/yy HH:mm:ss");
      
    } catch (ParseException ex) {
      _logger.error("Impossibile convetire la data fornita :"+ex.getMessage());
    }
    return dataLog;
    
  }
  
  
  /**
   * Torna il numero di banda [posizione 1 del file di log]
   * @return Long 
   */
  public Long getNumeroBanda(){
    Long valore=Long.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(0));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Long.class);
    
    return valore; 
  }
  
  /**
   * Torna la larghezza della banda [pos 5 del file di log]
   * @return Long 
   */
  public Double getLargBanda(){
    Double valore=Double.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(4));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
  }
  
  /**
   * Torna la lunghezza della banda [pos 6 del file di log]
   * @return Long 
   */
  public Double getLungBanda(){
    Double valore=Double.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(5));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
    
  }
  
  /**
   * Torna la larghezza della banda se eventualmente ridotta[pos 7 del file di log]
   * @return Long 
   */
  public Double getLargBandaRid(){
    Double valore=Double.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(6));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
  }
  
  /**
   * Torna la lunghezza della banda se eventualmente ridotta[pos 8 del file di log]
   * @return Long 
   */
  public Double getLungBandaRid(){
    Double valore=Double.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(7));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
    
  }
  
  /**
   * Torna lo spessore della banda [posizione 9 del file di log]
   * @return 
   */
  public Double getSpessoreBanda(){
    Double valore=Double.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(8));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
  }
  
  /**
   * Ritorna il codice banda (o articolo) [posizione 18 file di log]
   * @return 
   */
  public String getCodiceBanda(){
    if(infoLog==null || infoLog.isEmpty())
      return "";
    
    return ClassMapper.classToString(infoLog.get(17));
    
  }
  
  
  
  /**
   * [per Ima Ante] Torna il numero di passaggi della banda [posizione 4 del file di log] 
   * @return Long 
   */
  public Long getNumPasssaggio(){
    Long valore=Long.valueOf(0);
    
    if(infoLog==null || infoLog.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(infoLog.get(3));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Long.class);
    
    return valore;
  }
  
  /**
   * [per Ima Ante] Indica se la banda è uno scarto o meno 
   * Per capire se una banda è stata scartata o meno si controlla che il primo numero
   * del codice banda [posizione 2 del file di log] sia ==6
   * @return String
   */
  public String getTipoBanda(){
    if(infoLog==null || infoLog.isEmpty())
      return "";
    
    String codice=ClassMapper.classToString(infoLog.get(1));
    if(codice.isEmpty())
      return codice;
    //attenzione se il codice è vuoto il pezzo è considerato buono!!!
    if(codice.startsWith("6")){
      return SCARTO;
    }else{ 
      return BUONO;
    }  
  }
  
  /**
   * [per Ima Ante] Verifica se la banda ha subito una riduzione o meno
   * La ridudione si verifica se la 1a larghezza della banda [posizione 5 file log]
   * è differente dalla larghezza dalla 2a larghezza della banda [posizione 7 file log]
   * 
   * @return Boolean
   */
  public Boolean isRiduzione(){
    
    Double dim1=getLargBanda();
    Double dim2=getLargBandaRid();
    if(dim1!=null && dim2!=null && dim1.doubleValue()>dim2.doubleValue())
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
 
  /**
   * [per Ima Ante] Data la stringa delle lavorazioni critiche controlla se la banda è una lavorazione lucida
   * @param lavCritiche
   * @return Boolean
   */
  public Boolean isLavLucida(){
    String codLav=getTipoLavorazione18();
    if(!codLav.isEmpty()){
      if(LAVORAZCRSSTRING.indexOf(codLav)>0){
        return Boolean.TRUE;
      }
    }
      
    return Boolean.FALSE;
  }
  
  
  
  /**
   * [per Ima Ante] Torna il tipo di lavorazione della banda [ultima lettera della pos 18 del file di log]
   * @return 
   */
  public String getTipoLavorazione(){
    String codLav="";
    String codBanda=getCodiceBanda();
    if(codBanda!=null && !codBanda.isEmpty())
      codLav=codBanda.substring(codBanda.length()-1);
    
    return codLav;
  }
  
  /**
   * [per Ima Ante] Torna il tipo di lavorazione della banda [ultima lettera della pos 18 del file di log]
   * se il tipo di lavorazione è di 18 caratteri
   * @return  String
   */
  public String getTipoLavorazione18(){
    String codLav="";
    String codBanda=getCodiceBanda();
    if(codBanda!=null && !codBanda.isEmpty() && codBanda.length()==18)
      codLav=codBanda.substring(codBanda.length()-1);
    
    return codLav;
  }
  
  // --- metodi utili per l'utilizzo dei materiali  ---\\
  /**
   * [per Ima Ante]
   * @return 
   */
  public Boolean isPresent1Rid(){
    if(infoLog==null || infoLog.isEmpty())
      return Boolean.FALSE;
    
    Double value=ClassMapper.classToClass(infoLog.get(11),Double.class);
    if(value!=null && value.intValue()>0)
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public Boolean isPresent2Rid(){
    if(infoLog==null || infoLog.isEmpty())
      return Boolean.FALSE;
    
    Double value=ClassMapper.classToClass(infoLog.get(21),Double.class);
    if(value!=null && value.intValue()>0)
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public Boolean isProgTagliare(){
    if(infoLog==null || infoLog.isEmpty())
      return Boolean.FALSE;
    
    String stringa=ClassMapper.classToString(infoLog.get(18));
    if(stringa!=null && TAGLIARE.equals(stringa))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
    
  }

  private static final Logger _logger = Logger.getLogger(RigaLogFileCombimaTop.class);   
  
}
