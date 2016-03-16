package tkpe.foorumi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import java.sql.*;
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

        String jdbcOsoite = "jdbc:sqlite:foorumi.db";
        if (System.getenv("DATABASE_URL") != null) {

            if (System.getenv("DATABASE_URL") != null) {
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

            // VIESTIN VASTAANOTTO
            post("/baneplane", (req, res) -> {
                String viesti = req.queryParams("viesti");
                if (viesti.length() > 1000) {
                    int nope = viesti.length() - 1000;
                    return "Viestisi oli " + nope + " merkkiä liian pitkä.";
                }

                String author = req.queryParams("lahettaja");
                if (author.length() > 30) {
                    int nope = author.length() - 30;
                    return "Nimimerkkisi oli " + nope + " merkkiä liian pitkä.";
                }

                try (Connection con = database.getConnection()) {
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate("INSERT INTO Viesti (sisalto, lanka, kirjoittaja) VALUES (\'" + viesti + "\', 1, \'" + author + "\');");
                }

                return "Viestisi on lisätty lankaan<br/>";
            });

            Viesti viesti = viestiDao.findOne(4);
            System.out.println("ID: " + viesti.getId() + ". Kirjoittaja: " + viesti.getKirjoittaja() + " " + viesti.getAika() + ", Viesti: " + viesti.getSisalto());

            get("/", (req, res) -> {
                HashMap map = new HashMap<>();

                map.put("alueet", alueDao.findAll());

                return new ModelAndView(map, "index");
            }, new ThymeleafTemplateEngine());

//        Viesti viesti = viestiDao.findOne(4);
//        System.out.println("ID: " + viesti.getId() + ". Kirjoittaja: " + viesti.getKirjoittaja() + " " + viesti.getAika() + ", Viesti: " + viesti.getSisalto());
            // toimii muilla luokilla vastaava paitsi täällä ViestiDaolla. Ongelma on ViestiDao:n findOne-metodin TimeStamp-oliossa jollain tasolla.
        }
    }
}
