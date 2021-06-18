/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 * * Query per estrarre su impianto Ima Ante
 * per un dato giorno la qta dei pz scartati relativi alle due Combima 
 * Qui di seguito la lista delle causali definite come scarti di processo.
 * La qeury purtroppo ha un filtro statico!!!
 * @author lvita
 */
public class QueryPzScaImaAnteXCb extends CustomQuery{

  
  
//LEGENDA CAUSALI SCARTI IMA ANTE  
//IDCAUSDBIMA; CODCAUMVX; DESCR 		
//        14	B1	BORDO CON GRUMI COLLA
//        16	B2	BORDO CON MANCANZA COLLA
//        12	B3	BORDO MANCANTE
//        15	B8	BORDO IMA
//         4	G5	BORDO CORTO
//         6	S1	STICCHIAT. SPIGOLO/S
//         7	S2	STICCHIAT. LATO DRITTO
//        17	D0	FUORI SQUADRO
//        28	F1 	FORATURA
//        13	G4	BORDO COLORE ERRATO
//        27	G8	RIGHE (LAVORAZIONE I

  @Override
  public String toSQLString() throws QueryException {
    String dataRif=(String) getFilterValue(FilterQueryProdCostant.FTDATARIF);
    
    StringBuilder qry=new StringBuilder(
           " select count(*), cast(Grund As decimal) idcaus, station ").append( 
           "\n from ").append( 
        " ( select a.barcode, a.grund, ").append( 
            "\n (select max(b.zp) from dbo.tab_flow_c b where b.barcode=a.barcode and a.zpstatus>b.zp) as zp ").append( 
                "\n from dbo.tab_ETAusschuss a ").append( 
                "\n  where zpstatus>= convert(datetime ,(' ").append(dataRif).append(" 00:00:01'),120)   ").append( 
                "\n  and zpstatus<= convert(datetime ,('  ").append(dataRif).append("  23:59:59'),120)  )SC ").append( 
    "\n  inner join dbo.tab_flow_c D on sc.zp=D.zp  and sc.Barcode=D.barcode ").append( 
    "\n where  cast(Grund As decimal) in (14, 16, 12, 15, 4, 6, 7, 17, 28, 13, 27 ) ").append( 
    "\n  group by  cast(Grund As decimal),D.station  order by D.station, cast(Grund As decimal) "); 
 
    return qry.toString();
    
  }
  
}
