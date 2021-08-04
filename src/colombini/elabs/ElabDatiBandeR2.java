/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.costant.CostantsColomb;
import colombini.dtProd.R2.DtBandeR2Utils;
import colombini.model.DtBandeR2;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabDatiBandeR2 extends ElabClass{
 
  public static final String PATHFILES="//pegaso/uffici/Qualità2/R2_IMA/"; //"C:/Users/lvita/Desktop/file R2/";  //"
  
  
  
  private Date dataInizio;
  private Date dataFine;
  
  private String pathFilesBande="";
  
  public ElabDatiBandeR2(){
    
  }
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    Long dataN=Long.valueOf(0);
    List<Date> giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBDTBANDER2);
//    pathFilesBande=(String) prop.get(NameElabs.PATHFILESBANDER2);
    pathFilesBande=PATHFILES;
    
    if(StringUtils.IsEmpty(pathFilesBande)){
      addError("Percorso di rete dove reperire i file delle bande non specificato. Controllare il file di configurazione");
      return;
    }
      
    for(Date data:giorni){
      try {
        Map mappaDati=processFilesBande(data);
        if(mappaDati!=null && !mappaDati.isEmpty()){
          dataN=DateUtils.getDataForMovex(data);
          DtBandeR2Utils.getInstance().deleteDatiGg(con, CostantsColomb.AZCOLOM, dataN);
          DtBandeR2Utils.getInstance().insertDatiGg(con, mappaDati);
        }
      } catch (SQLException ex) {
        _logger.error("Impossibile salvare i dati per il giorno "+dataN+ " -> "+ex.getMessage());
        addError("Errore in fase di cancellazione/salvataggio dei dati relativi al gg "+dataN+" --> "+ex.toString());
      }
    }
  }
  
  private Map processFilesBande (Date dataRif){
    String dataS=DateUtils.dateToStr(dataRif, "yyyy-MM-dd");
    Map mappaVal=new HashMap();
    Long count=Long.valueOf(0);
    //prendo la lista di elementi del path
    File f=new File(pathFilesBande);
    
    List<String> totFiles=ArrayUtils.getListFromArray(f.list());
    String nomeFTemp="";
    for(String ftmpS:totFiles){
      //faccio il controllo sulla stringa della data per non caricare n oggetti di tipo file!!
      if(ftmpS!=null &&  ftmpS.contains(dataS)){
        try{
          nomeFTemp=ftmpS;
          processFile(pathFilesBande+ftmpS,dataRif,mappaVal);
          count++;
        }catch(IOException e){
         _logger.error("Errore in fase di accesso al file"+e.getMessage());
         addError("Errore in fase di accesso al file di log "+nomeFTemp+ " per il giorno "+dataS+" -->"+e.toString()); 
        }
      }
    }
    
    _logger.info("Processati per il giorno "+dataS + "n. "+count+" files");
    
    return mappaVal; 
  }
  
  private void processFile(String fileName,Date dataRif,Map mappaDati) throws FileNotFoundException, IOException{
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    
    long count=0;
    try{
      fR = new FileReader(fileName);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();  
      while(riga!=null && !riga.isEmpty()){
        count++;
        DtBandeR2 bd=elabDatiRiga(riga);
        bd.setDataRifN(DateUtils.getDataForMovex(dataRif));
        String key=bd.getDataRifN()+bd.getCodBanda();
        if(!mappaDati.containsKey(key)){
           mappaDati.put(key, bd);
        }else{
          DtBandeR2 bd2=(DtBandeR2) mappaDati.get(key);
          bd2.aggLngBandaBuona(bd.getLunTotBuona());
          bd2.aggLngBandaDiff(bd.getLunTotDif());
          bd2.incNBandeBuone(bd.getNBandeBuone());
          bd2.incNBande1Dif(bd.getNBande1Dif());
          bd2.incNBande2Dif(bd.getNBande2Dif());
          bd2.incNBande3Dif(bd.getNBande3Dif());
          bd2.incNBandePlus4Dif(bd.getNBandePlus4Dif());
          mappaDati.put(key,bd2);
        }
        
        riga = bfR.readLine();  
      }
      _logger.info("File "+fileName+" righe processate:"+count);
    }finally{
      if(bfR!=null)
        bfR.close();
      if(fR!=null)
        fR.close();
    }

  }
  
  
  private DtBandeR2 elabDatiRiga(String riga){
    DtBandeR2 info= new DtBandeR2();
    Double lOld=Double.valueOf(0);
    Integer numDif=Integer.valueOf(0);
    
    List elem=ArrayUtils.getListFromArray(riga.split(";"));
    
    Double lun=ClassMapper.classToClass(elem.get(1), Double.class);
    
//    info.setDataRifN(DateUtils.getDataForMovex(dataElab));
    info.setCodBanda(ClassMapper.classToString(elem.get(0)));
    info.setLunTotBuona(lun);
 
    
    //    --info.s
    int sizeL=elem.size();
    if(sizeL>=5){
      for(int i=5;i<sizeL;i++){
        String lS=(String)elem.get(i);
        lS=lS.replace(".", ",");
        Double l=ClassMapper.classToClass(lS, Double.class);
        //se i è pari allora posso calcolare la lugnhezza del difetto
        if(i%2==0){
          Double diff=l-lOld;
          if(diff<0){
            _logger.error("Differenza lunghezza banda fallata non corretta");
            continue;
          }  
          //aggiorno la lunghezza totale di banda difettata e diminuisco la lunghezza totale della banda buona
          info.aggLngBandaDiff(diff);
          info.aggLngBandaBuona(-diff);
          numDif++;
        }
        lOld=l;
      }
    }
    
    if(numDif>0){
       info.aggNumBandeDifettose(numDif);
       info.setNBandeBuone(Integer.valueOf(0));
    }else{
      info.incNBandeBuone();
    }
    
    
    return info;
  }

  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabDatiBandeR2.class); 

  
}
