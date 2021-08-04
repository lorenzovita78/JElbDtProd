/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.tab;

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.model.persistence.IndicatoriOeeGgBean;
import db.JDBCDataMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ArrayUtils;
import utils.DateUtils;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class IndicatoriOeeGraf implements ITable {
  
  public final static String TABLEOEEGRAF="ZIOEEG";
  
  public final static String TABLEOEEGRAFFIELDS="ZGCONO,ZGPLGR,ZGPRDS,ZGDTRF,ZGPGUA,ZGPSET,ZGPSE2,ZGPPGE,ZGPMIF,ZGPVRI,"
                                              + "ZGPRIL,ZGPSCA,ZGPSC2,ZGPOEE,ZGPOE2,ZGPOE3,ZGPEX1,ZGPEX2,ZGPEX3,ZGPPNR,ZGRGDT";
  
  public final static String TABLEOEEGRAFKEYFIELDS="ZGCONO,ZGPLGR";
  
  private String NUM="$FIELDNUM$";
  private String DEN="$FIELDDEN$";
  private String PERCSTDSUM="\n cast(sum("+NUM+")as dec(10,2))/cast(sum("+DEN+")as dec(10,2))*100";
  
  public final static String ZGCONO="ZGCONO";
  public final static String ZGPLGR="ZGPLGR";
//  public final static String ZGPRDS="ZGPRDS";
//  public final static String ZGDTRF="ZGDTRF";
//  public final static String ZGPGUA="ZGPGUA";
//  public final static String ZGPSET="ZGPSET";
//  public final static String ZGPSE2="ZGPSE2";
//  public final static String ZGPPGE="ZGPPGE";
//  public final static String ZGPMIF="ZGPMIF";
//  public final static String ZGPVRI="ZGPVRI";
//  public final static String ZGPRIL="ZGPRIL";
//  public final static String ZGPSCA="ZGPSCA";
//  public final static String ZGPSC2="ZGPSC2";
//  public final static String ZGPRUN="ZGPRUN";
//  public final static String ZGPRN2="ZGPRN2";
//  public final static String ZGPRN3="ZGPRN3";
//  public final static String ZGPEX1="ZGPEX1";
//  public final static String ZGPEX2="ZGPEX2";
//  public final static String ZGPEX3="ZGPEX3";
//  public final static String ZGPPNR="ZGPPNR";
//  public final static String ZGRGDT="ZGRGDT";

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLEOEEGRAF;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABLEOEEGRAFFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABLEOEEGRAFKEYFIELDS.split(","));
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER); //az
    types.add(Types.CHAR);    //centro di lavoro
    types.add(Types.CHAR);    //periodo
    types.add(Types.NUMERIC);  //dataRif
   
    types.add(Types.NUMERIC);    //tempi
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    types.add(Types.NUMERIC);    
    types.add(Types.NUMERIC);
    
    types.add(Types.TIMESTAMP);    
    
    return types;
  }

//--------------------------------------------------------------------------------------
  
  private String getStringPercStdSum(String num,String den){
    String f=PERCSTDSUM.replace(NUM, num);
    f=f.replace(DEN, den);

    return f;
  }

  public void elabDatiGraficoLinea(Connection con,String codLinea,String lineeFiltro,Date dataIni,Date dataFin) throws SQLException{
    PersistenceManager pm=new PersistenceManager(con);
    deleteDtGraf(pm,codLinea);
    insertDtGraf(pm, codLinea,lineeFiltro, dataIni, dataFin);
  }

  
  private void deleteDtGraf(PersistenceManager pm,String codLinea) throws SQLException{
    Map keyValues=new HashMap();
    keyValues.put(ZGCONO, CostantsColomb.AZCOLOM);
    keyValues.put(ZGPLGR, codLinea);
    
    
    
    pm.deleteDt(this, keyValues);
  }
  

  private void insertDtGraf(PersistenceManager pm,String codLinea,String lineeFiltro,Date dataIni,Date dataFin) throws SQLException {
    String insert=pm.getInsertStatment(this);
    insert=insert.substring(0, insert.lastIndexOf("VALUES"));

    Integer anno=DateUtils.getYear(dataIni);
    Integer mese=DateUtils.getMonth(dataIni)+1;
    Integer sett=DateUtils.getWorkWeek(dataIni);
    
    dataIni=DateUtils.RemoveTime(dataIni);
    dataFin=DateUtils.RemoveTime(dataFin);
    
    String select=" ( \n "+ getSelectAnniPrec(codLinea,anno,lineeFiltro)+
                    " \n ) \n UNION ( \n "+ getSelectMesiAnnoCorr(codLinea,anno,mese,lineeFiltro)+
                    " \n ) \n UNION ( \n "+ getSelectSettMeseCorr(codLinea,anno,mese,sett,lineeFiltro)+
                   " \n ) \n UNION ( \n "+ getSelectGgSettCorr(codLinea, dataIni, dataFin,lineeFiltro) +
                    " \n ) \n UNION ( \n "+ getSelectAnnoCorr(codLinea,anno,lineeFiltro)+ 
                    " \n ) ";

    insert+=" \n "+select;
//    _logger.debug(insert);
    PreparedStatement st=pm.getConnection().prepareStatement(insert);
    st.execute();
    st.close();
  }

  private String getSelectAnniPrec(String codLinea,Integer annoRif,String lineeString) {
    StringBuilder s=new StringBuilder();
    s.append(" SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(" , ").append(
            JDBCDataMapper.objectToSQL(codLinea)).append(" ,  DIGITS(").append(
        IndicatoriOeeSett.FLDZCANNO).append(") , ").append(
        " INT( ").append(IndicatoriOeeSett.FLDZCANNO).append("||'0101' ) ").append(" , ").append(
        getSubSelectOeeSett()).append(" , current timestamp ").append(
      "\n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeSett.TABLEOEESETT).append(
      "\n WHERE 1=1 ").append(
      " AND ").append(IndicatoriOeeSett.FLDZCCONO).append(" = ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(
      " AND ").append(IndicatoriOeeSett.FLDZCANNO).append(" < ").append(JDBCDataMapper.objectToSQL(annoRif));
    
    if(lineeString==null){ 
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" = ").append(JDBCDataMapper.objectToSQL(codLinea));
    } else{
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" IN ( ").append(lineeString).append(" )");
    }

    s.append("  GROUP BY ").append(IndicatoriOeeSett.FLDZCANNO).append(     
             " ORDER BY ").append(IndicatoriOeeSett.FLDZCANNO);        
            
    return s.toString();
  }

  private String getSelectMesiAnnoCorr(String codLinea,Integer annoRif,Integer meseRif,String lineeString) {
    StringBuilder s=new StringBuilder();
    s.append(" SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(" , ").append(
            JDBCDataMapper.objectToSQL(codLinea)).append(" , ").append(
        " case ").append(IndicatoriOeeSett.FLDZCMESE).append(
       " when 1 then 'GEN' when 2 then 'FEB' when 3 then 'MAR' when 4 then 'APR' when 5 then 'MAG' when 6 then 'GIU' ").append(
       " when 7 then 'LUG' when 8 then 'AGO' when 9 then 'SET' when 10 then 'OTT' when 11 then 'NOV' when 12 then 'DIC' end case , ").append(
       " case when ").append(IndicatoriOeeSett.FLDZCMESE).append(
            " < 10 then INT( ").append(IndicatoriOeeSett.FLDZCANNO).append(
            " ||'0'||").append(IndicatoriOeeSett.FLDZCMESE).append(
            " ||'01' ) else INT( ").append(IndicatoriOeeSett.FLDZCANNO).append(
            " || ").append(IndicatoriOeeSett.FLDZCMESE).append(
            "||'01' ) end case, ").append(
//       " INT( ").append(IndicatoriOeeSett.FLDZCANNO).append("||").append(IndicatoriOeeSett.FLDZCMESE).append("||'01' ) , ").append(
        getSubSelectOeeSett()).append(" , current timestamp ").append(
      "\n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeSett.TABLEOEESETT).append(
      "\n WHERE 1=1 ").append(
      " AND ").append(IndicatoriOeeSett.FLDZCCONO).append(" = ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(             
      " AND ").append(IndicatoriOeeSett.FLDZCANNO).append(" = ").append(JDBCDataMapper.objectToSQL(annoRif)).append(
      " AND ").append(IndicatoriOeeSett.FLDZCMESE).append(" < ").append(JDBCDataMapper.objectToSQL(meseRif));
//      " AND ").append(IndicatoriOeeSett.FLDZCMESE).append(" <= ").append(JDBCDataMapper.objectToSQL(meseRif));      
    
    if(lineeString==null){ 
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" = ").append(JDBCDataMapper.objectToSQL(codLinea)); 
    } else{
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" IN ( ").append(lineeString).append(" )");
    }


    s.append(" GROUP BY ").append(IndicatoriOeeSett.FLDZCANNO).append(",").append(IndicatoriOeeSett.FLDZCMESE).append(      
             " ORDER BY ").append(IndicatoriOeeSett.FLDZCANNO).append(",").append(IndicatoriOeeSett.FLDZCMESE);
      
    return s.toString();
  }

  
  private String getSelectSettMeseCorr(String codLinea,Integer annoRif,Integer meseRif,Integer settRif,String lineeString) {
    StringBuilder s=new StringBuilder();
    s.append(" SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(" , ").append(
            JDBCDataMapper.objectToSQL(codLinea)).append(" , ").append(
        "'WK'||").append(IndicatoriOeeSett.FLDZCSETT).append(" ,  MIN( ").append(
        IndicatoriOeeSett.FLDZCDTRN).append(") , ").append(
        getSubSelectOeeSett()).append(" , current timestamp ").append(
      "\n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeSett.TABLEOEESETT).append(
      "\n WHERE 1=1 ").append(
      " AND ").append(IndicatoriOeeSett.FLDZCCONO).append(" = ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(                   
      " AND ").append(IndicatoriOeeSett.FLDZCANNO).append(" = ").append(JDBCDataMapper.objectToSQL(annoRif)).append(
      " AND ").append(IndicatoriOeeSett.FLDZCMESE).append(" = ").append(JDBCDataMapper.objectToSQL(meseRif)).append(
     " AND ").append(IndicatoriOeeSett.FLDZCSETT).append(" < ").append(JDBCDataMapper.objectToSQL(settRif));
//      " AND ").append(IndicatoriOeeSett.FLDZCSETT).append(" > ").append(JDBCDataMapper.objectToSQL(settRif));
      
    
    if(lineeString==null){ 
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" = ").append(JDBCDataMapper.objectToSQL(codLinea)); 
    } else{
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" IN ( ").append(lineeString).append(" )");
    }

    s.append(" GROUP BY ").append(IndicatoriOeeSett.FLDZCANNO).append(
             ",").append(IndicatoriOeeSett.FLDZCMESE).append(",").append(IndicatoriOeeSett.FLDZCSETT).append(      
            " ORDER BY ").append(IndicatoriOeeSett.FLDZCANNO).append(
             ",").append(IndicatoriOeeSett.FLDZCMESE).append(",").append(IndicatoriOeeSett.FLDZCSETT);
    
    return s.toString();
  }
  
  
  private String getSelectGgSettCorr(String codLinea ,Date dataIni ,Date dataFin,String lineeString) {
    StringBuilder s=new StringBuilder();
    s.append(" SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(" , ").append(
            JDBCDataMapper.objectToSQL(codLinea)).append(" , ").append(
            " SUBSTR(").append(IndicatoriOeeGgBean.FLDZODTRF).append(",7,2)||'/'||SUBSTR("
            ).append(IndicatoriOeeGgBean.FLDZODTRF).append(",5,2) , ").append(
        IndicatoriOeeGgBean.FLDZODTRF).append(" , ").append(
        getSubSelectOeeGg()).append(" , current timestamp ").append(
      "\n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeGgBean.TABLEOEEGG).append(
      "\n WHERE 1=1 ").append(
      " AND ").append(IndicatoriOeeGgBean.FLDZOCONO).append(" = ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(                   
      " AND ").append(IndicatoriOeeGgBean.FLDZODATA).append(" between ").append(JDBCDataMapper.objectToSQL(dataIni)).append(
      " AND ").append(JDBCDataMapper.objectToSQL(dataFin));
    
      
          
    if(lineeString==null){ 
      s.append(" AND ").append(IndicatoriOeeGgBean.FLDZOPLGR).append(" = ").append(JDBCDataMapper.objectToSQL(codLinea)); 
    } else{
      s.append(" AND ").append(IndicatoriOeeGgBean.FLDZOPLGR).append(" IN ( ").append(lineeString).append(" )");
    }

    s.append(" GROUP BY ").append(IndicatoriOeeGgBean.FLDZODTRF).append(
             " ORDER BY ").append(IndicatoriOeeGgBean.FLDZODTRF);      
      
    return s.toString();
  }
  
  
  
  private String getSelectAnnoCorr(String codLinea,Integer annoRif,String lineeString) {
    StringBuilder s=new StringBuilder();
    s.append(" SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(" , ").append(
            JDBCDataMapper.objectToSQL(codLinea)).append(" , DIGITS (").append(
        IndicatoriOeeSett.FLDZCANNO).append(") , ").append(
        " INT( ").append(IndicatoriOeeSett.FLDZCANNO).append("||'1231' ) ").append(" , ").append(
        getSubSelectOeeSett()).append(" , current timestamp ").append(
      "\n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeSett.TABLEOEESETT).append(
      "\n WHERE 1=1 ").append(
      " AND ").append(IndicatoriOeeSett.FLDZCCONO).append(" = ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(                   
      " AND ").append(IndicatoriOeeSett.FLDZCANNO).append(" = ").append(JDBCDataMapper.objectToSQL(annoRif));
      
    
    if(lineeString==null){ 
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" = ").append(JDBCDataMapper.objectToSQL(codLinea)); 
    } else{
      s.append(" AND ").append(IndicatoriOeeSett.FLDZCPLGR).append(" IN ( ").append(lineeString).append(" )");
    }
    
    s.append(" GROUP BY ").append(IndicatoriOeeSett.FLDZCANNO);      
    
    return s.toString();
  }
  
  
  private String getSubSelectOeeSett(){
    StringBuilder sb=new StringBuilder(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTGUA, IndicatoriOeeSett.FLDZCTPRI)).append(",").append( 
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTSET, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTSE2, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTPGE, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTMIF, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTVRI, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTRIL, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTSCA, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTSC2, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTRUN, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTRN2, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTRN3, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTEX1, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTEX2, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTEX3, IndicatoriOeeSett.FLDZCTPRI)).append(",").append(    
        getStringPercStdSum(IndicatoriOeeSett.FLDZCTPNR, IndicatoriOeeSett.FLDZCTPRI));
    
    return sb.toString();
  }
  

  private String getSubSelectOeeGg(){
    StringBuilder sb=new StringBuilder(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTGUA, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append( 
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTSET, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTSE2, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTPGE, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTMIF, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTVRI, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTRIL, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTSCA, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(  
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTSC2, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTRUN, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTRN2, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTRN3, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTEX1, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTEX2, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTEX3, IndicatoriOeeGgBean.FLDZOTPRI)).append(",").append(    
        getStringPercStdSum(IndicatoriOeeGgBean.FLDZOTPNR, IndicatoriOeeGgBean.FLDZOTPRI));
    
    return sb.toString();
  }
  
  
  
}