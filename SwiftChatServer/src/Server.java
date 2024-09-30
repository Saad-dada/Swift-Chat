import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server is running on port 5000");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                // Handle client in a separate thread to allow multiple clients
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
