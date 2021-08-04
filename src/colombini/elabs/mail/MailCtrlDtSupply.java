/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.mail;

import colombini.conn.ColombiniConnections;
import colombini.query.bi.QryCheckAcqAZero;
import colombini.query.bi.QryCtrlArticoliSupply;
import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.ResultSetHelper;
import exception.QueryException;
import fileXLS.XlsXCsvFileGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import mail.MailMessageInfoBean;
import org.apache.log4j.Logger;
import utils.ClassMapper;
import utils.DateUtils;

/**
 * Classe che esegue la verifica per il Supply :
 * Come primo controllo verifichiamo se ci sono movimenti con articoli senza anagrafica.
 * Il controllo deve essere fatto sia su as400Colombini che as400Febal
 * @author lvita
 */
public class MailCtrlDtSupply extends ElabInvioMail{

  public static final String MESSAGE_CHECKBIACQ="CHECKBIACQ";
  public final static String ELBCTRLSUPPLY="ELBCTRLSUPPLY";
  
  public final static String PATHFILESCSV="PATHFILESCSV";
  
 

   
    


  @Override
  public void addInfoToMailMessage(Connection con, MailMessageInfoBean messageBase) {
    _logger.info("Controllo coerenza dati Supply ....");
    _logger.info("Azienda Colombini....");
    List errs=new ArrayList();
    List errs2=new ArrayList();
    Connection conF=null;
    File attach1=null;
    File attach2=null;
    try {
      conF=ColombiniConnections.getAs400FebalConnection();
      //Map prop=ElabsProps.getInstance().getProperties(ELBCTRLSUPPLY);
      String pathFiles=ClassMapper.classToString(getElabProperties().get(PATHFILESCSV));
      //ctrl 1
      QryCtrlArticoliSupply qry=new QryCtrlArticoliSupply();
      
      qry.setFilter(QryCtrlArticoliSupply.FLIBAS400, ColombiniConnections.getAs400LibPersColom());
      ResultSetHelper.fillListList(con, qry.toSQLString(), errs);
      qry.setFilter(QryCtrlArticoliSupply.FLIBAS400, ColombiniConnections.getAs400LibPersFebal());
      ResultSetHelper.fillListList(conF, qry.toSQLString(), errs2);
      errs.addAll(errs2);
      
      if(errs.size()>0){
        errs.add(0, new ArrayList(Arrays.asList("Azienda","CodiceArticolo")));
        XlsXCsvFileGenerator csvPPS=new XlsXCsvFileGenerator(pathFiles+"ArticoliSenzaPPS040.csv",XlsXCsvFileGenerator.FILE_CSV);
        try{
          attach1=csvPPS.generateFileCsv(errs);
        } catch(FileNotFoundException s){
            addError("Errore in fase di generazione del file "+csvPPS.getFileName() +" --> "+s.toString());
            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
        }
      }
     //-- fine 1
     
     //ctrl2 
     QryCheckAcqAZero qry2=new QryCheckAcqAZero();
     Long dataN=DateUtils.getNumericData(DateUtils.getYear(new Date()), Integer.valueOf(1), Integer.valueOf(1));
     qry2.setFilter(FilterFieldCostantXDtProd.FT_DATA,dataN);
     
     errs=new ArrayList();
     ResultSetHelper.fillListList(con, qry2.toSQLString(), errs);
     errs2=new ArrayList();
     ResultSetHelper.fillListList(conF, qry2.toSQLString(), errs2);
     errs.addAll(errs2);
     
     if(errs.size()>0){
       errs.add(0, new ArrayList(Arrays.asList("azienda","facility","magazzino","codFornitore","numOrdine","rigaOrdine","sottoriga","codArticolo","descArticolo","buyer","qta")));
       XlsXCsvFileGenerator csvPPS=new XlsXCsvFileGenerator(pathFiles+"AcquistiImportoZero.csv",XlsXCsvFileGenerator.FILE_CSV);
       try{
        attach2=csvPPS.generateFileCsv(errs);
       } catch(FileNotFoundException s){
            addError("Errore in fase di generazione del file "+csvPPS.getFileName() +" --> "+s.toString());
            _logger.error("Errore in fase di generazione del file -->"+s.getMessage());
       }
      }
     
     
     if(attach1!=null || attach2!=null){
       if(attach1!=null)
        messageBase.addFileAttach(attach1);
       if(attach2!=null)
        messageBase.addFileAttach(attach2);
     }else {
       //nel caso entrambi i file sono vuoti non mando la mail
       messageBase.setAddressesTo(new ArrayList());
     }
     
     
    } catch (QueryException ex) {
      _logger.error("Errore in fase di interrorazione del database -->"+ex.getMessage());
      addError("Errore in fase di interrorazione del database -->"+ex.toString());
    } catch (SQLException ex) {
      _logger.error("Errore in fase di interrorazione del database -->"+ex.getMessage());
      addError("Errore in fase di interrorazione del database -->"+ex.toString());
    } finally{
      try{
        if(conF!=null)
          conF.close();
      }catch(SQLException s){
        _logger.error("Errore in fase di chiusura della connessione --> "+s.getMessage());
        
      }
    }
  }

  @Override
  public String getIdMessage() {
    return MESSAGE_CHECKBIACQ;
  }
  
  private static final Logger _logger = Logger.getLogger(MailCtrlDtSupply.class);
}
