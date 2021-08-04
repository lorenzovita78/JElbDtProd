/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R1P1;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class RowInfoLogSchelling {

  
  private static final String  SEZIONATURA="Erzeuge Lage";
  private static final String  DIMPEZZO="Laenge";
  private static final String  POSZ="Pos_Z";
  private static final String  POSX="Pos_X";
  
  
  public static final String CODBANDA="CODBANDA";
  public static final String DIMBANDA="DIMBANDA";
  public static final String NUMBANDA="POSZBANDA";
  public static final String NUMTAGLI="NUMTAGLI";
  public static final String PM="PM";
  public static final String AM="AM";
  public static final Integer DODICI=Integer.valueOf(12);
  
  
  
  private Date data;
  private Date dataOra;

  //serve per indicare com'è il formato data nel file di log.
  private String dateFormat;
  
  
  private Map info;
  private String riga;
  
 
  public RowInfoLogSchelling(String riga,String dateFormat) {
    this.riga=riga;
    this.dateFormat=dateFormat;
    this.data=loadData();
  }
  
  
  public void loadInfoRow(){
    this.info=loadInfo();
  }
  
  
  public Date getData() {
    return data;
  }

  public Map getInfo() {
    return info;
  }

  public String getRiga() {
    return riga;
  }
  
//  public Date getDataOra() {
//    if(dataOra==null){
//      dataOra=loadDataOra();
//    }
//    
//    return dataOra;
//  }
  public Date getDataOra() {
    if(dataOra==null){
      dataOra=getDataOraRigaLog();
    }
    
    return dataOra;
  }
  
   /**
   * Data la string del file di log torna la data relativa
   * @param String riga
   * @return Date
   */
//  public Date loadData(){
//    Date datat=null;
//    if(riga==null || riga.isEmpty())
//      return datat;
//    
//    if(!riga.contains(PM) && !riga.contains(AM))
//      return datat;
//    
//    String[] infoData=riga.split(";");
//    
//    if(infoData==null || infoData.length==0)
//      return datat;
//    
//    String dataString=infoData[0];
//    infoData=null;
//    infoData=dataString.split(" ");
//    
//    if(infoData==null || infoData.length==0)
//      return datat;
//    
//    String dataS=infoData[0];
//    
//    datat=DateUtils.strToDate(dataS, "MM/dd/yyyy");
//    
//    return datat;
//  }
  
  
  public Date loadData(){
    Date datat=null;
    if(riga==null || riga.isEmpty() || !riga.contains(";"))
      return datat;
    
    
    String[] infoData=riga.split(";");
    
    if(infoData==null || infoData.length==0)
      return datat;
    
    String dataString=infoData[0];
        
    datat=DateUtils.strToDate(dataString, dateFormat);
    
    //        
//    if(!dataString.contains(PM) && !dataString.contains(AM)){
//      //presuppongo che per una data cia siano almeno le barre e i d
//      if(dataString.contains("/") && dataString.contains(":")){
//        datat=DateUtils.strToDate(dataString, "dd/MM/yyyy");
//      }else if (!dataString.contains(":")){
//        datat=DateUtils.strToDate(dataString, "MM/dd/yyyy");
//      }
//    }else {
//      datat=DateUtils.strToDate(dataString, "MM/dd/yyyy");
//    }
    
    return datat;
  }
  
  
  
  
//  public Date loadDataOra(){
//    Date dataora=null;
//    if(riga==null || riga.isEmpty())
//      return dataora;
//    
//    if(!riga.contains(PM) && !riga.contains(AM))
//      return dataora;
//    
//    String[] infoData=riga.split(";");
//    
//    if(infoData==null || infoData.length==0)
//      return dataora;
//    
//    String dataString=infoData[0];
//    infoData=null;
//    infoData=dataString.split(" ");
//    
//    if(infoData==null || infoData.length==0)
//      return dataora;
//    
//    String dataS=infoData[0];
//    String orario=infoData[1];
//    String tipoOra=infoData[2];
//    
//    infoData=null;
//    infoData=orario.split(":");
//    if(infoData==null || infoData.length==0)
//      return dataora;
//    
//    String hh=infoData[0];
//    String mm=infoData[1];
//    String ss=infoData[2];
//    Integer oraappo=new Integer(hh);
//    
//    String oraS="";
//    String dataAppo="";
//    
//    boolean orariomod=false;
//    if(PM.equals(tipoOra)){
//      if(oraappo<DODICI){
//        oraS= new Integer(oraappo+DODICI).toString();
//        oraS+=":";
//        orariomod=true;
//      }
//    }else{
//      if(DODICI.equals(oraappo)){
//        oraS="00:";
//        orariomod=true;
//      }
//    }
//    if(!orariomod)
//      oraS=hh+":";
//    
//   
//    dataAppo=dataS+" "+oraS+mm+":"+ss;
//    dataora=DateUtils.strToDate(dataAppo, "MM/dd/yyyy HH:mm:ss");
//    
//    return dataora;
//  }
  
  private Date getDataOraRigaLogAM(String dataStringAM){
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
  
  
  
  
  
  private Date getDataOraRigaLog(){
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
      dataora=getDataOraRigaLogAM(dataString);
    }
      
    
    return dataora;
  }
  
  
  
  
  private Map loadInfo(){
    Map mappa=new HashMap();
    if(riga==null || riga.isEmpty())
      return mappa;
    
    //la lista di informazioni ha senso solo se c'è il dettaglio della sezionatura
    if(riga.indexOf(SEZIONATURA)<0)
      return mappa;
      
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
        mappa.put(CODBANDA,codicePezzoS.trim());
        dimPezzoS=dimPezzoS.substring(dimPezzoS.indexOf(DIMPEZZO)+DIMPEZZO.length(),dimPezzoS.length());
        mappa.put(DIMBANDA,dimPezzoS.trim());
        posZS=posZS.trim();
        posZS=posZS.substring(posZS.indexOf(POSZ)+POSZ.length(),posZS.length());
        mappa.put(NUMBANDA,posZS.trim());
//        ----NEW
        posXS=posXS.trim();
        posXS=posXS.substring(posXS.indexOf(POSX)+POSX.length(),posXS.length());
        mappa.put(NUMTAGLI,posXS.trim());
      }
      
    }
    
    return mappa;
  }
  
  
  public String getCodicePezzo(){
    
    if(info==null || info.isEmpty())
      return "";
    
    return ClassMapper.classToString(info.get(CODBANDA));
  }
  
  
  public Boolean isPezzoScarto(){
    if(info==null || info.isEmpty())
      return false;
    String pz=ClassMapper.classToString(info.get(CODBANDA));
    
    return pz.startsWith("6");
  }
  
  
  public Double getDimensionePezzo(){
    
    if(info==null || info.isEmpty())
      return null;
    
    
    return ClassMapper.classToClass(info.get(DIMBANDA),Double.class);
  }
  
  
  public Integer getNumBanda(){
    
    if(info==null || info.isEmpty())
      return new Integer(0);
    
    
    return ClassMapper.classToClass(info.get(NUMBANDA),Integer.class);
  }
  
  
  public Integer getNumTagli(){
    
    if(info==null || info.isEmpty())
      return new Integer(0);
    
    
    return ClassMapper.classToClass(info.get(NUMTAGLI),Integer.class);
  }
  
  
  public Boolean isPresentSezionatura(){
    if(riga==null || riga.isEmpty())
      return false;
    
    if(riga.indexOf(SEZIONATURA)<0)
      return false;
    
    return true;
  }
  
  
  public Boolean isAssenteSezionatura(){
    if(riga==null || riga.isEmpty())
      return false;
    
    if(riga.indexOf(SEZIONATURA)<0)
      return true;
    
    return false;
  }
  
  
  
  
}
