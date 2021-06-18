/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import colombini.conn.ColombiniConnections;
import colombini.model.persistence.ColliVolumiCommessaTbl;
import db.CustomQuery;
import exception.QueryException;

/**
 * Query per l'estrazione dei volumi e dei colli delle bu per numero di commessa
 * E' possibile filtrare o per numero di commessa e data relativa
 * oppure è possibile indicare un periodo
 * @author lvita
 */
public class QryVolumeColliBuComm extends CustomQuery {

  @Override
  public String toSQLString() throws QueryException {
     
    
     String select=" select dcdtco, dccomm, dcdivi, dcncol, dcvol3, dcflcv "+
                   " from "+ ColombiniConnections.getAs400LibPersColom()+ "."+ColliVolumiCommessaTbl.TABLENAME+
                   " where 1=1"+
                   " and DCCONO ="+getFilterSQLValue(FilterQueryProdCostant.FTAZIENDA);
     
     if(isFilterPresent(FilterQueryProdCostant.FTDATARIF))
        select+=" and DCDTCO ="+getFilterSQLValue(FilterQueryProdCostant.FTDATARIF);           
     
     if(isFilterPresent(FilterQueryProdCostant.FTNUMCOMM))
        select+=" and DCCOMM ="+getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM);     
     
     
     if(isFilterPresent(FilterQueryProdCostant.FTDATAINI))
        select+=" and DCDTCO >="+getFilterSQLValue(FilterQueryProdCostant.FTDATAINI);
    
     if(isFilterPresent(FilterQueryProdCostant.FTDATAFIN))
        select+=" and DCDTCO <="+getFilterSQLValue(FilterQueryProdCostant.FTDATAFIN);     
     
     if(isFilterPresent(FilterQueryProdCostant.FTONLYCOMM))
       select+=" and DCCOMM <360";
     
     
     return select;
  }
  
  
  
}
