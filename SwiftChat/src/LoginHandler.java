import javax.swing.JLabel;
import java.io.IOException;

public class LoginHandler extends LoginPage {
    public static void login(String username, String password, JLabel errorLabel) {

        if (username.equals("") || password.equals("")) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        try {
            ServerConnection.connectToServer("localhost", 5000);

            ServerConnection.sendMessage("/login:" + username + ":" + password);

            String serverResponse = ServerConnection.receiveMessage();
            if (serverResponse.equals("Login successful")) {
                GlobalVariables.username = username;
                LoginPage.frame.dispose();
                new ChatInterface();
            } else if (serverResponse.equals("Login failed")) {
                errorLabel.setText("Login failed");
            }
            
        } catch (IOException e) {
            errorLabel.setText("Server is not available");
            e.printStackTrace();
        } finally {
            try {
                ServerConnection.disconnectFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}