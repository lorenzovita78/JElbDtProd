/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.costant.NomiLineeColomb;
import colombini.dtProd.materiali.UtilizzoMaterialiCalc;
import colombini.logFiles.ManageLogFileProduzione;
import colombini.util.InfoMapLineeUtil;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import elabObj.MessagesElab;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Classe che si occupa di generare i file di log giornalieri di alcune linee di produzione.
 * @author lvita
 */
public class ElabGenLogFileProd extends ElabClass {
  
  //parametro per indicare se necessario ricalcolare l'utilizzo dei materiali.
  //tale parametro non è valido per tutte le linee
  public final static String PRICALCUTLMAT="RICALCMAT";
  
  private Date dataInizio=null;
  private Date dataFine=null;
  
  private List<String> lineeToElab=null;
  private Boolean ricalcUtlMat=null;
  
  
  public ElabGenLogFileProd() {
    this.lineeToElab=getAllLineeToElab();
    ricalcUtlMat=Boolean.FALSE;
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
     
    //parametriFacoltativi 
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    if(parameter.get(PRICALCUTLMAT)!=null){
      this.ricalcUtlMat=ClassMapper.classToClass(parameter.get(PRICALCUTLMAT),Boolean.class);
    }
   
    
    this.getInfoElab().setDescription("Elaborazione Generazione Log File");
    
    return Boolean.TRUE;
    
  }

  @Override
  public void exec(Connection con) {
    
    List<Date> giorni=DateUtils.getDaysBetween(dataInizio, dataFine);
    
    
    if(isLineToElab(NomiLineeColomb.NESTINGGAL))
      addMessagesToElab(copyLogFiles(giorni, NomiLineeColomb.NESTINGGAL));
    
    if(isLineToElab(NomiLineeColomb.SQBL13266))
      addMessagesToElab(copyLogFiles(giorni, NomiLineeColomb.SQBL13266));
    
    if(isLineToElab(NomiLineeColomb.SQBL13268))
      addMessagesToElab(copyLogFiles(giorni, NomiLineeColomb.SQBL13268));
    
    if(isLineToElab(NomiLineeColomb.SCHELLLONG_GAL))
      addMessagesToElab(copyLogFiles(giorni, NomiLineeColomb.SCHELLLONG_GAL));
    
    if(isLineToElab(NomiLineeColomb.FORALBERTI))
      addMessagesToElab(copyLogFiles(giorni, NomiLineeColomb.FORALBERTI));
    
    if(isLineToElab(NomiLineeColomb.COMBIMAR1P0))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.COMBIMAR1P0));
    
    if(isLineToElab(NomiLineeColomb.SCHELLING1R1P0))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.SCHELLING1R1P0));
    
    if(isLineToElab(NomiLineeColomb.SCHELLING2R1P0))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.SCHELLING2R1P0));
    
    if(isLineToElab(NomiLineeColomb.COMBIMA1R1P1))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.COMBIMA1R1P1));
    
    if(isLineToElab(NomiLineeColomb.COMBIMA2R1P1))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.COMBIMA2R1P1));
    
    if(isLineToElab(NomiLineeColomb.SCHELLING1R1P1))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.SCHELLING1R1P1));
    
    if(isLineToElab(NomiLineeColomb.SCHELLING2R1P1))
      addMessagesToElab(loadLogFileImpIma(giorni,NomiLineeColomb.SCHELLING2R1P1));
    
    if(isLineToElab(NomiLineeColomb.MAWARTECL1))
      addMessagesToElab(loadLogFileMawArtecR1P1(giorni, NomiLineeColomb.MAWARTECL1));
    
    if(isLineToElab(NomiLineeColomb.MAWARTECL2))
      addMessagesToElab(loadLogFileMawArtecR1P1(giorni, NomiLineeColomb.MAWARTECL2));
    
    if(isLineToElab(NomiLineeColomb.FORANTEREM))
      addMessagesToElab(ManageLogFileProduzione.getInstance().loadLogFileAnteRem(giorni));
    
    //ricalcolo dei materiali
    if(ricalcUtlMat){
      UtilizzoMaterialiCalc calcMat=new UtilizzoMaterialiCalc();
      for(String lineaTmp:lineeToElab){
        if(isLineToCalcMat(lineaTmp)){
          calcMat.elabDatiLinea(con, giorni, lineaTmp);
        }
      }
      addMessagesToElab(calcMat.getMsg());
    }
    
  }
  
  
  
  public MessagesElab copyLogFiles(List<Date> giorni,String linea ){
    
    return ManageLogFileProduzione.getInstance().copyAndRenameFiles(giorni, linea);
    
  }
  
  
  
  public MessagesElab loadLogFileImpIma(List<Date> giorni,String linea){
     MessagesElab m=new MessagesElab();
     List<String> filesSource=InfoMapLineeUtil.getListLogFileSourceLinea(linea);
     m=ManageLogFileProduzione.getInstance().generateLogFileImpImaR1(filesSource, giorni, linea);
     
     return m;
  }
  
  
  public MessagesElab loadLogFileMawArtecR1P1(List <Date> giorni,String linea){
    
      return ManageLogFileProduzione.getInstance().generateLogsFileFromPathSource(giorni,
            InfoMapLineeUtil.getPathLogSourceFile(linea), linea);
    
      
  }
  
  
 
  
  
  
  private List<String> getAllLineeToElab(){
    List<String> linee=new ArrayList();
    linee.add(NomiLineeColomb.NESTINGGAL);
    linee.add(NomiLineeColomb.SCHELLLONG_GAL);
    linee.add(NomiLineeColomb.SQBL13266);
    linee.add(NomiLineeColomb.SQBL13268);
    
    linee.add(NomiLineeColomb.COMBIMAR1P0);
    linee.add(NomiLineeColomb.SCHELLING1R1P0);
    linee.add(NomiLineeColomb.SCHELLING2R1P0);
    
    linee.add(NomiLineeColomb.COMBIMA1R1P1);
    linee.add(NomiLineeColomb.COMBIMA2R1P1);
    linee.add(NomiLineeColomb.SCHELLING1R1P1);
    linee.add(NomiLineeColomb.SCHELLING2R1P1);
    
    linee.add(NomiLineeColomb.MAWARTECL1);
    linee.add(NomiLineeColomb.MAWARTECL2);
    
    linee.add(NomiLineeColomb.FORANTEREM);
    linee.add(NomiLineeColomb.FORALBERTI);
    
    return linee;
  }
  
  
// /**
//   * Invia messaggio via mail per verifica file di Log generati
//   */
//  private void sendInfoMailMessage(){
//    List<String> msgI=this.getMessElab().getInfos();
//    if(msgI==null || msgI.isEmpty())
//      return;
//    
//    //generazione messaggio via mail per capire com'è andata l'elaborazione
//    StringBuilder msg=new StringBuilder();
//    for(String s:msgI){
//      if(msg.length()==0)
//        msg.append(" Informazioni relative ai file di Log generati  \n");
//      
//      msg.append(s).append(" \n ");
//    }
//    
//    List to=ArrayUtils.getListFromArray(new Object [] {"lvita@colombinigroup.com"} );
//    MailUtil.getInstance().sendMessage(ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(MailCostants.SMTP_IPV4)), 
//             "infoJ@colombinigroup.com", "Elaborazione Generazione Log File", msg.toString(), to, null, null, null);
//    
//  } 
 
  private Boolean isLineToElab(String nomeLinea){
    return lineeToElab.contains(nomeLinea);
  }
  
  private Boolean isLineToCalcMat(String nomeLinea){
    if(NomiLineeColomb.COMBIMA1R1P1.equals(nomeLinea))
      return Boolean.TRUE;
    if(NomiLineeColomb.COMBIMA2R1P1.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SCHELLING1R1P1.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SCHELLING2R1P1.equals(nomeLinea)) 
      return Boolean.TRUE;
    
    if(NomiLineeColomb.COMBIMAR1P0.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SCHELLING1R1P0.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SCHELLING2R1P0.equals(nomeLinea)) 
      return Boolean.TRUE;
    
    if(NomiLineeColomb.SCHELLLONG_GAL.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SQBL13268.equals(nomeLinea)) 
      return Boolean.TRUE;
    if(NomiLineeColomb.SQBL13266.equals(nomeLinea)) 
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
 private static final Logger _logger = Logger.getLogger(ElabGenLogFileProd.class); 

  

}




 


