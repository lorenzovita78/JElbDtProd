/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.indicatoriOee.utils;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import db.Connections;
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
public class CalcTempoCicloSmart {
  
  
  private String centrodiLavoro;
  private String facility;
  private List<String> filtriComponenti;
  
  private Map <String,Double> articoliTempi;
  private Map <String,Double> colliProc;
  
  
  
  private  String STMZPDSUM=" select trim(psmtno) AS psmtno"+
                                 " from mcobmoddta.zpdsum "+
                                 " where 1=1 "+
                                 " and pscono= ? "+
                                 " and psprno= ? "+
                                 " and psproj= ? "+
                                 " and pselno= ? "+
                                 " and psridn= ? "+
                                 " and psridl= ? "; 
  
  
  private final String STMMPDOPE=" select POOPNO,POPITI "
                                 +" from MVXBDTA.mpdope"
                                 +" where 1=1 "
                                 +" and pocono=?"
                                 +" and pofaci=?"
                                 +" and poprno=?"
                                 +" and poplgr=?";
  
  
  public Map<String, Double> getArticoliTempi() {
    return articoliTempi;
  }

  public String getCentrodiLavoro() {
    return centrodiLavoro;
  }

  public String getFacility() {
    return facility;
  }

  public List<String> getFiltriComponenti() {
    return filtriComponenti;
  }

  
  
  public CalcTempoCicloSmart( String centrodiLavoro, String facility,List<String> componenti ) {
    this.centrodiLavoro = centrodiLavoro;
    this.facility = facility;
    this.filtriComponenti=componenti;
    
    if(filtriComponenti==null)
      filtriComponenti=new ArrayList();
    
    colliProc=new HashMap();
    articoliTempi=new HashMap();
  }
  
  

  public Double getTempoCicloCollo(Integer commessa ,Long collo) throws SQLException{
    Connection conAs400=null; 
    try { 
      conAs400=ColombiniConnections.getAs400ColomConnection();
      
      return getTempoCicloCollo(conAs400,commessa,collo);
    }finally{
      if(conAs400!=null)
        try {
        conAs400.close();
      } catch (SQLException ex) {
        _logger.error("Errore in fase di chiusura di connessione al db");
      }
    }
    
  }
  
  
  public Double getTempoCicloCollo(Connection conAs400 ,Integer commessa,Long collo) throws SQLException{
    Double tCiclo=Double.valueOf(0);
    if(facility==null){
      facility=CalcoloTempoCiclo.getFacilityFromCLav(conAs400, centrodiLavoro);
    }
    
    String key=commessa.toString()+collo.toString();
    if(colliProc.containsKey(key)){
      tCiclo= colliProc.get(key);
    }else{
      List articoliCollo=CalcoloTempoCiclo.getArticoliCollo(conAs400, commessa, collo);
      tCiclo=getTempoCiclo(conAs400,commessa,collo,articoliCollo);
      colliProc.put(key, tCiclo); 
    }
    
    
    return tCiclo;
  }

  
  private Double getTempoCiclo(Connection conAs400,Integer commessa,Long collo, List articoliCollo)  {
    Double tempo=Double.valueOf(0);
    if(articoliCollo==null || articoliCollo.isEmpty())
       return tempo;
    
    for(Object rec:articoliCollo){
      String articolo=(String)((List)rec).get(0); 
      String numOrdine=(String)((List)rec).get(1);
      Integer riga=(Integer)((List)rec).get(2);
      
      if(articoliTempi.containsKey(articolo)){
        tempo+=articoliTempi.get(articolo);
      }else{
        Double tempoArt=getTempoCicloArticoliCollo(conAs400,commessa,collo,articolo,numOrdine,riga);
        articoliTempi.put(articolo,tempoArt);
        tempo+=tempoArt;
      }
    }   
    
    
    
    return tempo;
  }


  
  private Double getTempoCicloArticoliCollo(Connection conAs400,Integer commessa,Long collo, String articolo, String numOrdine, Integer riga) {
    Double tempo=Double.valueOf(0);
    List<String> articoliToSearch=new ArrayList();
    articoliToSearch=getArticolitoSearch(conAs400, commessa, collo, articolo, numOrdine, riga);
    for(String codiceArt:articoliToSearch){
       if(!articoliTempi.containsKey(codiceArt)){
         Double tempoT=getTempoCicloArticolo(conAs400, codiceArt);   
         articoliTempi.put(codiceArt, tempoT);
         tempo+=tempoT;
         System.out.println("Articolo ->"+articolo+" ; componente->"+codiceArt+" ; tempoC->"+tempoT);
       }else{
         tempo+=articoliTempi.get(codiceArt);
//         _logger.debug("Articolo ->"+articolo+" ; componente->"+codiceArt+" ; tempoC->"+articoliTempi.get(codiceArt));
       }   
    }
    
    
    return tempo;
  }
  
  
  private List<String> getArticolitoSearch(Connection conAs400,Integer commessa,Long collo, String articolo, String numOrdine, Integer rigaOrd){
    List<String> articoliToSearch=new ArrayList();
    String componentCondition=getComponentCondition();
    String st=STMZPDSUM+componentCondition;
    PreparedStatement pstmt =null;
    ResultSet rs = null;
    
    String colloS=collo.toString();
    int lng=colloS.length();
    while(lng<5){
      colloS="0"+colloS;
      lng=colloS.length();
    }
    
    try{
        pstmt = conAs400.prepareStatement(st); 
        pstmt.setInt(1, CostantsColomb.AZCOLOM);
        pstmt.setString(2, articolo);
        pstmt.setString(3, commessa.toString());
        pstmt.setString(4,colloS);
        pstmt.setString(5,numOrdine);
        pstmt.setInt(6,rigaOrd);
        rs=pstmt.executeQuery();
        while(rs.next()){
          articoliToSearch.add(rs.getString("psmtno"));        
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
    
    return articoliToSearch;
  }
  
  
  private Double getTempoCicloArticolo(Connection conAs400,String codiceArt) {
    List<List> listaT=new ArrayList();
    Double tempo=Double.valueOf(0);
    try{
      listaT=CalcoloTempoCiclo.getListaTempiArticolo(conAs400, centrodiLavoro, facility, codiceArt);
      for(List record: listaT){
        tempo+=ClassMapper.classToClass(record.get(1),Double.class);
      }
    }catch(SQLException s){
      _logger.error("Attenzione impossible reperire il tempo ciclo per l'articolo "+codiceArt+" -->"+s.getMessage());
    }
    return tempo;
  }
  
  
  
  
  private String getComponentCondition(){
    if(filtriComponenti==null || filtriComponenti.isEmpty())
      return "";
    
    String conditionC=" and ( psmtno ";
    for(int i=0;i<filtriComponenti.size();i++){
       String comp=filtriComponenti.get(i);
       if(i==0){
         conditionC+=" LIKE  '"+comp+"%'";
       }else{
         conditionC+=" or psmtno LIKE '"+comp+"%'";
       }
    }
    conditionC+= "  ) ";   
    
    return conditionC;
    
  }
 
  
  private static final Logger _logger = Logger.getLogger(CalcTempoCicloSmart.class);
  
  
}
