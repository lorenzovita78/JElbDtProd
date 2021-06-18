/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.campionature;

import colombini.xls.FileXlsPoiDatiProd;



/**
 *
 * @author lvita
 */
public class XlsProduzione extends FileXlsPoiDatiProd{

  private Integer righeGG;
  private Integer colCausCampionature;
  
  
  public XlsProduzione(String fileName){
    super(fileName);
  }

  
  
  public void setColCausCampionature(Integer colCausCampionature) {
    this.colCausCampionature = colCausCampionature;
  }

  public void setRigheGG(Integer righeGG) {
    this.righeGG = righeGG;
  }

  @Override
  public Integer getDeltaRigheXls() {
    return Integer.valueOf(0);
  }
  
  
  @Override
  public Integer getNumRigheGg() {
    return righeGG;
  }

  @Override
  public Integer getNumColFermi() {
    return Integer.valueOf(-1);
  }

  @Override
  public Integer getColOraFineImp() {
    return Integer.valueOf(-1);
  }

  @Override
  public Integer getColOraIniImp() {
    return Integer.valueOf(-1);
  }

  @Override
  public Integer getColOraFineNnProd() {
    return colCausCampionature+2;
  }

  @Override
  public Integer getColOraIniNnProd() {
    return colCausCampionature+1;
  }

  @Override
  public Integer getColFineFermi() {
    return Integer.valueOf(-1);
  }

  @Override
  public Integer getColIniFermi() {
    return Integer.valueOf(-1);
  }
  
}
