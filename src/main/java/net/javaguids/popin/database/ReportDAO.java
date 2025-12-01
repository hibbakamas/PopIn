package net.javaguids.popin.database;

import net.javaguids.popin.exceptions.DatabaseOperationException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ReportDAO {

    public boolean addReport(int eventId, int attendeeId) {
        String sql = "INSERT INTO reports (event_id, attendee_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, attendeeId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DatabaseOperationException("Adding report failed, no rows affected.");
            }
            return true;

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error adding report for event " + eventId + ", attendee " + attendeeId, e);
        }
    }

    public boolean hasUserReported(int eventId, int attendeeId) {
        String sql = "SELECT COUNT(*) FROM reports WHERE event_id = ? AND attendee_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            stmt.setInt(2, attendeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException(
                    "Error checking report for event " + eventId + ", attendee " + attendeeId, e);
        }
    }

    /** event_id -> report_count */
    public Map<Integer, Integer> getReportCountsByEvent() {
        String sql = """
            SELECT event_id, COUNT(*) AS report_count
            FROM reports
            GROUP BY event_id
            ORDER BY report_count DESC
        """;

        Map<Integer, Integer> result = new HashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.put(rs.getInt("event_id"), rs.getInt("report_count"));
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving report counts.", e);
        }
    }
}