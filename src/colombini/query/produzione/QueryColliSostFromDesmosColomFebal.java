/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione;

import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryColliSostFromDesmosColomFebal extends CustomQuery {

  public final static String LINEETOEXCLUDEFEBAL="LINEETOEXCLUDEFEBAL";
  public final static String LINEETOEXCLUDECOLOM="LINEETOEXCLUDECOLOM";
  
  @Override
  public String toSQLString() throws QueryException {
//    StringBuilder qry=new StringBuilder(
//              " SELECT CLNCOL,CLNART,cast(CLLINP as varchar(8)),CLBOXN,CLPEDA,CLNROR,CLRIGA,CLARTI,CLDESC,CLSTRD  ").append(
//               "\n from [MVX2DESMOS].[dbo].SCxxxCOL inner join [MVX2DESMOS].[dbo].[ZZHEAD] on CLNROR=ZAORNO ").append(
//               "\n WHERE 1=1").append(
//               " and CLCOMM=").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
//               " and COMMESSA=").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
//               " and ").append(notInStatement("CLLINP",LINEETOEXCLUDECOLOM)).append(
//               " and ZAFOST<>'' ").append( 
//               " and CLNART=0 ").append(
//               "\n UNION" ).append(
//               "\n select distinct  Codice_collo,0 as numart,linea,box,pedana,rtrim(ltrim(NumeroOrdine)) as numOrdine, ").append(
//                      "\n 0 as rigaordine,articolo,descrizioneArticolo ,DescrizioneArticolo2 ").append(
//
//               "\n from  DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL inner join TesyFactory_FEBAL_PROD.dbo.lyhead ").append(
//               "\n on rtrim(ltrim(NumeroOrdine))=([£5ORNO] collate  database_default ) ").append(
//     "\n where 1=1 ").append(
//     " and Commessa = ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
//     " and dataSpedizione = ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN)).append(        
//     " and ").append(notInStatement("Linea",LINEETOEXCLUDEFEBAL)).append(
//     " and [£5FRE1]='SOST'").append(
//     " and [£5DIVI]<>'RS1' ");

  
    
    
  StringBuilder qry=new StringBuilder( 
    "select distinct  Codice_collo,0 as numart,linea,box,pedana,rtrim(ltrim(NumeroOrdine)) as numOrdine,0 as rigaordine,articolo,descrizioneArticolo ,DescrizioneArticolo2 ").append(
         "\n from  DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL    inner join TesyFactory_FEBAL_PROD.dbo.lyhead  on rtrim(ltrim(NumeroOrdine))=([£5ORNO] collate  database_default ) ").append(
         "\n inner join  (select okcuno from [MVX2DESMOS].[dbo].[OCUSMA_FASCECLI] where FASCECLIENTE in ('AAA','AAA*') group by okcuno) b on cliente=OKCUNO ").append(
         "\n WHERE 1=1").append(
         " and Commessa = ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         " and dataSpedizione = ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN)).append(      
         " and [£5FRE1]='SOST' ").append( 
         " and [£5DIVI]<>'RS1' ").append( 
         "\n UNION " ).append(
    "select distinct  Codice_collo,0 as numart,linea,box,pedana,rtrim(ltrim(NumeroOrdine)) as numOrdine, 0 as rigaordine,articolo,descrizioneArticolo ,DescrizioneArticolo2 ").append(
         "\n from  DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL ").append(
         "\n inner join TesyFactory_FEBAL_PROD.dbo.lyhead  on rtrim(ltrim(NumeroOrdine))=([£5ORNO] collate  database_default ) ").append(
         "\n inner join  (select ZAORNO,zafost from [MVX2DESMOS].[dbo].[ZZHEAD2] where commessa=" ).append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append( " and ZAFOST<>'' and ZAFOST>'1' ) b on NumeroOrdine =ZAORNO ").append(
         "\n WHERE 1=1").append( 
         "\n and Commessa = ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         " and dataSpedizione = ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN)).append(       
         " and [£5FRE1]='SOST' ").append( 
         " and [£5DIVI]<>'RS1' ").append( 
         "\n UNION " ).append(
    "SELECT CLNCOL,CLNART,cast(CLLINP as varchar(8)),CLBOXN,CLPEDA,CLNROR,CLRIGA,CLARTI,CLDESC,CLSTRD ").append(
         "\n from [MVX2DESMOS].[dbo].SCxxxCOL inner join [MVX2DESMOS].[dbo].[ZZHEAD] on CLNROR=ZAORNO ").append(
         "\n WHERE 1=1").append(
         "\n and CLCOMM= ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         "\n and COMMESSA= ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         "\n and ZAFOST<>'' and ZAFOST>'1' ").append(
         "\n and CLNART=0 ").append( 
         "\n UNION ").append( 
    "SELECT CLNCOL,CLNART,cast(CLLINP as varchar(8)),CLBOXN,CLPEDA,CLNROR,CLRIGA,CLARTI,CLDESC,CLSTRD ").append(
         "\n from [MVX2DESMOS].[dbo].SCxxxCOL inner join [MVX2DESMOS].[dbo].[ZZHEAD] on CLNROR=ZAORNO ").append( 
         "\n inner join  (select okcuno from [MVX2DESMOS].[dbo].[OCUSMA_FASCECLI] where FASCECLIENTE in ('AAA','AAA*') group by okcuno) b on CLCODC=OKCUNO ").append( 
         "\n WHERE 1=1").append(
         "\n and CLCOMM= ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         "\n and COMMESSA= ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
         "\n and ZAFOST<>'' ").append( 
         "\n and CLNART=0 ");
    
  
   return qry.toString();
  }
  
}
