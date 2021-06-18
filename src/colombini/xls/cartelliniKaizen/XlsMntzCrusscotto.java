/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.cartelliniKaizen;


import fileXLS.FileXlsXPoiRW;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 *
 * @author lvita
 */
public class XlsMntzCrusscotto extends FileXlsXPoiRW {
  
  public final static String SHEETR1="R1";
  public final static String SHEETR1STORICO="R1_Storico";
  
  public final static String SHEETG1R2="G1-R2";
  public final static String SHEETG1R2STORICO="G1-R2_Storico";
 
  
  public final static String R1P0="R1P0";
  public final static String R1P1="R1P1";
  public final static String R1P2="R1P2";
  public final static String R1P3="R1P3";
  public final static String R1P4="R1P4";
  public final static String R1ARTEC="R1ARTEC";
  public final static String R1FEBAL="FEBAL";
  
  public final static String G1P0="G1P0";
  public final static String G1P2="G1P2";
  
  public final static String R2P1="R2P1";
  
  //sheet R1
  public final static Integer CLN_SHR1_TKAPERTI=3;
  public final static Integer CLN_SHR1_TKCHIUSI=4;
  
  //sheet R1_Storico
  public final static Integer CLN_SHCR1_WEEK1=1;
  public final static Integer RG_SHC_R1_P0_TKA=3;
  public final static Integer RG_SHC_R1_P1_TKA=30;
  public final static Integer RG_SHC_R1_P2_TKA=57;
  public final static Integer RG_SHC_R1_P3_TKA=84;
  public final static Integer RG_SHC_R1_P4_TKA=111;
  public final static Integer RG_SHC_R1ARTEC_TKA=138;
  public final static Integer RG_SHC_R1FEBAL_TKA=166;
  
   //sheet G1-R2_Storico
  public final static Integer CLN_SHG1R2_WEEK1=1;
  public final static Integer RG_SHCG1R2_G1P0_TKA=3;
  public final static Integer RG_SHCG1R2_G1P2_TKA=30;
  public final static Integer RG_SHCG1R2_R2P1_TKA=57;
  

  
  public XlsMntzCrusscotto(String fileName) {
    super(fileName);
//    loadList();
  }
  
  
  public void aggSheetConsuntivoPiano(Integer settimana,String nSheetGraf,String codPiano,Long tkAperti,Long tkChiusi){
    
   Integer riga=getRowStabilimentoPiano(codPiano);
   XSSFSheet shG=this.getFileXls().getSheet(nSheetGraf);
   XSSFRow rwA=shG.getRow(riga);
   XSSFRow rwC=shG.getRow(riga+1);

//   XSSFCell cellA=rwA.getCell(settimana);
//   XSSFCell cellT=rwC.getCell(settimana);
//
//   cellA.setCellValue(tkAperti);
//   cellT.setCellValue(tkChiusi);
   
   //shift delle settimane.
   for (int c=XlsManutenzioniStd.CLN_SHI_WEEK1;c<XlsManutenzioniStd.CLN_SHI_WEEK52;c++){
      XSSFCell cellA=rwA.getCell(c);
      XSSFCell cellAPlus=rwA.getCell(c+1);
      double valA=cellAPlus.getNumericCellValue();
      cellA.setCellValue(valA);
      
      XSSFCell cellC=rwC.getCell(c);
      XSSFCell cellCPlus=rwC.getCell(c+1);
      double valC=cellCPlus.getNumericCellValue();
      cellC.setCellValue(valC);
      
    }
    XSSFCell cellA=rwA.getCell(XlsManutenzioniStd.CLN_SHI_WEEK52);
    cellA.setCellValue(tkAperti);
    XSSFCell cellC=rwC.getCell(XlsManutenzioniStd.CLN_SHI_WEEK52);        
    cellC.setCellValue(tkChiusi);
    
    
  }
  
 private Integer getRowStabilimentoPiano(String codPiano){
   if(R1P0.equals(codPiano) ){
     return RG_SHC_R1_P0_TKA;
   }else if(R1P1.equals(codPiano)){
     return RG_SHC_R1_P1_TKA;
   }else if(R1P2.equals(codPiano)){
     return RG_SHC_R1_P2_TKA;
   }else if(R1P3.equals(codPiano)){
     return RG_SHC_R1_P3_TKA;
   }else if(R1P4.equals(codPiano)){
     return RG_SHC_R1_P4_TKA;
   }else if(R1ARTEC.equals(codPiano)){
     return RG_SHC_R1ARTEC_TKA;
   }else if(R1FEBAL.equals(codPiano)){
     return RG_SHC_R1FEBAL_TKA;
   }else if(G1P0.equals(codPiano)){
     return RG_SHCG1R2_G1P0_TKA;
   }else if(G1P2.equals(codPiano)){
     return RG_SHCG1R2_G1P2_TKA;
   }else if(R2P1.equals(codPiano)){
     return RG_SHCG1R2_R2P1_TKA;
   }
   
   return Integer.valueOf(0);
 } 
  
  
//  private void loadList(){
//    listR1=new ArrayList();
//    listG1R2=new ArrayList();
//    
//    
//    List record=new ArrayList();
//    record.add(RG_SHR1_P0); 
//    record.add(RG_SHC_R1_P0_TKA);
//    listR1.add(record);
//    
//    record=new ArrayList();
//    record.add(RG_SHR1_P1); 
//    record.add(RG_SHC_R1_P1_TKA);
//    listR1.add(record);
//    
//    record=new ArrayList();
//    record.add(RG_SHR1_P2); 
//    record.add(RG_SHC_R1_P2_TKA);
//    listR1.add(record);
//    
//    record=new ArrayList();
//    record.add(RG_SHR1_P3); 
//    record.add(RG_SHC_R1_P3_TKA);
//    listR1.add(record);
//    
//    record=new ArrayList();
//    record.add(RG_SHR1_ARTEC); 
//    record.add(RG_SHC_R1ARTEC_TKA);
//    listR1.add(record);
//    
//      
//  }
// 
//  /**
//   * Aggiorna il file xls per i  dati relativi agli impianti di R1
//   * @param sett 
//   */
//  public void aggSheetR1(Integer sett){
//    aggSheets(sett,SHEETR1,SHEETR1STORICO,listR1);
//  }
// 
//   /**
//   * Aggiorna il file xls per i  dati relativi agli impianti di G1 e R2
//   * @param sett 
//   */
//  public void aggSheetG1R2(Integer sett){
//    aggSheets(sett,SHEETG1R2,SHEETG1R2STORICO,listG1R2);
//  }
  
//  /**
//   * Legge le informazioni dallo sheet dei dati e li aggiorna nello sheet dei grafici
//   * @param Integer settimana da aggiornare 
//   * @param nSheetDati nome dello sheet dei dati 
//   * @param nSheetGraf nome dello sheet dei grafici
//   * @param l1 lista contenente le righe da cui leggere
//   */
//  private void aggSheets(Integer settimana,String nSheetDati,String nSheetGraf,List<List> l1){
//    for(List rec:l1){
//      Integer rigaSd=ClassMapper.classToClass(rec.get(0),Integer.class);
//      Integer rigaSg=ClassMapper.classToClass(rec.get(1),Integer.class);
//      XSSFSheet shD=this.getFileXls().getSheet(nSheetDati);
//      XSSFRow rw=shD.getRow(rigaSd);
//      Double tkA=rw.getCell(XlsMntzCrusscotto.CLN_SHR1_TKAPERTI).getNumericCellValue();
//      Double tkC=rw.getCell(XlsMntzCrusscotto.CLN_SHR1_TKCHIUSI).getNumericCellValue();
//      
//      XSSFSheet shG=this.getFileXls().getSheet(nSheetGraf);
//      XSSFRow rwA=shG.getRow(rigaSg);
//      XSSFRow rwC=shG.getRow(rigaSg+1);
//      
//      XSSFCell cellA=rwA.getCell(settimana);
//      XSSFCell cellT=rwC.getCell(settimana);
//      
//      cellA.setCellValue(tkA);
//      cellT.setCellValue(tkC);
//    }
//  }
  
 
 
 
}
