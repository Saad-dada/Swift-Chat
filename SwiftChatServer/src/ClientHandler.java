import java.net.Socket;
import java.util.*;
import java.io.*;

public class ClientHandler extends Thread {
    // Static lists and maps to store online users, sockets, chat histories, etc.
    public static List<String> usernameOnlineList = new ArrayList<>();
    public static Map<String, Socket> usernameSocketMap = new HashMap<>();

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Initialize input and output streams for communication
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {

                String message = dataInputStream.readUTF();
                System.out.println("Received: " + message);

                // Handle various types of requests
                if (message.startsWith("/login:")) {
                    handleLogin(message);
                } else if (message.startsWith("/disconnect:")) {
                    handleDisconnect(message);
                } else if (message.startsWith("/register:")) {
                    handleRegistration(message);
                } else if (message.startsWith("/addUser:")) {
                    handleAddUser(message);
                } else if (message.startsWith("/sendMessage:")) {
                    handleSendMessage(message);
                } else if (message.startsWith("/createChat:")) {
                    handleCreateChat(message);
                } else if (message.startsWith("/getUsersInChat:")) {
                    handleGetUsersInChat(message);
                } else if (message.startsWith("/getChatHistory:")) {
                    handleGetChatHistory(message);
                } else if (message.startsWith("/userExist:")) {
                    handleUserExist(message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private void handleUserExist(String message) {
        String[] parts = message.split(":");
        String username = parts[1];

        try {
            if (LoginHandler.userExists(username)) {
                dataOutputStream.writeUTF("User found");
            } else {
                dataOutputStream.writeUTF("User not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle user login
    private void handleLogin(String message) throws IOException {
        String[] loginData = message.split(":");
        String username = loginData[1];
        String password = loginData[2];

        if (LoginHandler.validiateLogin(username, password)) {
            synchronized (usernameOnlineList) {
                // Remove old connection if user is already online
                usernameOnlineList.remove(username);
                usernameSocketMap.remove(username);

                // Add user to online list and store the socket
                usernameOnlineList.add(username);
                usernameSocketMap.put(username, socket);

                // Send success response
                dataOutputStream.writeUTF("/login_success:" + username);
                System.out.println("User connected: " + username);

                // Send chat list to the user
                List<String> chatList = CreateChatHandler.getChats(username);
                if (!chatList.isEmpty()) {
                    dataOutputStream.writeUTF("/added_to_chat:" + chatList.toString());
                }
            }
        } else {
            dataOutputStream.writeUTF("/login_failed:Invalid credentials");
        }
    }

    // Handle user disconnection
    private void handleDisconnect(String message) throws IOException {
        String[] disconnectData = message.split(":");
        String username = disconnectData[1];

        // Remove user from online lists
        usernameOnlineList.remove(username);
        usernameSocketMap.remove(username);

        System.out.println("User disconnected: " + username);
    }

    // Handle user registration
    private void handleRegistration(String message) throws IOException {
        String[] registerData = message.split(":");
        String username = registerData[1];
        String password = registerData[2];

        if (RegisterHandler.registerUser(username, password)) {
            dataOutputStream.writeUTF("/registration_success:" + username);
        } else {
            dataOutputStream.writeUTF("/registration_failed:Username already exists");
        }
    }

    // Handle adding a user to a chat room
    private void handleAddUser(String message) throws IOException {
        String[] chatData = message.split(":");
        String username = chatData[1];

        try {
            if (CreateChatHandler.getUserId(username) == -1) {
                dataOutputStream.writeUTF("User not found");
            } else {
                dataOutputStream.writeUTF("User added: " + username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle sending a message in a chat room
    private void handleSendMessage(String message) {
        String[] chatData = message.split(":");
        String chatName = chatData[1];
        String messageToSend = chatData[2];
        String sender = chatData[3];

        // Save the message to the database
        ChatHandler.saveMessageToDB(chatName, messageToSend, sender);
    }

    // Handle creating a new chat room
    private void handleCreateChat(String message) throws IOException {
        String[] chatData = message.split(":");
        String usernameListString = chatData[1].replace("[", "").replace("]", "").replace(" ", "");
        String chatName = chatData[2];
        String createdBy = chatData[3];
        List<String> usernameList = Arrays.asList(usernameListString.split(","));

        if (CreateChatHandler.createChat(chatName, usernameList, createdBy)) {
            dataOutputStream.writeUTF("/chat_created:" + chatName);
            System.out.println("Chat created: " + chatName);
        } else {
            dataOutputStream.writeUTF("Chat creation failed");
        }
    }

    // Handle retrieving users in a chat room
    private void handleGetUsersInChat(String message) throws IOException {
        String[] chatData = message.split(":");
        String chatName = chatData[1];
        String username = chatData[2];

        List<String> userList = ChatHandler.getUsersInChat(chatName);
        String messageToSend = "/usersInChat:" + chatName + ":" + userList.toString();
        sendToUser(username, messageToSend);
        System.out.println(messageToSend);
    }

    private void handleGetChatHistory(String message) {
        String[] parts = message.split(":");
        String chatRoom = parts[1];

        // Retrieve chat history for the room
        String chatHistory = ChatHandler.getChatHistory(chatRoom);
        try {
            if (chatHistory != null && !chatHistory.isEmpty()) {
                dataOutputStream.writeUTF("/chatHistory:" + chatRoom + ":" + chatHistory); // Send back to the client
            } else {
                dataOutputStream.writeUTF("No messages found"); // No history found
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Close resources (socket, input and output streams)
    private void closeResources() {
        try {
            if (socket != null)
                socket.close();
            if (dataInputStream != null)
                dataInputStream.close();
            if (dataOutputStream != null)
                dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message to a specific user
    public static void sendToUser(String username, String message) {
        try {
            Socket userSocket = usernameSocketMap.get(username);
            if (userSocket != null) {
                DataOutputStream out = new DataOutputStream(userSocket.getOutputStream());
                out.writeUTF(message);
            } else {
                System.out.println("No socket found for user: " + username);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
