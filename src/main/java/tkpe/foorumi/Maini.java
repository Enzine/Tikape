package tkpe.foorumi;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Maini {

    public static void main(String[] args) throws Exception {
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        Database database = new Database("jdbc:sqlite:foorumi.db");
        AlueDao alueDao = new AlueDao(database);
        LankaDao lankaDao = new LankaDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, lankaDao);
        
        // ALUEIDEN TULOSTUS
        get("/", (req, res) -> {
            HashMap map = new HashMap();
            map.put("alueet", alueDao.findAll());
            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());
        
        // ALUEEN TULOSTUS
        get("/:alue", (req, res) -> {
            HashMap map = new HashMap();
            map.put("langat", alueDao.findOne(
                            Integer.parseInt(
                                    req.params("alue")))
                    .getLangat());//getLangatSorted() 
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        // LANGAN TULOSTUS
        get("/:alue/:lanka", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", lankaDao
                    .findOne(Integer
                            .parseInt(req
                                    .params("lanka")))
                    .getviestitSorted());
            return new ModelAndView(map, "lanka");
        }, new ThymeleafTemplateEngine());
        

        Viesti viesti = viestiDao.findOne(4);
        System.out.println("ID: " + viesti.getId() + ". Kirjoittaja: " + viesti.getKirjoittaja() + " " + viesti.getAika() + ", Viesti: " + viesti.getSisalto());
        // toimii muilla luokilla vastaava paitsi täällä ViestiDaolla. Ongelma on ViestiDao:n findOne-metodin TimeStamp-oliossa jollain tasolla.
    }
}
