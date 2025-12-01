package net.javaguids.popin.database;

import net.javaguids.popin.exceptions.DatabaseOperationException;
import net.javaguids.popin.models.Event;
import net.javaguids.popin.models.PaidEvent;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public EventDAO() {
        createTableIfNotExists();
    }

    // ---------------- TABLE CREATION ----------------
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                date_time TEXT NOT NULL,
                venue TEXT NOT NULL,
                capacity INTEGER NOT NULL,
                organizer_id INTEGER NOT NULL,
                price REAL
            );
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to create events table.", e);
        }
    }

    // ---------------- CREATE EVENT ----------------
    public boolean createEvent(Event event) {
        String sql = """
            INSERT INTO events (title, description, date_time, venue, capacity, organizer_id, price)
            VALUES (?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getDateTime().format(FORMATTER));
            stmt.setString(4, event.getVenue());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getOrganizerId());

            if (event instanceof PaidEvent paidEvent) {
                stmt.setDouble(7, paidEvent.getPrice());
            } else {
                stmt.setNull(7, Types.REAL);
            }

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseOperationException("Creating event failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error creating event.", e);
        }
    }

    // ---------------- FIND BY ID ----------------
    public Event findById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEvent(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error finding event with id " + id, e);
        }
    }

    // ---------------- COUNT REGISTERED ----------------
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
            throw new DatabaseOperationException("Error counting registrations for event " + eventId, e);
        }
    }

    // ---------------- UPDATE EVENT ----------------
    public boolean updateEvent(Event event, Double price) {
        String sql = """
            UPDATE events
            SET title = ?, description = ?, date_time = ?, venue = ?, capacity = ?, organizer_id = ?, price = ?
            WHERE id = ?;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getDateTime().format(FORMATTER));
            stmt.setString(4, event.getVenue());
            stmt.setInt(5, event.getCapacity());
            stmt.setInt(6, event.getOrganizerId());

            if (price != null) {
                stmt.setDouble(7, price);
            } else {
                stmt.setNull(7, Types.REAL);
            }

            stmt.setInt(8, event.getId());
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new DatabaseOperationException("No event found to update with id " + event.getId());
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating event with id " + event.getId(), e);
        }
    }

    // ---------------- DELETE EVENT ----------------
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseOperationException("No event deleted for id " + id);
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting event with id " + id, e);
        }
    }

    // ---------------- FIND EVENTS ----------------
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY datetime(date_time) DESC;";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
            return events;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving all events.", e);
        }
    }

    public List<Event> findAllUpcoming() {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT * FROM events
            WHERE datetime(date_time) > datetime('now')
            ORDER BY datetime(date_time) ASC;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToEvent(rs));
            }
            return events;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving upcoming events.", e);
        }
    }

    public List<Event> findByOrganizerId(int organizerId) {
        List<Event> events = new ArrayList<>();
        String sql = """
            SELECT * FROM events
            WHERE organizer_id = ?
            ORDER BY datetime(date_time) DESC;
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, organizerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRowToEvent(rs));
                }
            }
            return events;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving events for organizer " + organizerId, e);
        }
    }

    // ---------------- ANALYTICS: COUNT ALL ----------------
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM events";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.getInt(1);

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error counting events.", e);
        }
    }

    // ---------------- MAP ROW TO EVENT ----------------
    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String dateString = rs.getString("date_time");
        String venue = rs.getString("venue");
        int capacity = rs.getInt("capacity");
        int organizerId = rs.getInt("organizer_id");
        double price = rs.getDouble("price");

        LocalDateTime dateTime = LocalDateTime.parse(dateString, FORMATTER);
        boolean hasPrice = !rs.wasNull();

        if (hasPrice) {
            return new PaidEvent(id, title, description, dateTime, venue, capacity, organizerId, price);
        }
        return new Event(id, title, description, dateTime, venue, capacity, organizerId);
    }
}