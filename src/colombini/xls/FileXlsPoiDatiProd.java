/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls;

import fileXLS.FileExcelJXL;
import fileXLS.FileXlsPoiR;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import utils.ClassMapper;
import utils.DateUtils;

/**
 * Oggetto che rappresenta il file xls dei dati di Produzione.
 * Utilizza le librerie poi per accedere alle proprietà del file
 * @author lvita
 */
public abstract class FileXlsPoiDatiProd extends FileXlsPoiR{
  
  public FileXlsPoiDatiProd(String fileName){
    super(fileName);
  }
  
  
  public HSSFSheet getSheetProduzione(){
    return getFileXls().getSheet(FileExcelJXL.SHEETPRODUZIONE);
  }
  
  
  public Integer getNumCol(){
   return  Integer.valueOf(18);
  }
  public abstract Integer getNumRigheGg();
  public abstract Integer getNumColFermi();
  public abstract Integer getDeltaRigheXls();
  
  
  public abstract Integer getColOraFineImp();
  public abstract Integer getColOraIniImp();
  public abstract Integer getColOraFineNnProd();
  public abstract Integer getColOraIniNnProd();
  
  public abstract Integer getColFineFermi();
  public abstract Integer getColIniFermi();
 
  /**
   * Torna il numero della prima riga corrispondente al giorno indicato
   * @param data
   * @return Integer riga
   */
  public Integer getRigaInizioGg(Date data){
    Integer numriga=null;
    Integer diffgiorni=null;
    
    int anno=DateUtils.getAnno(data);
    
    GregorianCalendar gc=new GregorianCalendar(anno,Calendar.JANUARY,1);
    Date inizioAnno=gc.getTime();
    diffgiorni=DateUtils.daysBetween(inizioAnno, data);
    //-- Modifica introdotta per gestire il giorno 29 Febbraio su tutti i file xls
//    try {
//      Date primoMarzo=DateUtils.StrToDate("01/03/"+anno, "dd/MM/yyyy");
//      if(DateUtils.afterEquals(data, primoMarzo))
//        diffgiorni++;
//    } catch (ParseException ex) {
//      _logger.error("Impossibile caricare la data 1 Marzo :"+ex.getMessage());
//    }
    //--
    
    numriga=new Integer(diffgiorni*getNumRigheGg());
    _logger.debug("-> GG da inizio anno : "+diffgiorni);
    
    //per escludere intestazioni nel caso si selezionasse il primo giorno dell'anno
    if(diffgiorni==0)
      numriga=3;
    
    return numriga;
  }
  
  /**
   * Torna il numero dell'ultima riga corrispondente al giorno indicato
   * @param data
   * @return Integer riga
   */
  public Integer getRigaFineGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    
    return getRigaFineGg(rigaIni);
            
  }
  
  /**
   * Data una riga iniziale torna il numero dell'ultima riga appartenente alla stessa giornata
   * @param rI riga iniziale (di un determinato gg) del file xls
   * @return Integer rF riga finale (di un determinato gg) del file xls
   */
  public Integer getRigaFineGg(Integer rI){
    return (rI+getNumRigheGg()-1);
  }
  
  /**
   * Torna una data corrispondente all'inizio del turno nel giorno indicato
   * @param data
   * @param rI riga iniziale (di un determinato gg) del file xls
   * @param rF riga finale (di un determinato gg) del file xls
   * @return Date data
   */
  public Date getInizioTurno(Date data,Integer rI,Integer rF){
    Date oraIni=null;
    Date iniT=null;
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cella=riga.getCell(getColOraIniImp()-1);
      if(cella.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
         oraIni=cella.getDateCellValue();
         break;
      }
    }
    
    if(oraIni!=null){
      iniT=DateUtils.AddTime(data, oraIni);
    }
    
    return iniT;
  }  
  
  /**
   * Torna una data corrispondente all'inizio del turno nel giorno indicato
   * @param data
   * @return Date data
   */
  public Date getInizioTurnoGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    return getInizioTurno(data, rigaIni, rigaFine);
  }
  
  /**
   * Torna come inizio turno le 6:00 della mattina o un orario successivo
   * @param data
   * @return 
   */
  public Date getInizioTurnoStdGg(Date data){
    Date iniT= getInizioTurnoGg(data);
    if(iniT==null)
      return null;
    
    try {  
      Date turno6=DateUtils.getInizioTurnoM(data);
      if(iniT.before(turno6))
        iniT=turno6;
      
    } catch (ParseException ex) {
     _logger.error("Impossibile convertire la data fornita :"+ex.getMessage());
    }
    
    return iniT;
  }
  
  /**
   * Torna una data corrispondente alla fine del turno nel giorno indicato
   * @param data
   * @param rI riga iniziale del file xls
   * @param rF riga finale del file xls
   * @return Date data 
   */
  public Date getFineTurno(Date data,Integer rI,Integer rF){
    Date oraFin=null;
    Date finT=null;
    
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cella=riga.getCell(getColOraFineImp()-1);
      if(cella.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
         oraFin=cella.getDateCellValue();
      }
    }
    
    if(oraFin!=null){
      finT=DateUtils.AddTime(data, oraFin);
    }
    
    if(finT!=null){
      int ora=DateUtils.getHours(finT);
      int minuti=DateUtils.getMinutes(finT);
      int secondi=DateUtils.getSeconds(finT);
      if(ora==0 && minuti==0 && secondi>0){
        _logger.warn("ATTENZIONE ORA FINE >  DI MEZZANOTTE  (= 00:00:01)   --> SETTIAMO ALLE 23:59:59 !!");
        try {
          finT=DateUtils.getFineGg(data);
        } catch (ParseException ex) {
         _logger.error("Impossibile riconverire la data fine per il gg"+data+" - "+ex.getMessage());
        }
      }
    }
    return finT;
  }  
  
  /**
   * Torna una data corrispondente alla fine del turno nel giorno indicato
   * @param data
   * @return Date data
   */
  public Date getFineTurnoGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    return getFineTurno(data, rigaIni, rigaFine);
  }
  
  
  /**
   * Torna come fine turno le 20:30 o un orario precedente
   * @param data
   * @return 
   */
  public Date getFineTurnoStdGg(Date data){
    Date finT= getFineTurnoGg(data);
    if(finT==null)
      return null;
    
    try {  
      Date turnoP=DateUtils.getFineTurnoP(data);
      if(finT.after(turnoP))
        finT=turnoP;
      
    } catch (ParseException ex) {
     _logger.error("Impossibile convertire la data fornita :"+ex.getMessage());
    }
    
    return finT;
  }
  
  /**
   * Torna la lista dei periodi di produzione specificati nel foglio xls
   * @param data
   * @param rI  riga iniziale (di un determinato gg) del file xls
   * @param rF  riga finale (di un determinato gg) del file xls
   * @return 
   */
  
  public List getPeriodiProdImpianto(Date data,Integer rI,Integer rF){
     List<List> periodiAp=new ArrayList();
     
     for(int i=rI;i<=rF;i++){
       HSSFRow riga=getSheetProduzione().getRow(i);
       HSSFCell cellIni=riga.getCell(getColOraIniImp()-1);
       HSSFCell cellFin=riga.getCell(getColOraFineImp()-1);
      
       if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
         List rec=new ArrayList();
         rec.add(DateUtils.AddTime(data, cellIni.getDateCellValue()));
         rec.add(DateUtils.AddTime(data, cellFin.getDateCellValue()));
         
         if(!periodiAp.contains(rec))
          periodiAp.add(rec);
       } 
     }
     
     return periodiAp;
   } 
  
   /**
    * Torna la durata espressa in secondi dei periodi forniti nella lista
    * @param periodiProd lista dei periodi (turni) indicati nel foglio exel
    * @return 
    */
   public Long getTempoFromPeriodi(List<List> periodi){
     Long tempo=Long.valueOf(0);
     if(periodi==null)
       return tempo;
     
     for(List per:periodi){
      Date dataI=ClassMapper.classToClass(per.get(0),Date.class);
      Date dataF=ClassMapper.classToClass(per.get(1),Date.class);
      tempo+=DateUtils.numSecondiDiff(dataI, dataF);
      
     }
     
     return tempo;
   }
  
  /**
   * Torna i minuti relativi al tempo di Produzione della linea in una determinata giornata
   * @param Date data
   * @return Long minuti
   */
  public Long getMinutiProdImpianto(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    return getMinutiProdImpianto(rigaIni, rigaFine);
  } 
   
  /**
   * Torna i minuti relativi al tempo di Produzione della linea in una determinata giornata
   * @param rI riga iniziale (di un determinato gg) del file xls
   * @param rF riga finale (di un determinato gg) del file xls
   * @return Long minuti
   */
  public Long getMinutiProdImpianto(Integer rI,Integer rF){
    Long minuti=Long.valueOf(0);
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellIni=riga.getCell(getColOraIniImp()-1);
      HSSFCell cellFin=riga.getCell(getColOraFineImp()-1);
      if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC && cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        Integer minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
        Integer minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
        
        //caso delle 23:59--> settiamo a mezzanotte
        if(minFin==FileExcelJXL.MIN2359)
          minFin++;
        
        //caso delle 00:00:01--> settiamo a mezzanotte
        if(minFin==0){
          minFin=FileExcelJXL.MINUTIGG;
        }
        
        if((minFin-minIni)<0 )
          _logger.error("Attenzione errore nel calcolo dei minuti per le ore produttive -> Somma negativa");
        
        minuti+=(minFin-minIni);
      }
    }
    return minuti;
  }
  
   
   
  /**
   * Torna la lista dei periodi di buco nella giornata lavorativa 
   * @param data
   * @param rI
   * @param rF
   * @return 
   */
  public List getListPeriodiChiusImpianto(Date data, Integer rI,Integer rF){
    
    List<List> periodiAp=getPeriodiProdImpianto(data,rI, rF);
    List<List> periodiCh=new ArrayList();
    
    Date dataIP=null;
    Date dataFP=null;
    
    for(List per:periodiAp){
      Date dataItmp=(Date)per.get(0);
      Date dataFtmp=(Date)per.get(1);
      if(dataFP!=null && DateUtils.numMinutiDiff(dataFP, dataItmp)>0 ) {
        List recC=new ArrayList();
        recC.add(dataFP);
        recC.add(dataItmp);
        periodiCh.add(recC);
      }
      
      dataIP=dataItmp;  
      dataFP=dataFtmp; 
    }
    
    
    return periodiCh;
  }
  
   
   public Long getTempoChiusImp(Date data,Integer rI,Integer rF){
     Long minuti=Long.valueOf(0);
     List<List> periodi=getListPeriodiChiusImpianto(data,rI, rF);
     
     for(List per:periodi){
       Integer minIni=DateUtils.getMinutes((Date) per.get(0))+DateUtils.getHours((Date) per.get(0))*60;
       Integer minFin=DateUtils.getMinutes((Date) per.get(1))+DateUtils.getHours((Date) per.get(1))*60;
        
       if(minFin==FileExcelJXL.MIN2359)
          minFin++;
        
        //caso delle 00:00:01--> settiamo a mezzanotte
        if(minFin==0){
          minFin=FileExcelJXL.MINUTIGG;
        }
        
        if((minFin-minIni)<0 )
          _logger.error("Attenzione errore nel calcolo dei minuti per le ore produttive -> Somma negativa");
        
        minuti+=(minFin-minIni);
     }
     
     return minuti;
   }
  
  
  
  /**
   * Torna i minuti relativi al tempo di Produzione della linea in una determinata giornata
   * @param Integer rI riga iniziale del file xls
   * @param Integer rF riga finale del file xls
   * @return Long minuti
   */
  public Long getMinutiImprodImpianto(Integer rI,Integer rF){
    Long minuti=Long.valueOf(0);
    for(int i=rI;i<=rF;i++){
      Integer minFin=Integer.valueOf(0);
      Integer minIni=Integer.valueOf(0);
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellIni=riga.getCell(getColOraIniNnProd()-1);
      HSSFCell cellFin=riga.getCell(getColOraFineNnProd()-1);
      
      if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
        minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
      if(cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
        minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;
        
      if(minIni>0 || minFin>0){ 
        if(minFin==FileExcelJXL.MIN2359)
          minFin++;

        if(minFin==0 && cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          minFin=FileExcelJXL.MINUTIGG;
        }

        if((minFin-minIni)<0 ){
          _logger.error("Attenzione errore nel calcolo dei minuti per le ore improduttive -> Somma del periodo negativa");
        }else{
          minuti+=(minFin-minIni);
        }  
      }
    }
    return minuti;
  }
  
  /**
   * Cerca i minuti di tempo improduttivo relativi ad una data causale
   * @param causale
   * @param rI
   * @param rF
   * @return 
   */
  public Long getMinutiImprodImpianto(String causale,Integer rI,Integer rF){
    Long minuti=Long.valueOf(0);
    for(int i=rI;i<=rF;i++){
      Integer minFin=Integer.valueOf(0);
      Integer minIni=Integer.valueOf(0);
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellCaus=riga.getCell(getColOraIniNnProd()-2);
      HSSFCell cellIni=riga.getCell(getColOraIniNnProd()-1);
      HSSFCell cellFin=riga.getCell(getColOraFineNnProd()-1);
      
      if(cellCaus!=null && cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING && causale.equals(cellCaus.getStringCellValue()) ){
        if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
          minFin=DateUtils.getMinutes(cellFin.getDateCellValue())+DateUtils.getHours(cellFin.getDateCellValue())*60;
        if(cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC)
          minIni=DateUtils.getMinutes(cellIni.getDateCellValue())+DateUtils.getHours(cellIni.getDateCellValue())*60;

        if(minIni>0 || minFin>0){ 
          if(minFin==FileExcelJXL.MIN2359)
            minFin++;

          if(minFin==0 && cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
            minFin=FileExcelJXL.MINUTIGG;
          }

          if((minFin-minIni)<0 ){
            _logger.error("Attenzione errore nel calcolo dei minuti per le ore improduttive -> Somma del periodo negativa");
          }else{
            minuti+=(minFin-minIni);
          }  
        }
      }
    }
    
    return minuti;
  }
  
  public List getPeriodiImprodImpianto(Date data,Integer rI,Integer rF){
    List periodi=new ArrayList();
    for(int i=rI;i<=rF;i++){
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellCaus=riga.getCell(getColOraIniNnProd()-2);
      HSSFCell cellIni=riga.getCell(getColOraIniNnProd()-1);
      HSSFCell cellFin=riga.getCell(getColOraFineNnProd()-1);
      
      if(cellCaus!=null && cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING ){
        Date dataF=null;
        Date dataI=null;
        
        if(cellFin.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          dataF=DateUtils.AddTime(data, cellFin.getDateCellValue());
        }
        
        if(cellIni.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
          dataI=DateUtils.AddTime(data, cellIni.getDateCellValue());
        }
          
        if(dataI!=null && dataF!=null){
          List record=new ArrayList();
          record.add(dataI);  
          record.add(dataF);
          periodi.add(record);
        }
      }
    }
    return periodi;
  }
  
  
  public  Long getDurataPeriodiInRange(List<List> periodi,Date dataInizioP, Date dataFineP) {
    Long durataT=Long.valueOf(0);
    int count=0;
    for(List periodoTmp : periodi ){
      Date dataInizioT=ClassMapper.classToClass(periodoTmp.get(0),Date.class);
      Date dataFineT=ClassMapper.classToClass(periodoTmp.get(1),Date.class);
       
      if(DateUtils.afterEquals(dataFineP, dataInizioT) && DateUtils.beforeEquals(dataInizioP, dataFineT) ) {
        count++;
        if( DateUtils.beforeEquals(dataInizioP, dataInizioT) && DateUtils.afterEquals(dataFineP, dataFineT)  ){ 
          // il fermo è all'interno del periodo di produzione
          durataT+=DateUtils.numSecondiDiff(dataInizioT, dataFineT);
        }else if((dataInizioT.before(dataInizioP) && dataFineT.after(dataFineP))){
          // il fermo è più grande del periodo di produzione
          durataT+=DateUtils.numSecondiDiff(dataInizioP, dataFineP);
        
        }else if(dataFineT.after(dataInizioP) && DateUtils.beforeEquals(dataFineT, dataFineP)){
          durataT+=DateUtils.numSecondiDiff(dataInizioP, dataFineT);
          
        }else if(DateUtils.afterEquals(dataInizioT, dataInizioP) && dataInizioT.before(dataFineP)){
          durataT+=DateUtils.numSecondiDiff(dataInizioT, dataFineP);
        }
   
      }
    
    }
    
    _logger.debug(" Totale sec  nel periodo"+durataT+ " n periodi"+count);
    return durataT;
    
  }
  
 
  
  public Long getMinutiImprodImpianto(Integer rigaIni,Integer rigaFine ,Integer col,String causale) {
    Double valore=Double.valueOf(0);
    
    for(int r=rigaIni;r<=rigaFine;r++){
        HSSFRow riga=getSheetProduzione().getRow(r);
        HSSFCell cellCausale=riga.getCell(col-1);
        HSSFCell cellVal=riga.getCell(col);

        if(cellCausale!=null && cellCausale.getCellType()==HSSFCell.CELL_TYPE_STRING && cellCausale.getStringCellValue().contains(causale)){
          if(cellVal!=null && cellVal.getCellType()==HSSFCell.CELL_TYPE_NUMERIC) 
          valore+=cellVal.getNumericCellValue();
        }
      
    }
    
    
    return valore.longValue();
    
  }
  
  
  /**
   * Torna i minuti relativi al tempo improduttivo della linea in una determinata giornata
   * @param Date data
   * @return Long minuti
   */
  public Long getMinutiImprodImpianto(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    return getMinutiImprodImpianto(rigaIni, rigaFine);
  } 
  
  
  /**
   * Date una riga iniziale ,una riga finale e un numero di colonna 
   * Torna il totale di tutte le celle di formato numerico
   * @param Integer rI riga iniziale
   * @param Integer rF riga finale
   * @param Integer cln colonna
   * @return Double valore
   */
  public Double getValNumColonna(Integer rI,Integer rF,Integer cln){
    return getValNumColonne(rI, rF, cln, cln);
  }
  
  /**
   * Data riga iniziale e finale ,una colonna iniziale  e finale 
   * torna l'insieme dei valori numerici nel range
   * @param Integer rI
   * @param Integer rF
   * @param Integer clnI
   * @param Integer clnF
   * @return Double val
   */
  public Double getValNumColonne(Integer rI,Integer rF,Integer clnI,Integer clnF){
    Double valore=Double.valueOf(0);
    
    for(int c=clnI;c<=clnF;c++){
      for(int r=rI;r<=rF;r++){
        HSSFRow riga=getSheetProduzione().getRow(r);
        HSSFCell cella=riga.getCell(c-1);

        if(cella.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
           valore+=cella.getNumericCellValue();
        }
      }
    }
    return valore;
  }
  
  /**
   * Restituisce una mappa contenente i fermi della giornata indicata
   * @param Date data
   * @return Map mappa fermi
   */
  public Map getMappaFermiGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    rigaIni+=this.getDeltaRigheXls();
    rigaFine-=this.getDeltaRigheXls();
    
    return getMappaFermiGG(rigaIni, rigaFine);
  }
  
  
  
  
  /**
   * Restituisce una mappa contenente i fermi di una determinata giornata inclusa tra rI e rF
   * @param Integer rI riga iniziale di una determinata giornata
   * @param Integer rF riga finale di una determinata giornata
   * @return Map mappa fermi
   */
  public Map getMappaFermiGG(Integer rI,Integer rF){
    HashMap fermi=new HashMap();
    Integer nFermi=Integer.valueOf(0);
    List colFermi=new ArrayList();
    
    Integer numColFermi=getNumColFermi();
    int j=1;
    while(j<=numColFermi){
      Integer posFermi=null;
      if(j==1){
        posFermi=getColIniFermi(); 
      }else{
        posFermi=getColIniFermi()+((numColFermi-1)*2);
      }
      colFermi.add(posFermi);
      j++;
    }
    //ciclo sulle posizione e poi sulle righe
    for(j=0;j<colFermi.size();j++){
      Integer coltmp=(Integer) colFermi.get(j);
      Map fermiTmp=getMappaValoriGG(rI, rF, coltmp);
      if(fermiTmp!=null && !fermiTmp.isEmpty())
        fermi.putAll(fermiTmp);
    }
    
       
    return fermi;
  }
  
  /**
   * Torna una mappa contenente come chiave una causale e come valore la durata in minuti.
   * Tale mappa viene generata andando a leggere il file xls da una certa riga iniziale ad una riga finale 
   * e in una colonna specificata
   * @param Integer rI riga iniziale
   * @param Integer rF riga finale 
   * @param Integer coltmp colonna di riferimento
   * @return Map mappa 
   */
  public Map getMappaValoriGG(Integer rI,Integer rF,Integer coltmp){
    HashMap mappaV=new HashMap();
    
    for(int i=rI;i<=rF;i++){
      Double dvalue=null;
      String causale=null;
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellCaus=riga.getCell(coltmp-1);
      HSSFCell cellVal=riga.getCell(coltmp);

      if(cellCaus!=null && cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING && cellVal!=null && cellVal.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        dvalue =cellVal.getNumericCellValue();
        causale =cellCaus.getStringCellValue();
      }
      if(causale!=null && !causale.isEmpty() && dvalue!=null){
        causale=causale.trim();
        if(!mappaV.containsKey(causale)){
          mappaV.put(causale, dvalue);
        }else {
          Double appo=(Double) mappaV.get(causale);
          appo+=dvalue;
          mappaV.put(causale,appo);
        }
      }   
    }
       
    return mappaV;
  }
  
 
  /**
   * Torna una lista di liste in cui il primo elemento è la causale il secondo il valore dei fermi 
   * di una determinata giornata.
   * Filtra all'interno delle righe della giornata in base al delta impostato nelle proprietà del file xls
   * @param Date data
   * @return List fermi
   */
  public List getListaFermiGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
    rigaIni+=this.getDeltaRigheXls();
    rigaFine-=this.getDeltaRigheXls();
    
    return getListaFermiGG(rigaIni, rigaFine);
  }
  
  
  
  /**
   * Torna una lista con tutti i fermi della giornata.
   * @param data
   * @return List fermi
   */
  public List getListaFermiTotGg(Date data){
    Integer rigaIni=getRigaInizioGg(data);
    Integer rigaFine=getRigaFineGg(rigaIni);
    
   
    
    return getListaFermiGG(rigaIni, rigaFine);
  }
  
  
  /**
   * Torna una lista di liste in cui il primo elemento è la causale il secondo il valore dei fermi tra rI e rF
   * @param Integer rI riga iniziale di una determinata giornata
   * @param Integer rF riga finale di una determinata giornata
   * @return List lista dei fermi
   */
  public List getListaFermiGG(Integer rI,Integer rF){
    List fermi=new ArrayList();
    List colFermi=new ArrayList();
    
    Integer numColFermi=getNumColFermi();
    int j=0;
    while(j<numColFermi){
      Integer posFermi=getColIniFermi()+(j*2);
      colFermi.add(posFermi);
      j++;
    }
    //ciclo sulle posizione e poi sulle righe
    for(j=0;j<colFermi.size();j++){
      Integer coltmp=(Integer) colFermi.get(j);
      List lstTmp=getListaFermiGG(rI, rF, coltmp);
      if(lstTmp!=null && !lstTmp.isEmpty())
        fermi.addAll(lstTmp);
    }
    
    return fermi;
  }
  
  
//  public List getListaFermiGG(Integer rI,Integer rF){
//    List fermi=new ArrayList();
//    List colFermi=new ArrayList();
//    
//    Integer numColFermi=getNumColFermi();
//    int j=1;
//    while(j<=numColFermi){
//      Integer posFermi=null;
//      if(j==1){
//        posFermi=getColIniFermi(); 
//      }else{
//        posFermi=getColIniFermi()+((numColFermi-1)*2);
//      }
//      colFermi.add(posFermi);
//      j++;
//    }
//    //ciclo sulle posizione e poi sulle righe
//    for(j=0;j<colFermi.size();j++){
//      Integer coltmp=(Integer) colFermi.get(j);
//      List lstTmp=getListaFermiGG(rI, rF, coltmp);
//      if(lstTmp!=null && !lstTmp.isEmpty())
//        fermi.addAll(lstTmp);
//    }
//    
//    return fermi;
//  }
  
  /**
   * Torna una lista di liste in cui il primo elemento è la causale il secondo il valore dei fermi 
   * tra  la riga rI e  la riga rF per la colonna cln
   * @param Integer rI riga iniziale di una determinata giornata
   * @param Integer rF riga finale di una determinata giornata
   * @param Integer cln colonna di riferimento dei fermi
   * @return List lista dei fermi
   */
  public List getListaFermiGG(Integer rI,Integer rF,Integer cln){
    List fermi=new ArrayList();
    
    for(int i=rI;i<=rF;i++){
      Double dvalue=null;
      String causale=null;
      HSSFRow riga=getSheetProduzione().getRow(i);
      HSSFCell cellCaus=riga.getCell(cln-1);
      HSSFCell cellVal=riga.getCell(cln);

      if(cellCaus!=null && cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING && cellVal!=null && cellVal.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
        dvalue =cellVal.getNumericCellValue();
        causale =cellCaus.getStringCellValue();
      }
      if(causale!=null && !causale.isEmpty() && dvalue!=null){
        List fermo=new ArrayList();
        fermo.add(causale.trim());
        fermo.add(dvalue);
        fermi.add(fermo);
      }   
    }

    return fermi;
  }
  
  
  /**
   * Torna la durata totale dei fermi in una determinata giornata
   * @param data
   * @return Long totale minuti dei fermi
   */
  public Long getDurataFermiGg(Date data){
    Map fermi=getMappaFermiGg(data);
  
    return getDurataFermiGg(fermi);
  }
  
  
  /**
   * Data la mappa dei fermi di una giornata ritorna il numero di minuti totale
   * @param Map mappaFermi
   * @return Long totale minuti
   */
  public Long getDurataFermiGg(Map mappaFermi){
    Long duratat=Long.valueOf(0);
    
    if(mappaFermi==null || mappaFermi.isEmpty())
      return duratat;  
    
    Set setKeys=mappaFermi.keySet();
    Iterator iter=setKeys.iterator();
    while (iter.hasNext()){
      String key=(String) iter.next();
      Long valore =ClassMapper.classToClass(mappaFermi.get(key),Long.class);
      duratat+=valore;
      
    }
    
    return duratat;
  }
  
  
   /**
   * Torna una lista di liste in cui il primo elemento è la causale il secondo il valore dei fermi tra rI e rF
   * @param Integer rI riga iniziale di una determinata giornata
   * @param Integer rF riga finale di una determinata giornata
   * @return List lista dei fermi
   */
//  public List getListaFermiGG(Integer rI,Integer rF){
//    List fermi=new ArrayList();
//    List colFermi=new ArrayList();
//    
//    Integer numColFermi=getNumColFermi();
//    int j=1;
//    while(j<=numColFermi){
//      Integer posFermi=null;
//      if(j==1){
//        posFermi=getColIniFermi(); 
//      }else{
//        posFermi=getColIniFermi()+((numColFermi-1)*2);
//      }
//      colFermi.add(posFermi);
//      j++;
//    }
//    //ciclo sulle posizione e poi sulle righe
//    for(j=0;j<colFermi.size();j++){
//      Integer coltmp=(Integer) colFermi.get(j);
//      for(int i=rI;i<=rF;i++){
//        Double dvalue=null;
//        String causale=null;
//        HSSFRow riga=getSheetProduzione().getRow(i);
//        HSSFCell cellCaus=riga.getCell(coltmp-1);
//        HSSFCell cellVal=riga.getCell(coltmp);
//        
//        if(cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING && cellVal.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
//          dvalue =cellVal.getNumericCellValue();
//          causale =cellCaus.getStringCellValue();
//        }
//        if(causale!=null && !causale.isEmpty() && dvalue!=null){
//          List fermo=new ArrayList();
//          fermo.add(causale.trim());
//          fermo.add(dvalue);
//          fermi.add(fermo);
//        }   
//      }
//    }
//    
//    return fermi;
//  }
  
//  public Map getMappaFermiGG(Integer rI,Integer rF){
//    HashMap fermi=new HashMap();
//    Integer nFermi=Integer.valueOf(0);
//    List colFermi=new ArrayList();
//    
//    Integer numColFermi=getNumColFermi();
//    int j=1;
//    while(j<=numColFermi){
//      Integer posFermi=null;
//      if(j==1){
//        posFermi=getColIniFermi(); 
//      }else{
//        posFermi=getColIniFermi()+((numColFermi-1)*2);
//      }
//      colFermi.add(posFermi);
//      j++;
//    }
//    //ciclo sulle posizione e poi sulle righe
//    for(j=0;j<colFermi.size();j++){
//      Integer coltmp=(Integer) colFermi.get(j);
//      for(int i=rI;i<=rF;i++){
//        Double dvalue=null;
//        String causale=null;
//        HSSFRow riga=getSheetProduzione().getRow(i);
//        HSSFCell cellCaus=riga.getCell(coltmp-1);
//        HSSFCell cellVal=riga.getCell(coltmp);
//        
//        if(cellCaus.getCellType()==HSSFCell.CELL_TYPE_STRING && cellVal.getCellType()==HSSFCell.CELL_TYPE_NUMERIC){
//          dvalue =cellVal.getNumericCellValue();
//          causale =cellCaus.getStringCellValue();
//        }
//        if(causale!=null && !causale.isEmpty() && dvalue!=null){
//          causale=causale.trim();
//          if(!fermi.containsKey(causale)){
//            fermi.put(causale, dvalue);
//          }else {
//            Double appo=(Double) fermi.get(causale);
//            appo+=dvalue;
//            fermi.put(causale,appo);
//          }
//          nFermi++;
//        }   
//      }
//    }
//    fermi.put(FileExcelJXL.NUMFERMIGG, nFermi);
//       
//    return fermi;
//  }
  
  private static final Logger _logger = Logger.getLogger(FileXlsPoiDatiProd.class); 
}


