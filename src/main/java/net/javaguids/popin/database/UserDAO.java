package net.javaguids.popin.database;

import net.javaguids.popin.models.Role;
import net.javaguids.popin.models.User;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role_name FROM users WHERE username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String roleName = rs.getString("role_name");

                    Role role = new Role(roleName);
                    User user = new User(id, uname, passwordHash, role);

                    return Optional.of(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("UserDAO.findByUsername error: " + e.getMessage());
        }

        return Optional.empty();
    }

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        new Role(rs.getString("role"))
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role_name) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().getName());

            int rows = stmt.executeUpdate();
            return rows == 1;

        } catch (SQLException e) {
            System.err.println("UserDAO.createUser error: " + e.getMessage());
            return false;
        }
    }
}
