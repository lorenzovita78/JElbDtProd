/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOeeGg.G1P0;


import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.costant.OeeCostant;
import colombini.exception.OEEException;
import colombini.query.indicatoriOee.linee.QueryiSagomatiG1P0ArticoliSpunt;
import colombini.query.indicatoriOee.QueryTempiCicloArticoli;
import db.ResultSetHelper;
import colombini.indicatoriOee.calc.ICalcIndicatoriOeeLinea;
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
import utils.ListUtils;

/**
 *
 * @author lvita
 */
public class CalcDtProdPantografi {

  
  private static CalcDtProdPantografi instance;
  
  private CalcDtProdPantografi(){
    
  }
  
  public static CalcDtProdPantografi getInstance(){
    if(instance==null){
      instance= new CalcDtProdPantografi();
    }
    
    return instance;
  }
  
  
  /**
   * Dato un pantografo e indicato un giorno torna il tempo di runtime della giornata
   * @param Date data
   * @param String centroLavoroMovex
   * @return Long tempo di Runtime approssimato ai secondi
   * @throws OEEException 
   */
//  public Long calcolaRunTimePantografoGg(Date data,String centroLavoroMovex) throws OEEException,SQLException{
//    ArrayList dati=loadDatiFromBitaBit(centroLavoroMovex,);
//    Long tempoRunTime = Long.valueOf(0);
//    
//    if(dati==null || dati.isEmpty())
//      return tempoRunTime;
//    
//    for(int i=0;i<dati.size();i++){
//      List record = (List) dati.get(i);
//      String articolo=ClassMapper.classToString(record.get(0));
//      Long numArticoliCalc=ClassMapper.classToClass(record.get(1), Long.class);
//      
//      Double runtimeArticolo=getTempoCicloMovex(centroLavoroMovex, articolo);
//      Double runtimeTot=numArticoliCalc*runtimeArticolo;
//      tempoRunTime+=runtimeTot.longValue();
//      
//    }
//    
//    return tempoRunTime;
//    
//  }
  
  
  
  /**
   * Calcola il tempo di runtime se gli viene passato la lista di articoli processati con le rispettive quantità
   * @param List dati
   * @return Long tempo di Runtime approssimato ai secondi
   * @throws OEEException 
   */
  public Long calcolaRunTimePantografo(ArrayList dati,String centroLavoroMovex) throws OEEException{
    Long tempoRunTime = Long.valueOf(0);
    Connection con=null;
    
    if(dati==null || dati.isEmpty())
      return tempoRunTime;
    
    try{
      con=ColombiniConnections.getAs400ColomConnection();
      for(int i=0;i<dati.size();i++){
        List record = (List) dati.get(i);
        String articolo=ClassMapper.classToString(record.get(0));
        Long numArticoli=ClassMapper.classToClass(record.get(1), Long.class);

        Double runtimeArticolo=getTempoCicloMovex(con,centroLavoroMovex, articolo);
        Double runtimeTot=numArticoli*runtimeArticolo;
        tempoRunTime+=runtimeTot.longValue();
      
      }
    } catch (SQLException ex) {
        _logger.error("Impossibile calcolare il tempo ciclo per il centro di lavoro:"+centroLavoroMovex+" :"+ex.getMessage());
        throw new OEEException(ex);
    } finally{
      if(con!=null){
        try {
          con.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura della connessione");
          throw new OEEException(ex);
        }
      }
    }
    return tempoRunTime;
    
  }
  
  /**
   * Calcola il tempo di runtime se gli viene passato la lista di articoli processati con le rispettive quantità
   * @param Connection con
   * @param ArrayList dati
   * @param String centroLavoroMovex
   * @return Long  tempo di Runtime approssimato ai secondi
   * @throws OEEException 
   */
  public Long calcolaRunTimePantografo(Connection con ,ArrayList dati,String centroLavoroMovex) throws OEEException{
    Long tempoRunTime = Long.valueOf(0);
    
    if(dati==null || dati.isEmpty())
      return tempoRunTime;
    
    
    for(int i=0;i<dati.size();i++){
        List record = (List) dati.get(i);
        String articolo=ClassMapper.classToString(record.get(0));
        Long numArticoli=ClassMapper.classToClass(record.get(1), Long.class);

        Double runtimeArticolo=getTempoCicloMovex(con,centroLavoroMovex, articolo);
        Double runtimeTot=numArticoli*runtimeArticolo;
//        System.out.println(" Articolo: "+articolo+" -> "+numArticoliCalc+" tc: "+runtimeArticolo +" tot: "+runtimeTot.toString().replace(".", ",") );
        tempoRunTime+=runtimeTot.longValue();
      
     }
    
     return tempoRunTime;
  }
  
  public Long calcolaRunTimePantografo(Connection con ,ArrayList dati,String centroLavoroMovex,Map percentuali) throws OEEException{
    Long tempoRunTime = Long.valueOf(0);
    
    if(dati==null || dati.isEmpty())
      return tempoRunTime;
    
    
    for(int i=0;i<dati.size();i++){
        List record = (List) dati.get(i);
        String articolo=ClassMapper.classToString(record.get(0));
        Long numArticoli=ClassMapper.classToClass(record.get(1), Long.class);
        
//        System.out.print("Articolo: "+articolo+" -> "+numArticoliCalc);
        Double runtimeArticolo=getTempoCicloMovex(con,centroLavoroMovex, articolo,(Map)percentuali.get(centroLavoroMovex));
        Double runtimeTot=numArticoli*runtimeArticolo;
//        System.out.print(" TOT: "+runtimeTot.toString().replace(".", ",")+" \n" );
        tempoRunTime+=runtimeTot.longValue();
      
     }
    
     return tempoRunTime;
  }
  
  
  //-------new
  
  public Map calcolaDatiProdPantografo(Connection con ,ArrayList dati,String centroLavoroMovex,Map percentuali,Map sagome) throws OEEException{
//    List info=new ArrayList();
    Double runtimeUff=Double.valueOf(0);
    Double runtimeLord=Double.valueOf(0);
    Double tempoRilav=Double.valueOf(0);
    Double tempoScarti=Double.valueOf(0);
    Long totPz=Long.valueOf(0);
    Long totScarti=Long.valueOf(0);
    List warning=new ArrayList();
    Map infoMap=new HashMap();
    
    if(dati==null || dati.isEmpty())
      return null;
    
    _logger.info("Pantografo :"+centroLavoroMovex);
    
    for(int i=0;i<dati.size();i++){
        List record = (List) dati.get(i);
        String articolo=ClassMapper.classToString(record.get(0));
        
        Long numArticoliEff=ClassMapper.classToClass(record.get(1), Long.class);
        Long numRilavorati=ClassMapper.classToClass(record.get(2), Long.class);
        Long numScarti=ClassMapper.classToClass(record.get(3), Long.class);
        
//        if(centroLavoroMovex.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110)) || 
//                centroLavoroMovex.equals(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146))){
//           Long var=numArticoliCalc/2;
//           var=var*2;
//           var=numArticoliCalc-var;
//           numArticoliCalc+=var;
//        }
        Integer numSag=(Integer) sagome.get(articolo);
        if(numSag==null){
          warning.add("Nessun moltiplicatore associato all'articolo : "+articolo);
          numSag=Integer.valueOf(1);
        }
        Long var=(numArticoliEff/numSag);
        Long var2=(numArticoliEff%numSag);
        if(var2>0)
          var++;
        var=var*numSag;
        var=var-numArticoliEff;
        Long numArticoliCalc=numArticoliEff+var;
        
        
        List tempi=getTempiCicloMovex(con,centroLavoroMovex, articolo,(Map)percentuali.get(centroLavoroMovex));
        if(tempi==null || tempi.isEmpty()){
          _logger.info(" ARTICOLO: "+articolo +"  - QTA:"+numArticoliCalc +" NO TEMPO CICLO !!");
          System.out.println(" ARTICOLO: "+articolo +"  - QTA:"+numArticoliCalc +" NO TEMPO CICLO !!");
        }
        
        if(tempi!=null && tempi.size()>0){
          Double ttmp1=(Double)tempi.get(0);
          Double ttmp2=(Double)tempi.get(1);
          String passegnato=(String) tempi.get(2);
          
          _logger.info(" ARTICOLO: "+articolo +" - QTA REALE:"+numArticoliEff + " QTA ASSG :"+numArticoliCalc +" NRIL :"+numRilavorati+" NSCARTI :"+numScarti+
                       " --> PANT_ASSEGNATO :"+passegnato+" TUFF: "+ttmp1.toString().replace(".", ",")+" TNEW: "+ttmp2.toString().replace(".", ","));
          
          System.out.println(" ARTICOLO: "+articolo +" - QTA:"+numArticoliCalc + " NRIL :"+numRilavorati+" NSCARTI :"+numScarti+
                       " --> PANT_ASSEGNATO :"+passegnato+" TUFF: "+ttmp1.toString().replace(".", ",")+" TNEW: "+ttmp2.toString().replace(".", ","))
                  ;
          runtimeUff+=(numArticoliCalc*ttmp1);
          runtimeLord+=(numArticoliCalc*ttmp2);
          
          tempoRilav+=(numRilavorati*ttmp1);
          tempoRilav+=(numRilavorati*ttmp2);
          
          tempoScarti+=(numScarti*ttmp1);
          tempoScarti+=(numScarti*ttmp2);
          totScarti+=numScarti;
          totPz+=numArticoliEff;
        }
     }
    
     infoMap.put(CostantsColomb.TRUNTIME, runtimeUff);
     infoMap.put(CostantsColomb.TRUN2, runtimeLord);
     infoMap.put(CostantsColomb.TRILAVORAZIONI, tempoRilav);
     infoMap.put(CostantsColomb.TSCARTI,tempoScarti);
     infoMap.put(CostantsColomb.NPZTOT, totPz);
     infoMap.put(CostantsColomb.NSCARTI, totScarti);
     infoMap.put(ICalcIndicatoriOeeLinea.LISTWARNINGS, warning);
     
//     info.add(runtimeUff);
//     info.add(runtimeLord);
//     info.add(tempoRilav);
//     info.add(tempoScarti);
//     info.add(totPz);
//     info.add(totScarti);
     
     return infoMap;
  }
  
  
  private List getTempiCicloMovex(Connection con,String centroLavoroMovex,String articolo,Map percentuali) throws OEEException{
    Double tempoCicloIni=Double.valueOf(0);
    Double tempoCicloFin=Double.valueOf(0);
    List tc=new ArrayList();
    String pantografo="";
    QueryTempiCicloArticoli qry=new QueryTempiCicloArticoli();
    qry.setFilter(QueryTempiCicloArticoli.CODICEPADRE, articolo);
    qry.setFilter(QueryTempiCicloArticoli.AZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(QueryTempiCicloArticoli.FACILITY, "F01");//facility che indica Galazzano da rendere costante
    qry.setFilter(QueryTempiCicloArticoli.LISTACENTRI,getStringListPantografi());
    try {
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>1){
        tempoCicloIni=ClassMapper.classToClass(obj[0], Double.class);
        pantografo=ClassMapper.classToString(obj[1]); 
      
        String campoP="P"+pantografo.trim();
        Double perc=ClassMapper.classToClass(percentuali.get(campoP),Double.class); 
        tempoCicloFin=tempoCicloIni*perc;

        if(tempoCicloIni.compareTo(tempoCicloFin)==0){
          tc.add(tempoCicloIni);
          tc.add(new Double(0));
        }else{
          tc.add(new Double(0));
          tc.add(tempoCicloFin);
        }
        tc.add(pantografo.trim());
      }
    } catch (QueryException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    } catch (SQLException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    }
    
    return tc;
  }
  
  
  
  
  /**
   * Torna  il tempo ciclo su Movex relativo per un determinato centro di lavoro e un certo articolo
   * @param String centroLavoroMovex
   * @param String articolo
   * @return Double tempoCiclo
   * @throws OEEException 
   */
  private Double getTempoCicloMovex(String centroLavoroMovex,String articolo) throws OEEException{
    Connection con=null;
    
    try{
      con=ColombiniConnections.getAs400ColomConnection();
    } catch (SQLException ex) {
        _logger.error(ex.getMessage());
      
    } finally{
      if(con!=null){
        try {
          con.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura della connessione");
          throw new OEEException(ex);
        }
      }
    }
    
    return getTempoCicloMovex(con, centroLavoroMovex, articolo);
  }
  
  /**
   * Torna  il tempo ciclo di Movex relativo per un determinato centro di lavoro e un certo articolo
   * @param Connection con
   * @param String centroLavoroMovex
   * @param String articolo
   * @return Double tempoCiclo
   * @throws OEEException 
   */
  private Double getTempoCicloMovex(Connection con,String centroLavoroMovex,String articolo) throws OEEException{
    Double tempoCiclo=Double.valueOf(0);
    
    QueryTempiCicloArticoli qry=new QueryTempiCicloArticoli();
    qry.setFilter(QueryTempiCicloArticoli.CODICEPADRE, articolo);
    qry.setFilter(QueryTempiCicloArticoli.AZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(QueryTempiCicloArticoli.FACILITY, "F01");//facility che indica Galazzano da rendere costante
    qry.setFilter(QueryTempiCicloArticoli.LISTACENTRI,getStringListPantografi());
    try {
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>0)
        tempoCiclo=ClassMapper.classToClass(obj[0], Double.class);
      
    } catch (QueryException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    } catch (SQLException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    }
    
    return tempoCiclo;
  }
  
  
  /**
   * Torna  il tempo ciclo di Movex relativo per un determinato centro di lavoro e un certo articolo
   * Per il calcolo del tempo ciclo prende in considerazione la mappa delle percetuali dei tempi 
   * applicando la percentuale corrispondente al pantografo non proprietario su cui è stato eseguito l'articolo
   * @param String con
   * @param String centroLavoroMovex
   * @param String articolo
   * @param Map percentuali
   * @return
   * @throws OEEException 
   */
  private Double getTempoCicloMovex(Connection con,String centroLavoroMovex,String articolo,Map percentuali) throws OEEException{
    Double tempoCiclo=Double.valueOf(0);
    String pantografo="";
    QueryTempiCicloArticoli qry=new QueryTempiCicloArticoli();
    qry.setFilter(QueryTempiCicloArticoli.CODICEPADRE, articolo);
    qry.setFilter(QueryTempiCicloArticoli.AZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(QueryTempiCicloArticoli.FACILITY, "F01");//facility che indica Galazzano da rendere costante
    qry.setFilter(QueryTempiCicloArticoli.LISTACENTRI,getStringListPantografi());
    try {
      Object [] obj=ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
      if(obj!=null && obj.length>1){
        tempoCiclo=ClassMapper.classToClass(obj[0], Double.class);
        pantografo=ClassMapper.classToString(obj[1]); 
      
        String campoP="P"+pantografo.trim();
        Double perc=ClassMapper.classToClass(percentuali.get(campoP),Double.class);
//        System.out.print(" TCICLOINI: "+tempoCiclo);
        
        tempoCiclo=tempoCiclo*perc;
//        System.out.print(" Pantografo: "+ pantografo + "TCICLONEW: "+tempoCiclo);
      }
    } catch (QueryException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    } catch (SQLException ex) {
        _logger.error("Errore nel reperimento dei dati da Movex per codicepadre: "+articolo+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    }
    
    return tempoCiclo;
  }
  
  
  /**
   * Torna la lista degli articoli prodotti da un determinato pantografo in un determinato giorno
   * @param data
   * @param centroLavoroMovex
   * @return
   * @throws OEEException 
   */
  public ArrayList loadDatiFromBitaBit(String centroLavoroMovex,Date dataInizio,Date dataFine) throws OEEException,SQLException{
    Connection con = null;
    ArrayList list=null;
    try{
      // attenzione i parametri di connessione sono scritti direttamente dentro al codice
      // sarà necessario prevedere un file di config
      con=ColombiniConnections.getDbBitaBitConnection();
      list=loadDatiFromBitaBit(con,centroLavoroMovex, dataInizio,dataFine );
    } finally{
      if(con!=null){
        try {
          con.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura della connessione");
          throw new OEEException(ex);
        }
      }
    }
    return list;
  }
  
  /**
   * Torna la lista degli articoli prodotti da un determinato pantografo in un determinato giorno
   * @param con
   * @param data
   * @param centroLavoroMovex
   * @return
   * @throws OEEException 
   */
  public ArrayList loadDatiFromBitaBit(Connection con,String centroLavoroMovex,Date dataInizio,Date dataFine) throws OEEException{
    String dataS="";
    ArrayList result=new ArrayList();
    
    String centroBit=OeeCostant.getCentroBitaBit(centroLavoroMovex);
    try {
      dataS=DateUtils.DateToStr(dataInizio, "yyyyMMdd");
//      oraini=DateUtils.DateToStr(dataInizio, "yyyyMMdd HH:mm:ss");
//      orafin=DateUtils.DateToStr(dataFine, "yyyyMMdd HH:mm:ss");
//       dtini=dataS+" 06:00:00";
//      dtfin=dataS+" 13:14:59";
      
      QueryiSagomatiG1P0ArticoliSpunt qry=new QueryiSagomatiG1P0ArticoliSpunt();
      qry.setFilter(QueryiSagomatiG1P0ArticoliSpunt.CENTROBITABIT, centroBit);
      qry.setFilter(QueryiSagomatiG1P0ArticoliSpunt.DATASTRING, dataS);
      //      qry.setFilter(QueryArticoliSpuntati.ORAINIZIO, oraini);
//      qry.setFilter(QueryArticoliSpuntati.ORAFINE, orafin);
      
      String query=qry.toSQLString();
      _logger.info(query);
      ResultSetHelper.fillListList(con, query, result);
      
    } catch (ParseException ex) {
      _logger.error("Impossibile convertire la data "+dataInizio.toString()+" per il centro di lavoro "+centroLavoroMovex);
      throw new OEEException(ex);
    } catch (QueryException ex) {
      _logger.error("Problemi nell'esecuzione della queryper il cdL "+centroLavoroMovex);
      throw new OEEException(ex);
    
    } catch (SQLException ex) {
        _logger.error("Errore nel reperimento dei dati di Bit a bit  per il giorno: "+dataInizio.toString()+"  - centro di lavoro: "+centroLavoroMovex);
       throw new OEEException(ex);
    }
    
    return result;
  
  }
  
  
  
 private String getStringListPantografi(){
   String listaPant=ListUtils.toCommaSeparatedString(OeeCostant.getCentriPantografi());
   listaPant=listaPant.replace(",", "','");
   listaPant="'"+listaPant+"'";
   
   return listaPant;
 }
  
  
 private static final Logger _logger = Logger.getLogger(CalcDtProdPantografi.class);   
}

  
