package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbConnection {
    public static Connection createConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mariadb://172.30.195.219:3306/minerals",
                    "dba", "aloangy"
            );
        }
        catch (SQLException e){
            System.out.println("Error");
        }
        return null;
    }
}
