package net.javaguids.popin.database;

import net.javaguids.popin.exceptions.DatabaseOperationException;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import net.javaguids.popin.models.Registration;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public RegistrationDAO() {
        createTableIfNotExists();
    }

    // ----------------------------------------------------
    // CREATE TABLE
    // ----------------------------------------------------
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
            throw new DatabaseOperationException("Failed to create registrations table.", e);
        }
    }

    // ----------------------------------------------------
    // CREATE REGISTRATION
    // ----------------------------------------------------
    public boolean registerUser(int eventId, int userId) {
        String sql = """
            INSERT INTO registrations (event_id, user_id, status)
            VALUES (?, ?, 'REGISTERED');
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseOperationException("Registration insert failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            // This hits for unique constraint as well (already registered)
            throw new DatabaseOperationException(
                    "Error registering user " + userId + " for event " + eventId, e);
        }
    }

    // ----------------------------------------------------
    // UPDATE REGISTRATION STATUS
    // ----------------------------------------------------
    public boolean updateStatus(int eventId, int userId, String status) {
        String sql = """
            UPDATE registrations SET status = ?
            WHERE event_id = ? AND user_id = ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, eventId);
            stmt.setInt(3, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error updating registration status for event " + eventId +
                            ", user " + userId + " to '" + status + "'", e);
        }
    }

    // ----------------------------------------------------
    // CHECK IF USER IS REGISTERED
    // ----------------------------------------------------
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
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error checking registration for event " + eventId + ", user " + userId, e);
        }
    }

    // ----------------------------------------------------
    // COUNT REGISTERED USERS (for capacity)
    // ----------------------------------------------------
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
            throw new DatabaseOperationException(
                    "Error counting registrations for event " + eventId, e);
        }
    }

    // ----------------------------------------------------
    // FIND USER IDS ATTENDING EVENT
    // ----------------------------------------------------
    public List<Integer> findUserIdsByEvent(int eventId) {
        List<Integer> list = new ArrayList<>();
        String sql = """
            SELECT user_id FROM registrations
            WHERE event_id = ? AND status = 'REGISTERED';
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("user_id"));
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error retrieving user IDs for event " + eventId, e);
        }
    }

    // ----------------------------------------------------
    // GET ALL REGISTRATION ENTRIES (raw)
    // ----------------------------------------------------
    public List<Registration> findAllByEvent(int eventId) {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT * FROM registrations WHERE event_id = ?";

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
            return list;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error retrieving registrations for event " + eventId, e);
        }
    }

    // ----------------------------------------------------
    // FIND EVENTS REGISTERED BY USER
    // ----------------------------------------------------
    public List<Event> findByUserId(int userId) {
        List<Event> events = new ArrayList<>();

        String sql = """
            SELECT e.*
            FROM registrations r
            JOIN events e ON r.event_id = e.id
            WHERE r.user_id = ? AND r.status = 'REGISTERED'
            ORDER BY datetime(e.date_time) ASC;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String dateTimeStr = rs.getString("date_time");
                String venue = rs.getString("venue");
                int capacity = rs.getInt("capacity");
                int organizerId = rs.getInt("organizer_id");
                double price = rs.getDouble("price");

                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, FORMATTER);

                Event e;

                if (!rs.wasNull()) {
                    e = new PaidEvent(id, title, description, dateTime, venue, capacity, organizerId, price);
                } else {
                    e = new Event(id, title, description, dateTime, venue, capacity, organizerId);
                }

                events.add(e);
            }
            return events;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving registrations for user " + userId, e);
        }
    }

    public List<Registration> listAll() {
        List<Registration> list = new ArrayList<>();
        String sql = "SELECT * FROM registrations";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Registration(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("user_id"),
                        rs.getString("status")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving all registrations.", e);
        }
    }
}