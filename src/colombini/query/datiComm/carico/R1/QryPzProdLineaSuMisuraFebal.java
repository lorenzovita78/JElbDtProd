/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.query.datiComm.carico.R1;

import colombini.query.datiComm.FilterFieldCostantXDtProd;
import db.CustomQuery;
import exception.QueryException;

/**
 *
 * @author lvita
 */
public class QryPzProdLineaSuMisuraFebal extends CustomQuery {

  @Override
  public String toSQLString() throws QueryException {
   String numCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_NUMCOMM);
   String dataCom=getFilterSQLValue(FilterFieldCostantXDtProd.FT_DATA);
   
   String lancio=dataCom.substring(0, 4)+numCom;
   
   StringBuilder sql=new StringBuilder (
                       " SELECT coalesce(sum(npz),0) from ( ").append(
                     "\n SELECT sum(qtadest) npz ").append(
                     "     FROM DesmosFebal.dbo.LPMSEMI_04_PDF_MAT_PROD_K ").append(
                     "    WHERE dataSP=").append(dataCom).append(
                     "      and numProd=").append(numCom).append(
                     "\n UNION ").append(
                     "\n SELECT sum(qtadest) ").append(
                     "     FROM DesmosFebal.dbo.LPMSEMI_04_PDF_MAT_PROD_C ").append(
                     "    where dataSP=").append(dataCom).append(
                     "      and numProd=").append(numCom).append(
                     "\n  UNION ").append(
                     "\n SELECT sum(qtadest) ").append(
                     "     FROM DesmosFebal.dbo.LPMSEMI_04_PDF_MAT_PROD_C_CATENE ").append(
                     "    where dataSP=").append(dataCom).append(
                     "      and numProd=").append(numCom).append(
                     "\n  UNION ").append(
                     "\n SELECT sum(qtadest) ").append(
                     "     FROM DesmosFebal.dbo.LPMSEMI_04_PDF_MAT_PROD_C_SCH_SP3 ").append(
                     "    where dataSP=").append(dataCom).append(
                     "      and numProd=").append(numCom).append(
                     "\n  UNION ").append(
                     "\n SELECT sum(qtadest) ").append(
                     "     FROM DesmosFebal.dbo.LPMSEMI_04_PDF_MAT_PROD_C_ANTE ").append(
                     "    where dataSP=").append(dataCom).append(
                     "      and numProd=").append(numCom).append(
                     "\n  UNION ").append(
                     "\n  SELECT sum(QTA) ").append(
                     "      FROM DesmosFebal.dbo.LPM30_PDF_PEZZI_FUORI_IMPIANTO_SL ").append(
                     "     where 1=1  ").append(
                     " 	and numProd=").append(numCom).append(
                     " 	and ClasseRotazione in ('K','C') ").append(
                     "\n UNION ").append(
                     "\n SELECT sum(QTA) ").append(
                     "     FROM DesmosFebal.dbo.LPM36_PDF_LISTA_FM_FUORI_IMPIANTO_SL ").append(
                     "    where 1=1  ").append(
                     "      and numProd=").append(numCom).append(
                     "      and ClasseRotazione in ('K','C') ").append(
                     "\n UNION ").append(
                     "\n SELECT sum(qta_effettiva) ").append( 
                     "     FROM DesmosFebal.dbo.ETK00_LST_ETIC_PDF_ANTECAM_LL_NEW_PROD ").append(
                     "    where desmoslancio=").append(lancio).append(
                     "      and carid='FRPRE' ").append(
                     "\n UNION ").append(
                     "\n SELECT sum(qta_effettiva) ").append(
                     " FROM DesmosFebal.dbo.ETK00_LST_ETIC_PDF_FIA_FIN_PROD ").append(
                     " where desmoslancio=").append(lancio).append(
                     " and opti like 'FIA%' ").append(
                     " and desmosDestinatario='41' ").append(
                     "\n  ) a ");
    
    return sql.toString(); //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
}
