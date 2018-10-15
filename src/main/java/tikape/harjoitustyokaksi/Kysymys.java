package tikape.harjoitustyokaksi;

public class Kysymys {

    public Integer id;
    private String kurssi;
    private String aihe;
    private String kysymysteksti;
    
    public Kysymys(String kysymysteksti) {
        this.kysymysteksti = kysymysteksti;
    }

    public String getKurssi() {
        return kurssi;
    }

    public void setKurssi(String kurssi) {
        this.kurssi = kurssi;
    }
    public int getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id=id;
    }

    public String getAihe() {
        return aihe;
    }

    public void setAihe(String aihe) {
        this.aihe = aihe;
    }

    public String getKysymysteksti() {
        return kysymysteksti;
    }

    public void setKysymysteksti(String kysymysteksti) {
        this.kysymysteksti = kysymysteksti;
    }
    public String toString(){
        return "KYSYMYS NRO: " + this.id + ", KURSSI: " + this.kurssi + "\n, AIHE: " + this.aihe + "\n, KYSYMYS: " + this.kysymysteksti;
    }
     

}
