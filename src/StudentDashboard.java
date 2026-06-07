import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends DashboardGUI {
    private JTable currentTable;
    private ProjectService service = new ProjectService();

    public StudentDashboard(String userId) {
        super(userId, "Student");
        initMenu();
        showMyProjects();
    }

    private void initMenu() {
        JButton btnProjects = createMenuButton("Proiectele Mele");
        JButton btnAllProjects = createMenuButton("Înscriere Proiecte");
        JButton btnAllExams = createMenuButton("Înscriere Examene");
        JButton btnGrades = createMenuButton("Notele Mele");
        JButton btnExamsBySubject = createMenuButton("Examene Materie");
        JButton btnProjectsBySubject = createMenuButton("Proiecte Materie");
        JButton btnProfile = createMenuButton("Profilul Meu");
        JButton btnLogout = createMenuButton("Logout");

        btnProjects.addActionListener(e -> showMyProjects());
        btnAllProjects.addActionListener(e -> showAvailableProjects());
        btnAllExams.addActionListener(e -> showAvailableExams());
        btnGrades.addActionListener(e -> showMyGrades());
        btnExamsBySubject.addActionListener(e -> showExamsBySubject());
        btnProjectsBySubject.addActionListener(e -> showProjectsBySubject());
        btnProfile.addActionListener(e -> showProfile());

        btnLogout.addActionListener(e -> {
            new WelcomeGUI().setVisible(true);
            dispose();
        });

        sideMenu.add(btnProjects);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnAllProjects);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnAllExams);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnGrades);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnExamsBySubject);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnProjectsBySubject);
        sideMenu.add(Box.createVerticalStrut(10));
        sideMenu.add(btnProfile);
        sideMenu.add(Box.createVerticalGlue());
        sideMenu.add(btnLogout);
    }

    private void showMyProjects() {
        headerTitle.setText("  Proiectele Mele (Anul Curent)");

        Object[] profile = service.getStudentProfile(Integer.parseInt(userId));
        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea profilului!");
            return;
        }
        int anStudiu = (int) profile[3];

        List<Object[]> filteredData = service.getMyProjectsByYear(Integer.parseInt(userId), anStudiu);

        String[] cols = {"ID", "Titlu", "Deadline", "Materie", "Status", "Cerințe", "Descriere"};

        contentPanel.removeAll();

        if (filteredData.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "<html><center>Nu ești înscris la niciun proiect din anul " + anStudiu + ".<br>" +
                            "Mergi la 'Înscriere Proiecte' pentru a te înscrie.</center></html>",
                    SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            contentPanel.add(emptyLabel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        DefaultTableModel model = new DefaultTableModel(filteredData.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(C_LIGHT);

        String[] statusuri = {"Neînceput", "În lucru", "Finalizat"};
        JComboBox<String> statusCombo = new JComboBox<>(statusuri);
        JButton btnUpdate = new JButton("Actualizează Status");

        JLabel lblInfo = new JLabel(" Proiecte din anul " + anStudiu);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnUpdate.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int idProiect = (int) model.getValueAt(row, 0);
                String status = (String) statusCombo.getSelectedItem();
                if (service.updateProjectStatus(Integer.parseInt(userId), idProiect, status)) {
                    JOptionPane.showMessageDialog(this, "Statusul a fost actualizat în baza de date!");
                    showMyProjects();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați un proiect din tabel!");
            }
        });

        actionPanel.add(lblInfo);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(new JLabel("Schimbă Status:"));
        actionPanel.add(statusCombo);
        actionPanel.add(btnUpdate);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAvailableExams() {
        headerTitle.setText("  Examene Disponibile pentru Înscriere (Anul Meu)");

        Object[] profile = service.getStudentProfile(Integer.parseInt(userId));
        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea profilului!");
            return;
        }
        int anStudiu = (int) profile[3];

        List<Object[]> data = service.getExamsByYear(anStudiu);
        String[] cols = {"ID", "Materie", "Data", "Ora", "Sala", "Tip"};

        contentPanel.removeAll();

        if (data.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "<html><center>Nu există examene disponibile pentru anul " + anStudiu + ".<br>" +
                            "Verificați mai târziu sau contactați profesorii.</center></html>",
                    SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            contentPanel.add(emptyLabel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(C_LIGHT);

        JLabel lblInfo = new JLabel(" Afișare examene pentru anul " + anStudiu);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionPanel.add(lblInfo);

        JButton btnEnroll = new JButton("Înscrie-te acum");
        btnEnroll.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int idExamen = (int) model.getValueAt(row, 0);

                if (service.enrollInExam(Integer.parseInt(userId), idExamen)) {
                    JOptionPane.showMessageDialog(this, "✓ Înscris cu succes la examen!");
                    showAvailableExams();
                } else {
                    JOptionPane.showMessageDialog(this, " Eroare: Poate ești deja înscris sau examenul nu mai este disponibil!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați un examen!");
            }
        });
        actionPanel.add(btnEnroll);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void renderTable(String[] cols, List<Object[]> data, boolean isProjectMode) {
        contentPanel.removeAll();
        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(C_LIGHT);

        if (isProjectMode) {
            String[] statusuri = {"Neînceput", "În lucru", "Finalizat"};
            JComboBox<String> statusCombo = new JComboBox<>(statusuri);
            JButton btnUpdate = new JButton("Actualizează Status");

            btnUpdate.addActionListener(e -> {
                int row = currentTable.getSelectedRow();
                if (row != -1) {
                    int idProiect = (int) model.getValueAt(row, 0);
                    String status = (String) statusCombo.getSelectedItem();
                    if (service.updateProjectStatus(Integer.parseInt(userId), idProiect, status)) {
                        JOptionPane.showMessageDialog(this, "Statusul a fost actualizat în baza de date!");
                        showMyProjects();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Selectați un proiect din tabel!");
                }
            });
            actionPanel.add(new JLabel("Schimbă Status:"));
            actionPanel.add(statusCombo);
            actionPanel.add(btnUpdate);
        } else {
            JButton btnEnroll = new JButton("Înscrie-te acum");
            btnEnroll.addActionListener(e -> {
                int row = currentTable.getSelectedRow();
                if (row != -1) {
                    int idExamen = (int) model.getValueAt(row, 0);

                    if (service.enrollInExam(Integer.parseInt(userId), idExamen)) {
                        JOptionPane.showMessageDialog(this, "✓ Înscris cu succes la examen!");
                        showAvailableExams();
                    } else {
                        JOptionPane.showMessageDialog(this, " Eroare: Poate ești deja înscris sau examenul nu mai este disponibil!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Selectați un examen!");
                }
            });
            actionPanel.add(btnEnroll);
        }

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAvailableProjects() {
        headerTitle.setText("  Proiecte Disponibile pentru Înscriere (Anul Meu)");

        Object[] profile = service.getStudentProfile(Integer.parseInt(userId));
        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea profilului!");
            return;
        }
        int anStudiu = (int) profile[3];

        List<Object[]> data = service.getProjectsByYear(anStudiu);
        String[] cols = {"ID", "Titlu", "Deadline", "Materie", "Cerințe", "Descriere"};

        contentPanel.removeAll();

        if (data.isEmpty()) {
            JLabel emptyLabel = new JLabel(
                    "<html><center>Nu există proiecte disponibile pentru anul " + anStudiu + ".<br>" +
                            "Verificați mai târziu sau contactați profesorii.</center></html>",
                    SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            contentPanel.add(emptyLabel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        DefaultTableModel model = new DefaultTableModel(data.toArray(new Object[0][]), cols);
        currentTable = new JTable(model);

        currentTable.removeColumn(currentTable.getColumnModel().getColumn(0));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(C_LIGHT);

        JLabel lblInfo = new JLabel(" Afișare proiecte pentru anul " + anStudiu);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionPanel.add(lblInfo);

        JButton btnEnroll = new JButton("Înscrie-te la Proiect");
        btnEnroll.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            if (row != -1) {
                int idProiect = (int) model.getValueAt(row, 0);

                if (service.enrollInProject(Integer.parseInt(userId), idProiect)) {
                    JOptionPane.showMessageDialog(this, "✓ Înscris cu succes la proiect!");
                    showAvailableProjects();
                } else {
                    JOptionPane.showMessageDialog(this, " Eroare: Poate ești deja înscris la acest proiect!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectați un proiect!");
            }
        });
        actionPanel.add(btnEnroll);

        contentPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyGrades() {
        headerTitle.setText("  Notele Mele");
        contentPanel.removeAll();

        List<Object[]> grades = service.getStudentGrades(Integer.parseInt(userId));

        if (grades.isEmpty()) {
            JLabel emptyLabel = new JLabel("Nu aveți încă note atribuite.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            contentPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            String[] cols = {"Tip", "Nume", "Materie", "Nota", "Status", "Data"};
            DefaultTableModel model = new DefaultTableModel(grades.toArray(new Object[0][]), cols);
            JTable table = new JTable(model);

            double suma = 0;
            int count = 0;
            for (Object[] grade : grades) {
                if (grade[3] != null) {
                    suma += (Double) grade[3];
                    count++;
                }
            }
            double medie = count > 0 ? suma / count : 0;

            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            statsPanel.setBackground(C_LIGHT);
            statsPanel.setBorder(BorderFactory.createTitledBorder("Statistici"));

            JLabel lblTotalNote = new JLabel("Total note: " + count);
            lblTotalNote.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JLabel lblMedie = new JLabel(String.format("Media generală: %.2f", medie));
            lblMedie.setFont(new Font("Segoe UI", Font.BOLD, 14));
            if (medie >= 5.0) {
                lblMedie.setForeground(new Color(0, 128, 0));
            } else {
                lblMedie.setForeground(Color.RED);
            }

            statsPanel.add(lblTotalNote);
            statsPanel.add(lblMedie);

            contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            contentPanel.add(statsPanel, BorderLayout.SOUTH);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfile() {
        headerTitle.setText("  Profilul Meu");

        Object[] profile = service.getStudentProfile(Integer.parseInt(userId));

        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Eroare la încărcarea profilului!");
            return;
        }

        String nume = (String) profile[0];
        String prenume = (String) profile[1];
        String email = (String) profile[2];
        int anStudiu = (int) profile[3];

        JDialog dialog = new JDialog(this, "Modificare Profil", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextField txtNume = new JTextField(nume);
        JTextField txtPrenume = new JTextField(prenume);
        JTextField txtEmail = new JTextField(email);
        JPasswordField txtParola = new JPasswordField();
        JTextField txtAnStudiu = new JTextField(String.valueOf(anStudiu));

        dialog.add(new JLabel(" Nume:"));
        dialog.add(txtNume);
        dialog.add(new JLabel(" Prenume:"));
        dialog.add(txtPrenume);
        dialog.add(new JLabel(" Email:"));
        dialog.add(txtEmail);
        dialog.add(new JLabel(" Parolă nouă (opțional):"));
        dialog.add(txtParola);
        dialog.add(new JLabel(" An Studiu (1-4):"));
        dialog.add(txtAnStudiu);

        JLabel lblInfo = new JLabel("<html><i>Lăsați parola goală pentru a o păstra pe cea curentă</i></html>");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.add(lblInfo);

        dialog.add(infoPanel);
        dialog.add(new JLabel(""));

        JButton btnSave = new JButton("Salvează Modificările");
        btnSave.addActionListener(e -> {
            try {
                String newNume = txtNume.getText().trim();
                String newPrenume = txtPrenume.getText().trim();
                String newEmail = txtEmail.getText().trim();
                String newParola = new String(txtParola.getPassword()).trim();
                int newAnStudiu = Integer.parseInt(txtAnStudiu.getText());

                if (newNume.isEmpty() || newPrenume.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Numele și prenumele nu pot fi goale!");
                    return;
                }
                if (newEmail.isEmpty() || !newEmail.contains("@")) {
                    JOptionPane.showMessageDialog(dialog, "Email invalid!");
                    return;
                }
                if (newAnStudiu < 1 || newAnStudiu > 4) {
                    JOptionPane.showMessageDialog(dialog, "Anul de studiu trebuie să fie între 1 și 4!");
                    return;
                }

                if (service.updateStudentProfile(Integer.parseInt(userId), newNume, newPrenume, newEmail,
                        newParola.isEmpty() ? null : newParola, newAnStudiu)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Profil actualizat cu succes!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Eroare la actualizarea profilului!\nVerificați dacă email-ul nu este deja folosit.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Anul de studiu trebuie să fie un număr valid!");
            }
        });

        dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void showExamsBySubject() {
        headerTitle.setText("  Examene pentru o Materie");
        contentPanel.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(C_LIGHT);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(C_LIGHT);
        topPanel.setBorder(BorderFactory.createTitledBorder(" Selectează Materia"));

        JLabel lblInfo = new JLabel("Alege materia pentru a vedea toate examenele:");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topPanel.add(lblInfo);

        JComboBox<String> comboMaterii = new JComboBox<>();
        comboMaterii.addItem("-- Selectează o materie --");
        List<Object[]> materii = service.getAllMaterii();
        for (Object[] materie : materii) {
            int id = (int) materie[0];
            String nume = (String) materie[1];
            int an = (int) materie[2];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ")");
        }
        topPanel.add(comboMaterii);

        JButton btnSearch = new JButton(" Caută Examene");
        topPanel.add(btnSearch);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Materie", "Data", "Ora", "Sala", "Tip", "Durata (min)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        btnSearch.addActionListener(e -> {
            int selectedIndex = comboMaterii.getSelectedIndex();
            if (selectedIndex > 0) {
                int idMaterie = (int) materii.get(selectedIndex - 1)[0];
                String numeMaterie = (String) materii.get(selectedIndex - 1)[1];

                List<Object[]> data = service.getExamsBySubject(idMaterie);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }

                if (data.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Nu există examene pentru materia: " + numeMaterie,
                            "Informație",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectează o materie din listă!");
            }
        });

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(C_LIGHT);
        JLabel lblNote = new JLabel("<html><i> Interogare cu <b>parametru variabil</b>: IDMaterie</i></html>");
        infoPanel.add(lblNote);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProjectsBySubject() {
        headerTitle.setText("  Proiecte pentru o Materie");
        contentPanel.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(C_LIGHT);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(C_LIGHT);
        topPanel.setBorder(BorderFactory.createTitledBorder(" Selectează Materia"));

        JLabel lblInfo = new JLabel("Alege materia pentru a vedea toate proiectele:");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topPanel.add(lblInfo);

        JComboBox<String> comboMaterii = new JComboBox<>();
        comboMaterii.addItem("-- Selectează o materie --");
        List<Object[]> materii = service.getAllMaterii();
        for (Object[] materie : materii) {
            int id = (int) materie[0];
            String nume = (String) materie[1];
            int an = (int) materie[2];
            comboMaterii.addItem(id + " - " + nume + " (An " + an + ")");
        }
        topPanel.add(comboMaterii);

        JButton btnSearch = new JButton(" Caută Proiecte");
        topPanel.add(btnSearch);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Titlu", "Deadline", "Materie", "Cerințe", "Descriere"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(0));

        btnSearch.addActionListener(e -> {
            int selectedIndex = comboMaterii.getSelectedIndex();
            if (selectedIndex > 0) {
                int idMaterie = (int) materii.get(selectedIndex - 1)[0];
                String numeMaterie = (String) materii.get(selectedIndex - 1)[1];

                List<Object[]> data = service.getProjectsBySubject(idMaterie);
                model.setRowCount(0);
                for (Object[] row : data) {
                    model.addRow(row);
                }

                if (data.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Nu există proiecte pentru materia: " + numeMaterie,
                            "Informație",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selectează o materie din listă!");
            }
        });

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(C_LIGHT);
        JLabel lblNote = new JLabel("<html><i> Interogare cu <b>parametru variabil</b>: IDMaterie</i></html>");
        infoPanel.add(lblNote);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        contentPanel.add(mainPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}