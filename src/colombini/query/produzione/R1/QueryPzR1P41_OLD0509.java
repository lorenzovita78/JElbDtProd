/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import static colombini.query.produzione.R1.QueryPzR1P41_OLD.TAB_PRODP4;
import db.CustomQuery;
import exception.QueryException;
import utils.ClassMapper;
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class QueryPzR1P41_OLD0509 extends CustomQuery {

  public final static String FT_ULTIMAFASEP4="FT_ULTIMAFASEP4";
  public final static String FT_FASE30="FT_FASE30";

  
  
  //public final static String TAB_PRODP4="[AvanzamentoProd].[dbo].[TBL_DatiProduzione]";
  //public final static String TAB_DESMOS_DESTINAZIONI="[AvanzamentoProd].[dbo].[VW_DatiDestinazioniP4_PROD]";
 
  public final static String TAB_PRODP4="[DesmosColombini].[dbo].[DatiProduzione]";
  public final static String TAB_DESMOS_DESTINAZIONI="[DesmosColombini].[dbo].[DatiDestinazioni]";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
    StringBuilder sql2=new StringBuilder("");
    String sqlF="";
    String ultimaFaseCondition ="";
    String joinLinea=" ) b  on  a.lineadestinazione=b.lineadestinazione";
    
    if(isFilterPresent(FT_ULTIMAFASEP4))
       ultimaFaseCondition =ClassMapper.classToString(getFilterValue(FT_ULTIMAFASEP4));
    
    sql.append(" select Collo , ncollo , b.LineaDestAbbreviata , Box, Pedana , odv , rigaOrdine ,  codSemilavorato , descAbb ,  Descrizione  , barcode ").append(
               "\n FROM \n");
              
              
    StringBuffer sqlSub1=new StringBuffer("SELECT [Collo]\n" ).append(
               " , ROW_NUMBER() OVER(partition by Collo  order by PartNumber)  as ncollo   \n" ).append(
               " , lineadestinazione  ").append(    
               " , Box , Pedana ").append(
               " ,odv  \n").append(
               " ,0 as  rigaOrdine \n").append(
               " ,[CodSemilavorato] \n" +
               " ,substring([Descrizione],1,30)  as descAbb \n" +
               " ,[Descrizione]\n" +
               " ,[PartNumber] as barcode "+
                " , case\n" +
               " when [descfase30] is not null and substring(ltrim([descfase30]),1,2) not in ('**') then [descfase30]\n" +
                " else lineadestinazione\n" +
                " end lineadest  "+                       
               "\n FROM ").append(TAB_PRODP4).append(
               "\n WHERE 1=1 ").append(
                addAND((ultimaFaseCondition)));
      
       String sqlSub2 ="\n select distinct[LineaDestinazione] ,[LineaDestAbbreviata] from "+TAB_DESMOS_DESTINAZIONI+"  WHERE 1=1 ";
      
      
      StringBuilder commCondition=new StringBuilder(
                     "\n and Commessa =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                     " and DataCommessa=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
     
      if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEE)){
        commCondition.append(addAND(inStatement(" linealogica ", FilterFieldCostantXDtProd.FT_LINEE))); 
      }
      
      sqlSub1.append(commCondition);
      
     if(isFilterPresent(FT_FASE30))
        joinLinea=" ) b  on  a.lineadest=b.lineadestinazione";
      
      sql.append(" ( ").append(sqlSub1).append( " ) a  inner join ( ").append(
                      sqlSub2).append(joinLinea);
              //sqlSub2).append("  ) b  on  a.lineadestinazione=b.lineadestinazione ");//collate database_default ");
      
     
      sqlF=sql.toString();
      
    return sqlF;
  }
  
}



//      sql2.append(" SELECT numCollo,  ROW_NUMBER() OVER(partition by numCollo  order by CodPan ) \n  ").append(
//                       " , linea,Box,Pedana,'0000000' as numOrdine ,0 as rigaOrdine ").append(
//                       " , SUBSTRING(desPan,1,CHARINDEX (' ' ,DesPan)-1) as codArticolo ").append(
//                       " , SUBSTRING(despan,CHARINDEX (' ' ,DesPan),30) as descArticolo ,DesPan ").append(
//                       ", CodPan" ).append(
//                     " FROM [DesmosColombini].[dbo].[LisPan] " );      
