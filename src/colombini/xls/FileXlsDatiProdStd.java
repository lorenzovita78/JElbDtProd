/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls;

import fileXLS.FileExcelJXL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class FileXlsDatiProdStd extends FileXlsPoiDatiProd{
  
  protected Map guastiMap=null;
  protected Map perditeGestionaliMap=null;
  protected Map setupMap=null;
  
  
  public FileXlsDatiProdStd(String fileName){
    super(fileName);
    initMaps();
  }
  
  protected void initMaps() {
    guastiMap=new HashMap();
    perditeGestionaliMap=new HashMap();
    setupMap=new HashMap();
  }

  public Map getGuastiMap() {
    return guastiMap;
  }

  public void setGuastiMap(Map guastiMap) {
    this.guastiMap = guastiMap;
  }

  public Map getPerditeGestionaliMap() {
    return perditeGestionaliMap;
  }

  public void setPerditeGestionaliMap(Map perditeGestionaliMap) {
    this.perditeGestionaliMap = perditeGestionaliMap;
  }

  public Map getSetupMap() {
    return setupMap;
  }

  public void setSetupMap(Map setupMap) {
    this.setupMap = setupMap;
  }
  
  
  
  @Override
  public Integer getNumRigheGg() {
    return FileExcelJXL.NUMRGXGIORNOVAL;
  }

  /**
   * Torna il delta delle righe da escludere da inizio della giornata e dalla fine della giornata
   * del file Xls. Di default è impostato a zero
   * @return Integer
   */
  @Override
  public Integer getDeltaRigheXls(){
    return Integer.valueOf(0);
  }
  
  @Override
  public Integer getNumColFermi() {
    return Integer.valueOf(2);
  }

  @Override
  public Integer getColOraFineImp() {
    return FileExcelJXL.POS_OREIMPFINE;
  }

  @Override
  public Integer getColOraIniImp() {
    return FileExcelJXL.POS_OREIMPINI;
  }

  @Override
  public Integer getColOraFineNnProd() {
    return FileExcelJXL.POS_ORENNPRODFINE;
  }

  @Override
  public Integer getColOraIniNnProd() {
    return FileExcelJXL.POS_ORENNPRODINI;
  }

  @Override
  public Integer getColFineFermi() {
    return FileExcelJXL.POS_VALFERMI3;
  }

  @Override
  public Integer getColIniFermi() {
    return FileExcelJXL.POS_CAUSFERMI1;
  }

  

  
  /**
   * Torna il tempo relativo ai fermi gestionali in un determinato giorno
   * @param Date data
   * @return Long tempo espresso in minuti
   */
  public Long getTempoPerGestionaliGg(Date data){
    Map fermi= this.getMappaFermiGg(data);
    return getTempoPerGestionaliGg(fermi);
  }
  
  /**
   * Torna il tempo relativo ai fermi gestionali in un determinato giorno
   * @param fermi Map
   * @return Long tempo espresso in minuti
   */
  public Long getTempoPerGestionaliGg(Map fermi){
    return getValoreFromMaps(fermi, this.getPerditeGestionaliMap());
  }
  
  /**
   * Torna il tempo di setup in un determinato giorno
   * @param data Date
   * @return Long tempo espresso in minuti
   */
  public Long getTempoSetupGg(Date data){
    Map fermi= this.getMappaFermiGg(data);
    return getTempoSetupGg(fermi);
  }
  
  /**
   * Torna il tempo di setup in un determinato giorno
   * @param Map fermi
   * @return Long tempo espresso in minuti
   */
   
  public Long getTempoSetupGg(Map fermi){
    return FileExcelJXL.getValoreFromMaps(fermi, this.getSetupMap());
  }
  
  
  /**
   * Torna il tempo totale dei fermi(guasti,setup,perdite gestionali..ecc) in un determinato giorno
   * @param Date data
   * @return Long tempo espresso in minuti
   */
  public Long getTempoTotFermiGg(Date data){
    Map fermi= this.getMappaFermiGg(data);
    return getTempoTotFermiGg(fermi);
  }
  
  /**
   * Torna il tempo totale dei fermi(guasti,setup,perdite gestionali..ecc) in un determinato giorno
   * @param Map fermi
   * @return Long tempo espresso in minuti
   */
  public Long getTempoTotFermiGg(Map fermi){
    return FileExcelJXL.getDurataFermi(fermi);
  }
  
  
  /**
   * Torna il tempo relativo ai guasti in un determinato giorno
   * @param Date data
   * @return Long tempo espresso in minuti
   */
  public Long getTempoGuastiGg(Date data){
    Long tsetup=getTempoSetupGg(data);
    Long tpg=getTempoPerGestionaliGg(data);
    Long ttotguasti=getTempoTotFermiGg(data);
            
    return (ttotguasti-tsetup-tpg);        
  }
  
  /**
   * Torna il tempo relativo ai guasti in un determinato giorno
   * @param Date data
   * @return Long tempo espresso in minuti
   */
  public Long getTempoGuastiGg(Map fermi){
    Long tsetup=getTempoSetupGg(fermi);
    Long tpg=getTempoPerGestionaliGg(fermi);
    Long ttotguasti=getTempoTotFermiGg(fermi);
            
    return (ttotguasti-tsetup-tpg);        
  }
  
  
  /**
   * Torna il tempo espresso in minuti relativo ad una daterminata causale in un determinato giorno
   * @param Date data
   * @param String causale
   * @return Long minuti
   */
  public Long getTempoCausale(Date data,String causale){
    Map fermi= this.getMappaFermiGg(data);
    return getTempoCausale(fermi, causale);
  }
  
  /**
   * Torna il tempo espresso in minuti relativo ad una daterminata causale di fermo
   * @param Map mappa dei fermi
   * @param String causale
   * @return Long minuti
   */
  public Long getTempoCausale(Map fermi,String causale){
    Long valore=Long.valueOf(0);
    if(fermi.containsKey(causale)){
      valore=ClassMapper.classToClass(fermi.get(causale),Long.class);
    }    
   
    return valore;
  }
  
  
  
  
  public Long getValoreFromMaps(Map mappavalori,Map mappachiavi){
    Long valore=Long.valueOf(0);
    if(mappavalori==null || mappachiavi==null)
      return valore;
    
    String key=null;
    Set set=mappavalori.keySet();
    Iterator iter=set.iterator();
    while (iter.hasNext()){
       key=(String) iter.next();
       if(mappachiavi.containsKey(key)){
         valore+=ClassMapper.classToClass(mappavalori.get(key),Long.class);
       }
    }
    return valore;
  }

 
  
}
