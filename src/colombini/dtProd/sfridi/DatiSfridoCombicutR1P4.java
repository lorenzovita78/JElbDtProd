/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.dtProd.sfridi;

import colombini.conn.ColombiniConnections;
import colombini.costant.ColomConnectionsCostant;
import db.JDBCDataMapper;
import db.ResultSetHelper;
import exception.ElabException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import utils.DateUtils;

/**
 *
 * @author lvita
 */
public class DatiSfridoCombicutR1P4 extends InfoSfridoCdL {
    
    public DatiSfridoCombicutR1P4(String cdL) {
        super(cdL);
    }

    @Override
    public List getInfoSfrido(Date dataInizio, Date dataFine) throws ElabException {
      if(dataInizio==null || dataFine==null){
      _logger.warn("Periodo di ricerca non valorizzato correttamente. Impossibile estrapolare i dati di sfrido!!");
      return null;
    }
    
    return loadDatiFromDb(dataInizio,dataFine);
    }
    
    private List loadDatiFromDb(Date dataInizio,Date dataFine) throws ElabException{
    List list=new ArrayList();
    Connection con=null;
    try {
      String dtIS = DateUtils.DateToStr(dataInizio, "yyMMdd");
      String dtFS=DateUtils.DateToStr(dataFine, "yyMMdd");
      con=ColombiniConnections.getDbIPCNetConnection();
      
      StringBuilder qry=new StringBuilder();
      
      qry.append("SELECT 30 az ,'01088' linea ,'20'+ substring(optinr, 3 , 6) data ").append(
                 " ,substring(optinr, 10,len(optinr)) nott  ").append(
                 " ,[Material] ,coalesce(substring([MaterialBarcode],1,15),'') ,0,0,0 \n").append(
                 " ,sum([VerbrauchtePlatten]-[VerbrauchteReste]) nr_pann_usati ").append(
                 " , 0 nr_pan_prod\n" ).append(
                 //" ,sum([VerbrauchteFlächeQM]-[VerbrauchteRestQM]) sup_pann_usati ").append(
                 ", sum([VerbrauchteFlächeQM]) sup_usataTot \n").append(
                 " ,sum([TeileProduziertQM])  + sum([ErzeugteGroßResteQM]) + sum([ErzeugteHandResteQM]) as sup_utillizata \n").append(
                 " ,0 sup_dif ,sum([VerbrauchteRestQM]) sup_resti_us,0 as sup_rif ,sum([AbfallBesäumQM]) sup_sfrido_fisio ").append(
                 " ,sum([ErzeugteGroßReste]) nr_resti_grandi_prod,sum([ErzeugteGroßResteQM]) sup_resti_grandi_prod \n ").append(
                 " ,sum([ErzeugteHandReste]) nr_resti_piccoli_prod,sum([ErzeugteHandResteQM]) sup_resti_piccoli_prod ").append(
                 " FROM [dbo].[VIEW_CUSTOMER_STATISTIC] a " ).append(
                 "  left outer join \n ( SELECT MaterialCode,min(materialBarcode) as materialBarcode \n" ).append(
                                       "  FROM [IMAIPCNET_2210000136_Transfer].[dbo].[VIEW_STORAGE_INVENTORY] \n" ).append(
                                       "  where MaterialBarcode not like 'R%' \n " ).append(
                                       "  group by MaterialCode ) b   \n on  a.material=b.materialcode").append( 
                 "\n WHERE  substring(optinr, 3 , 6) >= ").append(JDBCDataMapper.objectToSQL(dtIS)).append(
                 "\n AND  substring(optinr, 3 , 6) <= ").append(JDBCDataMapper.objectToSQL(dtFS)).append( 
                 "\n AND optinr in (\n").append(//consideriamo solo le ottimizzazioni presenti sulla lispre, ciè inviate a Sirio
                 "  select distinct a.lotname from [SirioLotto1].[dbo].[ImpLisPre] b inner join (\n").append(
                 "  select distinct lotname, info from [IMAIPCNET_2210000136_Transfer].[dbo].[TBL_TRANSFER_STORAGE_JOBS]) a\n").append(
                 "  on a.info=b.lotname)").append(        
                 "\n group by substring(optinr, 3 , 6) ,optinr,Material,MaterialBarcode \n").append(
                 "\n having sum([VerbrauchteFlächeQM])>0 \n").append(
                 "  order by optinr");        
                     
      ResultSetHelper.fillListList(con, qry.toString(), list);
      
    } catch (SQLException ex) {
      _logger.error(" Problemi di connessione con db :"+ColomConnectionsCostant.DBOTMNESTING+" --> "+ex.getMessage());
      throw new ElabException(ex);
    } catch (ParseException ex) {
      _logger.error(" Problemi di conversione di date --> "+ex.getMessage());
      throw new ElabException(ex);
    } finally{
      if(con!=null)
        try {
        con.close();
      } catch (SQLException ex) {
        _logger.error(" Problemi con la chiusura della connessione con db :"+ColomConnectionsCostant.DBOTMNESTING+" --> "+ex.getMessage());
      }
    }
       
    return list;
  }  
    
    
    
    
    
    private static final Logger _logger = Logger.getLogger(DatiSfridoCombicutR1P4.class); 
}
