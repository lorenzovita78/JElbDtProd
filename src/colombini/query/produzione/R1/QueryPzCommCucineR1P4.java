/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.produzione.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;

/**
 *
 * @author GGraziani
 */
public class QueryPzCommCucineR1P4 extends CustomQuery {
  
  public final static String TAB_PRODP4="[DesmosColombini].[dbo].[DatiProduzione]";
  public final static String TAB_DESMOS_DESTINAZIONI="[DesmosColombini].[dbo].[DatiDestinazioni]";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
    String sqlF="";
    String Condition =" ((a.destinazionedettaglio like 'P4_P3%') or a.destinazionedettaglio in ('P3_REM','P3_BS')) ";
    
    
    sql.append(" select Collo , ncollo , LineaDestAbbreviata , Box, Pedana , odv , rigaOrdine ,  codSemilavorato , descAbb ,  Descrizione  , barcode ").append(
               "\n FROM \n");
              
              
      StringBuffer sqlSub1=new StringBuffer("SELECT a.[Collo]\n" ).append(
               " , ROW_NUMBER() OVER(partition by a.Collo  order by a.PartNumber)  as ncollo   \n" ).append(
               " , a.lineadestinazione  ").append(    
               " , a.Box , a.Pedana ").append(
               " ,a.odv  \n").append(
               " ,coalesce(b.clnart,0) rigaOrdine \n").append(
               " ,a.[CodSemilavorato] \n" +
               " ,substring(a.[Descrizione],1,30)  as descAbb \n" +
               " ,a.[Descrizione]\n" +
               " ,a.[PartNumber] as barcode "+
              "\n FROM ").append(TAB_PRODP4).append(
               " as a \n inner join LDL05_BASE_SIRIO_IMA b on a.partnumber=b.partnumber").append(
               "\n WHERE 1=1 ").append(
                addAND((Condition)));
      
       String sqlSub2 ="\n select distinct[LineaDestinazione] ,[LineaDestAbbreviata] from "+TAB_DESMOS_DESTINAZIONI+"  WHERE 1=1 ";
      
              
      StringBuilder commCondition=new StringBuilder(
                     "\n and a.Commessa =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                     " and a.DataCommessa=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
      
      sqlSub1.append(commCondition);
      
     
      
      sql.append(" ( ").append(sqlSub1).append( " )as a inner join ( ").append(
                               sqlSub2).append("  ) c  on  a.lineadestinazione=c.lineadestinazione ");//collate database_default ");
      
      
     
      sqlF=sql.toString();
      
    return sqlF;
  }
  
}
