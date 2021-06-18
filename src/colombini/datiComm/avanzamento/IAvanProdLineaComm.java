/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.avanzamento;

import colombini.model.LineaLavBean;
import colombini.model.persistence.AvProdCommLineaBean;
import java.sql.Connection;


/**
 * interfaccia per gestire i dati relativi all'avanzamento di produzione di una linea
 
 * @author lvita
 */
public interface IAvanProdLineaComm {
 
 AvProdCommLineaBean getAvProdCommessa(Connection con ,LineaLavBean ll,Integer comm,Long dataComN); 
  
}
