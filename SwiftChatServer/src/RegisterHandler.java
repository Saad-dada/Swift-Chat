import java.sql.*;

public class RegisterHandler {
    public static boolean registerUser(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Registration successful");
                return true;
            } else {
                System.out.println("Registration failed");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}