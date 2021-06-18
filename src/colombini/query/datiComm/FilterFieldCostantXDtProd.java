/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.datiComm;

import db.Connections;

/**
 *
 * @author lvita
 */
public class FilterFieldCostantXDtProd {
 
  //filtri standard
  public final static String FT_CONDLINEA="FILTROCONDIZIONELINEA";
  public final static String FT_NUMCOMM="FILTRONUMCOMMESSA";
  
  public final static String FT_COMMESSEIN="FILTROCOMMESSEIN";
  
  public final static String FT_LISTCOMM="FILTROLISTACOMMESSE";
  public final static String FT_AZIENDA="FILTROAZIENDA";
  public final static String FT_DATA="FILTRODATA";
  
  
  
  public final static String FT_LINEA="FILTROSULINEA";
  //filtro per budget
  public final static String FT_CODBDG="CODBDG";
  //filtro per linee lavorative
  public final static String FT_LINEATT="FILTROLINEAATT";
  
  public final static String FT_UTLINEA="FILTROUTLINEA";
  //filtro linee lavorative da elaborare
  public final static String FT_LINEETOELAB="FT_LINEETOELAB";
  
  
  //filtro per avanzamento produzione
  public final static String FT_LINEAVP="FILTROLINEAAVP";
  //filtro per caricamento dati avanzamento produzione
  public final static String FT_LOADAVP="FILTROLOADAVP";
  
  public final static String FT_UNMIS="FILTROUNITAMIS";
  public final static String FT_DATADA="FILTRODATADA";
  public final static String FT_DATAA="FILTRODATAA";
  
  public final static String FT_LINEE="FILTROLINEELAV";
  
  public final static String FT_ARTICOLI="FILTROARTICOLI";
  
  public final static String FT_LANCIO_DESMOS="FT_LANCIO_DESMOS";
  
  //filtro BU uguale / diverso
  public final static String FT_BU_UGUALE="FT_BU_UGUALE";
  public final static String FT_BU_DIVERSO="FT_BU_DIVERSO";
  
  
  //campi standard
  public final static String FD_FACILITY="FACILITY";
  public final static String FD_DIVISIONE="DIVISIONE";
  public final static String FD_DITTA="DITTA";
  public final static String FD_NUMCOLLI="NUMCOLLI";
  public final static String FD_NUMPEZZI="NUMPEZZI";
  public final static String FD_VOLUME="VOLUME";
  public final static String FD_NUMORD="NUMORDINI";
  
  //dati simulazione budget
  public final static String FD_CODBUDGET="CODBUDGET";
  public final static String FD_DESBUDGET="DESBUDGET";
  
  //dati linee lavorative
  public final static String FD_AZIENDA="AZIENDA";
  public final static String FD_CODLINEA="CODLINEALAV";
  public final static String FD_DESCLINEA="DESCLINEALAV";
  public final static String FD_UNMIS="UNITAMISURA";
  public final static String FD_FLAG="FLAG";
  public final static String FD_CONDQUERY="CONDQUERY";
  public final static String FD_CLASSEXEC="CLASSEXEC";
  
  public final static String FD_NUMOPERTURNO="NUMOPERTURNO";
  public final static String FD_NUMORESTD="NUMORESTD";
  public final static String FD_NUMTURNI="NUMTURNI";
  public final static String FD_OREFLEXGG="OREFLEXGG";
  public final static String FD_OREFLEXTR="OREFLEXTR";
  public final static String FD_CADORARIA="CADZORARIA";
  public final static String FD_ALLCOMM="ALLCOMM";
  public final static String FD_ANTCOMM="ANTCOMM";
  
  public final static String FD_FLAGAVP="FLAGAVP";
  public final static String FD_CLASSAVP="CLASSAVP";
  
  public final static String FD_COEFFM3="COEFFM3";
  
  
  
  public final static Integer AZCOLOMBINI=Integer.valueOf(30);
  public final static Integer AZFEBAL=Integer.valueOf(10);
  
  public final static String TABMISDISEGNO="MPDCDM";
  public final static String TABDISTINTACOMM="ZPDSUM";
  public final static String TABFACILITY="CFACIL";
  public final static String TABELABCOMM="ZCOMME";
  public final static String TABLOGELABCOMM="ZJBLOG";
 
 
  
  
  /**
   * Torna il nome della tabella relativa ai dati della commessa <numero x>
   * della libreria <Connections.LIBAS400MCOBFILP>
   * @param String numeroCommessa
   * @return String 
   */
  public static String getNomeTabCommessa(String numeroCommessa){
    return Connections.getInstance().getLibraryTempAs400()+".SC"+numeroCommessa+"COL";
  }
  
  /**
   * Torna il nome della tabella relativa ai dati della distinta della commessa <numero x>
   * della libreria <Connections.LIBAS400MCOBFILP>
   * @param numeroCommessa
   * @return 
   */
  public static String getNomeTabDistintaCommessa(String numeroCommessa){
    return Connections.getInstance().getLibraryTempAs400()+"."+TABDISTINTACOMM+numeroCommessa;
  }
  
   public static String getNomeTabMisDisegnoCommessa(String numeroCommessa){
    return Connections.getInstance().getLibraryTempAs400()+"."+TABMISDISEGNO+numeroCommessa;
  }
   
}
