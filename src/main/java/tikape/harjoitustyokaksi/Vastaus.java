
package tikape.harjoitustyokaksi;

public class Vastaus {
    public Integer id;
    public Integer kysymys_id;
    private String vastausteksti;
    private Boolean oikein;
    private String oikeinteksti;
    
    
    public Vastaus (String vastausteksti){
        this.vastausteksti = vastausteksti;
        this.oikeinteksti = "oletus";
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
        return this.oikein;
        
    }
    public String getOikeinteksti(){
        return this.oikeinteksti;
    }

    public void setOikein(Boolean oikein) {
        if (oikein==true){
            oikeinteksti = "oikein";
            this.oikein = true;
        }else{
            oikeinteksti = "väärin";
            this.oikein = false;
        }
    }
    
    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id= id;
    }
    public String toString(){
        return this.id + this.vastausteksti + " (" + this.oikein + ")";
    }
    
}
