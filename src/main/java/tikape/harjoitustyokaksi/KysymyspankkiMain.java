package tikape.harjoitustyokaksi;

import java.io.File;
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
            
            //lisätään kysymys+siihen liittyvät vastaukset mappi2:een. Jos kysymyksellä
            //ei ole vastauksia, ei sitä lisätä
            for (int y = 0; y < kysymyslista.size(); y++) {
                System.out.println(kysymyslista.get(y));
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
                kysdao.saveOrUpdate(uusi);
                
            }
            
            if (!(req.queryParams("vastaus").equals(""))|| req.queryParams("vastaus").equals("null")){
                String vastausteksti = req.queryParams("vastaus");
                Boolean arvo = true; 
                if (req.queryParams("oikein").equals("true")){
                    arvo = true; 
                }else {
                    arvo = false;
                }

                Integer kysymys_id = Integer.parseInt(req.queryParams("kysymys_id"));

                Vastaus uusi = new Vastaus(vastausteksti);
                uusi.setKysymys(kysymys_id);
                uusi.setOikein(arvo);
                vasdao.saveOrUpdate(uusi);
            }
            
            //kysymyksen poistaminen: 
            if (!(req.queryParams("poistettavak").equals(""))|| req.queryParams("poistettavak").equals("null")){
                Integer poistettavaid = Integer.parseInt(req.queryParams("poistettavak"));
                List <Kysymys> kysymyslista = kysdao.findAll();
                for (Kysymys kys: kysymyslista){
                    if (kys.id.equals(poistettavaid)){
                        kysdao.delete(poistettavaid);
                    }
                }
            }
            
            
            //vastauksen poistaminen: 
            if (!(req.queryParams("poistettavav").equals(""))|| req.queryParams("poistettavav").equals("null")){
                Integer poistettavavid = Integer.parseInt(req.queryParams("poistettavav"));
                List <Vastaus> vastauslista = vasdao.findAll();
                for (Vastaus vas: vastauslista){
                    if (vas.id.equals(poistettavavid)){
                        vasdao.delete(poistettavavid);
                    }
                }
            }
            
            
                   
            res.redirect("/a");
            return "ok";
        });

            




            
     
        

//        Spark.get("/v", (req, res) -> {
//            HashMap map = new HashMap<>();
//            map.put("kysymykset", kysdao.findAll());
//            map.put ("vastaukset", vasdao.findAll());
//
//            
//             //tallennetaan uuteen mappiin kysymys ja sen vastauslista:  
//            List <Vastaus> vastauslista = new ArrayList<>();
//            List <Kysymys> kysymyslista = new ArrayList<>();
//            HashMap <Kysymys, ArrayList<Vastaus>> mappi2  = new HashMap<>();
//            
//            //lisätään kaikki kysymykset kysymyslistalle ja vastaukset vastauslistalle:
//            kysymyslista = kysdao.findAll();
//            vastauslista = vasdao.findAll();
//            
//            //lisätään kysymys+siihen liittyvät vastaukset mappi2:een. Jos kysymyksellä
//            //ei ole vastauksia, ei sitä lisätä
//            for (int y = 0; y < kysymyslista.size(); y++) {
//                System.out.println(kysymyslista.get(y));
//                for (int x = 0; x < vastauslista.size(); x++) {
//                    if (vastauslista.get(x).getKysymys()== kysymyslista.get(y).getId()){
//                        mappi2.putIfAbsent(kysymyslista.get(y), new ArrayList<>());
//                        mappi2.get(kysymyslista.get(y)).add(vastauslista.get(x));
//                    
//                    }else{
//                        mappi2.putIfAbsent(kysymyslista.get(y), new ArrayList<>());
//
//                    }
//                    
//                }
//            }
//            map.put("kysymysvastausparit", mappi2);
//                    
//            return new ModelAndView(map, "index2");
//        }, new ThymeleafTemplateEngine());
    }

        
}
