package net.javaguids.popin.database;

import net.javaguids.popin.exceptions.DatabaseOperationException;
import net.javaguids.popin.exceptions.DatabaseOperationException;

import java.sql.*;

/**
 * Handles event_attendance table:
 * event_id, user_id, status (GOING / INTERESTED / FAVORITE)
 */
public class AttendanceDAO {

    public AttendanceDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS event_attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_id INTEGER NOT NULL,
                user_id  INTEGER NOT NULL,
                status   TEXT NOT NULL,   -- GOING / INTERESTED / FAVORITE
                UNIQUE(event_id, user_id)
            );
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to create event_attendance table.", e);
        }
    }

    // ---------- PUBLIC API ----------

    public boolean markGoing(int eventId, int userId) {
        return setStatus(eventId, userId, "GOING");
    }

    public boolean markInterested(int eventId, int userId) {
        return setStatus(eventId, userId, "INTERESTED");
    }

    public boolean markFavorite(int eventId, int userId) {
        return setStatus(eventId, userId, "FAVORITE");
    }

    /** Remove any attendance record for this user/event. */
    public boolean clearAttendance(int eventId, int userId) {
        String sql = "DELETE FROM event_attendance WHERE event_id = ? AND user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error clearing attendance for event " + eventId + ", user " + userId, e);
        }
    }

    /** How many users marked status = 'GOING' for this event. */
    public int countGoingByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_attendance WHERE event_id = ? AND status = 'GOING'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error counting GOING attendance for event " + eventId, e);
        }
    }

    // ---------- INTERNAL HELPER ----------
    /**
     * Try to UPDATE status first; if nothing updated, INSERT a new row.
     */
    private boolean setStatus(int eventId, int userId, String status) {
        String updateSql = "UPDATE event_attendance SET status = ? WHERE event_id = ? AND user_id = ?";
        String insertSql = "INSERT INTO event_attendance (event_id, user_id, status) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            // 1) Try update
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, status);
                updateStmt.setInt(2, eventId);
                updateStmt.setInt(3, userId);

                int rows = updateStmt.executeUpdate();
                if (rows > 0) {
                    return true; // existing row updated
                }
            }

            // 2) If no row existed, insert a new one
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, eventId);
                insertStmt.setInt(2, userId);
                insertStmt.setString(3, status);

                int rows = insertStmt.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error setting attendance status '" + status + "' for event " + eventId +
                            ", user " + userId, e);
        }
    }
}