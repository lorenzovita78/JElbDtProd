/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.ClassMapper;

/**
 * Classe per gestire le informazioni di una singola riga di Log
 * @author lvita
 */
public class RowInfoLogCombima {
  
  
  public static final String SCARTO="SCARTO";
  public static final String BUONO="BUONO";
  public static final String TAGLIARE="tagliare";
  
  private static final String  LAVORAZCRSSTRING="B;C;J;G";
  
  private Date data;
  private List info;

  public RowInfoLogCombima() {
    info=new ArrayList();
  }

  public RowInfoLogCombima(Date data,List info) {
    this.info=new ArrayList();
    this.data=data;
    this.info=info;
  }
  
  public Date getData() {
    return data;
  }

  public void setData(Date data) {
    this.data = data;
  }

  public List getInfo() {
    return info;
  }

  public void setInfo(List info) {
    this.info = info;
  }
 
  /**
   * Torna il numero di banda [posizione 1 del file di log]
   * @return Long 
   */
  public Long getNumeroBanda(){
    Long valore=Long.valueOf(0);
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(0));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Long.class);
    
    return valore; 
  }
  
  /**
   * Torna il numero di passaggi della banda [posizione 4 del file di log]
   * @return Long 
   */
  public Long getNumPasssaggio(){
    Long valore=Long.valueOf(0);
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(3));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Long.class);
    
    return valore;
  }
  
  /**
   * Indica se la banda è uno scarto o meno 
   * Per capire se una banda è stata scartata o meno si controlla che il primo numero
   * del codice banda [posizione 2 del file di log] sia ==6
   * @return String
   */
  public String getTipoBanda(){
    if(info==null || info.isEmpty())
      return "";
    
    String codice=ClassMapper.classToString(info.get(1));
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
   * Verifica se la banda ha subito una riduzione o meno
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
   * Data la stringa delle lavorazioni critiche controlla se la banda è una lavorazione lucida
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
   * Torna la larghezza della banda [pos 5 del file di log]
   * @return Long 
   */
  public Double getLargBanda(){
    Double valore=Double.valueOf(0);
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(4));
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
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(5));
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
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(6));
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
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(7));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
    
  }
  
  /**
   * Torna lo spessore della banda [posizione 9 del file di log]
   * @return 
   */
  public Double getSpessBanda(){
    Double valore=Double.valueOf(0);
    
    if(info==null || info.isEmpty())
      return valore;
    
    String stringValue=ClassMapper.classToString(info.get(8));
    if(!stringValue.isEmpty())
      valore=ClassMapper.classToClass(stringValue, Double.class);
    
    return valore;
    
  }
  
  /**
   * Ritorna il codice banda (o articolo) [posizione 18 file di log]
   * @return 
   */
  public String getCodiceBanda(){
    if(info==null || info.isEmpty())
      return "";
    
    return ClassMapper.classToString(info.get(17));
    
  }
  
  /**
   * Torna il tipo di lavorazione della banda [ultima lettera della pos 18 del file di log]
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
   * Torna il tipo di lavorazione della banda [ultima lettera della pos 18 del file di log]
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
   * 
   * @return 
   */
  public Boolean isPresent1Rid(){
    if(info==null || info.isEmpty())
      return Boolean.FALSE;
    
    Double value=ClassMapper.classToClass(info.get(11),Double.class);
    if(value!=null && value.intValue()>0)
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public Boolean isPresent2Rid(){
    if(info==null || info.isEmpty())
      return Boolean.FALSE;
    
    Double value=ClassMapper.classToClass(info.get(21),Double.class);
    if(value!=null && value.intValue()>0)
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public Boolean isProgTagliare(){
    if(info==null || info.isEmpty())
      return Boolean.FALSE;
    
    String stringa=ClassMapper.classToString(info.get(18));
    if(stringa!=null && TAGLIARE.equals(stringa))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
    
  }
  
}
