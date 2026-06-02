package gui;

import network.TrafficClient;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private TrafficClient client;

    private static final Color BG = new Color(245, 247, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color ACCENT = new Color(37, 99, 235);
    private static final Color DANGER = new Color(220, 38, 38);
    private static final Color TEXT = new Color(17, 24, 39);
    private static final Color SUBTEXT = new Color(107, 114, 128);
    private static final Color BORDER = new Color(209, 213, 219);

    public LoginScreen() {
        client = new TrafficClient();
        boolean connected = client.connect();
        if (!connected) {
            JOptionPane.showMessageDialog(null,
                "Cannot connect to server!\nMake sure server is running.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Admin Login");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(32, 32, 32, 32)));

        // Logo
        JLabel icon = new JLabel("🚦");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to manage incidents");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(SUBTEXT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(TEXT);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(TEXT);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());

        
        JPanel webInfo = new JPanel();
        webInfo.setLayout(new BoxLayout(webInfo, BoxLayout.Y_AXIS));
        webInfo.setBackground(new Color(220, 252, 231));
        webInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(5, 150, 105)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        webInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel webIcon = new JLabel("For Citizens");
        webIcon.setFont(new Font("Segoe UI", Font.BOLD, 12));
        webIcon.setForeground(new Color(5, 150, 105));
        webIcon.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel webUrl = new JLabel("http://localhost:8081");
        webUrl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        webUrl.setForeground(ACCENT);
        webUrl.setAlignmentX(Component.LEFT_ALIGNMENT);
        webUrl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        webInfo.add(webIcon);
        webInfo.add(Box.createVerticalStrut(4));
        webInfo.add(webUrl);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

       
        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(emailLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(24));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(webInfo);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);

        add(card, gbc);

        emailField.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty()) {
            statusLabel.setText("Please enter your email");
            return;
        }
        if (password.isEmpty()) {
            statusLabel.setText("Please enter your password");
            return;
        }

        statusLabel.setText("Signing in...");
        statusLabel.setForeground(SUBTEXT);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return client.sendAndReceive("LOGIN:" + email + "|" + password);
            }
            protected void done() {
                try {
                    String resp = get();
                    if (resp != null && resp.startsWith("LOGIN_SUCCESS:")) {
                        String[] parts = resp.substring(14).split("\\|");
                        String name = parts[1];
                        String role = parts[2];
                        
                        if ("admin".equals(role)) {
                            dispose();
                            new AdminDashboard(client, name);
                        } else {
                            statusLabel.setText("Admin access only. Use web dashboard.");
                            statusLabel.setForeground(DANGER);
                        }
                    } else {
                        statusLabel.setText("Wrong email or password");
                        statusLabel.setForeground(DANGER);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(DANGER);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}
