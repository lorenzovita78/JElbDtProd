/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.model.persistence.middleware.as400Vdl;

import colombini.model.persistence.middleware.AMsgDetailContainerTable;

/**
 *
 * @author lvita
 */
public class MsgVdlDett_H2V extends AMsgDetailContainerTable{

  public final static String TABLE_NAME="H2V_ContainerMasterData";
                                         
  public final static String MESSAGEID="MessageID";
  public final static String DATAKEY="DataKey";
  
  public final static String M4CONO="M4CONO";
  public final static String M4PACT="M4PACT";
  public final static String M4PANM="M4PANM";
  public final static String M4PACK="M4PACK";
  public final static String M4VOMT="M4VOMT";
  public final static String M4PACL="M4PACL";
  public final static String M4PACW="M4PACW";
  public final static String M4PACH="M4PACH";
  
  
  
  @Override
  public String getClmMessageId() {
    return MESSAGEID;
  }

  @Override
  public String getClmDataType() {
    return DATAKEY;
  }

  @Override
  public String getClmAz() {
    return M4CONO;
  }

  @Override
  public String getClmContainer() {
    return M4PACT;
  }

  @Override
  public String getClmContainerDesc() {
    return M4PANM;
  }

  @Override
  public String getClmContainerType() {
    return M4PACK;
  }

  @Override
  public String getClmWeight() {
    return M4VOMT;
  }
  
  @Override
  public String getClmLenght() {
    return M4PACL;
  }

  @Override
  public String getClmWidth() {
   return M4PACW;
  }

  @Override
  public String getClmHeight() {
    return M4PACH;
  }

  @Override
  public String getLibName() {
    return "";
  }

  @Override
  public String getTabName() {
    return TABLE_NAME;
  }

  
  
}


