import javax.swing.JLabel;
import java.io.IOException;

public class LoginHandler extends LoginPage {
    public static void login(String username, String password, JLabel errorLabel) {
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }
        new Thread(() -> {
            try {
                ServerConnection.sendMessage("/login:" + username + ":" + password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
