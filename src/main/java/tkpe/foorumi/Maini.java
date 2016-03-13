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
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        Database database = new Database("jdbc:sqlite:foorumi.db");
        AlueDao alueDao = new AlueDao(database);
        LankaDao lankaDao = new LankaDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, lankaDao);

        Viesti viesti = viestiDao.findOne(4);
        System.out.println("ID: " + viesti.getId() + ". Kirjoittaja: " + viesti.getKirjoittaja() + " " + viesti.getAika() + ", Viesti: " + viesti.getSisalto());
        // toimii muilla luokilla vastaava paitsi täällä ViestiDaolla. Ongelma on ViestiDao:n findOne-metodin TimeStamp-oliossa jollain tasolla.
    }
}