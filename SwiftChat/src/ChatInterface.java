import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

public class ChatInterface {
    public static JFrame frame;

    private JPanel chatMenu;
    
    private JLabel chatLabel;
    private JScrollPane chatListScrollPane;
    public static JList<String> chatList;
    public static List<String> chats = new ArrayList<>();
    
    private JPanel chatArea;
    private JLabel chatNameLabel;
    private JScrollPane chatScrollPane;
    private JTextArea chatTextArea;
    private JScrollPane chatInputScrollPane;
    private JTextArea chatInputArea;
    private JButton attachFileButton;
    private JButton sendButton;

    private JButton chatInfoButton;
    private JButton createChatButton;

    public ChatInterface() {
        frame = new JFrame("Chat Application" + " - " + GlobalVariables.username);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(GlobalVariables.SECONDARY_COLOR);
        frame.setResizable(false);
        frame.setLayout(null);

        chatArea = new JPanel();
        chatArea.setLayout(null);
        chatArea.setBounds(320, 6, 940, 672);
        chatArea.setBackground(GlobalVariables.PRIMARY_COLOR);
        frame.add(chatArea);

        chatMenu = new JPanel();
        chatMenu.setLayout(null);
        chatMenu.setBounds(6, 6, 308, 672);
        chatMenu.setBackground(GlobalVariables.PRIMARY_COLOR);
        frame.add(chatMenu);

        chatLabel = new JLabel("Chats");
        chatLabel.setBounds(105, 10, 120, 40);
        chatLabel.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 34));
        chatLabel.setForeground(Color.WHITE);
        chatMenu.add(chatLabel);

        createChatButton = new JButton("+");
        createChatButton.setBounds(10, 15, 48, 30);
        createChatButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        createChatButton.setBackground(GlobalVariables.SECONDARY_COLOR);
        createChatButton.setForeground(Color.WHITE);
        chatMenu.add(createChatButton);

        createChatButton.addActionListener(e -> {
            new CreateChat();
        });

        chatListScrollPane = new JScrollPane();
        chatListScrollPane.setBounds(10, 60, 288, 600);
        chatMenu.add(chatListScrollPane);

        chatList = new JList<String>(chats.toArray(new String[0]));
        chatList.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatList.setCellRenderer(new CustomCellRenderer());
        chatList.setFixedCellHeight(60);
        chatListScrollPane.setViewportView(chatList);
        chatListScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());

        chatNameLabel = new JLabel();
        chatNameLabel.setBounds(10, 10, 530, 40);
        chatNameLabel.setFont(new Font("Monospaced", Font.BOLD, 34));
        chatNameLabel.setForeground(Color.WHITE);
        chatArea.add(chatNameLabel);

        chatInfoButton = new JButton("i");
        chatInfoButton.setBounds(890, 10, 40, 40);
        chatInfoButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatInfoButton.setBackground(GlobalVariables.SECONDARY_COLOR);
        chatInfoButton.setForeground(Color.WHITE);
        chatArea.add(chatInfoButton);

        chatInfoButton.addActionListener(e -> {
            String chatName = chatNameLabel.getText();
            String usersInChat[] = {"User1", "User2", "User3", "User4", "User5", "User6", "User7", "User8", "User9", "User10"};
            String chatInfo = "Chat Name: " + chatName + "\nUsers: " + String.join(", ", usersInChat);
            JOptionPane.showMessageDialog(null, chatInfo, "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        chatScrollPane = new JScrollPane();
        chatScrollPane.setBounds(10, 60, 920, 500);
        chatArea.add(chatScrollPane);

        chatTextArea = new JTextArea();
        chatTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatTextArea.setEditable(false);
        chatScrollPane.setViewportView(chatTextArea);

        chatInputScrollPane = new JScrollPane();
        chatInputScrollPane.setBounds(10, 570, 630, 60);
        chatArea.add(chatInputScrollPane);
        chatInputScrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        chatInputArea = new JTextArea();
        chatInputArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatInputScrollPane.setViewportView(chatInputArea);

        attachFileButton = new JButton("Attach File");
        attachFileButton.setBounds(650, 570, 170, 60);
        attachFileButton.setBackground(GlobalVariables.SECONDARY_COLOR);
        attachFileButton.setForeground(Color.WHITE);
        attachFileButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatArea.add(attachFileButton);

        attachFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "File attached", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        sendButton = new JButton("Send");
        sendButton.setBounds(830, 570, 100, 60);
        sendButton.setBackground(GlobalVariables.SECONDARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatArea.add(sendButton);

        sendButton.addActionListener(e -> {
            String chatInput = chatInputArea.getText();
            if (!chatInput.isEmpty()) {
                chatTextArea.append(chatInput + "\n");
                chatInputArea.setText("");
            }
        });

        chatArea.setVisible(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        chatList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedChat = chatList.getSelectedValue();
                    if (selectedChat != null) {
                        chatNameLabel.setText(selectedChat);
                        chatArea.setVisible(true);
                    }
                }
            }
        });
    }

    static class CustomCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            label.setPreferredSize(new Dimension(100, 60));
            label.setOpaque(true);
            label.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            return label;
        }
    }

    static class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(4, 4);
        }
    }
}
