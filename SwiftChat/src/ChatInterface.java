import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;

import java.awt.*;
import java.io.IOException;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatInterface {
    public static JFrame frame;

    private JPanel chatMenu;

    protected static JLabel chatLabel;
    protected static JScrollPane chatListScrollPane;
    public static JList<String> chatList;
    public static List<String> chats = new ArrayList<>();

    public static Map<String, String[]> chatHashmap = new HashMap<>();

    protected static JPanel chatArea;
    public static JLabel chatNameLabel;
    private JScrollPane chatScrollPane;
    protected static JTextPane chatTextArea;
    private JScrollPane chatInputScrollPane;
    private JTextArea chatInputArea;
    private JButton sendButton;

    protected static JButton chatInfoButton;
    protected static JButton createChatButton;

    protected static StyledDocument doc;
    protected static Style senderStyle;
    protected static Style messageStyle;
    protected static Style timeStyle;

    public ChatInterface() {
        frame = new JFrame("Chat Application" + " - " + GlobalVariables.username);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        chatList = new JList<>(chats.toArray(new String[0]));
        chatList.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatList.setCellRenderer(new CustomCellRenderer());
        chatList.setFixedCellHeight(60);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
            String selectedChat = chatList.getSelectedValue();
            if (selectedChat != null) {
                String[] users = ChatHandler.getUsersInChat(selectedChat);
                String createdBy = ChatHandler.getCreatedBy(selectedChat);

                String usersString = String.join(", ", users);

                String message = "Chat Name: " + selectedChat.toString() + "\nCreated by: " + createdBy
                        + "\nUsers in this chat: " + usersString;

                JOptionPane.showMessageDialog(null, message, "Chat Info", JOptionPane.INFORMATION_MESSAGE);

                System.out.println(message);

            } else {
                JOptionPane.showMessageDialog(null, "Please select a chat.", "Chat Info",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        chatScrollPane = new JScrollPane();
        chatScrollPane.setBounds(10, 60, 920, 500);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatArea.add(chatScrollPane);

        chatTextArea = new JTextPane();
        chatTextArea.setPreferredSize(new Dimension(920, 500));
        chatTextArea.setContentType("text/plain");

        doc = chatTextArea.getStyledDocument();

        senderStyle = chatTextArea.addStyle("SenderStyle", null);
        StyleConstants.setBold(senderStyle, true);
        StyleConstants.setForeground(senderStyle, Color.BLUE);

        messageStyle = chatTextArea.addStyle("MessageStyle", null);
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        timeStyle = chatTextArea.addStyle("TimeStyle", null);
        StyleConstants.setItalic(timeStyle, true);
        StyleConstants.setForeground(timeStyle, Color.GRAY);

        chatTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatTextArea.setEditable(false);
        chatScrollPane.setViewportView(chatTextArea);

        chatInputScrollPane = new JScrollPane();
        chatInputScrollPane.setBounds(10, 570, 820, 60);
        chatArea.add(chatInputScrollPane);
        chatInputScrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        chatInputArea = new JTextArea();
        chatInputArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        chatInputScrollPane.setViewportView(chatInputArea);

        sendButton = new JButton("Send");
        sendButton.setBounds(830, 570, 100, 60);
        sendButton.setBackground(GlobalVariables.SECONDARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Monospaced", Font.BOLD, 20));
        chatArea.add(sendButton);

        sendButton.addActionListener(e -> {
            String chatInput = chatInputArea.getText();
            if (!chatInput.isEmpty()) {
                ChatHandler.sendMessage(chatInput, chatList.getSelectedValue());
                chatInputArea.setText("");
            }
        });

        chatList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedChat = chatList.getSelectedValue();
                    if (selectedChat != null) {
                        chatNameLabel.setText(selectedChat);
                        chatArea.setVisible(true);
                        chatTextArea.setText("");
                        new Thread(() -> ChatHandler.syncChat(selectedChat)).start();
                    }
                }
            }
        });

        chatList.setSelectedIndex(-1);

        chatArea.setVisible(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static class CustomCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            label.setPreferredSize(new Dimension(100, 60));
            label.setOpaque(true);
            label.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            return label;
        }
    }

    public static void addChat(String chatName, String[] users) {
        chatHashmap.put(chatName, users);
        System.out.println(chatName + ": " + Arrays.toString(users));
    }

    static class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(4, 4);
        }
    }
}
