import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterSelectionGUI extends JFrame {

    private static final Color C_LIGHT = new Color(247, 242, 232);
    private static final Color C_MID_ACCENT = new Color(221, 196, 168);
    private static final Color C_DARK = new Color(92, 64, 51);

    public RegisterSelectionGUI() {
        super("Sistem Academic - Alegere Rol");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 200);
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_LIGHT);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(C_LIGHT);

        JLabel questionLabel = new JLabel("Vă înregistrați ca Student sau Profesor?", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setForeground(C_DARK);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(questionLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setBackground(C_LIGHT);

        JButton studentButton = createAestheticButton("Sunt STUDENT");
        JButton profesorButton = createAestheticButton("Sunt PROFESOR");

        buttonPanel.add(studentButton);
        buttonPanel.add(profesorButton);

        mainPanel.add(buttonPanel);
        add(mainPanel);

        attachListeners(studentButton, profesorButton);
    }

    private JButton createAestheticButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(C_MID_ACCENT);
        button.setForeground(C_DARK);

        button.setBorder(new RoundedBorder(C_DARK.brighter(), 1, 15));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }

    private void attachListeners(JButton studentButton, JButton profesorButton) {
        studentButton.addActionListener(e -> {
            new RegisterDetailsGUI("Student").setVisible(true);
            dispose();
        });

        profesorButton.addActionListener(e -> {
            new RegisterDetailsGUI("Profesor").setVisible(true);
            dispose();
        });
    }
}