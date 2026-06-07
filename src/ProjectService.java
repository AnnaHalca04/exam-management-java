import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectService {

    public boolean isMateriaAssignToProfesor(int idMaterie, int idProfesor) {
        String query = "SELECT COUNT(*) FROM dbo.Materie WHERE IDMaterie = ? AND IDProfesor = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMaterie);
            pstmt.setInt(2, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
//interogare simpla - proiecte profesor
    public List<Object[]> getProjectsForProfesor(int idProfesor) {
        List<Object[]> projects = new ArrayList<>();
        String query = "SELECT p.IDProiect, p.Titlu, p.Deadline, m.Nume AS Materie, p.Cerinte, p.Descriere " +
                "FROM dbo.Proiect p JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                "WHERE m.IDProfesor = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }

    public boolean addProject(String titlu, String descriere, String deadline, String cerinte, int idMaterie) {
        String query = "INSERT INTO dbo.Proiect (Titlu, Descriere, Deadline, Cerinte, IDMaterie) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, titlu);
            pstmt.setString(2, descriere);
            pstmt.setDate(3, Date.valueOf(deadline));
            pstmt.setString(4, cerinte);
            pstmt.setInt(5, idMaterie);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateProject(int idProiect, String titlu, String descriere, String deadline, String cerinte) {
        String query = "UPDATE dbo.Proiect SET Titlu = ?, Descriere = ?, Deadline = ?, Cerinte = ? WHERE IDProiect = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, titlu);
            pstmt.setString(2, descriere);
            pstmt.setDate(3, Date.valueOf(deadline));
            pstmt.setString(4, cerinte);
            pstmt.setInt(5, idProiect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }


//interogare simpla - examene profesor

    public List<Object[]> getExamsForProfesor(int idProfesor) {
        List<Object[]> exams = new ArrayList<>();
        String query = "SELECT e.IDExamen, m.Nume AS Materie, e.Data, e.Ora, s.CodSala, e.Tip, e.Durata, e.IDSala " +
                "FROM dbo.Examen e JOIN dbo.Materie m ON e.IDMaterie = m.IDMaterie " +
                "JOIN dbo.Sala s ON e.IDSala = s.IDSala";

        if (idProfesor > 0) query += " WHERE m.IDProfesor = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (idProfesor > 0) pstmt.setInt(1, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exams.add(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getTime(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getInt(8)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return exams;
    }

    public boolean addExam(String data, String ora, int durata, String sesiune, String tip, int idMaterie, int idSala) {
        if (ora.length() == 5) ora += ":00";
        String query = "INSERT INTO dbo.Examen (Data, Ora, Durata, Sesiune, Tip, IDMaterie, IDSala) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(data));
            pstmt.setTime(2, Time.valueOf(ora));
            pstmt.setInt(3, durata);
            pstmt.setString(4, sesiune);
            pstmt.setString(5, tip);
            pstmt.setInt(6, idMaterie);
            pstmt.setInt(7, idSala);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateExam(int idExamen, String data, String ora, int durata, String tip, int idSala) {
        if (ora.length() == 5) ora += ":00";
        String query = "UPDATE dbo.Examen SET Data = ?, Ora = ?, Durata = ?, Tip = ?, IDSala = ? WHERE IDExamen = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(data));
            pstmt.setTime(2, Time.valueOf(ora));
            pstmt.setInt(3, durata);
            pstmt.setString(4, tip);
            pstmt.setInt(5, idSala);
            pstmt.setInt(6, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteEntity(String tableName, String idColumn, int id) {
        String query = "DELETE FROM dbo." + tableName + " WHERE " + idColumn + " = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> getProjectsForStudent(int idStudent) {
        List<Object[]> projects = new ArrayList<>();
        String query = "SELECT p.IDProiect, p.Titlu, p.Deadline, m.Nume, sp.Status, p.Cerinte, p.Descriere FROM dbo.Proiect p JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie JOIN dbo.StudentProiect sp ON p.IDProiect = sp.IDProiect WHERE sp.IDStudent = ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) projects.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }

    public boolean updateProjectStatus(int idStudent, int idProiect, String noulStatus) {
        String query = "UPDATE dbo.StudentProiect SET Status = ? WHERE IDStudent = ? AND IDProiect = ?";
        try (Connection conn = DbConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, noulStatus); pstmt.setInt(2, idStudent); pstmt.setInt(3, idProiect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

// interogare complexa - profesori performanti
    public List<Object[]> getProfesorsAboveAverageProjects() {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT p.Nume, p.Prenume, p.Departament, COUNT(pr.IDProiect) AS NumarProiecte " +
                        "FROM dbo.Profesor p " +
                        "JOIN dbo.Materie m ON p.IDProfesor = m.IDProfesor " +
                        "LEFT JOIN dbo.Proiect pr ON m.IDMaterie = pr.IDMaterie " +
                        "GROUP BY p.Nume, p.Prenume, p.Departament " +
                        "HAVING COUNT(pr.IDProiect) > ( " +
                        "    SELECT AVG(cnt) FROM ( " +
                        "        SELECT COUNT(*) AS cnt " +
                        "        FROM dbo.Proiect pr2 " +
                        "        JOIN dbo.Materie m2 ON pr2.IDMaterie = m2.IDMaterie " +
                        "        GROUP BY m2.IDProfesor " +
                        "    ) AS subcerere " +
                        ") " +
                        "ORDER BY NumarProiecte DESC";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Departament"),
                        rs.getInt("NumarProiecte")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    //interogare complexa - studenti exemplari

    public List<Object[]> getStudentsWithAllProjectsCompleted() {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT s.Nume, s.Prenume, s.Email, s.AnStudiu, COUNT(sp.IDProiect) AS TotalProiecte " +
                        "FROM dbo.Student s " +
                        "JOIN dbo.StudentProiect sp ON s.IDStudent = sp.IDStudent " +
                        "WHERE NOT EXISTS ( " +
                        "    SELECT 1 FROM dbo.StudentProiect sp2 " +
                        "    WHERE sp2.IDStudent = s.IDStudent " +
                        "    AND sp2.Status != 'Finalizat' " +
                        ") " +
                        "GROUP BY s.Nume, s.Prenume, s.Email, s.AnStudiu " +
                        "ORDER BY s.Nume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getInt("AnStudiu"),
                        rs.getInt("TotalProiecte")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

    //interogare complexa - eficienta salii

    public List<Object[]> getExamsInUnderutilizedRooms() {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT e.IDExamen, m.Nume AS Materie, e.Data, e.Ora, " +
                        "       s.CodSala, s.Capacitate, " +
                        "       (SELECT COUNT(*) FROM dbo.StudentExamen se WHERE se.IDExamen = e.IDExamen) AS StudentiInscrisi " +
                        "FROM dbo.Examen e " +
                        "JOIN dbo.Materie m ON e.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.Sala s ON e.IDSala = s.IDSala " +
                        "WHERE (SELECT COUNT(*) FROM dbo.StudentExamen se WHERE se.IDExamen = e.IDExamen) < (s.Capacitate * 0.5) " +
                        "AND e.Data >= CAST(GETDATE() AS DATE) " +
                        "ORDER BY e.Data";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getInt("IDExamen"),
                        rs.getString("Materie"),
                        rs.getDate("Data"),
                        rs.getTime("Ora"),
                        rs.getString("CodSala"),
                        rs.getInt("Capacitate"),
                        rs.getInt("StudentiInscrisi")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

//interogare complexa (cu parametru) - analiza examenelor
    public List<Object[]> getSubjectsWithMostExamsInYear(int year) {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT m.Nume AS Materie, p.Nume AS Profesor, COUNT(e.IDExamen) AS NumarExamene " +
                        "FROM dbo.Materie m " +
                        "JOIN dbo.Profesor p ON m.IDProfesor = p.IDProfesor " +
                        "JOIN dbo.Examen e ON m.IDMaterie = e.IDMaterie " +
                        "WHERE YEAR(e.Data) = ? " +
                        "GROUP BY m.Nume, p.Nume " +
                        "HAVING COUNT(e.IDExamen) >= ( " +
                        "    SELECT AVG(cnt) FROM ( " +
                        "        SELECT COUNT(*) AS cnt " +
                        "        FROM dbo.Examen e2 " +
                        "        WHERE YEAR(e2.Data) = ? " +
                        "        GROUP BY e2.IDMaterie " +
                        "    ) AS subcerere " +
                        ") " +
                        "ORDER BY NumarExamene DESC";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("Materie"),
                        rs.getString("Profesor"),
                        rs.getInt("NumarExamene")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

//interogare simpla - status proiecte

    public List<Object[]> getProjectStatusStatistics() {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT p.Titlu, m.Nume AS Materie, sp.Status, COUNT(*) AS NumarStudenti " +
                        "FROM dbo.Proiect p " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.StudentProiect sp ON p.IDProiect = sp.IDProiect " +
                        "GROUP BY p.Titlu, m.Nume, sp.Status " +
                        "ORDER BY p.Titlu, sp.Status";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("Titlu"),
                        rs.getString("Materie"),
                        rs.getString("Status"),
                        rs.getInt("NumarStudenti")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }
//interogare simpla - ocupare sali

    public List<Object[]> getExamRoomOccupancy() {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT s.CodSala, s.Capacitate, COUNT(e.IDExamen) AS NumarExamene, " +
                        "       SUM(CASE WHEN e.Data >= CAST(GETDATE() AS DATE) THEN 1 ELSE 0 END) AS ExameneProgramate " +
                        "FROM dbo.Sala s " +
                        "LEFT JOIN dbo.Examen e ON s.IDSala = e.IDSala " +
                        "GROUP BY s.CodSala, s.Capacitate " +
                        "ORDER BY NumarExamene DESC";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("CodSala"),
                        rs.getInt("Capacitate"),
                        rs.getInt("NumarExamene"),
                        rs.getInt("ExameneProgramate")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }

//interogare simpla (cu parametru) - deadline-uri

    public List<Object[]> getUpcomingProjectDeadlines(int days) {
        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT p.Titlu, p.Deadline, m.Nume AS Materie, pr.Nume AS Profesor, " +
                        "       DATEDIFF(DAY, CAST(GETDATE() AS DATE), p.Deadline) AS ZileRamase " +
                        "FROM dbo.Proiect p " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.Profesor pr ON m.IDProfesor = pr.IDProfesor " +
                        "WHERE p.Deadline BETWEEN CAST(GETDATE() AS DATE) AND DATEADD(DAY, ?, CAST(GETDATE() AS DATE)) " +
                        "ORDER BY p.Deadline";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Object[]{
                        rs.getString("Titlu"),
                        rs.getDate("Deadline"),
                        rs.getString("Materie"),
                        rs.getString("Profesor"),
                        rs.getInt("ZileRamase")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }


    public boolean enrollInExam(int idStudent, int idExamen) {
        String checkQuery = "SELECT COUNT(*) FROM dbo.StudentExamen WHERE IDStudent = ? AND IDExamen = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idExamen);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String query = "INSERT INTO dbo.StudentExamen (IDStudent, IDExamen, Status) VALUES (?, ?, 'Inscris')";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean enrollInProject(int idStudent, int idProiect) {
        String checkQuery = "SELECT COUNT(*) FROM dbo.StudentProiect WHERE IDStudent = ? AND IDProiect = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idProiect);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String query = "INSERT INTO dbo.StudentProiect (IDStudent, IDProiect, Status) VALUES (?, ?, 'Neinceput')";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idProiect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean unenrollFromExam(int idStudent, int idExamen) {
        String query = "DELETE FROM dbo.StudentExamen WHERE IDStudent = ? AND IDExamen = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }


    public List<Object[]> getMateriiForProfesor(int idProfesor) {
        List<Object[]> materii = new ArrayList<>();
        String query =
                "SELECT IDMaterie, Nume, AnStudiu, Semestru, NumarCredite " +
                        "FROM dbo.Materie " +
                        "WHERE IDProfesor = ? " +
                        "ORDER BY AnStudiu, Semestru, Nume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materii.add(new Object[]{
                        rs.getInt("IDMaterie"),
                        rs.getString("Nume"),
                        rs.getInt("AnStudiu"),
                        rs.getInt("Semestru"),
                        rs.getInt("NumarCredite")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return materii;
    }

    public boolean addMaterie(String nume, int anStudiu, int semestru, int numarCredite, int idProfesor) {
        String query = "INSERT INTO dbo.Materie (Nume, AnStudiu, Semestru, NumarCredite, IDProfesor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nume);
            pstmt.setInt(2, anStudiu);
            pstmt.setInt(3, semestru);
            pstmt.setInt(4, numarCredite);
            pstmt.setInt(5, idProfesor);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMaterie(int idMaterie, String nume, int anStudiu, int semestru, int numarCredite) {
        String query = "UPDATE dbo.Materie SET Nume = ?, AnStudiu = ?, Semestru = ?, NumarCredite = ? WHERE IDMaterie = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nume);
            pstmt.setInt(2, anStudiu);
            pstmt.setInt(3, semestru);
            pstmt.setInt(4, numarCredite);
            pstmt.setInt(5, idMaterie);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int[] checkMaterieDeleteImpact(int idMaterie) {
        int[] impact = new int[4];

        try (Connection conn = DbConnection.getConnection()) {
            String q1 = "SELECT COUNT(*) FROM dbo.Proiect WHERE IDMaterie = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(q1)) {
                pstmt.setInt(1, idMaterie);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) impact[0] = rs.getInt(1);
            }

            String q2 = "SELECT COUNT(DISTINCT sp.IDStudent) FROM dbo.StudentProiect sp " +
                    "JOIN dbo.Proiect p ON sp.IDProiect = p.IDProiect WHERE p.IDMaterie = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(q2)) {
                pstmt.setInt(1, idMaterie);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) impact[1] = rs.getInt(1);
            }

            String q3 = "SELECT COUNT(*) FROM dbo.Examen WHERE IDMaterie = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(q3)) {
                pstmt.setInt(1, idMaterie);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) impact[2] = rs.getInt(1);
            }

            String q4 = "SELECT COUNT(DISTINCT se.IDStudent) FROM dbo.StudentExamen se " +
                    "JOIN dbo.Examen e ON se.IDExamen = e.IDExamen WHERE e.IDMaterie = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(q4)) {
                pstmt.setInt(1, idMaterie);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) impact[3] = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return impact;
    }

    public boolean deleteMaterieWithCascade(int idMaterie, int idProfesor) {
        String checkQuery = "SELECT COUNT(*) FROM dbo.Materie WHERE IDMaterie = ? AND IDProfesor = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, idMaterie);
            pstmt.setInt(2, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                String q1 = "DELETE FROM dbo.StudentProiect WHERE IDProiect IN " +
                        "(SELECT IDProiect FROM dbo.Proiect WHERE IDMaterie = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(q1)) {
                    pstmt.setInt(1, idMaterie);
                    pstmt.executeUpdate();
                }

                String q2 = "DELETE FROM dbo.Proiect WHERE IDMaterie = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(q2)) {
                    pstmt.setInt(1, idMaterie);
                    pstmt.executeUpdate();
                }

                String q3 = "DELETE FROM dbo.StudentExamen WHERE IDExamen IN " +
                        "(SELECT IDExamen FROM dbo.Examen WHERE IDMaterie = ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(q3)) {
                    pstmt.setInt(1, idMaterie);
                    pstmt.executeUpdate();
                }

                String q4 = "DELETE FROM dbo.Examen WHERE IDMaterie = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(q4)) {
                    pstmt.setInt(1, idMaterie);
                    pstmt.executeUpdate();
                }

                String q5 = "DELETE FROM dbo.Materie WHERE IDMaterie = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(q5)) {
                    pstmt.setInt(1, idMaterie);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMaterie(int idMaterie, int idProfesor) {
        String checkQuery = "SELECT COUNT(*) FROM dbo.Materie WHERE IDMaterie = ? AND IDProfesor = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, idMaterie);
            pstmt.setInt(2, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String query = "DELETE FROM dbo.Materie WHERE IDMaterie = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMaterie);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Object[]> getAllSali() {
        List<Object[]> sali = new ArrayList<>();
        String query =
                "SELECT IDSala, CodSala, Cladire, Etaj, Capacitate " +
                        "FROM dbo.Sala " +
                        "ORDER BY Cladire, Etaj, CodSala";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sali.add(new Object[]{
                        rs.getInt("IDSala"),
                        rs.getString("CodSala"),
                        rs.getString("Cladire"),
                        rs.getObject("Etaj") != null ? rs.getInt("Etaj") : null,
                        rs.getInt("Capacitate")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sali;
    }

    public boolean addSala(String codSala, String cladire, Integer etaj, int capacitate) {
        String query = "INSERT INTO dbo.Sala (CodSala, Cladire, Etaj, Capacitate) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codSala);
            pstmt.setString(2, cladire);
            if (etaj != null) {
                pstmt.setInt(3, etaj);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, capacitate);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSala(int idSala, String codSala, String cladire, Integer etaj, int capacitate) {
        String query = "UPDATE dbo.Sala SET CodSala = ?, Cladire = ?, Etaj = ?, Capacitate = ? WHERE IDSala = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codSala);
            pstmt.setString(2, cladire);
            if (etaj != null) {
                pstmt.setInt(3, etaj);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.setInt(4, capacitate);
            pstmt.setInt(5, idSala);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int checkSalaDeleteImpact(int idSala) {
        String query = "SELECT COUNT(*) FROM dbo.Examen WHERE IDSala = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean deleteSala(int idSala) {
        String query = "DELETE FROM dbo.Sala WHERE IDSala = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSala);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Object[]> getStudentsEnrolledInProject(int idProiect) {
        List<Object[]> students = new ArrayList<>();
        String query =
                "SELECT s.IDStudent, s.Nume, s.Prenume, s.Email, sp.Status, sp.Nota " +
                        "FROM dbo.Student s " +
                        "JOIN dbo.StudentProiect sp ON s.IDStudent = sp.IDStudent " +
                        "WHERE sp.IDProiect = ? " +
                        "ORDER BY s.Nume, s.Prenume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProiect);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Object[]{
                        rs.getInt("IDStudent"),
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getString("Status"),
                        rs.getObject("Nota")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public List<Object[]> getStudentsEnrolledInExam(int idExamen) {
        List<Object[]> students = new ArrayList<>();
        String query =
                "SELECT s.IDStudent, s.Nume, s.Prenume, s.Email, se.Status, se.Nota " +
                        "FROM dbo.Student s " +
                        "JOIN dbo.StudentExamen se ON s.IDStudent = se.IDStudent " +
                        "WHERE se.IDExamen = ? " +
                        "ORDER BY s.Nume, s.Prenume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idExamen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Object[]{
                        rs.getInt("IDStudent"),
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getString("Status"),
                        rs.getObject("Nota")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public boolean assignProjectGrade(int idStudent, int idProiect, double nota) {
        if (nota < 1.0 || nota > 10.0) {
            return false;
        }

        String query = "UPDATE dbo.StudentProiect SET Nota = ? WHERE IDStudent = ? AND IDProiect = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, nota);
            pstmt.setInt(2, idStudent);
            pstmt.setInt(3, idProiect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignProjectGradeAndStatus(int idStudent, int idProiect, double nota, String status) {
        if (nota < 1.0 || nota > 10.0) {
            return false;
        }

        String query = "UPDATE dbo.StudentProiect SET Nota = ?, Status = ? WHERE IDStudent = ? AND IDProiect = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, nota);
            pstmt.setString(2, status);
            pstmt.setInt(3, idStudent);
            pstmt.setInt(4, idProiect);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignExamGrade(int idStudent, int idExamen, double nota) {
        if (nota < 1.0 || nota > 10.0) {
            return false;
        }

        String query = "UPDATE dbo.StudentExamen SET Nota = ? WHERE IDStudent = ? AND IDExamen = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, nota);
            pstmt.setInt(2, idStudent);
            pstmt.setInt(3, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignExamGradeAndStatus(int idStudent, int idExamen, double nota, String status) {
        if (nota < 1.0 || nota > 10.0) {
            return false;
        }

        String query = "UPDATE dbo.StudentExamen SET Nota = ?, Status = ? WHERE IDStudent = ? AND IDExamen = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, nota);
            pstmt.setString(2, status);
            pstmt.setInt(3, idStudent);
            pstmt.setInt(4, idExamen);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getStudentGrades(int idStudent) {
        List<Object[]> grades = new ArrayList<>();

        String queryProiecte =
                "SELECT 'Proiect' AS Tip, p.Titlu AS Nume, m.Nume AS Materie, " +
                        "       sp.Nota, sp.Status, p.Deadline AS Data " +
                        "FROM dbo.StudentProiect sp " +
                        "JOIN dbo.Proiect p ON sp.IDProiect = p.IDProiect " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "WHERE sp.IDStudent = ? AND sp.Nota IS NOT NULL";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryProiecte)) {
            pstmt.setInt(1, idStudent);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(new Object[]{
                        rs.getString("Tip"),
                        rs.getString("Nume"),
                        rs.getString("Materie"),
                        rs.getDouble("Nota"),
                        rs.getString("Status"),
                        rs.getDate("Data")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String queryExamene =
                "SELECT 'Examen' AS Tip, e.Tip + ' - ' + m.Nume AS Nume, m.Nume AS Materie, " +
                        "       se.Nota, se.Status, e.Data " +
                        "FROM dbo.StudentExamen se " +
                        "JOIN dbo.Examen e ON se.IDExamen = e.IDExamen " +
                        "JOIN dbo.Materie m ON e.IDMaterie = m.IDMaterie " +
                        "WHERE se.IDStudent = ? AND se.Nota IS NOT NULL";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryExamene)) {
            pstmt.setInt(1, idStudent);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                grades.add(new Object[]{
                        rs.getString("Tip"),
                        rs.getString("Nume"),
                        rs.getString("Materie"),
                        rs.getDouble("Nota"),
                        rs.getString("Status"),
                        rs.getDate("Data")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return grades;
    }


    public Object[] getStudentProfile(int idStudent) {
        String query = "SELECT Nume, Prenume, Email, AnStudiu FROM dbo.Student WHERE IDStudent = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Object[]{
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getInt("AnStudiu")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateStudentProfile(int idStudent, String nume, String prenume, String email, String parola, int anStudiu) {
        String query;
        if (parola == null || parola.trim().isEmpty()) {
            query = "UPDATE dbo.Student SET Nume = ?, Prenume = ?, Email = ?, AnStudiu = ? WHERE IDStudent = ?";
        } else {
            query = "UPDATE dbo.Student SET Nume = ?, Prenume = ?, Email = ?, Parola = ?, AnStudiu = ? WHERE IDStudent = ?";
        }

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, email);

            if (parola == null || parola.trim().isEmpty()) {
                pstmt.setInt(4, anStudiu);
                pstmt.setInt(5, idStudent);
            } else {
                pstmt.setString(4, parola);
                pstmt.setInt(5, anStudiu);
                pstmt.setInt(6, idStudent);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object[] getProfesorProfile(int idProfesor) {
        String query = "SELECT Nume, Prenume, Email, Departament, Telefon FROM dbo.Profesor WHERE IDProfesor = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idProfesor);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Object[]{
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getString("Email"),
                        rs.getString("Departament"),
                        rs.getString("Telefon")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateProfesorProfile(int idProfesor, String nume, String prenume, String email, String parola, String departament, String telefon) {
        String query;
        if (parola == null || parola.trim().isEmpty()) {
            query = "UPDATE dbo.Profesor SET Nume = ?, Prenume = ?, Email = ?, Departament = ?, Telefon = ? WHERE IDProfesor = ?";
        } else {
            query = "UPDATE dbo.Profesor SET Nume = ?, Prenume = ?, Email = ?, Parola = ?, Departament = ?, Telefon = ? WHERE IDProfesor = ?";
        }

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nume);
            pstmt.setString(2, prenume);
            pstmt.setString(3, email);

            if (parola == null || parola.trim().isEmpty()) {
                pstmt.setString(4, departament);
                pstmt.setString(5, telefon);
                pstmt.setInt(6, idProfesor);
            } else {
                pstmt.setString(4, parola);
                pstmt.setString(5, departament);
                pstmt.setString(6, telefon);
                pstmt.setInt(7, idProfesor);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Object[]> getProjectsByYear(int anStudiu) {
        List<Object[]> projects = new ArrayList<>();
        String query =
                "SELECT p.IDProiect, p.Titlu, p.Deadline, m.Nume AS Materie, p.Cerinte, p.Descriere " +
                        "FROM dbo.Proiect p " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "WHERE m.AnStudiu = ? " +
                        "ORDER BY p.Deadline, m.Nume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, anStudiu);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Object[]{
                        rs.getInt("IDProiect"),
                        rs.getString("Titlu"),
                        rs.getDate("Deadline"),
                        rs.getString("Materie"),
                        rs.getString("Cerinte"),
                        rs.getString("Descriere")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }

    public List<Object[]> getExamsByYear(int anStudiu) {
        List<Object[]> exams = new ArrayList<>();
        String query =
                "SELECT e.IDExamen, m.Nume AS Materie, e.Data, e.Ora, s.CodSala, e.Tip " +
                        "FROM dbo.Examen e " +
                        "JOIN dbo.Materie m ON e.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.Sala s ON e.IDSala = s.IDSala " +
                        "WHERE m.AnStudiu = ? " +
                        "ORDER BY e.Data, e.Ora";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, anStudiu);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exams.add(new Object[]{
                        rs.getInt("IDExamen"),
                        rs.getString("Materie"),
                        rs.getDate("Data"),
                        rs.getTime("Ora"),
                        rs.getString("CodSala"),
                        rs.getString("Tip")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return exams;
    }

    public List<Object[]> getMyProjectsByYear(int idStudent, int anStudiu) {
        List<Object[]> projects = new ArrayList<>();
        String query =
                "SELECT p.IDProiect, p.Titlu, p.Deadline, m.Nume AS Materie, sp.Status, p.Cerinte, p.Descriere " +
                        "FROM dbo.Proiect p " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.StudentProiect sp ON p.IDProiect = sp.IDProiect " +
                        "WHERE sp.IDStudent = ? AND m.AnStudiu = ? " +
                        "ORDER BY p.Deadline";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            pstmt.setInt(2, anStudiu);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Object[]{
                        rs.getInt("IDProiect"),
                        rs.getString("Titlu"),
                        rs.getDate("Deadline"),
                        rs.getString("Materie"),
                        rs.getString("Status"),
                        rs.getString("Cerinte"),
                        rs.getString("Descriere")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }

//interogare simpla cu parametru - examene per materie

    public List<Object[]> getExamsBySubject(int idMaterie) {
        List<Object[]> exams = new ArrayList<>();
        String query =
                "SELECT e.IDExamen, m.Nume AS Materie, e.Data, e.Ora, s.CodSala, e.Tip, e.Durata " +
                        "FROM dbo.Examen e " +
                        "JOIN dbo.Materie m ON e.IDMaterie = m.IDMaterie " +
                        "JOIN dbo.Sala s ON e.IDSala = s.IDSala " +
                        "WHERE m.IDMaterie = ? " +
                        "ORDER BY e.Data, e.Ora";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMaterie);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                exams.add(new Object[]{
                        rs.getInt("IDExamen"),
                        rs.getString("Materie"),
                        rs.getDate("Data"),
                        rs.getTime("Ora"),
                        rs.getString("CodSala"),
                        rs.getString("Tip"),
                        rs.getInt("Durata")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return exams;
    }

    //interogare simpla cu parametru - proiecte per materie

    public List<Object[]> getProjectsBySubject(int idMaterie) {
        List<Object[]> projects = new ArrayList<>();
        String query =
                "SELECT p.IDProiect, p.Titlu, p.Deadline, m.Nume AS Materie, p.Cerinte, p.Descriere " +
                        "FROM dbo.Proiect p " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "WHERE m.IDMaterie = ? " +
                        "ORDER BY p.Deadline";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idMaterie);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Object[]{
                        rs.getInt("IDProiect"),
                        rs.getString("Titlu"),
                        rs.getDate("Deadline"),
                        rs.getString("Materie"),
                        rs.getString("Cerinte"),
                        rs.getString("Descriere")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return projects;
    }

    //interogare complexa cu parametru - studenti performanti ani

    public List<Object[]> getStudentsAboveYearAverage(int anStudiu) {
        List<Object[]> students = new ArrayList<>();
        String query =
                "SELECT s.Nume, s.Prenume, s.AnStudiu, AVG(sp.Nota) AS MediaStudent " +
                        "FROM dbo.Student s " +
                        "JOIN dbo.StudentProiect sp ON s.IDStudent = sp.IDStudent " +
                        "WHERE s.AnStudiu = ? " +
                        "  AND sp.Nota IS NOT NULL " +
                        "GROUP BY s.Nume, s.Prenume, s.AnStudiu " +
                        "HAVING AVG(sp.Nota) > ( " +
                        "    SELECT AVG(sp2.Nota) " +
                        "    FROM dbo.StudentProiect sp2 " +
                        "    JOIN dbo.Student s2 ON sp2.IDStudent = s2.IDStudent " +
                        "    WHERE s2.AnStudiu = ? " +
                        "      AND sp2.Nota IS NOT NULL " +
                        ") " +
                        "ORDER BY MediaStudent DESC";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, anStudiu);
            pstmt.setInt(2, anStudiu);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Object[]{
                        rs.getString("Nume"),
                        rs.getString("Prenume"),
                        rs.getInt("AnStudiu"),
                        rs.getDouble("MediaStudent")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return students;
    }

    public List<Object[]> getAllMaterii() {
        List<Object[]> materii = new ArrayList<>();
        String query =
                "SELECT IDMaterie, Nume, AnStudiu, Semestru " +
                        "FROM dbo.Materie " +
                        "ORDER BY AnStudiu, Semestru, Nume";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materii.add(new Object[]{
                        rs.getInt("IDMaterie"),
                        rs.getString("Nume"),
                        rs.getInt("AnStudiu"),
                        rs.getInt("Semestru")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return materii;
    }
}