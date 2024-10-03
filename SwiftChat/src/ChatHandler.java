import java.io.IOException;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

public class ChatHandler extends ChatInterface {

    public static void updateChatInterface(String chatName, String[] usernames) {
        System.out.println("\nYou have been added to chat: " + chatName);
        System.out.println("Chat members: " + String.join(", ", usernames));

        if (chats.contains(chatName)) {
            chats.remove(chatName);
            chatHashmap.remove(chatName);
            chatList.setListData(chats.toArray(new String[0]));
        }

        chats.add(chatName);
        chatList.setListData(chats.toArray(new String[0]));

        addChat(chatName, usernames);

        if (chatList.getListSelectionListeners().length == 0) {
            chatList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedChat = chatList.getSelectedValue();
                    chatNameLabel.setText(selectedChat);
                    chatNameLabel.repaint();
                    chatArea.setVisible(true);
                }
            });
        }
    }

    public static String[] getUsersInChat(String chatName) {
        try {
            ServerConnection.sendMessage("/getUsersInChat:" + chatName + ":" + GlobalVariables.username);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (GlobalVariables.serverMessage.startsWith("/usersInChat:")) {
                String[] parts = GlobalVariables.serverMessage.split(":");
                String _chatName = parts[1];

                if (!_chatName.equals(chatName)) {
                    return new String[0];
                }

                String[] users = parts[2].replace("[", "").replace("]", "").replace(" ", "").split(",");

                return users;
            }

            System.out.println("Users in chat: ");
            return new String[0];

        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static void sendMessage(String chatInput, String selectedValue) {
        try {
            ServerConnection
                    .sendMessage("/sendMessage:" + selectedValue + ":" + chatInput + ":" + GlobalVariables.username);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateChatArea(String chatName, String chatData, String sender, String time) {

        if (chatList.getSelectedValue() == null) {
            return;
        }

        if (chatList.getSelectedValue().toString().equals(chatName)) {
            SwingUtilities.invokeLater(() -> {
                try {
                    doc.insertString(doc.getLength(), "[" + time + "]" + "\n", timeStyle); // Time in gray and italic
                    doc.insertString(doc.getLength(), sender + ": ", senderStyle); // Sender's name in blue and bold
                    doc.insertString(doc.getLength(), chatData + "\n\n", messageStyle); // Message text in black

                    scrollToBottom();

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                chatTextArea.repaint();
            });
        }
    }

    public static void syncChat(String selectedChat) {
        try {
            ServerConnection.sendMessage("/getChatHistory:" + selectedChat);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scrollToBottom() {
        JScrollBar vertical = ((JScrollPane) ChatInterface.chatTextArea.getParent().getParent()).getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public static String getCreatedBy(String selectedChat) {
        try {
            ServerConnection.sendMessage("/getCreatedBy:" + selectedChat);

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (GlobalVariables.serverMessage.startsWith("/createdBy:")) {
                String[] parts = GlobalVariables.serverMessage.split(":");
                String chatName = parts[1];
                String createdBy = parts[2];

                System.out.println("Chat: " + chatName + " created by: " + createdBy);

                return createdBy;
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}