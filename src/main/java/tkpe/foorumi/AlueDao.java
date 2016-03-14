package tkpe.foorumi;

import java.sql.*;
import java.util.*;

public class AlueDao implements Dao<Alue, Integer> {

    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }

    @Override
    public Alue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Alue a = new Alue(nimi);
        
        LankaDao ld = new LankaDao(database, this);
        
        List<Lanka> palautettavat = new ArrayList<>();
        List<Lanka> langat = ld.findAll();
        
        for (Lanka l : langat) {
            if(l.getAlue().getId() == key)
                palautettavat.add(l);
        }
        
        a.setLangat(palautettavat); //

        rs.close();
        stmt.close();
        connection.close();

        a.setId(id);

        return a;
    }

    @Override
    public List<Alue> findAll() throws SQLException {
        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Alue");
        ResultSet rs = stmt.executeQuery();
        
        List<Alue> alueet = new ArrayList<>();
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            
            Alue a = new Alue(nimi);
            a.setId(id);
            
            alueet.add(a);
        }
        rs.close();
        stmt.close();
        con.close();
        
        return alueet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Alue> findAllIn(Collection<Integer> keys) throws SQLException {
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder muuttujat = new StringBuilder("?");
        for (int i = 1; i < keys.size(); i++) {
            muuttujat.append(", ?");
        }

        Connection con = database.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Alue WHERE id IN (" + muuttujat + ")");
        int laskuri = 1;
        for (Integer key : keys) {
            stmt.setObject(laskuri, key);
            laskuri++;
        }
        ResultSet rs = stmt.executeQuery();
        
        List<Alue> alueet = new ArrayList<>();
        
        while (rs.next()) {
            int id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            
            Alue a = new Alue(nimi);
            a.setId(id);
            
            alueet.add(a);
        }
        rs.close();
        stmt.close();
        con.close();
        
        return alueet;
    }
}
