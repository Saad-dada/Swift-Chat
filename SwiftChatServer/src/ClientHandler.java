import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientHandler extends Thread {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String message = dataInputStream.readUTF();
            System.out.println("Received: " + message);

            // Check if the message is a login request
            if (message.startsWith("/login:")) {
                String[] loginData = message.split(":");
                String username = loginData[1];
                String password = loginData[2];

                if (LoginHandler.validiateLogin(username, password)) {
                    dataOutputStream.writeUTF("Login successful");
                } else {
                    dataOutputStream.writeUTF("Login failed");
                }
            }

            if (message.startsWith("/register:")) {
                String[] registerData = message.split(":");
                String username = registerData[1];
                String password = registerData[2];

                if (RegisterHandler.registerUser(username, password)) {
                    dataOutputStream.writeUTF("Registration successful");
                } else {
                    dataOutputStream.writeUTF("Registration failed");
                }
            }

            if (message.startsWith("/addUser:")) {
                String[] chatData = message.split(":");
                String username = chatData[1];

                try {
                    if (CreateChatHandler.getUserId(username) == -1) {
                        dataOutputStream.writeUTF("User not found");
                    } else {
                        dataOutputStream.writeUTF("User added");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (message.startsWith("/createChat:")) {   
                String[] chatData = message.split(":");
                String usernameListString = chatData[1].replace("[", "").replace("]", "").replace(" ", "");
                String chatName = chatData[2];
                String createdBy = chatData[3];
                List<String> usernameList = Arrays.asList(usernameListString.split(","));

                if (CreateChatHandler.createChat(chatName, usernameList, createdBy)) {
                    dataOutputStream.writeUTF("Chat created");
                } else {
                    dataOutputStream.writeUTF("Chat creation failed");
                }
            }

            // Close streams and socket after handling the client
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
