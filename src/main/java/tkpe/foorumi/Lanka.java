package tkpe.foorumi;

import java.util.*;

public class Lanka {

    public int id;
    private String otsikko;
    private Alue alue;
    private List<Viesti> viestit;

    public Lanka(String otsikko) {
        this.otsikko = otsikko;
        this.viestit = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getOtsikko() {
        return otsikko;
    }
    
    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }

    public List<Viesti> getViestit() {
        return viestit;
    }
    
    public List<Viesti> getViestitSorted(){
        List<Viesti> v = getViestit();
        Collections.sort(v, 
                (v1, v2) -> v1.getAika()
                            .compareTo(
                            v2.getAika())
        );
        return v;
    }
    
    public Viesti getTuoreinViesti(){
        if(viestit.isEmpty()){
            return new Viesti("EI VIESTEJÄ xD", "SYSTEM32");
        }
        Viesti tuorein = viestit.get(0);
        
        for (int i = 1; i < viestit.size(); i++) {
            Viesti uusi = viestit.get(i);
            if(uusi.getAika().compareTo(tuorein.getAika())<0)
                tuorein = uusi;
        }
        
        return tuorein;
    }

    public void setViestit(List<Viesti> viestit) {
        this.viestit = viestit;
    }
    
    public Alue getAlue() {
        return alue;
    }

    public void setAlue(Alue alue) {
        this.alue = alue;
    }
}
