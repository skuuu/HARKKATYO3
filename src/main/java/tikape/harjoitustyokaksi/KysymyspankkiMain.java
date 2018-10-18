package tikape.harjoitustyokaksi;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
        
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        File tiedosto = new File("db", "kysymykset.db");
        Database db = new Database("jdbc:sqlite:" + tiedosto.getAbsolutePath());
        
        KysymysDao kysdao = new KysymysDao(db);
        VastausDao vasdao = new VastausDao(db);
        
   
        
        System.out.println("Hei maailma!");
        
        
        

        
        //GET
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
        
        
       //virhesivun näyttäminen: 
       Spark.get("/b", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "virheviesti");
        }, new ThymeleafTemplateEngine());
        
        
        
        
       
       
       
        //POST: 
        Spark.post("/a", (req, res)-> {
            //Jos lähetettävä lomake ei poista kysymystä/vastausta tai lisää näitä, näytetään virheviesti:
            if ((req.queryParams("kysymysteksti").equals("") || req.queryParams("kysymysteksti").equals("null"))
                    && (req.queryParams("vastaus").equals("")|| req.queryParams("vastaus").equals("null"))
                    && (req.queryParams("poistettavak").equals("")|| req.queryParams("poistettavak").equals("null"))
                    && (req.queryParams("poistettavav").equals("")|| req.queryParams("poistettavav").equals("null"))){
                res.redirect("/b");
                return "väärä syöte";
                
            }
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
            //vastaus lisätään vain, jos kyseisen id:n omaava kysymys on olemassa ja vastaukseen liityvän kysymyksen id on annettu ja integer-muotoinen:
            if ((!(req.queryParams("vastaus").equals(""))|| req.queryParams("vastaus").equals("null"))) {
                if (onkoInteger(req.queryParams("kysymys_id"))==false || onkoKysymysta(Integer.parseInt(req.queryParams("kysymys_id")), kysdao)==false || req.queryParams("kysymys_id").equals("") || req.queryParams("kysymys_id").equals("null")){
                    System.out.println("Kysymystä ei ole tietokannassa tai syötetty kysymyksen numero ei ole Integer");
                    res.redirect("/b");
                    return "Kysymystä ei ole tietokannassa tai syötetty kysymyksen numero ei ole Integer";
                    
                }
                //kysymyksen arvo:
                CheckboxGroup cg = new CheckboxGroup();
                String nimi = req.queryParams("oikein");
                Checkbox c1 = new Checkbox(nimi, false, cg);
                Checkbox cb = cg.getSelectedCheckbox();
                    
                //ei chekattu
                if(nimi==null) {
                    System.out.println("not checked");
                    Boolean arvo = false;
                    Integer kysymys_id = Integer.parseInt(req.queryParams("kysymys_id"));

                    Vastaus uusi = new Vastaus(req.queryParams("vastaus"));
                    uusi.setKysymys(kysymys_id);
                    uusi.setOikein(arvo);
                    vasdao.save(uusi);

                //chekattu
                } else {
//                    System.out.println(cb.getLabel() + " is checked");
                    Boolean arvo = true;
                    Integer kysymys_id = Integer.parseInt(req.queryParams("kysymys_id"));


                    Vastaus uusi = new Vastaus(req.queryParams("vastaus"));
                    uusi.setKysymys(kysymys_id);
                    uusi.setOikein(arvo);
                    vasdao.save(uusi);
                }
            }
            
         
            //kysymyksen poistaminen: 
            //kysymyksen poistaminen poistaa myös siihen liittyvät vastausvaihtoehdot
            if (!(req.queryParams("poistettavak").equals(""))|| req.queryParams("poistettavak").equals("null") && (onkoKysymysta(Integer.parseInt(req.queryParams("poistettavak")), kysdao)==true)){
                if (onkoInteger(req.queryParams("poistettavak"))==true){
                    Integer poistettavaid = Integer.parseInt(req.queryParams("poistettavak"));
                    List <Kysymys> kysymyslista = kysdao.findAll();
                    for (Kysymys kys: kysymyslista){
                        if (kys.id.equals(poistettavaid)){
                            vasdao.poistaVastaus(poistettavaid);
                            kysdao.delete(poistettavaid);
                        }
                    }
                //jos syöte ei ole integer, ohjataan virheviestisivulle:
                }else{
                    res.redirect("/b");
                    System.out.println("Syöte ei ole Integer");
                    return "Syöte ei ole Integer";
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
                //jos vastauksen numero ei ole integer, palautetaan virheviestisivu:
                }else{
                    res.redirect("/b");
                    System.out.println("Syöte ei ole Integer");
                    return "Syöte ei ole Integer";
                }
            
            }      
            res.redirect("/a");
            return "ok";
        });


    }
    //tarkistetaan, onko mainitulla id:llä olevaa kysymystä tietokannassa. 
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
