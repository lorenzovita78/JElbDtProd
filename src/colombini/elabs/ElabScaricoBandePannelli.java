/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs;

import colombini.conn.ColombiniConnections;
import colombini.dtProd.R1.XmlFileSezR1P4;
import colombini.model.persistence.tab.TabScaricoBndPanAs400;
import db.ResultSetHelper;
import db.persistence.PersistenceManager;
import elabObj.ALuncherElabs;
import elabObj.ElabClass;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class ElabScaricoBandePannelli extends ElabClass {

  public final static String CDLSEZR1P4="01083";
  public final static String CDLNESTR1P4="01088";
  
  public final static String REASONCODEONE="1";
  public final static String LOTTO="1";
  
  public final static String UPD_LOGUTILMAT="update [SirioLotto1].[dbo].[LogUtiMat] set Elaborato=1 ,DatOraUpdRec=getDate() where id=? ";
  private Date dataElab=null;
  
  private Boolean elabBandeR1P4;
  private Boolean elabPannelliR1P4;
  //
  private Boolean elabBandeR1P1;
  private Boolean elabBandeR1P0;
  
  
  @Override
  public Boolean configParams() {
    Map param =getInfoElab().getParameter();
    if(param==null || param.isEmpty())
      return Boolean.FALSE;
    
    dataElab=(Date) param.get(ALuncherElabs.DATAINIELB);
      
    if(dataElab==null)
      return Boolean.FALSE;
    
    if(this.getInfoElab().getCode().equals(NameElabs.ELBSCARICOBANDE)){
      setAllElab(Boolean.TRUE);
    } else {
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSCRCBNDR1P4))
         this.elabBandeR1P4=Boolean.TRUE;
      if (this.getInfoElab().getCode().equals(NameElabs.ELBSCRCPANR1P4))
         this.elabPannelliR1P4=Boolean.TRUE;
    } 
    return Boolean.TRUE;   
  }

    
  
  private void setAllElab(Boolean bool){
    this.elabBandeR1P4=bool;
    this.elabPannelliR1P4=bool;
    this.elabBandeR1P1=bool;
    this.elabBandeR1P0=bool;
    
  }  
    
    
  public ElabScaricoBandePannelli(){
    super();
    
    this.elabBandeR1P4=Boolean.FALSE;
    this.elabPannelliR1P4=Boolean.FALSE;
    this.elabBandeR1P1=Boolean.FALSE;
    this.elabBandeR1P0=Boolean.FALSE;
    
  }
  
  
  
  @Override
  public void exec(Connection con) {
    _logger.info("Inizio elaborazione SCARICO BANDE");
    
    if(elabBandeR1P4)
      elabBandeR1P4(con);
    
    if(elabPannelliR1P4)
      elabPannelliR1P4(con);
//      String pathFileS2=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESRC2));
//      String pathFileDest=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESDEST));
//      String filesNameS1=ClassMapper.classToString(getElabProperties().get(NameElabs.LISTFILESS1));
//      String filesNameS2=ClassMapper.classToString(getElabProperties().get(NameElabs.LISTFILESS2));
      
    
  }
  
  private void elabBandeR1P4(Connection con){
    String pathFileR1P4=ClassMapper.classToString(getElabProperties().get(NameElabs.PATHFILESXMLR1P4));
    File dir=new File(pathFileR1P4);
    if(!dir.isDirectory()){
      addError(" Percorso file sorgente errato . Impossibile proseguire nell'elaborazione");
      return;
    }
    
    try{
      Date dataRif=DateUtils.getInizioGg(dataElab);
    
      Map filesToElab=getMapFilesToElab(pathFileR1P4, dataRif);
      
      if(filesToElab.isEmpty()){
        addWarning("Attenzione nessun file da processare ...");
        return;
      }
      
      Map filesProcessed =getMapFilesProcessed(con, dataRif);
      
      List elToSave=getListInfoToSave(pathFileR1P4, filesToElab, filesProcessed);
      
      if(elToSave.size()>0){
        PersistenceManager pm=new PersistenceManager(con);
        pm.saveListDt(new TabScaricoBndPanAs400(), elToSave);
        addInfo("Salvataggio dati effettuato --->" +elToSave.toString());
      }
      
    }catch(ParseException p){
      addError("Problemi in fase di conversione dei dati -->"+p.getMessage());
    } catch (SQLException ex) {
      addError("Errore in fase di accesso al db -->"+ex.getMessage());
    }  
    
  }
  
  private void elabPannelliR1P4(Connection con){
    Connection conSqlS=null;
    Long idTmp=Long.valueOf(0);
    try{
      List<List> elms=new ArrayList();
      conSqlS=ColombiniConnections.getDbLotto1Connection();
      
      String sql="SELECT [id]  ,[CodMat], [DatOraInsRec] \n  FROM [SirioLotto1].[dbo].[LogUtiMat] " +
                 "\n  where elaborato=0  \n order by DatOraInsRec ";
      
      
      ResultSetHelper.fillListList(conSqlS, sql, elms);
      for(List l:elms){
        PersistenceManager pm=new PersistenceManager(con);
        idTmp=ClassMapper.classToClass(l.get(0),Long.class);
        
        List l4AsTmp=TabScaricoBndPanAs400.getListElmForAs400(CDLNESTR1P4, ClassMapper.classToString(l.get(1)), Double.valueOf(1),
                "N" , REASONCODEONE, LOTTO, ClassMapper.classToClass(l.get(2),Date.class),idTmp.toString() );
        List ll=new ArrayList();
        ll.add(l4AsTmp);
        pm.saveListDt(new TabScaricoBndPanAs400(), ll);
        updateLogUtiMatSirioLotto1(conSqlS,idTmp);
      }
      
    }catch( SQLException s){
      _logger.error("Errore in fase di scarico dei pannelli p4 id : "+idTmp+" --> "+s.getMessage());
      addError("Errore in fase di scarico dei pannelli p4 id : "+idTmp+" --> "+s.getMessage() );
      
    }finally{
     if(conSqlS!=null)
       try{
       conSqlS.close();
       } catch(SQLException s){
         _logger.error("Errore in fase di chiusura della connessione con db SirioLotto1 -->"+s.getMessage());
       }
    }
    
    
  }
  
  
  
  private void updateLogUtiMatSirioLotto1(Connection conSqls,Long id) throws SQLException{
    PreparedStatement ps = null;
   
    try{
      ps=conSqls.prepareStatement(UPD_LOGUTILMAT); 
      ps.setLong(1, id);
      ps.execute();

    }finally{
      if(ps!=null)
        ps.close();
    }
  }
  
  
  
   protected Map getMapFilesToElab(String path ,Date dataRif){
     Map filesToElab=new HashMap();
     List<File> files=FileUtils.getListFileFolderForDate(path, dataRif, null);
     for(File f:files){
       filesToElab.put(f.getName(),new Date(f.lastModified()));
     }
     
     return filesToElab;
   } 
   
   protected Map getMapFilesProcessed(Connection con ,Date dataRif) throws SQLException{
     
     List<List> elabs=getListElabFromMvx(con,CDLSEZR1P4,dataRif);
     
     Map filesProcessed =new HashMap();
     for(List el:elabs){
       String otmName=ClassMapper.classToString(el.get(6));
       Date otmDate=ClassMapper.classToClass(el.get(5),Date.class);
       filesProcessed.put(otmName,otmDate);  
      }
     
     return filesProcessed;
   }
   
   protected List getListElabFromMvx(Connection con,String cdl,Date datarif) throws SQLException {
    List l=new ArrayList();
    String sql=TabScaricoBndPanAs400.getQryInfo(cdl, datarif);
    
    ResultSetHelper.fillListList(con, sql, l);
    
    return l;
  }
  
  protected List getListInfoToSave(String pathFileSource,Map filesToElab,Map filesProcessed){
    List infos=new ArrayList();
    Set keys=filesToElab.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String nomeFileTmp=ClassMapper.classToString(iter.next());
      Date dataFile=ClassMapper.classToClass(filesToElab.get(nomeFileTmp),Date.class);
      if(!filesProcessed.containsKey(nomeFileTmp)){
        List l=elabFileXml(pathFileSource,nomeFileTmp,dataFile);
        infos.addAll(l);
      }else{
        addWarning ("Attenzione file "+nomeFileTmp +" già processato ");
      }
    }
     
    return infos; 
  }  
   
   
   private List elabFileXml(String pathFile,String nomeFile,Date dataFile)  {
     List l=new ArrayList();
     
     try{
       //Map<String , List<Map>> mappaXml=XmlUtils.getInstance().getMapFromXml(pathFile+"/"+nomeFile);
       //List<Map>  listQtaBrd= mappaXml.get(XmlFileSezR1P4.XML_TAG_BRDINFO);
       //List<Map>  listInfoBrd= mappaXml.get(XmlFileSezR1P4.XML_TAG_BOARD);
       
       XmlFileSezR1P4 fileXml=new XmlFileSezR1P4(pathFile+"/"+nomeFile);
       fileXml.loadPropertiesMap();
       List<Map>  listQtaBrd=fileXml.getListBoardInfo();
       List<Map>  listInfoBrd=fileXml.getListBoardUtilization();
       for(int i=0; i<listQtaBrd.size();i++){
         Map qtMap=listQtaBrd.get(i);
         Double qta=ClassMapper.classToClass(qtMap.get(XmlFileSezR1P4.XML_BRDINFO_QUsed),Double.class);
         String brdId=ClassMapper.classToString(qtMap.get(XmlFileSezR1P4.XML_BRDINFO_BrdId));
         String codArt=null;
         if(qta>0){
           if(i<listInfoBrd.size()){
             Map infoMap=listInfoBrd.get(i);
             String brId=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_id));
             codArt=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_BrdCode));
             Integer type=ClassMapper.classToClass(infoMap.get(XmlFileSezR1P4.XML_BOARD_Type),Integer.class);
             if(brdId.equals(brId) && type>0){
               List rec=new ArrayList();
               rec.add(CDLSEZR1P4);
               rec.add(codArt);
               rec.add(qta);
               rec.add("N");
               rec.add("1");
               rec.add(""); //info per Lotto non gestito per le bande
               rec.add(dataFile);
               rec.add(nomeFile);
               rec.add("");
               l.add(rec);
              }
            }
          }
        }
      } catch(IOException i){
        addError("Errore in fase di accesso al file "+nomeFile+"  -->"+i.getMessage());
      } catch(ParserConfigurationException p){
        addError("Errore in fase di lettura del file "+nomeFile+"  -->"+p.getMessage());
      } catch(SAXException s){
        addError("Errore in fase di lettura del file "+nomeFile+"  -->"+s.getMessage());
      }
     
    return l;
   }
  

  
  
  
  
  
  private static final Logger _logger = Logger.getLogger(ElabFilesArtecInFebal.class);

 
}

