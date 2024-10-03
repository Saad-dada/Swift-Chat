import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler {

    public static List<String> getUsersInChat(String chatName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int roomId = CreateChatHandler.getRoomId(chatName);
            if (roomId == -1) {
                throw new Exception("Room not found");
            }

            String query = "SELECT username FROM users JOIN participants ON users.user_id = participants.user_id WHERE room_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<String> userList = new ArrayList<>();
            while (resultSet.next()) {
                userList.add(resultSet.getString("username"));
            }

            return userList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveMessageToDB(String chatName, String messageToSend, String sender) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int roomId = CreateChatHandler.getRoomId(chatName);
            if (roomId == -1) {
                throw new Exception("Room not found");
            }

            // Insert message into DB
            String query = "INSERT INTO messages (room_id, message, user_id) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            preparedStatement.setString(2, messageToSend);
            preparedStatement.setInt(3, CreateChatHandler.getUserId(sender));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Message saved to DB");
            } else {
                System.out.println("Message failed to save to DB");
            }

            // Corrected query to fetch time
            String timeQuery = "SELECT sent_at FROM messages WHERE room_id = ? AND message = ? AND user_id = ?";
            PreparedStatement timeStatement = connection.prepareStatement(timeQuery);
            timeStatement.setInt(1, roomId);
            timeStatement.setString(2, messageToSend);
            timeStatement.setInt(3, CreateChatHandler.getUserId(sender));

            ResultSet resultSet = timeStatement.executeQuery();

            // Ensure result exists before accessing
            if (resultSet.next()) {
                String time = resultSet.getString("sent_at");
                // Broadcast the message along with the timestamp
                broadcastMessage(chatName, messageToSend, sender, time);
            } else {
                System.out.println("Failed to retrieve message time.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(String chatName, String messageToSend, String sender, String time) {
        List<String> userList = getUsersInChat(chatName);

        System.out.println("Broadcasting message to users in chat: " + chatName);
        System.out.println("Message: " + messageToSend + " from " + sender + " at " + time);

        for (String username : userList) {
            if (ClientHandler.usernameOnlineList.contains(username)) {
                System.out.println("Sending to: " + username);
                ClientHandler.sendToUser(username,
                        "/message:" + chatName + ":" + messageToSend + ":" + sender + ":" + time);
            } else {
                System.out.println("User not online: " + username);
            }
        }
    }

    public static String getChatHistory(String chatRoom) {
        StringBuilder chatHistory = new StringBuilder();

        try (Connection connection = DatabaseConnection.getConnection()) {
            int roomId = CreateChatHandler.getRoomId(chatRoom);
            if (roomId == -1) {
                throw new Exception("Room not found");
            }

            String query = "SELECT sent_at, user_id, message FROM messages WHERE room_id = ? ORDER BY sent_at ASC";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Building the chat history string
                String sentAt = resultSet.getString("sent_at");
                String userId = CreateChatHandler.getUsernameById(resultSet.getInt("user_id"));
                String message = resultSet.getString("message");

                // Append formatted message to chat history
                chatHistory.append(String.format("%s: %s: %s%n", sentAt, userId, message)); // Use format for better
                                                                                            // readability
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error retrieving chat history.";
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred.";
        }

        return chatHistory.toString();
    }

    public static String getCreatedBy(String chatName) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int roomId = CreateChatHandler.getRoomId(chatName);
            if (roomId == -1) {
                throw new Exception("Room not found");
            }

            String query = "SELECT username FROM users WHERE user_id = (SELECT created_by FROM chat_rooms WHERE room_id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, roomId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                return "User not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred.";
        }
    }

}
