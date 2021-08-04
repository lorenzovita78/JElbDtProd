/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P0;


import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.AIndicatoriLineaForOee;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.query.indicatoriOee.QueryColliCommessaMovex;
import colombini.util.InfoMapLineeUtil;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class IndicatoriPantografi extends AIndicatoriLineaForOee{
  
  private final static Double TEMPORUNPZVITALITY=Double.valueOf(80.00);
  
  private final static Double TEMPORUNPZARTEC=Double.valueOf(30.00);
  
  public final static String CAUSLAV1CENTRO="1C - Lavoro con 1 centro";
  
  public static final String ARTICOLICLI="[TP1547,TP1548]";
  public static final String LINEALOGICA="6128";
  
  public static final String MAPPERCENTUALI="MAPPERCENTUALI";
  public static final String MAPNSAGARTICOLI="MAPNSAGARTICOLI";

  public IndicatoriPantografi(Integer azienda, Date dataRif, String cdL) {
    super(azienda, dataRif, cdL,Boolean.FALSE);
  }
  
  
  @Override
  public void elabDatiOee(Connection con, Date dataIni, Date dataFin, Map parameter) {
    Map percentuali=(Map) parameter.get(IndicatoriPantografi.MAPPERCENTUALI);
    Map sagomeArti=(Map) parameter.get(IndicatoriPantografi.MAPNSAGARTICOLI);
    if(percentuali==null || percentuali.isEmpty()){
      getIoeeBean().addError("Mappa percentuali vuota impossibile calcolare Oee");
      return ;
    }
    
    getIoeeBean().addTGuasti(getTempoGuastiLav1Centro(this.getFermiGg()));
    //valido per centri 01043 e 01044
    //getIoeeBean().addTSetup(xls.getTempoCambioBordiGg(data)*60);
    //solo per 01044
    //getIoeeBean().setTMicrofermi(xls.getTempoMicrofermiGg(data) *60);
    
    try {
      if(getIoeeBean().getTProdImpianto()>0){
        ArrayList datiBit=CalcDtProdPantografi.getInstance().loadDatiFromBitaBit(getIoeeBean().getCdLav(),dataIni,dataFin );
      
        if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493).equals(getIoeeBean().getCdLav()))
          loadRichiesteClientiCommessa(con, datiBit);

        Map info=CalcDtProdPantografi.getInstance().calcolaDatiProdPantografo(con,datiBit, getIoeeBean().getCdLav(),percentuali,sagomeArti);

        if(info!=null && info.size()>0){      
          getIoeeBean().setTRun(ClassMapper.classToClass(info.get(CostantsColomb.TRUNTIME), Long.class));
          getIoeeBean().setTRun2(ClassMapper.classToClass(info.get(CostantsColomb.TRUN2), Long.class));
          getIoeeBean().setTRilavorazioni(ClassMapper.classToClass(info.get(CostantsColomb.TRILAVORAZIONI), Long.class));
          getIoeeBean().setTScarti(ClassMapper.classToClass(info.get(CostantsColomb.TSCARTI), Long.class));

          getIoeeBean().setNPzTot(ClassMapper.classToClass(info.get(CostantsColomb.NPZTOT), Long.class));
          getIoeeBean().setNScarti(ClassMapper.classToClass(info.get(CostantsColomb.NSCARTI), Long.class));
          
          getIoeeBean().addWarnings((List) info.get(ICalcIndicatoriOeeLinea.LISTWARNINGS));
        }

        //setup per cambi famiglie 01044 e 01046
        getIoeeBean().addTSetup(getTempoSetupCambioFamiglia(datiBit,getIoeeBean().getCdLav()));
      
      }
     //pz Vitality x 01045
     //DA VERIFICARE!!!!!!!
     if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(getIoeeBean().getCdLav()) ||
        InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(getIoeeBean().getCdLav())     ){
       Double runtmVitality=getInfoTCdl().getNumClTot()*TEMPORUNPZVITALITY;
       getIoeeBean().addTRun(runtmVitality.longValue());
      }
     if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(getIoeeBean().getCdLav()) ){
       Double runtimeGole=getInfoTCdl().getNumPileBandeTot()*TEMPORUNPZARTEC;
       getIoeeBean().addTRun( runtimeGole.longValue());
       
     }
    } catch (OEEException ex) {
      _logger.error("Problemi di accesso ai dati del db -->"+ex.getMessage());
      getIoeeBean().addError("Problemi nel reperimento dei dati di produzione");
      
    } catch (SQLException ex) {
      _logger.error("Problemi di accesso ai dati del db -->"+ex.getMessage());
      getIoeeBean().addError("Problemi di accesso al database");
    }
    
    
  }

  
  /**
   * Se presente la causale LAVORO CON 1 CENTRO il tempo guasti viene scalato per la metà del valore della causale
   * @param fermiGG 
   */
  private Long getTempoGuastiLav1Centro(List <InfoFermoCdL>fermiList){
    Long value=Long.valueOf(0);
    
    // Modifica del 09/05/2014   --> La causale viene utilizzata ora per tutti i pantografi
    //    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(cdLavoro) || 
    //       InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(cdLavoro)     ) {  
      
      if(fermiList!=null && !fermiList.isEmpty()){
        String causale="";
        for(InfoFermoCdL fermo : fermiList){
          causale=fermo.getDescCausale().trim();
          if(CAUSLAV1CENTRO.equals(causale)){  
             value+=fermo.getSec();
          }
        } 
      }
      if(value>0){
        value=value/2;
      }

    return -value;
    
  }
  
//  /**
//   * Se presente la causale LAVORO CON 1 CENTRO il tempo guasti viene scalato per la metà del valore della causale
//   * @param fermiGG 
//   */
//  private Long getTempoGuastiLav1Centro(List <List>fermiList,String cdLavoro){
//    Long value=Long.valueOf(0);
//    
//    // Modifica del 09/05/2014   --> La causale viene utilizzata ora per tutti i pantografi
//    //    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(cdLavoro) || 
//    //       InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(cdLavoro)     ) {  
//      
//      if(fermiList!=null && !fermiList.isEmpty()){
//        String causale="";
//        for(List record : fermiList){
//          causale=ClassMapper.classToString(record.get(0));
//          if(XlsPantografiStd.CAUSLAV1CENTRO.equals(causale)){  
//             value+=ClassMapper.classToClass(record.get(1), Long.class);
//          }
//        } 
//      }
//      if(value>0){
//        value=(value*60)/2;
//      }
////    }
//    return -value;
//    
//  }
      
  
  
  
  /**
   * Aggiunge alla lista dati le informazioni relative agli articoli su richiesta dei clienti per una data 
   * commessa
   * @param Connection con
   * @param List dati info di bit@bit
   * @throws OEEException 
   */
  public void loadRichiesteClientiCommessa(Connection con, ArrayList dati) throws OEEException {
    if(dati!=null && dati.size()>0){
      Integer commessa=getCommessaFromBitaBit(dati);
      List artCli=loadArticoliRichiestaClienti(con, commessa);
      if(artCli!=null && artCli.size()>0){
        for(int i=0;i<artCli.size();i++){
          List record =(List) artCli.get(i);
          record.add(new Long(0)); //pzRilavorati
          record.add(new Long(0));//pzScarti
          record.add("SETUP");
          record.add(commessa);
          dati.add(record);
        }           
      }
    }
  }

  

   private Integer getCommessaFromBitaBit(ArrayList dati){
     Integer ncomm=Integer.valueOf(Integer.MAX_VALUE);
     if(dati==null || dati.isEmpty())
       return null;
     
     for(Object record:dati){
//       Integer ncommTmp=  ClassMapper.classToClass(((List) record).get(3),Integer.class);
       Integer ncommTmp=  ClassMapper.classToClass(((List) record).get(5),Integer.class);
       if(ncommTmp!=null && ncommTmp<ncomm){
         ncomm=ncommTmp;
       }  
     }
     
     return ncomm;
   }
  
   private List loadArticoliRichiestaClienti(Connection con,Integer commessa) throws OEEException{
     List record=new ArrayList();
     QueryColliCommessaMovex qry=new QueryColliCommessaMovex();
     qry.setFilter(QueryColliCommessaMovex.COMMESSA, commessa);
     qry.setFilter(QueryColliCommessaMovex.ARTICOLI, ARTICOLICLI);
     qry.setFilter(QueryColliCommessaMovex.LINEALOG, LINEALOGICA);
     qry.setFilter(QueryColliCommessaMovex.RAGGRARTI, "Y");
     try {
       ResultSetHelper.fillListList(con, qry.toSQLString(), record);
       
     } catch (QueryException ex) {
       _logger.error("Impossibile caricare i dati relativi agli articoli clienti richiesti:"+ex.getMessage());
       throw new OEEException(ex);
     } catch (SQLException ex) {
       _logger.error("Impossibile caricare i dati relativi agli articoli clienti richiesti:"+ex.getMessage());
       throw new OEEException(ex);
     }
     
     return record;
   }
 
  
  /**
   * Metodo che introduce un ulteriore tempo di setup per ogni cambio famiglia di articoli
   * @param List info dati dal db di datibit
   * @return Long tempo in secondi
   */
  protected Long getTempoSetupCambioFamiglia(List <List>datibit,String cdLavoro){
    Long numCambio=getNumCambioFamiglie(datibit);
    Double valsetup=getSecSetupCambioFamiglia(cdLavoro);
    
    Double value=numCambio*valsetup;
    _logger.info("Setup Cambio Famiglia --> numero cambi:"+numCambio +" - tempoTot:"+value.toString());
    
    return value.longValue();
  }
  
  /**
   * Metodo che torna il numero di cambi di famiglie di setup in base ai dati forniti
   * @param List datibit lista contenente le informazioni reperite da bit a bit
   * @return Long numero di cambi
   */
  private Long getNumCambioFamiglie(List<List> datibit) {
    Long cambioF=Long.valueOf(0);
    if(datibit==null || datibit.isEmpty())
      return cambioF;
    
    String famigliaOld="";
    for(List record:datibit){
//      String famTmp=ClassMapper.classToString(record.get(2));
      String famTmp=ClassMapper.classToString(record.get(4));
      if(famTmp!=null && !famTmp.equals(famigliaOld))
        cambioF++;
      
      famigliaOld=famTmp;
    }
    
    return cambioF;
  }
  
  /**
   * Torna il valore in secondi del tempo stimato per un cambio di famiglia
   * --> valido solo per 01044 e 01046
   * @return Double
   */
  protected Double getSecSetupCambioFamiglia(String cdLavoro) {
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493).equals(cdLavoro)){
      return Double.valueOf("24.26");
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(cdLavoro)){
      return Double.valueOf("446.50");
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(cdLavoro)){
      return Double.valueOf("215.89");
    }
    
    return Double.valueOf(0);
  }
  
  
  
  public String getInfoLog(Date data){
    String log="\n TVELRID-> Tempo rilavorazioni ripassi  ;"
        ;
                
    return log;
  }
  
 

//  @Override
//  public IIndicatoriOeeGg getIndicatoriOeeLineaGg(Connection con, Date data, String centrodiLavoro, Map parameter) {
//    IndicatoriOeeGgBean ioee =new IndicatoriOeeGgBean(CostantsColomb.AZCOLOM, centrodiLavoro, data);
//    XlsPantografiStd xls=(XlsPantografiStd) parameter.get(ICalcIndicatoriOeeLinea.FILEXLS);
//    Map fermiTot=(Map) parameter.get(ICalcIndicatoriOeeLinea.MAPTOTFERMI);
//    Map percentuali=(Map) parameter.get(IndicatoriPantografi.MAPPERCENTUALI);
//    Map sagomeArti=(Map) parameter.get(IndicatoriPantografi.MAPNSAGARTICOLI);
//    
//    List fermiList=(List)parameter.get(ICalcIndicatoriOeeLinea.LISTFERMIOEE);
//    
//    Date dtInizio=xls.getInizioTurnoGg(data);
//    Date dtFine=xls.getFineTurnoGg(data);
//    if(dtInizio==null || dtFine==null){
//      ioee.addError(" Turni di lavoro  non specificati per linea . ");
//      return ioee;
//    }
//    
//    ioee.setTDispImp(xls.getMinutiProdImpianto(data)*60);
//    ioee.setTImprodImp(xls.getMinutiImprodImpianto(data)*60);
//    
//    ioee.setTPerditeGest(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_PERDGEST, Long.class)*60);
//    ioee.setTSetup(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_SETUP, Long.class)*60);
//    ioee.setTGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_CAUS_GUASTO, Long.class)*60);
//    ioee.setNGuasti(MapUtils.getNumberFromMap(fermiTot, CausaliLineeBean.TIPO_NGUASTI, Long.class));
//    
//    ioee.addTGuasti(getTempoGuastiLav1Centro(fermiList, centrodiLavoro));
//    //valido per centri 01043 e 01044
//    ioee.addTSetup(xls.getTempoCambioBordiGg(data)*60);
//    //solo per 01044
//    ioee.setTMicrofermi(xls.getTempoMicrofermiGg(data) *60);
//    
//    try {
//      if(ioee.getTProdImpianto()>0){
//        ArrayList datiBit=CalcDtProdPantografi.getInstance().loadDatiFromBitaBit(centrodiLavoro,dtInizio,dtFine );
//      
//        if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493).equals(centrodiLavoro))
//          loadRichiesteClientiCommessa(con, datiBit);
//
//        Map info=CalcDtProdPantografi.getInstance().calcolaDatiProdPantografo(con,datiBit, centrodiLavoro,percentuali,sagomeArti);
//
//        if(info!=null && info.size()>0){      
//          ioee.setTRun(ClassMapper.classToClass(info.get(CostantsColomb.TRUNTIME), Long.class));
//          ioee.setTRun2(ClassMapper.classToClass(info.get(CostantsColomb.TRUN2), Long.class));
//          ioee.setTRilavorazioni(ClassMapper.classToClass(info.get(CostantsColomb.TRILAVORAZIONI), Long.class));
//          ioee.setTScarti(ClassMapper.classToClass(info.get(CostantsColomb.TSCARTI), Long.class));
//
//          ioee.setNPzTot(ClassMapper.classToClass(info.get(CostantsColomb.NPZTOT), Long.class));
//          ioee.setNScarti(ClassMapper.classToClass(info.get(CostantsColomb.NSCARTI), Long.class));
//          
//          ioee.addWarnings((List) info.get(ICalcIndicatoriOeeLinea.LISTWARNINGS));
//        }
//
//        //setup per cambi famiglie 01044 e 01046
//        ioee.addTSetup(getTempoSetupCambioFamiglia(datiBit,centrodiLavoro));
//      
//      }
//     //pz Vitality x 01045
//     Double runtmVitality=xls.getNPzVitalityGg(data)*TEMPORUNPZVITALITY;
//     ioee.addTRun(runtmVitality.longValue());
//    
//    } catch (OEEException ex) {
//      _logger.error("Problemi di accesso ai dati del db -->"+ex.getMessage());
//      ioee.addError("Problemi nel reperimento dei dati di produzione");
//      
//    } catch (SQLException ex) {
//      _logger.error("Problemi di accesso ai dati del db -->"+ex.getMessage());
//      ioee.addError("Problemi di accesso al database");
//    }
//    
//    return ioee;
//  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriPantografi.class);

  
}

