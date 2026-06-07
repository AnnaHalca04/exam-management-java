import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDashboard extends DashboardGUI {
    private JTable currentTable;
    private ProjectService service = new ProjectService();

    public ProfesorDashboard(String userId) {
        super(userId, "Profesor");
        initMenu();
        showProjectManagement();
    }

    private void initMenu() {
        JButton btnProjects = createMenuButton("Gestionare Proiecte");
        JButton btnExams = createMenuButton("Gestionare Examene");
        JButton btnMaterii = createMenuButton("Gestionare Materii");
        JButton btnSali = createMenuButton("Gestionare Săli");
        JButton btnStatistics = createMenuButton("Statistici");
        JButton btnProfile = createMenuButton("Profilul Meu");
        JButton btnLogout = createMenuButton("Logout");

        btnProjects.addActionListener(e -> showProjectManagement());
        btnExams.addActionListener(e -> showExamManagement());
        btnMaterii.addActionListener(e -> showMateriiManagement());
        btnSali.addActionListener(e -> showSaliManagement());
        btnStatistics.addActionListener(e -> showStatistics());
        btnProfile.addActionListener(e -> showProfile());

        btnLogout.addActionListener(e -> {
            new WelcomeGUI().setVisible(true);
            dispose();
        });

        sideMenu.add(btnProjects);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnExams);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnMaterii);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnSali);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnStatistics);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnProfile);
        sideMenu.add(Box.createVerticalGlue());
        sideMenu.add(btnLogout);
    }


    private void showProjectManagement() {
        headerTitle.setText("  Gestionare Proiecte");
        List<Object[]> data = service.getProjectsForProfesor(Integer.parseInt(userId));
        String[] cols = {"ID", "Titlu", "Deadline", "Materie", "Cerințe", "Descriere"};
        updateContentTable("Proiect", cols, data);
    }

    private void showExamManagement() {
        headerTitle.setText("  Gestionare Examene");
        List<Object[]> data = service.getExamsForProfesor(Integer.parseInt(userId));
        String[] cols = {"ID", "Materie", "Data", "Ora", "Sala", "Tip", "Durata (min)", "IDSala"};
        updateContentTable("Examen", cols, data);

        if (currentTable.getColumnCount() > 6) {
            currentTable.removeColumn(currentTable.getColumnModel().getColumn(currentTable.getColumnCount() - 1));
        }
    }


    private void updateContentTable(String type, String[] cols, List<Object[]> data) {
        contentPanel.removeAll();

        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);
        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(C_LIGHT);

        JButton btnAdd = new JButton("Adaugă " + type);
        JButton btnEdit = new JButton("Modifică " + type);
        JButton btnDelete = new JButton("Șterge " + type);
        JButton btnViewStudents = new JButton("Vezi Studenți");

        btnEdit.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                if (type.equals("Proiect")) openEditProjectDialog(model, row);
                else openEditExamDialog(model, row);
            } else {
                JOptionPane.showMessageDialog(this, "Selectați un rând pentru a-l modifica!");
            }
        });

        btnAdd.addActionListener(e -> {
            if (type.equals("Proiect")) openAddProjectDialog();
            else openAddExamDialog();
        });

        btnDelete.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                String idColName = type.equals("Proiect") ? "IDProiect" : "IDExamen";
                if (service.deleteEntity(type, idColName, id)) {
                    JOptionPane.showMessageDialog(this, type + " a fost șters!");
                    if(type.equals("Proiect")) showProjectManagement(); else showExamManagement();
                }
            }
        });

        btnViewStudents.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int id = (int) model.getValueAt(row, 0);
                String title = (String) model.getValueAt(row, 1);
                if (type.equals("Proiect")) {
                    openStudentsProjectDialog(id, title);
                } else {
                    openStudentsExamDialog(id, title);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați un " + type.toLowerCase() + " pentru a vedea studenții!");
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewStudents);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);
        contentPanel.revalidate(); contentPanel.repaint();
    }


    private void openEditProjectDialog(DefaultTableModel model, int row) {
        int idProiect = (int) model.getValueAt(row, 0);
        String titluVechi = (String) model.getValueAt(row, 1);
        String deadlineVechi = model.getValueAt(row, 2).toString();
        String materieVeche = (String) model.getValueAt(row, 3);
        String cerinteVechi = (String) model.getValueAt(row, 4);
        String descriereVeche = (String) model.getValueAt(row, 5);

        JDialog dialog = new JDialog(this, "Modifică Proiect", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JTextField txtTitlu = new JTextField(titluVechi);
        JTextField txtDeadline = new JTextField(deadlineVechi);
        JTextField txtMaterie = new JTextField(materieVeche);
        txtMaterie.setEditable(false);
        JTextArea txtCerinte = new JTextArea(cerinteVechi, 3, 20);
        JTextArea txtDescriere = new JTextArea(descriereVeche, 3, 20);

        dialog.add(new JLabel(" Titlu Nou:")); dialog.add(txtTitlu);
        dialog.add(new JLabel(" Deadline Nou:")); dialog.add(txtDeadline);
        dialog.add(new JLabel(" Materie (Info):")); dialog.add(txtMaterie);
        dialog.add(new JLabel(" Cerințe Noi:")); dialog.add(new JScrollPane(txtCerinte));
        dialog.add(new JLabel(" Descriere Nouă:")); dialog.add(new JScrollPane(txtDescriere));

        JButton btnUpdate = new JButton("Salvează Modificările");
        btnUpdate.addActionListener(e -> {
            try {
                java.sql.Date deadline = java.sql.Date.valueOf(txtDeadline.getText());
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                if (deadline.before(today)) {
                    JOptionPane.showMessageDialog(dialog, " Data nu poate fi în trecut!", "Eroare", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (service.updateProject(idProiect, txtTitlu.getText(), txtDescriere.getText(),
                        txtDeadline.getText(), txtCerinte.getText())) {
                    JOptionPane.showMessageDialog(dialog, "✓ Proiect actualizat cu succes!");
                    dialog.dispose();
                    showProjectManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la actualizarea în baza de date!");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Data invalidă! Format: YYYY-MM-DD");
            }
        });

        dialog.add(new JLabel("")); dialog.add(btnUpdate);
        dialog.setVisible(true);
    }

    private void openAddProjectDialog() {
        JDialog dialog = new JDialog(this, "Adaugă Proiect Nou", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtTitlu = new JTextField();
        JTextArea txtDesc = new JTextArea(3, 20);
        JTextField txtDeadline = new JTextField("2026-05-15");
        JTextArea txtCerinte = new JTextArea(4, 20);

        JComboBox<String> comboMaterii = new JComboBox<>();
        List<Object[]> materiiProfesor = service.getMateriiForProfesor(Integer.parseInt(userId));
        for (Object[] mat : materiiProfesor) {
            int id = (int) mat[0];
            String nume = (String) mat[1];
            int an = (int) mat[2];
            int sem = (int) mat[3];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ", Sem " + sem + ")");
        }

        inputPanel.add(new JLabel(" Titlu:")); inputPanel.add(txtTitlu);
        inputPanel.add(new JLabel(" Descriere:")); inputPanel.add(new JScrollPane(txtDesc));
        inputPanel.add(new JLabel(" Deadline (YYYY-MM-DD):")); inputPanel.add(txtDeadline);
        inputPanel.add(new JLabel(" Materie:")); inputPanel.add(comboMaterii);
        inputPanel.add(new JLabel(" Cerințe:")); inputPanel.add(new JScrollPane(txtCerinte));

        JButton btnSave = new JButton("Salvează Proiect");
        btnSave.addActionListener(e -> {
            try {
                java.sql.Date deadline = java.sql.Date.valueOf(txtDeadline.getText());
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                if (deadline.before(today)) {
                    JOptionPane.showMessageDialog(dialog, " Data nu poate fi în trecut!", "Eroare", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (comboMaterii.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(dialog, "Selectați o materie!");
                    return;
                }

                int idMat = (int) materiiProfesor.get(comboMaterii.getSelectedIndex())[0];

                if (service.addProject(txtTitlu.getText(), txtDesc.getText(),
                        txtDeadline.getText(), txtCerinte.getText(), idMat)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Proiect adăugat cu succes!");
                    dialog.dispose();
                    showProjectManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la salvare!");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Data invalidă! Format: YYYY-MM-DD");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Date invalide!");
            }
        });

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(btnSave, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void openAddExamDialog() {
        JDialog dialog = new JDialog(this, "Adaugă Examen Nou", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JTextField txtData = new JTextField("2026-06-10");
        JTextField txtOra = new JTextField("10:00");
        JTextField txtDurata = new JTextField("120");
        JTextField txtSesiune = new JTextField("Vara 2026");
        String[] tipuri = {"Final", "Partial", "Restante"};
        JComboBox<String> cbTip = new JComboBox<>(tipuri);

        JComboBox<String> comboMaterii = new JComboBox<>();
        List<Object[]> materiiProfesor = service.getMateriiForProfesor(Integer.parseInt(userId));
        for (Object[] mat : materiiProfesor) {
            int id = (int) mat[0];
            String nume = (String) mat[1];
            int an = (int) mat[2];
            int sem = (int) mat[3];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ", Sem " + sem + ")");
        }

        JComboBox<String> comboSali = new JComboBox<>();
        List<Object[]> sali = service.getAllSali();
        for (Object[] sala : sali) {
            int id = (int) sala[0];
            String cod = (String) sala[1];
            String cladire = (String) sala[2];
            int capacitate = (int) sala[4];
            comboSali.addItem(id + " - " + cod + " (" + cladire + ", " + capacitate + " locuri)");
        }

        dialog.add(new JLabel(" Data (YYYY-MM-DD):")); dialog.add(txtData);
        dialog.add(new JLabel(" Ora (HH:mm):")); dialog.add(txtOra);
        dialog.add(new JLabel(" Durata (min):")); dialog.add(txtDurata);
        dialog.add(new JLabel(" Sesiune:")); dialog.add(txtSesiune);
        dialog.add(new JLabel(" Tip:")); dialog.add(cbTip);
        dialog.add(new JLabel(" Materie:")); dialog.add(comboMaterii);
        dialog.add(new JLabel(" Sală:")); dialog.add(comboSali);

        JButton btnSave = new JButton("Salvează Examen");
        btnSave.addActionListener(e -> {
            try {
                java.sql.Date dataExamen = java.sql.Date.valueOf(txtData.getText());
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                if (dataExamen.before(today)) {
                    JOptionPane.showMessageDialog(dialog, " Data nu poate fi în trecut!", "Eroare", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String sesiune = txtSesiune.getText();
                int currentYear = java.time.Year.now().getValue();
                if (sesiune.matches(".*\\d{4}.*")) {
                    String yearStr = sesiune.replaceAll("\\D", "");
                    if (!yearStr.isEmpty()) {
                        int sesiuneYear = Integer.parseInt(yearStr.substring(0, 4));
                        if (sesiuneYear < currentYear) {
                            JOptionPane.showMessageDialog(dialog, " Sesiunea nu poate fi în trecut! (Anul curent: " + currentYear + ")",
                                    "Eroare", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                if (comboMaterii.getSelectedIndex() < 0 || comboSali.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(dialog, "Selectați materia și sala!");
                    return;
                }

                int idMat = (int) materiiProfesor.get(comboMaterii.getSelectedIndex())[0];
                int idSala = (int) sali.get(comboSali.getSelectedIndex())[0];

                boolean ok = service.addExam(txtData.getText(), txtOra.getText(),
                        Integer.parseInt(txtDurata.getText()),
                        txtSesiune.getText(), (String)cbTip.getSelectedItem(),
                        idMat, idSala);

                if(ok) {
                    JOptionPane.showMessageDialog(dialog, "✓ Examen adăugat cu succes!");
                    dialog.dispose();
                    showExamManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la salvare!");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Data invalidă! Format: YYYY-MM-DD");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Date invalide! Verificați câmpurile.");
            }
        });

        dialog.add(new JLabel("")); dialog.add(btnSave);
        dialog.setVisible(true);
    }
    private void openEditExamDialog(DefaultTableModel model, int row) {
        int idExamen = (int) model.getValueAt(row, 0);
        String dataCurenta = model.getValueAt(row, 2).toString();
        String oraCurenta = model.getValueAt(row, 3).toString().substring(0, 5);
        String tipCurent = (String) model.getValueAt(row, 5);
        String durataCurenta = model.getValueAt(row, 6).toString();
        int idSalaCurent = Integer.parseInt(model.getValueAt(row, 7).toString());

        JDialog dialog = new JDialog(this, "Modifică Examen", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextField txtData = new JTextField(dataCurenta);
        JTextField txtOra = new JTextField(oraCurenta);
        JTextField txtDurata = new JTextField(durataCurenta);
        JComboBox<String> cbTip = new JComboBox<>(new String[]{"Final", "Partial", "Restante"});
        cbTip.setSelectedItem(tipCurent);

        JComboBox<String> comboSali = new JComboBox<>();
        List<Object[]> sali = service.getAllSali();
        int selectedSalaIndex = 0;
        for (int i = 0; i < sali.size(); i++) {
            Object[] sala = sali.get(i);
            int id = (int) sala[0];
            String cod = (String) sala[1];
            String cladire = (String) sala[2];
            int capacitate = (int) sala[4];
            comboSali.addItem(id + " - " + cod + " (" + cladire + ", " + capacitate + " locuri)");
            if (id == idSalaCurent) {
                selectedSalaIndex = i;
            }
        }
        comboSali.setSelectedIndex(selectedSalaIndex);

        dialog.add(new JLabel(" Data (YYYY-MM-DD):")); dialog.add(txtData);
        dialog.add(new JLabel(" Ora (HH:mm):")); dialog.add(txtOra);
        dialog.add(new JLabel(" Durata (min):")); dialog.add(txtDurata);
        dialog.add(new JLabel(" Tip:")); dialog.add(cbTip);
        dialog.add(new JLabel(" Sală:")); dialog.add(comboSali);

        JButton btnSave = new JButton("Salvează Modificările");
        btnSave.addActionListener(e -> {
            try {
                java.sql.Date dataExamen = java.sql.Date.valueOf(txtData.getText());
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

                if (dataExamen.before(today)) {
                    JOptionPane.showMessageDialog(dialog, " Data nu poate fi în trecut!", "Eroare", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (comboSali.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(dialog, "Selectați o sală!");
                    return;
                }

                int idSala = (int) sali.get(comboSali.getSelectedIndex())[0];

                if(service.updateExam(idExamen, txtData.getText(), txtOra.getText(),
                        Integer.parseInt(txtDurata.getText()), (String)cbTip.getSelectedItem(), idSala)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Examen modificat cu succes!");
                    dialog.dispose();
                    showExamManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la salvare!");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Data invalidă! Format: YYYY-MM-DD");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Date invalide!");
            }
        });

        dialog.add(new JLabel("")); dialog.add(btnSave);
        dialog.setVisible(true);
    }


    private void showStatistics() {
        headerTitle.setText("  Statistici și Rapoarte Avansate");
        contentPanel.removeAll();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("Profesori Performanți", createProfesorsAboveAveragePanel());

        tabbedPane.addTab("Studenți Exemplari", createStudentsCompletedPanel());

        tabbedPane.addTab("Eficiență Săli", createUnderutilizedRoomsPanel());

        tabbedPane.addTab("Analiza Examenelor", createSubjectsExamsPanel());

        tabbedPane.addTab("Status Proiecte", createProjectStatusPanel());

        tabbedPane.addTab("Ocupare Săli", createRoomOccupancyPanel());

        tabbedPane.addTab("Deadline-uri Apropiate", createUpcomingDeadlinesPanel());

        tabbedPane.addTab("Studenți-Proiecte", createStudentProjectsPanel());

        tabbedPane.addTab("Examene per Materie", createExamsBySubjectPanel());

        tabbedPane.addTab("Proiecte per Materie", createProjectsBySubjectPanel());

        tabbedPane.addTab("Studenți Performanți An", createStudentsAboveAveragePanel());

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    //interogare complexa
    private JPanel createProfesorsAboveAveragePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Profesori cu mai multe proiecte decât media");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> data = service.getProfesorsAboveAverageProjects();
        String[] cols = {"Nume", "Prenume", "Departament", "Nr. Proiecte"};
        JTable table = new JTable(data.toArray(new Object[0][]), cols);



        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare complexa
    private JPanel createStudentsCompletedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Studenți cu toate proiectele finalizate");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> data = service.getStudentsWithAllProjectsCompleted();
        String[] cols = {"Nume", "Prenume", "Email", "An Studiu", "Total Proiecte"};
        JTable table = new JTable(data.toArray(new Object[0][]), cols);


        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare complexa
    private JPanel createUnderutilizedRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Examene în săli subutilizate (<50% capacitate)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> data = service.getExamsInUnderutilizedRooms();
        String[] cols = {"ID", "Materie", "Data", "Ora", "Sala", "Capacitate", "Înscriși"};
        JTable table = new JTable(data.toArray(new Object[0][]), cols);
        table.removeColumn(table.getColumnModel().getColumn(0));


        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare complexa
    private JPanel createSubjectsExamsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Materii cu cele mai multe examene în anul curent");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("An:"));
        JTextField txtYear = new JTextField("2025", 5);
        topPanel.add(txtYear);
        JButton btnSearch = new JButton("Căutare");
        topPanel.add(btnSearch);

        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Materie", "Profesor", "Nr. Examene"}, 0);
        JTable table = new JTable(model);

        btnSearch.addActionListener(e -> {
            try {
                int year = Integer.parseInt(txtYear.getText());
                List<Object[]> data = service.getSubjectsWithMostExamsInYear(year);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Anul trebuie să fie un număr!");
            }
        });

        List<Object[]> initialData = service.getSubjectsWithMostExamsInYear(2025);
        for (Object[] row : initialData) {
            model.addRow(row);
        }


        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare simpla
    private JPanel createProjectStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Statistici pe statusul proiectelor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> data = service.getProjectStatusStatistics();
        String[] cols = {"Titlu Proiect", "Materie", "Status", "Nr. Studenți"};
        JTable table = new JTable(data.toArray(new Object[0][]), cols);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare simpla
    private JPanel createRoomOccupancyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(" Statistici ocupare săli pentru examene");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> data = service.getExamRoomOccupancy();
        String[] cols = {"Cod Sala", "Capacitate", "Total Examene", "Examene Programate"};
        JTable table = new JTable(data.toArray(new Object[0][]), cols);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare simpla
    private JPanel createUpcomingDeadlinesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Proiecte cu deadline apropiat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Următoarele"));
        JTextField txtDays = new JTextField("30", 5);
        topPanel.add(txtDays);
        topPanel.add(new JLabel("zile"));
        JButton btnSearch = new JButton("Căutare");
        topPanel.add(btnSearch);

        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Titlu", "Deadline", "Materie", "Profesor", "Zile Rămase"}, 0);
        JTable table = new JTable(model);

        btnSearch.addActionListener(e -> {
            try {
                int days = Integer.parseInt(txtDays.getText());
                List<Object[]> data = service.getUpcomingProjectDeadlines(days);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Numărul de zile trebuie să fie valid!");
            }
        });

        List<Object[]> initialData = service.getUpcomingProjectDeadlines(30);
        for (Object[] row : initialData) {
            model.addRow(row);
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare simpla
    //interogare simpla
    private JPanel createStudentProjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Relația Student-Proiect (pentru toate materiile)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        List<Object[]> results = new ArrayList<>();
        String query =
                "SELECT s.Nume AS NumeStudent, s.Prenume AS PrenumeStudent, " +
                        "       p.Titlu, m.Nume AS Materie, sp.Status, sp.Nota " +
                        "FROM dbo.Student s " +
                        "JOIN dbo.StudentProiect sp ON s.IDStudent = sp.IDStudent " +
                        "JOIN dbo.Proiect p ON sp.IDProiect = p.IDProiect " +
                        "JOIN dbo.Materie m ON p.IDMaterie = m.IDMaterie " +
                        "WHERE m.IDProfesor = ? " +
                        "ORDER BY s.Nume, p.Titlu";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object nota = rs.getObject("Nota");
                results.add(new Object[]{
                        rs.getString("NumeStudent"),
                        rs.getString("PrenumeStudent"),
                        rs.getString("Titlu"),
                        rs.getString("Materie"),
                        rs.getString("Status"),
                        nota != null ? nota : "-"
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String[] cols = {"Nume Student", "Prenume", "Titlu Proiect", "Materie", "Status", "Nota"};
        JTable table = new JTable(results.toArray(new Object[0][]), cols);


        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }


    private void showMateriiManagement() {
        headerTitle.setText("  Gestionare Materii");
        contentPanel.removeAll();

        List<Object[]> data = service.getMateriiForProfesor(Integer.parseInt(userId));
        String[] cols = {"ID", "Nume Materie", "An Studiu", "Semestru", "Credite ECTS"};
        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(C_LIGHT);

        JButton btnAdd = new JButton("Adaugă Materie");
        JButton btnEdit = new JButton("Modifică Materie");
        JButton btnDelete = new JButton("Șterge Materie");

        btnAdd.addActionListener(e -> openAddMaterieDialog());

        btnEdit.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                openEditMaterieDialog(model, row);
            } else {
                JOptionPane.showMessageDialog(this, "Selectați o materie pentru a o modifica!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int idMaterie = (int) model.getValueAt(row, 0);
                String numeMaterie = (String) model.getValueAt(row, 1);

                int[] impact = service.checkMaterieDeleteImpact(idMaterie);
                int nrProiecte = impact[0];
                int nrStudentiProiecte = impact[1];
                int nrExamene = impact[2];
                int nrStudentiExamene = impact[3];

                StringBuilder warning = new StringBuilder();
                warning.append("Sigur doriți să ștergeți materia \"").append(numeMaterie).append("\"?\n\n");

                if (nrProiecte == 0 && nrExamene == 0) {
                    warning.append("✓ Materia nu are proiecte sau examene asociate.\n");
                    warning.append("Ștergerea este sigură și nu va afecta studenții.");

                    int confirm = JOptionPane.showConfirmDialog(this,
                            warning.toString(),
                            "Confirmare ștergere",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (service.deleteMaterie(idMaterie, Integer.parseInt(userId))) {
                            JOptionPane.showMessageDialog(this, "✓ Materie ștearsă cu succes!");
                            showMateriiManagement();
                        } else {
                            JOptionPane.showMessageDialog(this, "Eroare la ștergere!", "Eroare", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    warning.append(" ATENȚIE - Vor fi șterse AUTOMAT:\n\n");

                    if (nrProiecte > 0) {
                        warning.append("🔸 ").append(nrProiecte).append(" proiect");
                        warning.append(nrProiecte > 1 ? "e" : "").append("\n");
                        if (nrStudentiProiecte > 0) {
                            warning.append("   (").append(nrStudentiProiecte).append(" student");
                            warning.append(nrStudentiProiecte > 1 ? "i înscriși" : " înscris").append(")\n");
                        }
                    }

                    if (nrExamene > 0) {
                        warning.append("🔸 ").append(nrExamene).append(" examen");
                        warning.append(nrExamene > 1 ? "e" : "").append("\n");
                        if (nrStudentiExamene > 0) {
                            warning.append("   (").append(nrStudentiExamene).append(" student");
                            warning.append(nrStudentiExamene > 1 ? "i înscriși" : " înscris").append(")\n");
                        }
                    }

                    warning.append("\n Această operație NU poate fi anulată!\n");
                    warning.append("Continuați cu ștergerea în cascadă?");

                    int confirm = JOptionPane.showConfirmDialog(this,
                            warning.toString(),
                            "Confirmare ștergere în cascadă",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (service.deleteMaterieWithCascade(idMaterie, Integer.parseInt(userId))) {
                            JOptionPane.showMessageDialog(this,
                                    "✓ Materie și toate datele asociate au fost șterse cu succes!",
                                    "Ștergere completă",
                                    JOptionPane.INFORMATION_MESSAGE);
                            showMateriiManagement();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Eroare la ștergerea în cascadă!\nOperațiunea a fost anulată.",
                                    "Eroare",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați o materie pentru a o șterge!");
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void openAddMaterieDialog() {
        JDialog dialog = new JDialog(this, "Adaugă Materie Nouă", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JTextField txtNume = new JTextField();
        JTextField txtAnStudiu = new JTextField("1");
        JTextField txtSemestru = new JTextField("1");
        JTextField txtCredite = new JTextField("5");

        dialog.add(new JLabel(" Nume Materie:"));
        dialog.add(txtNume);
        dialog.add(new JLabel(" An Studiu (1-4):"));
        dialog.add(txtAnStudiu);
        dialog.add(new JLabel(" Semestru (1-2):"));
        dialog.add(txtSemestru);
        dialog.add(new JLabel(" Număr Credite ECTS:"));
        dialog.add(txtCredite);

        JButton btnSave = new JButton("Salvează Materia");
        btnSave.addActionListener(e -> {
            try {
                String nume = txtNume.getText().trim();
                int anStudiu = Integer.parseInt(txtAnStudiu.getText());
                int semestru = Integer.parseInt(txtSemestru.getText());
                int credite = Integer.parseInt(txtCredite.getText());

                if (nume.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Numele materiei nu poate fi gol!");
                    return;
                }
                if (anStudiu < 1 || anStudiu > 4) {
                    JOptionPane.showMessageDialog(dialog, "Anul de studiu trebuie să fie între 1 și 4!");
                    return;
                }
                if (semestru < 1 || semestru > 2) {
                    JOptionPane.showMessageDialog(dialog, "Semestrul trebuie să fie 1 sau 2!");
                    return;
                }
                if (credite <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Numărul de credite trebuie să fie pozitiv!");
                    return;
                }

                if (service.addMaterie(nume, anStudiu, semestru, credite, Integer.parseInt(userId))) {
                    JOptionPane.showMessageDialog(dialog, "✓ Materie adăugată cu succes!");
                    dialog.dispose();
                    showMateriiManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la adăugarea materiei în baza de date!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vă rugăm introduceți valori numerice valide!");
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void openEditMaterieDialog(DefaultTableModel model, int row) {
        int idMaterie = (int) model.getValueAt(row, 0);
        String numeVechi = (String) model.getValueAt(row, 1);
        int anVechi = (int) model.getValueAt(row, 2);
        int semestruVechi = (int) model.getValueAt(row, 3);
        int crediteVechi = (int) model.getValueAt(row, 4);

        JDialog dialog = new JDialog(this, "Modifică Materie", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JTextField txtNume = new JTextField(numeVechi);
        JTextField txtAnStudiu = new JTextField(String.valueOf(anVechi));
        JTextField txtSemestru = new JTextField(String.valueOf(semestruVechi));
        JTextField txtCredite = new JTextField(String.valueOf(crediteVechi));

        dialog.add(new JLabel(" Nume Materie:"));
        dialog.add(txtNume);
        dialog.add(new JLabel(" An Studiu (1-4):"));
        dialog.add(txtAnStudiu);
        dialog.add(new JLabel(" Semestru (1-2):"));
        dialog.add(txtSemestru);
        dialog.add(new JLabel(" Număr Credite ECTS:"));
        dialog.add(txtCredite);

        JButton btnUpdate = new JButton("Salvează Modificările");
        btnUpdate.addActionListener(e -> {
            try {
                String nume = txtNume.getText().trim();
                int anStudiu = Integer.parseInt(txtAnStudiu.getText());
                int semestru = Integer.parseInt(txtSemestru.getText());
                int credite = Integer.parseInt(txtCredite.getText());

                if (nume.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Numele materiei nu poate fi gol!");
                    return;
                }
                if (anStudiu < 1 || anStudiu > 4) {
                    JOptionPane.showMessageDialog(dialog, "Anul de studiu trebuie să fie între 1 și 4!");
                    return;
                }
                if (semestru < 1 || semestru > 2) {
                    JOptionPane.showMessageDialog(dialog, "Semestrul trebuie să fie 1 sau 2!");
                    return;
                }
                if (credite <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Numărul de credite trebuie să fie pozitiv!");
                    return;
                }

                if (service.updateMaterie(idMaterie, nume, anStudiu, semestru, credite)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Materie actualizată cu succes!");
                    dialog.dispose();
                    showMateriiManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la actualizarea materiei!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vă rugăm introduceți valori numerice valide!");
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnUpdate);
        dialog.setVisible(true);
    }


    private void openStudentsProjectDialog(int idProiect, String titluProiect) {
        JDialog dialog = new JDialog(this, "Studenți înscriși la: " + titluProiect, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        List<Object[]> students = service.getStudentsEnrolledInProject(idProiect);

        if (students.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            JLabel emptyLabel = new JLabel("Niciun student nu este înscris la acest proiect.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            dialog.add(emptyPanel);
            dialog.setVisible(true);
            return;
        }

        String[] cols = {"IDStudent", "Nume", "Prenume", "Email", "Status", "Nota"};
        DefaultTableModel model = new DefaultTableModel(students.toArray(new Object[0][]), cols);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Atribuire Notă și Status"));

        JLabel lblNota = new JLabel("Nota (1-10):");
        JTextField txtNota = new JTextField(5);

        JLabel lblStatus = new JLabel("Status:");
        String[] statusOptions = {"Neînceput", "În lucru", "Finalizat"};
        JComboBox<String> comboStatus = new JComboBox<>(statusOptions);

        JButton btnAssignGrade = new JButton("Atribuie Nota + Status");

        btnAssignGrade.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Selectați un student din tabel!");
                return;
            }

            try {
                double nota = Double.parseDouble(txtNota.getText());

                if (nota < 1.0 || nota > 10.0) {
                    JOptionPane.showMessageDialog(dialog, "Nota trebuie să fie între 1 și 10!");
                    return;
                }

                int idStudent = (int) model.getValueAt(row, 0);
                String numeStudent = model.getValueAt(row, 1) + " " + model.getValueAt(row, 2);
                String selectedStatus = (String) comboStatus.getSelectedItem();

                if (service.assignProjectGradeAndStatus(idStudent, idProiect, nota, selectedStatus)) {
                    JOptionPane.showMessageDialog(dialog,
                            "✓ Nota " + nota + " și status '" + selectedStatus + "' atribuite pentru " + numeStudent + "!");
                    model.setValueAt(selectedStatus, row, 4);
                    model.setValueAt(nota, row, 5);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la atribuirea notei!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Introduceți o valoare numerică validă pentru notă!");
            }
        });

        gradePanel.add(lblNota);
        gradePanel.add(txtNota);
        gradePanel.add(lblStatus);
        gradePanel.add(comboStatus);
        gradePanel.add(btnAssignGrade);

        JButton btnClose = new JButton("Închide");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(gradePanel, BorderLayout.CENTER);
        bottomPanel.add(btnClose, BorderLayout.SOUTH);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openStudentsExamDialog(int idExamen, String numeExamen) {
        JDialog dialog = new JDialog(this, "Studenți înscriși la examen: " + numeExamen, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        List<Object[]> students = service.getStudentsEnrolledInExam(idExamen);

        if (students.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            JLabel emptyLabel = new JLabel("Niciun student nu este înscris la acest examen.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            dialog.add(emptyPanel);
            dialog.setVisible(true);
            return;
        }

        String[] cols = {"IDStudent", "Nume", "Prenume", "Email", "Status", "Nota"};
        DefaultTableModel model = new DefaultTableModel(students.toArray(new Object[0][]), cols);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        JPanel gradePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gradePanel.setBorder(BorderFactory.createTitledBorder("Atribuire Notă și Status"));

        JLabel lblNota = new JLabel("Nota (1-10):");
        JTextField txtNota = new JTextField(5);

        JLabel lblStatus = new JLabel("Status:");
        String[] statusOptions = {"Inscris", "Susținut"};
        JComboBox<String> comboStatus = new JComboBox<>(statusOptions);

        JButton btnAssignGrade = new JButton("Atribuie Nota + Status");

        btnAssignGrade.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Selectați un student din tabel!");
                return;
            }

            try {
                double nota = Double.parseDouble(txtNota.getText());

                if (nota < 1.0 || nota > 10.0) {
                    JOptionPane.showMessageDialog(dialog, "Nota trebuie să fie între 1 și 10!");
                    return;
                }

                int idStudent = (int) model.getValueAt(row, 0);
                String numeStudent = model.getValueAt(row, 1) + " " + model.getValueAt(row, 2);
                String selectedStatus = (String) comboStatus.getSelectedItem();

                if (service.assignExamGradeAndStatus(idStudent, idExamen, nota, selectedStatus)) {
                    JOptionPane.showMessageDialog(dialog,
                            "✓ Nota " + nota + " și status '" + selectedStatus + "' atribuite pentru " + numeStudent + "!");
                    model.setValueAt(selectedStatus, row, 4);
                    model.setValueAt(nota, row, 5);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la atribuirea notei!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Introduceți o valoare numerică validă pentru notă!");
            }
        });

        gradePanel.add(lblNota);
        gradePanel.add(txtNota);
        gradePanel.add(lblStatus);
        gradePanel.add(comboStatus);
        gradePanel.add(btnAssignGrade);
        gradePanel.add(btnAssignGrade);

        JButton btnClose = new JButton("Închide");
        btnClose.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(gradePanel, BorderLayout.CENTER);
        bottomPanel.add(btnClose, BorderLayout.SOUTH);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }


    private void showSaliManagement() {
        headerTitle.setText("  Gestionare Săli");
        contentPanel.removeAll();

        List<Object[]> data = service.getAllSali();
        String[] cols = {"ID", "Cod Sală", "Clădire", "Etaj", "Capacitate"};
        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(C_LIGHT);

        JButton btnAdd = new JButton("Adaugă Sală");
        JButton btnEdit = new JButton("Modifică Sală");
        JButton btnDelete = new JButton("Șterge Sală");

        btnAdd.addActionListener(e -> openAddSalaDialog());

        btnEdit.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                openEditSalaDialog(model, row);
            } else {
                JOptionPane.showMessageDialog(this, "Selectați o sală pentru a o modifica!");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int idSala = (int) model.getValueAt(row, 0);
                String codSala = (String) model.getValueAt(row, 1);

                int nrExamene = service.checkSalaDeleteImpact(idSala);

                StringBuilder warning = new StringBuilder();
                warning.append("Sigur doriți să ștergeți sala \"").append(codSala).append("\"?\n\n");

                if (nrExamene == 0) {
                    warning.append("✓ Sala nu are examene programate.\n");
                    warning.append("Ștergerea este sigură.");
                } else {
                    warning.append(" ATENȚIE: Sala are ").append(nrExamene);
                    warning.append(" examen").append(nrExamene > 1 ? "e" : "").append(" programat");
                    warning.append(nrExamene > 1 ? "e" : "").append("!\n\n");
                    warning.append("Nu se poate șterge sala cu examene programate.\n");
                    warning.append("Ștergeți sau mutați examenele mai întâi.");

                    JOptionPane.showMessageDialog(this,
                            warning.toString(),
                            "Eroare - Sală în folosință",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        warning.toString(),
                        "Confirmare ștergere",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (service.deleteSala(idSala)) {
                        JOptionPane.showMessageDialog(this, "✓ Sală ștearsă cu succes!");
                        showSaliManagement();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Eroare la ștergere!",
                                "Eroare",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați o sală pentru a o șterge!");
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(btnPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void openAddSalaDialog() {
        JDialog dialog = new JDialog(this, "Adaugă Sală Nouă", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JTextField txtCodSala = new JTextField();
        JTextField txtCladire = new JTextField();
        JTextField txtEtaj = new JTextField();
        JTextField txtCapacitate = new JTextField("30");

        dialog.add(new JLabel(" Cod Sală (ex: C309, Amf A):"));
        dialog.add(txtCodSala);
        dialog.add(new JLabel(" Clădire:"));
        dialog.add(txtCladire);
        dialog.add(new JLabel(" Etaj (opțional):"));
        dialog.add(txtEtaj);
        dialog.add(new JLabel(" Capacitate (locuri):"));
        dialog.add(txtCapacitate);

        JButton btnSave = new JButton("Salvează Sala");
        btnSave.addActionListener(e -> {
            try {
                String codSala = txtCodSala.getText().trim();
                String cladire = txtCladire.getText().trim();
                String etajStr = txtEtaj.getText().trim();
                Integer etaj = etajStr.isEmpty() ? null : Integer.parseInt(etajStr);
                int capacitate = Integer.parseInt(txtCapacitate.getText());

                if (codSala.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Codul sălii nu poate fi gol!");
                    return;
                }
                if (cladire.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Clădirea nu poate fi goală!");
                    return;
                }
                if (capacitate <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Capacitatea trebuie să fie pozitivă!");
                    return;
                }

                if (service.addSala(codSala, cladire, etaj, capacitate)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Sală adăugată cu succes!");
                    dialog.dispose();
                    showSaliManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Eroare: Codul sălii există deja sau date invalide!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vă rugăm introduceți valori numerice valide!");
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void openEditSalaDialog(DefaultTableModel model, int row) {
        int idSala = (int) model.getValueAt(row, 0);
        String codVechi = (String) model.getValueAt(row, 1);
        String cladireVeche = (String) model.getValueAt(row, 2);
        Object etajObj = model.getValueAt(row, 3);
        Integer etajVechi = etajObj != null ? (Integer) etajObj : null;
        int capacitateVeche = (int) model.getValueAt(row, 4);

        JDialog dialog = new JDialog(this, "Modifică Sală", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JTextField txtCodSala = new JTextField(codVechi);
        JTextField txtCladire = new JTextField(cladireVeche);
        JTextField txtEtaj = new JTextField(etajVechi != null ? String.valueOf(etajVechi) : "");
        JTextField txtCapacitate = new JTextField(String.valueOf(capacitateVeche));

        dialog.add(new JLabel(" Cod Sală:"));
        dialog.add(txtCodSala);
        dialog.add(new JLabel(" Clădire:"));
        dialog.add(txtCladire);
        dialog.add(new JLabel(" Etaj (opțional):"));
        dialog.add(txtEtaj);
        dialog.add(new JLabel(" Capacitate (locuri):"));
        dialog.add(txtCapacitate);

        JButton btnUpdate = new JButton("Salvează Modificările");
        btnUpdate.addActionListener(e -> {
            try {
                String codSala = txtCodSala.getText().trim();
                String cladire = txtCladire.getText().trim();
                String etajStr = txtEtaj.getText().trim();
                Integer etaj = etajStr.isEmpty() ? null : Integer.parseInt(etajStr);
                int capacitate = Integer.parseInt(txtCapacitate.getText());

                if (codSala.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Codul sălii nu poate fi gol!");
                    return;
                }
                if (cladire.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Clădirea nu poate fi goală!");
                    return;
                }
                if (capacitate <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Capacitatea trebuie să fie pozitivă!");
                    return;
                }

                if (service.updateSala(idSala, codSala, cladire, etaj, capacitate)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Sală actualizată cu succes!");
                    dialog.dispose();
                    showSaliManagement();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la actualizarea sălii!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vă rugăm introduceți valori numerice valide!");
            }
        });

        dialog.add(new JLabel(""));
        dialog.add(btnUpdate);
        dialog.setVisible(true);
    }


    //interogare simpla
    private JPanel createExamsBySubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Examene pentru o Materie (Parametru Variabil)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Selectează materia:"));

        JComboBox<String> comboMaterii = new JComboBox<>();
        List<Object[]> materii = service.getAllMaterii();
        for (Object[] materie : materii) {
            int id = (int) materie[0];
            String nume = (String) materie[1];
            int an = (int) materie[2];
            int sem = (int) materie[3];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ", Sem " + sem + ")");
        }
        topPanel.add(comboMaterii);

        JButton btnSearch = new JButton("Caută Examene");
        topPanel.add(btnSearch);

        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Materie", "Data", "Ora", "Sala", "Tip", "Durata"}, 0);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        btnSearch.addActionListener(e -> {
            if (comboMaterii.getSelectedIndex() >= 0) {
                int idMaterie = (int) materii.get(comboMaterii.getSelectedIndex())[0];
                List<Object[]> data = service.getExamsBySubject(idMaterie);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }
            }
        });



        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare simpla
    private JPanel createProjectsBySubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Proiecte pentru o Materie (Parametru Variabil)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Selectează materia:"));

        JComboBox<String> comboMaterii = new JComboBox<>();
        List<Object[]> materii = service.getAllMaterii();
        for (Object[] materie : materii) {
            int id = (int) materie[0];
            String nume = (String) materie[1];
            int an = (int) materie[2];
            int sem = (int) materie[3];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ", Sem " + sem + ")");
        }
        topPanel.add(comboMaterii);

        JButton btnSearch = new JButton("Caută Proiecte");
        topPanel.add(btnSearch);

        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Titlu", "Deadline", "Materie", "Cerințe", "Descriere"}, 0);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        btnSearch.addActionListener(e -> {
            if (comboMaterii.getSelectedIndex() >= 0) {
                int idMaterie = (int) materii.get(comboMaterii.getSelectedIndex())[0];
                List<Object[]> data = service.getProjectsBySubject(idMaterie);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }
            }
        });



        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //interogare complexa
    private JPanel createStudentsAboveAveragePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Studenți cu Medie peste Media Anului (Parametru în Subcerere)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Selectează anul:"));

        String[] ani = {"An 1", "An 2", "An 3", "An 4"};
        JComboBox<String> comboAn = new JComboBox<>(ani);
        topPanel.add(comboAn);

        JButton btnSearch = new JButton("Caută Studenți");
        topPanel.add(btnSearch);

        panel.add(topPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Nume", "Prenume", "An", "Medie"}, 0);
        JTable table = new JTable(model);

        btnSearch.addActionListener(e -> {
            int anStudiu = comboAn.getSelectedIndex() + 1;
            List<Object[]> data = service.getStudentsAboveYearAverage(anStudiu);
            model.setRowCount(0);
            for (Object[] row : data) {
                model.addRow(row);
            }

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Nu există studenți cu medie peste media anului " + anStudiu +
                                " sau nu există note atribuite.",
                        "Informație",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }


    private void showProfile() {
        headerTitle.setText("  Profilul Meu");

        Object[] profile = service.getProfesorProfile(Integer.parseInt(userId));

        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea profilului!");
            return;
        }

        String nume = (String) profile[0];
        String prenume = (String) profile[1];
        String email = (String) profile[2];
        String departament = (String) profile[3];
        String telefon = (String) profile[4];

        JDialog dialog = new JDialog(this, "Modificare Profil", true);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JTextField txtNume = new JTextField(nume);
        JTextField txtPrenume = new JTextField(prenume);
        JTextField txtEmail = new JTextField(email);
        JPasswordField txtParola = new JPasswordField();
        JTextField txtDepartament = new JTextField(departament != null ? departament : "");
        JTextField txtTelefon = new JTextField(telefon != null ? telefon : "");

        dialog.add(new JLabel(" Nume:"));
        dialog.add(txtNume);
        dialog.add(new JLabel(" Prenume:"));
        dialog.add(txtPrenume);
        dialog.add(new JLabel(" Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel(" Parolă nouă (opțional):"));
        dialog.add(txtParola);
        dialog.add(new JLabel(" Departament:"));
        dialog.add(txtDepartament);
        dialog.add(new JLabel(" Telefon:"));
        dialog.add(txtTelefon);

        JLabel lblInfo = new JLabel("<html><i>Lăsați parola goală pentru a o păstra pe cea curentă</i></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.add(lblInfo);

        dialog.add(infoPanel);
        dialog.add(new JLabel(""));

        JButton btnSave = new JButton("Salvează Modificările");
        btnSave.addActionListener(e -> {
            String newNume = txtNume.getText().trim();
            String newPrenume = txtPrenume.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newParola = new String(txtParola.getPassword()).trim();
            String newDepartament = txtDepartament.getText().trim();
            String newTelefon = txtTelefon.getText().trim();

            if (newNume.isEmpty() || newPrenume.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Numele și prenumele nu pot fi goale!");
                return;
            }
            if (newEmail.isEmpty() || !newEmail.contains("@")) {
                JOptionPane.showMessageDialog(dialog, "Email invalid!");
                return;
            }

            if (service.updateProfesorProfile(Integer.parseInt(userId), newNume, newPrenume, newEmail,
                    newParola.isEmpty() ? null : newParola, newDepartament, newTelefon)) {
                JOptionPane.showMessageDialog(dialog, "✓ Profil actualizat cu succes!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Eroare la actualizarea profilului!\nVerificați dacă email-ul nu este deja folosit.");
            }
        });

        dialog.add(btnSave);
        dialog.setVisible(true);
    }
}