/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P1;

import colombini.util.InfoMapLineeUtil;
import colombini.costant.NomiLineeColomb;
import colombini.xls.FileXlsDatiProdStd;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class XlsMawArtec extends FileXlsDatiProdStd{

  public final static String CODMAW1 ="MAW 1";
  public final static String CODMAW2 ="MAW 2";
  //orario massimo da considerare per i turni di lavoro ->16:30 (orario fino a quando si produce ARTEC)
  public final static Long MINMAXTURNO=Long.valueOf(990); 
//  public final static Long MINMAXTURNO=Long.valueOf(1230); 
  
  public final static Integer CLNGUASTILINEA=Integer.valueOf(25);
  
  public final static Integer CLNCODLINDEA =Integer.valueOf(21);
  
  public XlsMawArtec(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getColOraFineImp() {
    return Integer.valueOf(24);
  }

  @Override
  public Integer getColOraIniImp() {
    return Integer.valueOf(23);
  }

  @Override
  public Integer getColOraIniNnProd() {
    return Integer.valueOf(11);
  }

  @Override
  public Integer getColOraFineNnProd() {
    return Integer.valueOf(12);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(14);
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(17);
  }

  
  public Date getInizioTurnoGg(Date data,String linea) {
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    
    return getInizioTurnoGg(data,linea, rI, rF);
  }

  
  public Date getInizioTurnoGg(Date data,String linea, Integer rI, Integer rF) {
    Date dataT=null;
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellImp=riga.getCell(CLNCODLINDEA);
      String codLinea="";
      if(cellImp.getCellType()==HSSFCell.CELL_TYPE_STRING)
        codLinea=cellImp.getStringCellValue();
      
      if(!codLinea.isEmpty() && getLineaFromXlsCod(codLinea).equals(linea)){
        HSSFCell cell=riga.getCell(getColOraIniImp()-1);
        if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          dataT=cell.getDateCellValue();
         break;
        }
      }
    }
    
    if(dataT!=null)
      dataT=DateUtils.AddTime(data, dataT);
      
    return dataT;
  }

  public Date getFineTurnoGg(Date data,String linea) {
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    
    return getFineTurnoGg(data,linea, rI, rF);
  }
  
  public Date getFineTurnoGg(Date data,String linea, Integer rI, Integer rF) {
    Date dataT=null;
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellImp=riga.getCell(CLNCODLINDEA);
      String codLinea="";
      if(cellImp.getCellType()==HSSFCell.CELL_TYPE_STRING)
        codLinea=cellImp.getStringCellValue();
      
      if(!codLinea.isEmpty() && getLineaFromXlsCod(codLinea).equals(linea)){
        HSSFCell cell=riga.getCell(getColOraFineImp()-1);
        if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          dataT=cell.getDateCellValue();
        }
      }
    }
    //controllo per orario oltre le 16:30
    if(dataT!=null){
      dataT=DateUtils.AddTime(data, dataT);
    }  
    
    return dataT;
  }
  
  

public Long getMinutiFermiImpianto(Date data,String linea){
  Integer rI=getRigaInizioGg(data);
  Integer rF=getRigaFineGg(rI);
  
  
  return getMinutiFermiImpianto(data,linea, rI, rF);
}  

public Long getMinutiFermiImpianto(Date data, String linea, Integer rI, Integer rF) {
  Double minD=Double.valueOf(0);
  
  for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellImp=riga.getCell(CLNCODLINDEA);
      String codLinea="";
      if(cellImp.getCellType()==HSSFCell.CELL_TYPE_STRING)
        codLinea=cellImp.getStringCellValue();
      
      if(!codLinea.isEmpty() && getLineaFromXlsCod(codLinea).equals(linea)){
        HSSFCell cell=riga.getCell(CLNGUASTILINEA-1);
        if(cell.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          minD+=cell.getNumericCellValue();
        }
      }
    }
  
  return minD.longValue();
}
  

//  public Date getDataMaxTurnoPom(Date data) {
//    Date dtN=null;
//    try{
//      String ora=DateUtils.FormattaOra(MINMAXTURNO);
//      String dtString=DateUtils.DateToStr(data, "dd/MM/yyyy");
////      dtString+=" 16:30:00";
//      dtString+=" "+ora;
//      dtN=DateUtils.strToDate(dtString, "dd/MM/yyyy hh:mm"); 
//    }catch(ParseException ex){
//      _logger.error("Problema di conversione della data"+ex.getMessage());
//    }
//    
//     
//    return dtN;
//  }
  
  
  public Long getMinutiProdImpianto(Date data,String linea){
    List periodi=getListPeriodiProd(data, linea);
    
    return getMinutiProdImpianto(periodi);
  }
  
  
  
  public Long getMinutiProdImpianto(List<List> periodiP){
    Long minuti=Long.valueOf(0);
    if(periodiP==null || periodiP.isEmpty())
      return minuti;
    
    for(List periodo:periodiP){
      Long fin=ClassMapper.classToClass(periodo.get(0), Long.class);
      Long ini=ClassMapper.classToClass(periodo.get(1), Long.class);
      
//      if(fin>=MINMAXTURNO)
//        fin=MINMAXTURNO;
      
      minuti+=(fin-ini);
    }
    
    return minuti;
  }
  
  
  public Long getMinutiImprodImpianto(List<List> periodiProd,List<List> periodiNnProd){
    Long minuti=Long.valueOf(0);
    if(periodiNnProd==null || periodiNnProd.isEmpty())
      return minuti;
    
    if(periodiProd==null || periodiProd.isEmpty())
      return minuti;
    
    for(List perNnP:periodiNnProd){
      Long fin=ClassMapper.classToClass(perNnP.get(0), Long.class);
      Long ini=ClassMapper.classToClass(perNnP.get(1), Long.class);
      for(List periodo:periodiProd){
        Long finP=ClassMapper.classToClass(periodo.get(0), Long.class);
        Long iniP=ClassMapper.classToClass(periodo.get(1), Long.class);
        
        if(fin<=finP && ini>=iniP){
          minuti+=(fin-ini);  
        }
      }
      
      
    }
    
    return minuti;
  }
  
  
  
  public List getListPeriodiProd(Date data,String linea){
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    
    return getListPeriodiProdLinea(linea,data, rI, rF, getColOraIniImp(), getColOraFineImp());
  }
  
  public List getListPeriodiProd(String linea,Date data,Integer rI,Integer rF){
    
    return getListPeriodiProdLinea(linea, data, rI, rF, getColOraIniImp(), getColOraFineImp());
  }
  
  public List getListPeriodiMinProd(String linea,Integer rI,Integer rF){
    
    return getListMinutiProdLinea(linea, rI, rF, getColOraIniImp(), getColOraFineImp());
  }
  
//  public List getListPeriodiNnProd(Date data,String linea){
//    Integer rI=getRigaInizioGg(data);
//    Integer rF=getRigaFineGg(rI);
//    
//    return getListPeriodiLinea(linea, rI, rF, getColOraIniNnProd(), getColOraFineNnProd());
//  }
  
  public List getListPeriodiMinNnProd(String linea,Integer rI,Integer rF){
    
    return getListPeriodiMinNnProd(rI, rF, getColOraIniNnProd(), getColOraFineNnProd());
  }
  
  /**
   * Torna una lista di periodi in cui ogni elemento è composto da dataInizio e dataFine periodo
   * @param linea
   * @param data
   * @param rI
   * @param rF
   * @param cI
   * @param cF
   * @return 
   */
  private List getListPeriodiProdLinea(String linea,Date data,Integer rI,Integer rF,Integer cI,Integer cF ){
    List periodi=new ArrayList();
   
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellImp=riga.getCell(CLNCODLINDEA);
      String codLinea="";
      if(cellImp.getCellType()==HSSFCell.CELL_TYPE_STRING)
        codLinea=cellImp.getStringCellValue();
      
      if(!codLinea.isEmpty() && getLineaFromXlsCod(codLinea).equals(linea)){
        HSSFCell cellIni=riga.getCell(cI-1);
        HSSFCell cellFin=riga.getCell(cF-1);
        if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){

          List record=new ArrayList();
          record.add(DateUtils.AddTime(data, cellIni.getDateCellValue()));
          record.add(DateUtils.AddTime(data, cellFin.getDateCellValue())); 
          periodi.add(record);
        }
      } 
    }
    
    return periodi;
  }
  
  /**
   * Torna una lista di periodi espressa in minuti
   * @param linea
   * @param rI
   * @param rF
   * @param cI
   * @param cF
   * @return 
   */
  private List getListMinutiProdLinea(String linea,Integer rI,Integer rF,Integer cI,Integer cF ){
    List periodi=new ArrayList();
   
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellImp=riga.getCell(CLNCODLINDEA);
      String codLinea="";
      if(cellImp.getCellType()==HSSFCell.CELL_TYPE_STRING)
        codLinea=cellImp.getStringCellValue();
      
      if(!codLinea.isEmpty() && getLineaFromXlsCod(codLinea).equals(linea)){
        HSSFCell cellIni=riga.getCell(cI-1);
        HSSFCell cellFin=riga.getCell(cF-1);
        if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          Integer minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
          Integer minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
           
          //caso delle 23:59--> settiamo a mezzanotte
          if(minFin==1439)
            minFin++;
          
          List record=new ArrayList();
          record.add(minFin);
          record.add(minIni);
          periodi.add(record);
        }
      } 
    }
    
    return periodi;
  }
  
  
  private List getListPeriodiMinNnProd(Integer rI,Integer rF,Integer cI,Integer cF ){
    List periodi=new ArrayList();
   
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellIni=riga.getCell(cI-1);
      HSSFCell cellFin=riga.getCell(cF-1);
      if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        Integer minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
        Integer minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;

        //caso delle 23:59--> settiamo a mezzanotte
        if(minFin==1439)
          minFin++;

        List record=new ArrayList();
        record.add(minFin);
        record.add(minIni);
        periodi.add(record);

      }
    }
    
    return periodi;
  }
  
  
  
  private String getLineaFromXlsCod(String codXls){
    if(CODMAW1.equals(codXls))
      return InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL1);
    else if(CODMAW2.equals(codXls))
      return InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.MAWARTECL2);
    
    return "";
  }
  
  
  
 private static final Logger _logger = Logger.getLogger(XlsMawArtec.class);

  
}
