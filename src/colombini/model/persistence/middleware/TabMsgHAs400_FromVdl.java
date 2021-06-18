/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware;

/**
 *
 * @author lvita
 */
public class TabMsgHAs400_FromVdl extends TabMsgHAs400_ToVdl{

  protected final static String TABLE_NAME="ZVAIMS";
  
  
  @Override
  public String getTabName() {
    return TABLE_NAME;
  }
  
}
