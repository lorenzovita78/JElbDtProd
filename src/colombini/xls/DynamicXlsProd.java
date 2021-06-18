/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls;

import colombini.costant.CostantsColomb;
import colombini.query.indicatoriOee.linee.QueryForBiesseG1P2DatiProd;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
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
public class DynamicXlsProd extends FileXlsDatiProdStd {

  private StrutturaFileXlsDatiProd strutturaXls=null;
  
  public DynamicXlsProd(String fileName) {
    super(fileName);
  }

  
  public DynamicXlsProd(String fileName,StrutturaFileXlsDatiProd strutXls){
    super(fileName);
    this.strutturaXls=strutXls; 
  }

  @Override
  public Integer getNumRigheGg() {
    return strutturaXls.getRigheGg();
  }

  @Override
  public Integer getColOraIniImp() {
    return strutturaXls.getColOraAperturaImp();
  }

  @Override
  public Integer getColOraFineImp() {
    return strutturaXls.getColOraChiusuraImp();
  }

  @Override
  public Integer getColOraIniNnProd() {
    return strutturaXls.getColOreImprodIni();
  }

  @Override
  public Integer getColOraFineNnProd() {
    return strutturaXls.getColOreImprodFn();
  }

  @Override
  public Integer getColIniFermi() {
    return strutturaXls.getColIniFermi();
  }

  @Override
  public Integer getColFineFermi() {
    return strutturaXls.getColFinFermi();
  }

  @Override
  public Integer getNumColFermi() {
    return ((strutturaXls.getColFinFermi()-strutturaXls.getColIniFermi())+1)/2;
  }

  
  public Double getTotMinutiDipendenti(Date data){
    Double minuti =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    minuti=getMinutiFromOrario(rgIni, rgFin, strutturaXls.getColOraIniTurnoDip(), strutturaXls.getColOraFinTurnoDip());
    
    
    
    return minuti;
  }
  
  
  public Double getTotMinutiStraordinario(Date data){
    Double minuti =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    minuti=getMinutiFromOrario(rgIni, rgFin, strutturaXls.getColOreStraordinario(), strutturaXls.getColOreStraordinario());
    
    return minuti;
  }
  
  public Double getTotMinutiProduzione(Date data){
    Double minuti =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    if(strutturaXls.getColOraChiusuraImp()==strutturaXls.getColOraAperturaImp())
      minuti=getMinutiFromOrario(rgIni, rgFin, strutturaXls.getColOraAperturaImp(), strutturaXls.getColOraChiusuraImp());
    else
      minuti=new Double(getMinutiProdImpianto(rgIni, rgFin));
    
    return minuti;
  }
  
  public Double getTotMinutiNnProduzione(Date data){
    Double minuti =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    if(StrutturaFileXlsDatiProd.ORE.equals(strutturaXls.getTipoOreImprodImp())){
      minuti=getMinutiFromOrario(rgIni, rgFin, strutturaXls.getColOreImprodIni(), strutturaXls.getColOreImprodFn());
    }else{
      minuti=getValNumColonne(rgIni, rgFin, strutturaXls.getColOreImprodIni(), strutturaXls.getColOreImprodFn());
    }
    
    return minuti;
  }
  
  public Long getTotPzProd(Date data){
    Long pz =Long.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    if(strutturaXls.getColPzProd()>0)
      pz+=getValNumColonna(rgIni, rgFin, strutturaXls.getColPzProd()).longValue();
    if(strutturaXls.getColPzProd2()>0)
      pz+=getValNumColonna(rgIni, rgFin, strutturaXls.getColPzProd2()).longValue();
    
    return pz;
  }

  @Override
  public List getListaFermiGg(Date data) {
    return super.getListaFermiGg(data);
  }
  
  public Double getTotMinutiSetup(Date data){
    Double minuti =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    if(strutturaXls.getColIniSetup()>0 && strutturaXls.getColFinSetup()>0)
      minuti=getValNumColonne(rgIni, rgFin, strutturaXls.getColIniSetup(), strutturaXls.getColFinSetup());
    
    return minuti;
  }
          
  public Double getTotMinutiMicrofermi(Date data){
    Double val =Double.valueOf(0);
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    
    if(strutturaXls.getColIniMicroF()>0 && strutturaXls.getColFinMicroF()>0)
      val=getValNumColonne(rgIni, rgFin, strutturaXls.getColIniMicroF(), strutturaXls.getColFinMicroF());
    
    return val*strutturaXls.getFattMoltMicroF();
  }        
          
          
          
  public Double getMinutiFromOrario(Integer rgIni,Integer rgFine,Integer colIni,Integer colFine){
    Double minuti =Double.valueOf(0);
    for(int i=rgIni;i<=rgFine;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellIni=riga.getCell(colIni-1);
      HSSFCell cellFin=riga.getCell(colFine-1);
      if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        Integer minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
        Integer minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
        
        //caso delle 23:59--> settiamo a mezzanotte
        if(minFin==CostantsColomb.MIN2359)
          minFin++;
        
        //caso delle 00:00:01--> settiamo a mezzanotte
        if(minFin==0){
          minFin=CostantsColomb.MINUTIGG;
        }
        
        if((minFin-minIni)<0 )
          _logger.error("Attenzione errore nel calcolo dei minuti per le ore produttive -> Somma negativa");
        
        
        if(colIni==colFine)
          minuti+=minFin;
        else
          minuti+=(minFin-minIni);
      }
    }
    
    
    
    return minuti;
  }
  
  
  public List getListInfoGg(Date data,Connection con){
    Integer rgIni=super.getRigaInizioGg(data);
    Integer rgFin=super.getRigaFineGg(rgIni);
    List info=new ArrayList();
    
    int delta=-1;
    for(int i=rgIni;i<=rgFin;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      
      HSSFCell cop2=riga.getCell(strutturaXls.getColOperatore()+delta);
      String operatore=null;
      if(cop2.getCellType()==HSSFCell.CELL_TYPE_STRING){
        operatore=cop2.getStringCellValue();
      }
      
      HSSFCell cori3=riga.getCell(strutturaXls.getColOraIniTurnoDip()+delta);
      Date dataIni=null;
      Integer minIni=Integer.valueOf(0);
      if(cori3.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        dataIni=cori3.getDateCellValue();
        minIni=DateUtils.getMinutes(cori3.getDateCellValue())+DateUtils.getHours(cori3.getDateCellValue())*60;
      }
      
      HSSFCell corf4=riga.getCell(strutturaXls.getColOraFinTurnoDip()+delta);
      Date dataFin=null;
      Integer minFin=Integer.valueOf(0);
      if(corf4.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        dataFin=corf4.getDateCellValue();
        minFin=DateUtils.getMinutes(corf4.getDateCellValue())+DateUtils.getHours(corf4.getDateCellValue())*60;
      }
      
      
      HSSFCell corpp=riga.getCell(strutturaXls.getColPzProd()+delta);
      Double nPzXls=null;
      Double nPzLog=null;
      if(corpp.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
           nPzXls=corpp.getNumericCellValue();
        }
      
      if(operatore!=null || dataIni!=null || dataFin!=null || nPzXls!=null){
        List record=new ArrayList();
        record.add(DateUtils.dateToStr(data, "dd/MM/yyyy"));
        record.add(operatore !=null ? operatore : "");
        record.add(dataIni !=null ? DateUtils.dateToStr(dataIni, "HH:mm") : "");
        record.add(dataFin !=null ? DateUtils.dateToStr(dataFin, "HH:mm") : "");
        
        
        if(minFin>0 ){
          record.add(minFin-minIni);
          
          Date oraIni=DateUtils.AddTime(data, dataIni);
          Date oraFin=DateUtils.AddTime(data, dataFin);
          QueryForBiesseG1P2DatiProd q=new QueryForBiesseG1P2DatiProd();
          q.setFilter(QueryForBiesseG1P2DatiProd.DATAINIZIO, DateUtils.dateToStr(oraIni,"yyyyMMdd HH:mm:ss"));
          q.setFilter(QueryForBiesseG1P2DatiProd.DATAFINE, DateUtils.dateToStr(oraFin, "yyyyMMdd HH:mm:ss"));
          q.setFilter(QueryForBiesseG1P2DatiProd.TOTPZ, "Y");
          try{
            Object[] obj=ResultSetHelper.SingleRowSelect(con, q.toSQLString());
            if(obj!=null && obj.length>0)
              nPzLog=ClassMapper.classToClass(obj[0], Double.class);
            
          }catch(QueryException s){
            _logger.error("Errore in fase di lettura dati su db per periodo "+oraIni+ " - "+oraFin);
           
          }catch(SQLException s){
            _logger.error("Errore in fase di lettura dati su db per periodo "+oraIni+ " - "+oraFin);
          }  
        }else{
          record.add(0);
        }
        record.add(nPzXls !=null ? nPzXls : 0);
        record.add(nPzLog !=null ? nPzLog : 0);
        
        info.add(record);
      }
    }
    
    return info;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(DynamicXlsProd.class); 
  
}
