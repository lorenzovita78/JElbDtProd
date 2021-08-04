/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryMsgMiddlewareVDL extends CustomQuery{

  public final static String FT_STATUSMESSAGE="FT_STATUSMESSAGE";
  public final static String FT_OBJECTTYPE="FT_OBJECTTYPE";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuffer str=new StringBuffer("SELECT  messageid,status,messageobjecttype,insertts,completionts,rejectmessageid,rejectinformation  ").append(
                                      " FROM h2v_queue ").append(
                                      " WHERE 1=1");
     
    if(isFilterPresent(FT_STATUSMESSAGE)){
      str.append(" and status=").append(getFilterSQLValue(FT_STATUSMESSAGE));
    }
    
    if(isFilterPresent(FT_OBJECTTYPE)){
      str.append(" and MessageObjectType=").append(getFilterSQLValue(FT_OBJECTTYPE));
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATADA)){
      str.append(" and completionts>=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATADA));
    }
    
    if(isFilterPresent(FilterFieldCostantXDtProd.FT_DATAA)){
      str.append(" and completionts<=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATAA));
    }
    
    return str.toString(); //To change body of generated methods, choose Tools | Templates.
  }

 
  
  
  
  
  
}
