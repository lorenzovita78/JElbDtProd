/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.costant;

import colombini.util.InfoMapLineeUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che associa al nome della linea ( vedi classe <rif> OeeLinee <rif> )
 * il codice utilizzato per il calcolo degli OEE
 * @author lvita
 */
public class OeeCostant {
  
 
 
  //codifica Pantografi Bit@Bit  
  public static final String PANT_BIT2519 = "2519";
  public static final String PANT_BIT1493 = "1493";
  public static final String PANT_BIT22146 = "22146";
  public static final String PANT_BIT22110 = "22110";
  public static final String PANT_BIT4001 = "4001";
  public static final String PANT_BIT4038 = "4038";
  
  public static final String IMAGALPRINC = "01001";
  public final static String MAWARTEC="03003";  
  
  
  //GALAZZANO P2
//  public static final String IMAGAL_13200 = "01012";
//  public static final String IMAGAL_13267 = "01013";
//  public static final String IMAGAL_13269 = "01014";
//  public static final String IMAGALPRINC = "01001";
//  public static final String SCHELLLONG_GAL = "01000";
//  public static final String SQBL13266 = "01266";
//  public static final String SQBL13268 = "01268";
//  //foratrici
//  public static final String FORBIESSE="01002";
//  public static final String FORMORBIDELLI="01006";
//  public static final String FORMAW="01007";
//  public static final String PANTMORBIDELLI="01022";
//  public static final String FORBUSELLATO="01036";
//  
  
//ROVERETA 1 
  //LINEA IMA ANTE R1 P1
//  public final static String COMBIMA1R1P1="01008C1";
//  public final static String COMBIMA2R1P1="01008C2";
//  public final static String SCHELLING1R1P1="01008S1";
//  public final static String SCHELLING2R1P1="01008S2";
//  public final static String SPALLA1="SPALLA1";
//  public final static String SPALLA2="SPALLA2";
//  public final static String SPALLA1OPA="SPALLA1OPA";
//  public final static String SPALLA2OPA="SPALLA2OPA";
//  public final static String SPALLA1LUC="SPALLA1LUC";
//  public final static String SPALLA2LUC="SPALLA2LUC";
              
//  //LINEA IMA TOP R1 P0
//  public final static String COMBIMAR1P0="01035C0";
//  public final static String SCHELLING1R1P0="01035S1";
//  public final static String SCHELLING2R1P0="01035S2";
//  public final static String FORPRIESS1R1P0="01035F1";
//  public final static String FORPRIESS2R1P0="01035F2";
//  public final static String SORTSTATIONR1P0="01035SS";
  
  
  //LINEA MAW FIANCHI 
//  public final static String MAWFIANCHI2="01029";
  
//  public final static String MAWARTECL1="03003_M1";
//  public final static String MAWARTECL2="03003_M2";
  
//  public final static String STRETTOIOMAW="01024";
//  public final static String STRETTOIOPRIESS="01025";
//  public final static String IMBCAPPELLI="01041";
//  public final static String TAVOLISCRIV="01027";
//  public final static String FORALBERTI="01020";
//  
//  
//  public final static String FORANTEREM="03001";
//  
//  //ROVERETA 2
//  //P1
//  public final static String SQBSTEFANI="01062";
//  public final static String HOMAGPOST="01010";
//  public final static String SEZGABBIANI="01004";
  
  
  
  
  public static String getCentroBitaBit(String centrodiLavoroMovex ){
   
//    if(OeeCostant.PANTH2519.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT2519;
//    }else if (OeeCostant.PANTH1493.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT1493;
//    }else if (OeeCostant.PANTB22146.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT22146;
//    }else if (OeeCostant.PANTB22110.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT22110;
//    }else if (OeeCostant.PANTB4001.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT4001;
//    }else if (OeeCostant.PANTH4038.equals(centrodiLavoroMovex)){
//      return OeeCostant.PANT_BIT4038;        
//    }
    if(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH2519).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT2519;
    }else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT1493;
    }else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT22146;
    }else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT22110;
    }else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB4001).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT4001;
    }else if (InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH4038).equals(centrodiLavoroMovex)){
      return OeeCostant.PANT_BIT4038;        
    }
    
    
    return"";
  }
  
  
  public static List getCentriPantografi(){
    List pantografi=new ArrayList();
    //    pantografi.add(PANTH2519);
    //    pantografi.add(PANTH1493);
    //    pantografi.add(PANTB22146);
    //    pantografi.add(PANTB22110);
    //    pantografi.add(PANTB4001);
    //    pantografi.add(PANTH4038);
    
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH2519));
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH1493));
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22146));
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB22110));
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTB4001));
    pantografi.add(InfoMapLineeUtil.getCodiceLinea(NomiLineeColomb.PANTH4038));

    
    return pantografi;
  }
  
}
