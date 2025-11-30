package net.javaguids.popin.database;

import net.javaguids.popin.models.Role;
import net.javaguids.popin.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    // ----------------------------------
    // FIND USER BY USERNAME (used in login)
    // ----------------------------------
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

    // ----------------------------------
    // FIND USER BY ID
    // ----------------------------------
    public User findById(int id) {
        String sql = "SELECT id, username, password_hash, role_name FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        new Role(rs.getString("role_name"))
                );
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.findById error: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------
    // CREATE USER (used in signup)
    // ----------------------------------
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

    // ----------------------------------
    // LIST ALL USERS (for admin user list)
    // ----------------------------------
    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role_name FROM users";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String uname = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String roleName = rs.getString("role_name");

                Role role = new Role(roleName);
                User user = new User(id, uname, passwordHash, role);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.listAll error: " + e.getMessage());
        }
        return users;
    }

    // ----------------------------------
    // ANALYTICS: COUNT ALL USERS
    // ----------------------------------
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("UserDAO.countAll error: " + e.getMessage());
            return 0;
        }
    }

    // ----------------------------------
    // DELETE USER BY ID (admin delete)
    // ----------------------------------
    public boolean deleteById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.err.println("UserDAO.deleteById error: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------
    // UPDATE PASSWORD (for profile page)
    // ----------------------------------
    public boolean updatePassword(int id, String newHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHash);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.err.println("UserDAO.updatePassword error: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------
    // UPDATE USERNAME (for profile page)
    // ----------------------------------
    public boolean updateUsername(int id, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newUsername);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.err.println("UserDAO.updateUsername error: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------
    // EMAIL NOTIFICATIONS (for profile page)
    // ----------------------------------

    // Returns true if notifications are enabled (defaults to true on error)
    public boolean getEmailNotifications(int id) {
        String sql = "SELECT email_notifications FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int value = rs.getInt("email_notifications"); // 1 or 0
                return value != 0;
            }
        } catch (SQLException e) {
            System.err.println("UserDAO.getEmailNotifications error: " + e.getMessage());
        }
        return true; // safe default
    }

    public boolean updateEmailNotifications(int id, boolean enabled) {
        String sql = "UPDATE users SET email_notifications = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enabled ? 1 : 0);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.err.println("UserDAO.updateEmailNotifications error: " + e.getMessage());
            return false;
        }
    }
}