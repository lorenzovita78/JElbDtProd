/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.costant.CostantsColomb;
import colombini.exception.OEEException;
import colombini.query.indicatoriOee.QueryGgSettimana;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class OeeUtils {
  
  public final static String ANNO="ANNO";
  public final static String MESE="MESE";
  public final static String SETTIMANA="SETT";
  public final static String DATAINI="DATAINI";
  public final static String DATAFIN="DATAFIN";
  public final static Long TEMPOTOTSSETT=Long.valueOf(604800);
  
  public final static String LISTGGLAV="LISTGGLAV";
  
  public final static String TABOEEGG="RTVOEE31PF";
  public final static String TABOEESETT="RTVOEE30PF";
  
  public final static String AZIENDA="AZIENDA";
  public final static String CENTRO="CENTRO";
  public final static String DATA="DATA";
  public final static String TTOT="TTOT";
  public final static String TDII="TDII";
  public final static String TIMI="TIMI";
  public final static String TPRI="TPRI";
  public final static String TGUA="TGUA";
  public final static String TSET="TSET";
  public final static String TSCA="TSCA";
  public final static String TVRI="TVRI";
  public final static String TMIF="TMIF";
  public final static String TRILV="TRILV";
  public final static String TPGE="TPGE";
  public final static String TRUN="TRUN";
  public final static String OEE="OEE";
  public final static String TEEP="TEEP";
  public final static String TPNRO="TPNRO";
  public final static String NGUASTI="NGUASTI";
  public final static String NPZTOT="NPZTOT";
  public final static String DTINS="DTINS";
  public final static String ORAINS="ORAINS";
  public final static String UTINS="UTINS";
  
  //nuovi valori
  public final static String TLORDO="TLORDO";
  public final static String NPZTURNI="NPZTURNI";
  public final static String NSCARTI="NSCARTI";
  public final static String NCICLI="NCICLI";
  

  
  public final static Integer NCIFREDECOEE=3;
  
  
  
  private static OeeUtils instance;
  
  private OeeUtils(){
    
  }
  
  public static OeeUtils getInstance(){
    if(instance==null){
      instance= new OeeUtils();
    }
    
    return instance;
  }
  
  public Map getInfoSett(Connection con ,Date giornoRif,Boolean allDayWeek) throws OEEException{
    Map mapInfo=new HashMap();
    List  result =new ArrayList();
    Long dtIni=null;
    Long dtFin=null;
    Integer sett=null;
    List ggLav=new ArrayList();
    Date ggIniSett=null;
    
    if(giornoRif==null){
      ggIniSett=DateUtils.addDays(new Date(), -7);
    }else{
      ggIniSett=giornoRif;
    }
          
//    Date ggCorr=new Date();
    try{
      Integer anno=ClassMapper.classToClass(DateUtils.DateToStr(ggIniSett, "yyyy"),Integer.class);
      Integer mese=ClassMapper.classToClass(DateUtils.DateToStr(ggIniSett, "MM"),Integer.class);
      Long dtN=ClassMapper.classToClass(DateUtils.DateToStr(ggIniSett, "yyyyMMdd"),Long.class);

      QueryGgSettimana qry=new QueryGgSettimana();
      qry.setFilter(QueryGgSettimana.FILTERANNO, anno);
      qry.setFilter(QueryGgSettimana.FILTERGG, dtN);
      qry.setFilter(QueryGgSettimana.FILTERAZ, CostantsColomb.AZCOLOM);

      ResultSetHelper.fillListList(con, qry.toSQLString(), result);

      if(result==null || result.isEmpty())
        return null;

      Long dtAppo=null;
      for(int i=0;i<result.size();i++){
        if(i==0){
           dtIni=ClassMapper.classToClass( ((List)result.get(i)).get(0), Long.class);
           mese=ClassMapper.classToClass( ((List)result.get(i)).get(3), Integer.class);
        }
        dtAppo=ClassMapper.classToClass( ((List)result.get(i)).get(0), Long.class);
        sett=ClassMapper.classToClass( ((List)result.get(i)).get(1), Integer.class);
        Integer plav=ClassMapper.classToClass( ((List)result.get(i)).get(2), Integer.class);
        
        //se settato a True aggiungo tutti i giorni della settimana
        if(allDayWeek){
          ggLav.add(dtAppo);
          //se settato a False aggiungo tutti i giorni della settimana solo se la percentuale è > 0
        }else if(!allDayWeek && plav>0){  
          ggLav.add(dtAppo);
        }
      }
      dtFin=dtAppo;

      mapInfo.put(ANNO, anno);
      mapInfo.put(MESE, mese);
      mapInfo.put(SETTIMANA, sett);
      mapInfo.put(DATAINI, dtIni);
      mapInfo.put(DATAFIN, dtFin);
      mapInfo.put(LISTGGLAV, ggLav);

       return mapInfo;
    
   } catch (SQLException ex) {
     _logger.error("Problemi nell'interrogazione dell'As400 per calcolo settimanale --> " +ex.getMessage());
     throw new OEEException("Problemi nel caricamento delle informazioni relative della settimana ");
   } catch (QueryException ex) {
     _logger.error("Problemi nell'interrogazione dell'As400 per calcolo settimanale --> " +ex.getMessage());
     throw new OEEException(" Problemi nel caricamento delle informazioni relative della settimana ");
   } catch (ParseException ex) {
     _logger.error("Problemi di conversione di data per calcolo settimanale --> " +ex.getMessage() );
     throw new OEEException(" Problemi nel caricamento delle informazioni relative della settimana");
   }
  }
  
  
  public Map getInfoSett(Connection con,Date giornoRif ) throws OEEException {
    
    return getInfoSett(con, giornoRif, Boolean.FALSE);
   
 }
  
  
 public String getDtElabString() throws ParseException{
    String dtElab=DateUtils.DateToStr(new Date(), "yyyyMMdd");
   
    return dtElab;
  }
 
  public String getOraElabString() throws ParseException{
    String oraElab=DateUtils.DateToStr(new Date(), "HH:mm:ss");
   
    return oraElab;
  }
 
  public  double arrotonda( double numero, int nCifreDecimali ){
    return Math.round( numero * Math.pow( 10, nCifreDecimali ) )/Math.pow( 10, nCifreDecimali );
  }
   
  
  
//  public void storeDtSett(Connection con,IMapDtOee dtoee,Map info,boolean ggLav) throws SQLException{
//    String insert= INSERTOEESETT + " \n " +getSelectGG(dtoee, info, ggLav);  
//    _logger.debug(insert);
//    PreparedStatement st=con.prepareStatement(insert);
//    st.execute();
//    st.close();
//  }
//  
//  
//  
//  public void deleteDtSett(Connection con,IMapDtOee dtoee,Map info) throws SQLException{
//    Integer anno=(Integer) info.get(ANNO);
//    Integer mese=(Integer) info.get(MESE);
//    Integer sett=(Integer) info.get(SETTIMANA);
//    String cdL=dtoee.getNomeCentroLav();
//    if(cdL==null)
//      cdL=dtoee.getCentrodiLavoro();
//    
//    String delete= " DELETE FROM "+ColombiniConnections.getAs400LibPersColom()+"."+TABOEESETT+
//                   "  WHERE ANNO="+JDBCDataMapper.objectToSQL(anno)
//                   +"  AND MESE="+JDBCDataMapper.objectToSQL(mese)
//                   +"  AND SETT="+JDBCDataMapper.objectToSQL(sett)
//                   +"  AND CENTRO="+JDBCDataMapper.objectToSQL(cdL);
//            
//    _logger.debug(delete);
//    PreparedStatement st=con.prepareStatement(delete);
//    st.execute();
//    st.close();
//  }
//  
//  
//  private String getSelectGG(IMapDtOee dtoee,Map info,boolean ggLav){
//    StringBuilder select=new StringBuilder();
//    String cdL=dtoee.getNomeCentroLav();
//    if(cdL==null)
//      cdL=dtoee.getCentrodiLavoro();
//    
//    select.append("SELECT ").append(JDBCDataMapper.objectToSQL(cdL)).append(    //CENTRO
//                  " ,").append(JDBCDataMapper.objectToSQL(info.get(ANNO))).append(                  //ANNO 
//                  " ,").append(JDBCDataMapper.objectToSQL(info.get(MESE))).append(                  //MESE 
//                  " ,").append(JDBCDataMapper.objectToSQL(info.get(SETTIMANA))).append(             //SETT 
////                  " \n ,").append(" SUM( ").append(IMapDtOee.CLN_TTOT).append(" )").append(               //TTOT
//                  " , ").append(JDBCDataMapper.objectToSQL(TEMPOTOTSSETT)).append(  //tempo totale di una settimana
//                  " ,").append(" SUM( ").append(IMapDtOee.CLN_TDISPIMP).append(" )").append(           //TDII
//                  " ,").append(" SUM( ").append(IMapDtOee.CLN_TIMPRODIMP).append(" )").append(         //TIMI
//                  " ,").append(" SUM( ").append(IMapDtOee.CLN_TPRODIMP).append(" )").append(           //TIMI
//                  " ,").append(" SUM( ").append(dtoee.getCTGuasti()).append(" )").append(           //TGUA
//                  " \n ,").append(" SUM( ").append(dtoee.getCTSetup()).append(" )").append(            //TSET
//                  " ,").append(" SUM( ").append(dtoee.getCTScarti()).append(" )").append(           //TSCA
//                  " ,").append(" SUM( ").append(dtoee.getCTVelRidotta()).append(" )").append(       //TVRI
//                  " ,").append(" SUM( ").append(dtoee.getCTMicroFermi()).append(" )").append(       //TMIF
//                  " ,").append(" SUM( ").append(dtoee.getCTPerditeGestionali()).append(" )").append(//TPGE          
//                  " \n ,").append(" SUM( ").append(dtoee.getCTRun()).append(" )").append(              //TRUN
//                  " ,").append(" SUM( ").append(dtoee.getCTSetup1()).append(" )").append(           //TSET1
//                  " ,").append(getStringPerdNonRil()).append(                                       //TPNRO
//                 " ,").append(" SUM( ").append(dtoee.getCNGuasti()).append(" )").append(           //NGUASTI
//                 " \n ,").append(" SUM( ").append(dtoee.getCNPzTot()).append(" )").append(            //NPZTOT
//                 " ,").append(" SUM( ").append(dtoee.getCNScarti()).append(" )").append(           //NSCARTI
//                 " ,").append( getStringOEE() ).append(                                            //OEE 
//                 " ,").append( getStringTEEP() ).append(                                            //TEEP 
//                 " ,").append( getStringMTBF() ).append(                                            //MTBF
//                 " ,").append( getStringMTTR() ).append(                                            //MTTR
//                 " \n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(TABOEEGG).append(
//                 " WHERE 1=1").append(
//                 " and centro =").append(JDBCDataMapper.objectToSQL(dtoee.getCentrodiLavoro())).append(
//                 " and data between ").append(JDBCDataMapper.objectToSQL(info.get(DATAINI))).append(
//                 " and ").append(JDBCDataMapper.objectToSQL(info.get(DATAFIN)));
//    
//    if(ggLav){ 
//      List listaGg=(List) info.get(LISTGGLAV);
//      String ggS=ListUtils.toCommaSeparatedString(listaGg);
//      select.append(" AND DATA IN ( ").append(ggS).append( " )"); 
//            
//    }        
//            
//    return select.toString();
//            
//  }
//  
//  private String getStringPerdNonRil(){
//    StringBuffer str=new StringBuffer(" ( SUM ( ").append(IMapDtOee.CLN_TPRODIMP).append(" ) ").append( 
//                                            " - SUM ( ").append(IMapDtOee.CLN_TGUASTI).append(        
//                                                    " + ").append(IMapDtOee.CLN_TSETUP).append(
//                                                    " + ").append(IMapDtOee.CLN_TSCARTI).append( 
//                                                    " + ").append(IMapDtOee.CLN_TVELRID).append( 
//                                                    " + ").append(IMapDtOee.CLN_TMICROF).append( 
//                                                    " + ").append(IMapDtOee.CLN_TPGEST).append( 
//                                                    " + ").append(IMapDtOee.CLN_TRUN).append( 
//                                                    " + ").append(IMapDtOee.CLN_TLORDO).append( " )   ) TPNRO");
//    
//    return str.toString();
//  }
//  
//  private String getStringOEE(){
//    StringBuffer str=new StringBuffer(" cast( sum( ").append(IMapDtOee.CLN_TRUN).append(" ) as dec(10 , 2) )  ").append( 
//                                        "  / cast( sum( ").append(IMapDtOee.CLN_TPRODIMP).append( " ) as dec(10 , 2) ) *100  OEE");
//                                                    
//    return str.toString();
//  }
//  
//  private String getStringTEEP(){
//    StringBuffer str=new StringBuffer(" cast( sum( ").append(IMapDtOee.CLN_TRUN).append(" ) as dec(10 , 2) )  ").append( 
//                                      "  / cast( ").append(JDBCDataMapper.objectToSQL(TEMPOTOTSSETT)).append("  as dec(10 , 2) ) *100  TEEP");
////                                        "  / cast( sum( ").append(IMapDtOee.CLN_TTOT).append( " ) as dec(10 , 2) ) *100  TEEP");
//                                                    
//    return str.toString();
//  }
//  
//  private String getStringMTBF(){
//    StringBuffer str=new StringBuffer(" case when SUM( ").append(IMapDtOee.CLN_NGUASTI).append(" )<=0 then 0").append( 
//                                      " else ( (cast(sum( ").append(IMapDtOee.CLN_TRUN).append(" ) as dec(10 , 2)) ").append(
//                                      " / cast(sum( ").append(IMapDtOee.CLN_NGUASTI).append(" ) as dec(10 , 2)))) end MRBF");  
//                                                    
//    return str.toString();
//  }
//  
//  private String getStringMTTR(){
//    StringBuffer str=new StringBuffer(" case when SUM( ").append(IMapDtOee.CLN_NGUASTI).append(" )<=0 then 0").append( 
//                                      " else ( (cast(sum( ").append(IMapDtOee.CLN_TGUASTI).append(" ) as dec(10 , 2)) ").append(
//                                      " / cast(sum( ").append(IMapDtOee.CLN_NGUASTI).append(" ) as dec(10 , 2))) )  end MTTR");  
//                                                    
//    return str.toString();
//  }
  
  
  
  private static final Logger _logger = Logger.getLogger(OeeUtils.class); 
  
}
