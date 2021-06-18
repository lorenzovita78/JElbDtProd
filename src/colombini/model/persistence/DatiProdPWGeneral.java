/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;

import colombini.model.CausaliLineeBean;
import colombini.model.datiProduzione.InfoFermoCdL;
import colombini.model.datiProduzione.InfoTurniCdL;
import db.ConnectionsProps;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class DatiProdPWGeneral {
  
  private Integer azienda;
  private String cdL;
  private Date dataRif;
  
  private InfoTurniCdL infoTCdl;
  private List<InfoFermoCdL> fermiGg;
  private List<List> operatoriGg;
  
  
  
  public DatiProdPWGeneral(Integer azienda,Date dataRif, String cdL) {
    this.azienda=azienda;
    this.dataRif = dataRif;
    this.cdL = cdL;
    
    infoTCdl=new InfoTurniCdL(azienda, cdL, dataRif);
    this.fermiGg=new ArrayList();
    this.operatoriGg=new ArrayList();
 
  }

  
  
  public void retriveDataCdl(Connection con) throws SQLException{
    infoTCdl.retriveInfo(con);
    if(infoTCdl.getIdTurno()!=null){
      retrieveOperatori(con, infoTCdl.getIdTurno());
      fermiGg=InfoFermoCdL.retrieveFermi(con, infoTCdl.getIdTurno());
    }  
  }

  
  
  private void retrieveOperatori(Connection con, Integer idTrCdl) throws SQLException {
    if(idTrCdl==null)
      return;
    
    String library=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ConnectionsProps.AS400_LIB_PERS));
    String select="SELECT ( (HOUR(TPT1FN)*60+MINUTE(TPT1FN) ) -  ( HOUR(TPT1IN)*60+MINUTE(TPT1IN) ) ) + "
                      + " ( (HOUR(TPT2FN)*60+MINUTE(TPT2FN) ) -  ( HOUR(TPT2IN)*60+MINUTE(TPT2IN) )  )  as minDip,"
                  + " TPIDTO, TPDSOP , TPT1IN , TPT1FN , TPT2IN , TPT2FN "+
                  "\n from "+library+". ZDPWPT "+
                  "\n where tpidco="+JDBCDataMapper.objectToSQL(idTrCdl);
    
    ResultSetHelper.fillListList(con, select, operatoriGg);
      
    
  }

  public Integer getMinTurniOperatori(){
    Integer min=Integer.valueOf(0);
    for(List record:operatoriGg){
      min+=ClassMapper.classToClass(record.get(0),Integer.class);
    }
    
    return min;
  }
  
  public Integer getMinTurniOperatori(List<InfoFermoCdL> oreImprod){
    Integer min=Integer.valueOf(0);
    Integer minImprod=Integer.valueOf(0);
    for(List record:operatoriGg){
      min+=ClassMapper.classToClass(record.get(0),Integer.class);
    }
    
    for(InfoFermoCdL fermo:oreImprod){
      Date dataIni=fermo.getOraInizio();
      Date dataFin=fermo.getOraFine();
      Long durata=fermo.getSec();
      Integer nOp=Integer.valueOf(0);
      
      for(List record:operatoriGg){
        Date t1i=ClassMapper.classToClass(record.get(3),Date.class);  
        Date t1f=ClassMapper.classToClass(record.get(4),Date.class);
        Date t2i=ClassMapper.classToClass(record.get(5),Date.class);   
        Date t2f=ClassMapper.classToClass(record.get(6),Date.class);
        
        if(t1i!=null)
          t1i=DateUtils.AddTime(this.infoTCdl.getDataRif(),t1i);
        if(t1f!=null)
          t1f=DateUtils.AddTime(this.infoTCdl.getDataRif(),t1f);
        if(t2i!=null)
          t2i=DateUtils.AddTime(this.infoTCdl.getDataRif(),t2i);
        if(t2f!=null)
          t2f=DateUtils.AddTime(this.infoTCdl.getDataRif(),t2f);
        
        
        if( (DateUtils.beforeEquals(dataIni, t1f) &&
            DateUtils.afterEquals(dataFin, t1i)     ) ||
            ( DateUtils.beforeEquals(dataIni, t2f) &&
            DateUtils.afterEquals(dataFin, t2i)     )       )  {
          
          nOp+=1;
        }  
          
      }
      minImprod+=nOp*durata.intValue();
      
    }
    
    
    
    return min-minImprod;
  }
  
  public Integer getMinProdOperatori(){
    Integer min=getMinTurniOperatori();
    if(min<=0)
      return (this.infoTCdl.getMinutiTotOperatoriT1()+this.infoTCdl.getMinutiTotOperatoriT2()+this.infoTCdl.getMinutiTotOperatoriT3()+
            this.infoTCdl.getMinutiTotOperatoriT4());  
    
    return  min;
  }
  
  public List<Integer> getMinProdOperatorixTurno(){
    List t=new ArrayList();
    Integer t1=Integer.valueOf(0);
    Integer t2=Integer.valueOf(0);
    Integer t3=Integer.valueOf(0);
    Integer t4=Integer.valueOf(0);
    
    for(List record:operatoriGg){
      Date t1ini=ClassMapper.classToClass(record.get(3),Date.class);
      t1ini=DateUtils.AddTime(this.infoTCdl.getDataRif(), t1ini);
      Date t1fin=new Date( ClassMapper.classToClass(record.get(4),Date.class ).getTime());
      t1fin=DateUtils.AddTime(this.infoTCdl.getDataRif(), t1fin);
      Date t2ini=new Date(ClassMapper.classToClass(record.get(5),Date.class).getTime() );
      t2ini=DateUtils.AddTime(this.infoTCdl.getDataRif(), t2ini);
      Date t2fin=new Date(ClassMapper.classToClass(record.get(6),Date.class).getTime());
      t2fin=DateUtils.AddTime(this.infoTCdl.getDataRif(), t2fin);
      
      
      if(this.infoTCdl.existT1()){
        t1+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT1(), this.infoTCdl.getOraFineT1(), t1ini, t1fin);
        t1+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT1(), this.infoTCdl.getOraFineT1(), t2ini, t2fin);
      }
      
      if(this.infoTCdl.existT2()){
        t2+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT2(), this.infoTCdl.getOraFineT2(), t1ini, t1fin);
        t2+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT2(), this.infoTCdl.getOraFineT2(), t2ini, t2fin);
      }
      
      if(this.infoTCdl.existT3()){
        t3+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT3(), this.infoTCdl.getOraFineT3(), t1ini, t1fin);
        t3+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT3(), this.infoTCdl.getOraFineT3(), t2ini, t2fin);
      }
      
      if(this.infoTCdl.existT4()){
        t4+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT4(), this.infoTCdl.getOraFineT4(), t1ini, t1fin);
        t4+=DateUtils.getMinutiIntersect(this.infoTCdl.getOraInizioT4(), this.infoTCdl.getOraFineT4(), t2ini, t2fin);
      }
      
      
    }
    t.add(t1+t2+t3+t4);
    t.add(t1);
    t.add(t2);
    t.add(t3);
    t.add(t4);
      
    
    return t;
  }
  
  
  public Integer getMinProdImpiantoOld(Date oraInizio,Date oraFine){
    Integer min=Integer.valueOf(0);
    if(oraInizio==null || oraFine==null)
      return infoTCdl.getMinutiT1()+infoTCdl.getMinutiT2()+infoTCdl.getMinutiT3()+
             infoTCdl.getMinutiT4();    
    
    if(DateUtils.afterEquals(infoTCdl.getOraInizioT1(), oraInizio) &&
       DateUtils.beforeEquals(infoTCdl.getOraFineT1(), oraFine)     )
      min+=infoTCdl.getMinutiT1();
    
    if(DateUtils.afterEquals(infoTCdl.getOraInizioT2(), oraInizio) &&
       DateUtils.beforeEquals(infoTCdl.getOraFineT2(), oraFine)     )
      min+=infoTCdl.getMinutiT2();
    
    if(DateUtils.afterEquals(infoTCdl.getOraInizioT3(), oraInizio) &&
       DateUtils.beforeEquals(infoTCdl.getOraFineT3(), oraFine)     )
      min+=infoTCdl.getMinutiT3();
    
    if(DateUtils.afterEquals(infoTCdl.getOraInizioT4(), oraInizio) &&
       DateUtils.beforeEquals(infoTCdl.getOraFineT4(), oraFine)     )
      min+=infoTCdl.getMinutiT4();
      
    return min;
  }
  
  public Integer getMinProdImpianto(Date oraInizio,Date oraFine){
    Integer min=Integer.valueOf(0);
    if(oraInizio==null || oraFine==null)
      return infoTCdl.getMinutiT1()+infoTCdl.getMinutiT2()+infoTCdl.getMinutiT3()+
             infoTCdl.getMinutiT4();    
    
    if(infoTCdl.existT1()){
      min+=DateUtils.getMinutiIntersect(infoTCdl.getOraInizioT1(), infoTCdl.getOraFineT1(), oraInizio, oraFine);
    }
    
    if(infoTCdl.existT2()){
      min+=DateUtils.getMinutiIntersect(infoTCdl.getOraInizioT2(), infoTCdl.getOraFineT2(), oraInizio, oraFine);
    }
     
    if(infoTCdl.existT3()){
      min+=DateUtils.getMinutiIntersect(infoTCdl.getOraInizioT3(), infoTCdl.getOraFineT3(), oraInizio, oraFine);
    }
    
    if(infoTCdl.existT4()){
      min+=DateUtils.getMinutiIntersect(infoTCdl.getOraInizioT4(), infoTCdl.getOraFineT4(), oraInizio, oraFine);
    }
    
    return min;
  }
  
  
 
  
  public Map getMapTpFermiSec(Date oraInizio,Date oraFine,String cdlRif){
    Map fermiMap=new HashMap();
    List<InfoFermoCdL> fermi=getFermiGg(cdlRif);
    for(InfoFermoCdL fermo :fermi){
      if(fermo.getOraFine().after(oraInizio) && fermo.getOraInizio().before(oraFine)){
        String tipo=fermo.getTipo();
        Date oraInizioT=fermo.getOraInizio();
        Date oraFineT=fermo.getOraFine();
        if(fermo.getOraInizio().before(oraInizio))
          oraInizioT=oraInizio;

        if(fermo.getOraFine().after(oraFine))
          oraFineT=oraFine;

        Integer sec=DateUtils.numSecondiDiff(oraInizioT, oraFineT).intValue();
        if(fermiMap.containsKey(tipo)){
          fermiMap.put(tipo, ((Integer)fermiMap.get(tipo)) +sec);
        }else{
          fermiMap.put(tipo, sec);
        }
      }
    }
    
    return fermiMap;
  }

  
  
  public Integer getNumFermi(){
    Integer num=Integer.valueOf(0);
    for(InfoFermoCdL f:fermiGg){
      if(!CausaliLineeBean.TIPO_CAUS_ORENONPROD.equals(f.getTipo()) )
        num++;
    }
    
    return num;
  }

  
  public Double getMinFermiTipo(String tipo ,String cdl, Date dataIniF ,Date dataFinF){
    List<InfoFermoCdL> fermiLoc=getListFermiTipo(tipo, cdl, dataIniF, dataFinF);
    Double sec=Double.valueOf(0);
    for(InfoFermoCdL f:fermiLoc){
      sec+=f.getSec();
    }
    
    return sec/60;
  }
  
  public Double getMinFermi(List tipiFermi ,String cdl, Date dataIniF ,Date dataFinF){
    List<InfoFermoCdL> fermiLoc=getListFermiTipo(tipiFermi, cdl, dataIniF, dataFinF);
    Double sec=Double.valueOf(0);
    for(InfoFermoCdL f:fermiLoc){
      sec+=f.getSec();
    }
    
    return sec/60;
  }
  
  
  
  public Double getMinutiNnProduttivi(String cdlRif,Date dataInizio,Date dataFine){
    return getMinFermiTipo(CausaliLineeBean.TIPO_CAUS_ORENONPROD, cdlRif, dataInizio, dataFine);
  }
  
  public Double getMinutiNnProduttivi(String cdlRif){
    return getMinutiNnProduttivi(cdlRif, null, null);
  }
  
  public Double getMinutiSetup(String cdlRif,Date dataInizio,Date dataFine){
    return getMinFermiTipo(CausaliLineeBean.TIPO_CAUS_SETUP, cdlRif, dataInizio, dataFine);
  }
  
  public Double getMinutiSetup(String cdlRif){
    return getMinutiSetup(cdlRif, null, null);
  }
  
  public Double getMinutiGuasti(String cdlRif,Date dataInizio,Date dataFine){
    return getMinFermiTipo(CausaliLineeBean.TIPO_CAUS_GUASTO, cdlRif, dataInizio, dataFine);
  }
  
  public Double getMinutiGuasti(String cdlRif){
    return getMinutiGuasti(cdlRif, null, null);
  }
  
  public Double getMinutiMicrofermi(String cdlRif,Date dataInizio,Date dataFine){
    return getMinFermiTipo(CausaliLineeBean.TIPO_CAUS_MICROFRM, cdlRif, dataInizio, dataFine);
  }
  
  public Double getMinutiMicrofermi(String cdlRif){
    return getMinutiMicrofermi(cdlRif, null, null);
  }
  
  
  
  public List<InfoFermoCdL> getListFermiTipo(List tipiFermi ,String cdl, Date dataIniF ,Date dataFinF){
    List fermiLoc=new ArrayList();
    List<InfoFermoCdL> fermi=getFermiGg(cdl);
    for(InfoFermoCdL fermoTmp :fermi){
      if(tipiFermi.contains(fermoTmp.getTipo())){
        Date inizioF=fermoTmp.getOraInizio();
        Date fineF=fermoTmp.getOraFine();
        
        //se le date passate come parametri sono valorizzate allora filtro solo i fermi all'interno del periodo fornito
        if(dataIniF!=null && dataFinF!=null){
          //verifico se c'è incrocio nei periodi
          if(fineF.after(dataIniF) && inizioF.before(dataFinF)){
            InfoFermoCdL fClone =fermoTmp.clone();
            if(inizioF.before(dataIniF)){
              fClone.setOraInizio(dataIniF);
            }
            if(fineF.after(dataFinF)){
              fClone.setOraFine(dataFinF);
            }      
            fermiLoc.add(fClone);
          }      
        }else{
          fermiLoc.add(fermoTmp);
        }
      }
    }
    return fermiLoc;
  }
  
  
  public List<InfoFermoCdL> getListFermiTipo(String tipo ,String cdl, Date dataIniF ,Date dataFinF){
    List fermiLoc=new ArrayList();
    List<InfoFermoCdL> fermi=getFermiGg(cdl);
    for(InfoFermoCdL fermoTmp :fermi){
      if(tipo.equals(fermoTmp.getTipo())){
        Date inizioF=fermoTmp.getOraInizio();
        Date fineF=fermoTmp.getOraFine();
        
        //se le date passate come parametri sono valorizzate allora filtro solo i fermi all'interno del periodo fornito
        if(dataIniF!=null && dataFinF!=null){
          //verifico se c'è incrocio nei periodi
          if(fineF.after(dataIniF) && inizioF.before(dataFinF)){
            InfoFermoCdL fClone =fermoTmp.clone();
            if(inizioF.before(dataIniF)){
              fClone.setOraInizio(dataIniF);
            }
            if(fineF.after(dataFinF)){
              fClone.setOraFine(dataFinF);
            }      
            fermiLoc.add(fClone);
          }      
        }else{
          fermiLoc.add(fermoTmp);
        }
      }
    }
    return fermiLoc;
  }
  
  public InfoTurniCdL getInfoTCdl() {
    return infoTCdl;
  }

  public List<InfoFermoCdL> getFermiGg() {
    return fermiGg;
  }

  public List<InfoFermoCdL> getFermiGg(String cdl) {
    if(StringUtils.isEmpty(cdl))
      return fermiGg;

    List l=new ArrayList();
    for(InfoFermoCdL f:fermiGg){
      if(cdl.equals(f.getCdl()))
        l.add(f);
    }

    return l;
  }
  
  
   public List<InfoFermoCdL> getFermiGg(String cdl,String tipo) {
    if(StringUtils.isEmpty(cdl) || StringUtils.IsEmpty(tipo))
      return fermiGg;

    List l=new ArrayList();
    for(InfoFermoCdL f:fermiGg){
      if(cdl.equals(f.getCdl()) && tipo.equals(f.getTipo()) )
        l.add(f);
    }

    return l;
  }
  
  
  public List<List> getOperatoriGg() {
    return operatoriGg;
  }
  
  
  public Boolean isValid(){
    return (this.getInfoTCdl().getIdTurno()!=null && this.getInfoTCdl().getIdTurno()> 0);
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(DatiProdPWGeneral.class);
  
}
