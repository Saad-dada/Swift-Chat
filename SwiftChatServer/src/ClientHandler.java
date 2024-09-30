import java.net.Socket;
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

                if(LoginHandler.validiateLogin(username, password)){
                    dataOutputStream.writeUTF("Login successful");
                } else {
                    dataOutputStream.writeUTF("Login failed");
                }
            }

            if (message.startsWith("/register:")) {
                String[] registerData = message.split(":");
                String username = registerData[1];
                String password = registerData[2];

                if(RegisterHandler.registerUser(username, password)){
                    dataOutputStream.writeUTF("Registration successful");
                } else {
                    dataOutputStream.writeUTF("Registration failed");
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
