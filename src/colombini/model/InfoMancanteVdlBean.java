/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.elabs.ElabGestColliEsteroInVDL;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import db.persistence.IBeanPersSIMPLE;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class InfoMancanteVdlBean  implements IBeanPersSIMPLE{
  
  public final static String TABNAME="ZDPVLD";
  
  public final static String TABKEYFIELDS="VDDTRF,VDCCOL";
  
  
  
  public final static String VDDTRF="VDDTRF";
  public final static String VDRFGG="VDRFGG";
  public final static String VDTPCO="VDTPCO";
  public final static String VDCCOL="VDCCOL";
  public final static String VDDCOL="VDDCOL";
  public final static String VDPLGR="VDPLGR";
  public final static String VDPLNM="VDPLNM";
  public final static String VDNBOX="VDNBOX";
  public final static String VDPEDA="VDPEDA";
  public final static String VDORNO="VDORNO";
  public final static String VDCUNO="VDCUNO";
  public final static String VDFEXT="VDFEXT";
  public final static String VDNSPE="VDNSPE";
  public final static String VDTPSP="VDTPSP";
  public final static String VDDVET="VDDVET";
  public final static String VDSTVT="VDSTVT";
  public final static String VDCCON="VDCCON";
  public final static String VDDFAC="VDDFAC";
  public final static String VDDCDL="VDDCDL";
  public final static String VDSUNO="VDSUNO";
  public final static String VDLMDT="VDLMDT";
  
  
  //
  
  
  private final static String STM_FOR_TIPOCLI="SELECT TRIM(OKCFC4) as TIPOCLI , TRIM(OKCUNM) as NOMECLI" 
                                      + " FROM MVXBDTA.OCUSMA"                             
                                      + " where OKCONO=?"
                                      + " and OKCUNO=?";
  
  
  private final static String STM_FOR_VOLCOL="SELECT SUM(CLVOM3) as VOLUME" 
                                      + " FROM MCOBFILP.SCXXXCOL"                             
                                      + " where 1=1"
                                      + " and CLNART=0"
                                      + " and CLCOMM=?"
                                      + " and CLNROR=?";
  
  private final static String STM_FOR_VOLORD="SELECT SUM(EAVOL3) as VOLUME" 
                                      + " FROM MVXBDTA.DOHEAD"                             
                                      + " where 1=1"
                                      + " and EARORC=3"
                                      + " and EACONO=?"
                                      + " and EAORNO=?"
                                      + " and EACONN=?"
                                      ;  
  
  /*  private final static String STM_FOR_BOX_PED_VOL="SELECT Substring(ORDLMO, 9, 2) AS Box,Substring(ORDLMO, 11, 2) AS Ped,ORVOM3 AS Vol  FROM " +
                                            " mcobmoddta.ZCOMME " +
                                            " INNER JOIN mvxbdta.DCONSI ON zccono=dacono AND zccdld = dacdld " +
                                            " INNER JOIN mvxbdta.DOHEAD ON DACONO=EACONO AND DACONN=EACONN " +
                                            " INNER JOIN mvxbdta.MDNHEA ON EACONO=OQCONO AND EAORNO=OQRIDN " +
                                            " INNER JOIN mvxbdta.MPTRNS ON OQCONO=ORCONO AND OQWHLO=ORWHLO AND OQDNNO=ORDNNO " +
                                            " WHERE 1=1 " +
                                            " AND OQCONO=? " +
                                            " AND substring(ORDLMO,1,3)=? " +
                                            " AND substring(ORDLMO,4,5)=? " +
                                            " AND OQRIDN=? " ;*/
              /*                              "UNION " +
                                            "SELECT Substring(ORDLMO,9,2) AS Box,Substring(ORDLMO,11,2) AS Ped,ORVOM3 AS Vol  FROM " +
                                            "mcobmoddta.ZCOMME " +
                                            "INNER JOIN mvxbdta.DCONSI ON zccono=dacono AND zccdld = dacdld " +
                                            "INNER JOIN mvxbdta.DOHEAD ON DACONO=EACONO AND DACONN=EACONN " +
                                            "INNER JOIN mcobmoddta.ZMDNHEA ON EACONO=OQCONO AND EAORNO=OQRIDN " +
                                            "INNER JOIN mcobmoddta.ZMPTRNS ON OQCONO=ORCONO AND OQWHLO=ORWHLO AND OQDNNO=ORDNNO " +
                                            "WHERE 1=1 " +
                                            "AND OQCONO=30 " +
                                            "AND substring(ORDLMO,1,3)=? " +
                                            "AND substring(ORDLMO,4,5)=? " +
                                            "AND OQRIDN=? " +
                                            "FETCH FIRST 1 ROWS ONLY ";
           */
    /*       "SELECT MAX(BoxId) as Box, MAX(PlateId) as Pedana from ( "
                                        + "SELECT TruckloadNumber,ColloID,BoxId,PlateId " +
                                        "FROM [AvanzamentoVDL].[dbo].[V2H_ColloInfo_Header]" +
                                        "where TruckloadNumber=? " +
                                        "and ColloID=? " +
                                        "UNION " +
                                        "SELECT TruckloadNumber,ColloID,BoxId,PlateId " +
                                        "FROM [AvanzamentoVDL].[dbo].[Storico_ColloInfo_Header] " +
                                        "where TruckloadNumber=? " + 
                                        " and ColloID=? " +
                                        ") as a group by TruckloadNumber,ColloID "*/
                                        ; 
  
  private  static final String CLMN_TIPOCLI="TIPOCLI";
  private  static final String CLMN_NOMECLI="NOMECLI";
  private  static final String CLMN_VOL="VOLUME";
  private  static final String CLMN_BOX="BOX";
  private  static final String CLMN_PEDANA="PED";
  private  static final String CLMN_VOLCOL="VOL";  
          
  private final static Integer ST500=Integer.valueOf(500);
  
  public final static Integer ST550=Integer.valueOf(550);
  public final static Integer ST560=Integer.valueOf(560);
  public final static Integer ST570=Integer.valueOf(570);
  
  
  public final static String CLIENTEITA="ITA";
  public final static String CLIENTERSM="RSM";
  
  public final static String TIPOMANCANTEMATT="M";
  public final static String TIPOMANCANTEPOM="P";
  public final static String TIPOMANCANTEGENERICO="I"; // se la spedizione associata ha un orario antecedente alle 6:00
  
  
  public final static String TIPOMANCATECOMMESSA="C";
  
  
  private Date dataRif;
 
  private String rifGg;
  private String tipoCommessa;
  private String codCollo;
  private String descrCollo;
  private String  nOrdine;
  private Integer nSpedizione;
  private String  codCliente;
  private String  lineaLogica;
  private String  descrLineaLogica;
  private Integer box;
  private Integer pedana;
  private String  descrVettore;
  private String  codFornitore;
  
  
  //indica se il mancante è relativo alla mattina o al pomeriggio
  private String  tipoSpedizione;
  
  private String  codAziendaVdl;
  private String  descrLineaVdl;
  private String  descrStabVdl;
  private Boolean estero;

  private Integer statoVettore;
  
  
  
  
  
  public InfoMancanteVdlBean(){
    this.estero=Boolean.FALSE;
    this.tipoCommessa="";
  }
  
  public InfoMancanteVdlBean(Date dataRif){
    this.dataRif=dataRif;
    //this.codCollo=collo;
    this.tipoCommessa="";
    this.estero=Boolean.FALSE;
    
  }
  
  public Date getDataRif() {
    return dataRif;
  }

  public void setDataRif(Date dataRif) {
    this.dataRif = dataRif;
  }

  public String getRifGg() {
    return rifGg;
  }

  public void setRifGg(String rifGg) {
    this.rifGg = rifGg;
  }

  
  public String getCodCollo() {
    return codCollo;
  }

  public void setCodCollo(String codCollo) {
    this.codCollo = codCollo;
    loadTipoCommessa();
  }

  public String getDescrCollo() {
    return descrCollo;
  }

  public void setDescrCollo(String descrCollo) {
    this.descrCollo = descrCollo;
  }

  public String getNOrdine() {
    return nOrdine;
  }

  public void setNOrdine(String nOrdine) {
    this.nOrdine = nOrdine;
  }

  public Integer getNSpedizione() {
    return nSpedizione;
  }

  public void setNSpedizione(Integer nSpedizione) {
    this.nSpedizione = nSpedizione;
  }

  public String getCodCliente() {
    return codCliente;
  }

  public void setCodCliente(String codCliente) {
    this.codCliente = codCliente;
  }

  public String getLineaLogica() {
    return lineaLogica;
  }

  public Integer getLineaLogNumber(){
    return ClassMapper.classToClass(this.lineaLogica, Integer.class);
  }

  public String getTipoCommessa() {
    return tipoCommessa;
  }

  public void setTipoCommessa(String tipoCommessa) {
    this.tipoCommessa = tipoCommessa;
  }
  
  
  public void setLineaLogica(String lineaLogica) {
    this.lineaLogica = lineaLogica;
  }


  public String getDescrLineaLogica() {
    return descrLineaLogica;
  }

  public void setDescrLineaLogica(String descrLineaLogica) {
    this.descrLineaLogica = descrLineaLogica;
  }

  public Integer getBox() {
    return box;
  }

  public void setBox(Integer box) {
    this.box = box;
  }

  public Integer getPedana() {
    return pedana;
  }

  public void setPedana(Integer pedana) {
    this.pedana = pedana;
  }

  public String getDescrVettore() {
    return descrVettore;
  }

  public void setDescrVettore(String descrVettore) {
    this.descrVettore = descrVettore;
  }

  public String getCodAziendaVdl() {
    return codAziendaVdl;
  }

  public void setCodAziendaVdl(String codAziendaVdl) {
    this.codAziendaVdl = codAziendaVdl;
  }

  public String getDescrLineaVdl() {
    return descrLineaVdl;
  }

  public void setDescrLineaVdl(String descrLineaVdl) {
    this.descrLineaVdl = descrLineaVdl;
  }

  public String getDescrStabVdl() {
    return descrStabVdl;
  }

  public void setDescrStabVdl(String descrStabVdl) {
    this.descrStabVdl = descrStabVdl;
  }

  public Boolean isEstero() {
    return estero;
  }

  public void setEstero(Boolean estero) {
    this.estero = estero;
  }

  public String getStringEstero(){
    if(this.estero)
      return "Y";
    
    return "N";
  }
  public Integer getStatoVettore() {
    return statoVettore;
  }

  public void setStatoVettore(Integer statoVettore) {
    this.statoVettore = statoVettore;
  }

  public String getTipoSpedizione() {
    return tipoSpedizione;
  }

  public void setTipoSpedizione(String tipoSpedizione) {
    this.tipoSpedizione = tipoSpedizione;
  }

  public String getCodFornitore() {
    return codFornitore;
  }

  public void setCodFornitore(String codFornitore) {
    this.codFornitore = codFornitore;
  }
  
  
  
 
  public static Boolean checkClienteEstero(Connection con,String codCliente) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Boolean estero=Boolean.FALSE;
    try{
      ps = con.prepareStatement(STM_FOR_TIPOCLI); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, codCliente);
      
      rs=ps.executeQuery();
      while(rs.next()){
        String tipoCli=rs.getString(CLMN_TIPOCLI);
        if(!CLIENTEITA.equals(tipoCli) && !CLIENTERSM.equals(tipoCli))
          estero=Boolean.TRUE;
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    
    return estero;
  }
  
  
  public static String getNomeClienteEstero(Connection con,String codCliente) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Boolean estero=Boolean.FALSE;
    String nomeCli="";
    try{
      ps = con.prepareStatement(STM_FOR_TIPOCLI); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, codCliente);
      
      rs=ps.executeQuery();
      while(rs.next()){
        String tipoCli=rs.getString(CLMN_TIPOCLI);
        nomeCli=rs.getString(CLMN_NOMECLI);
        if(!CLIENTEITA.equals(tipoCli) && !CLIENTERSM.equals(tipoCli))
          estero=Boolean.TRUE;
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    
    return estero ? nomeCli : "";
  }
  
  
    public static void getBoxPedNumVol(Connection con,String Ord, String Collo, ElabGestColliEsteroInVDL.InfoColloIntoVDL bean) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    int boxPedRet[];
          boxPedRet = new int[3];
    String box;
    String ped;
    Double vol;
    
    String collo=Collo.substring(3,8);
    String Comm=Collo.substring(0,3);
    List<List> Result=new ArrayList();
    
    try{
  
      String qery="SELECT CAST(Substring(ORDLMO, 9, 2) as varchar(5)) AS Box,CAST(Substring(ORDLMO, 11, 2) as varchar(5)) AS Ped,ORVOM3 AS Vol  FROM " +
                                            " mcobmoddta.ZCOMME " +
                                            " INNER JOIN mvxbdta.DCONSI ON zccono=dacono AND zccdld = dacdld " +
                                            " INNER JOIN mvxbdta.DOHEAD ON DACONO=EACONO AND DACONN=EACONN " +
                                            " INNER JOIN mvxbdta.MDNHEA ON EACONO=OQCONO AND EAORNO=OQRIDN " +
                                            " INNER JOIN mvxbdta.MPTRNS ON OQCONO=ORCONO AND OQWHLO=ORWHLO AND OQDNNO=ORDNNO " +
                                            " WHERE 1=1 " +
                                            " AND OQCONO=" + CostantsColomb.AZCOLOM + 
                                            " AND substring(ORDLMO,1,3)='" + Comm +  "'" + 
                                            " AND substring(ORDLMO,4,5)='" + collo + "'" + 
                                            " AND OQRIDN='" + Ord + "'" +
                                            " FETCH FIRST 1 ROWS ONLY" ;
       ResultSetHelper.fillListList(con, qery, Result);
       
       if(Result.size() == 0){
             String qery2="SELECT CAST(Substring(ORDLMO, 9, 2) as varchar(5)) AS Box,CAST(Substring(ORDLMO, 11, 2) as varchar(5)) AS Ped,ORVOM3 AS Vol  FROM " +
                                            " mcobmoddta.ZCOMME " +
                                            " INNER JOIN mvxbdta.DCONSI ON zccono=dacono AND zccdld = dacdld " +
                                            " INNER JOIN mvxbdta.DOHEAD ON DACONO=EACONO AND DACONN=EACONN " +
                                            " INNER JOIN mcobmoddta.ZMDNHEA ON EACONO=OQCONO AND EAORNO=OQRIDN " +
                                            " INNER JOIN mcobmoddta.ZMPTRNS ON OQCONO=ORCONO AND OQWHLO=ORWHLO AND OQDNNO=ORDNNO " +
                                            " WHERE 1=1 " +
                                            " AND OQCONO=" + CostantsColomb.AZCOLOM + 
                                            " AND substring(ORDLMO,1,3)='" + Comm +  "'" + 
                                            " AND substring(ORDLMO,4,5)='" + collo + "'" + 
                                            " AND OQRIDN='" + Ord + "'"+
                                            " FETCH FIRST 1 ROWS ONLY" ;
          ResultSetHelper.fillListList(con, qery2, Result);
       }
       
       for(List rec:Result){
       bean.setBox(ClassMapper.classToString(rec.get(0)));
       bean.setPedana(ClassMapper.classToString(rec.get(1)));
       bean.setVol(ClassMapper.classToClass(rec.get(2),Double.class));
       }
       
     }catch(SQLException s ){
      _logger.error("Errore in fase di interrogazione db -->"+s.getMessage())  ; 
         
     } finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    //return "";
  }
    
  public static Double getVolumeNumOrdine(Connection con,String numOrdine,Integer numSpe) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Double  vol3=Double.valueOf(0);
    
    try{
      ps = con.prepareStatement(STM_FOR_VOLORD); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, numOrdine);
      ps.setInt(3, numSpe);
      
      rs=ps.executeQuery();
      while(rs.next()){
        vol3=rs.getDouble(CLMN_VOL);
        
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    
    return vol3;
  }
      
  
    public static Double getVolumeNumOrdineCollo(Connection con,String numOrdine,Integer numSpe,String collo) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    Double  vol3=Double.valueOf(0);
    
    try{
      ps = con.prepareStatement(STM_FOR_VOLORD); 
      ps.setInt(1, CostantsColomb.AZCOLOM); //fisso l'azienda 
      ps.setString(2, numOrdine);
      ps.setInt(3, numSpe);
      
      rs=ps.executeQuery();
      while(rs.next()){
        vol3=rs.getDouble(CLMN_VOL);
        
      }
     }finally{
      if(ps!=null)
        ps.close();
      if(rs!=null)
        rs.close();
    } 
    
    
    return vol3;
  }
  
  public Boolean isVettoreAttivo(){
   if(ST550.equals(this.statoVettore) || ST560.equals(this.statoVettore) || ST570.equals(this.statoVettore) )
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  public Boolean isVettoreDisattivo(){
    Boolean attivo=isVettoreAttivo();
    
    return !attivo;
  }
  
  private String getStringStatoVett(){
    if(isVettoreAttivo())
      return "A";
    
    return "D";
    
  }
  
  
  public Boolean isMancanteMattina(){
    if(TIPOMANCANTEMATT.equals(this.tipoSpedizione))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  public Boolean isMancantePomeriggio(){
    if(TIPOMANCANTEPOM.equals(this.tipoSpedizione))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }
  
  
  public Boolean isMancanteGenerico(){
    if(TIPOMANCANTEGENERICO.equals(this.tipoSpedizione))
      return Boolean.TRUE;
    
    return Boolean.FALSE;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fields=new HashMap();
    fields.put(VDDTRF,this.dataRif);
    fields.put(VDRFGG, this.rifGg);
    fields.put(VDTPCO, this.tipoCommessa);
    fields.put(VDCCOL,this.codCollo);
    fields.put(VDDCOL,this.descrCollo );
    fields.put(VDPLGR,this.lineaLogica);
    fields.put(VDPLNM,this.descrLineaLogica);
    fields.put(VDNBOX,this.box);
    fields.put(VDPEDA,this.pedana);
    fields.put(VDORNO,this.nOrdine);
    fields.put(VDCUNO,this.codCliente);
    fields.put(VDFEXT,this.getStringEstero());
    fields.put(VDNSPE,this.nSpedizione);
    fields.put(VDTPSP,this.tipoSpedizione);
    fields.put(VDDVET,this.descrVettore);
    fields.put(VDSTVT,this.getStringStatoVett());
    fields.put(VDCCON,this.codAziendaVdl);
    fields.put(VDDFAC,this.descrStabVdl);
    fields.put(VDDCDL,this.descrLineaVdl);
    fields.put(VDSUNO,this.codFornitore);
    fields.put(VDLMDT,new Date());

    return fields;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fields=new HashMap();
    fields.put(VDDTRF,this.dataRif);
    fields.put(VDRFGG, this.rifGg);
    
    return fields;
  }

  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom(); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getTableName() {
    return TABNAME; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<String> getFields() {
    List l=new ArrayList();
    l.add(VDDTRF);l.add(VDRFGG);l.add(VDTPCO);l.add(VDCCOL);l.add(VDDCOL);
    l.add(VDPLGR);l.add(VDPLNM);l.add(VDNBOX);l.add(VDPEDA);l.add(VDORNO);
    l.add(VDCUNO);l.add(VDFEXT);l.add(VDNSPE);l.add(VDTPSP);l.add(VDDVET);
    l.add(VDSTVT);l.add(VDCCON);l.add(VDDFAC);l.add(VDDCDL);l.add(VDSUNO);
    l.add(VDLMDT);

    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(VDDTRF);l.add(VDRFGG);l.add(VDCCOL);
    
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    types.add(Types.DATE);   //dataRiferimento
    types.add(Types.CHAR);      //rif Giornata
    types.add(Types.CHAR);      //tipo Commessa
    types.add(Types.CHAR);      //codice collo
    types.add(Types.CHAR);      //descr collo
    types.add(Types.CHAR);      //codice cdL
    types.add(Types.CHAR);      //descr cdL
    types.add(Types.INTEGER);    //box
    types.add(Types.INTEGER);    //pedana
    types.add(Types.CHAR);      //nOrdine
    types.add(Types.CHAR);      //codCliente
    types.add(Types.CHAR);    //estero Y o N
    types.add(Types.INTEGER); //spedizione
    types.add(Types.CHAR);    //tipo spedizione
    types.add(Types.CHAR);    //descrizione vettore
    types.add(Types.CHAR);    //stato vettore
    
    
    types.add(Types.CHAR);    //cod azienda VDL
    types.add(Types.CHAR);    //desc stab VDL
    types.add(Types.CHAR);    //desc linea VDL
    
    
    types.add(Types.CHAR);    //codice fornitore
    types.add(Types.TIMESTAMP); //data inserimento
    
    
    return types;
  }

  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }

  private void loadTipoCommessa() {
    if(this.codCollo!=null && codCollo.length()>3){
      Integer nComm=ClassMapper.classToClass(codCollo.substring(0,3),Integer.class);
      if(nComm!=null && nComm<=365){
        setTipoCommessa(TIPOMANCATECOMMESSA);
      }
    }
  }
  
  
  private static final Logger _logger = Logger.getLogger(InfoMancanteVdlBean.class);
  
  
}
