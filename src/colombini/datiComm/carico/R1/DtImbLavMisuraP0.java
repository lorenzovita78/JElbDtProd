/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

  package colombini.datiComm.carico.R1;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.carico.CaricoComOnTAP;

/**
 *
 * @author lvita
 */
public class DtImbLavMisuraP0 extends CaricoComOnTAP{

  
  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_LAVMISURAIMB_EDPC;
  }
  
}
