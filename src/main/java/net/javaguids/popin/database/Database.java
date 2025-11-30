package net.javaguids.popin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:popin.db";

    static {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role_name TEXT NOT NULL,
                    email_notifications INTEGER DEFAULT 1
                );
            """;
            stmt.execute(createUsersTable);

            // NEW: reports table (very simple: event + attendee)
            String createReportsTable = """
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event_id INTEGER NOT NULL,
                    attendee_id INTEGER NOT NULL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (event_id) REFERENCES events(id),
                    FOREIGN KEY (attendee_id) REFERENCES users(id)
                );
            """;
            stmt.execute(createReportsTable);

            // Event & registration tables are handled in EventDAO / RegistrationDAO

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}