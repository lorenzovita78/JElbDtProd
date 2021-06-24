/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.R1;

import as400.Utility400;
import colombini.conn.ColombiniConnections;
import colombini.model.CausaliLineeBean;
import colombini.model.datiProduzione.IFermiLinea;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.datiProduzione.InfoTurniCdL;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class FermiLotto1R1P4 implements IFermiLinea {

  
  private final static String LOTTO1A="01084A";
  private final static String  LOTTO1A_IMP="1";
  private final static String LOTTO1B="01084B";
  private final static String  LOTTO1B_IMP="2";
  private final static String LOTTO1C="01084C";
  private final static String  LOTTO1C_IMP="3";
  
  @Override
  public List<InfoFermoCdL> getListFermiLinea(Connection con,InfoTurniCdL infoTCdl, Map causaliFermi) {
    List<InfoFermoCdL> listFBeans=new ArrayList();
    List<List> listF=new ArrayList();
    List<List> listMF=new ArrayList();
    
    try {
     
      listF=getListFermi(infoTCdl.getDataRif(), infoTCdl.getCdl(),Boolean.TRUE);
      listMF=getListFermi(infoTCdl.getDataRif(), infoTCdl.getCdl(),Boolean.FALSE);
      if(listF!=null){
        if(listMF!=null)
          listF.addAll(listMF);
      }
      for(List fermo:listF){
        InfoFermoCdL fermoBean=new InfoFermoCdL(infoTCdl.getIdTurno(), infoTCdl.getCdl(), infoTCdl.getDataRif());
        String codCaus=ClassMapper.classToString(fermo.get(1));
        CausaliLineeBean c=null;
        
        if(codCaus!=null){
           c=(CausaliLineeBean) causaliFermi.get(codCaus);
        }else{
           c=getCausMF(causaliFermi);
        }
        
        Date dataIni=ClassMapper.classToClass(fermo.get(2), Date.class);
        Date dataFin=ClassMapper.classToClass(fermo.get(3), Date.class);
        String note=ClassMapper.classToString(fermo.get(4));
        fermoBean.loadIdFermo(con);
        fermoBean.setIdCausale(c.getIdCausale().intValue());
        fermoBean.setOraInizio(dataIni);
        fermoBean.setOraFine(dataFin);
        fermoBean.setNote(note);
        //fermoBean.
        listFBeans.add(fermoBean);
      }
      

      
      
    } catch (SQLException ex) {
     // throws new ElabException(ex.getMessage());
    } catch (ParseException ex) {
      
    }
    
    return listFBeans;
  }
  
  
  private CausaliLineeBean getCausMF(Map causaliFermi) {
    CausaliLineeBean cb=null;
    Long idCausMf=null;
    
    Iterator cf = causaliFermi.entrySet().iterator();
        while(cf.hasNext() && idCausMf==null){
          cb  = (CausaliLineeBean)cf.next();
          if(cb.getTipo().equals(CausaliLineeBean.TIPO_CAUS_MICROFRM) ) 
              continue;
        }
        
    return cb;    
  }
  
  
  private List getListFermi(Date dataRif,String cdl,Boolean isFermo) throws SQLException, ParseException{
    Connection con =null;
    List lfermi=new ArrayList();
    
    try{
      con=ColombiniConnections.getDbLotto1Connection();
      ResultSetHelper.fillListList(con, getQueryListFermi(dataRif,cdl,isFermo), lfermi);
     
    }finally{
      if(con!=null){
        try{
        con.close();
        } catch(SQLException s){
          _logger.warn("Errore in fase di chiusura della connessione "+s.getMessage());
        }
      }
    }
    return lfermi;
    
  }
  
  
  
  private String getQueryListFermi(Date dataRif,String cdl,Boolean isFermo) throws ParseException{
    StringBuffer s= new StringBuffer();
    String dataS= DateUtils.DateToStr(dataRif, "yyyy-MM-dd");
    
    String select = "SELECT idCau , Cod  ,[DatOraIni] , [DatOraFin], [Note]";
    
    if(!isFermo)
        select =" SELECT idCau, Cod, min(convert(time,(datOraIni))) ,dateadd(ss,sum(datediff(ss,datOraIni,datOraFin)),note";
    
    s.append( select  ).append(
             "  FROM [dbo].[LOG_FERMI] a left outer join CAUSALI_FERMI b ON idCau=b.id\n").append(
             "  where convert(date,(datOraIni))= ").append(JDBCDataMapper.objectToSQL(dataS)).append(
             " and A.Imp=").append(JDBCDataMapper.objectToSQL(getCodiceImp(cdl)));        
    
    if(isFermo)
        s.append(" and Cod is not null");
    else 
        s.append(" and Cod is null");
    
    return s.toString();
    
  }
  

  private String getCodiceImp(String cdl){
    if(LOTTO1A.equals(cdl))
      return LOTTO1A_IMP;
    else if(LOTTO1B.equals(cdl))
      return LOTTO1B_IMP;
    else if(LOTTO1C.equals(cdl))
      return LOTTO1C_IMP;
    
    return "";
  }
  
  
  private static final org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(FermiLotto1R1P4.class); 
  
}
