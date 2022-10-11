import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnection {
    public static Connection createConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://192.168.3.113:3306/minerals",
                    "dba", "aloangy"
            );
        }
        catch (SQLException e){
            System.out.println("Error");
        }
        return null;
    }
}
