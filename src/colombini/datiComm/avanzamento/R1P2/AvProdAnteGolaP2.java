/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento.R1P2;

import colombini.costant.TAPWebCostant;
import colombini.datiComm.avanzamento.R1P3.AvProdLineeOnTAP;


/**
 *
 * @author lvita
 */
public class AvProdAnteGolaP2 extends AvProdLineeOnTAP{

  @Override
  public String getCodLineaOnTAP() {
    return TAPWebCostant.CDL_ANTEGOLAP2_EDPC;
  } 
  
  
//  @Override
//  public void prepareDtProd(Connection conAs400) throws DatiCommLineeException {
//  }

//  @Override
//  protected Integer getPzProd(Connection conAs400) throws DatiCommLineeException {
//    Integer nPz=null;
//    QueryAVCommFromTAP qry=new QueryAVCommFromTAP();
//    qry.setFilter(FilterFieldCostant.FT_AZIENDA, getInfoLinea().getAzienda());
//    qry.setFilter(FilterFieldCostant.FT_LINEA, getInfoLinea().getCodLineaLav());
//    qry.setFilter(FilterFieldCostant.FT_DATA,getInfoLinea().getDataCommessa());
//    qry.setFilter(FilterFieldCostant.FT_NUMCOMM,getInfoLinea().getCommessa());
//    
//    try{
//      Object [] obj=ResultSetHelper.SingleRowSelect(conAs400,qry.toSQLString());
//      if(obj!=null && obj.length>1){
//        nPz=ClassMapper.classToClass(obj[1],Integer.class);
//      }
//    
//    } catch(SQLException s){
//      _logger.error("Errore in fase di esecuzione query --> "+s.getMessage());
//      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
//    } catch (QueryException ex) {
//       _logger.error("Errore in fase di esecuzione query --> "+ex.getMessage());
//      throw new DatiCommLineeException("Impossibile interrogare il database per reperire i dati relativi ai pz prodotti");
//    }
//    
//    return nPz;
//  }
  
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AvProdAnteGolaP2.class);

  
}
