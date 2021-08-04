/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence;


import as400.Utility400;
import colombini.conn.ColombiniConnections;
import db.JDBCDataMapper;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import db.persistence.IBeanPersSIMPLE;
import db.persistence.ISequence;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Bean per la mappatura dell'archivio ZTAPPI.
 * Tale archivio contiene le informazioni relative ai file pdf associati ad un determinato pezzo
 * L'archivio rientra nel progettino web Tracciatura colli/pz per commessa 
 * @author lvita
 */
public class BeanLogSpuntaForTAP  implements IBeanPersSIMPLE,ISequence {
  
  public final static String TABNAME="ZTAPCP";
  public final static String TABFIELDS="TPIDPZ,TPIDCR,TPBARP,TPDTIN,TPDTPR,TPDTSC,TPNOTE,TPUTIN,TPUTMD";
  
  public final static String TABKEYFIELDS="TPIDPZ";
  
  
  
  public final String TPIDPZ="TPIDPZ";
  public final String TPIDCR="TPIDCR";
  public final String TPBARP="TPBARP";
  public final String TPDTIN="TPDTIN";
  public final String TPDTPR="TPDTPR";
  public final String TPDTSC="TPDTSC";
  public final String TPNOTE="TPNOTE";
  public final String TPUTIN="TPUTIN";
  public final String TPUTMD="TPUTMD";
  
  private Long idPz;
  private Long idCarrello;
  private String barcode;
  private Date dataIns;
  private Date dataPrel;
  private Date dataSca;
  private String note;
  private String utInserimento;
  private String utModifica;

  
  
  public BeanLogSpuntaForTAP(String barcode, Date dataIns,String utInserimento) {
    this.barcode = barcode;
    this.dataIns = dataIns;
    this.utInserimento = utInserimento;
    
    this.idCarrello = Long.valueOf(0);
    
    this.dataPrel = null;
    this.dataSca = null;
    this.note = null;
    this.utModifica = null;
  }
  

  
  public void loadIdPz(Connection con) throws SQLException {
      this.idPz=Utility400.getSequenceDbValue(con, getSequenceName());
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
    
    types.add(Types.NUMERIC);      //id
    types.add(Types.NUMERIC);      //idcarrello
    types.add(Types.CHAR);      //barcode
    types.add(Types.TIMESTAMP); //data inserimento
    types.add(Types.TIMESTAMP); //data prelievo
    types.add(Types.TIMESTAMP); //data scarto
    types.add(Types.CHAR);      //Note
    types.add(Types.CHAR);      //utenteInserimento
    types.add(Types.CHAR);      //utenteInserimento
    
    return types;
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(TPIDPZ,this.idPz);
    fieldsValue.put(TPIDCR,this.idCarrello);
    fieldsValue.put(TPBARP,this.barcode);
    fieldsValue.put(TPDTIN,this.dataIns);
    fieldsValue.put(TPDTPR,this.dataPrel);
    fieldsValue.put(TPDTSC,this.dataSca);
    fieldsValue.put(TPNOTE,this.note);
    fieldsValue.put(TPUTIN,this.utInserimento);
    fieldsValue.put(TPUTMD,this.utModifica);
    
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(TPBARP, this.barcode);
    fieldsValue.put(TPUTIN, this.utInserimento);
    
    
    
    return fieldsValue;
  }
 
  @Override
  public Boolean validate() {
    if(this.idPz==null )
      return Boolean.FALSE;
    if(this.barcode==null || this.dataIns==null || this.utInserimento==null)
      return Boolean.FALSE;
    
    return Boolean.TRUE;
  }
  
  
   @Override
  public String getSequenceName() {
     return  getLibraryName()+"."+"SEQ_ZTAPCP";
  }
  
  
  
  @Override
  public String toString(){
    return this.barcode+" - "+this.dataIns+" - "+utInserimento;
  }
  
  
  public void deleleOldRecord(Connection con) throws SQLException{
    String qry="DELETE FROM "+getLibraryName()+"."+TABNAME+
            " where 1=1 "+
            " and "+TPUTIN+" ="+JDBCDataMapper.objectToSQL(this.utInserimento)+
            " and DATE("+TPDTIN+" ) = DATE ("+JDBCDataMapper.objectToSQL(this.dataIns)+" )";
    
     
    PreparedStatement st=con.prepareStatement(qry);
    st.execute(); 
     
  }
  
  
  private static final Logger _logger = Logger.getLogger(BeanLogSpuntaForTAP.class);

 

  
}

