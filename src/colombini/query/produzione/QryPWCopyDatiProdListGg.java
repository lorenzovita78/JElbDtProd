/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;
import java.util.ArrayList;
import utils.ArrayUtils;

/**
 *
 * @author lvita
 */
public class QryPWCopyDatiProdListGg extends CustomQuery{

  public final static String FIELDS="COCONO,COPLGR,CODTRF,COT1IN,COT1FN,COT1NO,"
          + "                        COT1OT,COT1MTCOT2IN,COT2FN,COT2NO,COT2OT,COT2MT,COT3IN,COT3FN,COT3NO,"
          + "                        COT3OT,COT3MT,COT4IN,COT4FN,COT4NO,COT4OT,COT4MT";
  @Override
  public String toSQLString() throws QueryException {
    
    String query="Select "+ FIELDS+" \n"
            + " from mcobmoddta.zdpwco "
            + "where codtrf="+getFilterSQLValue(FilterQueryProdCostant.FTDATARIF);
    
    return query;
  }

  public ArrayList<String> getFields() {
    return (ArrayList<String>) ArrayUtils.getListFromArray(FIELDS.split(","));
  }
  
  
  
  
  
  
}
