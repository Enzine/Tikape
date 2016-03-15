package tkpe.foorumi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Maini {

    public static void main(String[] args) throws Exception {
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        String jdbcOsoite = "jdbc:sqlite:foorumi.db";
        if(System.getenv("DATABASE_URL") != null){
            jdbcOsoite = System.getenv("DATABASE_URL");
        }
        
        Database database = new Database(jdbcOsoite);
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
                    .getLangat());//getLangatSorted() TIMESTAMP KORJATTAVA TÄTÄ VARTEN
            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());
        
        // LANGAN TULOSTUS
        get("/:alue/:lanka", (req, res) -> {
            HashMap map = new HashMap<>();
//            List<Viesti> l = new ArrayList<>();
//            l.add(new Viesti("xDDDDD", "make"));
//            map.put("viestit", l);
            map.put("viestit", lankaDao
                    .findOne(
                            Integer.parseInt(req
                                    .params("lanka")))
                    .getViestitSorted()); //TÄMÄKIN VAATII MELKO VARMASTI TIMESTAMPIEN KORJAAMISEN
            return new ModelAndView(map, "lanka");
        }, new ThymeleafTemplateEngine());
        

        Viesti viesti = viestiDao.findOne(4);
        System.out.println("ID: " + viesti.getId() + ". Kirjoittaja: " + viesti.getKirjoittaja() + " " + viesti.getAika() + ", Viesti: " + viesti.getSisalto());
        // toimii muilla luokilla vastaava paitsi täällä ViestiDaolla. Ongelma on ViestiDao:n findOne-metodin TimeStamp-oliossa jollain tasolla.
    }
}
