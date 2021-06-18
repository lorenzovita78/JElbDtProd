/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.datiComm.carico;

/**
 *
 * @author lvita
 */
public class CaricoComFromTAPSingleton {
  
  private static CaricoComFromTAPSingleton instance;
  
  private CaricoComFromTAPSingleton(){
    
  }
  
  public static CaricoComFromTAPSingleton getInstance(){
    if(instance==null){
      instance= new CaricoComFromTAPSingleton();
    }
    
    return instance;
  }
  
  
  
}
