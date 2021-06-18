/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.query.indicatoriOee;

import db.CustomQuery;
import exception.QueryException;

/**
 * Query che fornisce il tempo di produzione di un certo centro di lavoro ,
 * in base ai pz prodotti degli ordini di produzione evasi nella giornata indicata
 * @author lvita
 */
public abstract class QueryRuntimeOP extends CustomQuery{
  
  public final static String CENTROLAVORO="CENTROLAVORO";
  public final static String CENTROLAVOROPADRE="CENTROLAVOROPADRE";
  public final static String DATA="DATA";
  public final static String ORAINI="ORAINI";
  public final static String ORAFIN="ORAFIN";
  public final static String VALRIP="VALRIP";
  
  public final static String FILTERCLPRINCNO="FILTERCLPRINCNO";
  
  @Override
  public String toSQLString() throws QueryException {
    
    String centroLav=getFilterSQLValue(CENTROLAVORO);
    String centroLavPadre=null;
    
    String data=getFilterSQLValue(DATA);
    String oraini=(String) getFilterValue(ORAINI);
    String orafin=(String) getFilterValue(ORAFIN);
    String valripartizione=getFilterSQLValue(VALRIP);
    String condCLMWOPTR="";
    String condCLMWOHED="";
    
    if(isFilterPresent(CENTROLAVOROPADRE))
      centroLavPadre=getFilterSQLValue(CENTROLAVOROPADRE);
    
    if(centroLav!=null && centroLavPadre==null){
       condCLMWOPTR=" AND ( (DJPLGR = "+centroLav +" AND DJDPLG ='' )"
                           +" OR ( DJDPLG = "+centroLav+" )  )";
       condCLMWOHED=" and voplgr = "+centroLav;
    
    }else if(centroLav==null && centroLavPadre!=null){
       condCLMWOPTR=" AND DJPLGR = "+centroLavPadre;
       condCLMWOHED=" and voplgr = "+centroLavPadre;
    
    }else if(centroLav!=null && centroLavPadre!=null){
      condCLMWOPTR=" AND DJDPLG = "+centroLav;
      //condCLMWOPTR+=" AND DJPLGR = "+centroLavPadre;  commentata perchè la query si rallenta
      condCLMWOHED=" and voplgr= "+centroLavPadre;
    
    }
    
    
    
    StringBuffer qry=new StringBuffer(
                    getSelect1()).append(  
                    "\n FROM mwoptr inner join mwoope").append( 
                      " on djcono=vocono and djfaci=vofaci and djprno=voprno and djmfno=vomfno and djopno=voopno ").append( 
                    "\n WHERE 1=1").append(
                     " and DJCONO=30").append( //filtro statico sull'azienda
                      condCLMWOPTR).append(
                      " and djrgdt =").append(data).append(
                      " and djrgtm>=").append(oraini).append(
                      " and djrgtm<=").append(orafin).append(
                    getGroupBy1()).append(
                    getOrderBy1()).append(  
                     "\n UNION ALL ").append(
                    getSelect2(valripartizione) ).append( 
                    "\n FROM mwohed ").append(
                     " inner join mwoope on vhcono=vocono and vhfaci=vofaci and vhprno=voprno and vhmfno=vomfno ").append(
                     " left  join mwoptr on vhcono=djcono and vhfaci=djfaci and vhprno=djprno and vhmfno=djmfno  and voopno=djopno ").append(
                    "\n WHERE 1=1 and VHCONO=30 ").append(
                     " and vowost='90'").append( 
                     " and vhmaqt>0 ").append(   
                     " and vhorty in ('FV1' , 'FVX' , 'FVZ') ").append(                                
                      condCLMWOHED).append(
                    " and djopno is null ").append(         
                    " and vorefd=").append(data).append(
                    getGroupBy2()).append(
                    getOrderBy2());

    return qry.toString();
  }
  
  
  public abstract String getSelect1();
  
  public abstract String getSelect2(String valRipartizione);
  
  public abstract String getGroupBy1();
  
  public abstract String getGroupBy2();
  
  public abstract String getOrderBy1();
  
  public abstract String getOrderBy2();
  
}
