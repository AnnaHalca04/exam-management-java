import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {

    private static final Color C_LIGHT = new Color(247, 242, 232);
    private static final Color C_MID_ACCENT = new Color(221, 196, 168);
    private static final Color C_DARK = new Color(92, 64, 51);

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JButton registerLinkButton;

    private LoginService loginService = new LoginService();

    public LoginGUI() {
        super("Sistem Academic - Login");

        UIManager.put("Panel.background", C_LIGHT);
        UIManager.put("Label.foreground", C_DARK);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_LIGHT);

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));
        mainContentPanel.setBackground(C_LIGHT);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(C_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContentPanel.add(titleLabel);
        mainContentPanel.add(Box.createVerticalStrut(30));


        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainContentPanel.add(emailLabel);
        emailField = new JTextField(25);
        styleTextField(emailField);
        mainContentPanel.add(emailField);
        mainContentPanel.add(Box.createVerticalStrut(40));

        JLabel passwordLabel = new JLabel("Parolă");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainContentPanel.add(passwordLabel);
        passwordField = new JPasswordField(40);
        styleTextField(passwordField);
        mainContentPanel.add(passwordField);
        mainContentPanel.add(Box.createVerticalStrut(30));

        loginButton = createAestheticButton("Login");
        mainContentPanel.add(loginButton);
        mainContentPanel.add(Box.createVerticalStrut(20));

        registerLinkButton = new JButton("<html><u>Nu ai cont? Creează unul!</u></html>");
        registerLinkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLinkButton.setForeground(C_DARK.darker());
        registerLinkButton.setBackground(C_LIGHT);
        registerLinkButton.setBorderPainted(false);
        registerLinkButton.setContentAreaFilled(false);
        registerLinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContentPanel.add(registerLinkButton);
        mainContentPanel.add(Box.createVerticalStrut(10));


        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(C_DARK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContentPanel.add(statusLabel);

        add(mainContentPanel);
        attachListeners();
    }

    private JButton createAestheticButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
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


    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height + 15));
        field.setBorder(new RoundedBorder(C_MID_ACCENT.darker(), 1, 15));
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(C_DARK);
        field.setCaretColor(C_DARK);
        field.setMargin(new Insets(5, 15, 5, 15));
        field.setBackground(Color.WHITE);
    }


    private void attachListeners() {
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String parola = new String(passwordField.getPassword());

            String[] userDetails = loginService.authenticateUser(email, parola);

            if (userDetails != null) {
                String userId = userDetails[0];
                String userType = userDetails[1];

                statusLabel.setText("Login reușit! Bun venit, " + userType);
                statusLabel.setForeground(C_DARK);

                if (userType.equals("Profesor")) {
                    new ProfesorDashboard(userId).setVisible(true);
                } else if (userType.equals("Student")) {
                    new StudentDashboard(userId).setVisible(true);
                }

                dispose();
            } else {
                statusLabel.setText("Email sau parolă incorecte.");
                statusLabel.setForeground(Color.RED.darker());
            }
        });

        registerLinkButton.addActionListener(e -> {
            new RegisterSelectionGUI().setVisible(true);
            dispose();
        });
    }
}