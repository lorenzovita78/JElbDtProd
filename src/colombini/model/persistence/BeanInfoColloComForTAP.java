/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;


import colombini.costant.CostantsColomb;
import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import db.persistence.IBeanPersSIMPLE;

/**
 * Bean per la mappatura dell'archivio ZTAPCI.
 * Tale archivio contiene le informazioni relative ai colli/pz che devono essere prodotti per una determinata
 * lcommessa da un certo centro di Lavoro.
 * L'archivio rientra nel progettino web Tracciatura colli/pz per commessa e risulta necessario
 * per capire l'avanzamento di produzione di una determinata linea (vedi anche archivio ZTAPCP)
 * @author lvita
 */
public class BeanInfoColloComForTAP  implements IBeanPersSIMPLE {
  
  public final static String TABNAME="ZTAPCI";
  public final static String TABFIELDS="TICONO,TIPLGR,TIBARP,TIDTCO,TICOMM,TINCOL,TINART,TILINP,"
                                       + "TIBOXN,TIPEDA,TIORNO,TIPONR,TICONN,TICART,TIDART,TISTRD,TICCLR,TIDTIN";
  
  public final static String TABKEYFIELDS="TICONO,TIPLGR,TIBARP";
  
  
  public final String TICONO="TICONO";
  public final String TIPLGR="TIPLGR";
  public final String TIBARP="TIBARP";
  
  public final String TIDTCO="TIDTCO";
  public final String TICOMM="TICOMM";
  public final String TINCOL="TINCOL";
  public final String TINART="TINART";
  public final String TILINP="TILINP";
  public final String TIBOXN="TIBOXN";
  public final String TIPEDA="TIPEDA";
  public final String TIORNO="TIORNO";
  public final String TIPONR="TIPONR";
  public final String TICONN="TICONN";
  
  public final String TICART="TICART";
  public final String TIDART="TIDART";
  public final String TISTRD="TISTRD";
  
  public final String TICCLR="TICCLR";
  
  public final String TIDTIN="TIDTIN";
  
  
  private Integer az;
  private String cdL;
  private String barcode;
  
  private Long dataComN;
  private Integer commessa;
  private Integer collo;
  private Integer nArticolo;
  
  private String lineaLogica;
  private Integer pedana;
  private Integer box;
  private String nOrdine;
  private Integer rigaOrdine;
  private Integer spedizione;
  
  private String codArticolo;
  private String descArticolo;
  private String descEstesa;
  private String pathFile;
  
  private String codColore;
  
  //proprietà che mi indica se il bena deve anche essere inserito nell'archio per gestiore la stampa dell'etichetta
  private Boolean etkPresent;
  
  public BeanInfoColloComForTAP(String cdL) {
    this.cdL=cdL;
    this.etkPresent=Boolean.FALSE;
  }
  
  public BeanInfoColloComForTAP(String cdL,Integer commessa, Integer collo, Integer nart,Boolean printable) {
    this.cdL=cdL;
    this.commessa = commessa;
    this.collo = collo;
    this.nArticolo = nart;

    
    this.nOrdine="";
    this.descEstesa="";
    this.pathFile="";
    
    this.pedana=Integer.valueOf(0);
    this.box=Integer.valueOf(0);
    this.spedizione=Integer.valueOf(0);
    this.rigaOrdine=Integer.valueOf(0);
    this.etkPresent=printable;
  }
  

  
  public String getBarcodeStd(){
    String bar="";
    if(commessa<100){
      bar+="0"+commessa.toString();
    }else{
      bar=commessa.toString();
    }
    
    int len=collo.toString().length();
    while(len<5){
      bar+="0";
      len++;
    }
    bar+=collo.toString();
    
    
    //se il codice articolo è =0 allora non lo inseriamo nel barcode!!
    if(nArticolo>0 && nArticolo<10){
      bar+="0"+nArticolo.toString();  
    }else if(nArticolo>0){
      bar+=nArticolo.toString();
    }
    
    return bar;
  }

  
  
  public Integer getAz() {
    return az;
  }

  public void setAz(Integer az) {
    this.az = az;
  }
  
  public String getCdL() {
    return cdL;
  }

  public void setCdL(String cdL) {
    this.cdL = cdL;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Long getDataComN() {
    return dataComN;
  }

  public void setDataComN(Long dataComN) {
    this.dataComN = dataComN;
  }

  public Integer getCommessa() {
    return commessa;
  }

  public void setCommessa(Integer commessa) {
    this.commessa = commessa;
  }

  public Integer getCollo() {
    return collo;
  }

  public void setCollo(Integer collo) {
    this.collo = collo;
  }

  public Integer getnArticolo() {
    return nArticolo;
  }

  public void setnArticolo(Integer nArticolo) {
    this.nArticolo = nArticolo;
  }

  public String getLineaLogica() {
    return lineaLogica;
  }

  public void setLineaLogica(String lineaLogica) {
    this.lineaLogica = lineaLogica;
  }

  public Integer getPedana() {
    return pedana;
  }

  public void setPedana(Integer pedana) {
    this.pedana = pedana;
  }

  public Integer getBox() {
    return box;
  }

  public void setBox(Integer box) {
    this.box = box;
  }

  public String getnOrdine() {
    return nOrdine;
  }

  public void setnOrdine(String nOrdine) {
    this.nOrdine = nOrdine;
  }

  public Integer getRigaOrdine() {
    return rigaOrdine;
  }

  public void setRigaOrdine(Integer rigaOrdine) {
    this.rigaOrdine = rigaOrdine;
  }

  public Integer getSpedizione() {
    return spedizione;
  }

  public void setSpedizione(Integer spedizione) {
    this.spedizione = spedizione;
  }

  public String getCodArticolo() {
    return codArticolo;
  }

  public void setCodArticolo(String codArticolo) {
    this.codArticolo = codArticolo;
  }

  public String getDescArticolo() {
    return descArticolo;
  }

  public void setDescArticolo(String descArticolo) {
    this.descArticolo = descArticolo;
  }

  public String getDescEstesa() {
    return descEstesa;
  }

  public void setDescEstesa(String descEstesa) {
    this.descEstesa = descEstesa;
  }

  public String getPathFile() {
    return pathFile;
  }

  public void setPathFile(String pathFile) {
    this.pathFile = pathFile;
  }

  public String getCodColore() {
    return codColore;
  }

  public void setCodColore(String codColore) {
    this.codColore = codColore;
  }

  public Boolean isPrintable() {
    return etkPresent;
  }

  public void setEtkPresent(Boolean etkPresent) {
    this.etkPresent = etkPresent;
  }

  
  public String getKeyCommColl(){
    return this.dataComN.toString()+this.commessa.toString()+this.collo.toString();
  }
  
  public void loadInfoBox(Connection con){
    if(commessa!=null && collo!=null && nArticolo!=null){
      String qry=" SELECT CLBOXN from MCOBFILP.SC"+commessa.toString()+"COL "+
                 "\n WHERE clncol="+JDBCDataMapper.objectToSQL(collo)+
                 "\n and clnart=0";
      //+JDBCDataMapper.objectToSQL(nArticolo);
      try {
        Object[] obj=ResultSetHelper.SingleRowSelect(con, qry);
        if(obj!=null && obj.length>0){
          box=ClassMapper.classToClass(obj[0],Integer.class);
        }
      } catch (SQLException ex) {
       _logger.error("Attenzione errore in fase di accesso al database per reperire l'informazione del box -->"+ex.getMessage());
      }
    }else{
      _logger.warn(" Attenzione informazioni di base non presente per individuare il numero di box del collo");
    }
  }

  public void loadInfoBoxFebal(Connection con){
    if(commessa!=null && collo!=null ){
      String qry=" SELECT Box from DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL "+
                 "\n WHERE 1=1"+
                 " and Commessa="+JDBCDataMapper.objectToSQL(commessa)+
                 " and Codice_collo="+JDBCDataMapper.objectToSQL(collo);
      //+JDBCDataMapper.objectToSQL(nArticolo);
      try {
        Object[] obj=ResultSetHelper.SingleRowSelect(con, qry);
        if(obj!=null && obj.length>0){
          box=ClassMapper.classToClass(obj[0],Integer.class);
        }
      } catch (SQLException ex) {
       _logger.error("Attenzione errore in fase di accesso al database per reperire l'informazione del box -->"+ex.getMessage());
      }
    }else{
      _logger.warn(" Attenzione informazioni di base non presente per individuare il numero di box del collo");
    }
  }
  
  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABNAME;
  }

  @Override
  public List<String> getFields() {
    return ArrayUtils.getListFromArray(TABFIELDS.split(","));
  }

  @Override
  public List<String> getKeyFields() {
    return ArrayUtils.getListFromArray(TABKEYFIELDS.split(","));
  }

  
  
  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    
    
    
    types.add(Types.INTEGER);   //az
    types.add(Types.CHAR);      //centro di lavoro
    
    types.add(Types.CHAR);      //barcode
    types.add(Types.NUMERIC); //datacommessa
    types.add(Types.INTEGER); //commessa
    types.add(Types.INTEGER);    //numero collo
    types.add(Types.INTEGER);    //numero articolo
    
    types.add(Types.CHAR);    //lineaLav
    types.add(Types.INTEGER);    //box
    types.add(Types.INTEGER);    //pedana
    
    types.add(Types.CHAR);    //n ordine
    types.add(Types.INTEGER);    //riga ordine
    
    types.add(Types.INTEGER);    //spedizione
    types.add(Types.CHAR);    //articolo
    types.add(Types.CHAR);    //desc articolo
    types.add(Types.CHAR);    //desc estesa
    
     types.add(Types.CHAR);    //cod Colore
    
    types.add(Types.TIMESTAMP); //data inserimento
    
    
    return types;
  }

  
  
   @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(TICONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(TIPLGR, this.cdL);
    fieldsValue.put(TIBARP, this.barcode);
    fieldsValue.put(TIDTCO, this.dataComN);
    fieldsValue.put(TICOMM, this.commessa);
    fieldsValue.put(TINCOL, this.collo);
    
    fieldsValue.put(TINART, this.nArticolo);
    fieldsValue.put(TILINP, this.lineaLogica);
    fieldsValue.put(TIBOXN, this.box);
    fieldsValue.put(TIPEDA, this.pedana);
    fieldsValue.put(TIORNO, this.nOrdine);
    fieldsValue.put(TIPONR, this.rigaOrdine);
    fieldsValue.put(TICONN, this.spedizione);
    
    
    fieldsValue.put(TICART, this.codArticolo);
    fieldsValue.put(TIDART, this.descArticolo);
    fieldsValue.put(TISTRD, this.descEstesa);
    fieldsValue.put(TICCLR, this.codColore);
    
    
    fieldsValue.put(TIDTIN, new Date());
    
    
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(TICONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(TIPLGR, this.cdL);
    fieldsValue.put(TIDTCO, this.dataComN);
    fieldsValue.put(TICOMM, this.commessa);
    
    
    
    return fieldsValue;
  }
 
  
  
  
//  public static BeanInfoColloComForTAP getInfoCollo(String codLinea,String riga){
//    String comm=riga.substring(0, 3);
//    String collo=riga.substring(3,8);
//    String tipo=riga.substring(8,10);
////    String prgForm=riga.substring(22,36);
//    String coArt=riga.substring(44,53).trim();
//    String descArt=riga.substring(53,riga.length()).trim();
//    if(descArt.length()>30)
//      descArt=descArt.substring(0, 30);
//   
//    
//    BeanInfoColloComForTAP icar=new BeanInfoColloComForTAP(codLinea,Integer.valueOf(comm), Integer.valueOf(collo), Integer.valueOf(tipo));
//    
//    icar.setCodArticolo(coArt);
//    icar.setDescArticolo(descArt);
//
//    return icar;
//  }
  
  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
  
  private static final Logger _logger = Logger.getLogger(BeanInfoColloComForTAP.class);

  
}

