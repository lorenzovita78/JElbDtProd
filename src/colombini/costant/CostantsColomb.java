/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.costant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lvita
 */
public class CostantsColomb {
  
  //tempi
  public final static Long TEMPOTOTS=new Long(60*60*24); //totale sec di un gg considerando 24h
  public final static Long TEMPOTOT2TURNISEC=new Long(52200);//14,5 h * 3600 sec
  public final static Long TEMPOTOT1TURNOSEC=new Long(26100);//14,5 h * 3600 sec
  public final static Long TEMPOTOT2TURNIMIN=new Long(870);//14,5 =870 min
  
  public static final Integer MINUTIGG=Integer.valueOf(1440);
  public static final Integer MIN2359=Integer.valueOf(1439);
  
  //VALORI DI DEFAULT
  public final static Integer AZCOLOM=Integer.valueOf(30);
  public final static Integer AZFEBAL=Integer.valueOf(10);
  public final static String UTDEFAULT="UTJBATCH";
  
  
  //Stabilimenti
  public static final String GALAZZANO="G1";
  public static final String ROVERETA1="R1";
  public static final String ROVERETA2="R2";
  
  //Piani
  public static final String PIANO0="P0";
  public static final String PIANO1="P1";
  public static final String PIANO2="P2";
  public static final String PIANO3="P3";
  public static final String PIANO4="P4";
 
  //costanti per tempi
 
  public final static String TDISPON="TDISPON";
  public final static String TIMPROD="TIMPROD";
  public final static String TPRODUZ="TPRODUZ";
  public final static String TGUASTI="TGUASTI";
  public final static String TSETUP="TSETUP";
  public final static String TSCARTI="TSCARTI";
  public final static String TVELRIDOTTA="TVELRIDOTTA";
  public final static String TMICROFERMI="TMICROFERMI";
  public final static String TRILAVORAZIONI="TRILAVORAZIONI";
  public final static String TPERDGEST="TPERDGEST";
  public final static String TFERMI="TFERMI";
  public final static String TRUNTIME="TRUNTIME";
  public final static String TRUN2="TRUN2";
  public final static String TRUN3="TRUN3";
  
  public final static String TEXT1="TEXTRA1";
  public final static String TEXT2="TEXTRA2";
  public final static String TEXT3="TEXTRA3";
  
  public final static String OEE="OEE";
  public final static String TEEP="TEEP";
  public final static String TLORDO="TLORDO";
  public final static String TPNRO="TPNRO";
  public final static String NPZTURNI="NPZTURNI";
  public final static String NSCARTI="NSCARTI";
  public final static String NCICLI="NCICLI";
  public final static String NGUASTI="NGUASTI";
  public final static String NPZTOT="NPZTOT";
  public final static String LISTFERMI="LISTFERMI";
 
  public final static String TAVVIO="TAVVIO";
  public final static String  NPEDANE="NPEDANE";
  
  public final static String WARNINGS="WARNINGS";
  public final static String ERRORS="ERRORS";
  //nuovi valori
  
  ///-----
  
  public final static String BUFEBAL="FB1";
  
  public final static String BUARTEC="A03";
  public final static String BUIDEA="J08";
  
  public final static String CODDITTAFEBAL="B";
  
  
  
  
  //---GESTIONE NUMERICOMMESSA
  public static final Integer NUMMAX_COMMESSA=360;
  public static final Integer NUMMIN_COMMESSA=7;
  
  public static final Integer VALINI_MINICOMMESSA=Integer.valueOf(797);
  public static final Integer VALBASE_MINICOMMESSA=Integer.valueOf(800);
  public static final Integer NUMGG_MINICOMMESSA=Integer.valueOf(196);
  //---------
  
  
  public static Integer getMiniCommessaValue(Integer commessa){
    Integer value= CostantsColomb.VALBASE_MINICOMMESSA+commessa;
    
    if(commessa>NUMGG_MINICOMMESSA){
      //se siamo dopo il 15 Luglio la numerazione riparte da 797+
      // se siamo prima del 15 Luglio la numerazione parte da 800 
      value=CostantsColomb.VALINI_MINICOMMESSA+commessa;  
     } 
     return value;   
  }
  
  public static Boolean isCommessa(Integer numero){
    if(numero>=NUMMIN_COMMESSA && numero<=NUMMAX_COMMESSA) 
     return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  /**
   * Torna la lista degli stabilimenti Colombini
   * @return  List
   */
  public static List<String> getListStabilimenti(){
    List l=new ArrayList();
    l.add(CostantsColomb.GALAZZANO);
    l.add(CostantsColomb.ROVERETA1);
    l.add(CostantsColomb.ROVERETA2);
    
    return l;
  }
  
  /**
   * Torna la lista dei piani presenti negli stabilimenti Colombini
   * @return  List
   */
  public static List<String> getListPiani(){
    List l=new ArrayList();
    l.add(CostantsColomb.PIANO0);
    l.add(CostantsColomb.PIANO1);
    l.add(CostantsColomb.PIANO2);
    l.add(CostantsColomb.PIANO3);
    l.add(CostantsColomb.PIANO4);
    
    return l;
  }
  
}
