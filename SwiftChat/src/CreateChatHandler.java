import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

public class CreateChatHandler extends CreateChat {
    public static void createChat(List<String> usernames, String chatName) {
        try {
            List<String> usernameList = usernames;
            String createdBy = GlobalVariables.username;
            ServerConnection.sendMessage("/createChat:" + usernameList + ":" + chatName + ":" + createdBy);

            if (GlobalVariables.serverMessage.equals("/chat_created:" + chatName)) {
                JOptionPane.showMessageDialog(null, "Chat created Successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } else if (GlobalVariables.serverMessage.equals("Chat not created")) {
                JOptionPane.showMessageDialog(null, "Chat not created", "Error", JOptionPane.ERROR_MESSAGE);
            }
            CreateChat.frame.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String username) {
        try {
            if (username.equals(GlobalVariables.username)) {
                JOptionPane.showMessageDialog(null, "You cannot add yourself", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (CreateChat.usernames.contains(username)) {
                JOptionPane.showMessageDialog(null, "User already added", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ServerConnection.sendMessage("/addUser:" + username);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (GlobalVariables.serverMessage.equals("User added: " + username)) {
                usernames.clear();
                usernames.add(GlobalVariables.username);
                usernames.add(username);

                System.out.println("Usernames: " + usernames);

                usernameAddedLabel.setText("Added Users: " + usernames);
                usernameAddedLabel.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }

            System.out.println(GlobalVariables.serverMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
