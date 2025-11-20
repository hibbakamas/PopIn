package net.javaguids.popin.database;

import net.javaguids.popin.models.Registration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    public RegistrationDAO() {
        createTableIfNotExists();
    }
    // CREATE TABLE
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS registrations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                status TEXT NOT NULL,
                UNIQUE(event_id, user_id)
            );
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // CREATE REGISTRATION
    public boolean registerUser(int eventId, int userId) {
        String sql = """
            INSERT INTO registrations (event_id, user_id, status)
            VALUES (?, ?, 'REGISTERED');
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            // UNIQUE constraint means user is already registered
            System.err.println("User already registered or DB error: " + e.getMessage());
            return false;
        }
    }

    // UPDATE REGISTRATION STATUS
    public boolean updateStatus(int eventId, int userId, String status) {
        String sql = """
            UPDATE registrations
            SET status = ?
            WHERE event_id = ? AND user_id = ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, eventId);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CHECK IF USER IS ALREADY REGISTERED
    public boolean isUserRegistered(int eventId, int userId) {
        String sql = """
            SELECT 1 FROM registrations
            WHERE event_id = ? AND user_id = ? AND status = 'REGISTERED';
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if found

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // COUNT REGISTERED USERS (for capacity)
    public int countRegistered(int eventId) {
        String sql = """
            SELECT COUNT(*) FROM registrations
            WHERE event_id = ? AND status = 'REGISTERED';
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // GET LIST OF ALL REGISTRATIONS (ATTENDEES)
    public List<Registration> findAllByEvent(int eventId) {
        List<Registration> list = new ArrayList<>();

        String sql = """
            SELECT * FROM registrations
            WHERE event_id = ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Registration(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("user_id"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
