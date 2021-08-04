/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package colombini.model;

import colombini.model.persistence.TempiCicloForBiesseG1;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.ClassMapper;

/**
 *
 * @author lvita
 */
public class IProdSupervisoreForBiesseBean {
  
  
    String cdl;
    List<TempiCicloForBiesseG1> articoliBeans;
    List<List> fermiGiornata;
    Long qtaTot;
    
    List fermiTot;
    Map mapIdFasiProdElab;
    Map mapTCicloArt;
    Date dtFineUltLotto;
            
    Long tempoProdSupervisore;
    Long tempoNnProdSupervisore;
    Double tRuntimeEff;
    Double tMicrof;
    Long tMicrofEventi;
    
    public IProdSupervisoreForBiesseBean(String cl){
      articoliBeans=new ArrayList();
      fermiGiornata=new ArrayList();
      qtaTot=Long.valueOf(0);
      tempoProdSupervisore=Long.valueOf(0);
      tempoNnProdSupervisore=Long.valueOf(0);
      tRuntimeEff=Double.valueOf(0);
      tMicrof=Double.valueOf(0);
      fermiTot=new ArrayList();
      mapIdFasiProdElab=new HashMap();
      mapTCicloArt=new HashMap();
      tMicrofEventi=Long.valueOf(0);
      cdl=cl;
    }        

    
    public void addArticolo(TempiCicloForBiesseG1 b){
      this.articoliBeans.add(b);
    }
    
    public void addIdProdArticolo(Long id){
      this.mapIdFasiProdElab.put(id, id);
    }
    
    public void addTCiCloArt (String codArt,Double tciclo){
       this.mapTCicloArt.put(codArt,tciclo);
    }
    
    public Boolean isPresentIdProdArt(Long id){
      return mapIdFasiProdElab.containsKey(id);
    }
    
    public Boolean isPresentTCicloArt(String codArt){
      return mapTCicloArt.containsKey(codArt);
    }
    
    
    public void addTempoProdSupervisore(Long tempo){
      this.tempoProdSupervisore+=tempo;
    }
    
    public void addTempoNnProdSupervisore(Long tempo){
      this.tempoNnProdSupervisore+=tempo;
    }
    
    public void addTRuntimeEff(Double tempo){
      this.tRuntimeEff+=tempo;
    }
    
    public void addQta(Integer qta){
      this.qtaTot+=qta;
    }
    
    public void addTMicroF(Double t){
      this.tMicrof+=t;
    }
    
    public List<TempiCicloForBiesseG1>  getArticoliBeans() {
      return articoliBeans;
    }

    public void setArticoliBeans(List<TempiCicloForBiesseG1> articoliBeans) {
      this.articoliBeans = articoliBeans;
    }

    public Long getQtaTot() {
      return qtaTot;
    }

    public void setQtaTot(Long qtaTot) {
      this.qtaTot = qtaTot;
    }

    public Long getTempoProdSupervisore() {
      return tempoProdSupervisore;
    }

    public void setTempoProdSupervisore(Long tempoProdSupervisore) {
      this.tempoProdSupervisore = tempoProdSupervisore;
    }

    public Long getTempoNnProdSupervisore() {
      return tempoNnProdSupervisore;
    }

    public void setTempoNnProdSupervisore(Long tempoNnProdSupervisore) {
      this.tempoNnProdSupervisore = tempoNnProdSupervisore;
    }

//    public List getFermiTot() {
//      return fermiTot;
//    }
//
//    public void setFermiTot(List fermiToc) {
//      this.fermiTot = fermiToc;
//    }

    public Map getMapIdFasiProdElab() {
      return mapIdFasiProdElab;
    }

    public void setMapIdFasiProdElab(Map mapIdFasiProdElab) {
      this.mapIdFasiProdElab = mapIdFasiProdElab;
    }

    public Date getDtFineUltLotto() {
      return dtFineUltLotto;
    }

    public void setDtFineUltLotto(Date dtFineUltLotto) {
      this.dtFineUltLotto = dtFineUltLotto;
    }

    public String getCdl() {
      return cdl;
    }

  public Double gettRuntimeEff() {
    return tRuntimeEff;
  }

  public void settRuntimeEff(Double tRuntimeEff) {
    this.tRuntimeEff = tRuntimeEff;
  }

  public Double gettMicrof() {
    return tMicrof;
  }

  public void settMicrof(Double tMicrof) {
    this.tMicrof = tMicrof;
  }

  
  public Map getMapTCicloArt() {
    return mapTCicloArt;
  }

  public void setMapTCicloArt(Map mapTCicloArt) {
    this.mapTCicloArt = mapTCicloArt;
  }

  public List<List> getFermiGiornata() {
    return fermiGiornata;
  }

  public void setFermiGiornata(List<List> fermiGiornata) {
    this.fermiGiornata = fermiGiornata;
  }

  public Long gettMicrofEventi() {
    return tMicrofEventi;
  }

  public void settMicrofEventi(Long tMicrofEventi) {
    this.tMicrofEventi = tMicrofEventi;
  }
    
  public Long getTempoFermiNnCausalizzati(){
    Long t=Long.valueOf(0);
    for(List f:fermiGiornata){
      Integer caus=ClassMapper.classToClass(f.get(0),Integer.class);
      if(caus<=0)
        t+=ClassMapper.classToClass(f.get(1),Integer.class);
    }
    
    return t;
  }
  
  
  public List getListInfoArtProd(){
    if(articoliBeans.isEmpty())
      return null;
    
    List l=new ArrayList();
    List i=new ArrayList();
    i.add("ARTICOLO");i.add("QTA");i.add("DATAINIPROD");i.add("TEMPOPROD");
    i.add("TEMPOCICLO");i.add("TEMPOFERMI");i.add("TEMPOMICROF");
    i.add("LISTAFERMI");
    l.add(i);
    
    for(TempiCicloForBiesseG1 b:articoliBeans){
      l.add(b.getInfoList());
    }
    
    
    return l;
  }
    
  
  
}
