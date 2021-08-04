/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.R1;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import utils.XmlUtils;

/**
 *
 * @author lvita
 */
public class XmlFileSezR1P4 {
  public final static String XML_TAG_BOARD="Board";
  public final static String XML_TAG_BRDINFO="BrdInfo";
  public final static String XML_TAG_SOLUTION="Solution";
  public final static String XML_TAG_PATTERN="Pattern";
  public final static String XML_TAG_MATERIAL="Material";
  public final static String XML_TAG_DROP="Drop";
  public final static String XML_TAG_PIECE="Piece";
  
  public final static String XML_MATERIAL_CODE="Code";
  
  public final static String XML_BOARD_id="id";
  public final static String XML_BOARD_BrdCode="BrdCode";
  public final static String XML_BOARD_Type="Type";
  public final static String XML_BOARD_MatCode="MatCode";
  
  public final static String XML_BRDINFO_BrdId="BrdId";
  public final static String XML_BRDINFO_QUsed="QUsed";
  
  public final static String XML_DROP_Q="Q";
  public final static String XML_SOLUTION_Prod="Prod";
  public final static String XML_SOLUTION_Drop="Drop";
  public final static String XML_SOLUTION_Lost="Lost";
  public final static String XML_SOLUTION_Kerf="Kerf";
  public final static String XML_SOLUTION_Trim="Trim";
  public final static String XML_SOLUTION_Scrap="Scrap";
  public final static String XML_SOLUTION_BrdNo="BrdNo";
  public final static String XML_SOLUTION_CutLen="CutLen1";
  
  
  
  private String nomeFileCompleto;
  
  //mappa del file xml che ha come chiave il nome del tag  e come elemento una lista contenente una mappa chiave valore con tutte 
  //le proprietà del tag
  private Map <String , List<Map>> mapProperties=null;  
  
  public XmlFileSezR1P4 (String nomeFile){
    this.nomeFileCompleto=nomeFile;
  }

  public String getNomeFileCompleto() {
    return nomeFileCompleto;
  }

  public Map<String, List<Map>> getMapProperties() {
    return mapProperties;
  }
  
  public void loadPropertiesMap() throws ParserConfigurationException, SAXException, IOException{
    mapProperties=new HashMap <String , List<Map>> ();
    
    mapProperties=XmlUtils.getInstance().getMapFromXml(nomeFileCompleto);
    _logger.info("Mappa valori xml caricata ");
  }
  
  public List<Map> getListBoardUtilization(){
    
    return getListInfoMap(XML_TAG_BOARD);
  }
  
  public List<Map> getListBoardInfo(){
    
    return getListInfoMap(XML_TAG_BRDINFO);
  }
 
  public List<Map> getListSolution(){
    
    return getListInfoMap(XML_TAG_SOLUTION);
  }
 
   public List<Map> getListPattern(){
    
    return getListInfoMap(XML_TAG_PATTERN);
  }
   
  public List<Map> getListMaterial(){
    
    return getListInfoMap(XML_TAG_MATERIAL);
  } 
   
  public List<Map> getListDrop(){
    
    return getListInfoMap(XML_TAG_DROP);
  } 
  
  public List<Map> getListPiece(){
    
    return getListInfoMap(XML_TAG_PIECE);
  }
  
  protected List<Map> getListInfoMap(String tagXml){
    checkMap();
    
    return mapProperties.get(tagXml);
  } 
  
  protected void checkMap(){
    try{
      if(mapProperties==null)
        loadPropertiesMap();
      
    } catch(IOException i){
        _logger.error("Errore in fase di accesso al file "+nomeFileCompleto+"  -->"+i.getMessage());
    } catch(ParserConfigurationException p){
        _logger.error("Errore in fase di lettura del file "+nomeFileCompleto+"  -->"+p.getMessage());
    } catch(SAXException s){
        _logger.error("Errore in fase di lettura del file "+nomeFileCompleto+"  -->"+s.getMessage());
    }
  }
  
  
  private static final Logger _logger = Logger.getLogger(XmlFileSezR1P4.class);
}

