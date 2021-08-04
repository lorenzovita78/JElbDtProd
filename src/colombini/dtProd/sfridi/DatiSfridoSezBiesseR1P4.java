/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.costant.CostantsColomb;
import colombini.dtProd.R1.XmlFileSezR1P4;
import colombini.elabs.NameElabs;
import db.ResultSetHelper;
import exception.ElabException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import utils.ClassMapper;
import utils.DateUtils;
import utils.FileUtils;

/**
 *
 * @author lvita
 */
public class DatiSfridoSezBiesseR1P4 extends InfoSfridoCdL{
  
  
  

  private Map filesProcessed  =null;
  
  
  public DatiSfridoSezBiesseR1P4(String cdL) {
    super(cdL);
    this.filesProcessed=new HashMap();
    
  }

  @Override
  public List getInfoSfrido(Date dataInizio, Date dataFine) throws ElabException {
    List info=new ArrayList();
    String pathFile=(String) getParams().get(NameElabs.PATHFILESXMLR1P4);
    
    Map fileToElab=getMapFilesToElab(pathFile, dataInizio, dataFine);
    Map fileProcessed=new HashMap();  //da modificare per inserire i record già processati
    
    info=getListInfoToSave(pathFile,fileToElab,fileProcessed);
    
    return info;
  }
  
  
  protected Map getMapFilesToElab(String path ,Date dataInizio, Date dataFine){
     Map filesToElab=new HashMap();
     List<File> files=FileUtils.getListOnlyFilesForDate(path, dataInizio, dataFine);
     
     for(File f:files){
       filesToElab.put(f.getName(),new Date(f.lastModified()));
     }
     
     return filesToElab;
   } 
  
  
  protected void loadMapFilesProcessed(Connection con ,Date dataInizio,Date dataFine) throws SQLException{
     
    List<List> elabs=new ArrayList();
    String sql=" ";
    
    ResultSetHelper.fillListList(con, sql, elabs);
     
    for(List el:elabs){
      String otmName=ClassMapper.classToString(el.get(6));
      Date otmDate=ClassMapper.classToClass(el.get(5),Date.class);
      filesProcessed.put(otmName,otmDate);  
    }
    
  }
  
  
  private List getListInfoToSave(String pathFile, Map filesToElab, Map fileProcessed) {
    List infos=new ArrayList();
    Set keys=filesToElab.keySet();
    Iterator iter=keys.iterator();
    //addInfoColumn(infos);
    Integer contatore=Integer.valueOf(1);
    while(iter.hasNext()){
      String nomeFileTmp=ClassMapper.classToString(iter.next());
      Date dataFile=ClassMapper.classToClass(filesToElab.get(nomeFileTmp),Date.class);
      if(!filesProcessed.containsKey(nomeFileTmp)){
        List l=elabFileXml(pathFile,nomeFileTmp,dataFile,contatore);
        contatore++;
        infos.add(l);
      }else{
        //addWarning ("Attenzione file "+nomeFileTmp +" già processato ");
      } 
    }
    return infos;  
  }

  
//  private void addInfoColumn(List info){
//    List l=new ArrayList();
//    l.add("Ottimizzazione");
//    l.add("Materiale");
//    l.add("Bande usate nr");
//    l.add("Bande usate mq");
//    l.add("Resti usati nr");
//    l.add("Resti usati mq");
//    l.add("Prodotto mq");
//    l.add("Resti prod nr");
//    l.add("Resti prod mq");
//    l.add("SfridoLama mq");
//    l.add("SfridoRifilo mq");
//    l.add("Scarto mq");
//    
//    info.add(l);
//    
//  }
  
  private List elabFileXml(String pathFile, String nomeFileTmp, Date dataFile,Integer contatore) {
    List l=new ArrayList();
     
    try{
      _logger.info("Process file "+nomeFileTmp); 
      
      XmlFileSezR1P4 fileXml=new XmlFileSezR1P4(pathFile+"/"+nomeFileTmp);
      fileXml.loadPropertiesMap();
      
      List<Map>  listQtaBrd=fileXml.getListBoardInfo(); //Tag Brd Info 
      List<Map>  listInfoBrd=fileXml.getListBoardUtilization(); // Tag Board
      List<Map>  listPatterns=fileXml.getListPattern();
      List<Map>  listDrop=fileXml.getListDrop();
      List<Map> listPiece=fileXml.getListPiece();
    
      Double prodBr=Double.valueOf(0);
      Double dropBr=Double.valueOf(0);
      Double lostBr=Double.valueOf(0);
      Double qtaBr=Double.valueOf(0);
      
      Double prodRe=Double.valueOf(0);
      Double dropRe=Double.valueOf(0);
      Double lostRe=Double.valueOf(0);
      Double qtaRe=Double.valueOf(0);
      
      Double qtaDrop=Double.valueOf(0);
      Double qtaPz=Double.valueOf(0);
      Double kerf=Double.valueOf(0);
      Double trim=Double.valueOf(0);
      Double scrap=Double.valueOf(0);
      
      String material=null;
      String barCode="";
      for (int i=0;i<listQtaBrd.size();i++){
        Map boardMap=listQtaBrd.get(i);
        
        String brdId=ClassMapper.classToString(boardMap.get(XmlFileSezR1P4.XML_BRDINFO_BrdId));
         
        Double qta=ClassMapper.classToClass(boardMap.get(XmlFileSezR1P4.XML_BRDINFO_QUsed),Double.class);
        Double prodTmp=ClassMapper.classToClass(boardMap.get(XmlFileSezR1P4.XML_SOLUTION_Prod),Double.class);
        Double dropTmp=ClassMapper.classToClass(boardMap.get(XmlFileSezR1P4.XML_SOLUTION_Drop),Double.class);
        Double lostTmp=ClassMapper.classToClass(boardMap.get(XmlFileSezR1P4.XML_SOLUTION_Lost),Double.class);
        
        if(qta>0){
          Map infoMap=listInfoBrd.get(i);
          String brId=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_id));
          Integer type=ClassMapper.classToClass(infoMap.get(XmlFileSezR1P4.XML_BOARD_Type),Integer.class);
          if(brdId.equals(brId)){
            if(type>0){
              if(StringUtils.isEmpty(barCode))
                barCode=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_BrdCode));
              
              prodBr+=prodTmp;
              dropBr+=dropTmp;
              lostBr+=lostTmp;
              qtaBr+=qta;
              if(material==null)
                material=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_MatCode));
            }else{
              prodRe+=prodTmp;
              dropRe+=dropTmp;
              lostRe+=lostTmp;
              qtaRe+=qta;
              if(material==null)
                material=ClassMapper.classToString(infoMap.get(XmlFileSezR1P4.XML_BOARD_MatCode));
            }
          }
        }
      }
      
      for(Map pattern:listPatterns){
         kerf+=ClassMapper.classToClass(pattern.get(XmlFileSezR1P4.XML_SOLUTION_Kerf),Double.class);
         trim+=ClassMapper.classToClass(pattern.get(XmlFileSezR1P4.XML_SOLUTION_Trim),Double.class);
         scrap+=ClassMapper.classToClass(pattern.get(XmlFileSezR1P4.XML_SOLUTION_Scrap),Double.class);
      }
      
      if(listDrop!=null){
        for(Map drop:listDrop){
           qtaDrop+=ClassMapper.classToClass(drop.get(XmlFileSezR1P4.XML_DROP_Q),Double.class);
        }
      }
      
      if(listPiece!=null){
        for(Map piece:listPiece){
           qtaPz+=ClassMapper.classToClass(piece.get(XmlFileSezR1P4.XML_DROP_Q),Double.class);
        }
      }
      
//      l.add(nomeFileTmp);
//      l.add(material);
//      l.add(qtaBr);
//      l.add((prodBr+dropBr+lostBr)/1000000);
//      l.add(qtaRe);
//      l.add((prodRe+dropRe+lostRe)/1000000);
//      l.add((prodBr+prodRe)/1000000);
//      l.add(qtaDrop);
//      l.add((dropBr)/1000000);
//      l.add((kerf)/1000000);
//      l.add((trim)/1000000);
//      l.add((scrap)/1000000);
      
      
      l.add(CostantsColomb.AZCOLOM);
      l.add(ISfridoInfo.SEZR1P4);
      l.add(DateUtils.getDataForMovex(dataFile));
      l.add(contatore);
      //l.add(nomeFileTmp);
      l.add(material);
      l.add(barCode);
      
      l.add(Double.valueOf(0));
      l.add(Double.valueOf(0));
      l.add(Double.valueOf(0));
      
      l.add(qtaBr);
      l.add(qtaPz);
      l.add((prodBr+dropBr+lostBr+prodRe+dropRe+lostRe)/1000000); //totale disponibile
      //l.add(qtaRe);
      //l.add((prodRe+dropRe+lostRe)/1000000);
      l.add((prodBr+prodRe+dropBr)/1000000); //totale prodotto
      l.add(Double.valueOf(0));
      //l.add(qtaDrop);
      l.add((dropBr)/1000000); //sovraproduzione
      
      l.add((kerf)/1000000); //rifilo
      l.add((trim)/1000000); //lama
      
      l.add(qtaDrop);              //qta resti prodotti
      l.add((prodRe+dropRe+lostRe)/1000000); // Mq Resti Utilizzati
      l.add(qtaRe);                //qta Resti Utilizzati 
      
      l.add(Double.valueOf(0));  //sup Pers4
      
//      //l.add((scrap)/1000000);
      
    } catch(IOException i){
       _logger.error("Errore in fase di accesso al file "+nomeFileTmp+"  -->"+i.getMessage());
    } catch(ParserConfigurationException p){
       _logger.error("Errore in fase di lettura del file "+nomeFileTmp+"  -->"+p.getMessage());
    } catch(SAXException s){
       _logger.error("Errore in fase di lettura del file "+nomeFileTmp+"  -->"+s.getMessage());
    }
    
    return l;
  }
  
  
  
  private static final Logger _logger = Logger.getLogger(DatiSfridoSezBiesseR1P4.class); 
  
  



}