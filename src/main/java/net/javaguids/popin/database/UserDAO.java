package net.javaguids.popin.database;

import net.javaguids.popin.exceptions.DatabaseOperationException;
import net.javaguids.popin.models.*;
import net.javaguids.popin.exceptions.DatabaseOperationException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    // ----------------------------------
    // Map a DB row -> correct User subclass
    // ----------------------------------
    private User mapRowToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String uname = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String roleName = rs.getString("role_name");
        String upper = roleName != null ? roleName.toUpperCase() : "";

        User user;
        switch (upper) {
            case "ADMIN" -> user = new Admin(uname, passwordHash);
            case "ORGANIZER" -> user = new Organizer(uname, passwordHash);
            case "ATTENDEE" -> user = new Attendee(uname, passwordHash);
            default -> {
                // fallback: keep data but treat as ATTENDEE by default
                System.err.println("UserDAO: unknown role '" + roleName + "', defaulting to ATTENDEE");
                user = new Attendee(uname, passwordHash);
            }
        }

        user.setId(id);
        return user;
    }

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
                    return Optional.of(mapRowToUser(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error finding user by username '" + username + "'", e);
        }
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
                return mapRowToUser(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding user by id " + id, e);
        }
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
            if (rows != 1) {
                throw new DatabaseOperationException("Creating user failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error creating user '" + user.getUsername() + "'", e);
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
                users.add(mapRowToUser(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving all users.", e);
        }
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
            throw new DatabaseOperationException("Error counting users.", e);
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
            throw new DatabaseOperationException("Error deleting user with id " + id, e);
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
            throw new DatabaseOperationException("Error updating password for user " + id, e);
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
            throw new DatabaseOperationException(
                    "Error updating username for user " + id + " to '" + newUsername + "'", e);
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
            // If user not found, default to true (or you might want false)
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error reading email notifications for user " + id, e);
        }
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
            throw new DatabaseOperationException(
                    "Error updating email notifications for user " + id, e);
        }
    }
}