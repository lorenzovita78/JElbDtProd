/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package colombini.exception;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lvita
 */
public class DatiCommLineeException extends Exception {
  
  private List listaErrori = new ArrayList();
  private boolean listErr = false;

	
  
  public DatiCommLineeException(Exception ex) {
    super(ex);
  }
  
  public DatiCommLineeException(String message) {
	  this(message,(Throwable)null);
	}

  public DatiCommLineeException(String message, Throwable cause) {
    super(message, cause);
  }

  
  public DatiCommLineeException(String message, Throwable cause,List error){
	super(message, cause);
	listaErrori.addAll(error);
	listErr  = true;
  }
  
  
  public List getListaErrori(){
	  return listaErrori;
  }  
  
  
  public boolean isPresentListErr(){
    return listErr;
  }


	
  
}
