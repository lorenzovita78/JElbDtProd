/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 * La query cerca tutti i cassetti prodotti (filtro NC) dalle linee logiche relative all'imballo cassetti +
 * i pezzi prodotti dalle linee logiche artec che non fanno però pezzi artec
 * Si escludono i cassetti che hanno un fornitore esterno (mbpuit=2)
 * Si escludono inoltre i cassetti con dim >900 che sono prodotti in ARTEC
 * @author lvita
 */
public class QryNPzStrettoioCassettiArtec extends CustomQuery{
  
  @Override
  public String toSQLString() throws QueryException {
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    String tabZpdsum=FilterFieldCostantXDtProd.getNomeTabDistintaCommessa(numCom);
    
    StringBuilder qry=new StringBuilder();
    qry.append("select  cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " , sum(qta) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
               " from ( select  clarti ,cfdivi ,sum(clqta) as qta ").append(
                        " from ").append(tabCommessa).append(
                     " , ").append(tabZpdsum).append(     
                     " , ").append(FilterFieldCostantXDtProd.TABFACILITY).append(
                     " where 1=1").append(
                     " and CLNART<>0 ").append(
                     " and CLAMGS=").append(dataC).append(
                     " and pscono=").append(azdefault).append(
                     " and clnror=psridn ").append(
                     " and clriga=psridl ").append(
                     " and clncol=pselno ").append(
                     " and psrlev=1 ").append(
                     " and cfcono=pscono ").append(
                     " and cffaci=psfcco ").append(
                     "\n AND CLLINP in ( '36090', '36091', '36010','36015','36020','36025','36030','36200','36150' ) ").append(
                     " and CLARTI LIKE 'NC%' ").append(
                    " group by clarti,cfdivi ) a,  ").append(
                  " ( select distinct POPRNO  ").append(
                     " from MPDOPE ").append(
                     " where 1=1  ").append(
                     " and pocono=").append(azdefault).append(
                     " and poprno  LIKE 'NC%' ").append(
                     " and poplgr='03009' ").append( 
                     " ) b " ).append(
                     "  where a.clarti=b.POPRNO ").append(
                     " group by cfdivi" );
    
    return qry.toString();
    
   }
}
