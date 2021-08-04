/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.exception.OEEException;
import colombini.indicatoriOee.calc.CalcIndicatoriOeeGg;
import colombini.indicatoriOee.utils.OeeUtils;
import colombini.model.persistence.tab.IndicatoriOeeGraf;
import colombini.model.persistence.tab.IndicatoriOeeSett;
import colombini.util.InfoMapLineeUtil;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class ElabCalcOee extends ElabClass {
 
  public final static String CALCSETT="WEEK"; //lancia schedualazione consuntivo settimanale
  public final static String CALCGG="DAY";    //lancia schedualazione calcolo giornaliero
  public final static String CALCGRAF="GRAF";  //lancia generazione grafico in base alle date fornite
  
  public final static String PTIPOCALCOLO="TIPOCALC";
  public final static String PLINEELAB="LINEELAB";
  public final static String PRICALCOLOSETT="RICALSETT";
  public final static String PINVIOMAIL="MAIL";
  public final static String PALLGGSETT="ALLGGWK"; //parametro per considerare tutti i giorni della settimana nel consolidamento settimanale
  
  public final static String STRETTP2="STRETTP2";
  
  
  private String tipoCalc;
  private String stringLineeElab; //stringa che può indicare lo stabilimento e il piano da calcolare oppure i nomi delle codiciLinee separati da ","
  private Date dataInizio;
  private Date dataFine;
  private Boolean ricalcoloSett=Boolean.FALSE;
  private Boolean invioMail=Boolean.FALSE;
  private Boolean includeAllDaysWk=Boolean.FALSE;
  //
  private Map<String,String> lineeOee;

  private Map mapErrors=null;
  private Map mapWarns=null;
  
  
  
  public ElabCalcOee(){
    super();
    this.mapErrors=new HashMap<String,List<String>>();
    this.mapWarns=new HashMap<String,List<String>>();
    
  }
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(NameElabs.ELBCOEEDAY.equals(this.getInfoElab().getCode()))
      this.tipoCalc=CALCGG;
    else if(NameElabs.ELBCOEEWEEK.equals(this.getInfoElab().getCode()))
      this.tipoCalc=CALCSETT;
    else if(NameElabs.ELBCOEEGRAF.equals(this.getInfoElab().getCode()))
      this.tipoCalc=CALCGRAF;
    
    
    if(tipoCalc==null || tipoCalc.isEmpty()){
      _logger.error("Tipo Calcolo OEE non specificato  . Impossibile proseguire");
      return Boolean.FALSE;
    }
    
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    if(parameter.get(PLINEELAB)!=null){
      this.stringLineeElab=ClassMapper.classToString(parameter.get(PLINEELAB));   
    }  
    if(parameter.get(PRICALCOLOSETT)!=null){
      this.ricalcoloSett=ClassMapper.classToClass(parameter.get(PRICALCOLOSETT),Boolean.class);
    }
    if(parameter.get(PINVIOMAIL)!=null){
      this.invioMail=ClassMapper.classToClass(parameter.get(PINVIOMAIL),Boolean.class);
    }
    if(parameter.get(PALLGGSETT)!=null){
      this.includeAllDaysWk=ClassMapper.classToClass(parameter.get(PALLGGSETT),Boolean.class);
    }
    
    
    if(ricalcoloSett==null)
      ricalcoloSett=Boolean.FALSE;
    
    if(invioMail==null)
      invioMail=Boolean.FALSE;
  
    
    return Boolean.TRUE;
  }

  
  private void msgInfoElab(){
    StringBuilder msg=new StringBuilder("Parametri Elaborazione --> ");
    msg.append("\n +tipoCalcolo:").append(tipoCalc);
    msg.append("\n +dataInizio:").append(dataInizio==null ? "" : dataInizio.toString());
    msg.append("   +dataFine:").append(dataFine==null ? "" : dataFine.toString());
    msg.append("\n +lineeElaborazione:").append(StringUtils.isEmpty(stringLineeElab) ? "" : stringLineeElab); 
    msg.append("\n +ricalcoloSettimana:").append(ricalcoloSett); 
    msg.append(" +invioMail:").append(invioMail); 
    
    addInfo(msg.toString());
    System.out.println(msg);
    
  }
  

  /**
   * Torna la mappa che contiene tutte le Linee per cui è attivo il calcolo OEE  <nomeLinea-StabPiano>
   * @return Map<String,String>  contiene <nomeLinea-StabPiano>
   */
  public Map<String, String> getLineeOee() {
    return lineeOee;
  }
  
  
  /**
   * L'elaborazine prevede 3 fasi 
   * 1)Calcolo Indicatori Oee Gg
   * 2)Eventuale consolidamento della settimana
   * 3)Popolazione tabella per grafici 
   * 4)invio segnalazioni
   * @param con
   */
  @Override
  public void exec(Connection con) {
    PersistenceManager pm=null;
    msgInfoElab();
    
    _logger.info("--------------- INIZIO ELABORAZIONE CALCOLO INDICATORI OEE ---------------");
    
    try{
      pm=new PersistenceManager(con);
      lineeOee=InfoMapLineeUtil.getMapLineeOee();
      Map lineeElab=getLineeToElab();
      _logger.info("Linee in elaborazione : "+lineeElab);

      if(CALCSETT.equals(tipoCalc) || CALCGG.equals(tipoCalc) ){
        if(CALCGG.equals(tipoCalc)){
          _logger.info(" ... Calcolo giornaliero ...");
          elabIndicatoriOeeGg(pm,lineeElab);
          if(ricalcoloSett){
            _logger.info(" ... Ricalcolo settimanale ...");
            elabIndicatoriOeeSett(con, lineeElab);
          }
        } else{  
          _logger.info(" ... Consolidamento settimana ...");
          elabIndicatoriOeeSett(con,lineeElab);
        }
        //elaborazione dei grafici standard (le date prese sono quelle della settimana corrente)
        elabGraficiIndicatoriOee(con, lineeElab);
      }
      //se viene richiesta l'elaborazione diretta dei grafici
      // vengono prese come date di elaborazione quelle fornite da riga di comando
      //se non presenti allora ritorniamo alla graficazione standard
      if(CALCGRAF.equals(tipoCalc)){
        if(dataInizio!=null && dataFine!=null)
          elabGraficiIndicatoriOee(con, lineeElab,dataInizio,dataFine);  
        else
          elabGraficiIndicatoriOee(con, lineeElab);
      }
    } finally{ 
      if(pm!=null)
        pm=null;
      _logger.info("--------------- FINE ELABORAZIONE CALCOLO INDICATORI OEE ---------------");
    }
  }
  
 
   
  private Map getLineeToElab(){
     //se la stringa relativa alla codiciLinee lavorative da elaborare è vuota allora elaboriamo tutte le codiciLinee.
     if(StringUtils.isEmpty(stringLineeElab)){
       return lineeOee;
     }else{
       if(stringLineeElab.length()<5){
         return getLineeToElabFromRc();
       }else{
         if(stringLineeElab.contains(",")){
           return getLineeToElab(ArrayUtils.getListFromArray(stringLineeElab.split(",")));    
         }else{
           Map lineeToElab=new HashMap();
           String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(stringLineeElab);
           if(!StringUtils.IsEmpty(nomeLinea))
             lineeToElab.put(nomeLinea, lineeOee.get(nomeLinea));
           
           return lineeToElab;
         }
       }
       
     }
   } 
   
  /**
    * Data una lista di linee torna la mappa di linee contenente <nomeLinea-stab:piano>
    * @param List linee
    * @return Map linee
    */
  private Map getLineeToElab(List<String> linee){
    Map lineeToElab=new HashMap();
    
    for(String codLinea:linee){
      String nomeLinea=InfoMapLineeUtil.getNomeLineaFromCodice(codLinea);
      if(!StringUtils.IsEmpty(nomeLinea))
        lineeToElab.put(nomeLinea, lineeOee.get(nomeLinea));
    }
    
    return lineeToElab;
  }
   
  /**
   * Torna la mappa delle linee da elaborare in base alla stringa fornita da riga di comando
   * @return Map
   */
  private Map getLineeToElabFromRc() {
    Map lineeToElab=new HashMap();
    Boolean onlyStab=Boolean.FALSE;
    Set keys=lineeOee.keySet();
    Iterator iter=keys.iterator();
    
    if(stringLineeElab.length()<=2){
      onlyStab=Boolean.TRUE;
    }
    while(iter.hasNext()){
      String key=(String) iter.next();
      String posLinea=(String) lineeOee.get(key);
      if(onlyStab){
        String stab=posLinea.substring(0, 2);
        if(stringLineeElab.contains(stab))
          lineeToElab.put(key,posLinea);
      }else{
        if(stringLineeElab.equals(posLinea))
          lineeToElab.put(key,posLinea);
      }
    }
   
            
    return lineeToElab;        
  }
  
  
  
   /**
   * 
   */
   private void elabIndicatoriOeeGg(PersistenceManager pm,Map lineeElab) {
     List giorni=getListGiorniElab();
     
     CalcIndicatoriOeeGg calcGg=new CalcIndicatoriOeeGg(giorni, lineeElab);
     calcGg.execute(pm);
     mapErrors.putAll(calcGg.getMapErrorsElab());
     mapWarns.putAll(calcGg.getMapWarningsElab());


//     if(checkStabilimentoLinee(CostantsColomb.ROVERETA1, lineeElab)){
//       CalcIndicatoriOeeGgR1 calcOeeR1=new CalcIndicatoriOeeGgR1(giorni, lineeElab);
//       calcOeeR1.execute(pm);
//       mapErrors.putAll(calcOeeR1.getMapErrorsElab());
//       mapWarns.putAll(calcOeeR1.getMapWarningsElab());
//     }  

     
   }
   
   
 /**
  * Lancia il consolidamento settimanale degli Oee per la codiciLinee specificate.
  * Se le codiciLinee non sono indicate lancia il calcolo per tutte le codiciLinee attive.
  * Se la data inizio non è indicata allora lancia il consolidamento togliendo 7 gg dalla data di 
  * sistema.
  * @param con
  * @param lineeElab 
  */
  private void elabIndicatoriOeeSett(Connection con,Map lineeElab){
    
    //se entrambe le date sono espresse allora devo calcolare più settimane
    if(dataInizio!=null && dataFine!=null){
      Date dataRifSett=dataInizio;
      Date dataFineSett=new Date();
      
      if(dataFine.before(dataFineSett))
        dataFineSett=dataFine;
      
      Integer settElab=DateUtils.getWorkWeek(dataRifSett);
      Integer settFine=DateUtils.getWorkWeek(dataFineSett);
//      Integer settCorrente=DateUtils.getWorkWeek(new Date());
      //verifichiamo sempre che la settimana di elaborare non sia quella corrente.
//      while(settElab<=settFine && settElab<settCorrente){
      while(settElab<=settFine &&  dataRifSett.compareTo(new Date())<0){
        consolidamentoSett(con, lineeElab, dataRifSett);
        dataRifSett=DateUtils.addDays(dataRifSett, 7);
        settElab=DateUtils.getWorkWeek(dataRifSett);
      }   
    } else {
    
      Date dataRif=null; 
      if(dataInizio!=null)
        dataRif=dataInizio;
      
      consolidamentoSett(con, lineeElab, dataRif);
    }
  }
  
  
  private void elabGraficiIndicatoriOee(Connection con ,Map lineeElab){
    Date dataIni=null;
    Date dataFin=null;
    
    Date oggi=DateUtils.RemoveTime(new Date());
    GregorianCalendar gc= new GregorianCalendar();
    gc.setTime(oggi);
    int gg=gc.get(GregorianCalendar.DAY_OF_WEEK);    
   
    //se il giorno è lunedì,grafichiamo la settimana precedente
    if(GregorianCalendar.MONDAY==gg){
      dataIni=DateUtils.addDays(oggi, -7);
    }else if(GregorianCalendar.TUESDAY==gg){
      dataIni=DateUtils.addDays(oggi, -1);
    }else if(GregorianCalendar.WEDNESDAY==gg){
      dataIni=DateUtils.addDays(oggi, -2);
    }else if(GregorianCalendar.THURSDAY==gg){
      dataIni=DateUtils.addDays(oggi, -3);
    }else if(GregorianCalendar.FRIDAY==gg){
      dataIni=DateUtils.addDays(oggi, -4);
    }else if(GregorianCalendar.SATURDAY==gg){
      dataIni=DateUtils.addDays(oggi, -5);
    }else if(GregorianCalendar.SUNDAY==gg){
      dataIni=DateUtils.addDays(oggi, -6);
    }
   
   dataFin=DateUtils.addDays(dataIni,5);
    
   
   elabGraficiIndicatoriOee(con, lineeElab, dataIni, dataFin);
  }
  
  
  
  private void elabGraficiIndicatoriOee(Connection con ,Map lineeElab,Date dataIni,Date dataFin){ 
    _logger.info(" ... Elaborazione per grafici ...");
    IndicatoriOeeGraf oeeG=new IndicatoriOeeGraf();
   
    Set keys=lineeElab.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String nomeLinea=(String) iter.next();
      String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
      try{
        _logger.info("Graficazione Oee linea : "+codLinea+" periodo : "+dataIni +" - "+dataFin);
        
        if(NomiLineeColomb.STRETTOIOMAW.equals(nomeLinea) || NomiLineeColomb.STRETTOIOPRIESS.equals(nomeLinea)){
          String filtroS="'"+InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOMAW)+"','"+InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.STRETTOIOPRIESS)+"'";
          oeeG.elabDatiGraficoLinea(con, STRETTP2,filtroS, dataIni, dataFin);
        }else{
          oeeG.elabDatiGraficoLinea(con, codLinea,null, dataIni, dataFin);
        }
        

      } catch(SQLException s){
        _logger.error("Errori in fase di generazione dati per grafici Oee "+codLinea+" -->"+s.toString());
        addErrorToMap(codLinea, " Errori in fase di generazione dati per grafici Oee "+" -->"+s.toString());
      }
    }
   
  }
  
  
  private void addErrorToMap(String cdL,String error){
    List errs=new ArrayList();
    if(mapErrors.containsKey(cdL)){
      errs=(List) mapErrors.get(cdL);
    }
    errs.add(error); 
    mapErrors.put(cdL,errs);
  }
  
  
  private void consolidamentoSett(Connection con ,Map lineeElab,Date dataRif) {
    Integer settRif=DateUtils.getWorkWeek(dataRif);
    Map infoSett=null;
    try{
      infoSett=OeeUtils.getInstance().getInfoSett(con,dataRif,includeAllDaysWk);
    } catch(OEEException e){
      _logger.error(e.getMessage());
       
    }
    if(infoSett==null || infoSett.isEmpty())
      return;
    
    _logger.info(" ... Consolidamento settimana n."+settRif+" ...");
    IndicatoriOeeSett oeeS=new IndicatoriOeeSett();

    Set keys=lineeElab.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String nomeLinea=(String) iter.next();
      String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
      try{
        if(NomiLineeColomb.COMBIMA1R1P1.equals(nomeLinea)|| NomiLineeColomb.COMBIMA2R1P1.equals(nomeLinea)||
            NomiLineeColomb.SCHELLING1R1P1.equals(nomeLinea) ||   NomiLineeColomb.SCHELLING2R1P1.equals(nomeLinea)  )
          oeeS.consolidamentoDatiSett(con, infoSett, codLinea,false);
        else
          oeeS.consolidamentoDatiSett(con, infoSett, codLinea,true);
      
      } catch(SQLException s){
        _logger.error("Errori in fase di consolidamento dei dati di Oee per la linea "+codLinea+" -->"+s.getMessage());
        addErrorToMap(codLinea," Problemi in fase di consolidamento dei dati di Oee   -->>"+s.toString()); 
      }
     
    }
  }
  
  
  
  public  Boolean checkStabilimentoLinee(String stabilimento,Map linee){
      Boolean trovato=Boolean.FALSE;
      Set keys=linee.keySet();
      Iterator iter=keys.iterator();
      while(iter.hasNext() && !trovato){
        String linea=(String) iter.next();
        String posizione=lineeOee.get(linea);
        if(posizione.contains(stabilimento))
          trovato=Boolean.TRUE;
      }
      
      return trovato;
   }
   
   
   private List<Date> getListGiorniElab(){
     List giorni=new ArrayList();
     Date giornoCorrente=new  Date();
     Date datatmp=null;
     //controllo se la data passata è cmq superiore al gg corrente
     if(dataInizio!=null && dataInizio.compareTo(giornoCorrente)>0)
       dataInizio=DateUtils.addDays(giornoCorrente, -1);

     if(dataFine!=null && dataFine.compareTo(giornoCorrente)>0)
        dataFine=DateUtils.addDays(giornoCorrente, -1);

     if(dataInizio==null && dataFine==null){
       dataInizio=DateUtils.addDays(giornoCorrente, -1);
       dataFine=dataInizio;
     }else if(dataInizio!=null && dataFine==null ){
       dataFine=dataInizio;
     }

     //costruisco l'array dei giorni da processare
     datatmp=dataInizio;
     while (datatmp.compareTo(dataFine)<=0){
       giorni.add(datatmp);
       datatmp=DateUtils.addDays(datatmp, 1);
     }
     
     
     return giorni;
   }
   
   @Override
  public List<MailMessageInfoBean> getListMailsErrorMessage(Connection con) {
    if(!invioMail)
      return null;
    
      
    if(mapErrors.isEmpty() && mapWarns.isEmpty()){
      _logger.info("Nessun messaggio di errore e warning presente.");
      return null;
    }
    Map lineeElab=getLineeToElab();
    List mails=new ArrayList();
    MailMessageInfoBean b=null;
    
    b=getMailMessage(con,CostantsColomb.GALAZZANO, CostantsColomb.PIANO0, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    b=getMailMessage(con,CostantsColomb.GALAZZANO, CostantsColomb.PIANO2, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    b=getMailMessage(con,CostantsColomb.ROVERETA1, CostantsColomb.PIANO0, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    b=getMailMessage(con,CostantsColomb.ROVERETA1, CostantsColomb.PIANO1, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    b=getMailMessage(con,CostantsColomb.ROVERETA1, CostantsColomb.PIANO2, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    b=getMailMessage(con,CostantsColomb.ROVERETA2, CostantsColomb.PIANO1, lineeElab);
    if(b!=null){
      mails.add(b);
    }
    
    return mails;
    
  }
  
  

  private MailMessageInfoBean getMailMessage(Connection con, String stab,String piano,Map lineeElab){
    MailMessageInfoBean mailM=null;
    String codMailMessage=NameElabs.MESSAGE_ELABOEEGENERIC+stab+piano;
    String textMail=getMessage(stab, piano, lineeElab);
    if(!StringUtils.isEmpty(textMail)){
      mailM=new MailMessageInfoBean(codMailMessage);
      mailM.retriveInfo(con);
      mailM.setText(textMail);
    }
    
    
    return mailM;
  }
  
  
  private String getMessage(String stab,String piano,Map lineeElab){
    List<String> linee=InfoMapLineeUtil.getListLinee(stab,piano, lineeElab);
    StringBuilder msgTot=new StringBuilder();
    for(String nomeLinea:linee){
      String codLinea=InfoMapLineeUtil.getCodiceLinea(nomeLinea);
      StringBuilder msg=new StringBuilder();
      List<String> errors=(List<String>) mapErrors.get(codLinea);
      List<String> warns=(List<String>) mapWarns.get(codLinea);
      if(errors!=null && !errors.isEmpty()){
        msg.append("\n -- ERRORI Elaborazione Indicatori Oee: \n");
        for(String error:errors){
           msg.append(" - ").append(error).append("\n"); 
        }
      }
      if(warns!=null && !warns.isEmpty()){
        msg.append("\n -- ALLARMI Elaborazione Indicatori Oee: \n");
        for(String warn:warns){
           msg.append(" - ").append(warn).append("\n"); 
        }
      }
      if(msg.length()>5){
        msgTot.append("\n --# LINEA ").append(nomeLinea).append(" - ").append(codLinea).append(msg).append("\n  --# \n");
      }
    }
    
    return msgTot.toString();
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabCalcOee.class);

  
  
}



