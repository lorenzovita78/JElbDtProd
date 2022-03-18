/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model.persistence;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import db.persistence.IBeanPersSIMPLE;
import exception.QueryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author lvita
 */
public class Allegati implements IBeanPersSIMPLE{

  public final static String TABLENAME="ZZBSTO";
  
  public final static String Z2CONO="Z2CONO";  
  public final static String Z2ORNO="Z2ORNO";
  public final static String Z2TDOC="Z2TDOC";
  public final static String Z2PTHS="Z2PTHS";
  public final static String Z2TITL="Z2TITL";
  public final static String Z2DMAG="Z2DMAG";
  public final static String Z2DDIP="Z2DDIP";
  public final static String Z2DDFP="Z2DDFP";
  public final static String Z2PTHD="Z2PTHD";
  public final static String Z2NOTE="Z2NOTE";
  
  

    
  private String cono;
  private String ordine;
  private String tipoDoc;
  private String path;
  private String titolo;
  private Date dataUltAggior;
  private Date dataPresaCarico;
  private Date dataFineCarico;
  private String pathDest;
  private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public Date getDataFineCarico() {
        return dataFineCarico;
    }

    public void setDataFineCarico(Date dataFineCarico) {
        this.dataFineCarico = dataFineCarico;
    }

    public String getCono() {
        return cono;
    }

    public void setCono(String cono) {
        this.cono = cono;
    }

    public Date getDataUltAggior() {
        return dataUltAggior;
    }

    public void setDataUltAggior(Date dataUltAggior) {
        this.dataUltAggior = dataUltAggior;
    }
    
    public String getOrdine() {
        return ordine;
    }

    public void setOrdine(String ordine) {
        this.ordine = ordine;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Date getDataPresaCarico() {
        return dataPresaCarico;
    }

    public void setDataPresaCarico(Date dataPresaCarico) {
        this.dataPresaCarico = dataPresaCarico;
    }

    public String getPathDest() {
        return pathDest;
    }

    public void setPathDest(String pathDest) {
        this.pathDest = pathDest;
    }
    
      public Allegati( ){
//    this.ordine=ordine;
//    this.tipoDoc=tipoDoc;
//    this.path=path;
//    this.cono=cono;
//    this.dataUltAggior=dataUltAggior;
//    this.titolo=titolo;
      }

  

  
  @Override
  public Map<String, Object> getFieldValuesMap() {
    Map fieldsValue=new HashMap();
    fieldsValue.put(Z2CONO, this.cono);
    fieldsValue.put(Z2ORNO, this.ordine);
    fieldsValue.put(Z2TDOC, this.tipoDoc);
    fieldsValue.put(Z2PTHS, this.path);
    fieldsValue.put(Z2TITL, this.titolo);
    fieldsValue.put(Z2DMAG, this.dataUltAggior);
    fieldsValue.put(Z2DDIP, this.dataPresaCarico);
    fieldsValue.put(Z2DDFP, this.dataFineCarico);
    fieldsValue.put(Z2PTHD, this.pathDest);
    fieldsValue.put(Z2NOTE, this.note);
    return fieldsValue;
  }

  @Override
  public Map<String, Object> getFieldValuesForDelete() {
    Map fields=new HashMap();
    fields.put(Z2CONO, this.cono);
    fields.put(Z2ORNO, this.ordine);
    fields.put(Z2TDOC, this.tipoDoc);
    fields.put(Z2DMAG, this.dataUltAggior);
    return fields;
  }

  public Map<String, Object> getFieldValuesForUpdate() {
    Map fields=new HashMap();

    fields.put(Z2PTHD, this.pathDest);    
    fields.put(Z2DDFP, this.dataFineCarico);
    
    return fields;
  }
  
////  public Map<String, Object> getFieldValuesForErrorUpdate() {
////    Map fields=new HashMap();
////    fields.put(Z2NOTE, "Errore copia file");    
////    return fields;
////  }
  
  @Override
  public String getLibraryName() {
      //Modifica fatta per test
    return "mcobmoddem";
  }

  @Override
  public String getTableName() {
    return TABLENAME;
  }

  
  @Override
  public List<String> getFields() {
    List l=new ArrayList();
    l.add(Z2CONO);
    l.add(Z2ORNO);
    l.add(Z2TDOC);
    l.add(Z2PTHS);
    l.add(Z2TITL);
    l.add(Z2DMAG);
    l.add(Z2DDIP);
    l.add(Z2DDFP);
    l.add(Z2PTHD);
    l.add(Z2NOTE);
    return l;
  }

  @Override
  public List<String> getKeyFields() {
    List l=new ArrayList();
    l.add(Z2CONO);
    l.add(Z2ORNO);
    l.add(Z2TDOC);
    //l.add(Z2DDIP);
    //l.add(Z2DMAG);
    return l;
  }

  @Override
  public List<Integer> getFieldTypes() {
    List<Integer> types=new ArrayList();
    types.add(Types.VARCHAR); //CONO
    types.add(Types.VARCHAR); //ORDINE
    types.add(Types.VARCHAR); //TIPODOC
    types.add(Types.VARCHAR); //PATHSORGENTE
    types.add(Types.VARCHAR); //TITOLO
    types.add(Types.TIMESTAMP); //DATAULTAGGIOR
    types.add(Types.TIMESTAMP); //DATAPRESACARICO
    types.add(Types.TIMESTAMP); //DATAFINECARICO
    types.add(Types.VARCHAR); //PATHDEST
    types.add(Types.VARCHAR); //NOTE
    return types;
  }

  
  @Override
  public Boolean validate() {
    return Boolean.TRUE;
  }
  
   public void updateAllegati(PreparedStatement ps,Connection con,String cono,String tipoFile,String ordine,java.sql.Date dataFineCarico, String pathDest) throws QueryException, SQLException{
    
    boolean rs = true;
    try{
      ps.setString(1, pathDest);
      ps.setDate(2, dataFineCarico);
      ps.setString(3, cono);
      ps.setString(4, ordine); 
      ps.setString(5, tipoFile);
      rs=ps.execute();
    }
     catch (SQLException ex)
     {
         _logger.error("Errore query update -->"+ps.executeQuery().toString());
      }

  }
   
  public void updateErrorAllegati(PreparedStatement ps,Connection con,String cono,String tipoFile,String ordine, String Nota) throws QueryException, SQLException{
    
    boolean rs = true;
    try{
      ps.setString(1, Nota);
      ps.setString(2, cono);
      ps.setString(3, ordine); 
      ps.setString(4, tipoFile);
      rs=ps.execute();
    }
     catch (SQLException ex)
     {
         _logger.error("Errore query update -->"+ps.executeQuery().toString());
      }

  }
    
  private static final Logger _logger = Logger.getLogger(Allegati.class); 
}
