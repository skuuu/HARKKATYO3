
package tikape.harjoitustyokaksi;

import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.sql.*;


public class KysymysDao implements Dao <Kysymys, Integer> {
    private Database db;
    
    public KysymysDao (Database db){
        this.db = db;
    }
    

    @Override
    public Kysymys findOne(Integer key) throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT FROM Kysymys WHERE id=?");
        stmt.setInt(0, key);
        
        ResultSet rs = stmt.executeQuery();
        
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
            
        }

        Kysymys teht = new Kysymys(rs.getString("kysymysteksti"));

        stmt.close();
        rs.close();

        con.close();

        return teht;
        
    }
     

    @Override
    public List<Kysymys> findAll() throws SQLException {
        Connection con = db.getConnection();
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM Kysymys");
        
        ResultSet rs = stmt.executeQuery();
        List <Kysymys> kysymykset = new ArrayList<>();

        while (rs.next()){
            Kysymys kysymys = new Kysymys(rs.getString("kysymysteksti"));
            kysymys.setId(Integer.parseInt(rs.getString("id")));
            kysymys.setAihe(rs.getString("aihe"));
            kysymys.setKurssi(rs.getString("kurssi"));
            kysymykset.add(kysymys);
        }
        
        stmt.close();
        rs.close();

        con.close();

        return kysymykset;
    }

   

    @Override
    public void delete(Integer id) throws SQLException {
        if (id==0){
            System.out.println("mallikysymystä ei voi poistaa");;
        }else{
            Connection conn = db.getConnection();

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Kysymys"
                    + " WHERE id = ?");

            stmt.setInt(1, id);



            stmt.executeUpdate();
            stmt.close();

            conn.close(); 
        }
    }
    
    public Kysymys save(Kysymys kysymys)throws SQLException{
        Connection conn = db.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kysymys"
                + " (kysymysteksti, aihe, kurssi)"
                + " VALUES (?,?,?)");
        
        stmt.setString(1, kysymys.getKysymysteksti());
        stmt.setString(2, kysymys.getAihe());
        stmt.setString(3, kysymys.getKurssi());
        
        stmt.executeUpdate();
        stmt.close();
        
        //laitetaan id: 
        stmt = conn.prepareStatement("SELECT * FROM Kysymys"
                + " WHERE kysymysteksti = ?");
        stmt.setString(1, kysymys.getKysymysteksti());

        ResultSet rs = stmt.executeQuery();
        rs.next(); // vain 1 tulos

        Kysymys a = new Kysymys(rs.getString("kysymysteksti"));

        stmt.close();
        rs.close();

        conn.close();

        return a;
        
        
    }
}
