/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm;

import colombini.util.DatiCommUtils;
import db.Connections;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per estrazione linee lavorative a commessa per calcolo carico e avanzamento commessa
 * ZLFLCC=1 indica che per la linea è previsto il calcolo del carico della commessa
 * ZLFLAP=1 indica che per la linea è previsto il calcolo dell'avanzamento della commessa
 *          il calcolo si attiva però solo nel caso in cui è esplicitata la classe java da eseguire ZLCJAP
 * Se ZLFLCC=1 && ZLFLAP=1 && ZLCJAP='' allora in questo caso viene popolata la tabella ZDPCAV (dati avanzamento commessa)
 * con i pz che dovrebbero essere prodotti per la commessa ma poi non viene mai aggiornato il campo dei pz non 
 * @author lvita
 */
public class QryLineeLavToElab extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
    sql.append(" SELECT ZLCONO as ").append(FilterFieldCostantXDtProd.FD_AZIENDA).append(
               " ,ZLPLGR as ").append(FilterFieldCostantXDtProd.FD_CODLINEA).append(
               " ,ZLPLDE as ").append(FilterFieldCostantXDtProd.FD_DESCLINEA).append(
               " ,ZLUNME as ").append(FilterFieldCostantXDtProd.FD_UNMIS).append(
               " ,ZLNOPT as ").append(FilterFieldCostantXDtProd.FD_NUMOPERTURNO).append(
               " ,ZLNORS as ").append(FilterFieldCostantXDtProd.FD_NUMORESTD).append(
               " ,ZLNTRN as ").append(FilterFieldCostantXDtProd.FD_NUMTURNI).append(
               " ,ZLOFXG as ").append(FilterFieldCostantXDtProd.FD_OREFLEXGG).append(
               " ,ZLOFXT as ").append(FilterFieldCostantXDtProd.FD_OREFLEXTR).append(
               " ,ZLCADO as ").append(FilterFieldCostantXDtProd.FD_CADORARIA).append(        
               " ,ZLALLC as ").append(FilterFieldCostantXDtProd.FD_ALLCOMM).append(
               " ,ZLANTC as ").append(FilterFieldCostantXDtProd.FD_ANTCOMM).append(        
               " ,ZLFLCC as ").append(FilterFieldCostantXDtProd.FD_FLAG).append(
               " ,ZLQRCC as ").append(FilterFieldCostantXDtProd.FD_CONDQUERY).append(
               " ,ZLCJCC as ").append(FilterFieldCostantXDtProd.FD_CLASSEXEC).append(
               
               " ,ZLFLAP as ").append(FilterFieldCostantXDtProd.FD_FLAGAVP).append(
               " ,ZLCJAP as ").append(FilterFieldCostantXDtProd.FD_CLASSAVP);        
    
    sql.append(" from ").append(Connections.getInstance().getLibraryPersAs400()).append(
                   " .").append(DatiCommUtils.TABDTLINEELAV);
    
    sql.append(" where 1=1");
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEATT))
      sql.append(" and ZLFLCC = 1"); //controllo solo le linee attive
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEAVP))
      sql.append(" and ZLFLAP = 1"); //controllo solo le linee per cui gestire l'avanzamento produzione
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_LOADAVP))
      sql.append(" and ZLCJAP <> ''"); //controllo per prendere solo le linee che hanno un calcolo automatico 
    
     if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEETOELAB))
      sql.append(addAND(inStatement("ZLPLGR", FilterFieldCostantXDtProd.FT_LINEETOELAB))); //filtro per elaborare solo alcune linee
    
    
    return sql.toString();
  }
  
  
  
}

