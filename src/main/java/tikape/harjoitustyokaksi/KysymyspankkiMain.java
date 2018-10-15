package tikape.harjoitustyokaksi;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class KysymyspankkiMain {

    public static void main(String[] args) throws Exception {
        File tiedosto = new File("db", "kysymykset.db");
        Database db = new Database("jdbc:sqlite:" + tiedosto.getAbsolutePath());
        
        KysymysDao kysdao = new KysymysDao(db);
        VastausDao vasdao = new VastausDao(db);
        
        Vastaus muokattava = new Vastaus ("tyhja");
   
        
        System.out.println("Hei maailma!");
        
        
        

        
        //sivun näyttäminen
        Spark.get("/a", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kysymykset", kysdao.findAll());
            map.put ("vastaukset", vasdao.findAll());

            
             //tallennetaan uuteen mappiin kysymys ja sen vastauslista:  
            List <Vastaus> vastauslista = new ArrayList<>();
            List <Kysymys> kysymyslista = new ArrayList<>();
            HashMap <Kysymys, ArrayList<Vastaus>> mappi2  = new HashMap<>();
            
            //lisätään kaikki kysymykset kysymyslistalle ja vastaukset vastauslistalle:
            kysymyslista = kysdao.findAll();
            vastauslista = vasdao.findAll();
            
            //lisätään kysymys+siihen liittyvät vastaukset mappi2:een. 
            for (int y = 0; y < kysymyslista.size(); y++) {
                for (int x = 0; x < vastauslista.size(); x++) {
                    if (vastauslista.get(x).getKysymys()== kysymyslista.get(y).getId()){
                        mappi2.putIfAbsent(kysymyslista.get(y), new ArrayList<>());
                        mappi2.get(kysymyslista.get(y)).add(vastauslista.get(x));
                    
                    }else{
                        mappi2.putIfAbsent(kysymyslista.get(y), new ArrayList<>());

                    }
                    
                }
            }
            map.put("kysymysvastausparit", mappi2);
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        
       
        
        
        //postaaminen: 
        Spark.post("/a", (req, res)-> {

            //kysymyksen lisääminen: 
            if (!(req.queryParams("kysymysteksti").equals("") || req.queryParams("kysymysteksti").equals("null"))){
                String kysymysteksti = req.queryParams("kysymysteksti");
                String aihe = req.queryParams("aihe");
                String kurssi = req.queryParams("kurssi");
            
            
                Kysymys uusi = new Kysymys (kysymysteksti);
                uusi.setAihe(aihe);
                uusi.setKurssi(kurssi);
                kysdao.save(uusi);
                
            }
            //vastauksen lisääminen:
            if ((!(req.queryParams("vastaus").equals(""))|| req.queryParams("vastaus").equals("null"))
            && (onkoKysymysta(Integer.parseInt(req.queryParams("kysymys_id")), kysdao)==true)) {
                String vastausteksti = req.queryParams("vastaus");
                Boolean arvo = true; 
                if (req.queryParams("oikein").trim().equals("true")){
                    arvo = true;
                    Integer kysymys_id = Integer.parseInt(req.queryParams("kysymys_id"));
                    

                    Vastaus uusi = new Vastaus(vastausteksti);
                    uusi.setKysymys(kysymys_id);
                    uusi.setOikein(arvo);
                    vasdao.save(uusi);
                
                }else if (req.queryParams("oikein").trim().equals("false")){
                    arvo = false;
                    Integer kysymys_id = Integer.parseInt(req.queryParams("kysymys_id"));

                    Vastaus uusi = new Vastaus(vastausteksti);
                    uusi.setKysymys(kysymys_id);
                    uusi.setOikein(arvo);
                    vasdao.save(uusi);
                }else{
                    System.out.println("virheellinen Boolean-arvo");
                }

                
            }
            
            //kysymyksen poistaminen: 
            //kysymyksen poistaminen poistaa myös siihen liittyvät vastausvaihtoehdot
            if (!(req.queryParams("poistettavak").equals(""))|| req.queryParams("poistettavak").equals("null") && (onkoKysymysta(Integer.parseInt(req.queryParams("kysymys_id")), kysdao)==true)){
                if (onkoInteger(req.queryParams("poistettavak"))==true){
                    Integer poistettavaid = Integer.parseInt(req.queryParams("poistettavak"));
                    List <Kysymys> kysymyslista = kysdao.findAll();
                    for (Kysymys kys: kysymyslista){
                        if (kys.id.equals(poistettavaid)){
                            vasdao.poistaVastaus(poistettavaid);
                            kysdao.delete(poistettavaid);
                        }
                    }
                }
            }
            
            
            //vastauksen poistaminen: 
            if (!(req.queryParams("poistettavav").equals(""))|| req.queryParams("poistettavav").equals("null")){
                if (onkoInteger(req.queryParams("poistettavav"))==true){
                    Integer poistettavavid = Integer.parseInt(req.queryParams("poistettavav"));
                    List <Vastaus> vastauslista = vasdao.findAll();
                    for (Vastaus vas: vastauslista){
                        if (vas.id.equals(poistettavavid)){
                            vasdao.delete(poistettavavid);
                        }
                    }
                }
            }
            
            
                   
            res.redirect("/a");
            return "ok";
        });


    }
    //tarkistaa, onko mainitulla id:llä olevaa kysymystä tietokannassa. 
    public static Boolean onkoKysymysta(int id, KysymysDao kysdao) throws SQLException{
        for (Kysymys kys: kysdao.findAll()){
            if (kys.getId()==id){
                return true;
            }

        }
        return false;
    }
    
    //tarkistetaan, että syötetty luku on Integer: 
    public static boolean onkoInteger(String s){
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
}
    
}
