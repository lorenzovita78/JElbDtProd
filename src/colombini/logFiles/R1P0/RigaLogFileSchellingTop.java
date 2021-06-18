/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P0;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class RigaLogFileSchellingTop {
  
  private static final String  SEZIONATURA="Erzeuge Lage";
  private static final String  DIMPEZZO="Laenge";
  private static final String  BANDA="Schnittplan";
  
  
  private static final String  POSZ="Pos_Z";
  private static final String  POSX="Pos_X";
  
  
  public static final String CODBANDA="CODBANDA";
  public static final String DIMBANDA="DIMBANDA";
  public static final String NUMBANDA="POSZBANDA";
  public static final String NUMTAGLI="NUMTAGLI";
  public static final String PM="PM";
  public static final String AM="AM";
  public static final Integer DODICI=Integer.valueOf(12);
  
  private Date dataRif;
  private Map info;
  private Boolean sezionatura=Boolean.FALSE;
  private Boolean banda=Boolean.FALSE;
  private String codiceBanda;
  private Double dimBanda;
  private Integer numBanda;
  private Integer numTagli;
  
  public RigaLogFileSchellingTop(String riga) {
    dataRif=getDataRigaLog(riga);
    info=getInfoRigaLog(riga);
  }

 
  private Date getDataRigaLogAM(String dataStringAM){
    Date dataora=null;
    
    String [] infoData=dataStringAM.split(" ");
    
    if(infoData==null || infoData.length==0)
      return dataora;
    
    String dataS=infoData[0];
    String orario=infoData[1];
    String tipoOra=infoData[2];
    
    infoData=null;
    infoData=orario.split(":");
    if(infoData==null || infoData.length==0)
      return dataora;
    
    String hh=infoData[0];
    String mm=infoData[1];
    String ss=infoData[2];
    Integer oraappo=new Integer(hh);
    
    String oraS="";
    String dataAppo="";
    
    boolean orariomod=false;
    if(PM.equals(tipoOra)){
      if(oraappo<DODICI){
        oraS= new Integer(oraappo+DODICI).toString();
        oraS+=":";
        orariomod=true;
      }
    }else{
      if(DODICI.equals(oraappo)){
        oraS="00:";
        orariomod=true;
      }
    }
    if(!orariomod)
      oraS=hh+":";
    
   
    dataAppo=dataS+" "+oraS+mm+":"+ss;
    dataora=DateUtils.strToDate(dataAppo, "MM/dd/yyyy HH:mm:ss");
    
    return dataora;
  }
  
  
  
  
  
  private Date getDataRigaLog(String riga){
    Date dataora=null;
    
    if(riga==null || riga.isEmpty() || !riga.contains(";"))
      return dataora;
    
    
    String[] infoData=riga.split(";");
    
    if(infoData==null || infoData.length==0)
      return dataora;
    
    String dataString=infoData[0];
            
    //        
    if(!riga.contains(PM) && !riga.contains(AM)){
      //presuppongo che per una data cia siano almeno le barre e i d
      if(riga.contains("/") && riga.contains(":")){
        dataora=DateUtils.strToDate(dataString, "dd/MM/yyyy HH:mm:ss");
      }
    }else {
      dataora=getDataRigaLogAM(dataString);
    }
      
    
    return dataora;
  }
  
  private Map getInfoRigaLog(String riga){
    Map mappa=new HashMap();
    
    if(riga==null || riga.isEmpty())
      return null;
    
    if(riga.indexOf(BANDA)>0){
      banda=Boolean.TRUE;
      return null;
    }
    
    //la lista di informazioni ha senso solo se c'è il dettaglio della sezionatura
    if(riga.indexOf(SEZIONATURA)<0){
      sezionatura=Boolean.FALSE;
      return null;
    }  
    
    sezionatura=Boolean.TRUE;
    String [] infoR=riga.split(SEZIONATURA);
    if(infoR!=null && infoR.length>0){
      String infoSezionatura=infoR[1];
      infoSezionatura=infoSezionatura.replace(" | ","-");
      String [] infodett=infoSezionatura.split("-");
      int idx=0;
      if(infodett!=null && infodett.length>0){
        String codicePezzoS=infodett[0];
        String dimPezzoS=infodett[1];
        String posZS=infodett[2];
        String posXS=infodett[3];
        idx=codicePezzoS.indexOf("BC");
        codicePezzoS=codicePezzoS.substring(idx+3,codicePezzoS.length());
        codiceBanda=codicePezzoS.trim();
        dimPezzoS=dimPezzoS.substring(dimPezzoS.indexOf(DIMPEZZO)+DIMPEZZO.length(),dimPezzoS.length());
        dimBanda=ClassMapper.classToClass(dimPezzoS.trim(),Double.class);
        posZS=posZS.trim();
        posZS=posZS.substring(posZS.indexOf(POSZ)+POSZ.length(),posZS.length());
        numBanda=ClassMapper.classToClass(posZS.trim(),Integer.class);
//        ----NEW
        posXS=posXS.trim();
        posXS=posXS.substring(posXS.indexOf(POSX)+POSX.length(),posXS.length());
        numTagli=ClassMapper.classToClass(posXS.trim(),Integer.class);
      }
      
    }
    
    return mappa;
  }

  
  public Date getDataRif() {
    return dataRif;
  }
  

  public Map getInfo() {
    return info;
  }

  public String getCodiceBanda() {
    return codiceBanda;
  }

  public Double getDimBanda() {
    return dimBanda;
  }

  public Integer getNumBanda() {
    return numBanda;
  }

  public Integer getNumTagli() {
    return numTagli;
  }
  
  public Boolean isPresentSezionatura() {
    return sezionatura;
  }
  
  public Boolean isPresentBanda() {
    return banda;
  }
  
  public Boolean isPezzoScarto(){
    if(codiceBanda==null || codiceBanda.isEmpty())
      return Boolean.FALSE;
    
    return codiceBanda.startsWith("6");
  }
  
  
}
