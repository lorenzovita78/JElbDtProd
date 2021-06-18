
import elabObj.ALuncherElabs;
import java.util.List;
import utils.FilePropUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lvita
 */
public class LuncherElabDtProd extends ALuncherElabs{
  
  
  private final static String FILEPROSELABS="../props/elabsJLV.properties";
  
  public LuncherElabDtProd(List listParams) {
    super(listParams);
  }

  
  @Override
  public String getFileNamePropsElabs() {
    return FILEPROSELABS; //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getSeparatorPropsElabs() {
    return FilePropUtils.STRSEPMULTIPROP2;
  }
  
 
  
}
