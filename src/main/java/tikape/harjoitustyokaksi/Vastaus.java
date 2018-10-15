
package tikape.harjoitustyokaksi;

public class Vastaus {
    public Integer id;
    public Integer kysymys_id;
    private String vastausteksti;
    private Boolean oikein;
    
    
    public Vastaus (String vastausteksti){
        this.vastausteksti = vastausteksti;
    }        

    public Integer getKysymys() {
        return kysymys_id;
    }

    public void setKysymys(Integer kysymys_id) {
        this.kysymys_id = kysymys_id;
    }

    public String getVastausteksti() {
        return vastausteksti;
    }

    public void setVastausteksti(String vastausteksti) {
        this.vastausteksti = vastausteksti;
    }

    public Boolean getOikein() {
        return oikein;
    }

    public void setOikein(Boolean oikein) {
        this.oikein = oikein;
    }
    
    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id= id;
    }
    public String toString(){
        return this.id + this.vastausteksti;
    }
    
}
