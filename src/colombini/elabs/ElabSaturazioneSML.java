/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.elabs.old.ElabSfridiProdColombiniOld;
import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.tab.R1.DtSaturazioneMSL;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import fileXLS.FileXlsXPoiRW;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabSaturazioneSML extends ElabClass{
  
//  public final static String PATHFILE="//pegaso/Produzione/Rovereta1/VDL-MSL/Saturazione MSL";
  
  public final static String MAGSML="MSL";
  
  public final static String EMPTY="EMPTY";
  public final static String SSKU0="SSKU0";

  public final static String SHEETGIACENZA="giacenzaperarticoli";
  
  public final static String TABLE10="10";
  public final static String TABLE14="14";
  public final static String TABLE28="28";
  
  
  public final static Integer COLTAVOLA=Integer.valueOf(5);
  public final static Integer COLARTI=Integer.valueOf(6);
  
  public final static Integer COLDEP=Integer.valueOf(9);
  public final static Integer COLDES=Integer.valueOf(10);
  
  
  public final static String STMSTART="select mbwhlo,mbsuno from mitbal"
                                    +" where mbcono= ? "
                                    +" and mbwhlo= (select MBSUWH from mitbal "
                                    +" where mbcono= ?"
                                    +" and mbwhlo= ? "
                                    +" and mbitno= ? )"
                                    +" and mbitno= ? ";
  
   public final static String STMSTART2="select MBWHLO,MBSUNO,MBSUWH from mitbal"
                                    +" where mbcono= ? "
                                    +" and mbitno= ?"
                                    +" and mbresp<>'TSTCMP' "
                                    +" and mbpuit<3 ";
                                   
  
  
  public final static String STMFINISH="select MBWHLO from mitbal "
                                       +" where mbcono= ? "
                                       +" and mbitno= ? "
                                       +" and mbpuit=3 "
                                       +" and mbresp<>'TSTCMP' "
                                       +" and mbsuwh= ? ";

  private Date dateElab;

  public ElabSaturazioneSML() {
    
  }
  
  @Override
  public Boolean configParams() {
     Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dateElab=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    
    if(dateElab==null )
      return Boolean.FALSE;
    
    return Boolean.TRUE;
     
  }

  @Override
  public void exec(Connection con) {
    PersistenceManager manP=null;
    String fileName="";
    try { 
      _logger.info(" ----- Inizio Elaborazione Saturazione MSL -----");
      manP=new PersistenceManager(con);
      Integer year=DateUtils.getYear(dateElab);
      //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBSATURAZIONEMSL);
      String pathFile=(String) getElabProperties().get(NameElabs.PATHFILESATMSL);
      String pathC=pathFile+"/"+year+"/";
      String pathNew=pathC+"temp/";
      
      fileName=getFileToElab(pathC);
      
      if(fileName==null || fileName.isEmpty()){
        _logger.warn("Attenzione nessun file modificato in data"+dateElab);
        addWarning("Nessun file modificato in data "+dateElab);
      }
      
      if(!StringUtils.isEmpty(fileName)){
        FileUtils.copyFile(pathC+fileName, pathNew+fileName);
        Map itnoMap=prepareItnoMap(con,pathNew+fileName);
        
        List listToSave=elabIntoMap(itnoMap);
        
        if(listToSave!=null && !listToSave.isEmpty()){
          DtSaturazioneMSL dt=new DtSaturazioneMSL();
          List listFD=new ArrayList();
          listFD.add(CostantsColomb.AZCOLOM);
          listFD.add(DateUtils.getDataForMovex(dateElab));
          
          manP.deleteDtWithKey(dt, listFD);
          manP.saveListDt(dt, listToSave);
        }
      }
      
    } catch (IOException ex) {
      _logger.error("Problemi in fase di accesso al file : "+fileName+" --> "+ex.getMessage());
      addError("Problemi in fase di accesso al file : "+fileName+" --> "+ex.getMessage());
    } catch(SQLException s){
      _logger.error("Problemi in fase di salvataggio dei dati --> "+s.getMessage());
      addError("Problemi in fase di salvataggio dei dati --> "+s.getMessage());
    }  finally{
      _logger.info(" ----- Fine Elaborazione Saturazione MSL -----");
    }   
  }

  
  
  
  
 
  
  private List elabIntoMap(Map itnoMap){
    List dataStore=new ArrayList();
    Map typesWh=new HashMap();
    
    Set keys=itnoMap.keySet();
    Iterator iter=keys.iterator();
//    System.out.println(" ARTICOLO;TIPOT;OCCORRENZE");
    while (iter.hasNext()){
      String articolo=ClassMapper.classToString(iter.next());
      ObjIItno iitno=(ObjIItno) itnoMap.get(articolo);
      String dep=iitno.getSupplier();
      if(StringUtils.isEmpty(dep))
        dep=iitno.getwHDeparture();
      
      if(dep==null){
        _logger.info("Attenzione articolo senza partenza "+articolo);
        dep="";
      }
      String des=iitno.getwHDestination();
      if(des==null){
        _logger.info("Attenzione articolo senza destinazione "+articolo);
        des="";
      }
      
//      System.out.println(articolo+";"+iitno.getTypeTable()+";"+iitno.getOccorrenze());
      
      String keyW=dep+"-"+des;
      InfoSat sat=null;
      if(typesWh.containsKey(keyW)){
        sat=(InfoSat) typesWh.get(keyW);
        sat.addNTable(iitno.getTypeTable(),iitno.getOccorrenze());
      }else{
        sat=new InfoSat();
        sat.setDeparture(dep);
        sat.setDestination(des);
        
        sat.addNTable(iitno.getTypeTable(),iitno.getOccorrenze());
      }
      
      typesWh.put(keyW,sat);
      
    }
//    System.out.println(";;;;\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    System.out.println("DEPARTURE;DESTINATION;NTAV10;NTAV14;NTAV28");
    
    keys=typesWh.keySet();
    iter=keys.iterator();
    while (iter.hasNext()){
      String keyString=(String) iter.next();
      InfoSat is=(InfoSat) typesWh.get(keyString);
      List record=new ArrayList();
      record.add(CostantsColomb.AZCOLOM);
      record.add(DateUtils.getDataForMovex(dateElab));
      int idx=keyString.lastIndexOf("-");
      String dep =keyString.substring(0, idx);
      String dest=keyString.substring(idx+1);
      record.add(dep);
      record.add(dest);
      record.add(is.getnTav10());
      record.add(is.getnTav14());
      record.add(is.getnTav28());
      record.add(DateUtils.getDataSysLong());
      record.add(DateUtils.getOraSysLong());
      dataStore.add(record);
      
      System.out.println(dep+";"+dest+";"+is.getnTav10()+";"+is.getnTav14()+";"+is.getnTav28());
      
    }
    
    return dataStore;
  }
  
  
  private String getFileToElab(String pathC){
    
    File d=new File(pathC);
    if(d.isFile()){
      _logger.warn("Path "+pathC+" non valido. Impossibile proseguire");
      addError("Path "+pathC+" non valido. Impossibile proseguire l'elaborazione ");
      return ""; 
    }
    
    
    List<File> files=ArrayUtils.getListFromArray(d.listFiles());
    for(File f:files){
      Date datef=new Date(f.lastModified());
      if(DateUtils.numGiorniDiff(datef, dateElab)==0){
        return(f.getName());
      }
    }
    
    
    

    return "";
  }
  
  private Map prepareItnoMap(Connection con,String fileName) {
    Map itnoMap=new HashMap<String,ObjIItno>();
    int i=1;
    Long artProc=Long.valueOf(0);
    Long rwProc=Long.valueOf(0);
    FileXlsXPoiRW xlsx=new FileXlsXPoiRW(fileName);
    try{
       xlsx.prepareFileXls();
       XSSFSheet sh=xlsx.getSheet(SHEETGIACENZA);
       int rows=sh.getLastRowNum();
       
       while(i<=rows){
         ObjIItno ii=null;
         XSSFRow row=sh.getRow(i);
         if(row!=null){
            rwProc++;
            XSSFCell cellT=row.getCell(COLTAVOLA);
            Double tavola=null;
            String typeT="";
            if(cellT!=null){
              tavola=cellT.getNumericCellValue();
              typeT=tavola.toString().substring(0,2);
            }
            
            XSSFCell cellA=row.getCell(COLARTI);
            String articolo="";
            
            if(cellA!=null && cellA.getCellType()==XSSFCell.CELL_TYPE_NUMERIC ){
              articolo=ClassMapper.classToString(cellA.getNumericCellValue());
            } else if(cellA!=null && cellA.getCellType()==XSSFCell.CELL_TYPE_STRING){ 
              articolo=cellA.getStringCellValue();
            }
            
            
            
            if(!StringUtils.isEmpty(articolo) && !articolo.contains(EMPTY) && !articolo.contains(SSKU0)){
              if(!itnoMap.containsKey(articolo)){
                
                artProc++;
                ii=new ObjIItno(articolo);
                ii.setTypeTable(typeT);
                
                ObjDep dept=getDeparture(con, CostantsColomb.AZCOLOM, articolo,MAGSML);
                if(dept!=null){
                  ii.setwHDeparture(dept.getWarehouse());
                  ii.setSupplier(dept.getSupplier());
                }
                
                String dest=getDestination(con, CostantsColomb.AZCOLOM, articolo, MAGSML);  
                ii.setwHDestination(dest);
               
              }else{
                ii=(ObjIItno) itnoMap.get(articolo);
                ii.addOccorrenze();
                if(!ii.getTypeTable().equals(typeT)){
                  _logger.warn("Attenzione articolo "+articolo+" su due tipologie di tavole differenti "+ii.getTypeTable()+" --  "+typeT);
                  addInfo("Articolo "+articolo+" su due tipologie di tavole differenti "+ii.getTypeTable()+" --  "+typeT);
                }  
              }
              
              XSSFCell cellDep=row.createCell(COLDEP);
              XSSFCell cellDes=row.createCell(COLDES);
                
              String depa=ii.getSupplier();
              if(StringUtils.isEmpty(depa))
                depa=ii.getwHDeparture(); 

              cellDep.setCellValue(depa);
              cellDes.setCellValue(ii.getwHDestination());
                
              itnoMap.put(articolo,ii);
              
            }
         }  
         i++;
       }
       
     xlsx.closeAndSaveWFile();   
    
    }catch(FileNotFoundException e){
     _logger.error("File xls non trovato "+e.getMessage()); 
     addError("File xls "+fileName+" non trovato "+e.toString() );
    }catch(IOException e){
      _logger.error("Problemi di accesso al file "+e.getMessage()); 
      _logger.error("Problemi di accesso al file "+fileName+" --> "+e.toString()); 
    } catch (Exception ex) {
      _logger.error("Eccezione generica per riga"+i+" --> "+ex.getMessage());
      addError("Errore in fase di modifica file xls "+fileName+ " su riga"+i+" --> "+ex.getMessage());
    } finally{
      _logger.info("Righe processate :"+rwProc+" articoliProc : "+artProc);
    }
    
    return itnoMap;
  }
  
  
  
  private ObjDep getDeparture(Connection con, Integer az,String itno, String warehouse){
    ObjDep dep= null;
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    try{
        pstmt = con.prepareStatement(STMSTART2); 
        pstmt.setInt(1, az);
        pstmt.setString(2, itno);
        rs=pstmt.executeQuery();
        while(rs.next()){
          String whIni=rs.getString("MBSUWH");
          //se la colonna MSSUWH (mag fornitore) è vuota allora posso prendere i dati del fornitore
          if(whIni==null || whIni.isEmpty() || !MAGSML.equals(whIni)){
            String whS=rs.getString("mbwhlo");
            String suno=rs.getString("mbsuno").trim();
            dep=new ObjDep(whS);
            dep.setSupplier(suno);
          }
          
        }
        
      } catch (SQLException ex) {
        _logger.error("Impossibile estrarre informazioni per articolo :"+itno+" e magazzino :"+warehouse);
        addError("Impossibile estrarre partenza per articolo :"+itno+" e magazzino :"+warehouse+" -->"+ex.toString());
//      } catch (Exception ex) {
//        _logger.error("Eccezione generica per :"+itno+" e magazzino :"+warehouse+" --> "+ex.getMessage());
      } finally{
        try {
          if(pstmt!=null)
            pstmt.close();
          if(rs!=null)
            rs.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura dello statment per articolo :"+itno+" e magazzino :"+warehouse);
        }
      }
    
    
    return dep;
  }
  
  
  private String getDestination(Connection con, Integer az,String itno, String warehouse){
    String destination = null;
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    
    try{
        pstmt = con.prepareStatement(STMFINISH); 
        pstmt.setInt(1, az);
        pstmt.setString(2, itno);
        pstmt.setString(3, warehouse);
         
        rs=pstmt.executeQuery();
        if(rs.next()){
          destination=rs.getString("mbwhlo");
        }
        
      } catch (SQLException ex) {
        _logger.error("Impossibile estrarre informazioni per articolo :"+itno+" e magazzino :"+warehouse);
        addError("Impossibile estrarre destinazione per articolo :"+itno+" e magazzino :"+warehouse+" -->"+ex.toString());
//      } catch (Exception ex) {
//        _logger.error("Eccezione generica per :"+itno+" e magazzino :"+warehouse+" --> "+ex.toString());
//        addError("Errore per articolo :"+itno+" e magazzino :"+warehouse+" --> "+ex.toString());
      } finally{
        try {
          if(pstmt!=null)
            pstmt.close();
          if(rs!=null)
            rs.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura dello statment per articolo :"+itno+" e magazzino :"+warehouse);
        }
      }
    
    return destination;
  }

  
  public class ObjIItno{
    private String itno;
    private Long ntable;
    private Integer qta;
    private Integer occorrenze;
    private String typeTable;
    
    private String wHDeparture;
    private String supplier;
    private String wHDestination;

    public ObjIItno(String itno) {
      this.itno=itno;
      this.occorrenze=Integer.valueOf(1);
    }

    
    
    public String getItno() {
      return itno;
    }

    public void setItno(String itno) {
      this.itno = itno;
    }

    public Long getNtable() {
      return ntable;
    }

    public void setNtable(Long ntable) {
      this.ntable = ntable;
    }

    public Integer getQta() {
      return qta;
    }

    public void setQta(Integer qta) {
      this.qta = qta;
    }

    public String getSupplier() {
      return supplier;
    }

    public void setSupplier(String supplier) {
      this.supplier = supplier;
    }

    public String getTypeTable() {
      return typeTable;
    }

    public void setTypeTable(String typeTable) {
      this.typeTable = typeTable;
    }

    public String getwHDeparture() {
      return wHDeparture;
    }

    public void setwHDeparture(String wHDeparture) {
      this.wHDeparture = wHDeparture;
    }

    public String getwHDestination() {
      return wHDestination;
    }

    public void setwHDestination(String wHDestination) {
      this.wHDestination = wHDestination;
    }

    public Integer getOccorrenze() {
      return occorrenze;
    }

    public void setOccorrenze(Integer occorrenze) {
      this.occorrenze = occorrenze;
    }
    
    public void addOccorrenze(){
      occorrenze++;
    }
    
  }
  
  public class ObjDep {
    String warehouse;
    String supplier;

    public ObjDep(String warehouse) {
      this.warehouse = warehouse;
    }

     
    public String getSupplier() {
      return supplier;
    }

    public void setSupplier(String supplier) {
      this.supplier = supplier;
    }

    public String getWarehouse() {
      return warehouse;
    }

    public void setWarehouse(String warehouse) {
      this.warehouse = warehouse;
    }
    
  
  }
  
  
  public class InfoSat{
    String departure;
    String destination;
    
    Integer nTav10;
    Integer nTav14;
    Integer nTav28;

    
    public InfoSat() {
      departure="";
      destination="";
      nTav10=Integer.valueOf(0);
      nTav14=Integer.valueOf(0);
      nTav28=Integer.valueOf(0);
    }

    
    public String getKey(){
      return departure+"-"+destination;
    }
    
    
    
    public String getDeparture() {
      return departure;
    }

    public void setDeparture(String depature) {
      this.departure = depature;
    }

    public String getDestination() {
      return destination;
    }

    public void setDestination(String destination) {
      this.destination = destination;
    }

    public Integer getnTav10() {
      return nTav10;
    }

    public void setnTav10(Integer nTav10) {
      this.nTav10 = nTav10;
    }

    public Integer getnTav14() {
      return nTav14;
    }

    public void setnTav14(Integer nTav14) {
      this.nTav14 = nTav14;
    }

    public Integer getnTav28() {
      return nTav28;
    }

    public void setnTav28(Integer nTav28) {
      this.nTav28 = nTav28;
    }

    
    public void addNTable(String type,Integer qta){
      if(TABLE10.equals(type)){
        nTav10+=qta;
      }else if(TABLE14.equals(type)){
        nTav14+=qta;
      }else if(TABLE28.equals(type)){
        nTav28+=qta;
      }
      
    }
    
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabSfridiProdColombiniOld.class);   
  
}

