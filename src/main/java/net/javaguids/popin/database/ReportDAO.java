package net.javaguids.popin.database;

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
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("ReportDAO.addReport error: " + e.getMessage());
            return false;
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
            System.err.println("ReportDAO.hasUserReported error: " + e.getMessage());
            return false;
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

        } catch (SQLException e) {
            System.err.println("ReportDAO.getReportCountsByEvent error: " + e.getMessage());
        }

        return result;
    }
}