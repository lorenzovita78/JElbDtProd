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
    StringBuilder qry=new StringBuilder(
              " SELECT CLNCOL,CLNART,cast(CLLINP as varchar(8)),CLBOXN,CLPEDA,CLNROR,CLRIGA,CLARTI,CLDESC,CLSTRD  ").append(
               "\n from [MVX2DESMOS].[dbo].SCxxxCOL inner join [MVX2DESMOS].[dbo].[ZZHEAD] on CLNROR=ZAORNO ").append(
               "\n WHERE 1=1").append(
               " and CLCOMM=").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
               " and COMMESSA=").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
               " and ").append(notInStatement("CLLINP",LINEETOEXCLUDECOLOM)).append(
               " and ZAFOST<>'' ").append( 
               " and CLNART=0 ").append(
               "\n UNION" ).append(
               "\n select distinct  Codice_collo,0 as numart,linea,box,pedana,rtrim(ltrim(NumeroOrdine)) as numOrdine, ").append(
                      "\n 0 as rigaordine,articolo,descrizioneArticolo ,DescrizioneArticolo2 ").append(

               "\n from  DesmosFebal.dbo.LDF_TXT_FILE_PER_VDL inner join TesyFactory_FEBAL_PROD.dbo.lyhead ").append(
               "\n on rtrim(ltrim(NumeroOrdine))=([£5ORNO] collate  database_default ) ").append(
     "\n where 1=1 ").append(
     " and Commessa = ").append(getFilterSQLValue(FilterQueryProdCostant.FTNUMCOMM)).append(
     " and dataSpedizione = ").append(getFilterSQLValue(FilterQueryProdCostant.FTDATACOMMN)).append(        
     " and ").append(notInStatement("Linea",LINEETOEXCLUDEFEBAL)).append(
     " and [£5FRE1]='SOST'").append(
     " and [£5DIVI]<>'RS1' ");

  
  
   return qry.toString();
  }
  
}
