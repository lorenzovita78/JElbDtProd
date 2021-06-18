/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class CalcoloTempoCiclo {
  
  private static final String OPZIONEINCLUDI="1";
  private static final String TIPODISEGNO="2";
  private static final String TIPOCARATTERISTICA="3";
  
  private final static String STMCOLLI="SELECT clnart,trim(CLARTI) articolo, trim(CLNROR) nordine,clriga riga"
                                      + " FROM mcobmoddta.SCXXXCOL"                             
                                      + " where CLNART<>0"
                                      + " and clcomm=?"
                                      + " and clncol=?";
  
  private static final String STMFACI= " select MWFACI FROM MPDWCT,MITWHL "
                                       +" WHERE PPCONO=MWCONO"
                                       +" and PPWHLO=MWWHLO "
                                       +"and PPCONO= ? AND  PPPLGR=?";
  
  
  private final String STMCONFIG=" SELECT obcfin "
                                       +" FROM ooline  "
                                       +" WHERE OBCONO=?"            
                                       +" and OBORNO=?"       
                                       +" and OBPONR=?" 
                                       +" and OBPOSX=?";
  
  
  private final String STMFEATURES=" select QJECVS,QJFTID,QJOPTN,QJOPNV"
                                         +" from MPDCDF "
                                         +" where 1=1 "
                                         +" and QJCONO=?"
                                         +" and QJCFIN=?";
  
  private final String STMDRAWINGS=" select QKDMID,QKMEVA,INT(QKMEVA) INTV "
                                         +" from MPDCDM "
                                         +" where 1=1 "
                                         +" and QKCONO=?"
                                         +" and QKCFIN=?";
  
  
  
  
  
  private final String STMMPDOPE=" select POOPNO,POPITI "
                                 +" from MVXBDTA.mpdope"
                                 +" where 1=1 "
                                 +" and pocono=?"
                                 +" and pofaci=?"
                                 +" and poprno=?"
                                 +" and poplgr=?";

  
  private final String STMMPDOMA=" select PNOPNO,PNFTID,PNOPTN,PNINCE,PNOTYP,PNNUVF,PNNUVT"
                                +" from MVXBDTA.MPDOMA"
                                +" where 1=1"
                                +" and pncono=?" 
                                +" and pnfaci=?"
                                +" and pnprno=?"
                                +" and pnopno=?";
  
  
  private final static String STMCOLLI2="SELECT clnart,trim(CLARTI) articolo, trim(CLNROR) nordine,clriga riga"
                                      + " FROM LVITA.SC2015COL "                             
                                      + " where 1=1 "
                                      + " and clcomm=?"
                                      + " and clncol=?"
                                      + " and CLNART<>0";
  

  private List listaColli;
  private String centroLavoro;
  private Map <String,List> articoliOperaz;
  private Map colliProc;
  private Map <String,List> dettOperazioniArt;
  private String facility;
  //tale valore dipende dal parametro occorrenze
  private Double tRilavorazioni=Double.valueOf(0);
  private Long numArticoli=Long.valueOf(0);
  
  
  public CalcoloTempoCiclo(String centroMovex) {
    this.centroLavoro=centroMovex;
    prepareMap();
  }
  
  public CalcoloTempoCiclo(String centroMovex,String facility) {
    this.centroLavoro=centroMovex;
    this.facility=facility;
    prepareMap();
  }
  
  private void prepareMap(){
    articoliOperaz=new HashMap();
    dettOperazioniArt=new HashMap();
    colliProc=new HashMap <String,String>();
  }
  
  public String getCentroLavoro() {
    return centroLavoro;
  }

  public void setCentroLavoro(String centroLavoro) {
    this.centroLavoro = centroLavoro;
  }

  public List getListaColli() {
    return listaColli;
  }

  public void setListaColli(List listaColli) {
    this.listaColli = listaColli;
  }

  public String getFacility() {
    return facility;
  }

  public Double gettRilavorazioni() {
    return tRilavorazioni;
  }

  public Long getNumArticoli() {
    return numArticoli;
  }
  
  
  /**
   * Data una lista di colli<commessa e numero collo> e un numero max di occorrenze per ogni collo torna il tempo ciclo totale per la produzione di tali colli
   * @param List listaColli
   * @param Integer occorenze numero occorenze
   * @return Double tempo ciclo
   * @throws SQLException
   * @throws QueryException 
   */
   
  public Double getTempoCiclo(List listaColli,Integer occorenze) throws SQLException, QueryException {
    Connection conAs400=null; 
    try { 
      conAs400=ColombiniConnections.getAs400ColomConnection();
      return getTempoCiclo(conAs400,listaColli,occorenze);
    }finally{
      if(conAs400!=null)
        try {
        conAs400.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura di connessione al db");
      }
    }
  }
  
  /**
   * Data una lista contenente commessa e numero collo calcola il tempo ciclo totale 
   * @param Connection con
   * @param List listaColli
   * @param Integer occorrenze numero di ripetizioni di un collo oltre le quali il tempo impiegato alla sua lavorazione viene considerato rilavorazione
   * @return Double  tempo ciclo totale
   * @throws SQLException 
   */
  public Double getTempoCiclo(Connection con,List<List> listaColli,Integer occorrenze) throws SQLException {
    Double tempoCicloTot=Double.valueOf(0);
   
    List articoliCollo=null;
    
    if(listaColli==null || listaColli.isEmpty())
      return tempoCicloTot;
    
    if(facility==null || facility.isEmpty())
      facility=getFacilityFromCLav(con, centroLavoro);
    _logger.debug("Inizio processo colli");
    for(List record:listaColli){
      articoliCollo=new ArrayList();
      Integer commessa=ClassMapper.classToClass(((List)record).get(0),Integer.class);
      Long collo=ClassMapper.classToClass(((List)record).get(1),Long.class);
      String key=commessa.toString()+collo.toString();
      //se il collo non è mai stato processato calcolo il tempo ciclo
      if(!colliProc.containsKey(key)){
        numArticoli++;
        articoliCollo=getArticoliCollo(con, commessa, collo);
        colliProc.put(key,new Integer(1));
        Double tempoCiclo=getTempoCicloArticoli(con,articoliCollo);
        _logger.debug(" Commessa - Collo :"+commessa+"-"+collo + " lista articoli :"+articoliCollo.toString()+" tempoCiclo:"+tempoCiclo);
        tempoCicloTot+=tempoCiclo;
        
      }else{
      //se il collo è già stato processato ma il numero di volte è >= al numero di occorrenze previsto allora
      //calcolo cmq il tempo ciclo ,lo dimezzo  e lo considero come rilavorazione
        Integer occTmp=(Integer) colliProc.get(key);
        if(occorrenze!=null && occTmp>=occorrenze){
          articoliCollo=getArticoliCollo(con, commessa, collo);
          colliProc.put(key,occTmp+1);
          Double tempoAppo=getTempoCicloArticoli(con,articoliCollo);
          _logger.debug(" Commessa - Collo :"+commessa+"-"+collo + " lista articoli :"+articoliCollo.toString()+" tempoCiclo:"+tempoAppo +" occorenze:"+(occTmp+1));
          tRilavorazioni+=(tempoAppo/2);
        }else{
          colliProc.put(key,occTmp+1);
          _logger.debug(" Commessa - Collo :"+commessa+"-"+collo + " occorenze:"+(occTmp+1));
        }
      }
    }
    
    return tempoCicloTot;
  } 
  
  /**
   * Torna una lista contenente articolo numero ordine e riga dell'ordine relativamente al collo e alla commessa che gli sono stati forniti
   * @param Connection con
   * @param Integer commessa
   * @param Long collo
   * @return List 
   */
  public synchronized  static  List getArticoliCollo(Connection con,Integer commessa,Long collo){
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    List articoliCollo=new ArrayList();
    try{
       pstmt = con.prepareStatement(STMCOLLI); 
       //pstmt = con.prepareStatement(STMCOLLI2); 
        pstmt.setLong(1, Long.valueOf(commessa));
        pstmt.setLong(2, collo);
        rs=pstmt.executeQuery();
        while(rs.next()){
          List info=new ArrayList();
          String articolo=rs.getString("ARTICOLO");
          String numordine=rs.getString("NORDINE");
          Integer riga=ClassMapper.classToClass(rs.getString("RIGA"), Integer.class);
          info.add(articolo.trim());
          info.add(numordine.trim());
          info.add(riga);
          articoliCollo.add(info);        
        }
        
      } catch (SQLException ex) {
        _logger.error("Impossibile estrarre informazioni per commessa n :"+commessa+" collo :"+collo);
      } finally{
        try {
          if(pstmt!=null)
            pstmt.close();
          if(rs!=null)
            rs.close();
        } catch (SQLException ex) {
          _logger.error("Errore nella chiusura dello statment commessa n :"+commessa+" collo :"+collo);
        }
      } 
    
    return articoliCollo;
  }
  
  
  /**
   * Data una lista di articoli di un determinato collo torna il tempo ciclo necessario alla loro produzione
   * @param Connection con
   * @param List articoliCollo lista contenente articolo,numero ordine,riga dell'ordine
   * @return Double tempoCiclo
   * @throws SQLException 
   */
  private Double getTempoCicloArticoli(Connection con,List articoliCollo) throws SQLException{
    Double tempoCiclo=Double.valueOf(0);
    
    if(articoliCollo==null|| articoliCollo.isEmpty())
      return tempoCiclo;
    
    for(Object rec:articoliCollo){
      List tempiArticolo=null;
      String artTmp=(String)((List)rec).get(0); 
      String numOTmp=(String)((List)rec).get(1);
      Integer riga=(Integer)((List)rec).get(2);
      
      if(articoliOperaz.containsKey(artTmp)){
        tempiArticolo=(List) articoliOperaz.get(artTmp);
      }else{
        tempiArticolo=getListaTempiArticolo(con, centroLavoro, facility, artTmp);
        articoliOperaz.put(artTmp,tempiArticolo);
      }
      //se l'articolo per quel centro di lavoro non ha assegnato nessun tempo ciclo allora passo avanti
      if(tempiArticolo==null || tempiArticolo.isEmpty())
        continue;

      //se l'articolo prevede 1 solo tempo ciclo per la linea allora prendo quello
      if(tempiArticolo!=null && tempiArticolo.size()==1){
        Double tempoAppo=ClassMapper.classToClass(((List) tempiArticolo.get(0)).get(1),Double.class);
        _logger.debug("Articolo: "+artTmp+ " tempoCiclo: "+tempoAppo);
        tempoCiclo+=tempoAppo;   
      }else{
        // se l'articolo prevede più tempi relativamente a quella linea allora esplodo le operazioni e le configurazioni dell'ordine relativo  
        Long configOrd=getConfigurazioneOrdine(con, numOTmp, riga);
        Map configurazioni=new HashMap();
        for(Object obj:tempiArticolo){
          List caratterOper=null;
          List caratterConfig=null;
          Long operTmp= ClassMapper.classToClass(((List) obj).get(0),Long.class);
          Double tcicloTmp=ClassMapper.classToClass(((List) obj).get(1),Double.class);
          String key=artTmp+"-"+operTmp;
          if(dettOperazioniArt.containsKey(key)){
            caratterOper=dettOperazioniArt.get(key);
          }else{ 
            caratterOper=getCaratteristicheOperazione(con,facility,artTmp,operTmp);
            dettOperazioniArt.put(key,caratterOper);
          } 
          if(caratterOper==null || caratterOper.isEmpty()){
            _logger.debug("Articolo: "+artTmp+" con più tempi ciclo ma nessuna operazione per opzione:" + operTmp + " tempoCiclo: "+tcicloTmp);
            tempoCiclo+=tcicloTmp;
          }else {
            if(configurazioni.containsKey(configOrd)){
              caratterConfig=(List) configurazioni.get(configOrd);
            }else{
              //carico le caratteristiche della configurazione dell'ordine
              caratterConfig=getCaratteristicheConfigOrd(con, configOrd);
              if(existCaratOperDisegno(caratterOper))//se esiste un' opzione che fa riferimento ad una misura da disegno -> carico i disegni legati alla configurazione dell'ordine
                caratterConfig.addAll(getDisegniConfigOrd(con, configOrd));
              
              configurazioni.put(configOrd,caratterConfig);
            }
            
            if(verificaCorrispOperazConfig(caratterOper, caratterConfig)){
               _logger.debug("Articolo: "+artTmp+" con più tempi ciclo->opzione:" + operTmp + " tempoCiclo: "+tcicloTmp);
              tempoCiclo+=tcicloTmp;
            }
          }
        }
      }
    }
    
    return tempoCiclo;
  } 
  
  /**
   * Torna una lista contenente operazione e tempo ciclo relativo associati ad un determinato articolo
   * @param Connection con
   * @param String centroLavoro
   * @param String facility
   * @param String articolo
   * @return List
   * @throws SQLException 
   */
  public static synchronized  List getListaTempiArticolo(Connection con,String centroLavoro,String facility,String articolo) throws SQLException{
    ResultSet rs = null;
    List listArticoli=new ArrayList();
    ResultSetHelper rsh=null;
    
    
    try{
      String query=" select POOPNO,POPITI from MVXBDTA.mpdope  where 1=1 ";
      query+=" and POCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM);
      query+=" and POFACI="+JDBCDataMapper.objectToSQL(facility);
      query+=" and POPRNO="+JDBCDataMapper.objectToSQL(articolo);
      query+=" and POPLGR="+JDBCDataMapper.objectToSQL(centroLavoro);
      
      rsh = new ResultSetHelper(con, query);
     
//      POPITI,POOPNO,PNFTID,PNOPTN,PNINCE
      while(rsh.resultSet.next()){
        List record=new ArrayList();
        Double ope=rsh.resultSet.getDouble("POOPNO");
        record.add(ope.longValue()); //numero operazione
        record.add(rsh.resultSet.getDouble("POPITI")); //tpciclo
        
        listArticoli.add(record);
      }
     }finally{
      if(rsh!=null)
        rsh.close();
      if(rs!=null)
        rs.close();
    } 
    
    return listArticoli;
  }
  
   /**
   * Torna una lista di tutte le caratteristiche ed opzioni di una data operazione di un determinato articolo
   * @param Connection con
   * @param String facility
   * @param String articolo
   * @param Long operazione
   * @return List
   * @throws SQLException 
   */
   private List getCaratteristicheOperazione(Connection con, String facility, String articolo, Long operazione) throws SQLException {
     List listOpzioni=new ArrayList();
     PreparedStatement ps = null;
     ResultSet rs = null;
     
    try{
      ps = con.prepareStatement(STMMPDOMA); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, facility);
      ps.setString(3, articolo); 
      ps.setLong(4, operazione);
      rs=ps.executeQuery();
      while(rs.next()){
        CaratOperazione oper=new CaratOperazione();
        oper.setOperazione(operazione);
        oper.setCaratteristica(rs.getString("PNFTID")); //feature
        String opz=rs.getString("PNOPTN");
        if(opz!=null)
          oper.setOpzione(opz.trim()); //option 

        oper.setInclEscl(rs.getString("PNINCE")); //includi/escludi
        oper.setTipoOper(rs.getString("PNOTYP")); // tipo opzione (caratteristica/disegno)
        if(oper.isMisuraDisegno()){
          oper.setValoreRangeIni(rs.getDouble("PNNUVF"));
          oper.setValoreRangeFin(rs.getDouble("PNNUVT"));
        }
        listOpzioni.add(oper);
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 

     return  listOpzioni;
  }
  
  /**
    * Dato il numero di ordine e la sua riga relativa torna il numero di configurazione
    * @param Connection con
    * @param String numOrdine
    * @param Integer numRiga
    * @return Long configurazione 
    * @throws SQLException 
    */
  private Long getConfigurazioneOrdine(Connection con,String numOrdine,Integer numRiga) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Long configurazione=Long.valueOf(-1) ;
    Integer riga=numRiga/100;
    Integer sottoriga=numRiga-(riga*100);
    try{
      ps = con.prepareStatement(STMCONFIG); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, numOrdine);
      ps.setInt(3, riga); 
      ps.setInt(4, sottoriga);
      rs=ps.executeQuery();
      while(rs.next()){
        configurazione=rs.getLong("OBCFIN");
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return configurazione;
  }
  
  /**
   * Torna la lista di caratteristiche di una determinata configurazione d'ordine
   * @param Connetcion con
   * @param Long config configurazione
   * @return List <CaratConfig>
   * @throws SQLException 
   */
  private List<CaratConfig> getCaratteristicheConfigOrd(Connection con,Long config) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    List caratteristiche=new ArrayList();
    
    try{
      ps = con.prepareStatement(STMFEATURES); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setLong(2, config);
      rs=ps.executeQuery();
      while(rs.next()){
        CaratConfig conf=new CaratConfig();
        conf.setNumeroConfig(config);
        conf.setCaratteristica(rs.getString("QJFTID")); //caratteristica
        String appoOpz=rs.getString("QJOPTN");
        if(appoOpz!=null)
          conf.setOpzione(appoOpz.trim()); //opzione
        
        
        caratteristiche.add(conf);
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return caratteristiche;
  }
  
  /**
   * Torna la lista delle misure da disegno di una determinata configurazione d'ordine
   * @param Connetcion con
   * @param Long config
   * @return List <CaratConfig>
   * @throws SQLException 
   */
  private List<CaratConfig> getDisegniConfigOrd(Connection con,Long config) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    List disegni=new ArrayList();
    
    try{
      ps = con.prepareStatement(STMDRAWINGS); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setLong(2, config);
      rs=ps.executeQuery();
      while(rs.next()){
        CaratConfig conf=new CaratConfig();
        conf.setNumeroConfig(config);
        conf.setCaratteristica(rs.getString("QKDMID")); //disegno
        String misAppo=rs.getString("QKMEVA");
        conf.setMisuraS(misAppo.trim());
        Long valMis=rs.getLong("INTV");
        conf.setFlagN(valMis>0);
        conf.loadValoreMisura();
        disegni.add(conf);
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    return disegni;
  }
  
 
  /**
   * Verifica la corrispondenza tra la lista di caratteristiche di una operazione e le caratteristiche di una determinata configurazione d'ordine
   * @param List opzioniOper lista opzioni operazione
   * @param List caratterConfig lista caratteristiche configurazione
   * @return Boolean torna tr
   * @throws SQLException 
   */
  private Boolean verificaCorrispOperazConfig(List<CaratOperazione> carattOper,List<CaratConfig>  carattConfig) throws SQLException {
    Boolean corrispondenza=Boolean.FALSE; 
    
    if(carattOper==null || carattOper.isEmpty())
      return corrispondenza;
    
    if(carattConfig==null || carattConfig.isEmpty())
      return corrispondenza;
    
    
    // 1) opzioni tutte di inclusione
    if(allCaratOperAreInclude(carattOper)){
      for(CaratOperazione opz:carattOper){
        if(IsCaratOperInList(opz, carattConfig)){
            corrispondenza=Boolean.TRUE;
        } 
      }
    } else if(allCaratOperAreExclude(carattOper)){ 
    // 2) opzioni tutte di esclusione
      corrispondenza=Boolean.TRUE; 
      for(CaratOperazione oper :carattOper){
        //se tutte la caratteristiche sono di esclusione
        if(IsCaratOperInList(oper, carattConfig)){
          corrispondenza=Boolean.FALSE; 
        }
      }
    } else {
      //caso particolare inclusione ed esclusione presenti contemporanemante
      //in questo caso vince l'esclusione sull'inclusione se entrambre sono verificate
      _logger.info("!!! Opzioni con inclusione ed esclusione !!!");
      boolean appoCorrEsclusione=false;
      boolean appoCorrInclusione=false;
      //cerco prima le opzioni di esclusione
      for (CaratOperazione opz : carattOper){
        if(!opz.isInclusione()){
          if(IsCaratOperInList(opz, carattConfig)){
             appoCorrEsclusione=true;
             break;
          }  
        }
      }
      //se ho già trovato una caratteristica di esclusione non verifico quelle di inclusione
      if(!appoCorrEsclusione){
      //cerco le opzioni di inclusione
        for (CaratOperazione opz : carattOper){
          if(opz.isInclusione()){
            if(IsCaratOperInList(opz, carattConfig)){
               appoCorrInclusione=true;
               break;
            }  
          }
        }
      }
      //l'esclusione vince sull'inclusione
      if(appoCorrEsclusione){
        corrispondenza=Boolean.FALSE;
      }else if(!appoCorrEsclusione  && appoCorrInclusione){
        corrispondenza=Boolean.TRUE;
      }else if(!appoCorrEsclusione && !appoCorrInclusione){
        _logger.error("No inclusione No esclusione --> NO TEMPO CICLO!!!!");
      }
    }   

    return corrispondenza;
  }

  /**
   * Verifica se tutte le caratteristiche dell'operazione sono di inclusione
   * @param List opzioniOper
   * @return Boolean <TRUE> se tutte le opzioni della lista sono di inclusione
   */
  private boolean allCaratOperAreInclude(List<CaratOperazione> opzioniOper){
    for(CaratOperazione opz:opzioniOper){
      if(!opz.isInclusione()){
        return Boolean.FALSE;
      }  
    }
    
    return  Boolean.TRUE;
  }
  
  
  /**
   * Verifica se tutte le caratteristiche dell'operazione  sono di esclusione
   * @param List opzioniOper
   * @return Boolean <TRUE> se tutte le opzioni della lista sono di esclusione 
   */
  private boolean allCaratOperAreExclude(List<CaratOperazione> opzioniOper){
    for(CaratOperazione opz:opzioniOper){
      if(opz.isInclusione()){
        return Boolean.FALSE;
      }  
    }
    
    return  Boolean.TRUE;
  }
  
  
  /**
   * Verifica se nella lista di caratteristiche dell'operazione ne esiste una relativa ad una misura da disegno
   * @param opzioniOper
   * @return Boolean <TRUE> se esiste almento un opzioni relativa ad una misura da disegno
   */
  private boolean existCaratOperDisegno(List<CaratOperazione> opzioniOper){
    for(CaratOperazione opz:opzioniOper){
      if(TIPODISEGNO.equals(opz.getTipoOper())){
        return Boolean.TRUE;
      }  
    }
    return  Boolean.FALSE;
  }
  
  
  /**
   * Verifica se c'è corrispondenza tra la caratteristica dell'operazione passata e la lista delle carratteristiche di una configurazione
   * @param CaratOperazione opz caratteristica di una determinata operazione
   * @param List listCarattConfig lista delle caratteristiche di una data configurazione
   * @return <TRUE> se c'è corrispondenza tra la caratteristica dell'operazione e la lista delle carratteristiche di una configurazione  
   */
  private boolean IsCaratOperInList(CaratOperazione opz,List<CaratConfig> listCarattConfig ){
    if(listCarattConfig==null || listCarattConfig.isEmpty())
      return false;
    
    for(CaratConfig conf:listCarattConfig){
      //se l'opzione è relativa ad una misura di disegno
      if(opz.isMisuraDisegno() && conf.isDisegno()){
         if(conf.getCaratteristica().equals(opz.getCaratteristica())) //se coincidono le caratteristico devo controllare le misure
           if(conf.getValoreMisura()!=null 
              &&  conf.getValoreMisura()>=opz.getValoreRangeIni() && conf.getValoreMisura()<=opz.getValoreRangeFin())
           return true;
      }else if(!opz.isMisuraDisegno() && !conf.isDisegno()){
        //se l'opzione è relativa invece ad una caratteristica
        if(conf.getCaratteristica().equals(opz.getCaratteristica())){
          if(conf.getOpzione().equals(opz.getOpzione()))
            return true;
        }
      }
    }
    
    return false;
  }
  
  /**
   * Torna la facility di appartenenza di un determinato centro di lavoro
   * @param Connection con
   * @param String centroLavoro
   * @return String facility
   * @throws SQLException 
   */
  public synchronized static String getFacilityFromCLav(Connection con ,String centroLavoro) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String facility="";
    try{
      ps = con.prepareStatement(STMFACI); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, centroLavoro);
      
      rs=ps.executeQuery();
      while(rs.next()){
        facility=rs.getString("MWFACI");
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    return facility.trim();
  }

 

  //inner class per gestione delle caratteristiche di un operazione di un determinato articolo
   public class CaratOperazione{
     
    private Long operazione;
    private String caratteristica;
    private String opzione;
    private String inclEscl;
    private String tipoOper;
    private Double valoreRangeIni;
    private Double valoreRangeFin;
    
    public Long getOperazione() {
      return operazione;
    }

    public void setOperazione(Long operazione) {
      this.operazione = operazione;
    }
    
    public String getCaratteristica() {
      return caratteristica;
    }

    public void setCaratteristica(String caratteristica) {
      this.caratteristica = caratteristica;
    }

    public String getInclEscl() {
      return inclEscl;
    }

    public void setInclEscl(String inclEscl) {
      this.inclEscl = inclEscl;
    }

    public String getOpzione() {
      return opzione;
    }

    public void setOpzione(String opzione) {
      this.opzione = opzione;
    }
    
    public boolean isInclusione(){
      if(OPZIONEINCLUDI.equals(inclEscl))
        return true;
      
      return false;
    }
    
    public String getTipoOper() {
      return tipoOper;
    }

    public void setTipoOper(String tipoOper) {
      this.tipoOper = tipoOper;
    }
    
    public boolean isMisuraDisegno(){
      if(TIPODISEGNO.equals(tipoOper))
        return true;
      
      return false;
    }

    public Double getValoreRangeFin() {
      return valoreRangeFin;
    }

    public void setValoreRangeFin(Double valoreRangeFin) {
      this.valoreRangeFin = valoreRangeFin;
    }

    public Double getValoreRangeIni() {
      return valoreRangeIni;
    }

    public void setValoreRangeIni(Double valoreRangeIni) {
      this.valoreRangeIni = valoreRangeIni;
    }
   
    
    
  }
   
  //inner class per gestire tutte le tipologie di caratteristiche/disegni di una configurazione d'ordine
  public class CaratConfig{
    
    private Long   numeroConfig;
    private String caratteristica;
    private String opzione;
    private String misuraS=null;
    private Double valoreMisura=null;
    private Boolean flagN=Boolean.FALSE;
    
    public String getCaratteristica() {
      return caratteristica;
    }

    public void setCaratteristica(String caratteristica) {
      this.caratteristica = caratteristica;
    }

    public String getOpzione() {
      return opzione;
    }

    public void setOpzione(String opzione) {
      this.opzione = opzione;
    }

    public Long getNumeroConfig() {
      return numeroConfig;
    }

    public void setNumeroConfig(Long numeroConfig) {
      this.numeroConfig = numeroConfig;
    }

    public String getMisuraS() {
      return misuraS;
    }

    public Boolean getFlagN() {
      return flagN;
    }

    public void setFlagN(Boolean flagN) {
      this.flagN = flagN;
    }

    
    public void setMisuraS(String misuraS) {
      this.misuraS = misuraS;
    }

    public Double getValoreMisura() {
      
      return valoreMisura;
    }

    public void loadValoreMisura() {
      if(flagN && this.misuraS!=null && this.misuraS.length()==15){
        String valore=misuraS.substring(0,9)+"."+misuraS.substring(9);
        valoreMisura=Double.valueOf(valore);
      }
    }

   

    public Boolean isDisegno() {
      if(misuraS!=null && !misuraS.isEmpty())
        return Boolean.TRUE;
      
      return Boolean.FALSE;
    }

  }
  
  
  private static final Logger _logger = Logger.getLogger(CalcoloTempoCiclo.class);
}



  
  
  
  