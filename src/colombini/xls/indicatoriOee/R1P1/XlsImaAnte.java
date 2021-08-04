/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.xls.indicatoriOee.R1P1;



import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.util.InfoMapLineeUtil;
import colombini.xls.indicatoriOee.R1.XlsImpiantiImaR1;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class XlsImaAnte extends XlsImpiantiImaR1 {
  
  private final static Integer COL_OREIMPROD_COMBIMA=new Integer(13);
  private final static Integer COL_OREIMPROD_SCHELLING=new Integer(9);
  
  private final static Integer COL_FERMI_COMBIMA=new Integer(15);
  private final static Integer COL_FERMI_SCHELLING=new Integer(11);
  
  private final static String IMP1="IMP.1";
  private final static String IMP2="IMP.2";
  
  public XlsImaAnte(String fileName) {
    super(fileName);
  }

  
  
  

  @Override
  public Date getInizioTurnoGg(Date data) {
    
    Date iniT= super.getInizioTurnoGg(data);
    if(iniT==null)
      return null;
    
    try {  
      Date turno6=DateUtils.getInizioTurnoM(data);
      if(iniT.before(turno6))
        iniT=turno6;
      
    } catch (ParseException ex) {
     _logger.error("Impossibile convertire la data fornita :"+ex.getMessage());
    }
    
    return iniT;
  }

  @Override
  public Date getFineTurnoGg(Date data) {
    Date finT= super.getFineTurnoGg(data);
    if(finT==null)
      return null;
    
    try {  
      Date turnoP=DateUtils.getFineTurnoP(data);
      if(finT.after(turnoP))
        finT=turnoP;
      
    } catch (ParseException ex) {
     _logger.error("Impossibile convertire la data fornita :"+ex.getMessage());
    }
    
    return finT;
    
  }

  
  public Long getMinutiImprodImpianto(Date data,String linea){
    Long tempoNProd=Long.valueOf(0);
    Map causali=null;
    Integer rgI=getRigaInizioGg(data);
    Integer rgF=getRigaFineGg(rgI);
    
    //carico la mappa delle causali delle ore non produttive
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA1R1P1).equals(linea) || 
            InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA2R1P1).equals(linea)){
      causali=getMappaValoriGG(rgI, rgF, COL_OREIMPROD_COMBIMA); 
    }else if( InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P1).equals(linea) ||
            InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P1).equals(linea)){
      causali=getMappaValoriGG(rgI, rgF, COL_OREIMPROD_SCHELLING);
    }
    
    //verifico se tutti i minuti devono 
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA1R1P1).equals(linea) || 
            InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P1).equals(linea))
      return getMinutiNNProd(causali, IMP1, IMP2);
    
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA2R1P1).equals(linea) ||
            InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P1).equals(linea))
      return getMinutiNNProd(causali, IMP2, IMP1);
    
    
    return tempoNProd;
  }
  
  private Long getMinutiNNProd(Map fermi,String lineaIncl,String lineaEscl){
    Long tempoNProd=Long.valueOf(0);
    if(fermi==null || fermi.isEmpty())
      return tempoNProd;
    
    Set keys=fermi.keySet();
    Iterator iter=keys.iterator();
    while(iter.hasNext()){
      String caus=(String) iter.next();
      Long valueTmp=ClassMapper.classToClass(fermi.get(caus),Long.class);
      if(caus.contains(lineaIncl)){
        tempoNProd+=valueTmp;
      }else if( caus.contains(lineaEscl)){
        
      }else{
        tempoNProd+=valueTmp/2;
      }
    }
    
    return tempoNProd;
  }
  
  
  @Override
  public List getListaFermiLinea(Integer rI,Integer rF,String linea){
    List fermi=new ArrayList();
    
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA1R1P1).equals(linea) || 
       InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.COMBIMA2R1P1).equals(linea)){
      fermi=getListaFermiGG(rI, rF, COL_FERMI_COMBIMA);
    }else if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING1R1P1).equals(linea) || 
             InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.SCHELLING2R1P1).equals(linea)){
      fermi=getListaFermiGG(rI, rF, COL_FERMI_SCHELLING);
    }
    
    return fermi;
  } 
  
//  /**
//   * Da rimuovere al termine del passaggio al nuovo sistema
//   */
//  public List getListaFermiGG(Date data,String linea){
//    List fermi=new ArrayList();
//    Integer rI=getRigaInizioGg(data);
//    Integer rF=getRigaFineGg(rI);
//    rI+=getDeltaRigheXls();
//    rF-=getDeltaRigheXls();
//    
//    if(OeeCostant.COMBIMA1R1P1.equals(linea) || OeeCostant.COMBIMA2R1P1.equals(linea)){
//      fermi=getListaFermiGG(rI, rF, COL_FERMI_COMBIMA);
//    }else if(OeeCostant.SCHELLING1R1P1.equals(linea) || OeeCostant.SCHELLING2R1P1.equals(linea)){
//      fermi=getListaFermiGG(rI, rF, COL_FERMI_SCHELLING);
//    }
//    
//    return fermi;
//  } 
//  
  
  private static final Logger _logger = Logger.getLogger(XlsImaAnte.class); 
}
