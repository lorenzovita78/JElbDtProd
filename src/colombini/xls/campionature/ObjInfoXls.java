/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.campionature;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.FilePropUtils;

/**
 *
 * @author lvita
 */
public class ObjInfoXls {
  
  public final static String CAMPIONATURE="Campionature";
  
  private String stabilimento;
  private String piano;
  private String codLinea;
  private String nomeFile;
  private String pathFile;
  private Integer righeGG;
  private List<Integer>  colsCausale;  // lista contenente tutte le colonne che prevedono la causale di Campionatura
  private Integer nColPeriodo; // se 1 indica che la quantità è già il numero di minuti
                               // se 2 indica che il periodo è un orario con ora inizio e ora fine
  
  private String error; //eventuale errore nella apertura/lettura del file
  
  
  
  public ObjInfoXls(){
    colsCausale=new ArrayList();
  }
  
  
  public Long getMinutiCampionature(Date dataInizio,Date dataFine){
    Long minuti= Long.valueOf(0);
    XlsProduzione xls=null;
    try {
      
      xls=new XlsProduzione(getFileNameComplete());
      xls.openFile();
      xls.setRigheGG(righeGG);
      
      Integer rigaIni=xls.getRigaInizioGg(dataInizio);
      Integer rigaFine=xls.getRigaFineGg(dataFine);
      
      if(colsCausale.size()==1){
        xls.setColCausCampionature(colsCausale.get(0));
        minuti=xls.getMinutiImprodImpianto(CAMPIONATURE, rigaIni, rigaFine); 
      }else{
        for(Integer colTmp : colsCausale){
          minuti+=xls.getMinutiImprodImpianto(rigaIni, rigaFine, colTmp,CAMPIONATURE);
        }
      }
      
    } catch (FileNotFoundException ex) {
      error=" File "+getFileNameComplete()+ " non trovato ";
     _logger.error(error+" --> "+ex.getMessage());
    } catch (IOException ex) {
      error=" Impossibile accedere al file "+getFileNameComplete();
     _logger.error(error+ "  --> "+ex.getMessage());
    } finally {
      try {
        if(xls!=null)
          xls.closeFile();
      } catch (IOException ex) {
        _logger.error(" Impossibile chiudere il file "+getFileNameComplete()+ "  -->"+ex.getMessage());
      }  
    }
    
    
    
    return minuti;
  }
  
  
  public String getFileNameComplete(){
    
    return FilePropUtils.getInstance().getJavaPath(pathFile+nomeFile);
  }
  
  
  public String getCodLinea() {
    return codLinea;
  }

  public void setCodLinea(String codLinea) {
    this.codLinea = codLinea;
  }

  public List getColsCausale() {
    return colsCausale;
  }

  public void setColsCausale(List colsCausale) {
    this.colsCausale = colsCausale;
  }

  public Integer getNColPeriodo() {
    return nColPeriodo;
  }

  public void setNColPeriodo(Integer nColPeriodo) {
    this.nColPeriodo = nColPeriodo;
  }

  public String getNomeFile() {
    return nomeFile;
  }

  public void setNomeFile(String nomeFile) {
    this.nomeFile = nomeFile;
  }

  public String getPathFile() {
    return pathFile;
  }

  public void setPathFile(String pathFile) {
    this.pathFile = pathFile;
  }

  public String getPiano() {
    return piano;
  }

  public void setPiano(String piano) {
    this.piano = piano;
  }

  public Integer getRigheGG() {
    return righeGG;
  }

  public void setRigheGG(Integer righeGG) {
    this.righeGG = righeGG;
  }

  public String getStabilimento() {
    return stabilimento;
  }

  public void setStabilimento(String stabilimento) {
    this.stabilimento = stabilimento;
  }
  
  
  public void addColCausale(Integer nCol){
    colsCausale.add(nCol);
  }

  public String getError() {
    return error;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ObjInfoXls.class); 
  
}
