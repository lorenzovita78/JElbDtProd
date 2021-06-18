/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.exception;

import exception.JLibException;

/**
 *
 * @author lvita
 */
public class OEEException extends JLibException{

  public OEEException() {
    super();
  }
  
  
  public OEEException(String mString){
    super(mString);
  }

  public OEEException(Throwable cause) {
     super(cause);
  }
  
}
