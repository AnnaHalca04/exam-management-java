import javax.swing.*;
import java.awt.*;

public abstract class DashboardGUI extends JFrame {
    protected static final Color C_LIGHT = new Color(247, 242, 232);
    protected static final Color C_MID_ACCENT = new Color(221, 196, 168);
    protected static final Color C_DARK = new Color(92, 64, 51);

    protected JPanel sideMenu;
    protected JPanel contentPanel;
    protected JLabel headerTitle;
    protected String userId;
    protected String userRole;

    public DashboardGUI(String userId, String userRole) {
        super("Sistem Academic - " + userRole);
        this.userId = userId;
        this.userRole = userRole;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sideMenu = new JPanel();
        sideMenu.setPreferredSize(new Dimension(250, 750));
        sideMenu.setBackground(C_DARK);
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel logoLabel = new JLabel("ACADEMIC APP");
        logoLabel.setForeground(C_LIGHT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sideMenu.add(logoLabel);
        sideMenu.add(Box.createVerticalStrut(40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(850, 70));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_MID_ACCENT));

        headerTitle = new JLabel("  Bun venit în sistem");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 22));
        headerTitle.setForeground(C_DARK);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(C_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(sideMenu, BorderLayout.WEST);
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(headerPanel, BorderLayout.NORTH);
        rightContainer.add(contentPanel, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);
    }

    protected JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setBackground(C_DARK);
        btn.setForeground(C_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(C_DARK.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(C_DARK);
            }
        });
        return btn;
    }
}