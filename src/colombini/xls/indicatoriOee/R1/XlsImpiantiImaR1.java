/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1;


import colombini.costant.CostantsColomb;
import colombini.xls.FileXlsDatiProdStd;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lvita
 */
public abstract class XlsImpiantiImaR1 extends FileXlsDatiProdStd {
  
  public XlsImpiantiImaR1(String fileName){
    super(fileName);
  }
  
  @Override
  public Integer getNumRigheGg() {
    return Integer.valueOf(40);
  }
  
  
  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(10);
  }
  
  
  public List getListaFermiTotGg(Date data,String linea){
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    
    return getListaFermiLinea(rI, rF, linea);
  }
  
  
  public List getListaFermiGg(Date data,String linea){
    Integer rI=getRigaInizioGg(data);
    Integer rF=getRigaFineGg(rI);
    rI+=getDeltaRigheXls();
    rF-=getDeltaRigheXls();
    
    return getListaFermiLinea(rI, rF, linea);
  }
  
  //per entrambi gli Impianti Ima R1 consideriamo come tempo massimo della giornata 14,30 h
  @Override
  public Long getMinutiProdImpianto(Date data) {
    Long tmpProd=super.getMinutiProdImpianto(data);
    
    if(tmpProd==null || tmpProd==0)
       tmpProd=CostantsColomb.TEMPOTOT2TURNIMIN;
    
    if(tmpProd>CostantsColomb.TEMPOTOT2TURNIMIN)
       tmpProd=CostantsColomb.TEMPOTOT2TURNIMIN;
    
    return tmpProd;
  }
  
  
  
  public abstract List getListaFermiLinea(Integer rI,Integer rF,String linea);
  
}
