/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.G1P2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class LogOttimizzatoreG1P2 {

  public final static String PREFIXQTAPAN="B 01";
  public final static String PREFIXFORMATI="F 01";
  public final static Double MLAMA=Double.valueOf("0.0055");
  public final static Double MRIFILO=Double.valueOf("0.01");
  
  private File fileName;
  
  private Long numOttimizzazione=Long.valueOf(0);
  private Integer numPannelli=Integer.valueOf(0);
  private String codicePnp="";
  private String codicePan="";
  private Double  spessorePan=Double.valueOf(0);
  
  private Double  m2Totali=Double.valueOf(0);
  private Double  m2SMLUtil=Double.valueOf(0);
  private Double  m2SfridoFisiologico=Double.valueOf(0);
  private Double  m2Manual=Double.valueOf(0);
  private Double  m2SfridoRifiloIni=Double.valueOf(0);
  

 
  private Double  m2SovraProd=Double.valueOf(0);
  
//  private Map  che
  
  
  public LogOttimizzatoreG1P2(File fileName){
    this.fileName=fileName;
  }

  public Double getM2SfridoFisiologico() {
    return m2SfridoFisiologico;
  }

  public Double getM2SMLUtil() {
    return m2SMLUtil;
  }

  public Double getM2Totali() {
    return m2Totali;
  }

  public Double getM2Manual() {
    return m2Manual;
  }

  
  public Integer getNumPannelli() {
    return numPannelli;
  }

  public String getPathFile() {
    return fileName.getPath();
  }

  public String getCodicePnp() {
    return codicePnp;
  }

  public String getCodicePan() {
    return codicePan;
  }
  
  public Double getM2SfridoTotale(){
    return m2Totali-m2SMLUtil;
  }

  public Long getNumOttimizzazione() {
    return numOttimizzazione;
  }

  public Double getM2SfridoRifiloIni() {
    return m2SfridoRifiloIni;
  }

  public void setM2SfridoRifiloIni(Double m2SfridoRifiloIni) {
    this.m2SfridoRifiloIni = m2SfridoRifiloIni;
  }

  public Double getM2SovraProd() {
    return m2SovraProd;
  }

  public void setM2SovraProd(Double m2SovraProd) {
    this.m2SovraProd = m2SovraProd;
  }

  public Double getSpessorePan() {
    return spessorePan;
  }
  
  
  
  public void elabFile() throws FileNotFoundException, IOException{
    
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    Integer oldComb=null;
    Integer nPnp=Integer.valueOf(0);
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        if(count==1){
          String ott=riga.substring(13,19);
          numOttimizzazione=Long.valueOf(ott);
        }
        
        if(isRigaPannello(riga)){
          Double dim1=getDim1Pannello(riga)/1000;
          Double dim2=getDim2Pannello(riga)/1000;
          spessorePan=getSpessorePannello(riga);
          Double qta=getQtaPannelli(riga);
          codicePnp=getCodicePnP(riga);
          codicePan=getCodicePAN(riga);
          numPannelli+=qta.intValue();
          m2Totali+=(dim1*dim2*qta);
          m2SfridoRifiloIni+=((dim1+dim2)*MRIFILO*qta*2);
          nPnp++;
          
        }else if(isRigaSML(riga) && isRigaSMLValid(riga)){
//          if(oldComb==null || oldComb<getNCombSML(riga)){
            Double lun=getDim2SML(riga)/1000;
            Double lar=getDim3SML(riga)/1000;
            Double qta=getNPzSML(riga);
            
            m2SfridoFisiologico+=((lun+lar)*MLAMA*qta);
            //m2SfridoRifiloIni+=((lun+lar)*MRIFILO*qta);
            m2SMLUtil+=((lun*lar)*qta);
            
            if(isSMLManual(riga)){
              m2Manual+=((lun*lar)*qta);
              m2SovraProd+=((lun*lar)*qta);
            }
            
            oldComb=getNCombSML(riga);
//          }
        }
        
        
        riga = bfR.readLine();  
      }
      _logger.info("File "+getPathFile()+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
      if(nPnp>1)
        codicePan="";
      if(m2Manual.doubleValue()==m2SMLUtil.doubleValue()){
        _logger.info("Ottimizzazione n."+numOttimizzazione+" --> mq SML: "+m2SMLUtil+ " mq manuali: "+m2Manual);
        m2Manual=Double.valueOf(0);
      }
      
    }
    
  }
  
  public Double checkOtm() throws FileNotFoundException, IOException{
    
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    Double m2Manual=Double.valueOf(0);
    Integer oldComb=null;
    Integer nPnp=Integer.valueOf(0);
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        if(count==1){
          String ott=riga.substring(13,19);
          numOttimizzazione=Long.valueOf(ott);
        }
        
        if(isRigaPannello(riga)){
          Double dim1=getDim1Pannello(riga)/1000;
          Double dim2=getDim2Pannello(riga)/1000;
          Double qta=getQtaPannelli(riga);
          codicePnp=getCodicePnP(riga);
          codicePan=getCodicePAN(riga);
          numPannelli+=qta.intValue();
          m2Totali+=(dim1*dim2*qta);
          nPnp++;
          
        }else if(isRigaSML(riga) && isRigaSMLValid(riga)){
//          if(oldComb==null || oldComb<getNCombSML(riga)){
            Double lun=getDim2SML(riga)/1000;
            Double lar=getDim3SML(riga)/1000;
            Double qta=getNPzSML(riga);
            
            m2SfridoFisiologico+=((lun+lar)*MLAMA*qta);
            m2SMLUtil+=((lun*lar)*qta);
            
            if(isSMLManual(riga)){
              m2Manual+=((lun*lar)*qta);
            }
            
            
            oldComb=getNCombSML(riga);
//          }
        }
        
        
        riga = bfR.readLine();  
      }
      _logger.info("File "+getPathFile()+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
      if(nPnp>1)
        codicePan="";
        
    }
    
    if(m2Manual.doubleValue()==m2SMLUtil.doubleValue()){
      _logger.info("Ottimizzazione n."+numOttimizzazione+" --> mq SML: "+m2SMLUtil+ " mq manuali: "+m2Manual);
      m2Manual=Double.valueOf(0);
    }
      
    
    return m2Manual;
    
  }
  
  
  
  private Boolean isRigaPannello(String riga){
    return riga.startsWith(PREFIXQTAPAN);
  }
  
  
  private Double getDim1Pannello(String riga){
    String part=riga.substring(42,50);
    Double dim1=ClassMapper.classToClass(part, Double.class);
    dim1=dim1/1000;
    
    
    return dim1;
  }
  
  private Double getDim2Pannello(String riga){
    String part=riga.substring(51,59);
    Double dim2=ClassMapper.classToClass(part, Double.class);
    dim2=dim2/1000;
    
    
    return dim2;
  }
  
  private Double getSpessorePannello(String riga){
    String part=riga.substring(33,41);
    Double spe=ClassMapper.classToClass(part, Double.class);
    spe=spe/1000;
    
    
    return spe;
  }
  
  
  private String getCodicePnP(String riga){
//    return riga.substring(62,68);
    return riga.substring(62,77).trim();
  }
  
   private String getCodicePAN(String riga){
//    return riga.substring(175,183);
     return riga.substring(175,190).trim();
  }
  
  private Double getQtaPannelli(String riga){
    String part=riga.substring(150,157);
    Double qta=ClassMapper.classToClass(part, Double.class);
    
    return qta;
  }
  
  //info relative ai semilavorati
  
  private Boolean isRigaSML(String riga){
    return riga.startsWith(PREFIXFORMATI);
  }
  
  /**
   * Verifica se la riga relativa alle informazioni sui pz di SML è valida o meno
   * @param riga
   * @return Boolean 
   */
  private Boolean isRigaSMLValid(String riga){
    String part=riga.substring(15, 16);
    Integer num=ClassMapper.classToClass(part, Integer.class);
    
    return (num==0);
  }
  
  private Integer getNCombSML(String riga){
    String part=riga.substring(5,9);
    Integer nComb=ClassMapper.classToClass(part, Integer.class);
    
    return nComb;
  }
  
  private Double getNPzSML(String riga){
    String part=riga.substring(24,30);
    Double nPz=ClassMapper.classToClass(part, Double.class);
    
    return nPz;
  }
  
  private Double getDim1SML(String riga){
    String part=riga.substring(52,60);
    Double dim1=ClassMapper.classToClass(part, Double.class);
    dim1=dim1/1000;
    
    
    return dim1;
  }
  
  private Double getDim2SML(String riga){
    String part=riga.substring(61,69);
    Double dim2=ClassMapper.classToClass(part, Double.class);
    dim2=dim2/1000;
    
    
    return dim2;
  }
  
  private Double getDim3SML(String riga){
    String part=riga.substring(70,78);
    Double dim3=ClassMapper.classToClass(part, Double.class);
    dim3=dim3/1000;
    
    
    return dim3;
  }
  
  private String getDescSML(String riga){
    String part=riga.substring(97,119);
    return part;
  }
  
  private String getCodSML(String riga){
    String part=riga.substring(177,191);
    return part.trim();
  }
  
  private boolean  isSMLManual(String riga){
    if(StringUtils.isEmpty(getCodSML(riga)) )
      return true;   
    
    return false;
    
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(LogOttimizzatoreG1P2.class);   
}
