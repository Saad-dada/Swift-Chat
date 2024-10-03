import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection {
    private static Socket socket;
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    public static void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        System.out.println("Connected to server");
    }

    public static void disconnectFromServer() throws IOException {
        sendMessage("/disconnect:" + GlobalVariables.username);
        if (dataInputStream != null)
            dataInputStream.close();
        if (dataOutputStream != null)
            dataOutputStream.close();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (socket != null)
            socket.close();
        System.out.println("Disconnected from server");
    }

    public static void sendMessage(String message) throws IOException {
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }

    public static String receiveMessage() throws IOException {
        return dataInputStream.readUTF();
    }
}