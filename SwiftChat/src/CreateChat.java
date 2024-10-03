import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateChat {
    protected static JFrame frame;
    private JLabel createChatLabel;

    private JPanel chatSearchPanel;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel chatName;
    private JTextField chatNameField;
    private JButton addUserButton;
    private JButton createChatButton;

    // Declare usernames at the class level to maintain state
    protected static List<String> usernames = new ArrayList<>();
    protected static JLabel usernameAddedLabel;

    CreateChat() {
        frame = new JFrame("Create Chat");
        frame.setSize(480, 360);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(GlobalVariables.SECONDARY_COLOR);
        frame.setResizable(false);
        frame.setLayout(null);

        usernames.clear();

        createChatLabel = new JLabel("Create Chat");
        createChatLabel.setBounds(160, 10, 160, 40);
        createChatLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        createChatLabel.setForeground(Color.WHITE);
        frame.add(createChatLabel);

        chatSearchPanel = new JPanel();
        chatSearchPanel.setLayout(null);
        chatSearchPanel.setBounds(6, 56, 454, 262);
        chatSearchPanel.setBackground(GlobalVariables.PRIMARY_COLOR);
        frame.add(chatSearchPanel);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 120, 40);
        usernameLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        usernameLabel.setForeground(Color.WHITE);
        chatSearchPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(140, 10, 300, 40);
        usernameField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatSearchPanel.add(usernameField);

        chatName = new JLabel("Chat Name:");
        chatName.setBounds(10, 60, 120, 40);
        chatName.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatName.setForeground(Color.WHITE);
        chatSearchPanel.add(chatName);

        chatNameField = new JTextField();
        chatNameField.setBounds(140, 60, 300, 40);
        chatNameField.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatSearchPanel.add(chatNameField);

        addUserButton = new JButton("Add User");
        addUserButton.setBounds(10, 110, 140, 40);
        addUserButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatSearchPanel.add(addUserButton);

        usernameAddedLabel = new JLabel();
        usernameAddedLabel.setBounds(10, 160, 430, 80);
        usernameAddedLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        usernameAddedLabel.setForeground(Color.WHITE);
        chatSearchPanel.add(usernameAddedLabel);

        addUserButton.addActionListener(e -> {
            if (usernameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            CreateChatHandler.addUser(usernameField.getText());

            usernameField.setText("");
        });

        createChatButton = new JButton("Create Chat");
        createChatButton.setBounds(165, 110, 274, 40);
        createChatButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatSearchPanel.add(createChatButton);

        createChatButton.addActionListener(e -> {
            String chatName = chatNameField.getText();

            if (chatName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a chat name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usernames.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please add at least one user", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> usernameList = new ArrayList<>(Arrays.asList(usernames.toArray(new String[0])));

            new Thread(() -> {
                CreateChatHandler.createChat(usernameList, chatName);
            }).start();

        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
