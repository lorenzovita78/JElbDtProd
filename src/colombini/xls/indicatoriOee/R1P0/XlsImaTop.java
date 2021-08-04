/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P0;


import colombini.costant.NomiLineeColomb;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.indicatoriOee.R1.XlsImpiantiImaR1;
import fileXLS.FileExcelJXL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class XlsImaTop extends XlsImpiantiImaR1 {
  
 
  
  private final static Integer COL_FERMI_COMBIMA=new Integer(16);
  private final static Integer COL_FERMI_SCHELLING=new Integer(14);
  private final static Integer COL_FERMI_PRIESS=new Integer(18);
  
  public XlsImaTop(String fileName) {
    super(fileName);
  }

 
  
  /**
   * Torna la lista dei minuti di tempo improduttivo
   * @param data
   * @param causali
   * @return Long minuti di tempo improduttivo 
   */
  public Long getMinutiImprodImpianto(Date data,Map causali) {
    Long minuti=Long.valueOf(0);
    String caus="";
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellCaus=riga.getCell(getColOraIniNnProd()-2);
      HSSFCell cellIni=riga.getCell(getColOraIniNnProd()-1);
      HSSFCell cellFin=riga.getCell(getColOraFineNnProd()-1);
      if(cellCaus!= null && cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING){
        caus=cellCaus.getStringCellValue();
      }
      if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC
              && causali.containsKey(caus)){
        Integer minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
        Integer minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
        
        if(minFin==FileExcelJXL.MIN2359)
          minFin++;
        
        if(minFin==0){
          minFin=FileExcelJXL.MINUTIGG;
        }
        
        if((minFin-minIni)<0 )
          _logger.error("Attenzione errore nel calcolo dei minuti per le ore improduttive -> Somma negativa");
          
        minuti+=(minFin-minIni);
      }
    }
    return minuti;
    
  }
  
  
  
  @Override
  public List getListaFermiLinea(Integer rI,Integer rF,String linea){
    List fermi=new ArrayList();
    
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMAR1P0).equals(linea) ){
      fermi=getListaFermiGG(rI, rF, COL_FERMI_COMBIMA);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P0).equals(linea) || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P0).equals(linea)){
      fermi=getListaFermiGG(rI, rF, COL_FERMI_SCHELLING);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORPRIESS1R1P0).equals(linea) || InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.FORPRIESS2R1P0).equals(linea)){
      fermi=getListaFermiGG(rI, rF, COL_FERMI_PRIESS);
    }
    
    return fermi;
  } 
  
  
  private static final Logger _logger = Logger.getLogger(XlsImaTop.class); 
}
