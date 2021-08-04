/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import colombini.query.datiComm.carico.QryNumPezziCommessaStd;
import exception.QueryException;


/**
 *
 * @author lvita
 */
public class QryNPzForatriceMawPries extends QryNumPezziCommessaStd{
  
 
  @Override
  public String toSQLString() throws QueryException {
    
    setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA," AND( (CLLINP>='06053' AND CLLINP<='06056') OR  (CLLINP>='06250' AND  CLLINP<='06252') )");
    
    String qry1=super.toSQLString();
    qry1=qry1.substring(0, qry1.indexOf("order"));
    
    setFilter(FilterFieldCostantXDtProd.FT_CONDLINEA,"AND ((CLLINP>='06030' AND  CLLINP<='06031') OR  (CLLINP in('06033','06034','06039') ) )  AND clarti like 'SP%' ");
 
    String qry2=super.toSQLString();
    qry2=qry2.substring(0, qry2.indexOf("order"));
    
    StringBuffer sql=new StringBuffer(" select ").append(FilterFieldCostantXDtProd.FD_DIVISIONE).append(
                ", sum(NUMPEZZI) as ").append(FilterFieldCostantXDtProd.FD_NUMPEZZI).append(
              " from ( ").append(qry1).append("\n   union  \n ").append(qry2).append(
                    " ) a ").append(
              " where 1=1 ").append(
              " group by ").append(FilterFieldCostantXDtProd.FD_DIVISIONE);
    
    
    
    return sql.toString();
  }
  
}
