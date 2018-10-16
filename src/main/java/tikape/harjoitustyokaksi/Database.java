
package tikape.harjoitustyokaksi;
import java.sql.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(databaseAddress);
//    }
    
    
    public Connection getConnection() throws SQLException {
    String dbUrl = System.getenv("JDBC_DATABASE_URL");
        if (dbUrl != null && dbUrl.length() > 0) {
            return DriverManager.getConnection(dbUrl);
        }

        return DriverManager.getConnection(databaseAddress);
    }

}
