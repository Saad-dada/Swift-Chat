import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class CreateChatHandler {
    public static boolean createChat(String chatName, List<String> usernameList, String createdBy) {

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Step 1: Insert into chat_rooms and get generated room ID
            String query = "INSERT INTO chat_rooms (room_name, created_by) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

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
}
