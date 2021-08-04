/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.logFiles.G1P2;



import colombini.costant.CostantsColomb;
import colombini.costant.MisureG1P2Costant;
import colombini.util.InfoMapLineeUtil;
import colombini.costant.NomiLineeColomb;
import colombini.logFiles.ALogFile;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class LogFileSquadrabordaLong extends ALogFile{

  public final static Integer EVPROD=Integer.valueOf(11);
  
  public final static String BORDATURA="BORDATURA";
  public final static String SEZIONATURA="SEZIONATURA";
  
  
  private String linea;
  
  public LogFileSquadrabordaLong(String file,String linea) {
    super(file);
    this.linea=linea;
  }
  
  @Override
  public Map processLogFile(Date dataInizio, Date dataFine) throws IOException, ParseException {
    if(getBfR()==null)
      return null;
    Long nriga=Long.valueOf(1);
    Map mapTempi=new HashMap();
    try{
    
    Date fineGg=DateUtils.getFineGg(dataInizio);
    
    String schema="";
    Integer bandeSchema=Integer.valueOf(0);
    Integer tagliSchema=Integer.valueOf(0);
    Integer tagliCiclo=Integer.valueOf(0);
    Integer bandeCiclo=Integer.valueOf(0);
    
    Double tempoLordo=Double.valueOf(0);
    Double tempoNetto=Double.valueOf(0);    
    Long bandeTot=Long.valueOf(0);
    Long cicli=Long.valueOf(0);
    boolean proxev11=false;
    
    String riga=getBfR().readLine();
   
    while(riga!=null && !riga.isEmpty()){
      List info=getListInfoRow(riga);
      Date dataLog= getDataLog(info);
      
      if(dataLog!=null && dataLog.after(fineGg)){
        break;
      }
//      _logger.info(riga);
      if(dataLog!=null && dataLog.after(dataInizio) && dataLog.before(dataFine) && getSpessoreBanda(info)!=null){
        if(EVPROD.equals(getTipoEvento(info))){
          if(StringUtils.isEmpty(schema) || !schema.equals(getSchema(info)) ){  
            schema=getSchema(info);
            bandeSchema=Integer.valueOf(0);
            tagliSchema=Integer.valueOf(0);
          }
          tagliCiclo=getTagliSchema(info)-tagliSchema;
          if(tagliCiclo<0)
            tagliCiclo=1;
          
          Double tempoGeo=getTempoGeoTrasversale(tagliCiclo, getLunghezzaBanda(info));
          if(tempoGeo<0)
            _logger.info("Tempo Geo negativo . Riga"+nriga);
          
          tempoLordo+=tempoGeo;
          Integer bandeCicloTeo=getBandeCicloTeo(getSpessoreBanda(info), getNumTagli(info));
          
          bandeCiclo=(getBandeSchema(info)-bandeSchema)*getNumTagli(info);
          if(bandeCiclo<0)
             bandeCiclo=bandeCicloTeo;
          
          bandeTot+=bandeCiclo;
          
          
          //caso in cui non è previsto lo spessore
          if(bandeCicloTeo==0){
//            System.out.println(riga);
            _logger.warn("Attenzione spessore "+getSpessoreBanda(info)+" non previsto nel calcolo delle bande ciclo teoriche");
            bandeCicloTeo=bandeCiclo;
          }
            
          if(bandeCiclo>bandeCicloTeo)
            bandeCiclo=bandeCicloTeo;
          
          if(bandeCicloTeo>0)
            tempoNetto+= ((bandeCiclo/new Double(bandeCicloTeo))*tempoGeo);
          
//          String appo=new Double(OeeGgUtils.getInstance().arrotonda(tempoGeo, 2)).toString();
//          System.out.println(getNLinea(info)+";"+appo.replace(".", ",") +";"+bandeCiclo+";"+bandeCicloTeo+";"+tempoNetto.longValue()+";"+tempoLordo.longValue());         
          cicli++;        
          //--------------------          
          Double incremento=((bandeCiclo/new Double(bandeCicloTeo))*tempoGeo);
          //_logger.info("RIGA:"+nriga+"; LB:"+getLunghezzaBanda(info)+"; SP:"+getSpessoreBanda(info)+"; TAGLICICLO:"+tagliCiclo+"; BANDECICLO:"+bandeCiclo+
          //                   "; NUMTAGLI:"+getNumTagli(info)+"; BANDECICLOTEO:"+bandeCicloTeo+"; INCTEMPO:"+incremento.longValue()+"; TRUN:"+tempoNetto.longValue());
          //--------------------
          if(getNumTagli(info)==2){
            proxev11=false;
            riga = getBfR().readLine();
            nriga++;
            while(riga!=null && !riga.isEmpty() && !proxev11){
               info=getListInfoRow(riga);
               if(EVPROD.equals(getTipoEvento(info))){
                 proxev11=true;
               }else{
                 riga=getBfR().readLine();
                 nriga++;
               }  
            }
          }
          bandeSchema=getBandeSchema(info);
          tagliSchema=getTagliSchema(info);
        } 
      }//fine if orario
      riga = getBfR().readLine();
      nriga++;
    }
    mapTempi.put(CostantsColomb.NCICLI,cicli);
    mapTempi.put(CostantsColomb.NPZTURNI,bandeTot);
    mapTempi.put(CostantsColomb.TRUNTIME,tempoNetto.longValue());
    mapTempi.put(CostantsColomb.TLORDO,tempoLordo.longValue());
    
    }catch(Exception ex){
      _logger.error("Riga n."+nriga); 
    }
    return mapTempi;
  }

  public Map loadDatiUtlsMateriali(Date data) throws ParseException, IOException{
    if(getBfR()==null)
      return null;
    Double sezionatura=Double.valueOf(0);
    Double bordatura=Double.valueOf(0);
    String schema="";
    Integer tagliSchema=Integer.valueOf(0);
    Integer tagliCiclo=Integer.valueOf(0);
    Map mapVal=new HashMap();
    Long nriga=Long.valueOf(0);
    String riga=getBfR().readLine();
    nriga++;
    while(riga!=null && !riga.isEmpty()){
      List info=getListInfoRow(riga);
      Date dataLog= getDataLog(info);
      
      if(dataLog!=null && DateUtils.daysBetween(dataLog, data)==0){
        if(EVPROD.equals(getTipoEvento(info))){
          if(StringUtils.isEmpty(schema) || !schema.equals(getSchema(info)) ){  
            schema=getSchema(info);
            tagliSchema=Integer.valueOf(0);
          }
          tagliCiclo=getTagliSchema(info)- tagliSchema;
          sezionatura+=tagliCiclo*getNumTagli(info)*getLarghezzaBandaD(info);
          bordatura+=tagliCiclo*getLunghezzaBandaD(info)*getNumTagli(info);
          
          tagliSchema=getTagliSchema(info);
        }
        riga = getBfR().readLine();
        nriga++;
      }  
    }
    
    mapVal.put(BORDATURA,bordatura/new Double(1000));
    mapVal.put(SEZIONATURA,sezionatura/new Double(1000));
    
    _logger.info("File "+getFileName()+" righe processate:"+nriga);

    return mapVal;
    
  }  
  
  public Map loadDatiProd(Date dataIni,Date dataFine) throws ParseException, IOException{
    if(getBfR()==null)
      return null;
    String schema="";
    Long totbande=Long.valueOf(0);
    Long totPz=Long.valueOf(0);
    Integer bandeSchema=Integer.valueOf(0);
    Integer tagliSchema=Integer.valueOf(0);
    Integer tagliCiclo=Integer.valueOf(0);
    Integer bandeCiclo=Integer.valueOf(0);
     boolean proxev11=false;
     
    Map mapVal=new HashMap();
    Long nriga=Long.valueOf(0);
    String riga=getBfR().readLine();
    nriga++;
    while(riga!=null && !riga.isEmpty()){
      List info=getListInfoRow(riga);
      Date dataLog= getDataLog(info);
      
      if(dataLog!=null && dataLog.after(dataIni) && dataLog.before(dataFine) ){
        if(EVPROD.equals(getTipoEvento(info))){
          if(StringUtils.isEmpty(schema) || !schema.equals(getSchema(info)) ){  
            schema=getSchema(info);
            tagliSchema=Integer.valueOf(0);
            bandeSchema=Integer.valueOf(0);
          }
          tagliCiclo=getTagliSchema(info)- tagliSchema;
          bandeCiclo=(getBandeSchema(info)-bandeSchema)*getNumTagli(info);
          if(bandeCiclo<0)
             bandeCiclo=getBandeCicloTeo(getSpessoreBanda(info), getNumTagli(info));;
          
          totbande+=bandeCiclo;
          totPz+=(bandeCiclo*tagliCiclo);
          
          if(getNumTagli(info)==2){
            proxev11=false;
            riga = getBfR().readLine();
            nriga++;
            while(riga!=null && !riga.isEmpty() && !proxev11){
               info=getListInfoRow(riga);
               if(EVPROD.equals(getTipoEvento(info))){
                 proxev11=true;
               }else{
                 riga=getBfR().readLine();
                 nriga++;
               }  
            }
          }
          
          bandeSchema=getBandeSchema(info);
          tagliSchema=getTagliSchema(info);
        }
        riga = getBfR().readLine();
        nriga++;
      }  
    }
    
    mapVal.put("PEZZI",totPz);
    mapVal.put("BANDE",totbande);
    
    _logger.info("File "+getFileName()+" righe processate:"+nriga+ " per periodo "+dataIni+" - "+dataFine);

    return mapVal;
    
  }  
  
  
  
  private List getListInfoRow(String riga){
    List info=new ArrayList();
    if(riga==null)
      return info;
    
    Object [] objArray=riga.split(";");
    info=ArrayUtils.getListFromArray(objArray);
    
    return info;
  }
  
  private Date getDataLog(List info) throws ParseException{
    if(info==null)
      return null;
    
    String s=ClassMapper.classToString(info.get(1));
    s=s.replace(".", ":");
    Date data=DateUtils.StrToDate(s, "yyyy-MM-dd HH:mm:ss");
    
    return data;
  }
  
  
  
  private Long getNLinea(List info) throws ParseException{
    if(info==null)
      return null;
    
    Long nl=ClassMapper.classToClass(info.get(0),Long.class);
    
    return nl;
  }
  
  private Integer getTipoEvento(List info) throws ParseException{
    if(info==null)
      return null;
    
    Integer ev=ClassMapper.classToClass(info.get(2),Integer.class);
    
    return ev;
  }
  
  private String getSchema(List info) throws ParseException{
    if(info==null)
      return null;
    
    String s1=ClassMapper.classToString(info.get(4));
    String s2=ClassMapper.classToString(info.get(5));
    
    return s1+s2;
  }
  
  private Integer getSpessoreBanda(List info) throws ParseException{
    if(info==null)
      return null;
    
    Integer ev=ClassMapper.classToClass(info.get(18),Integer.class);
    
    return ev;
  }
  
  private Long getLunghezzaBanda(List info) throws ParseException{
    if(info==null)
      return null;
    
    Double lb=ClassMapper.classToClass(info.get(19),Double.class);
    
    return lb.longValue();
  }
  
  
  //utile per la sezionatura
  private Double getLunghezzaBandaD(List info) throws ParseException{
    if(info==null)
      return null;
    
    Double lb=ClassMapper.classToClass(info.get(19),Double.class);
    
    return lb!=null ? lb : Double.valueOf(0);
  }
  
  //utile per la sezionatura
  private Double getLarghezzaBandaD(List info) throws ParseException{
    if(info==null)
      return null;
    
    Double lb=ClassMapper.classToClass(info.get(20),Double.class);
    
    return lb!=null ? lb : Double.valueOf(0);
  }
  
  
  
  private Integer getBandeSchema(List info) throws ParseException{
    if(info==null)
      return null;
    
    Integer bs=ClassMapper.classToClass(info.get(21),Integer.class);
    
    return bs;
  }
  
  
  private Integer getNumTagli(List info) throws ParseException{
    if(info==null)
      return null;
    
    Integer nt=null;
    try{
       nt=ClassMapper.classToClass(info.get(26),Integer.class);
    
    }catch(NumberFormatException f){
      _logger.error("Attenzione riga log con numTagli non definiti -->"+info.toString());
    }
    
    return nt == null ? Integer.valueOf(0) : nt ;
  }
  
  private Integer getTagliSchema(List info) throws ParseException{
    if(info==null)
      return null;
    
    Integer ev=ClassMapper.classToClass(info.get(27),Integer.class);
    
    return ev == null ? Integer.valueOf(0) : ev ;
  }
  
  
  private Integer getBandeCicloTeo(Integer spessore,Integer numTagli){
    Integer bandeTeo=Integer.valueOf(0);
    if(MisureG1P2Costant.SPESS14.equals(spessore)){
      bandeTeo=14*numTagli; 
    } else if(MisureG1P2Costant.SPESS16.equals(spessore)){
      bandeTeo=12*numTagli; 
    } else if(MisureG1P2Costant.SPESS18.equals(spessore)){
      bandeTeo=11*numTagli; 
    } else if(MisureG1P2Costant.SPESS25.equals(spessore)){
      bandeTeo=8*numTagli; 
    } else if(MisureG1P2Costant.SPESS35.equals(spessore)){
      bandeTeo=5*numTagli; 
    }
    
    return bandeTeo;
  }
  
//  tempo_geometria_trasversale = ((tagli - 1) * Worksheets("Geometrie").Cells(1, 3).Value) + 
//                                   Worksheets("Geometrie").Cells(2, 3).Value + 
//                                  (lunghezza * Worksheets("Geometrie").Cells(3, 3).Value)
  private Double getTempoGeoTrasversale(Integer tagli,Long lunghezza){
    Double tempoGeo=Double.valueOf(0);
    
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SQBL13266).equals(linea) ){
      tempoGeo=((tagli-1)*MisureG1P2Costant.TTAGLIOSEC13266) +
               MisureG1P2Costant.TULTIMOTAGLIOSEC13266+
               (lunghezza*MisureG1P2Costant.AVANZSECMM);
      
    } else if( InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SQBL13268).equals(linea) ){
      tempoGeo=((tagli-1)*MisureG1P2Costant.TTAGLIOSEC13268) +
               MisureG1P2Costant.TULTIMOTAGLIOSEC13268+
               (lunghezza*MisureG1P2Costant.AVANZSECMM);
    }
     
    return tempoGeo;  
  }

//  /**
//   * Torna il nome del file che è condizionato dalla data
//   * @param Date data
//   * @return String nome del file
//   * @throws ParseException 
//   */
//  public static String getLogFileName(Date data) throws ParseException{
//     String dtS=DateUtils.DateToStr(data, "yyyy-MM-dd");
//     
//    return dtS+".pst"; 
//  }
//  
//  /**
//   * Torna il path dove risiede il file della linea indicata
//   * @param String linea
//   * @return String path
//   */
//  public static String getPathLogFile(String linea) {
//     String pathFile="";
//    if(NomiLineeColomb.SQBL13266.equals(linea))
//       pathFile=MisureG1P2Costant.pathFileLog13266;
//    else if (NomiLineeColomb.SQBL13268.equals(linea))
//       pathFile=MisureG1P2Costant.pathFileLog13268; 
//     
//    return pathFile; 
//  }
//  
//  /**
//   * Torna il nome del file di log (comprensivo di path) di una determinata giornata.
//   * @param Date data
//   * @param String linea
//   * @return String percorso completo file
//   * @throws ParseException 
//   */
//  public static String getLogFileNameComplete(Date data,String linea) throws ParseException{
//    String path=getPathLogFile(linea);
//    String name=getLogFileName(data);
//    
//    
//    return path+name;
//  }
  
  private static final Logger _logger = Logger.getLogger(LogFileSquadrabordaLong.class);

  
}  