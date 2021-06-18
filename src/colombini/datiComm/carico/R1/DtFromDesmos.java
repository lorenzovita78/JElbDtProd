/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.datiComm.carico.R1;


import colombini.datiComm.carico.IDatiCaricoLineaComm;
import colombini.exception.DatiCommLineeException;
import colombini.model.LineaLavBean;
import colombini.model.persistence.CaricoCommLineaBean;
import colombini.util.DatiCommUtils;
import exception.QueryException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public abstract class DtFromDesmos  implements IDatiCaricoLineaComm {

  @Override
  public List<CaricoCommLineaBean> getDatiCommessa(Connection con, LineaLavBean ll) throws DatiCommLineeException {
    List beans=new ArrayList();
    try {
      
      Map dittaDiv=DatiCommUtils.getInstance().loadMapDittDIV(con);
      String lancio= ll.getDataCommessa().toString().substring(0,4)+"__"+ll.getCommessaString();
      
      
      List<List> info=loadDatiFromDesmos(ll.getCommessa(),lancio);
      if(info==null || info.isEmpty())
        _logger.warn("Linea "+ll.getCodLineaLav()+ " -  Attenzione dati non presenti su desmos per commessa: "+ll.getCommessa());
      
      for(List record:info){
        CaricoCommLineaBean bean=new CaricoCommLineaBean();
        String ditta=ClassMapper.classToString(record.get(0));
        if(ditta==null || ditta.isEmpty()){
          _logger.error(" LINEA : "+ll.getCodLineaLav()+ " - ATTENZIONE DITTA NON VALORIZZATA PER COMMESSA "+ll.getCommessa());
          continue; 
        }  
        
        Double valore=ClassMapper.classToClass(record.get(1),Double.class);

        DatiCommUtils.getInstance().addInfoCaricoComBu(beans, ll.getCodLineaLav(), ll.getDataCommessa(),ll.getCommessa() , ll.getUnitaMisura(),
                                                       ClassMapper.classToString(dittaDiv.get(ditta)), valore);
        
//        bean.setCommessa(ll.getCommessa());
//        bean.setLineaLav(ll.getCodLineaLav());
//        bean.setDataRifN(ll.getDataCommessa());
//        bean.setUnitaMisura(ll.getUnitaMisura());
//        bean.setDivisione(ClassMapper.classToString(dittaDiv.get(ditta)));
//        bean.setValore(valore);
//        beans.add(bean);
      }
      
      
    } catch (SQLException ex) {
      _logger.error("Impossibile stabilire la connessione con il Database : "+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (QueryException ex) {
      _logger.error("Impossibile eseguire la query :"+ex.getMessage());
      throw new DatiCommLineeException(ex);
    } catch (Exception e){
      _logger.error("Problema generico :"+e.getMessage());
      throw new DatiCommLineeException(e);
    }
    
    
    return beans;
  }
  
  
  
  public abstract List loadDatiFromDesmos(Integer comm,String lancio) throws SQLException;
    
  
  private static final Logger _logger = Logger.getLogger(DtFromDesmos.class);   
}
