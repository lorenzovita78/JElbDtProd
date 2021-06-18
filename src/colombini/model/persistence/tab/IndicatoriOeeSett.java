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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ListUtils;
import db.persistence.ITable;

/**
 *
 * @author lvita
 */
public class IndicatoriOeeSett implements ITable {
  
  public final static String ANNO="ANNO";
  public final static String MESE="MESE";
  public final static String SETTIMANA="SETT";
  public final static String DATAINI="DATAINI";
  public final static String DATAFIN="DATAFIN";
  public final static String LISTGGLAV="LISTGGLAV";
  
  
  public final static String TABLEOEESETT="ZIOEEW";
  public final static String TABLEOEESETTFIELDS="ZCCONO,ZCPLGR,ZCANNO,ZCMESE,ZCSETT,ZCDTRN,ZCTTOT,ZCTDII,ZCTIMI,ZCTPRI,ZCTGUA,ZCTSET,ZCTSE2,ZCTPGE,ZCTMIF,ZCTVRI,ZCTRIL,ZCTSCA,ZCTSC2,ZCTRUN,ZCTRN2,ZCTRN3,ZCTEX1,ZCTEX2,ZCTEX3,ZCTPNR,ZCNPZT,ZCNPZP,ZCNGUA,ZCNSCA,ZCNRIP,ZCNBND,ZCNCCL,ZCPOEE,ZCTEEP,ZCMTBF,ZCMTTR,ZCRGDT";
  public final static String TABLEOEESETTKEYFIELDS="ZCCONO,ZCPLGR,ZCANNO,ZCMESE,ZCSETT";
  
  public final static String FLDZCCONO="ZCCONO";
  public final static String FLDZCPLGR="ZCPLGR";
  public final static String FLDZCANNO="ZCANNO";
  public final static String FLDZCMESE="ZCMESE"; 
  public final static String FLDZCSETT="ZCSETT"; 
  public final static String FLDZCDTRN="ZCDTRN"; 
  public final static String FLDZCTTOT="ZCTTOT";
  public final static String FLDZCTDII="ZCTDII";
  public final static String FLDZCTIMI="ZCTIMI";
  public final static String FLDZCTPRI="ZCTPRI";
  public final static String FLDZCTGUA="ZCTGUA";
  public final static String FLDZCTSET="ZCTSET";
  public final static String FLDZCTSE2="ZCTSE2";
  public final static String FLDZCTPGE="ZCTPGE";
  public final static String FLDZCTMIF="ZCTMIF";
  public final static String FLDZCTVRI="ZCTVRI";
  public final static String FLDZCTRIL="ZCTRIL";
  public final static String FLDZCTSCA="ZCTSCA";
  public final static String FLDZCTSC2="ZCTSC2";
  public final static String FLDZCTRUN="ZCTRUN";
  public final static String FLDZCTRN2="ZCTRN2";
  public final static String FLDZCTRN3="ZCTRN3";
  public final static String FLDZCTEX1="ZCTEX1";
  public final static String FLDZCTEX2="ZCTEX2";
  public final static String FLDZCTEX3="ZCTEX3";
  public final static String FLDZCTPNR="ZCTPNR";
  public final static String FLDZCNPZT="ZCNPZT";
  public final static String FLDZCNPZP="ZCNPZP";
  public final static String FLDZCNGUA="ZCNGUA";
  public final static String FLDZCNSCA="ZCNSCA";
  public final static String FLDZCNRIP="ZCNRIP";
  public final static String FLDZCNBND="ZCNBND";
  public final static String FLDZCNCCL="ZCNCCL";
  public final static String FLDZCPOEE="ZCPOEE";
  public final static String FLDZCTEEP="ZCTEEP";
  public final static String FLDZCMTBF="ZCMTBF";
  public final static String FLDZCMTTR="ZCMTTR";
  public final static String FLDZCRGDT="ZCRGDT";
  
  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLEOEESETT;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABLEOEESETTFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABLEOEESETTKEYFIELDS.split(","));
  }

  @Override
  public List<Integer> getFieldTypes() {
   List<Integer> types=new ArrayList();
    
    types.add(Types.INTEGER);  //az
    types.add(Types.CHAR);     //centro di lavoro
    types.add(Types.INTEGER);  //anno
    types.add(Types.INTEGER);  //mese
    types.add(Types.INTEGER);  //sett
    types.add(Types.NUMERIC);  //dataRifN
    
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
  
  
  public void consolidamentoDatiSett(Connection con,Map parameterWeek,String codLinea) throws SQLException{
    consolidamentoDatiSett(con, parameterWeek, codLinea, Boolean.TRUE);
  }
  
  
  
  public void consolidamentoDatiSett(Connection con,Map parameterWeek,String codLinea,Boolean ggLav) throws SQLException{
    PersistenceManager pm=new PersistenceManager(con);
    deleteWeek(pm, parameterWeek, codLinea);
    insertWeek(pm, parameterWeek, codLinea, ggLav);
  }

  
  private void deleteWeek(PersistenceManager pm,Map parameterWeek,String codLinea) throws SQLException{
    Map keyValues=new HashMap();
    keyValues.put(FLDZCCONO, CostantsColomb.AZCOLOM);
    keyValues.put(FLDZCPLGR, codLinea);
    keyValues.put(FLDZCANNO, parameterWeek.get(ANNO));
    keyValues.put(FLDZCSETT, parameterWeek.get(SETTIMANA));
    
    
    
    pm.deleteDt(this, keyValues);
  }
  
  private void insertWeek(PersistenceManager pm,Map parameterWeek,String codLinea,Boolean ggLav) throws SQLException {
    String insert=pm.getInsertStatment(this);
    insert=insert.substring(0, insert.lastIndexOf("VALUES"));
    
    String select=getSelect(parameterWeek,codLinea,ggLav);
    insert+=" \n "+select;
    _logger.debug(insert);
    PreparedStatement st=pm.getConnection().prepareStatement(insert);
    st.execute();
    st.close();
  }
  
  private String getSelect(Map parameterWeek, String codLinea,Boolean ggLav) {
    StringBuilder select=new StringBuilder();
    
    select.append("SELECT ").append(JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)).append(
                  " , ").append(JDBCDataMapper.objectToSQL(codLinea)).append(    //CENTRO
                  " , ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(ANNO))).append(                  //ANNO 
                  " , ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(MESE))).append(                  //MESE 
                  " , ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(SETTIMANA))).append(             //SETT 
                  " , ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(DATAINI))).append(             //SETT 
                  "\n , ").append(JDBCDataMapper.objectToSQL(CostantsColomb.TEMPOTOTS*7)).append(           //tempo totale di una settimana
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTDII).append(" )").append(           //TDII
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTIMI).append(" )").append(         //TIMI
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTPRI).append(" )").append(           //TIMI
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTGUA).append(" )").append(          
                  " \n ,").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTSET).append(" )").append(            
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTSE2).append(" )").append(          
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTPGE).append(" )").append(       
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTMIF).append(" )").append(       
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTVRI).append(" )").append(          
                  " \n ,").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTRIL).append(" )").append(              
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTSCA).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTSC2).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTRUN).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTRN2).append(" )").append(           
                  " \n , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTRN3).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTEX1).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTEX2).append(" )").append(           
                  " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZOTEX3).append(" )").append(           
                  " , ").append(getStringPerdNonRil()).append(                                       
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONPZT).append(" )").append(           
                 " \n , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONPZP).append(" )").append(            
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONGUA).append(" )").append(                     
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONSCA).append(" )").append(           
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONRIP).append(" )").append(           
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONBND).append(" )").append(           
                 " , ").append(" SUM( ").append(IndicatoriOeeGgBean.FLDZONCCL).append(" )").append(           
            " , ").append( getStringOEE() ).append(                                            
                 " , ").append( getStringTEEP() ).append(                                            //TEEP 
                 " , ").append( getStringMTBF() ).append(                                            //MTBF
                 " , ").append( getStringMTTR() ).append(                                            //MTTR
                 " , current timestamp ").append(
                 " \n FROM ").append(ColombiniConnections.getAs400LibPersColom()).append(".").append(IndicatoriOeeGgBean.TABLEOEEGG).append(
                 "\n WHERE 1=1").append(
                 " and ZOPLGR =").append(JDBCDataMapper.objectToSQL(codLinea)).append(
                 "\n and ZODTRN between ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(DATAINI))).append(
                 " and ").append(JDBCDataMapper.objectToSQL(parameterWeek.get(DATAFIN)));
    
    if(ggLav){ 
      List listaGg=(List) parameterWeek.get(LISTGGLAV);
      String ggS=ListUtils.toCommaSeparatedString(listaGg);
      select.append(" AND ZODTRN IN ( ").append(ggS).append( " )"); 
            
    }        
    
    
    return select.toString();
  }
  
  private String getStringPerdNonRil(){
    StringBuffer str=new StringBuffer(" ( SUM ( ").append(IndicatoriOeeGgBean.FLDZOTPRI).append(" ) ").append( 
                                          " - SUM ( ").append(IndicatoriOeeGgBean.FLDZOTGUA).append(        
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTSET).append(
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTSE2).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTPGE).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTMIF).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTVRI).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTRIL).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTSCA).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTSC2).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTRUN).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTRN2).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTRN3).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTEX1).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTEX2).append( 
                                                  " + ").append(IndicatoriOeeGgBean.FLDZOTEX3).append( 
                                                  " )   ) TPNRO");
    
    return str.toString();
  }
  
  private String getStringOEE(){
    StringBuffer str=new StringBuffer(" cast( sum( ").append(IndicatoriOeeGgBean.FLDZOTRUN).append(" ) as dec(10 , 2) )  ").append( 
                                        "  / cast( sum( ").append(IndicatoriOeeGgBean.FLDZOTPRI).append( " ) as dec(10 , 2) ) *100  ");
                                                    
    return str.toString();
  }
  
  private String getStringTEEP(){
    StringBuffer str=new StringBuffer(" cast( sum( ").append(IndicatoriOeeGgBean.FLDZOTRUN).append(" ) as dec(10 , 2) )  ").append( 
                                      "  / cast ( ").append(JDBCDataMapper.objectToSQL(CostantsColomb.TEMPOTOTS*7)).append("  as dec(10 , 2) ) *100  ");
//                                        "  / cast( sum( ").append(IMapDtOee.CLN_TTOT).append( " ) as dec(10 , 2) ) *100  TEEP");
                                                    
    return str.toString();
  }
  
  private String getStringMTBF(){
    StringBuffer str=new StringBuffer(" case when SUM( ").append(IndicatoriOeeGgBean.FLDZONGUA).append(" )<=0 then 0").append( 
                                      " else ( (cast(sum( ").append(IndicatoriOeeGgBean.FLDZOTRUN).append(" ) as dec(10 , 2)) ").append(
                                      " / cast(sum( ").append(IndicatoriOeeGgBean.FLDZONGUA).append(" ) as dec(10 , 2)))) end MRBF");  
                                                    
    return str.toString();
  }
  
  private String getStringMTTR(){
    StringBuffer str=new StringBuffer(" case when SUM( ").append(IndicatoriOeeGgBean.FLDZONGUA).append(" )<=0 then 0").append( 
                                      " else ( (cast(sum( ").append(IndicatoriOeeGgBean.FLDZOTGUA).append(" ) as dec(10 , 2)) ").append(
                                      " / cast(sum( ").append(IndicatoriOeeGgBean.FLDZONGUA).append(" ) as dec(10 , 2))) )  end MTTR");  
                                                    
    return str.toString();
  }

  
  private static final Logger _logger = Logger.getLogger(IndicatoriOeeSett.class);
  
}
