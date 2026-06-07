import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginService {


    public String[] authenticateUser(String email, String parola) {
        String queryStudent = "SELECT IDStudent, Parola FROM Student WHERE Email = ?";
        String queryProfesor = "SELECT IDProfesor, Parola FROM Profesor WHERE Email = ?";

        try (Connection conn = DbConnection.getConnection()) {

            String[] result = checkCredentials(conn, queryStudent, email, parola, "Student");
            if (result != null) return result;

            return checkCredentials(conn, queryProfesor, email, parola, "Profesor");

        } catch (SQLException e) {
            System.err.println("Eroare BD la autentificare: " + e.getMessage());
            return null;
        }
    }

    private String[] checkCredentials(Connection conn, String query, String email, String parola, String userType) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getString("Parola").equals(parola)) {
                    String idColumn = (userType.equals("Student") ? "IDStudent" : "IDProfesor");
                    String userId = rs.getString(idColumn);
                    return new String[]{userId, userType};
                }
            }
        }
        return null;
    }


    private int getNextId(Connection conn, String tableName, String idColumn) throws SQLException {
        String query = "SELECT ISNULL(MAX(" + idColumn + "), 0) FROM dbo." + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        }
        return 1;
    }

    public boolean registerUser(String userType, String nume, String prenume, String email, String parola,
                                String anStudiu, String departament, String telefon) throws SQLException {

        String query;
        String tableName = userType.equals("Student") ? "Student" : "Profesor";
        String idColumn = userType.equals("Student") ? "IDStudent" : "IDProfesor";

        try (Connection conn = DbConnection.getConnection()) {

            int nextId = getNextId(conn, tableName, idColumn);

            if ("Student".equals(userType)) {
                query = "INSERT INTO Student (IDStudent, Nume, Prenume, Email, Parola, AnStudiu) VALUES (?, ?, ?, ?, ?, ?)";
            } else if ("Profesor".equals(userType)) {
                query = "INSERT INTO Profesor (IDProfesor, Nume, Prenume, Email, Parola, Departament, Telefon) VALUES (?, ?, ?, ?, ?, ?, ?)";
            } else {
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, nextId);
                pstmt.setString(2, nume);
                pstmt.setString(3, prenume);
                pstmt.setString(4, email);
                pstmt.setString(5, parola);

                if ("Student".equals(userType)) {
                    try {
                        pstmt.setInt(6, Integer.parseInt(anStudiu));
                    } catch (NumberFormatException e) {
                        throw new SQLException("Eroare de format: Anul de studiu trebuie sa fie un numar valid (1-4).", e);
                    }
                } else if ("Profesor".equals(userType)) {
                    pstmt.setString(6, departament);
                    pstmt.setString(7, telefon);
                }

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}