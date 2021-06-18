
import db.Connections;
import db.ResultSetHelper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ExecUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lvita
 */
public class Test {
  
  
  
  public static void main(String[] args) throws InterruptedException {
    
    _logger.info("------- TEST ------------");
    _logger.info("Inizio");
    
    System.out.println("Test...");
//      List commands=new ArrayList();
//      commands.add("E:");
//      commands.add("C:\\tabcmd\\extras\\Command Line Utility\tabcmd");
//      executeCommands(commands);
    try {
      List l=new ArrayList();
      //System.out.println("Comando eseguito correttamente ");
      Connection con =Connections.getOracleConnection("192.168.112.13", "ORCL", "DBS_HV", "DBS_HV");
      
      String sql=" select MESSAGEID, STATUS, MESSAGEOBJECTTYPE, INSERTTS\n" +
                 ", COMPLETIONTS, REJECTMESSAGEID, REJECTINFORMATION, REPORTTS\n" +
                 " FROM v2h_queue ";
      
      ResultSetHelper.fillListList(con, sql, l);
      System.out.println("List msg -->"+l.toString());
      
      
    } catch (SQLException ex) {
      _logger.error("Errore in fase di connessione al db -->"+ex.getMessage()+" -- "+ex.getSQLState());
    }
     
    
    _logger.info("Fine");
    
  }
  
  
  
  private static void executeCommands(List<String> commands){
    if(commands==null || commands.isEmpty()){
      _logger.warn(" Lista comandi da lanciare via batch vuota");
    }
   
    int count=commands.size();
    int i=0;
    Boolean ok =Boolean.TRUE;
    for(String command:commands){
      try{
         int exec=-1;
         if(ok) 
           exec=ExecUtil.ExecuteCommand(command);
         
         if(exec>0){
           ok=Boolean.FALSE;  
         }
         
      } catch(IOException ioe ){
        _logger.error(" Errore in fase di esecuzione del comando --> "+ioe.getMessage());
        
      } catch (InterruptedException ex) {
        _logger.error(" Errore in fase di esecuzione del comando --> "+ex.getMessage());
       
      }   
    }
    
  }
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Test.class); 
}
