/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.cartelliniKaizen;

import fileXLS.FileXlsPoiRW;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import objBean.TicketBean;
import objBean.TicketSettBean;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class XlsManutenzioniStd extends FileXlsPoiRW{

  public final static String SHEETCARTELLINATURE="CartelliniLinea";
  public final static String SHEETINDICATORI="IndicatoriLinea";
  
  //sheet cartellinature
  public final static Integer CLN_SHC_DTINIZIO=1;
  public final static Integer CLN_SHC_STATO=3;
  public final static Integer CLN_SHC_DTFINE=15;
  public final static Integer CLN_SHC_TIPO=17;
  public final static Integer CLN_SHC_ID=0;
  
  public final static Integer RG_SHC_INIZIO=6;
  
  //sheet indicatori
  public final static Integer CLN_SHI_TMPMEDIO=5;
  public final static Integer CLN_SHI_WEEK1=1;
  public final static Integer CLN_SHI_WEEK52=52;
  
  public final static Integer CLN_SHI_VALME12=2;
  public final static Integer CLN_SHI_VALMEC=14;
  
  public final static Integer RG_SHI_TKAPERTI=3;
  public final static Integer RG_SHI_TKCHIUSI=4;
  public final static Integer RG_SHI_TMPMEDIO=32;
  public final static Integer RG_SHI_VALMESI=34;
  public final static Integer RG_SHI_MESI=35;
  
  
  public final static String APERTO="APERTO";
  public final static String CHIUSO="CHIUSO";
  
  
  
  
  public XlsManutenzioniStd(String fileName) {
    super(fileName);
  }
  
  
  
  public HSSFSheet getSheetCartellinature(){
    return getWorkBookFileXls().getSheet(SHEETCARTELLINATURE);
  }
  
  
  public HSSFSheet getSheetIndicatori(){
    return getWorkBookFileXls().getSheet(SHEETINDICATORI);
  }
 
  
  public Integer getClnDataInizio(){
    return CLN_SHC_DTINIZIO;
  }
  
  public Integer getClnDataFine(){
    return CLN_SHC_DTFINE;
  }
  
  public Integer getClnStatoTk(){
    return CLN_SHC_STATO;
  }
  
  
  
  /**
   * Torna la lista dei ticket del file xls.
   * @return 
   */
  public List<TicketBean> getListTicket(){
    List tickets=new ArrayList();
    
    
    
    return tickets;
  }
  
  
  public TicketSettBean getInfoTicketsSett(Date data){
    TicketSettBean settBean=new TicketSettBean();
    Integer i=RG_SHC_INIZIO;
    Long diffgg=Long.valueOf(0);
    
    settBean.setAnno(DateUtils.getYear(data));
    settBean.setMese(DateUtils.getMonth(data)+1);
    settBean.setSettimana(DateUtils.getWorkWeek(data));
    int rows=getSheetCartellinature().getLastRowNum();
    try{
      while(i<=rows){
        Date dataInizio=null;
        Date dataFine=null;
        String stato="";
        String tipo="";
        HSSFRow riga=this.getSheetCartellinature().getRow(i);
        if(riga==null){
          i++;
          continue;
        }
        
        if(riga.getCell(CLN_SHC_TIPO)!=null ){ 
          tipo=riga.getCell(CLN_SHC_TIPO).getStringCellValue();
        }
        
        //se la colonna tipologia è in qualche modo valorizzata 
        if(!StringUtils.isEmpty(tipo)){
          i++;
          continue;
        }
        if(riga.getCell(CLN_SHC_DTINIZIO)!=null && 
            riga.getCell(CLN_SHC_DTINIZIO).getCellType()==HSSFCell.CELL_TYPE_NUMERIC){

          dataInizio=riga.getCell(CLN_SHC_DTINIZIO).getDateCellValue();   
        }


        if(riga.getCell(CLN_SHC_DTFINE)!=null &&
            riga.getCell(CLN_SHC_DTFINE).getCellType()==HSSFCell.CELL_TYPE_NUMERIC){

          dataFine=riga.getCell(CLN_SHC_DTFINE).getDateCellValue();   
        }


        if(riga.getCell(CLN_SHC_STATO)!=null &&
            ( riga.getCell(CLN_SHC_STATO).getCellType()==HSSFCell.CELL_TYPE_STRING || 
                riga.getCell(CLN_SHC_STATO).getCellType()==HSSFCell.CELL_TYPE_FORMULA )   ){
          stato=riga.getCell(CLN_SHC_STATO).getStringCellValue().toUpperCase();
        }


        if(CHIUSO.equals(stato) ){ // dataFine!=null
          settBean.add1TkChiuso();
          if(dataFine!=null){
            diffgg+=getNumGGLav(dataInizio, dataFine);
          }else{
            _logger.info("Riga n."+i+ "ticket chiuso senza data fine ");
            if(dataInizio==null){
              _logger.warn("Attenzione !! Riga n."+i+ "ticket chiuso senza data inizio e fine - Setto dataInizio con la data corrente ");
              diffgg+=1;
              HSSFCell cell=riga.getCell(CLN_SHC_DTINIZIO);
              if(cell!=null){
                cell.setCellValue(data);
              }else{
                _logger.warn("Attenzione cella relativa alla data inizio non valorizzata. Impossibile settare la data apertura tk");
              }
            }else{
              HSSFCell cell=riga.getCell(CLN_SHC_DTFINE);
              diffgg+=DateUtils.daysBetween(dataInizio, data)+1;
              if(cell!=null){
                cell.setCellValue(data);
              }else{
                _logger.warn("Attenzione cella relativa alla data fine non valorizzata. Impossibile settare la data chiusura tk");
              }
            }  
          }  
        }else if(dataInizio!=null){
          _logger.debug("Riga tk aperto :"+(i+1));
          Integer meseDiff=DateUtils.monthsBetween(dataInizio, data);
          settBean.addTkMeseRif(meseDiff);
          settBean.add1TkAperto();
        }

        i++;
      }
    }finally{
      _logger.info("Righe processate "+i+ " di "+rows);
    }
    Double tmedio=Double.valueOf(0);
    if(settBean.getTkChiusi()>0)
      tmedio=diffgg/new Double(settBean.getTkChiusi());
    
    settBean.setTempoMedioGg(tmedio);
    
    
    return settBean;
  }
  
  public void elabDatiSett(Date data,TicketSettBean bean) throws ParseException{
    Integer sett=bean.getSettimana();
    String mese="";
    if(bean.getMese()<10)
      mese="0";
    
    mese+=bean.getMese();
    Date dataNew=DateUtils.StrToDate("01"+mese+bean.getAnno(), "ddMMyyyy");
    //fisso i valori settimanali dei tk aperti  e chiusi
    if(getWorkBookFileXls().isWriteProtected()){
      
      getWorkBookFileXls().unwriteProtectWorkbook();
    }
    //shift delle settimane
    HSSFRow rA=getSheetIndicatori().getRow(RG_SHI_TKAPERTI);
    HSSFRow rC=getSheetIndicatori().getRow(RG_SHI_TKCHIUSI);
    
    for (int c=CLN_SHI_WEEK1;c<CLN_SHI_WEEK52;c++){
      HSSFCell cellA=rA.getCell(c);
      HSSFCell cellAPlus=rA.getCell(c+1);
      double valA=cellAPlus.getNumericCellValue();
      cellA.setCellValue(valA);
      
      HSSFCell cellC=rC.getCell(c);
      HSSFCell cellCPlus=rC.getCell(c+1);
      double valC=cellCPlus.getNumericCellValue();
      cellC.setCellValue(valC);
      
      
    }
    HSSFCell cellA=rA.getCell(CLN_SHI_WEEK52);
    cellA.setCellValue(bean.getTkAperti());
    HSSFCell cellC=rC.getCell(CLN_SHI_WEEK52);        
    cellC.setCellValue(bean.getTkChiusi());
    
    
    //aggiorna grafico settimane
//    HSSFCell cellA=getSheetIndicatori().getRow(RG_SHI_TKAPERTI).getCell(sett);
//    cellA.setCellValue(bean.getTkAperti());
//    HSSFCell cellC=getSheetIndicatori().getRow(RG_SHI_TKCHIUSI).getCell(sett);        
//    cellC.setCellValue(bean.getTkChiusi());
    
    HSSFCell cellT=getSheetIndicatori().getRow(RG_SHI_TMPMEDIO).getCell(CLN_SHI_TMPMEDIO);
    cellT.setCellValue(bean.getTempoMedioGg());
    
    //aggiorna grafico mesi precedenti
    int j=12;
    for(int i=CLN_SHI_VALME12;i<=CLN_SHI_VALMEC;i++){
      HSSFCell cellM=getSheetIndicatori().getRow(RG_SHI_VALMESI).getCell(i);
      cellM.setCellValue(bean.getValTkMeseRif(j));
      if(j<12){
        Date dataAppo=DateUtils.addMonths(dataNew, -j);
        HSSFCell cellTM=getSheetIndicatori().getRow(RG_SHI_MESI).getCell(i);
        cellTM.setCellValue(dataAppo);
      }
      j--;
    }
    
//    this.getWorkBookFileXls().
  }  
  
  
 private Integer getNumGGLav(Date dtInizio,Date dtFine){
   Integer numGg=Integer.valueOf(0);
   if(dtInizio==null || dtFine==null)
    return numGg;
   
   Integer ggTot=DateUtils.daysBetween(dtInizio, dtFine)+1;
   Integer ggNnLav=getNumGGNnLav(dtInizio, dtFine);
   
   return (ggTot-ggNnLav);
   
 }
 
 
private Integer getNumGGNnLav(Date dtInizio,Date dtFine){
  Integer numG=Integer.valueOf(0);
  if(dtInizio==null || dtFine==null)
    return numG;
  
  Date datatmp=dtInizio;
  GregorianCalendar gc= new GregorianCalendar();
  while (datatmp.compareTo(dtFine)<=0){
    gc.setTime(datatmp);
    int gg=gc.get(GregorianCalendar.DAY_OF_WEEK);
    if(gg==GregorianCalendar.SUNDAY || gg==GregorianCalendar.SATURDAY){
      numG++;
    }
    datatmp=DateUtils.addDays(datatmp, 1);
  }
  
  return numG;
}  
  
  private static final Logger _logger = Logger.getLogger(XlsManutenzioniStd.class);   
}
