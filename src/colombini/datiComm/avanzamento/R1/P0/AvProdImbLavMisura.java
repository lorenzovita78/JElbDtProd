/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1.P0;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.avanzamento.R1P3.AvProdLineeOnTAP;

/**
 *
 * @author lvita
 */
public class AvProdImbLavMisura extends AvProdLineeOnTAP {

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_LAVMISURAIMB_EDPC;
  }
  
}
