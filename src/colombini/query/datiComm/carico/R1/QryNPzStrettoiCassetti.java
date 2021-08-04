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
public class QryNPzStrettoiCassetti extends CustomQuery{
  
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
    
    StringBuilder subquery1=new StringBuilder();
    subquery1.append("select  cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
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
                     " AND ( ( CLLINP in ('06030' ,'06031','06034','06039') and CLARTI LIKE 'NC%'  and cldim2<900  ) ").append(
                     " OR ( CLLINP in ('06516','06517','06518')  and CLARTI LIKE 'NC%'  and cdditt <>'B' )  )").append(  //mod il 21/05/2015
                    //" OR ( CLLINP in ('36010','36030')  and CLARTI LIKE 'NC%'  and cldim2<900  and cdditt <>'A' )  )").append(
                    " group by clarti,cfdivi ) a,  ").append(
                  " ( select distinct mbitno ").append(
                      " from MITBAL ").append(
                     " where 1=1  ").append(
                     " and mbcono=").append(azdefault).append(
                     " and MBITNO  LIKE 'NC%' ").append(
                     " and mbpuit<>2 ) b " ).append(
                     "  where a.clarti=b.mbitno ").append(
                     " group by cfdivi" );
    
    StringBuilder subquery2=new StringBuilder();
    subquery2.append(" select  cfdivi as ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                     " , sum(clqta) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
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
                     " AND CLLINP in ('06090','06091','06092','06095','06098','46091') ").append(
                     " group by cfdivi ");
    
    String qry="SELECT "+FilterFieldCostantXDtProd.FD_DIVISIONE+
               " , SUM( "+FilterFieldCostantXDtProd.FD_NUMPEZZI +" ) as "+FilterFieldCostantXDtProd.FD_NUMPEZZI +
               " from ( \n ("+ subquery1.toString() + " ) \n union \n (  "+subquery2+ " )  ) c"+
               " group by "+FilterFieldCostantXDtProd.FD_DIVISIONE;
    
    return qry;
    
   }
}
