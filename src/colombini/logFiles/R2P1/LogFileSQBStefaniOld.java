/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.R2P1;



import colombini.costant.CostantsColomb;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
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
public class LogFileSQBStefaniOld {

  public final static String MATERIALE="MATERIALE";
  public final static String PROGRAMMIUTIL="PROGRAMMIUTIL";
  public final static String PATHFILEREPORT="//172.18.0.126/report/";
  public final static String PATHFILEPROGRAM="//172.18.0.126/Programmi/";
  
  
  public final static Double SETUP=Double.valueOf(125.1);
  public final static Double SETUPCAMBIOPROG=Double.valueOf(201.3);
  
  
  private String fileName;
  
  public LogFileSQBStefaniOld(String fn) {
    this.fileName=fn;
  }
  
  
  public Map elabFile(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException{
    
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    Long runtime=Long.valueOf(0);
    Long pz=Long.valueOf(0);
    Long nPR=Long.valueOf(0);
    Double setup=Double.valueOf(0);
    Double materiale=Double.valueOf(0);
    Long fermi=Long.valueOf(0);
    Map mappaProg=new HashMap();
    Map result=new HashMap();
    InfoRow rwOld=null;
    
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      String oldPrg="";
      while(riga!=null && !riga.isEmpty()){
        count++;
        InfoRow rw=new InfoRow(riga,dataInizio);
//        if(DateUtils.afterEquals(rw.getOraInizio(), dataInizio) && DateUtils.beforeEquals(rw.getOraInizio(),dataFine)  ){
        if(DateUtils.beforeEquals(rw.getOraInizio(), dataFine) && 
            DateUtils.afterEquals(rw.getOraFine(),dataInizio)  ){
          
          if(rwOld!=null ){
            Long sec=DateUtils.numSecondiDiff(rwOld.getOraFine(), rw.getOraInizio());
            if(sec>=3600){
              _logger.warn("Attenzione fermo tra un programma ed un altro > di 1 ora --> ora fine:"+rwOld.getOraFine()+
                           " -- ora inizio successivo: "+rw.getOraInizio());
              fermi+=sec;
            }
            
          }
          nPR++;
          runtime+=rw.getSecTot();
          pz+=rw.getQta();
          materiale+=rw.getQtaBordoTot()*2;//??? da verificare se moltiplicare x due
          String key=rw.getCodProgramma();
          ObjPrgSQBStefani obj=null;
          if(mappaProg.containsKey(key)){
            obj=(ObjPrgSQBStefani) mappaProg.get(key);
          }else{
            obj=new ObjPrgSQBStefani(key);
            obj.setLunghezza(rw.getLunghezza());
            obj.setLarghezza(rw.getLarghezza());
            obj.setSpessore(rw.getSpessore());
          }
          obj.addQta(rw.getQta());
          obj.addTempo(rw.getSecTot());

          mappaProg.put(key,obj);

          if(rw.getQta()==1){
            setup+=SETUP;
          }else{
            setup+=SETUPCAMBIOPROG;
          }

          oldPrg=rw.getCodProgramma();
        }
        rwOld=rw;
        riga = bfR.readLine();  
      }
      result.put(CostantsColomb.TRUNTIME, runtime);
      result.put(CostantsColomb.TSETUP, setup.longValue());
      result.put(CostantsColomb.NPZTOT, pz);
      result.put(CostantsColomb.TGUASTI,fermi);
      result.put(CostantsColomb.NCICLI, nPR);
      result.put(MATERIALE, materiale);
      result.put(PROGRAMMIUTIL, mappaProg);
      
      _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }
    
    return result;
  }
  
  
  
  
//  public static String getFilePath(Date data){
//    
//    return null;
//  }
  
  public class InfoRow{
    private String codProgramma;
    private Double lunghezza;
    private Double larghezza;
    private Double spessore;
    private Long qta;
    private String bordo1;
    private String bordo2;
    private Double qtaBordoTot;
    
    private Date oraInizio;
    private Date oraFine;
    private Long secTot;
    
    
    public InfoRow(String riga,Date dataRif){
      if(riga==null)
        return;
      String dataCS=DateUtils.getDataForMovex(dataRif).toString();
      List info=ArrayUtils.getListFromArray(riga.split(";"));
      if(info!=null && info.size()>17){
        codProgramma=ClassMapper.classToString(info.get(1));
        lunghezza=ClassMapper.classToClass(info.get(3),Double.class);
        larghezza=ClassMapper.classToClass(info.get(4),Double.class);
        spessore=ClassMapper.classToClass(info.get(5),Double.class);
        qta=ClassMapper.classToClass(info.get(7),Long.class);
        bordo1=ClassMapper.classToString(info.get(9));
        bordo2=ClassMapper.classToString(info.get(11));
        qtaBordoTot=ClassMapper.classToClass(info.get(13),Double.class);
        
        String orIni=ClassMapper.classToString(info.get(15));
        String orFin=ClassMapper.classToString(info.get(16));
        try {
          oraInizio=DateUtils.StrToDate(dataCS+" "+orIni, "yyyyMMdd HH.mm.ss");
          oraFine=DateUtils.StrToDate(dataCS+" "+orFin, "yyyyMMdd HH.mm.ss");
        } catch (ParseException ex) {
          _logger.error(" Problemi di conversioni della data --> " +ex.getMessage());
        }
        
        secTot=ClassMapper.classToClass(info.get(17),Long.class);
      }
    }

    public String getBordo1() {
      return bordo1;
    }

    public String getBordo2() {
      return bordo2;
    }

    public String getCodProgramma() {
      return codProgramma;
    }

    public Double getLarghezza() {
      return larghezza;
    }

    public Double getLunghezza() {
      return lunghezza;
    }

    public Long getQta() {
      return qta;
    }

    public Double getQtaBordoTot() {
      return qtaBordoTot;
    }

    public Long getSecTot() {
      return secTot;
    }

    public Double getSpessore() {
      return spessore;
    }

    public Date getOraFine() {
      return oraFine;
    }

    public void setOraFine(Date oraFine) {
      this.oraFine = oraFine;
    }

    public Date getOraInizio() {
      return oraInizio;
    }

    public void setOraInizio(Date oraInizio) {
      this.oraInizio = oraInizio;
    }
    
    
    
    
  }
  
  
  
  
  
   private static final Logger _logger = Logger.getLogger(LogFileSQBStefaniOld.class);   
}
