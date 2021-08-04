/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QryVolumeColliBuComm;
import db.ResultSetHelper;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;
import db.persistence.IBeanPersSIMPLE;

/**
 *
 * @author lvita
 */
public class ColliVolumiCommessaTbl implements IBeanPersSIMPLE {

  public final static String TABLENAME="ZDPCCV";
  
  public final static String DCCONO="DCCONO";
  public final static String DCDTCO="DCDTCO";
  public final static String DCCOMM="DCCOMM";
  public final static String DCDIVI="DCDIVI";
  public final static String DCNCOL="DCNCOL";
  public final static String DCVOL3="DCVOL3";
  public final static String DCFLCV="DCFLCV";
  public final static String DCRGDT="DCRGDT";
  
  private Integer azienda;
  private Long dataCommN;
  private Integer commessa;
  private String divisione;
  private Integer numeroColli;
  private Double volume;
  private String flagCommValid;

  
  public ColliVolumiCommessaTbl(){
    
  }
  
  public ColliVolumiCommessaTbl(Integer az,Long dataCommN, Integer commessa) {
    this.azienda=az;
    this.dataCommN = dataCommN;
    this.commessa = commessa;
  }

  public String getDivisione() {
    return divisione;
  }

  public void setDivisione(String divisione) {
    this.divisione = divisione;
  }
  
  public Integer getNumeroColli() {
    return numeroColli;
  }

  public void setNumeroColli(Integer numeroColli) {
    this.numeroColli = numeroColli;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public String getFlagCommValid() {
    return flagCommValid;
  }

  public void setFlagCommValid(String flagCommValid) {
    this.flagCommValid = flagCommValid;
  }
  
  
  
  @Override
  public String getLibraryName() {
    return ColombiniConnections.getAs400LibPersColom();
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  @Override
  public List<String> getFields() {
    List fields=getKeyFields();
    
    fields.add(DCNCOL);
    fields.add(DCVOL3);
    fields.add(DCFLCV);
    fields.add(DCRGDT);
           
            
    return fields;
  }

  @Override
  public List<String> getKeyFields() {
    List fields=new ArrayList();
    fields.add(DCCONO);
    fields.add(DCDTCO);
    fields.add(DCCOMM);
    fields.add(DCDIVI);
    
    return fields;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List types=new ArrayList();
    
    types.add(Types.INTEGER);
    types.add(Types.NUMERIC);
    types.add(Types.INTEGER);
    types.add(Types.CHAR);
    types.add(Types.INTEGER);
    types.add(Types.NUMERIC);
    types.add(Types.CHAR);
    types.add(Types.TIMESTAMP);
    
    return types;
    
  }

  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(DCCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(DCDTCO, this.dataCommN);
    fieldsValue.put(DCCOMM, this.commessa);
    fieldsValue.put(DCDIVI, this.divisione);
    fieldsValue.put(DCNCOL, this.numeroColli);
    fieldsValue.put(DCVOL3, this.volume);
    

    fieldsValue.put(DCRGDT, new Date());

    
    return fieldsValue;
  }

  
  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fieldsValue=new HashMap();
    
    fieldsValue.put(DCCONO, CostantsColomb.AZCOLOM);
    fieldsValue.put(DCDTCO, this.dataCommN);
    fieldsValue.put(DCCOMM, this.commessa);
    fieldsValue.put(DCDIVI, this.divisione);
    
    return fieldsValue;
  }

  
  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
  
  public List<ColliVolumiCommessaTbl> retrieveDatiComm(Connection con ,Integer az,Long dataCommN,Integer comm,Long dataIniN,Long dataFinN,Boolean onlyComm) 
          throws SQLException, QueryException{
    List<List> l=new ArrayList();
    List<ColliVolumiCommessaTbl> lnew=new ArrayList();
    
    QryVolumeColliBuComm qry=new QryVolumeColliBuComm();
    
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, az);
    if(comm!=null){
      qry.setFilter(FilterQueryProdCostant.FTNUMCOMM, comm);
      qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataCommN);
    }else{
      qry.setFilter(FilterQueryProdCostant.FTDATAINI, dataIniN);
      qry.setFilter(FilterQueryProdCostant.FTDATAFIN, dataFinN);
    }
    if(onlyComm)
      qry.setFilter(FilterQueryProdCostant.FTONLYCOMM, "Y");
     
    ResultSetHelper.fillListList(con, qry.toSQLString(), l);
    for(List rec:l){
       Long dataCN=ClassMapper.classToClass(rec.get(0),Long.class);
       Integer nCommN=ClassMapper.classToClass(rec.get(1),Integer.class);
       
       ColliVolumiCommessaTbl cvc=new ColliVolumiCommessaTbl(az, dataCN, nCommN);
       cvc.setDivisione(ClassMapper.classToString(rec.get(2)));
       cvc.setNumeroColli(ClassMapper.classToClass(rec.get(3),Integer.class));
       cvc.setVolume(ClassMapper.classToClass(rec.get(4),Double.class));
       cvc.setFlagCommValid(ClassMapper.classToString(rec.get(5)));
       lnew.add(cvc);
    }
    
    return lnew;
  }
  
}
