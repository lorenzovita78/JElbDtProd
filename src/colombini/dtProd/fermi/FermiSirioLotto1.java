/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.fermi;

import colombini.conn.ColombiniConnections;
import colombini.model.datiProduzione.IFermiLinea;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.datiProduzione.InfoTurniCdL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class FermiSirioLotto1 implements IFermiLinea{

  private final static String LOTTO1A="01084A";
  private final static String LOTTO1B="01084B";
  private final static String LOTTO1C="01084C";
  
  private final static String LOTTO1IMPIANTOA="1";
  private final static String LOTTO1IMPIANTOB="2";
  private final static String LOTTO1IMPIANTOC="3";
  
  public List<InfoFermoCdL> getListFermiLinea(InfoTurniCdL infoTCdl, Map causaliFermi) {
    Connection conDbLotto1= null;
    List fermi=new ArrayList();
    try{
      conDbLotto1=ColombiniConnections.getDbLotto1Connection();
      String cdl=infoTCdl.getCdl();
      
      
    } catch(SQLException s) {
      
    }finally{
      if(conDbLotto1!=null)
        try{
          conDbLotto1.close();
        }catch(SQLException s) {
          _logger.error("Errore in fase di chiusura della connessione con db Lotto 1 "+s.getMessage());
        }    
    }
    return fermi;
  }
  
  
  
  private String getCodImpianto(String codLinea){
    if(LOTTO1A.equals(codLinea))
      return LOTTO1IMPIANTOA;
    else if(LOTTO1B.equals(codLinea))
      return LOTTO1IMPIANTOB;
    else if (LOTTO1C.equals(codLinea))
      return LOTTO1IMPIANTOC;
    
    return "";
  }
  
  private static final Logger _logger = Logger.getLogger(FermiSirioLotto1.class); 

  @Override
  public List<InfoFermoCdL> getListFermiLinea(Connection con, InfoTurniCdL infoTCdl, Map causaliFermi) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  
}
