/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee.linee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query su database SQLSERVER per ottenere il numero di articoli e la tipologia prodotti in una giornata
 * su un determinato pantografo
 * @author lvita
 */
public class QueryiSagomatiG1P0ArticoliSpunt extends  CustomQuery{

  public final static String CENTROBITABIT="CENTROBITABIT";
  public final static String DATASTRING="DATASTRING";
  
  @Override
  public String toSQLString() throws QueryException {
    StringBuilder qry=new StringBuilder(); 
    String centrobitbit=null;
    String orainizio=null;
    String orafine=null;
    String dataS=null;
    
    //filtri necessari per la query se nn fossero valorizzati sarebbe da lanciare un eccezione
    if(isFilterPresent(CENTROBITABIT))
      centrobitbit=getFilterSQLValue(CENTROBITABIT);
    
   
    if(isFilterPresent(DATASTRING))
      dataS=getFilterSQLValue(DATASTRING);
    
   
    qry.append(" select P7ARTI ,num_Lavorati,Num_Ripantografati,num_Rilavorati,Setup,ID_Commessa ").append(
            " from  b2b_dati.dbo.CentriLavoro_Anagrafica a,  ").append(
            "              b2b_dati.dbo.OEE_PannelliGiorno b       ").append(
            "   where a.nome=").append(centrobitbit).append(
            "      and a.id=b.id_pantografo  " ).append(
            "     and data=convert(datetime,(").append(dataS).append("),112)").append(
            " order by setup ");
    
    
    return qry.toString();
  }
  
  
}



    
    



// qry.append("select articolo,count(*) num from").append(
//            " ( select  distinct b.id_pannello pannello,c.p7arti articolo").append(
//            "    from b2b_dati.dbo.CentriLavoro_Anagrafica a,b2b_dati.dbo.Log_Pantografi b,").append(
//                "     b2b_dati.dbo.Commesse_Dettagli_esecuzione c ").append(
//                " where a.id=b.id_pantografo").append(
//                " and b.id_pannello=c.id_pannello").append(
//                " and a.nome=").append(centrobitbit).append(
//                " and b.ora between convert(datetime,(").append(orainizio).append("),120) ").append(
//                " and convert(datetime,(").append(orafine).append("),120) ").append(
//                " ) a").append(
//          " where 1=1").append(
//          " group by articolo");
    