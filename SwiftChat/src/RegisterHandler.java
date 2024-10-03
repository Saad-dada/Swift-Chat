import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class RegisterHandler extends LoginPage {

    public static void register(String username, String password, String confirmPass, JLabel errorLabel) {
        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPass)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        new Thread(() -> {
            try {
                ServerConnection.sendMessage("/register:" + username + ":" + password);

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    errorLabel.setText("Server is not available");
                });
                e.printStackTrace();
            }
        }).start();
    }
}
