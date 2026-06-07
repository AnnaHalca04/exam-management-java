import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeGUI extends JFrame {

    private static final Color C_LIGHT = new Color(247, 242, 232);
    private static final Color C_DARK = new Color(92, 64, 51);

    public WelcomeGUI() {
        super("Sistem Academic - Start");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_LIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(C_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(C_LIGHT);

        JLabel titleLabel = new JLabel("Aplicație examene & proiecte", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(C_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(titleLabel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(C_LIGHT);

        JButton loginButton = createAestheticButton("Autentificare (Login)");
        JButton registerButton = createAestheticButton("Înregistrare");

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        attachListeners(loginButton, registerButton);
    }

    private JButton createAestheticButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
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

    private void attachListeners(JButton loginButton, JButton registerButton) {
        loginButton.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });

        registerButton.addActionListener(e ->
        {
            new RegisterSelectionGUI().setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeGUI().setVisible(true);
        });
    }
}