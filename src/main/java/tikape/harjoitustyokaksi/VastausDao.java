
package tikape.harjoitustyokaksi;

import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.sql.*;

public class VastausDao implements Dao <Vastaus, Integer> {
    private Database db;
    
    public VastausDao (Database db){
        this.db = db;
    }
    

    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT FROM Vastaus WHERE id=?");
        stmt.setInt(1, key);
        
        
        ResultSet rs = stmt.executeQuery();
        
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Vastaus vastaus = new Vastaus(rs.getString("vastausteksti"));

        stmt.close();
        rs.close();

        con.close();

        return vastaus;
        
    }

    //muokkaa niin, että palauttaa vain yhden kysymyksen vastaukset, 
    // eli ne vastaukset, joiden kysymys_id=kysymys.id
    @Override
    public List<Vastaus> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Vastaus");
        
        ResultSet rs = stmt.executeQuery();
        List <Vastaus> vastaukset = new ArrayList<>();

        while (rs.next()){
            Vastaus vastaus = new Vastaus(rs.getString("vastausteksti"));
            vastaus.setKysymys(Integer.parseInt(rs.getString("kysymys_id")));
            vastaus.setOikein(rs.getBoolean("oikein"));
            vastaus.setId(Integer.parseInt(rs.getString("id")));
            vastaukset.add(vastaus);
        }
        
        stmt.close();
        rs.close();

        con.close();

        return vastaukset;
    }

    @Override
    public Vastaus saveOrUpdate(Vastaus vastaus) throws SQLException {
        if (vastaus.id==null){
            return save(vastaus);
        }else{
            return update(vastaus);
        }
    }
    public List<Vastaus> etsikysymyksenvastaukset(Integer kysymys_id) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Vastaus WHERE vastaus.kysymys_id=?");
        stmt.setInt(kysymys_id, kysymys_id);

                
        ResultSet rs = stmt.executeQuery();
        List <Vastaus> vastaukset = new ArrayList<>();

        while (rs.next()){
            Vastaus vastaus = new Vastaus(rs.getString("vastausteksti"));
            vastaukset.add(vastaus);
        }
        
        stmt.close();
        rs.close();

        con.close();

        return vastaukset;
    }
    
    
    
    //poistaa halutun vastauksen id:n perusteella:
    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = db.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Vastaus"
                + " WHERE id = ?");
        
        stmt.setInt(1, key);
        
        
        
        stmt.executeUpdate();
        stmt.close();
        
        conn.close();        
    }
    
    public Vastaus save(Vastaus vastaus)throws SQLException{
        Connection conn = db.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Vastaus"
                + " (kysymys_id, oikein, vastausteksti)"
                + " VALUES (?,?,?)");
        
        stmt.setInt(1, (vastaus.getKysymys()));
        stmt.setBoolean(2, vastaus.getOikein());
        stmt.setString(3, vastaus.getVastausteksti());
        
        
        stmt.executeUpdate();
        stmt.close();
        
        //laitetaan id: 
        stmt = conn.prepareStatement("SELECT * FROM Vastaus"
                + " WHERE vastausteksti = ?");
        stmt.setString(1, vastaus.getVastausteksti());

        ResultSet rs = stmt.executeQuery();
        rs.next(); // vain 1 tulos

        Vastaus a = new Vastaus(rs.getString("vastausteksti"));

        stmt.close();
        rs.close();

        conn.close();

        return a;
        
        
    }
    public Vastaus update(Vastaus vastaus) throws SQLException{
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("UPDATE Vastaus SET"
                + " vastausteksti = ? WHERE id = ?");
        stmt.setString(1, vastaus.getVastausteksti());
        stmt.setInt(2, vastaus.getId());

        stmt.executeUpdate();

        stmt.close();
        con.close();

        return vastaus;
    }
    
}

