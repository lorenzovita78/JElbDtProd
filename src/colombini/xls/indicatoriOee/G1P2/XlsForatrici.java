/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.G1P2;


import colombini.xls.FileXlsDatiProdStd;
import java.util.Date;

/**
 *
 * @author lvita
 */
public class XlsForatrici extends FileXlsDatiProdStd{
  
  final static Integer POSINISETUP=Integer.valueOf(13);
  final static Integer POSFINSETUP=Integer.valueOf(15);
  
  
  public XlsForatrici(String fileName) {
    super(fileName);
  }

  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(20);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(16);
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(19);
  }
 
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(5);
  }
  
  
  public Integer getColIniSetup(){
    return POSINISETUP;
  }
  
  public Integer getColFinSetup(){
    return POSFINSETUP;
  }

  
  
 
  
  @Override
  public Long getTempoSetupGg(Date data){
    Integer rigaIni=this.getRigaInizioGg(data);
    Integer rigaFin=this.getRigaFineGg(rigaIni);
    
    rigaIni+=this.getDeltaRigheXls();
    rigaFin-=this.getDeltaRigheXls();
    
    return this.getValNumColonne(rigaIni, rigaFin, getColIniSetup(), getColFinSetup()).longValue();
     
  }
 
  public Long getTempoMicrofermiGg(Date data){
    return Long.valueOf(0);
  }
}
