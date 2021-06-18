/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.bi;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che verifica gli articoli che sono stati movimentati ma che non hanno un contratto
 * 
 * @author lvita
 */
public class QryCtrlArticoliSupply extends CustomQuery{
  
   public final static String FLIBAS400="FLIBAS400"; 
  
  @Override
  public String toSQLString() throws QueryException {
    
    String library=(String) getFilterValue(FLIBAS400);
    
    StringBuilder s=new StringBuilder();
    s.append("select * from ( select distinct j39soc,j39art ").append(
                              " from ").append(library).append(".zjrsf39   exception join  ").append(
                            "  ( select j36soc,j36art from ").append(library).append(".zjrsf36 where 1=1) a ").append(
                            " on j39soc=a.j36soc and j39art=a.j36art ) c \n");//.append(
//             "  exception join  ").append(
//             " (select j36soc,j36art from ").append(library).append(".zjrsf36 where j36csc<>'COMMESSA') b ").append(
//                     " on c.j39soc=b.j36soc and c.j39art=b.j36art");
    
    
    
    return s.toString(); //To change body of generated methods, choose Tools | Templates.
  }
  
  
}
