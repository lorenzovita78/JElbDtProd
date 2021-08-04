/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.logFiles.R1P0.LogFileBima;
import colombini.logFiles.R1P0.LogFileBima.InfoLogBima;
import colombini.util.InfoMapLineeUtil;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import fileXLS.XlsXCsvFileGenerator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class ElabLogBimaR1P0 extends ElabClass{

  private Date dataIni;
  private Date dataFin;
  
  private String formatFile="WORK_$ddMMYY$.htm";
  private String pathFile="\\\\srvhelpdesk\\LogFileProd\\R1\\ImaTop\\BIMA\\";
  private String COLUMN="Data;Ora;Programma;CodArticolo;DescArticolo;Lunghezza;Larghezza;Spessore;Durata(ss)";
  
  
  @Override
  public Boolean configParams() {
    Map parm=getInfoElab().getParameter();
    
    if(parm.get(ALuncherElabs.DATAINIELB)!=null){
      dataIni=(Date) parm.get(ALuncherElabs.DATAINIELB);
    }
    
    if(parm.get(ALuncherElabs.DATAFINELB)!=null){
      dataFin=(Date) parm.get(ALuncherElabs.DATAFINELB);
    }
    
    if(dataIni==null)
      return Boolean.FALSE;
    
    if(dataFin==null)
      dataFin=dataIni;
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    //Map propsElab=ElabsProps.getInstance().getProperties(NameElabs.ELBLOGBIMA);
    String pathfile=(String) getElabProperties().get(NameElabs.PATHFILELOGBIMA);
    String fileCsv=(String) getElabProperties().get(NameElabs.PATHFILECSVBIMA);
    XlsXCsvFileGenerator fCsv=new XlsXCsvFileGenerator(fileCsv, XlsXCsvFileGenerator.FILE_CSV);;
    Date dataTmp=dataIni;
    Connection conSql=null;
    List result=null;
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    
    try{
      Map programs=new HashMap<String,List>();
      conSql=ColombiniConnections.getDbImaTopConnection();
      result=new ArrayList();
      pstmt=conSql.prepareStatement("select ItemNo,ItemDecription,Length,Width,Thickness from dbo.tab_et where GroovingPRG=?");
      
      while(DateUtils.beforeEquals(dataTmp, dataFin)){
        String fileName=pathfile;
        fileName=InfoMapLineeUtil.getStringReplaceWithDate(fileName, dataTmp);
        LogFileBima log=new LogFileBima(fileName);
        if(!log.exist()){
          dataTmp=DateUtils.addDays(dataTmp, 1);
          continue;
        }  
        try {
          List<InfoLogBima> infoLogDd=log.processFile(null, null); 
          for(InfoLogBima i: infoLogDd){
            List l=new ArrayList();
            l.add(i.getDataString());
            l.add(i.getOraString());
            l.add(i.getProgramName());
              
            if(!programs.containsKey(i.getProgramName()+"_"+i.getDataString())){
              
              pstmt.setString(1, i.getProgramName());
              rs=pstmt.executeQuery();
              
              if(rs.next()){
                l.add(rs.getString(1));
                l.add(rs.getString(2));
                l.add(rs.getFloat(3));
                l.add(rs.getFloat(4));
                l.add(rs.getFloat(5));
              }else{
                addWarning(" Attenzione non trovato il programma "+i.getProgramName() +" sul database Ima Top");
                l.add(" -- ");
                l.add(" -- ");
                l.add(Float.valueOf(0));
                l.add(Float.valueOf(0));
                l.add(Float.valueOf(0));
              }
              
              l.add(i.getSec());
              programs.put(i.getProgramName()+"_"+i.getDataString(),l);
           
            }else{
              //prendo i valori dal programma già esistente nella mappa
              List l2=(List) programs.get(i.getProgramName()+"_"+i.getDataString());
              l.add(l2.get(3));
              l.add(l2.get(4));
              l.add(l2.get(5));
              l.add(l2.get(6));
              l.add(l2.get(7));
              
              l.add(8, i.getSec());  
            }
            result.add(l);
          }

        } catch (IOException ex) {
          _logger.error("Errore in fase di accesso al file "+pathfile+" -->"+ex.getMessage());
          addError("Errore in fase di accesso al file "+pathfile+" -->"+ex.toString());
        }

        dataTmp=DateUtils.addDays(dataTmp, 1);
      }
    
      if(result.size()>0){
        result.add(0,ArrayUtils.getListFromArray(COLUMN.split(";")));
        fCsv.generateFileCsv(result);
      }
      
    } catch(SQLException s){
      _logger.error("Errore in fase di collegamento o interrogazione del db Ima Top -->"+s.getMessage());
      addError("Errore in fase di collegamento o interrogazione del db Ima Top -->"+s.toString());
    } catch (FileNotFoundException ex) {
      addError("Errore in fase di generazione del file "+fCsv.getFileName() +" --> "+ex.toString());
      _logger.error("Errore in fase di generazione del file -->"+ex.getMessage());
    } finally{
      try {
        if(pstmt!=null)
          pstmt.close();
        if(rs!=null)
          rs.close();
        if(conSql!=null)
          conSql.close();
        
      } catch (SQLException ex) {
        _logger.error(" Errore in fase di chiusura della connesione al db Ima Top");
      }
    }
    
  }  
    
  
//  private List getInfoPartProgram(PreparedStatement p ,String program){
//    List info=new ArrayList();
//    PreparedStatement p=null;
//    p=co 
//    
//    
//    
//    return info;
//  }
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ElabLogBimaR1P0.class);   
}
