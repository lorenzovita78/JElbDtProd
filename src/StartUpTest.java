

import db.persistence.PersistenceManager;
import colombini.conn.ColombiniConnections;
import colombini.costant.CostantsColomb;
import colombini.dtProd.R1.XmlFileSezR1P4;
import colombini.model.persistence.ColliVolumiCommessaTbl;
import colombini.query.produzione.FilterQueryProdCostant;
import colombini.query.produzione.QueryCommToShip;
import colombini.query.produzione.QueryConteggioColliBu;
import colombini.util.DatiProdUtils;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.QueryException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.ClassMapper;
import utils.DateUtils;
import utils.ParameterMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author lvita
 */
public class StartUpTest {

  /**
   * @param args the command line arguments
   */
  
  
  private final static String FILEELABS="../props/elabsSchedJLV.config";
  private final static String FILEPROSELABS="../props/elabsJLV.properties";
  
  
  
  public static void main(String[] args) throws ParseException, IOException, SQLException, QueryException {
   
    _logger.info(" -----  AVVIO APPLICAZIONE ... #### \t\t\t\t -----");
    System.out.println("----- AVVIO APPLICAZIONE  ... -----");
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    List<Map> listaElabs=new ArrayList();
    Boolean checkElab=Boolean.TRUE;
    try {
         System.out.println(".. Lettura configurazione elaborazione da riga di comando...");
         _logger.info(".. Lettura configurazione elaborazione da riga di comando..."); 
         String prm="";
         //ELBCODALAVFORG1
   //      prm="CODELAB=ELBCOEEDAY;DATAINI=09/12/2019;DATAFIN=11/12/2019;RICALSETT=TRUE;MAIL=FALSE;LINEELAB=01044";//;
//          prm="CODELAB=ELBXLSMANUT;DATAINI=29/05/2016;DATAFIN=29/05/2016;";
//         prm="CODELAB=ELBLOGFILEPROD;DATAINI=23/10/2017;DATAFIN=24/10/2017;RICALCMAT=TRUE;LINEELAB=COMBIMA1R1P1,COMBIMA2R1P1,SCHELLING1R1P1,SCHELLING2R1P1,COMBIMAR1P0,SCHELLING1R1P0,SCHELLING2R1P0,";//
 //          prm="CODELAB=ELBMNGFILES;TYPEOPR=DELETE;TYPEDT=DYR;VALUEG=-3;PATHSRC=\\\\scarico_dati\\3tec\\P1\\pdf_desmos;";
       //       prm="CODELAB=ELBAVPRODCOM;DATAINI=10/05/2019;DATAFIN=10/05/2019;LINEELAB=01020";//
 //           prm="CODELAB=ELBMANCANTIFROMVDL;DATAINI=29/09/2017;DATAFIN=29/09/2017;TYPEOPR=POM;MAIL=TRUE";//FRCNCOMM=265";
//          prm="CODELAB=ELBTWBXFORBI;WKBKTABLEAU=TEST_DS_Vendite_AreaM_ITALIA;PROJTABLEAU=PoCAndDevelopment;TYPEDEST=M;MACROM=I;DATANELNOME=S"; 
     //     prm="CODELAB=ELBCOSTISFRIDO;DATAINI=01/12/2020;DATAFIN=31/12/2020";
  //       prm="CODELAB=ELBMANCANTIFROMVDL;DATAINI=12/06/2019;DATAFIN=12/06/2019;TYPEOPR=MATT;MAIL=FALSE;";//FRCNCOMM=075;DESCRCOMM=Mattina;";
    //    prm="CODELAB=ELBMANCANTIFROMVDL;DATAINI=14/01/2021;DATAFIN=14/01/2021;TYPEOPR=MATT;MAIL=TRUE;DESCRCOMM=Pomeriggio;FRCNCOMM=013";
 //          prm="CODELAB=ELBCTRLSUPPLY;DATAINI=10/07/2019;DATAFIN=10/07/2019;MAIL=FALSE;";
  //          prm="CODELAB=ELBSFRIDGRUPPO;DATAINI=04/12/2017;DATAFIN=05/12/2017";
 //           prm="CODELAB=ELBSFRIDGRUPPOOLD;DATAINI=09/03/2017;DATAFIN=10/03/2017";
 //           prm="CODELAB=ELBTWBXFORBI;WKBKTABLEAU=CruscottoVendite_STORE_ITALIA;PROJTABLEAU=Vendite;TYPEDEST=K;MACROM=I;FILEXMLPORT=D:\\1_svilJava\\zzAppoFile\\regolaKeyAccount.xml";
 //           prm="CODELAB=ELBFILEDESCRPHR;DIRSOURCE=D:\\vvTest\\elabJava\\NullaOsta\\;MODELNAME=NULLAOSTA;CODAZIENDA=SM02000;ANNORIF=2017;STRUCTFILENAME=$CODISS$.pdf";
 //           prm="CODELAB=ELBFILEDESCRPHR;DIRSOURCE=D:\\vvTest\\elabJava\\xxx\\dicSpont;MODELNAME=DICREDPAR;CODAZIENDA=SM02000;ANNORIF=2017;CHARSPLIT=_;STRUCTFILENAME=XXX_XXX_XXX_XXX_$CODISS$.pdf";
  //          prm="CODELAB=ELBFILEDESCRPHR;DIRSOURCE=D:\\vvTest\\elabJava\\xxx\\IGR;MODELNAME=IGR;ANNORIF=2017;CHARSPLIT=_;STRUCTFILENAME=XXX_$CODAZ$_$CODISS$_XXX_XXX_XXX.pdf";
  //          prm="CODELAB=ELBFILEDESCRPHR;DIRSOURCE=D:\\vvTest\\elabJava\\xxx\\IGR-R;MODELNAME=RICEVIGR;ANNORIF=2017;CHARSPLIT=_;STRUCTFILENAME=XXX_$CODAZ$_$CODISS$_XXX_XXX_XXX.pdf";
  //           prm="CODELAB=ELBSFRIDSEZR1P4;DATAINI=01/02/2019;DATAFIN=28/02/2019;";
    //        prm="CODELAB=ELBCOPYDTPROD;DATAINI=03/05/2019;DATAFIN=09/05/2017;LINEELAB=01020";
        //     prm="CODELAB=ELBCOLLIEXTINVDL;DATAINI=CRDAY;DATAFIN=CRDAY;";
     //      prm="CODELAB=ELBSTOREDTPRODMT;DATAINI=01/10/2020;DATAFIN=31/10/2020;";//LINEELAB=01084";
          //   prm="CODELAB=ELBDATIPRODCOMM;DATAINI=26/03/2021;DATAFIN=26/03/2021";//LINEELAB=01035I";
      //       prm="CODELAB=ELBCARICOCOM;COMMESSA=40;DATACOMM=09/02/2021;TIPOCOMM=0;LINEELAB=06516";
     //       prm="CODELAB=ELBDSCARTIXOTT;DATAINI=28/04/2021;DATAFIN=28/04/2021;LINEELAB=01084;";
 //           prm="CODELAB=ELBLOGBIMA;DATAINI=01/01/2020;DATAFIN=25/05/2020;";//LINEELAB=01035I";
//              prm="CODELAB=ELBDTIMPIMAR1;DATAINI=11/09/2017;DATAFIN=11/09/2017;";
    //          prm="CODELAB=ELBDSCARTIIMATOPR1;DATAINI=11/09/2017;DATAFIN=17/09/2017;";
  //          prm="CODELAB=ELBLOGFILEPROD;LINEELAB=SCHELLING1R1P0,SCHELLING1R1P1;DATAINI=09/10/2017;DATAFIN=11/10/2017;RICALCMAT=TRUE;";
    //        prm="CODELAB=ELBCTRLVASISTASANTE;DATAINI=CRDAY;DATAFIN=CRDAY;";
    //      prm="CODELAB=ELBPZTOPRODIMACLIR1P1;DATAINI=CRDAY;DATAFIN=CRDAY";
       //      prm="CODELAB=ELBMDLWRAS2VDL;DATAINI=CRDAY;DATAFIN=CRDAY";
             //  prm="CODELAB=ELBMNGFILES;TYPEOPR=COPY;PATHSRC=\\\\Sezp4wn6\\export\\Xml;PATHDST=\\\\srvhelpdesk\\LogFileProd\\R1\\SezP4;TYPEDT=DFF;VALUEG=-1";
             //  prm="CODELAB=ELBMNGFILES;TYPEOPR=MOVE;PATHSRC=\\\\pegaso\\flussi\\FileProd\\COLOMB\\ANTEIMA\\TEST;PATHDST=\\\\pegaso\\flussi\\FileProd\\COLOMB\\ANTEIMA\\TEST\\BCK;TYPEDT=DFF;VALUEG=30";
  //           prm="CODELAB=ELBFILESARTECINFEBAL;DATAINI=CRDAY;DATAFIN=CRDAY;";
//             prm="CODELAB=ELBSCARICOBANDE;DATAINI=CRDAY;DATAFIN=CRDAY";
//             prm="CODELAB=ELBDSCARTIIMATOPR1;DATAINI=CRDAY-2;DATAFIN=CRDAY-2";
//             prm="CODELAB=ELBTWBXFORBI;WKBKTABLEAU=CruscottoVendite_Mostre;PROJTABLEAU=Vendite;TYPEDEST=C;MACROM=I;FILEXMLPORT=D:\\99_temp\\01TableauXml\\regolaClientiMostra.xml";
  //             prm="CODELAB=ELBSFRIDSEZR1P4;DATAINI=01/01/2019;DATAFIN=31/01/2019";
  //   prm="CODELAB=ELBMNGFILES;TYPEOPR=DELETE;TYPEDT=DYR;VALUEG=-5;PATHSRC=\\\\scarico_dati\\3tec\\P1\\pdf_desmos";
 //        prm="CODELAB=ELBLOGFILETOTCOMB;DATAINI=CRDAY;DATAFIN=CRDAY;";    
 //        prm="CODELAB=ELBLOGFILEPROD;LINEELAB=SCHELLING1R1P0,SCHELLING2R1P0;DATAINI=08/10/2018;DATAFIN=09/10/2018;";
        //prm="CODELAB=ELBFILESARTECINFEBAL;DATAINI=CRDAY-1;DATAFIN=CRDAY-1;FILEPROPSELAB=../props/filesZoccoli.properties;PATHFILECOMM=//pegaso/flussi/FileProd/ARTEC/COMMESSE/$$$FINE.PDF";   //FORCEWRITE=True;COMMESSA=255;
    //  prm="CODELAB=ELBFILESARTECINFEBAL;DATAINI=CRDAY-1;DATAFIN=CRDAY-1;FILEPROPSELAB=../props/filesAnteRem.properties;PATHFILECOMM=//pegaso/flussi/FileProd/ARTEC/COMMESSE/$$$FINE.PDF;FORCEWRITE=True;COMMESSA=127;COMMTEST=S";
     // prm="CODELAB=ELBFILESARTECINFEBAL;DATAINI=CRDAY-1;DATAFIN=CRDAY-1;FILEPROPSELAB=../props/filesArtec.properties;PATHFILECOMM=//pegaso/flussi/FileProd/ARTEC/COMMESSE/$$$FINE.PDF;COMMESSA=127;FORCEWRITE=True;COMMTEST=S";
   //      prm="CODELAB=ELBFILESARTECINFEBAL;DATAINI=29/01/2020;DATAFIN=29/01/2020;FILEPROPSELAB=../props/filesZoccoli.properties;PATHFILECOMM=//pegaso/flussi/FileProd/ARTEC/COMMESSE/$$$FINE.PDF;";
// //         prm="CODELAB=ELBSTOREDTPRODMT;DATAINI=01/01/2018;DATAFIN=30/09/2018;";//LINEELAB=01039";
   //         prm="CODELAB=ELBMANPRECOMR1P3;DATAINI=29/03/2019;DATAFIN=29/03/2019;";
         //   prm="CODELAB=ELBDSCARTIIMALOTTO1;DATAINI=18/09/2019;DATAFIN=18/09/2019;LINEELAB=01084"; 
    //    prm="CODELAB=ELBCTRLPUBBLB2B;DATAINI=15/07/2019;DATAFIN=15/07/2019;"; 
    //    prm="CODELAB=ELBUTLMATERIALIPROD;DATAINI=01/06/2019;DATAFIN=12/06/2019;LINEELAB=SEZBIESSEP4;"; 
//     prm="CODELAB=ELBCARICOCOM;COMMESSA=179;DATACOMM=28/06/2019;TIPOCOMM=0;LINEELAB=01084"; 
//     prm="CODELAB=ELBAVPRODCOM;DATAINI=15/06/2019;DATAFIN=15/06/2019;LINEELAB=01084";
  //       prm="CODELAB=ELBMDLWRAS2VDL;DATAINI=CRDAY;DATAFIN=CRDAY;";
  //       prm="CODELAB=ELABSFRIDIMATOP;DATAINI=01/03/2019;DATAFIN=30/06/2019;";
        //prm="CODELAB=ELBSCRCPANR1P4;DATAINI=09/09/2019;DATAFIN=09/09/2019;";
        // prm="CODELAB=ELBCOSTISFRIDO;DATAINI=01/02/2019;DATAFIN=30/06/2019;";
        // prm="CODELAB=ELBCOEEDAY;DATAINI=15/07/2019;DATAFIN=30/07/2019;RICALSETT=TRUE;MAIL=FALSE;LINEELAB=01075,01076;";
     //    prm="CODELAB=ELBGESTLIBRETTI;DATAINI=18/12/2020;DATAFIN=18/12/2020;COPYFILES=N;COMMESSA=9";
    //     prm="CODELAB=ELBCHECKDTPROD;DATAINI=CRDAY-9;DATAFIN=CRDAY-5;PIANO=P1;STAB=R1";
       //  prm="CODELAB=ELBCIRCOLAREAGNT;DATAINI=CRDAY;DATAFIN=CRDAY;";
      // prm="CODELAB=ELBDTSTRETTARTEC;DATAINI=30/09/2020;DATAFIN=30/09/2020;COMMESSA=283;";//COMMAFEBAL=S";
         //prm="CODELAB=ELBSTOREDTPRODMT;DATAINI=01/01/2019;DATAFIN=31/03/2019;";
         //prm="CODELAB=ELBSTOREDTPRODWK;DATAINI=01/01/2019;DATAFIN=31/12/2019;";
        // prm="CODELAB=ELBLOGFILETAVOLI;DATAINI=19/03/2020;DATAFIN=19/03/2020;";
        // prm="CODELAB=ELBSFRIDCCUTR1P4;DATAINI=01/03/2020;DATAFIN=31/03/2020;";
    //     prm="CODELAB=ELBMAILCTRLQUAL;DATAINI=CRDAY;DATAFIN=CRDAY;";
         //prm="CODELAB=ELBMANCANTIVDLTEST;DATAINI=CRDAY;DATAFIN=CRDAY;TYPEOPR=MATT;MAIL=TRUE;";//DESCRCOMM=Pomeriggio;";
//         prm="CODELAB=ELBMNGFILES;TYPEOPR=MOVE;TYPEDT=DYR;VALUEG=-15;PATHSRC=\\\\scarico_dati\\3tec\\P1\\pdf_desmos;PATHDST=\\\\scarico_dati\\3tec\\P1\\pdf_desmos\\OLD";
     //    prm="CODELAB=ELBMAILCTRLMSGINCASVDL;DATAINI=CRDAY-1;DATAFIN=CRDAY-1;TYPEMSG=BundleData";
       //  prm="CODELAB=ELBMAILCTRLMSGINCASVDL;DATAINI=CRDAY-1;DATAFIN=CRDAY-1;TYPEMSG=BundleData";
//         prm="CODELAB=ELBDTIMPIMAR1;TIMEMINEXC=402;TYPESCHED=WEEK;PERIOD=1,1,1,1,1,1,1;DATAINI=PRDAY;DATAFIN=PRDAY";
//         prm="CODELAB=ELBMANCANTIFROMVDL;DATAINI=CRDAY;DATAFIN=CRDAY;TYPEOPR=MATT;MAIL=TRUE;DESCRCOMM=Errata Corrige";
         //  prm="CODELAB=ELBSFRIDSEZR1P4;DATAINI=01/06/2020;DATAFIN=30/06/2020;";
         
         //prm="CODELAB=ELBDSCARTIIMALOTTO1;DATAINI=24/02/2021;DATAFIN=26/02/2021";
        // prm="CODELAB=ELBSFRIDCCUTR1P4;DATAINI=01/04/2021;DATAFIN=11/04/2021";
        // prm="CODELAB=ELBCOSTISFRIDO;DATAINI=01/03/2021;DATAFIN=31/03/2021";
        
        prm="CODELAB=ELBDTSTRETTARTEC;DATAINI=CRDAY-3;DATAFIN=CRDAY-3"; //;COMMESSA=64";
       //prm="CODELAB=ELBDATIPRODCOMM;DATAINI=28/06/2021;DATAFIN=29/06/2021;MAIL=FALSE";
      // prm="CODELAB=ELBLOADFERMIDTPROD;DATAINI=25/06/2021;DATAFIN=25/06/2021;LINEELAB=01084B";
       //prm="CODELAB=ELBUPDDTAMGAM;DATAINI=23/06/2021;DATAFIN=23/06/2021;MAIL=FALSE";
      //  prm="CODELAB=ELBCARICOCOM;DATAINI=04/06/2021;DATAFIN=04/06/2021;MAIL=FALSE";
         Map parameter =ParameterMap.getParameterMap(prm);         
         if(parameter==null || parameter.isEmpty()){
           System.out.println("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
           _logger.info("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
         }else{ 
           _logger.debug("Mappa parametri letti per riga: "+parameter.toString());
           listaElabs.add(parameter);
           checkElab=Boolean.FALSE;
         }
         
         

//         System.out.println(test.toString());
               
//         Connection con =Connections.getOracleConnection("192.168.112.13", "COLOMBINI-WMSDB", "COLMSL", "pasta");
//         if(con!=null){
//           System.out.println("Yuhhuhu");
//           con.close();
//         }else{
//           System.out.println("NUUUUUUUUUU");
//         }
         
//         LogFileSquadrabordaLong lg=new LogFileSquadrabordaLong("\\\\srvhelpdesk\\LogFileProd\\01266_20160526.log", NomiLineeColomb.SQBL13266);
//         lg.initialize();
//         Date d=DateUtils.strToDate("26/05/2016", "dd/MM/yyyy");//new Date()
//         Map prod=lg.loadDatiProd(DateUtils.getInizioGg(d), DateUtils.getFineGg(d));
////         Map prod2=lg.processLogFile(DateUtils.getInizioGg(new Date()),DateUtils.getFineGg(new Date()));
//         System.out.println(prod.toString());
//         System.out.println(prod.toString());
//       }else{
//        System.out.println(".. Lettura configurazione elaborazioni da lanciare  da file config...");
//        _logger.info(".. Lettura configurazione elaborazioni da lanciare da file config...");
//        fR = new FileReader(FILEELABS);
//        bfR=new BufferedReader(fR);
//
//        riga = bfR.readLine();  
//        while(riga!=null ){
//          count++;
//          if(!riga.isEmpty() && !riga.contains("#")){
//           Map parameter =ParameterMap.getParameterMap(riga);
//           if(parameter==null || parameter.isEmpty()){
//             System.out.println("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
//             _logger.info("Stringa parametri vuota o valorizzata in maniera errata. Impossibile Proseguire");
//           }else{ 
//             _logger.debug("Mappa parametri letti per riga: "+parameter.toString());             
//             listaElabs.add(parameter);
//           }
//          }
//          riga=bfR.readLine(); 
//        }
//       }
      //lancio il gestore delle elaborazioni 
//     test();    
 //     testConn();   
 //       testXML();

      LuncherElabDtProd lunch=new  LuncherElabDtProd(listaElabs);
      lunch.run(checkElab);
      
//    } catch(FileNotFoundException ex){
//      _logger.error("File "+FILEELABS +" non trovato. Impossibile lanciare l'elaborazione!!");
//    } catch(IOException ex){
//      _logger.error("Errore in fase di lettura del file"+FILEELABS +" --> "+ex.getMessage());
    }finally{
      _logger.info("File "+FILEELABS+" righe lette :"+count);
      try{
        if(bfR!=null)
          bfR.close();
        if(fR!=null)
          fR.close();
      } catch(FileNotFoundException ex){
        _logger.warn("Errore in fase di chiusura del file"+FILEELABS +" --> "+ex.getMessage());
      } catch(IOException ex){
        _logger.warn("Errore in fase di chiusura del file"+FILEELABS +" --> "+ex.getMessage());
      }
      
      System.out.println("----- TERMINE APPLICAZIONE... -----"); 
      _logger.info(" ----- \t\t\t\t #### TERMINE APPLICAZIONE #### \t\t\t\t -----");
    }  
  }
  
  
  private static final Logger _logger = Logger.getLogger(StartUpTest.class); 

  private static void test() throws SQLException, QueryException {
    Connection con = ColombiniConnections.getAs400ColomConnection();
    
    Date dataCorrente=DateUtils.strToDate("06/08/2016", "dd/MM/yyyy");;
    Date dataTmp=DateUtils.strToDate("01/08/2016", "dd/MM/yyyy");
    
    while(DateUtils.beforeEquals(dataTmp, dataCorrente)){
      Long giornoCorrenteN=DateUtils.getDataForMovex(dataTmp);
      dataTmp=DateUtils.addDays(dataTmp, 1);
      
      DatiProdUtils.getInstance().elabColliVolumiCommesse(con, giornoCorrenteN,Boolean.TRUE);

    }
  }
  
  private static void testConn() throws SQLException{
    List l=new ArrayList();
    Connection con =ColombiniConnections.getDbIncasConnection();
    
    ResultSetHelper.fillListList(con, "select top 1000 * from [EsColombiniPre74].[dbo].[HSTVDLTX]", l);
    
    System.out.println(" -->"+l.toString());
  }
  
  private static String getCommessa(Connection con,Long dataCorrN){
    String comm=null;
    try{
    QueryCommToShip qry=new QueryCommToShip();
    qry.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
    qry.setFilter(FilterQueryProdCostant.FTDATARIF, dataCorrN);

    Object []obj = ResultSetHelper.SingleRowSelect(con, qry.toSQLString());
    if(obj!=null && obj.length>0)
      comm=ClassMapper.classToString(obj[0]);
    
    } catch(QueryException q){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+q.getMessage());
      
      
    } catch(SQLException s){
      _logger.error("Errore in fase di interrogazione per reperire il numero di commessa -->"+s.getMessage());
      
      
    }
    
    return comm;
  }
  
  
  private static void elabColliCommessa(Connection con,String commessa,Long dataN){
    List<List> l=new ArrayList();
    try{
      if(!existsInfoColliComm(con, commessa, dataN)){
        QueryConteggioColliBu query=new QueryConteggioColliBu();
        query.setFilter(FilterQueryProdCostant.FTAZIENDA, CostantsColomb.AZCOLOM);
        query.setFilter(FilterQueryProdCostant.FTDATACOMMN, dataN);
        query.setFilter(QueryConteggioColliBu.FILTERCOLLICOMM, "YES");
        ResultSetHelper.fillListList(con, query.toSQLString(), l);
        for(List e:l){
          e.add(0, CostantsColomb.AZCOLOM);
          e.add(1, commessa);
          e.add(2, dataN);
          e.add(new Date());
        }
        ColliVolumiCommessaTbl tab=new ColliVolumiCommessaTbl();
        PersistenceManager man=new PersistenceManager(con);
        man.saveListDt(tab, l);
      }
      
    } catch(SQLException s){
      
    } catch (QueryException q){
      
    }
  }
  
  private static Boolean existsInfoColliComm(Connection con,String commessa,Long dataN) throws SQLException{
    String s="select distinct ZCNCOM from " 
             +ColombiniConnections.getAs400LibPersColom()+"."+ColliVolumiCommessaTbl.TABLENAME+
            "\n WHERE ZCCONO="+JDBCDataMapper.objectToSQL(CostantsColomb.AZCOLOM)+   
             " and ZCNCOM ="+JDBCDataMapper.objectToSQL(commessa)+
             " and ZCDTCO = "+JDBCDataMapper.objectToSQL(dataN);
    
    Object [] obj=ResultSetHelper.SingleRowSelect(con, s);
    if(obj!=null && obj.length>0)
      return Boolean.TRUE;
    
    
    return Boolean.FALSE;
  }
  
  
  private static void testXML(){
    try {
       XmlFileSezR1P4 xml=new XmlFileSezR1P4("D:/@@Lavoro@@/0__PRJ Colombini/2018/08_P4Sfrido_ScaricoB/143BSP4d.xml");
       xml.loadPropertiesMap();
       List solution =xml.getListSolution();
       List pattern =xml.getListPattern();
       System.out.println(" Mappa >>"+solution.toString());
       System.out.println(" Mappa >>"+pattern.toString());
         
    } catch(ParserConfigurationException p){
      _logger.error("Errore in fase di decodifica del file xml -->"+p.getMessage());
    } catch(SAXException s){
      _logger.error("Errore in fase di decodifica del file xml -->"+s.getMessage());
    } catch(IOException i){
      _logger.error("Errore in fase di decodifica del file xml -->"+i.getMessage());
    }
}

private static void printNote(NodeList nodeList) {
     
    
    for (int count = 0; count < nodeList.getLength(); count++) {

	Node tempNode = nodeList.item(count);

	// make sure it's element node.
	if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

		// get node name and value
		System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
		System.out.println("Node Value =" + tempNode.getTextContent());

		if (tempNode.hasAttributes()) {

			// get attributes names and values
			NamedNodeMap nodeMap = tempNode.getAttributes();

			for (int i = 0; i < nodeMap.getLength(); i++) {

				Node node = nodeMap.item(i);
				System.out.println("attr name : " + node.getNodeName());
				System.out.println("attr value : " + node.getNodeValue());

			}

		}

		if (tempNode.hasChildNodes()) {

			// loop again if has child nodes
			printNote(tempNode.getChildNodes());

		}

		System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

	}

    }

  }




}