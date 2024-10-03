import javax.swing.SwingUtilities;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            ServerConnection.connectToServer("23.ip.gl.ply.gg", 4869);

            new Thread() {
                public void run() {
                    listenForMessages();
                }
            }.start();

            new LoginPage();
        } catch (IOException e) {
            System.out.println("Server is not running");
            try {
                ServerConnection.disconnectFromServer();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static void listenForMessages() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = ServerConnection.receiveMessage();

                    // Call appropriate handler based on the message
                    if (message.startsWith("/login_success:")) {
                        handleLoginSuccess(message);
                    } else if (message.startsWith("/login_failed:")) {
                        handleLoginFailure(message);
                    } else if (message.startsWith("/registration_success:")) {
                        handleRegistrationSuccess(message);
                    } else if (message.startsWith("/registration_failed:")) {
                        handleRegistrationFailure(message);
                    } else if (message.startsWith("/chat_created:")) {
                        handleChatCreation(message);
                    } else if (message.startsWith("/added_to_chat:")) {
                        handleChatUpdate(message);
                    } else if (message.startsWith("/removed_from_chat:")) {
                        handleChatUpdate(message);
                    } else if (message.startsWith("/message:")) {
                        receiveMessageAndUpdate(message);
                    } else if (message.startsWith("/chatHistory:")) {
                        handleChatSync(message);
                    } else {
                        System.out.println("Server: " + message);
                        GlobalVariables.serverMessage = message;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleChatCreation(String message) {
        String chatName = message.substring("/chat_created:".length());
        GlobalVariables.serverMessage = "/chat_created:" + chatName;
    }

    private static void handleRegistrationSuccess(String message) {
        GlobalVariables.serverMessage = "Registration successful";
        SwingUtilities.invokeLater(() -> {
            LoginPage.errorRegLabel.setText("Successful: Please Login!");
        });
    }

    private static void handleRegistrationFailure(String message) {
        String errorMessage = message.substring("/registration_failed:".length());
        GlobalVariables.serverMessage = "Registration failed";
        SwingUtilities.invokeLater(() -> {
            LoginPage.errorRegLabel.setText("Registration failed: " + errorMessage);
        });
    }

    public static void handleLoginSuccess(String message) {
        String loginMessage = message.substring("/login_success:".length());
        GlobalVariables.serverMessage = "Login successful";
        GlobalVariables.username = loginMessage;

        SwingUtilities.invokeLater(() -> {
            if (LoginPage.frame != null)
                LoginPage.frame.dispose();
            new ChatInterface();
        });
    }

    private static void handleLoginFailure(String message) {
        String errorMessage = message.substring("/login_failed:".length());
        GlobalVariables.serverMessage = "Login failed";
        SwingUtilities.invokeLater(() -> {
            LoginPage.errorLabel.setText("Login failed: " + errorMessage);
        });
    }

    private static void handleChatUpdate(String message) {
        String[] parts = message.split(":");
        String[] chatsName = parts[1].replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");

        for (String chatName : chatsName) {
            String usernames[] = ChatHandler.getUsersInChat(chatName);

            if (usernames.length == 0) {
                System.out.println("No users in chat: " + chatName);
            }

            ChatHandler.updateChatInterface(chatName, usernames);
        }
    }

    private static void handleChatSync(String s_message) {
        String[] parts = s_message.split(":", 3);

        // Check if parts has at least 3 elements
        if (parts.length < 3) {
            System.err.println("Invalid message format: " + s_message);
            return; // Exit the method if the format is incorrect
        }

        String chatName = parts[1];
        String history = parts[2];
        String[] messages = history.split("\n");

        for (String message : messages) {
            // Split the message and check its format
            String[] messageParts = message.split(":");
            if (messageParts.length < 5) {
                System.err.println("Invalid message structure: " + message);
                continue; // Skip this message if the format is incorrect
            }

            String time = messageParts[0] + " " + messageParts[1] + ":" + messageParts[2];
            String sender = messageParts[3].replace(" ", "");
            String chatData = messageParts[4].replace(" ", "");

            ChatHandler.updateChatArea(chatName, chatData, sender, time);
        }
    }

    private static void receiveMessageAndUpdate(String message) {
        String[] parts = message.split(":");
        String chatName = parts[1];
        String chatData = parts[2];
        String sender = parts[3];
        String time = parts[4] + ":" + parts[5] + ":" + parts[6];

        ChatHandler.updateChatArea(chatName, chatData, sender, time);

        System.out.println("Message: " + chatName + " Input: " + chatData + " Sender: " + sender);
    }
}
