/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.produzione.R1;

import colombini.query.produzione.FilterQueryProdCostant;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QueryProdImaTop extends CustomQuery{

  
  
  @Override
  public String toSQLString() throws QueryException {
    String dataRif=getFilterSQLValue(FilterQueryProdCostant.FTDATARIF);
    
    StringBuffer qry=new StringBuffer(
            " select TOTALE ,").append(
            " OnlyPriess+BimaAndPriess+RiduzioneBimaAndPriess+RiduzioneandPriess as FORFORARE,").append(
            " DirectSortingStation+OnlyBima+RiduzioneandBima+OnlyRiduzione as FORPASSAGGIO,").append(
            " OnlyBima+RiduzioneandBima+BimaAndPriess+RiduzioneBimaAndPriess+OnlyRiduzione-BimaAndPriessAndSplitter as BIMA,").append(
            "\n BimaAndPriessAndSplitter+OnlySplitter as SpLITTER ").append(
            "  from ( ").append(
                    "\n select count(*) as TOTALE,\n" ).append(
                      " sum(case when drillingprg <> '' and groovingprg = '' and Width = WidthSaw then 1 else 0 end) as OnlyPriess,\n" ).append(
                      " sum(case when drillingprg <> '' and groovingprg = '' and Width < WidthSaw then 1 else 0 end) as RiduzioneandPriess,\n" ).append(
                      " sum(case when drillingprg = '' and groovingprg <> '' and Width = WidthSaw then 1 else 0 end) as OnlyBima, \n" ).append(
                      " sum(case when drillingprg = '' and groovingprg <> '' and Width < WidthSaw then 1 else 0 end) as RiduzioneandBima,\n" ).append(
                      " sum(case when drillingprg <> '' and groovingprg <> '' and Width = WidthSaw then 1 else 0 end) as BimaAndPriess,\n" ).append(
                      " sum(case when drillingprg <> '' and groovingprg <> '' and Width < WidthSaw then 1 else 0 end) as RiduzioneBimaAndPriess,\n" ).append(
                      " sum(case when drillingprg <> '' and groovingprg <> '' and splitterID <> '' then 1 else 0 end) as BimaAndPriessAndSplitter,\n" ).append(
                      " sum(case when drillingprg = '' and groovingprg = '' and splitterID <> '' then 1 else 0 end) as OnlySplitter, \n" ).append(
                      " sum(case when drillingprg = '' and groovingprg = '' and Width < WidthSaw then 1 else 0 end) as OnlyRiduzione, \n" ).append(
                      " sum(case when drillingprg = '' and groovingprg = '' and Width = WidthSaw then 1 else 0 end) as DirectSortingStation, \n" ).append(
                      " sum(case when Sequenze = 'OTF' then 0 else 1 end) as Imballo \n").append(
                     "\n from tab_et ").append(
                     " where 1=1  ").append(
                     " and CommissionDate = ").append(dataRif).append(
                     " and KenBC=2  ) a");
                   
    return qry.toString();
  }
  
  
  
  
//          StringBuffer qry=new StringBuffer(
//            " select TOTALE ,").append(
//            " OnlyPriess+BimaAndPriess as FORFORARE,").append(
//            " DirectSortingStation+OnlyBima as FORPASSAGGIO,").append(
//            " OnlyBima+(BimaAndPriess - BimaAndPriessAndSplitter) as BIMA,").append(
//            "\n BimaAndPriessAndSplitter as SpLITTER ").append(
//            "  from ( ").append(
//                    "\n select count(*) as TOTALE, ").append(
//                          "\n sum(case when drillingprg <> '' and groovingprg = '' then 1 else 0 end) as OnlyPriess, ").append(
//                          "\n sum(case when drillingprg = '' and groovingprg <> '' then 1 else 0 end) as OnlyBima, ").append(
//                          "\n sum(case when drillingprg <> '' and groovingprg <> '' then 1 else 0 end) as BimaAndPriess, ").append(
//                          "\n sum(case when drillingprg <> '' and groovingprg <> '' and splitterID <> '' then 1 else 0 end) as BimaAndPriessAndSplitter, ").append(
//                          "\n sum(case when drillingprg = '' and groovingprg = '' and splitterID <> '' then 1 else 0 end) as OnlySplitter, ").append(
//                          "\n sum(case when drillingprg = '' and groovingprg = '' and splitterID = '' then 1 else 0 end) as DirectSortingStation ").append(
//                     "\n from tab_et ").append(
//                     " where 1=1  ").append(
//                     " and CommissionDate = ").append(dataRif).append(
//                     " ) a");
}
