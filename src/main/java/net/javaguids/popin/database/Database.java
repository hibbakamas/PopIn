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

            // USERS TABLE (role stored as text: role_name)
            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        role_name TEXT NOT NULL
                    );
                    """;

            // EVENTS TABLE (for Person B)
            String createEventsTable = """
                    CREATE TABLE IF NOT EXISTS events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        description TEXT,
                        date_time TEXT NOT NULL,
                        venue TEXT NOT NULL,
                        capacity INTEGER NOT NULL,
                        organizer_id INTEGER NOT NULL,
                        FOREIGN KEY (organizer_id) REFERENCES users(id)
                    );
                    """;

            // REGISTRATIONS TABLE (basic structure)
            String createRegistrationsTable = """
                    CREATE TABLE IF NOT EXISTS registrations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY (event_id) REFERENCES events(id),
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    );
                    """;

            stmt.execute(createUsersTable);
            stmt.execute(createEventsTable);
            stmt.execute(createRegistrationsTable);

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
