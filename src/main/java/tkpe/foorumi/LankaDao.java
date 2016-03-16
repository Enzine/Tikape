package tkpe.foorumi;

import java.sql.*;
import java.util.*;

public class LankaDao implements Dao<Lanka, Integer> {

    private Database database;
    private Dao<Alue, Integer> alueDao;

    public LankaDao(Database database, Dao<Alue, Integer> alueDao) {
        this.database = database;
        this.alueDao = alueDao;
    }

    @Override
    public Lanka findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Lanka WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String otsikko = rs.getString("otsikko");
        Integer alue = rs.getInt("alue");

        Lanka l = new Lanka(otsikko);

        rs.close();
        stmt.close();
        connection.close();

        l.setId(id);
        l.setAlue(this.alueDao.findOne(alue));
        
        
        return l;
    }

    @Override
    public List<Lanka> findAll() throws SQLException {
        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Lanka");
        ResultSet rs = stmt.executeQuery();
        
        Map<Integer, List<Lanka>> lankojenAlueet = new HashMap<>();
        
        List<Lanka> langat = new ArrayList<>();
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String otsikko = rs.getString("otsikko");
            
            Lanka l = new Lanka(otsikko);
            l.setId(id);
            
            int fk = rs.getInt("alue");
            
            if (!lankojenAlueet.containsKey(fk)) {
                lankojenAlueet.put(fk, new ArrayList<>());
            }
            lankojenAlueet.get(fk).add(l);
            langat.add(l);
        }
        
        rs.close();
        stmt.close();
        con.close();
        
        for (Alue alue : this.alueDao.findAll()) {
            if (!lankojenAlueet.containsKey(alue.getId())) {
                continue;
            }
            
            for (Lanka lanka : lankojenAlueet.get(alue.getId())) {
                lanka.setAlue(alue);
            }
        }
        
        return langat;
    }

    @Override
    public List<Lanka> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Lanka WHERE id IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }
        ResultSet rs = stmt.executeQuery();
        
        Map<Integer, List<Lanka>> lankojenAlueet = new HashMap<>();
        
        List<Lanka> langat = new ArrayList<>();
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String otsikko = rs.getString("otsikko");
            
            Lanka l = new Lanka(otsikko);
            l.setId(id);
            
            int fk = rs.getInt("alue");
            
            if (!lankojenAlueet.containsKey(fk)) {
                lankojenAlueet.put(fk, new ArrayList<>());
            }
            lankojenAlueet.get(fk).add(l);
        }
        
        rs.close();
        stmt.close();
        con.close();
        
        for (Alue alue : this.alueDao.findAll()) {
            if (!lankojenAlueet.containsKey(alue.getId())) {
                continue;
            }
            
            for (Lanka lanka : lankojenAlueet.get(alue.getId())) {
                lanka.setAlue(alue);
            }
        }
        
        return langat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
