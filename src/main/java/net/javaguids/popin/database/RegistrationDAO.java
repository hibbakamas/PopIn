package net.javaguids.popin.database;

import net.javaguids.popin.models.Registration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
            SELECT 1
            FROM registrations
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
            SELECT COUNT(*)
            FROM registrations
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

    public List<Integer> findUserIdsByEvent(int eventId) {
        List<Integer> list = new ArrayList<>();

        String sql = """
            SELECT user_id
            FROM registrations
            WHERE event_id = ? AND status = 'REGISTERED';
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // GET LIST OF ALL REGISTRATIONS (ATTENDEES) FOR AN EVENT
    public List<Registration> findAllByEvent(int eventId) {
        List<Registration> list = new ArrayList<>();

        String sql = """
            SELECT *
            FROM registrations
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

    // 🔥 NEW: FIND REGISTRATIONS BY USER (for "My Registrations" page)
    // at top of class (with your other fields)
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // 🔥 NEW VERSION: returns events, not registrations
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

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String dateString = rs.getString("date_time");
                    String venue = rs.getString("venue");
                    int capacity = rs.getInt("capacity");
                    int organizerId = rs.getInt("organizer_id");
                    double price = rs.getDouble("price");

                    LocalDateTime dateTime =
                            LocalDateTime.parse(dateString, FORMATTER);

                    // if price is not null -> PaidEvent, else free Event
                    Event event;
                    if (!rs.wasNull()) {
                        event = new PaidEvent(
                                id, title, description, dateTime,
                                venue, capacity, organizerId, price
                        );
                    } else {
                        event = new Event(
                                id, title, description, dateTime,
                                venue, capacity, organizerId
                        );
                    }

                    events.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }}
