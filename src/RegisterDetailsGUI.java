import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Objects;

public class RegisterDetailsGUI extends JFrame {

    private static final Color C_LIGHT = new Color(247, 242, 232);
    private static final Color C_MID_ACCENT = new Color(221, 196, 168);
    private static final Color C_DARK = new Color(92, 64, 51);

    private JTextField numeField, prenumeField, emailField;
    private JPasswordField passwordField;

    private JTextField anStudiuField;

    private JTextField departamentField, telefonField;

    private JButton registerButton;
    private JLabel statusLabel;

    private String userType;
    private LoginService loginService = new LoginService();
    private JPanel contentWrapper;

    public RegisterDetailsGUI(String userType) {
        super("Inregistrare ca " + userType);
        this.userType = userType;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_LIGHT);

        contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
        contentWrapper.setBackground(C_LIGHT);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        buildGUI();

        JScrollPane scrollPane = new JScrollPane(contentWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);

    }

    private void buildGUI() {

        JLabel titleLabel = new JLabel("Detalii Cont " + userType, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(C_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentWrapper.add(titleLabel);
        contentWrapper.add(Box.createVerticalStrut(20));

        contentWrapper.add(createLabel("Nume:"));
        numeField = createAestheticTextField();
        contentWrapper.add(numeField);
        contentWrapper.add(Box.createVerticalStrut(10));

        contentWrapper.add(createLabel("Prenume:"));
        prenumeField = createAestheticTextField();
        contentWrapper.add(prenumeField);
        contentWrapper.add(Box.createVerticalStrut(10));

        contentWrapper.add(createLabel("Email:"));
        emailField = createAestheticTextField();
        contentWrapper.add(emailField);
        contentWrapper.add(Box.createVerticalStrut(10));

        contentWrapper.add(createLabel("Parola:"));
        passwordField = new JPasswordField(25);
        styleTextField(passwordField);
        contentWrapper.add(passwordField);
        contentWrapper.add(Box.createVerticalStrut(10));

        if ("Student".equals(userType)) {
            contentWrapper.add(Box.createVerticalStrut(10));
            contentWrapper.add(createLabel("An Studiu (1-4):"));
            anStudiuField = createAestheticTextField();
            contentWrapper.add(anStudiuField);
            contentWrapper.add(Box.createVerticalStrut(10));

        } else if ("Profesor".equals(userType)) {
            contentWrapper.add(Box.createVerticalStrut(10));
            contentWrapper.add(createLabel("Departament:"));
            departamentField = createAestheticTextField();
            contentWrapper.add(departamentField);
            contentWrapper.add(Box.createVerticalStrut(10));

            contentWrapper.add(createLabel("Telefon:"));
            telefonField = createAestheticTextField();
            contentWrapper.add(telefonField);
            contentWrapper.add(Box.createVerticalStrut(10));
        }

        registerButton = createAestheticButton("Inregistrare Cont");
        contentWrapper.add(Box.createVerticalStrut(20));
        contentWrapper.add(registerButton);

        statusLabel = new JLabel("Completati detaliile contului.", SwingConstants.CENTER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(C_DARK);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contentWrapper.add(Box.createVerticalStrut(10));
        contentWrapper.add(statusLabel);

        attachRegisterListener();
    }


    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(C_DARK);
        return label;
    }

    private JTextField createAestheticTextField() {
        JTextField field = new JTextField(25);
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(350, 400));
        field.setBorder(new RoundedBorder(C_MID_ACCENT.darker(), 1, 15));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(C_DARK);
        field.setCaretColor(C_DARK);
        field.setMargin(new Insets(5, 15, 5, 15));
        field.setBackground(Color.WHITE);
    }

    private void styleTextField(JPasswordField field) {
        field.setMaximumSize(new Dimension(350, 400));
        field.setBorder(new RoundedBorder(C_MID_ACCENT.darker(), 1, 15));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(C_DARK);
        field.setCaretColor(C_DARK);
        field.setMargin(new Insets(5, 15, 5, 15));
        field.setBackground(Color.WHITE);
    }

    private JButton createAestheticButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(350, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(C_DARK);
        button.setForeground(C_LIGHT);
        button.setBorder(new RoundedBorder(C_DARK, 1, 20));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private void attachRegisterListener() {
        registerButton.addActionListener(e -> {
            String nume = numeField.getText();
            String prenume = prenumeField.getText();
            String email = emailField.getText();
            String parola = new String(passwordField.getPassword());

            String anStudiu = anStudiuField != null ? anStudiuField.getText() : null;
            String departament = departamentField != null ? departamentField.getText() : null;
            String telefon = telefonField != null ? telefonField.getText() : null;

            try {
                LoginService loginService = new LoginService();

                boolean success = loginService.registerUser(userType, nume, prenume, email, parola, anStudiu, departament, telefon);

                if (success) {
                    statusLabel.setText("Inregistrare reusita! Va puteti autentifica acum.");
                    statusLabel.setForeground(Color.GREEN.darker());

                    new LoginGUI().setVisible(true);
                    dispose();
                } else {
                    statusLabel.setText("Eroare necunoscuta la inregistrare.");
                    statusLabel.setForeground(Color.RED);
                }
            } catch (SQLException ex) {
                String errorMessage = ex.getMessage();

                if (errorMessage.contains("UQ_") || errorMessage.contains("UNIQUE") || errorMessage.contains("duplicate")) {
                    statusLabel.setText("Eroare: Email-ul este deja folosit.");
                } else if (errorMessage.contains("CK_") || errorMessage.contains("CHECK") || errorMessage.contains("format")) {
                    statusLabel.setText("Eroare: Anul de studiu trebuie sa fie intre 1 si 4.");
                } else {
                    statusLabel.setText("Eroare BD. Verificati log-ul.");
                }
                statusLabel.setForeground(Color.RED);
            }
        });
    }
}