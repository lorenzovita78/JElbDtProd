/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author GGraziani
 */
public class QueryPzCommFornitori extends CustomQuery {

  public final static String PACKTYPEDISEQ="PACKTYPEDISEQ";
  public final static String COMMISSIONNO="COMMISSIONNO";
  public final static String COMMISSIONDATE="COMMISSIONDATE";
  public final static String COMMISSIONYEAR="COMMISSIONYEAR";
  public final static String FLUSSO="FLUSSO";
  public final static String FORNITORE="FORNITORE";

  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder cond=new StringBuilder(

                                       " where 1=1 \n");
    

    
    String select=" select \n" +
        "       collo = A.codice_collo\n" +
        "       ,prog_collo = A.Progressivo_p\n" +    
        "       ,CASE WHEN Flusso='P2' THEN 'P2MF1' WHEN Flusso='SC' THEN 'P0MP0' WHEN Flusso='LSM' THEN 'P4LSM' ELSE 'P2MF1' END as linea\n" + 
        "       ,box = A.box_vdl\n" +
        "       ,pedana = A.ped_vdl\n" +
        "       ,orderno = A.riferimento\n" +
        "       ,nr_riga = A.RigaOdv\n" +
        "       ,refart = A.mtno_p\n" +
        "       ,ItemDescription = A.itds_p\n" +
        "       ,ItemDescription = A.itds_p\n" +
        "       ,barcode = cast(A.commessa as char(3)) + cast(A.codice_collo as char(5)) + right('000' + cast(A.progressivo_p as varchar(3)),3)\n" +
        "       ,Colore = ''";

    
    select+= " from DesmosColombini.dbo.Dati_Tracciatura as A\n" +
            "\n" +
            "left outer join MVX2DESMOS.dbo.ZCOMME as Z\n" +
            "on A.Commessa = Z.ZCCCOM and A.ACDLD=Z.ZCCDLD ";
   
    

    if(isFilterPresent(COMMISSIONNO)){
      cond.append(addAND(eqStatement(" Z.ZCCCOM ", COMMISSIONNO))); 
    }
    
    if(isFilterPresent(COMMISSIONDATE)){
      cond.append(" and Z.ZCCDLD=convert(datetime, ").append(getFilterSQLValue(COMMISSIONDATE)).append(" ,120 )");
    }else if(isFilterPresent(COMMISSIONYEAR)){
      cond.append(" and year(CommissionDate)= ").append(getFilterSQLValue(COMMISSIONYEAR));
    }
  
    
    if(isFilterPresent(FLUSSO))
      {
      cond.append(addAND(inStatement(" Flusso ", FLUSSO))); 
    }
    
    if(isFilterPresent(FORNITORE))
      {
      cond.append(addAND(inStatement(" Fornitore ", FORNITORE))); 
    }
    
    return select + cond.toString(); 
  
  }
 
  
  
  
}
