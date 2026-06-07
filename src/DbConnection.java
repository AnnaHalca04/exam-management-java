import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
            "databaseName=Examene si proiecte;" +
            "integratedSecurity=true;" +
            "trustServerCertificate=true;";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}