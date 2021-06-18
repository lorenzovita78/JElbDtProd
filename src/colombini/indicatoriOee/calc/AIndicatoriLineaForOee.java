/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.calc;

import colombini.model.CausaliLineeBean;
import colombini.model.persistence.DatiProdPWGeneral;
import colombini.model.persistence.IndicatoriOeeGgBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import utils.DateUtils;
import utils.MapUtils;

/**
 *
 * @author lvita
 */
public abstract class AIndicatoriLineaForOee extends DatiProdPWGeneral implements ICalcIndicatoriOeeLinea{
  
  private IndicatoriOeeGgBean ioeeBean=null;
  
  private Boolean turniStd=null;
  
  public AIndicatoriLineaForOee(Integer azienda, Date dataRif, String cdL) {
    //istanzio l'oggeto per i dati di produzione
    super(azienda, dataRif, cdL);
    //istanzio il bean degli indicatori Oee
    ioeeBean=new IndicatoriOeeGgBean(azienda, cdL, dataRif);
    
    this.turniStd=Boolean.TRUE;
  }
  
  public AIndicatoriLineaForOee(Integer azienda, Date dataRif, String cdL,Boolean trnStd) {
    //istanzio l'oggeto per i dati di produzione
    super(azienda, dataRif, cdL);
    //istanzio il bean degli indicatori Oee
    ioeeBean=new IndicatoriOeeGgBean(azienda, cdL, dataRif);
    
    this.turniStd=trnStd;
  }
  
  
  
  
  @Override
  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
    
    loadIndicatoriLinea(con, parameter);
    
    return ioeeBean;
  }

  
  public IndicatoriOeeGgBean getIoeeBean() {
    return ioeeBean;
  }

  public Boolean getTurniStd() {
    return turniStd;
  }

  public void setTurniStd(Boolean turniStd) {
    this.turniStd = turniStd;
  }
  
  
  
  
  private void loadIndicatoriLinea(Connection con,Map parameter) {
    try{
      super.retriveDataCdl(con);
      
      if(getInfoTCdl().getIdTurno()==null){
        ioeeBean.addWarning("Turni non presenti per la giornata indicata .Elaborazione interrotta");
        return;
      }
      
      Date dataInP=getInfoTCdl().getMinOraInizio();
      Date dataFnP=getInfoTCdl().getMaxOraFine();
      
      if(turniStd){
        Date dataInStd=getOraIniStd(getInfoTCdl().getDataRif());
        Date dataFnStd=getOraFinStd(getInfoTCdl().getDataRif());
      
        if(dataInP.before(dataInStd))
          dataInP=dataInStd;
      
        if(dataFnP.after(dataFnStd))
          dataFnP=dataFnStd;
      }
      setDatiForIndicatori(dataInP,dataFnP);
      //chiamata al metodo astratto che deve essere reimplementato per ciascuna linea
      elabDatiOee(con,dataInP,dataFnP,parameter);
      
    } catch(SQLException s){
      ioeeBean.addError("Errore in fase di generazione degli Indicatori Oee per linea "+getInfoTCdl().getCdl()+
              " giorno "+getInfoTCdl().getDataRif() +" -->"+ s.getMessage());
    } catch (ParseException ex) {
      ioeeBean.addError("Errore in fase di generazione degli Indicatori Oee per linea "+getInfoTCdl().getCdl()+
              " giorno "+getInfoTCdl().getDataRif() +" -->"+ ex.getMessage());
    } 
    
  }

  
  private void setDatiForIndicatori(Date dtInF,Date dtFnF) {
    ioeeBean.setTDispImp(Long.valueOf(this.getMinProdImpianto(dtInF, dtFnF)*60));
    Map fermiGg=this.getMapTpFermiSec(dtInF, dtFnF,ioeeBean.getCdLav());
    
    ioeeBean.setTImprodImp(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_ORENONPROD, Long.class));
    ioeeBean.setTGuasti(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class));
    ioeeBean.setTSetup(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class));
    ioeeBean.setTMicrofermi(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_MICROFRM, Long.class));
    ioeeBean.setTPerditeGest(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class));
    ioeeBean.setTVelRidotta(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_VELRID, Long.class));
    ioeeBean.setTScarti(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_SCARTO, Long.class));
    ioeeBean.setTRilavorazioni(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_RIPASSI, Long.class));
    //verificare se va bene aggiungere i ripassi alle lavorazioni
    ioeeBean.addTRilavorazioni(MapUtils.getNumberFromMap(fermiGg, CausaliLineeBean.TIPO_CAUS_RECUPERI, Long.class));
    
    ioeeBean.setNPzTot(Long.valueOf(getInfoTCdl().getNumPzTot()));
    ioeeBean.setNGuasti(Long.valueOf(this.getNumFermi()));
    
  }

  
  
  /**
   * Torna l'inizio del turno della mattina : ore 6:00
   * @param data
   * @return
   * @throws ParseException 
   */
  public Date getOraIniStd(Date data) throws ParseException{
    return DateUtils.getInizioTurnoM(data);
  }
  
  
  /**
   * Torna la fine del turno del pomeriggio : ore 20:30
   * @param data
   * @return
   * @throws ParseException 
   */
  public Date getOraFinStd(Date data) throws ParseException{
    return DateUtils.getFineTurnoP(data);
  }

  
  
  
  public  abstract void elabDatiOee(Connection con,Date dataIni,Date dataFin,Map parameter);
  
  

  
  
}
