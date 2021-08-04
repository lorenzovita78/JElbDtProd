/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.datiProduzione;

import colombini.costant.CostantsColomb;
import db.ConnectionsProps;
import db.JDBCDataMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class InfoTurniCdL {
  private Integer idTurno;
  
  private Integer azienda;
  private String cdl;
  private Date dataRif;
  
  private Date oraInizioT1;
  private Date oraFineT1;
  private Integer numPzT1;
  private Integer numClT1;
  private Integer numPileBandeT1;
  private Integer numScartiT1;
  private Integer numOperatoriT1;
  private Integer minutiTotOperatoriT1;
  
  private Date oraInizioT2;
  private Date oraFineT2;
  private Integer numPzT2;
  private Integer numClT2;
  private Integer numPileBandeT2;
  private Integer numScartiT2;
  private Integer numOperatoriT2;
  private Integer minutiTotOperatoriT2;
  
  private Date oraInizioT3;
  private Date oraFineT3;
  private Integer numPzT3;
  private Integer numClT3;
  private Integer numPileBandeT3;
  private Integer numScartiT3;
  private Integer numOperatoriT3;
  private Integer minutiTotOperatoriT3;
  
  
  private Date oraInizioT4;
  private Date oraFineT4;
  private Integer numPzT4;
  private Integer numClT4;
  private Integer numPileBandeT4;
  private Integer numScartiT4;
  private Integer numOperatoriT4;
  private Integer minutiTotOperatoriT4;

  public InfoTurniCdL(Integer idTurno) {
    this.idTurno = idTurno;
  }

  public InfoTurniCdL(Integer azienda, String cdl, Date dataRif) {
    this.azienda = azienda;
    this.cdl = cdl;
    this.dataRif = dataRif;
  }

  public Integer getIdTurno() {
    return idTurno;
  }

  public Integer getAzienda() {
    return azienda;
  }

  public String getCdl() {
    return cdl;
  }

  public Date getDataRif() {
    return dataRif;
  }

  
  public Date getOraInizioT1() {
    return oraInizioT1;
  }

  public void setOraInizioT1(Date oraInizioT1) {
    this.oraInizioT1 = oraInizioT1;
  }

  public Date getOraFineT1() {
    return oraFineT1;
  }

  public void setOraFineT1(Date oraFineT1) {
    this.oraFineT1 = oraFineT1;
  }

  public Integer getNumPzT1() {
    return numPzT1;
  }

  public void setNumPzT1(Integer numPzT1) {
    this.numPzT1 = numPzT1;
  }

  public Integer getNumClT1() {
    return numClT1;
  }

  public void setNumClT1(Integer numClT1) {
    this.numClT1 = numClT1;
  }

  public Integer getNumPileBandeT1() {
    return numPileBandeT1;
  }

  public void setNumPileBandeT1(Integer numPileBandeT1) {
    this.numPileBandeT1 = numPileBandeT1;
  }

  public Integer getNumScartiT1() {
    return numScartiT1;
  }

  public void setNumScartiT1(Integer numScartiT1) {
    this.numScartiT1 = numScartiT1;
  }

  public Date getOraInizioT2() {
    return oraInizioT2;
  }

  public void setOraInizioT2(Date oraInizioT2) {
    this.oraInizioT2 = oraInizioT2;
  }

  public Date getOraFineT2() {
    return oraFineT2;
  }

  public void setOraFineT2(Date oraFineT2) {
    this.oraFineT2 = oraFineT2;
  }

  public Integer getNumPzT2() {
    return numPzT2;
  }

  public void setNumPzT2(Integer numPzT2) {
    this.numPzT2 = numPzT2;
  }

  public Integer getNumClT2() {
    return numClT2;
  }

  public void setNumClT2(Integer numClT2) {
    this.numClT2 = numClT2;
  }

  public Integer getNumPileBandeT2() {
    return numPileBandeT2;
  }

  public void setNumPileBandeT2(Integer numPileBandeT2) {
    this.numPileBandeT2 = numPileBandeT2;
  }

  public Integer getNumScartiT2() {
    return numScartiT2;
  }

  public void setNumScartiT2(Integer numScartiT2) {
    this.numScartiT2 = numScartiT2;
  }

  public Date getOraInizioT3() {
    return oraInizioT3;
  }

  public void setOraInizioT3(Date oraInizioT3) {
    this.oraInizioT3 = oraInizioT3;
  }

  public Date getOraFineT3() {
    return oraFineT3;
  }

  public void setOraFineT3(Date oraFineT3) {
    this.oraFineT3 = oraFineT3;
  }

  public Integer getNumPzT3() {
    return numPzT3;
  }

  public void setNumPzT3(Integer numPzT3) {
    this.numPzT3 = numPzT3;
  }

  public Integer getNumClT3() {
    return numClT3;
  }

  public void setNumClT3(Integer numClT3) {
    this.numClT3 = numClT3;
  }

  public Integer getNumPileBandeT3() {
    return numPileBandeT3;
  }

  public void setNumPileBandeT3(Integer numPileBandeT3) {
    this.numPileBandeT3 = numPileBandeT3;
  }

  public Integer getNumScartiT3() {
    return numScartiT3;
  }

  public void setNumScartiT3(Integer numScartiT3) {
    this.numScartiT3 = numScartiT3;
  }

  public Date getOraInizioT4() {
    return oraInizioT4;
  }

  public void setOraInizioT4(Date oraInizioT4) {
    this.oraInizioT4 = oraInizioT4;
  }

  public Date getOraFineT4() {
    return oraFineT4;
  }

  public void setOraFineT4(Date oraFineT4) {
    this.oraFineT4 = oraFineT4;
  }

  public Integer getNumPzT4() {
    return numPzT4;
  }

  public void setNumPzT4(Integer numPzT4) {
    this.numPzT4 = numPzT4;
  }

  public Integer getNumClT4() {
    return numClT4;
  }

  public void setNumClT4(Integer numClT4) {
    this.numClT4 = numClT4;
  }

  public Integer getNumPileBandeT4() {
    return numPileBandeT4;
  }

  public void setNumPileBandeT4(Integer numPileBandeT4) {
    this.numPileBandeT4 = numPileBandeT4;
  }

  public Integer getNumScartiT4() {
    return numScartiT4;
  }

  public void setNumScartiT4(Integer numScartiT4) {
    this.numScartiT4 = numScartiT4;
  }

  public Integer getNumOperatoriT1() {
    return numOperatoriT1;
  }

  public void setNumOperatoriT1(Integer numOperatoriT1) {
    this.numOperatoriT1 = numOperatoriT1;
  }

  public Integer getMinutiTotOperatoriT1() {
    return minutiTotOperatoriT1;
  }

  public void setMinutiTotOperatoriT1(Integer minutiTotOperatoriT1) {
    this.minutiTotOperatoriT1 = minutiTotOperatoriT1;
  }

  public Integer getNumOperatoriT2() {
    return numOperatoriT2;
  }

  public void setNumOperatoriT2(Integer numOperatoriT2) {
    this.numOperatoriT2 = numOperatoriT2;
  }

  public Integer getMinutiTotOperatoriT2() {
    return minutiTotOperatoriT2;
  }

  public void setMinutiTotOperatoriT2(Integer minutiTotOperatoriT2) {
    this.minutiTotOperatoriT2 = minutiTotOperatoriT2;
  }

  public Integer getNumOperatoriT3() {
    return numOperatoriT3;
  }

  public void setNumOperatoriT3(Integer numOperatoriT3) {
    this.numOperatoriT3 = numOperatoriT3;
  }

  public Integer getMinutiTotOperatoriT3() {
    return minutiTotOperatoriT3;
  }

  public void setMinutiTotOperatoriT3(Integer minutiTotOperatoriT3) {
    this.minutiTotOperatoriT3 = minutiTotOperatoriT3;
  }

  public Integer getNumOperatoriT4() {
    return numOperatoriT4;
  }

  public void setNumOperatoriT4(Integer numOperatoriT4) {
    this.numOperatoriT4 = numOperatoriT4;
  }

  public Integer getMinutiTotOperatoriT4() {
    return minutiTotOperatoriT4;
  }

  public void setMinutiTotOperatoriT4(Integer minutiTotOperatoriT4) {
    this.minutiTotOperatoriT4 = minutiTotOperatoriT4;
  }
  
  //----------------------------------------------------------------------------
  
  protected String getTurnoString(Date dataIni , Date dataFin){
    String orario="";
    if(dataIni!=null && dataFin!=null){
      try{
       orario=DateUtils.DateToStr(dataIni, "HH:mm")+ " - "+DateUtils.DateToStr(dataFin, "HH:mm");
       
      } catch (ParseException ex) {
        _logger.error("Errore in fase di conversione della data --> "+ex.getMessage());
      }
    }
    
    return orario;
      
  }
  
  public String getT1String(){
    if(existT1())
      return getTurnoString(oraInizioT1, oraFineT1);
    
    return "";
  }
  
  public String getT2String(){
    if(existT2())
      return getTurnoString(oraInizioT2, oraFineT2);
    
    return "";
  }
  
  public String getT3String(){
    if(existT3())
      return getTurnoString(oraInizioT3, oraFineT3);
    
    return "";
  }
  
  public String getT4String(){
    if(existT4())
      return getTurnoString(oraInizioT4, oraFineT4);
    
    return "";
  }
  
  
  public Integer getMinutiT1(){
    if(oraFineT1.after(dataRif))
      return DateUtils.numMinutiDiff(oraInizioT1, oraFineT1).intValue();
    
    return Integer.valueOf(0);
  }
  
  public Integer getMinutiT2(){
    if(oraFineT2.after(dataRif))
      return DateUtils.numMinutiDiff(oraInizioT2, oraFineT2).intValue();
    
    return Integer.valueOf(0);
  }
  
  public Integer getMinutiT3(){
    if(oraFineT3.after(dataRif))
      return DateUtils.numMinutiDiff(oraInizioT3, oraFineT3).intValue();
    
    return Integer.valueOf(0);
  }
  
  public Integer getMinutiT4(){
    if(oraFineT4.after(dataRif))
      return DateUtils.numMinutiDiff(oraInizioT4, oraFineT4).intValue();
    
    return Integer.valueOf(0);
  }
  
  public Boolean existT1(){
    if(!oraFineT1.equals(dataRif))
      return true;
    
    return false;
  }
  
  public Boolean existT2(){
    if(!oraFineT2.equals(dataRif))
      return true;
    
    return false;
  }
  
  public Boolean existT3(){
    if(!oraFineT3.equals(dataRif))
      return true;
    
    return false;
  }
  
  public Boolean existT4(){
    if(!oraFineT4.equals(dataRif))
      return true;
    
    return false;
  }
  
  
  public Date getMinOraInizio(){
    Date dI=null;
    if(existT1()){
      dI=oraInizioT1;
    }else if(existT2()){
      dI=oraInizioT2;
    }else if(existT3()){
      dI=oraInizioT3;
    }else if(existT4()){
      dI=oraInizioT4;
    }
    return dI;
  }
  
  public Date getMaxOraFine(){
    Date dF=null;
    if(existT4()){
      dF=oraFineT4; 
    }else if(existT3()){
      dF=oraFineT3;
    }else if(existT2()){
      dF=oraFineT2;
    }else if(existT1()){
      dF=oraFineT1;
    }
    return dF;
  }
  
  
  public List<List> getListTurniValidi(Date oraInizio,Date oraFine){
    List<List> turni=new ArrayList();
    
    if(oraInizio==null)
      oraInizio=getMinOraInizio();
    
    if(oraFine==null)
      oraFine=getMaxOraFine();
    
    if(existT1())
      if(DateUtils.afterEquals(oraInizioT1, oraInizio) 
        &&  DateUtils.beforeEquals(oraFineT1, oraFine)      ){
        
      List turno=new ArrayList();
      turno.add(oraInizioT1);
      turno.add(oraFineT1);
      turni.add(turno);
      
    }
    
    if(existT2()){
      List turno=new ArrayList();
      if(DateUtils.beforeEquals(oraInizioT2, oraInizio) && (
              DateUtils.beforeEquals(oraFineT2, oraFine)) ){
        
        turno.add(oraInizio);
        turno.add(oraFineT2);
        turni.add(turno);
        
      }
      else if(DateUtils.afterEquals(oraInizioT2, oraInizio) 
        &&  DateUtils.beforeEquals(oraFineT2, oraFine)      ){
        
        turno.add(oraInizioT2);
        turno.add(oraFineT2);
        turni.add(turno);
      }
    }  
    if(existT3()){
      List turno=new ArrayList();
      if(DateUtils.afterEquals(oraInizioT3, oraInizio) 
        &&  DateUtils.beforeEquals(oraFineT3, oraFine)      ){ 

        turno.add(oraInizioT3);
        turno.add(oraFineT3);
        turni.add(turno);
      }
        
      else if(DateUtils.afterEquals(oraInizioT3, oraInizio) 
          &&  DateUtils.afterEquals(oraFineT3, oraFine)      ){ 

        turno.add(oraInizioT3);
        turno.add(oraFine);
        turni.add(turno);
      }
    }  
    if(existT4())
      if(DateUtils.afterEquals(oraInizioT4, oraInizio) 
        &&  DateUtils.beforeEquals(oraFineT4, oraFine)      ){ 
      List turno=new ArrayList();
      turno.add(oraInizioT4);
      turno.add(oraFineT4);
      turni.add(turno);
    }
    
    
    return turni;
  }
  
  public List<List> getListBuchiTurni(List<List> periodiProd){
    List<List> buchi=new ArrayList();
    Date dataIP=null;
    Date dataFP=null;
    
    for(List per:periodiProd){
      Date dataItmp=(Date)per.get(0);
      Date dataFtmp=(Date)per.get(1);
      if(dataFP!=null && DateUtils.numMinutiDiff(dataFP, dataItmp)>0 ) {
        List recC=new ArrayList();
        recC.add(dataFP);
        recC.add(dataItmp);
        buchi.add(recC);
      }
      
      dataIP=dataItmp;  
      dataFP=dataFtmp; 
    }
    
    return buchi;
  }
  
  
  public List<List> getListBuchiTurni(Date oraInizio,Date oraFine){
    
    List<List> turni=getListTurniValidi(oraInizio,oraFine);
    
    return getListBuchiTurni(turni);
  }
  
  
  public Integer getNumPzTot(){
    Integer tot=getNumPzT1()+getNumPzT2()+getNumPzT3()+getNumPzT4();
    return tot;
  }
  
  public Integer getNumClTot(){
    Integer tot=getNumClT1()+getNumClT2()+getNumClT3()+getNumClT4();
    return tot;
  }
  
  public Integer getNumScartiTot(){
    Integer tot=getNumScartiT1()+getNumScartiT2()+getNumScartiT3()+getNumScartiT4();
    return tot;
  }
  
  public Integer getNumPileBandeTot(){
    Integer tot=getNumPileBandeT1()+getNumPileBandeT2()+getNumPileBandeT3()+getNumPileBandeT4();
    return tot;
  }
  
  public void retriveInfo(Connection con) throws SQLException{
    ResultSet rs=null;
    PreparedStatement ps=null;
    String cdlPadre="";
    String flagTrn="";
    String cdlFiltro="";
    
    String library=ClassMapper.classToString(ConnectionsProps.getInstance().getProperty(ConnectionsProps.AS400_LIB_PERS));
    
    String sqlCdl="select CLPLGP ,CLFGTC from "+library+".ZDPWCL"+
                "\n where clcono="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)
                  + " and clplgr="+JDBCDataMapper.objectToSQL(cdl);
                  
    
    try{
      ps=con.prepareStatement(sqlCdl);
      rs=ps.executeQuery();
      if(rs.next()){
        cdlPadre=rs.getString("CLPLGP").trim();
        flagTrn=rs.getString("CLFGTC").trim();
        if("1".equals(flagTrn)){
          cdlFiltro=cdl;
        }else{
          cdlFiltro=cdlPadre;
        }
      }
    } finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    }
    
    String sql="SELECT COIDCO,COT1IN,COT1FN,COT1PP,COT1PS,COT1CP,COT1BP,COT1NO,COT1OT,COT1MT,"
                          + " COT2IN,COT2FN,COT2PP,COT2PS,COT2CP,COT2BP,COT2NO,COT2OT,COT2MT, "
                          + " COT3IN,COT3FN,COT3PP,COT3PS,COT3CP,COT3BP,COT3NO,COT3OT,COT3MT,"
                          + " COT4IN,COT4FN,COT4PP,COT4PS,COT4CP,COT4BP,COT4NO,COT4OT,COT4MT "+
                "\n from "+library+".ZDPWCO"
            +   "\n where cocono="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)
                  + " and coplgr="+JDBCDataMapper.objectToSQL(cdlFiltro)
                  + " and codtrf="+JDBCDataMapper.objectToSQL(dataRif);
    try{
      ps=con.prepareStatement(sql);
      rs=ps.executeQuery();
      if(rs.next()){
        this.idTurno= rs.getInt("COIDCO") ;
        this.oraInizioT1=rs.getTimestamp("COT1IN");
        oraInizioT1=DateUtils.AddTime(dataRif, oraInizioT1);
        this.oraInizioT2=rs.getTimestamp("COT2IN");
        oraInizioT2=DateUtils.AddTime(dataRif, oraInizioT2);
        this.oraInizioT3=rs.getTimestamp("COT3IN");
        oraInizioT3=DateUtils.AddTime(dataRif, oraInizioT3);
        this.oraInizioT4=rs.getTimestamp("COT4IN");
        oraInizioT4=DateUtils.AddTime(dataRif, oraInizioT4);
        this.oraFineT1=rs.getTimestamp("COT1FN");
        oraFineT1=DateUtils.AddTime(dataRif, oraFineT1);
        this.oraFineT2=rs.getTimestamp("COT2FN");
        oraFineT2=DateUtils.AddTime(dataRif, oraFineT2);
        this.oraFineT3=rs.getTimestamp("COT3FN");
        oraFineT3=DateUtils.AddTime(dataRif, oraFineT3);
        this.oraFineT4=rs.getTimestamp("COT4FN");
        oraFineT4=DateUtils.AddTime(dataRif, oraFineT4);
        
        this.numPzT1=rs.getInt("COT1PP");
        this.numPzT2=rs.getInt("COT2PP");
        this.numPzT3=rs.getInt("COT3PP");
        this.numPzT4=rs.getInt("COT4PP");
        this.numClT1=rs.getInt("COT1CP");
        this.numClT2=rs.getInt("COT2CP");
        this.numClT3=rs.getInt("COT3CP");
        this.numClT4=rs.getInt("COT4CP");
        this.numPileBandeT1=rs.getInt("COT1BP");
        this.numPileBandeT2=rs.getInt("COT2BP");
        this.numPileBandeT3=rs.getInt("COT3BP");
        this.numPileBandeT4=rs.getInt("COT4BP");
        this.numScartiT1=rs.getInt("COT1PS");
        this.numScartiT2=rs.getInt("COT2PS");
        this.numScartiT3=rs.getInt("COT3PS");
        this.numScartiT4=rs.getInt("COT4PS");
        this.numOperatoriT1=rs.getInt("COT1NO");
        this.numOperatoriT2=rs.getInt("COT2NO");
        this.numOperatoriT3=rs.getInt("COT3NO");
        this.numOperatoriT4=rs.getInt("COT4NO");
        this.minutiTotOperatoriT1=rs.getInt("COT1OT")*60+rs.getInt("COT1MT");
        this.minutiTotOperatoriT2=rs.getInt("COT2OT")*60+rs.getInt("COT2MT");
        this.minutiTotOperatoriT3=rs.getInt("COT3OT")*60+rs.getInt("COT3MT");
        this.minutiTotOperatoriT4=rs.getInt("COT4OT")*60+rs.getInt("COT4MT");
      }else{
        _logger.debug("Turni non presenti per la giornata "+dataRif+" e la linea indicata"+cdl);
      }
   
    } finally{
      if(ps!=null)
        ps.close();
      
      if(rs!=null)
        rs.close();
    }
  
  }
  
  
  
  
  private static final Logger _logger = Logger.getLogger(InfoTurniCdL.class);
}
