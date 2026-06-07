import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public static void main(String[] args) {
    try (Connection conn = DbConnection.getConnection()) {
        if (conn != null) {
            System.out.println("Conexiune la baza de date reusita!");
        } else {
            System.err.println("Conexiune esuata. Verificati setarile.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}