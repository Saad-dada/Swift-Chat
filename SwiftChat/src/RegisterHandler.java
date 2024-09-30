import javax.swing.JLabel;
import java.io.IOException;

public class RegisterHandler extends LoginPage {

    public static boolean isRegistered = false;

    public static void register(String username, String password, String confirmPass, JLabel errorLabel) {
        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPass)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        try {
            ServerConnection.connectToServer("localhost", 5000);

            // Send register message to the server
            ServerConnection.sendMessage("/register:" + username + ":" + password);

            String serverResponse = ServerConnection.receiveMessage();

            // Handle server response immediately
            if (serverResponse.equals("Registration successful")) {
                errorLabel.setText(serverResponse);
                isRegistered = true;

                // Switch to login panel after a brief delay
                new javax.swing.Timer(2000, e -> {
                    LoginPage.registerPanel.setVisible(false);
                    LoginPage.loginPanel.setVisible(true);
                }).start();
            } else if (serverResponse.equals("Registration failed")) {
                errorLabel.setText(serverResponse);
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
