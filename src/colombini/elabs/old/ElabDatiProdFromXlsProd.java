/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.elabs.old;


import db.persistence.PersistenceManager;
import colombini.costant.CostantsColomb;
import colombini.costant.NomiLineeColomb;
import colombini.elabs.NameElabs;
import colombini.model.CausaliLineeBean;
import colombini.model.GestoreFermiGg;
import colombini.model.persistence.DtProdLineaBeanP;
import colombini.xls.DynamicXlsProd;
import colombini.xls.StrutturaFileXlsDatiProd;
import elabObj.ElabClass;
import elabObj.ALuncherElabs;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ArrayUtils;
import utils.ClassMapper;
import utils.DateUtils;
import utils.MapUtils;
import utils.StringUtils;

/**
 * Classe che verifica tutti i file xls di produzione e calcola i minuti di campionatura
 * @author lvita
 */
public class ElabDatiProdFromXlsProd extends ElabClass{
  
  private Date dataInizio;
  private Date dataFine;
  private List lineeToElab=null;
  private Boolean storeFermi=Boolean.TRUE;
  private Boolean storeAllData=Boolean.TRUE;
  
  
  @Override
  public Boolean configParams() {
    Map parameter= this.getInfoElab().getParameter();
    if(parameter==null || parameter.isEmpty()){
      _logger.error(" Lista parametri vuota. Impossibile lanciare l'elaborazione");
      return Boolean.FALSE;
    }
    
    if(parameter.get(ALuncherElabs.DATAINIELB)!=null){
      this.dataInizio=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAINIELB),Date.class);
    }  
    
    if(parameter.get(ALuncherElabs.DATAFINELB)!=null){
      this.dataFine=ClassMapper.classToClass(parameter.get(ALuncherElabs.DATAFINELB),Date.class);
    }  
    
    if(dataInizio==null || dataFine==null)
      return Boolean.FALSE;
    
    String linee="";
    if(parameter.get(ALuncherElabs.LINEELAB)!=null){
      linee=ClassMapper.classToString(parameter.get(ALuncherElabs.LINEELAB));   
    }  
    
    if(!StringUtils.isEmpty(linee)){
      lineeToElab=ArrayUtils.getListFromArray(linee.split(","));
    }
    
    return Boolean.TRUE;
  }

  @Override
  public void exec(Connection con) {
    _logger.info("Inizio elaborazione dati di produzione da file Xls ");
    //Map prop=ElabsProps.getInstance().getProperties(NameElabs.ELBDATIPRODFROMXLS);
    String fileStructXls=ClassMapper.classToString(getElabProperties().get(NameElabs.FILESTRUCTXLS));
    
    List<StrutturaFileXlsDatiProd> files=getListXls(fileStructXls);
    elabFilesXls(con, files);
  }
  
 
  
  
  
  private List<StrutturaFileXlsDatiProd> getListXls(String fileStructXls){
    List objFiles=new ArrayList();
    FileReader fR = null;
    BufferedReader bfR = null;
    String riga;
    Long count=Long.valueOf(0);
    //Boolean filtroLinee=Boolean.FALSE;
    
    Boolean filtroLinee=(lineeToElab!=null && lineeToElab.size()>0);
    
    try{
      fR = new FileReader(fileStructXls);
      bfR=new BufferedReader(fR);
      
      riga = bfR.readLine();
      
      while(riga!=null){
        count++;
        //prima riga con intestazione
        if( count==1 || riga.startsWith("#") ){
          riga = bfR.readLine();
          continue;
        }
          
        
        StrutturaFileXlsDatiProd obj=new StrutturaFileXlsDatiProd();
        List record=ArrayUtils.getListFromArray(riga.split(";"));
        if( record!=null && record.size()>0 && !StringUtils.IsEmpty((String)record.get(0))  ){
          
          obj.setStabilimento(ClassMapper.classToString(record.get(0)));
          obj.setCodLinea(ClassMapper.classToString(record.get(1)));
          obj.setPathFileC(ClassMapper.classToString(record.get(2)));
          obj.setRigheGg(ClassMapper.classToClass(record.get(3),Integer.class));
          obj.setColOraIniTurnoDip(ClassMapper.classToClass(record.get(4), Integer.class));
          obj.setColOraFinTurnoDip(ClassMapper.classToClass(record.get(5), Integer.class));
          obj.setColOreStraordinario(ClassMapper.classToClass(record.get(6), Integer.class));

          obj.setColOraAperturaImp(ClassMapper.classToClass(record.get(7), Integer.class));
          obj.setColOraChiusuraImp(ClassMapper.classToClass(record.get(8), Integer.class));
//          obj.setTipoOreProdImp(ClassMapper.classToString(record.get(9)));

          obj.setColOreImprodIni(ClassMapper.classToClass(record.get(9), Integer.class));
          obj.setColOreImprodFn(ClassMapper.classToClass(record.get(10), Integer.class));
          obj.setTipoOreImprodImp(ClassMapper.classToString(record.get(11)));

          obj.setColPzProd(ClassMapper.classToClass(record.get(12), Integer.class));
          obj.setColPzProd2(ClassMapper.classToClass(record.get(13), Integer.class));

          obj.setColIniFermi(ClassMapper.classToClass(record.get(14), Integer.class));
          obj.setColFinFermi(ClassMapper.classToClass(record.get(15), Integer.class));

          obj.setColIniSetup(ClassMapper.classToClass(record.get(16), Integer.class));
          obj.setColFinSetup(ClassMapper.classToClass(record.get(17), Integer.class));

          obj.setColIniMicroF(ClassMapper.classToClass(record.get(18), Integer.class));
          obj.setColFinMicroF(ClassMapper.classToClass(record.get(19), Integer.class));
          
          obj.setFattMoltMicroF(ClassMapper.classToClass(record.get(20), Double.class));
          
          //if(!filtroLinee || (lineeToElab.contains(obj.getCodLinea())))
            objFiles.add(obj);
        }
        riga = bfR.readLine();  
        
      }        
    _logger.info("File "+fileStructXls+" righe processate:"+count);
    
    } catch (IOException ex) {
      _logger.error(" Errore nella lettura del file "+fileStructXls + " -->"+ex.getMessage());
      addError("Errore nella lettura del file "+fileStructXls+" --> "+ex.toString());
    } finally{
      _logger.info("Righe processate"+count);
      try{
        if(bfR!=null)
          bfR.close();
        if(fR!=null)
          fR.close();
      } catch (IOException ex) {
      _logger.error(" Errore nella chiusura del file "+fileStructXls + " -->"+ex.getMessage());
      }  
    }
    
    return objFiles;
  }
  
  
  private void elabFilesXls(Connection conAs400,List <StrutturaFileXlsDatiProd> files){
    DynamicXlsProd dyn=null;
    PersistenceManager pm=null;
    Date dataTmp=null;
//    List listBeans=new ArrayList();
    String tipoCausale=CausaliLineeBean.FLAGDESC;
    try{
      pm=new PersistenceManager(conAs400);  
      for(StrutturaFileXlsDatiProd obj : files){
        dataTmp=dataInizio;
        try {
          if(NomiLineeColomb.SCHELLLONG_GAL.equals(obj.getCodLinea())){
            tipoCausale=CausaliLineeBean.FLAGCODICE;
          }
          Map causaliLinea=CausaliLineeBean.getMapCausaliLinea(conAs400, CostantsColomb.AZCOLOM, obj.getCodLinea(), tipoCausale); 
          dyn=new DynamicXlsProd(obj.getPathFileC(), obj);
          dyn.openFile();
          while(DateUtils.numGiorniDiff(dataTmp, dataFine)>=0){

            GestoreFermiGg fg= new GestoreFermiGg(DateUtils.getDataForMovex(dataTmp),CostantsColomb.AZCOLOM, obj.getCodLinea());
            List fermiGg=dyn.getListaFermiGg(dataTmp);
            fg.elabOccorrrenzeFermi(fermiGg, causaliLinea);

            Map totFermiTipo=fg.getTotFermiTipo();
            
            //salvataggio dei dati dei fermi 
            if(storeFermi)
              fg.storeDatiGgLinea(pm);
            
            //caricamento e salvataggio di tutti i dati di produzione 
            if(storeAllData){
              DtProdLineaBeanP bean=new DtProdLineaBeanP(obj.getCodLinea(),dataTmp);

              bean.setTotMinutiTurniDip(dyn.getTotMinutiDipendenti(dataTmp));
              bean.setTotMinutiStraordinario(dyn.getTotMinutiStraordinario(dataTmp));
              bean.setTotMinutiProduzione(dyn.getTotMinutiProduzione(dataTmp));
              bean.setTotMinutiNnProduzione(dyn.getTotMinutiNnProduzione(dataTmp));
              bean.setTotPzProd(dyn.getTotPzProd(dataTmp));

              bean.setTotMinutiSetup(dyn.getTotMinutiSetup(dataTmp));
              bean.setTotMinutiMicrofermi(dyn.getTotMinutiMicrofermi(dataTmp));

              bean.addMinutiGuasti(MapUtils.getNumberFromMap(totFermiTipo, CausaliLineeBean.TIPO_CAUS_GUASTO, Double.class));
              bean.addMinutiSetup(MapUtils.getNumberFromMap(totFermiTipo, CausaliLineeBean.TIPO_CAUS_SETUP, Double.class));
              bean.addMinutiMicroF(MapUtils.getNumberFromMap(totFermiTipo, CausaliLineeBean.TIPO_CAUS_MICROFRM, Double.class));
              bean.addMinutiPerdGest(MapUtils.getNumberFromMap(totFermiTipo, CausaliLineeBean.TIPO_CAUS_PERDGEST, Double.class));

              //salvataggio dati produzione
              
              if(bean.isDtValid()){
                try{
                  pm.deleteDtFromBean(bean);
                  pm.storeDtFromBean(bean);
                } catch(SQLException s){
                  _logger.error("Errore in fase di gestione di cancellazione/salvataggio del dato "+s.getMessage());
                }
              }else{
                addError("Dati per la linea "+bean.getLinea()+" nel giorno "+bean.getDataRif() +" non validi \n");
              }
            }  
            dataTmp=DateUtils.addDays(dataTmp, 1);
          }          
        } catch (FileNotFoundException ex) {
          String msg=" File "+obj.getPathFileC()+"  non trovato.Impossibile proseguire";
          addError(msg);
          _logger.error(msg+" --> "+ex.getMessage());
        } catch (IOException ex) {
          String msg=" Problemi di accesso al file "+obj.getPathFileC()+" .Impossibile proseguire";
          addError(msg);
          _logger.error(msg+" --> "+ex.getMessage());
        }finally{
          if(dyn!=null)
            try {
            dyn.closeFile();
          } catch (IOException ex) {
            String msg=" Problemi nella chiusura del file "+obj.getPathFileC()+" .";
            addError(msg);
            _logger.error(msg+" --> "+ex.getMessage());
          }
        }
      }
    }finally{
      if(pm!=null)
        pm=null;
    }
    
//    return listBeans;
  } 
  

  
  
  private static final Logger _logger = Logger.getLogger(ElabDatiProdFromXlsProd.class); 

  
  
}

