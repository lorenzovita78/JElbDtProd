
import elabObj.ALuncherElabs;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.ParameterMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author lvita
 */
public class StartUp {

  /**
   * @param args the command line arguments
   */
  
  private final static String FILEELABS="../props/elabsSchedJLV.config";
  
  
  
  public static void main(String[] args) {
   
    _logger.info(" -----  AVVIO APPLICAZIONE ... #### \t\t\t\t -----");
    System.out.println("----- AVVIO APPLICAZIONE  ... -----");
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    List<Map> listaElabs=new ArrayList();
    Boolean checkElab=Boolean.TRUE;
    try {
       if(args!=null && args.length>0){
          System.out.println(".. Lettura configurazione elaborazione da riga di comando...");
         _logger.info(".. Lettura configurazione elaborazione da riga di comando...");
         int ln=args.length;
         _logger.info(".. Numero parametri passati da riga comandi >>"+ln);
         String pamS="";
         for(int i=0;i<ln;i++){
           pamS+=ClassMapper.classToString(args[i]);
         }
         _logger.info("Stringa parametri letta : "+pamS);
         Map parameter =ParameterMap.getParameterMap(pamS);
         if(parameter==null || parameter.isEmpty()){
           System.out.println("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
           _logger.info("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
         }else{ 
           _logger.debug("Mappa parametri letti per riga: "+parameter.toString());
           listaElabs.add(parameter);
           checkElab=Boolean.FALSE;
        }
       }else{
        System.out.println(".. Lettura configurazione elaborazioni da lanciare  da file config...");
        _logger.info(".. Lettura configurazione elaborazioni da lanciare da file config...");
        fR = new FileReader(FILEELABS);
        bfR=new BufferedReader(fR);

        riga = bfR.readLine();  
        while(riga!=null ){
          count++;
          if(!riga.isEmpty() && !riga.contains("#")){
           Map parameter =ParameterMap.getParameterMap(riga);
           if(parameter==null || parameter.isEmpty()){
             System.out.println("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
             _logger.info("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
           }else{ 
             _logger.debug("Mappa parametri letti per riga: "+parameter.toString());             
             listaElabs.add(parameter);
           }
          }
          riga=bfR.readLine(); 
        }
       }
      //lancio il gestore delle elaborazioni 
      LuncherElabDtProd lunch=new  LuncherElabDtProd(listaElabs);
      lunch.run(checkElab);
      
    } catch(FileNotFoundException ex){
      _logger.error("File "+FILEELABS +" non trovato. Impossibile lanciare l'elaborazione!!");
    } catch(IOException ex){
      _logger.error("Errore in fase di lettura del file"+FILEELABS +" --> "+ex.getMessage());
    }finally{
      _logger.info("File "+FILEELABS+" righe lette :"+count);
      try{
        if(bfR!=null)
          bfR.close();
        if(fR!=null)
          fR.close();
      } catch(FileNotFoundException ex){
        _logger.warn("Errore in fase di chiusura del file"+FILEELABS +" --> "+ex.getMessage());
      } catch(IOException ex){
        _logger.warn("Errore in fase di chiusura del file"+FILEELABS +" --> "+ex.getMessage());
      }
      
      System.out.println("----- TERMINE APPLICAZIONE... -----"); 
      _logger.info(" ----- \t\t\t\t #### TERMINE APPLICAZIONE #### \t\t\t\t -----");
    }  
       
     
     
       
     }
  
  
  private static final Logger _logger = Logger.getLogger(StartUp.class); 
}
