/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi.old;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.dtProd.sfridi.CalcCostiSfridoArticoliMvx;
import colombini.query.produzione.R1.QuerySfridoImpImaXCosti;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class CostiSfridoArticoliImpImaR1 {
  
  
  private String cdLavoro;
  private Integer annoRif;
  private Integer meseRif;

  
  public CostiSfridoArticoliImpImaR1(String cdLavoro, Integer annoRif, Integer meseRif) {
    this.cdLavoro = cdLavoro;
    this.annoRif = annoRif;
    this.meseRif = meseRif;
  }
  
  
  public List getDatiSfridoImpiantiIma() throws SQLException, QueryException{
    List lista=new ArrayList();
    Connection conSQl=getConnectionSqlServer();
    QuerySfridoImpImaXCosti qry=new QuerySfridoImpImaXCosti();
    qry.setFilter(QuerySfridoImpImaXCosti.ANNO, annoRif);
    qry.setFilter(QuerySfridoImpImaXCosti.MESE, meseRif);
    qry.setFilter(QuerySfridoImpImaXCosti.STABILIMENTO, CostantsColomb.ROVERETA1);
    qry.setFilter(QuerySfridoImpImaXCosti.CDL, cdLavoro);
    
    String piano=CostantsColomb.PIANO0;
    if(CalcCostiSfridoArticoliMvx.OTMR1P1.equals(cdLavoro))
      piano=CostantsColomb.PIANO1;
    
    qry.setFilter(QuerySfridoImpImaXCosti.PIANO, piano);
    
    String q=qry.toSQLString();
    _logger.info("Esecuzione query:"+q);
    ResultSetHelper.fillListList(conSQl, q, lista);
    
   
            
    return lista;        
  }
  
  
  
  
  private Connection getConnectionSqlServer() throws SQLException{
    if((CalcCostiSfridoArticoliMvx.OTMR1P0).equals(cdLavoro))
      return ColombiniConnections.getDbImaTopConnection();
    else
      return ColombiniConnections.getDbImaAnteConnection();
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(CostiSfridoArticoliImpImaR1.class); 
  
}
