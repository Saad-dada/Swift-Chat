import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CreateChatHandler {
    public static boolean createChat(String chatName, List<String> usernameList, String createdBy) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Step 1: Insert into chat_rooms and get generated room ID
            String query = "INSERT INTO chat_rooms (room_name, created_by) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, chatName);
            preparedStatement.setInt(2, getUserId(createdBy));

            preparedStatement.executeUpdate();

            // Retrieve the generated room ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int roomId = -1;
            if (generatedKeys.next()) {
                roomId = generatedKeys.getInt(1);
            }

            if (roomId == -1) {
                throw new Exception("Failed to retrieve room ID");
            }

            // Step 2: Insert participants into the participants table
            String query2 = "INSERT INTO participants (room_id, user_id) VALUES (?, ?)";
            for (String username : usernameList) {
                PreparedStatement preparedStatement2 = connection.prepareStatement(query2);

                preparedStatement2.setInt(1, roomId);
                preparedStatement2.setInt(2, getUserId(username));

                preparedStatement2.executeUpdate();
            }

            for (String username : usernameList) {
                CreateChatHandler.notifyUserOfChat(username, chatName);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getRoomId(String roomName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT room_id FROM chat_rooms WHERE room_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, roomName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("room_id");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getUserId(String username) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            } else {
                throw new Exception("User not found: " + username);
            }
        }
    }

    public static void notifyUserOfChat(String username, String chatName) {
        if (ClientHandler.usernameOnlineList.contains(username)) {
            int roomId = getRoomId(chatName);
            if (roomId != -1) {
                List<String> usernameList = new ArrayList<>();
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String query = "SELECT username FROM users WHERE user_id IN (SELECT user_id FROM participants WHERE room_id = ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, roomId);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        usernameList.add(resultSet.getString("username"));
                    }
                } catch (Exception e) {
                }

                try {
                    Socket userSocket = ClientHandler.usernameSocketMap.get(username);
                    if (userSocket != null) {
                        DataOutputStream dataOutputStream = new DataOutputStream(userSocket.getOutputStream());
                        dataOutputStream.writeUTF("/added_to_chat:" + chatName + ":" + usernameList);
                        System.out.println("/added_to_chat:" + chatName + ":" + usernameList);
                    } else {
                        System.out.println("No socket found for user: " + username);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static List<String> getChats(String username) {
        List<String> chatList = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT room_name FROM chat_rooms WHERE room_id IN (SELECT room_id FROM participants WHERE user_id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getUserId(username));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                chatList.add(resultSet.getString("room_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatList;
    }

    public static String getUsernameById(int int1) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT username FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, int1);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}