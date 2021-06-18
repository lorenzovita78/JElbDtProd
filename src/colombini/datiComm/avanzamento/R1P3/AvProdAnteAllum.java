/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P3;

import colombini.costant.TAPWebCostant;

/**
 *
 * @author lvita
 */
public class AvProdAnteAllum extends AvProdLineeOnTAP {

 
 @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_ANTEALLUM_EDPC;
  }
  
 
}
