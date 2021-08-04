/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per conoscere i numeri dei pezzi della linea Articoli Commerciali P3
 * La query si divide in due parti :
 * 1 - pezzi per bu esclusi quelli di Idea
 * 2 - pezzi relativi a Idea ( che potrebbero essere legate anche ad altre BU ( filtro articolo like 'J%')
 * 
 * @author lvita
 */
public class QryNPziRepComm extends CustomQuery {

  //modifica 26/11/2013 inclusa linea 11000...c'è il passaggio da linp 10000 a 11000
  
  @Override
  public String toSQLString() throws QueryException {
    
    String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
    String dataC=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
    
    String azdefault=FilterFieldCostantXDtProd.AZCOLOMBINI.toString();
    if(numCom==null || numCom.isEmpty())
      throw new QueryException("Commessa non definita impossibile interrogare il database");
    
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_AZIENDA))
      azdefault=getFilterSQLValue(FilterFieldCostantXDtProd.FT_AZIENDA);
    
    String tabCommessa=FilterFieldCostantXDtProd.getNomeTabCommessa(numCom);
    String tabZpdsum=FilterFieldCostantXDtProd.getNomeTabDistintaCommessa(numCom);
    
    
    String select1="select  cfdivi as "+FilterFieldCostantXDtProd.FD_DIVISIONE+
                        " , sum(clqta) as "+FilterFieldCostantXDtProd.FD_NUMPEZZI;
    
    String select2="select  'J08' as "+FilterFieldCostantXDtProd.FD_DIVISIONE+
                        " , sum(clqta) as "+FilterFieldCostantXDtProd.FD_NUMPEZZI;
    
    String groupBy =" \n group by cfdivi ";
            
    StringBuilder whereBase =new StringBuilder();
    StringBuilder fromBase =new StringBuilder();
    StringBuilder sql=new StringBuilder();
    
    fromBase.append(" from ").append(tabZpdsum).append(
                    " , ").append(FilterFieldCostantXDtProd.TABFACILITY).append(
                    " , ").append(tabCommessa);
    
    
    StringBuilder fromSql1=new StringBuilder(" exception join (select distinct MMITNO ").append(
                     " FROM mitmas ,mitbal ").append(
                    " where mmcono = mbcono ").append(
                      " and mmitno = mbitno ").append(
                      " and(  ( mbwhlo='A30' AND MBPUIT=2 ) ").append(
                      " or  (  mbwhsl like 'IMB%' ) ").append(
                      " or  (  mbwhsl ='IDEA' ) )   )  A on clarti=A.MMITNO ");
    
    StringBuilder fromSql2=new StringBuilder(" inner join ").append(
                              " (select distinct MMITNO FROM mitmas ,mitbal ").append(
     " where mmcono = mbcono ").append(
     " and mmitno = mbitno ").append(
     " and mbwhsl='IDEA' ) B on  clarti=b.MMITNO ");
    
    
   whereBase.append(" where 1=1").append(
               " and CLNART<>0 ").append(
               " and CLLINP  in ('10000','11000') ").append(
               " and CLAMGS=").append(dataC).append(
               " and pscono=").append(azdefault).append(
               " and clnror=psridn ").append(
               " and clriga=psridl ").append(
               " and clncol=pselno ").append(
               " and psrlev=1 ").append(
               " and cfcono=pscono ").append(
               " and cffaci=psfcco ");
    
    sql.append(" SELECT ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(" , ").append(
            " SUM( ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(" ) ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(" FROM ( ").append(
            select1).append(fromBase.toString()).append(
            fromSql1).append( whereBase).append(
            " and clarti not like 'J%' ").append(
            groupBy).append(
            "\n UNION \n ").append(
            select2).append(fromBase).append(
            fromSql2).append(whereBase).append(
            "   ) ZZ  \n group by ").append(FilterFieldCostantXDtProd.FD_DIVISIONE);
            
   
   
    return sql.toString();
  }
  
  
//  String select1="select  cfdivi as "+FilterFieldCostantXDtProd.FD_DIVISIONE+
//                        " , sum(clqta) as "+FilterFieldCostantXDtProd.FD_NUMPEZZI;
//    
//    String select2="select  'J08' as "+FilterFieldCostantXDtProd.FD_DIVISIONE+
//                        " , sum(clqta) as "+FilterFieldCostantXDtProd.FD_NUMPEZZI;
//    
//    String groupBy =" \n group by cfdivi ";
//            
//    StringBuilder subsql=new StringBuilder();
//    StringBuilder sql=new StringBuilder();
//    
//    subsql.append(
//               " from ").append(tabCommessa).append(
//               " , ").append(tabZpdsum).append(
//               " , ").append(FilterFieldCostantXDtProd.TABFACILITY).append(
//               " where 1=1").append(
//               " and CLNART<>0 ").append(
//               " and CLLINP  in ('10000','11000') ").append(
//               " and CLAMGS=").append(dataC).append(
//               " and pscono=").append(azdefault).append(
//               " and clnror=psridn ").append(
//               " and clriga=psridl ").append(
//               " and clncol=pselno ").append(
//               " and psrlev=1 ").append(
//               " and cfcono=pscono ").append(
//               " and cffaci=psfcco ");
//    
//    sql.append(select1).append(subsql.toString()).append(
//            " and clarti not like 'J%' ").append(
//            condArticoli).append(
//            groupBy).append(
//            "\n UNION \n ").append(
//            select2).append(subsql.toString()).append(
//            " and clarti like 'J%' ").append(
//            " \n order by ").append(FilterFieldCostantXDtProd.FD_DIVISIONE);
  
  
  
}
