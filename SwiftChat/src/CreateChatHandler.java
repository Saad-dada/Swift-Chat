import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

public class CreateChatHandler {
    public static void createChat(List<String> usernames, String chatName) {
        try {
            ServerConnection.connectToServer("localhost", 5000);
            List<String> usernameList = usernames;
            String createdBy = GlobalVariables.username;
            ServerConnection.sendMessage("/createChat:" + usernameList + ":" + chatName + ":" + createdBy);
            String serverResponse = ServerConnection.receiveMessage();

            if (serverResponse.equals("Chat created")) {
                CreateChat.frame.dispose();
                JOptionPane.showMessageDialog(null, "Chat created Successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                
            } else if (serverResponse.equals("Chat not created")) {
                JOptionPane.showMessageDialog(null, "Chat created Successfully", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void addUser(String username) {
        try {
            ServerConnection.connectToServer("localhost", 5000);
            ServerConnection.sendMessage("/addUser:" + username);
            String serverResponse = ServerConnection.receiveMessage();

            if (username.equals(GlobalVariables.username)) {
                JOptionPane.showMessageDialog(null, "You cannot add yourself", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (CreateChat.usernames.contains(username)) {
                JOptionPane.showMessageDialog(null, "User already added", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (serverResponse.equals("User added")) {
                CreateChat.usernames.add(GlobalVariables.username);
                CreateChat.usernames.add(username);
            } else if (serverResponse.equals("User not found")) {
                JOptionPane.showMessageDialog(null, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
