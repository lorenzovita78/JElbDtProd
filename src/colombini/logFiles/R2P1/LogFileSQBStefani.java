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
public class LogFileSQBStefani {

  public final static String MATERIALE="MATERIALE";
  public final static String PROGRAMMIUTIL="PROGRAMMIUTIL";
  public final static String PATHFILEREPORT="//172.18.0.126/report/";
  public final static String PATHFILEPROGRAM="//172.18.0.126/Programmi/";
  
  
  public final static Double SETUP=Double.valueOf(125.1);
  public final static Double SETUPCAMBIOPROG=Double.valueOf(201.3);
  
  
  private String fileName;
  
  public LogFileSQBStefani(String fn) {
    this.fileName=fn;
  }
  
  
  public Map elabFile(Date dataInizio,Date dataFine) throws FileNotFoundException, IOException{
    
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Double count=Double.valueOf(0);
    Long tprod=Long.valueOf(0);
    Long pz=Long.valueOf(0);
    Long nPR=Long.valueOf(0);
    Double setup=Double.valueOf(0);
    Double materiale=Double.valueOf(0);
    Long fermi=Long.valueOf(0);
    Map mappaProg=new HashMap();
    Map result=new HashMap();
    InfoRow rwOld=null;
    
    List fermiList=new ArrayList();
    
    
    //modifica da eliminare non appena sistemano il file di log
    dataInizio=DateUtils.addMinutes(dataInizio, 60);
    dataFine=DateUtils.addMinutes(dataFine, 60);
    //
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      riga = bfR.readLine();  
      Long sec=Long.valueOf(0);
      InfoRow rw=null;
      InfoRow finalrw=null;
      while(riga!=null && !riga.isEmpty()){
        count++;
        sec=Long.valueOf(0);
        rw=new InfoRow(riga);
        if(DateUtils.beforeEquals(rw.getOraElab(), dataFine) && 
           DateUtils.afterEquals(rw.getOraElab(), dataInizio)  ){
           if(rwOld!=null )
             sec=DateUtils.numSecondiDiff(rwOld.getOraElab(), rw.getOraElab());
           
           if(sec>=2000){
             _logger.warn("Attenzione fermo tra un programma ed un altro > di 1 ora --> ora fine:"
                    +rwOld.getOraElab()+" -- ora inizio successivo: "+rw.getOraElab());
             
             List record=new ArrayList();
             record.add(rwOld.getOraElab());
             record.add(rw.getOraElab());
             fermiList.add(record);
             
             fermi+=sec;
           }
           
           tprod+=sec;
           pz++;
           materiale+=rw.getQtaBordoTot();
           Double d=rw.getLunghezza()/1000;
           String key=rw.getCodProgramma()+"-"+d.toString().substring(0, 3);
           ObjPrgSQBStefani obj=null;
           if(mappaProg.containsKey(key)){
             obj=(ObjPrgSQBStefani) mappaProg.get(key);             
           }else{
             //se il programma non è nella mappa significa che è stato appena cambiato.
             //quindi non consideriamo il tempo tra la riga precedente e l'attuale come runtime ma come setup.
             obj=new ObjPrgSQBStefani(rw.getCodProgramma());
             obj.setLunghezza(rw.getLunghezza());
             obj.setLarghezza(rw.getLarghezza());
             obj.setSpessore(rw.getSpessore());
             setup+=SETUPCAMBIOPROG;

           }
//           obj.addTempo(sec);
           obj.addQta(Long.valueOf(1));
           mappaProg.put(key,obj);
           
           if(rwOld!=null && rwOld.getCodProgramma().equals(rw.getCodProgramma()) && sec >13 && sec<=SETUP.longValue()){
             setup+=SETUP.longValue()-sec;
           }
           
           
           rwOld=rw;
        }else{
          if(finalrw==null)
            finalrw=rw;
        }
        riga = bfR.readLine();  
      }
//      if(rwOld!=null && finalrw!=null){
//          ObjPrgSQBStefani obj=null;
//          sec=DateUtils.numSecondiDiff(rwOld.getOraElab(), finalrw.getOraElab());
//          
//          Double d=finalrw.getLunghezza()/1000;
//          String key=finalrw.getCodProgramma()+"-"+d.toString().substring(0, 3);
//
//          if(mappaProg.containsKey(key)){
//            obj=(ObjPrgSQBStefani) mappaProg.get(key);
//            
//          } else{
//             obj=new ObjPrgSQBStefani(finalrw.getCodProgramma());
//             obj.setLunghezza(finalrw.getLunghezza());
//             obj.setLarghezza(finalrw.getLarghezza());
//             obj.setSpessore(finalrw.getSpessore());
//             setup+=SETUPCAMBIOPROG;
//          }
//          
//          obj.addQta(Long.valueOf(1));
//          mappaProg.put(key,obj);
//        }
      
      result.put(CostantsColomb.TPRODUZ,tprod);
      result.put(CostantsColomb.TSETUP, setup.longValue());
      result.put(CostantsColomb.NPZTOT, pz);
      result.put(CostantsColomb.TGUASTI,fermi);
      result.put(CostantsColomb.LISTFERMI,fermiList);
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
    private String bordo1;
    private String bordo2;
    private Double qtaBordoTot;
    
    private Date oraElab;
    
    
    
    public InfoRow(String riga){
      if(riga==null)
        return;
      List info=ArrayUtils.getListFromArray(riga.split(";"));
      if(info!=null && info.size()>17){
        String anno=ClassMapper.classToString(info.get(0));
        String mese=addZero(ClassMapper.classToString(info.get(1)));
        String gg=ClassMapper.classToString(info.get(2));
        String ora=addZero(ClassMapper.classToString(info.get(3)));
        String min=addZero(ClassMapper.classToString(info.get(4)));
        String sec=addZero(ClassMapper.classToString(info.get(5)));
        
        
        codProgramma=ClassMapper.classToString(info.get(8));
        lunghezza=ClassMapper.classToClass(info.get(10),Double.class);
        larghezza=ClassMapper.classToClass(info.get(11),Double.class);
        spessore=ClassMapper.classToClass(info.get(12),Double.class);
        bordo1=ClassMapper.classToString(info.get(14));
        bordo2=ClassMapper.classToString(info.get(16));
        qtaBordoTot=ClassMapper.classToClass(info.get(15),Double.class)+ClassMapper.classToClass(info.get(17),Double.class);
        
        try {
          oraElab=DateUtils.StrToDate(anno+mese+gg+" "+ora+min+sec, "yyyyMMdd HHmmss");        
        } catch (ParseException ex) {
          _logger.error(" Problemi di conversioni della data --> " +ex.getMessage());
        }
        
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

   
    public Double getQtaBordoTot() {
      return qtaBordoTot;
    }

    public Double getSpessore() {
      return spessore;
    }

    public Date getOraElab() {
      return oraElab;
    }

    
    
    
    private String addZero(String info){
      if(info!=null && info.length()==1)
        return "0"+info;
      
      return info;
    }
    
    
    
  }
  
  
  
  
  
   private static final Logger _logger = Logger.getLogger(LogFileSQBStefani.class);   
}
