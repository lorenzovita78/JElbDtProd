/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryCommToShip extends CustomQuery {

  public final static String NOCHECKGGLAV="NOCHECKGGLAV";
  
  public final static String ORDERBYDATADESC="ORDERBYDATADESC";
  
  
  @Override
  public String toSQLString() throws QueryException {
    
    String select=" SELECT zccdld , zcccom  ";
    
    StringBuffer s=new StringBuffer(
                                   "\n FROM MCOBMODDTA.zcomme inner join MVXBDTA.csycal  ").append(
                                        " on zccono=cdcono and zccdld=cdymd8 ").append(
               "\n WHERE 1=1 ").append(
               
               "\n and cddivi='' ").append(          
                 " and ZCCONO = ").append(getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA));
    
    if(isFilterPresent(FilterQueryProdCostant.FTDATARIF))
      s.append(" and ZCCDLD = ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATARIF));
    else if(isFilterPresent(FilterQueryProdCostant.FTDATAFIN))
      s.append(" and ZCCDLD <= ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN));
    //nel caso cerchiamo solo il numero della commessa principale della giornata filtriamo per la tipologia sulla zcomme
    if(isFilterPresent(FilterQueryProdCostant.FTONLYCOMM)){
      s.append("\n and zcmccd=0 " );
    }else if(isFilterPresent(FilterQueryProdCostant.FTONLYMINICOMM)){
      s.append("\n and zcmccd=1 " );
    }else if(isFilterPresent(FilterQueryProdCostant.FTONLYNANOCOMM)){
      s.append("\n and zcmccd=2 " );
    } else {  
      //se ci interessano tutte le commesse indipendentemente dal tipo allora aggiungo 
      //la tipologia nella select per poterle distinguere
      select+=" , zcmccd";
    }
    
    
    if(!isFilterPresent(NOCHECKGGLAV)){
      s.append("\n and cdwdpc=100" );
      //s.append("\n and cdwdpc=0" ); //Per casi speciali dove la data commessa è spostata
    }
    
    
    if(isFilterPresent(ORDERBYDATADESC)){
        s.append(" order By ZCCDLD desc");
    }
    
    return select + s.toString();
  }
  
  
  
}
