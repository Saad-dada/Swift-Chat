import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class LoginPage {
    public static JFrame frame;

    protected static JPanel registerPanel;
    private JLabel registerLabel;
    private JLabel usernameRegLabel;
    private JLabel passwordRegLabel;
    private JLabel confirmPasswordLabel;
    private JTextField usernameRegField;
    private JPasswordField passwordRegField;
    private JPasswordField confirmPasswordField;
    private JButton registerRegButton;
    private JButton loginRegButton;
    public static JLabel errorRegLabel;

    protected static JPanel loginPanel;
    private JLabel loginLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    public static JLabel errorLabel;

    LoginPage() {
        frame = new JFrame("Login Page");
        frame.setSize(1280, 720);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    ServerConnection.disconnectFromServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                frame.dispose();
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(GlobalVariables.SECONDARY_COLOR);
        frame.setResizable(false);

        frame.setLayout(null);

        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(380, 85, 520, 550);
        loginPanel.setBackground(GlobalVariables.PRIMARY_COLOR);
        loginPanel.setBorder(new LineBorder(Color.BLACK, 2));
        frame.add(loginPanel);

        loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Monospaced", Font.BOLD, 35));
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setBounds(225, 50, 120, 40);
        loginPanel.add(loginLabel);

        usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(50, 150, 120, 40);
        loginPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        usernameField.setBounds(200, 150, 250, 40);
        loginPanel.add(usernameField);

        passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 250, 120, 40);
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        passwordField.setBounds(200, 250, 250, 40);
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLUE);
        loginButton.setBounds(50, 350, 180, 40);
        loginPanel.add(loginButton);

        registerButton = new JButton("Register > ");
        registerButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        registerButton.setBackground(Color.GREEN);
        registerButton.setBounds(270, 350, 180, 40);
        loginPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            loginPanel.setVisible(false);
            registerPanel.setVisible(true);
        });

        errorLabel = new JLabel();
        errorLabel.setFont(new Font("Monospaced", Font.ITALIC, 20));
        errorLabel.setForeground(Color.RED);
        errorLabel.setBounds(50, 450, 450, 40);
        loginPanel.add(errorLabel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            new Thread(() -> {
                LoginHandler.login(username, password, errorLabel);
            }).start();

        });

        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBounds(380, 85, 520, 550);
        registerPanel.setBackground(GlobalVariables.PRIMARY_COLOR);
        registerPanel.setBorder(new LineBorder(Color.BLACK, 2));
        registerPanel.setVisible(false);
        frame.add(registerPanel);

        registerLabel = new JLabel("Register");
        registerLabel.setFont(new Font("Monospaced", Font.BOLD, 35));
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setBounds(180, 50, 180, 40);
        registerPanel.add(registerLabel);

        usernameRegLabel = new JLabel("Username: ");
        usernameRegLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        usernameRegLabel.setForeground(Color.WHITE);
        usernameRegLabel.setBounds(50, 150, 120, 40);
        registerPanel.add(usernameRegLabel);

        usernameRegField = new JTextField();
        usernameRegField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        usernameRegField.setBounds(200, 150, 250, 40);
        registerPanel.add(usernameRegField);

        passwordRegLabel = new JLabel("Password: ");
        passwordRegLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        passwordRegLabel.setForeground(Color.WHITE);
        passwordRegLabel.setBounds(50, 250, 120, 40);
        registerPanel.add(passwordRegLabel);

        passwordRegField = new JPasswordField();
        passwordRegField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        passwordRegField.setBounds(200, 250, 250, 40);
        registerPanel.add(passwordRegField);

        confirmPasswordLabel = new JLabel("Confirm Pass: ");
        confirmPasswordLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        confirmPasswordLabel.setForeground(Color.WHITE);
        confirmPasswordLabel.setBounds(50, 350, 200, 40);
        registerPanel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        confirmPasswordField.setBounds(220, 350, 230, 40);
        registerPanel.add(confirmPasswordField);

        registerRegButton = new JButton("Register");
        registerRegButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        registerRegButton.setForeground(Color.WHITE);
        registerRegButton.setBackground(Color.BLUE);
        registerRegButton.setBounds(50, 450, 180, 40);

        registerRegButton.addActionListener(e -> {
            String username = usernameRegField.getText();
            String password = new String(passwordRegField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            RegisterHandler.register(username, password, confirmPassword, errorRegLabel);
        });

        registerPanel.add(registerRegButton);

        loginRegButton = new JButton("< Login");
        loginRegButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        loginRegButton.setBackground(Color.GREEN);
        loginRegButton.setBounds(270, 450, 180, 40);
        registerPanel.add(loginRegButton);

        errorRegLabel = new JLabel();
        errorRegLabel.setFont(new Font("Monospaced", Font.ITALIC, 20));
        errorRegLabel.setForeground(Color.RED);
        errorRegLabel.setBounds(50, 500, 450, 40);
        registerPanel.add(errorRegLabel);

        loginRegButton.addActionListener(e -> {
            registerPanel.setVisible(false);
            loginPanel.setVisible(true);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
