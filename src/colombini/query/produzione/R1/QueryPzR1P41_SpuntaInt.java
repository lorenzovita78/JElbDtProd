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
 * @author lvita
 */
public class QueryPzR1P41_SpuntaInt extends CustomQuery {

  public final static String FT_ULTIMAFASEP4="FT_ULTIMAFASEP4";
  public final static String FT_FASE30="FT_FASE30";

  
  
  //public final static String TAB_PRODP4="[AvanzamentoProd].[dbo].[TBL_DatiProduzione]";
  //public final static String TAB_DESMOS_DESTINAZIONI="[AvanzamentoProd].[dbo].[VW_DatiDestinazioniP4_PROD]";
 
  public final static String TAB_PRODP4="[DesmosColombini].[dbo].[DatiProduzione]";
  public final static String TAB_DESMOS_DESTINAZIONI="[DesmosColombini].[dbo].[DatiDestinazioni]";
  
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder sql=new StringBuilder();
    String sqlF="";
    String Operatore="= ";
    String ultimaFaseCondition ="";
    String joinLinea=" ) b  on  a.lineadestinazione=b.lineadestinazione";
    
    if(isFilterPresent(FT_ULTIMAFASEP4))
       ultimaFaseCondition =ClassMapper.classToString(getFilterValue(FT_ULTIMAFASEP4));
    
    if(ultimaFaseCondition.contains("%"))Operatore="LIKE ";

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
               " ,case \n"+
                    "when [descfase20] "+Operatore+ultimaFaseCondition+" and [descfase30] is not null and substring(ltrim([descfase30]),1,2) not in ('**') then [descfase30] \n"+
                    "when [descfase20] "+Operatore+ultimaFaseCondition+" and [descfase40] is not null and substring(ltrim([descfase40]),1,2) not in ('**') then [descfase40] \n"+
                    "when [descfase20] "+Operatore+ultimaFaseCondition+" and [descfase50] is not null and substring(ltrim([descfase50]),1,2) not in ('**') then [descfase50] \n"+
                    "when [descfase20] "+Operatore+ultimaFaseCondition+" then lineadestinazione \n"+   
                    "when [descfase30] "+Operatore+ultimaFaseCondition+" and [descfase40] is not null and substring(ltrim([descfase40]),1,2) not in ('**') then [descfase40] \n"+
                    "when [descfase30] "+Operatore+ultimaFaseCondition+" and [descfase50] is not null and substring(ltrim([descfase50]),1,2) not in ('**') then [descfase50] \n"+
                    "when [descfase30] "+Operatore+ultimaFaseCondition+" then lineadestinazione \n"+
                    "when [descfase40] "+Operatore+ultimaFaseCondition+" and [descfase50] is not null and substring(ltrim([descfase50]),1,2) not in ('**') then [descfase50] \n"+
                    "when [descfase40] "+Operatore+ultimaFaseCondition+" then lineadestinazione \n"+
                    "else lineadestinazione \n"+
                "end lineadest \n"+                 
               "\n FROM ").append(TAB_PRODP4).append(
               "\n WHERE 1=1 ");
      
       String sqlSub2 ="\n select distinct[LineaDestinazione] ,[LineaDestAbbreviata] from "+TAB_DESMOS_DESTINAZIONI+"  WHERE 1=1 ";
      
      
      StringBuilder commCondition=new StringBuilder(
                     "\n and Commessa =").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM)).append(
                     " and DataCommessa=").append(getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA));
              
      commCondition.append("and ([descfase30]").append(Operatore).append(ultimaFaseCondition).append("or [descfase40]").append(Operatore).append(ultimaFaseCondition).append("or [descfase50]").append(Operatore).append(ultimaFaseCondition).append("or [ultima_faseP4]").append(Operatore).append(ultimaFaseCondition).append(")");

      //CDL_SKIPPERR1P4_EDPC CDL_LSMCARRP4_EDPC
      
      if(isFilterPresent(FilterFieldCostantXDtProd.FT_LINEE)){
        commCondition.append(addAND(inStatement(" linealogica ", FilterFieldCostantXDtProd.FT_LINEE))); 
      }
      
      sqlSub1.append(commCondition);
      
     if(isFilterPresent(FT_ULTIMAFASEP4))
        joinLinea=" ) b  on  a.lineadest=b.lineadestinazione";
      
      sql.append(" ( ").append(sqlSub1).append( " ) a  inner join ( ").append(
                      sqlSub2).append(joinLinea);
              //sqlSub2).append("  ) b  on  a.lineadestinazione=b.lineadestinazione ");//collate database_default ");
      
     
      sqlF=sql.toString();
      
    return sqlF;
  }
  
}