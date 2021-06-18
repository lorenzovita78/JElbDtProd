/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.ResultSetHelper;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;


/**
 *
 * @author lvita
 */
public class ElabUpdDataAmmagamma extends ElabClass{

   private final String UPD_ZACLIE=" UPDATE AMMAGAMMA.ZACLIE  set ZEVAL1=? ,  ZEVAL2=? ,  ZEVAL3=? , " +
                                                      " ZEVAL4=? ,  ZEVAL5=? ,  ZEVAL6=? , ZEVAL7 =? "+
                                                       " ,  ZERGDT=? ,  ZERGTM=? , ZECHID=? "+
                                   " WHERE ZECUNO = ? ";
                                  
  private final String UPD_ZAWDAY=" UPDATE AMMAGAMMA.ZAWDAY  set ZWSCDB=? ,  ZWRGDT=? ,  ZWRGTM=? , ZWCHID=?  "+
                                   " WHERE ZWDATE = ? and ZWPDAY>0";
  
  private final String UPD_ZAWDAY_TEST=" UPDATE AMMAGAMMA.ZAWDAY  set   ZWRGDT=? ,  ZWRGTM=? , ZWCHID=?  "+
                                   " WHERE ZWDATE = ? ";
  public  Date dataRif=null;
  
  @Override
  public Boolean configParams() {
    Map parm=getInfoElab().getParameter();
    
    if(parm.get(ALuncherElabs.DATAINIELB)==null)
      return Boolean.FALSE;
    
    dataRif=(Date) parm.get(ALuncherElabs.DATAINIELB);
    
    
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    //aggiornamento anagrafica clienti
    //updZaclie(con);
    //aggiornamento calendario di fabbrica
    updZawday(con);
  }

  private void updZaclie(Connection con) {
    List<List> lCli=new ArrayList();
     try {
       ResultSetHelper.fillListList(con, getQryCli(), lCli);
       for(List tmpCli:lCli){
         InfoCli cliTmp=null;
         cliTmp=new InfoCli(ClassMapper.classToString(tmpCli.get(0)));
         cliTmp.setShipD1(ClassMapper.classToClass(tmpCli.get(1),Integer.class));
         cliTmp.setShipD2(ClassMapper.classToClass(tmpCli.get(2),Integer.class));
         cliTmp.setShipD3(ClassMapper.classToClass(tmpCli.get(3),Integer.class));
         cliTmp.setShipD4(ClassMapper.classToClass(tmpCli.get(4),Integer.class));
         cliTmp.setShipD5(ClassMapper.classToClass(tmpCli.get(5),Integer.class));
         cliTmp.setShipD6(ClassMapper.classToClass(tmpCli.get(6),Integer.class));
         cliTmp.setShipD7(ClassMapper.classToClass(tmpCli.get(7),Integer.class));
         
         _logger.info("Aggiornamento cliente "+cliTmp.getCodCLi());
         updCli(con, cliTmp);
         
       }
     } catch (SQLException ex) {
       _logger.error("Errore in fase di lettura dei dati relativi a i clienti -->"+ex.getMessage());
       addError("Errore in fase di lettura dei dati relativi a i clienti -->"+ex.getMessage());
     }
    
  }
  
  private void updCli(Connection con, InfoCli bcli) {
    PreparedStatement ps = null;
    try{
      ps=con.prepareStatement(UPD_ZACLIE); 
      Long dataN=DateUtils.getDataForMovex(new Date());
      Integer oraN=DateUtils.getOraForMovex(new Date());
      
      ps.setInt(1, bcli.getShipD1());
      ps.setInt(2, bcli.getShipD2());
      ps.setInt(3, bcli.getShipD3());
      ps.setInt(4, bcli.getShipD4());
      ps.setInt(5, bcli.getShipD5());
      ps.setInt(6, bcli.getShipD6());
      ps.setInt(7, bcli.getShipD7());
      
      ps.setLong(8, dataN);
      ps.setInt(9, oraN);
      ps.setString(10, "JAVA_ELB");
      
      ps.setString(11, bcli.getCodCLi());
      ps.execute();

    } catch(SQLException s){
      _logger.error("Errore in fase di aggiornamento del cliente  "+bcli.getCodCLi() + "-->"+s.getMessage());
      addError("Errore in fase di aggiornamento del cliente "+bcli.getCodCLi() + "-->"+s.toString());
    } finally{
      try{
      if(ps!=null)
        ps.close();
      } catch(SQLException s){
        _logger.error("Errore in fase di chiusura dello statment del cliente "+bcli.getCodCLi()+" --> "+s.getMessage());
      }
    }  
    
  }
  
  private void updWorkDay(Connection con, Date dataRif) {
    PreparedStatement ps = null;
    try{
      ps=con.prepareStatement(UPD_ZAWDAY); 
      Long dataN=DateUtils.getDataForMovex(new Date());
      Integer oraN=DateUtils.getOraForMovex(new Date());
      
      ps.setInt(1, Integer.valueOf(1));
      ps.setLong(2, dataN);
      ps.setInt(3, oraN);
      ps.setString(4, "JAVA_ELB");
      
      ps.setDate(5, new java.sql.Date( dataRif.getTime()));
      ps.execute();

    } catch(SQLException s){
      _logger.error("Errore in fase di aggiornamento del giorno "+dataRif + "-->"+s.getMessage());
      addError("Errore in fase di aggiornamento del giorno "+dataRif + "-->"+s.toString());
    } finally{
      try{
      if(ps!=null)
        ps.close();
      } catch(SQLException s){
        _logger.error("Errore in fase di chiusura dello statment  --> "+s.getMessage());
      }
    }  
    
  }
  
  private void updZawday(Connection con) {
    Date dcurrent=new Date();
    if(DateUtils.daysBetween(dataRif, dcurrent)==0)
      return;
    _logger.info("Aggironamento data su calendario di fabbrica "+dataRif);
    updWorkDay(con,dataRif);
    
  }
  
  
  
  private String getQryCli(){
    String qry=" select okcuno,OKVAL1,OKVAL2,OKVAL3,OKVAL4,OKVAL5,OKVAL6,OKVAL7 from MCOBMODDTA.ZORSCA where OKCONO=30 and OKFLGS='S'";
    return qry;
  }
  
  
  private class InfoCli{
    private String codCLi;
    private Integer shipD1;
    private Integer shipD2;
    private Integer shipD3;
    private Integer shipD4;
    private Integer shipD5;
    private Integer shipD6;
    private Integer shipD7;
    
    public InfoCli(String codCli){
      this.codCLi=codCli;
    }

    public String getCodCLi() {
      return codCLi;
    }


    public Integer getShipD1() {
      return shipD1;
    }

    public void setShipD1(Integer shipD1) {
      this.shipD1 = shipD1;
    }

    public Integer getShipD2() {
      return shipD2;
    }

    public void setShipD2(Integer shipD2) {
      this.shipD2 = shipD2;
    }

    public Integer getShipD3() {
      return shipD3;
    }

    public void setShipD3(Integer shipD3) {
      this.shipD3 = shipD3;
    }

    public Integer getShipD4() {
      return shipD4;
    }

    public void setShipD4(Integer shipD4) {
      this.shipD4 = shipD4;
    }

    public Integer getShipD5() {
      return shipD5;
    }

    public void setShipD5(Integer shipD5) {
      this.shipD5 = shipD5;
    }

    public Integer getShipD6() {
      return shipD6;
    }

    public void setShipD6(Integer shipD6) {
      this.shipD6 = shipD6;
    }

    public Integer getShipD7() {
      return shipD7;
    }

    public void setShipD7(Integer shipD7) {
      this.shipD7 = shipD7;
    }
    
    
    
    
  }
  
    private static final Logger _logger = Logger.getLogger(ElabUpdDataAmmagamma.class);
}
