package practice.pra;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnectionTest {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // MySQL 8.x
    // MySQL 5.x = com.mysql.jdbc.Driver
    private static final String URL = "jdbc:mysql://localhost:3306/springdb";
    private static final String USER = "root";
    private static final String PASSWORD = "0000";

    @Test
    public void testConnection() throws Exception{
        Class.forName(DRIVER);

        try(Connection con = DriverManager.getConnection(URL, USER, PASSWORD)){
            System.out.println(con);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}