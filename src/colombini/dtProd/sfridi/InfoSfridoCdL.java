/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import exception.ElabException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lvita
 */
public class InfoSfridoCdL  implements ISfridoInfo{
  
  private String cdL;
  private Map params; 
  
  public InfoSfridoCdL(String cdL) {
    this.cdL = cdL;
    params=new HashMap();
  }

  public String getCdL() {
    return cdL;
  }

  public Map getParams() {
    return params;
  }

  public void setParams(Map params) {
    this.params = params;
  }
  
  
  
  @Override
  public List getInfoSfrido(Date dataInizio, Date dataFine) throws ElabException {
    return new ArrayList();
  }
  
}
