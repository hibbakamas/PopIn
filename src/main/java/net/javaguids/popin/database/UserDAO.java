package net.javaguids.popin.database;

import net.javaguids.popin.models.Role;
import net.javaguids.popin.models.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role_name FROM users WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(rs.getString("role_name"));

                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            role
                    );
                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return Optional.empty();
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role_name) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().getName());

            int rows = ps.executeUpdate();
            return rows == 1;

        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
}
