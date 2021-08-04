/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.R1P2;

import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.indicatoriOee.utils.CalcoloTempoCiclo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class IndicatoriStrettoiMawPriess extends AIndicatoriLineaForOee {

  public IndicatoriStrettoiMawPriess(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    List colliGg=(List) parameter.get(ICalcIndicatoriOeeLinea.LISTCOLLI);
    try {
      getIoeeBean().setTRun(getTempoRunTime(con, colliGg, getIoeeBean().getCdLav()));
    } catch (SQLException ex) {
      getIoeeBean().addError("Problemi di accesso al db per calcolare il tempo di runtime");
    }
  }
  
  public Long getTempoRunTime(Connection con,List colliGg,String cdLavoro) throws SQLException  {
    Double tcicloT=Double.valueOf(0);
    Long tRun=Long.valueOf(0);
    
    if(colliGg!=null){
      CalcoloTempoCiclo ct=new CalcoloTempoCiclo(cdLavoro);
      tcicloT=ct.getTempoCiclo(con, colliGg,null);
      tRun=tcicloT.longValue();
//        tVelRidotta=ct.gettRilavorazioni().longValue();
     
    }
     
    
    return tRun;
    
  }
  
  
//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    XlsStrettoiPriessMaw xls=(XlsStrettoiPriessMaw) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermi=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    List colliGg=(List) parameter.get(ICalcIndicatoriOeeLinea.LISTCOLLI);
//    
//    IndicatoriOeeGgBean ioee=new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    ioee.setNPzTot(Long.valueOf(colliGg.size()));
//    
//    Integer rigaIni=xls.getRigaInizioGg(data);
//    Integer rigaFin=xls.getRigaFineGg(rigaIni);
//    Date dtInizio=xls.getInizioTurno(data, rigaIni, rigaFin);
//    Date dtFine=xls.getFineTurno(data, rigaIni, rigaFin);
//    
//    if(dtInizio==null || dtFine==null){
//      ioee.addError(" Turni di lavoro  non specificati.");
//      return ioee;
//    }
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data)*60);
//    ioee.setTMicrofermi(xls.getTempoMicrofermi(rigaIni, rigaFin)*30);
//    
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermi, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    try {
//      ioee.setTRun(getTempoRunTime(con, colliGg, centrodiLavoro));
//    } catch (SQLException ex) {
//      ioee.addError("Problemi di accesso al db per calcolare il tempo di runtime");
//    }
//    
//    
//    return ioee;
//  }
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriStrettoiMawPriess.class);

  
  
}
