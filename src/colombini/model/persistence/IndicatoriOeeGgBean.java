/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;



import colombini.costant.CostantsColomb;
import colombini.indicatoriOee.calc.IIndicatoriOeeGg;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.DateUtils;
import utils.StringUtils;

/**
 * Bean che rappresenta i dati OEE giornalieri di una linea (utilizzata anche per la persistenza
 * @author lvita
 */
public class IndicatoriOeeGgBean implements IIndicatoriOeeGg {

  
  
  
  public final static String LIBRARY="MCOBMODDTA";
  public final static String TABLEOEEGG="ZIOEED";
  public final static String TABLEOEEGGFIELDS="ZOCONO,ZOPLGR,ZODATA,ZODTRN,ZOTTOT,ZOTDII,ZOTIMI,ZOTPRI,ZOTGUA,ZOTSET,ZOTSE2,ZOTPGE,ZOTMIF,ZOTVRI,ZOTRIL,ZOTSCA,ZOTSC2,ZOTRUN,ZOTRN2,ZOTRN3,ZOTEX1,ZOTEX2,ZOTEX3,ZOTPNR,ZONPZT,ZONPZP,ZONGUA,ZONSCA,ZONRIP,ZONBND,ZONCCL,ZOPOEE,ZOTEEP,ZOMTBF,ZOMTTR,ZORGDT";
  public final static String TABLEOEEGGKEYFIELDS="ZOCONO,ZOPLGR,ZODTRF";
  
  public final static String FLDZOCONO="ZOCONO";
  public final static String FLDZOPLGR="ZOPLGR";
  public final static String FLDZODATA="ZODATA";
  public final static String FLDZODTRF="ZODTRN";
  public final static String FLDZOTTOT="ZOTTOT";
  public final static String FLDZOTDII="ZOTDII";
  public final static String FLDZOTIMI="ZOTIMI";
  public final static String FLDZOTPRI="ZOTPRI";
  public final static String FLDZOTGUA="ZOTGUA";
  public final static String FLDZOTSET="ZOTSET";
  public final static String FLDZOTSE2="ZOTSE2";
  public final static String FLDZOTPGE="ZOTPGE";
  public final static String FLDZOTMIF="ZOTMIF";
  public final static String FLDZOTVRI="ZOTVRI";
  public final static String FLDZOTRIL="ZOTRIL";
  public final static String FLDZOTSCA="ZOTSCA";
  public final static String FLDZOTSC2="ZOTSC2";
  public final static String FLDZOTRUN="ZOTRUN";
  public final static String FLDZOTRN2="ZOTRN2";
  public final static String FLDZOTRN3="ZOTRN3";
  public final static String FLDZOTEX1="ZOTEX1";
  public final static String FLDZOTEX2="ZOTEX2";
  public final static String FLDZOTEX3="ZOTEX3";
  public final static String FLDZOTPNR="ZOTPNR";
  public final static String FLDZONPZT="ZONPZT";
  public final static String FLDZONPZP="ZONPZP";
  public final static String FLDZONGUA="ZONGUA";
  public final static String FLDZONSCA="ZONSCA";
  public final static String FLDZONRIP="ZONRIP";
  public final static String FLDZONBND="ZONBND";
  public final static String FLDZONCCL="ZONCCL";
  public final static String FLDZOPOEE="ZOPOEE";
  public final static String FLDZOTEEP="ZOTEEP";
  public final static String FLDZOMTBF="ZOMTBF";
  public final static String FLDZOMTTR="ZOMTTR";
  public final static String FLDZORGDT="ZORGDT";
  

  
  protected Integer azienda;
  protected String cdLavoro;
  protected Date data;
  protected Long dataRifN;
  
  protected Long tDispImp;
  protected Long tImprodImp;
  
  
  protected Long tGuasti;
  protected Long tSetup;
  protected Long tSetup2;
  protected Long tPerditeGest;
  protected Long tVelRidotta;
  protected Long tMicrofermi;
  protected Long tRilavorazioni;
  protected Long tScarti;
  protected Long tScarti2;
  
  
  protected Long tRun;
  protected Long tRun2;
  protected Long tRun3;
  
  protected Long tExt1;
  protected Long tExt2;
  protected Long tExt3;
  
  
  protected Long nPzTot;
  protected Long nPzTurni;
  protected Long nGuasti;
  protected Long nScarti;
  protected Long nRilav;
  protected Long nCicliLav;
  protected Long nBande;

  
  protected List<String> warnings;
  
  protected List<String> errors;
  
  
  //date che rappresentano l'inizio e la fine del periodo di lavoro
  protected Date dataInizioT;
  protected Date dataFineT;
  
  
  
  
  protected final void initialize(){
    azienda=Integer.valueOf(0);    
    cdLavoro="";    
    dataRifN=Long.valueOf(0);      
                 
    tDispImp=Long.valueOf(0);      
    tImprodImp=Long.valueOf(0);    
               
    tGuasti=Long.valueOf(0);       
    tSetup=Long.valueOf(0);        
    tSetup2=Long.valueOf(0);       
    tPerditeGest=Long.valueOf(0);  
    tVelRidotta=Long.valueOf(0);   
    tMicrofermi=Long.valueOf(0);   
    tRilavorazioni=Long.valueOf(0);
    tScarti=Long.valueOf(0);       
    tScarti2=Long.valueOf(0);      
               
    tExt1=Long.valueOf(0);       
    tExt2=Long.valueOf(0);       
    tExt3=Long.valueOf(0);       
               
    tRun=Long.valueOf(0);          
    tRun2=Long.valueOf(0);        
    tRun3=Long.valueOf(0);
               
    nPzTot=Long.valueOf(0);      
    nPzTurni=Long.valueOf(0);  
    nGuasti=Long.valueOf(0);   
    nScarti=Long.valueOf(0);   
    nRilav=Long.valueOf(0);     
    nCicliLav=Long.valueOf(0);  
    nBande=Long.valueOf(0);
    
    warnings=new ArrayList();
    errors=new ArrayList();
  }
  
  
  public IndicatoriOeeGgBean(Integer azienda,String cdL,Date data){
    initialize();
    this.azienda=azienda;
    this.cdLavoro=cdL;
    this.data=data;
    
    this.dataRifN=DateUtils.getDataForMovex(data);
  }
  
  
  
  @Override
  public Integer getAzienda() {
    return azienda;
  }

  @Override
  public String getCdLav() {
    return cdLavoro;
  }

  @Override
  public Date getData() {
    return data;
  }

  
  @Override
  public Long getDataRifN() {
    return dataRifN;
  }

  @Override
  public Long getTTotImpianto() {
    return new Long(24*3600);
  }

  @Override
  public Long getTDispImpianto() {
    return tDispImp;
  }

  @Override
  public Long getTImprodImpianto() {
    return tImprodImp;
  }

  @Override
  public Long getTProdImpianto() {
    return tDispImp-tImprodImp;
  }

  @Override
  public Long getTGuasti() {
    return tGuasti;
  }

  @Override
  public Long getTSetup() {
    return tSetup;
  }

  @Override
  public Long getTSetup2() {
    return tSetup2;
  }

  @Override
  public Long getTPerditeGestionali() {
    return tPerditeGest;
  }

  @Override
  public Long getTMicroFermi() {
    return tMicrofermi;
  }

  @Override
  public Long getTVelocitaRidotta() {
    return tVelRidotta;
  }

  @Override
  public Long getTRilavorazioni() {
    return tRilavorazioni;
  }

  @Override
  public Long getTScarti() {
    return tScarti;
  }

  @Override
  public Long getTScarti2() {
    return tScarti2;
  }


  @Override
  public Long getTRun() {
    return tRun;
  }

  @Override
  public Long getTRun2() {
    return tRun2;
  }

  @Override
  public Long getTRun3() {
      return tRun3;
  }

  @Override
  public Long getTExt1() {
    return tExt1;
  }

  @Override
  public Long getTExt2() {
    return tExt2;
  }

  @Override
  public Long getTExt3() {
    return tExt3;
  }

  
  
  @Override
  public Long getTPerditeNnRilevate() {
    Long tProd=getTProdImpianto();
    
    Long tappo=tGuasti+tSetup+tSetup2+tPerditeGest+tVelRidotta+tMicrofermi+tRilavorazioni+tScarti+tScarti2+tExt1+tExt2+tExt3+tRun+tRun2+tRun3;
    
    return tProd-tappo;
  }

  @Override
  public Long getNumPzTot() {
    return nPzTot;
  }

  @Override
  public Long getNumPzTurni() {
    return nPzTurni;
  }

  @Override
  public Long getNumGuasti() {
    return nGuasti;
  }

  @Override
  public Long getNumScarti() {
    return nScarti;
  }

  @Override
  public Long getNumRilavorazioni() {
    return nRilav;
  }

  @Override
  public Long getNumBande() {
    return nBande;
  }

  @Override
  public Long getNumCicliLav() {
    return nCicliLav;
  }

  @Override
  public Double getOee() {
    Long prod=getTProdImpianto();
    if(prod<=0){
      _logger.error("TEMPO PRODUZIONE IMPIANTO = 0 . Impossibile calcolare dati OEE");
      return Double.valueOf(0);
    }
    
    return  tRun/new Double(prod);
  }

  @Override
  public Double getTeep() {
    return  tRun/new Double(getTTotImpianto());
  }

  @Override
  public Double getMtbf() {
    if(nGuasti==0)
      return Double.valueOf(0);
    
    Double val=tRun/new Double(nGuasti);
    
    return val;
  }

  @Override
  public Double getMttr() {
    if(nGuasti==0)
      return Double.valueOf(0);
    
    Double val=tGuasti/new Double(nGuasti);
    
    return val;
  }

  @Override
  public Long getNumeroScarti() {
    return nScarti;
  }

  @Override
  public Long getNumeroCicliLav() {
    return nCicliLav;
  }

  
   @Override
  public List<String> getWarnings() {
    return warnings;
  }

  @Override
  public List<String> getErrors() {
    return errors;
  }


  
  public void setAzienda(Integer azienda) {
    this.azienda = azienda;
  }

  public void setCdLavoro(String cdLavoro) {
    this.cdLavoro = cdLavoro;
  }

  public void setDataRifN(Long dataRifN) {
    this.dataRifN = dataRifN;
  }

  public void setNBande(Long nBande) {
    this.nBande = nBande;
  }

  public void setNCicliLav(Long nCicliLav) {
    this.nCicliLav = nCicliLav;
  }

  public void setNGuasti(Long nGuasti) {
    this.nGuasti = nGuasti;
  }

  public void setNPzTot(Long nPzTot) {
    this.nPzTot = nPzTot;
  }

  public void setNPzTurni(Long nPzTurni) {
    this.nPzTurni = nPzTurni;
  }

  public void setNRilav(Long nRilav) {
    this.nRilav = nRilav;
  }

  public void setNScarti(Long nScarti) {
    this.nScarti = nScarti;
  }

  public void setTDispImp(Long tDispImp) {
    this.tDispImp = tDispImp;
  }

  public void setTExt1(Long tExt1) {
    this.tExt1 = tExt1;
  }

  public void setTExt2(Long tExt2) {
    this.tExt2 = tExt2;
  }

  public void setTExt3(Long tExt3) {
    this.tExt3 = tExt3;
  }

  public void setTGuasti(Long tGuasti) {
    this.tGuasti = tGuasti;
  }

  public void setTImprodImp(Long tImprodImp) {
    this.tImprodImp = tImprodImp;
  }

  public void setTMicrofermi(Long tMicrofermi) {
    this.tMicrofermi = tMicrofermi;
  }

  public void setTPerditeGest(Long tPerditeGest) {
    this.tPerditeGest = tPerditeGest;
  }

  public void setTRilavorazioni(Long tRilavorazioni) {
    this.tRilavorazioni = tRilavorazioni;
  }

  public void setTRun(Long tRun) {
    this.tRun = tRun;
  }

  public void setTRun2(Long tRun2) {
    this.tRun2 = tRun2;
  }

  public void setTRun3(Long tRun3) {
    this.tRun3 = tRun3;
  }

  public void setTScarti(Long tScarti) {
    this.tScarti = tScarti;
  }

  public void setTScarti2(Long tScarti2) {
    this.tScarti2 = tScarti2;
  }

  public void setTSetup(Long tSetup) {
    this.tSetup = tSetup;
  }

  public void setTSetup2(Long tSetup2) {
    this.tSetup2 = tSetup2;
  }

  public void setTVelRidotta(Long tVelRidotta) {
    this.tVelRidotta = tVelRidotta;
  }

  private Long getValue(Long tempo){
    if(tempo==null) 
      return Long.valueOf(0);
    
    return tempo;
  }
  
  
  public void addTDispImp(Long tDispImp) {
    this.tDispImp += getValue(tDispImp);
  }
  
  public void addTImprodImp(Long tImprodImp) {
    this.tImprodImp += getValue(tImprodImp);
  }

  public void addTGuasti(Long tGuasti) {
    this.tGuasti += getValue(tGuasti);
  }
  
  public void addTMicrofermi(Long tMicrofermi) {
    this.tMicrofermi += getValue(tMicrofermi);
  }

  public void addTPerditeGest(Long tPerditeGest) {
    this.tPerditeGest += getValue(tPerditeGest);
  }

  public void addTRilavorazioni(Long tRilavorazioni) {
    this.tRilavorazioni += getValue(tRilavorazioni);
  }

  public void addTRun(Long tRun) {
    this.tRun += getValue(tRun);
  }

  public void addTRun2(Long tRun2) {
    this.tRun2 += getValue(tRun2);
  }

  public void addTRun3(Long tRun3) {
    this.tRun3 += getValue(tRun3);
  }

  public void addTScarti(Long tScarti) {
    this.tScarti += getValue(tScarti);
  }

  public void addTScarti2(Long tScarti2) {
    this.tScarti2 += getValue(tScarti2);
  }

  public void addTSetup(Long tSetup) {
    this.tSetup += getValue(tSetup);
  }

  public void addTSetup2(Long tSetup2) {
    this.tSetup2 += getValue(tSetup2);
  }

  public void addTVelRidotta(Long tVelRidotta) {
    this.tVelRidotta += getValue(tVelRidotta);
  }
  
  public void addTExt1(Long tExt1) {
    this.tExt1 += getValue(tExt1);
  }

  public void addTExt2(Long tExt2) {
    this.tExt2 += getValue(tExt2);
  }

  public void addTExt3(Long tExt3) {
    this.tExt3 = getValue(tExt3);
  }
  
// -------------------------PERSISTENZA------------------------------------
  
  
   @Override
  public String getLibraryName() {
    return LIBRARY;
  }

  @Override
  public String getTableName() {
    return TABLEOEEGG;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABLEOEEGGFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABLEOEEGGKEYFIELDS.split(","));
  }
  
  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldValues=new HashMap();
    
    fieldValues.put(FLDZOCONO,CostantsColomb.AZCOLOM);
    fieldValues.put(FLDZOPLGR,this.cdLavoro);
    fieldValues.put(FLDZODATA,this.data);
    fieldValues.put(FLDZODTRF,this.dataRifN);
    fieldValues.put(FLDZOTTOT,this.getTTotImpianto());
    fieldValues.put(FLDZOTDII,this.tDispImp);
    fieldValues.put(FLDZOTIMI,this.tImprodImp);
    fieldValues.put(FLDZOTPRI,this.getTProdImpianto());
    fieldValues.put(FLDZOTGUA,this.tGuasti);
    fieldValues.put(FLDZOTSET,this.tSetup);
    fieldValues.put(FLDZOTSE2,this.tSetup2);
    fieldValues.put(FLDZOTPGE,this.tPerditeGest);
    fieldValues.put(FLDZOTMIF,this.tMicrofermi);
    fieldValues.put(FLDZOTVRI,this.tVelRidotta);
    fieldValues.put(FLDZOTRIL,this.tRilavorazioni);
    fieldValues.put(FLDZOTSCA,this.tScarti);
    fieldValues.put(FLDZOTSC2,this.tScarti2);
    fieldValues.put(FLDZOTRUN,this.tRun);
    fieldValues.put(FLDZOTRN2,this.tRun2);
    fieldValues.put(FLDZOTRN3,this.tRun3);
    fieldValues.put(FLDZOTEX1,this.tExt1);
    fieldValues.put(FLDZOTEX2,this.tExt2);
    fieldValues.put(FLDZOTEX3,this.tExt3);
    fieldValues.put(FLDZOTPNR,this.getTPerditeNnRilevate());
    fieldValues.put(FLDZONPZT,this.nPzTot);
    fieldValues.put(FLDZONPZP,this.nPzTurni);
    fieldValues.put(FLDZONGUA,this.nGuasti);
    fieldValues.put(FLDZONSCA,this.nScarti);
    fieldValues.put(FLDZONRIP,this.nRilav);
    fieldValues.put(FLDZONBND,this.nBande);
    fieldValues.put(FLDZONCCL,this.nCicliLav);
    fieldValues.put(FLDZOPOEE,this.getOee());
    fieldValues.put(FLDZOTEEP,this.getTeep());
    fieldValues.put(FLDZOMTBF,this.getMtbf());
    fieldValues.put(FLDZOMTTR,this.getMttr());
    fieldValues.put(FLDZORGDT,new Date());
    
    
    
    return fieldValues;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldValues=new HashMap();
    fieldValues.put(FLDZOCONO,this.azienda);
    fieldValues.put(FLDZOPLGR,this.cdLavoro);
    fieldValues.put(FLDZODATA,this.data);
    
    return fieldValues;
  }
  
 

  @Override
  public List<Integer> getFieldTypes() {
     List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //centro di lavoro
    types.add(Types.DATE); //data
    types.add(Types.NUMERIC); //dataRifN
    
    types.add(Types.INTEGER);    //tempi
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    
    types.add(Types.INTEGER);    //quantità
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    types.add(Types.INTEGER);
    types.add(Types.INTEGER);    
    
    types.add(Types.NUMERIC); //OEE
    types.add(Types.NUMERIC); 
    types.add(Types.NUMERIC); 
    types.add(Types.NUMERIC); 
    
    
    types.add(Types.TIMESTAMP); //data inserimento
    
    return types;
  }

  @Override
  public String toString() {
    return cdLavoro+" - "+dataRifN;
  }
  
  
  
  public Boolean checkDateTurni(Date dataInizioT,Date dataFineT){
    if(dataInizioT== null || dataFineT==null){
      addError("Turni di lavoro  non specificati per linea ");
      return Boolean.FALSE;
    }
    
    if(dataFineT.before(dataInizioT)){
      addWarning("Turni di lavoro non corretti o fuori dal range( 06:00-20:30) del calcolo");
      return Boolean.FALSE;
    }
    
    if(dataInizioT.after(dataFineT)){
      addWarning("Turni di lavoro non corretti o fuori dal range( 06:00-20:30) del calcolo");
      return Boolean.FALSE;
    }
    
    return Boolean.TRUE;
  }
  
  
  
  @Override
  public Boolean validate() {
    if(getTProdImpianto()<=0){
      addError(" Tempo produzione impianto = 0 ");
    }
    if(StringUtils.isEmpty(cdLavoro)){
      addError(" Linea di lavoro non indicata ");
    }  
    if(data==null || dataRifN<=0){
      addError(" Data di riferimento del calcolo non indicata ");
    }  
    
    if(!getErrors().isEmpty())
      return Boolean.FALSE; 
    
    // se ci sono errori non salvo il bean se ci sono warning si
    if(getTDispImpianto()>=(CostantsColomb.TEMPOTOTS)){
      addWarning(" Attenzione tempo disponibilità impianto superiore al tempo totale della giornata");  
    }
    if(getTImprodImpianto()<0){
      addWarning(" Attenzione ore improduttive negative"); 
    }
    if(getTRun()<=0){
      addWarning(" Attenzione tempo di runtime della linea = 0 ");
    }   
    if(getTPerditeNnRilevate()<0){
      addWarning(" Attenzione tempo perdite non rilevate < 0");
    }
    
    
    return Boolean.TRUE;  
  }

 
  public void addWarning(String msg){
    if(warnings.isEmpty()){
      warnings.add(" Indicatori Oee non corretti per gg : "+this.getDataRifN() +" --> \n"+ msg);
    }else{
      warnings.add(msg);
    }
  }
  
  public void addError(String msg){
    if(errors.isEmpty()){
      errors.add(" Impossibile calcolare Indicatori Oee per gg : "+this.getDataRifN() +" --> \n"+ msg );
    }else{
      errors.add(msg);
    }
  }
  
  public void addErrors(List list){
    errors.addAll(list);
  }

  public void addWarnings(List list){
    warnings.addAll(list);
  }
  
  @Override
  public String getInfo() {
     StringBuilder info=new StringBuilder(
              "\n @@ Linea: ").append(cdLavoro).append(" ; Data : ").append(dataRifN).append(
              " ; TDisp: ").append(tDispImp).append(" ; TImi: ").append(tImprodImp).append(
              " ; TProd: ").append(this.getTProdImpianto()).append(
              " ; TGua: ").append(tGuasti).append(" ; TSetup: ").append(tSetup).append(
              " ; TSetup2: ").append(tSetup2).append(" ; TPerdGest: ").append(tPerditeGest).append(
              " ; TMicrof: ").append(tMicrofermi).append(" ; \n  TVelRid: ").append(tVelRidotta).append(
              " ; TScarti: ").append(tScarti).append(" ; TScarti2: ").append(tScarti2).append(
              " ; TRilav: ").append(tRilavorazioni).append(
             "  ; TRun: ").append(tRun).append(" ; TRun2: ").append(tRun2).append(
             " ; TRun3: ").append(tRun3).append(" ; TExt1: ").append(tExt1).append(
             " ; TExt2: ").append(tExt2).append(" ; TExt3: ").append(tExt3).append(
             " ; \n TPnRil: ").append(this.getTPerditeNnRilevate()).append(" ;  nPzTot: ").append(nPzTot).append(
             " ; nPzTurni: ").append(nPzTurni).append(" ; nGuasti: ").append(nGuasti).append(
             " ; nScarti: ").append(nScarti).append(" ; nRilav: ").append(nRilav).append(
             " ; nCicli: ").append(nCicliLav).append(" ; nBande: ").append(nBande).append("  ; @@" );
             
                        
     
     
     return info.toString();
  }
  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(IndicatoriOeeGgBean.class);
}

