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
import utils.StringUtils;

/**
 *
 * @author lvita
 */
public class QueryPzCommLotto1 extends CustomQuery {

  public final static String FT_FORLINEA_LOTTO1="FT_FORLINEA_LOTTO1";
  public final static String FT_DESCFS30_LIKE="FT_DESCFS30_LIKE";
  
  public final static String TAB_DESMOS_LOTTO1="[DesmosColombini].[dbo].[LDL09_CODICI_BS_SEZ_PIANO4]";
  //public final static String TAB_DESMOS_LOTTO1="[DesmosColombini].[dbo].[LDL09_FILE_TXT_BS_SEZ_PIANO4]";
  public final static String TAB_DESMOS_LOTTO1_LDL05="[DesmosColombini].[dbo].[LDL05_BASE_SIRIO_IMA]";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
    StringBuilder sql2=new StringBuilder("");
    String sqlF="";
    String stringDestToRepl="$$$_DEST_$$$";
    
    sql.append(" select Collo , ncollo , b.LineaDestAbbreviata , Box, Pedana , numOrd , rigaOrdine ,  codSemilavorato , descAbb ,  Descrizione  , barcode ").append(
               "\n FROM \n");
              
              
      StringBuffer sqlSub0=new StringBuffer("SELECT [Collo]\n" ).append(
               " , ROW_NUMBER() OVER(partition by Collo  order by PartNumber)  as ncollo   \n" ).append(
               " , ").append( stringDestToRepl ).append(" as dest ").append(    
               " , Box , Pedana ").append(
               " ,NumeroOrdineCliente numOrdine \n").append(
               " ,coalesce([NumeroRigaOrdineCliente],0) rigaOrdine \n").append(
               " ,[CodSemilavorato] \n" +
               " ,substring([Descrizione],1,30)  as descAbb \n" +
               " ,[Descrizione]\n" +
               " ,[PartNumber] as barcode ,  Commessa , DataCommessa ,DescFase30,Linea  ").append(  
              "\n FROM ").append(TAB_DESMOS_LOTTO1).append(
               "\n WHERE 1=1 and spec4='LOT1' ");
      
      StringBuffer sqlSub1 =new StringBuffer("\n select PartNumber,Odv as numOrd from ").append(TAB_DESMOS_LOTTO1_LDL05).append("  WHERE 1=1 ");
      
      String sqlSub2 ="\n select distinct[LineaDestinazione] ,[LineaDestAbbreviata] from [DesmosColombini].[dbo].[DatiDestinazioni]  WHERE 1=1 ";
      
      
      StringBuilder commCondition=new StringBuilder(
                     "\n and Commessa =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
          //           " and DataCommessa=CONVERT(date,").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(" )");
                                        " and DataCommessa=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
     
      
      sqlSub0.append(commCondition);
      
      sqlSub1.append(commCondition);
      
      
      
      sql.append(" ( ").append(sqlSub0).append( " ) a  left outer join ( ").append(
                               sqlSub1).append("  ) c  on  a.barcode=c.PartNumber ");
      //                   " inner join (").append(sqlSub2).append(" ) b  on  a.dest=b.LineaDestinazione").append(
     
      
      if(isFilterPresent(FT_FORLINEA_LOTTO1) || isFilterPresent(FT_DESCFS30_LIKE)){
        sql.append(" inner join (").append(sqlSub2).append(" ) b  on  a.dest=b.LineaDestinazione");
        
        sqlF=sql.toString();
        if(isFilterPresent(FT_DESCFS30_LIKE)){
          sqlF=sqlF.replace(stringDestToRepl, "LineaDestinazione");
        }else{
          sqlF=sqlF.replace(stringDestToRepl, " case when ([DescFase30] is null or [DescFase30]='') then LineaDestinazione else DescFase30 end  ");
        }
      }else{
        sqlF=sql.toString();
        sqlF=sqlF.replace("b.LineaDestAbbreviata", "Linea");
        sqlF=sqlF.replace(stringDestToRepl, "Linea");
        //sqlF=sql2.toString();
      }
      
       
      StringBuilder filterCondition=new StringBuilder(commCondition);
      
      if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEE)){
        filterCondition.append(addAND(inStatement(" Linea ", FilterFieldCostantXDtProd.FT_LINEE))); 
      }
 
      if(isFilterPresent(FT_DESCFS30_LIKE)){
        String descF=ClassMapper.classToString(getFilterValue(FT_DESCFS30_LIKE));
        filterCondition.append(" and descfase30  like '%").append(descF).append("%'"); 
      }
      
      //aggiunta pezzi per linea 6023 presi direttamente dalla LisPan
//      if(isFilterPresent(FT_FORLINEA_LOTTO1)){
//                sql2.append(" SELECT numCollo,  ROW_NUMBER() OVER(partition by numCollo  order by CodPan ) \n  ").append(
//                    " , 'P1FIMAW2',Box,Pedana,'0000000' as numOrdine ,0 as rigaOrdine ").append(
//                       " , SUBSTRING(desPan,1,CHARINDEX (' ' ,DesPan)-1) as codArticolo ").append(
//                       " , SUBSTRING(despan,CHARINDEX (' ' ,DesPan),30) as descArticolo ,DesPan ").append(
//                       ", CodPan" ).append(
//                     " FROM [DesmosColombini].[dbo].[LisPan] ").append(
//                      "\n WHERE 1=1 ").append(
//                     " and Commessa =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
//                     " and DataCommessa=CONVERT(date,").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA)).append(" )").append(
//                     " and Linea='6023' ");
//      }
      
      if(isFilterPresent(FT_FORLINEA_LOTTO1)){
                sql2.append(" select Collo , ncollo , LineaDestAbbreviata , Box, Pedana , numOrdine , rigaOrdine ,  codSemilavorato , descAbb ,  Descrizione  , barcode ").append(
                       "\n FROM (\n SELECT [Collo]\n" ).append(
               " , ROW_NUMBER() OVER(partition by Collo  order by PartNumber)  as ncollo   \n" ).append(
               " , linea as dest ").append(    
               " , Box , Pedana ").append(
               " ,NumeroOrdineCliente numOrdine \n").append(
               " ,coalesce([NumeroRigaOrdineCliente],0) rigaOrdine \n").append(
               " ,[CodSemilavorato] \n" +
               " ,substring([Descrizione],1,30)  as descAbb \n" +
               " ,[Descrizione]\n" +
               " ,[PartNumber] as barcode ,  Commessa , DataCommessa , Linea , LineaDestAbbreviata  ").append( 
               " FROM [DesmosColombini].[dbo].LDL05_BASE_SIRIO_IMA ").append(
                       "inner join  [DesmosColombini].[dbo].[DatiDestinazioni]      on  linea=Linealogica").append(          
               "\n WHERE 1=1 ").append(
               "  and spec4 is null ").append(
               " and linea not in ('6089','6028') ").append(commCondition).append(" )  BIS");
                       
      }
      
      
      sqlF+=" WHERE 1=1 "+filterCondition.toString();
      if(!StringUtils.IsEmpty(sql2.toString()))
          sqlF+="\n UNION \n"+sql2.toString();
      
      
    return sqlF;
  }
  
}



//      sql2.append(" SELECT numCollo,  ROW_NUMBER() OVER(partition by numCollo  order by CodPan ) \n  ").append(
//                       " , linea,Box,Pedana,'0000000' as numOrdine ,0 as rigaOrdine ").append(
//                       " , SUBSTRING(desPan,1,CHARINDEX (' ' ,DesPan)-1) as codArticolo ").append(
//                       " , SUBSTRING(despan,CHARINDEX (' ' ,DesPan),30) as descArticolo ,DesPan ").append(
//                       ", CodPan" ).append(
//                     " FROM [DesmosColombini].[dbo].[LisPan] " );      
